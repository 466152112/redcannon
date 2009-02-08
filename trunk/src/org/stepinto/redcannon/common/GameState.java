package org.stepinto.redcannon.common;

public class GameState {
	private BoardImage board;
	private int player;

	public GameState(BoardImage board, int player) {
		super();
		this.board = board;
		this.player = player;
	}

	public BoardImage getBoard() {
		return board;
	}

	public int getPlayer() {
		return player;
	}
}
