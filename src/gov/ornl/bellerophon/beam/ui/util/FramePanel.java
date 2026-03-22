/*******************************************************************************
 * This file is part of the Bellerophon client side application.
 * 
 * Filename: FramePanel.java
 * Author: Eric J. Lingerfelt
 * Author Contact: lingerfeltej@ornl.gov
 * Copyright (c) 2009 - 2013, Oak Ridge National Laboratory
 * All rights reserved.
 *******************************************************************************/
package gov.ornl.bellerophon.beam.ui.util;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import gov.ornl.bellerophon.beam.data.util.CustomFile;

/**
 * The Class FramePanel.
 *
 * @author Eric J. Lingerfelt
 */
public class FramePanel extends JPanel {

	private BufferedImage image;
	private JScrollPane pane;
	private boolean fitToWindow;
	private CustomFile file;
	
	public FramePanel(){
		setOpaque(false);
	}
	
	/**
	 * Sets the scroll pane.
	 *
	 * @param pane the new scroll pane
	 */
	public void setScrollPane(JScrollPane pane){
		this.pane = pane;
	}
	
	/**
	 * Gets the file.
	 *
	 * @return the file
	 */
	public CustomFile getFile(){
		return file;
	}
	
	/**
	 * Sets the file.
	 *
	 * @param file the new file
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void setFile(CustomFile file) throws IOException{
		this.file = file;
		if(file!=null && file.getContents()!=null){
			InputStream in = new ByteArrayInputStream(file.getContents());
			image = ImageIO.read(in);
			if(image != null){
				setSize(image.getWidth(null), image.getHeight(null));
			}else{
				setSize(500, 500);
			}
		}else{
			image = null;
			setSize(500, 500);
		}
		setPreferredSize(getSize());
		repaint();
	}
	
	/**
	 * Sets the fit to window.
	 *
	 * @param fitToWindow the new fit to window
	 */
	public void setFitToWindow(boolean fitToWindow){
		this.fitToWindow = fitToWindow;
		if(fitToWindow){
			pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			setSize(pane.getViewport().getExtentSize());
		}else{
			pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
			if(image!=null){
				setSize(image.getWidth(null), image.getHeight(null));
			}else{
				setSize(500, 500);
			}
		}
		setPreferredSize(getSize());
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		if(image!=null){
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			if(fitToWindow){
				double xscale = pane.getViewport().getExtentSize().getWidth()/image.getWidth();
				double yscale = pane.getViewport().getExtentSize().getHeight()/image.getHeight();
				double scale = Math.min(xscale, yscale);
				if(scale<1.0){
					g2.drawRenderedImage(image, AffineTransform.getScaleInstance(scale, scale));
				}else{
					g2.drawImage(image, 0, 0, null);
				}
			}else{
				g2.drawImage(image, 0, 0, null);
			}
		}else{
			if(file != null){
				g2.drawString("The url " + file.getPath() + " does not exist!", 20, 50);
			}
		}
	}
}
