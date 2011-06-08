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
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import com.ibm.icu.util.StringTokenizer;

// TODO: Auto-generated Javadoc
/**
 * The Class SplitAndFormatLatexFile.
 * 
 * @author Brynjar Gretarsson
 */
public class SplitAndFormatLatexFile
{

	/**
	 * Split and format all.
	 * 
	 * @param inputDirectory
	 *            the input directory
	 * @param outputDirectory
	 *            the output directory
	 * @param executionDirectory
	 *            the execution directory
	 * @param docsFileName
	 *            the docs file name
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void splitAndFormatAll( String inputDirectory, String outputDirectory, String executionDirectory, String docsFileName )
			throws IOException
	{
		File dir = new File( inputDirectory );
		File[] files = dir.listFiles();
		if( !outputDirectory.endsWith( "\\" ) && !outputDirectory.endsWith( "/" ) )
		{
			outputDirectory += "/";
		}
		for( int i = 0; i < files.length; i++ )
		{
			if( !files[i].getName().startsWith( "." ) )
			{
				splitAndFormat( inputDirectory, outputDirectory + files[i].getName(), files[i].getName(), docsFileName );
				RemoveLatexTags.remove( new File( outputDirectory + files[i].getName(), docsFileName ) );
				runResampling( executionDirectory, outputDirectory + files[i].getName(), 50 );
			}
		}
	}

	/**
	 * Run resampling.
	 * 
	 * @param directory
	 *            the directory
	 * @param outputDirectory
	 *            the output directory
	 * @param numberOfTopics
	 *            the number of topics
	 */
	public static void runResampling( String directory, String outputDirectory, int numberOfTopics )
	{
		Resampling r = new Resampling( directory, outputDirectory, numberOfTopics );
		r.start();
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
		String executionDirectory = "/graphs/topicVisualizations/ucsb_papers/";
		processDirectory( executionDirectory );
	}

