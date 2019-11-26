 package com.filestreaming.serviceimpl;

import java.io.BufferedOutputStream;
import java.io.IOException;
 
public class StreamUpload{
	
	private final static byte[] BUFFER_SIZE = new byte[8192];

	private BufferedOutputStream bos;
	
	
	public StreamUpload(BufferedOutputStream bos) throws IOException {
		this.bos = bos;
    }


	public synchronized int processBytes(int bytes) throws IOException {
		bos.write(BUFFER_SIZE, 0, bytes);
		return bytes;
	}

}
