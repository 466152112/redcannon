package org.stepinto.redcannon.ai;

import org.stepinto.redcannon.ai.learn.FeatureExtractor;

public class CandidateFeatureExtractor implements FeatureExtractor<CandidateWithProperties> {
	private int dim;
	
	public CandidateFeatureExtractor(int dim) {
		this.dim = dim;
	}
	
	@Override
	public double[] extract(CandidateWithProperties c) {
		double result[] = c.getProperties();
		assert(result.length == dim);
		return result;
	}

	@Override
	public int getDimension() {
		return dim;
	}
}
