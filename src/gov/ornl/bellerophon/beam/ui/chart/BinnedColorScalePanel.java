package gov.ornl.bellerophon.beam.ui.chart;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.*;

public class BinnedColorScalePanel extends JPanel{

	private ArrayList<Double> binValueList;
	private ArrayList<Color> binColorList;
	private int binHeight, binWidth, yOffset, chartMarginTop, chartMarginBottom;
	private int scaleLabelDistance = 5;
	private DecimalFormat df;
	
	public BinnedColorScalePanel(){
		setOpaque(false);
	}
	
	public void setCurrentState(ArrayList<Double> binValueList, ArrayList<Color> binColorList, DecimalFormat df, int binWidth, int totalHeight, int chartMarginTop, int chartMarginBottom){
		this.binValueList = binValueList;
		this.binColorList = binColorList;
		this.df = df;
		this.binWidth = binWidth;
		this.chartMarginTop = chartMarginTop;
		this.chartMarginBottom = chartMarginBottom;
		totalHeight -= (chartMarginTop + chartMarginBottom);
		this.binHeight = (int) ((double)totalHeight/(double) binColorList.size());
		if(binHeight*binColorList.size() < 1.01*totalHeight && binHeight*binColorList.size() > 0.99*totalHeight){
			binHeight-=1;
		}
		if(binHeight*binColorList.size() < totalHeight){
			this.yOffset = (int) ((totalHeight-(binHeight*binColorList.size()))/2.0);
		}
		this.yOffset += chartMarginTop;
		setPreferredSize(new Dimension(binWidth+80, binColorList.size()*binHeight+2*yOffset));
		repaint();
	}
	
	public void setTotalHeight(int totalHeight){
		totalHeight -= (chartMarginTop + chartMarginBottom);
		this.binHeight = (int) ((double)totalHeight/(double) binColorList.size());
		if(binHeight*binColorList.size() < 1.01*totalHeight && binHeight*binColorList.size() > 0.99*totalHeight){
			binHeight-=1;
		}
		if(binHeight*binColorList.size() < totalHeight){
			this.yOffset = (int) ((totalHeight-(binHeight*binColorList.size()))/2.0);
		}
		this.yOffset += chartMarginTop;
		setPreferredSize(new Dimension(binWidth+50, binColorList.size()*binHeight+2*yOffset));
		repaint();
	}

	public void paintComponent(Graphics g){
		
		Graphics2D g2 = (Graphics2D) g;
        super.paintComponent(g2); 

        RenderingHints hintsText = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING
														, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		RenderingHints hintsShape = new RenderingHints(RenderingHints.KEY_ANTIALIASING
														, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHints(hintsText);
		g2.setRenderingHints(hintsShape);

		if(binColorList!=null){
		
	        g2.setColor(Color.BLACK);
	        g2.fillRect(0, yOffset, binWidth, binHeight*binColorList.size()+2);
	        
	        int counter = binHeight;
	        for(Color c: binColorList){
	        	g2.setColor(c);
	        	g2.fillRect(1, (binColorList.size()*binHeight)-counter+1+yOffset, binWidth-2, binHeight);
	        	counter+=binHeight;
	        }
	        
	        FontMetrics fm = g2.getFontMetrics(g2.getFont());
	        g2.setColor(Color.BLACK);
	        
	        if(binValueList.size()>20){

		        for(int i=0; i<binValueList.size(); i+=10){
		        	g2.drawString(df.format(binValueList.get(binValueList.size()-1-i)), binWidth+scaleLabelDistance, i*binHeight+(fm.getAscent()/2)+yOffset);
		        	if(i!=0 && i!=binValueList.size()-1){
		        		g2.drawLine(0, i*binHeight+1+yOffset, binWidth-1, i*binHeight+1+yOffset);
		        	}
		        }
		        
	        }else{
	        	
	        	for(int i=0; i<binValueList.size(); i++){
	        		if(i!=binValueList.size()-1){
	        			g2.drawString(df.format(binValueList.get(binValueList.size()-1-i)), binWidth+scaleLabelDistance, (int) (i*binHeight+(fm.getAscent()/2)+yOffset+0.5*binHeight));
	        		}
		        	if(i!=0 && i!=binValueList.size()-1){
		        		g2.drawLine(0, i*binHeight+1+yOffset, binWidth-1, i*binHeight+1+yOffset);
		        	}
		        }
	        	
	        }
	        
		}

	}
	
}
