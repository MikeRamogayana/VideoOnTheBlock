package ui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ConnectionUI extends GridPane {
	
	private Stage stage = null;
	private Stage primaryStage = null;
	
	private String remoteIp;
	private int remotePort;
	private int localPort;
	
	private Label lblLocalPort = null;
	private TextField txtLocalPort = null;
	private ToggleGroup toggle = null;
	private RadioButton rbWait = null;
	private RadioButton rbConnect = null;
	private Label lblRemoteIp = null;
	private TextField txtRemoteIp = null;
	private Label lblRemotePort = null;
	private TextField txtRemotePort = null;
	private Button btnConnect = null;
	private Label lblStatus = null;
	
	public ConnectionUI(Stage primaryStage) {
		
		this.primaryStage = primaryStage;

		lblLocalPort = new Label("Local Port Number");
		txtLocalPort = new TextField();

		rbWait = new RadioButton("Wait For Connection");
		rbWait.setSelected(true);
		rbWait.setOnAction(e->{
			if(rbWait.isSelected()) {
				txtRemoteIp.setDisable(true);
				txtRemotePort.setDisable(true);
			}
		});
		rbConnect = new RadioButton("Connect To Someone");
		rbConnect.setOnAction(e->{
			if(rbConnect.isSelected()) {
				txtRemoteIp.setDisable(false);
				txtRemotePort.setDisable(false);
			}
		});

		toggle = new ToggleGroup();
		rbWait.setToggleGroup(toggle);
		rbConnect.setToggleGroup(toggle);
		
		lblRemoteIp = new Label("Remote IP Address");
		txtRemoteIp = new TextField();
		txtRemoteIp.setDisable(true);
		
		lblRemotePort = new Label("Remote Port Number");
		txtRemotePort = new TextField();
		txtRemotePort.setDisable(true);
		
		btnConnect = new Button("Connect");
		btnConnect.setMaxWidth(Double.MAX_VALUE);
		btnConnect.setDefaultButton(true);
		btnConnect.setOnAction(e -> {
			if(rbConnect.isSelected()) {
				if(txtRemoteIp.getText().matches("[a-zA-Z0-9]+") && txtRemotePort.getText().matches("[0-9]+")
						&& txtLocalPort.getText().matches("[0-9]+")) {
					remoteIp = txtRemoteIp.getText();
					remotePort = Integer.valueOf(txtRemotePort.getText());
					localPort = Integer.valueOf(txtLocalPort.getText());
					stage.close();
				} else {
					lblStatus.setVisible(true);
				}
			} if(rbWait.isSelected()) {
				if(txtLocalPort.getText().matches("[0-9]+")) {
					localPort = Integer.valueOf(txtLocalPort.getText());
					stage.close();
				} else {
					lblStatus.setVisible(true);
				}
			}
		});
		
		lblStatus = new Label("Error!!! Check your inputs.");
		lblStatus.setVisible(false);
		lblStatus.setStyle("-fx-background-color: red;");
		lblStatus.setMaxWidth(Double.MAX_VALUE);
		lblStatus.setPadding(new Insets(5,5,5,5));
		lblStatus.setTextAlignment(TextAlignment.CENTER);

		GridPane.setConstraints(lblLocalPort, 0, 0);
		GridPane.setConstraints(txtLocalPort, 1, 0);
		
		GridPane.setConstraints(rbWait, 0, 1);
		GridPane.setConstraints(rbConnect, 1, 1);
		
		GridPane.setConstraints(lblRemoteIp, 0, 2);
		GridPane.setConstraints(txtRemoteIp, 1, 2);
		
		GridPane.setConstraints(lblRemotePort, 0, 3);
		GridPane.setConstraints(txtRemotePort, 1, 3);

		GridPane.setConstraints(btnConnect, 0, 4, 2, 1);

		GridPane.setConstraints(lblStatus, 0, 5, 2, 1);
		
		this.setHgap(5);
		this.setVgap(5);
		this.setPadding(new Insets(5,5,5,5));
		this.getChildren().addAll(lblLocalPort, txtLocalPort, rbWait, rbConnect,
				lblRemoteIp, txtRemoteIp, lblRemotePort, txtRemotePort,
				btnConnect, lblStatus);
	}
	
	public void start() {
		stage = new Stage();
		stage.setResizable(false);
		stage.initOwner(primaryStage);
		stage.initModality(Modality.WINDOW_MODAL);
		stage.setScene(new Scene(this, 290, 180));
		stage.showAndWait();
	}

	public String getRemoteIp() {
		return remoteIp;
	}

	public void setRemoteIp(String remoteIp) {
		this.remoteIp = remoteIp;
	}

	public int getRemotePort() {
		return remotePort;
	}

	public void setRemotePort(int remotePort) {
		this.remotePort = remotePort;
	}

	public int getLocalPort() {
		return localPort;
	}

	public void setLocalPort(int localPort) {
		this.localPort = localPort;
	}

}
