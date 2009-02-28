package org.stepinto.redcannon.ai;

import org.stepinto.redcannon.common.*;
import java.util.*;

public class StateHash {
	private static long BLACK_MASK;
	private static long RED_MASK;
	// private static final int INITIAL_HASH_CAPACITY = 64*1024*1024 - 1;
	
	static {
		Random random = new Random();
		BLACK_MASK = random.nextLong();
		RED_MASK = random.nextLong();
	}

	private Map<Long, StateInfo> hash = new HashMap<Long, StateInfo>();
	
	public StateInfo lookUp(BoardImage board, int player) {
		long key = getHashKey(board, player);
		return hash.get(key);
	}
	
	public void put(BoardImage board, int player, StateInfo info) {
		long key = getHashKey(board, player);
		hash.put(key, info);
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

	public void putAll(StateHash sh) {
		hash.putAll(sh.hash);
	}

	public void clear() {
		hash.clear();
	}
}
