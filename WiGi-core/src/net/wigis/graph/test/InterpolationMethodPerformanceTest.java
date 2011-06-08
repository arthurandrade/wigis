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

package net.wigis.graph.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.utilities.GraphFunctions;
import net.wigis.graph.dnv.utilities.InterpolationMethod;
import net.wigis.graph.dnv.utilities.Timer;
import net.wigis.graph.dnv.utilities.Vector2D;

// TODO: Auto-generated Javadoc
/**
 * The Class InterpolationMethodPerformanceTest.
 * 
 * @author Brynjar Gretarsson
 */
public class InterpolationMethodPerformanceTest
{

	/**
	 * Simulate selection.
	 * 
	 * @param node
	 *            the node
	 * @param graph
	 *            the graph
	 * @param level
	 *            the level
	 * @param maxDepth
	 *            the max depth
	 */
	public static void simulateSelection( DNVNode node, DNVGraph graph, int level, int maxDepth )
	{
		// Get rid of old interpolation data
		InterpolationMethod.resetInterpolationData( graph, level );

		// Perform the BFS
		InterpolationMethod.performBFS( node, maxDepth, false );

		// Set the weights
		InterpolationMethod.setWeights( graph, level, maxDepth, false, node );
	}

	/**
	 * Simulate movement.
	 * 
	 * @param node
	 *            the node
	 * @param graph
	 *            the graph
	 * @param movement
	 *            the movement
	 * @param level
	 *            the level
	 */
	public static void simulateMovement( DNVNode node, DNVGraph graph, Vector2D movement, int level )
	{
		// Perform the movement
		node.moveRelatedNodes( movement, true, true );
		node.move( movement, false, false );
		node.setSelected( true );
		InterpolationMethod.applyFunction( node, null, graph, movement, level, 5 );
	}

