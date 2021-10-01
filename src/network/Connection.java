package network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

import model.Message;

/**
 * Class for handling network connection based on UDP.
 * 
 * @author Mothupi Mike Ramogayana
 *
 */
public class Connection implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static DatagramSocket socket = null;
	
	public Connection(int localport) {
		try {
			socket = new DatagramSocket(localport);
		} catch (SocketException e) {
			System.err.println(e.getMessage());
		}
		
	}
	
	public void sendBytes(byte[] bytes, String host, int port) {
		try {
			DatagramPacket packet = new DatagramPacket(bytes, bytes.length,
					InetAddress.getByName(host), port);
			socket.send(packet);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void send(Message message) {
		byte[] bytes = new byte[1024];
		try {
			InputStream is = new ByteArrayInputStream((message.getText()).getBytes(Charset.defaultCharset()));
			@SuppressWarnings("unused")
			int count;
			while((count = is.read(bytes)) > 0) {
				DatagramPacket packet = new DatagramPacket(bytes, bytes.length,
						InetAddress.getByName(message.getRemoteHost()), message.getRemotePort());
				socket.send(packet);
				System.out.println("Packet Sent");
			}
			
			is.close();
		} catch (UnknownHostException e) {
			System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
	
	public Message receive() {
		String received = null;
		byte[] bytes = new byte[2048];
		DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
		try {
			socket.receive(packet);
			received = new String(packet.getData(), Charset.defaultCharset());
			Message message = new Message(received.strip(), packet.getAddress().getHostName(),
					packet.getPort());
			return message;
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		return null;
	}
	
	public byte[] receiveBytes() {
		byte[] bytes = new byte[64000];
		DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
		try {
			socket.receive(packet);
			return packet.getData();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		return null;
	}
	
	public void sendObject(Object obj, String host, int port) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			ObjectOutput oo = new ObjectOutputStream(bos);
			oo.writeObject(obj);
			oo.flush();
			oo.close();
			byte[] buffer = bos.toByteArray();
			byte[] bytes = new byte[2048];
			
			InputStream is = new ByteArrayInputStream(buffer);
			@SuppressWarnings("unused")
			int count;
			while((count = is.read(bytes)) > 0) {
				DatagramPacket packet = new DatagramPacket(bytes, bytes.length,
						InetAddress.getByName(host), port);
				socket.send(packet);
				System.out.println("Packet Sent");
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public byte[] receiveObject() {
		byte[] bytes = new byte[2048];
		DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
		try {
			socket.receive(packet);
			return packet.getData();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		return null;
	}

}
