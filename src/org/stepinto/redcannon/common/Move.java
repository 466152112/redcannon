package org.stepinto.redcannon.common;

public class Move {
	private Position source;
	private Position target;

	public Move(Position source, Position target) {
		super();
		this.source = source;
		this.target = target;
	}

	public Position getSource() {
		return source;
	}

	public Position getTarget() {
		return target;
	}
	
	public String toString() {
		return source + " --> " + target;
	}
}
