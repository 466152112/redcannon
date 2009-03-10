package org.stepinto.redcannon.ai;

import java.util.*;
import org.stepinto.redcannon.common.*;
import org.stepinto.redcannon.ai.log.*;

public class NaiveSelector implements Selector {
	private StateSet historyStates;
	
	public NaiveSelector() {
		historyStates = null;
	}
	
	public NaiveSelector(StateSet historyStates) {
		this.historyStates = historyStates;
	}

	@Override
	public void select(List<Candidate> candi, BoardImage board, int player,
			int depth, int alpha, int beta, int depthLimit,
			StateHash<StateInfo> hash, SearchLogger logger) {
		int opponent = GameUtility.getOpponent(player);

		for (int x = 0; x < ChessGame.BOARD_WIDTH; x++)
			for (int y = 0; y < ChessGame.BOARD_HEIGHT; y++)
				if (board.getColorAt(x, y) == player) {
					Position[] targets = RuleEngine.getLegalMoves(board, x, y);
					for (Position target : targets) {
						assert (board.getColorAt(target) != player);

						Move move = new Move(new Position(x, y), target);
						int killedUnit = board.performMove(move);
						// this move leads to a new state that never happended
						// in history (which is ban move)
						if (historyStates == null || !historyStates.contains(board, opponent)) {
							StateInfo hashedState = hash.lookUp(board, opponent);
							String reason;
							int priority;
							
							if (hashedState != null && hashedState.getBeta() >= -alpha) {
								reason = "historical heuristic move.";
								priority = -hashedState.getAlpha() + 10;
							} else {
								if (killedUnit != ChessGame.EMPTY) {
									reason = "killing move.";
									priority = 10 + unitScoreTable.getUnitScore(target, killedUnit);
								} else {
									reason = "normal move.";
									priority = 10;
								}
							}

							priority += historyTable.getMoveScore(move);
							priority += killerTable.getMoveScore(depth, move);

							candi.add(new Candidate(move, priority, reason));
						}

						board.unperformMove(move, killedUnit);
					}
				}
	}

	@Override
	public void notifyBestMove(BoardImage board, int player, int depth,
			Move bestMove, int score, List<Candidate> candi) {
		if (bestMove != null) {
			historyTable.addMove(bestMove);
			killerTable.addMove(depth, bestMove);
		}
		// if (depth == 0)
		// historyTable.dump(System.out);
	}

	private UnitScoreTable unitScoreTable = new NaiveUnitScoreTable();
	private HistoryMoveTable historyTable = new HistoryMoveTable();
	private KillerMoveTable killerTable = new KillerMoveTable();
}
