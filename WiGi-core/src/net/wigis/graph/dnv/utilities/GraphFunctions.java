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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.wigis.graph.GraphsPathFilter;
import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVEntity;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.EdgeNodeAndValue;
import net.wigis.graph.dnv.EdgeQueue;
import net.wigis.graph.dnv.geometry.Circle;
import net.wigis.graph.dnv.geometry.Geometric;
import net.wigis.graph.dnv.geometry.Line;
import net.wigis.graph.dnv.geometry.Rectangle;
import net.wigis.graph.dnv.geometry.Text;
import net.wigis.graph.jgrapht.JGraphTEdge;
import net.wigis.graph.jgrapht.converter.JGraphTConverter;
import net.wigis.settings.Settings;

import org.jgrapht.GraphPath;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.BronKerboschCliqueFinder;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.alg.KShortestPaths;

// TODO: Auto-generated Javadoc
/**
 * A class containing some useful static functions.
 * 
 * @author Brynjar Gretarsson
 */
public final class GraphFunctions
{
	// private static RandomGraphGenerator generator;

	/** The Constant DNV_NODE. */
	public static final String DNV_NODE = "DNV Node";

	/** The Constant DNV_EDGE. */
	public static final String DNV_EDGE = "DNV Edge";

	// private static VertexFactory factory;

	// private static List<DNVNode> allNeighbors = new ArrayList<DNVNode>();

	/** The Constant numberOfNeighborLevels. */
	public static final int numberOfNeighborLevels = 3;

	/** The Constant colorEntity. */
	public static final Vector3D colorEntity = new Vector3D( 1.0f, 1.0f, 0.3f );

	/** The Constant colorDocument. */
	public static final Vector3D colorDocument = new Vector3D( 0.7f, 0.7f, 0.9f );

	/** The Constant colorContainer. */
	public static final Vector3D colorContainer = new Vector3D( 1.0f, 0.3f, 0.3f );

	/** The selected nodes. */
	public static Map<String, DNVNode> selectedNodes = new HashMap<String, DNVNode>();

	/**
	 * Logger.
	 * 
	 * @param node1
	 *            the node1
	 * @param node2
	 *            the node2
	 * @param display
	 *            the display
	 * @return the distance
	 */
	// // private static Log logger = LogFactory.getLog( GraphFunctions.class );

	/*
	 * public static float fitness( DNVGraph graph, Integer level ) { float
	 * fitness = 0;
	 * 
	 * List<DNVNode> allNodes = graph.getNodes( level ); Iterator<DNVNode> i =
	 * allNodes.iterator(); Iterator<DNVNode> j; float numberOfNodes =
	 * graph.getGraphSize( level ); float numberOfEdges = graph.getEdges( level
	 * ).size(); float sumOfDistances = 0; float tempDistance = 0; float
	 * sumOfRestingErrors = 0; DNVNode iNode; DNVNode jNode; while( i.hasNext()
	 * ) { iNode = i.next(); j = allNodes.iterator(); while( j.hasNext() ) {
	 * jNode = j.next(); if( jNode != iNode ) { tempDistance = getDistance(
	 * iNode, jNode ); if( iNode.isNeighbor( jNode ) ) { sumOfRestingErrors +=
	 * Math.abs( tempDistance - iNode.getEdgeToNeighbor( jNode.getId()
	 * ).getRestingDistance() ); } else { sumOfDistances += tempDistance; } } }
	 * }
	 * 
	 * sumOfDistances = sumOfDistances / numberOfNodes / numberOfNodes;
	 * sumOfRestingErrors = sumOfRestingErrors / numberOfEdges / numberOfEdges;
	 * fitness = sumOfDistances - sumOfRestingErrors;
	 * 
	 * return fitness; }
	 */

	// private static float angle( DNVNode node1, DNVNode node2 )
	// {
	// Vector2D point1 = new Vector2D( node1.getPosition() );
	// point1.normalize();
	// Vector2D point2 = new Vector2D( node2.getPosition() );
	// point2.normalize();
	//
	// return point1.angle( point2 );
	// }

	/**
	 * @param node1
	 * @param node2
	 * @return The distance between node1 and node2.
	 */
	public static float getDistance( DNVNode node1, DNVNode node2, boolean display )
	{
		return getDistance( node1.getPosition( display ), node2.getPosition( display ) );
	}

	/**
	 * Gets the distance.
	 * 
	 * @param x1
	 *            the x1
	 * @param y1
	 *            the y1
	 * @param point2
	 *            the point2
	 * @return the distance
	 */
	public static float getDistance( float x1, float y1, Vector2D point2 )
	{
		float x2 = point2.getX();
		float y2 = point2.getY();

		return getDistance( x1, y1, x2, y2 );
	}

	/**
	 * Gets the distance.
	 * 
	 * @param point1
	 *            the point1
	 * @param point2
	 *            the point2
	 * @return The distance between point1 and point2.
	 */
	public static float getDistance( Vector2D point1, Vector2D point2 )
	{
		float x1 = point1.getX();
		float y1 = point1.getY();
		float x2 = point2.getX();
		float y2 = point2.getY();

		return getDistance( x1, y1, x2, y2 );
	}

