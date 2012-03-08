package net;

public class JNILib {
	static 
	{
	    System.loadLibrary("JNILib"); 
	}
	public native static void initMatrix(int dim, double alpha);
	public native static void passValue(int row, int neighborCnt, int[] neighbors);
	public native static void decomMatrix();
	public native static double[] LUSolver(double[] vec);
}
