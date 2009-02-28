package org.stepinto.redcannon.ucci;

import java.io.*;
import java.util.*;
import org.stepinto.redcannon.ai.*;
import org.stepinto.redcannon.common.*;

public class ChessEngineLoader extends Thread {
	private ChessEngine engine;
	private PrintStream infoOut;
	
	public ChessEngineLoader(ChessEngine engine) {
		this.engine = engine;
		this.infoOut = new PrintStream(new UcciOutputStream(System.out));
	}
	
	public void processHello(String cmd[]) {
		if (cmd.length == 1) {
			engine.hello(infoOut);
			
			System.out.println("id name RedCannon");
			System.out.println("id author Chao Shi");
			System.out.println("ucciok");
		}
		else
			infoOut.println("More or less arguments.");
	}
	
	public void processSetOption(String cmd[]) {
		if (cmd.length == 3) {
			try {
				engine.setOption(cmd[1], cmd[2]);
			} catch (UnsupportedOptionException ex) {
				infoOut.println("Unsupported option: " + cmd[1]);
			}
		}
		else
			infoOut.println("More or less arguments.");
	}
	
	public void processSetGameState(String cmd[]) {
		try {
			StringBuffer fen = new StringBuffer();
			Move moves[] = new Move[0];
			boolean inFenPart = true;
			
			for (int i = 2; i < cmd.length; i++) {
				if (cmd[i].equalsIgnoreCase("moves"))
					inFenPart = false;
				else {
					if (inFenPart)
						fen.append(cmd[i]).append(" ");
					else {
						moves = Arrays.copyOf(moves, moves.length + 1);
						moves[moves.length - 1] = parseMove(cmd[i]);
					}
				}
			}
			
			engine.setGameState(FenParser.parseString(fen.toString()), moves);
		}
		catch (InvalidMoveStringException ex) {
			ex.printStackTrace(infoOut);
		}
		catch (InvalidFenFormatException ex) {
			ex.printStackTrace(infoOut);
		}
		
	}
	
	private Move parseMove(String string) throws InvalidMoveStringException {
		if (string.length() == 4) {
			char ch1 = string.charAt(0);
			char ch2 = string.charAt(1);
			char ch3 = string.charAt(2);
			char ch4 = string.charAt(3);
			
			if ('a' <= ch1 && ch1 <= 'i' && '0' <= ch2 && ch2 <= '9' &&
				'a' <= ch3 && ch3 <= 'i' && '0' <= ch4 && ch4 <= '9') {
				Position source = new Position(ch1 - 'a', ChessGame.BOARD_HEIGHT - ch2 + '0' - 1);
				Position target = new Position(ch3 - 'a', ChessGame.BOARD_HEIGHT - ch4 + '0' - 1);
				return new Move(source, target);
			}
			else
				throw new InvalidMoveStringException("Invalid character found.");
		}
		else
			throw new InvalidMoveStringException("The input string is too long or short.");
	}
	
/*	private void log(String line) {
		try {
			PrintStream stream = new PrintStream(new FileOutputStream(new File("d:\\a.txt"), true));
			stream.println(line);
			stream.close();
		} catch (Exception ex) {
		}
	}
*/
	public void run() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String line;
		
		try {
			while ((line = reader.readLine()) != null) {
				// log(line);
				
				String cmd[] = line.split("\\ +");
				if (cmd[0].equalsIgnoreCase("ucci"))
					processHello(cmd);
				else if (cmd[0].equalsIgnoreCase("isready"))
					System.out.println("readyok");
				else if (cmd[0].equalsIgnoreCase("setoption"))
					processSetOption(cmd);
				else if (cmd[0].equalsIgnoreCase("position"))
					processSetGameState(cmd);
				else if (cmd[0].equalsIgnoreCase("go"))
					processGo(cmd);
				else if (cmd[0].equalsIgnoreCase("stop"))
					processStop(cmd);
				else if (cmd[0].equalsIgnoreCase("quit")) {
					System.out.println("bye");
					return;
				}
			}
		}
		catch (IOException ex) {
			ex.printStackTrace(infoOut);
		}
	}
	
	private void processStop(String[] cmd) {
		if (cmd.length == 1) {
			Move bestMove = engine.stop();
			printMove(bestMove);
		}
		else
			infoOut.println("More or less arguments.");
	}

	private void processGo(String[] cmd) {
		new Thread() {
			public void run() {
				Move bestMove = engine.go(infoOut);
				printMove(bestMove);
			}
		}.start();
	}
	
	private void printMove(Move move) {
		if (move == null)
			System.out.println("nobestmove");
		else {
			Position source = move.getSource();
			Position target = move.getTarget();
			System.out.printf("bestmove %c%d%c%d\n", source.getX() + 'a', ChessGame.BOARD_HEIGHT - source.getY() - 1,
					target.getX() + 'a', ChessGame.BOARD_HEIGHT - target.getY() - 1);
		}
	}

	public static void main(String args[]) {
		//boolean iterative = false;
		boolean iterative = true;
		int timeLimit = 10000;
		for (int i = 0; i < args.length; i++) {
			//if (args[i].equals("--iterative"))
			//	iterative = true;
			if (args[i].equals("--time-limit")) {
				i++;
				timeLimit = Integer.parseInt(args[i]) * 1000;
			}
		}
		
		SearchEngine engine = (iterative ? new IterativeSearchEngine() : new NaiveSearchEngine());
		engine.addEvaluator(new NaiveEvaluator());
		engine.addSelector(new NaiveSelector());
		engine.setTimeLimit(timeLimit);
		new ChessEngineLoader(new RedCannonEngine(engine)).run();
	}
}