	/**
	 * Gets the distance.
	 * 
	 * @param x1
	 *            the x1
	 * @param y1
	 *            the y1
	 * @param x2
	 *            the x2
	 * @param y2
	 *            the y2
	 * @return The distance between the two points (x1,y1,z2) and (x2,y2,z2)
	 */
	public static float getDistance( float x1, float y1, float x2, float y2 )
	{

		return (float)Math.sqrt( Math.pow( x1 - x2, 2 ) + Math.pow( y1 - y2, 2 ) );
	}

	/**
	 * Sets the selected node.
	 * 
	 * @param graph
	 *            the graph
	 * @param selectedNode
	 *            the selected node
	 */
	public static void setSelectedNode( DNVGraph graph, DNVNode selectedNode )
	{
		if( selectedNode == null )
			selectedNodes.remove( graph.toString() );
		else
			selectedNodes.put( graph.toString(), selectedNode );
	}

	/**
	 * Gets the graph width.
	 * 
	 * @param graph
	 *            the graph
	 * @param level
	 *            the level
	 * @param display
	 *            the display
	 * @return the graph width
	 */
	public static float getGraphWidth( DNVGraph graph, Integer level, boolean display )
	{
		return getGraphWidth( graph.getNodes( level ), display );
	}

	/**
	 * Gets the graph width.
	 * 
	 * @param nodes
	 *            the nodes
	 * @param display
	 *            the display
	 * @return the graph width
	 */
	public static float getGraphWidth( Collection<DNVNode> nodes, boolean display )
	{
		return getMaxXPosition( nodes, display ) - getMinXPosition( nodes, display );
	}

	/**
	 * Gets the graph height.
	 * 
	 * @param graph
	 *            the graph
	 * @param level
	 *            the level
	 * @param display
	 *            the display
	 * @return the graph height
	 */
	public static float getGraphHeight( DNVGraph graph, Integer level, boolean display )
	{
		return getGraphHeight( graph.getNodes( level ), display );
	}

	/**
	 * Gets the graph height.
	 * 
	 * @param nodes
	 *            the nodes
	 * @param display
	 *            the display
	 * @return the graph height
	 */
	public static float getGraphHeight( Collection<DNVNode> nodes, boolean display )
	{
		return getMaxYPosition( nodes, display ) - getMinYPosition( nodes, display );
	}

	/**
	 * Gets the max x position.
	 * 
	 * @param graph
	 *            the graph
	 * @param level
	 *            the level
	 * @param display
	 *            the display
	 * @return the max x position
	 */
	public static float getMaxXPosition( DNVGraph graph, Integer level, boolean display )
	{
		return getMaxXPosition( graph.getNodes( level ), display );
	}

	/**
	 * Gets the max x position.
	 * 
	 * @param nodeList
	 *            the node list
	 * @param display
	 *            the display
	 * @return the max x position
	 */
	public static float getMaxXPosition( Collection<DNVNode> nodeList, boolean display )
	{
		if( nodeList != null )
		{
			Iterator<DNVNode> nodes = nodeList.iterator();
			DNVNode tempNode = null;
			float max = Float.NEGATIVE_INFINITY;
			float temp;
			while( nodes.hasNext() )
			{
				tempNode = nodes.next();
				temp = tempNode.getPosition( display ).getX();
				if( temp > max )
					max = temp;
			}

			return max;
		}

		return 0;
	}

	/**
	 * Gets the max y position.
	 * 
	 * @param graph
	 *            the graph
	 * @param level
	 *            the level
	 * @param display
	 *            the display
	 * @return the max y position
	 */
	public static float getMaxYPosition( DNVGraph graph, Integer level, boolean display )
	{
		return getMaxYPosition( graph.getNodes( level ), display );
	}

	/**
	 * Gets the max y position.
	 * 
	 * @param nodeList
	 *            the node list
	 * @param display
	 *            the display
	 * @return the max y position
	 */
	public static float getMaxYPosition( Collection<DNVNode> nodeList, boolean display )
	{
		if( nodeList != null )
		{
			Iterator<DNVNode> nodes = nodeList.iterator();
			DNVNode tempNode = null;
			float max = Float.NEGATIVE_INFINITY;
			float temp;
			while( nodes.hasNext() )
			{
				tempNode = nodes.next();
				temp = tempNode.getPosition( display ).getY();
				if( temp > max )
					max = temp;
			}

			return max;
		}

		return 0;
	}

	/**
	 * Gets the min x position.
	 * 
	 * @param graph
	 *            the graph
	 * @param level
	 *            the level
	 * @param display
	 *            the display
	 * @return the min x position
	 */
	public static float getMinXPosition( DNVGraph graph, Integer level, boolean display )
	{
		return getMinXPosition( graph.getNodes( level ), display );
	}

	/**
	 * Gets the min x position.
	 * 
	 * @param nodeList
	 *            the node list
	 * @param display
	 *            the display
	 * @return the min x position
	 */
	public static float getMinXPosition( Collection<DNVNode> nodeList, boolean display )
	{
		float min = Float.POSITIVE_INFINITY;
		if( nodeList != null )
		{
			Iterator<DNVNode> nodes = nodeList.iterator();
			DNVNode tempNode = null;
			float temp;
			while( nodes.hasNext() )
			{
				tempNode = nodes.next();
				temp = tempNode.getPosition( display ).getX();
				if( temp < min )
					min = temp;
			}

			return min;
		}

		return 0;
	}

