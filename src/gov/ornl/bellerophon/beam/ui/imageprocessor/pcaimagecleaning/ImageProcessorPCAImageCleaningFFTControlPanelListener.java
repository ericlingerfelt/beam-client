package gov.ornl.bellerophon.beam.ui.imageprocessor.pcaimagecleaning;

import gov.ornl.bellerophon.beam.enums.PCAImageType;

public interface ImageProcessorPCAImageCleaningFFTControlPanelListener {
	public void fftControlPanelShowCrossHairsChanged(PCAImageType type); 
	public void fftControlPanelShowColorBarsChanged(PCAImageType type); 
	public void fftControlPanelShowAxisChanged(PCAImageType type); 
	public void fftControlPanelApplySigmaChanged(PCAImageType type); 
	public void fftControlPanelMapOutliersChanged(PCAImageType type); 
	public void fftControlPanelZoomSizeChanged(PCAImageType type); 
	public void fftControlPanelSigmaValueChanged(PCAImageType type); 
	public void fftControlPanelColorMapChanged(PCAImageType type); 
	public void fftControlPanelChartScaleChanged(PCAImageType type); 
}