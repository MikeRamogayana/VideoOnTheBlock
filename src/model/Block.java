package model;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Time;
import java.time.Instant;

import com.google.gson.Gson;

public class Block implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private int id = 0;
	private String hash = "0000000000000000000000000000000";
	private String prevHash = "0000000000000000000000000000000";
	private String publicKey = "0000000000000000000000000000000";
	private long timestamp = Time.from(Instant.now()).getTime();
	private Video video = null;
	private double timeWatched = 0.0;
	
	/**
	 * Construct a Block with the given arguments
	 * 
	 * @param id - Auto-generated id for the block
	 * @param hash - The hash of this block
	 * @param prevHash - The hash of the previous block or null if this is genesis block
	 * @param timestamp - The instance when the block was created
	 * @param views - List of transactions of an abstract type
	 */
	public Block(int id, String prevHash, String publicKey, Video video, double timeWatched) {
		super();
		this.id = id;
		this.prevHash = prevHash;
		this.publicKey = publicKey;
		this.timestamp = Time.from(Instant.now()).getTime();
		this.video = video;
		this.timeWatched = timeWatched;
		this.hash = toHash(toString());
	}
	
	/**
	 * Default constructor
	 */
	public Block() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getPrevHash() {
		return prevHash;
	}

	public void setPrevHash(String prevHash) {
		this.prevHash = prevHash;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String privateKey) {
		this.publicKey = privateKey;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public Video getDataFile() {
		return video;
	}

	public void setDataFile(Video video) {
		this.video = video;
	}
	
	public double getTimeWatched() {
		return timeWatched;
	}

	public static String toHash(String text) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA3-256");
			byte[] buffer = md.digest(text.getBytes(StandardCharsets.UTF_8));
			StringBuilder sb = new StringBuilder();
			for(byte bite: buffer) {
				sb.append(String.format("%02x", bite));
			}
			return sb.toString().toUpperCase();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public String toString() {
		String str = String.valueOf(id) + prevHash + String.valueOf(timestamp) 
		+ (new Gson().toJson(video)) + String.valueOf(timeWatched);
		
		return str;
	}
}
