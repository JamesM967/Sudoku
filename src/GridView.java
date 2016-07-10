import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;


public class GridView {
	
	private int myHeight;
	private int myWidth;
	private int myGridDimension;
	private Scene myScene;
	private Group myGroup;
	private BorderPane myView;
	private TilePane myTilepane;
	private Grid myGrid;
	
	/**
	 * Default setup
	 */
	public GridView(int difficulty) {
		myHeight = 420;
		myWidth = 420;
		initialize(difficulty);
	}
	
	public GridView(int height, int width, int difficulty) {
		myHeight = height;
		myWidth = width;
		initialize(difficulty);
	}

	public GridView(int height, int width, Scene scene, int difficulty) {
		myHeight = height;
		myWidth = width;
		initialize(scene, difficulty);
	}
	
    /**
     * initializes the necessary components to create a TabPane with at least one instance of UI
     */
    private void initialize (int difficulty) {
        myView = new BorderPane();
        VBox titleBox = makeTitle();
        myView.setTop(titleBox);
        myGridDimension = 3;
        myGrid = new Grid(myGridDimension*myGridDimension, difficulty);
        myTilepane = createGrid(myGridDimension);
        myGroup = new Group(myTilepane);
        myView.setCenter(myGroup);
        myScene = new Scene(myView, myWidth, myHeight);
    }

    private VBox makeTitle() {
		VBox titleBox = new VBox(30);
		Text title = new Text("SUDOKU");
		title.setFont(Font.font("Times New Roman", 50));
		titleBox.getChildren().add(title);
		Button writingButton = new Button("Pencil");
		writingButton.setFont(Font.font("Times New Roman", 22));
		writingButton.setOnAction(new EventHandler<ActionEvent>() {
		@Override public void handle(ActionEvent e) {
		    String currentText = writingButton.getText();
		    switchWritingButtonName(writingButton);
			myGrid.setWritingMode();
		    }
		});	
		titleBox.getChildren().add(writingButton);
		titleBox.setAlignment(Pos.CENTER);
		return titleBox;
	}

	private void initialize (Scene scene, int difficulty) {  
    	myView = new BorderPane();
        myGridDimension = 3;
        myGrid = new Grid(myGridDimension*myGridDimension, difficulty);
        myTilepane = createGrid(myGridDimension);
        myGroup = new Group(myTilepane);
        myView.setCenter(myGroup);
        scene = new Scene(myView, myWidth, myHeight);
        myScene = scene;
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
						Square square = new Square(42, myGridDimension*myGridDimension);
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
    	if (currentText == "Pen") {
	    	writingButton.setText("Pencil");
	    	writingButton.setFont(Font.font("Times New Roman", 22));
	    }
	    else if (currentText == "Pencil") {
	    	writingButton.setText("Pen");
	    	writingButton.setFont(Font.font("Times New Roman", 22));
	    }
    }
    
	public Scene getScene() {
    	return myScene;
    }
}
