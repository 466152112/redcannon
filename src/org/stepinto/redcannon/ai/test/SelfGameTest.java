package org.stepinto.redcannon.ai.test;

import java.io.*;
import org.stepinto.redcannon.ai.*;
import org.stepinto.redcannon.common.*;

public class SelfGameTest {
	public static void main(String args[]) throws Exception {
		// boolean iterative = false;
		File fen = null;
		
		// parse args
		if (args.length > 0)
			fen = new File(args[0]);
		
		// set up engine
		SearchEngine engine = new IterativeSearchEngine();
		engine.addEvaluator(new NaiveEvaluator());
		engine.addSelector(new NaiveSelector());
		engine.setTimeLimit(5000);
		
		// go
		BoardImage board = (fen == null ? GameUtility.createStartBoard() : FenParser.parseFile(fen).getBoard());
		int player = (fen == null ? ChessGame.RED : FenParser.parseFile(fen).getPlayer());
		
		while (true) {
			board.dump(System.out);
			
			engine.setInitialBoard(board);
			engine.setInitialPlayer(player);
			
			TimeCounter counter = new TimeCounter();
			counter.start();
			System.out.printf("%s is thinking...", GameUtility.getColorName(player));
			SearchResult result = engine.search();
			System.out.println(counter.getTimeString());
			result.dump(System.out);
			System.out.println();
			
			board.performMove(result.getBestMove());
			if (GameUtility.hasPlayerWon(board, player)) {
				System.out.println(GameUtility.getColorName(player) + " has won.");
				return;
			}
			
			player = GameUtility.getOpponent(player);
		}
	}
}
