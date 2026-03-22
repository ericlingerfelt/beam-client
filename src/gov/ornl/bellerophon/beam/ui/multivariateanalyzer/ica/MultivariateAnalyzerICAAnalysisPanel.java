package gov.ornl.bellerophon.beam.ui.multivariateanalyzer.ica;

import gov.ornl.bellerophon.beam.data.feature.MultivariateAnalyzerData;
import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import java.awt.Frame;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class MultivariateAnalyzerICAAnalysisPanel extends JPanel{

	public MultivariateAnalyzerICAAnalysisPanel(Frame frame, MultivariateAnalyzerData d) {
		
		double[] col = {10, TableLayoutConstants.FILL, 10};
		double[] row = {10, TableLayoutConstants.FILL, 10};
		setLayout(new TableLayout(col, row));
		add(new JLabel("ICA Analysis is currently under development."), "1, 1, c, c");
		
	}

	public void setCurrentState() {
		
		
	}

	public void exportCurrentImage() {
		
		
	}

	public void exportCurrentData() {
		
		
	}

}