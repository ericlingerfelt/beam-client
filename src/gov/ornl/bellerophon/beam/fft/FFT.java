package gov.ornl.bellerophon.beam.fft;

import org.jtransforms.fft.DoubleFFT_2D;
import org.jtransforms.fft.RealFFTUtils_2D;

public class FFT {

	public static void main(String[] args) {

		int PRINTDIM = 10;

		// retType = "FFT_REAL", "FFT_IMAGINARY", "FFT_MODULUS", "FFT_ARGUMENT";
		String retType = "FFT_MODULUS";
		
		// Input matrix from "Cleaned_Image" or "Removed_Noise"
		int NDIM = 1024;
		double[][] res = new double[NDIM][NDIM];
		
		// Only works when NDIM is a power of 2;
		double[][] res2D = new double[NDIM][NDIM];
		for ( int i=0; i<NDIM; i++)
			for ( int j=0; j<NDIM; j++)
				res2D[i][j] = res[i][j];
			
		perform2DFFT(res2D, "FORWARD", retType);
		
		System.out.println("\nJavaFFT_2D Output: ");
		for ( int i=0; i<PRINTDIM; i++) {
			for ( int j=0; j<PRINTDIM; j++)
				System.out.printf("%12.8f  ", res2D[i][j]);
			System.out.println();
		}
		System.out.println();
		
		// Input matrix from "Cleaned_Image" or "Removed_Noise"
		int DIM1 = 800;
		int DIM2 = 1000;
		res = new double[DIM1][DIM2];
		
		// Works for 2D matrix with arbitrary dimensionalities;
		double[][] res2D_Full = new double[DIM1][2*DIM2];
		for ( int i=0; i<DIM1; i++)
			for ( int j=0; j<DIM2; j++) {
				res2D_Full[i][j] = res[i][j];
				res2D_Full[i][DIM2+j] = 0.;
			}
			
		perform2DFFT(res2D_Full, "FORWARD_FULL", retType);
		
		System.out.println("\nJavaFFT_2D_Full Output: ");
		for ( int i=0; i<PRINTDIM; i++) {
			for ( int j=0; j<PRINTDIM; j++)
				System.out.printf("%12.8f  ", res2D_Full[i][j]);
			System.out.println();
		}
		System.out.println();
		
	}

	public static float[][]  fftshift(float [][] data, boolean bComplex, boolean bSign)
	{
		int step = 1;
		if (bComplex) step = 2;
		int height = data.length;

		int width = data[0].length/step;

		float [][] revan = new float [data.length][data[0].length];

		int pH = 0;
		int pW = 0;
		if(bSign) {
			pH = (int) Math.ceil(height/2.0);
			pW = (int) Math.ceil(width/2.0);
		}
		else{
			pH = (int) Math.floor(height/2.0);
			pW = (int) Math.floor(width/2.0);
		}
		int i=0;
		int j=0;
		if (step==1){
			for(j=pH;j<height;j++){
				for (i=pW;i<width;i++){				
					revan[j-pH][i-pW] = data[j][i];
				}
				for (i=0;i<pW;i++){
					revan[j-pH][i+width-pW] = data[j][i];
				}				
			}
			for(j=0;j<pH;j++){
				for (i=pW;i<width;i++){				
					revan[j+height-pH][i-pW] = data[j][i];
				}
				for (i=0;i<pW;i++){
					revan[j+height-pH][i+width-pW] = data[j][i];
				}				
			}
		}
		else{
			for(j=pH;j<height;j++){
				for (i=pW;i<width;i++){				
					revan[j-pH][(i-pW)*2] = data[j][i*2];
					revan[j-pH][(i-pW)*2+1] = data[j][i*2+1];
				}
				for (i=0;i<pW;i++){
					revan[j-pH][(i+width-pW)*2] = data[j][i*2];
					revan[j-pH][(i+width-pW)*2+1] = data[j][i*2+1];
				}				
			}
			for(j=0;j<pH;j++){
				for (i=pW;i<width;i++){				
					revan[j+height-pH][(i-pW)*2] = data[j][i*2];
					revan[j+height-pH][(i-pW)*2+1] = data[j][i*2+1];					
				}
				for (i=0;i<pW;i++){
					revan[j+height-pH][(i+width-pW)*2] = data[j][i*2];
					revan[j+height-pH][(i+width-pW)*2+1] = data[j][i*2+1];
				}				
			}		
		}			
		return revan;
	}

