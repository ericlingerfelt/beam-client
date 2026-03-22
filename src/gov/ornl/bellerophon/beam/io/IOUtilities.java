/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: IOUtilities.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2015, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * The Class IOUtilities.
 *
 * @author Eric J. Lingerfelt
 */
public class IOUtilities {

	/**
	 * Read url.
	 *
	 * @param urlString the url string
	 * @param brl the brl
	 * @return the byte[]
	 * @throws MalformedURLException the malformed url exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws FileNotFoundException the file not found exception
	 */
	public static byte[] readURL(String urlString, BytesReadListener brl) throws MalformedURLException
																									, IOException
																									, FileNotFoundException{
		URL url = new URL(urlString);
		HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
		urlConnection.setDoOutput(true);
		InputStream inputStream = urlConnection.getInputStream();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		IOUtilities.readStream(inputStream, baos, brl);
		byte[] outputArray = baos.toByteArray();
		baos.close();
		inputStream.close();
		return outputArray;
	}
	
	public static void writeURLToFile(String urlString, File file) throws MalformedURLException
																			, IOException
																			, FileNotFoundException{
		IOUtilities.writeURLToFile(urlString, file, null);
	}
	
	public static void writeURLToFile(String urlString, File file, BytesReadListener brl) throws MalformedURLException
																								, IOException
																								, FileNotFoundException{
		URL url = new URL(urlString);
		HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
		urlConnection.setDoOutput(true);
		InputStream inputStream = urlConnection.getInputStream();
		FileOutputStream fos = new FileOutputStream(file);
		IOUtilities.readStream(inputStream, fos, brl);
		fos.close();
		inputStream.close();
	}
	
	/**
	 * Read url.
	 *
	 * @param urlString the url string
	 * @return the byte[]
	 * @throws MalformedURLException the malformed url exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static byte[] readURL(String urlString) throws MalformedURLException, IOException{
		return readURL(urlString, null);
	}
	
	/**
	 * Read file.
	 *
	 * @param file the file
	 * @return the byte[]
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static byte[] readFile(File file) throws IOException{
		int i = (int)file.length();
		byte[] buffer = new byte[i];
		FileInputStream fis= new FileInputStream(file);
		fis.read(buffer);
		fis.close();
		return buffer;
	}
	
	/**
	 * Write file.
	 *
	 * @param file the file
	 * @param array the array
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void writeFile(File file, byte[] array) throws IOException{
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(array);
		fos.close();
	}
	
	/**
	 * Write file.
	 *
	 * @param filename the filename
	 * @param array the array
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void writeFile(String filename, byte[] array) throws IOException{
		FileOutputStream fos = new FileOutputStream(new File(filename));
		fos.write(array);
		fos.close();
	}
	
	/**
	 * Read stream.
	 *
	 * @param in the in
	 * @param out the out
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void readStream(InputStream in, OutputStream out) throws IOException{
		readStream(in, out, null);
	}
	
	/**
	 * Read stream.
	 *
	 * @param in the in
	 * @param out the out
	 * @param brl the brl
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void readStream(InputStream in, OutputStream out, BytesReadListener brl) throws IOException{
		long totalBytesRead = 0L;
		synchronized(in){
			synchronized(out){
				byte[] buffer = new byte[256];
				while(true){
					int bytesRead = in.read(buffer);
					if(bytesRead==-1){break;}
					if(brl!=null){
						totalBytesRead += (long)bytesRead;
						brl.setBytesRead(totalBytesRead);
					}
					out.write(buffer, 0, bytesRead);
				}
			}
		}
	}
}
