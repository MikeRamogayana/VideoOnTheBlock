package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javafx.scene.Scene;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import network.Connection;
import ui.UploadUI;

public class Client implements Comparable<Client>, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String publicKey;
	private String privateKey;
	private String username;
	private String hostName;
	private int port;
	private static Connection connection;
	private ArrayList<Video> videos;
	
	/**
	 * @param username
	 * @param localhost
	 * @param localport
	 * @param videoList
	 */
	public Client(String username, String host, int port, ArrayList<Video> videos) {
		super();
		this.username = username;
		this.hostName = host;
		this.port = port;
		this.videos = videos;
		
		Client.connection = new Connection(port);
		
		this.publicKey = Block.toHash(this.username);
		this.privateKey = Block.toHash(this.publicKey);
		
	}
	
	public Client(String hostname, int localport) {
		super();
		this.username = hostname.toUpperCase() + " " + String.valueOf(localport);
		this.hostName = hostname;
		this.port = localport;
		this.videos = new ArrayList<>();
		
		Client.connection = new Connection(localport);
		
		this.publicKey = Block.toHash(this.username);
		this.privateKey = Block.toHash(this.publicKey);
	}
	
	public Client() {
		this.publicKey = Block.toHash(this.username);
		this.privateKey = Block.toHash(this.publicKey);
	}
	
	/**
	 * 
	 */
	public Client(int localport) {
		super();
		this.username = "LOCALHOST " + String.valueOf(localport);
		this.hostName = "localhost";
		this.port = localport;
		this.videos = new ArrayList<>();
		
		Client.connection = new Connection(localport);
		this.publicKey = Block.toHash(this.username);
		this.privateKey = Block.toHash(this.publicKey);
	}
	
	public String getPublicKey() {
		return publicKey;
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getHostName() {
		return hostName;
	}
	public void setLocalHost(String hostName) {
		this.hostName = hostName;
	}
	public int getLocalPort() {
		return port;
	}
	public void setLocalPort(int localport) {
		this.port = localport;
	}
	public ArrayList<Video> getDataFiles() {
		return videos;
	}
	public void setDataFiles(ArrayList<Video> videos) {
		this.videos = videos;
	}
	public Connection getConnection() {
		return connection;
	}
	public void setConnection(Connection connection) {
		Client.connection = connection;
	}
	
	public void removeDataFile(Video video) {
		if(videos.contains(video)) {
			videos.remove(video);
		}
	}
	
	public void addDataFile(Video video) {
		if(videos.contains(video)) {
			return;
		}
		videos.add(video);
	}

	public void uploadDataFile(Stage primaryStage) {
		Stage stage = new Stage();
		stage.setResizable(false);
		stage.initOwner(primaryStage);
		stage.initModality(Modality.WINDOW_MODAL);
		stage.setScene(new Scene(new UploadUI(this), 248, 100));
		stage.showAndWait();
	}
	
	public void sendDataFile(Client client, Video video) {
		Message message = new Message();
		message.setRemoteHost(client.hostName);
		message.setRemotePort(client.getLocalPort());
		
		File file = new File(video.getFilepath());
		try {
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
			StringBuilder sb = new StringBuilder();
			int c = isr.read();
			while(c >= 0) {
				sb.append(c);
				c = isr.read();
			}
			isr.close();
			message.setText(sb.toString());
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
		} catch (UnsupportedEncodingException e) {
			System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		System.out.println(message.getText());
		connection.send(message);
	}
	
	public void downloadDataFile(Video video) {
		DirectoryChooser dc = new DirectoryChooser();
		File file = dc.showDialog(null);
		if(file != null) {
			file = new File(file.getAbsolutePath() + "/" + video.getTitle()
			+ video.getFilepath().substring(video.getFilepath().lastIndexOf(".")));
			System.out.println(file);
			
			try {
				FileOutputStream fos = new FileOutputStream(file, false);
				byte[] bytes = new byte[2048];
				while((bytes = connection.receiveBytes()) != null) {
					fos.write(bytes, 0, bytes.length);
				}
				fos.flush();
				fos.close();
			} catch (FileNotFoundException e) {
				System.err.println(e.getMessage());
			} catch (UnsupportedEncodingException e) {
				System.err.println(e.getMessage());
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}
	}

	@Override
	public int compareTo(Client client) {
		if(client.getUsername().equals(this.getUsername())) {
			return 1;
		}
		return 0;
	}
	
	@Override
	public String toString() {
		String strFile = "[";
		if(videos.size() > 0) {
			strFile += "{";
			for(Video df: videos) {
				strFile += df.toString() +",";
			}
			strFile = strFile.substring(0, strFile.length()-1);
			strFile += "}";
		}
		strFile += "]";
		String str = "{\"username\":\"" + this.username + "\","
				+ "\"hostName\":\"" + this.hostName + "\","
				+ "\"port\":" + this.getLocalPort() + ","
				+ "\"connection\":{\"socket\": null},"
				+ "\"videos\":" + strFile + "}" ;
		
		return str;
	}
}
