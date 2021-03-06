package org.stepinto.redcannon.ai;

import java.io.*;
import java.util.*;
import org.stepinto.redcannon.common.*;
import org.stepinto.redcannon.ai.log.*;

public class NaiveSearchEngine implements SearchEngine {
	public static final int DEFAULT_DEPTH_LIMIT = 23;
	public static final int DEFAULT_TIME_LIMIT = Integer.MAX_VALUE;
	
	private Evaluator[] evaluators;
	private Selector[] selectors;

	private int depthLimit;
	private int timeLimit;  // unit: ms
	private long startTime;
	private BoardImage board;
	private StateHash<StateInfo> hash;
	private int player;
	private int depth;
	private Statistics stat;
	private SearchLogger logger;
	
	private SearchResult doSearch(int alpha, int beta) {
		int stateId = stat.getNumberOfStates();
		int timeLeft = timeLimit - (int)(System.currentTimeMillis() - startTime);
		stat.increaseStates();
		stat.updateMaxDepth(depth);
		
		printEnterStateMessage(stateId, board, player, depth, alpha, beta);
		
		// look up hash for identical state
		StateInfo hashedState = hash.lookUp(board, player);
		if (hashedState != null) {
			// check if there's a identical state we've searched before
			// and it reaches deeper or equal than this
			if (hashedState.getHeight() >= depthLimit - depth && hashedState.getBeta() >= beta) {
				printIdenticalStateFoundMessage(hashedState);
				stat.increaseHashHits();
				return new SearchResult(hashedState.getBestMove(), hashedState.getAlpha());
			}
			else
				stat.increaseHashMisses();
		}
		
		// call each evaluator
		for (Evaluator e : evaluators) {
			EvaluateResult result = e.evaluate(board, player, depth, alpha, beta, depthLimit, timeLeft, logger);
			if (result != null) {
				printEvaluateMessage(result, e);
				stat.increaseEvaluatedStates();
				return new SearchResult(null, result.getScore());
			}
		}
		
		// get candidates
		List<Candidate> candi = new ArrayList<Candidate>();
		for (Selector s : selectors)
			s.select(candi, board, player, depth, alpha, beta, depthLimit, hash, logger);
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
			hash.put(board, player, new StateInfo(stateId, alpha, beta, bestMove, depthLimit - depth));
		
		// notify best-move
		for (Evaluator e : evaluators)
			e.notifyBestMove(board, player, depth, bestMove, alpha, candi);
		for (Selector s : selectors)
			s.notifyBestMove(board, player, depth, bestMove, alpha, candi);
		
		if (logger != null)
			printLeaveStateMessage(bestMove, candi, candiScore);
		return new SearchResult(bestMove, alpha);
	} 
	
	public NaiveSearchEngine(BoardImage board, int player) {
		this.board = board;
		this.player = player;
		
		hash = new StateHash<StateInfo>();
		evaluators = new Evaluator[0];
		selectors = new Selector[0];
		stat = new Statistics();
		
		depthLimit = DEFAULT_DEPTH_LIMIT;
		timeLimit = DEFAULT_TIME_LIMIT;
	}
	
	public NaiveSearchEngine(GameState state) {
		this(state.getBoard(), state.getPlayer());
	}
	
	public NaiveSearchEngine() {
		this(new BoardImage(), ChessGame.RED);
	}

	@Override
	public SearchResult search() {
		if (logger != null)
			logger.beginSearch();
		
		startTime = System.currentTimeMillis();
		SearchResult result = doSearch(Evaluator.MIN_SCORE, Evaluator.MAX_SCORE);
		
		if (logger != null)
			logger.endSearch();
		return result;
	}

	@Override
	public void setLogger(SearchLogger logger) {
		this.logger = logger;
	}

	@Override
	public void setStateHash(StateHash<StateInfo> hash) {
		this.hash = hash;
	}

	@Override
	public void setInitialBoard(BoardImage board) {
		this.board = board.duplicate();
	}

	@Override
	public void setInitialPlayer(int player) {
		this.player = player;		
	}
	
	@Override
	public void addEvaluator(Evaluator e) {
		evaluators = Arrays.copyOf(evaluators, evaluators.length+1);
		evaluators[evaluators.length-1] = e;
	}
	
	@Override
	public void addSelector(Selector s) {
		selectors = Arrays.copyOf(selectors, selectors.length+1);
		selectors[selectors.length-1] = s;
	}

	@Override
	public void setDepthLimit(int depthLimit) {
		this.depthLimit = depthLimit;
	}

	@Override
	public void setTimeLimit(int timeLimit) {
		this.timeLimit = timeLimit;
	}
	
	@Override
	public Statistics getStatistics() {
		return stat;
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
	
	@Override
	public void clearHash() {
		hash.clear();
	}
}
