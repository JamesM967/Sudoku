package application;

import javafx.application.Application;
import javafx.stage.Stage;
import view.ChooseGameScreen;

public class Sudoku extends Application {

	private static final int DEFAULT_GAME_HEIGHT = 600;
	private static final int DEFAULT_GAME_WIDTH = 600;

	@Override
	public void start(Stage stage) {
		ChooseGameScreen display = new ChooseGameScreen(DEFAULT_GAME_HEIGHT, DEFAULT_GAME_WIDTH, stage);
		stage.setScene(display.getScene());
		stage.setTitle("Suduku!!!!");
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

}
