/******************************************************************************************************
 * Copyright (c) 2010, University of California, Santa Barbara
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are 
 * permitted provided that the following conditions are met:
 * 
 *    * Redistributions of source code must retain the above copyright notice, this list of
 *      conditions and the following disclaimer.
 *    * Redistributions in binary form must reproduce the above copyright notice, this list of
 *      conditions and the following disclaimer in the documentation and/or other materials 
 *      provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 *****************************************************************************************************/

package net.wigis.graph.data.utilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import net.wigis.graph.GraphsPathFilter;
import net.wigis.settings.Settings;

// TODO: Auto-generated Javadoc
/**
 * The Class GenerateDocsFileForDirectory.
 * 
 * @author Brynjar Gretarsson
 */
public class GenerateDocsFileForDirectory
{

	/**
	 * Process directory.
	 * 
	 * @param rootDir
	 *            the root dir
	 * @param dataDir
	 *            the data dir
	 * @param fileExtension
	 *            the file extension
	 * @param listingsFile
	 *            the listings file
	 */
	public static void processDirectory( String rootDir, String dataDir, String fileExtension, String listingsFile )
	{
		File dir = new File( rootDir, dataDir );
		String currentPath = "";
		File output = new File( rootDir, listingsFile );
		try
		{
			FileWriter fw = new FileWriter( output );
			BufferedWriter bw = new BufferedWriter( fw );

			handleDir( dir, currentPath, fileExtension, bw );

			bw.close();
			fw.close();
		}
		catch( IOException ioe )
		{
			ioe.printStackTrace();
		}
	}

	/**
	 * Handle dir.
	 * 
	 * @param fileOrDir
	 *            the file or dir
	 * @param currentPath
	 *            the current path
	 * @param fileExtension
	 *            the file extension
	 * @param bw
	 *            the bw
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private static void handleDir( File fileOrDir, String currentPath, String fileExtension, BufferedWriter bw ) throws IOException
	{
		if( fileOrDir.isDirectory() )
		{
			currentPath += fileOrDir.getName() + "/";
			String list[] = fileOrDir.list();
			for( int i = 0; i < list.length; i++ )
			{
				File nextFileOrDir = new File( fileOrDir.getAbsolutePath(), list[i] );
				handleDir( nextFileOrDir, currentPath, fileExtension, bw );
			}
		}
		else if( fileOrDir.getName().endsWith( fileExtension ) )
		{
			bw.write( currentPath + fileOrDir.getName() + "\n" );
		}
	}

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main( String args[] )
	{
		GraphsPathFilter.init();
		String directory = Settings.GRAPHS_PATH + "topicVisualizations" + "/" + "kdd-data-2011-01-10" + "/";
		processDirectory( directory, "documents", ".txt", "docs.txt" );
	}
}
