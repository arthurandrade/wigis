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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.wigis.graph.PaintBean;
import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.settings.Settings;

// TODO: Auto-generated Javadoc
/**
 * The Class InterpolationMethod.
 * 
 * @author Brynjar Gretarsson
 */
public final class InterpolationMethod
{

	/**
	 * Logger.
	 * 
	 * @param graph
	 *            the graph
	 * @param level
	 *            the level
	 */
	// // private static Log logger = LogFactory.getLog(
	// InterpolationMethod.class );

	public static void resetInterpolationData( DNVGraph graph, Integer level )
	{
		List<DNVNode> nodes = graph.getNodes( level );
		graph.clearInterpolationList( level );
		for( int i = 0; i < nodes.size(); i++ )
		{
			nodes.get( i ).resetInterpolationData();
		}
	}

	/**
	 * Reset interpolation weight.
	 * 
	 * @param graph
	 *            the graph
	 * @param level
	 *            the level
	 */
	public static void resetInterpolationWeight( DNVGraph graph, Integer level )
	{
		List<DNVNode> nodes = graph.getNodes( level );
		for( int i = 0; i < nodes.size(); i++ )
		{
			nodes.get( i ).resetInterpolationWeights();
		}
	}

	// public static int performBFSOld( DNVNode selectedNode )
	// {
	// return performBFSOld( selectedNode, Integer.MAX_VALUE );
	// }
	//
	// public static int performBFSOld( DNVNode selectedNode, int maxDepth )
	// {
	// Timer bfsTimer = new Timer( Timer.MILLISECONDS );
	// bfsTimer.setStart();
	// selectedNode.setDistanceFromSelectedNode( 0 );
	// int returnValue = 0;
	// if( maxDepth > 0 )
	// {
	// returnValue = handleList( selectedNode.getNeighbors(), 1, maxDepth );
	// }
	// bfsTimer.setEnd();
	// if( Settings.DEBUG )
	// {
	// System.out.println( "Interpolation BFS took " + bfsTimer.getLastSegment(
	// Timer.SECONDS ) + " seconds." );
	// }
	//
	// return returnValue;
	// }

	// private static int handleList( List<DNVNode> nodesToHandle, int distance,
	// int maxDepth )
	// {
	// DNVNode tempNode;
	// List<DNVNode> nextList = new ArrayList<DNVNode>();
	// for( int i = 0; i < nodesToHandle.size(); i++ )
	// {
	// tempNode = nodesToHandle.get( i );
	// if( tempNode.getDistanceFromSelectedNode() > distance )
	// {
	// tempNode.setDistanceFromSelectedNode( distance );
	// nextList.addAll( tempNode.getNeighbors() );
	// }
	// }
	//
	// if( !nextList.isEmpty() && distance < maxDepth )
	// {
	// return handleList( nextList, distance + 1, maxDepth );
	// }
	//
	// return distance - 1;
	// }

	/**
	 * Perform bfs.
	 * 
	 * @param selectedNode
	 *            the selected node
	 * @param maxDepth
	 *            the max depth
	 * @param useActualDistance
	 *            the use actual distance
	 * @return the float
	 */
	public static float performBFS( DNVNode selectedNode, float maxDepth, boolean useActualDistance )
	{
		Timer bfsTimer = new Timer( Timer.MILLISECONDS );
		List<DNVNode> queue = new ArrayList<DNVNode>();
		bfsTimer.setStart();
		selectedNode.setDistanceFromSelectedNode( 0 );
		selectedNode.setActualDistanceFromSelectedNode( 0 );
		selectedNode.setDistanceFromNode( selectedNode, 0 );
		Iterator<DNVEdge> edges;
		queue.add( selectedNode );
		DNVNode tempNode;
		float actualDistance;
		int maxDistance = 0;
		int distance;
		float maxActualDistance = 0;
		DNVEdge tempEdge;
		if( maxDepth > 0 )
		{
			while( queue.size() > 0 )
			{
				tempNode = queue.remove( 0 );
				distance = tempNode.getDistanceFromNodeWithId( selectedNode.getId() );
				actualDistance = tempNode.getActualDistanceFromSelectedNode();
				if( actualDistance > maxActualDistance )
				{
					maxActualDistance = actualDistance;
				}

				if( distance > maxDistance )
				{
					maxDistance = distance;
				}

				if( distance < maxDepth )
				{
					// From edges
					edges = tempNode.getFromEdges().iterator();
					while( edges.hasNext() )
					{
						tempEdge = edges.next();
						if( tempEdge.isVisible() )
						{
							addNode( selectedNode, distance + 1, actualDistance + tempEdge.getRestingDistance(), queue, tempEdge.getTo() );
						}
					}

					// To edges
					edges = tempNode.getToEdges().iterator();
					while( edges.hasNext() )
					{
						tempEdge = edges.next();
						if( tempEdge.isVisible() )
						{
							addNode( selectedNode, distance + 1, actualDistance + tempEdge.getRestingDistance(), queue, tempEdge.getFrom() );
						}
					}
				}
			}
		}
		bfsTimer.setEnd();
		if( Settings.DEBUG )
		{
			System.out.println( "Interpolation BFS took " + bfsTimer.getLastSegment( Timer.SECONDS ) + " seconds." );
		}

		// if( useActualDistance )
		// return Math.round( maxActualDistance );

		return maxDistance;
	}

