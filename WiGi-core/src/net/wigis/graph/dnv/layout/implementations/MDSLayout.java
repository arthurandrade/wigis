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

package net.wigis.graph.dnv.layout.implementations;

import java.util.List;

import mdsj.MDSJ;
import net.wigis.graph.GraphsPathFilter;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.layout.interfaces.MDSLayoutInterface;
import net.wigis.graph.dnv.utilities.GraphFunctions;
import net.wigis.settings.Settings;

// TODO: Auto-generated Javadoc
/**
 * The Class MDSLayout.
 * 
 * @author Brynjar Gretarsson
 */
public class MDSLayout implements MDSLayoutInterface
{

	/**
	 * Run layout.
	 * 
	 * @param graph
	 *            the graph
	 * @param level
	 *            the level
	 * @param useStressMinimization
	 *            the use stress minimization
	 */
	public void runLayout( DNVGraph graph, int level, boolean useStressMinimization )
	{
		List<DNVNode> nodes = graph.getNodes( level );
		double dissimilarityMatrix[][] = createDissimilarityMatrix( nodes );
		double[][] output;
		if( useStressMinimization )
		{
			output = MDSJ.stressMinimization( dissimilarityMatrix );
		}
		else
		{
			output = MDSJ.classicalScaling( dissimilarityMatrix );
		}

		DNVNode tempNode;
		for( int i = 0; i < nodes.size(); i++ )
		{
			tempNode = nodes.get( i );
			tempNode.setPosition( (float)output[0][i], (float)output[1][i] );
		}

		printMatrix( output );
	}

	/**
	 * Creates the dissimilarity matrix.
	 * 
	 * @param nodes
	 *            the nodes
	 * @return the double[][]
	 */
	private static double[][] createDissimilarityMatrix( List<DNVNode> nodes )
	{
		double[][] dissimilarityMatrix = new double[nodes.size()][nodes.size()];

		DNVNode node1;
		DNVNode node2;
		int count = 0;
		double maxValue = 0;
		double value;
		for( int i = 0; i < nodes.size(); i++ )
		{
			for( int j = i; j < nodes.size(); j++ )
			{
				if( j == i )
				{
					dissimilarityMatrix[i][j] = 0;
				}
				else
				{
					node1 = nodes.get( i );
					node2 = nodes.get( j );
					value = GraphFunctions.findShortestPathDistanceInNumberOfHops( node1, node2 );
					if( value != Float.POSITIVE_INFINITY )
					{
						maxValue = Math.max( value, maxValue );
					}
					dissimilarityMatrix[i][j] = value;
					dissimilarityMatrix[j][i] = dissimilarityMatrix[i][j];
				}
			}
		}

		for( int i = 0; i < nodes.size(); i++ )
		{
			for( int j = i + 1; j < nodes.size(); j++ )
			{
				value = dissimilarityMatrix[i][j];
				if( value == Double.POSITIVE_INFINITY )
				{
					dissimilarityMatrix[i][j] = maxValue;
					dissimilarityMatrix[j][i] = maxValue;
				}
			}
		}
		System.out.println( count + " edges in graph" );

		printMatrix( dissimilarityMatrix );

		return dissimilarityMatrix;
	}

	/**
	 * Prints the matrix.
	 * 
	 * @param matrix
	 *            the matrix
	 */
	private static void printMatrix( double[][] matrix )
	{
		for( int i = 0; i < matrix[0].length; i++ )
		{
			System.out.print( "_" );
		}
		System.out.println();
		for( int i = 0; i < matrix.length; i++ )
		{
			System.out.print( "|" );
			for( int j = 0; j < matrix[i].length; j++ )
			{
				if( j < matrix[i].length - 1 )
				{
					System.out.print( matrix[i][j] + "," );
				}
				else
				{
					System.out.print( matrix[i][j] );
				}
			}
			System.out.println( "|" );
		}
		for( int i = 0; i < matrix[0].length; i++ )
		{
			System.out.print( "_" );
		}
		System.out.println();
	}

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main( String args[] )
	{
		GraphsPathFilter.init();
		DNVGraph graph = new DNVGraph( Settings.GRAPHS_PATH + "_UCI_venezuela.dnv" );
		new MDSLayout().runLayout( graph, 0, true );
		graph.writeGraph( Settings.GRAPHS_PATH + "_UCI_venezuela_MDS.dnv" );
	}

	public static final String LABEL = "MDS Layout";
	
	@Override
	public String getLabel()
	{
		return LABEL;
	}
}
