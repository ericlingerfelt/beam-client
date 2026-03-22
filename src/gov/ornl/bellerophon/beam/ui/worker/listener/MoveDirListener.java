package gov.ornl.bellerophon.beam.ui.worker.listener;

import gov.ornl.bellerophon.beam.data.util.CustomFile;

public interface MoveDirListener {
	public void updateAfterMoveDir(CustomFile customFile, CustomFile newParentCustomFile);
}
