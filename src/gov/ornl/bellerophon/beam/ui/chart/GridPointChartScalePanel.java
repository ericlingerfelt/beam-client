package gov.ornl.bellerophon.beam.ui.chart;

import gov.ornl.bellerophon.beam.ui.chart.GridPointChartPanel.GridPointChartType;
import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import javax.swing.*;

public class GridPointChartScalePanel extends JPanel{

	private int index;
	private GridPointChartPanel chartPanel;
	private BinnedColorScalePanel scalePanel;
	
	public GridPointChartScalePanel(int index){
		
		this.index = index;
		chartPanel = new GridPointChartPanel(GridPointChartType.GRID_CELL_MAP);
		chartPanel.setMarginLeft(60);
		chartPanel.setMarginRight(30);
		chartPanel.setMarginTop(30);
		chartPanel.setMarginBottom(20);
		
		scalePanel = new BinnedColorScalePanel();
		
		setOpaque(false);
		double[] col = {TableLayoutConstants.PREFERRED, 
										10, TableLayoutConstants.PREFERRED};
		double[] row = {TableLayoutConstants.PREFERRED};
		setLayout(new TableLayout(col, row));
		add(chartPanel, 	"0, 0, r, t");	
		add(scalePanel, 	"2, 0, l, t");	
	}
	
	public GridPointChartPanel getChartPanel(){return chartPanel;}
	public BinnedColorScalePanel getScalePanel(){return scalePanel;}
	public int getIndex(){return index;}
	public void showColorBars(boolean showColorBars){
		removeAll();
		if(showColorBars){
			double[] col = {TableLayoutConstants.PREFERRED, 
								10, TableLayoutConstants.PREFERRED};
			double[] row = {TableLayoutConstants.PREFERRED};
			setLayout(new TableLayout(col, row));
			add(chartPanel, 	"0, 0, r, t");	
			add(scalePanel, 	"2, 0, l, t");	
		}else{
			double[] col = {TableLayoutConstants.PREFERRED};
			double[] row = {TableLayoutConstants.PREFERRED};
			setLayout(new TableLayout(col, row));
			add(chartPanel, 	"0, 0, c, t");	
		}
		validate();
		repaint();
	}
	
	public void showAxis(boolean showAxis){
		chartPanel.setShowAxis(showAxis);
		validate();
		repaint();
	}
}
