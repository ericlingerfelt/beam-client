package gov.ornl.bellerophon.beam.ui.worker.listener;

import gov.ornl.bellerophon.beam.data.util.CustomFile;

public interface MoveDataFileListener {
	public void updateAfterMoveDataFile(CustomFile customFile, CustomFile newParentCustomFile);
}
