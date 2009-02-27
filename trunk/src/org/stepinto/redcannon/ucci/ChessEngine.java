package org.stepinto.redcannon.ucci;

import java.io.*;
import org.stepinto.redcannon.common.*;

public interface ChessEngine {
	public void hello(PrintStream out);
	public void setOption(String key, String value) throws UnsupportedOptionException;
	public void setGameState(GameState state, Move moves[]);
	public void setBanMoves(Move banMoves[]);
	public Move go(PrintStream out);
	public Move stop();
}
