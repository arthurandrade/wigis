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

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import net.wigis.settings.Settings;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

// TODO: Auto-generated Javadoc
/**
 * The Class ConvertTouchGraphCSVToManyEyes.
 * 
 * @author Brynjar Gretarsson
 */
public class ConvertTouchGraphCSVToManyEyes
{

	/**
	 * Convert.
	 * 
	 * @param inputFilename
	 *            the input filename
	 * @param outputFilename
	 *            the output filename
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void convert( String inputFilename, String outputFilename ) throws IOException
	{
		FileReader fr = new FileReader( inputFilename );
		CSVReader reader = new CSVReader( fr );
		FileWriter fw = new FileWriter( outputFilename );
		CSVWriter writer = new CSVWriter( fw );

		// Skip changing the first line
		String[] line = reader.readNext();
		writer.writeNext( line );

		while( ( line = reader.readNext() ) != null )
		{
			for( int i = 0; i < line.length; i++ )
			{
				line[i] = "a" + line[i];
			}
			writer.writeNext( line );
		}

		reader.close();
		fr.close();
		writer.close();
		fw.close();
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
		String path = Settings.GRAPHS_PATH + "touchgraph\\";
		String[] files = { "smallworld_10_20_NODES.csv", "smallworld_100_200_NODES.csv", "smallworld_1000_2000_NODES.csv",
				"smallworld_10000_20000_NODES.csv", "smallworld_100000_200000_NODES.csv", "smallworld_1000000_2000000_NODES.csv", };

		for( int i = 0; i < files.length; i++ )
		{
			convert( path + files[i], path + "ManyEyes" + files[i] );
		}
	}
}
