package org.stepinto.redcannon.ai;

import java.util.*;
import org.stepinto.redcannon.common.*;

public class SearchEngine {
	private Evaluator[] evaluators;
	private Selector[] selectors;
	private Validator[] validators;
	
	public void addEvvaluator(Evaluator e) {
		evaluators = Arrays.copyOf(evaluators, evaluators.length+1);
		evaluators[evaluators.length-1] = e;
	}
	
	public void addSelector(Selector s) {
		selectors = Arrays.copyOf(selectors, selectors.length+1);
		selectors[selectors.length-1] = s;
	}
	
	public void addValidator(Validator v) {
		validators = Arrays.copyOf(validators, validators.length+1);
		validators[validators.length-1] = v;
	}
	
	private BoardImage board;
	private StateHash hash;
	private int player;
	private int maxDepth;
	private int depth;
	private Statistics stat;
	
	private boolean debug;
	
	public Statistics getStatistics() {
		return stat;
	}
	
	private SearchResult doSearch(int alpha, int beta) {
		int stateId = stat.getNumberOfStates();
		byte[] boardCompressed = board.compress();
		stat.increaseStates();
		
		if (debug)
			printEnterStateMessage(stateId, board, player, depth, alpha, beta);
		
		// look up hash for identical state
		StateInfo hashedState = hash.lookUp(boardCompressed, player);
		if (hashedState != null) {
			// check if there's a identical state we've searched before
			// and it reaches deeper or equal than this
			if (hashedState.getHeight() >= maxDepth - depth && hashedState.getBeta() >= beta) {
				if (debug)
					printIdenticalStateFoundMessage(hashedState);
				stat.increaseHashedStates();
				return new SearchResult(hashedState.getBestMove(), hashedState.getAlpha());
			}
		}
		
		// call each evaluator
		for (Evaluator e : evaluators) {
			EvaluateResult result = e.evaluate(board, player, depth, debug);
			if (result.isSearchTerminated()) {
				if (debug)
					printEvaluateMessage(result, e);
				stat.increaseEvaluatedStates();
				return new SearchResult(null, result.getScore());
			}
		}
		
		// get candidates
		List<Candidate> candi = new ArrayList<Candidate>();
		for (Selector s : selectors)
			s.select(candi, board, player, depth, debug);
		Collections.sort(candi, new Comparator<Candidate>() {
			@Override
			public int compare(Candidate a, Candidate b) {
				return b.getPriority() - a.getPriority();
			}
		});
		
		// search
		Move bestMove = null;
		Map<Candidate, Integer> candiScore = (debug ? new HashMap<Candidate, Integer>() : null);
			
		for (Candidate c : candi) {
			Move move = c.getMove();
			assert(board.getColorAt(move.getSource()) == player);
			assert(board.getColorAt(move.getTarget()) != player);
			
			// perform move
			int killedUnit = board.performMove(move);
			player = GameUtility.getOpponent(player);
			depth++;
			
			// search recursively
			SearchResult tmpResult = doSearch(-alpha, -beta);
			
			// undo move
			board.unperformMove(move, killedUnit);
			player = GameUtility.getOpponent(player);
			depth--;
			
			// update candi-score
			if (debug)
				candiScore.put(c, -tmpResult.getScore());
			
			// beta-cuts & update alpha
			if (-tmpResult.getScore() > beta) {
				stat.increaseBetaCuts();
				break;
			}
			if (-tmpResult.getScore() > alpha) {
				alpha = -tmpResult.getScore();
				bestMove = move;
			}
		}
		
		// update hash
		hash.put(boardCompressed, player, new StateInfo(alpha, beta, bestMove, maxDepth - depth));
		
		printLeaveStateMessage(stateId, bestMove, candi, candiScore);
		return new SearchResult(bestMove, alpha);
	} 
	
	private void printIdenticalStateFoundMessage(StateInfo hashedState) {
		System.out.println("Identical state found in hash.");
		System.out.println("Best-move: " + hashedState.getBestMove());
		System.out.println("Alpha: " + hashedState.getAlpha());
		System.out.println("Beta: " + hashedState.getBeta());
		System.out.println("Height: " + hashedState.getHeight());
	}
	
	private void printEvaluateMessage(EvaluateResult result, Evaluator evaluator) {
		System.out.println("Evaluated by " + evaluator.getClass().getSimpleName() + ".");
		System.out.println("Score: " + result.getScore());
		System.out.println("Reason: " + result.getReason());
	}
	
	private void printEnterStateMessage(int stateId, BoardImage board, int player, int depth, int alpha, int beta) {
		System.out.println(">> Entering state #" + stateId + ".");
		board.dump(System.out);
		System.out.println("Player: " + GameUtility.getColorName(player));
		System.out.println("Depth: " + depth);
		System.out.println("Alpha: " + alpha);
		System.out.println("Beta: " + beta);
	}
	
	private void printLeaveStateMessage(int stateId, Move bestMove, List<Candidate> candi, Map<Candidate, Integer> candiScore) {
		System.out.println(">> Leaving state #" + stateId + ".");
		for (Candidate c : candi) {
			System.out.print(bestMove == c.getMove() ? "* " : "  ");
			System.out.println(c.getMove() + "   P: " + c.getPriority() + "  S: " + candiScore.get(c));
		}
	}
	
	public SearchEngine(BoardImage board, int player) {
		this.board = board;
		this.player = player;
		
		hash = new StateHash();
	}
	
	public SearchResult search() {
		return doSearch(Evaluator.MIN_SCORE, Evaluator.MAX_SCORE);
	}
}
