package org.stepinto.redcannon.common;

public class GameUtility {
	public static int getAttackDirection(int player) {
		switch (player) {
		case ChessGame.BLACK:
			return 1;
		case ChessGame.RED:
			return 1;
		default:
			assert(false);
			return 0;
		}
	}
	
	public static boolean isBeyondRiver(int player, Position pos) {
		return isBeyondRiver(player, pos.getY());
	}
	
	public static boolean isBeyondRiver(int player, int y) {
		if (getAttackDirection(player) == 1)
			return y < ChessGame.BOARD_HEIGHT/2;
		else
			return y >= ChessGame.BOARD_HEIGHT/2;
	}
	
	public static int getOpponent(int player) {
		switch (player) {
		case ChessGame.BLACK:
			return ChessGame.RED;
		case ChessGame.RED:
			return ChessGame.BLACK;
		default:
			assert(false);
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
			}
			else {
				final int PALACE_MIN_Y = 7;
				final int PALACE_MAX_Y = 9;
				return PALACE_MIN_Y <= y && y <= PALACE_MAX_Y;
			}		
		}
		else
			return false;
	}
}