	/**
	 * Gets the min y position.
	 * 
	 * @param graph
	 *            the graph
	 * @param level
	 *            the level
	 * @param display
	 *            the display
	 * @return the min y position
	 */
	public static float getMinYPosition( DNVGraph graph, Integer level, boolean display )
	{
		return getMinYPosition( graph.getNodes( level ), display );
	}

	/**
	 * Gets the min y position.
	 * 
	 * @param nodeList
	 *            the node list
	 * @param display
	 *            the display
	 * @return the min y position
	 */
	public static float getMinYPosition( Collection<DNVNode> nodeList, boolean display )
	{
		if( nodeList != null )
		{
			Iterator<DNVNode> nodes = nodeList.iterator();
			DNVNode tempNode = null;
			float min = Float.POSITIVE_INFINITY;
			float temp;
			while( nodes.hasNext() )
			{
				tempNode = nodes.next();
				temp = tempNode.getPosition( display ).getY();
				if( temp < min )
					min = temp;
			}

			return min;
		}

		return 0;
	}

	/** The center. */
	private static Vector2D center = new Vector2D();

	/**
	 * Gets the center of gravity.
	 * 
	 * @param i
	 *            the i
	 * @return the center of gravity
	 */
	public static Vector2D getCenterOfGravity( Iterator<DNVNode> i )
	{
		DNVNode tempNode = null;
		float totalX = 0;
		float totalY = 0;
		float number = 0;
		Vector2D position;
		Object temp;
		while( i.hasNext() )
		{
			temp = i.next();
			if( temp instanceof DNVNode )
				tempNode = (DNVNode)temp;
			if( tempNode != null )
			{
				position = tempNode.getPosition();
				totalX += position.getX();
				totalY += position.getY();
				number++;
			}
		}

		center.setX( totalX / number );
		center.setY( totalY / number );

		return center;
	}

	/**
	 * Gets the average x position.
	 * 
	 * @param i
	 *            the i
	 * @return the average x position
	 */
	public static float getAverageXPosition( Iterator<DNVNode> i )
	{
		DNVNode tempNode = null;
		float total = 0;
		float number = 0;
		Object temp;
		float subnodes = 0;
		while( i.hasNext() )
		{
			temp = i.next();
			if( temp instanceof DNVNode )
				tempNode = (DNVNode)temp;
			subnodes = tempNode.getTotalNumberOfSubNodes();
			total += tempNode.getPosition().getX() * subnodes;
			number += subnodes;
		}

		return total / number;

	}

	/**
	 * Gets the average y position.
	 * 
	 * @param i
	 *            the i
	 * @return the average y position
	 */
	public static float getAverageYPosition( Iterator<DNVNode> i )
	{
		DNVNode tempNode = null;
		float total = 0;
		float number = 0;
		Object temp;
		float subnodes = 0;
		while( i.hasNext() )
		{
			temp = i.next();
			if( temp instanceof DNVNode )
				tempNode = (DNVNode)temp;
			subnodes = tempNode.getTotalNumberOfSubNodes();
			total += tempNode.getPosition().getY() * subnodes;
			number += subnodes;
		}

		return total / number;

	}

	/** The processed edges. */
	private static Map<Integer, Float> processedEdges = new HashMap<Integer, Float>();

	/**
	 * Initialize positions.
	 * 
	 * @param graph
	 *            the graph
	 */
	public static void initializePositions( DNVGraph graph )
	{
		if( graph != null )
		{
			int maxLevel = graph.getMaxLevel();
			for( int i = 0; i <= maxLevel; i++ )
			{
				initializeLevel( i, graph );
			}
		}
	}

