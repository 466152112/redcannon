package org.stepinto.redcannon.ui;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

import org.stepinto.redcannon.ai.*;
import org.stepinto.redcannon.common.*;

public class DemoApplet extends Applet implements MouseListener {
	private static final long serialVersionUID = 1L;
	
	// time limit
	private static final int AI_TIME_LIMIT = 5000;
	
	// sizes
	private static final int GRID_SIZE = 55;
	private static final int MARGIN = 45;
	private static final int CANVAS_WIDTH = ChessGame.BOARD_WIDTH * GRID_SIZE + MARGIN * 2;
	private static final int CANVAS_HEIGHT = ChessGame.BOARD_HEIGHT * GRID_SIZE + MARGIN * 2;
	private static final int UNIT_RADIUS = 22;
	private static final int UNIT_TEXT_FONT_SIZE = 18;
	
	// colors
	private static final Color GRID_COLOR = new Color(149, 114, 59);
	private static final Color BACKGROUND_COLOR = new Color(240, 240, 10);
	private static final Color RED_UNIT_TEXT_COLOR = new Color(204, 0, 0);
	private static final Color BLACK_UNIT_TEXT_COLOR = new Color(0, 0, 0);
	private static final Color UNIT_BACKGROUND_COLOR = new Color(255, 237, 162);

	// positions & board
	private Position sourcePos;
	private Position targetPos;
	private BoardImage board;
	
	// states
	private static final int NO_INTERACTION = 0;
	private static final int WAIT_FOR_SELECT_UNIT = 1;
	private static final int WAIT_FOR_SELECT_TARGET = 2;
	private int state;
	
	// double buffer
	// TODO:
	
	@Override
	public void init() {
		setSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setBackground(BACKGROUND_COLOR);
		addMouseListener(this);
		setFont(getUnitTextFont());
		
		board = GameUtility.createStartBoard();
		new Thread() {
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
			
			public Move getHumanMove(BoardImage board, int player) {
				return waitForUserMove();
			}
			
			public Move getAiMove(BoardImage board, int player) {
				System.out.print("Wait for AI-player...");
				TimeCounter tc = new TimeCounter();
				tc.start();
				
				SearchEngine engine = new IterativeSearchEngine(board.duplicate(), player);
				engine.addEvaluator(new NaiveEvaluator());
				engine.addSelector(new NaiveSelector());
				engine.setTimeLimit(AI_TIME_LIMIT);
				
				SearchResult result = engine.search();
				return result.getBestMove();
			}
		}.start();
		
		state = NO_INTERACTION;
	}
	
