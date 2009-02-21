package org.stepinto.redcannon.ai;

import org.stepinto.redcannon.common.*;
import org.stepinto.redcannon.ai.log.*;

public class NaiveEvaluator implements Evaluator {
	private final static int EVALUATE_THRESHOLD = 20;
	
	@Override
	public EvaluateResult evaluate(BoardImage board, int player, int depth,
			SearchLogger logger) {
		int score = UnitScoreUtility.getUnitScoreDifference(board, player);
		
		if (Math.abs(score) > EVALUATE_THRESHOLD) {
			String reason = "difference of board score exceeds threshold."; 
			return new EvaluateResult(score, reason);
		}
		else if (depth >= SearchEngine.MAX_DEPTH) {
			String reason = "reaches depth limit.";
			return new EvaluateResult(score, reason);
		}
		else
			return null;
	}
}
