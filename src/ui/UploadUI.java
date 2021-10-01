package ui;

import java.io.File;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import model.Client;
import model.Video;

public class UploadUI extends GridPane {
	
	private String title = null;
	private String filepath = null;
	private long size = 0;
	
	private Label lblTitle = null;
	private FileChooser filechooser = null;
	private TextField txtTitle = null;
	private Button btnSelect = null;
	private Button btnUpload = null;
	private Label lblStatus = null;
	
	public UploadUI(Client client) {
		lblTitle = new Label("Title");
		txtTitle = new TextField();

		filechooser = new FileChooser();
		
		btnSelect = new Button("Select Video");
		btnSelect.setMaxWidth(Double.MAX_VALUE);
		btnSelect.setOnAction(e->{
			lblStatus.setVisible(false);
			File file = filechooser.showOpenDialog(null);
			if(file != null) {
				filepath = file.getAbsolutePath();
				size = file.length();
				btnUpload.setDisable(false);
			}
		});
		
		btnUpload = new Button("Upload Video");
		btnUpload.setMaxWidth(Double.MAX_VALUE);
		btnUpload.setDisable(true);
		btnUpload.setOnAction(e->{
			if(txtTitle.getText().matches("[a-zA-Z0-9\\s]+")) {
				title = txtTitle.getText();
				Video video = new Video(client.getDataFiles().size() + 1,title, filepath, size, client.getPrivateKey());
				client.addDataFile(video);
				lblStatus.setStyle("-fx-background-color: greenyellow;");
				lblStatus.setText("Video uploaded successfully");
				lblStatus.setVisible(true);
				btnUpload.setDisable(true);
				txtTitle.clear();
			} else {
				lblStatus.setStyle("-fx-background-color: orangered;");
				lblStatus.setText("Please enter title");
				lblStatus.setVisible(true);
			}
		});
		
		lblStatus = new Label();
		lblStatus.setMaxWidth(Double.MAX_VALUE);
		lblStatus.setPadding(new Insets(5,5,5,5));
		lblStatus.setVisible(false);
		
		GridPane.setConstraints(lblTitle, 0, 0);
		GridPane.setConstraints(txtTitle, 1, 0);
		GridPane.setConstraints(btnSelect, 0, 1);
		GridPane.setConstraints(btnUpload, 1, 1);
		GridPane.setConstraints(lblStatus, 0, 2, 2, 1);
		
		this.setPadding(new Insets(5,5,5,5));
		this.setHgap(5);
		this.setVgap(5);
		this.getChildren().addAll(lblTitle, txtTitle, btnSelect, btnUpload, lblStatus);
	}

}
