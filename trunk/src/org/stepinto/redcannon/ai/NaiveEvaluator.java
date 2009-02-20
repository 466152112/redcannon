package org.stepinto.redcannon.ai;

import org.stepinto.redcannon.common.*;

public class NaiveEvaluator implements Evaluator {
	private final static int EVALUATE_THRESHOLD = 20;
	
	@Override
	public EvaluateResult evaluate(BoardImage board, int player, int depth,
			boolean debug) {
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
