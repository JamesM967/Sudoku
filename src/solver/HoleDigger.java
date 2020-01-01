package solver;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HoleDigger {

	private int numberOfFilledSquaresLeft;
	private int finalGridCount;
	private int gridDimension;
	private int subGridDimension;
	private int[] rowCounts;
	private int[] columnCounts;
	private int[][] blockCounts;

	public HoleDigger(int dimension, int numHoles) {
		numberOfFilledSquaresLeft = dimension * dimension;
		finalGridCount = numberOfFilledSquaresLeft - numHoles;
		gridDimension = dimension;
		subGridDimension = (int) Math.sqrt(dimension);
		rowCounts = initializeStartingCounts();
		columnCounts = initializeStartingCounts();
		blockCounts = initializeStartingBlockCounts();
	}
	
	public int[][] digHolesInSolution(int[][] solution) {
		int[][] puzzle = solution.clone();
		return emptyRandomSquaresToMatchDifficulty(puzzle);
	}

	private int[][] emptyRandomSquaresToMatchDifficulty(int[][] puzzle) {
		List<int[]> uncheckedSquares = initializeListOfAllSquares();

		for (int i = 0; i < gridDimension * gridDimension; i++) {
			int[] randomSquareCooridinates = pickRandomCell(uncheckedSquares);
			int randI = randomSquareCooridinates[0];
			int randJ = randomSquareCooridinates[1];
			if (squareIsEmptyable(puzzle, randI, randJ)) {
				emptySquare(puzzle, randI, randJ);
			}
		}
		return puzzle;
	}

	private List<int[]> initializeListOfAllSquares() {
		List<int[]> allSquares = new ArrayList<>();

		for (int i = 0; i < gridDimension; i++) {
			for (int j = 0; j < gridDimension; j++) {
				int[] cellTemp = new int[2];
				cellTemp[0] = i;
				cellTemp[1] = j;
				allSquares.add(cellTemp);
			}
		}
		return allSquares;
	}

	private int[] pickRandomCell(List<int[]> uncheckedSquares) {
		Random generator = new Random();
		int rand = generator.nextInt(uncheckedSquares.size());
		int[] cell = uncheckedSquares.get(rand);
		uncheckedSquares.remove(rand);
		return cell;
	}
	
	private void emptySquare(int[][] puzzle, int rowCoordinate, int colCoordinate) {
		puzzle[rowCoordinate][colCoordinate] = -1;
		decrementGridCounts(rowCoordinate, colCoordinate);
		numberOfFilledSquaresLeft--;
	}

	private boolean squareIsEmptyable(int[][] puzzle, int randX, int randY) {
		return holeLeavesSufficientInfo(randX, randY)
				&& numberOfFilledSquaresLeft > finalGridCount
				&& keepsUniquenessOfSolution(puzzle, randX, randY);
	}

	private boolean keepsUniquenessOfSolution(int[][] puzzle, int randX, int randY) {
		Solver solver;
		int[][] solutionTesterPuzzle = puzzle.clone();
		solver = new Solver(gridDimension, solutionTesterPuzzle);
		solver.substitute(randX, randY);
		solver.createValidSolution();
		return solver.holeDug();
	}

	private boolean holeLeavesSufficientInfo(int randI, int randJ) {
		return enoughInRow(randI) || enoughInCol(randJ) || enoughInBlock(randI, randJ);
	}

	private boolean enoughInRow(int i) {
		return rowCounts[i] > 0;
	}

	private boolean enoughInCol(int j) {
		return columnCounts[j] > 0;
	}

	private boolean enoughInBlock(int i, int j) {
		return blockCounts[i][j] > 0;
	}

	private void decrementGridCounts(int rowCoor, int colCoor) {
		decrementRowCount(rowCoor);
		decrementColumnCounts(colCoor);
		decrementBlockCounts(rowCoor, colCoor);
	}

	private void decrementRowCount(int rowCoordinate) {
		rowCounts[rowCoordinate]--;
	}
	
	private void decrementColumnCounts(int colCoordinate) {
		columnCounts[colCoordinate]--;
	}
	
	private void decrementBlockCounts(int rowCoordinate, int colCoordinate) {
		int rowBlock = rowCoordinate / subGridDimension;
		int columnBlock = colCoordinate / subGridDimension;
		blockCounts[rowBlock][columnBlock]--;
	}

	private int[] initializeStartingCounts() {
		int[] counts = new int[gridDimension];
		for (int i = 0; i < gridDimension; i++) {
			counts[i] = gridDimension;
		}
		return counts;
	}

	private int[][] initializeStartingBlockCounts() {
		int[][] counts = new int[subGridDimension][subGridDimension];
		for (int i = 0; i < subGridDimension; i++) {
			for (int j = 0; j < subGridDimension; j++) {
				counts[i][j] = gridDimension;
			}
		}
		return counts;
	}

}
