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

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.wigis.graph.GraphsPathFilter;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.settings.Settings;

// TODO: Auto-generated Javadoc
/**
 * The Class GenerateHistogramOfConnectivity.
 * 
 * @author Brynjar Gretarsson
 */
public class GenerateHistogramOfConnectivity
{

	/**
	 * Logger.
	 * 
	 * @param filename
	 *            the filename
	 * @param includeZeros
	 *            the include zeros
	 * @param level
	 *            the level
	 */
	// // private static Log logger = LogFactory.getLog(
	// GenerateHistogramOfConnectivity.class );

	public static void generateHistogram( String filename, boolean includeZeros, int level )
	{
		System.out.println( "Starting histogram generation of " + filename );
		DNVGraph graph = new DNVGraph( filename );
		HashMap<Integer, Integer> histogram = generateHistogram( graph, level );

		writeHistogram( filename, includeZeros, histogram );
	}

	public static void writeHistogram( String filename, boolean includeZeros, HashMap<Integer, Integer> histogram )
	{
		Integer maxConnectivity = 0;
		Integer counter2;
		FileWriter fw;
		try
		{
			if( includeZeros )
				filename += "_with_zeros";
			fw = new FileWriter( filename + ".csv" );
			fw.write( "Connectivity,Number of Nodes\n" );
			if( includeZeros )
			{
				for( int i = 2; i <= maxConnectivity; i++ )
				{
					counter2 = histogram.get( i );
					if( counter2 == null )
					{
						counter2 = 0;
					}
					fw.write( i + "," + counter2 + "\n" );
					System.out.println( i + " : " + counter2 );
				}
			}
			else
			{
				Object[] keys = histogram.keySet().toArray();
				Arrays.sort( keys );
				for( int i = 0; i < keys.length; i++ )
				{
					counter2 = histogram.get( keys[i] );
					fw.write( keys[i] + "," + counter2 + "\n" );
					System.out.println( keys[i] + " : " + counter2 );
				}
			}
			fw.close();
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
	}

	/**
	 * Generate histogram.
	 * 
	 * @param graph
	 *            the graph
	 * @param level
	 *            the level
	 * @return the hash map
	 */
	public static HashMap<Integer, Integer> generateHistogram( DNVGraph graph, int level )
	{
		HashMap<Integer, Integer> histogram = new HashMap<Integer, Integer>();
		List<DNVNode> nodes = graph.getNodes( level );
		DNVNode tempNode;
		Integer connectivity;
		Integer counter;
		for( int i = 0; i < nodes.size(); i++ )
		{
			tempNode = nodes.get( i );
			connectivity = tempNode.getConnectivity();
			counter = histogram.get( connectivity );
			if( counter == null )
			{
				counter = 0;
			}
			counter++;
			// if( connectivity > maxConnectivity )
			// {
			// maxConnectivity = connectivity;
			// }
			histogram.put( connectivity, counter );
		}
		return histogram;
	}

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main( String args[] )
	{

		boolean includeZeros = false;
		GraphsPathFilter.init();
		generateHistogram( Settings.GRAPHS_PATH + "facebook/facebook_Brynjar Gretarsson_2.dnv", includeZeros, 0 );
//		generateHistogram( Settings.GRAPHS_PATH + "smallworld_10_20.dnv", includeZeros, 0 );
		generateHistogram( Settings.GRAPHS_PATH + "smallworld_100_200.dnv", includeZeros, 0 );
//		generateHistogram( Settings.GRAPHS_PATH + "smallworld_1000_2000.dnv", includeZeros, 0 );
//		generateHistogram( Settings.GRAPHS_PATH + "smallworld_10000_20000.dnv", includeZeros, 0 );
		// generateHistogram( Settings.GRAPHS_PATH +
		// "smallworld_100000_200000.dnv", includeZeros, 0);
		// generateHistogram( Settings.GRAPHS_PATH +
		// "smallworld_1000000_2000000.dnv", includeZeros, 0);
		/*
		 * includeZeros = true; generateHistogram( Settings.GRAPHS_PATH +
		 * "smallworld_10_20.dnv", includeZeros ); generateHistogram(
		 * Settings.GRAPHS_PATH + "smallworld_100_200.dnv", includeZeros);
		 * generateHistogram( Settings.GRAPHS_PATH + "smallworld_1000_2000.dnv",
		 * includeZeros); generateHistogram( Settings.GRAPHS_PATH +
		 * "smallworld_10000_20000.dnv", includeZeros); generateHistogram(
		 * Settings.GRAPHS_PATH + "smallworld_100000_200000.dnv", includeZeros);
		 * generateHistogram( Settings.GRAPHS_PATH +
		 * "smallworld_1000000_2000000.dnv", includeZeros);
		 * 
		 * generateHistogram( Settings.GRAPHS_PATH + "smallworld_1000_2000.dnv",
		 * false );
		 */
	}
}
