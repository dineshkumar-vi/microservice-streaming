package com.filestreaming.service;

import java.io.IOException;

public interface ProcessBytes {
	
	public int processBytes(int bytes,byte[] b) throws IOException;

}
