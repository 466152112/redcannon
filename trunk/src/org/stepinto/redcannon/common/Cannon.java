package org.stepinto.redcannon.common;

import java.util.*;

public class Cannon extends Unit {
	public Cannon(int color, Position position) {
		super(color, position);
	}

	@Override
	public Position[] getLegalMoves(BoardImage board) {
		final int MAX_LEGAL_MOVES = ChessGame.BOARD_WIDTH*2 + ChessGame.BOARD_HEIGHT*2 - 2;
		final int DX[] = {1, -1, 0, 0};
		final int DY[] = {0, 0, 1, -1};
		
		Position[] legalMoves = new Position[MAX_LEGAL_MOVES];
		int player = getColor();
		int count = 0;
		
		for (int i = 0; i < DX.length; i++) {
			int dx = DX[i];
			int dy = DY[i];
			int x = getPosition().getX() + dx;
			int y = getPosition().getY() + dy;
			while (Position.isValid(x, y) && board.isEmptyAt(x, y)) {
				legalMoves[count] = new Position(x, y);	
				x += DX[i];
				y += DY[i];
				count++;
			}
			if (Position.isValid(x, y)) {  // we meet another unit
				// skip it and continue to move, until we meet another or are out of board
				x += dx;
				y += dy;
				while (Position.isValid(x, y) && !board.isEmptyAt(x, y)) {
					x += dx;
					y += dy;
				}
				
				if (Position.isValid(x, y) && board.getColorAt(x, y) != player) {
					legalMoves[count] = new Position(x, y);
					count++;
				}
			}
		}
		
		return Arrays.copyOf(legalMoves, count);
	}

}
