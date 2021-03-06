package org.stepinto.redcannon.common;

public class TimeCounter {
	private long startTime;
	
	public TimeCounter() {
	}
	
	public TimeCounter(boolean startNow) {
		if (startNow)
			start();
	}
	
	public void start() {
		startTime = System.currentTimeMillis();
	}
	
	public long getTimeMillis() {
		return System.currentTimeMillis() - startTime;
	}
	
	public String getTimeString() {
		long time = getTimeMillis();
		return time/1000 + "." + time/10%100 + "s";
	}
}
