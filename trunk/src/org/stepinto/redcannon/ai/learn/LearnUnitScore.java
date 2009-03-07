package org.stepinto.redcannon.ai.learn;

import java.util.*;
import org.apache.commons.lang.*;
import org.stepinto.redcannon.ai.*;
import org.stepinto.redcannon.common.*;

public class LearnUnitScore {
	private int cpuNum = 2;
	private int populationSize = 32;
	private int generationNum = 32;
	private double mutationRate = 0.15;
	private double crossoverRate = 0.15;
	private boolean verbose = false;
	private int moveTime = 1000;
	private boolean randomStart; 
	
	public int getCpuNum() {
		return cpuNum;
	}

	public void setCpuNum(int cpuNum) {
		this.cpuNum = cpuNum;
	}

	public int getPopulationSize() {
		return populationSize;
	}

	public void setPopulationSize(int populationSize) {
		this.populationSize = populationSize;
	}

	public int getGenerationNum() {
		return generationNum;
	}

	public void setGenerationNum(int generationNum) {
		this.generationNum = generationNum;
	}

	public double getMutationRate() {
		return mutationRate;
	}

	public void setMutationRate(double mutationRate) {
		this.mutationRate = mutationRate;
	}

	public double getCrossoverRate() {
		return crossoverRate;
	}

	public void setCrossoverRate(double crossoverRate) {
		this.crossoverRate = crossoverRate;
	}

	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	public int getMoveTime() {
		return moveTime;
	}

	public void setMoveTime(int moveTime) {
		this.moveTime = moveTime;
	}

	public boolean isRandomStart() {
		return randomStart;
	}

	public void setRandomStart(boolean randomStart) {
		this.randomStart = randomStart;
	}

	public void run() {
		Random random = new Random();
		ArrayList<Individual> individuals = new ArrayList<Individual>();
		
		// first fill individuals
		if (randomStart) {
			for (int i = 0; i < populationSize; i++) {
				int array[] = new int [8];
				array[0] = 0;
				array[1] = 500;
				for (int j = 2; j < array.length; j++)
					array[j] = random.nextInt(16);
				individuals.add(new Individual(array));
			}
		}
		else {
			for (int i = 0; i < populationSize; i++)
				individuals.add(new Individual(NaiveUnitScoreTable.DEFAULT_TABLE));
		}
		
		// re-produce
		for (int i = 0; i < generationNum; i++) {			
			// log
			System.out.println(">> Entering generation #" + i + "...");
			
			// crossover
			System.out.println(">> Processing crossovers...");
			for (int j = 0; j < crossoverRate * populationSize; j++) {
				int p = 0; 
				int q = 0;
				while (p == q) {
					p = random.nextInt(populationSize);
					q = random.nextInt(populationSize);
				}
				Individual newIndividual = Individual.crossover(random, individuals.get(p), individuals.get(q)); 
				individuals.add(newIndividual);
				
				if (verbose)
					System.out.printf("%s and %s --> %s\n", individuals.get(p), individuals.get(q), newIndividual);
			}
			
			// mutation
			System.out.println(">> Processing mutations...");
			for (int j = 0; j < mutationRate * populationSize; j++) {
				int p = random.nextInt(populationSize);
				Individual newIndividual = individuals.get(p).mutate(random);
				individuals.add(newIndividual);
				
				if (verbose)
					System.out.printf("%s --> %s\n", individuals.get(p), newIndividual);
			}
			
			// selection
			System.out.println(">> Selecting good individuals...");
			final Map<Individual, Integer> scores = Collections.synchronizedMap(new HashMap<Individual, Integer>());
			for (Individual indv : individuals)
				scores.put(indv, 0);
			
			WorkQueue tournamentTasks = new WorkQueue(cpuNum);			
			for (int j = 0; j < individuals.size(); j++)
				for (int k = 0; k < individuals.size(); k++)
					if (j != k)
						tournamentTasks.addTask(new TournamentTask(scores, individuals.get(j), individuals.get(k), moveTime, verbose));
			tournamentTasks.executeAll();
			
			Collections.sort(individuals, new IndividualComparator(scores));
			while (individuals.size() > populationSize)
				individuals.remove(individuals.size() - 1);
			
			// print result
			System.out.println(">> Generation #" + i + " results:"); 
			for (Individual indv : individuals) {
				System.out.printf("%s: %d\n", indv, scores.get(indv));
			}
		}
	}
	
