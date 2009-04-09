package org.stepinto.redcannon.ai.learn;

import java.io.*;

public class TrainingCase {
	public TrainingCase(double[] x, int y) {
		super();
		this.x = x;
		this.y = y;
	}
	
	public double[] getX() {
		return x;
	}
	
	public double getX(int i) {
		return x[i];
	}
	
	public int getY() {
		return y;
	}
	
	public void dump(PrintStream out) {
		out.print(y);
		for (int i = 0; i < x.length; i++)
			out.printf(" %d:%f", i+1, x[i]);
		out.println();
	}
	
	private double x[];
	private int y;
}
