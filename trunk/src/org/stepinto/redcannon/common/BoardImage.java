package org.stepinto.redcannon.common;

import java.util.*;

public class BoardImage {
	public BoardImage() {
		units = new Unit[ChessGame.BOARD_WIDTH][ChessGame.BOARD_HEIGHT];
	}

	public boolean isEmptyAt(Position pos) {
		return isEmptyAt(pos.getX(), pos.getY());
	}

	public boolean isEmptyAt(int x, int y) {
		return units[x][y] == null;
	}

	public Unit getUnitAt(Position pos) {
		return getUnitAt(pos.getX(), pos.getY());
	}

	public Unit getUnitAt(int x, int y) {
		return units[x][y];
	}

	public int getColorAt(Position pos) {
		return getColorAt(pos.getX(), pos.getY());
	}

	public int getColorAt(int x, int y) {
		Unit unit = getUnitAt(x, y);
		if (unit == null)
			return ChessGame.EMPTY;
		else
			return unit.getColor();
	}

	public void addUnit(Unit unit) {
		int x = unit.getPosition().getX();
		int y = unit.getPosition().getY();
		units[x][y] = unit;
	}
	
	public void batchAddUnits(Unit[] list) {
		for (Unit u : list)
			addUnit(u);
	}
	
	public Unit[] getUnits() {
		Unit[] ret = new Unit[ChessGame.MAX_UNITS];
		int count = 0;
		for (int x = 0; x < ChessGame.BOARD_WIDTH; x++)
			for (int y = 0; y < ChessGame.BOARD_HEIGHT; y++)
				if (units[x][y] != null)
					ret[count++] = units[x][y];
		return Arrays.copyOf(ret, count);
	}

	public Unit[] getUnitOf(int player) {
		Unit[] ret = new Unit[ChessGame.MAX_UNITS_EACH_PLAYER];
		int count = 0;
		for (int x = 0; x < ChessGame.BOARD_WIDTH; x++)
			for (int y = 0; y < ChessGame.BOARD_HEIGHT; y++)
				if (units[x][y] != null && units[x][y].getColor() == player)
					ret[count++] = units[x][y];
		return Arrays.copyOf(ret, count);
	}

	private Unit units[][];
}
