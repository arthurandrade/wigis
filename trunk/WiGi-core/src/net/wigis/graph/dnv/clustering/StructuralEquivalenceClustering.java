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

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.wigis.graph.GraphsPathFilter;
import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.utilities.Vector2D;
import net.wigis.graph.dnv.utilities.Vector3D;
import net.wigis.settings.Settings;

// TODO: Auto-generated Javadoc
/**
 * The Class StructuralEquivalenceClustering.
 * 
 * @author Brynjar Gretarsson
 */
public class StructuralEquivalenceClustering
{

	/**
	 * Cluster.
	 * 
	 * @param graph
	 *            the graph
	 * @param currentLevel
	 *            the current level
	 */
	public static void cluster( DNVGraph graph, Integer currentLevel )
	{
		List<DNVNode> currentNodes = graph.getNodes( currentLevel );
		List<DNVNode> tempList;
		Map<String, List<DNVNode>> nodesByNeighborlist = new HashMap<String, List<DNVNode>>();
		List<DNVNode> neighbors;
		String id;
		for( DNVNode node : currentNodes )
		{
			neighbors = node.getNeighbors();
			Collections.sort( neighbors );
			id = "";
			for( DNVNode neighbor : neighbors )
			{
				id += neighbor.getId() + "-";
			}

			tempList = nodesByNeighborlist.get( id );
			if( tempList == null )
			{
				tempList = new LinkedList<DNVNode>();
				nodesByNeighborlist.put( id, tempList );
			}
			if( !tempList.contains( node ) )
			{
				tempList.add( node );
			}
		}

		Map<Integer, DNVNode> nodeToParent = new HashMap<Integer, DNVNode>();
		Map<Integer, List<DNVNode>> parentToNode = new HashMap<Integer, List<DNVNode>>();
		DNVNode newNode;
		Vector2D position;
		Vector3D color;
		for( String neighborListId : nodesByNeighborlist.keySet() )
		{
			tempList = nodesByNeighborlist.get( neighborListId );
			position = new Vector2D( 0, 0 );
			color = new Vector3D( 0, 0, 0 );
			if( tempList != null && tempList.size() > 0 )
			{
				newNode = new DNVNode( graph );
				newNode.setFirstChild( tempList.get( 0 ) );
				newNode.setSubNodes( tempList );
				graph.addNode( currentLevel + 1, newNode );
				parentToNode.put( newNode.getId(), tempList );
				for( DNVNode childNode : tempList )
				{
					nodeToParent.put( childNode.getId(), newNode );
					position.add( childNode.getPosition() );
					color.add( childNode.getColor() );
					childNode.setParentNode( newNode );
				}
				position.dotProduct( 1.0f / (float)tempList.size() );
				color.dotProduct( 1.0f / (float)tempList.size() );
				if( tempList.size() == 1 )
				{
					newNode.setLabel( tempList.get( 0 ).getLabel() );
					newNode.setIcon( tempList.get( 0 ).getIcon() );
					newNode.setLabelColor( tempList.get( 0 ).getLabelColor() );
					newNode.setLabelOutlineColor( tempList.get( 0 ).getLabelOutlineColor() );
					newNode.setForceLabel( tempList.get( 0 ).isForceLabel() );
				}
				else
				{
					newNode.setLabel( "" + tempList.size() );
				}
				newNode.setColor( color );
				newNode.setPosition( position );
			}
		}

		for( DNVNode tempNode : currentNodes )
		{
			DNVNode parentNode = tempNode.getParentNode();
			DNVNode parentNode2;
			// DNVEdge newEdge;
			DNVNode tempNode2;
			List<DNVEdge> edgeList = tempNode.getFromEdges();
			for( DNVEdge tempEdge : edgeList )
			{
				tempNode2 = tempEdge.getTo();
				parentNode2 = tempNode2.getParentNode();
				if( parentNode2 != null && !parentNode2.getId().equals( parentNode.getId() ) && parentNode != parentNode2 )
				{
					if( !parentNode.isNeighbor( parentNode2 ) )
					{
						createEdge( graph, currentLevel, parentNode, parentNode2, tempEdge );
					}
				}
			}

			edgeList = tempNode.getToEdges();
			for( DNVEdge tempEdge : edgeList )
			{
				tempNode2 = tempEdge.getFrom();
				parentNode2 = tempNode2.getParentNode();
				if( parentNode2 != null && !parentNode2.getId().equals( parentNode.getId() ) && parentNode != parentNode2 )
				{
					if( !parentNode.isNeighbor( parentNode2 ) )
					{
						createEdge( graph, currentLevel, parentNode2, parentNode, tempEdge );
					}
				}
			}
		}
	}

	/**
	 * Creates the edge.
	 * 
	 * @param graph
	 *            the graph
	 * @param currentLevel
	 *            the current level
	 * @param fromNode
	 *            the from node
	 * @param toNode
	 *            the to node
	 * @param tempEdge
	 *            the temp edge
	 */
	private static void createEdge( DNVGraph graph, Integer currentLevel, DNVNode fromNode, DNVNode toNode, DNVEdge tempEdge )
	{
		DNVEdge newEdge;
		newEdge = new DNVEdge( currentLevel + 1, DNVEdge.DEFAULT_RESTING_DISTANCE, false, fromNode, toNode, graph );
		newEdge.setColor( tempEdge.getColor() );
		newEdge.setDirectional( tempEdge.isDirectional() );
		newEdge.setThickness( tempEdge.getThickness() );
		newEdge.setLabel( tempEdge.getLabel() );
		newEdge.setLabelColor( tempEdge.getLabelColor() );
		newEdge.setLabelOutlineColor( tempEdge.getLabelOutlineColor() );
		newEdge.setForceLabel( tempEdge.isForceLabel() );
		toNode.addToEdge( newEdge );
		fromNode.addFromEdge( newEdge );
		graph.addNode( currentLevel + 1, newEdge );
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
		DNVGraph graph = new DNVGraph( Settings.GRAPHS_PATH + Settings.DEFAULT_GRAPH );
		System.out.println( graph.getNodes( 0 ).size() );
		cluster( graph, 0 );
		System.out.println( graph.getNodes( 0 ).size() );
		System.out.println( graph.getNodes( 1 ).size() );
		graph.writeGraph( Settings.GRAPHS_PATH + Settings.DEFAULT_GRAPH + "_clustered.dnv" );
	}
}