	/**
	 * Initialize level.
	 * 
	 * @param level
	 *            the level
	 * @param graph
	 *            the graph
	 */
	private static void initializeLevel( int level, DNVGraph graph )
	{
		processedEdges.clear();
		Iterator<DNVNode> nodes;
		Vector2D center = Vector2D.ZERO;
		System.out.println( "Processing level " + level );
		if( level > 0 )
		{
			System.out.println( "     Updating edge lengths and masses" );
			nodes = graph.getNodes( level ).iterator();
			while( nodes.hasNext() )
				nodes.next().updateEdgelengthsAndMass( processedEdges, false );
		}

		nodes = graph.getNodes( level ).iterator();
		int size = graph.getGraphSize( level );
		DNVNode tempNode;
		DNVNode parentNode;
		int siblings = 0;
		int i = 0;
		System.out.println( "     Setting the positions for " + size + " nodes." );
		float distance;
		Timer distanceTimer = new Timer( Timer.NANOSECONDS );
		Timer placingTimer = new Timer( Timer.NANOSECONDS );
		while( nodes.hasNext() )
		{
			tempNode = nodes.next();
			parentNode = tempNode.getParentNode();
			if( parentNode != null )
			{
				center = parentNode.getPosition();
				siblings = parentNode.getNumberOfSubNodes();
			}
			else
			{
				center = new Vector2D( (float)Math.random(), (float)Math.random() );
				siblings = 1;
			}

			distanceTimer.setStart();
			distance = getDistanceFromFirstSibling( tempNode );
			distanceTimer.setEnd();
			if( distanceTimer.getLastSegment( Timer.SECONDS ) > 1 )
			{
				System.out.println( "Getting distance from " + tempNode.getId() + " to " + tempNode.getFirstSibling().getId() + " took "
						+ distanceTimer.getLastSegment( Timer.SECONDS ) + " seconds. Distance is " + distance );
			}
			placingTimer.setStart();
			tempNode.placeNode( i, siblings, distance, center, true, graph.getMaxLevel() );
			placingTimer.setEnd();
			// System.out.println( "Placing node took " +
			// placingTimer.getLastSegment( Timer.SECONDS ) + " seconds." );
			i++;
		}

		System.out.println( "Average time for getting distance " + distanceTimer.getAverageTime( Timer.SECONDS ) + " seconds." );
		System.out.println( "Average time for placing node     " + placingTimer.getAverageTime( Timer.SECONDS ) + " seconds." );

	}

	/**
	 * Gets the distance from first sibling.
	 * 
	 * @param node
	 *            the node
	 * @return the distance from first sibling
	 */
	public static float getDistanceFromFirstSibling( DNVNode node )
	{
		float distance = 0;

		DNVNode otherNode = node.getFirstSibling();
		if( otherNode != null )
		{
			if( otherNode == node )
				return 0;

			distance = findShortestPathDistance( node, otherNode );
			if( distance == Float.POSITIVE_INFINITY )
				System.out.println( "Unable to find path between nodes " + node.getId() + " and " + otherNode.getId() );
		}

		return distance;
	}

	/**
	 * Find shortest path distance.
	 * 
	 * @param startNode
	 *            the start node
	 * @param goalNode
	 *            the goal node
	 * @return the float
	 */
	public static float findShortestPathDistance( DNVNode startNode, DNVNode goalNode )
	{
		Map<Integer, Float> visitedNodes = new HashMap<Integer, Float>();
		EdgeQueue edgeQueue = new EdgeQueue();
		addTimer.reset();
		float value = findShortestPathDistance( edgeQueue, visitedNodes, 0, Float.POSITIVE_INFINITY, startNode, goalNode, true, false );

		return value;
	}

	/**
	 * Find shortest path distance in number of hops.
	 * 
	 * @param startNode
	 *            the start node
	 * @param goalNode
	 *            the goal node
	 * @return the float
	 */
	public static float findShortestPathDistanceInNumberOfHops( DNVNode startNode, DNVNode goalNode )
	{
		Map<Integer, Float> visitedNodes = new HashMap<Integer, Float>();
		EdgeQueue edgeQueue = new EdgeQueue();
		addTimer.reset();
		float value = findShortestPathDistance( edgeQueue, visitedNodes, 0, Float.POSITIVE_INFINITY, startNode, goalNode, false, false );

		return value;
	}

	/** The add timer. */
	private static Timer addTimer = new Timer( Timer.NANOSECONDS );

	// private static Timer processTimer = new Timer( Timer.NANOSECONDS );
	/**
	 * Find shortest path distance.
	 * 
	 * @param edgeQueue
	 *            the edge queue
	 * @param visitedNodes
	 *            the visited nodes
	 * @param distance
	 *            the distance
	 * @param bestDistance
	 *            the best distance
	 * @param startNode
	 *            the start node
	 * @param goalNode
	 *            the goal node
	 * @param useActualEdgeLengths
	 *            the use actual edge lengths
	 * @return the float
	 */
	private static float findShortestPathDistance( EdgeQueue edgeQueue, Map<Integer, Float> visitedNodes, float distance, float bestDistance,
			DNVNode startNode, DNVNode goalNode, boolean useActualEdgeLengths, boolean visibleOnly )
	{
		// Stopping condition
		if( startNode == goalNode )
		{
			return distance;
		}
		if( distance > bestDistance )
			return Float.POSITIVE_INFINITY;

		addTimer.setStart();
		edgeQueue.addAll( visitedNodes, startNode, startNode.getFromEdges(visibleOnly), distance, useActualEdgeLengths );
		edgeQueue.addAll( visitedNodes, startNode, startNode.getToEdges(visibleOnly), distance, useActualEdgeLengths );
		addTimer.setEnd();

		EdgeNodeAndValue enav;
		float newDistance = 0;
		Float previousDistance = 0.0f;
		while( !edgeQueue.isEmpty() )
		{
			enav = edgeQueue.pop();
			visitedNodes = enav.getPath();
			previousDistance = visitedNodes.get( enav.getNode().getId() );
			if( previousDistance == null )
			{
				visitedNodes.put( enav.getNode().getId(), enav.getValue() );
				newDistance = findShortestPathDistance( edgeQueue, visitedNodes, enav.getValue(), bestDistance, enav.getOtherNode(), goalNode,
						useActualEdgeLengths, visibleOnly );
				if( newDistance < bestDistance )
				{
					bestDistance = newDistance;
				}
			}
		}

		return bestDistance;
	}

