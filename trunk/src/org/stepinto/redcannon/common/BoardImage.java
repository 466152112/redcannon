package org.stepinto.redcannon.common;

import java.util.*;
import java.io.*;

import org.apache.commons.lang.ArrayUtils;

public class BoardImage { 
	private static long zobristMasks[][];  // 256 * 16
	
	static {
		// init zobrist-mask
		zobristMasks = new long [256][16];
		Random random = new Random();
		for (int i = 0; i < 256; i++)
			for (int j = 0; j < 16; j++)
				zobristMasks[i][j] = random.nextLong();
	}
	
	public BoardImage() {
		units = new int[256];
		zobrist = 0;
	}
	
	private BoardImage(int units[], long zobrist) {
		this.units = units;
		this.zobrist = zobrist;
	}
	
	public boolean isEmptyAt(Position pos) {
		return units[pos.toInteger()] == ChessGame.EMPTY;
	}

	public boolean isEmptyAt(int x, int y) {
		return units[Position.toInteger(x, y)] == ChessGame.EMPTY;
	}

	public int getUnitAt(Position pos) {
		return units[pos.toInteger()] & 0x7;
	}

	public int getUnitAt(int x, int y) {
		return units[Position.toInteger(x, y)] & 0x7;
	}

	public int getColorAt(Position pos) {
		int unit = units[pos.toInteger()];
		if (unit == ChessGame.EMPTY)
			return ChessGame.EMPTY;
		else
			return ((unit>>3) == 0) ? ChessGame.BLACK : ChessGame.RED; 
	}

	public int getColorAt(int x, int y) {
		int unit = units[Position.toInteger(x, y)];
		if (unit == ChessGame.EMPTY)
			return ChessGame.EMPTY;
		else
			return ((unit>>3) == 0) ? ChessGame.BLACK : ChessGame.RED;		
	}

	public void setColorAt(Position pos, int color) {
		int oldValue = units[pos.toInteger()];
		int newValue;
		
		switch (color) {
		case ChessGame.EMPTY:
			newValue = ChessGame.EMPTY;
			break;
		case ChessGame.BLACK:
			newValue = (~0x8) & oldValue;
			break;
		case ChessGame.RED:
			newValue = 0x8 | oldValue;
			break;
		default:
			assert(false);
			newValue = 0;
		}
		
		units[pos.toInteger()] = newValue;
		updateZobristCode(pos.toInteger(), oldValue, newValue);
	}


	public void setColorAt(int x, int y, int color) {
		int oldValue = units[Position.toInteger(x, y)];
		int newValue;
		
		switch (color) {
		case ChessGame.EMPTY:
			newValue = ChessGame.EMPTY;
			break;
		case ChessGame.BLACK:
			newValue = (~0x8) & oldValue;
			break;
		case ChessGame.RED:
			newValue = 0x8 | oldValue;
			break;
		default:
			assert(false);
			newValue = 0;
		}
		
		units[Position.toInteger(x, y)] = newValue;
		updateZobristCode(Position.toInteger(x, y), oldValue, newValue);		
	}
	
	public void setUnitAt(Position pos, int unit) {		
		int oldValue = units[pos.toInteger()];
		int newValue = (unit == ChessGame.EMPTY ? 0 : (oldValue & ~0x7) | unit); 
		units[pos.toInteger()] = newValue;
		updateZobristCode(pos.toInteger(), oldValue, newValue);
	}
	
	public void setUnitAt(int x, int y, int unit) {
		int oldValue = units[Position.toInteger(x, y)];
		int newValue = (unit == ChessGame.EMPTY ? 0 : (oldValue & ~0x7) | unit); 
		units[Position.toInteger(x, y)] = newValue;
		updateZobristCode(Position.toInteger(x, y), oldValue, newValue);
	}	
	
	// return killed unit
	// if nothing killed, return ChessGame.EMPTY
	public int performMove(Move move) {
		Position source = move.getSource();
		Position target = move.getTarget();
		assert(!isEmptyAt(source));
		assert(getColorAt(target) != getColorAt(source));
		
		int sourceColor = getColorAt(source);
		int sourceUnit = getUnitAt(source);
		int targetColor = getColorAt(target);
		int targetUnit = getUnitAt(target);
		
		setColorAt(source, ChessGame.EMPTY);
		setUnitAt(source, ChessGame.EMPTY);
		setColorAt(target, sourceColor);
		setUnitAt(target, sourceUnit);
		
		return (targetColor == ChessGame.EMPTY) ? ChessGame.EMPTY : targetUnit;
	}
	
	public void unperformMove(Move move, int killedUnit) {
		Position source = move.getSource();
		Position target = move.getTarget();
		assert(isEmptyAt(source));
		assert(!isEmptyAt(target));
		
		int sourceColor = getColorAt(target);
		int sourceUnit = getUnitAt(target);
		
		setColorAt(source, sourceColor);
		setUnitAt(source, sourceUnit);
		if (killedUnit == ChessGame.EMPTY) {
			setColorAt(target, ChessGame.EMPTY);
			setUnitAt(target, ChessGame.EMPTY);
		}
		else {
			setColorAt(target, GameUtility.getOpponent(sourceColor));
			setUnitAt(target, killedUnit);
		}
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof BoardImage) {
			BoardImage board = (BoardImage)obj;
			return getZobristCode() == board.getZobristCode() && Arrays.equals(units, board.units);
		}
		else
			return false;
	}
	
	public void dump(PrintStream out) {
		out.print(" ");
		for (int x = 0;x  < ChessGame.BOARD_WIDTH; x++) {
			out.print(" ");
			out.print(x);
		}
		out.println();
		
		for (int y = 0; y < ChessGame.BOARD_HEIGHT; y++) {
			out.print(y);
			
			for (int x = 0; x < ChessGame.BOARD_HEIGHT; x++) {
				out.print(" ");
				
				if (isEmptyAt(x, y))
					out.print(".");
				else
					out.print(GameUtility.getUnitSymbol(getColorAt(x, y), getUnitAt(x, y)));
			}
			out.println();
		}
	}
	
	// deep-copy
	public BoardImage duplicate() {
		return new BoardImage(ArrayUtils.clone(units), zobrist);
	}
	
	private void updateZobristCode(int pos, int oldValue, int newValue) {
		zobrist ^= zobristMasks[pos][oldValue];
		zobrist ^= zobristMasks[pos][newValue];
	}
	
	public long getZobristCode() {
		return zobrist;
	}

	private int units[];
	private long zobrist;
}
