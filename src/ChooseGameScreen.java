import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class ChooseGameScreen {

	private int myHeight;
	private int myWidth;
	private BorderPane myBP;
	private Group myGroup;
	private VBox myButtonBox;
	private Scene myScene;
	private Stage mainStage;
	
	public ChooseGameScreen(int height, int width, Stage stage) {
		mainStage = stage;
		myHeight = height;
		myWidth = width;
		initialize();
	}
	
	private void initialize() {
		myBP = new BorderPane();
		myGroup = new Group();
		myButtonBox = new VBox();
		myGroup.getChildren().add(myButtonBox);
		addRegularButton(myGroup, 0);
		addRegularButton(myGroup, 1);
		addRegularButton(myGroup, 2);
		myBP.setCenter(myGroup);
		myScene = new Scene(myBP, myWidth, myHeight);
	}
	
	private void addRegularButton(Group group, int difficulty) {
		Button regular;
		if (difficulty == 0) {
			regular = new Button("Easy");
		}
		else if (difficulty == 1) {
			regular = new Button("Medium");
		}
		else {
			regular = new Button("Hard");
		}
		myButtonBox.getChildren().add(regular);
		regular.setOnAction(new EventHandler<ActionEvent>() {
		    @Override public void handle(ActionEvent e) {
		    	GridView gv = new GridView(myHeight, myWidth, difficulty);
		        myScene = gv.getScene();
		        mainStage.setScene(myScene);
		        mainStage.show();
		    }
		});
	}
	
	public Scene getScene() {
		return myScene;
	}
	
}
