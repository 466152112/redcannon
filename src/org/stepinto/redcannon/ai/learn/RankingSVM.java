package org.stepinto.redcannon.ai.learn;

import java.io.*;
import java.util.*;
import org.apache.commons.lang.*;

public class RankingSVM<T> {
	private FeatureExtractor<T> extractor;
	private SVM svm;

	public RankingSVM(FeatureExtractor<T> extractor) {
		super();
		this.extractor = extractor;
		this.svm = new SVM(extractor.getDimension());
	}
	
	public void add(T obj1, T obj2) {
		double feature1[] = extractor.extract(obj1);
		double feature2[] = extractor.extract(obj2);
		// svm.add(ArrayUtils.addAll(feature1, feature2), -1);
		// svm.add(ArrayUtils.addAll(feature2, feature1), 1);
		svm.add(diff(feature1, feature2), -1);
		svm.add(diff(feature2, feature1), 1);
	}
	
	public void addRaw(double x[], int y) {
		assert(y == 1 || y == -1);
		svm.add(x, y);
	}
	
	private double[] diff(double a[], double b[]) {
		assert(a != null);
		assert(b != null);
		assert(a.length == b.length);
		
		double c[] = new double [a.length];
		for (int i = 0; i < a.length; i++)
			c[i] = a[i] - b[i];
		return c;
	}
	
	public void train() {
		svm.train();
	}
	
	public void dump(PrintStream out) {
		assert(out != null);
		svm.dump(out);
	}
	
	public Comparator<T> getComparator() {
		return new RankingSVMComparator<T>(svm, extractor);
	}
	
	public int size() {
		return svm.size();
	}
}
