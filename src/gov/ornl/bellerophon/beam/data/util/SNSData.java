package gov.ornl.bellerophon.beam.data.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import gov.ornl.bellerophon.beam.data.Data;
import gov.ornl.bellerophon.beam.io.IOUtilities;

public class SNSData implements Data{

	private double chiSquared, ffParam;
	private double[] qArray;
	private double[] energyArrayExp, energyArrayFit;
	private double[][] signalArrayExp, signalArrayFit;
	
	public SNSData(){
		initialize();
	}
	
	public SNSData clone(){
		SNSData sd = new SNSData();
		return sd;
	}
	
	public void initialize(){
		chiSquared = 0.0;
		ffParam = 0.0;
		qArray = new double[]{0.3, 0.5, 0.7, 0.9, 1.1, 1.3, 1.5, 1.7, 1.9};
	}

	public double getFFParam(){return ffParam;}
	public void setFFParam(double ffParam){this.ffParam = ffParam;}
	public double getChiSquared(){return chiSquared;}
	public double[] getQArray(){return qArray;}
	public double[] getEnergyArrayExp(){return energyArrayExp;}
	public double[] getEnergyArrayFit(){return energyArrayFit;}
	public double[][] getSignalArrayExp(){return signalArrayExp;}
	public double[][] getSignalArrayFit(){return signalArrayFit;}
	
	public void populateFromResultsZipFile(File file){
		try{
			Path path = Files.createTempDirectory(null);
			File dir = path.toFile();
			unzip(file.getAbsolutePath(), dir.getAbsolutePath());
			File[] listOfFiles = dir.listFiles();
			for(int i=0; i<listOfFiles.length; i++){
				if(listOfFiles[i].isFile()){
					processFile(listOfFiles[i]);
				}
			}
			dir.deleteOnExit();
		}catch (IOException ioe){
			ioe.printStackTrace();
		}	
	}

	private void processFile(File file){
		try{
			String contents = new String(IOUtilities.readFile(file));
			contents = contents.trim();
			if(file.getName().equals("chi2.txt")){
				chiSquared = Double.valueOf(contents);
			}else if(file.getName().equals("experiment.txt")){
				String[] array = contents.split("\n");
				energyArrayExp = new double[array.length-1];
				signalArrayExp = new double[9][array.length-1];
				for(int i=1; i<array.length; i++){
					String line = array[i].trim();
					if(line.equals("")){
						continue;
					}
					String[] subarray = line.split(" ");
					energyArrayExp[i-1] = 1000*Double.valueOf(subarray[0]);
					signalArrayExp[0][i-1] = Math.log10(Double.valueOf(subarray[1]));
					signalArrayExp[1][i-1] = Math.log10(Double.valueOf(subarray[3]));
					signalArrayExp[2][i-1] = Math.log10(Double.valueOf(subarray[5]));
					signalArrayExp[3][i-1] = Math.log10(Double.valueOf(subarray[7]));
					signalArrayExp[4][i-1] = Math.log10(Double.valueOf(subarray[9]));
					signalArrayExp[5][i-1] = Math.log10(Double.valueOf(subarray[11]));
					signalArrayExp[6][i-1] = Math.log10(Double.valueOf(subarray[13]));
					signalArrayExp[7][i-1] = Math.log10(Double.valueOf(subarray[15]));
					signalArrayExp[8][i-1] = Math.log10(Double.valueOf(subarray[17]));
				}
			}else if(file.getName().equals("fitted.txt")){
				String[] array = contents.split("\n");
				energyArrayFit = new double[array.length-1];
				signalArrayFit = new double[9][array.length-1];
				for(int i=1; i<array.length; i++){
					String line = array[i].trim();
					if(line.equals("")){
						continue;
					}
					String[] subarray = line.split(" ");
					energyArrayFit[i-1] = 1000*Double.valueOf(subarray[0]);
					signalArrayFit[0][i-1] = Math.log10(Double.valueOf(subarray[1]));
					signalArrayFit[1][i-1] = Math.log10(Double.valueOf(subarray[3]));
					signalArrayFit[2][i-1] = Math.log10(Double.valueOf(subarray[5]));
					signalArrayFit[3][i-1] = Math.log10(Double.valueOf(subarray[7]));
					signalArrayFit[4][i-1] = Math.log10(Double.valueOf(subarray[9]));
					signalArrayFit[5][i-1] = Math.log10(Double.valueOf(subarray[11]));
					signalArrayFit[6][i-1] = Math.log10(Double.valueOf(subarray[13]));
					signalArrayFit[7][i-1] = Math.log10(Double.valueOf(subarray[15]));
					signalArrayFit[8][i-1] = Math.log10(Double.valueOf(subarray[17]));
					for(int j=0; j<9; j++){
						if(new Double(signalArrayFit[j][i-1]).equals(Double.NaN)){
							signalArrayFit[j][i-1] = signalArrayExp[j][i-1];
						}
					}
				}
			}
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
	}
	
	private void unzip(String zipFilePath, String destDirectory) throws IOException {
		File destDir = new File(destDirectory);
		if(!destDir.exists()){
			destDir.mkdir();
		}
		ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
		ZipEntry entry = zipIn.getNextEntry();
		while(entry != null){
			String filePath = destDirectory + File.separator + entry.getName();
			if(!entry.isDirectory()){
				extractFile(zipIn, filePath);
			}else{
				File dir = new File(filePath);
				dir.mkdir();
			}
			zipIn.closeEntry();
			entry = zipIn.getNextEntry();
		}
		zipIn.close();
	}

	private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
		byte[] bytesIn = new byte[4096];
		int read = 0;
		while ((read = zipIn.read(bytesIn)) != -1) {
			bos.write(bytesIn, 0, read);
		}
		bos.close();
	}

}