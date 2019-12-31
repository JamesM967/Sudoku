package solver;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Solver {

	private static final int DEFAULT_NUM_SQUARES_TO_AUTOFILL = 11;
	private static final int EMPTY_SQUARE = -1;
	private static final int SMALLEST_POSSIBLE_SQUARE_VALUE = 1;

	private final int gridDimension;
	private Map<Integer, Map<Integer, Set<Integer>>> allRemainingPossibilitiesBoard = new HashMap<>();
	private int[][] solution;
	private Map<Integer, Set<Integer>> squaresSetInStone = new HashMap<>();
	private int[] countsOfAllNumbersOnGrid;
	private Set<Integer> disqualifiedValues = new HashSet<>();
	private boolean isHoleDug;
	
	public Solver(int dimension) {
		this.gridDimension = dimension;
		solution = new int[dimension][dimension];
		countsOfAllNumbersOnGrid = new int[dimension];
		countNumbersOnGrid();
		randomAutoFill(DEFAULT_NUM_SQUARES_TO_AUTOFILL);
		fillInBlanksOfSolution();
	}
	
	public Solver(int dimension, int[][] partialSolution) {
		this.gridDimension = dimension;
		solution = partialSolution;
		countsOfAllNumbersOnGrid = new int[dimension];
		countNumbersOnGrid();
		enterAll();
		fillInBlanksOfSolution();
		isHoleDug = false;
	}

	public int[][] createValidSolution() {
		dfsSolve(0, 0);
		return solution;
	}

	private void dfsSolve(int i, int j) {
		if (isOutOfBounds(i) || isOutOfBounds(j)) {
			isHoleDug = true;
			return;
		}

		Set<Integer> nonUpdatedPossibilities = getCurrentPossibilitiesForSquare(i, j);
		Set<Integer> possibilities = updatePossibilities(i, j, nonUpdatedPossibilities);
		int chosenNumber = selectPossibleNumber(possibilities);

		//end of the board and it can't be changed
		if (atLastSquareOfGrid(i, j) && isSetInStone(i, j)) {
			return;
		}

		//this square was passed in as a given and cannot be changed
		if (isSetInStone(i, j)) {
			i = incrementIWhenJAtEnd(i, j);
			j = incrementJIndex(j);
			dfsSolve(i,j);
			return;
		}

		//there is a yet untested number for this square
		if (!optionsEmpty(chosenNumber)) {
			solution[i][j] = chosenNumber;
			removeOptionAsPossibility(i, j, chosenNumber);
			if (atLastSquareOfGrid(i, j)) {
				return;
			}
			i = incrementIWhenJAtEnd(i, j);
			j = incrementJIndex(j);
			dfsSolve(i, j);
			return;
		}

		//there are no valid paths to take
		if (optionsEmpty(chosenNumber)) {
			createNewPossibilities(i, j);
			j = backupJ(j);
			i = backupIWhenJAtEnd(i, j);
			if (isOutOfBounds(i) || isOutOfBounds(j)) {
				isHoleDug = true;
				return;
			}
			while (isSetInStone(i, j)) {
				j = backupJ(j);
				i = backupIWhenJAtEnd(i, j);
				if (isOutOfBounds(i) || isOutOfBounds(j)) {
					isHoleDug = true;
					return;
				}
			}
			int number = solution[i][j];
			if (solution[i][j] != EMPTY_SQUARE) {
				removeOptionAsPossibility(i, j, number);
			}
			solution[i][j] = EMPTY_SQUARE;
			dfsSolve(i, j);
		}
	}

	private boolean atLastSquareOfGrid(int i, int j) {
		return i == gridDimension - 1 && j == gridDimension - 1;
	}

	private boolean optionsEmpty(int choice) {
		return choice == -1;
	}
	
	private Set<Integer> updatePossibilities(int i, int j, Set<Integer> possibilities) {
		//Check all numbers in the square's row
		removePossibilitiesForRow(i, possibilities);

		//Check all numbers in the square's column
		removePossibilitiesForColumn(j, possibilities);

		//Check all numbers in the square's block
		removePossibilitiesForBlock(i, j, possibilities);

		//Remove any disqualified values
		for (int k = 0; k < gridDimension; k++) {
			if (disqualifiedValues.contains(k)) {
				possibilities.remove(k);
			}
		}

		return possibilities;
	}

	private void removePossibilitiesForRow(int i, Set<Integer> possibilities) {
		for (int j = 0; j < gridDimension; j++) {
			possibilities.remove(solution[i][j]);
		}
	}

	private void removePossibilitiesForColumn(int j, Set<Integer> possibilities) {
		for (int i = 0; i < gridDimension; i++) {
			possibilities.remove(solution[i][j]);
		}
	}

	private void removePossibilitiesForBlock(int i, int j, Set<Integer> possibilities) {
		int iblock = i / 3;
		int jblock = j / 3;
		int istart = iblock * 3;
		int jstart = jblock * 3;
		for (int l = istart; l < istart + 3; l++) {
			for (int m = jstart; m < jstart + 3; m++) {
				possibilities.remove(solution[l][m]);
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

	private void fillInBlanksOfSolution() {
		for (int i = 0; i < gridDimension; i++) {
			for (int j = 0; j < gridDimension; j++) {
				if (solution[i][j] < SMALLEST_POSSIBLE_SQUARE_VALUE) {
					solution[i][j] = EMPTY_SQUARE;
				}
			}
		}
	}
	
	private void countNumbersOnGrid() {
		int currentNum;
		for (int i = 0; i < gridDimension; i++) {
			for (int j = 0; j < gridDimension; j++) {
				currentNum = solution[i][j];
				if (squareValueIsValid(currentNum)) {
					countsOfAllNumbersOnGrid[currentNum - 1]++;
				}
			}
		}
	}

	private void randomAutoFill(int numAutoFilled) {
		Random generator = new Random();
		for (int i = 0; i < numAutoFilled; i++) {
			int randNum = -1;
			int randI = -1;
			int randJ = -1;
			while (randNum < 1 || !moreAllowed(randNum) || !isOpen(randI, randJ)) {
				randI = generator.nextInt(gridDimension);
				randJ = generator.nextInt(gridDimension);
				Set<Integer> currentPossibilities = getCurrentPossibilitiesForSquare(randI, randJ);
				Set<Integer> possibilities = updatePossibilities(randI, randJ, currentPossibilities);
				randNum = selectPossibleNumber(possibilities);
			}
			enter(randNum, randI, randJ);
		}
		
	}
	
	private void enterAll() {
		int number;
		for (int i = 0; i < gridDimension; i++) {
			for (int j = 0; j < gridDimension; j++) {
				number = solution[i][j];
				if (number >= SMALLEST_POSSIBLE_SQUARE_VALUE) {
					enter(number, i, j);
					setInStone(i, j);
				}
			}
		}
	}
	
	private void enter(int num, int i, int j) {
		clearOutPossibilitiesForSquareExceptChosen(num, i , j);
		countsOfAllNumbersOnGrid[num-1]++;
		solution[i][j] = num;
		setInStone(i, j);
	}

	private boolean squareValueIsValid(int value) {
		return value >= SMALLEST_POSSIBLE_SQUARE_VALUE && value <= gridDimension;
	}
	
	private boolean moreAllowed(int randNum) {
		return countsOfAllNumbersOnGrid[randNum-1] < gridDimension;
	}

	private boolean isOpen(int randI, int randJ) {
		//bad x val
		if (randI < 0 || randI >= gridDimension) {
			return false;
		}
		//bad y val
		if (randJ < 0 || randJ >= gridDimension) {
			return false;
		}
		return solution[randI][randJ] <= 0;
	}

	void substitute(int randI, int randJ) {
		disqualifiedValues.clear();
		int disqualifiedValue = solution[randI][randJ];
		solution[randI][randJ] = EMPTY_SQUARE;
		disqualifiedValues.add(disqualifiedValue);
		markNumbersSetInStone();
	}
	
	private void markNumbersSetInStone() {
		for (int i = 0; i < gridDimension; i++) {
			for (int j = 0; j < gridDimension; j++) {
				if (squareValueIsValid(solution[i][j])) {
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

