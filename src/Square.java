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
	private int myValue;
	private int mySize;
	private int noteSpacing;
	private boolean isSelected;
	private boolean isSetInStone;
	private GridPane myNoteGrid;
	private Rectangle myImage;
	private int myCorrectNum;
	private int myCurrentNum;
	
	public Square(int size, int noteDimension) {
		initialize(size, noteDimension);
	}
	
	private void initialize(int squareSize, int noteDimension) {
		myNotes = new Note(noteDimension);
		mySize = squareSize;
		isSelected = false;
		isSetInStone = false;
		createSquare(squareSize);
		initializeNoteGrid(noteDimension/3);
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
	
	public void setValue(int val) {
		myValue = val;
	}
	
	private void createSquare(int size) {
        this.setHeight(size);
        this.setWidth(size);
        this.setStroke(Color.BLACK);
        this.setFill(Color.WHITE);
    }
	
	public void select() {
		this.setFill(Color.web("#ffe580")); //light yellowish color
		isSelected = true;
		StackPane stack = (StackPane) this.getParent();
		if (stack.getChildren().size() == 2) {
			stack.getChildren().add(1, new Text());
		}
		StackPane clickStack = (StackPane) stack.getChildren().get(2);
		clickStack.requestFocus();
	}
	
	public void deselect() {
		this.setFill(Color.WHITE); //change color back to white
		isSelected = false;
	}
	
	public void editNote(String number) {
		int numval = Integer.parseInt(number);
		int row = numval/3;
		int col = numval%3-1;
		if (col < 0) {
			row--;
			col = 2;
		}
		if (myNotes.hasNote(numval)) {
			removeNote(number, row, col);
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
	
	private void removeNote(String number, int row, int col) {
		myNoteGrid.add(new Text(""), col, row);
	}
	
	public Rectangle getImage() {
		return myImage;
	}
	
	public boolean isSelected() {
		return isSelected;
	}
	
	public void setCorrectNum(int val) {
		myCorrectNum = val;
	}
	
	public void setCurrentNum(int val) {
		myCurrentNum = val;
	}
	
	public boolean isCorrect() {
		if (myCurrentNum == myCorrectNum) {
			return true;
		}
		return false;
	}
	
	public int getValue() {
		return myValue;
	}
	
	public GridPane getNotesView() {
		return myNoteGrid;
	}
	
	public void setInStone() {
		isSetInStone = true;
	}
	
	public boolean getIsSetInStone() {
		return isSetInStone;
	}
}
