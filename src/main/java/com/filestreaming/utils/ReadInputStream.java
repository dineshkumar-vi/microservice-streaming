package com.filestreaming.utils;

import java.io.IOException;
import java.io.InputStream;

public class ReadInputStream extends InputStream {
	
	private final InputStream _copyStream;
	
	public ReadInputStream(InputStream incomingStream) {
        if (!incomingStream.markSupported()) {
            throw new IllegalArgumentException("marking not supported");
        }
       // incomingStream.mark(Integer.MAX_VALUE);
        _copyStream = incomingStream;
    }

	@Override
    public void close() throws IOException {
		//_copyStream.reset();
    }

    @Override
    public int read() throws IOException {
        return _copyStream.read();
    }

}


