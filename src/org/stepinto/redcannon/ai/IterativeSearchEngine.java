package org.stepinto.redcannon.ai;

import java.util.*;
import org.stepinto.redcannon.ai.log.*;
import org.stepinto.redcannon.common.*;

public class IterativeSearchEngine implements SearchEngine {
	public static final int DEFAULT_DEPTH_LIMIT = 23;
	public static final int DEFAULT_TIME_LIMIT = 30000;
	public static final int SERACH_STOP_THRESHOLD = 100;
	
	private BoardImage board;
	private int player;
	private Evaluator evaluators[];
	private Selector selectors[];
	private int depthLimit;
	private int timeLimit;
	private SearchLogger logger;
	private StateHash hash;
	private Statistics stat;
	
	public IterativeSearchEngine(BoardImage board, int player) {
		this.board = board.duplicate();
		this.player = player;
		
		evaluators = new Evaluator[0];
		selectors = new Selector[0];
		depthLimit = DEFAULT_DEPTH_LIMIT;
		timeLimit = DEFAULT_TIME_LIMIT;
		hash = new StateHash();
		stat = new Statistics();
	}
	
	public IterativeSearchEngine(GameState state) {
		this(state.getBoard(), state.getPlayer());
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
	public Statistics getStatistics() {
		return stat;
	}

	@Override
	public SearchResult search() {
		TimeCounter counter = new TimeCounter();
		SearchResult result = null;
		
		counter.start();
		for (int depth = 1; depth <= depthLimit && counter.getTimeMillis() < timeLimit; depth++) {
			// System.out.print("searching... depth=" + depth); 
			
			// init naive engine
			SearchEngine naiveEngine = new NaiveSearchEngine(board, player);
			for (Evaluator e : evaluators)
				naiveEngine.addEvaluator(e);
			for (Selector s : selectors)
				naiveEngine.addSelector(s);
			naiveEngine.setStateHash(hash);
			naiveEngine.setLogger(logger);
			naiveEngine.setDepthLimit(depth);
			
			// search & get result
			result = naiveEngine.search();
			if (result.getScore() > SERACH_STOP_THRESHOLD)
				break;
			// System.out.println(", states=" + naiveEngine.getStatistics().getNumberOfStates());
		}
		
		return result;
	}

	@Override
	public void setDepthLimit(int depthLimit) {
		this.depthLimit = depthLimit;
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
	public void setLogger(SearchLogger logger) {
		this.logger = logger;
	}

	@Override
	public void setStateHash(StateHash hash) {
		this.hash = hash;
	}

	@Override
	public void setTimeLimit(int timeLimit) {
		this.timeLimit = timeLimit;
	}
}
