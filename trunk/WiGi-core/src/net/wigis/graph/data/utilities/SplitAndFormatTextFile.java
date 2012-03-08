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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

// TODO: Auto-generated Javadoc
/**
 * The Class SplitAndFormatTextFile.
 * 
 * @author Brynjar Gretarsson
 */
public class SplitAndFormatTextFile
{

	/**
	 * Split and format.
	 * 
	 * @param directory
	 *            the directory
	 * @param inputFileName
	 *            the input file name
	 * @param outputFileName
	 *            the output file name
	 * @param docsFileName
	 *            the docs file name
	 * @param linesPerFile
	 *            the lines per file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void splitAndFormat( String directory, String inputFileName, String outputFileName, String docsFileName, int linesPerFile )
			throws IOException
	{
		if( !directory.endsWith( "\\" ) && !directory.endsWith( "/" ) )
		{
			directory += "/";
		}
		FileReader fr = new FileReader( directory + inputFileName );
		BufferedReader br = new BufferedReader( fr );

		FileWriter fw = new FileWriter( directory + docsFileName );
		BufferedWriter bw = new BufferedWriter( fw );

		int docCounter = 0;
		int lineCounter = 0;
		String line;
		String currentDoc = "";
		while( ( line = br.readLine() ) != null )
		{
			lineCounter++;

			if( line.endsWith( "-" ) )
			{
				line = line.substring( 0, line.lastIndexOf( "-" ) );
			}

			line += "\n";

			currentDoc += line;

			if( lineCounter % linesPerFile == 0 )
			{
				docCounter++;

				FileWriter docWriter = new FileWriter( directory + outputFileName + docCounter + ".txt" );
				BufferedWriter bufferedDocWriter = new BufferedWriter( docWriter );

				bufferedDocWriter.write( currentDoc );

				bufferedDocWriter.close();
				docWriter.close();

				bw.write( outputFileName + docCounter + ".txt" + "\n" );

				currentDoc = "";
			}
		}

		bw.close();
		fw.close();

		br.close();
		fr.close();
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
		String directory = "/graphs/topicVisualizations/jod_phd/";
		String inputFileName = "phd_thesis_txt_format.txt";
		String outputFileName = "thesis";
		String docsFileName = "docs.txt";

		SplitAndFormatTextFile.splitAndFormat( directory, inputFileName, outputFileName, docsFileName, 100 );
	}
}
