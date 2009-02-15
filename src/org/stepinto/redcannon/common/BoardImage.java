package org.stepinto.redcannon.common;

public class BoardImage {
	public BoardImage() {
		units = new byte[256];
		colors = new byte[256];
	}

	public boolean isEmptyAt(Position pos) {
		return colors[pos.toInteger()] == ChessGame.EMPTY;
	}

	public boolean isEmptyAt(int x, int y) {
		return colors[Position.toInteger(x, y)] == ChessGame.EMPTY;
	}

	public int getUnitAt(Position pos) {
		return (int)units[pos.toInteger()];
	}

	public int getUnitAt(int x, int y) {
		return (int)units[Position.toInteger(x, y)];
	}

	public int getColorAt(Position pos) {
		return (int)colors[pos.toInteger()];
	}

	public int getColorAt(int x, int y) {
		return (int)colors[Position.toInteger(x, y)];
	}

	public void setColorAt(Position pos, int color) {
		colors[pos.toInteger()] = (byte)color;
	}
	
	public void setColorAt(int x, int y, int color) {
		colors[Position.toInteger(x, y)] = (byte)color;
	}
	
	public void setUnitAt(Position pos, int unit) {
		units[pos.toInteger()] = (byte)unit;
	}
	
	public void setUnitAt(int x, int y, int unit) {
		units[Position.toInteger(x, y)] = (byte)unit;
	}	
	
	public boolean equals(Object obj) {
		if (obj instanceof BoardImage) {
			BoardImage board = (BoardImage)obj;
			for (int i = 0; i < colors.length; i++) {
				if (colors[i] != board.colors[i] || units[i] != board.units[i])
					return false;
			}
			return true;
		}
		else
			return false;
	}

	private byte units[];
	private byte colors[];
}
