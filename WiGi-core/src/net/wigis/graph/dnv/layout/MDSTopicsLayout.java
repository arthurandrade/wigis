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

package net.wigis.graph.dnv.layout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import mdsj.MDSJ;
import net.wigis.graph.dnv.DNVEntity;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.utilities.Timer;
import net.wigis.settings.Settings;

// TODO: Auto-generated Javadoc
/**
 * The Class MDSTopicsLayout.
 * 
 * @author Brynjar Gretarsson
 */
public class MDSTopicsLayout
{

	/**
	 * Run layout.
	 * 
	 * @param graph
	 *            the graph
	 * @param level
	 *            the level
	 * @param dissimilarityMatrix
	 *            the dissimilarity matrix
	 * @param useStressMinimization
	 *            the use stress minimization
	 */
	public static void runLayout( DNVGraph graph, int level, double[][] dissimilarityMatrix, boolean useStressMinimization )
	{
		double[][] output;
		Timer timer = new Timer( Timer.MILLISECONDS );
		timer.setStart();
		if( useStressMinimization )
		{
			output = MDSJ.stressMinimization( dissimilarityMatrix );
		}
		else
		{
			output = MDSJ.classicalScaling( dissimilarityMatrix );
		}
		timer.setEnd();
		System.out.println( "=========================================================================" );
		System.out.println( "Running MDS part of layout took " + timer.getLastSegment( Timer.SECONDS ) + " seconds." );

		int id;
		for( DNVEntity entity : graph.getNodesByType( level, "topic" ).values() )
		{
			DNVNode node = (DNVNode)entity;
			id = Integer.parseInt( node.getBbId().substring( 1 ) ) - 1;
			try
			{
				node.setPosition( (float)output[0][id], (float)output[1][id] );
				node.setProperty( "pinned", "true" );
			}
			catch( IndexOutOfBoundsException e )
			{
				System.out.println( "WARNING: topic node t" + ( id + 1 ) + " doesn't exist in dissimilarity matrix" );
			}
		}
		// CreateDNVFromDocumentTopics.generateDissimilarityEdges( graph,
		// dissimilarityMatrix, 1 );
		Double width = getWidth( dissimilarityMatrix );
		timer.setStart();
		FruchtermanReingold.runLayout( width.floatValue(), width.floatValue(), graph, 0.05f, level, false, false, false );
		timer.setEnd();
		System.out.println( "Running Force Directed part of layout took " + timer.getLastSegment( Timer.SECONDS ) + " seconds." );

		// CreateDNVFromDocumentTopics.removeDissimilarityEdges( graph );

		timer.setStart();
		for( DNVEntity entity : graph.getNodesByType( level, "topic" ).values() )
		{
			entity.removeProperty( "pinned" );
		}
		timer.setEnd();

		System.out.println( "Entire MDS Topics Layout took " + timer.getTotalTime( Timer.SECONDS ) + " seconds." );
		System.out.println( "=========================================================================" );
	}

	/**
	 * Gets the width.
	 * 
	 * @param dissimilarityMatrix
	 *            the dissimilarity matrix
	 * @return the width
	 */
	public static Double getWidth( double[][] dissimilarityMatrix )
	{
		if( dissimilarityMatrix != null )
		{
			double average = getAverage( dissimilarityMatrix );
			return Math.sqrt( average * dissimilarityMatrix.length );
		}

		return null;
	}

	/**
	 * Gets the average.
	 * 
	 * @param dissimilarityMatrix
	 *            the dissimilarity matrix
	 * @return the average
	 */
	private static double getAverage( double[][] dissimilarityMatrix )
	{
		double total = 0;
		double count = 0;
		for( int i = 0; i < dissimilarityMatrix.length; i++ )
		{
			for( int j = 0; j < i; j++ )
			{
				total += dissimilarityMatrix[i][j];
				count++;
			}
		}

		return total / count;
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
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void main( String args[] ) throws IOException
	{
		String directory = Settings.GRAPHS_PATH + "topicVisualizations" + File.separator + "infovis2006-2009" + File.separator;
		String file = "matrixKL.txt";
		String graphsFile = "infovis2006-2009_15.4_2_false_false_0.3_true_false_false.dnv";

		System.out.println( "loading graph" );
		DNVGraph graph = new DNVGraph( directory + graphsFile );
		System.out.println( "reading matrix" );
		double[][] dissimilarityMatrix = readMatrix( directory, file );

		for( int i = 0; i < dissimilarityMatrix.length; i++ )
		{
			for( int j = i; j < dissimilarityMatrix[i].length; j++ )
			{
				System.out.println( "t" + ( i + 1 ) + " -> t" + ( j + 1 ) + " = " + dissimilarityMatrix[i][j] );
			}
		}
		System.out.println( "running layout" );
		runLayout( graph, 0, dissimilarityMatrix, true );
		System.out.println( "writing graph" );
		graph.writeGraph( Settings.GRAPHS_PATH + graphsFile );
	}
}
