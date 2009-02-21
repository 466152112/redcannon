package org.stepinto.redcannon.ai.log;

import java.util.*;
import java.io.*;
import org.apache.commons.codec.binary.*;

public class SearchLogger {
	private PrintStream out;
	private Map<Integer, StateLogInfo> states = new HashMap<Integer, StateLogInfo>();
	private Stack<Integer> stateIdStack = new Stack<Integer>();
	
	public SearchLogger(OutputStream out) {
		this.out = new PrintStream(out);
	}
	
	public void beginSearch() {
		out.println("<states>");
	}
	
	public void endSearch() {
		out.println("</states>");
	}
	
	public void enterState(int stateId) {
		if (!stateIdStack.isEmpty()) {
			int parent = stateIdStack.peek();
			states.get(parent).addChildState(stateId);
		}
		stateIdStack.push(stateId);
		StateLogInfo log = new StateLogInfo(stateId);
		states.put(stateId, log);
	}
	
	public void printMessage(String message) {
		int stateId = stateIdStack.peek();
		states.get(stateId).appendMessage(message);
	}
	
	public void leaveState() {
		int stateId = stateIdStack.pop();
		StateLogInfo log = states.remove(stateId);
		out.println("  <state id=" + stateId + ">");
		out.println("    <message>" + new String(Base64.encodeBase64(log.getMessage().getBytes())) + "</message>");
		for (int child : log.getChildStates())
			out.println("    <child-state id=" + child + " />");
		out.println("  </state>");
	}
}

