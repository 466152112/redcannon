package org.stepinto.redcannon.ai;

import java.io.*;
import java.util.*;
import org.stepinto.redcannon.common.*;
import org.stepinto.redcannon.ai.log.*;

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
	
	public static final int MAX_DEPTH = 23;
	
	private BoardImage board;
	private StateHash hash;
	private int player;
	private int depth;
	private Statistics stat;
	
	private SearchLogger logger;
	
	public Statistics getStatistics() {
		return stat;
	}
	
	private SearchResult doSearch(int alpha, int beta) {
		int stateId = stat.getNumberOfStates();
		byte[] boardCompressed = board.compress();
		stat.increaseStates();
		stat.updateMaxDepth(depth);
		
		printEnterStateMessage(stateId, board, player, depth, alpha, beta);
		
		// look up hash for identical state
		StateInfo hashedState = hash.lookUp(boardCompressed, player);
		if (hashedState != null) {
			// check if there's a identical state we've searched before
			// and it reaches deeper or equal than this
			if (hashedState.getHeight() >= MAX_DEPTH - depth && hashedState.getBeta() >= beta) {
				printIdenticalStateFoundMessage(hashedState);
				stat.increaseHashHits();
				return new SearchResult(hashedState.getBestMove(), hashedState.getAlpha());
			}
			else
				stat.increaseHashMisses();
		}
		
		// call each evaluator
		for (Evaluator e : evaluators) {
			EvaluateResult result = e.evaluate(board, player, depth, logger);
			if (result != null) {
				printEvaluateMessage(result, e);
				stat.increaseEvaluatedStates();
				return new SearchResult(null, result.getScore());
			}
		}
		
		// get candidates
		List<Candidate> candi = new ArrayList<Candidate>();
		for (Selector s : selectors)
			s.select(candi, board, player, depth, logger);
		Collections.sort(candi, new Comparator<Candidate>() {
			@Override
			public int compare(Candidate a, Candidate b) {
				return b.getPriority() - a.getPriority();
			}
		});
		
		// search
		Move bestMove = null;
		Map<Candidate, Integer> candiScore = (logger == null ? null : new HashMap<Candidate, Integer>());
			
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
			if (logger != null)
				candiScore.put(c, -tmpResult.getScore());
			
			// update alpha & beta-cuts
			if (-tmpResult.getScore() > alpha) {
				alpha = -tmpResult.getScore();
				bestMove = move;
			}
			if (-tmpResult.getScore() >= beta) {
				stat.increaseBetaCuts();
				break;
			}
		}
		
		// update hash
		if (bestMove != null)
			hash.put(boardCompressed, player, new StateInfo(stateId, alpha, beta, bestMove, MAX_DEPTH - depth));
		
		if (logger != null)
			printLeaveStateMessage(bestMove, candi, candiScore);
		return new SearchResult(bestMove, alpha);
	} 
	
	private void printIdenticalStateFoundMessage(StateInfo hashedState) {
		if (logger != null) {
			logger.printMessage("Identical state found in hash.\n");
			logger.printMessage("State-id: " + hashedState.getStateId() + "\n");
			logger.printMessage("Best-move: " + hashedState.getBestMove() + "\n");
			logger.printMessage("Alpha: " + hashedState.getAlpha() + "\n");
			logger.printMessage("Beta: " + hashedState.getBeta() + "\n");
			logger.printMessage("Height: " + hashedState.getHeight() + "\n");
			
			logger.leaveState();
		}
	}
	
	private void printEvaluateMessage(EvaluateResult result, Evaluator evaluator) {
		if (logger != null) {
			logger.printMessage("Evaluated by " + evaluator.getClass().getSimpleName() + ".\n");
			logger.printMessage("Score: " + result.getScore() + "\n");
			logger.printMessage("Reason: " + result.getReason() + "\n");
			
			logger.leaveState();
		}
	}
	
	private void printEnterStateMessage(int stateId, BoardImage board, int player, int depth, int alpha, int beta) {
		if (logger != null) {
			logger.enterState(stateId);
			
			ByteArrayOutputStream boardStream = new ByteArrayOutputStream();
			board.dump(new PrintStream(boardStream));
			
			logger.printMessage(">> Entering state #" + stateId + ".\n");
			logger.printMessage(boardStream.toString() + "\n");
			logger.printMessage("Player: " + GameUtility.getColorName(player) + "\n");
			logger.printMessage("Depth: " + depth + "\n");
			logger.printMessage("Alpha: " + alpha + "\n");
			logger.printMessage("Beta: " + beta + "\n");
		}
	}
	
	private void printLeaveStateMessage(Move bestMove, List<Candidate> candi, Map<Candidate, Integer> candiScore) {
		if (logger != null) {
			for (Candidate c : candi) {
				logger.printMessage((bestMove != null && bestMove.equals(c.getMove())) ? "* " : "  ");
				logger.printMessage(c.getMove() + "   P: " + c.getPriority() + "  R: " + c.getReason() + "  S: " + candiScore.get(c) + "\n");
			}
			logger.leaveState();
		}
	}
	
	/*private void printCandidateMessage(List<Candidate> candi) {
		for (Candidate c : candi)
			System.out.println(c.getMove() + "   P: " + c.getPriority() + "  R: " + c.getReason());
	}*/
	
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
		if (logger != null)
			logger.beginSearch();
		SearchResult result = doSearch(Evaluator.MIN_SCORE, Evaluator.MAX_SCORE);
		if (logger != null)
			logger.endSearch();
		return result;
	}

	public void setLogger(SearchLogger logger) {
		this.logger = logger;
	}
}
