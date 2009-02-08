package org.stepinto.redcannon.common;

import java.util.*;

public class King extends Unit {
	public King(int color, Position position) {
		super(color, position);
	}

	@Override
	public Position[] getLegalMoves(BoardImage board) {
		final int DX[] = {1, -1, 0, 0};
		final int DY[] = {0, 0, 1, -1};
		int player = getColor();
		Position[] legalMoves = new Position[5];
		int count = 0;
		
		for (int i = 0; i < DX.length; i++) {
			int x = getPosition().getX() + DX[i];
			int y = getPosition().getY() + DY[i];
			if (Position.isValid(x, y) && GameUtility.isInPalace(player, x, y) && board.getColorAt(x, y) != player) {
				legalMoves[count] = new Position(x, y);
				count++;
			}
		}
		
		// flying move
		int attackDir = GameUtility.getAttackDirection(player);
		int x = getPosition().getX();
		int y = getPosition().getY() + attackDir;
		while (Position.isValid(x, y) && board.isEmptyAt(x, y))
			y += attackDir;
		if (Position.isValid(x, y)) {
			Unit unit = board.getUnitAt(x, y);
			if (unit instanceof King) {
				legalMoves[count] = new Position(x, y);
				count++;
			}
		}
		
		return Arrays.copyOf(legalMoves, count);
	}
}
