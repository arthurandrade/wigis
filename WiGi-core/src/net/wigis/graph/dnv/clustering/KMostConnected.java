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

package net.wigis.graph.dnv.clustering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.utilities.NumberOfNeighborSort;
import net.wigis.graph.dnv.utilities.Vector2D;
import net.wigis.graph.dnv.utilities.Vector3D;
import net.wigis.settings.Settings;

// TODO: Auto-generated Javadoc
/**
 * The Class KMostConnected.
 * 
 * @author Brynjar Gretarsson
 */
public class KMostConnected
{

	/** The sorter. */
	private static NumberOfNeighborSort sorter = new NumberOfNeighborSort();

	/** The multicolor. */
	private static boolean multicolor = false;

	/**
	 * Logger.
	 * 
	 * @param graph
	 *            the graph
	 * @param numberOfClusters
	 *            the number of clusters
	 * @param currentLevel
	 *            the current level
	 * @param sort
	 *            the sort
	 */
	// // private static Log logger = LogFactory.getLog( KMostConnected.class );

	public static void cluster( DNVGraph graph, int numberOfClusters, Integer currentLevel, boolean sort )
	{
		List<DNVNode> currentNodes = graph.getNodes( currentLevel );
		cluster( currentNodes, graph, numberOfClusters, currentLevel, sort );
	}

	/**
	 * Creates the parent node.
	 * 
	 * @param tempNode
	 *            the temp node
	 * @param newLevel
	 *            the new level
	 * @param graph
	 *            the graph
	 * @param nodeDistance
	 *            the node distance
	 * @param nodeToParent
	 *            the node to parent
	 * @param parentToNodes
	 *            the parent to nodes
	 * @return the dNV node
	 */
	public static DNVNode createParentNode( DNVNode tempNode, Integer newLevel, DNVGraph graph, Map<Integer, Integer> nodeDistance,
			Map<Integer, DNVNode> nodeToParent, Map<Integer, List<DNVNode>> parentToNodes )
	{
		DNVNode newNode = new DNVNode( new Vector2D( tempNode.getPosition() ), graph );
		newNode.setColor( tempNode.getColor() );
		newNode.setLevel( newLevel.intValue() );
		newNode.setFirstChild( tempNode );
		graph.addNode( newLevel, newNode );
		if( multicolor && newLevel == 1 )
		{
			Vector3D color = new Vector3D( (float)Math.max( 0.3, Math.random() ), (float)Math.max( 0.3, Math.random() ), (float)Math.max( 0.3, Math
					.random() ) );
			newNode.setColor( color );
		}
		else
		{
			Vector3D color = new Vector3D( tempNode.getColor() );
			newNode.setColor( color );
		}

		nodeDistance.put( tempNode.getId(), Integer.valueOf( 0 ) );
		nodeToParent.put( tempNode.getId(), newNode );
		List<DNVNode> tempList = new ArrayList<DNVNode>();
		tempList.add( tempNode );
		parentToNodes.put( newNode.getId(), tempList );
		return newNode;
	}

