package org.stepinto.redcannon.common;

public class GameUtility {
	public static int getAttackDirection(int player) {
		switch (player) {
		case ChessGame.BLACK:
			return 1;
		case ChessGame.RED:
			return 1;
		default:
			assert (false);
			return 0;
		}
	}

	public static boolean isBeyondRiver(int player, Position pos) {
		return isBeyondRiver(player, pos.getY());
	}

	public static boolean isBeyondRiver(int player, int y) {
		if (getAttackDirection(player) == 1)
			return y < ChessGame.BOARD_HEIGHT / 2;
		else
			return y >= ChessGame.BOARD_HEIGHT / 2;
	}

	public static int getOpponent(int player) {
		switch (player) {
		case ChessGame.BLACK:
			return ChessGame.RED;
		case ChessGame.RED:
			return ChessGame.BLACK;
		default:
			assert (false);
			return 0;
		}
	}

	public static boolean isInPalace(int player, int x, int y) {
		final int PALACE_MIN_X = 3;
		final int PALACE_MAX_X = 5;

		if (PALACE_MIN_X <= x && x <= PALACE_MAX_X) {
			if (GameUtility.getAttackDirection(player) == 1) {
				final int PALACE_MIN_Y = 0;
				final int PALACE_MAX_Y = 3;
				return PALACE_MIN_Y <= y && y <= PALACE_MAX_Y;
			} else {
				final int PALACE_MIN_Y = 7;
				final int PALACE_MAX_Y = 9;
				return PALACE_MIN_Y <= y && y <= PALACE_MAX_Y;
			}
		} else
			return false;
	}

	public static BoardImage createStartBoard() {
		final int RED = ChessGame.RED;
		final int BLACK = ChessGame.BLACK;

		BoardImage board = new BoardImage();
		Unit[] redUnits = { new Rook(RED, 0, 0), new Horse(RED, 1, 0),
				new Elephant(RED, 2, 0), new Advisor(RED, 3, 0),
				new King(RED, 4, 0), new Advisor(RED, 5, 0),
				new Elephant(RED, 6, 0), new Horse(RED, 7, 0),
				new Rook(RED, 8, 0), new Cannon(RED, 1, 2), new Cannon(RED, 7, 2),
				new Pawn(RED, 0, 3), new Pawn(RED, 2, 3), new Pawn(RED, 4, 3),
				new Pawn(RED, 6, 3), new Pawn(RED, 8, 3)};
		Unit[] blackUnits = { new Rook(BLACK, 0, 9), new Horse(BLACK, 1, 9),
				new Elephant(BLACK, 2, 9), new Advisor(BLACK, 3, 9),
				new King(BLACK, 4, 9), new Advisor(BLACK, 5, 9),
				new Elephant(BLACK, 6, 9), new Horse(BLACK, 7, 9),
				new Rook(BLACK, 8, 9), new Cannon(BLACK, 1, 7), new Cannon(BLACK, 7, 7),
				new Pawn(BLACK, 0, 6), new Pawn(BLACK, 2, 6), new Pawn(BLACK, 4, 6),
				new Pawn(BLACK, 6, 6), new Pawn(BLACK, 8, 6)};
		
		board.batchAddUnits(redUnits);
		board.batchAddUnits(blackUnits);
		return board;
	}
}
