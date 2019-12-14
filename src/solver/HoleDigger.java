package solver;

import solver.Solver;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HoleDigger {

	private int myNums;
	private int myNumsFinal;
	private int myDimension;
	private int[][] myPuzzle;
	private int[][][] myRowColCounts;
	private List<int[]> myRandomCells;
	
	public HoleDigger(int dimension, int difficulty) {
		myNums = 81;
		myNumsFinal = setDifficulty(difficulty);
		myDimension = dimension;
		myRowColCounts = createCountsArray();
	}
	
	public int[][] dig(int[][] solution) {
		myPuzzle = new int[myDimension][myDimension];
		for (int i = 0; i < myDimension; i++) {
			for (int j = 0; j < myDimension; j++) {
				myPuzzle[i][j] = solution[i][j];
			}
		}
		randomDelete();
		return myPuzzle;
	}

	private void randomDelete() {
		myRandomCells = new ArrayList<int[]>();
		addCellsToList(myRandomCells);
		int size = myDimension*myDimension;
		int[] rand;
		int randX, randY;
		for (int i = 0; i < myDimension*myDimension; i++) {
			rand = pickRandomCell(size);
			randX = rand[0];
			randY = rand[1];
			delete(randX, randY);
			size--;
		}	
	}

	private int[] pickRandomCell(int limit) {
		Random generator = new Random();
		int rand = generator.nextInt(limit);
		int[] cell = myRandomCells.get(rand);
		myRandomCells.remove(rand);
		return cell;
	}
	
	private void delete(int rowCoor, int colCoor) {
		if (!isDeletable(rowCoor, colCoor)) {
			return;
		}
		myPuzzle[rowCoor][colCoor] = -1;
		updateCounts(rowCoor, colCoor);
		myNums--;
	}

	private void updateCounts(int rowCoor, int colCoor) {
		updateRowCounts(rowCoor);
		updateColCounts(colCoor);
		updateBlockCounts(rowCoor, colCoor);
	}

	private void updateRowCounts(int rowCoor) {
		for (int k = 0; k < myDimension; k++) {
			myRowColCounts[rowCoor][k][0]--;
		}	
	}
	
	private void updateColCounts(int colCoor) {
		for (int k = 0; k < myDimension; k++) {
			myRowColCounts[k][colCoor][1]--;
		}		
	}
	
	private void updateBlockCounts(int rowCoor, int colCoor) {
		int rowblock = rowCoor/3;
		int colblock = colCoor/3;
		int rowstart = rowblock*3;
		int colstart = colblock*3;
		for (int l = rowstart; l < rowstart+3; l++) {
			for (int m = colstart; m < colstart+3; m++) {
				myRowColCounts[l][m][2]--;
			}
 		}
	}

	private boolean isOpen(int randX, int randY) {
		return !isOutOfBounds(randX, randY) && myPuzzle[randX][randY] == -1;
	}
	
	private boolean isOutOfBounds(int xCoor, int yCoor) {
		return xCoor < 0 || xCoor > 8 || yCoor < 0 || yCoor > 8;
	}
	
	private boolean isDeletable(int randX, int randY) {
		return leavesSufficientInfo(randX, randY) && (myNums > myNumsFinal) && keepsUniqueness(randX, randY);
	}
	
	private boolean keepsUniqueness(int randX, int randY) {
		Solver solver;
		int[][] tester, result;
		tester = myPuzzle.clone();
		solver = new Solver(myDimension, tester);
		solver.substitute(randX, randY, 0);
		result = solver.createValid();
		if (!solver.holeDug()) {
			myPuzzle = tester;
			return false;
		}
		myPuzzle = result;
		return true;
	}

	private boolean leavesSufficientInfo(int randX, int randY) {
		return enoughInRow(randX, randY) || enoughInCol(randX, randY) || enoughInBlock(randX, randY);
	}
	
	private int[][][] createCountsArray() {
		int[][][] counts = new int[myDimension][myDimension][3];
		for (int i = 0; i < myDimension; i++) {
			for (int j = 0; j < myDimension; j++) {
				counts[i][j][0] = 9;
				counts[i][j][1] = 9;
				counts[i][j][2] = 9;
			}
		}
		return counts;
	}
	
	private boolean enoughInRow(int i, int j) {
		return myRowColCounts[i][j][0] > 0;
	}
	
	private boolean enoughInCol(int i, int j) {
		return myRowColCounts[i][j][1] > 0;
	}
	
	private boolean enoughInBlock(int i, int j) {
		return myRowColCounts[i][j][2] > 0;
	}
	
	private void addCellsToList(List<int[]> myRandomCells2) {
		for (int i = 0; i < myDimension; i++) {
			for (int j = 0; j < myDimension; j++) {
				int[] cellTemp = new int[2];
				cellTemp[0] = i;
				cellTemp[1] = j;
				myRandomCells2.add(cellTemp);
			}
		}
	}
	
	private int setDifficulty(int difficulty) {
		int numsLeft;
		if (difficulty == 0) {
			numsLeft = 60;
		}
		else if (difficulty == 1) {
			numsLeft = 50;
		}
		else {
			numsLeft = 40;
		}
		return numsLeft;
	}
}
