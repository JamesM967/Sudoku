package view;

import java.util.ArrayList;

import config.Difficulty;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import solver.HoleDigger;
import solver.Solver;


public class Grid {

	public static final int EMPTY_SQUARE = -1;

	private final int gridDimension;
	private Square[][] squares;
	private ArrayList<Square> selectedSquares;
	private int[][] solution;
	private int[][] puzzle;
	private boolean inNoteMode;
	
	Grid(int dimension, Difficulty difficulty) {
		gridDimension = dimension;
		inNoteMode = false;
		squares = new Square[dimension][dimension];
		selectedSquares = new ArrayList<>();
		solution = generateCorrectGrid();
		puzzle = generatePuzzle(difficulty);
	}
	
	private int[][] generateCorrectGrid() {
		Solver solver = new Solver(gridDimension);
		return solver.createValidSolution();
	}
	
	void writeInStartingNumbers() {
		for (int i = 0; i < gridDimension; i++) {
			for (int j = 0; j < gridDimension; j++) {
				Square square = squares[i][j];
				square.setCorrectNum(solution[i][j]);
				square.setCurrentNum(puzzle[i][j]);
				StackPane stack = (StackPane) square.getParent();
				int number = puzzle[i][j];
				String strNumber = Integer.toString(number);
				Text textNumber = new Text(strNumber);
				textNumber.setFont(Font.font("Times New Roman", 22));
				if (square.isSetInStone()) {
					textNumber.setFont(Font.font("Times New Roman", FontWeight.BOLD, 22));
					stack.getChildren().add(textNumber);
				}
			}
		}
	}
	
	private int[][] generatePuzzle(Difficulty difficulty) {
		HoleDigger digger = new HoleDigger(gridDimension, difficulty.getNumSquaresLeftOpen());
		return digger.digHolesInSolution(solution);
	}
	
	void add(int bigRow, int bigCol, int smallRow, int smallCol, Square square) {
		squares[gridDimension / 3 * bigRow + smallRow][gridDimension / 3 * bigCol + smallCol] = square;
	}
	
	private void deselectAll() {
		for (Square sq : selectedSquares) {
			sq.deselect();
		}
		selectedSquares.clear();
	}
	
	private void selectNew(Square square) {
		selectedSquares.add(square);
		for (int i = 0; i < gridDimension; i++) {
			for (int j = 0; j < gridDimension; j++) {
				Square next = squares[i][j];
				if (square.getCurrentNum() == next.getCurrentNum() && next.getCurrentNum() != 0) {
					selectedSquares.add(next);
					next.select();
				}
			}
		}
	}
	
	void makeSquareClickable(Square square) {
		StackPane stack = (StackPane) square.getParent();
		StackPane clickStack = (StackPane) stack.getChildren().get(2);
		clickStack.setOnMouseClicked(e ->
		{
			if (!square.isSelected() && !square.isSetInStone()) {
				square.select();
				deselectAll();
				selectNew(square);
			}
			else {
				square.deselect();
			}
		});
		clickStack.setOnKeyPressed(ke -> {
			int stacksize = stack.getChildren().size();
			String number = ke.getText();
			if (stacksize > 2) {
				stack.getChildren().remove(1);
			}
			try {
				int numval = Integer.parseInt(number);
				Text textNumber = new Text(number);
				textNumber.setFont(Font.font("Times New Roman", 22));
				if (!square.isSetInStone() && (!number.equals("0"))) {
					Square picked = (Square) stack.getChildren().get(0);
					picked.setCurrentNum(numval);
					if (!inNoteMode) {
						stack.getChildren().add(1, textNumber);
					} else {
						square.editNote(number);
						stack.getChildren().add(1, square.getNotesView());
					}
					if (checkForCorrectness()) {
						System.out.println("WINNER!");
					}
				}
			} catch (NumberFormatException e) {
				throw e;
			}
		});
	}
	
	private boolean checkForCorrectness() {
		for (int i = 0; i < gridDimension; i++) {
			for (int j = 0; j < gridDimension; j++) {
				Square square = squares[i][j];
				if (!square.isCorrect()) {
					return false;
				}
			}
		}
		return true;
	}
	
	void flipNoteMode() {
		inNoteMode = !inNoteMode;
	}
	
	void addHoverEffect(Square square) {
		InnerShadow is = new InnerShadow();
		StackPane stack = (StackPane) square.getParent();
		StackPane clickStack = (StackPane) stack.getChildren().get(2);

		//Hover effect added to square
		clickStack.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> square.setEffect(is));

		//Removing the shadow when the mouse cursor is off
		clickStack.addEventHandler(MouseEvent.MOUSE_EXITED, e -> square.setEffect(null));
	}
	
	void findSet() {
		for (int i = 0; i < gridDimension; i++) {
			for (int j = 0; j < gridDimension; j++) {
				if (puzzle[i][j] != EMPTY_SQUARE) {
					Square square = squares[i][j];
					square.setInStone();
				}
			}
		}
	}
	
}
