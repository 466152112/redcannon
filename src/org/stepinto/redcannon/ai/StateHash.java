package org.stepinto.redcannon.ai;

import java.util.*;

public class StateHash {
	private final class State {
		public State(byte[] bytes, int player) {
			this.bytes = bytes;
			this.player = player;
		}
		
		public boolean equals(Object obj) {
			if (obj instanceof State) {
				State s = (State)obj;
				return player == s.player && Arrays.equals(bytes, s.bytes);
			}
			return false;
		}
		
		public int hashCode() {
			return Arrays.hashCode(bytes) + player;
		}
		
		private byte[] bytes;
		private int player;
	}
	
	private Map<State, StateInfo> hash = new HashMap<State, StateInfo>();
	
	public StateInfo lookUp(byte[] boardCompressed, int player) {
		return hash.get(new State(boardCompressed, player));
	}
	
	public void put(byte[] boardCompressed, int player, StateInfo info) {
		hash.put(new State(boardCompressed, player), info);
	}
}