	/**
	 * Removes the isolated nodes.
	 * 
	 * @param graph
	 *            the graph
	 * @param level
	 *            the level
	 */
	public static void removeIsolatedNodes( DNVGraph graph, Integer level )
	{
		List<DNVNode> nodes = graph.getNodes( level );
		DNVNode tempNode;
		for( int i = 0; i < nodes.size(); i++ )
		{
			tempNode = nodes.get( i );
			if( tempNode.getNeighbors().size() == 0 )
			{
				graph.removeNode( level, tempNode );
			}
		}
	}

	/**
	 * Clear higher levels.
	 * 
	 * @param level
	 *            the level
	 * @param graph
	 *            the graph
	 */
	public static void clearHigherLevels( int level, DNVGraph graph )
	{
		for( int i = level + 1; i <= graph.getMaxLevel(); i++ )
		{
			graph.clearLevel( i );
		}
	}

	/*
	 * public static List<DNVNode> convertCollectionToList( Collection<DNVNode>
	 * collection ) { if( collection instanceof List )
	 * return(List<DNVNode>)collection;
	 * 
	 * return new ArrayList<DNVNode>(collection); }
	 */
	/**
	 * Convert collection to list.
	 * 
	 * @param collection
	 *            the collection
	 * @return the list
	 */
	public static List<DNVEntity> convertCollectionToList( Collection<DNVEntity> collection )
	{
		if( collection instanceof List<?> )
			return (List<DNVEntity>)collection;

		return new ArrayList<DNVEntity>( collection );
	}

	/**
	 * Gets the maximum pairwise shortest path.
	 * 
	 * @param graph
	 *            the graph
	 * @param level
	 *            the level
	 * @return the maximum pairwise shortest path
	 */
	public static int getMaximumPairwiseShortestPath( DNVGraph graph, int level )
	{
		float max = 0;
		float temp;
		List<DNVNode> nodes = graph.getNodes( level );
		for( int i = 0; i < nodes.size(); i++ )
		{
			for( int j = i + 1; j < nodes.size(); j++ )
			{
				temp = findShortestPathDistanceInNumberOfHops( nodes.get( i ), nodes.get( j ) );
				if( temp != Float.POSITIVE_INFINITY && temp > max )
				{
					max = temp;
				}
			}
		}

		return (int)max;
	}

	/**
	 * Gets the edge to node of type.
	 * 
	 * @param graph
	 *            the graph
	 * @param node
	 *            the node
	 * @param type
	 *            the type
	 * @return the edge to node of type
	 */
	public static DNVEdge getEdgeToNodeOfType( DNVGraph graph, DNVNode node, String type )
	{
		Iterator<DNVEdge> edges;
		DNVNode neighbor;
		DNVEdge edge;
		try
		{
			edges = node.getFromEdges().iterator();
			while( edges.hasNext() )
			{
				edge = edges.next();
				neighbor = edge.getTo();
				if( neighbor.getType().equals( type ) )
				{
					return edge;
				}
			}
		}
		catch( NullPointerException npe )
		{}

		try
		{
			edges = node.getToEdges().iterator();
			while( edges.hasNext() )
			{
				edge = edges.next();
				neighbor = edge.getFrom();
				if( neighbor.getType().equals( type ) )
				{
					return edge;
				}
			}
		}
		catch( NullPointerException npe )
		{}
		return null;
	}

	/**
	 * Gets the k nearest node ids.
	 * 
	 * @param k
	 *            the k
	 * @param node
	 *            the node
	 * @param graph
	 *            the graph
	 * @param level
	 *            the level
	 * @param restingDistance
	 *            the resting distance
	 * @param type
	 *            the type
	 * @return the k nearest node ids
	 */
	public static List<Integer> getKNearestNodeIds( int k, DNVNode node, DNVGraph graph, int level, boolean restingDistance, String type )
	{
		List<Integer> theIds = new ArrayList<Integer>();

		DNVNode[] theNodes = getKNearestNodes( k, node, graph, level, restingDistance, type );

		String tempStr;
		for( int i = 0; i < k; i++ )
		{
			if( theNodes[i] != null )
			{
				tempStr = theNodes[i].getBbId();
				tempStr = tempStr.replace( "user", "" );
				try
				{
					theIds.add( Integer.parseInt( tempStr ) );
				}
				catch( NumberFormatException nfe )
				{}
			}
		}

		return theIds;
	}

