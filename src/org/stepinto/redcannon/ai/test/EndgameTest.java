package org.stepinto.redcannon.ai.test;

import java.io.*;
import org.stepinto.redcannon.common.*;
import org.stepinto.redcannon.ai.*;
import org.stepinto.redcannon.ai.log.*;

public class EndgameTest {
	private File endgameTestFile = null;
	private boolean debug = false;
	private boolean iterative = false;
	private boolean ranking = false;
	private int timeLimit = 30 * 1000;
	private File rankingDataFile = null;
	private boolean dumpRanking = false;
	
	public void setDumpRanking(boolean dumpRanking) {
		this.dumpRanking = dumpRanking;
	}

	public void setEndgameTestFile(File endgameTestFile) {
		this.endgameTestFile = endgameTestFile;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public void setIterative(boolean iterative) {
		this.iterative = iterative;
	}

	public void setRanking(boolean ranking) {
		this.ranking = ranking;
	}

	public void setTimeLimit(int timeLimit) {
		this.timeLimit = timeLimit;
	}

	public void setRankingDataFile(File rankingDataFile) {
		this.rankingDataFile = rankingDataFile;
	}

	public void run() throws InvalidFenFormatException, IOException {
		GameState state = FenParser.parseFile(endgameTestFile);
		SearchEngine engine = (iterative ? new IterativeSearchEngine(state) : new NaiveSearchEngine(state));
		engine.addEvaluator(new NaiveEvaluator());
		engine.setTimeLimit(timeLimit);
		
		if (debug) {
			File logFile = new File(endgameTestFile.getAbsolutePath() + ".log");
			SearchLogger logger = new SearchLogger(new FileOutputStream(logFile));
			engine.setLogger(logger);
		}
		if (ranking) {
			RankingSelector selector = new RankingSelector(null, null);
			System.out.printf("Learning to rank from %s...", rankingDataFile.getPath());  
			selector.train(rankingDataFile);
			System.out.println();
			engine.addSelector(selector);
		}
		else if (dumpRanking) {
			PrintStream out = new PrintStream(rankingDataFile);
			RankingSelector selector = new RankingSelector(null, out);
			engine.addSelector(selector);
		}
		else {
			engine.addSelector(new NaiveSelector());
		}
		
		System.out.println(endgameTestFile.getPath());
		state.getBoard().dump(System.out);
		
		TimeCounter counter = new TimeCounter();
		counter.start();
		SearchResult result = engine.search();
		result.dump(System.out);
		engine.getStatistics().dump(System.out);
		System.out.println("Time: " + counter.getTimeString());
	}
	
	private static void printHelp() {
		System.out.println("Usage:");
		System.out.println("    EndgameTest ENDGFAME-FILE [--iterative] [--rank RANKING-DATA-FILE] [--dump-rank RANKING-DATA-FILE] [--time-limit TIME-LIMIT-IN-SEC]");
	}
	
	public static void main(String args[]) throws Exception {
		EndgameTest app = new EndgameTest();
		
		// parse arg
		if (args.length == 0) {
			printHelp();
			System.exit(1);
		}
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("--debug"))
				app.setDebug(true);
			else if (args[i].equals("--iterative"))
				app.setIterative(true);
			else if (args[i].equals("--rank") && i+1 < args.length) {
				i++;
				app.setRanking(true);
				app.setRankingDataFile(new File(args[i]));
			}
			else if (args[i].equals("--dump-rank") && i+1 < args.length) {
				i++;
				app.setDumpRanking(true);
				app.setRankingDataFile(new File(args[i]));
			}
			else if (args[i].equals("--time-limit") && i+1 < args.length) {
				i++;
				app.setTimeLimit(Integer.parseInt(args[i]) * 1000);
			}
			else
				app.setEndgameTestFile(new File(args[i]));
		}
				
		// process
		app.run();
	}
}
