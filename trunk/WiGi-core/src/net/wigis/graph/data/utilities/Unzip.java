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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.ice.tar.TarEntry;
import com.ice.tar.TarInputStream;

// TODO: Auto-generated Javadoc
/**
 * The Class Unzip.
 * 
 * @author Brynjar Gretarsson
 */
public class Unzip
{

	/**
	 * Copy input stream.
	 * 
	 * @param in
	 *            the in
	 * @param out
	 *            the out
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static final void copyInputStream( InputStream in, OutputStream out ) throws IOException
	{
		byte[] buffer = new byte[1024];
		int len;

		while( ( len = in.read( buffer ) ) >= 0 )
			out.write( buffer, 0, len );

		in.close();
		out.close();
	}

	/**
	 * Extract.
	 * 
	 * @param filename
	 *            the filename
	 * @param outputDirectory
	 *            the output directory
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static final void extract( String filename, String outputDirectory ) throws IOException
	{
		if( !outputDirectory.endsWith( "/" ) )
		{
			outputDirectory += "/";
		}
		if( filename.endsWith( "tar.gz" ) )
		{
			untar( filename, outputDirectory );
		}
		else if( filename.endsWith( ".zip" ) )
		{
			unzip( filename, outputDirectory );
		}

	}

	/**
	 * Unzip.
	 * 
	 * @param filename
	 *            the filename
	 * @param outputDirectory
	 *            the output directory
	 */
	private static final void unzip( String filename, String outputDirectory )
	{
		Enumeration<?> entries;
		ZipFile zipFile;

		try
		{
			zipFile = new ZipFile( filename );

			entries = zipFile.entries();

			while( entries.hasMoreElements() )
			{
				ZipEntry entry = (ZipEntry)entries.nextElement();

				if( entry.isDirectory() )
				{
					// Assume directories are stored parents first then
					// children.
					System.out.println( "Extracting directory: " + entry.getName() );
					// This is not robust, just for demonstration purposes.
					( new File( outputDirectory + entry.getName() ) ).mkdirs();
					continue;
				}

				System.out.println( "Extracting file: " + entry.getName() );
				copyInputStream( zipFile.getInputStream( entry ),
						new BufferedOutputStream( new FileOutputStream( outputDirectory + entry.getName() ) ) );
			}

			zipFile.close();
		}
		catch( IOException ioe )
		{
			System.out.println( "Unhandled exception:" );
			ioe.printStackTrace();
			return;
		}

	}

	/**
	 * Gets the input stream.
	 * 
	 * @param tarFileName
	 *            the tar file name
	 * @return the input stream
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static InputStream getInputStream( String tarFileName ) throws IOException
	{

		if( tarFileName.substring( tarFileName.lastIndexOf( "." ) + 1, tarFileName.lastIndexOf( "." ) + 3 ).equalsIgnoreCase( "gz" ) )
		{
			System.out.println( "Creating an GZIPInputStream for the file" );
			return new GZIPInputStream( new FileInputStream( new File( tarFileName ) ) );

		}
		else
		{
			System.out.println( "Creating an InputStream for the file" );
			return new FileInputStream( new File( tarFileName ) );
		}
	}

	/**
	 * Untar.
	 * 
	 * @param filename
	 *            the filename
	 * @param outputDirectory
	 *            the output directory
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private static void untar( String filename, String outputDirectory ) throws IOException
	{
		System.out.println( "Reading TarInputStream... (using classes from http://www.trustice.com/java/tar/)" );
		TarInputStream tin = new TarInputStream( getInputStream( filename ) );
		TarEntry tarEntry = tin.getNextEntry();
//		if( new File( outputDirectory ).exists() )
//		{
			while( tarEntry != null )
			{
				File destPath = new File( outputDirectory + File.separator + tarEntry.getName() );
				System.out.println( "Processing " + destPath.getAbsoluteFile() );
				if( !tarEntry.isDirectory() )
				{
					FileOutputStream fout = new FileOutputStream( destPath );
					tin.copyEntryContents( fout );
					fout.close();
				}
				else
				{
					destPath.mkdirs();
				}
				tarEntry = tin.getNextEntry();
			}
			tin.close();
//		}
//		else
//		{
//			System.out.println( "That destination directory doesn't exist! " + outputDirectory );
//		}
	}

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static final void main( String[] args ) throws IOException
	{
		Unzip.extract( "C:\\graphs\\topicVisualizations\\moby.tar.gz", "C:\\graphs\\topicVisualizations\\" );
	}
}