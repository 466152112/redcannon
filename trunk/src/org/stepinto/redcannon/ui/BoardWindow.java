package org.stepinto.redcannon.ui;

import org.eclipse.swt.widgets.*;

import org.stepinto.redcannon.common.*;

public class BoardWindow extends Thread {
	private static final String WINDOW_TITLE = "RedCannon";
	
	private BoardImage board;
	
	// ui widgets
	private Display display;
	private Shell shell;
	private BoardControl boardControl;
	
	public BoardWindow(BoardImage board) {
		this(null, null, board);
	}

	public BoardWindow(Display display, Shell shell, BoardImage board) {
		this.display = display;
		this.shell = shell;
		this.board = board;
		if (display != null && shell != null)
			this.boardControl = new BoardControl(shell, board);
	}

	private void runMainLoop() {
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	private void cleanUp() {
		boardControl.dispose();
		shell.dispose();
		display.dispose();
	}

	public void run() {
		createWindow();
		runMainLoop();
		cleanUp();
	}

	private void createWindow() {
		if (display == null)
			display = new Display();
		if (shell == null)
			shell = new Shell();
		shell.setText(WINDOW_TITLE);
		if (boardControl == null)
			boardControl = new BoardControl(shell, board);
		shell.pack();
		shell.open();
	}

	public static void main(String args[]) throws InterruptedException {
		BoardImage board = GameUtility.createStartBoard();
		BoardWindow window = new BoardWindow(board);
		window.start();
		while (true)
			System.out.println(window.waitForUserMove());
	}

	public void setSelectedUnit(Position pos) {
		boardControl.setSelectedUnit(pos);
	}
	
	public Move waitForUserMove() throws InterruptedException {
		return waitForUserMove(ChessGame.EMPTY);
	}
	
	public Move waitForUserMove(int player) throws InterruptedException {
		while (boardControl == null)
			Thread.sleep(100);
		return boardControl.waitForUserMove(player);
	}
	
	public void redraw() {
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				boardControl.redraw();
			}
		});
	}
}
