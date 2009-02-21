package org.stepinto.redcannon.ai;

import java.util.*;
import org.stepinto.redcannon.common.*;
import org.stepinto.redcannon.ai.log.*;

public class NaiveSelector implements Selector {

	@Override
	public void select(List<Candidate> candi, BoardImage board, int player,
			int depth, SearchLogger logger) {
		int opponent = GameUtility.getOpponent(player);
		
		for (int x = 0; x < ChessGame.BOARD_WIDTH; x++)
			for (int y = 0; y < ChessGame.BOARD_HEIGHT; y++)
				if (board.getColorAt(x, y) == player) {
					Position[] targets = RuleEngine.getLegalMoves(board, x, y);
					for (Position target : targets) {
//						if (board.getColorAt(target) == player) {
//							board.dump(System.out);
//							System.out.println(GameUtility.getColorName(player));
//							System.out.println(new Move(new Position(x,y), target));
//						}
						assert(board.getColorAt(target) != player);
						
						Move move = new Move(new Position(x, y), target);
						String reason;
						int priority;
						
						if (board.getColorAt(target) == opponent) {
							reason = "killing move.";
							priority = 20;
						}
						else {
							reason = "normal move.";
							priority = 10;
						}
						
						candi.add(new Candidate(move, priority, reason));
					}
				}
	}

}
