package org.stepinto.redcannon.ai.learn;

import java.util.*;

public class WorkQueue {
	private static class WorkerThread extends Thread {
		private List<Runnable> queue;
		
		public WorkerThread(List<Runnable> queue) {
			this.queue = queue;
		}
		
		@Override
		public void run() {
			while (!queue.isEmpty()) {
				Runnable task = queue.remove(0);
				task.run();
			}
		}
	}
	
	private Thread threads[];
	private List<Runnable> queue;
	
	public WorkQueue(int threadNum) {
		queue = Collections.synchronizedList(new LinkedList<Runnable>());
		threads = new Thread[threadNum];
		for (int i = 0; i < threadNum; i++)
			threads[i] = new WorkerThread(queue);
	}
	
	public void addTask(Runnable task) {
		queue.add(task);
	}
	
	public void executeAll() {
		for (Thread t : threads)
			t.start();
		for (Thread t : threads)
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}
}
