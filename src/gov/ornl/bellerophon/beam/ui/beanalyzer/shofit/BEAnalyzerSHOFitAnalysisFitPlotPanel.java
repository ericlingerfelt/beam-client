package gov.ornl.bellerophon.beam.ui.beanalyzer.shofit;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import gov.ornl.bellerophon.beam.enums.ComplexValueType;
import gov.ornl.bellerophon.beam.ui.plot.StaticPlotter;

import javax.swing.*;

public class BEAnalyzerSHOFitAnalysisFitPlotPanel extends JPanel implements MouseMotionListener, MouseListener{

	StaticPlotter staticPlotter;
	
	// Define some standard colors for convenience
    Color AIyellow=new Color (255,204,0);
    Color AIorange=new Color(255,153,0);
    Color AIred=new Color(204,51,0);
    Color AIpurple=new Color(153,102,153);
    Color AIblue=new Color(102,153,153);
    Color AIgreen=new Color(153,204,153);
    Color gray51=new Color(51,51,51);
    Color gray102=new Color(102,102,102);
    Color gray153=new Color(153,153,153);
    Color gray204=new Color(204,204,204);
    Color gray245=new Color(245,245,245);
    Color gray250=new Color(252,252,252);
	
	//linear mode
	int plotmode;
	
	//upper left corner of plot
	int x1 = 0;
	int y1 = 0;
	
	//lower right corner of plot
	int x2 = 800;
	int y2 = 600;
	
	//max number of points per curve
	int kmax = 2000;
	
	//max number of curves
	int imax = 40;
	
	//int indicating solid line plot
	int[] mode = {1};
	
	//dotsize (not used for solid line plot but required parameter)
	int dotSize = 3;
	
	//offset for legend
	int xlegoff = 80;
	int ylegoff = 40;
	
	//number of decimal places for numbers on x and y axis
	int xdplace = 0;
	int ydplace = 4;
	
	//number of data points for each curve
	int[] npoints = {1};
	
	//set to NO autoscale to max and min of x and y sets
	int doscalex = 0;
	int doscaley = 0;
	
	//say yes to plot the curve
	int[] doplot = {1};
	
	//Min and max of x and y on plot
	//overridden if autoscaling
	double xmin = 0;
	double xmax = 0;
		
	double ymin = 0;
	double ymax = 0;
	
	//set empty space around plot as fraction of total height
	//and width of plot
	double delxmin = 0.0;
	double delymin = 0.0;
	double delxmax = 0.0;
	double delymax = 0.0;
	
	//Set colors for lines or curves
	Color[] lcolor = new Color[2];
	
	Color bgcolor=Color.white;        // plot background color
    Color axiscolor=gray51;           // axis color
    Color legendfg=gray250;           // legend box color
    Color framefg=Color.white;        // frame color
    Color dropShadow = gray153;       // legend box dropshadow color
    Color legendbg=gray204;           // legend box frame color
    Color labelcolor = gray51;        // axis label color
    Color ticLabelColor = gray51;     // axis tic label color
	
	//title of x axis
	String xtitle = "Temperature (T9)";
	
	//title of y axis
	String ytitle = "Rate";
	
	//set curve title for legend
	String[] curveTitle = null;
	
	//set style of log plot (show number or log of number on each axis)
	int logStyle = 1;
	
	//number of intervals between x and y tick marks
	int ytickIntervals = 0;
	int xtickIntervals = 0;
	
	//do NOT show the legend
	boolean showLegend = true;
	
	//double arrays to hold x and y points 
	//first entry for each curve and next entry for number of points
	double[][] x = new double[imax][kmax];
	double[][] y = new double[imax][kmax];
	
	//show major minor tick marks
	//for X and Y
	//must change to current variables
	//here and in AstroPilotProjectStaticPlotter
	boolean majorX = true;
    boolean minorX = false;
    boolean majorY = true;
    boolean minorY = false;
    
    //Show title and subtitle
    boolean title = true;
    boolean subtitle = false;
    
    //Title and subtitle names
    String titleString = "";
    String subtitleString = "";
    
    //Is the legend inside the graph?
    boolean insideLegend = true;
    
    //Initialize legend position
    String location = "NW";
    
