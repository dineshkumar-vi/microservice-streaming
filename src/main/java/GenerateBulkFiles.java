
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;



public class GenerateBulkFiles {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
		try {

			JsonFactory jfactory = new JsonFactory();
			

			/*** write to file ***/
			JsonGenerator jGenerator = jfactory.createJsonGenerator(
				new FileOutputStream(new File("")), JsonEncoding.UTF8);

			
			jGenerator.useDefaultPrettyPrinter();
			
			jGenerator.writeStartObject(); // {

			
//			jGenerator.writeNumberField("id", 12345);

	    		
			jGenerator.writeFieldName("accounts"); 
			jGenerator.writeStartArray(); // [

			for (int i = 0; i < 500000; i++) {
				
				jGenerator.writeStartObject(); // {
				
				
				jGenerator.writeStringField("accountId", "ID_"+(i+1));
				
				jGenerator.writeFieldName("addresses");
				jGenerator.writeStartArray(); // [
				jGenerator.writeStartObject(); // {
				jGenerator.writeStringField("type", "HOME");
				jGenerator.writeStringField("effectiveDate", "07/01/2010");
				jGenerator.writeStringField("addressLine1", "Test Line1");
				jGenerator.writeStringField("addressLine2", "Test Line2");
				jGenerator.writeStringField("addressLine3", "Test Line3");
				jGenerator.writeStringField("city", "Newyork");
				jGenerator.writeStringField("state", "CA");
				jGenerator.writeStringField("country", "US");
				jGenerator.writeNumberField("zipcode", 12345);
				jGenerator.writeStringField("nameOfRecord", ""+(10000 + i));
				jGenerator.writeStringField("comment", "Testing Data");
				
				
				jGenerator.writeEndObject(); // }
				jGenerator.writeEndArray(); // ]
				jGenerator.writeEndObject(); // }
			}

			jGenerator.writeEndArray(); // ]
			jGenerator.writeBooleanField("includeSuccessRecords", true);
			jGenerator.writeEndObject(); // }
			
			

			jGenerator.close();
			
			System.out.println(" Completed ");

		} catch (JsonGenerationException e) {

			e.printStackTrace();

		} catch (JsonMappingException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}

	}
		
}