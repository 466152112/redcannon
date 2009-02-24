package org.stepinto.redcannon.ai.test;

import java.io.*;
import java.util.*;
import org.stepinto.redcannon.ai.*;
import org.stepinto.redcannon.common.*;

public class EndgameBenchmark {
	private static class WorkerThread extends Thread {
		private File file;
		private String result;
		
		public WorkerThread(File file) {
			this.file = file;
		}
		
		private String getResult() {
			return result;
		}
		
		private String getWinner(int player, int score) {
			if (Math.abs(score) <= 100)
				return "tie";
			else {
				int winner = (score > 0 ? player : GameUtility.getOpponent(player));
				return GameUtility.getColorName(winner);
			}
		}
		
		public void run() {
			try {
				GameState state = FenParser.parseFile(file);
				SearchEngine engine = new SearchEngine(state);
				engine.addEvaluator(new NaiveEvaluator());
				engine.addSelector(new NaiveSelector());
				
				TimeCounter counter = new TimeCounter(true);
				counter.start();
				SearchResult sr = engine.search();
				
				result = String.format("%s\t%s", counter.getTimeString(), getWinner(state.getPlayer(), sr.getScore()));
				return;
			} 
			catch (Exception ex) {
				result = "crash";
			}
		}
	}
	
	public static void main(String args[]) throws Exception {
		int timeLimit = 30;
		File dir = new File("test/endgames");
		
		// parse args
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("--time-limit")) {
				i++;
				timeLimit = Integer.parseInt(args[i]);
			}
			else
				dir = new File(args[i]);
		}
		
		// run benchmark
		Map<File, String> result = new HashMap<File, String>();
		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith("fen");
			}};
		File files[] = dir.listFiles(filter);
		Arrays.sort(files);
		
		for (File file : files) {
			System.out.print(file.getPath() + "\t");
			
			WorkerThread thread = new WorkerThread(file);			
			thread.start();
			thread.join(timeLimit * 1000L);
			if (thread.isAlive()) {
				result.put(file, "timeout");
				thread.interrupt();
			}
			else
				result.put(file, thread.getResult());
			
			System.out.println(" " + result.get(file));
		}
	}
}
