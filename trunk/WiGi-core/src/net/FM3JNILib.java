package net;

public class FM3JNILib {
	static 
	{
		if(!AllLoadedNativeLibrariesInJVM.listAllLoadedNativeLibrariesFromJVM().contains("FM3JNILib.dll")){
			System.loadLibrary("FM3JNILib"); 
		}
	}
	public native static float[] runFM3(int dim, int[] edgeIndexes, int edgenum);
}
