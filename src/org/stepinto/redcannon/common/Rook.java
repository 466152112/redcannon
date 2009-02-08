package org.stepinto.redcannon.common;

import java.util.*;

public class Rook extends Unit {
	public Rook(int color, int x, int y) {
		super(color, x, y);
	}
	
	public Rook(int color, Position position) {
		super(color, position);
	}

	@Override
	public Position[] getLegalMoves(BoardImage board) {
		final int DX[] = {1, -1, 0, 0};
		final int DY[] = {0, 0, 1, -1};
		
		Position[] legalMoves = new Position[ChessGame.BOARD_WIDTH*2 + ChessGame.BOARD_HEIGHT*2 - 2];
		int sx = getPosition().getX();
		int sy = getPosition().getY();
		int count = 0;
		int player = getColor();
		int opponent = GameUtility.getOpponent(player);
		
		for (int i = 0; i < DX.length; i++) {
			int dx = DX[i];
			int dy = DY[i];
			int x = sx + dx;
			int y = sy + dx;
			while (Position.isValid(x, y) && board.isEmptyAt(x, y)) {
				legalMoves[count] = new Position(x, y);
				x += dx;
				y += dy;
				count++;
			}
			
			if (Position.isValid(x, y) && board.getColorAt(x, y) == opponent) {
				legalMoves[count] = new Position(x, y);
				count++;
			}
		}
		
		return Arrays.copyOf(legalMoves, count);
	}
	
	@Override
	public String getChineseSymbol() {
		return "³µ";
	}

	@Override
	public String getSymbol() {
		return "R";
	}
}
