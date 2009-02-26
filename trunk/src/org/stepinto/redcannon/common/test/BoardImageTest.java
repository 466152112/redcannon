package org.stepinto.redcannon.common.test;

import junit.framework.*;
import org.stepinto.redcannon.common.*;

public class BoardImageTest extends TestCase {
//	public void testCompressAndDepress() {
//		BoardImage board1 = GameUtility.createStartBoard();
//		byte[] c1 = board1.compress();
//		BoardImage board2 = new BoardImage(c1);
//		byte[] c2 = board2.compress(); 
//		
//		assertEquals(c1.length, c2.length);
//		for (int i = 0; i < c1.length; i++)
//			assertEquals(c1[i], c2[i]);
//	}
	
	public void testZobristCode() {
		BoardImage board = GameUtility.createStartBoard();
		long zobrist = board.getZobristCode();
		board.performMove(new Move(new Position(4, 0), new Position(4, 1)));
		board.performMove(new Move(new Position(4, 1), new Position(4, 2)));
		board.performMove(new Move(new Position(4, 2), new Position(4, 0)));
		assertEquals(zobrist, board.getZobristCode());
	}
	
	public void testSetColorAndUnit() {
		BoardImage board = new BoardImage();
		
		assertEquals(ChessGame.EMPTY, board.getColorAt(0, 0));
		assertEquals(ChessGame.EMPTY, board.getUnitAt(0, 0));
		
		board.setColorAt(0, 0, ChessGame.RED);
		board.setUnitAt(0, 0, ChessGame.KING);
		assertEquals(ChessGame.RED, board.getColorAt(0, 0));
		assertEquals(ChessGame.KING, board.getUnitAt(0, 0));
	}
}
