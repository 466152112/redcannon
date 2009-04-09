package org.stepinto.redcannon.ai.learn;

import java.io.*;
import java.util.*;
import liblinear.*;

public class SVM {
	private List<TrainingCase> cases = new ArrayList<TrainingCase>();
	private double maxX[];
	private double minX[];
	private int dim;
	private Model model;
	
	public SVM(int dim) {
		this.dim = dim;
	}
	
	public void clear() {
		cases.clear();
	}
	
	public void add(double x[], int y) {
		assert(x != null);
		assert(x.length == dim);
		
		cases.add(new TrainingCase(x, y));	
	}
	
	public void train() {
		// scale
		maxX = new double[dim];
		minX = new double[dim];
		for (int i = 0; i < dim; i++) {
			maxX[i] = Double.MIN_VALUE;
			minX[i] = Double.MAX_VALUE;
		}
		for (TrainingCase c : cases)
			for (int i = 0; i < dim; i++) {
				maxX[i] = Math.max(maxX[i], c.getX(i));
				minX[i] = Math.min(minX[i], c.getX(i));
			}
		
		// set up problem 
		Problem problem = new Problem();
		problem.l = cases.size();
		problem.n = dim;
		problem.x = new FeatureNode[cases.size()][dim];
		for (int i = 0; i < cases.size(); i++) {
			double scaledX[] = scale(cases.get(i).getX());
			for (int j = 0; j < dim; j++)
				problem.x[i][j] = new FeatureNode(j+1, scaledX[j]);
		}
		problem.y = new int [cases.size()];
		for (int i = 0; i < cases.size(); i++)
			problem.y[i] = cases.get(i).getY();
		
		// train
		Parameter param = new Parameter(SolverType.L1LOSS_SVM_DUAL, 1, 0.1);
		Linear.disableDebugOutput();
		model = Linear.train(problem, param);
	}
	
	public int predict(double x[]) {
		assert(x != null);
		assert(x.length == dim);
		double scaledX[] = scale(x);
		
		FeatureNode nodes[] = new FeatureNode[dim];
		for (int i = 0; i < dim; i++)
			nodes[i] = new FeatureNode(i+1, scaledX[i]);
		return Linear.predict(model, nodes);
	}
	
	private double[] scale(double x[]) {
		assert(x != null);
		assert(x.length == dim);
		
		double result[] = new double [dim];
		for (int i = 0; i < dim; i++) {
			double mean = (maxX[i] + minX[i]) / 2;
			double range = maxX[i] - minX[i];
			result[i] = (x[i] - mean) / range * 2;
		}
		return result;
//		return x;
	}
	
	public void dump(PrintStream out) {
		assert(out != null);
		
		for (TrainingCase t : cases)
			t.dump(out);
	}
	
	public int size() {
		return cases.size();
	}
}
