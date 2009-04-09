package org.stepinto.redcannon.ai;

import java.io.*;
import java.util.*;

import org.stepinto.redcannon.ai.learn.*;
import org.stepinto.redcannon.ai.log.*;
import org.stepinto.redcannon.common.*;

public class RankingSelector implements Selector {
	public static int PROPERTY_DIMENSIONS = 22;
	
	private NaiveSelector naive;
	private RankingSVM<CandidateWithProperties> svm;
	private PrintStream dumpTrainingCasesStream;
	
	public RankingSelector() {
		this(null, null);
	}
	
	public RankingSelector(StateSet historyStates, PrintStream stream) {
		naive = new NaiveSelector(historyStates);
		dumpTrainingCasesStream = stream;
	}
	
	public void train(File dataFile) throws IOException {
		svm = new RankingSVM<CandidateWithProperties>(
				new CandidateFeatureExtractor(PROPERTY_DIMENSIONS)
			  );
		
		BufferedReader reader = new BufferedReader(new FileReader(dataFile));
		String line;
		
		while ((line = reader.readLine()) != null) {
			String parts[] = line.split("\t");
			if (parts.length == PROPERTY_DIMENSIONS + 1) {
				int y = Integer.parseInt(parts[0]);
				double x[] = new double [PROPERTY_DIMENSIONS];
				for (int i = 1; i < parts.length; i++) {
					String tmp[] = parts[i].split(":");
					if (tmp.length == 2) {
						int label = Integer.parseInt(tmp[0]);
						double value = Double.parseDouble(tmp[1]);
						x[label-1] = value;
					}
				}
				svm.addRaw(x, y);
			}
		}
		
		reader.close();
		svm.train();
	}

	@Override
	public void notifyBestMove(BoardImage board, int player, int depth,
			Move bestMove, int score, List<Candidate> candi) {
		naive.notifyBestMove(board, player, depth, bestMove, score, candi);
		
		if (bestMove != null) {
			CandidateWithProperties bestCandi = null;
			for (Candidate c : candi)
				if (c instanceof CandidateWithProperties && c.getMove().equals(bestMove)) {
					bestCandi = (CandidateWithProperties)c;
					break;
				}
			
			double bestCandiProp[] = bestCandi.getProperties();
			for (Candidate c : candi)
				if (c instanceof CandidateWithProperties) {
					if (c == bestCandi)
						break;
					double currentProp[] = ((CandidateWithProperties)c).getProperties();
					
					if (dumpTrainingCasesStream != null)
						printTrainingCase(dumpTrainingCasesStream, bestCandiProp, currentProp);
				}
		}
	}
	
	private void printTrainingCase(PrintStream out, double prop1[], double prop2[]) {
		assert(prop1.length == prop2.length);
		assert(prop1.length == PROPERTY_DIMENSIONS);
		double diff[] = new double [PROPERTY_DIMENSIONS];
		for (int i = 0; i < diff.length; i++)
			diff[i] = prop1[i] - prop2[i];
		
		out.print("-1");
		for (int i = 0; i < diff.length; i++)
			out.printf("\t%d:%f", i+1, diff[i]);
		out.println();
		out.print("1");
		for (int i = 0; i < diff.length; i++)
			out.printf("\t%d:%f", i+1, -diff[i]);
		out.println();
	}

	@Override
	public void select(List<Candidate> candi, BoardImage board, int player,
			int depth, int alpha, int beta, int depthLimit,
			StateHash<StateInfo> hash, SearchLogger logger) {
		// find raw candidates
		List<Candidate> rawCandi = new ArrayList<Candidate>();
		naive.select(rawCandi, board, player, depth, alpha, beta, depthLimit, hash, logger);
		
		// attach properties with them
		List<CandidateWithProperties> propCandi = new ArrayList<CandidateWithProperties>();
		for (Candidate raw : rawCandi)
			propCandi.add(new CandidateWithProperties(raw.getMove(), raw.getPriority(), raw.getReason(),
					makeProperties(board, player, depth, raw)));
		
		// sort using our svm
		if (svm != null) {
			Collections.sort(propCandi, svm.getComparator());
			for (Candidate c : propCandi)
				c.setPriority(20);
		}
		candi.addAll(propCandi);
	}
	
	private double[] makeProperties(BoardImage board, int player, int depth, Candidate candi) {
		HistoryMoveTable historyTable = naive.getHistoryTable();
		KillerMoveTable killerTable = naive.getKillerTable();
		UnitScoreTable unitScoreTable = naive.getUnitScoreTable();
		
		Move move = candi.getMove();
		int killedUnit = board.performMove(move);
		int movingUnit = board.getUnitAt(move.getTarget());
		
		// properties
		// 0: history-score
		// 1: killer-score
		// 2: source-x
		// 3: source-y
		// 4: target-x
		// 5: target-y
		// 6-12: source unit type
		// 13: source unit score
		// 14-20: target unit type
		// 21: target unit score
		double prop[] = new double[22];
		prop[0] = historyTable.getMoveScore(move);
		prop[1] = killerTable.getMoveScore(depth, move);
		prop[2] = move.getSource().getX();
		prop[3] = convertY(player, move.getSource().getY());
		prop[4] = move.getTarget().getX();
		prop[5] = convertY(player, move.getTarget().getY());
		prop[5 + movingUnit] = 1;
		prop[13] = unitScoreTable.getUnitScore(move.getSource(), movingUnit);
		if (killedUnit != ChessGame.EMPTY)
			prop[13 + killedUnit] = 1;
		prop[21] = unitScoreTable.getUnitScore(move.getTarget(), killedUnit);
		
		board.unperformMove(move, killedUnit);
		return prop;
	}
	
	private int convertY(int player, int y) {
		return player == ChessGame.RED ? y  : ChessGame.BOARD_HEIGHT - 1 - y;
	}
}