	/**
	 * Adds the node.
	 * 
	 * @param selectedNode
	 *            the selected node
	 * @param distance
	 *            the distance
	 * @param actualDistance
	 *            the actual distance
	 * @param queue
	 *            the queue
	 * @param node
	 *            the node
	 */
	private static void addNode( DNVNode selectedNode, int distance, float actualDistance, List<DNVNode> queue, DNVNode node )
	{
		if( node.getDistanceFromNodeWithId( selectedNode.getId() ) > distance )
		{
			node.setDistanceFromSelectedNode( distance );
			node.setDistanceFromNode( selectedNode, distance );
			node.setActualDistanceFromSelectedNode( actualDistance );
			queue.add( node );
		}
	}

	/** The scalar1. */
	private static float scalar1 = 3;

	/** The scalar2. */
	private static float scalar2 = 2;

	/** The s curve low end. */
	private static float sCurveLowEnd = 0;

	/** The s curve high end. */
	private static float sCurveHighEnd = 1;

	/**
	 * Sets the weights.
	 * 
	 * @param graph
	 *            the graph
	 * @param level
	 *            the level
	 * @param maxDistance
	 *            the max distance
	 * @param useActualDistance
	 *            the use actual distance
	 * @param selectedNode
	 *            the selected node
	 */
	public static void setWeights( DNVGraph graph, Integer level, float maxDistance, boolean useActualDistance, DNVNode selectedNode )
	{
		setWeights( graph, level, maxDistance, scalar1, scalar2, sCurveLowEnd, sCurveHighEnd, useActualDistance, selectedNode );
	}

	/**
	 * Sets the weights.
	 * 
	 * @param graph
	 *            the graph
	 * @param level
	 *            the level
	 * @param maxDistance
	 *            the max distance
	 * @param sCurveLowEnd
	 *            the s curve low end
	 * @param sCurveHighEnd
	 *            the s curve high end
	 * @param useActualDistance
	 *            the use actual distance
	 * @param selectedNode
	 *            the selected node
	 */
	public static void setWeights( DNVGraph graph, Integer level, float maxDistance, float sCurveLowEnd, float sCurveHighEnd,
			boolean useActualDistance, DNVNode selectedNode )
	{
		setWeights( graph, level, maxDistance, scalar1, scalar2, sCurveLowEnd, sCurveHighEnd, useActualDistance, selectedNode );
	}

