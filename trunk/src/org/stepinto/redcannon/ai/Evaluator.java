package org.stepinto.redcannon.ai;

import org.stepinto.redcannon.common.*;

public interface Evaluator {
	public static final int MIN_SCORE = -100;
	public static final int MAX_SCORE = +100;
	
	public EvaluateResult evaluate(BoardImage board, int player, int depth, boolean debug);
}
