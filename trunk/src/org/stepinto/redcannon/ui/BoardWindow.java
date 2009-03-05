package org.stepinto.redcannon.ui;

import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

import org.stepinto.redcannon.common.*;

public class BoardWindow extends Thread {
	private static final String WINDOW_TITLE = "RedCannon";

	private static final int GRID_SIZE = 55;
	private static final int MARGIN = 45;
	private static final int UNIT_RADIUS = 22;

	private static final int CANVAS_WIDTH = ChessGame.BOARD_WIDTH * GRID_SIZE
			+ MARGIN * 2;
	private static final int CANVAS_HEIGHT = ChessGame.BOARD_HEIGHT * GRID_SIZE
			+ MARGIN * 2;

	private static final RGB GRID_COLOR = new RGB(149, 114, 59);
	private static final RGB BACKGROUND_COLOR = new RGB(240, 240, 10);
	private static final RGB RED_UNIT_TEXT_COLOR = new RGB(204, 0, 0);
	private static final RGB BLACK_UNIT_TEXT_COLOR = new RGB(0, 0, 0);
	private static final RGB UNIT_BACKGROUND_COLOR = new RGB(255, 237, 162);

	// state
	private static final int NO_INTERACTION = 0;
	private static final int WAIT_FOR_SELECT_UNIT = 1;
	private static final int WAIT_FOR_SELECT_TARGET = 2;

	private BoardImage board;
	private int state;
	private Position targetPos;
	private Position sourcePos;

	private Font unitTextFont;

	// ui widgets
	private Display display;
	private Shell shell;
	private Canvas canvas;

	public BoardWindow() {
		this(new BoardImage());
	}

	public BoardWindow(BoardImage board) {
		this.board = board;
		this.state = NO_INTERACTION;
	}

	public Move waitForUserMove() {
		state = WAIT_FOR_SELECT_UNIT;
		while (targetPos == null || sourcePos == null)
			sleep(100);
		Move ret = new Move(sourcePos, targetPos);
		sourcePos = null;
		targetPos = null;
		return ret;
	}

