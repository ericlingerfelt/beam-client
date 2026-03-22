package gov.ornl.bellerophon.beam.ui.dialog;

import info.clearthought.layout.*;

import java.awt.*;
import java.text.DecimalFormat;
import javax.swing.*;
import gov.ornl.bellerophon.beam.io.BytesReadListener;
import gov.ornl.bellerophon.beam.ui.util.*;

public class DelayDownloadDialog extends JDialog implements BytesReadListener{
	
	private Window owner;
	private JProgressBar bar;
	private DecimalFormat format;
	private long contentLength;
	private int counter = 0;
	private WordWrapLabel textArea;
	
	public DelayDownloadDialog(Window owner, String string, String title){
		
		super(owner, title, Dialog.ModalityType.APPLICATION_MODAL);
		this.owner = owner;
		setSize(800, 200);

		textArea = new WordWrapLabel();
		textArea.setText(string);
		
		JScrollPane sp = new JScrollPane(textArea
								, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED
								, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		format = new DecimalFormat("#######0.0");
		setSize(750, 200);
		
		bar = new JProgressBar();
		bar.setStringPainted(true);
		bar.setBorderPainted(true);
		bar.setMaximum(100);
		bar.setMinimum(0);
		
		Container c = this.getContentPane();
		double[] col = {10, TableLayoutConstants.FILL, 10};
		double[] row = {10, TableLayoutConstants.FILL
						, 10, TableLayoutConstants.PREFERRED, 10};
		c.setLayout(new TableLayout(col, row));
		c.add(sp, 	"1, 1, f, f");
		c.add(bar, 	"1, 3, f, c");
	}
	
	public void setBytesRead(long bytesRead){
		if(counter==200){
			bar.setValue((int) Math.ceil((((double)bytesRead/(double)contentLength)*100)));
			setTitle("Downloading Results: " 
					+ format.format(bytesRead/1000000L) + " MB out of " 
					+ format.format(contentLength/1E6) + " MB");
			repaint();
			counter = 0;
		}else if(bytesRead==contentLength){
			bar.setValue(100);
			setTitle("Downloading Results: " 
					+ format.format(contentLength/1E6) + " MB out of " 
					+ format.format(contentLength/1E6) + " MB");
			repaint();
		}else{
			counter++;
		}
	}
	
	public void setContentLength(long contentLength) {
		this.contentLength = contentLength;
		setTitle("Downloading Results"
				+ ": 0.0 MB out of " 
				+ format.format(contentLength/(long)1E6) + " MB");
		repaint();
	}
	
	/**
	 * Open.
	 */
	public void open(){
		setLocationRelativeTo(owner);
		OpenDialogWorker task = new OpenDialogWorker(this);
		task.execute();
	}
	
	/**
	 * Close.
	 */
	public void close(){
		setVisible(false);
		dispose();
	}

}