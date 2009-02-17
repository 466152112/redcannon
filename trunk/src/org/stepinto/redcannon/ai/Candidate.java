package org.stepinto.redcannon.ai;

import org.stepinto.redcannon.common.Move;

public class Candidate {
	private Move move;
	private int priority;
	private String reason;

	public Candidate(Move move, int priority, String reason) {
		super();
		this.move = move;
		this.priority = priority;
		this.reason = reason;
	}

	public Move getMove() {
		return move;
	}

	public int getPriority() {
		return priority;
	}

	public String getReason() {
		return reason;
	}
}
