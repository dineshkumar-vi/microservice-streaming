package com.filestreaming.controllor;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.Date;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.cloud.ReadChannel;
import com.google.gson.stream.JsonReader;
import com.filestreaming.serviceimpl.ConainerUpload;
import com.filestreaming.serviceimpl.GcloudUpload;

@RestController
public class FileDownloadStreaming {

	private static final Logger logger = Logger.getLogger(FileDownloadStreaming.class.getName());

	private static int DEFAULT_BUFFER_SIZE = 8192;

	private final static byte[] STREAM_BUFFER_SIZE = new byte[8192];

	@GetMapping("/checkserver")
	public String checkServer(@RequestParam(value = "name", defaultValue = "World") String name) {
		System.out.println("Name " + name);
		logger.info("FileDownloadStreaming checkServer started " + new Date());
		return "ok";
	}

	/**
	 * Postman streaming / Apigee streaming / Service Streaming - File attachment
	 * External upload storing the data into gcloud storage i.e SFTP server
	 * Streaming size : 8 KB Payload tested upto: 4 GB
	 * 
	 * @param request
	 * @return
	 * @throws FileUploadException
	 * @throws IOException
	 */
	@PostMapping("/externalUpload")
	public String externalUpload(HttpServletRequest request) throws FileUploadException, IOException {

		logger.info("FileDownloadStreaming externalUpload started " + new Date());
		try {

			ServletFileUpload upload = new ServletFileUpload();
			FileItemIterator iterStream = upload.getItemIterator(request);
			GcloudUpload gcloudUpload = new GcloudUpload();
			while (iterStream.hasNext()) {
				FileItemStream item = iterStream.next();
				if (!item.isFormField()) {
					logger.info("FileDownloadStreaming externalUpload Starting Time " + new Date());
					String fileName = "externalUpload.json";
					try (InputStream stream = item.openStream();
							BufferedInputStream reader = new BufferedInputStream(stream, DEFAULT_BUFFER_SIZE);) {
						gcloudUpload.uploadByteArray(reader, fileName);
						logger.info("externalUpload Ending Time " + new Date());
					}
				} else {
					try (InputStream stream = item.openStream();) {
						logger.info("Form Data ========== > " + item.getFieldName() + "==== Value ==>"
								+ Streams.asString(stream));
					}
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.info(e.getMessage());
			return null;
		}

		return "ok";
	}

	/**
	 * Postman streaming / Apigee streaming / Service Streaming - File attachment
	 * Internal upload storing the data into the temp folder inside of container
	 * Streaming size : 8 KB Payload tested upto: 4 GB
	 * 
	 * @param request
	 * @return
	 * @throws FileUploadException
	 * @throws IOException
	 */
	@PostMapping("/containerUpload")
	public String containerUpload(HttpServletRequest request) throws FileUploadException, IOException {

		logger.info("FileDownloadStreaming uploadstreaming started " + new Date());
		try {
			ConainerUpload conainerUpload = new ConainerUpload();
			ServletFileUpload upload = new ServletFileUpload();
			FileItemIterator iterStream = upload.getItemIterator(request);
			while (iterStream.hasNext()) {
				FileItemStream item = iterStream.next();
				logger.info("FileDownloadStreaming uploadStreaming Starting Time " + new Date());
				try (InputStream stream = item.openStream();
						BufferedInputStream reader = new BufferedInputStream(stream, DEFAULT_BUFFER_SIZE);
						FileOutputStream fos = new FileOutputStream("/tmp/containerUpload.json");
						BufferedOutputStream bos = new BufferedOutputStream(fos);

				) {
					conainerUpload.copyFile(reader, bos);
					logger.info("Ending Time " + new Date());
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.info(e.getMessage());
			return null;
		}

		return "ok";
	}

	/**
	 * Consumer streaming / Apigee streaming / Service Streaming - JSON body
	 * External upload storing the data into gcloud storage i.e SFTP server
	 * Streaming size : 8 KB Payload tested upto: 4 GB
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@PostMapping(path = "/streamUpload", consumes = "application/json")
	public String streamingJson(HttpServletRequest request) throws Exception {

		logger.info("FileDownloadStreaming streamingJson started " + new Date());
		String fileName = "streamUpload.json";
		GcloudUpload gcloudUpload = new GcloudUpload();
		try (BufferedInputStream bufferedInputStream = new BufferedInputStream(request.getInputStream(),
				DEFAULT_BUFFER_SIZE)) {
			gcloudUpload.uploadByteArray(bufferedInputStream, fileName);
			logger.info("FileDownloadStreaming streamingJson ended " + new Date());
		}
		return "ok";
	}

	/**
	 * Consumer payload / Apigee streaming / Service process full payload - JSON
	 * body External upload storing the data into gcloud storage i.e SFTP server
	 * Streaming size : 8 KB Payload tested upto: 400 MB
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@PostMapping(path = "/postBodyUpload", consumes = "application/json")
	public String saveJson(@RequestBody String requestBody) throws Exception {
		logger.info("FileDownloadStreaming saveJson started " + new Date());
		String fileName = "postBodyUpload.json";
		GcloudUpload gcloudUpload = new GcloudUpload();
		gcloudUpload.uploadByteArray(requestBody.getBytes(), fileName);
		logger.info("FileDownloadStreaming saveJson ended " + new Date());
		return "ok";
	}

	/**
	 * File from : Gcloud storage - File download as a stream Streaming size : 2 MB
	 * 
	 * @param request
	 * @param response
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/download/{fileName}")
	public String download(HttpServletRequest request, HttpServletResponse response,
			@PathVariable("fileName") String fileName) throws Exception {
		logger.info("FileDownloadStreaming download started " + new Date());

		GcloudUpload gcloud = new GcloudUpload();

		System.out.println(fileName);

		response.setContentType("application/json");
		response.addHeader("Content-Disposition", "attachment; filename=" + fileName);

		OutputStream os = response.getOutputStream();
		WritableByteChannel outChannel = Channels.newChannel(os);
		ReadChannel reader = gcloud.download(fileName);
		ByteBuffer bytes = ByteBuffer.allocate(126 * 1024);
		while (reader.read(bytes) > 0) {
			bytes.flip();
			outChannel.write(bytes);
			bytes.clear();
		}

		reader.close();
		os.close();

		logger.info("FileDownloadStreaming download ended " + new Date());
		return "ok";
	}

	@PostMapping(path = "/uploadAndValidate", consumes = "application/json")
	public String uploadAndValidate(HttpServletRequest request) throws Exception {
		logger.info("FileDownloadStreaming saveJson started " + new Date());

		try (FileOutputStream fos = new FileOutputStream("/tmp/uploadAndValidate.json");
				BufferedOutputStream bos = new BufferedOutputStream(fos);

				final PipedInputStream pipedInputStream = new PipedInputStream();
				final PipedOutputStream pipedOutputStream = new PipedOutputStream();

				BufferedInputStream bufferedInputStream = new BufferedInputStream(request.getInputStream(),
						DEFAULT_BUFFER_SIZE);) {
			pipedInputStream.connect(pipedOutputStream);

			Thread pipeWriter = new Thread(new Runnable() {
				@Override
				public void run() {
					int read = 0;
					try {
						System.out.println("Before Read the stream : " + bufferedInputStream.available());
						while ((read = bufferedInputStream.read(STREAM_BUFFER_SIZE)) != -1) {
							pipedOutputStream.write(STREAM_BUFFER_SIZE, 0, read);
							bos.write(STREAM_BUFFER_SIZE, 0, read);
							System.out.println(" Data avilable in Read " + read);
							// Thread.sleep(100);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});

			/* Thread for reading data from pipe */
			Thread pipeReader = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						InputStream targetStream = new BufferedInputStream(pipedInputStream, DEFAULT_BUFFER_SIZE);
						JsonReader reader = new JsonReader(new InputStreamReader(targetStream, "UTF-8"));
						System.out.println(reader);
						reader.setLenient(true);
						reader.beginObject();
						while (reader.hasNext()) {
							String name = reader.nextName();
							if (name.equals("accounts")) {
								reader.beginArray();
								while (reader.hasNext()) {
									reader.beginObject();
									if (reader.nextName().equals("accountId")) {
										System.out.println("Json Type Data : " + reader.nextString());

									}
									if (reader.nextName().equals("addresses")) {
										reader.beginArray();
										while (reader.hasNext()) {
											reader.beginObject();
											if (reader.nextName().equals("type")) {
												reader.nextString();

											}
											if (reader.nextName().equals("effectiveDate")) {
												reader.nextString();
											}
											if (reader.nextName().equals("addressLine1")) {
												reader.nextString();
											}
											if (reader.nextName().equals("addressLine2")) {
												reader.nextString();
											}
											if (reader.nextName().equals("addressLine3")) {
												reader.nextString();
											}
											if (reader.nextName().equals("city")) {
												reader.nextString();
											}
											if (reader.nextName().equals("state")) {
												reader.nextString();
											}
											if (reader.nextName().equals("country")) {
												reader.nextString();
											}
											if (reader.nextName().equals("zipcode")) {
												reader.nextString();
											}
											if (reader.nextName().equals("nameOfRecord")) {
												reader.nextString();
											}
											if (reader.nextName().equals("comment")) {
												reader.nextString();
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
						reader.nextName();
						reader.nextBoolean();
						reader.endObject();
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			});

			/* Start thread */
			pipeWriter.start();
			pipeReader.start();

			/* Join Thread */
			pipeWriter.join();
			pipeReader.join();
		}

		logger.info("FileDownloadStreaming saveJson ended " + new Date());
		return "ok";
	}

}