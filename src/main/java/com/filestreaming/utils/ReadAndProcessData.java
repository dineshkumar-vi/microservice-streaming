package com.filestreaming.utils;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.filestreaming.service.ProcessBytes;
import com.filestreaming.serviceimpl.StreamUpload;
import com.filestreaming.serviceimpl.ValidateJson;


public class ReadAndProcessData extends FilterInputStream {
	
	private List<ProcessBytes> listeners = new ArrayList<ProcessBytes>();
	

	public ReadAndProcessData(InputStream in, StreamUpload processBytes1, ValidateJson processBytes2) {
		super(in);
		listeners.add(processBytes2);
	}
	
	
	@Override
	public int read(byte b[]) throws IOException {
		int readByte =  super.read(b, 0, b.length);
		
		if(readByte != -1) {
			for (ProcessBytes processBytes : listeners) {
	          	processBytes.processBytes(readByte, b);
	        }
		}
		return readByte;
	 }

}
