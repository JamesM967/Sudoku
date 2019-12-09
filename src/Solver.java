import java.util.Random;

public class Solver {

	private int myDimension;
	private int[][][] myCreatorBoard;
	private int[][] mySolution;
	private int[][] mySet;
	private int myNumAutoFilled;
	private int[] myNumUsed;
	private int[] myNotAllowed;
	private boolean isHoleDug;
	
	public Solver(int dimension) {
		myDimension = dimension;
		myNumAutoFilled = 11;
		myCreatorBoard = createAllPossibilities(dimension);
		mySolution = new int[dimension][dimension];
		mySet = new int[dimension][dimension];
		myNumUsed = new int[dimension];
		countNums();
		blankAllowed();
		randomAutoFill(myNumAutoFilled);
		fillInBlanksOfSolution();
	}
	
	public Solver(int dimension, int[][] partialSolution) {
		myDimension = dimension;
		myNumAutoFilled = 11;
		myCreatorBoard = createAllPossibilities(dimension);
		mySolution = partialSolution;
		mySet = new int[dimension][dimension];
		myNumUsed = new int[dimension];
		countNums();
		blankAllowed();
		enterAll();
		fillInBlanksOfSolution();
		isHoleDug = false;
	}

	public int[][] createValid() {
		dfsSolve(0, 0);
		return mySolution;
	}

	private void dfsSolve(int i, int j) {
		if (isOutOfBounds(i, j)) {
			isHoleDug = true;
			return;
		}
		int[] possibilities = fillPossibilities(i, j);
		possibilities = updatePossibilities(i, j, possibilities);
		int choice = selectPossibility(possibilities);
		int isSet = mySet[i][j];
		//end of the board and its a given
		if ((i == 8) && (j == 8) && (isSet == 1)) {
			return;
		}
		//this square was passed in as a given and cannot be changed
		if (isSet == 1) {
			i = iteratei(i, j);
			j = iteratej(j);
			dfsSolve(i,j);
			return;
		}
		//there is a yet untested number for this square
		if (!optionsEmpty(choice)) {
			mySolution[i][j] = choice;
			checkOffUsed(i, j, choice);
			if (i == 8 && j == 8) {
				int test = 1;
				return;
			}
			i = iteratei(i, j);
			j = iteratej(j);
			dfsSolve(i, j);
			return;
		}
		//there are no valid paths to take
		if (optionsEmpty(choice)) {
			myCreatorBoard[i][j] = createNewPossibilities();
			j = backupj(j);
			i = backupi(i, j);
			if (isOutOfBounds(i, j)) {
				isHoleDug = true;
				return;
			}
			isSet = mySet[i][j];
			while (isSet == 1) {
				j = backupj(j);
				i = backupi(i, j);
				if (isOutOfBounds(i, j)) {
					isHoleDug = true;
					return;
				}
				isSet = mySet[i][j];
			}
			int number = mySolution[i][j];
			if (mySolution[i][j] != -1) {
				checkOffUsed(i, j, number);
			}
			mySolution[i][j] = -1;
			dfsSolve(i, j);
		}
	}

	private boolean optionsEmpty(int choice) {
		return choice == -1;
	}
	
	private int[] updatePossibilities(int i, int j, int[] possibilities) {
		int number;
		for (int k = 0; k < myDimension; k++) {
			number = mySolution[i][k];
			if (number > 0) {
				possibilities[number-1] = -1;
			}
		}
		for (int k = 0; k < myDimension; k++) {
			number = mySolution[k][j];
			if (number > 0) {
				possibilities[number-1] = -1;
			}
		}
		int iblock = i/3;
		int jblock = j/3;
		int istart = iblock*3;
		int jstart = jblock*3;
		for (int l = istart; l < istart+3; l++) {
			for (int m = jstart; m < jstart+3; m++) {
				number = mySolution[l][m];
				if (number > 0) {
					possibilities[number-1] = -1;
				}
			}
 		}
		for (int k = 0; k < myDimension; k++) {
			if (myNotAllowed[k] == 1) {
				possibilities[k] = -1;
			}
		}
		return possibilities;
	}

	private void checkOffUsed(int i, int j, int choice) {
		myCreatorBoard[i][j][choice-1] = -1;		
	}
	
	private int backupi(int i, int j) {
		if (j == 8) {
			i--;
		}
		return i;
	}

