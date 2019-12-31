package view;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Square extends Rectangle {

	private Note myNotes;
	private int noteSpacing;
	private boolean isSelected;
	private boolean isUneditable;
	private GridPane myNoteGrid;
	private int correctNum;
	private int currentNum;
	
	public Square(int size, int noteDimension) {
		initialize(size, noteDimension);
	}
	
	private void initialize(int squareSize, int noteDimension) {
		myNotes = new Note(noteDimension);
		isSelected = false;
		isUneditable = false;
		createSquare(squareSize);
		initializeNoteGrid(noteDimension / 3);
	}
	
	private void initializeNoteGrid(int dimension) {
		myNoteGrid = new GridPane();
		myNoteGrid.setPrefSize(42, 42);
		myNoteGrid.setGridLinesVisible(false);
		noteSpacing = 0;
		myNoteGrid.setHgap(noteSpacing);
		myNoteGrid.setVgap(noteSpacing);
		for (int i = 0; i < dimension; i++) {
			ColumnConstraints cs = new ColumnConstraints();
		    cs.setMinWidth(14);
			cs.setMaxWidth(14);
			cs.setHalignment(HPos.CENTER);
		    RowConstraints rs = new RowConstraints();
		    rs.setMinHeight(14);
			rs.setMaxHeight(14);
			rs.setValignment(VPos.CENTER);
		    myNoteGrid.getColumnConstraints().add(cs);
		    myNoteGrid.getRowConstraints().add(rs);
		}
	}
	
	private void createSquare(int size) {
        this.setHeight(size);
        this.setWidth(size);
        this.setStroke(Color.BLACK);
        this.setFill(Color.WHITE);
    }
	
	public void select() {
		//highlights square in yellow
		this.setFill(Color.web("#ffe580"));
		isSelected = true;
		StackPane stack = (StackPane) this.getParent();
		if (stack.getChildren().size() == 2) {
			stack.getChildren().add(1, new Text());
		}
		StackPane clickStack = (StackPane) stack.getChildren().get(2);
		clickStack.requestFocus();
	}
	
	void deselect() {
		//return square color to white
		this.setFill(Color.WHITE);
		isSelected = false;
	}
	
	void editNote(String number) {
		int numval = Integer.parseInt(number);
		int row = numval / 3;
		int col = numval % 3 - 1;
		if (col < 0) {
			row--;
			col = 2;
		}
		if (myNotes.hasNote(numval)) {
			removeNote(row, col);
			myNotes.removeNumber(numval);
		}
		else {
			addNote(number, row, col);
			myNotes.addNumber(numval);
		}
	}
	
	private void addNote(String number, int row, int col) {
		Text textNumber = new Text(number);
		textNumber.setFont(Font.font("Times New Roman", 10));
		myNoteGrid.add(textNumber, col, row);
	}
	
	private void removeNote(int row, int col) {
		myNoteGrid.add(new Text(""), col, row);
	}

	boolean isSelected() {
		return isSelected;
	}
	
	void setCorrectNum(int val) {
		correctNum = val;
	}

	int getCurrentNum() {
		return currentNum;
	}

	void setCurrentNum(int val) {
		currentNum = val;
	}
	
	boolean isCorrect() {
		return currentNum == correctNum;
	}

	GridPane getNotesView() {
		return myNoteGrid;
	}
	
	void setInStone() {
		isUneditable = true;
	}
	
	boolean isSetInStone() {
		return isUneditable;
	}
}
