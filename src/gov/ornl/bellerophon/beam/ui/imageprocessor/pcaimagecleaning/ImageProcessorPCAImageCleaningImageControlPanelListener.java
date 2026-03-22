package gov.ornl.bellerophon.beam.ui.imageprocessor.pcaimagecleaning;

import gov.ornl.bellerophon.beam.enums.PCAImageType;

public interface ImageProcessorPCAImageCleaningImageControlPanelListener {
	public void imageControlPanelShowCrossHairsChanged(PCAImageType type); 
	public void imageControlPanelShowColorBarsChanged(PCAImageType type); 
	public void imageControlPanelShowAxisChanged(PCAImageType type); 
	public void imageControlPanelApplySigmaChanged(PCAImageType type); 
	public void imageControlPanelMapOutliersChanged(PCAImageType type); 
	public void imageControlPanelZoomSizeChanged(PCAImageType type); 
	public void imageControlPanelSigmaValueChanged(PCAImageType type); 
	public void imageControlPanelColorMapChanged(PCAImageType type); 
}