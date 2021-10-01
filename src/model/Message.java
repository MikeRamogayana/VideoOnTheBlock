package model;

public class Message {

	private String text;
	private String remotehost;
	private int remoteport;
	
	public Message(String text, String remotehost, int remoteport) {
		this.text = text;
		this.remotehost = remotehost;
		this.remoteport = remoteport;
	}
	
	public Message() { }

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getRemoteHost() {
		return remotehost;
	}

	public void setRemoteHost(String remotehost) {
		this.remotehost = remotehost;
	}

	public int getRemotePort() {
		return remoteport;
	}

	public void setRemotePort(int remoteport) {
		this.remoteport = remoteport;
	}
	
}
