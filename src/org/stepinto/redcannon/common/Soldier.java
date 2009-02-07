package org.stepinto.redcannon.common;

import java.util.*;

public class Soldier extends Unit {
	public Soldier(int color, Position position) {
		super(color, position);
	}

	@Override
	public Position[] getLegalMoves(BoardImage board) {
		int player = getColor();
		int attackDir = GameUtility.getAttackDirection(player);
		
		if (GameUtility.isBeyondRiver(player, getPosition())) {
			final int DX[] = {1, -1, 0};
			final int DY[] = {0, 0, attackDir};
			Position[] legalMoves = new Position[4];
			int count = 0;
			
			for (int i = 0; i < DX.length; i++) {
				Position adj = getPosition().move(DX[i], DY[i]);
				if (board.getColorAt(adj) != getColor()) {
					legalMoves[count] = adj;
					count++;
				}
			}
			
			return Arrays.copyOf(legalMoves, count);
		}
		else {
			Position possibleMove = getPosition().move(0, attackDir);
			if (possibleMove.isValid()) {
				Position[] legalMoves = new Position[1];
				legalMoves[0] = possibleMove;
				return legalMoves;
			}
			else
				return new Position[0];
		}
	}
}
