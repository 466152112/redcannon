package org.stepinto.redcannon.ai;

import java.util.*;
import org.stepinto.redcannon.common.*;
import org.stepinto.redcannon.ai.log.*;

public interface Selector {
	public void select(List<Candidate> candi, BoardImage board, int player, int depth, int alpha, int beta, int depthLimit, StateHash hash, SearchLogger logger); 
}