	/**
	 * Gets the k nearest nodes.
	 * 
	 * @param k
	 *            the k
	 * @param node
	 *            the node
	 * @param graph
	 *            the graph
	 * @param level
	 *            the level
	 * @param restingDistance
	 *            the resting distance
	 * @param type
	 *            the type
	 * @return the k nearest nodes
	 */
	public static DNVNode[] getKNearestNodes( int k, DNVNode node, DNVGraph graph, int level, boolean restingDistance, String type )
	{
		DNVNode[] theNodes = new DNVNode[k];
		Vector2D position = node.getPosition();
		Comparator<DNVNode> sort;
		if( restingDistance )
			sort = new RestingDistanceSort();
		else
			sort = new DistanceSort( position );
		Iterator<DNVNode> nodes = graph.getNodes( level ).iterator();
		List<DNVNode> nodesList = new ArrayList<DNVNode>();
		DNVNode tempNode;
		while( nodes.hasNext() )
		{
			tempNode = nodes.next();
			if( tempNode.getType().equals( type ) && !tempNode.equals( node ) )
				nodesList.add( tempNode );
		}

		Collections.sort( nodesList, sort );

		for( int i = 0; i < k && i < nodesList.size(); i++ )
		{
			theNodes[i] = nodesList.get( i );
		}

		return theNodes;
	}

	/**
	 * Gets the node by type.
	 * 
	 * @param type
	 *            the type
	 * @param graph
	 *            the graph
	 * @param level
	 *            the level
	 * @return the node by type
	 */
	public static DNVNode getNodeByType( String type, DNVGraph graph, Integer level )
	{
		Map<Integer, DNVEntity> nodes = graph.getNodesByType( level, type );
		if( nodes != null && nodes.size() > 0 )
			return (DNVNode)nodes.values().iterator().next();

		return null;
	}

	/**
	 * Color k nearest nodes.
	 * 
	 * @param graph
	 *            the graph
	 * @param level
	 *            the level
	 * @param k
	 *            the k
	 * @param centerType
	 *            the center type
	 * @param peersType
	 *            the peers type
	 */
	public static void colorKNearestNodes( DNVGraph graph, int level, int k, String centerType, String peersType )
	{
		for( DNVNode tempNode : graph.getNodes( level ) )
		{
			tempNode.setColor( 1, 1, 1 );
		}

		DNVNode activeNode = GraphFunctions.getNodeByType( centerType, graph, level );
		DNVNode[] peersholder = GraphFunctions.getKNearestNodes( k, activeNode, graph, level, false, peersType );

		for( int i = 0; i < peersholder.length; i++ )
		{
			peersholder[i].setColor( 1f, 0.5f, 0.5f );
		}
	}

	/**
	 * Convert color.
	 * 
	 * @param datasourceColor
	 *            the datasource color
	 * @return the vector3 d
	 */
	public static Vector3D convertColor( String datasourceColor )
	{
		Vector3D newColor = new Vector3D();
		float r;
		float g;
		float b;

		if( datasourceColor == null )
		{
			r = 0;
			g = 0;
			b = 0;
			newColor.set( r, g, b );
			return newColor;
		}

		if( datasourceColor.startsWith( "#" ) )
		{
			datasourceColor = datasourceColor.substring( 1 );
		}

		r = Integer.parseInt( datasourceColor.substring( 0, 2 ), 16 ) / 255.0f;
		g = Integer.parseInt( datasourceColor.substring( 2, 4 ), 16 ) / 255.0f;
		b = Integer.parseInt( datasourceColor.substring( 4, 6 ), 16 ) / 255.0f;
		newColor.set( r, g, b );

		return newColor;
	}

	/**
	 * Are connected.
	 * 
	 * @param itemNode
	 *            the item node
	 * @param friendNode
	 *            the friend node
	 * @return true, if successful
	 */
	public static boolean areConnected( DNVNode itemNode, DNVNode friendNode )
	{
		Map<Integer, DNVNode> neighborMap = itemNode.getNeighborMap();
		DNVNode node = neighborMap.get( friendNode.getId() );
		if( node != null )
			return true;

		return false;
	}

	/**
	 * Clear hop distance for all nodes.
	 * 
	 * @param graph
	 *            the graph
	 * @param level
	 *            the level
	 */
	public static void clearHopDistanceForAllNodes( DNVGraph graph, int level )
	{
		for( DNVNode node : graph.getNodes( level ) )
		{
			node.removeProperty( "hopDistance" );
		}
	}

	/**
	 * Find shortest path to all nodes in number of hops.
	 * 
	 * @param centralNode
	 *            the central node
	 */
	public static void findShortestPathToAllNodesInNumberOfHops( DNVNode centralNode )
	{
		centralNode.setProperty( "hopDistance", "" + 0.0f );
		Map<Integer, DNVNode> neighbors = centralNode.getNeighborMap();
		handleNodes( 1, neighbors );
	}

	/**
	 * Handle nodes.
	 * 
	 * @param distance
	 *            the distance
	 * @param nodes
	 *            the nodes
	 */
	private static void handleNodes( float distance, Map<Integer, DNVNode> nodes )
	{
		Map<Integer, DNVNode> neighbors = new HashMap<Integer, DNVNode>();
		float hopDistance;
		for( DNVNode node : nodes.values() )
		{
			hopDistance = getHopDistance( node );
			if( hopDistance > distance )
			{
				node.setProperty( "hopDistance", "" + distance );
				// if( distance > 3 )
				// {
				// node.setRadius( 1 );
				// }
				for( DNVNode tempNode : node.getNeighbors() )
				{
					neighbors.put( tempNode.getId(), tempNode );
				}
			}
		}

		if( neighbors.size() > 0 )
		{
			handleNodes( distance + 1, neighbors );
		}
	}

