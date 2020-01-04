/*
 * 
 * Copyright (c) 2019 - Paris Liu, paris0120@gmail.com
 */
package com.parisliu.SFTP_Sync;

/**
 * Class used to store the information of a downloaded file
 * 
 * @author Paris Liu
 *
 */
public class FileInfo {
	//private String url;
	private Long downloadTimeStamp = (long) 0;
	private Long size;
	private Integer permission;
	private Integer lastModifiedTimeStamp;
	private boolean readable = true;
	
 
	
 
	
	public boolean isReadable() {
		return readable;
	}
	public void setReadable(boolean readable) {
		this.readable = readable;
	}
	public Long getDownloadTimeStamp() {
		return downloadTimeStamp;
	}
	public void setDownloadTimeStamp(Long downloadTimeStamp) {
		this.downloadTimeStamp = downloadTimeStamp;
	}
	public Long getSize() {
		return size;
	}
	public void setSize(Long size) {
		this.size = size;
	}
	public Integer getLastModifiedTimeStamp() {
		return lastModifiedTimeStamp;
	}
	public void setLastModifiedTimeStamp(Integer lastModifiedTimeStamp) {
		this.lastModifiedTimeStamp = lastModifiedTimeStamp;
	}
	public Integer getPermission() {
		return permission;
	}
	public void setPermission(Integer permission) {
		this.permission = permission;
	}


	
	
	
}
