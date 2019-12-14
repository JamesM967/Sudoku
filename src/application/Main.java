package application;

import javafx.application.Application;
import javafx.stage.Stage;
import view.ChooseGameScreen;

public class Main extends Application {

	@Override
	public void start(Stage s) {
		ChooseGameScreen display = new ChooseGameScreen(600, 600, s);
		s.setScene(display.getScene());
		s.setTitle("Suduku!!!!");
		s.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

}
