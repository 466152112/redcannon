package org.stepinto.redcannon.ai;

import org.stepinto.redcannon.common.*;
import org.stepinto.redcannon.ai.log.*;

public interface Evaluator {
	public static final int MIN_SCORE = -1000;
	public static final int MAX_SCORE = +1000;
	
	public EvaluateResult evaluate(BoardImage board, int player, int depth, int depthLimit, int timeLeft,
			SearchLogger logger);
}
