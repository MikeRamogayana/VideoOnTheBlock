package ui;

import java.io.File;

import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.media.MediaPlayer.Status;

public class MediaPlayerUI extends StackPane {
	
	private Button btnPlay = null;
	private  double timeWatched = 0;
	
	public MediaPlayerUI(String url) {
		this.setStyle("-fx-background-color: black;");
		
		Media media = new Media(url);
		MediaPlayer player = new MediaPlayer(media);
		MediaView view = new MediaView(player);
		view.fitWidthProperty().bind(this.widthProperty().multiply(0.8));
		view.fitHeightProperty().bind(this.heightProperty());
		view.setPreserveRatio(true);

		btnPlay = new Button();
		btnPlay.setPrefSize(40, 40);
		btnPlay.setStyle("-fx-background-image: url(" + (new File("data/images/"
				+ "play.png")).toURI().toString() + "); -fx-background-color: transparent;");
		btnPlay.setDisable(true);
		view.setOnMouseEntered(e->{
			btnPlay.setVisible(true);
			view.setOnMouseClicked(event -> {
				if(player.getStatus() == Status.PAUSED) {
					btnPlay.setStyle("-fx-background-image: url(" + (new File("data/images/"
							+ "pause.png")).toURI().toString() + "); -fx-background-color: transparent;");
					player.play();
				}
				if(player.getStatus() == Status.PLAYING) {
					btnPlay.setStyle("-fx-background-image: url(" + (new File("data/images/"
							+ "play.png")).toURI().toString() + "); -fx-background-color: transparent;");
					player.pause();
				}
			});
		});
		view.setOnMouseExited(e->{
			btnPlay.setVisible(false);
		});
		player.setOnReady(new Runnable() {

			@Override
			public void run() {
				btnPlay.setStyle("-fx-background-image: " + "url(" + (new File("data/images/"
						+ "pause.png")).toURI().toString() + "); -fx-background-color: transparent;");
				timeWatched = media.getDuration().toMinutes();
				player.play();
			}
			
		});
		
		this.getChildren().addAll(view, btnPlay);
		
	}

	public double getTimeWatched() {
		return timeWatched;
	}

}