	/**
	 * Sets the weights.
	 * 
	 * @param graph
	 *            the graph
	 * @param level
	 *            the level
	 * @param maxDistance
	 *            the max distance
	 * @param scalar1
	 *            the scalar1
	 * @param scalar2
	 *            the scalar2
	 * @param sCurveLowEnd
	 *            the s curve low end
	 * @param sCurveHighEnd
	 *            the s curve high end
	 * @param useActualDistance
	 *            the use actual distance
	 * @param selectedNode
	 *            the selected node
	 */
	public static void setWeights( DNVGraph graph, Integer level, float maxDistance, float scalar1, float scalar2, float sCurveLowEnd,
			float sCurveHighEnd, boolean useActualDistance, DNVNode selectedNode )
	{
		Timer weightsTimer = new Timer( Timer.MILLISECONDS );
		DNVNode tempNode;
		float tempWeight;
		weightsTimer.setStart();
		Iterator<DNVNode> nodes = graph.getNodes( level ).iterator();
		float tempDistance;
		while( nodes.hasNext() )
		{
			tempNode = nodes.next();
			if( useActualDistance )
				tempDistance = tempNode.getActualDistanceFromSelectedNode();
			else
				tempDistance = tempNode.getDistanceFromNodeWithId( selectedNode.getId() );

			if( tempDistance != Integer.MAX_VALUE )
			{
				graph.addInterpolationNode( tempNode, level );
				tempWeight = 1.0f - ( (tempDistance) / (maxDistance) );
//				tempWeight =  1.0f / (tempDistance + 1);
				// System.out.println( "1.0 - (" +
				// tempNode.getDistanceFromSelectedNode() + " / " + maxDistance
				// + ") = " + tempWeight );
				if( tempWeight > sCurveLowEnd )
				{
//					tempWeight = ( tempWeight - sCurveLowEnd ) / (
//					sCurveHighEnd - sCurveLowEnd );
//					tempWeight = scalar1 * tempWeight * tempWeight - scalar2
//					* tempWeight * tempWeight * tempWeight;
					tempNode.setInterpolationWeight( selectedNode.getId(), tempWeight );
				}
			}
			else
			{
				tempWeight = 0;
				tempNode.setInterpolationWeight( selectedNode.getId(), tempWeight );
			}
		}

		weightsTimer.setEnd();
		if( Settings.DEBUG )
		{
			System.out.println( "Interpolation set weights took " + weightsTimer.getLastSegment( Timer.SECONDS ) + " seconds." );
		}
	}

	/**
	 * Apply function.
	 * 
	 * @param selectedNode
	 *            the selected node
	 * @param pb
	 *            the pb
	 * @param graph
	 *            the graph
	 * @param movement
	 *            the movement
	 * @param level
	 *            the level
	 * @param temperature
	 *            the temperature
	 */
	public static void applyFunction( DNVNode selectedNode, PaintBean pb, DNVGraph graph, Vector2D movement, Integer level, float temperature )
	{
		Timer applyTimer = new Timer( Timer.MILLISECONDS );
		applyTimer.setStart();
		Timer moveTimer = new Timer( Timer.MILLISECONDS );
		if( selectedNode != null )
		{
			List<DNVNode> nodes = graph.getInterpolationList( level );
			DNVNode tempNode;
			Vector2D tempMove = new Vector2D();
			for( int i = 0; i < nodes.size(); i++ )
			{
				tempNode = nodes.get( i );
				if( tempNode.getDistanceFromNodeWithId( selectedNode.getId() ) != Integer.MAX_VALUE && !tempNode.isSelected() && selectedNode != null )
				{
					tempMove.set( movement );
					if( tempNode != null && selectedNode != null )
					{
						tempMove.dotProduct( tempNode.getInterpolationWeight( selectedNode.getId() ) );
					}
	
					// System.out.println( tempNode.getDistanceFromSelectedNode() +
					// " : " + tempNode.getInterpolationWeight() + " : " + tempMove
					// + " : " + tempMove.length() );
					moveTimer.setStart();
					tempNode.move( tempMove, true, false );
					moveTimer.setEnd();
				}
			}
	
			// if( pb != null )
			// {
			// DNVNode selectedNode = pb.getSelectedNode();
			// if( selectedNode != null )
			// {
			// Vector2D difference = new Vector2D();
			// float length;
			// for( int distance : selectedNode.getDistances() )
			// {
			// Map<Integer,DNVNode> nodesAtDistance =
			// selectedNode.getNodesAtDistance( distance );
			// List<DNVNode> theNodes = new
			// ArrayList<DNVNode>(nodesAtDistance.values());
			// for( int i = 0; i < theNodes.size(); i++ )
			// {
			// DNVNode node1 = theNodes.get( i );
			// node1.getForce().set( 0, 0 );
			// for( int j = i+1; j < theNodes.size(); j++ )
			// {
			// DNVNode node2 = theNodes.get( j );
			// if( !node1.equals( node2 ) )
			// {
			// float overlap = pb.getOverlap( node1, node2 );
			// if( overlap > 0 )
			// {
			// System.out.println( "overlap is " + overlap );
			// difference.set( node1.getPosition() );
			// difference.subtract( node2.getPosition() );
			// difference.normalize();
			// difference.dotProduct( overlap );
			// node1.addForce( difference );
			// }
			// }
			// }
			// }
			//					
			// for( int id : nodesAtDistance.keySet() )
			// {
			// DNVNode node1 = nodesAtDistance.get( id );
			// difference.set( node1.getForce() );
			// Vector2D move = new Vector2D( movement );
			// move.normalize();
			// length = difference.length();
			// difference.normalize();
			// move.dotProduct( difference.dotProduct( move ) / move.length() /
			// move.length() );
			// difference.set( move.getY(), move.getX() );
			// difference.dotProduct( Math.min( length, temperature ) );
			// node1.move( difference, true, false );
			// }
			// }
			// }
			// }
		}
		
		applyTimer.setEnd();
		if( Settings.DEBUG )
		{
			System.out.println( "Interpolation apply function took " + applyTimer.getLastSegment( Timer.SECONDS ) + " seconds." );
			System.out.println( "Interpolation move node took " + moveTimer.getTotalTime( Timer.SECONDS ) + " seconds." );
		}
	}

