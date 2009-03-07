package org.stepinto.redcannon.ai;

import org.stepinto.redcannon.common.*;

public abstract class UnitScoreTable {
	public abstract int getUnitScore(Position pos, int unit);
	
	public int getUnitScoreDifference(BoardImage board, int player) {
		int score = 0;
		for (int x = 0; x < ChessGame.BOARD_WIDTH; x++)
			for (int y = 0; y < ChessGame.BOARD_HEIGHT; y++)
				if (!board.isEmptyAt(x, y)) {
					Position pos = new Position(x, y);
					int unit = board.getUnitAt(pos);
					
					if (board.getColorAt(x, y) == player)
						score += getUnitScore(pos, unit);
					else
						score -= getUnitScore(pos, unit);
				}
		return score;
	}
}
