package org.stepinto.redcannon.ai;

import org.apache.commons.lang.ArrayUtils;
import org.stepinto.redcannon.common.*;

public class KillerMoveTable {
	private static final int MAX_DEPTH = 128;
	private static final int KILLER_MOVE_NUM = 2;
	private static final int KILLER_MOVE_SCORE = 64;
	
	private Move killerMoves[][];
	
	public KillerMoveTable() {
		killerMoves = new Move[MAX_DEPTH + 1][KILLER_MOVE_NUM]; 
	}
	
	public boolean isKillerMove(int depth, Move move) {
		return ArrayUtils.contains(killerMoves[depth], move);
	}
	
	public int getMoveScore(int depth, Move move) {
		return isKillerMove(depth, move) ? KILLER_MOVE_SCORE : 0; 
	}
	
	public void addMove(int depth, Move move) {
		if (!isKillerMove(depth, move)) {
			for (int i = 1; i < killerMoves[depth].length; i++)
				killerMoves[depth][i] = killerMoves[depth][i-1];
			killerMoves[depth][0] = move;
		}
	}
}
