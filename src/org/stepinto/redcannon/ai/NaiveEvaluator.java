package org.stepinto.redcannon.ai;

import org.stepinto.redcannon.common.*;
import org.stepinto.redcannon.ai.log.*;

public class NaiveEvaluator implements Evaluator {
	private final static int EVALUATE_THRESHOLD = 20;
	
	public static int[] countUnits(BoardImage board) {
		int result[] = new int [8];
		for (int x = 0; x < ChessGame.BOARD_WIDTH; x++)
			for (int y = 0; y < ChessGame.BOARD_HEIGHT; y++)
				if (!board.isEmptyAt(x, y))
					result[board.getUnitAt(x, y)]++;
		return result;
	}
	
	public static boolean hasAttackUnit(BoardImage board) {
		int count[] = countUnits(board);
		if (count[ChessGame.ROOK] > 0 || count[ChessGame.HORSE] > 0 || count[ChessGame.CANNON] > 0 || count[ChessGame.PAWN] > 0)
			return true;
		else {
			if (count[ChessGame.KING] == 2) {
				// check will the king kills the other king in one move
				for (int x = 0; x < ChessGame.BOARD_WIDTH; x++)
					for (int y = 0; y < ChessGame.BOARD_HEIGHT; y++)
						if (board.getUnitAt(x, y) == ChessGame.KING) {
							Position moveTargets[] = RuleEngine.getLegalMoves(board, x, y);
							for (Position p : moveTargets)
								if (board.getUnitAt(p) == ChessGame.KING)
									return true;
						}
				return false;
			}
			else
				return false;
		}
	}
	
	@Override
	public EvaluateResult evaluate(BoardImage board, int player, int depth,
			SearchLogger logger) {
		int score = UnitScoreUtility.getUnitScoreDifference(board, player); 
		
		if (Math.abs(score) > EVALUATE_THRESHOLD) {
			String reason = "difference of board score exceeds threshold."; 
			return new EvaluateResult(score, reason);
		}
		else if (!hasAttackUnit(board)) {
			String reason = "no attack unit exists.";
			return new EvaluateResult(0, reason);
		}
		else if (depth >= SearchEngine.MAX_DEPTH) {
			String reason = "reaches depth limit.";
			return new EvaluateResult(score, reason);
		}
		else
			return null;
	}
}
