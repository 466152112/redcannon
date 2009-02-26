package org.stepinto.redcannon.ai.learn;

import java.io.*;
import java.util.Map;

import org.stepinto.redcannon.ai.*;
import org.stepinto.redcannon.common.BoardImage;
import org.stepinto.redcannon.common.ChessGame;
import org.stepinto.redcannon.common.FenParser;
import org.stepinto.redcannon.common.GameState;
import org.stepinto.redcannon.common.GameUtility;

public class EvaluateLearner {
	public static void writeTrainingCases(PrintStream out, StateHash hash) throws IOException {		
		for (Map.Entry<CompressState, StateInfo> entry : hash) {
			int inputs[] = getInputs(entry.getKey());
			int output = getOutput(entry.getValue());
			
			out.print(output);
			for (int i = 0; i < inputs.length; i++)
				out.print(" " + i + ":" + inputs[i]);
			out.println();
		}
	}
	
	private static int[] getInputs(CompressState state) {
		// (90*14 = 1260)
		// 0 - 1259: units
		// 1260 - 1274: unit count powers
		
		int player = state.getPlayer();
		int result[] = new int [1275];
		BoardImage board = new BoardImage(state.getBoard());
		for (int x = 0; x < ChessGame.BOARD_WIDTH; x++)
			for (int y = 0; y < ChessGame.BOARD_HEIGHT; y++)
				if (!board.isEmptyAt(x, y)) {
					int pos = x * ChessGame.BOARD_HEIGHT + y;
					if (GameUtility.getAttackDirection(player) == -1)
						pos = 90 - pos - 1;
					result[pos * 14 + board.getUnitAt(x, y) - 1] = 1;
				}
		for (int x = 0; x < ChessGame.BOARD_WIDTH; x++)
			for (int y = 0; y < ChessGame.BOARD_HEIGHT; y++)
				if (!board.isEmptyAt(x, y)) {
					int index = board.getUnitAt(x, y)-1 + (board.getColorAt(x, y) == player ? 0 : 7);
					result[index + 90*14]++;
				}
		return result;
	}
	
	private static int getOutput(StateInfo info) {
		return info.getAlpha();
	}
	
	public static void main(String args[]) throws Exception {
		GameState state = FenParser.parseFile(new File(args[0]));
		SearchEngine engine = new SearchEngine(state);
		engine.addEvaluator(new NaiveEvaluator());
		engine.addSelector(new NaiveSelector());
		engine.search();
		
		writeTrainingCases(System.out, engine.getStateHash());
	}
}