    int xoffset=100;         // pixels to left of y axis
    int yoffset=40;         // pixels below x axis
    int topmarg=30;         // pixels above graph
    int rightmarg=20;       // pixels to right of graph
	
    int mouseX = 0;
	int mouseY = 0;
	boolean showWindow = false;
	boolean mouseDragging = false;
	Rectangle square = new Rectangle();
    
	boolean init = false;
	
	private double[] fitDataArray;
	private ComplexValueType complexValueType;
	
	public BEAnalyzerSHOFitAnalysisFitPlotPanel(){
		
		staticPlotter = new StaticPlotter();
		
    	setBackground(Color.white);
    	
    	addMouseListener(this);
		addMouseMotionListener(this);
		
		square.width = 120;
    	square.height = 120;
    	
    	setCurrentData(null, null, null, ComplexValueType.AMP);
    	
    	init = true;
	}

	public String getXTitle(){
		return "Frequency (kHz)";
	}
	
	public String getYTitle(){
		return complexValueType.toString();
	}
	
	public double[][] getXValueArray(){
		return x;
	}
	
	public double[][] getYValueArray(){
		return y;
	}
	
	public void setPreferredSize(int size){
		Dimension preferredSize = new Dimension(size, size);
		super.setPreferredSize(preferredSize);
		x2 = (int) preferredSize.getWidth();
		y2 = (int) preferredSize.getHeight();
		repaint();
	}
	
	public void setCurrentData(double[] fitDataArray, double[] mainDataArray, double[] wArray, ComplexValueType complexValueType){

		this.fitDataArray = fitDataArray;
		this.complexValueType = complexValueType;
		
		titleString = "";
		xtitle = "Frequency (kHz)";
		ytitle = complexValueType.toString();
		
		if(fitDataArray==null){
			
			int size = 0;
			imax = size;
			kmax = 0;
			
			mode = new int[size];
			doplot = new int[size];	
			curveTitle = new String[1];
			curveTitle[0] = "";
			
			npoints = new int[size];
			lcolor = new Color[size];
			
			x = new double[imax][kmax];
			y = new double[imax][kmax];
			
		}else if(mainDataArray==null){
			
			int size = 1;
			imax = size;
			kmax = fitDataArray.length;
			
			mode = new int[size];
			mode[0] = 1;
			
			doplot = new int[size];	
			doplot[0] = 1;
			
			curveTitle = new String[size];
			curveTitle[0] = "SHO Fit Result";
			
			npoints = new int[size];
			npoints[0] = kmax;
			
			lcolor = new Color[size];
			lcolor[0] = Color.BLACK;
			
			x = new double[imax][kmax];
			y = new double[imax][kmax];
			
			x[0] = wArray;
			y[0] = fitDataArray;
			
			xmin = wArray[0];
			xmax = wArray[wArray.length-1];
			
			ymin = getMinValue(y[0]);
			ymax = getMaxValue(y[0]);
			
		}else{
			
			int size = 2;
			imax = size;
			kmax = fitDataArray.length;
			
			mode = new int[size];
			mode[0] = 1;
			mode[1] = 5;
			
			doplot = new int[size];	
			doplot[0] = 1;
			doplot[1] = 1;
			
			curveTitle = new String[size];
			curveTitle[0] = "SHO Fit Result";
			curveTitle[1] = "Experimental Data";
			
			npoints = new int[size];
			npoints[0] = kmax;
			npoints[1] = kmax;
			
			lcolor = new Color[size];
			lcolor[0] = Color.BLACK;
			lcolor[1] = Color.RED;
			
			x = new double[imax][kmax];
			y = new double[imax][kmax];
			
			x[0] = wArray;
			y[0] = fitDataArray;
			
			x[1] = wArray;
			y[1] = mainDataArray;
			
			xmin = wArray[0];
			xmax = wArray[wArray.length-1];
			
			double ymin0 = getMinValue(y[0]);
			double ymax0 = getMaxValue(y[0]);
			
			double ymin1 = getMinValue(y[1]);
			double ymax1 = getMaxValue(y[1]);
			
			ymin = Math.min(ymin0, ymin1);
			ymax = Math.max(ymax0, ymax1);
			
		}

		xtickIntervals = 10;
		ytickIntervals = 10;
		
		ydplace = 4;
		xdplace = 0;

		showLegend = false;
		plotmode = 0;
		dotSize = 4;

		minorX = true;
		majorX = true;			
		minorY = false;
		majorY = true;
		
		doscalex = 0;
		doscaley = 0;

		repaint();
			
	}
	
