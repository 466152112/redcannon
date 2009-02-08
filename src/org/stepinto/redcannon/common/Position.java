package org.stepinto.redcannon.common;

public class Position {
	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	@Override
	public int hashCode() {
		return x * 16 + y;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Position) {
			Position pos = (Position)obj;
			return x == pos.x && y == pos.y;
		}
		else
			return false;
	}
	
	public boolean isValid() {
		return isValid(x, y);
	}
	
	public static boolean isValid(int x, int y) {
		return 0 < x && x < ChessGame.BOARD_WIDTH && 0 < y && y < ChessGame.BOARD_HEIGHT;
	}
	
	public Position move(int dx, int dy) {
		return new Position(x + dx, y + dy);
	}
	
	public String toString() {
		return "(" + x + ", " + y + " " + ")";
	}

	private int x;
	private int y;
}
