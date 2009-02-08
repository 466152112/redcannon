package org.stepinto.redcannon.common;

import java.util.*;

public class Advisor extends Unit {
	public Advisor(int color, int x, int y) {
		super(color, x, y);
	}
	
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
			
			if (Position.isValid(nx, ny) && board.getColorAt(nx, ny) != player && GameUtility.isInPalace(player, nx, ny)) {
				legalMoves[count] = new Position(x, y);
				count++;
			}
		}
		
		return Arrays.copyOf(legalMoves, count);
	}

	@Override
	public String getChineseSymbol() {
		return getColor() == ChessGame.RED ? "ÊË" : "Ê¿";
	}

	@Override
	public String getSymbol() {
		return "A";
	}
}
