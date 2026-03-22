package gov.ornl.bellerophon.beam.ui.multivariateanalyzer.pca;

import gov.ornl.bellerophon.beam.data.feature.MultivariateAnalyzerData;
import gov.ornl.bellerophon.beam.ui.export.DataExporter;
import gov.ornl.bellerophon.beam.ui.export.ImageExporter;
import gov.ornl.bellerophon.beam.ui.worker.listener.GetPCASResultsListener;
import gov.ornl.bellerophon.beam.ui.worker.listener.GetPCAUVResultsListener;
import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import java.awt.Frame;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MultivariateAnalyzerPCAAnalysisPanel extends JPanel implements GetPCAUVResultsListener,  
																			GetPCASResultsListener,
																			ImageExporter,
																			DataExporter, 
																			ChangeListener{
	
	private MultivariateAnalyzerData d;
	private JTabbedPane pane;
	private MultivariateAnalyzerPCAScreePanel screePanel;
	private MultivariateAnalyzerPCALoadingPanel loadingsPanel;
	private MultivariateAnalyzerPCAEigenPanel eigenPanel;
	private MultivariateAnalyzerPCALoadingEigenPanel loadingsEigenPanel;
	private MultivariateAnalyzerPCASetViewPanel setPanel;
	
	public MultivariateAnalyzerPCAAnalysisPanel(Frame frame, MultivariateAnalyzerData d) {
		
		this.d = d;
	
		setPanel = new MultivariateAnalyzerPCASetViewPanel(d);
		
		screePanel = new MultivariateAnalyzerPCAScreePanel(frame, d, this);
		loadingsPanel = new MultivariateAnalyzerPCALoadingPanel(frame, d, setPanel);
		eigenPanel = new MultivariateAnalyzerPCAEigenPanel(frame, d, setPanel);
		loadingsEigenPanel = new MultivariateAnalyzerPCALoadingEigenPanel(frame, d, setPanel);
		
		pane = new JTabbedPane();
		pane.add("Scree Plot", 						screePanel);
		pane.add("Loading Maps", 					loadingsPanel);
		pane.add("Eigenvectors", 					eigenPanel);
		pane.add("Loading Map/Eigenvector Pairs", 	loadingsEigenPanel);
		pane.addChangeListener(this);
		
		ArrayList<MultivariateAnalyzerPCASetViewPanelListener> listenerList = new ArrayList<MultivariateAnalyzerPCASetViewPanelListener>();
		listenerList.add(loadingsPanel);
		listenerList.add(eigenPanel);
		listenerList.add(loadingsEigenPanel);
		
		setPanel.setMultivariateAnalyzerPCASetViewPanelListenerList(listenerList);
		
	}

	public void setCurrentState() {
		removeAll();
		if(d.getDataFile()!=null && d.getDataFile().getPCADataSet()!=null){
			layoutUI();
		}
		validate();
		repaint();
		
	}

	public void exportCurrentImage() {
		
		switch(pane.getSelectedIndex()){
		case 0:
			screePanel.exportCurrentImage();
			break;
		case 1:
			loadingsPanel.exportCurrentImage();
			break;
		case 2:
			eigenPanel.exportCurrentImage();
			break;
		case 3:
			loadingsEigenPanel.exportCurrentImage();
			break;
		}		

	}

	public void exportCurrentData() {
		
		switch(pane.getSelectedIndex()){
		case 0:
			screePanel.exportCurrentData();
			break;
		case 1:
			loadingsPanel.exportCurrentData();
			break;
		case 2:
			eigenPanel.exportCurrentData();
			break;
		case 3:
			loadingsEigenPanel.exportCurrentData();
			break;
		}
	}

	public void updateAfterGetPCASResults() {
		screePanel.setCurrentState();
	}
	
	public void updateAfterGetPCAUVResults() {
		setPanel.setCurrentState();
		loadingsPanel.setCurrentState();
		eigenPanel.setCurrentState();
		loadingsEigenPanel.setCurrentState();
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
			case 1:
			case 2:
			case 3:
				double[] col2 = {10, TableLayoutConstants.FILL, 10};
				double[] row2 = {10, TableLayoutConstants.FILL, 10
									, TableLayoutConstants.PREFERRED, 10};
				setLayout(new TableLayout(col2, row2));
				add(pane,		"1, 1, f, f");
				add(setPanel, 	"1, 3, f, f");
				break;
		}
		validate();
		repaint();
	}
	
	public void stateChanged(ChangeEvent ce) {
		if(ce.getSource()==pane){
			layoutUI();
			if(pane.getSelectedIndex()>0){
				setPanel.setListenerIndex(pane.getSelectedIndex()-1);
			}
			switch(pane.getSelectedIndex()){
				case 0:
					screePanel.refreshPlot();
					break;
				case 1:
					loadingsPanel.arrangeSet();
					break;
				case 2:
					eigenPanel.arrangeSet();
					break;
				case 3:
					loadingsEigenPanel.arrangeSet();
					break; 
			}		
			
		}
		
	}

}
