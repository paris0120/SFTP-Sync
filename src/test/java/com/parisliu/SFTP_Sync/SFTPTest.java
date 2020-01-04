package com.parisliu.SFTP_Sync;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.parisliu.SFTP_Sync.FileInfo;
import com.parisliu.SFTP_Sync.SFTP;

public class SFTPTest {
	@Test
    public void testHelloWorld() {
		HashMap<String, FileInfo> data = new HashMap<String, FileInfo>();
		FileInfo file = new FileInfo();
		File testFile = new File("");
		
		data.put("testUrl", file);
		
		SFTP test;
		try {
			test = new SFTP(new File(""));

			test.saveInfoFile(data, testFile);
			HashMap<String, FileInfo> testData = test.loadInfoFile(testFile);
			assertNotNull(testData.get("testUrl"));
			assertEquals(testData.get("testUrl").getDownloadTimeStamp(), data.get("testUrl").getDownloadTimeStamp());
			assertEquals(testData.get("testUrl").getLastModifiedTimeStamp(), data.get("testUrl").getLastModifiedTimeStamp());
			assertEquals(testData.get("testUrl").getPermission(), data.get("testUrl").getPermission());
			assertEquals(testData.get("testUrl").getSize(), data.get("testUrl").getSize());
			if(testFile.exists()) {
				testFile.delete();
			}
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
    }
}
