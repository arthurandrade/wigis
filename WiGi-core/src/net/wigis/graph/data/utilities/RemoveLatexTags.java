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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import net.wigis.graph.data.uploader.TopicFileUploadBean;
import net.wigis.graph.dnv.utilities.Commands;
import net.wigis.settings.Settings;

// TODO: Auto-generated Javadoc
/**
 * The Class RemoveLatexTags.
 * 
 * @author Brynjar Gretarsson
 */
public class RemoveLatexTags
{

	/**
	 * Removes the all.
	 * 
	 * @param directory
	 *            the directory
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void removeAll( String directory ) throws IOException
	{
		if( !directory.endsWith( "\\" ) && !directory.endsWith( "/" ) )
		{
			directory += "/";
		}
		File dir = new File( directory + "original" );
		File[] files = dir.listFiles();
		String docs = "";
		for( int i = 0; i < files.length; i++ )
		{
			if( !files[i].getName().startsWith( "." ) )
			{
				remove( directory + "original", directory + "cleaned", files[i].getName(), files[i].getName() );
				docs += "cleaned" + "/" + files[i].getName() + "\n";
			}
		}
		writeFile( directory, "docs.txt", docs );
	}

	/**
	 * Removes the.
	 * 
	 * @param directory
	 *            the directory
	 * @param inputFileName
	 *            the input file name
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void remove( String directory, String inputFileName ) throws IOException
	{
		remove( directory, directory + "cleaned", inputFileName, inputFileName + ".txt" );
	}

	/**
	 * Removes the.
	 * 
	 * @param directory
	 *            the directory
	 * @param outputDirectory
	 *            the output directory
	 * @param inputFileName
	 *            the input file name
	 * @param outputFileName
	 *            the output file name
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void remove( String directory, String outputDirectory, String inputFileName, String outputFileName ) throws IOException
	{
		if( !directory.endsWith( "\\" ) && !directory.endsWith( "/" ) && !directory.equals( "" ) )
		{
			directory += "/";
		}
		if( !outputDirectory.endsWith( "\\" ) && !outputDirectory.endsWith( "/" ) && !outputDirectory.equals( "" ) )
		{
			outputDirectory += "/";
		}

		new File( outputDirectory ).mkdirs();
		String prefix = "";
		if( File.separator.equals( "/" ) )
		{
			prefix = "/opt/local/bin/";
		}
		String output = Commands.runSystemCommand( prefix + "detex " + inputFileName, directory );

		writeFile( outputDirectory, outputFileName, output );
	}

	/**
	 * Write file.
	 * 
	 * @param path
	 *            the path
	 * @param name
	 *            the name
	 * @param content
	 *            the content
	 */
	public static void writeFile( String path, String name, String content )
	{
		try
		{
			new File( path ).mkdirs();
			BufferedWriter out = new BufferedWriter( new FileWriter( path + name ) );
			System.out.println( "writing file:" + path + name );
			out.write( content );
			out.close();
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
	}

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void main( String args[] ) throws IOException
	{
		String directory = Settings.GRAPHS_PATH + "topicVisualizations/2003/";
		removeAll( directory );
		TopicFileUploadBean.runTopicDetection( directory, 50, true, 4 );
		SplitAndFormatLatexFile.processDirectory( directory );
	}

	/**
	 * Removes the.
	 * 
	 * @param file
	 *            the file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void remove( File file ) throws IOException
	{
		FileReader fr = new FileReader( file );
		BufferedReader br = new BufferedReader( fr );
		// String directory = file.getParent();
		String line;
		while( ( line = br.readLine() ) != null )
		{
			remove( "", "", line, line );
		}
	}

}
