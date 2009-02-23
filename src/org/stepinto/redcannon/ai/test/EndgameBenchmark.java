package org.stepinto.redcannon.ai.test;

import java.io.*;
import java.util.*;

import org.stepinto.redcannon.ai.NaiveEvaluator;
import org.stepinto.redcannon.ai.NaiveSelector;
import org.stepinto.redcannon.ai.SearchEngine;
import org.stepinto.redcannon.ai.SearchResult;
import org.stepinto.redcannon.common.FenParser;
import org.stepinto.redcannon.common.GameState;
import org.stepinto.redcannon.common.GameUtility;
import org.stepinto.redcannon.common.TimeCounter;

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
		boolean verbose = false;
		
		// parse args
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("--time-limit")) {
				i++;
				timeLimit = Integer.parseInt(args[i]);
			}
			else if (args[i].equals("--verbose"))
				verbose = true;
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
			if (verbose)
				System.out.print("Processing " + file.getPath() + "...");
			
			WorkerThread thread = new WorkerThread(file);			
			thread.start();
			thread.join(timeLimit * 1000L);
			if (thread.isAlive()) {
				result.put(file, "timeout");
				thread.interrupt();
			}
			else
				result.put(file, thread.getResult());
			
			if (verbose)
				System.out.println(" " + result.get(file).split("\t")[0]);
		}
	}
}
