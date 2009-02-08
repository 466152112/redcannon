package org.stepinto.redcannon.common;

public abstract class Unit {
	public Unit(int color, int x, int y) {
		this(color, new Position(x, y));
	}
	
	public Unit(int color, Position position) {
		this.color = color;
		this.position = position;
		this.alive = true;
	}
	
	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	public boolean isAlive() {
		return alive;
	}

	public int getColor() {
		return color;
	}
	
	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}
	
	public boolean canMoveTo(BoardImage board, Position pos) {
		Position[] legalMoves = getLegalMoves(board);
		for (Position move : legalMoves)
			if (move.equals(pos))
				return true;
		return false;
	}
	
	public abstract String getChineseSymbol();
	public abstract String getSymbol();
	
	public abstract Position[] getLegalMoves(BoardImage board);  

	private boolean alive;
	private int color;
	private Position position;
}
