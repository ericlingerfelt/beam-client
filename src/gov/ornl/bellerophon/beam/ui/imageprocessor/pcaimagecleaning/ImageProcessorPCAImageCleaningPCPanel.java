package gov.ornl.bellerophon.beam.ui.imageprocessor.pcaimagecleaning;

import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.DecimalFormat;

import gov.ornl.bellerophon.beam.data.feature.ImageProcessorData;
import gov.ornl.bellerophon.beam.data.util.PCAImageCleaningDataSet;
import gov.ornl.bellerophon.beam.ui.dialog.AttentionDialog;
import gov.ornl.bellerophon.beam.ui.format.Borders;
import gov.ornl.bellerophon.beam.ui.worker.GeneratePCAImagesWorker;
import gov.ornl.bellerophon.beam.ui.worker.listener.GeneratePCAImagesListener;
import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ImageProcessorPCAImageCleaningPCPanel extends JPanel implements ChangeListener, KeyListener{

	private JSlider pcSlider;
	private JTextField valueField;
	private JSpinner pcSpinner;
	private SpinnerNumberModel pcModel;
	private GeneratePCAImagesListener gpil;
	private ImageProcessorData d;
	private PCAImageCleaningDataSet picds;
	private Frame frame;
	
	public ImageProcessorPCAImageCleaningPCPanel(Frame frame, ImageProcessorData d, GeneratePCAImagesListener gpil){
		
		this.d = d;
		this.frame = frame;
		this.gpil = gpil;
		
		pcSlider = new JSlider();
		pcSlider.setPaintLabels(true);
		pcSlider.setPaintTicks(true);
		pcSlider.setSnapToTicks(true);
		
		pcModel = new SpinnerNumberModel();
		pcSpinner = new JSpinner(pcModel);
		((JSpinner.DefaultEditor) pcSpinner.getEditor()).getTextField().addKeyListener(this);
		
		valueField = new JTextField(7);
		valueField.setEditable(false);
		
		JLabel pcLabel = new JLabel("PC Limit:");
		JLabel valueLabel = new JLabel("Variance:");
		
		setBorder(Borders.getBorder("PCA Options"));
		JPanel pcCutoffPanel = new JPanel();
		double[] columnPCCutoff = {TableLayoutConstants.PREFERRED, 7
										, TableLayoutConstants.FILL, 7
										, TableLayoutConstants.FILL, 7
										, TableLayoutConstants.FILL, 7
										, TableLayoutConstants.FILL, 7
										, TableLayoutConstants.FILL, 7
										, TableLayoutConstants.FILL, 10
										, TableLayoutConstants.PREFERRED, 7
										, TableLayoutConstants.PREFERRED};
		double[] rowPCCutoff = {TableLayoutConstants.PREFERRED};
		pcCutoffPanel.setLayout(new TableLayout(columnPCCutoff, rowPCCutoff));
		pcCutoffPanel.add(pcLabel,   	"0, 0, r, c");
		pcCutoffPanel.add(pcSlider,   	"2, 0, 10, 0, f, c");
		pcCutoffPanel.add(pcSpinner,  	"12, 0, f, c");
		pcCutoffPanel.add(valueLabel,  	"14, 0, f, c");
		pcCutoffPanel.add(valueField,  	"16, 0, f, c");
		
		double[] column = {10, TableLayoutConstants.FILL, 10};
		double[] row = {10, TableLayoutConstants.FILL, 10};
		setLayout(new TableLayout(column, row));
		add(pcCutoffPanel,   "1, 1, f, c");
	}
	
	public void setCurrentState(){
		
		picds = d.getDataFile().getPCAImageCleaningDataSet();
		
		pcSlider.removeChangeListener(this);
		pcSlider.setMinimum(1);
		pcSlider.setMaximum(picds.getPCADataList().size());
		pcSlider.setValue(picds.getMaxComponentIndex()+1);
		pcSlider.addChangeListener(this);
		
		pcSpinner.removeChangeListener(this);
		pcModel.setMinimum(1);
		pcModel.setMaximum(picds.getPCADataList().size());
		pcModel.setValue(pcSlider.getValue());
		pcSpinner.addChangeListener(this);
		
		valueField.setText(new DecimalFormat("0.000E0").format(picds.getPCADataList().get(pcSlider.getValue()-1).getS()));
		
	}

	private boolean goodPCCutOff(){
		boolean goodPCCutOff = true;
		try{
			int cutoffValue = Integer.valueOf(((JSpinner.DefaultEditor) pcSpinner.getEditor()).getTextField().getText());
			if(cutoffValue<1 ||  cutoffValue>picds.getPCADataList().size()){
				AttentionDialog.createDialog(frame, "Please enter an integer value between 1 and " + picds.getPCADataList().size() + ".");
				goodPCCutOff = false;
			}
		}catch(NumberFormatException nfe){
			AttentionDialog.createDialog(frame, "Please enter an integer value between 1 and " + picds.getPCADataList().size() + ".");
			goodPCCutOff = false;
		}
		return goodPCCutOff;
	}
	
	public void setPCCutOff(int pcCutOff){
		pcSlider.removeChangeListener(this);
		pcSlider.setValue(pcCutOff);
		valueField.setText(new DecimalFormat("0.000E0").format(picds.getPCADataList().get(pcSlider.getValue()-1).getS()));
		pcSlider.addChangeListener(this);
	}
	
	public void stateChanged(ChangeEvent ce) {
		if(ce.getSource()==pcSlider){
			pcModel.setValue(pcSlider.getValue());
			valueField.setText(new DecimalFormat("0.000E0").format(picds.getPCADataList().get(pcSlider.getValue()-1).getS()));
			if(!pcSlider.getValueIsAdjusting()){
				picds.setMaxComponentIndex(pcSlider.getValue()-1);
				GeneratePCAImagesWorker worker = new GeneratePCAImagesWorker(gpil, picds, frame);
				worker.execute();
			}
		}else if(ce.getSource()==pcSpinner){
			pcSlider.setValue((int) pcModel.getValue());
		}
	}

	public void keyTyped(KeyEvent ke){}
	public void keyPressed(KeyEvent ke){}
	public void keyReleased(KeyEvent ke){
		if(ke.getKeyCode()==KeyEvent.VK_ENTER){
			if(!goodPCCutOff()){
				pcModel.setValue(pcSlider.getValue());
				valueField.setText(new DecimalFormat("0.000E0").format(picds.getPCADataList().get(pcSlider.getValue()-1).getS()));
				picds.setMaxComponentIndex(pcSlider.getValue()-1);
				GeneratePCAImagesWorker worker = new GeneratePCAImagesWorker(gpil, picds, frame);
				worker.execute();
			}
		}
	}
	
}
