/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: DataFileTransferUtilities.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2015, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.io;

import gov.ornl.bellerophon.beam.data.MainData;
import gov.ornl.bellerophon.beam.data.util.DataFile;
import gov.ornl.bellerophon.beam.enums.FileTransferMethod;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class DataFileTransferUtilities{
	
	public static void transferTempFile(DataFile datafile, FileTransferMethod method, BytesReadListener brl, InputStream knownHostsInputStream) throws JSchException, IOException{
		
		//If using SCP, then transfer the file to the server and disconnect
		if(method==FileTransferMethod.SCP){
		
			//Create a new JSch
			JSch jsch = new JSch();	
			
			//Set the private key
			jsch.addIdentity("tempKeyPair", MainData.getUser().getScpPrivateKey().replace("\\n", "\n").getBytes(), null, null);
	
			//Set the known hosts file dynamically
			jsch.setKnownHosts(knownHostsInputStream);
			
			//Create a session and connect
			Session session = jsch.getSession("beam_scp", MainData.SERVER_DOMAIN_NAME, 22);
			session.connect();
	 
			String command = "scp -t /data/tmp/" + datafile.getTempName() + ".h5";
			Channel channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);
	
			OutputStream out = channel.getOutputStream();
			InputStream in = channel.getInputStream();
			channel.connect(); 
			
			in.read();
	
			File file = datafile.getFile();
			
			long filesize=file.length();
			command = "C0666 " + filesize + " " + datafile.getTempName() + ".h5" + "\n";
			out.write(command.getBytes());
			out.flush();
			
			in.read();
			
			FileInputStream fis=new FileInputStream(file);
			IOUtilities.readStream(fis, out, brl);
			fis.close();
			fis=null;
	
			// send '\0'
			byte[] buf=new byte[1024];
			buf[0] = 0; 
			out.write(buf, 0, 1); 
			out.flush();
			
			in.read();
		
			out.close();
	
			channel.disconnect();
			session.disconnect();
			
		}else if(method==FileTransferMethod.HTTPS){
			
			
			
		}

	}
	
}