	/**
	 * Takes in an array of filenames for the graphs that should be tested. Also
	 * takes in an integer indicating how many iterations should be done for
	 * each graph. Each iteration randomly selects a node from the current graph
	 * and sets up the interpolation as if the node was just clicked on. Then
	 * the node is moved by a distance equivalent to the diagonal of the space
	 * occupied by the graph. (from upper left corner down to bottom right
	 * corner) (The movement distance and direction are irrelevant to the
	 * complexity of the algorithm)
	 * 
	 * @param fileNames
	 *            the file names
	 * @param numberOfIterationsPerGraph
	 *            the number of iterations per graph
	 * @param maxDepths
	 *            the max depths
	 * @param outputFileName
	 *            the output file name
	 */
	public static void runTestsForGivenGraphs( String[] fileNames, int numberOfIterationsPerGraph, int[] maxDepths, String outputFileName )
	{
		System.out.print( "Interpolation Method Performance Test\nRunning " + numberOfIterationsPerGraph + " iterations for each graph.\n\n" );
		File file = new File( outputFileName );
		try
		{
			FileWriter fw = new FileWriter( file );
			fw.write( "Filename, Part, Number of Nodes, Number of Edges, Average Time in Milliseconds, Maximum Depth, Number of Iterations\n" );
			DNVGraph graph;
			Vector2D movement = new Vector2D();
			int maxDepth = Integer.MAX_VALUE;
			int level = 0;
			double globalMaxY;
			double globalMinY;
			double globalMaxX;
			double globalMinX;
			DNVNode movedNode;
			int graphSize;
			int tempIndex;
			String tempFileName;
			Timer[] selectionTimers = new Timer[fileNames.length];
			Timer[] movementTimers = new Timer[fileNames.length];
			for( int i = 0; i < fileNames.length; i++ )
			{
				graph = new DNVGraph( fileNames[i] );
				globalMaxY = GraphFunctions.getMaxYPosition( graph, level, false );
				globalMinY = GraphFunctions.getMinYPosition( graph, level, false );
				globalMaxX = GraphFunctions.getMaxXPosition( graph, level, false );
				globalMinX = GraphFunctions.getMinXPosition( graph, level, false );
				if( globalMinY == globalMaxY )
				{
					globalMinY -= 10;
					globalMaxY += 10;
				}
				if( globalMinX == globalMaxX )
				{
					globalMinX -= 10;
					globalMaxX += 10;
				}
				movement.setX( getRandomSign() * (float)( globalMaxX - globalMinX ) / 3.0f );
				movement.setY( getRandomSign() * (float)( globalMaxY - globalMinY ) / 3.0f );
				graphSize = graph.getGraphSize( level );
				System.out.print( "---------------------------------------------------------------------------------------\n" );
				System.out.print( "Results for " + fileNames[i] + "\n" );
				for( int k = 0; k < maxDepths.length; k++ )
				{
					maxDepth = maxDepths[k];
					selectionTimers[i] = new Timer( Timer.NANOSECONDS );
					movementTimers[i] = new Timer( Timer.NANOSECONDS );
					for( int j = 0; j < numberOfIterationsPerGraph; j++ )
					{
						movedNode = graph.getNodes( level ).get( (int)( Math.random() * graphSize ) );

						selectionTimers[i].setStart();
						simulateSelection( movedNode, graph, level, maxDepth );
						selectionTimers[i].setEnd();

						movementTimers[i].setStart();
						simulateMovement( movedNode, graph, movement, level );
						movementTimers[i].setEnd();
					}

					System.out.print( "With maxDepth set as " + maxDepth + ":\n" );
					System.out.print( "Average selection time: " + selectionTimers[i].getAverageTime( Timer.MILLISECONDS ) + " milliseconds.\n" );
					System.out.print( "Average movement time:  " + movementTimers[i].getAverageTime( Timer.MILLISECONDS ) + " milliseconds.\n" );
					System.out.print( "Average total time:     "
							+ ( movementTimers[i].getAverageTime( Timer.MILLISECONDS ) + selectionTimers[i].getAverageTime( Timer.MILLISECONDS ) )
							+ " milliseconds.\n" );
					System.out.println();
					fw.write( fileNames[i] + ",selection," + graphSize + "," + graph.getEdges( level ).size() + ","
							+ selectionTimers[i].getAverageTime( Timer.MILLISECONDS ) + "," + maxDepth + "," + numberOfIterationsPerGraph + "\n" );
					fw.write( fileNames[i] + ",movement," + graphSize + "," + graph.getEdges( level ).size() + ","
							+ movementTimers[i].getAverageTime( Timer.MILLISECONDS ) + "," + maxDepth + "," + numberOfIterationsPerGraph + "\n" );
					fw.write( fileNames[i] + ",total," + graphSize + "," + graph.getEdges( level ).size() + ","
							+ ( movementTimers[i].getAverageTime( Timer.MILLISECONDS ) + selectionTimers[i].getAverageTime( Timer.MILLISECONDS ) )
							+ "," + maxDepth + "," + numberOfIterationsPerGraph + "\n" );
				}
				System.out.print( "---------------------------------------------------------------------------------------\n\n" );

				tempIndex = fileNames[i].lastIndexOf( "." );
				tempFileName = fileNames[i].substring( 0, tempIndex ) + "_interp" + fileNames[i].substring( tempIndex );
				graph.writeGraph( tempFileName );
			}

			fw.close();
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
	}

	/**
	 * Gets the random sign.
	 * 
	 * @return the random sign
	 */
	private static int getRandomSign()
	{
		if( Math.random() < 0.5 )
			return -1;

		return 1;
	}

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main( String[] args )
	{
		int maxDepths[] = { 3, 1000000 };
		String files[] = { "C:\\graphs\\smallworld_10_20.dnv", "C:\\graphs\\smallworld_100_200.dnv", "C:\\graphs\\smallworld_1000_2000.dnv",
				"C:\\graphs\\smallworld_10000_20000.dnv", "C:\\graphs\\smallworld_100000_200000.dnv", "C:\\graphs\\smallworld_1000000_2000000.dnv" };

		// Run it once to warm up memory space, garbage collection etc.
		runTestsForGivenGraphs( files, 10, maxDepths, "InterpolationResults1.csv" );

		// Run it again to get the real values
		runTestsForGivenGraphs( files, 500, maxDepths, "InterpolationResults2.csv" );
	}
}
