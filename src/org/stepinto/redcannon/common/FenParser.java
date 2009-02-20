package org.stepinto.redcannon.common;

import java.io.*;

public class FenParser {
	public static GameState parseString(String str)
			throws InvalidFenFormatException {
		String parts[] = str.replaceAll(";.*", "").split("\\ +");
		if (parts.length != 6)
			throw new InvalidFenFormatException(
					"More or less parts in fen string");

		// process 1st part
		BoardImage board = buildBoard(parts[0]);

		// process 2nd part
		int player;
		if (parts[1].length() != 1)
			throw new InvalidFenFormatException(
					"More characters found in the second parts of fen string.");
		if (parts[1].equals("w") || parts[1].equals("r"))
			player = ChessGame.RED;
		else if (parts[1].equals("b"))
			player = ChessGame.BLACK;
		else
			throw new InvalidFenFormatException("Unexpected char: " + parts[1]
					+ ".");

		// ignore other parts
		return new GameState(board, player);
	}

	public static GameState parseFile(File file) throws IOException,
			InvalidFenFormatException {
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(file)));
		StringBuffer sb = new StringBuffer();
		String line;

		while ((line = br.readLine()) != null)
			sb.append(line);
		return parseString(sb.toString());
	}

	public static GameState parseFile(String filePath) throws IOException,
			InvalidFenFormatException {
		return parseFile(new File(filePath));
	}

	private static BoardImage buildBoard(String str)
			throws InvalidFenFormatException {
		BoardImage board = new BoardImage();
		int x = 0;
		int y = 0;
		int i;

		for (i = 0; i < str.length() && x <= ChessGame.BOARD_WIDTH
				&& y <= ChessGame.BOARD_HEIGHT; i++) {
			char ch = str.charAt(i);
			switch (ch) {
			case 'r':
				board.setColorAt(x, y, ChessGame.BLACK);
				board.setUnitAt(x, y, ChessGame.ROOK);
				x++;
				break;
			case 'n':
				board.setColorAt(x, y, ChessGame.BLACK);
				board.setUnitAt(x, y, ChessGame.HORSE);
				x++;
				break;
			case 'b':
				board.setColorAt(x, y, ChessGame.BLACK);
				board.setUnitAt(x, y, ChessGame.ELEPHANT);
				x++;
				break;
			case 'a':
				board.setColorAt(x, y, ChessGame.BLACK);
				board.setUnitAt(x, y, ChessGame.ADVISOR);
				x++;
				break;
			case 'k':
				board.setColorAt(x, y, ChessGame.BLACK);
				board.setUnitAt(x, y, ChessGame.KING);
				x++;
				break;
			case 'c':
				board.setColorAt(x, y, ChessGame.BLACK);
				board.setUnitAt(x, y, ChessGame.CANNON);
				x++;
				break;
			case 'p':
				board.setColorAt(x, y, ChessGame.BLACK);
				board.setUnitAt(x, y, ChessGame.PAWN);
				x++;
				break;
			case 'R':
				board.setColorAt(x, y, ChessGame.RED);
				board.setUnitAt(x, y, ChessGame.ROOK);
				x++;
				break;
			case 'N':
				board.setColorAt(x, y, ChessGame.RED);
				board.setUnitAt(x, y, ChessGame.HORSE);
				x++;
				break;
			case 'B':
				board.setColorAt(x, y, ChessGame.RED);
				board.setUnitAt(x, y, ChessGame.ELEPHANT);
				x++;
				break;
			case 'A':
				board.setColorAt(x, y, ChessGame.RED);
				board.setUnitAt(x, y, ChessGame.ADVISOR);
				x++;
				break;
			case 'K':
				board.setColorAt(x, y, ChessGame.RED);
				board.setUnitAt(x, y, ChessGame.KING);
				x++;
				break;
			case 'C':
				board.setColorAt(x, y, ChessGame.RED);
				board.setUnitAt(x, y, ChessGame.CANNON);
				x++;
				break;
			case 'P':
				board.setColorAt(x, y, ChessGame.RED);
				board.setUnitAt(x, y, ChessGame.PAWN);
				x++;
				break;
			case '/':
				x = 0;
				y++;
				break;
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				x += (ch - '0');
				break;
			default:
				throw new InvalidFenFormatException("Unexpected char: " + ch
						+ ".");
			}
		}

		if (i < str.length())
			throw new InvalidFenFormatException("Out of board.");
		if (x != ChessGame.BOARD_WIDTH || y != ChessGame.BOARD_HEIGHT - 1)
			throw new InvalidFenFormatException("Out of board.");

		return board;
	}
}
