package org.stepinto.redcannon.ui;

import java.io.*;
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
	private Button newButton;
	private Button undoButton;
	private Button quitButton;
	private Text debugMessageTextBox; 
	
	private Thread currentPlayerThinkThread;
	
	// async
	// wait-for-user-move --> perform-move --> think --> perform-move
	//            +------> undo --> wait-for-user-move...
	private class WaitForAiMoveThread extends Thread {		
		@Override
		public void run() {	
			printDebugMessage("Waiting for AI to move...");
			
			SearchEngine engine = new IterativeSearchEngine(board.duplicate(), player);
			TimeCounter counter = new TimeCounter(); 
			engine.addEvaluator(new NaiveEvaluator());
			engine.addSelector(new NaiveSelector(history));
			engine.setTimeLimit(AI_TIME_LIMIT);
			counter.start();
			
			SearchResult result = engine.search();
			Move move = result.getBestMove();
			printDebugMessage(String.format(" %s\n", counter.getTimeString()));
			printDebugMessage(result);
			printDebugMessage(engine.getStatistics());
			
			if (move == null)
				printDebugMessage("You have won!");
			else {
				display.syncExec(new PerformMoveThread(move));
				new WaitForNextPlayerThread().start();
			}
		}
		
		private void printDebugMessage(SearchResult result) {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			result.dump(new PrintStream(stream));
			printDebugMessage(stream.toString());
		}
		
		private void printDebugMessage(Statistics stat) {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			stat.dump(new PrintStream(stream));
			printDebugMessage(stream.toString());
		}
		
		private void printDebugMessage(String message) {
			display.syncExec(new PrintDebugMessageThread(message));
		}
	}
	
	private class PrintDebugMessageThread extends Thread {
		private String message;
		
		public PrintDebugMessageThread(String message) {
			this.message = message;
		}
		
		@Override
		public void run() {
			debugMessageTextBox.append(message);
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
				display.syncExec(new PrintDebugMessageThread("Waiting for user to move...\n"));
				
				Move move = boardControl.waitForUserMove(player);
				display.syncExec(new PerformMoveThread(move));
				display.syncExec(new PrintDebugMessageThread(String.format("User moves: %s\n", move)));
				display.syncExec(new EnableUndoButtonThread(false));
				new WaitForNextPlayerThread().start();
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
				currentPlayerThinkThread = new WaitForAiMoveThread();
			else
				currentPlayerThinkThread = new WaitForUserMoveThread();
			currentPlayerThinkThread.start();
		}
	}
	
	public DemoApplication() {
		this(new Display(), new Shell());
	}
	
	public DemoApplication(Display display, Shell shell) {
		this.display = display;
		this.shell = shell;
	}
	
	private void newGame(BoardImage board) {
		this.isRedAi = false;
		this.isBlackAi = true;
		this.undoStack = new Stack<GameState>();
		this.history = new StateSet();
		this.board = board;
		player = ChessGame.BLACK;
		
		if (currentPlayerThinkThread != null)
			currentPlayerThinkThread.interrupt();
		new WaitForNextPlayerThread().start();
	}
	
	private void newButtonClicked() {
		newGame(GameUtility.createStartBoard());
		boardControl.setBoard(board);
		boardControl.redraw();
		
		printDebugMessage("New game started.\n");
	}
	
	private void quitButtonClicked() {
		System.exit(0);
	}
	
	private void undoButtonClicked() {
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
	
	private void createWindow() {
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
		newButton = new Button(panel, SWT.PUSH);
		newButton.setText("&New");
		newButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
		newButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				newButtonClicked();
			}
		});
		
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
		
		// debug-message-text-box
		debugMessageTextBox = new Text(shell, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		debugMessageTextBox.setEditable(false);
		GridData debugMessageTextBoxGridData = new GridData();
		debugMessageTextBoxGridData.horizontalSpan = 2;
		debugMessageTextBoxGridData.horizontalAlignment = GridData.FILL;
		debugMessageTextBoxGridData.heightHint = 100;
		debugMessageTextBox.setLayoutData(debugMessageTextBoxGridData);
		panel.pack();
		
		shell.pack();
		shell.open();
	}
	
	private void printDebugMessage(String message) {
		debugMessageTextBox.append(message);
	}
	
	@Override
	public void run() {
		newGame(GameUtility.createStartBoard());
		createWindow();
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
