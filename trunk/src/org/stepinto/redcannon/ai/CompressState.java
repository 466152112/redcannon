package org.stepinto.redcannon.ai;

import java.util.*;

public class CompressState {
	public CompressState(byte[] bytes, int player) {
		this.bytes = bytes;
		this.player = player;
	}

	public boolean equals(Object obj) {
		if (obj instanceof CompressState) {
			CompressState s = (CompressState) obj;
			return player == s.player && Arrays.equals(bytes, s.bytes);
		}
		return false;
	}

	public int hashCode() {
		return Arrays.hashCode(bytes) + player;
	}

	public byte[] getBoard() {
		return bytes;
	}

	public int getPlayer() {
		return player;
	}

	private byte[] bytes;
	private int player;
}
