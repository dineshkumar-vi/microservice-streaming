package com.filestreaming.serviceimpl;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.filestreaming.service.ProcessBytes;

public class ValidateJson implements ProcessBytes {


	Gson gson = null;

	public ValidateJson() throws FileNotFoundException {
		this.gson = new GsonBuilder().create();
	}

	@Override
	public int processBytes(int bytes, byte[] b) throws IOException {
		
		
		try (InputStream targetStream = new ByteArrayInputStream(b);
				JsonReader reader = new JsonReader(new InputStreamReader(targetStream, "UTF-8"));) {
				reader.setLenient(true);
				reader.beginObject();
				while (reader.hasNext()) {
					String name = reader.nextName();
					if(name.equals("accounts")) {
						 reader.beginArray();
						 while (reader.hasNext()) {
							 reader.beginObject();
		                      if(reader.nextName().equals("accountId")) {
		                    	  	System.out.println(reader.nextString());
		                      }
		                      if(reader.nextName().equals("addresses")) {
		                    	  	reader.beginArray();
		                    	  	 while (reader.hasNext()) {
		                    	  		reader.beginObject();
		                    	  		 if(reader.nextName().equals("type")) {
		     	                    	  	System.out.println(reader.nextString());
		     	                      }
		                    	  		 if(reader.nextName().equals("effectiveDate")) {
		     	                    	  	System.out.println(reader.nextString());
		     	                      }
		                    	  		 if(reader.nextName().equals("addressLine1")) {
		     	                    	  	System.out.println(reader.nextString());
		     	                      }
		                    	  		 if(reader.nextName().equals("addressLine2")) {
		     	                    	  	System.out.println(reader.nextString());
		     	                      }
		                    	  		if(reader.nextName().equals("addressLine3")) {
		     	                    	  	System.out.println(reader.nextString());
		     	                     }
		                    	  		if(reader.nextName().equals("city")) {
		     	                    	  	System.out.println(reader.nextString());
		     	                     }
		                    	  		if(reader.nextName().equals("state")) {
		     	                    	  	System.out.println(reader.nextString());
		     	                     }
		                    	  		if(reader.nextName().equals("country")) {
		     	                    	  	System.out.println(reader.nextString());
		     	                     }
		                    	  		if(reader.nextName().equals("zipcode")) {
		     	                    	  	System.out.println(reader.nextString());
		     	                     }
		                    	  		if(reader.nextName().equals("nameOfRecord")) {
		     	                    	  	System.out.println(reader.nextString());
		     	                     }
		                    	  		if(reader.nextName().equals("comment")) {
		     	                    	  	System.out.println(reader.nextString());
		     	                     }
			                    	  	reader.endObject();
		                    	  	 }
		                    	  	reader.endArray();
		                      }
		                      reader.endObject();
		                  }
						 reader.endArray();
					}
					break;
				}
				reader.endObject();
			}


		
		
		return bytes;
	}

}
