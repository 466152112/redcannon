package org.stepinto.redcannon.ai.test;

import java.io.*;
import org.stepinto.redcannon.ai.*;
import org.stepinto.redcannon.common.*;
import org.stepinto.redcannon.ui.*;

public class SelfGameTest {
	public static void main(String args[]) throws Exception {
		// boolean iterative = false;
		File fen = null;
		boolean gui = false;
		int timeLimit = 5000;
		
		// parse args
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("--gui"))
				gui = true;
			else if (args[i].equals("--time-limit")) {
				i++;
				timeLimit = Integer.parseInt(args[i]) * 1000;
			}
			else
				fen = new File(args[i]);
		}
		
		// set up engine
		StateSet historyStates = new StateSet();
		SearchEngine engine = new IterativeSearchEngine();
		engine.addEvaluator(new NaiveEvaluator());
		engine.addSelector(new NaiveSelector(historyStates));
		engine.setTimeLimit(timeLimit);
		
		// go
		BoardImage board = (fen == null ? GameUtility.createStartBoard() : FenParser.parseFile(fen).getBoard());
		BoardWindow window = new BoardWindow(board);
		int player = (fen == null ? ChessGame.RED : FenParser.parseFile(fen).getPlayer());
		
		window.start();
		while (true) {
			board.dump(System.out);
			historyStates.add(board, player);
			
			engine.setInitialBoard(board);
			engine.setInitialPlayer(player);
			engine.clearHash();
			
			TimeCounter counter = new TimeCounter();
			counter.start();
			System.out.printf("%s is thinking...", GameUtility.getColorName(player));
			SearchResult result = engine.search();
			System.out.println(counter.getTimeString());
			result.dump(System.out);
			engine.getStatistics().dump(System.out);
			System.out.println();
			
			board.performMove(result.getBestMove());
			window.setSelectedUnit(result.getBestMove().getTarget());
			window.redraw();
			if (GameUtility.hasPlayerWon(board, player)) {
				System.out.println(GameUtility.getColorName(player) + " has won.");
				return;
			}
			
			player = GameUtility.getOpponent(player);
		}
	}
}
