package org.stepinto.redcannon.ai;

public class EvaluateResult {
	private int score;
	private String reason;

	public EvaluateResult(int score, String reason) {
		super();
		this.score = score;
		this.reason = reason;
	}

	public int getScore() {
		return score;
	}

	public String getReason() {
		return reason;
	}
}