	public static double[][] fftshift(double [][] data, boolean bComplex, boolean bSign)
	{
		int step = 1;
		if (bComplex) step = 2;
		int height = data.length;

		int width = data[0].length/step;

		double [][] revan = new double [data.length][data[0].length];

		int pH = 0;
		int pW = 0;
		if(bSign) {
			pH = (int) Math.ceil(height/2.0);
			pW = (int) Math.ceil(width/2.0);
		}
		else{
			pH = (int) Math.floor(height/2.0);
			pW = (int) Math.floor(width/2.0);
		}
		int i=0;
		int j=0;
		if (step==1){
			for(j=pH;j<height;j++){
				for (i=pW;i<width;i++){				
					revan[j-pH][i-pW] = data[j][i];
				}
				for (i=0;i<pW;i++){
					revan[j-pH][i+width-pW] = data[j][i];
				}				
			}
			for(j=0;j<pH;j++){
				for (i=pW;i<width;i++){				
					revan[j+height-pH][i-pW] = data[j][i];
				}
				for (i=0;i<pW;i++){
					revan[j+height-pH][i+width-pW] = data[j][i];
				}				
			}
		}
		else{
			for(j=pH;j<height;j++){
				for (i=pW;i<width;i++){				
					revan[j-pH][(i-pW)*2] = data[j][i*2];
					revan[j-pH][(i-pW)*2+1] = data[j][i*2+1];
				}
				for (i=0;i<pW;i++){
					revan[j-pH][(i+width-pW)*2] = data[j][i*2];
					revan[j-pH][(i+width-pW)*2+1] = data[j][i*2+1];
				}				
			}
			for(j=0;j<pH;j++){
				for (i=pW;i<width;i++){				
					revan[j+height-pH][(i-pW)*2] = data[j][i*2];
					revan[j+height-pH][(i-pW)*2+1] = data[j][i*2+1];					
				}
				for (i=0;i<pW;i++){
					revan[j+height-pH][(i+width-pW)*2] = data[j][i*2];
					revan[j+height-pH][(i+width-pW)*2+1] = data[j][i*2+1];
				}				
			}		
		}			
		return revan;
	}
	
	public static void perform2DFFT (double[][] signal, String compMode, String retType) {
		
		int DIM1 = signal.length;;
		int DIM2 = signal[0].length;
		
		switch (compMode) {
		
			case "FORWARD":
				
				if( DIM1!=DIM2 ) { 
					System.out.println("Input matrix for FFT is not a square Matrix!");
					System.exit(1);
				}

				if( (DIM1 & (DIM1 - 1)) == 0 ) {
					double[][] container = new double[DIM1][DIM2];
					
					DoubleFFT_2D solver = new DoubleFFT_2D(DIM1, DIM2);
					solver.realForward(signal);
					
					RealFFTUtils_2D unpacker = new RealFFTUtils_2D(DIM1, DIM2);
					for( int k1=0; k1<DIM1; k1++)
						for( int k2=0; k2<DIM2; k2++) {
							double real = unpacker.unpack(k1, 2*k2, signal);
							double image = unpacker.unpack(k1, 2*k2+1, signal);
							switch (retType) {
								case "FFT_REAL":
									container[k1][k2] = real;
								break;
								
								case "FFT_IMAGINARY":
									container[k1][k2] = image;
								break;
								
								case "FFT_MODULUS":
									container[k1][k2] = Math.sqrt(real*real + image*image);
								break;
								
								case "FFT_ARGUMENT":
									container[k1][k2] = Math.atan2(image, real);
								break;
								
								default:
								break;
							}
						}
					for( int k1=0; k1<DIM1; k1++)
						for( int k2=0; k2<DIM2; k2++)
							signal[k1][k2] = container[k1][k2];
				} else {
					System.out.println("Input Matrix's dimensions are not powers of 2!");
				}
				
			break;
			
			case "FORWARD_FULL":
				
				DoubleFFT_2D solver = new DoubleFFT_2D(DIM1, DIM2/2);
				solver.realForwardFull(signal);
					
				for( int k1=0; k1<DIM1; k1++)
					for( int k2=0; k2<DIM2/2; k2++) {
						switch (retType) {
							case "FFT_REAL":
								signal[k1][k2] = signal[k1][2*k2];
							break;
							
							case "FFT_IMAGINARY":
								signal[k1][k2] = signal[k1][2*k2+1];
							break;
							
							case "FFT_MODULUS":
								signal[k1][k2] = Math.sqrt(signal[k1][2*k2]*signal[k1][2*k2] + signal[k1][2*k2+1]*signal[k1][2*k2+1]);
							break;
							
							case "FFT_ARGUMENT":
								signal[k1][k2] = Math.atan2(signal[k1][2*k2+1], signal[k1][2*k2]);
							break;
							
							default:
							break;
						}
					}
				
			break;
			
			default:
			break;
		}
	}
}