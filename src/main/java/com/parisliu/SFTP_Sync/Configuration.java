package com.parisliu.SFTP_Sync;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * class used to save backup job information
 * 
 * @author Paris
 *
 */
public class Configuration {
 
	private String server; //server address
	private String user; //user name
	private String password; //password 
	private String folder = ""; //remote download folder 
	private long timestamp;



	public long getTimestamp() {
		return timestamp;
	}



	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public void setNewTimestamp() {
		this.timestamp = System.currentTimeMillis();
	}
 
	public String getServer() {
		return server;
	}
	public void setServer(String server) {		
		this.server = server;
	}
	public String enterUser() {
		if(user==null || user.isEmpty()) {
			System.out.print("User ID:");
			Scanner input = new Scanner(System.in);
			user = input.nextLine();
		}
		return user;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String enterPassword() {
		if(password==null || password.isEmpty()) {
			System.out.print("Password:");
			Scanner input = new Scanner(System.in);
			password = input.nextLine();
		}
		return password;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getFolder() {
		return folder;
	}
	public void setFolder(String folder) {
		this.folder = folder;
	}
}
