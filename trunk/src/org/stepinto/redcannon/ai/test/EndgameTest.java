package org.stepinto.redcannon.ai.test;

import java.io.*;
import java.util.*;
import org.stepinto.redcannon.common.*;
import org.stepinto.redcannon.ai.*;
import org.stepinto.redcannon.ai.log.*;

public class EndgameTest {
	public void run(File file, boolean debug, boolean iterative) throws InvalidFenFormatException, IOException {
		GameState state = FenParser.parseFile(file);
		SearchEngine engine = (iterative ? new IterativeSearchEngine(state) : new NaiveSearchEngine(state));
		engine.addEvaluator(new NaiveEvaluator());
		engine.addSelector(new NaiveSelector());
		
		if (debug) {
			File logFile = new File(file.getAbsolutePath() + ".log");
			SearchLogger logger = new SearchLogger(new FileOutputStream(logFile));
			engine.setLogger(logger);
		}
		
		System.out.println(file.getPath());
		state.getBoard().dump(System.out);
		
		SearchResult result = engine.search();
		result.dump(System.out);
		engine.getStatistics().dump(System.out);
	}
	
	public static void main(String args[]) throws Exception {
		boolean debug = false;
		boolean iterative = false;
		File file = new File("test/endgames");
		
		// parse arg
		for (String arg : args)
			if (arg.equals("--debug"))
				debug = true;
			else if (arg.equals("--iterative"))
				iterative = true;
			else
				file = new File(arg);
				
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
				new EndgameTest().run(f, debug, iterative);
		}
		else {
			new EndgameTest().run(file, debug, iterative);
		}
	}
}
