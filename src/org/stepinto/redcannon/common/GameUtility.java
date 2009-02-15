package org.stepinto.redcannon.common;

public class GameUtility {
	public static int getAttackDirection(int player) {
		switch (player) {
		case ChessGame.BLACK:
			return 1;
		case ChessGame.RED:
			return -1;
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
		final int X[] = {0, 1, 2, 3, 4, 5, 6, 7, 8, 1, 7, 0, 2, 4, 6, 8};
		final int Y[] = {9, 9, 9, 9, 9, 9, 9, 9, 9, 7, 7, 6, 6, 6, 6, 6};
		final int UNIT[] = {ChessGame.ROOK, ChessGame.HORSE, ChessGame.ELEPHANT,
				ChessGame.ADVISOR, ChessGame.KING, ChessGame.ADVISOR,
				ChessGame.ELEPHANT, ChessGame.HORSE, ChessGame.ROOK,
				ChessGame.CANNON, ChessGame.CANNON, ChessGame.PAWN,
				ChessGame.PAWN, ChessGame.PAWN, ChessGame.PAWN, ChessGame.PAWN};
		assert(X.length == Y.length && UNIT.length == Y.length);
		
		BoardImage board = new BoardImage();
		for (int i = 0; i < X.length; i++) {
			int x = X[i];
			int y = Y[i];
			int unit = UNIT[i];
			board.setColorAt(x, y, ChessGame.BLACK);
			board.setUnitAt(x, y, unit);
			board.setColorAt(x, ChessGame.BOARD_HEIGHT - y - 1, ChessGame.RED);
			board.setUnitAt(x, ChessGame.BOARD_HEIGHT - y - 1, unit);
		}
		return board;
	}

	public static String getUnitChineseSymbol(int color, int unit) {
		final int BLACK = ChessGame.BLACK;
		
		switch (unit) {
		case ChessGame.ADVISOR:
			return color == BLACK ? "士" : "仕";
		case ChessGame.CANNON:
			return color == BLACK ? "砲" : "炮";
		case ChessGame.ELEPHANT:
			return color == BLACK ? "象" : "相";
		case ChessGame.HORSE:
			return "马";
		case ChessGame.KING:
			return color == BLACK ? "将" : "帅";
		case ChessGame.PAWN:
			return color == BLACK ? "卒" : "兵";
		case ChessGame.ROOK:
			return "车";
		default:
			assert(false);
			return null;
		}
	}
}
