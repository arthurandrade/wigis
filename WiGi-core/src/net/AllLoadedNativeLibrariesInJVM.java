package net;

import java.util.List;

/***
 * This class extracts all native libraries  loaded in the JVM
 */
public class AllLoadedNativeLibrariesInJVM {
	public static List<String> listAllLoadedNativeLibrariesFromJVM() {
		ClassLoader appLoader = ClassLoader.getSystemClassLoader();
		ClassLoader currentLoader = AllLoadedNativeLibrariesInJVM.class.getClassLoader();


		ClassLoader[] loaders = new ClassLoader[] { appLoader, currentLoader };
		final List<String> libraries = ClassScope.getLoadedLibraries(loaders);
		/*for (String library : libraries) {
			System.out.println(library);
		}*/
		return libraries;
	}

	public static void main(String[] args) throws Exception {
		listAllLoadedNativeLibrariesFromJVM();
	}
}
