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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.utilities.Vector2D;
import net.wigis.graph.dnv.utilities.Vector3D;

// TODO: Auto-generated Javadoc
/**
 * The Class ConnectedClustering.
 * 
 * @author Brynjar Gretarsson
 */
public class ConnectedClustering
{

	/**
	 * Logger.
	 * 
	 * @param graph
	 *            the graph
	 * @param currentLevel
	 *            the current level
	 */
	// // private static Log logger = LogFactory.getLog(
	// ConnectedClustering.class );

	public static void cluster( DNVGraph graph, Integer currentLevel )
	{
		Integer newLevel = currentLevel + 1;
		// Integer biggestId = graph.getBiggestId();

		// graph.initializeId( biggestId + 1 );

		Map<Integer, DNVNode> nodeToParent = new HashMap<Integer, DNVNode>();
		Map<Integer, List<DNVNode>> parentToNodes = new HashMap<Integer, List<DNVNode>>();

		Map<Integer, DNVNode> allNodes = new HashMap<Integer, DNVNode>( graph.getNodesMap( currentLevel ) );
		DNVNode tempNode;
		DNVNode parentNode;
		while( allNodes.size() > 0 )
		{
			tempNode = allNodes.values().iterator().next();

			allNodes.remove( tempNode.getId() );
			parentNode = createParentNode( tempNode, newLevel, graph, nodeToParent, parentToNodes );

			handleNeighbors( tempNode.getNeighbors(), parentNode, allNodes, nodeToParent );
		}

		// Set connections from parent to sub nodes
		Iterator<Integer> ids = nodeToParent.keySet().iterator();
		Integer tempId;
		List<DNVNode> tempList;
		DNVNode tempNode2;
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

		// Create the edges and set the connections from parent to subnodes
		List<DNVEdge> edgeList;
		ids = parentToNodes.keySet().iterator();
		DNVNode tempParent;
		DNVNode tempParent2;
		DNVEdge tempEdge;
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
		/*
		 * float constant = 0.1f; List<DNVNode> currentNodes = graph.getNodes(
		 * currentLevel ); for( int i = 0; i < currentNodes.size(); i++ ) {
		 * tempNode = currentNodes.get( i ); edgeList = tempNode.getFromEdges();
		 * for( int j = 0; j < edgeList.size(); j++ ) { tempEdge = edgeList.get(
		 * j ); tempNode2 = tempEdge.getTo(); // tempEdge.setRestingDistance(
		 * DNVEdge.DEFAULT_RESTING_DISTANCE + constant * (
		 * tempNode.getTotalNumberOfSubNodes() +
		 * tempNode2.getTotalNumberOfSubNodes() ) ); } edgeList =
		 * tempNode.getToEdges(); for( int j = 0; j < edgeList.size(); j++ ) {
		 * tempEdge = edgeList.get( j ); tempNode2 = tempEdge.getFrom(); //
		 * tempEdge.setRestingDistance( DNVEdge.DEFAULT_RESTING_DISTANCE +
		 * constant * ( tempNode.getTotalNumberOfSubNodes() +
		 * tempNode2.getTotalNumberOfSubNodes() ) ); } }
		 */
	}

	/**
	 * Handle neighbors.
	 * 
	 * @param neighbors
	 *            the neighbors
	 * @param parentNode
	 *            the parent node
	 * @param remainingNodes
	 *            the remaining nodes
	 * @param nodeToParent
	 *            the node to parent
	 */
	private static void handleNeighbors( List<DNVNode> neighbors, DNVNode parentNode, Map<Integer, DNVNode> remainingNodes,
			Map<Integer, DNVNode> nodeToParent )
	{
		DNVNode tempNode;
		for( int i = 0; i < neighbors.size(); i++ )
		{
			tempNode = neighbors.get( i );
			if( remainingNodes.containsKey( tempNode.getId() ) )
			{
				remainingNodes.remove( tempNode.getId() );
				nodeToParent.put( tempNode.getId(), parentNode );
				handleNeighbors( tempNode.getNeighbors(), parentNode, remainingNodes, nodeToParent );
			}
		}
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
	 * @param nodeToParent
	 *            the node to parent
	 * @param parentToNodes
	 *            the parent to nodes
	 * @return the dNV node
	 */
	public static DNVNode createParentNode( DNVNode tempNode, Integer newLevel, DNVGraph graph, Map<Integer, DNVNode> nodeToParent,
			Map<Integer, List<DNVNode>> parentToNodes )
	{
		DNVNode newNode = new DNVNode( new Vector2D( tempNode.getPosition() ), graph );
		newNode.setColor( tempNode.getColor() );
		newNode.setLevel( newLevel.intValue() );
		newNode.setFirstChild( tempNode );
		graph.addNode( newLevel, newNode );
		Vector3D color = new Vector3D( tempNode.getColor() );
		newNode.setColor( color );

		nodeToParent.put( tempNode.getId(), newNode );
		List<DNVNode> tempList = new ArrayList<DNVNode>();
		tempList.add( tempNode );
		parentToNodes.put( newNode.getId(), tempList );

		return newNode;
	}
}
