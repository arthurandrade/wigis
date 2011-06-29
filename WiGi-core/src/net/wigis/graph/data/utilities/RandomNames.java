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
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.wigis.graph.GraphsPathFilter;
import net.wigis.settings.Settings;

/**
 * @author brynjar
 *
 */
public class RandomNames
{
	private static List<String> maleNames = new ArrayList<String>();
	private static List<String> femaleNames = new ArrayList<String>();
	private static List<String> lastNames = new ArrayList<String>();
	
	private static Map<String,Boolean> takenNames = new HashMap<String,Boolean>();
	private static boolean initialized = false;
	
	public static String getRandomName()
	{
		if( Math.random() < 0.5 )
		{
			return getRandomName( true );
		}
		
		return getRandomName( false );
	}
	
	public static String getRandomName( boolean male )
	{
		try
		{
			initialize();
		}
		catch( IOException ioe ){}
		
		String name;
		if( male )
		{
			name = getRandom( maleNames ) + " " + getRandom( lastNames );
		}
		else
		{
			name = getRandom( femaleNames ) + " " + getRandom( lastNames );			
		}
		
		if( !isNameTaken( name ) )
		{
			addTakenName( name );
		}
		else
		{
			return getRandomName( male );
		}
		
		return name;
	}

	public static void addTakenName( String name )
	{
		takenNames.put( name, true );
	}
	
	public static String getRandomFirstName( boolean male )
	{
		try
		{
			initialize();
		}
		catch( IOException ioe ){}
		
		if( male )
		{
			return getRandom( maleNames );
		}
		else
		{
			return getRandom( femaleNames );
		}
	}
	
	/**
	 * @param maleNames2
	 * @return
	 */
	private static String getRandom( List<String> list )
	{
		return list.get( (int)(Math.random() * list.size()) );
	}

	private static void readFileIntoList( String filename, List<String> list ) throws IOException
	{
		File file = new File( filename );
		FileReader fr = new FileReader( file );
		BufferedReader br = new BufferedReader( fr );
		String line;
		while( (line = br.readLine() ) != null )
		{
			line = line.substring( 0,1 ).toUpperCase() + line.substring( 1 ).toLowerCase();
			list.add( line );
//			System.out.println( line );
		}
//		System.out.println();
		br.close();
		fr.close();
	}
	
	public static synchronized void initialize() throws IOException
	{
		if( !initialized )
		{
			GraphsPathFilter.init();
			readFileIntoList( Settings.GRAPHS_PATH + "UserStudy/nameGenerator/FirstNamesMale.txt", maleNames );
			readFileIntoList( Settings.GRAPHS_PATH + "UserStudy/nameGenerator/FirstNamesFemale.txt", femaleNames );
			readFileIntoList( Settings.GRAPHS_PATH + "UserStudy/nameGenerator/LastNames.txt", lastNames );
			initialized = true;
		}
	}
	
	public static void main( String args[] ) throws IOException
	{
		for( int i = 0; i < 1000; i++ )
		{
			System.out.println( getRandomName() );
		}
	}

	public static String getRandomFirstName()
	{
		if( Math.random() < 0.5 )
		{
			return getRandomFirstName( true );
		}
		
		return getRandomFirstName( false );
	}

	public static boolean isNameTaken( String name )
	{
		return takenNames.containsKey( name );
	}
}
