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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.wigis.graph.GraphsPathFilter;
import net.wigis.graph.data.uploader.TopicFileUploadBean;
import net.wigis.graph.dnv.utilities.Vector3D;
import net.wigis.settings.Settings;

// TODO: Auto-generated Javadoc
/**
 * The Class ProcessGenres.
 * 
 * @author Brynjar Gretarsson
 */
public class ProcessGenres
{

	/** The genre substring to field and color map. */
	private static Map<String, String> genreSubstringToFieldAndColorMap = new HashMap<String, String>();

	/** The genre keys ordered. */
	private static List<String> genreKeysOrdered = new ArrayList<String>();

	/**
	 * Process.
	 * 
	 * @param inputFilename
	 *            the input filename
	 * @param outputFilename
	 *            the output filename
	 * @param outputFilename2
	 *            the output filename2
	 */
	public static void process( String inputFilename, String outputFilename, String outputFilename2 )
	{
		File file = new File( inputFilename );
		File outputFile = new File( outputFilename );
		if( file.exists() )
		{
			try
			{
				BufferedReader csv = new BufferedReader( new FileReader( file ) );
				String[] line;
				String lineStr;
				lineStr = csv.readLine();
				Map<String, Map<String, Integer>> artistToGenreCount = new HashMap<String, Map<String, Integer>>();
				Map<String, Map<String, Integer>> artistToAlbumCount = new HashMap<String, Map<String, Integer>>();
				Map<String, Map<String, Integer>> artistToAlbumArtistCount = new HashMap<String, Map<String, Integer>>();
				Map<String, Map<String, Integer>> artistToYearCount = new HashMap<String, Map<String, Integer>>();
				Map<String, Integer> genreCount = new HashMap<String, Integer>();
				Map<String, Integer> tempCount;
				String[] genres;
				while( ( lineStr = csv.readLine() ) != null )
				{
					line = lineStr.split( "," );
					if( line.length >= 3 )
					{
						for( int i = 0; i < line.length; i++ )
						{
							line[i] = StringUtils.replaceAll( line[i], "\"", "" );
						}
						genres = line[2].split( "," );
						updateCount( genreCount, genres );

						tempCount = getCount( line, artistToGenreCount );
						updateCount( tempCount, genres );

						genres = new String[] { line[1] };
						tempCount = getCount( line, artistToAlbumCount );
						updateCount( tempCount, genres );

						genres = new String[] { line[3] };
						tempCount = getCount( line, artistToAlbumArtistCount );
						updateCount( tempCount, genres );

						genres = new String[] { line[4] };
						tempCount = getCount( line, artistToYearCount );
						updateCount( tempCount, genres );
					}
				}
				csv.close();

				String genre;
				String album;
				String albumArtist;
				String year;
				Map<String, Integer> totalCount = new HashMap<String, Integer>();
				Writer bw = new BufferedWriter( new FileWriter( outputFile ) );
				bw.write( "\"Artist\",\"Album artist\",\"Genre\",\"Album\",\"Year\"\n" );
				for( String artist : artistToGenreCount.keySet() )
				{
					genre = getMaxCount( artist, artistToGenreCount );
					album = getMaxCount( artist, artistToAlbumCount );
					albumArtist = getMaxCount( artist, artistToAlbumArtistCount );
					year = getMaxCount( artist, artistToYearCount );

					System.out.println( artist );
					System.out.println( "\t" + genre );
					System.out.println( "\t" + album );
					System.out.println( "\t" + albumArtist );
					System.out.println( "\t" + year );
					if( !artist.equals( "" ) )
					{
						bw.write( "\"" + artist + "\",\"" + albumArtist + "\",\"" + genre + "\",\"" + album + "\",\"" + year + "\"\n" );
					}

					totalCount.put( genre, genreCount.get( genre ) );
				}

				bw.close();

				generateGenreSubstringToFieldAndColorMap();

				bw = new BufferedWriter( new FileWriter( outputFilename2 ) );
				bw.write( "All Departments:,Field Mapping:,Color Mapping\n" );
				boolean found = false;
				for( String g : totalCount.keySet() )
				{
					System.out.println( g + ":" );
					found = false;
					for( String key : genreKeysOrdered )
					{
						if( g.toLowerCase().contains( key ) )
						{
							System.out.println( "\t" + genreSubstringToFieldAndColorMap.get( key ) );
							bw.write( "\"" + g + "\"" + genreSubstringToFieldAndColorMap.get( key ) + "\n" );
							found = true;
							break;
						}
					}

					if( !found )
					{
						Vector3D color = new Vector3D( (float)Math.random(), (float)Math.random(), (float)Math.random() );
						String lowerCaseG = g.toLowerCase();
						genreKeysOrdered.add( lowerCaseG );
						genreSubstringToFieldAndColorMap.put( lowerCaseG, "," + g + "," + color.toHexColor() );
						System.out.println( "\t" + genreSubstringToFieldAndColorMap.get( lowerCaseG ) );
						bw.write( "\"" + g + "\"" + genreSubstringToFieldAndColorMap.get( lowerCaseG ) + "\n" );
					}
				}

				bw.close();

			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * Gets the max count.
	 * 
	 * @param artist
	 *            the artist
	 * @param artistToSomethingCount
	 *            the artist to something count
	 * @return the max count
	 */
	private static String getMaxCount( String artist, Map<String, Map<String, Integer>> artistToSomethingCount )
	{
		Map<String, Integer> map = artistToSomethingCount.get( artist );
		int maxCount = 0;
		int count;
		String maxSomething = "nothing";
		for( String something : map.keySet() )
		{
			if( !something.trim().equals( "" ) )
			{
				count = map.get( something );
				if( count > maxCount )
				{
					count = maxCount;
					maxSomething = something;
				}
			}
		}

		return maxSomething;
	}

	/**
	 * Gets the count.
	 * 
	 * @param line
	 *            the line
	 * @param artistToGenreCount
	 *            the artist to genre count
	 * @return the count
	 */
	private static Map<String, Integer> getCount( String[] line, Map<String, Map<String, Integer>> artistToGenreCount )
	{
		Map<String, Integer> tempCount;
		tempCount = artistToGenreCount.get( line[0] );
		if( tempCount == null )
		{
			tempCount = new HashMap<String, Integer>();
			artistToGenreCount.put( line[0], tempCount );
		}
		return tempCount;
	}

	/**
	 * Update count.
	 * 
	 * @param tempCount
	 *            the temp count
	 * @param genres
	 *            the genres
	 */
	private static void updateCount( Map<String, Integer> tempCount, String[] genres )
	{
		for( int i = 0; i < genres.length; i++ )
		{
			Integer count = tempCount.get( genres[i] );
			if( count == null )
			{
				count = 0;
			}
			count++;
			tempCount.put( genres[i], count );
		}
	}

	/**
	 * Generate genre substring to field and color map.
	 */
	public static void generateGenreSubstringToFieldAndColorMap()
	{
		genreSubstringToFieldAndColorMap.clear();

		// genreSubstringToFieldAndColorMap.put( "pop", ",Pop,#ff0000" );
		// genreKeysOrdered.add( "pop" );
		// genreSubstringToFieldAndColorMap.put( "metal", ",Metal,#00aa00" );
		// genreKeysOrdered.add( "metal" );
		// genreSubstringToFieldAndColorMap.put( "hard rock", ",Metal,#00aa00"
		// );
		// genreKeysOrdered.add( "hard rock" );
		// genreSubstringToFieldAndColorMap.put( "rap", ",Rap,#0000ff" );
		// genreKeysOrdered.add( "rap" );
		// genreSubstringToFieldAndColorMap.put( "rock", ",Rock,#00ff00" );
		// genreKeysOrdered.add( "rock" );
		// genreSubstringToFieldAndColorMap.put( "", ",Other,#000000" );
		// genreKeysOrdered.add( "" );
	}

	/**
	 * Copy directory.
	 * 
	 * @param sourceLocation
	 *            the source location
	 * @param targetLocation
	 *            the target location
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void copyDirectory( File sourceLocation, File targetLocation ) throws IOException
	{

		if( sourceLocation.isDirectory() )
		{
			if( !targetLocation.exists() )
			{
				targetLocation.mkdirs();
			}

			String[] children = sourceLocation.list();
			for( int i = 0; i < children.length; i++ )
			{
				copyDirectory( new File( sourceLocation, children[i] ), new File( targetLocation, children[i] ) );
			}
		}
		else
		{

			InputStream in = new FileInputStream( sourceLocation );
			OutputStream out = new FileOutputStream( targetLocation );

			// Copy the bits from instream to outstream
			byte[] buf = new byte[1024];
			int len;
			while( ( len = in.read( buf ) ) > 0 )
			{
				out.write( buf, 0, len );
			}
			in.close();
			out.close();
		}
	}

	/**
	 * Gets the number of lines.
	 * 
	 * @param file
	 *            the file
	 * @return the number of lines
	 */
	public static int getNumberOfLines( File file )
	{
		try
		{
			BufferedReader br = new BufferedReader( new FileReader( file ) );
			int count = 0;
			while( br.readLine() != null )
			{
				count++;
			}

			br.close();

			return count;
		}
		catch( IOException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return 0;
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
		GraphsPathFilter.init();
		String directory = Settings.GRAPHS_PATH + "topicVisualizations/musicExperiment/";
		File lyricsFolder = new File( "/Users/brynjar/lyrics" );
		File newFolder = new File( directory + "lyrics" );
		copyDirectory( lyricsFolder, newFolder );
		File docsFile = new File( directory + "lyrics/docs.txt" );
		File newDocsFile = new File( directory + "docs.txt" );
		copyDirectory( docsFile, newDocsFile );
		int numberOfTopics = getNumberOfLines( docsFile ) / 10;
		docsFile.delete();
		File genreFile = new File( directory + "lyrics/genres.txt" );
		File newGenreFile = new File( directory + "genres.txt" );
		copyDirectory( genreFile, newGenreFile );
		genreFile.delete();
		GenerateDocsFileForDirectory.processDirectory( directory, "lyrics", ".txt", "docs.txt" );
		TopicFileUploadBean.copyTopicDetectionCode( Settings.GRAPHS_PATH + "topicVisualizations/", directory );
		TopicFileUploadBean.runTopicDetection( directory, numberOfTopics, true, 2 );
		process( directory + "genres.txt", directory + "department.txt", directory + "departmentToColorMapping.txt" );
	}
}
