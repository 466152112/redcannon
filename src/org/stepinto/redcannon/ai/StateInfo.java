package org.stepinto.redcannon.ai;

import org.stepinto.redcannon.common.Move;

public class StateInfo {
	public StateInfo(int alpha, int beta, Move bestMove, int height) {
		super();
		this.alpha = alpha;
		this.beta = beta;
		this.bestMove = bestMove;
		this.height = height;
	}

	public int getAlpha() {
		return alpha;
	}

	public int getBeta() {
		return beta;
	}

	public int getHeight() {
		return height;
	}
	
	public Move getBestMove() {
		return bestMove;
	}

	private int alpha;
	private int beta;
	private int height;
	private Move bestMove;
}
