package gov.ornl.bellerophon.beam.ui.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import gov.ornl.bellerophon.beam.data.util.*;
import gov.ornl.bellerophon.beam.ui.format.Colors;

import javax.swing.*;

public class GridPointChartPanel extends JPanel implements MouseListener, MouseMotionListener{
	
	private GridPointCellPanelSelectionListener gridPointCellSelectionListener;
	private GridPointCellPanelMouseListener gridPointCellMouseListener;
	private int gridWidth, gridHeight, squareSize, 
					marginLeft, marginRight, marginTop, marginBottom, 
					xMax, yMax;
	private Color[][] colorArray;
	private GridPoint selectedGridPoint, mouseOverGridPoint;
	private boolean showMouseOverGridHighlight, showSelectedGridHighlight, showCrossHairs;
	private String xTitle, yTitle;
	private ArrayList<String> xTickLabelList, yTickLabelList;
	private boolean showAxis = true;
	
	public enum GridPointChartType {GRID_CELL_MAP, HISTROGRAM}
	private GridPointChartType gridPointChartType;
	
	public GridPointChartPanel(GridPointChartType gridPointChartType){
		this.gridPointChartType = gridPointChartType;
		showMouseOverGridHighlight = true;
		showSelectedGridHighlight = true;
		showCrossHairs = true;
		addMouseListener(this);
		addMouseMotionListener(this);
		setOpaque(false);
	}
	
	public int getMarginTop(){return marginTop;}
	public int getMarginBottom(){return marginBottom;}
	public int getSquareSize(){return squareSize;}
	public int getGridWidth(){return gridWidth;}
	public int getGridHeight(){return gridHeight;}
	
	public void setShowAxis(boolean showAxis){
		this.showAxis = showAxis;
		repaint();
	}
	
	public void setSelectedGridPoint(GridPoint selectedGridPoint){
		this.selectedGridPoint = selectedGridPoint;
		repaint();
	}
	
	public void showCrossHairs(boolean showCrossHairs){
		this.showCrossHairs = showCrossHairs;
		repaint();
	}
	
	public void setMarginLeft(int margin){
		this.marginLeft = margin;
		repaint();
	}
	
	public void setMarginRight(int margin){
		this.marginRight = margin;
		repaint();
	}
	
	public void setMarginTop(int margin){
		this.marginTop = margin;
		repaint();
	}
	
	public void setMarginBottom(int margin){
		this.marginBottom = margin;
		repaint();
	}
	
	public void setXTickLabelList(ArrayList<String> xTickLabelList){
		this.xTickLabelList = xTickLabelList;
		repaint();
	}
	
	public void setYTickLabelList(ArrayList<String> yTickLabelList){
		this.yTickLabelList = yTickLabelList;
		repaint();
	}
	
	public void setXTitle(String xTitle){
		this.xTitle = xTitle;
		repaint();
	}
	
	public void setYTitle(String yTitle){
		this.yTitle = yTitle;
		repaint();
	}
	
	public void showMouseOverGridHighlight(boolean showMouseOverGridHighlight){
		this.showMouseOverGridHighlight = showMouseOverGridHighlight;
		repaint();
	}
	
	public void showSelectedGridHighlight(boolean showSelectedGridHighlight){
		this.showSelectedGridHighlight = showSelectedGridHighlight;
		repaint();
	}
	
	public void setColorArray(Color[][] colorArray){
		this.colorArray = colorArray;
		repaint();
	}
	
	public void setGridPointCellSelectionListener(GridPointCellPanelSelectionListener gridPointCellSelectionListener) {
		this.gridPointCellSelectionListener = gridPointCellSelectionListener;
	}
	
	public void setGridPointCellMouseListener(GridPointCellPanelMouseListener gridPointCellMouseListener) {
		this.gridPointCellMouseListener = gridPointCellMouseListener;
	}
	
