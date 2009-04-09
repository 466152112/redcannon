package org.stepinto.redcannon.ai.learn;

import java.util.*;
import org.apache.commons.lang.*;

public class RankingSVMComparator<T> implements Comparator<T> {
	private SVM svm;
	private FeatureExtractor<T> extractor;
	
	public RankingSVMComparator(SVM svm, FeatureExtractor<T> extractor) {
		super();
		this.svm = svm;
		this.extractor = extractor;
	}
	
	@Override
	public int compare(T left, T right) {
		assert(left != null);
		assert(right != null);
		
		if (ArrayUtils.isEquals(left, right))
			return 0;
		else {
			double featureLeft[] = extractor.extract(left);
			double featureRight[] = extractor.extract(right);
			
			boolean lessThan1 = svm.predict(diff(featureLeft, featureRight)) < 0;
			boolean lessThan2 = svm.predict(diff(featureRight, featureLeft)) < 0;
			if (lessThan1 && !lessThan2)
				return -1;
			else if (!lessThan1 && lessThan2)
				return 1;
			else
				return 0;
		}
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
}
