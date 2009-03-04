package org.stepinto.redcannon.ai;

import org.stepinto.redcannon.common.*;
import java.util.*;

public class StateHash<T> {
	private static long BLACK_MASK;
	private static long RED_MASK;
	
	static {
		Random random = new Random();
		BLACK_MASK = random.nextLong();
		RED_MASK = random.nextLong();
	}

	private Map<Long, T> hash = new HashMap<Long, T>();
	
	public T lookUp(BoardImage board, int player) {
		long key = getHashKey(board, player);
		return hash.get(key);
	}
	
	public void put(BoardImage board, int player, T info) {
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

	public void putAll(StateHash<T> sh) {
		hash.putAll(sh.hash);
	}

	public void clear() {
		hash.clear();
	}
}
