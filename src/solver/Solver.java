package solver;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Solver {

	private static final int DEFAULT_NUM_SQUARES_TO_AUTOFILL = 11;
	private static final int EMPTY_SQUARE = -1;
	private static final int SMALLEST_POSSIBLE_SQUARE_VALUE = 1;

	private final int gridDimension;
	private int[][][] allRemainingPossibilitiesBoard;
	private int[][] solution;
	private boolean[][] squaresSetInStone;
	private int[] myNumUsed;
	private Set<Integer> disqualifiedValues = new HashSet<>();
	private boolean isHoleDug;
	
	public Solver(int dimension) {
		this.gridDimension = dimension;
		allRemainingPossibilitiesBoard = createAllPossibilities(dimension);
		solution = new int[dimension][dimension];
		squaresSetInStone = new boolean[dimension][dimension];
		myNumUsed = new int[dimension];
		countNums();
		randomAutoFill(DEFAULT_NUM_SQUARES_TO_AUTOFILL);
		fillInBlanksOfSolution();
	}
	
	public Solver(int dimension, int[][] partialSolution) {
		this.gridDimension = dimension;
		allRemainingPossibilitiesBoard = createAllPossibilities(dimension);
		solution = partialSolution;
		squaresSetInStone = new boolean[dimension][dimension];
		myNumUsed = new int[dimension];
		countNums();
		enterAll();
		fillInBlanksOfSolution();
		isHoleDug = false;
	}

	public int[][] createValid() {
		dfsSolve(0, 0);
		return solution;
	}

	private void dfsSolve(int i, int j) {
		if (isOutOfBounds(i) || isOutOfBounds(j)) {
			isHoleDug = true;
			return;
		}
		int[] possibilities = fillPossibilities(i, j);
		possibilities = updatePossibilities(i, j, possibilities);
		int choice = selectPossibility(possibilities);
		boolean gridIsSetForCurrentPosition = squaresSetInStone[i][j];
		//end of the board and its a given
		if (i == gridDimension - 1 && j == gridDimension - 1 && gridIsSetForCurrentPosition) {
			return;
		}
		//this square was passed in as a given and cannot be changed
		if (gridIsSetForCurrentPosition) {
			i = incrementIWhenJAtEnd(i, j);
			j = incrementJIndex(j);
			dfsSolve(i,j);
			return;
		}
		//there is a yet untested number for this square
		if (!optionsEmpty(choice)) {
			solution[i][j] = choice;
			checkOffUsed(i, j, choice);
			if (i == gridDimension - 1 && j == gridDimension - 1) {
				return;
			}
			i = incrementIWhenJAtEnd(i, j);
			j = incrementJIndex(j);
			dfsSolve(i, j);
			return;
		}
		//there are no valid paths to take
		if (optionsEmpty(choice)) {
			allRemainingPossibilitiesBoard[i][j] = createNewPossibilities();
			j = backupJ(j);
			i = backupIWhenJAtEnd(i, j);
			if (isOutOfBounds(i) || isOutOfBounds(j)) {
				isHoleDug = true;
				return;
			}
			gridIsSetForCurrentPosition = squaresSetInStone[i][j];
			while (gridIsSetForCurrentPosition) {
				j = backupJ(j);
				i = backupIWhenJAtEnd(i, j);
				if (isOutOfBounds(i) || isOutOfBounds(j)) {
					isHoleDug = true;
					return;
				}
				gridIsSetForCurrentPosition = squaresSetInStone[i][j];
			}
			int number = solution[i][j];
			if (solution[i][j] != EMPTY_SQUARE) {
				checkOffUsed(i, j, number);
			}
			solution[i][j] = EMPTY_SQUARE;
			dfsSolve(i, j);
		}
	}

	private boolean optionsEmpty(int choice) {
		return choice == -1;
	}
	
	private int[] updatePossibilities(int i, int j, int[] possibilities) {
		int number;
		for (int k = 0; k < gridDimension; k++) {
			number = solution[i][k];
			if (number > 0) {
				possibilities[number - 1] = -1;
			}
		}
		for (int k = 0; k < gridDimension; k++) {
			number = solution[k][j];
			if (number > 0) {
				possibilities[number - 1] = -1;
			}
		}
		int iblock = i / 3;
		int jblock = j / 3;
		int istart = iblock * 3;
		int jstart = jblock * 3;
		for (int l = istart; l < istart + 3; l++) {
			for (int m = jstart; m < jstart + 3; m++) {
				number = solution[l][m];
				if (number > 0) {
					possibilities[number - 1] = -1;
				}
			}
 		}
		for (int k = 0; k < gridDimension; k++) {
			if (disqualifiedValues.contains(k)) {
				possibilities[k] = -1;
			}
		}
		return possibilities;
	}

	private void checkOffUsed(int i, int j, int choice) {
		allRemainingPossibilitiesBoard[i][j][choice-1] = -1;
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

	private int selectPossibility(int[] possibilities) {
		int pCount = 0;
		for (int k = 0; k < gridDimension; k++) {
			if (possibilities[k] != -1) {
				pCount++;
			}
		}
		int choice = -1;
		Random generator = new Random();
		if (pCount > 0) {
			int rand  = generator.nextInt(pCount);
			for (int k = 0; k< gridDimension; k++) {
				if (possibilities[k] != -1 && rand == 0) {
					choice = possibilities[k];
					break;
				}
				if (possibilities[k] != -1) {
					rand--;
				}
			}
		}
		return choice;
	}
	
	private int[] createNewPossibilities() {
		int[] startingPossibilities = new int[gridDimension];
		for (int i = 0; i < gridDimension; i++) {
			startingPossibilities[i] = i + 1;
		}
		return startingPossibilities;
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
	
	private int[] fillPossibilities(int i, int j) {
		int[] possibilities = new int[gridDimension];
		int val;
		for (int k = 0; k < gridDimension; k++) {
			val = allRemainingPossibilitiesBoard[i][j][k];
			possibilities[k] = val;
		}
		return possibilities;
	}
	
	private void countNums() {
		int currentNum;
		for (int i = 0; i < gridDimension; i++) {
			for (int j = 0; j < gridDimension; j++) {
				currentNum = solution[i][j];
				if (currentNum >= 1) {
					myNumUsed[currentNum - 1]++;
				}
			}
		}
	}

	private void randomAutoFill(int numAutoFilled) {
		Random generator = new Random();
		for (int i = 0; i < numAutoFilled; i++) {
			int randNum = -1;
			int randX = -1;
			int randY = -1;
			while (randNum < 1 || !moreAllowed(randNum) || !isOpen(randX, randY)) {
				randX = generator.nextInt(gridDimension);
				randY = generator.nextInt(gridDimension);
				int[] possibilities = fillPossibilities(randX, randY);
				possibilities = updatePossibilities(randX, randY, possibilities);
				randNum = selectPossibility(possibilities);
			}
			enter(randNum, randX, randY);
		}
		
	}
	
	private void enterAll() {
		int number;
		for (int i = 0; i < gridDimension; i++) {
			for (int j = 0; j < gridDimension; j++) {
				number = solution[i][j];
				if (number >= SMALLEST_POSSIBLE_SQUARE_VALUE) {
					enter(number, i, j);
					squaresSetInStone[i][j] = true;
				}
			}
		}
	}
	
	private void enter(int num, int randX, int randY) {
		for (int i = 0; i < gridDimension; i++) {
			if (i + 1 != num) {
				allRemainingPossibilitiesBoard[randX][randY][i] = -1;
			}
		}
		myNumUsed[num-1]++;
		solution[randX][randY] = num;
		squaresSetInStone[randX][randY] = true;
	}

	
	
	private boolean moreAllowed(int randNum) {
		return myNumUsed[randNum-1] < gridDimension;
	}

	private boolean isOpen(int randX, int randY) {
		//bad x val
		if (randX < 0 || randX >= gridDimension) {
			return false;
		}
		//bad y val
		if (randY < 0 || randY >= gridDimension) {
			return false;
		}
		return solution[randX][randY] <= 0;
	}

	private int[][][] createAllPossibilities(int dimension) {
		int[][][] possibilities = new int[dimension][dimension][dimension];
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				for (int k = 1; k <= dimension; k++) {
					possibilities[i][j][k] = k;
				}
			}
		}
		return possibilities;
	}

	void substitute(int randX, int randY) {
		disqualifiedValues.clear();
		int disqualifiedValue = solution[randX][randY];
		solution[randX][randY] = EMPTY_SQUARE;
		disqualifiedValues.add(disqualifiedValue);
		setInStone();
	}
	
	private void setInStone() {
		for (int i = 0; i < gridDimension; i++) {
			for (int j = 0; j < gridDimension; j++) {
				squaresSetInStone[i][j] = solution[i][j] > 0;
			}
		}
	}
	
	private boolean isOutOfBounds(int index) {
		return index < 0 || index >= gridDimension;
	}
	
	boolean holeDug() {
		return isHoleDug;
	}
}