	private double getMinValue(double[] array){
		double minValue = Double.MAX_VALUE;
		for(int i=0; i<array.length; i++){
			minValue = Math.min(minValue, array[i]);
		}
		return minValue;
	}
	
	private double getMaxValue(double[] array){
		double maxValue = -1*Double.MAX_VALUE;
		for(int i=0; i<array.length; i++){
			maxValue = Math.max(maxValue, array[i]);
		}
		return maxValue;
	}
	
	public void mouseEntered(MouseEvent me){
		mouseX = me.getX();
		mouseY = me.getY();
		showWindow = true;
		repaint();
	}
	
	public void mouseExited(MouseEvent me){
		mouseX = me.getX();
		mouseY = me.getY();
		showWindow = false;
		repaint();
	}
	
	public void mousePressed(MouseEvent me){
		mouseX = me.getX();
		mouseY = me.getY();
		mouseDragging = true;	
		repaint();
	}
	public void mouseClicked(MouseEvent me){}
	
	public void mouseReleased(MouseEvent me){
		mouseX = me.getX();
		mouseY = me.getY();
		mouseDragging = false;	
		repaint();
	}
	
	
	public void mouseMoved(MouseEvent me){
		mouseX = me.getX();
		mouseY = me.getY();
		repaint();
	}
	
	public void mouseDragged(MouseEvent me){
		mouseX = me.getX();
		mouseY = me.getY();
		mouseDragging = true;
		repaint();
	}	
	
    public void paintComponent(Graphics g){
    	
    	Graphics2D g2 = (Graphics2D)g;
		super.paintComponent(g2);
		RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHints(hints);
		
		if(init && fitDataArray!=null){
		
	        staticPlotter.plotIt(plotmode,x1,y1,x2,y2,
	                  kmax,imax,mode,
	                  dotSize,xlegoff,ylegoff,xdplace,ydplace,
	                  npoints,doscalex,doscaley,doplot,xmin,xmax,ymin,ymax,
	                  delxmin,delxmax,delymin,delymax,
	                  lcolor,bgcolor,axiscolor,legendfg,framefg,
	                  dropShadow,legendbg,labelcolor,ticLabelColor,
	                  xtitle,ytitle,curveTitle,logStyle,ytickIntervals,
	                  xtickIntervals,showLegend,x,y,majorX, minorX, 
	                  majorY, minorY, title, subtitle, 
	                  titleString, subtitleString, 
	                  insideLegend, location, 
	                  xoffset, yoffset, topmarg, rightmarg, g2);  
	        
	        if(showWindow && mouseDragging){
	
	    		square.x = mouseX - 60;
	    		square.y = mouseY - 60;
	 
	    		g2.clip(square);
	    	
	    		g2.scale(2, 2);
	
				int shiftX = ((1*mouseX - x1)/2);
				int shiftY = ((1*mouseY - y1)/2);
	
				int newX1 = x1 - shiftX;
				int newY1 = y1 - shiftY;
				int newX2 = x2 - shiftX;
				int newY2 = y2 - shiftY;  
				
				staticPlotter.plotIt(plotmode,newX1,newY1,newX2,newY2,
	                  kmax,imax,mode,
	                  dotSize,xlegoff,ylegoff,xdplace,ydplace,
	                  npoints,doscalex,doscaley,doplot,xmin,xmax,ymin,ymax,
	                  delxmin,delxmax,delymin,delymax,
	                  lcolor,bgcolor,axiscolor,legendfg,framefg,
	                  dropShadow,legendbg,labelcolor,ticLabelColor,
	                  xtitle,ytitle,curveTitle,logStyle,ytickIntervals,
	                  xtickIntervals,showLegend,x,y,majorX, minorX, 
	                  majorY, minorY, title, subtitle, 
	                  titleString, subtitleString, 
	                  insideLegend, location, 
	                  xoffset, yoffset, topmarg, rightmarg, g2);
	                  
	    	}
        
		}
              
    }
  
}  