	/**
	 * Gets the hop distance.
	 * 
	 * @param node
	 *            the node
	 * @return the hop distance
	 */
	public static float getHopDistance( DNVNode node )
	{
		String hopDistanceStr = node.getProperty( "hopDistance" );
		if( hopDistanceStr == null )
		{
			return Float.POSITIVE_INFINITY;
		}

		try
		{
			return Float.parseFloat( hopDistanceStr );
		}
		catch( NumberFormatException nfe )
		{
			return Float.POSITIVE_INFINITY;
		}
	}

	/**
	 * Scale positions.
	 * 
	 * @param graph
	 *            the graph
	 * @param level
	 *            the level
	 * @param newWidth
	 *            the new width
	 * @param newHeight
	 *            the new height
	 * @param oldWidth
	 *            the old width
	 * @param oldHeight
	 *            the old height
	 */
	public static void scalePositions( DNVGraph graph, int level, double newWidth, double newHeight, double oldWidth, double oldHeight )
	{
		double xRatio = newWidth / oldWidth;
		double yRatio = newHeight / oldHeight;
		for( DNVNode node : graph.getNodes( level ) )
		{
			// System.out.print( "Transforming " + node.getPosition() + " to "
			// );
			node.setPosition( (float)( node.getPosition().getX() * xRatio ), (float)( node.getPosition().getY() * yRatio ) );
			// System.out.println( node.getPosition() );
		}

		if( graph.getGeometricObjects( level ) != null )
		{
			for( Geometric g : graph.getGeometricObjects( level ) )
			{
				if( g instanceof Circle )
				{
					Circle c = (Circle)g;
					c.setCenter( (float)( c.getCenter().getX() * xRatio ), (float)( c.getCenter().getY() * yRatio ) );
				}
				else if( g instanceof Rectangle )
				{
					Rectangle r = (Rectangle)g;
					r.setBottomRight( (float)( r.getBottomRight().getX() * xRatio ), (float)( r.getBottomRight().getY() * yRatio ) );
					r.setTopLeft( (float)( r.getTopLeft().getX() * xRatio ), (float)( r.getTopLeft().getY() * yRatio ) );
				}
				else if( g instanceof Line )
				{
					Line l = (Line)g;
					l.setStart( (float)( l.getStart().getX() * xRatio ), (float)( l.getStart().getY() * yRatio ) );
					l.setEnd( (float)( l.getEnd().getX() * xRatio ), (float)( l.getEnd().getY() * yRatio ) );
				}
				else if( g instanceof Text )
				{
					Text t = (Text)g;
					t.setPosition( (float)( t.getPosition().getX() * xRatio ), (float)( t.getPosition().getY() * yRatio ) );
				}
			}
		}
	}

	public static Collection<Set<DNVNode>> getAllMaximalCliques( DNVGraph graph, int level )
	{
		BronKerboschCliqueFinder<DNVNode, JGraphTEdge<DNVNode>> cliqueFinder = getCliqueFinder( graph, level );
		Collection<Set<DNVNode>> cliques = cliqueFinder.getAllMaximalCliques();

		return cliques;
	}

	/**
	 * @param graph
	 * @param level
	 * @return
	 */
	private static BronKerboschCliqueFinder<DNVNode, JGraphTEdge<DNVNode>> getCliqueFinder( DNVGraph graph, int level )
	{
		UndirectedGraph<DNVNode, JGraphTEdge<DNVNode>> g = JGraphTConverter.convertDNVToUndirectedJGraphT( graph, level );
		BronKerboschCliqueFinder<DNVNode, JGraphTEdge<DNVNode>> cliqueFinder = new BronKerboschCliqueFinder<DNVNode, JGraphTEdge<DNVNode>>( g );
		return cliqueFinder;
	}

	public static Collection<Set<DNVNode>> getBiggestMaximalCliques( DNVGraph graph, int level )
	{
		BronKerboschCliqueFinder<DNVNode, JGraphTEdge<DNVNode>> cliqueFinder = getCliqueFinder( graph, level );
		Collection<Set<DNVNode>> cliques = cliqueFinder.getBiggestMaximalCliques();

		return cliques;
	}
	
	public static List<List<DNVNode>> getNodesOnKShortestPaths( DNVGraph graph, int level, DNVNode startNode, DNVNode endNode, int k )
	{
		List<List<DNVNode>> allPaths = new ArrayList<List<DNVNode>>();
		
		UndirectedGraph<DNVNode,JGraphTEdge<DNVNode>> g = JGraphTConverter.convertDNVToUndirectedJGraphT( graph, level );
		
		KShortestPaths<DNVNode,JGraphTEdge<DNVNode>> paths = new KShortestPaths<DNVNode, JGraphTEdge<DNVNode>>( g, startNode, k );
		List<GraphPath<DNVNode,JGraphTEdge<DNVNode>>> thePaths = paths.getPaths( endNode );
		if( thePaths == null )
		{
			return null;
		}

		for( GraphPath<DNVNode,JGraphTEdge<DNVNode>> path : thePaths )
		{
			List<DNVNode> tempPath = new ArrayList<DNVNode>();
			tempPath.add( path.getStartVertex() );
			DNVNode lastNode = path.getStartVertex();
			for( JGraphTEdge<DNVNode> edge : path.getEdgeList() )
			{
				if( edge.getSource() == lastNode )
				{
					tempPath.add( edge.getTarget() );
					lastNode = edge.getTarget();
				}
				else
				{
					tempPath.add( edge.getSource() );
					lastNode = edge.getSource();
				}
			}
			allPaths.add( tempPath );
		}

		
		return allPaths;
	}
	