	/**
	 * Gets the scalar1.
	 * 
	 * @return the scalar1
	 */
	public static float getScalar1()
	{
		return scalar1;
	}

	/**
	 * Sets the scalar1.
	 * 
	 * @param scalar1
	 *            the new scalar1
	 */
	public static void setScalar1( float scalar1 )
	{
		InterpolationMethod.scalar1 = scalar1;
	}

	/**
	 * Gets the scalar2.
	 * 
	 * @return the scalar2
	 */
	public static float getScalar2()
	{
		return scalar2;
	}

	/**
	 * Sets the scalar2.
	 * 
	 * @param scalar2
	 *            the new scalar2
	 */
	public static void setScalar2( float scalar2 )
	{
		InterpolationMethod.scalar2 = scalar2;
	}

	/**
	 * Gets the s curve low end.
	 * 
	 * @return the s curve low end
	 */
	public static float getSCurveLowEnd()
	{
		return sCurveLowEnd;
	}

	/**
	 * Sets the s curve low end.
	 * 
	 * @param curveLowEnd
	 *            the new s curve low end
	 */
	public static void setSCurveLowEnd( float curveLowEnd )
	{
		sCurveLowEnd = curveLowEnd;
	}

	/**
	 * Gets the s curve high end.
	 * 
	 * @return the s curve high end
	 */
	public static float getSCurveHighEnd()
	{
		return sCurveHighEnd;
	}

	/**
	 * Sets the s curve high end.
	 * 
	 * @param curveHighEnd
	 *            the new s curve high end
	 */
	public static void setSCurveHighEnd( float curveHighEnd )
	{
		sCurveHighEnd = curveHighEnd;
	}

	/**
	 * Increase s curve low end.
	 */
	public static void increaseSCurveLowEnd()
	{
		sCurveLowEnd += 0.01;
		if( sCurveLowEnd > sCurveHighEnd )
			sCurveLowEnd -= 0.01;

		System.out.println( "SCurve Low End: " + sCurveLowEnd );
	}

	/**
	 * Decrease s curve low end.
	 */
	public static void decreaseSCurveLowEnd()
	{
		sCurveLowEnd -= 0.01;
		if( sCurveLowEnd < 0 )
			sCurveLowEnd = 0;

		System.out.println( "SCurve Low End: " + sCurveLowEnd );
	}

	/**
	 * Increase s curve high end.
	 */
	public static void increaseSCurveHighEnd()
	{
		sCurveHighEnd += 0.01;
		if( sCurveHighEnd > 1 )
			sCurveHighEnd = 1;

		System.out.println( "SCurve High End: " + sCurveHighEnd );
	}

	/**
	 * Decrease s curve high end.
	 */
	public static void decreaseSCurveHighEnd()
	{
		sCurveHighEnd -= 0.01;
		if( sCurveHighEnd < sCurveLowEnd )
			sCurveHighEnd += 0.01;

		System.out.println( "SCurve High End: " + sCurveHighEnd );
	}
}
