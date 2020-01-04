package com.parisliu.SFTP_Sync;

import java.io.File; 
import java.io.IOException; 
import java.util.HashMap;
import java.util.Vector;
 

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.ChannelSftp.LsEntry; 

public class SFTP {
	Configuration config;
	private ObjectMapper objectMapper = new ObjectMapper();
	private File configFile;

	private Session session;
	private ChannelSftp sftpChannel; 
	private JSch jsch = new JSch();

	private boolean savePassword = true; //whether save password in config file
	private File path;
	
	public SFTP (File path) throws JsonParseException, JsonMappingException, IOException {
		this.path = path;
		
		if(!path.exists()) {
			path.mkdirs();
		}

		
		configFile = new File(path.getAbsolutePath() + File.separator + "config.cfg");
		if(configFile.exists()) {
			try {
				config = objectMapper.readValue(configFile, Configuration.class);
			}
			catch (com.fasterxml.jackson.databind.exc.MismatchedInputException | com.fasterxml.jackson.core.io.JsonEOFException e) {
				config = new Configuration();	
				config.setNewTimestamp();
			}
		}
		else {
			config = new Configuration();
			config.setNewTimestamp();
		}		 
		
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
	        public void run() {
        		try {
					disconnect();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            System.out.println("Bye!");
	        }
	    }, "Shutdown-thread"));
	}

	 
	public void startNewTask() {
		config.setNewTimestamp();
	}
	public void setServer(String server) {
		config.setServer(server);
	}
	public void setUser(String user) {
		config.setUser(user);
	}
	public void setPassword(String password) {
		config.setPassword(password);
	} 
	 
	public void disconnect() throws JsonGenerationException, JsonMappingException, IOException {
    	if(sftpChannel!=null) {
    		sftpChannel.exit();
    	}
    	if(session!=null) {
    		session.disconnect();
    	}
		saveConfig();
	}

    public boolean connect() throws JSchException { 
    	String server = config.getServer(); 
//    	String pass = config.getPassword();
    	java.util.Properties properties = new java.util.Properties(); 
    	properties.put("StrictHostKeyChecking", "no");

    	
    	while(true) {
        	String user = config.enterUser();
        	if(user.isEmpty()) {
        		return false;
        	} 

    		String pass = config.enterPassword();
        	if(pass.isEmpty()) {
        		return false;
        	}
        	session = jsch.getSession(user, server); 
        	session.setConfig(properties); 
        	session.setPassword(pass); 

        	
        	
        	
        	try {
        		session.connect();
        		break;
        	}
        	catch (com.jcraft.jsch.JSchException e) { //wrong password   
        		System.out.println(e.getMessage());
    			switch(e.getMessage()) {
    			case "connection is closed by foreign host":
    				System.out.println("Server is not online.");
    				System.exit(0);
    				default:
    	    			System.out.println("Wrong user name or password. Try again.");
    	    			config.setPassword(null);
    	    			config.setUser(null); 
    			}
        	}
        	
        	
    	}

    	Channel channel = session.openChannel( "sftp" );
    	channel.connect();
    	sftpChannel = (ChannelSftp) channel;
    	return true;
    }

    
    
	private void saveConfig() throws JsonGenerationException, JsonMappingException, IOException {
		if(!isSavePassword()) {
			config.setPassword(null);
		} 
		objectMapper.writeValue(configFile, config);
	}
	
	

	public boolean isSavePassword() {
		return savePassword;
	}
	
	public void setSavePassword(boolean savePassword) {
		this.savePassword = savePassword;
	}
	
	
	public boolean checkFolder(String folder) {
		File dir = new File(folder);
        if(!dir.exists()) {
        	dir.mkdirs();
        }
        return dir.exists();
	}

	/**
	 * load file information for the folder
	 * @param folder	folder whose information to be returned
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public HashMap<String, FileInfo> loadInfoFile(File folder) throws JsonParseException, JsonMappingException, IOException {
		File configFile = new File(folder.getAbsolutePath() + File.separator + ".info");
		if(configFile.exists()) {
			try {
				return objectMapper.readValue(configFile, new TypeReference<HashMap<String, FileInfo>>(){}); 
			}
			catch (com.fasterxml.jackson.core.io.JsonEOFException e) {
				return new HashMap<String, FileInfo>();
			}
		}
		else {
			return new HashMap<String, FileInfo>();
		}
	}
	
	/**
	 * 
	 * @param data	file data to save
	 * @param folder	folder whose information to be saved
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public void saveInfoFile(HashMap<String, FileInfo> data, File folder) throws JsonGenerationException, JsonMappingException, IOException {
		File configFile = new File(folder.getAbsolutePath() + File.separator + ".info");
		objectMapper.writeValue(configFile, data);
	}
	
	public void download() throws JsonParseException, JsonMappingException, SftpException, IOException {
		downloadFolder("/" + config.getFolder(),config.getFolder());
	}
	
	private boolean isReadable(int permissions) {
	    return ((permissions & 0400) > 0);
	}
	
    private void downloadFolder(String url, String folder) throws SftpException, JsonParseException, JsonMappingException, IOException  {
//    	System.out.println(url);
        String destPath = path.getAbsolutePath() + File.separator + folder;
        HashMap<String, FileInfo> fileInfo = loadInfoFile(new File(destPath));
        System.out.println(url);
        Vector<LsEntry> filelist = sftpChannel.ls(url); 
        
        
        if(checkFolder(destPath)) {
        	for(LsEntry file:filelist) { 
        		String fileUrl = url + "/" + file.getFilename();
    			FileInfo info = fileInfo.get(fileUrl);
    			if(info==null) {
    				info = new FileInfo();
    			}
    			info.setPermission(file.getAttrs().getPermissions());
    			
        		if(isReadable(info.getPermission())) { //has permission 
        			if(file.getAttrs().isDir()) { //download folder
                		if(file.getFilename().replaceAll("\\.", "").length()>0) { //not . or ..
                			//String folderUrl = url + "/" + file.getFilename();
                			if(!info.getDownloadTimeStamp().equals(config.getTimestamp()) && info.isReadable()) {
                    			try {                    			
                        			downloadFolder(fileUrl, folder + File.separator + file.getFilename());
                        			info.setReadable(true);
                    			} catch (SftpException e) { 
                        			info.setReadable(false);
                				}  
                				
                			}
                		}
                	}
                	else { //download file
                		File output = new File(destPath + "/" + file.getFilename());
                		File tmp = new File(destPath + "/" + file.getFilename() + ".tmp");
                		while(!info.getDownloadTimeStamp().equals(config.getTimestamp())) {
                    		if(!session.isConnected()) {
                    			System.out.println("Reconnect: " + output.getAbsolutePath());
                    			try {
        							connect();
        						} catch (JSchException e) {
        							// TODO Auto-generated catch block
        							e.printStackTrace();
        						}
                    		}
//                    		String fileUrl = url + "/" + file.getFilename();
//                    		FileInfo info = fileInfo.get(fileUrl);
                    		if(info.getDownloadTimeStamp() == 0) {// not in the database, for corrupted info file
                    			if(output.exists()) { //local file exsit
                    				if(file.getAttrs().getSize()== output.length()) {//same file size
                    					info.setDownloadTimeStamp(config.getTimestamp());
                    				}
                    			}
                    		}
                    		if(info.getDownloadTimeStamp()==0 || !info.getDownloadTimeStamp().equals(config.getTimestamp())) {
                    			if(info.getDownloadTimeStamp()==0 || !info.getLastModifiedTimeStamp().equals(file.getAttrs().getMTime())) {
                    				if(info.isReadable()) {
                    					System.out.println("Downloading: " + output.getAbsolutePath());
                                		try { 
                    						sftpChannel.get(fileUrl, tmp.getAbsolutePath(),new DownloadProgressMonitor(),ChannelSftp.RESUME);
                    						info.setReadable(true);
                    						if(output.exists()) {
                    	        				output.delete();
                    	        			}
                    						tmp.renameTo(output); 
                							info.setDownloadTimeStamp(config.getTimestamp());
                    					} catch (SftpException e) {
                    						if(!tmp.exists()) {
                    							System.out.println("Denied: " + output.getAbsolutePath());
                    							info.setDownloadTimeStamp(config.getTimestamp());
                    							info.setReadable(false);
//                    							break;
                    						}
                    					}  
                    				}
                        			
                     			}           				
                    		}
                		}	
        		}//end of process a file


//        			info.setLastModifiedTimeStamp(file.getAttrs().getMTime());
//    				//info.setPermission(file.getAttrs().getPermissions());
//    				info.setSize(file.getAttrs().getSize());
//        			info.setDownloadTimeStamp(config.getTimestamp());
//        			fileInfo.put(fileUrl, info); 

    			info.setLastModifiedTimeStamp(file.getAttrs().getMTime());
				//info.setPermission(file.getAttrs().getPermissions());
				info.setSize(file.getAttrs().getSize()); 
    			info.setDownloadTimeStamp(config.getTimestamp()); 
    			fileInfo.put(fileUrl, info);
        		
            	
            	} 
                saveInfoFile(fileInfo, new File(destPath));
            }
        }
        
    }

	public void setFolder(String folder) {
		config.setFolder(folder);
	}


	public void printInfo() {
		System.out.println("Server: " + config.getServer());
		System.out.println("User: " + config.getUser());
		System.out.println("Folder: " + config.getFolder());
		System.out.println("Backup timestamp: " + config.getTimestamp());
		System.out.println("Backup path: " + path.getAbsolutePath());
		
	}
}
