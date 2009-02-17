package org.stepinto.redcannon.ai;

import java.util.*;
import org.stepinto.redcannon.common.*;

public interface Selector {
	public void select(List<Candidate> candi, BoardImage board, int player, int depth, boolean debug); 
}
