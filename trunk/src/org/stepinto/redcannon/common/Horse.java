package org.stepinto.redcannon.common;

import java.util.Arrays;

public class Horse extends Unit {
	public Horse(int color, int x, int y) {
		super(color, x, y);
	}
	
	public Horse(int color, Position position) {
		super(color, position);
	}

	@Override
	public Position[] getLegalMoves(BoardImage board) {
		final int HORSE_DX[] = {2, 2, -2, -2, 1, 1, -1, -1};
		final int HORSE_DY[] = {1, -1, 1, -1, 2, -2, 2, -2};
		final int BARRI_DX[] = {1, 1, -1, -1, 0, 0, 0, 0};
		final int BARRI_DY[] = {0, 0, 0, 0, 1, -1, 1, -1};
		
		Position[] legalMoves = new Position[HORSE_DX.length];
		int count = 0;
		int x = getPosition().getX();
		int y = getPosition().getY();
		
		for (int i = 0; i < HORSE_DX.length; i++) {
			int bx = x + BARRI_DX[i];
			int by = y + BARRI_DY[i];
			int nx = x + HORSE_DX[i];
			int ny = y + HORSE_DY[i];
			
			if (board.isEmptyAt(bx, by) && Position.isValid(nx, ny)) {
				legalMoves[count] = new Position(nx, ny);
				count++;
			}
		}
		
		return Arrays.copyOf(legalMoves, count);
	}
	
	@Override
	public String getChineseSymbol() {
		return "Âí";
	}

	@Override
	public String getSymbol() {
		return "H";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		else if (obj instanceof Horse)
			return super.equals(obj); 
		else
			return false;
	}
}
