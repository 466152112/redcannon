package org.stepinto.redcannon.ui;

import java.io.*;
import java.util.*;
import org.stepinto.redcannon.common.*;
import org.stepinto.redcannon.ai.*;
import org.stepinto.redcannon.ai.log.*;

public class DemoApplication {
	public void run(File file, boolean debug, boolean iterative, int timeLimit) throws InvalidFenFormatException, IOException {
		GameState state = FenParser.parseFile(file);
		SearchEngine engine = (iterative ? new IterativeSearchEngine(state) : new NaiveSearchEngine(state));
		engine.addEvaluator(new NaiveEvaluator());
		engine.addSelector(new NaiveSelector());
		engine.setTimeLimit(timeLimit);
		
		if (debug) {
			File logFile = new File(file.getAbsolutePath() + ".log");
			SearchLogger logger = new SearchLogger(new FileOutputStream(logFile));
			engine.setLogger(logger);
		}
		
		System.out.println(file.getPath());
		state.getBoard().dump(System.out);
		
		TimeCounter counter = new TimeCounter();
		counter.start();
		SearchResult result = engine.search();
		result.dump(System.out);
		engine.getStatistics().dump(System.out);
		System.out.println("Time: " + counter.getTimeString());
	}
	
	public static void main(String args[]) throws Exception {
		boolean debug = false;
		boolean iterative = false;
		int timeLimit = Integer.MAX_VALUE;
		File file = new File("test/endgames");
		
		// parse arg
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("--debug"))
				debug = true;
			else if (args[i].equals("--iterative"))
				iterative = true;
			else if (args[i].equals("--time-limit")) {
				i++;
				timeLimit = Integer.parseInt(args[i]) * 1000;
			}
			else
				file = new File(args[i]);
		}
				
		// process
		if (file.isDirectory()) {
			FilenameFilter filter = new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith("fen");
				}};
				
			File[] files = file.listFiles(filter);
			Arrays.sort(files);
			for (File f : files)
				new DemoApplication().run(f, debug, iterative, timeLimit);
		}
		else {
			new DemoApplication().run(file, debug, iterative, timeLimit);
		}
	}
}
