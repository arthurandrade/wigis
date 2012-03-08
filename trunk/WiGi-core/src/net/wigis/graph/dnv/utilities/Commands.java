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

package net.wigis.graph.dnv.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

// TODO: Auto-generated Javadoc
/**
 * The Class Commands.
 * 
 * @author Brynjar Gretarsson
 */
public class Commands
{

	/**
	 * Run system command.
	 * 
	 * @param command
	 *            the command
	 * @return the string
	 */
	public static String runSystemCommand( String command )
	{
		return runSystemCommand( command, null );
	}

	/**
	 * Run system command.
	 * 
	 * @param command
	 *            the command
	 * @param directory
	 *            the directory
	 * @return the string
	 */
	public static String runSystemCommand( String command, String directory )
	{
		String stdOut = "";
		try
		{
			String s = "";
			File dir = null;
			if( directory != null && !directory.equals( "" ) )
			{
				dir = new File( directory );
			}
			System.out.println( "running command '" + command + "'" );
			// run a command
			// using the Runtime exec method:
			List<String> commandList = new ArrayList<String>();
			Scanner scanner = new Scanner( command );
			while( scanner.hasNext() )
			{
				commandList.add( scanner.next() );
			}
			ProcessBuilder pb = new ProcessBuilder( commandList );
			Map<String, String> env = pb.environment();
			// for( String key : env.keySet() )
			// {
			// System.out.println( key + "=" + env.get( key ) );
			// }
			if( File.separator.equals( "\\" ) )
			{
				env.put( "PATH", "C:\\cygwin\\bin;C:\\Program Files\\MATLAB\\R2009b\\bin" );
			}
			else
			{
				env.put( "PATH", env.get( "PATH" ) + ":/opt/local/bin/" );
			}
			pb.directory( dir );
			Process p = pb.start();

			BufferedReader stdInput = new BufferedReader( new InputStreamReader( p.getInputStream() ) );

			BufferedReader stdError = new BufferedReader( new InputStreamReader( p.getErrorStream() ) );

			// read the output from the command
			// System.out.println(
			// "Here is the standard output of the command:\n" );
			while( ( s = stdInput.readLine() ) != null )
			{
				// System.out.println( s );
				stdOut += s + "\n";
			}

			// read any errors from the attempted command
			System.out.println( "Here is the standard error of the command (if any):\n" );
			while( ( s = stdError.readLine() ) != null )
			{
				System.out.println( s );
			}

		}
		catch( IOException e )
		{
			System.out.println( "exception happened - here's what I know: " );
			e.printStackTrace();
		}
		return stdOut;

	}

}
