package solver;

import view.Grid;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HoleDigger {

	private int numberOfFilledSquaresLeft;
	private final int finalGridCount;
	private final int gridDimension;
	private final int subGridDimension;
	private int[] rowCounts;
	private int[] columnCounts;
	private int[][] blockCounts;
	private final Solver solver;

	public HoleDigger(int dimension, int numHoles) {
		numberOfFilledSquaresLeft = dimension * dimension;
		finalGridCount = numberOfFilledSquaresLeft - numHoles;
		gridDimension = dimension;
		subGridDimension = (int) Math.sqrt(dimension);
		rowCounts = initializeStartingCounts();
		columnCounts = initializeStartingCounts();
		blockCounts = initializeStartingBlockCounts();
		solver = new Solver(gridDimension);
	}
	
	public int[][] digHolesInSolution(int[][] solution) {
		int[][] puzzle = deepCopySudokuGrid(solution);
		return emptyRandomSquaresToMatchDifficulty(puzzle);
	}

	private int[][] emptyRandomSquaresToMatchDifficulty(int[][] puzzle) {
		List<int[]> uncheckedSquares = initializeListOfAllSquares();

		while(!uncheckedSquares.isEmpty()) {
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
		puzzle[rowCoordinate][colCoordinate] = Grid.EMPTY_SQUARE;
		decrementGridCounts(rowCoordinate, colCoordinate);
		numberOfFilledSquaresLeft--;
	}

	private boolean squareIsEmptyable(int[][] puzzle, int randX, int randY) {
		return holeLeavesSufficientInfo(randX, randY)
				&& numberOfFilledSquaresLeft > finalGridCount
				&& keepsUniquenessOfSolution(puzzle, randX, randY);
	}

	private boolean keepsUniquenessOfSolution(int[][] puzzle, int rowCoordinate, int colCoordinate) {
		int[][] solutionTesterPuzzle = deepCopySudokuGrid(puzzle);
		int numberAtCoordinates = puzzle[rowCoordinate][colCoordinate];
		solutionTesterPuzzle[rowCoordinate][colCoordinate] = Grid.EMPTY_SQUARE;
		solver.createValidSolution(solutionTesterPuzzle, IntStream.of(numberAtCoordinates).boxed().collect(Collectors.toSet()));
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

	private void decrementGridCounts(int rowCoordinate, int colCoordinate) {
		decrementRowCount(rowCoordinate);
		decrementColumnCounts(colCoordinate);
		decrementBlockCounts(rowCoordinate, colCoordinate);
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

	private int[][] deepCopySudokuGrid(int[][] inputGrid) {
		int[][] outputGrid = new int[gridDimension][gridDimension];
		for (int i = 0; i < gridDimension; i++) {
			for (int j = 0; j < gridDimension; j++) {
				outputGrid[i][j] = inputGrid[i][j];
			}
		}
		return outputGrid;
	}

}
