package net;

public class FM3JNILib {
	static 
	{
	    System.loadLibrary("FM3JNILib"); 
	}
	public native static float[] runFM3(int dim, int[] edgeIndexes, int edgenum);
}
