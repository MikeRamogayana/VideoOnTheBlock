package ui;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import datastructure.Graph;
import datastructure.Graph.Edge;
import datastructure.Graph.Vertex;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Block;
import model.Blockchain;
import model.Client;
import model.Message;
import model.Video;

/**
 * This Handles The Functioning Logic Of The App
 * 
 * @author Mothupi Mike Ramogayana
 *
 */
public class BlockchainUI extends VBox {
	
	private Blockchain blockchain = null;
	
	private Stage stage = null;
	private Client client = null;
	
	private MenuBar menubar = null;
	private HBox hbContent = null;
	
	private TabPane tpLeft = null;
	private TabPane tpRight = null;
	
	private Tab tabVideos = null;
	private Tab tabClients = null;
	private Tab tabHistory = null;
	private Tab tabStatus = null;
	
	private VBox vbStatus = null;
	
	private boolean isConnected = false;
	Message message = null;

	private Graph<Client> clientsGraph = null;
	
	public BlockchainUI(Stage stage) {
		this.stage = stage;
		setup();
		/*
		stage.setOnCloseRequest(e->{
			if(client != null && clientsGraph != null) {
				for(int i = 0; i < clientsGraph.getVertices().size(); i++) {
					if(clientsGraph.getVertices().get(i).getValue().compareTo(client) == 1) {
						clientsGraph.getVertices().remove(i);
						break;
					}
				}
				sendGraphToAll();
			}
		});
		*/
	}
	
	/**
	 * Displays The UI
	 */
	public void start() {
		stage.setScene(new Scene(this, 900, 600));
		stage.show();
		if(!isConnected) {
			initConnection();
		}
	}
	
	/**
	 * Initialize Connection
	 */
	private void initConnection() {
		ConnectionUI connectionUI = new ConnectionUI(stage);
		connectionUI.start();
		if(connectionUI.getLocalPort() != 0) {
			client = new Client(connectionUI.getLocalPort());
			if(connectionUI.getRemoteIp() != null && connectionUI.getRemotePort() != 0) {
				initCommunition(connectionUI.getRemoteIp(), connectionUI.getRemotePort());
			} else {
				initBlockchain();
				addStatusText("ME @ " + client.getLocalPort() + ": Waiting for connection...", Color.ORANGE);
			}
			listenToMessages();
		}
	}

	private void initBlockchain() {
		blockchain = new Blockchain();
		
	}

