package model;

import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;

import com.google.gson.Gson;

public class Video implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int id;
	private String title;
	private String filepath;
	private long size;
	private String privateKey;
	/**
	 * @param id
	 * @param title
	 * @param filepath
	 * @param size
	 */
	public Video(int id, String title, String filepath, long size, String privateKey) {
		super();
		this.id = id;
		this.title = title;
		try {
			this.filepath = new File(filepath).toURI().toURL().toString();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		this.size = size;
		this.privateKey = privateKey;
	}
	/**
	 * 
	 */
	public Video() {
		super();
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getFilepath() {
		return filepath;
	}
	public void setFilepath(String filepath) {
		try {
			this.filepath = new File(filepath).toURI().toURL().toString();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	
	public String getPrivateKey() {
		return privateKey;
	}
	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
