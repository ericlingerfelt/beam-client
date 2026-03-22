/*******************************************************************************
 * This file is part of the Bellerophon Environment 
 * for Analysis of Materials client application.
 * 
 * Filename: BytesWrittenListener.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2015, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.io;


/**
 * The listener interface for receiving bytesWritten events.
 * The class that is interested in processing a bytesWritten
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addBytesWrittenListener<code> method. When
 * the bytesWritten event occurs, that object's appropriate
 * method is invoked.
 *
 * @see BytesWrittenEvent
 */
public interface BytesWrittenListener {
	
	/**
	 * Sets the bytes written.
	 *
	 * @param bytesWritten the new bytes written
	 */
	public void setBytesWritten(long bytesWritten);
}