	public Dimension getPreferredSize(){
		return new Dimension(marginLeft+marginRight+gridWidth*squareSize+1, marginTop+marginBottom+gridHeight*squareSize+1);
	}
	
	public void setGridWidth(int gridWidth){
		this.gridWidth = gridWidth;
		xMax = marginLeft+gridWidth*squareSize-1;
		repaint();
	}
	
	public void setGridHeight(int gridHeight){
		this.gridHeight = gridHeight;
		yMax = marginTop+gridHeight*squareSize-1;
		repaint();
	}
	
	public void setSquareSize(int squareSize){
		this.squareSize = squareSize;
		xMax = marginLeft+gridWidth*squareSize-1;
		yMax = marginTop+gridHeight*squareSize-1;
		repaint();
	}
	
	private GridPoint getGridPoint(int x, int y){
		if(x < marginLeft || x > xMax || y < marginTop || y > yMax){
			return null;
		}
		
		int gridX = (int) ((x-marginLeft+squareSize)/(double)squareSize);
		int gridY = 0;
		
		switch(gridPointChartType){
			case HISTROGRAM:
				gridY = (int) ((-y+marginTop+(gridHeight*squareSize))/(double)squareSize)+1;
				break;
			case GRID_CELL_MAP:
				gridY = (int) ((y-marginTop+squareSize)/(double)squareSize);
				break;
		}
		
		GridPoint gp = new GridPoint();
		gp.setX(gridX);
		gp.setY(gridY);
		return gp;
	}

	public void mouseMoved(MouseEvent me) {
		GridPoint gp = getGridPoint(me.getX(), me.getY());
		mouseOverGridPoint = gp;
		if(gridPointCellMouseListener!=null){
			gridPointCellMouseListener.gridPointCellPanelMouseOvered(mouseOverGridPoint);
		}
		repaint();
	}
	
	public void mouseClicked(MouseEvent me){
		GridPoint gp = getGridPoint(me.getX(), me.getY());
		if(gp!=null){
			if(selectedGridPoint==null){
				selectedGridPoint = gp;
			}else if(selectedGridPoint.equals(gp)){
				selectedGridPoint = null;
			}else{
				selectedGridPoint = gp;
			}
		}
		if(gridPointCellSelectionListener!=null){
			gridPointCellSelectionListener.gridPointCellPanelSelected(selectedGridPoint);
		}
		repaint();
	}
	
	public void mousePressed(MouseEvent me){}
	public void mouseReleased(MouseEvent me){}
	public void mouseEntered(MouseEvent me){}
	public void mouseExited(MouseEvent me){
		mouseOverGridPoint = null;
		if(gridPointCellMouseListener!=null){
			gridPointCellMouseListener.gridPointCellPanelMouseOvered(mouseOverGridPoint);
		}
		repaint();
	}
	public void mouseDragged(MouseEvent me) {}

