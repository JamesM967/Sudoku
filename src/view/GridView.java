package view;

import config.Difficulty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;


public class GridView {

	private static final int DEFAULT_SUB_GRID_DIMENSION = 3;
	private static final int DEFAULT_POWER_TO_RAISE_SUB_GRID_DIM = 2;
	private static final String NOTE_MODE_TEXT = "Pencil";
	private static final String NOT_NOTE_MODE_TEXT = "Pen";

	private int myHeight;
	private int myWidth;
	private int subGridDimension;
	private Scene myScene;
	private Group myGroup;
	private BorderPane myView;
	private TilePane myTilepane;
	private Grid myGrid;
	
	public GridView(int height, int width, Difficulty difficulty) {
		myHeight = height;
		myWidth = width;
		initialize(difficulty);
	}

    /**
     * initializes the necessary components to create a TabPane with at least one instance of UI
     */
    private void initialize (Difficulty difficulty) {
        myView = new BorderPane();
        VBox titleBox = makeTitle();
        myView.setTop(titleBox);
        subGridDimension = DEFAULT_SUB_GRID_DIMENSION;
        myGrid = new Grid((int) Math.pow(subGridDimension, DEFAULT_POWER_TO_RAISE_SUB_GRID_DIM), difficulty);
        myTilepane = createGrid(subGridDimension);
        myGroup = new Group(myTilepane);
        myView.setCenter(myGroup);
        myScene = new Scene(myView, myWidth, myHeight);
    }

    private VBox makeTitle() {
		VBox titleBox = new VBox(30);
		Text title = new Text("SUDOKU");
		title.setFont(Font.font("Times New Roman", 50));
		titleBox.getChildren().add(title);
		Button writingButton = new Button(NOTE_MODE_TEXT);
		writingButton.setFont(Font.font("Times New Roman", 22));
		writingButton.setOnAction(new EventHandler<ActionEvent>() {
		@Override public void handle(ActionEvent e) {
		    switchWritingButtonName(writingButton);
			myGrid.flipNoteMode();
		    }
		});	
		titleBox.getChildren().add(writingButton);
		titleBox.setAlignment(Pos.CENTER);
		return titleBox;
	}

    private TilePane createGrid(int dim) {
		TilePane bigGrid = new TilePane();
		bigGrid.setStyle("-fx-background-color: #000000");
		bigGrid.setPrefRows(dim);
	    bigGrid.setPrefColumns(dim);
		bigGrid.setHgap(1);
		bigGrid.setVgap(1);
	    for (int i = 0; i < dim; i++) {
			for (int j = 0; j < dim; j++) {
				TilePane miniGrid = new TilePane();
				miniGrid.setPrefRows(dim);
			    miniGrid.setPrefColumns(dim);
			    miniGrid.setPrefTileWidth(42);
			    miniGrid.setPrefTileHeight(42);
				for (int m = 0; m < dim; m++) {
					for (int n = 0; n < dim; n++) {
						Square square = new Square(42, (int) Math.pow(subGridDimension, DEFAULT_POWER_TO_RAISE_SUB_GRID_DIM));
						StackPane bottomStack = new StackPane();
						StackPane clickStack = new StackPane();
						clickStack.setOpacity(0);
						miniGrid.getChildren().add(bottomStack);
						bottomStack.getChildren().add(0, square);
						bottomStack.getChildren().add(1, new Text());
						bottomStack.getChildren().add(2, clickStack);
						myGrid.add(i, j, m, n, square);
						myGrid.makeSquareClickable(square);
						myGrid.addHoverEffect(square);
					}
				}
				bigGrid.getChildren().add(miniGrid);
			}
		}
	    myGrid.findSet();
		myGrid.writeInStartingNumbers();
	    return bigGrid;
	}
    
    private void switchWritingButtonName(Button writingButton) {
    	String currentText = writingButton.getText();
    	if (currentText.equals(NOT_NOTE_MODE_TEXT)) {
	    	writingButton.setText(NOTE_MODE_TEXT);
	    	writingButton.setFont(Font.font("Times New Roman", 22));
	    }
	    else if (currentText.equals(NOTE_MODE_TEXT)) {
	    	writingButton.setText(NOT_NOTE_MODE_TEXT);
	    	writingButton.setFont(Font.font("Times New Roman", 22));
	    }
    }
    
	Scene getScene() {
    	return myScene;
    }
}
