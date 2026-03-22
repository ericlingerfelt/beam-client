package gov.ornl.bellerophon.beam.ui.multivariateanalyzer.pca;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import gov.ornl.bellerophon.beam.data.util.PCADataSet;
import gov.ornl.bellerophon.beam.enums.ChartScaleType;
import gov.ornl.bellerophon.beam.ui.plot.StaticPlotter;

import javax.swing.*;

public class MultivariateAnalyzerPCAScreePlotPanel extends JPanel implements MouseMotionListener, MouseListener, ComponentListener{

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
	int logStyle = 0;
	
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
    
    int xoffset=85;         // pixels to left of y axis
    int yoffset=40;         // pixels below x axis
    int topmarg=30;         // pixels above graph
    int rightmarg=20;       // pixels to right of graph
	
    double pcLimit;
    
    int mouseX = 0;
	int mouseY = 0;
	boolean showWindow = false;
	boolean mouseDragging = false;
	Rectangle square = new Rectangle();
    
	private PCADataSet pds;
	private MultivariateAnalyzerPCAScreePanel owner;
	
	public MultivariateAnalyzerPCAScreePlotPanel(MultivariateAnalyzerPCAScreePanel owner){
		this.owner = owner;
		addComponentListener(this);
		staticPlotter = new StaticPlotter();
    	setBackground(Color.white);
    	addMouseListener(this);
		addMouseMotionListener(this);
		square.width = 120;
    	square.height = 120;
    	setPreferredSize(new Dimension());
    	setOpaque(false);
	}

	public void setPreferredSize(Dimension preferredSize){
		super.setPreferredSize(preferredSize);
		x2 = (int) preferredSize.getWidth();
		y2 = (int) preferredSize.getHeight();
		repaint();
	}
	
	public String getXTitle(){
		return xtitle;
	}
	
	public String getYTitle(){
		return ytitle;
	}
	
	public double[] getXValueArray(){
		return x[0];
	}
	
	public double[] getYValueArray(){
		return y[0];
	}
	
	public void setCurrentData(PCADataSet pds){

		this.pds = pds;
		
		titleString = "";
		xtitle = "Principal Component";
		ytitle = "Variance";
		
		int size = 1;
		imax = size;
		kmax = pds.getPCADataList().size();
		
		mode = new int[size];
		mode[0] = 2;
		
		doplot = new int[size];	
		doplot[0] = 1;
		
		curveTitle = new String[size];
		curveTitle[0] = "";
		
		npoints = new int[size];
		npoints[0] = kmax;
		
		x = new double[imax][kmax];
		y = new double[imax][kmax];

		for(int i=1; i<=pds.getPCADataList().size(); i++){
			x[0][i-1] = i;
			y[0][i-1] = pds.getPCADataList().get(i-1).getS();
		}
		
		xtickIntervals = 10;
		ytickIntervals = 10;
		
		ydplace = 3;
		xdplace = 0;
		
		lcolor[0] = Color.BLACK;
		
		showLegend = false;
		plotmode = 0;
		dotSize = 4;

		minorX = true;
		majorX = true;			
		minorY = false;
		majorY = true;
		
		xmin = 1;
		xmax = pds.getPCADataList().size();
		
		ymin = getMinValue(y[0]);
		ymax = getMaxValue(y[0]);

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
	
	public void componentResized(ComponentEvent ce) {}
	public void componentMoved(ComponentEvent ce) {}
	public void componentShown(ComponentEvent ce) {
		setPreferredSize(owner.getPlotBounds());
		repaint();
	}
	public void componentHidden(ComponentEvent ce) {}
	
	public void setPlotMode(ChartScaleType xMode, ChartScaleType yMode){
		if(xMode==ChartScaleType.LIN && yMode==ChartScaleType.LIN){
			plotmode = 0;
			xdplace = 0;
			ydplace = 3;
			xtitle = "Principal Component";
			ytitle = "Variance";
		}else if(xMode==ChartScaleType.LIN && yMode==ChartScaleType.LOG){
			plotmode = 1;
			xdplace = 0;
			ydplace = 4;
			xtitle = "Principal Component";
			ytitle = "Log10(Variance)";
		}else if(xMode==ChartScaleType.LOG && yMode==ChartScaleType.LIN){
			plotmode = 3;
			xdplace = 3;
			ydplace = 3;
			xtitle = "Log10(Principal Component)";
			ytitle = "Variance";
		}else if(xMode==ChartScaleType.LOG && yMode==ChartScaleType.LOG){
			plotmode = 2;
			xdplace = 3;
			ydplace = 4;
			xtitle = "Log10(Principal Component)";
			ytitle = "Log10(Variance)";
		}
		repaint();
	}
	
	public void setPCLimit(double pcLimit){
		if(plotmode==2 || plotmode==3){
			double log10 = 0.434294482;
			pcLimit=log10*Math.log(pcLimit);
		}
		this.pcLimit = pcLimit;
		repaint();
	}
	
    public void paintComponent(Graphics g){
    	Graphics2D g2 = (Graphics2D)g;
		super.paintComponent(g2);
		RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHints(hints);
		
		if(pds!=null){
		
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
	        
	        double currentXValue = pcLimit;
	        double log10 = 0.434294482;
	        
	        double xmin1 = xmin;
        	double xmax1 = xmax;
	        if(plotmode==2 || plotmode==3){
	        	xmin1 = log10*Math.log(xmin);
	        	xmax1 = log10*Math.log(xmax);
	        }
	        
	        //draw red line
	        
			int wid = (x2-x1);
			xmin1 = xmin1-delxmin*Math.abs(xmax1-xmin1);
			double xscale = 1.0*(wid-xoffset-rightmarg)/(Math.abs(xmax1-xmin1));
			int xPos = (int)((currentXValue-xmin1)*xscale);
			g2.setColor(Color.red);
			g2.drawLine(xPos + xoffset + x1,y2 - yoffset,xPos + xoffset + x1,y1 + topmarg); 
	        
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
				
				//draw red line under magnifcation
				g2.setColor(Color.red);
				g2.drawLine(xPos + xoffset + newX1,newY2 - yoffset,xPos + xoffset + newX1,newY1 + topmarg); 
	                  
	    	}
        
		}
              
    }
  
}  
