package org.stepinto.redcannon.common;

import java.util.*;

public class Elephant extends Unit {
	public Elephant(int color, int x, int y) {
		super(color, x, y);
	}
	
	public Elephant(int color, Position position) {
		super(color, position);
	}

	@Override
	public Position[] getLegalMoves(BoardImage board) {
		final int DX[] = {2, 2, -2, -2};
		final int DY[] = {2, -2, 2, -2};
		Position[] legalMoves = new Position[4];
		int count = 0;
		int player = getColor();
		
		for (int i = 0; i < DX.length; i++) {
			int x = getPosition().getX() + DX[i];
			int y = getPosition().getY() + DY[i];
			if (Position.isValid(x, y) && GameUtility.isBeyondRiver(player, y) && board.getColorAt(x, y) != player) {
				legalMoves[count] = new Position(x, y);
				count++;
			}
		}
		
		return Arrays.copyOf(legalMoves, count);
	}

	@Override
	public String getChineseSymbol() {
		return getColor() == ChessGame.RED ? "Ïà" : "Ïó";
	}

	@Override
	public String getSymbol() {
		return "E";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		else if (obj instanceof Elephant)
			return super.equals(obj); 
		else
			return false;
	}
}
