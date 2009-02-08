package org.stepinto.redcannon.common.test;

import org.stepinto.redcannon.common.*;
import junit.framework.*;

public class FenParserTest extends TestCase {
	public void test1() throws Exception {
		final String FEN_STRING = "rnbakabnr/9/1c5c1/p1p1p1p1p/9/9/P1P1P1P1P/1C5C1/9/RNBAKABNR w - - 0 1";
		BoardImage expectedBoard = GameUtility.createStartBoard();
		int expectedPlayer = ChessGame.RED;
		GameState state = FenParser.parseString(FEN_STRING);
		
		assertEquals(expectedBoard, state.getBoard());
		assertEquals(expectedPlayer, state.getPlayer());
	}
}
