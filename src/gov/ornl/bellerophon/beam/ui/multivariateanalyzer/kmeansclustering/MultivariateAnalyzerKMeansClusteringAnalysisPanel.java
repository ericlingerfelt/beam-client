package gov.ornl.bellerophon.beam.ui.multivariateanalyzer.kmeansclustering;

import gov.ornl.bellerophon.beam.data.feature.MultivariateAnalyzerData;
import gov.ornl.bellerophon.beam.ui.export.DataExporter;
import gov.ornl.bellerophon.beam.ui.export.ImageExporter;
import gov.ornl.bellerophon.beam.ui.worker.listener.GetKMeansClusteringResultsListener;
import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import java.awt.Frame;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class MultivariateAnalyzerKMeansClusteringAnalysisPanel extends JPanel implements GetKMeansClusteringResultsListener,  
																									ImageExporter,
																									DataExporter{
	
	private MultivariateAnalyzerData d;
	private JTabbedPane pane;
	private MultivariateAnalyzerKMeansClusteringResultsPanel resultsPanel;
	
	public MultivariateAnalyzerKMeansClusteringAnalysisPanel(Frame frame, MultivariateAnalyzerData d) {
		
		this.d = d;

		resultsPanel = new MultivariateAnalyzerKMeansClusteringResultsPanel(frame, d, this);
		
		pane = new JTabbedPane();
		pane.add("K-Means Clustering Results", 	resultsPanel);
		
	}

	public void setCurrentState() {
		removeAll();
		if(d.getDataFile()!=null && d.getDataFile().getKMeansClusteringDataSet()!=null){
			layoutUI();
		}
		validate();
		repaint();
		
	}

	public void exportCurrentImage() {
		
		switch(pane.getSelectedIndex()){
		case 0:
			resultsPanel.exportCurrentImage();
			break;
		}		

	}

	public void exportCurrentData() {
		
		switch(pane.getSelectedIndex()){
		case 0:
			resultsPanel.exportCurrentData();
			break;
		}
	}

	private void layoutUI(){
		removeAll();
		switch(pane.getSelectedIndex()){
			case 0:
				double[] col1 = {10, TableLayoutConstants.FILL, 10};
				double[] row1 = {10, TableLayoutConstants.FILL, 10};
				setLayout(new TableLayout(col1, row1));
				add(pane, "1, 1, f, f");
				break;
		}
		validate();
		repaint();
	}

	public void updateAfterGetKMeansClusteringResults() {
		resultsPanel.setCurrentState();
	}

}
