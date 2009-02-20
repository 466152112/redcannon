package org.stepinto.redcannon.common;

import java.util.Arrays;

public class RuleEngine {
	// advisor
	private static Position[] getAdvisorLegalMoves(BoardImage board, int x, int y) {
		assert(board.getUnitAt(x, y) == ChessGame.ADVISOR);
		
		final int DX[] = {1, -1, 1, -1};
		final int DY[] = {1, 1, -1, -1};
		Position[] legalMoves = new Position[4];
		int count = 0;
		int player = board.getColorAt(x, y);
		
		for (int i = 0; i < DX.length; i++) {
			int dx = DX[i];
			int dy = DY[i];
			int nx = x + dx;
			int ny = y + dy;
			
			if (Position.isValid(nx, ny) && board.getColorAt(nx, ny) != player && GameUtility.isInPalace(player, nx, ny)) {
				legalMoves[count] = new Position(nx, ny);
				count++;
			}
		}
		
		return Arrays.copyOf(legalMoves, count);
	}
	
	// cannon
	private static Position[] getCannonLegalMoves(BoardImage board, int x, int y) {
		assert(board.getUnitAt(x, y) == ChessGame.CANNON);
		
		final int MAX_LEGAL_MOVES = ChessGame.BOARD_WIDTH*2 + ChessGame.BOARD_HEIGHT*2 - 2;
		final int DX[] = {1, -1, 0, 0};
		final int DY[] = {0, 0, 1, -1};
		
		Position[] legalMoves = new Position[MAX_LEGAL_MOVES];
		int player = board.getColorAt(x, y);
		int count = 0;
		
		for (int i = 0; i < DX.length; i++) {
			int dx = DX[i];
			int dy = DY[i];
			int nx = x + dx;
			int ny = y + dy;
			while (Position.isValid(nx, ny) && board.isEmptyAt(nx, ny)) {
				legalMoves[count] = new Position(nx, ny);	
				nx += DX[i];
				ny += DY[i];
				count++;
			}
			if (Position.isValid(nx, ny)) {  // we meet another unit
				// skip it and continue to move, until we meet another or are out of board
				nx += dx;
				ny += dy;
				while (Position.isValid(nx, ny) && !board.isEmptyAt(nx, ny)) {
					nx += dx;
					ny += dy;
				}
				
				if (Position.isValid(nx, ny) && board.getColorAt(nx, ny) != player) {
					legalMoves[count] = new Position(nx, ny);
					count++;
				}
			}
		}
		
		return Arrays.copyOf(legalMoves, count);
	}
	
	private static Position[] getElephantLegalMoves(BoardImage board, int x, int y) {
		assert(board.getUnitAt(x, y) == ChessGame.ELEPHANT);
		
		final int DX[] = {2, 2, -2, -2};
		final int DY[] = {2, -2, 2, -2};
		final int BX[] = {1, 1, -1, -1};
		final int BY[] = {1, -1, 1, -1};
		
		Position[] legalMoves = new Position[4];
		int count = 0;
		int player = board.getColorAt(x, y);
		
		for (int i = 0; i < DX.length; i++) {
			int nx = x + DX[i];
			int ny = y + DY[i];
			int bx = x + BX[i];
			int by = y + BY[i];
			if (Position.isValid(nx, ny) && GameUtility.isBeyondRiver(player, ny) && board.getColorAt(nx, ny) != player && board.isEmptyAt(bx, by)) {
				legalMoves[count] = new Position(nx, ny);
				count++;
			}
		}
		
		return Arrays.copyOf(legalMoves, count);		
	}
	
	private static Position[] getHorseLegalMoves(BoardImage board, int x, int y) {
		assert(board.getUnitAt(x, y) == ChessGame.HORSE);
		
		final int HORSE_DX[] = {2, 2, -2, -2, 1, 1, -1, -1};
		final int HORSE_DY[] = {1, -1, 1, -1, 2, -2, 2, -2};
		final int BARRI_DX[] = {1, 1, -1, -1, 0, 0, 0, 0};
		final int BARRI_DY[] = {0, 0, 0, 0, 1, -1, 1, -1};
		
		Position[] legalMoves = new Position[HORSE_DX.length];
		int count = 0;
		int player = board.getColorAt(x, y);
		
		for (int i = 0; i < HORSE_DX.length; i++) {
			int bx = x + BARRI_DX[i];
			int by = y + BARRI_DY[i];
			int nx = x + HORSE_DX[i];
			int ny = y + HORSE_DY[i];
			
			if (Position.isValid(nx, ny) && board.isEmptyAt(bx, by) && board.getColorAt(nx, ny) != player) {
				legalMoves[count] = new Position(nx, ny);
				count++;
			}
		}
		
		return Arrays.copyOf(legalMoves, count);
	}
	