	private static class TournamentTask implements Runnable {
		private Map<Individual, Integer> scores;
		private Individual red, black;
		private int moveTime;
		private boolean verbose;
		
		public TournamentTask(Map<Individual, Integer> scores, Individual red,
				Individual black, int moveTime, boolean verbose) {
			this.scores = scores;
			this.red = red;
			this.black = black;
			this.moveTime = moveTime;
			this.verbose = verbose;
		}
		
		public void run() {
			TimeCounter counter = new TimeCounter();
			counter.start();
			int winner = runGame(new NaiveUnitScoreTable(red.toIntArray()), new NaiveUnitScoreTable(black.toIntArray()));
			switch (winner) {
			case ChessGame.RED:
				scores.put(red, scores.get(red) + 3);
				break;
			case ChessGame.BLACK:
				scores.put(black, scores.get(black) + 3);
				break;
				
			case ChessGame.EMPTY:
				scores.put(red, scores.get(red) + 1);
				scores.put(black, scores.get(black) + 1);
				break;
			}

			if (verbose) {
				System.out.printf("[%s] %s vs. %s:  winner = %s, time = %s\n", Thread.currentThread().getName(), red, black, GameUtility.getColorName(winner),
						counter.getTimeString());
			}
		}
		
		public int runGame(NaiveUnitScoreTable redSt, NaiveUnitScoreTable blackSt) {
			BoardImage board = GameUtility.createStartBoard();
			StateSet historyStates = new StateSet();
			int player = ChessGame.RED;
			int round = 0;
			
			while (round < 200) {
				TimeCounter counter = new TimeCounter();	
				historyStates.add(board, player);
				
				SearchEngine engine = new IterativeSearchEngine(board, player);
				engine.addEvaluator(new NaiveEvaluator(player == ChessGame.RED ? redSt : blackSt));
				engine.addSelector(new NaiveSelector(historyStates));
				engine.setTimeLimit(moveTime);
				
				counter.start();
				Move move = engine.search().getBestMove();
				if (verbose)
					System.out.printf("[%s] round %d: move = %s, time = %s, states = %d\n", Thread.currentThread().getName(), round, move, counter.getTimeString(), engine.getStatistics().getNumberOfStates());
				
				if (move == null)
					return GameUtility.getOpponent(player);
				board.performMove(move);
				player = GameUtility.getOpponent(player);
				round++;
			}
			
			return ChessGame.EMPTY;
		}
	}
	
	private static class IndividualComparator implements Comparator<Individual> {
		private Map<Individual, Integer> scores;
		
		public IndividualComparator(Map<Individual, Integer> scores) {
			this.scores = scores;
		}
		
		@Override
		public int compare(Individual a, Individual b) {
			return scores.get(b) - scores.get(a);
		}
	}
	
	public static void main(String args[]) {
		if (ArrayUtils.contains(args, "--help")) {
			System.out.printf("%s --cpu [num] --population [num] --move-time [s] --verbose --random-start \n", LearnUnitScore.class.getName());
		}
		else {
			LearnUnitScore learner = new LearnUnitScore();
			for (int i = 0; i < args.length; i++)
				if (args[i].equals("--cpu")) {
					i++;
					learner.setCpuNum(Integer.parseInt(args[i]));
				}
				else if (args[i].equals("--population")) {
					i++;
					learner.setPopulationSize(Integer.parseInt(args[i]));
				}
				else if (args[i].equals("--move-time")) {
					i++;
					learner.setMoveTime(Integer.parseInt(args[i]));
				}
				else if (args[i].equals("--verbose")) {
					learner.setVerbose(true);
				}
				else if (args[i].equals("--random-start")) {
					learner.setRandomStart(true);
				}
			learner.run();
		}
	}
}
