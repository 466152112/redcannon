package org.stepinto.redcannon.ai;

import java.util.*;

public class StateHash implements Iterable<Map.Entry<CompressState, StateInfo>> {
	private Map<CompressState, StateInfo> hash = new HashMap<CompressState, StateInfo>();
	
	public StateInfo lookUp(byte[] boardCompressed, int player) {
		return hash.get(new CompressState(boardCompressed, player));
	}
	
	public void put(byte[] boardCompressed, int player, StateInfo info) {
		hash.put(new CompressState(boardCompressed, player), info);
	}

	public void putAll(StateHash sh) {
		hash.putAll(sh.hash);
	}
	
	public Iterator<Map.Entry<CompressState, StateInfo>> iterator() {
		return hash.entrySet().iterator();
	}
}
