package org.stepinto.redcannon.ai;

import java.io.*;

public class Statistics {
	private int states;
	private int hashHits;
	private int evaluatedStates;
	private int betaCuts;
	private int maxDepth;
	private int hashMisses;
	
	public int getNumberOfStates() {
		return states;
	}
	
	public int getNumberOfBetaCuts() {
		return betaCuts;
	}
	
	public int getNumberOfHashHits() {
		return hashHits;
	}
	
	public int getNumberOfHashMisses() {
		return hashMisses;
	}
	
	public int getNumberOfEvaluatedStates() {
		return evaluatedStates;
	}

	public void increaseStates() {
		states++;
	}
	
	public void increaseStates(int n) {
		states += n;
	}
	
	public void increaseHashHits() {
		hashHits++;
	}
	
	public void increaseHashHits(int n) {
		hashHits += n;
	}
	
	public void increaseEvaluatedStates() {
		evaluatedStates++;
	}
	
	public void increaseEvaluatedStates(int n) {
		evaluatedStates += n;
	}
	
	public void increaseBetaCuts() {
		betaCuts++;
	}
	
	public void increaseBetaCuts(int n) {
		betaCuts += n;
	}
	
	public void increaseHashMisses() {
		hashMisses++;
	}
	
	public void increaseHashMisses(int n) {
		hashMisses += n;
	}
	
	public void updateMaxDepth(int depth) {
		if (depth > maxDepth)
			maxDepth = depth;
	}
	
	public double getAverageDegree() {
		double left = 1;
		double right = Integer.MAX_VALUE;
		
		for (int i = 0; i < 64; i++) {
			double mid = left + (right - left) / 2;
			double tmpSum = (1 - Math.pow(mid, maxDepth)) / (1 - mid);
			if (tmpSum < states)
				left = mid;
			else
				right = mid;
		}
		return right;
	}
	
	public void dump(PrintStream out) {
		out.println("States: " + states);
		out.println("Hash-hits: " + hashHits);
		out.println("Hash-misses: " + hashMisses);
		out.println("Evaluated-states: " + evaluatedStates);
		out.println("Beta-cuts: " + betaCuts);
		out.println("Max-depth: " + maxDepth);
		out.printf("Average-degree: %.2f\n", getAverageDegree());
	}
}
