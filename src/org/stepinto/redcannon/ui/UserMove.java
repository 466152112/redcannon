package org.stepinto.redcannon.ui;

import org.stepinto.redcannon.common.*;

public class UserMove {
	private Unit unit;
	private Position target;
	
	public UserMove(Unit unit, Position target) {
		super();
		this.unit = unit;
		this.target = target;
	}
	
	public Unit getUnit() {
		return unit;
	}

	public Position getTarget() {
		return target;
	}
	
	public String toString() {
		return unit.getPosition() + " -> " + target;
	}
}
