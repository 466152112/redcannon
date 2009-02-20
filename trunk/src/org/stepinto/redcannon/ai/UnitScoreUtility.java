package org.stepinto.redcannon.ai;

import org.stepinto.redcannon.common.*;

public class UnitScoreUtility {
	private final static int KING_SCORE = 500;
	
	private final static int ROOK_SCORE = 10;
	private final static int HORSE_SCORE = 8;
	private final static int CANNON_SCORE = 8;
	
	private final static int ELEPHANT_SCORE = 3;
	private final static int ADVISOR_SCORE = 3;
	private final static int PAWN_SCORE = 1;
	
	public static int getUnitScore(int unit) {
		switch (unit) {
		case ChessGame.KING:
			return KING_SCORE;
		case ChessGame.ROOK:
			return ROOK_SCORE;
		case ChessGame.HORSE:
			return HORSE_SCORE;
		case ChessGame.CANNON:
			return CANNON_SCORE;
		case ChessGame.ELEPHANT:
			return ELEPHANT_SCORE;
		case ChessGame.ADVISOR:
			return ADVISOR_SCORE;
		case ChessGame.PAWN:
			return PAWN_SCORE;
		default:
			assert(false);
			return 0;
		}
	}
	
	public static int getUnitScoreDifference(BoardImage board, int player) {
		int score = 0;
		for (int x = 0; x < ChessGame.BOARD_WIDTH; x++)
			for (int y = 0; y < ChessGame.BOARD_HEIGHT; y++)
				if (!board.isEmptyAt(x, y)) {
					if (board.getColorAt(x, y) == player)
						score += getUnitScore(board.getUnitAt(x, y));
					else
						score -= getUnitScore(board.getUnitAt(x, y));
				}
		return score;
	}
}
