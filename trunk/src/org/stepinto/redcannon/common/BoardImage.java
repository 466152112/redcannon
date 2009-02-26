package org.stepinto.redcannon.common;

import java.io.PrintStream;

public class BoardImage {
	public BoardImage() {
		units = new byte[256];
		colors = new byte[256];
	}

	public BoardImage(byte[] bytes) {
		units = new byte[256];
		colors = new byte[256];
		
		int i = 0;
		for (int x = 0; x < ChessGame.BOARD_WIDTH; x++)
			for (int y = 0; y < ChessGame.BOARD_HEIGHT; y += 2) {
				byte k = bytes[i++];
				depressAt(x, y+1, (byte)(0xf & k));
				k = (byte)(k >> 4);
				depressAt(x, y, k);
			}
		assert(i == bytes.length);
	}
	
	public void depressAt(int x, int y, byte k) {
		if (k != 0) {
			if (((k >> 3) & 1) == 0)
				setColorAt(x, y, ChessGame.BLACK);
			else {

				setColorAt(x, y, ChessGame.RED);
			}
			setUnitAt(x, y, k & 0x7);
		}
		else {
			setColorAt(x, y, ChessGame.EMPTY);
			setUnitAt(x, y, ChessGame.EMPTY);
		}
	}

	public boolean isEmptyAt(Position pos) {
		return colors[pos.toInteger()] == ChessGame.EMPTY;
	}

	public boolean isEmptyAt(int x, int y) {
		return colors[Position.toInteger(x, y)] == ChessGame.EMPTY;
	}

	public int getUnitAt(Position pos) {
		return (int)units[pos.toInteger()];
	}

	public int getUnitAt(int x, int y) {
		return (int)units[Position.toInteger(x, y)];
	}

	public int getColorAt(Position pos) {
		return (int)colors[pos.toInteger()];
	}

	public int getColorAt(int x, int y) {
		return (int)colors[Position.toInteger(x, y)];
	}

	public void setColorAt(Position pos, int color) {
		colors[pos.toInteger()] = (byte)color;
	}
	
	public void setColorAt(int x, int y, int color) {
		colors[Position.toInteger(x, y)] = (byte)color;
	}
	
	public void setUnitAt(Position pos, int unit) {
		units[pos.toInteger()] = (byte)unit;
	}
	
	public void setUnitAt(int x, int y, int unit) {
		units[Position.toInteger(x, y)] = (byte)unit;
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
			for (int i = 0; i < colors.length; i++) {
				if (colors[i] != board.colors[i] || units[i] != board.units[i])
					return false;
			}
			return true;
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
	
	public byte[] compress() {
		byte[] result = new byte[45];
		int i = 0;
		for (int x = 0; x < ChessGame.BOARD_WIDTH; x++)
			for (int y = 0; y < ChessGame.BOARD_HEIGHT; y += 2) {
				result[i] = (byte) ((compressAt(x,y)<<4) | compressAt(x,y+1));
				i++;
			}
		return result;
	}
	
	private byte compressAt(int x, int y) {
		return (byte) (((getColorAt(x, y) == ChessGame.RED) ? 0x8 : 0x0) | getUnitAt(x, y));
	}
	
	// deep-copy
	public BoardImage duplicate() {
		BoardImage board = new BoardImage();
		for (int i = 0; i < units.length; i++)
			board.units[i] = units[i];
		for (int i = 0; i < colors.length; i++)
			board.colors[i] = colors[i];
		return board;
	}

	private byte units[];
	private byte colors[];
}
