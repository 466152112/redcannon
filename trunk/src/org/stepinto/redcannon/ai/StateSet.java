package org.stepinto.redcannon.ai;

import org.stepinto.redcannon.common.*;
import java.util.*;

public class StateSet {
	private static long BLACK_MASK;
	private static long RED_MASK;
	
	static {
		Random random = new Random();
		BLACK_MASK = random.nextLong();
		RED_MASK = random.nextLong();
	}

	private Set<Long> hash = new HashSet<Long>();
	
	public boolean contains(BoardImage board, int player) {
		long key = getHashKey(board, player);
		return hash.contains(key);
	}
	
	public void add(BoardImage board, int player) {
		long key = getHashKey(board, player);
		hash.add(key);
	}
	
	private long getHashKey(BoardImage board, int player) {
		long boardZobrist = board.getZobristCode();
		switch (player) {
		case ChessGame.BLACK:
			return boardZobrist ^ BLACK_MASK;
		case ChessGame.RED:
			return boardZobrist ^ RED_MASK;
		default:
			assert(false);
			return 0;
		}
	}

	public void putAll(StateSet sh) {
		hash.addAll(sh.hash);
	}

	public void clear() {
		hash.clear();
	}
}
