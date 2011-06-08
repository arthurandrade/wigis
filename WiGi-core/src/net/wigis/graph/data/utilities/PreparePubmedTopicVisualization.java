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

import net.wigis.settings.Settings;

// TODO: Auto-generated Javadoc
/**
 * The Class PreparePubmedTopicVisualization.
 * 
 * @author Brynjar Gretarsson
 */
public class PreparePubmedTopicVisualization
{

	/**
	 * Prepare.
	 * 
	 * @param directory
	 *            the directory
	 * @param textFile
	 *            the text file
	 * @param docsFile
	 *            the docs file
	 */
	public static void prepare( String directory, String textFile, String docsFile )
	{
		String outputDir = "cleaned" + "/";
		String outputDirectory = directory + outputDir;
		File file = new File( directory, textFile );
		FileReader fr;
		try
		{
			fr = new FileReader( file );
			BufferedReader br = new BufferedReader( fr );
			String line;
			StringBuilder currentContents = new StringBuilder();
			StringBuilder docsFileContents = new StringBuilder();
			String currentFileName = "";
			while( ( line = br.readLine() ) != null )
			{
				currentContents.append( line ).append( "\n" );
				if( line.startsWith( "<PMID>" ) )
				{
					currentFileName = line.substring( 6, line.indexOf( "</PMID>" ) );
				}
				else if( line.startsWith( "ZZYEAR" ) )
				{
					currentFileName = line.substring( 6 ).trim() + "-" + currentFileName;
				}
				else if( line.startsWith( "###EOF###" ) )
				{
					docsFileContents.append( outputDir ).append( currentFileName ).append( "\n" );
					writeFile( outputDirectory, currentFileName, currentContents.toString() );
					currentContents = new StringBuilder();
				}
			}

			writeFile( directory, docsFile, docsFileContents.toString() );

			br.close();
			fr.close();
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
	}

	/**
	 * Merge files.
	 * 
	 * @param path
	 *            the path
	 * @param prefix
	 *            the prefix
	 * @param filename1
	 *            the filename1
	 * @param filename2
	 *            the filename2
	 * @param outputFilename
	 *            the output filename
	 */
	public static void mergeFiles( String path, String prefix, String filename1, String filename2, String outputFilename )
	{
		File file1 = new File( path, filename1 );
		File file2 = new File( path, filename2 );
		File output = new File( path, outputFilename );

		try
		{
			FileReader fr1 = new FileReader( file1 );
			BufferedReader br1 = new BufferedReader( fr1 );
			FileReader fr2 = new FileReader( file2 );
			BufferedReader br2 = new BufferedReader( fr2 );
			FileWriter fw = new FileWriter( output );
			BufferedWriter bw = new BufferedWriter( fw );

			String line1;
			String line2;

			while( ( line1 = br1.readLine() ) != null && ( line2 = br2.readLine() ) != null )
			{
				bw.write( prefix + line1 + "-" + line2 + "\n" );
			}

			bw.close();
			fw.close();

			br1.close();
			fr1.close();

			br2.close();
			fr2.close();
		}
		catch( IOException ioe )
		{
			ioe.printStackTrace();
		}
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
	 */
	public static void main( String args[] )
	{
		String directory = Settings.GRAPHS_PATH + "topicVisualizations" + "/" + "pubmed_influenza_subset" + "/";
		// String textFile = "pubmed.influenza.txt";
		String docsFile = "docs.txt";
		// prepare( directory, textFile, docsFile );

		mergeFiles( directory, "cleaned/", "year.subset.txt", "pmid.subset.txt", docsFile );
	}
}
