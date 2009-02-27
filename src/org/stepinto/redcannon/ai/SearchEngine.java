package org.stepinto.redcannon.ai;

import org.stepinto.redcannon.common.*;
import org.stepinto.redcannon.ai.log.*;

public interface SearchEngine {
	public void addEvaluator(Evaluator e);
	public void addSelector(Selector s);
	
	public void setInitialBoard(BoardImage board);
	public void setInitialPlayer(int player);
	public void setDepthLimit(int depthLimit);
	public void setTimeLimit(int timeLimit);
	public void setLogger(SearchLogger logger);
	public void setStateHash(StateHash hash);
	
	public SearchResult search();
	public Statistics getStatistics();
}
