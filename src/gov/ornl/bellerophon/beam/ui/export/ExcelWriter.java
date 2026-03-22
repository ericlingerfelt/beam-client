package gov.ornl.bellerophon.beam.ui.export;

/**
 * The ExcelWriter interface.
 *
 * @author Eric J. Lingerfelt
 */
public interface ExcelWriter {
	
	/**
	 * Writes the excel output to a file.
	 *
	 * @param filepath the filepath
	 * @throws Exception the exception
	 */
	public void writeExcel(String filepath) throws Exception;
}
