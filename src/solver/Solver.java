package solver;

import view.Grid;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Solver {

	private static final int SMALLEST_POSSIBLE_SQUARE_VALUE = 1;

	private final int gridDimension;
	private Map<Integer, Map<Integer, Set<Integer>>> allRemainingPossibilitiesBoard;
	private Map<Integer, Set<Integer>> squaresSetInStone;
	private int[] countsOfAllNumbersOnGrid;
	private Set<Integer> disqualifiedNumbers;
	private boolean isHoleDug;
	
	public Solver(int dimension) {
		this.gridDimension = dimension;
	}

	public int[][] createValidSolution(int[][] puzzle) {
		return createValidSolution(puzzle, new HashSet<>());
	}

	int[][] createValidSolution(int[][] puzzle, Set<Integer> disqualifiedNumbers) {
		clearSolvingState();
		this.disqualifiedNumbers.addAll(disqualifiedNumbers);
		countNumbersOnGrid(puzzle);
		markNumbersSetInStone(puzzle);
		enterAll(puzzle);
		fillInBlanksOfSolution(puzzle);
		return dfsSolve(puzzle, 0, 0);
	}

	private void clearSolvingState() {
		allRemainingPossibilitiesBoard = new HashMap<>();
		squaresSetInStone = new HashMap<>();
		countsOfAllNumbersOnGrid = new int[gridDimension];
		disqualifiedNumbers = new HashSet<>();
		isHoleDug = false;
	}

	private int[][] dfsSolve(int[][] puzzle, int i, int j) {
		//off the board
		if (isOutOfBounds(i) || isOutOfBounds(j)) {
			isHoleDug = true;
			return puzzle;
		}

		Set<Integer> nonUpdatedPossibilities = getCurrentPossibilitiesForSquare(i, j);
		Set<Integer> possibilities = updatePossibilities(puzzle, i, j, nonUpdatedPossibilities);
		int chosenNumber = selectPossibleNumber(possibilities);

		//end of the board and it can't be changed
		if (atLastSquareOfGrid(i, j) && isSetInStone(i, j)) {
			return puzzle;
		}

		//this square was passed in as a given and cannot be changed
		if (isSetInStone(i, j)) {
			i = incrementIWhenJAtEnd(i, j);
			j = incrementJIndex(j);
			return dfsSolve(puzzle, i,j);
		}

		//there is a yet untested number for this square
		if (!optionsEmpty(chosenNumber)) {
			puzzle[i][j] = chosenNumber;
			removeOptionAsPossibility(i, j, chosenNumber);
			if (atLastSquareOfGrid(i, j)) {
				return puzzle;
			}
			i = incrementIWhenJAtEnd(i, j);
			j = incrementJIndex(j);
			return dfsSolve(puzzle, i, j);
		} else {
			createNewPossibilities(i, j);
			j = backupJ(j);
			i = backupIWhenJAtEnd(i, j);
			if (isOutOfBounds(i) || isOutOfBounds(j)) {
				isHoleDug = true;
				return puzzle;
			}
			while (isSetInStone(i, j)) {
				j = backupJ(j);
				i = backupIWhenJAtEnd(i, j);
				if (isOutOfBounds(i) || isOutOfBounds(j)) {
					isHoleDug = true;
					return puzzle;
				}
			}
			int number = puzzle[i][j];
			if (puzzle[i][j] != Grid.EMPTY_SQUARE) {
				removeOptionAsPossibility(i, j, number);
			}
			puzzle[i][j] = Grid.EMPTY_SQUARE;
			return dfsSolve(puzzle, i, j);
		}
	}

	private boolean atLastSquareOfGrid(int i, int j) {
		return i == gridDimension - 1 && j == gridDimension - 1;
	}

	private boolean optionsEmpty(int choice) {
		return choice == -1;
	}
	
	private Set<Integer> updatePossibilities(int[][] puzzle, int i, int j, Set<Integer> possibilities) {
		//Check all numbers in the square's row
		removePossibilitiesForRow(puzzle, i, possibilities);

		//Check all numbers in the square's column
		removePossibilitiesForColumn(puzzle, j, possibilities);

		//Check all numbers in the square's block
		removePossibilitiesForBlock(puzzle, i, j, possibilities);

		//Remove any disqualified values
		for (int k = 0; k < gridDimension; k++) {
			if (disqualifiedNumbers.contains(k)) {
				possibilities.remove(k);
			}
		}

		return possibilities;
	}

	private void removePossibilitiesForRow(int[][] puzzle, int i, Set<Integer> possibilities) {
		for (int j = 0; j < gridDimension; j++) {
			possibilities.remove(puzzle[i][j]);
		}
	}

	private void removePossibilitiesForColumn(int[][] puzzle, int j, Set<Integer> possibilities) {
		for (int i = 0; i < gridDimension; i++) {
			possibilities.remove(puzzle[i][j]);
		}
	}

	private void removePossibilitiesForBlock(int[][] puzzle, int i, int j, Set<Integer> possibilities) {
		int iblock = i / 3;
		int jblock = j / 3;
		int istart = iblock * 3;
		int jstart = jblock * 3;
		for (int l = istart; l < istart + 3; l++) {
			for (int m = jstart; m < jstart + 3; m++) {
				possibilities.remove(puzzle[l][m]);
			}
		}
	}
	
	private int backupIWhenJAtEnd(int i, int j) {
		if (j == gridDimension - 1) {
			i--;
		}
		return i;
	}

	private int backupJ(int j) {
		j--;
		if (j == -1) {
			j = gridDimension - 1;
		}
		return j;
	}

	private int incrementJIndex(int j) {
		j++;
		if (j >= gridDimension) {
			j = 0;
		}
		return j;
	}

	private int incrementIWhenJAtEnd(int i, int j) {
		if (j == gridDimension - 1) {
			i++;
		}
		return i;
	}

	private int selectPossibleNumber(Set<Integer> possibilities) {
		Random generator = new Random();
		if (possibilities.isEmpty()) {
			return -1;
		}
		int random  = generator.nextInt(possibilities.size());
		Integer[] choiceArray = possibilities.toArray(new Integer[0]);
		return choiceArray[random];
	}
	
	private void createNewPossibilities(int i, int j) {
		Map<Integer, Set<Integer>> iPossibilities =
				allRemainingPossibilitiesBoard.computeIfAbsent(i, k -> new HashMap<>());
		iPossibilities.compute(j, (k, v) -> createPossibleNumbersForASquare());
	}

	private Set<Integer> getCurrentPossibilitiesForSquare(int i, int j) {
		allRemainingPossibilitiesBoard.computeIfAbsent(i, k -> new HashMap<>());
		return allRemainingPossibilitiesBoard.get(i).computeIfAbsent(j, k -> createPossibleNumbersForASquare());
	}

	private void removeOptionAsPossibility(int i, int j, int choice) {
		Set<Integer> currentPossibilities = getCurrentPossibilitiesForSquare(i , j);
		currentPossibilities.remove(choice);
	}

	private void clearOutPossibilitiesForSquareExceptChosen(int num, int i, int j) {
		Set<Integer> remainingPossibilities = getCurrentPossibilitiesForSquare(i, j);
		remainingPossibilities.clear();
		remainingPossibilities.add(num);
	}

	private Set<Integer> createPossibleNumbersForASquare() {
		return IntStream
				.rangeClosed(SMALLEST_POSSIBLE_SQUARE_VALUE, gridDimension)
				.boxed()
				.collect(Collectors.toSet());
	}

	private void fillInBlanksOfSolution(int[][] puzzle) {
		for (int i = 0; i < gridDimension; i++) {
			for (int j = 0; j < gridDimension; j++) {
				if (puzzle[i][j] < SMALLEST_POSSIBLE_SQUARE_VALUE) {
					puzzle[i][j] = Grid.EMPTY_SQUARE;
				}
			}
		}
	}
	
	private void countNumbersOnGrid(int[][] puzzle) {
		int currentNum;
		for (int i = 0; i < gridDimension; i++) {
			for (int j = 0; j < gridDimension; j++) {
				currentNum = puzzle[i][j];
				if (squareValueIsValid(currentNum)) {
					countsOfAllNumbersOnGrid[currentNum - 1]++;
				}
			}
		}
	}

	/**
	 *
	 * Autofilling initial squares can optimize the solving process
	 * without sacrificing puzzle originality
	 *
	 * @param numAutoFilled Number of squares to randomly fill
	 */
	private void randomAutoFill(int[][] puzzle, int numAutoFilled) {
		Random generator = new Random();
		for (int i = 0; i < numAutoFilled; i++) {
			int randNum = -1;
			int randI = -1;
			int randJ = -1;
			while (randNum < 1 || !moreAllowed(randNum) || !isOpen(puzzle, randI, randJ)) {
				randI = generator.nextInt(gridDimension);
				randJ = generator.nextInt(gridDimension);
				Set<Integer> currentPossibilities = getCurrentPossibilitiesForSquare(randI, randJ);
				Set<Integer> possibilities = updatePossibilities(puzzle, randI, randJ, currentPossibilities);
				randNum = selectPossibleNumber(possibilities);
			}
			enter(puzzle, randNum, randI, randJ);
		}
		
	}
	
	private void enterAll(int[][] puzzle) {
		int number;
		for (int i = 0; i < gridDimension; i++) {
			for (int j = 0; j < gridDimension; j++) {
				number = puzzle[i][j];
				if (number >= SMALLEST_POSSIBLE_SQUARE_VALUE) {
					enter(puzzle, number, i, j);
					setInStone(i, j);
				}
			}
		}
	}
	
	private void enter(int[][] puzzle, int num, int i, int j) {
		clearOutPossibilitiesForSquareExceptChosen(num, i , j);
		countsOfAllNumbersOnGrid[num-1]++;
		puzzle[i][j] = num;
		setInStone(i, j);
	}

	private boolean squareValueIsValid(int value) {
		return value >= SMALLEST_POSSIBLE_SQUARE_VALUE && value <= gridDimension;
	}
	
	private boolean moreAllowed(int randNum) {
		return countsOfAllNumbersOnGrid[randNum-1] < gridDimension;
	}

	private boolean isOpen(int[][] puzzle, int randI, int randJ) {
		//bad x val
		if (randI < 0 || randI >= gridDimension) {
			return false;
		}
		//bad y val
		if (randJ < 0 || randJ >= gridDimension) {
			return false;
		}
		return puzzle[randI][randJ] <= 0;
	}

	private void markNumbersSetInStone(int[][] puzzle) {
		for (int i = 0; i < gridDimension; i++) {
			for (int j = 0; j < gridDimension; j++) {
				if (squareValueIsValid(puzzle[i][j])) {
					setInStone(i, j);
				}
			}
		}
	}

	private void setInStone(int i, int j) {
		Set<Integer> currentlySetInStone = squaresSetInStone.computeIfAbsent(i, k -> new HashSet<>());
		currentlySetInStone.add(j);
	}

	private boolean isSetInStone(int i, int j) {
		return squaresSetInStone.getOrDefault(i, new HashSet<>()).contains(j);
	}

	private boolean isOutOfBounds(int index) {
		return index < 0 || index >= gridDimension;
	}
	
	boolean holeDug() {
		return isHoleDug;
	}
}

