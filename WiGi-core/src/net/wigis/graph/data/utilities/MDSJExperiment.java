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
import java.io.FileReader;
import java.io.IOException;

import mdsj.MDSJ;
import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.layout.FruchtermanReingold;
import net.wigis.graph.dnv.layout.RandomLayout;
import net.wigis.graph.dnv.utilities.GraphFunctions;
import net.wigis.settings.Settings;

// TODO: Auto-generated Javadoc
/**
 * The Class MDSJExperiment.
 * 
 * @author Brynjar Gretarsson
 */
public class MDSJExperiment
{

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void main( String[] args ) throws IOException
	{
		double[][] input = { // input dissimilarity matrix
		{ 0.00, 2.04, 1.92, 2.35, 2.06, 2.12, 2.27, 2.34, 2.57, 2.43, 1.90, 2.41 },
				{ 2.04, 0.00, 2.10, 2.00, 2.23, 2.04, 2.38, 2.36, 2.23, 2.36, 2.57, 2.34 },
				{ 1.92, 2.10, 0.00, 1.95, 2.21, 2.23, 2.32, 2.46, 1.87, 1.88, 2.41, 1.97 },
				{ 2.35, 2.00, 1.95, 0.00, 2.05, 1.78, 2.08, 2.27, 2.14, 2.14, 2.38, 2.17 },
				{ 2.06, 2.23, 2.21, 2.05, 0.00, 2.35, 2.23, 2.18, 2.30, 1.98, 1.74, 2.06 },
				{ 2.12, 2.04, 2.23, 1.78, 2.35, 0.00, 2.21, 2.12, 2.21, 2.12, 2.17, 2.23 },
				{ 2.27, 2.38, 2.32, 2.08, 2.23, 2.21, 0.00, 2.04, 2.44, 2.19, 1.74, 2.13 },
				{ 2.34, 2.36, 2.46, 2.27, 2.18, 2.12, 2.04, 0.00, 2.19, 2.09, 1.71, 2.17 },
				{ 2.57, 2.23, 1.87, 2.14, 2.30, 2.21, 2.44, 2.19, 0.00, 1.81, 2.53, 1.98 },
				{ 2.43, 2.36, 1.88, 2.14, 1.98, 2.12, 2.19, 2.09, 1.81, 0.00, 2.00, 1.52 },
				{ 1.90, 2.57, 2.41, 2.38, 1.74, 2.17, 1.74, 1.71, 2.53, 2.00, 0.00, 2.33 },
				{ 2.41, 2.34, 1.97, 2.17, 2.06, 2.23, 2.13, 2.17, 1.98, 1.52, 2.33, 0.00 } };

		int numberOfNodes = 10;
		input = generateRandomInput( numberOfNodes );
		// String directory = Settings.GRAPHS_PATH + "topicVisualizations" +
		// File.separator + "calit2" + File.separator;
		// String file = "matrixKL.txt";
		// input = readMatrix( directory, file );
		// double [][] weights = generateRandomInput( numberOfNodes );
		int n = input[0].length; // number of data objects
		double[][] output = MDSJ.stressMinimization( input ); // apply MDS
		DNVGraph graph = generateGraph( input, n, output );
		graph.writeGraph( Settings.GRAPHS_PATH + "MDS_stressMinimization.dnv" );

		output = MDSJ.classicalScaling( input ); // apply MDS
		graph = generateGraph( input, n, output );
		graph.writeGraph( Settings.GRAPHS_PATH + "MDS_classicalMDS.dnv" );

		float width = GraphFunctions.getGraphWidth( graph, 0, false );

		RandomLayout.runLayout( graph, 0, width );
		FruchtermanReingold.runLayout( width, width, graph, 0.01f, 0, false, false );
		graph.writeGraph( Settings.GRAPHS_PATH + "MDS_FR.dnv" );
	}

	/**
	 * Read matrix.
	 * 
	 * @param directory
	 *            the directory
	 * @param file
	 *            the file
	 * @return the double[][]
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static double[][] readMatrix( String directory, String file ) throws IOException
	{
		FileReader fr = new FileReader( directory + file );
		BufferedReader br = new BufferedReader( fr );
		String line;
		String[] lineArray;
		double[][] doubleArray;
		line = br.readLine();
		lineArray = line.split( " " );
		int counter = 0;
		doubleArray = new double[lineArray.length][lineArray.length];
		for( int i = 0; i < lineArray.length; i++ )
		{
			doubleArray[counter][i] = Double.parseDouble( lineArray[i] );
		}
		counter++;
		while( ( line = br.readLine() ) != null )
		{
			lineArray = line.split( " " );
			for( int i = 0; i < lineArray.length; i++ )
			{
				doubleArray[counter][i] = Double.parseDouble( lineArray[i] );
			}
			counter++;
		}

		br.close();
		fr.close();

		return doubleArray;
	}

	/**
	 * Generate random input.
	 * 
	 * @param size
	 *            the size
	 * @return the double[][]
	 */
	public static double[][] generateRandomInput( int size )
	{
		double[][] input = new double[size][size];
		for( int i = 0; i < size; i++ )
		{
			for( int j = 0; j < i; j++ )
			{
				if( i == j )
				{
					input[i][j] = 0;
				}
				else
				{
					input[i][j] = Math.random() * 10.0;
					input[j][i] = input[i][j];
				}
			}
		}

		return input;
	}

	/**
	 * Generate graph.
	 * 
	 * @param input
	 *            the input
	 * @param n
	 *            the n
	 * @param output
	 *            the output
	 * @return the dNV graph
	 */
	private static DNVGraph generateGraph( double[][] input, int n, double[][] output )
	{
		DNVGraph graph = new DNVGraph();
		for( int i = 0; i < n; i++ )
		{ // output all coordinates
			DNVNode node = new DNVNode( graph );
			node.setPosition( (float)output[0][i], (float)output[1][i] );
			node.setColor( 0.3f, 0.7f, 1 );
			node.setLabel( "" + i );
			node.setBbId( "" + i );
			graph.addNode( 0, node );
		}
		for( int i = 0; i < input.length; i++ )
		{
			for( int j = 0; j < i; j++ )
			{
				DNVEdge edge = new DNVEdge( graph );
				DNVNode from = (DNVNode)graph.getNodeByBbId( "" + i );
				DNVNode to = (DNVNode)graph.getNodeByBbId( "" + j );
				edge.setFrom( from );
				edge.setTo( to );
				edge.setLabel( "" + Math.round( 100.0 * input[i][j] ) / 100.0 );
				edge.setRestingDistance( (float)input[i][j] );
				edge.setK( 1 );
				graph.addNode( 0, edge );
			}
		}
		return graph;
	}
}