	private static Position[] getKingLegalMoves(BoardImage board, int x, int y) {
		assert(board.getUnitAt(x, y) == ChessGame.KING);
		
		final int DX[] = {1, -1, 0, 0};
		final int DY[] = {0, 0, 1, -1};
		int player = board.getColorAt(x, y);
		Position[] legalMoves = new Position[5];
		int count = 0;
		
		for (int i = 0; i < DX.length; i++) {
			int nx = x + DX[i];
			int ny = y + DY[i];
			if (Position.isValid(nx, ny) && GameUtility.isInPalace(player, nx, ny) && board.getColorAt(nx, ny) != player) {
				legalMoves[count] = new Position(nx, ny);
				count++;
			}
		}
		
		// flying move
		int attackDir = GameUtility.getAttackDirection(player);
		int nx = x;
		int ny = y + attackDir;
		while (Position.isValid(nx, ny) && board.isEmptyAt(nx, ny))
			ny += attackDir;
		if (Position.isValid(nx, ny)) {
			int unit = board.getUnitAt(nx, ny);
			if (unit == ChessGame.KING) {
				legalMoves[count] = new Position(nx, ny);
				count++;
			}
		}
		
		return Arrays.copyOf(legalMoves, count);
	}
	
	private static Position[] getPawnLegalMoves(BoardImage board, int x, int y) {
		int player = board.getColorAt(x, y);
		int attackDir = GameUtility.getAttackDirection(player);
		Position pos = new Position(x, y);
		
		if (GameUtility.isBeyondRiver(player, y)) {
			final int DX[] = {1, -1, 0};
			final int DY[] = {0, 0, attackDir};
			Position[] legalMoves = new Position[4];
			int count = 0;
			
			for (int i = 0; i < DX.length; i++) {
				Position adj = pos.move(DX[i], DY[i]);
				if (adj.isValid() && board.getColorAt(adj) != player) {
					legalMoves[count] = adj;
					count++;
				}
			}
			
			return Arrays.copyOf(legalMoves, count);
		}
		else {
			Position possibleMove = pos.move(0, attackDir);
			if (possibleMove.isValid() && board.getColorAt(possibleMove) != player) {
				Position[] legalMoves = new Position[1];
				legalMoves[0] = possibleMove;
				return legalMoves;
			}
			else
				return new Position[0];
		}
	}
	
	private static Position[] getRookLegalMoves(BoardImage board, int x, int y) {
		final int DX[] = {1, -1, 0, 0};
		final int DY[] = {0, 0, 1, -1};
		
		Position[] legalMoves = new Position[ChessGame.BOARD_WIDTH*2 + ChessGame.BOARD_HEIGHT*2 - 2];
		int count = 0;
		int player = board.getColorAt(x, y);
		int opponent = GameUtility.getOpponent(player);
		
		for (int i = 0; i < DX.length; i++) {
			int dx = DX[i];
			int dy = DY[i];
			int nx = x + dx;
			int ny = y + dx;
			while (Position.isValid(nx, ny) && board.isEmptyAt(nx, ny)) {
				legalMoves[count] = new Position(nx, ny);
				nx += dx;
				ny += dy;
				count++;
			}
			
			if (Position.isValid(nx, ny) && board.getColorAt(nx, ny) == opponent) {
				legalMoves[count] = new Position(nx, ny);
				count++;
			}
		}
		
		return Arrays.copyOf(legalMoves, count);
	}
	
	public static Position[] getLegalMoves(BoardImage board, int x, int y) {
		int unit = board.getUnitAt(x, y);
		switch (unit) {
		case ChessGame.ADVISOR:
			return getAdvisorLegalMoves(board, x, y);
		case ChessGame.CANNON:
			return getCannonLegalMoves(board, x, y);
		case ChessGame.HORSE:
			return getHorseLegalMoves(board, x, y);
		case ChessGame.KING:
			return getKingLegalMoves(board, x, y);
		case ChessGame.PAWN:
			return getPawnLegalMoves(board, x, y);
		case ChessGame.ROOK:
			return getRookLegalMoves(board, x, y);
		case ChessGame.ELEPHANT:
			return getElephantLegalMoves(board, x, y);
		default:
			assert(false);
			return null;
		}
	}
}
