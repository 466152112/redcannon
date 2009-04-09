package org.stepinto.redcannon.ai;

import org.stepinto.redcannon.common.*;

public class CandidateWithProperties extends Candidate {
	private double prop[];
	
	public CandidateWithProperties(Move move, int priority, String reason, double prop[]) {
		super(move, priority, reason);
		this.prop = prop;
	}
	
	public double[] getProperties() {
		return prop;
	}
}