	private int backupj(int j) {
		j--;
		if (j == -1) {
			j = 8;
		}
		return j;
	}

	private int iteratej(int j) {
		j++;
		if (j == myDimension) {
			j = 0;
		}
		return j;
	}

	private int iteratei(int i, int j) {
		if (j == 8) {
			i++;
		}
		return i;
	}

	private int selectPossibility(int[] possibilities) {
		int pCount = 0;
		for (int k = 0; k < myDimension; k++) {
			if (possibilities[k] != -1) {
				pCount++;
			}
		}
		int choice = -1;
		Random generator = new Random();
		if (pCount > 0) {
			int rand  = generator.nextInt(pCount);
			for (int k = 0; k<myDimension; k++) {
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
		int[] startingPossibilities = new int[myDimension];
		for (int i = 0; i<myDimension; i++) {
			startingPossibilities[i] = i+1;
		}
		return startingPossibilities;
	}
	
	private void fillInBlanksOfSolution() {
		for (int i = 0; i < myDimension; i++) {
			for (int j = 0; j < myDimension; j++) {
				if (mySolution[i][j] <= 0) {
					mySolution[i][j] = -1;
				}
			}
		}
	}
	
	private int[] fillPossibilities(int i, int j) {
		int[] possibilities = new int[9];
		int val;
		for (int k = 0; k < myDimension; k++) {
			val = myCreatorBoard[i][j][k];
			possibilities[k] = val;
		}
		return possibilities;
	}
	
	private void countNums() {
		int currentNum;
		for (int i = 0; i < myDimension; i++) {
			for (int j = 0; j < myDimension; j++) {
				currentNum = mySolution[i][j];
				if (currentNum >= 1) {
					myNumUsed[currentNum-1]++;
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
				randX = generator.nextInt(9);
				randY = generator.nextInt(9);
				int[] possibilities = fillPossibilities(randX, randY);
				possibilities = updatePossibilities(randX, randY, possibilities);
				randNum = selectPossibility(possibilities);
				//randNum = generator.nextInt(9) + 1;
			}
			enter(randNum, randX, randY);
		}
		
	}
	
	private void enterAll() {
		int number;
		for (int i = 0; i < myDimension; i++) {
			for (int j = 0; j < myDimension; j++) {
				number = mySolution[i][j]; 
				if (number > 0) {
					enter(number, i, j);
					mySet[i][j] = 1;
				}
			}
		}
	}
	
	private void enter(int num, int randX, int randY) {
		for (int i = 0; i < myDimension; i++) {
			if (i+1 != num) {
				myCreatorBoard[randX][randY][i] = -1;
			}
		}
		myNumUsed[num-1]++;
		mySolution[randX][randY] = num;
		mySet[randX][randY] = 1;
	}

	
	
	private boolean moreAllowed(int randNum) {
		return myNumUsed[randNum-1] < 9;
	}

	private boolean isOpen(int randX, int randY) {
		//bad x val
		if (randX < 0 || randX > 8) {
			return false;
		}
		//bad y val
		if (randY < 0 || randY > 8) {
			return false;
		}
		return mySolution[randX][randY] <= 0;
	}

	private int[][][] createAllPossibilities(int dimension) {
		int[][][] possibilities = new int[dimension][dimension][dimension];
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				for (int k = 0; k < dimension; k++) {
					possibilities[i][j][k] = k+1;
				}
			}
		}
		return possibilities;
	}

	public void substitute(int randX, int randY, int i) {
		blankAllowed();
		int notAllowed = mySolution[randX][randY];
		mySolution[randX][randY] = -1;
		myNotAllowed[notAllowed-1] = 1;
		setInStone();
	}
	
	private void setInStone() {
		for (int i = 0; i < myDimension; i++) {
			for (int j = 0; j < myDimension; j++) {
				if (mySolution[i][j] > 0) {
					mySet[i][j] = 1;
				}
				else {
					mySet[i][j] = -1;
				}
			}
		}
	}
	
	private void blankAllowed() {
		myNotAllowed = new int[myDimension];
		for (int i = 0; i < myDimension; i++) {
			myNotAllowed[i] = 0;
		}
	}
	
	private boolean isOutOfBounds(int i, int j) {
		if (i < 0 || i > 8) {
			return true;
		}
		//bad y val
		return j < 0 || j > 8;
	}
	
	public boolean holeDug() {
		return isHoleDug;
	}
}