	/**
	 * Cluster.
	 * 
	 * @param currentNodes
	 *            the current nodes
	 * @param graph
	 *            the graph
	 * @param numberOfClusters
	 *            the number of clusters
	 * @param currentLevel
	 *            the current level
	 * @param sort
	 *            the sort
	 */
	public static void cluster( List<DNVNode> currentNodes, DNVGraph graph, int numberOfClusters, Integer currentLevel, boolean sort )
	{
		Integer newLevel = currentLevel + 1;

		Integer biggestId = graph.getBiggestId();
		if( Settings.DEBUG )
			System.out.println( "Biggest id in graph is " + biggestId + ". Setting next id as " + ( biggestId + 1 ) );

		// graph.initializeId( biggestId + 1 );

		List<DNVNode> tempNodeList = new ArrayList<DNVNode>();

		if( sort )
			Collections.sort( currentNodes, sorter );

		DNVNode tempNode;
		DNVNode tempNode2;
		DNVNode newNode;
		DNVEdge tempEdge;

		System.out.println( "First node neighbors : " + currentNodes.get( 0 ).getNeighbors().size() );
		System.out.println( "Last node neighbors : " + currentNodes.get( currentNodes.size() - 1 ).getNeighbors().size() );

		Map<Integer, Integer> nodeDistance = new HashMap<Integer, Integer>();
		Map<Integer, DNVNode> nodeToParent = new HashMap<Integer, DNVNode>();
		Map<Integer, List<DNVNode>> parentToNodes = new HashMap<Integer, List<DNVNode>>();

		List<DNVNode> tempList;

		// Assign all nodes with a parent node
		for( int i = 0; i < numberOfClusters; i++ )
		{
			tempNode = currentNodes.get( i );
			newNode = createParentNode( tempNode, newLevel, graph, nodeDistance, nodeToParent, parentToNodes );
			if( Settings.DEBUG )
			{
				System.out.println( "Parent node has id " + newNode.getId() + " subnode has id " + tempNode.getId() );
				System.out.println( "Subnode[" + tempNode.getId() + "] has " + tempNode.getNeighbors().size() + " neighbors" );
			}
			tempNodeList = tempNode.getNeighbors();
			handleNeighbors( 1, tempNodeList, newNode, nodeDistance, nodeToParent );
		}

		// Handle nodes that are not connected to any other nodes
		tempList = graph.getNodes( currentLevel );
		for( int i = 0; i < tempList.size(); i++ )
		{
			tempNode = tempList.get( i );
			if( tempNode.getNeighbors().size() == 0 )
			{
				newNode = createParentNode( tempNode, newLevel, graph, nodeDistance, nodeToParent, parentToNodes );
				if( Settings.DEBUG )
				{
					System.out.println( "Node with id " + tempNode.getId() + " at level " + currentLevel + " has no neighbors. Create parent node." );
					System.out.println( "Parent node has id " + newNode.getId() );
					System.out.println( "" );
				}
			}
		}

		if( Settings.DEBUG )
		{
			System.out.println( "Number of nodes that have a parent node: " + nodeToParent.keySet().size() );
			System.out.println( "Number of nodes at current level: " + currentNodes.size() );

			for( int i = 0; i < currentNodes.size(); i++ )
			{
				if( nodeToParent.get( currentNodes.get( i ).getId() ) == null )
				{
					System.out.println( "node " + currentNodes.get( i ).getId() + " has no parent." );
				}
			}
		}

		// Set connections from parent to sub nodes
		Iterator<Integer> ids = nodeToParent.keySet().iterator();
		Integer tempId;
		while( ids.hasNext() )
		{
			tempId = ids.next();
			tempNode = nodeToParent.get( tempId );
			tempList = parentToNodes.get( tempNode.getId() );
			tempNode2 = (DNVNode)graph.getNodeById( tempId );
			if( tempNode2 != null && !tempList.contains( tempNode2 ) )
			{
				tempList.add( tempNode2 );
				parentToNodes.put( tempNode.getId(), tempList );
				tempNode.setLabel( "" + tempList.size() );
			}

			if( tempNode2 == null )
			{
				System.out.println( "Trying to add a link from a non-existing node" );
			}
		}

		// Create the edges
		List<DNVEdge> edgeList;
		ids = parentToNodes.keySet().iterator();
		DNVNode tempParent;
		DNVNode tempParent2;
		while( ids.hasNext() )
		{
			tempId = ids.next();
			tempParent2 = (DNVNode)graph.getNodeById( tempId );
			tempList = parentToNodes.get( tempId );
			tempParent2.setSubNodes( tempList );
			for( int i = 0; i < tempList.size(); i++ )
			{
				tempNode = tempList.get( i );
				edgeList = tempNode.getFromEdges();
				for( int j = 0; j < edgeList.size(); j++ )
				{
					tempEdge = edgeList.get( j );
					tempNode2 = tempEdge.getTo();
					tempParent = nodeToParent.get( tempNode2.getId() );
					if( tempParent != null && !tempParent.getId().equals( tempId ) && tempParent != tempParent2 )
					{
						if( !tempParent.getNeighbors().contains( tempParent2 ) )
						{
							tempEdge = new DNVEdge( newLevel.intValue(), DNVEdge.DEFAULT_RESTING_DISTANCE, false, tempParent2, tempParent, graph );
							tempParent2.addFromEdge( tempEdge );
							tempParent.addToEdge( tempEdge );
							graph.addNode( newLevel, tempEdge );
						}
					}
				}

				edgeList = tempNode.getToEdges();
				for( int j = 0; j < edgeList.size(); j++ )
				{
					tempEdge = edgeList.get( j );
					tempNode2 = tempEdge.getFrom();
					tempParent = nodeToParent.get( tempNode2.getId() );

					if( tempParent != null && !tempParent.getId().equals( tempId ) && tempParent != tempParent2 )
					{
						tempParent2 = (DNVNode)graph.getNodeById( tempId );
						if( !tempParent.getNeighbors().contains( tempParent2 ) )
						{
							tempEdge = new DNVEdge( newLevel.intValue(), DNVEdge.DEFAULT_RESTING_DISTANCE, false, tempParent, tempParent2, graph );
							tempParent2.addToEdge( tempEdge );
							tempParent.addFromEdge( tempEdge );
							graph.addNode( newLevel, tempEdge );
						}
					}
				}
			}
		}

		// float constant = 0.1f;
		for( int i = 0; i < currentNodes.size(); i++ )
		{
			tempNode = currentNodes.get( i );
			edgeList = tempNode.getFromEdges();
			for( int j = 0; j < edgeList.size(); j++ )
			{
				tempEdge = edgeList.get( j );
				tempNode2 = tempEdge.getTo();
				// tempEdge.setRestingDistance( DNVEdge.DEFAULT_RESTING_DISTANCE
				// + constant * ( tempNode.getTotalNumberOfSubNodes() +
				// tempNode2.getTotalNumberOfSubNodes() ) );
			}
			edgeList = tempNode.getToEdges();
			for( int j = 0; j < edgeList.size(); j++ )
			{
				tempEdge = edgeList.get( j );
				tempNode2 = tempEdge.getFrom();
				// tempEdge.setRestingDistance( DNVEdge.DEFAULT_RESTING_DISTANCE
				// + constant * ( tempNode.getTotalNumberOfSubNodes() +
				// tempNode2.getTotalNumberOfSubNodes() ) );
			}
		}

	}

