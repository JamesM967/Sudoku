import java.util.ArrayList;
import java.util.Random;

import javafx.event.EventHandler;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;


public class Grid {

	private int myDimension;
	private Square[][] mySquares;
	private ArrayList<Square> mySelected;
	private int[][] mySolution;
	private int[][] myPuzzle;
	private int[][] mySet;
	
	public Grid(int dimension, int difficulty) {
		myDimension = dimension;
		mySquares = new Square[dimension][dimension];
		mySelected = new ArrayList<Square>(); 
		mySolution = generateCorrectGrid();
		myPuzzle = generatePuzzle(difficulty);
		int test = 1;
	}
	
	private int[][] generateCorrectGrid() {
		Solver solver = new Solver(myDimension);
		return solver.createValid();
	}
	
	public void writeInStartingNumbers() {
		for (int i = 0; i < myDimension; i++) {
			for (int j = 0; j < myDimension; j++) {
				Square square = mySquares[i][j];
				square.setCorrectNum(mySolution[i][j]);
				square.setCurrentNum(myPuzzle[i][j]);
				StackPane stack = (StackPane) square.getParent();
				int number = myPuzzle[i][j];
				String strNumber = Integer.toString(number);
				Text textNumber = new Text(strNumber);
				textNumber.setFont(Font.font("Times New Roman", 22));
				if (square.getIsSetInStone()) {
					textNumber.setFont(Font.font("Times New Roman", FontWeight.BOLD, 22));
					stack.getChildren().add(textNumber);
				}
			}
		}
	}
	
	private int[][] generatePuzzle(int difficulty) {
		int[][] puzzle = new int[myDimension][myDimension];
		HoleDigger digger = new HoleDigger(myDimension, difficulty);
		puzzle = digger.dig(mySolution);
		return puzzle;
	}
	
	public void add(int bigRow, int bigCol, int smallRow, int smallCol, Square square) {
		mySquares[myDimension/3*bigRow + smallRow][myDimension/3*bigCol + smallCol] = square;
	}
	
	private void deselectAll() {
		for (Square sq : mySelected) {
			sq.deselect();
		}
		mySelected.clear();
	}
	
	private void selectNew(Square square) {
		mySelected.add(square);
		for (int i = 0; i < myDimension; i++) {
			for (int j = 0; j < myDimension; j++) {
				Square next = mySquares[i][j];
				if (square.getValue() == next.getValue() && next.getValue() != 0) {
					mySelected.add(next);
					next.select();
				}
			}
		}
	}
	
	public void makeSquareClickable(Square square) {
		StackPane stack = (StackPane) square.getParent();
		StackPane clickStack = (StackPane) stack.getChildren().get(2);
		clickStack.setOnMouseClicked(new EventHandler<MouseEvent>()
		        {
		            @Override
		            public void handle(MouseEvent t) {
		                if (!square.isSelected()) {
		                	square.select();
		                	deselectAll();
		                	selectNew(square);
		                }
		                else 
		                	square.deselect();
		            }
		        });
		clickStack.setOnKeyPressed(new EventHandler<KeyEvent>() 
			{
				public void handle(KeyEvent ke) {
					int stacksize = stack.getChildren().size();
					String number = ke.getText();
					if (stacksize > 2) {
						stack.getChildren().remove(1);
					}
					try {
						int numval = Integer.parseInt(number);
						Text textNumber = new Text(number);
						textNumber.setFont(Font.font("Times New Roman", 22));
						if (!square.getIsSetInStone() && (!number.equals("0"))) {
							Square picked = (Square) stack.getChildren().get(0);
							picked.setCurrentNum(numval);
							stack.getChildren().add(1, textNumber);
							if (checkForCorrectness()) {
								System.out.println("WINNER!");
							}
						}
					} catch (NumberFormatException e) {
						int test = 1;
					}
				}
			});
	}
	
	private boolean checkForCorrectness() {
		for (int i = 0; i < myDimension; i++) {
			for (int j = 0; j < myDimension; j++) {
				Square square = mySquares[i][j];
				if (!square.isCorrect()) {
					return false;
				}
			}
		}
		return true;
	}
	
	public void addHoverEffect(Square square) {
		InnerShadow is = new InnerShadow();
		StackPane stack = (StackPane) square.getParent();
		StackPane clickStack = (StackPane) stack.getChildren().get(2);
		clickStack.addEventHandler(MouseEvent.MOUSE_ENTERED, 
		    new EventHandler<MouseEvent>() {
		        @Override public void handle(MouseEvent e) {
		            square.setEffect(is);
		        }
		});
		//Removing the shadow when the mouse cursor is off
		clickStack.addEventHandler(MouseEvent.MOUSE_EXITED, 
		    new EventHandler<MouseEvent>() {
		        @Override public void handle(MouseEvent e) {
		            square.setEffect(null);
		        }
		});
	}
	
	public void findSet() {
		for (int i = 0; i < myDimension; i++) {
			for (int j = 0; j < myDimension; j++) {
				if (myPuzzle[i][j] != -1) {
					Square square = mySquares[i][j];
					square.setInStone();
				}
			}
		}
	}
	
}
