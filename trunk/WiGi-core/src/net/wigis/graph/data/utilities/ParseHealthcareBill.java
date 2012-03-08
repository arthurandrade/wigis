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
import java.io.Writer;

import net.wigis.settings.Settings;

// TODO: Auto-generated Javadoc
/**
 * The Class ParseHealthcareBill.
 * 
 * @author johno
 * @author Brynjar Gretarsson
 */
public class ParseHealthcareBill
{

	/**
	 * Fetch the entire contents of a text file, and return it in a String. This
	 * style of implementation does not throw Exceptions to the caller.
	 * 
	 * @param aFile
	 *            is a file which already exists and can be read.
	 * @param outputDirectory
	 *            the output directory
	 * @param outputFilePrefix
	 *            the output file prefix
	 * @param docsFile
	 *            the docs file
	 * @param delimiter
	 *            the delimiter
	 * @return the contents
	 */
	static public void getContents( String aFile, String outputDirectory, String outputFilePrefix, String docsFile, String delimiter )
	{
		// ...checks on aFile are elided
		StringBuilder contents = new StringBuilder();
		StringBuilder fileList = new StringBuilder();
		try
		{
			// use buffering, reading one line at a time
			// FileReader always assumes default encoding is OK!
			BufferedReader input = new BufferedReader( new FileReader( aFile ) );
			try
			{
				String line = null; // not declared within while loop
				/*
				 * readLine is a bit quirky : it returns the content of a line
				 * MINUS the newline. it returns null only for the END of the
				 * stream. it returns an empty String if two newlines appear in
				 * a row.
				 */
				String nextFile = "";
				while( ( line = input.readLine() ) != null )
				{
					// remove numbering from the start of each line
					if( line.indexOf( ' ' ) != -1 )
					{
						// logger("line is: " +line);
						String num = line.substring( 0, line.indexOf( ' ' ) ).trim();
						String clean = "";
						try
						{
							Integer.parseInt( num );
							// wont continue unless its a number
							clean = line.substring( num.length(), line.length() );
						}
						catch( NumberFormatException e )
						{
							// TODO Auto-generated catch block
							// System.out.println("Looks like no number: " +
							// line);
							clean = line;
							// e.printStackTrace();
						}

						// now identify if the line is a section title
						clean = removeFrenchCharacters( clean );
						clean = clean.replaceAll( "ë", " " );
						clean = clean.replaceAll( "ø", " " );
						clean = clean.replaceAll( "¿", " " );
						clean = clean.replaceAll( "í", "" );
						clean = clean.trim();

						// logger(clean);
						if( clean.startsWith( delimiter ) )
						{
							// logger( "SECTION HEADER FOUND: " + clean );
							// sections.add(contents.toString());
							if( !nextFile.equals( "" ) )
							{
								String dirpath = outputDirectory + nextFile;
								new File( outputDirectory + outputFilePrefix ).mkdirs();
								Writer output = new BufferedWriter( new FileWriter( dirpath ) );
								try
								{
									// FileWriter always assumes default
									// encoding is
									// OK!

									output.write( contents.toString() );
									fileList.append( nextFile + "\n" );
								}
								finally
								{
									output.close();
								}
							}

							nextFile = outputFilePrefix + clean.substring( 0, Math.min( 15, clean.length() ) ).trim() + ".txt";
							while( nextFile.contains( ".." ) )
							{
								nextFile.replaceAll( "..", "." );
							}

							contents = new StringBuilder();
						}

						contents.append( clean );
						contents.append( System.getProperty( "line.separator" ) );
					}
				}
				Writer output = new BufferedWriter( new FileWriter( outputDirectory + docsFile ) );
				try
				{
					output.write( fileList.toString() );
				}
				catch( IOException e )
				{
					e.printStackTrace();
				}
				finally
				{
					output.close();
				}
			}
			finally
			{
				input.close();
			}
		}
		catch( IOException ex )
		{
			ex.printStackTrace();
		}

	}

	/** The characters. */
	private static String characters[] = { "À", "Â", "Ä", "È", "É", "Ê", "Ë", "Î", "Ï", "Ô", "Œ", "Ù", "Û", "Ü", "Ÿ", "à", "â", "ä", "è", "é", "ê",
			"ë", "î", "ï", "ô", "œ", "ù", "û", "ü", "ÿ", "Ç", "ç", "«", "»" };

	/** The replacements. */
	private static String replacements[] = { "A", "A", "A", "E", "E", "E", "E", "I", "I", "O", "OE", "U", "U", "U", "Y", "a", "a", "a", "e", "e",
			"e", "e", "i", "i", "o", "oe", "u", "u", "u", "y", "C", "c", "'", "'" };

	/**
	 * Removes the french characters.
	 * 
	 * @param input
	 *            the input
	 * @return the string
	 */
	private static String removeFrenchCharacters( String input )
	{
		String output = input;

		for( int i = 0; i < characters.length; i++ )
		{
			output = output.replaceAll( characters[i], replacements[i] );
		}

		return output;
	}

	/**
	 * Simple test harness.
	 * 
	 * @param aArguments
	 *            the arguments
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void main( String[] aArguments ) throws IOException
	{
		/*
		 * String outputDirectory = Settings.GRAPHS_PATH +
		 * "topicVisualizations/health/"; String inputFile =
		 * Settings.GRAPHS_PATH + "topicVisualizations/health.txt"; String
		 * outputFilePrefix = "sections/"; String docsFile = "docs.txt";
		 * getContents( inputFile, outputDirectory, outputFilePrefix, docsFile,
		 * "SEC." );
		 */
		String outputDirectory = Settings.GRAPHS_PATH + "topicVisualizations/french/";
		String inputFile = Settings.GRAPHS_PATH
				+ "topicVisualizations/french/data/2245-PIERRE_CHODERLOS_DE_LACLOS-Les_liaisons_dangereuses-[InLibroVeritas.net].txt";
		String outputFilePrefix = "data/";
		String docsFile = "docs.txt";
		getContents( inputFile, outputDirectory, outputFilePrefix, docsFile, "LETTRE" );

		// System.out.println("New file contents: " + getContents(testFile));
	}

	/**
	 * Logger.
	 * 
	 * @param s
	 *            the s
	 */
	public static void logger( String s )
	{
		System.out.println( "BillParser: " + s );
	}
}
