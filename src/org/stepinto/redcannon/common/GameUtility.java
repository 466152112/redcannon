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
		if (getAttackDirection(player) == 1)
			return pos.getY() < ChessGame.BOARD_HEIGHT/2;
		else
			return pos.getY() >= ChessGame.BOARD_HEIGHT/2;
	}
}
