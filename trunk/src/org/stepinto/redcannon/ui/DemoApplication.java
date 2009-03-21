package org.stepinto.redcannon.ui;

import java.util.*;

import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.stepinto.redcannon.common.*;
import org.stepinto.redcannon.ai.*;

public class DemoApplication implements Runnable {
	private static final int AI_TIME_LIMIT = 10000;
	
	private BoardImage board;
	private int player;
	private Stack<GameState> undoStack;
	private StateSet history;
	private boolean isBlackAi;
	private boolean isRedAi;
	
	// window controls
	private Shell shell;
	private Display display;
	private BoardControl boardControl;
	private Button undoButton;
	private Button quitButton;
	private Thread currentPlayerThinkThread;
	
	// async
	// wait-for-user-move --> perform-move --> think --> perform-move
	//            +------> undo --> wait-for-user-move...
	private class ThinkThread extends Thread {		
		@Override
		public void run() {	
			SearchEngine engine = new IterativeSearchEngine(board.duplicate(), player);
			engine.addEvaluator(new NaiveEvaluator());
			engine.addSelector(new NaiveSelector(history));
			engine.setTimeLimit(AI_TIME_LIMIT);
				
			SearchResult result = engine.search();
			Move move = result.getBestMove();
			display.syncExec(new PerformMoveThread(move));
			new WaitForNextPlayerThread().start();
		}
	}
	
	private class EnableUndoButtonThread extends Thread {
		private boolean enable = true;
		
		public EnableUndoButtonThread(boolean enable) {
			this.enable = enable;
		}
		
		@Override
		public void run() {
			undoButton.setEnabled(enable);
		}
	} 
	
	private class PerformMoveThread extends Thread {
		private Move move;
		
		public PerformMoveThread(Move move) {
			this.move = move;
		}
		
		@Override
		public void run() {
			undoStack.push(new GameState(board.duplicate(), player));
			board.performMove(move);
			boardControl.redraw();
		}
	}
	
	private boolean canUndo() {
		for (GameState state : undoStack) {
			if (!isAiPlayer(state.getPlayer()))
				return true;
		}
		return false;
	}
	
	private class WaitForUserMoveThread extends Thread {
		@Override
		public void run() {
			try {
				display.syncExec(new EnableUndoButtonThread(canUndo()));
				
				Move move = boardControl.waitForUserMove(player);
				display.syncExec(new PerformMoveThread(move));
				new WaitForNextPlayerThread().start();
				
				display.syncExec(new EnableUndoButtonThread(false));
			}
			catch (InterruptedException ex) {
			}
		}
	}
	
	private class WaitForNextPlayerThread extends Thread {
		@Override
		public void run() {
			player = GameUtility.getOpponent(player);
			if (isCurrentPlayerAi())
				currentPlayerThinkThread = new ThinkThread();
			else
				currentPlayerThinkThread = new WaitForUserMoveThread();
			currentPlayerThinkThread.start();
		}
	}
	
	public DemoApplication() {
		this(GameUtility.createStartBoard());
	}
	
	public DemoApplication(BoardImage board) {
		this(new Display(), new Shell(), board);
	}
	
	public DemoApplication(Display display, Shell shell, BoardImage board) {
		this.isRedAi = false;
		this.isBlackAi = true;
		this.undoStack = new Stack<GameState>();
		this.history = new StateSet();
		
		this.display = display;
		this.shell = shell;
		this.board = board;
		   
		// set layout
		shell.setLayout(new GridLayout(2, false));
		
		// create controls
		// board control
		GridData boardControlGridData = new GridData();
		boardControl = new BoardControl(shell, board);
		boardControlGridData.minimumWidth = boardControl.getSize().x;
		boardControlGridData.minimumHeight = boardControl.getSize().y;
		boardControlGridData.grabExcessHorizontalSpace = true;
		boardControlGridData.grabExcessVerticalSpace = true;
		boardControl.setLayoutData(boardControlGridData);
		
		// panel
		Composite panel = new Composite(shell, SWT.NONE);
		panel.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		panel.setLayout(new GridLayout(1, false));
		undoButton = new Button(panel, SWT.PUSH);
		undoButton.setText("&Undo");
		undoButton.setEnabled(false);
		undoButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
		undoButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				undoButtonClicked();
			}
		});
		quitButton = new Button(panel, SWT.PUSH);
		quitButton.setText("&Quit");
		quitButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
		quitButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				quitButtonClicked();
			}
		});
		panel.pack();
		
		shell.pack();
		shell.open();
	}
	
	public void quitButtonClicked() {
		System.exit(0);
	}
	
	public void undoButtonClicked() {
		if (!isCurrentPlayerAi()) {
			currentPlayerThinkThread.interrupt();
			
			history.remove(board, player);
			while (isAiPlayer(undoStack.peek().getPlayer())) {
				GameState state = undoStack.pop();
				history.remove(state.getBoard(), state.getPlayer());
			}
			
			GameState state = undoStack.pop();
			board = state.getBoard();
			player = state.getPlayer();
			
			boardControl.setBoard(board);
			boardControl.redraw();
			
			currentPlayerThinkThread = new WaitForUserMoveThread();
			currentPlayerThinkThread.start();
		}
	}
	
	private boolean isAiPlayer(int player) {
		return player == ChessGame.BLACK ? isBlackAi : isRedAi;
	}
	
	private boolean isCurrentPlayerAi() {
		return isAiPlayer(player);
	}
	
	@Override
	public void run() {
		player = ChessGame.BLACK;
		new WaitForNextPlayerThread().start();
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
	
	public static void main(String args[]) throws InterruptedException {
		new DemoApplication().run();
	}
}
