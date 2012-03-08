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
 * The Class SplitNSCTAFile.
 * 
 * @author Brynjar Gretarsson
 */
public class SplitNSCTAFile
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
			int newSection = isNewSection( line );
			if( newSection == 1 )
			{
				// line = br.readLine();
				if( line != null )
				{
					if( nextFileName != null )
					{
						writeFile( directory + outputPrefix, nextFileName, contents );
						docs.append( outputPrefix ).append( nextFileName ).append( "\n" );
					}
					contents = new StringBuilder();
					line = replaceCharacters( line, characterMap );
					nextFileName = line.trim() + ".txt";
					contents.append( line ).append( "\n" );
				}
			}
			else if( newSection == 2 )
			{
				if( nextFileName != null )
				{
					writeFile( directory + outputPrefix, nextFileName, contents );
					docs.append( outputPrefix ).append( nextFileName ).append( "\n" );
				}
				nextFileName = null;
				contents = new StringBuilder();
			}
			else
			{
				line = replaceCharacters( line, characterMap );
				contents.append( line ).append( "\n" );
			}
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
	 * Checks if is new section.
	 * 
	 * @param line
	 *            the line
	 * @return the int
	 */
	private static int isNewSection( String line )
	{
		String foundToken = "[FOUND]";
		String taskRegex = "[0-9]+.[0-9]+.[0-9]+ Task";
		String sectionRegex = "[0-9]+.[0-9]+.[0-9]+";
		String tempLine = line.replaceFirst( taskRegex, foundToken );
		if( tempLine.startsWith( foundToken ) )
		{
			System.out.println( line );
			return 1;
		}
		else
		{
			tempLine = line.replaceFirst( sectionRegex, foundToken );
			if( tempLine.startsWith( foundToken ) )
			{
				System.out.println( line );
				return 2;
			}
		}

		return 0;
	}

	// private static String lastSection = "";
	// private static boolean isNewSection( String line )
	// {
	// String[] lineArray = line.split( " " );
	// if( lineArray.length > 0 )
	// {
	// String checkString = lineArray[0];
	// if( lastSection.equals( "" ) )
	// {
	// if( checkString.equals( "1." ) )
	// {
	// lastSection = checkString;
	// return true;
	// }
	// }
	// else
	// {
	// String[] numbers = checkString.split( "." );
	// int[] values = new int[numbers.length];
	// for( int i = 0; i < numbers.length; i++ )
	// {
	// if( numbers[i] != null && numbers[i] != "" )
	// {
	// if( !isNumber( numbers[i] ) )
	// {
	// return false;
	// }
	//						 
	// values[i] = Integer.parseInt( numbers[i] );
	// }
	// }
	// }
	// }
	// return false;
	// }

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

		if( output != null )
		{
			for( String key : characterMap.keySet() )
			{
				output = output.replaceAll( key, characterMap.get( key ) );
			}
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
		fileName = fileName.replaceAll( "/", "" );
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
		String directory = Settings.GRAPHS_PATH + "topicVisualizations/NS_CTA/";
		String outputPrefix = "data/";
		String inputFile = "NS_CTA_IPP_v1.4.txt";
		String docsFile = "docs.txt";
		String characterMapFile = "characterMap.csv";
		new File( directory, outputPrefix ).mkdirs();
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
