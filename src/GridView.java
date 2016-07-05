import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
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
	public GridView() {
		myHeight = 400;
		myWidth = 400;
		initialize();
	}
	
	public GridView(int height, int width) {
		myHeight = height;
		myWidth = width;
		initialize();
	}

	public GridView(int height, int width, Scene scene) {
		myHeight = height;
		myWidth = width;
		initialize(scene);
	}
	
    /**
     * initializes the necessary components to create a TabPane with at least one instance of UI
     */
    private void initialize () {
        myView = new BorderPane();
        myGridDimension = 3;
        myGrid = new Grid(myGridDimension*myGridDimension);
        myTilepane = createGrid(myGridDimension);
        myGroup = new Group(myTilepane);
        myView.setCenter(myGroup);
        myScene = new Scene(myView, myWidth, myHeight);
    }

    private void initialize (Scene scene) {  
    	myView = new BorderPane();
        myGridDimension = 3;
        myGrid = new Grid(myGridDimension*myGridDimension);
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
			    miniGrid.setPrefTileWidth(40);
			    miniGrid.setPrefTileHeight(40);
				for (int m = 0; m < dim; m++) {
					for (int n = 0; n < dim; n++) {
						Square square = new Square(40);
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
    
	public Scene getScene() {
    	return myScene;
    }
}
