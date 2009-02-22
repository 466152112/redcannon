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

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Move other = (Move) obj;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		return true;
	}
}
