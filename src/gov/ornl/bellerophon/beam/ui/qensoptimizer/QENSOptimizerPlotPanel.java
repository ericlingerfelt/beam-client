package gov.ornl.bellerophon.beam.ui.qensoptimizer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.TreeSet;

import gov.ornl.bellerophon.beam.data.util.SNSDataSet;
import gov.ornl.bellerophon.beam.ui.plot.StaticPlotter;

import javax.swing.*;

public class QENSOptimizerPlotPanel extends JPanel implements MouseMotionListener, MouseListener{

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
	int x1 = 4;
	int y1 = 4;
	
	//lower right corner of plot
	int x2 = 500;
	int y2 = 500;
	
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
    
    int xoffset=65;         // pixels to left of y axis
    int yoffset=40;         // pixels below x axis
    int topmarg=30;         // pixels above graph
    int rightmarg=20;       // pixels to right of graph
	
    SNSDataSet sds = null;
    
    int mouseX = 0;
	int mouseY = 0;
	boolean showWindow = false;
	boolean mouseDragging = false;
	Rectangle square = new Rectangle();
    
	public QENSOptimizerPlotPanel(){
		staticPlotter = new StaticPlotter();
    	setBackground(Color.white);
    	addMouseListener(this);
		addMouseMotionListener(this);
		square.width = 120;
    	square.height = 120;
	}

	public void setSNSDataSet(SNSDataSet sds){
		
		this.sds = sds;
		
		TreeSet<Double> xSet = new TreeSet(sds.getDataMap().keySet());
		
		titleString = "Force-Field Optimization Plot";
		xtitle = "Force Field Parameter (Kcal/mol)";
		ytitle = "Chi-squared";
		
		int size = 1;
		imax = size;
		kmax = xSet.size();
		
		mode = new int[size];
		mode[0] = 5;
		
		doplot = new int[size];	
		doplot[0] = 1;
		
		curveTitle = new String[size];
		curveTitle[0] = "";
		
		npoints = new int[size];
		npoints[0] = kmax;
		
		x = new double[imax][kmax];
		y = new double[imax][kmax];

		for(int i=0; i<xSet.size(); i++){
			x[0][i] = (double) xSet.toArray()[i];
			y[0][i] = sds.getDataMap().get(x[0][i]).getChiSquared();
		}
		
		xtickIntervals = 13;
		ytickIntervals = 10;
		
		ydplace = 0;
		xdplace = 2;
		
		lcolor[0] = Color.RED;
		
		showLegend = false;
		plotmode = 0;
		dotSize = 10;

		minorX = true;
		majorX = true;			
		minorY = false;
		majorY = true;
		
		xmin = 0.0;
		xmax = 0.13;
		
		ymin = 0;
		ymax = 150;

		repaint();
			
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
		
		if(sds!=null){
		
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
