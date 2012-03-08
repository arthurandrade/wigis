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

package net.wigis.svetlin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

// TODO: Auto-generated Javadoc
/**
 * The Class File_.
 * 
 * @author Svetlin Bostandjiev
 */
public class __File
{
	public static void p( Object o )
	{
		System.out.println( o );
	}

	public static void pe( Object o )
	{
		System.err.println( o );
	}

	// ======================================
	// get file content
	// ======================================
	public static String getFileContent( String path )
	{
		String s = "";

		BufferedReader br = null;

		try
		{
			br = new BufferedReader( new InputStreamReader( new DataInputStream( new FileInputStream( path ) ) ) );

			String line = null;
			while( ( line = br.readLine() ) != null )
			{
				s += line;
				s += "\n";
			}
		}
		catch( Exception e )
		{
			System.err.println( "Error: " + e.getMessage() );
		}

		return s;
	}

	// ======================================
	// save string to file
	// ======================================
	public static void saveStringToFile( String path, String s )
	{
		try
		{
			BufferedWriter out = new BufferedWriter( new FileWriter( path ) );
			out.write( s );
			out.close();
		}
		catch( IOException e )
		{
			pe( "error saving file: " + e.getMessage() );
		}
	}

	// ======================================
	// append string to file
	// ======================================
	public static void appendStringToFile( String path, String s )
	{
		try
		{
			BufferedWriter out = new BufferedWriter( new FileWriter( path, true ) );
			out.write( s );
			out.close();
		}
		catch( IOException e )
		{
			pe( e.getMessage() );
		}
	}

	// ======================================
	// delete file contents
	// ======================================
	public static void deleteFileContents( String path )
	{
		try
		{
			BufferedWriter out = new BufferedWriter( new FileWriter( path ) );
			out.write( "" );
			out.close();
		}
		catch( IOException e )
		{
			pe( e.getMessage() );
		}
	}

	// ======================================
	// open file
	// ======================================
	public static void startFile( String path )
	{
		net.wigis.svetlin.__Cmd.runCmd( "start " + path );
	}

	// ======================================
	// delete file
	// ======================================
	/**
	 * Delete file.
	 * 
	 * @param path
	 *            the path
	 */
	public static void deleteFile( String path )
	{
		File file = new File( path );

		file.delete();
	}

	// ======================================
	// exists
	// ======================================
	/**
	 * Exists.
	 * 
	 * @param path
	 *            the path
	 * @return true, if successful
	 */
	public static boolean exists( String path )
	{
		File f = new File( path );

		return f.exists();
	}

	// ======================================
	// execute/open/run
	// ======================================
	/**
	 * Execute.
	 * 
	 * @param path
	 *            the path
	 */
	public static void execute( String path )
	{
		net.wigis.svetlin.__Cmd.runCmd( "start " + path );
	}

	/**
	 * Run.
	 * 
	 * @param path
	 *            the path
	 */
	public static void run( String path )
	{
		execute( path );
	}

	/**
	 * Open.
	 * 
	 * @param path
	 *            the path
	 */
	public static void open( String path )
	{
		execute( path );
	}

}
