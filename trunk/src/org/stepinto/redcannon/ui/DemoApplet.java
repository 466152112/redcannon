package org.stepinto.redcannon.ui;

import javax.swing.*;
import org.eclipse.swt.awt.*;
import org.eclipse.swt.widgets.*;
import org.stepinto.redcannon.ai.*;
import org.stepinto.redcannon.common.*;

public class DemoApplet extends JApplet {
	private static final long serialVersionUID = 1L;
	private static final int AI_TIME_LIMIT = 5000;
	private BoardWindow boardWindow;
	private BoardImage board;
	private Thread windowThread;
	private Thread gameThread;
	
	public void init() {
		int width = Integer.parseInt("width");
		int height = Integer.parseInt("height");
		setSize(width, height);
		setLayout(new java.awt.GridLayout(1, 1));
		final java.awt.Canvas awtCanvas = new java.awt.Canvas();
		add(awtCanvas);
		 
		windowThread = new Thread() {
			@Override
			public void run() {
				Display display = new Display();
				Shell shell = SWT_AWT.new_Shell(display, awtCanvas);
				board = GameUtility.createStartBoard();
				boardWindow = new BoardWindow(display, shell, board);
				gameThread.start();
				boardWindow.run();
			}
		};
		
		gameThread = new Thread() {
			@Override
			public void run() {
				int humanPlayer = ChessGame.RED;
				int aiPlayer = ChessGame.BLACK;

				while (true) {
					// human moves
					Move move = getHumanMove(board, humanPlayer);
					board.performMove(move);
					repaint();
					
					if (GameUtility.hasPlayerWon(board, humanPlayer)) {
						System.out.println("Human has won!");
						return;
					}
					
					// ai moves
					move = getAiMove(board, aiPlayer);
					if (move == null) { // defeated?
						System.out.println("AI has given up!"); 
						return;
					}
					board.performMove(move);
					repaint();
					
					if (GameUtility.hasPlayerWon(board, aiPlayer)) {
						System.out.println("AI has won!");
						return;
					}
				}
			}
			
			private Move getHumanMove(BoardImage board, int player) {
				return boardWindow.waitForUserMove(player);
			}
			
			private Move getAiMove(BoardImage board, int player) {
				TimeCounter tc = new TimeCounter();
				tc.start();
				
				SearchEngine engine = new IterativeSearchEngine(board.duplicate(), player);
				engine.addEvaluator(new NaiveEvaluator());
				engine.addSelector(new NaiveSelector());
				engine.setTimeLimit(AI_TIME_LIMIT);
				
				SearchResult result = engine.search();
				return result.getBestMove();
			}
		};
		
		windowThread.start();
	}
	
	public void stop() {
		windowThread.interrupt();
		gameThread.interrupt();
	}
}
