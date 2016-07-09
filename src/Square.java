import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class Square extends Rectangle {
	
	private int[] myNotes;
	private int myValue;
	private int mySize;
	private int noteSpacing;
	private boolean isSelected;
	private boolean isSetInStone;
	private GridPane myHiddenGrid;
	private StackPane myNumberPane;
	private Rectangle myImage;
	private int myCorrectNum;
	private int myCurrentNum;
	
	public Square(int size) {
		initialize(size);
		myValue = 0;
	}
	
	public Square(int size, int val) {
		initialize(size);
		myValue = val;
	}
	
	private void initialize(int squareSize) {
		myNotes = new int[9];
		mySize = squareSize;
		isSelected = false;
		isSetInStone = false;
		createSquare(squareSize);
		initializeNoteGrid();
		initializeNumberPane();
	}
	
	private void initializeNoteGrid() {
		myHiddenGrid = new GridPane();
		myHiddenGrid.setGridLinesVisible(false);
		noteSpacing = mySize/3;
		myHiddenGrid.setHgap(noteSpacing);
		myHiddenGrid.setVgap(noteSpacing);
	}
	
	private void initializeNumberPane() {
		myNumberPane = new StackPane();
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
	
	public GridPane getNotesGrid() {
		return myHiddenGrid;
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
	
	public void setInStone() {
		isSetInStone = true;
	}
	
	public boolean getIsSetInStone() {
		return isSetInStone;
	}
}
