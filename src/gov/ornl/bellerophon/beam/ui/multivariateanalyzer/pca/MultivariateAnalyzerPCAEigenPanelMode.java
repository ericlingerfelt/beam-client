package gov.ornl.bellerophon.beam.ui.multivariateanalyzer.pca;

public enum MultivariateAnalyzerPCAEigenPanelMode {
	
	PLOT_MODE ("1D Plots"), 
	CELL_MODE ("2D Colormaps");

	private String string;

	MultivariateAnalyzerPCAEigenPanelMode(String string){
		this.string = string;
	}

	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	public String toString(){return string;}

}