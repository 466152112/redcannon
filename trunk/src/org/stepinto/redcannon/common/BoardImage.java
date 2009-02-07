package org.stepinto.redcannon.common;

public class BoardImage {
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
	 
	 private Unit units[][];
}
