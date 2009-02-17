package org.stepinto.redcannon.ai;

import org.stepinto.redcannon.common.Move;

public class SearchResult {
	private Move bestMove;
	private int score;

	public SearchResult(Move bestMove, int score) {
		super();
		this.bestMove = bestMove;
		this.score = score;
	}

	public Move getBestMove() {
		return bestMove;
	}

	public int getScore() {
		return score;
	}
}
