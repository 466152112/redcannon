package org.stepinto.redcannon.ui;

import org.stepinto.redcannon.common.*;

public class UserMove {
	private Position source;
	private Position target;
	
	public UserMove(Position source, Position target) {
		super();
		this.source = source;
		this.target = target;
	}
	
	public Position getSouce() {
		return source;
	}

	public Position getTarget() {
		return target;
	}
	
	public String toString() {
		return source + " -> " + target;
	}
}
