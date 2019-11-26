package com.filestreaming.serviceimpl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.core.io.ClassPathResource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.ReadChannel;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;


public class GcloudUpload {

	private static final String BUCKET_NAME = "streamingfile";
	
	private static Storage storage = null;
	
	public GcloudUpload() throws IOException {
		InputStream stream = new ClassPathResource("gcloudkey.json").getInputStream();
		GoogleCredentials credentials = GoogleCredentials.fromStream(stream);
		storage =  StorageOptions.newBuilder().setCredentials(credentials).build().getService();
	}

	
	/**
	 * Upload the stream
	 * @param filePart
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("deprecation")
	public String uploadByteArray(BufferedInputStream filePart, String fileName) throws IOException {		
		return storage.create(BlobInfo.newBuilder(BUCKET_NAME, fileName).build(), filePart).getMediaLink();
	}
	
	/**
	 * Upload the byte array
	 * @param filePart
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public String uploadByteArray(byte[] content, String fileName) throws IOException {			
		return storage.create(BlobInfo.newBuilder(BUCKET_NAME, fileName).build(), content).getMediaLink();
	}
	
	/**
	 * Download the file from storage
	 * @param fileName
	 * @return
	 */
	public ReadChannel download(String fileName) {
		return storage.get(BUCKET_NAME, fileName).reader();
	}

}
