package org.stepinto.redcannon.ui;

import java.io.*;
import org.stepinto.redcannon.ai.*;
import org.stepinto.redcannon.common.*;

public class EndgameDemo {
	private static Move getAiMove(BoardImage board, int player) {
		System.out.print("Wait for AI-player...");
		TimeCounter tc = new TimeCounter();
		tc.start();
		
		NaiveSearchEngine engine = new NaiveSearchEngine(board.duplicate(), player);
		engine.addEvaluator(new NaiveEvaluator());
		engine.addSelector(new NaiveSelector());
		
		System.out.println(" " + tc.getTimeString());
		SearchResult result = engine.search();
		
		result.dump(System.out);
		engine.getStatistics().dump(System.out);
		return result.getBestMove();
	}
	
	private static Move getHumanMove(BoardImage board, int player, BoardWindow window) {
		System.out.println("Wait for human-player...");
		Move move = window.waitForUserMove();
		System.out.println(move);
		while (board.getColorAt(move.getSource()) != player || !RuleEngine.isLegalMove(board, move)) {
			System.out.println("Invalid move!");
			move = window.waitForUserMove();
			System.out.println(move);
		}
		return move;
	}
	
	public static void main(String args[]) throws Exception {
		File file = new File(args[0]);
		GameState state = FenParser.parseFile(file);
		BoardImage board = state.getBoard();
		int humanPlayer = state.getPlayer();
		int aiPlayer = GameUtility.getOpponent(humanPlayer);
		BoardWindow window = new BoardWindow(board);
		window.start();
		
		while (true) {
			// human moves
			Move move = getHumanMove(board, humanPlayer, window);
			board.performMove(move);
			window.redraw();
			
			if (GameUtility.hasPlayerWon(board, humanPlayer)) {
				System.out.println("Human has won!");
				return;
			}
			
			// ai moves
			move = getAiMove(board, aiPlayer);
			board.performMove(move);
			window.redraw();
			
			if (GameUtility.hasPlayerWon(board, aiPlayer)) {
				System.out.println("AI has won!");
				return;
			}
		}
	}
}