	private void runMainLoop() {
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	private void cleanUp() {
		canvas.dispose();
		// unitTextFont.dispose();
		shell.dispose();
		display.dispose();
	}

	public void run() {
		createWindow();
		runMainLoop();
		cleanUp();
	}

	private void createWindow() {
		display = new Display();

		// unitTextFont = new Font(display, UNIT_TEXT_FONT_NAME,
		// UNIT_TEXT_FONT_SIZE, UNIT_TEXT_FONT_STYLE);

		shell = new Shell();
		shell.setSize(SWT.MIN, SWT.MIN);
		shell.setText(WINDOW_TITLE);

		canvas = new Canvas(shell, SWT.DOUBLE_BUFFERED);
		canvas.setSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				paintBoard(e.gc);
			}
		});
		canvas.addMouseListener(new MouseListener() {
			public void mouseDown(MouseEvent e) {
				if (e.button == 1) {
					if (state == WAIT_FOR_SELECT_UNIT) {
						Position pos = getClickedPosition(e.x, e.y);
						if (!board.isEmptyAt(pos)) {
							sourcePos = pos;
							state = WAIT_FOR_SELECT_TARGET;
						}
						redraw();
					} else if (state == WAIT_FOR_SELECT_TARGET) {
						Position pos = getClickedPosition(e.x, e.y);
						// if (!board.isEmptyAt(pos)) {
							if (board.getColorAt(pos) == board
									.getColorAt(sourcePos)) {
								sourcePos = pos;
								state = WAIT_FOR_SELECT_TARGET;
							} else {
								targetPos = pos;
								state = NO_INTERACTION;
							}
						// }
					} else {
						// sourcePos = null;
						// state = WAIT_FOR_SELECT_UNIT;
					}
					redraw();
				}
			}

			public void mouseDoubleClick(MouseEvent e) {
			}

			public void mouseUp(MouseEvent e) {
			}
		});

		shell.pack();
		shell.open();
	}

	private void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void setBoard(BoardImage board) {
		this.board = board;
	}

	public void redraw() {
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				canvas.redraw();
			}
		});
	}

	private void paintBoard(GC gc) {
		// draw background
		gc.setBackground(new Color(gc.getDevice(), BACKGROUND_COLOR));
		gc.fillRectangle(display.getClientArea());

		// draw grids
		gc.setForeground(new Color(gc.getDevice(), GRID_COLOR));
		gc.setLineWidth(2);

		final int RIVER_BOTTOM_Y = 4;
		final int RIVER_TOP_Y = 5;
		for (int x = 0; x < ChessGame.BOARD_WIDTH; x++) {
			drawLine(gc, x, 0, x, RIVER_BOTTOM_Y);
			drawLine(gc, x, ChessGame.BOARD_HEIGHT - 1, x, RIVER_TOP_Y);
		}
		for (int y = 0; y < ChessGame.BOARD_HEIGHT; y++)
			drawLine(gc, 0, y, ChessGame.BOARD_WIDTH - 1, y);
		drawLine(gc, 0, 0, 0, ChessGame.BOARD_HEIGHT - 1);
		drawLine(gc, ChessGame.BOARD_WIDTH - 1, 0, ChessGame.BOARD_WIDTH - 1, ChessGame.BOARD_HEIGHT - 1);

		// draw palace area
		final int PALACE_MIN_X = 3;
		final int PALACE_MAX_X = 5;
		final int PALACE_MIN_Y_BOTTOM = 0;
		final int PALACE_MAX_Y_BOTTOM = 2;
		final int PALACE_MIN_Y_TOP = 7;
		final int PALACE_MAX_Y_TOP = 9;
		drawLine(gc, PALACE_MIN_X, PALACE_MIN_Y_BOTTOM, PALACE_MAX_X,
				PALACE_MAX_Y_BOTTOM);
		drawLine(gc, PALACE_MAX_X, PALACE_MIN_Y_BOTTOM, PALACE_MIN_X,
				PALACE_MAX_Y_BOTTOM);
		drawLine(gc, PALACE_MIN_X, PALACE_MIN_Y_TOP, PALACE_MAX_X,
				PALACE_MAX_Y_TOP);
		drawLine(gc, PALACE_MAX_X, PALACE_MIN_Y_TOP, PALACE_MIN_X,
				PALACE_MAX_Y_TOP);

		// draw stones
		for (int x = 0; x < ChessGame.BOARD_WIDTH; x++)
			for (int y = 0; y < ChessGame.BOARD_HEIGHT; y++)
				if (!board.isEmptyAt(x, y))
					drawUnit(gc, new Position(x, y), board.getUnitAt(x, y));
	}

	private void drawUnit(GC gc, Position pos, int unit) {
		RGB textColor = board.getColorAt(pos) == ChessGame.BLACK ? BLACK_UNIT_TEXT_COLOR
				: RED_UNIT_TEXT_COLOR;
		int centerX = pos.getX() * GRID_SIZE + MARGIN;
		int centerY = pos.getY() * GRID_SIZE + MARGIN;

		Color fgColor = new Color(gc.getDevice(), textColor);
		Color bgColor = new Color(gc.getDevice(), UNIT_BACKGROUND_COLOR);

		gc.setLineWidth(1);

		if (pos.equals(sourcePos)) {
			gc.setForeground(bgColor);
			gc.setBackground(fgColor);
		} else {
			gc.setForeground(fgColor);
			gc.setBackground(bgColor);
		}
		gc.fillOval(centerX - UNIT_RADIUS, centerY - UNIT_RADIUS,
				2 * UNIT_RADIUS, 2 * UNIT_RADIUS);
		gc.drawOval(centerX - UNIT_RADIUS, centerY - UNIT_RADIUS,
				2 * UNIT_RADIUS, 2 * UNIT_RADIUS);
		gc.drawOval(centerX - UNIT_RADIUS + 2, centerY - UNIT_RADIUS + 2,
				2 * UNIT_RADIUS - 4, 2 * UNIT_RADIUS - 4);

		// FIXME: not to hard code this
		String text = GameUtility.getUnitChineseSymbol(board.getColorAt(pos),
				unit);
		Point textSize = gc.stringExtent(text);
		int textWidth = textSize.x;
		int textHeight = textSize.y;
		gc.drawText(text, centerX - textWidth / 2, centerY - textHeight / 2);
	}

	private void drawLine(GC gc, int x1, int y1, int x2, int y2) {
		x1 = MARGIN + GRID_SIZE * x1;
		y1 = MARGIN + GRID_SIZE * y1;
		x2 = MARGIN + GRID_SIZE * x2;
		y2 = MARGIN + GRID_SIZE * y2;
		gc.drawLine(x1, y1, x2, y2);
	}

	private Position getClickedPosition(int mouseX, int mouseY) {
		int minDist = Integer.MAX_VALUE;
		int retX = -1, retY = -1;

		for (int x = 0; x < ChessGame.BOARD_WIDTH; x++)
			for (int y = 0; y < ChessGame.BOARD_HEIGHT; y++) {
				int posX = GRID_SIZE * x + MARGIN;
				int posY = GRID_SIZE * y + MARGIN;
				int dist = Math.abs(mouseX - posX) + Math.abs(mouseY - posY);
				if (dist < minDist) {
					retX = x;
					retY = y;
					minDist = dist;
				}
			}
		return new Position(retX, retY);
	}

	public static void main(String args[]) {
		BoardImage board = GameUtility.createStartBoard();
		BoardWindow window = new BoardWindow(board);
		window.start();
		while (true)
			System.out.println(window.waitForUserMove());
	}

	public void selectUnit(Position pos) {
		// state = WAIT_FOR_SELECT_TARGET;
		sourcePos = pos;
	}
}
