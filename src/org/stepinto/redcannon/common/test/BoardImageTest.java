package org.stepinto.redcannon.common.test;

import junit.framework.*;
import org.stepinto.redcannon.common.*;

public class BoardImageTest extends TestCase {
	public void testCompressAndDepress() {
		BoardImage board1 = GameUtility.createStartBoard();
		byte[] c1 = board1.compress();
		BoardImage board2 = new BoardImage(c1);
		byte[] c2 = board2.compress(); 
		
		assertEquals(c1.length, c2.length);
		for (int i = 0; i < c1.length; i++)
			assertEquals(c1[i], c2[i]);
	}
}
