package org.stepinto.redcannon.ui;

import javax.swing.*;

import org.eclipse.swt.SWT;
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
	private Display display;
	Shell shell;
	
	public void init() {
		int width = Integer.parseInt(getParameter("width"));
		int height = Integer.parseInt(getParameter("height"));
		setSize(width, height);
		setLayout(new java.awt.GridLayout(1, 1));
		final java.awt.Canvas awtCanvas = new java.awt.Canvas();
		add(awtCanvas);
		 
		windowThread = new Thread() {
			@Override
			public void run() {
				display = new Display();
				shell = SWT_AWT.new_Shell(display, awtCanvas);
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
				StateSet history = new StateSet();

				while (true) {
					// human moves
					history.add(board, humanPlayer);
					Move move = getHumanMove(board, humanPlayer, history);
					board.performMove(move);
					repaint();
					
					if (GameUtility.hasPlayerWon(board, humanPlayer)) {
						showMessage("Human has won!");
						return;
					}
					
					// ai moves
					history.add(board, aiPlayer);
					move = getAiMove(board, aiPlayer, history);
					if (move == null) { // defeated?
						showMessage("AI has given up!"); 
						return;
					}
					board.performMove(move);
					repaint();
					
					if (GameUtility.hasPlayerWon(board, aiPlayer)) {
						showMessage("AI has won!");
						return;
					}
				}
			}
			
			private void showMessage(final String message) {
				display.syncExec(new Runnable() {
					@Override
					public void run() {
						MessageBox messageBox = new MessageBox(shell, SWT.OK | SWT.ICON_INFORMATION);
						messageBox.setMessage(message);
						messageBox.open();
					}
				});
			}
			
			private Move getHumanMove(BoardImage board, int player, StateSet history) {
				return boardWindow.waitForUserMove(player);
			}
			
			private Move getAiMove(BoardImage board, int player, StateSet history) {
				TimeCounter tc = new TimeCounter();
				tc.start();
				
				SearchEngine engine = new IterativeSearchEngine(board.duplicate(), player);
				engine.addEvaluator(new NaiveEvaluator());
				engine.addSelector(new NaiveSelector(history));
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
