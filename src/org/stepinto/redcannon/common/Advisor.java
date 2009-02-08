package org.stepinto.redcannon.common;

import java.util.*;

public class Advisor extends Unit {
	public Advisor(int color, Position position) {
		super(color, position);
	}

	@Override
	public Position[] getLegalMoves(BoardImage board) {
		final int DX[] = {1, -1, 1, -1};
		final int DY[] = {1, 1, -1, -1};
		Position[] legalMoves = new Position[4];
		int count = 0;
		int player = getColor();
		int x = getPosition().getX();
		int y = getPosition().getY();
		
		for (int i = 0; i < DX.length; i++) {
			int dx = DX[i];
			int dy = DY[i];
			int nx = x + dx;
			int ny = y + dy;
			
			if (Position.isValid(nx, ny) && board.getColorAt(nx, ny) != player && isInPalace(nx, ny)) {
				legalMoves[count] = new Position(x, y);
				count++;
			}
		}
		
		return Arrays.copyOf(legalMoves, count);
	}

	private boolean isInPalace(int x, int y) {
		final int PALACE_MIN_X = 3;
		final int PALACE_MAX_X = 5;
		int player = getColor();
		
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
