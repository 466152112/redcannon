package org.stepinto.redcannon.ai.log;

import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import org.apache.commons.codec.binary.*;

public class SearchDebugger {
	private static Map<Integer, StateLogInfo> states = new HashMap<Integer, StateLogInfo>();
	
	private static void loadStates(File file) throws Exception {
		SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
		parser.parse(file, new DefaultHandler() {
			private StateLogInfo state;
			private boolean inMessageBlock;
			
			@Override
			public void startElement(String uri, String localName, 
					String qName, Attributes atts) throws SAXException {
				if (qName.equals("state")) {
					int stateId = Integer.parseInt(atts.getValue("id"));
					state = new StateLogInfo(stateId);
				}
				else if (qName.equals("message"))
					inMessageBlock = true;
				else if (qName.equals("child-state")) {
					int stateId = Integer.parseInt(atts.getValue("id"));
					state.addChildState(stateId);
				}
			}
			
			@Override
			public void characters(char[] ch, int start, int length) throws SAXException {
				if (inMessageBlock) {
					byte[] bytes = new String(ch, start, length).getBytes();
					String message = new String(Base64.decodeBase64(bytes));
					state.appendMessage(message);
				}
			}
			
			@Override
			public void endElement(String uri, String localName, String qName) throws SAXException {
				if (qName.equals("state")) {
					SearchDebugger.states.put(state.getStateId(), state);
					state = null;
				}
				else if (qName.equals("message"))
					inMessageBlock = false;
			}
		});
	}
	
	private static int readStateId() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in)); 
		while (true) {
			System.out.print("Enter state-id> ");
			
			try {
				String line = reader.readLine();
				return Integer.parseInt(line);
			} catch (IOException ex) {
				System.out.println("expect state-id.");
			} catch (NumberFormatException ex) {
				System.out.println("expect state-id.");
			}
		}
	}
	
	private static void printState(int id) {
		StateLogInfo state = states.get(id);
		state.dump(System.out);
	}
	
	public static void main(String args[]) throws Exception {
		System.out.println("Debugger for red-cannon search");
		System.out.println("by Chao Shi <charlescpp@gmail.com>");
		
		loadStates(new File(args[0]));
		printState(0);
		
		int id;
		while (true) {
			id = readStateId();
			if (id < states.size())
				printState(id);
			else
				System.out.println("Out of range.");
		}
	}
}
