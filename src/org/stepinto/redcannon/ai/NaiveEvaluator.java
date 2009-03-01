package org.stepinto.redcannon.ai;

import org.stepinto.redcannon.common.*;
import org.stepinto.redcannon.ai.log.*;

public class NaiveEvaluator implements Evaluator {
	private final static int EVALUATE_THRESHOLD = 20;
	
	private static int[] countUnits(BoardImage board) {
		int result[] = new int [8];
		for (int x = 0; x < ChessGame.BOARD_WIDTH; x++)
			for (int y = 0; y < ChessGame.BOARD_HEIGHT; y++)
				if (!board.isEmptyAt(x, y))
					result[board.getUnitAt(x, y)]++;
		return result;
	} 
	
	private static boolean hasAttackUnit(BoardImage board) {
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
	
	// return the color of player that does not have a king
	// return ChessGame.EMPTY if both have kings
	private int getPlayerHasNoKing(BoardImage board) {
		boolean blackHasKing = false;
		boolean redHasKing = false;
		
		for (int x = 0; x < ChessGame.BOARD_WIDTH; x++)
			for (int y = 0; y < ChessGame.BOARD_HEIGHT; y++)
				if (board.getUnitAt(x, y) == ChessGame.KING) {
					int color = board.getColorAt(x, y);
					if (color == ChessGame.BLACK)
						blackHasKing = true;
					else if (color == ChessGame.RED)
						redHasKing = true;
				}
		
		if (blackHasKing)
			return redHasKing ? ChessGame.EMPTY : ChessGame.RED; 
		else if (redHasKing)
			return ChessGame.BLACK;
		else {
			assert (false);
			return ChessGame.EMPTY;
		}
	}
	
	@Override
	public EvaluateResult evaluate(BoardImage board, int player, int depth, int alpha, int beta,
			int depthLimit, int timeLeft, SearchLogger logger) {
		// check if one player's king has been killed
		int playerHasNoKing = getPlayerHasNoKing(board);
		if (playerHasNoKing != ChessGame.EMPTY) {
			if (playerHasNoKing == player) {
				String reason = "our king was killed.";
				return new EvaluateResult(Evaluator.MIN_SCORE, reason);
			}
			else {
				String reason = "opponent's king was killed.";
				return new EvaluateResult(Evaluator.MAX_SCORE, reason);
			}
		}
		
		// check if both player have no attack units
		if (!hasAttackUnit(board)) {
			String reason = "no attack unit exists.";
			return new EvaluateResult(0, reason);
		}
		
		// calculate unit-score-diff
		int score = UnitScoreUtility.getUnitScoreDifference(board, player);
		if (Math.abs(score) > EVALUATE_THRESHOLD) {
			String reason = "difference of board score exceeds threshold."; 
			return new EvaluateResult(score, reason);
		}
		
		// check if we've reached the depth limit
		if (depth >= depthLimit) {
			String reason = "reaches depth limit.";
			return new EvaluateResult(score, reason);
		}
		 
		// check if time's up
		// return a large value to make the caller not to update its current alpha
		if (timeLeft < 0) {
			String reason = "time's up.";
			return new EvaluateResult(Evaluator.MAX_SCORE, reason);
		}
		
		return null;
	}

	@Override
	public void notifyBestMove(BoardImage board, int player, int depth,
			Move bestMove, int score) {
		
	}
}