	public static DNVNode getMostFrequentIntermediateNode( DNVGraph graph, int level, DNVNode startNode, DNVNode endNode )
	{
		int numberOfPaths = 0;
		boolean done = false;
		DNVNode mostFrequentNode = null;
		DNVNode secondMostFrequentNode;
		while( !done )
		{
			numberOfPaths += 10;
//			System.out.println( numberOfPaths );
			List<DNVNode> allNodes = getNodesSortedByFrequencyOnShortestPath( graph, level, startNode, endNode, numberOfPaths );
			if( allNodes == null )
			{
				return null;
			}
			mostFrequentNode = null;
			secondMostFrequentNode = null;
			for( DNVNode node : allNodes )
			{
				if( node != startNode && node != endNode )
				{
					if( mostFrequentNode == null )
					{
						mostFrequentNode = node;
					}
					else
					{
						secondMostFrequentNode = node;
						break;
					}
				}
			}
			
			int count1 = Integer.parseInt( mostFrequentNode.getProperty( "nodeCount" ) );
			int count2 = Integer.parseInt( secondMostFrequentNode.getProperty( "nodeCount" ) );
//			System.out.println( "First candidate:" + mostFrequentNode.getLabel() );
//			System.out.println( "Second candidate:" + secondMostFrequentNode.getLabel() );
//			System.out.println( "diff:" + Math.abs( count1 - count2 ) );
//			System.out.println( "percentage:" + count1 / (double)numberOfPaths );
			if( (count1 / (double)numberOfPaths) > 0.7 /*|| Math.abs( count1 - count2 ) >= numberOfPaths * 0.1*/ || numberOfPaths >= 300 )
			{
				done = true;
			}

			for( DNVNode node : allNodes )
			{
				if( node == mostFrequentNode && done )
				{
					node.setProperty( "pathFrequency_" + startNode.getId() + "_" + endNode.getId(), node.getProperty( "nodeCount" ) );
				}
				node.removeProperty( "nodeCount" );
			}
		}
		
		
		return mostFrequentNode;
	}

	public static List<DNVNode> getNodesSortedByFrequencyOnShortestPath( DNVGraph graph, int level, DNVNode startNode, DNVNode endNode, int numberOfPaths )
	{
		List<List<DNVNode>> allPaths = GraphFunctions.getNodesOnKShortestPaths( graph, level, startNode, endNode, numberOfPaths );
		if( allPaths == null )
		{
			return null;
		}
//		System.out.println( "Finding paths from " + startNode.getLabel() + " to " + endNode.getLabel() );
		int count = 0;
		for( List<DNVNode> path : allPaths )
		{
//			System.out.println( "Start of path " + count++ + " length = " + path.size() );
			for( DNVNode node : path )
			{
//				System.out.println( "\t" + node.getLabel() );
				Integer nodeCount = 0;
				if( node.hasProperty( "nodeCount" ) )
				{
					nodeCount = Integer.parseInt( node.getProperty( "nodeCount" ) );
				}
				nodeCount++;
				node.setProperty( "nodeCount", "" + nodeCount );
			}
		}
		
		List<DNVNode> allNodes = graph.getNodes( level );
		SortByFloatProperty sbfp = new SortByFloatProperty( "nodeCount", true );
		Collections.sort( allNodes, sbfp );
		return allNodes;
	}
	
	public static boolean doesPathExist( DNVGraph graph, int level, DNVNode startNode, DNVNode endNode )
	{
		UndirectedGraph<DNVNode, JGraphTEdge<DNVNode>> g = JGraphTConverter.convertDNVToUndirectedJGraphT( graph, level );
		ConnectivityInspector<DNVNode, JGraphTEdge<DNVNode>> inspector = new ConnectivityInspector<DNVNode, JGraphTEdge<DNVNode>>( g );

		return inspector.pathExists( startNode, endNode );
	}

	public static void main( String args[] )
	{
		GraphsPathFilter.init();
		DNVGraph graph = new DNVGraph( Settings.GRAPHS_PATH + "UserStudy/testGraphs/graph1small.dnv" );
		DNVNode startNode = graph.getNodes().get( (int)(Math.random()*graph.getNodes().size()) );
		DNVNode endNode = graph.getNodes().get( (int)(Math.random()*graph.getNodes().size()) );
		DNVNode frequent = getMostFrequentIntermediateNode( graph, 0, startNode, endNode );
		
		System.out.println( "Most frequent node " + frequent.getLabel() + " with " + frequent.getProperty( "pathFrequency_" + startNode.getId() + "_" + endNode.getId() ) + " occurrances." );
		 
	}
}