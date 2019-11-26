package com.filestreaming.serviceimpl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;

import org.springframework.stereotype.Service;

@Service
public class ConainerUpload {
	
	private final static byte[] BUFFER_SIZE = new byte[8196];
	
	public void copyFile(final BufferedInputStream from, final BufferedOutputStream to) throws IOException {
		try {
			int read = 0;
			while ((read = from.read(BUFFER_SIZE)) != -1) {
				to.write(BUFFER_SIZE, 0, read);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
