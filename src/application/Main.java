package application;

import javafx.application.Application;
import javafx.stage.Stage;
import ui.BlockchainUI;

public class Main extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		stage.setTitle("Video On The Block");
		BlockchainUI blockchainUI = new BlockchainUI(stage);
		blockchainUI.start();
	}

}