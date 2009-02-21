package org.stepinto.redcannon.ai.log;

import java.io.*;
import java.util.*;

public class StateLogInfo {
	public StateLogInfo(int stateId) {
		this.stateId = stateId;
		this.message = null;
		this.childStates = new ArrayList<Integer>();
	}
	
	public StateLogInfo(int stateId, String message, List<Integer> childStates) {
		super();
		this.stateId = stateId;
		this.message = message;
		this.childStates = childStates;
	}

	public int getStateId() {
		return stateId;
	}

	public String getMessage() {
		return message;
	}

	public List<Integer> getChildStates() {
		return childStates;
	}
	
	public void appendMessage(String message) {
		this.message += message;
	}
	
	public void addChildState(int childStateId) {
		childStates.add(childStateId);
	}
	
	public void dump(PrintStream out) {
		out.println("State-id: " + stateId);
		out.println(message);
		out.print("Child-states:");
		for (int i : childStates)
			out.print(" " + i);
		out.println();
	}

	private int stateId;
	private String message;
	private List<Integer> childStates;
}
