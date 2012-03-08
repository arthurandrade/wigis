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
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import net.wigis.graph.GraphsPathFilter;
import net.wigis.settings.Settings;
import au.com.bytecode.opencsv.CSVReader;

// TODO: Auto-generated Javadoc
/**
 * The Class SplitFrenchTextFile.
 * 
 * @author Brynjar Gretarsson
 */
public class SplitFrenchTextFile
{

	/**
	 * Split.
	 * 
	 * @param directory
	 *            the directory
	 * @param inputFile
	 *            the input file
	 * @param outputPrefix
	 *            the output prefix
	 * @param docsFile
	 *            the docs file
	 * @param characterMapFile
	 *            the character map file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void split( String directory, String inputFile, String outputPrefix, String docsFile, String characterMapFile ) throws IOException
	{
		Map<String, String> characterMap = readCharacterMap( directory, characterMapFile );
		File input = new File( directory, inputFile );
		BufferedReader br = new BufferedReader( new FileReader( input ) );
		String line;
		String nextFileName = null;
		StringBuilder contents = new StringBuilder();
		StringBuilder docs = new StringBuilder();
		while( ( line = br.readLine() ) != null )
		{
			if( line.startsWith( " " ) )
			{
				// System.out.println( line );
			}
			if( line.trim().length() <= 1 )
			{
				line = br.readLine();
				if( line != null )
				{
					if( nextFileName != null )
					{
						writeFile( directory + outputPrefix, nextFileName, contents );
						contents = new StringBuilder();
						docs.append( outputPrefix ).append( nextFileName ).append( "\n" );
					}
					line = replaceCharacters( line, characterMap );
					nextFileName = line.trim() + ".txt";
					contents.append( line ).append( "\n" );
				}
			}
			line = replaceCharacters( line, characterMap );
			contents.append( line ).append( "\n" );
		}

		// write the last section
		if( nextFileName != null )
		{
			writeFile( directory + outputPrefix, nextFileName, contents );
		}

		// write the list of documents
		writeFile( directory, docsFile, docs );

		br.close();
	}

	/**
	 * Replace characters.
	 * 
	 * @param input
	 *            the input
	 * @param characterMap
	 *            the character map
	 * @return the string
	 */
	private static String replaceCharacters( String input, Map<String, String> characterMap )
	{
		String output = input;

		for( String key : characterMap.keySet() )
		{
			output = output.replaceAll( key, characterMap.get( key ) );
		}

		return output;
	}

	/**
	 * Read character map.
	 * 
	 * @param directory
	 *            the directory
	 * @param characterMapFile
	 *            the character map file
	 * @return the map
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private static Map<String, String> readCharacterMap( String directory, String characterMapFile ) throws IOException
	{
		CSVReader csv = new CSVReader( new FileReader( new File( directory, characterMapFile ) ) );

		String[] line = null;
		Map<String, String> characterMap = new HashMap<String, String>();
		while( ( line = csv.readNext() ) != null )
		{
			if( line.length >= 2 )
			{
				System.out.println( line[0] + "->" + line[1] );
				characterMap.put( line[0], line[1] );
			}
		}

		return characterMap;
	}

	/**
	 * Write file.
	 * 
	 * @param directory
	 *            the directory
	 * @param fileName
	 *            the file name
	 * @param contents
	 *            the contents
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private static void writeFile( String directory, String fileName, StringBuilder contents ) throws IOException
	{
		System.out.println( "Writing file " + fileName );
		BufferedWriter bw = new BufferedWriter( new FileWriter( new File( directory, fileName ) ) );
		bw.write( contents.toString() );
		bw.close();
	}

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 * @throws ServletException
	 *             the servlet exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void main( String args[] ) throws ServletException, IOException
	{
		GraphsPathFilter gpf = new GraphsPathFilter();
		gpf.init( null );
		String directory = Settings.GRAPHS_PATH + "topicVisualizations/french-1/";
		String outputPrefix = "data/";
		String inputFile = "XVIII - Laclos - Les liaisons dangereuses.doc.txt";
		String docsFile = "docs.txt";
		String characterMapFile = "characterMap.csv";
		split( directory, inputFile, outputPrefix, docsFile, characterMapFile );

		// Map<String,String> characterMap = readCharacterMap( directory,
		// characterMapFile );
		// BufferedReader br = new BufferedReader( new FileReader( new File(
		// directory, "stopwords.txt" ) ) );
		// String line;
		// StringBuilder contents = new StringBuilder();
		// while( (line = br.readLine()) != null )
		// {
		// line = replaceCharacters( line, characterMap );
		// contents.append( line ).append( "\n" );
		// }
		// br.close();
		//		
		// writeFile( directory, "stopwords.txt", contents );
		// readCharacterMap( directory, characterMap );
	}
}
