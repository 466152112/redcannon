package org.stepinto.redcannon.ucci;

import java.io.*;
import org.stepinto.redcannon.common.*;
import org.stepinto.redcannon.ai.*;

public class RedCannonEngine implements ChessEngine {
	private SearchEngine engine;
	
	public RedCannonEngine(SearchEngine engine) {
		this.engine = engine;
	}
	
	@Override
	public Move go(PrintStream out) {		
		SearchResult result = engine.search();
		result.dump(out);
		engine.getStatistics().dump(out);
		return result.getBestMove();
	}

	@Override
	public void hello(PrintStream out) {
		out.println("RedCannon, Chinese Chess Engine");
		out.println("by Chao Shi <charlescpp@gmail.com>");
	}

	@Override
	public void setBanMoves(Move[] banMoves) {
		// TODO: to support ban-moves
	}

	@Override
	public void setGameState(GameState state, Move[] moves) {
		BoardImage board = state.getBoard().duplicate();
		int player = state.getPlayer();
		
		for (Move m : moves) {
			board.performMove(m);
			player = GameUtility.getOpponent(player);
		}
		
		engine.setInitialBoard(board);
		engine.setInitialPlayer(player);
	}

	@Override
	public void setOption(String key, String value)
			throws UnsupportedOptionException {
		// TODO: to support options
	}

	@Override
	public Move stop() {
		// TODO to support stops
		return null;
	}

}