	@Override
	public void paint(Graphics g) {
		((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		drawBoard(g);
		for (int x = 0; x < ChessGame.BOARD_WIDTH; x++)
			for (int y = 0; y < ChessGame.BOARD_HEIGHT; y++)
				if (!board.isEmptyAt(x, y))
					drawUnit(g, new Position(x, y), board.getUnitAt(x, y)); 
	}
	
	public void drawBoard(Graphics g) {
		// draw background
		g.setColor(BACKGROUND_COLOR);
		g.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);

		// draw grids
		g.setColor(GRID_COLOR);
		
		final int RIVER_BOTTOM_Y = 4;
		final int RIVER_TOP_Y = 5;
		for (int x = 0; x < ChessGame.BOARD_WIDTH; x++) {
			drawLine(g, x, 0, x, RIVER_BOTTOM_Y);
			drawLine(g, x, ChessGame.BOARD_HEIGHT - 1, x, RIVER_TOP_Y);
		}
		for (int y = 0; y < ChessGame.BOARD_HEIGHT; y++)
			drawLine(g, 0, y, ChessGame.BOARD_WIDTH - 1, y);
		drawLine(g, 0, 0, 0, ChessGame.BOARD_HEIGHT - 1);
		drawLine(g, ChessGame.BOARD_WIDTH - 1, 0, ChessGame.BOARD_WIDTH - 1, ChessGame.BOARD_HEIGHT - 1);

		// draw palace area
		final int PALACE_MIN_X = 3;
		final int PALACE_MAX_X = 5;
		final int PALACE_MIN_Y_BOTTOM = 0;
		final int PALACE_MAX_Y_BOTTOM = 2;
		final int PALACE_MIN_Y_TOP = 7;
		final int PALACE_MAX_Y_TOP = 9;
		drawLine(g, PALACE_MIN_X, PALACE_MIN_Y_BOTTOM, PALACE_MAX_X,
				PALACE_MAX_Y_BOTTOM);
		drawLine(g, PALACE_MAX_X, PALACE_MIN_Y_BOTTOM, PALACE_MIN_X,
				PALACE_MAX_Y_BOTTOM);
		drawLine(g, PALACE_MIN_X, PALACE_MIN_Y_TOP, PALACE_MAX_X,
				PALACE_MAX_Y_TOP);
		drawLine(g, PALACE_MAX_X, PALACE_MIN_Y_TOP, PALACE_MIN_X,
				PALACE_MAX_Y_TOP);
	}
	
	private void drawLine(Graphics g, int x1, int y1, int x2, int y2) {
		x1 = MARGIN + GRID_SIZE * x1;
		y1 = MARGIN + GRID_SIZE * y1;
		x2 = MARGIN + GRID_SIZE * x2;
		y2 = MARGIN + GRID_SIZE * y2;
		g.drawLine(x1, y1, x2, y2);
	}
	
	public void drawUnit(Graphics g, Position pos, int unit) {
		
		
		Color textColor = board.getColorAt(pos) == ChessGame.BLACK ? BLACK_UNIT_TEXT_COLOR
				: RED_UNIT_TEXT_COLOR;
		int centerX = pos.getX() * GRID_SIZE + MARGIN;
		int centerY = pos.getY() * GRID_SIZE + MARGIN;
		Color fgColor, bgColor;
		
		// gc.setLineWidth(1);
		if (pos.equals(sourcePos)) {
			fgColor = textColor;
			bgColor = UNIT_BACKGROUND_COLOR;
		} else {
			fgColor = UNIT_BACKGROUND_COLOR;
			bgColor = textColor;
		}
		g.setColor(bgColor);
		g.fillOval(centerX - UNIT_RADIUS, centerY - UNIT_RADIUS,
				2 * UNIT_RADIUS, 2 * UNIT_RADIUS);
		g.setColor(fgColor);
		g.drawOval(centerX - UNIT_RADIUS, centerY - UNIT_RADIUS,
				2 * UNIT_RADIUS, 2 * UNIT_RADIUS);
		g.drawOval(centerX - UNIT_RADIUS + 2, centerY - UNIT_RADIUS + 2,
				2 * UNIT_RADIUS - 4, 2 * UNIT_RADIUS - 4);

		// FIXME: not to hard code this
		g.setColor(fgColor);
		String text = GameUtility.getUnitChineseSymbol(board.getColorAt(pos),
				unit);
		// Rectangle2D textSize = g.getFontMetrics().getStringBounds(text, g);
		FontMetrics metrics = g.getFontMetrics();
		g.drawString(text, centerX - metrics.stringWidth(text) / 2, centerY + metrics.getAscent() - (metrics.getAscent() + metrics.getDescent()) / 2 - 2);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		Position pos = getClickedPosition(e.getX(), e.getY());
		
		if (e.getButton() == MouseEvent.BUTTON1) {
			if (state == WAIT_FOR_SELECT_UNIT) {
				if (!board.isEmptyAt(pos)) {
					sourcePos = pos;
					state = WAIT_FOR_SELECT_TARGET;
				}
				repaint();
			} else if (state == WAIT_FOR_SELECT_TARGET) {
				if (board.getColorAt(pos) == board.getColorAt(sourcePos)) {
					sourcePos = pos;
					state = WAIT_FOR_SELECT_TARGET;
				} else {
					targetPos = pos;
					state = NO_INTERACTION;
				}
			}
			repaint();
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {	
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
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
	
	public Move waitForUserMove() {
		state = WAIT_FOR_SELECT_UNIT;
		while (targetPos == null || sourcePos == null)
			sleep(100);
		Move ret = new Move(sourcePos, targetPos);
		sourcePos = null;
		targetPos = null;
		return ret;
	}
	
	private void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
		}
	}
	
	private Font getUnitTextFont() {
		if (Font.getFont("微软雅黑") != null)
			return new Font("微软雅黑", Font.PLAIN, UNIT_TEXT_FONT_SIZE);
		else if (Font.getFont("黑体") != null)
			return new Font("黑体", Font.PLAIN, UNIT_TEXT_FONT_SIZE);
		else
			return new Font(null, Font.BOLD, UNIT_TEXT_FONT_SIZE);
	}
}
