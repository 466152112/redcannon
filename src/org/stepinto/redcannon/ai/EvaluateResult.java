package org.stepinto.redcannon.ai;

public class EvaluateResult {
	private boolean searchTerminated;
	private int score;
	private String reason;

	public EvaluateResult(boolean searchTerminated, int score, String reason) {
		super();
		this.searchTerminated = searchTerminated;
		this.score = score;
		this.reason = reason;
	}

	public boolean isSearchTerminated() {
		return searchTerminated;
	}

	public int getScore() {
		return score;
	}

	public String getReason() {
		return reason;
	}
}
