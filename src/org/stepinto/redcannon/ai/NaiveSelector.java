package org.stepinto.redcannon.ai;

import java.util.*;
import org.stepinto.redcannon.common.*;
import org.stepinto.redcannon.ai.log.*;

public class NaiveSelector implements Selector {

	@Override
	public void select(List<Candidate> candi, BoardImage board, int player,
			int depth, int alpha, int beta, int depthLimit, StateHash hash, SearchLogger logger) {
		int opponent = GameUtility.getOpponent(player);
		
		for (int x = 0; x < ChessGame.BOARD_WIDTH; x++)
			for (int y = 0; y < ChessGame.BOARD_HEIGHT; y++)
				if (board.getColorAt(x, y) == player) {
					Position[] targets = RuleEngine.getLegalMoves(board, x, y);
					for (Position target : targets) {
						assert(board.getColorAt(target) != player);
						
						Move move = new Move(new Position(x, y), target);
						int killedUnit = board.performMove(move);
						StateInfo hashedState = hash.lookUp(board, opponent);
						String reason;
						int priority;
						
						// if (board.getColorAt(target) == opponent) {
						if (hashedState != null && hashedState.getBeta() >= -alpha) {
							reason = "hashed move.";
							priority = -hashedState.getAlpha() + 10;
						}
						else {
							if (killedUnit != ChessGame.EMPTY) {
								reason = "killing move.";
								priority = 10 + UnitScoreUtility.getUnitScore(killedUnit);
							}
							else {
								reason = "normal move.";
								priority = 10;
							}
						}
						
						
						board.unperformMove(move, killedUnit);
						candi.add(new Candidate(move, priority, reason));
					}
				}
	}

}
