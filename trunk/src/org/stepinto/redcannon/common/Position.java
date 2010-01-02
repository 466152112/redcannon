package org.stepinto.redcannon.common;

public class Position {
	public Position(int x, int y) {
		this.x = (short)x;
		this.y = (short)y;
	}

	public Position(int value) {
		this.x = (short)(value >> 4);
		this.y = (short)(value & 0xf);
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
		return 0 <= x && x < ChessGame.BOARD_WIDTH && 0 <= y && y < ChessGame.BOARD_HEIGHT;
	}
	
	public Position move(int dx, int dy) {
		return new Position(x + dx, y + dy);
	}
	
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
	
	public int toInteger() {
		return (x << 4) | y;
	}
	
	public static int toInteger(int x, int y) {
		return (x << 4) | y;
	}

	private short x;
	private short y;
}
