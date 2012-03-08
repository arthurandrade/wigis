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
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

// TODO: Auto-generated Javadoc
/**
 * The Class FindUnique.
 * 
 * @author Brynjar Gretarsson
 */
public class FindUnique
{

	/**
	 * Find unique.
	 * 
	 * @param files
	 *            the files
	 * @param headers
	 *            the headers
	 */
	public static void findUnique( String files[], String headers[] )
	{
		HashMap<String, Integer> counter = new HashMap<String, Integer>();
		for( int i = 0; i < files.length; i++ )
		{
			try
			{
				CSVReader csv = new CSVReader( new FileReader( files[i] ) );
				String line[] = csv.readNext();
				int[] indices = new int[headers.length];
				for( int k = 0; k < headers.length; k++ )
				{
					indices[k] = -1;
				}

				for( int j = 0; j < line.length; j++ )
				{
					for( int k = 0; k < headers.length; k++ )
					{
						if( line[j].equals( headers[k] ) )
						{
							indices[k] = j;
							break;
						}
					}
				}

				Integer count;
				String key;
				while( ( line = csv.readNext() ) != null )
				{
					key = "";
					for( int k = 0; k < indices.length; k++ )
					{
						key += line[indices[k]] + " - ";
					}
					count = counter.get( key );
					if( count == null )
					{
						count = 0;
					}
					count++;
					counter.put( key, count );
				}
			}
			catch( IOException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		List<StringAndCount> output = new LinkedList<StringAndCount>();
		for( String key : counter.keySet() )
		{
			output.add( new StringAndCount( key, counter.get( key ) ) );
		}

		SortStringAndCount ssac = new SortStringAndCount();
		Collections.sort( output, ssac );
		String header = "";
		for( int k = 0; k < headers.length; k++ )
		{
			header += headers[k] + " - ";
		}
		System.out.println( "Line - Count - " + header );
		int i = 1;
		for( StringAndCount o : output )
		{
			System.out.println( i + " - " + o.count + " - " + o.string );
			i++;
		}
	}

	/**
	 * The Class SortStringAndCount.
	 */
	private static class SortStringAndCount implements Comparator<StringAndCount>
	{

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare( StringAndCount s1, StringAndCount s2 )
		{
			if( s1.count > s2.count )
			{
				return -1;
			}

			if( s1.count < s2.count )
			{
				return 1;
			}

			return s1.string.compareTo( s2.string );
		}

	}

	/**
	 * The Class StringAndCount.
	 */
	private static class StringAndCount
	{

		/** The string. */
		String string;

		/** The count. */
		Integer count;

		/**
		 * Instantiates a new string and count.
		 * 
		 * @param string
		 *            the string
		 * @param count
		 *            the count
		 */
		public StringAndCount( String string, Integer count )
		{
			this.string = string;
			this.count = count;
		}
	}

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main( String args[] )
	{
		String files[] = { "/graphs/topicVisualizations/NSF/NSF-awards-Oct-2005_to_June-2007-Feb03.csv",
				"/graphs/topicVisualizations/NSF/NSF-awards-July-2007_to_Sept-2008-Feb03.csv",
				"/graphs/topicVisualizations/NSF/NSF-awards-Oct-2008_to_Jan-2010-Feb03.csv" };

		String headers[] = { "PROGRAM_NAME", "PROGRAM_OFFICER_NAME", };
		findUnique( files, headers );
	}
}
