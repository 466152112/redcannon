package org.stepinto.redcannon.ai.learn;

public interface FeatureExtractor<T> {
	public int getDimension();
	public double[] extract(T obj);
}