	public void paintComponent(Graphics g){
		
		Graphics2D g2 = (Graphics2D) g;
        super.paintComponent(g2); 

        RenderingHints hintsText = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING
        											, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        RenderingHints hintsShape = new RenderingHints(RenderingHints.KEY_ANTIALIASING
        											, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHints(hintsText);
        g2.setRenderingHints(hintsShape);
        
        float maxfontSize = 13;
        if((float)(squareSize*3)>=maxfontSize){
        	g2.setFont(g2.getFont().deriveFont(maxfontSize));
        }else{
        	g2.setFont(g2.getFont().deriveFont((float)squareSize*3));
        }

        FontMetrics fm = g2.getFontMetrics(g2.getFont());
        
        if(colorArray!=null){
        	
        	for(int i=0; i<colorArray.length; i++){
				for(int j=0; j<colorArray[0].length; j++){
					
					Color value = colorArray[i][j];
		         	
		         	g2.setColor(value);
		         	
		         	switch(gridPointChartType){
		 	        	case HISTROGRAM:
		 	        		g2.fillRect((i)*squareSize+marginLeft, ((gridHeight-1)-(j))*squareSize+marginTop, squareSize, squareSize);
		 	        		break;
		 	        	case GRID_CELL_MAP:
		             		g2.fillRect((i)*squareSize+marginLeft, (j)*squareSize+marginTop, squareSize, squareSize);
		 	        		break;
		 	        		
		         	}
		         	
				}
        	}
        	
        }
       
        g2.setColor(Color.black);
        g2.drawRect(marginLeft, marginTop, gridWidth*squareSize, gridHeight*squareSize);
        
        //Y Grid lines and tickmark labels
        if(gridPointChartType==GridPointChartType.GRID_CELL_MAP && showAxis){
   

			int x1 = marginLeft;
    		int x2 = gridWidth*squareSize+marginLeft;
    		int y1 = marginTop;
    		int y2 = marginTop;
    		g2.drawLine(x1, y1, x2, y2);
    		g2.drawString(String.valueOf(1), x1-fm.stringWidth(String.valueOf(1))-10, marginTop+(fm.getAscent()/2));
    		
        	for(int i=10; i<gridHeight; i+=10){
        		x1 = marginLeft;
        		x2 = gridWidth*squareSize+marginLeft;
        		y1 = i*squareSize+marginTop;
        		y2 = i*squareSize+marginTop;
        		g2.drawLine(x1, y1, x2, y2);
        		g2.drawString(String.valueOf(i), x1-fm.stringWidth(String.valueOf(i+1))-10, (i-1)*squareSize+marginTop+(fm.getAscent()/2)+4);
        	}
        	
        	x1 = marginLeft;
    		x2 = gridWidth*squareSize+marginLeft;
    		y1 = gridHeight*squareSize+marginTop;
    		y2 = gridHeight*squareSize+marginTop;
    		g2.drawString(String.valueOf(gridHeight), x1-fm.stringWidth(String.valueOf(gridHeight))-10, (gridHeight-1)*squareSize+marginTop+(fm.getAscent()/2)+4);

        }else if(gridPointChartType==GridPointChartType.HISTROGRAM){
        	int tickLabelCounter = 0;
        	for(int i=0; i<=gridHeight; i+=20){
        		int x1 = marginLeft;
        		int x2 = gridWidth*squareSize+marginLeft;
        		int y1 = i*squareSize+marginTop;
        		int y2 = i*squareSize+marginTop;
        		g2.drawLine(x1, y1, x2, y2);
        		g2.drawString(yTickLabelList.get(tickLabelCounter), x1-fm.stringWidth(yTickLabelList.get(tickLabelCounter))-10, (gridHeight-1-i)*squareSize+marginTop+(squareSize/2)+(fm.getAscent()/2));
        		tickLabelCounter++;
        	}
        }
        
        //X Grid lines and tickmark labels
        if(gridPointChartType==GridPointChartType.GRID_CELL_MAP && showAxis){
        	
        	int x1 = marginLeft;
    		int x2 = marginLeft;
    		int y1 = marginTop;
    		int y2 = gridHeight*squareSize+marginTop;
    		g2.drawLine(x1, y1, x2, y2);
    		g2.drawString(String.valueOf(1), x2+(squareSize/2)-(fm.stringWidth(String.valueOf(1))/2), y1-10);
        	
        	for(int i=10; i<gridWidth; i+=10){
        		x1 = i*squareSize+marginLeft;
        		x2 = i*squareSize+marginLeft;
        		y1 = marginTop;
        		y2 = gridHeight*squareSize+marginTop;
        		g2.drawLine(x1, y1, x2, y2);
        		g2.drawString(String.valueOf(i), x2+(squareSize/2)-(fm.stringWidth(String.valueOf(i+1))/2)-squareSize, y1-10);
        	}
        	
        	x1 = gridWidth*squareSize+marginLeft;
    		x2 = gridWidth*squareSize+marginLeft;
    		y1 = marginTop;
    		y2 = gridHeight*squareSize+marginTop;
    		g2.drawString(String.valueOf(gridWidth), x2+(squareSize/2)-(fm.stringWidth(String.valueOf(gridWidth))/2)-squareSize, y1-10);
        	
        }else if(gridPointChartType==GridPointChartType.HISTROGRAM){
        	
        	int tickLabelCounter = 0;
        	for(int i=0; i<=gridWidth; i+=20){
        		int x1 = i*squareSize+marginLeft;
        		int x2 = i*squareSize+marginLeft;
        		int y1 = marginTop;
        		int y2 = gridHeight*squareSize+marginTop;
        		g2.drawLine(x1, y1, x2, y2);
        		g2.drawString(xTickLabelList.get(tickLabelCounter), x2-(fm.stringWidth(xTickLabelList.get(tickLabelCounter))/2), y2+fm.getAscent()+5);
        		tickLabelCounter++;
        	}
        	
        }
        
        //Axis labels
        if(gridPointChartType==GridPointChartType.HISTROGRAM){
        	g2.drawString(xTitle, marginLeft+((gridWidth*squareSize)/2)-(fm.stringWidth(xTitle)/2), gridHeight*squareSize+marginTop+2*fm.getAscent()+10);
        	g2.transform(AffineTransform.getRotateInstance(-Math.PI/2));
    		g2.drawString(yTitle, -marginTop-((gridHeight*squareSize)/2)-(fm.stringWidth(yTitle)/2), fm.getAscent());
    		g2.transform(AffineTransform.getRotateInstance(Math.PI/2));
        }
        
        Stroke stroke = g2.getStroke();
        
        if(mouseOverGridPoint!=null && showMouseOverGridHighlight){
        	
        	switch(gridPointChartType){
	        	case HISTROGRAM:
	        		if(showCrossHairs){
		        		g2.setColor(Colors.RED);
		        		int yFactor = ((gridHeight-1)-(mouseOverGridPoint.getY()-1))*squareSize;
		        		if(yFactor>=0){
		        			g2.drawRect(marginLeft, yFactor+marginTop, gridWidth*squareSize, squareSize);
		        		}
			        	g2.drawRect((mouseOverGridPoint.getX()-1)*squareSize+marginLeft, marginTop, squareSize, gridHeight*squareSize);
	        		}
	        		break;
	        	case GRID_CELL_MAP:
	        		if(showCrossHairs){
		        		g2.setColor(Colors.RED);
		        		int yFactor = ((gridHeight-1)-(mouseOverGridPoint.getY()-1))*squareSize;
		        		if(yFactor>=0){
		        			g2.drawRect(marginLeft, (mouseOverGridPoint.getY()-1)*squareSize+marginTop, gridWidth*squareSize, squareSize);
		        		}
			        	g2.drawRect((mouseOverGridPoint.getX()-1)*squareSize+marginLeft, marginTop, squareSize, gridHeight*squareSize);
	        		}
	        		break;
	    	}
        }
        
        if(selectedGridPoint!=null && showSelectedGridHighlight){
        	
        	switch(gridPointChartType){
        		case HISTROGRAM:
	        		g2.setColor(Colors.RED);
	        		g2.setStroke(new BasicStroke(2));
	        		g2.drawRect((selectedGridPoint.getX()-1)*squareSize+marginLeft, ((gridHeight-1)-(selectedGridPoint.getY()-1))*squareSize+marginTop, squareSize, squareSize);
	        		break;
	        	case GRID_CELL_MAP:
	        		g2.setColor(Colors.RED);
	        		g2.setStroke(new BasicStroke(3));
	        		g2.drawRect((selectedGridPoint.getX()-1)*squareSize+marginLeft, (selectedGridPoint.getY()-1)*squareSize+marginTop, squareSize, squareSize);
	        		break;
	    	}
        }
        
        g2.setStroke(stroke);
 
	}

}