	/**
	 * Listen To Messages And Respond And Change The UI Accordingly
	 */
	private void listenToMessages() {
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(true) {
					message = client.getConnection().receive();
					if(message != null) {
						System.out.println(message.getText().toString());
						addStatusText("FROM: " + message.getRemoteHost().toUpperCase() + " ON PORT: "
								+ message.getRemotePort() + ": " + message.getText().toUpperCase(), Color.GREEN);
						
						if(message.getText().contains("GRAPH REQUEST")) {
							
							sendGraph(message.getRemoteHost(), message.getRemotePort());
							
						} else if(message.getText().contains("GRAPH RECEIVE")) {
							
							receiveGraph(message.getRemoteHost(), message.getRemotePort());
							
						} else if(message.getText().contains("BLOCKCHAIN REQUEST")) {
							
							sendBlockchain(message.getRemoteHost(), message.getRemotePort());
							
						} else if(message.getText().contains("BLOCKCHAIN RECEIVE")) {
							
							receiveBlockchain(message.getRemoteHost(), message.getRemotePort());
							
						} else if(message.getText().contains("BLOCK VALIDATE")) {
							
							validateBlock(message.getRemoteHost(), message.getRemotePort());
							
						} else if(message.getText().contains("BLOCK RECEIVE VALIDATED")) {
							
							receiveValidatedBlock(message.getRemoteHost(), message.getRemotePort());
							
						} else if (message.getText().contains("BLOCK RECEIVE")) {
							
							receiveBlock(message.getRemoteHost(), message.getRemotePort());
							
						} else if(message.getText().contains("HELO CLIENT")) {
							
							sendClient(message.getRemoteHost(), message.getRemotePort());
							client.getConnection().send(new Message("BLOCKCHAIN REQUEST", 
									message.getRemoteHost(), message.getRemotePort()));
							
						} else if(message.getText().contains("REQUEST CLIENT")) {
							
							sendClient(message.getRemoteHost(), message.getRemotePort());
							
						} else if(message.getText().contains("RECEIVE CLIENT")) {
							
							Client newClient = receiveClient(message.getRemoteHost(), message.getRemotePort());
							updateGraph(newClient);
							sendGraphToAll();
							
						} else if(message.getText().contains("HELO")) {
							
							client.getConnection().send(new Message("HELO CLIENT",
									message.getRemoteHost(), message.getRemotePort()));
							addStatusText("TO: " + message.getRemoteHost() + " ON " + message.getRemotePort() 
									+ ": " + "HELO CLIENT", Color.GREEN);
							
						}
						message = null;
					}
				}
			}
		});
		thread.start();
	}
	
	/**
	 * Sends An Updated Graph To All Clients On The Network
	 */
	protected void sendGraphToAll() {
		for(Vertex<Client> vertex: clientsGraph.getVertices()) {
			sendGraph(vertex.getValue().getHostName(), vertex.getValue().getLocalPort());
		}
	}

	/**
	 * Get A Client Instance From Remote Client
	 * 
	 * @param remoteHost
	 * @param remotePort
	 * @return
	 */
	protected Client receiveClient(String remoteHost, int remotePort) {
		Client client = null;
		byte[] bytes = this.client.getConnection().receiveBytes();
		try {
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
			client = (Client) ois.readObject();
			ois.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return client;
	}

	/**
	 * Sends A Client Instance To Remote Client
	 * 
	 * @param remoteHost
	 * @param remotePort
	 */
	protected void sendClient(String remoteHost, int remotePort) {
		client.getConnection().send(new Message("RECEIVE CLIENT", remoteHost, remotePort));
		addStatusText("TO: " + remoteHost + " ON " + remotePort 
				+ ": " + "BLOCK RECEIVE", Color.GREEN);
		
		client.getConnection().sendObject(client, remoteHost, remotePort);
	}

	/**
	 * Receive And Add A Validated Block To The Blockchain
	 * 
	 * @param remoteHost
	 * @param remotePort
	 */
	protected void receiveValidatedBlock(String remoteHost, int remotePort) {
		Block block = receiveBlock(remoteHost, remotePort);
		if(blockchain != null) {
			addStatusText("UPDATING BLOCKCHAIN...", Color.BLUE);
			blockchain.addValidatedBlock(block);
			addStatusText("BLOCKCHAIN UPDATED SUCCESSFULLY...", Color.BLUE);
		}
		addBlockchainHistory();
	}

	/**
	 * Validates Block From Remote Client
	 * 
	 * @param remoteHost
	 * @param remotePort
	 */
	protected void validateBlock(String remoteHost, int remotePort) {
		Block block = receiveBlock(remoteHost, remotePort);
		boolean isValid = true;
		if(blockchain != null) {
			isValid = blockchain.validateBlock(block, client);
		} else {
			blockchain = new Blockchain();
			blockchain.addValidatedBlock(block);
		}
		addBlockchainHistory();
		if(isValid) {
			addStatusText("BLOCK VALIDATED SUCCESSFULLY!!!", Color.BLUE);
			for(Vertex<Client> vertex: clientsGraph.getVertices()) {
				client.getConnection().send(new Message("BLOCK RECEIVE VALIDATED", vertex.getValue().getHostName(),
						vertex.getValue().getLocalPort()));
				client.getConnection().sendObject(block, vertex.getValue().getHostName(),
						vertex.getValue().getLocalPort());
			}
			return;
		}
		addStatusText("INVALID BLOCK, REJECTED!!!", Color.RED);
	}

	/**
	 * Receive Block From Remote Client
	 * 
	 * @param remoteHost
	 * @param remotePort
	 * @return
	 */
	protected Block receiveBlock(String remoteHost, int remotePort) {
		addStatusText("TO: " + remoteHost + " ON " + remotePort 
				+ ": " + "BLOCK RECEIVE", Color.GREEN);
		
		Block block = null;
		byte[] bytes = client.getConnection().receiveBytes();
		try {
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
			block = (Block) ois.readObject();
			ois.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return block;
	}

	/**
	 * Send Block To Remote Client
	 * 
	 * @param block
	 * @param remoteHost
	 * @param remotePort
	 */
	protected void sendBlock(Block block, String remoteHost, int remotePort) {
		client.getConnection().send(new Message("BLOCK RECEIVE", remoteHost, remotePort));
		addStatusText("TO: " + remoteHost + " ON " + remotePort
				+ ": " + "BLOCK RECEIVE", Color.GREEN);
		
		client.getConnection().sendObject(block, remoteHost, remotePort);
	}

	/**
	 * Receives A Copy Of Blockchain From Remote Client
	 * 
	 * @param remoteHost
	 * @param remotePort
	 */
	protected void receiveBlockchain(String remoteHost, int remotePort) {
		addStatusText("TO: " + remoteHost + " ON " + remotePort 
				+ ": " + "BLOCKCHAIN REQUEST", Color.GREEN);
		
		blockchain = new Blockchain();
		byte[] bytes = new byte[2048];
		while(true) {
			bytes = client.getConnection().receiveBytes();
			try {
				ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
				Block block = (Block) ois.readObject();
				if(block == null) {
					break;
				}
				blockchain.addValidatedBlock(block);
				ois.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		addBlockchainHistory();
	}
	

	/**
	 * Send The Copy OF The Blockchain To Remote Client
	 * 
	 * @param remoteHost
	 * @param remotePort
	 */
	protected void sendBlockchain(String remoteHost, int remotePort) {
		if(blockchain != null) {
			client.getConnection().send(new Message("BLOCKCHAIN RECEIVE", remoteHost, remotePort));
			addStatusText("TO: " + remoteHost + " ON " + remotePort
					+ ": " + "BLOCKCHAIN RECEIVE", Color.GREEN);
			
			for(Block block: blockchain) {
				client.getConnection().sendObject(block, remoteHost, remotePort);
			}
			client.getConnection().sendObject(null, remoteHost, remotePort);
		}
	}

	/**
	 * Receives Graph Content From Remote Client And Create New Graph
	 * 
	 * @param remoteHost
	 * @param remotePort
	 */
	protected void receiveGraph(String remoteHost, int remotePort) {
		addStatusText("TO: " + remoteHost + " ON " + remotePort 
				+ ": " + "GRAPH RECEIVE", Color.GREEN);
		
		byte[] bytes = new byte[2048];
		while(true) {
			bytes = client.getConnection().receiveObject();
			try {
				ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
				Client inClient = (Client) ois.readObject();
				ois.close();
				if(inClient == null) {
					break;
				}
				System.out.println(inClient.toString());
				updateGraph(inClient);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		this.addClientPanes();
	}

	/**
	 * Sends Graph Contents To Remote Client
	 * 
	 * @param remoteHost
	 * @param remotePort
	 */
	protected void sendGraph(String remoteHost, int remotePort) {
		client.getConnection().send(new Message("GRAPH RECEIVE", remoteHost, remotePort));
		addStatusText("TO: " + remoteHost + " ON " + remotePort 
				+ ": " + "SENDING GRAPH...", Color.ORANGE);
		
		if(clientsGraph != null) {
			for(Vertex<Client> vertex: clientsGraph.getVertices()) {
				if(vertex != null) {
					client.getConnection().sendObject(vertex.getValue(), remoteHost, remotePort);
				}
			}
			client.getConnection().sendObject(null, remoteHost, remotePort);
			addStatusText("TO: " + remoteHost + " ON " + remotePort 
					+ ": " + "GRAPH SENT...", Color.ORANGE);
			return;
		}

		addStatusText("TO: " + remoteHost + " ON " + remotePort 
		+ ": " + "GRAPH IS NULL", Color.ORANGE);
	}

	/**
	 * Create A New Instance Of Graph
	 * 
	 * @param client
	 */
	private void createNewGraph(Client client){
		Vertex<Client> v1 = new Vertex<>(this.client);
		Vertex<Client> v2 = new Vertex<>(client);
		Edge<Client> edge = new Edge<>(1, v1, v2);
		
		clientsGraph = new Graph<>();
		clientsGraph.getVertices().add(v1);
		clientsGraph.getVertices().add(v2);
		clientsGraph.getEdges().add(edge);
		

		addStatusText("NEW GRAPH CREATED...", Color.BLUE);
	}
	
	/**
	 * Updates An Existing Clients Graph
	 * 
	 * @param client
	 */
	private void updateGraph(Client client) {
		if(clientsGraph != null) {
			for(int i = 0; i < clientsGraph.getVertices().size(); i++) {
				if(clientsGraph.getVertices().get(i).getValue().compareTo(client) == 1) {
					clientsGraph.getVertices().get(i).getValue().setDataFiles(client.getDataFiles());

					addStatusText("GRAPH UPDATED...", Color.BLUE);
					return;
				}
			}
			
			Vertex<Client> vertex = new Vertex<>(client);
			Edge<Client> edge = new Edge<Client>(1, new Vertex<>(this.client), vertex);
			
			clientsGraph.getVertices().add(vertex);
			clientsGraph.getEdges().add(edge);
			addStatusText("GRAPH UPDATED...", Color.BLUE);
		} else {
			createNewGraph(client);
		}
	}

	/**
	 * Initiates Communication With Remote Client
	 * 
	 * @param remoteIp
	 * @param remotePort
	 */
	private void initCommunition(String remoteIp, int remotePort) {
		addStatusText("TO: " + remoteIp.toUpperCase() + " ON PORT: "
				+ remotePort + ": HELO", Color.GREEN);
		this.client.getConnection().send(new Message("HELO", remoteIp,
				remotePort));
	}
	
	/**
	 * Sets Up The User Interface
	 */
	private void setup() {
		addMenuBar();
		addContentHBox();
		addTextArea();
	}
	
	/**
	 * Add A TextArea To Show Running Processes
	 */
	private void addTextArea() {
		vbStatus = new VBox();
		ScrollPane sp = new ScrollPane(vbStatus);
		tabStatus.setContent(sp);
	}
	
	/**
	 * Add Text To The TextArea For Show Running Processes
	 * 
	 * @param text
	 * @param color
	 */
	private void addStatusText(String text, Color color) {
		Text t = new Text(text);
		t.setFill(color);
		Platform.runLater(new Runnable() {

			@Override
			public void run() {

				vbStatus.getChildren().add(t);
			}
		});
	}
	
	/**
	 * Add Menu Bar To The User Interface
	 */
	private void addMenuBar() {
		menubar = new MenuBar();
		Menu menu = new Menu("File");
		MenuItem itemUpload = new MenuItem("Upload");
		MenuItem itemRefresh = new MenuItem("Refresh");
		
		itemUpload.setOnAction((e)->{
			UploadUI uploadUI = new UploadUI(this.client);
			Stage uploadStage = new Stage();
			uploadStage.initModality(Modality.WINDOW_MODAL);
			uploadStage.initOwner(stage);
			uploadStage.setScene(new Scene(uploadUI, 250, 100));
			uploadStage.showAndWait();

			tabVideos.setContent(createDataFileListView(this.client));
			if(clientsGraph != null) {
				updateGraph(this.client);
				sendGraphToAll();
			}
		});
		
		itemRefresh.setOnAction(e -> {
			tabVideos.setContent(createDataFileListView(this.client));
			addClientPanes();
		});
		menu.getItems().addAll(itemUpload, itemRefresh);
		menubar.getMenus().add(menu);
		this.getChildren().add(menubar);
	}
	
	/**
	 * Add HBox As To First Layer
	 */
	private void addContentHBox() {
		hbContent = new HBox();
		
		tabVideos = new Tab("My Videos", null);
		tabVideos.setClosable(false);
		tabClients = new Tab("Connected Clients", null);
		tabClients.setClosable(false);
		
		tpLeft = new TabPane();
		tpLeft.getTabs().addAll(tabVideos, tabClients);
		tpLeft.prefWidthProperty().bind(hbContent.prefWidthProperty().divide(2));
		tpLeft.prefHeightProperty().bind(hbContent.prefHeightProperty());
		
		tabHistory = new Tab("Blocks History", null);
		tabHistory.setClosable(false);
		tabStatus = new Tab("Current Status", null);
		tabStatus.setClosable(false);
		
		tpRight = new TabPane();
		tpRight.getTabs().addAll(tabHistory, tabStatus);
		tpRight.prefWidthProperty().bind(hbContent.prefWidthProperty().divide(2));
		tpRight.prefHeightProperty().bind(hbContent.prefHeightProperty());
		
		hbContent.getChildren().addAll(tpLeft, tpRight);
		hbContent.setSpacing(5);
		hbContent.setPadding(new Insets(5,5,5,5));
		hbContent.prefWidthProperty().bind(this.widthProperty());
		hbContent.prefHeightProperty().bind(this.heightProperty());
		
		this.getChildren().add(hbContent);
	}
	
	/**
	 * Add Lists OF Clients And Their Files
	 */
	private void addClientPanes() {
		
		if(clientsGraph != null) {
			ObservableList<TitledPane> list = FXCollections.observableArrayList();
			
			for(Vertex<Client> vertex: clientsGraph.getVertices()) {
				ObservableList<GridPane> listGrid = FXCollections.observableArrayList();
				if(vertex != null) {
					if(vertex.getValue().compareTo(client) != 1) {
						for(Video df: vertex.getValue().getDataFiles()) {
							GridPane gp = new GridPane();
							Label label = new Label(df.getTitle());
							label.prefWidthProperty().bind(gp.widthProperty().divide(2.5));
							Hyperlink link = new Hyperlink("Watch Video");
							link.prefWidthProperty().bind(gp.widthProperty().divide(2.5));
							link.setOnAction(e->{
								Stage playerStage = new Stage();
								playerStage.initModality(Modality.WINDOW_MODAL);
								playerStage.initOwner(stage);
								MediaPlayerUI player = new MediaPlayerUI(df.getFilepath());
								playerStage.setScene(new Scene(player, 700, 400));
								playerStage.setOnCloseRequest(eve->{
									playerStage.close();
								});
								playerStage.showAndWait();
								
								Block block = blockchain.createBlock(vertex.getValue().getPublicKey(), df, player.getTimeWatched());
								System.out.println(block.toString());
								
								client.getConnection().send(new Message("BLOCK VALIDATE", vertex.getValue().getHostName(),
										vertex.getValue().getLocalPort()));
								
								client.getConnection().sendObject(block, vertex.getValue().getHostName(), 
										vertex.getValue().getLocalPort());
								
							});
							GridPane.setConstraints(label, 0, 0);
							GridPane.setConstraints(link, 1, 0);
							gp.getChildren().addAll(label, link);
							listGrid.add(gp);
						}
						ListView<GridPane> lvGrid = new ListView<>(listGrid);
						TitledPane tp = new TitledPane(vertex.getValue().getUsername(), lvGrid);
						list.add(tp);
					}
				}
			}
			ListView<TitledPane> lv = new ListView<>(list);
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					tabClients.setContent(lv);
				}
			});
		}
	}

	private void addBlockchainHistory() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				if(blockchain != null) {
					ObservableList<TitledPane> list = FXCollections.observableArrayList();
					for(Block block: blockchain) {
						if(block.getDataFile() != null) {
							GridPane gp = new GridPane();
							Label label00 = new Label("File Title");
							label00.prefWidthProperty().bind(gp.widthProperty().divide(2.5));
							Label label10 = new Label(block.getDataFile().getTitle());
							label10.prefWidthProperty().bind(gp.widthProperty().divide(2.5));
							Label label01 = new Label("Block Hash");
							label01.prefWidthProperty().bind(gp.widthProperty().divide(2.5));
							Label label11 = new Label(block.getHash());
							label11.prefWidthProperty().bind(gp.widthProperty().divide(2.5));
							Label label02 = new Label("Previous Hash");
							label02.prefWidthProperty().bind(gp.widthProperty().divide(2.5));
							Label label12 = new Label(block.getPrevHash());
							label12.prefWidthProperty().bind(gp.widthProperty().divide(2.5));
							Label label03 = new Label("Watch Time(min)");
							label03.prefWidthProperty().bind(gp.widthProperty().divide(2.5));
							Label label13 = new Label(String.valueOf(block.getTimeWatched()));
							label13.prefWidthProperty().bind(gp.widthProperty().divide(2.5));
							GridPane.setConstraints(label00, 0, 0);
							GridPane.setConstraints(label10, 1, 0);
							GridPane.setConstraints(label01, 0, 1);
							GridPane.setConstraints(label11, 1, 1);
							GridPane.setConstraints(label02, 0, 2);
							GridPane.setConstraints(label12, 1, 2);
							GridPane.setConstraints(label03, 0, 3);
							GridPane.setConstraints(label13, 1, 3);
							gp.getChildren().addAll(label00, label01, label10, label11,label02, label12, 
									label03, label13);
							
							TitledPane tp = new TitledPane(block.getHash(), gp);
							list.add(tp);
						}
					}
					ListView<TitledPane> lv = new ListView<>(list);
					tabHistory.setContent(lv);
				}
			}
			
		});
	}
	
	/**
	 * Create A List Of Files And Places The in A ListView
	 * 
	 * @param client
	 * @return
	 */
	public ListView<GridPane> createDataFileListView(Client client) {
		ObservableList<GridPane> list = FXCollections.observableArrayList();
		ListView<GridPane> listView = new ListView<>(list);
		for(Video video: client.getDataFiles()) {
			GridPane gridPane = new GridPane();
			
			Label label = new Label(video.getTitle());
			label.prefWidthProperty().bind(gridPane.widthProperty());

			gridPane.setHgap(50);
			GridPane.setConstraints(label, 0, 0);
			
			gridPane.getChildren().addAll(label);
			
			list.add(gridPane);
		}
		return listView;
	}
}