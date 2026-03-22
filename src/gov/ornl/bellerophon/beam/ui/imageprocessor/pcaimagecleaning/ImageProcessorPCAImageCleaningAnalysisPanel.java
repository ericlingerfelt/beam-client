package gov.ornl.bellerophon.beam.ui.imageprocessor.pcaimagecleaning;

import gov.ornl.bellerophon.beam.data.feature.ImageProcessorData;
import gov.ornl.bellerophon.beam.ui.export.DataExporter;
import gov.ornl.bellerophon.beam.ui.export.ImageExporter;
import gov.ornl.bellerophon.beam.ui.worker.GeneratePCAImagesWorker;
import gov.ornl.bellerophon.beam.ui.worker.listener.GeneratePCAImagesListener;
import gov.ornl.bellerophon.beam.ui.worker.listener.GetPCAImageCleaningResultsListener;
import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import java.awt.Frame;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ImageProcessorPCAImageCleaningAnalysisPanel extends JPanel implements GetPCAImageCleaningResultsListener, 
																					GeneratePCAImagesListener,
																					ImageExporter,
																					DataExporter, 
																					ChangeListener{
	
	private Frame frame;
	private ImageProcessorData d;
	private JTabbedPane pane;
	private ImageProcessorPCAImageCleaningScreePanel screePanel;
	private ImageProcessorPCAImageCleaningImagePanel imagePanel;
	private ImageProcessorPCAImageCleaningFFTPanel fftPanel;
	private ImageProcessorPCAImageCleaningLoadingPanel loadingsPanel;
	private ImageProcessorPCAImageCleaningPCPanel pcPanel;
	private ImageProcessorPCAImageCleaningSetViewPanel setPanel;
	
	public ImageProcessorPCAImageCleaningAnalysisPanel(Frame frame, ImageProcessorData d) {
		
		this.frame = frame;
		this.d = d;
	
		setPanel = new ImageProcessorPCAImageCleaningSetViewPanel(d);
		
		imagePanel = new ImageProcessorPCAImageCleaningImagePanel(frame, d);
		fftPanel = new ImageProcessorPCAImageCleaningFFTPanel(frame, d);
		loadingsPanel = new ImageProcessorPCAImageCleaningLoadingPanel(frame, d, setPanel);
		screePanel = new ImageProcessorPCAImageCleaningScreePanel(frame, d, this);
		pcPanel = new ImageProcessorPCAImageCleaningPCPanel(frame, d, this);
		
		pane = new JTabbedPane();
		pane.add("Scree Plot", 		screePanel);
		pane.add("Images", 			imagePanel);
		pane.add("FFT Images", 		fftPanel);
		pane.add("Loading Maps", 	loadingsPanel);
		pane.addChangeListener(this);
		
		ArrayList<ImageProcessorPCAImageCleaningSetViewPanelListener> listenerList = new ArrayList<ImageProcessorPCAImageCleaningSetViewPanelListener>();
		listenerList.add(loadingsPanel);
		
		setPanel.setImageProcessorPCAImageCleaningSetViewPanelListenerList(listenerList);
		
	}

	public void setCurrentState() {
		removeAll();
		if(d.getDataFile()!=null && d.getDataFile().getPCAImageCleaningDataSet()!=null){
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
				imagePanel.exportCurrentImage();
				break;
			case 2:
				fftPanel.exportCurrentImage();
				break;
			case 3:
				loadingsPanel.exportCurrentImage();
				break;
		}		

	}

	public void exportCurrentData() {
		
		switch(pane.getSelectedIndex()){
			case 0:
				screePanel.exportCurrentData();
				break;
			case 1:
				imagePanel.exportCurrentData();
				break;
			case 2:
				fftPanel.exportCurrentData();
				break;
			case 3:
				loadingsPanel.exportCurrentData();
				break;
			
		}
	}

	public void updateAfterGetPCAImageCleaningResults() {
		GeneratePCAImagesWorker worker = new GeneratePCAImagesWorker(this, d.getDataFile().getPCAImageCleaningDataSet(), frame);
		worker.execute();
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
				double[] col2 = {10, TableLayoutConstants.FILL, 10};
				double[] row2 = {10, TableLayoutConstants.FILL, 10
									, TableLayoutConstants.PREFERRED, 10};
				setLayout(new TableLayout(col2, row2));
				add(pane,		"1, 1, f, f");
				add(pcPanel, 	"1, 3, f, f");
				break;
			case 3:
				double[] col3 = {10, TableLayoutConstants.FILL, 10};
				double[] row3 = {10, TableLayoutConstants.FILL, 10
									, TableLayoutConstants.PREFERRED, 10};
				setLayout(new TableLayout(col3, row3));
				add(pane,		"1, 1, f, f");
				add(setPanel, 	"1, 3, f, f");
				break;
		}
		validate();
		repaint();
	}

	public void updateAfterGeneratePCAImages() {
		setPanel.setCurrentState();
		pcPanel.setCurrentState();
		pcPanel.setPCCutOff(d.getDataFile().getPCAImageCleaningDataSet().getMaxComponentIndex()+1);
		screePanel.setCurrentState();
		screePanel.setPCCutOff(d.getDataFile().getPCAImageCleaningDataSet().getMaxComponentIndex()+1);
		imagePanel.setCurrentState();
		fftPanel.setCurrentState();
		loadingsPanel.setCurrentState();
		setCurrentState();
		
	}

	public void stateChanged(ChangeEvent ce) {
		if(ce.getSource()==pane){
			layoutUI();	
			if(pane.getSelectedIndex()>0){
				setPanel.setListenerIndex(pane.getSelectedIndex()-3);
			}
			switch(pane.getSelectedIndex()){
				case 1:
					loadingsPanel.arrangeSet();
					break;
			}	
		}
	}

}