	/**
	 * Process directory.
	 * 
	 * @param executionDirectory
	 *            the execution directory
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void processDirectory( String executionDirectory ) throws IOException
	{
		String inputDirectory = executionDirectory + "original/";
		String outputDirectory = executionDirectory + "split/";
		new File( outputDirectory ).delete();
		String docsFileName = "currentDoc.txt";
		splitAndFormatAll( inputDirectory, outputDirectory, executionDirectory, docsFileName );
	}

	/**
	 * Split and format.
	 * 
	 * @param directory
	 *            the directory
	 * @param inputFileName
	 *            the input file name
	 * @param docsFileName
	 *            the docs file name
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void splitAndFormat( String directory, String inputFileName, String docsFileName ) throws IOException
	{
		splitAndFormat( directory, directory, inputFileName, docsFileName );
	}

	/**
	 * Split and format.
	 * 
	 * @param inputDirectory
	 *            the input directory
	 * @param outputDirectory
	 *            the output directory
	 * @param inputFileName
	 *            the input file name
	 * @param docsFileName
	 *            the docs file name
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void splitAndFormat( String inputDirectory, String outputDirectory, String inputFileName, String docsFileName ) throws IOException
	{
		if( !inputDirectory.endsWith( "\\" ) && !inputDirectory.endsWith( "/" ) )
		{
			inputDirectory += "/";
		}
		if( !outputDirectory.endsWith( "\\" ) && !outputDirectory.endsWith( "/" ) )
		{
			outputDirectory += "/";
		}
		new File( outputDirectory ).mkdirs();
		File file = new File( inputDirectory + inputFileName );
		try
		{
			String docref = "";
			int chapterCounter = 0;
			int abstractCounter = 0;
			int sectionCounter = 0;
			int subsectionCounter = 0;
			int subsubsectionCounter = 0;
			String currentSection = "";
			Scanner scanner = new Scanner( file );
			String section = "";
			String newfilename = "";
			while( scanner.hasNextLine() )
			{
				String line = scanner.nextLine();
				// remove latex special syntax
				currentSection = cleanLine( currentSection, line );

				if( line.trim().toLowerCase().startsWith( "\\section" ) || line.trim().toLowerCase().startsWith( "\\chapter" )
						|| line.trim().toLowerCase().startsWith( "\\subsection" ) || line.trim().toLowerCase().startsWith( "\\subsubsection" )
						|| line.trim().toLowerCase().startsWith( "\\firstsection" ) || line.trim().toLowerCase().startsWith( "\\abstract" ) )
				{
					// if its a section, get the section title and create a
					// new file
					if( chapterCounter > 0 || sectionCounter > 0 )
					{
						writeFile( outputDirectory, newfilename, currentSection );
					}
					currentSection = "";
					if( line.trim().toLowerCase().startsWith( "\\chapter" ) )
					{
						chapterCounter++;
						sectionCounter = 0;
						subsectionCounter = 0;
						subsubsectionCounter = 0;
					}
					else if( line.trim().toLowerCase().startsWith( "\\abstract" ) )
					{
						abstractCounter++;
					}
					else if( line.trim().toLowerCase().startsWith( "\\section" ) || line.trim().toLowerCase().startsWith( "\\firstsection" ) )
					{
						sectionCounter++;
						subsectionCounter = 0;
						subsubsectionCounter = 0;
					}
					else if( line.trim().toLowerCase().startsWith( "\\subsection" ) )
					{
						subsectionCounter++;
						subsubsectionCounter = 0;
					}
					else if( line.trim().toLowerCase().startsWith( "\\subsubsection" ) )
					{
						subsubsectionCounter++;
					}

					section = "";
					if( chapterCounter > 0 )
					{
						section += chapterCounter;
					}
					if( abstractCounter > 0 && sectionCounter == 0 )
					{
						section += "ABSTRACT";
					}
					if( sectionCounter > 0 )
					{
						if( section.equals( "" ) )
						{
							section += sectionCounter;
						}
						else
						{
							section += "." + sectionCounter;
						}
					}
					if( subsectionCounter > 0 )
					{
						section += "." + subsectionCounter;
					}
					if( subsubsectionCounter > 0 )
					{
						section += "." + subsubsectionCounter;
					}
					if( !section.equals( "ABSTRACT" ) )
					{
						if( line.contains( "}" ) )
						{
							line = line.substring( line.indexOf( "{" ) + 1, line.lastIndexOf( "}" ) );
						}
						else
						{
							if( line.contains( "{" ) )
							{
								line = line.substring( line.indexOf( "{" ) );
							}
						}
					}
					String temp = "";
					temp = cleanLine( temp, line );
					// can't have ':' in file name
					temp = temp.replaceAll( ":", "-" );
					temp = temp.replaceAll( "/", "" );
					String removeToken = "\\";
					String replaceToken = "";
					temp = StringUtils.replaceAll( temp, removeToken, replaceToken );
					temp = StringUtils.replaceAll( temp, "{", "" );
					temp = StringUtils.replaceAll( temp, "}", "" );
					temp = StringUtils.replaceAll( temp, "$", "" );
					temp = StringUtils.replaceAll( temp, "\"", "" );
					temp = temp.trim();
					temp = StringUtils.replaceAll( temp, " ", "_" );
					section += "-" + temp;
					newfilename = section + ".txt";
					docref = docref + outputDirectory + newfilename + "\n";
				}
			}
			if( newfilename.equals( "" ) )
			{
				newfilename = "document.txt";
				docref += outputDirectory + newfilename + "\n";
			}
			writeFile( outputDirectory, newfilename, currentSection );
			writeFile( outputDirectory, docsFileName, docref );
		}
		catch( FileNotFoundException e )
		{
			e.printStackTrace();
		}

	}

	/**
	 * Clean line.
	 * 
	 * @param currentSection
	 *            the current section
	 * @param line
	 *            the line
	 * @return the string
	 */
	private static String cleanLine( String currentSection, String line )
	{
		StringTokenizer lineTokenizer = new StringTokenizer( line, " \t\n\r\f{}%", true );
		String token;
		while( lineTokenizer.hasMoreTokens() )
		{
			token = lineTokenizer.nextToken();
			if( token.equals( "%" ) )
			{
				while( lineTokenizer.hasMoreTokens() && !lineTokenizer.nextToken().equals( "\n" ) )
				{
					// Skip everything to the end of the line
				}
				currentSection += "\n";
			}
			else if( token.startsWith( "\\" ) )
			{
				if( ( token.equals( "\\cite" ) || token.equals( "\\ref" ) || token.equals( "\\begin" ) || token.equals( "\\end" ) || token
						.equals( "\\label" ) )
						&& lineTokenizer.hasMoreTokens() )
				{
					token = lineTokenizer.nextToken();
					if( token.equals( "{" ) )
					{
						while( lineTokenizer.hasMoreTokens() && !lineTokenizer.nextToken().equals( "}" ) )
						{
							// Skip what's between { and }
						}
					}
				}
				if( token.equals( "\\textit" ) || token.equals( "\\emph" ) )
				{
					token = lineTokenizer.nextToken();
					if( token.equals( "{" ) )
					{
						while( lineTokenizer.hasMoreTokens() && !token.equals( "}" ) )
						{
							currentSection += token;
							token = lineTokenizer.nextToken();
						}
					}
				}
			}
			else
			{
				currentSection += token;
			}
		}
		return currentSection;
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
}
