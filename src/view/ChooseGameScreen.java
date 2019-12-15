package view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class ChooseGameScreen {

	private int screenHeight;
	private int screenWidth;
	private BorderPane myBP;
	private Group myGroup;
	private Scene myScene;
	private Stage mainStage;
	
	public ChooseGameScreen(int screenHeight, int screenWidth, Stage stage) {
		mainStage = stage;
		this.screenHeight = screenHeight;
		this.screenWidth = screenWidth;
		initialize();
	}
	
	private void initialize() {
		myBP = new BorderPane();
		myGroup = new Group();
		VBox titleBox = new VBox();
		titleBox.setAlignment(Pos.CENTER);
		Text title = makeTitle();
		titleBox.getChildren().add(title);
		myBP.setTop(titleBox);
		VBox buttonBox = createButtonBox();
		myGroup.getChildren().add(buttonBox);
		myBP.setCenter(myGroup);
		myScene = new Scene(myBP, screenWidth, screenHeight);
	}
	
	private VBox createButtonBox() {
		VBox buttonBox = new VBox(12);
		buttonBox.setAlignment(Pos.CENTER);
		addDifficultyButton(buttonBox, 0);
		addDifficultyButton(buttonBox, 1);
		addDifficultyButton(buttonBox, 2);
		return buttonBox;
	}

	private Text makeTitle() {
		Text title = new Text("SUDOKU");
		title.setFont(Font.font("Times New Roman", 100));
		return title;
	}

	private void addDifficultyButton(VBox buttonBox, int difficulty) {
		Button difficultyButton = createDifficultyButton(difficulty);
		buttonBox.getChildren().add(difficultyButton);
		difficultyButton.setOnAction(new EventHandler<ActionEvent>() {
		    @Override public void handle(ActionEvent e) {
		    	GridView gv = new GridView(screenHeight, screenWidth, difficulty);
		        myScene = gv.getScene();
		        mainStage.setScene(myScene);
		        mainStage.show();
		    }
		});
	}
	
	private Button createDifficultyButton(int difficulty) {
		Button button;
		if (difficulty == 0) {
			button = new Button("Easy");
			button.setFont(Font.font("Times New Roman", 25));
		}
		else if (difficulty == 1) {
			button = new Button("Medium");
			button.setFont(Font.font("Times New Roman", 25));
		}
		else {
			button = new Button("Hard");
			button.setFont(Font.font("Times New Roman", 25));
		}
		return button;
	}
	
	public Scene getScene() {
		return myScene;
	}
	
}
