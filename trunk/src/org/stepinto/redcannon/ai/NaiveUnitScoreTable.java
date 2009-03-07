package org.stepinto.redcannon.ai;

import org.stepinto.redcannon.common.*;

public class NaiveUnitScoreTable extends UnitScoreTable {
	private final static int DEFAULT_KING_SCORE = 500;
	private final static int DEFAULT_ROOK_SCORE = 10;
	private final static int DEFAULT_CANNON_SCORE = 8;
	private final static int DEFAULT_HORSE_SCORE = 8;
	private final static int DEFAULT_ELEPHANT_SCORE = 3;
	private final static int DEFAULT_ADVISOR_SCORE = 3;
	private final static int DEFAULT_PAWN_SCORE = 1;
	
	private final static int DEFAULT_TABLE[] = new int[] {
		0, // empty
		DEFAULT_KING_SCORE,
		DEFAULT_ROOK_SCORE,
		DEFAULT_CANNON_SCORE,
		DEFAULT_HORSE_SCORE,
		DEFAULT_ELEPHANT_SCORE,
		DEFAULT_ADVISOR_SCORE,
		DEFAULT_PAWN_SCORE
	};
	
	private int table[];
	
	public NaiveUnitScoreTable() {
		table = DEFAULT_TABLE;
	}
	
	public NaiveUnitScoreTable(int table[]) {
		this.table = table;
	}
	
	@Override
	public int getUnitScore(Position pos, int unit) {
		assert(0 < unit && unit < table.length);
		return table[unit];
	}
}
