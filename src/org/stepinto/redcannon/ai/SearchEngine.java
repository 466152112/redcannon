package org.stepinto.redcannon.ai;

import java.util.*;
import org.stepinto.redcannon.common.*;

public class SearchEngine {
	private Evaluator[] evaluators;
	private Selector[] selectors;
	private Validator[] validators;
	
	public void addEvaluator(Evaluator e) {
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
	
	public static final int MAX_DEPTH = 15;
	
	private BoardImage board;
	private StateHash hash;
	private int player;
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
		stat.updateMaxDepth(depth);
		
		if (debug)
			printEnterStateMessage(stateId, board, player, depth, alpha, beta);
		
		// look up hash for identical state
		StateInfo hashedState = hash.lookUp(boardCompressed, player);
		if (hashedState != null) {
			// check if there's a identical state we've searched before
			// and it reaches deeper or equal than this
			if (hashedState.getHeight() >= MAX_DEPTH - depth && hashedState.getBeta() >= beta) {
				if (debug)
					printIdenticalStateFoundMessage(hashedState);
				stat.increaseHashHits();
				return new SearchResult(hashedState.getBestMove(), hashedState.getAlpha());
			}
		}
		
		// call each evaluator
		for (Evaluator e : evaluators) {
			EvaluateResult result = e.evaluate(board, player, depth, debug);
			if (result != null) {
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
		
		if (debug)
			printCandidateMessage(candi);
		
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
			SearchResult tmpResult = doSearch(-beta, -alpha);
			
			// undo move
			board.unperformMove(move, killedUnit);
			player = GameUtility.getOpponent(player);
			depth--;
			
			// update candi-score
			if (debug)
				candiScore.put(c, -tmpResult.getScore());
			
			// update alpha & beta-cuts
			if (-tmpResult.getScore() > alpha) {
				alpha = -tmpResult.getScore();
				bestMove = move;
			}
			if (-tmpResult.getScore() > beta) {
				stat.increaseBetaCuts();
				break;
			}
		}
		
		// update hash
		hash.put(boardCompressed, player, new StateInfo(stateId, alpha, beta, bestMove, MAX_DEPTH - depth));
		
		// System.out.println("alpha = " + alpha);
		if (debug)
			printLeaveStateMessage(stateId, bestMove, candi, candiScore);
		return new SearchResult(bestMove, alpha);
	} 
	
	private void printIdenticalStateFoundMessage(StateInfo hashedState) {
		System.out.println("Identical state found in hash.");
		System.out.println("State-id: " + hashedState.getStateId());
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
			System.out.println(c.getMove() + "   P: " + c.getPriority() + "  R: " + c.getReason() + "  S: " + candiScore.get(c));
		}
	}
	
	private void printCandidateMessage(List<Candidate> candi) {
		for (Candidate c : candi)
			System.out.println(c.getMove() + "   P: " + c.getPriority() + "  R: " + c.getReason());
	}
	
	public SearchEngine(BoardImage board, int player) {
		this.board = board;
		this.player = player;
		
		hash = new StateHash();
		evaluators = new Evaluator[0];
		selectors = new Selector[0];
		validators = new Validator[0];
		stat = new Statistics();
	}
	
	public SearchEngine(GameState state) {
		this(state.getBoard(), state.getPlayer());
	}
	
	public SearchResult search() {
		return doSearch(Evaluator.MIN_SCORE, Evaluator.MAX_SCORE);
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}
}
