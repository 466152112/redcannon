package org.stepinto.redcannon.ai;

import java.io.*;
import java.util.*;

import org.stepinto.redcannon.common.*;

public class HistoryMoveTable {
	private int count[][];
	private int maxCount;
	
	private static final int MAX_MOVE_SCORE = 128;
	
	public HistoryMoveTable() {
		count = new int [256][256];
		for (int i = 0; i < 256; i++)
			for (int j = 0; j < 256; j++)
				count[i][j] = 0;
		
		maxCount = 1;  // prevent division by zero
	}
	
	public void addMove(Move move) {
		int source = move.getSource().toInteger();
		int target = move.getTarget().toInteger();
		count[source][target]++;
		if (count[source][target] > maxCount)
			maxCount = count[source][target];
	}
	
	public int getMoveScore(Move move) {
		int source = move.getSource().toInteger();
		int target = move.getTarget().toInteger();
		/*System.out.printf("count=%d  ", count[source][target]);
		System.out.printf("normalized=%d\n",count[source][target] * MAX_MOVE_SCORE / maxCount);*/ 
		return count[source][target] * MAX_MOVE_SCORE / maxCount;
		// return count[source][target];
		
//		int result = count[source][target];
//		int tmpMaxCount = maxCount;
//		while (tmpMaxCount > MAX_MOVE_SCORE) {
//			tmpMaxCount = tmpMaxCount >> 1;
//			result = result >> 1;
//		}
//		return result;
	}
	
	public void dump(PrintStream out) {
		class MoveScore {
			public Move move;
			public int score;
		}
		class MoveScoreComparator implements Comparator<MoveScore> {
			@Override
			public int compare(MoveScore a, MoveScore b) {
				return b.score - a.score;
			}
		}
		
		List<MoveScore> list = new ArrayList<MoveScore>();
		for (int i = 0; i < 256; i++)
			for (int j = 0; j < 256; j++)
				if (count[i][j] > 0) {
					Position source = new Position(i);
					Position target = new Position(j);
					MoveScore moveScore = new MoveScore();
					moveScore.move = new Move(source, target);
					moveScore.score = getMoveScore(moveScore.move);
					list.add(moveScore);
				}
		Collections.sort(list, new MoveScoreComparator());
		
		for (MoveScore ms : list)
			out.printf("Move: %s\tScore: %d\n", ms.move, ms.score);
	}
}
