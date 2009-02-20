package org.stepinto.redcannon.ai;

import java.io.*;
import java.util.*;

public class Statistics {
	private int states;
	private int hashHits;
	private int evaluatedStates;
	private int betaCuts;
	private int maxDepth;
	
	public int getNumberOfStates() {
		return states;
	}

	public void increaseStates() {
		states++;
	}
	
	public void increaseHashHits() {
		hashHits++;
	}
	
	public void increaseEvaluatedStates() {
		evaluatedStates++;
	}
	
	public void increaseBetaCuts() {
		betaCuts++;
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
		out.println("Evaluated-states: " + evaluatedStates);
		out.println("Beta-cuts: " + betaCuts);
		out.printf("Average-degree: %.2f\n", getAverageDegree());
	}
}