	/**
	 * Handle neighbors.
	 * 
	 * @param distance
	 *            the distance
	 * @param neighbors
	 *            the neighbors
	 * @param parentNode
	 *            the parent node
	 * @param nodeDistance
	 *            the node distance
	 * @param nodeToParent
	 *            the node to parent
	 */
	private static void handleNeighbors( int distance, List<DNVNode> neighbors, DNVNode parentNode, Map<Integer, Integer> nodeDistance,
			Map<Integer, DNVNode> nodeToParent )
	{
		DNVNode tempNode;
		Integer tempDistance;
		for( int i = 0; i < neighbors.size(); i++ )
		{
			tempNode = neighbors.get( i );
			if( multicolor && parentNode.getLevel() == 1 )
				tempNode.setColor( parentNode.getColor() );
			tempDistance = nodeDistance.get( tempNode.getId() );
			if( tempDistance == null || tempDistance > distance )
			{
				nodeDistance.put( tempNode.getId(), Integer.valueOf( distance ) );
				nodeToParent.put( tempNode.getId(), parentNode );
				handleNeighbors( distance + 1, tempNode.getNeighbors(), parentNode, nodeDistance, nodeToParent );
			}
			else
			{
				// System.out.println( "Stopping expansion at node " +
				// tempNode.getId() + " distance " + tempDistance +
				// " for parent node " + parentNode.getId() );
			}
		}
	}

	// private static void initializeRandomPositions( Vector2D positions[],
	// float max_x, float max_y, float max_z,
	// float min_x, float min_y, float min_z )
	// {
	// for( int i = 0; i < positions.length; i++ )
	// {
	// if( positions[i] == null )
	// positions[i] = new Vector2D();
	// positions[i].setX( min_x + max_x * (float)Math.random() );
	// positions[i].setY( min_y + max_y * (float)Math.random() );
	// }
	// }
}
