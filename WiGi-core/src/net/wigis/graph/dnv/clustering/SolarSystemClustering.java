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
import net.wigis.settings.Settings;

// TODO: Auto-generated Javadoc
/**
 * The Class SolarSystemClustering.
 * 
 * @author Brynjar Gretarsson
 */
public class SolarSystemClustering
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
	// SolarSystemClustering.class );

	public static void cluster( DNVGraph graph, int currentLevel )
	{
		int newLevel = currentLevel + 1;

		Integer biggestId = graph.getBiggestId();
		if( Settings.DEBUG )
			System.out.println( "Biggest id in graph is " + biggestId + ". Setting next id as " + ( biggestId + 1 ) );
		// graph.initializeId( biggestId + 1 );

		List<DNVNode> nodes = new ArrayList<DNVNode>( graph.getNodes( currentLevel ) );
		List<DNVNode> parents = new ArrayList<DNVNode>();
		Map<Integer, Map<Integer, DNVNode>> parentToPlanetsOrMoons = new HashMap<Integer, Map<Integer, DNVNode>>();
		Map<Integer, DNVNode> subNodeToParent = new HashMap<Integer, DNVNode>();
		List<DNVNode> planets;
		List<DNVNode> moons;
		DNVNode sun;
		DNVNode planet;
		Map<Integer, DNVNode> tempMap;
		DNVNode parent;

		// Find the suns and their solar systems
		while( nodes.size() > 0 )
		{
			sun = nodes.remove( (int)( Math.random() * nodes.size() ) );
			parent = createParentNode( sun, newLevel, graph );
			tempMap = parentToPlanetsOrMoons.get( parent.getId() );
			if( tempMap == null )
			{
				tempMap = new HashMap<Integer, DNVNode>();
				parentToPlanetsOrMoons.put( parent.getId(), tempMap );
			}

			parents.add( parent );

			planets = sun.getNeighbors();
			addAllNodesToMap( planets, tempMap );
			setLinkToParent( planets, parent, subNodeToParent );
			nodes.removeAll( planets );
			for( int i = 0; i < planets.size(); i++ )
			{
				planet = planets.get( i );
				moons = planet.getNeighbors();
				addAllNodesToMap( moons, tempMap );
				setLinkToParent( moons, parent, subNodeToParent );
				nodes.removeAll( moons );
			}
		}

		// Assign parent nodes with sub nodes
		List<DNVNode> subNodes;
		for( int i = 0; i < parents.size(); i++ )
		{
			parent = parents.get( i );
			tempMap = parentToPlanetsOrMoons.get( parent.getId() );
			subNodes = new ArrayList<DNVNode>( tempMap.values() );
			parent.setSubNodes( subNodes );
		}

		// Create the edges
		Iterator<DNVNode> iterator;
		List<DNVNode> neighbors;
		DNVNode subNode;
		DNVNode neighbor;
		DNVNode secondParent;
		Map<Integer, Boolean> isNeighbor;
		DNVEdge newEdge;
		for( int i = 0; i < parents.size(); i++ )
		{
			parent = parents.get( i );
			isNeighbor = new HashMap<Integer, Boolean>();
			tempMap = parentToPlanetsOrMoons.get( parent.getId() );
			iterator = tempMap.values().iterator();
			while( iterator.hasNext() )
			{
				subNode = iterator.next();
				neighbors = subNode.getNeighbors();
				for( int j = 0; j < neighbors.size(); j++ )
				{
					neighbor = neighbors.get( j );
					secondParent = subNodeToParent.get( neighbor.getId() );
					if( !secondParent.equals( parent ) && isNeighbor.get( secondParent.getId() ) == null )
					{
						isNeighbor.put( secondParent.getId(), true );
						newEdge = new DNVEdge( newLevel, DNVEdge.DEFAULT_RESTING_DISTANCE, false, parent, secondParent, graph );
						graph.addNode( newLevel, newEdge );
					}
				}
			}
		}
	}

	/**
	 * Sets the link to parent.
	 * 
	 * @param planets
	 *            the planets
	 * @param parent
	 *            the parent
	 * @param subNodeToParent
	 *            the sub node to parent
	 */
	private static void setLinkToParent( List<DNVNode> planets, DNVNode parent, Map<Integer, DNVNode> subNodeToParent )
	{
		for( int i = 0; i < planets.size(); i++ )
		{
			subNodeToParent.put( planets.get( i ).getId(), parent );
		}
	}

	/**
	 * Adds the all nodes to map.
	 * 
	 * @param nodes
	 *            the nodes
	 * @param map
	 *            the map
	 */
	public static void addAllNodesToMap( List<DNVNode> nodes, Map<Integer, DNVNode> map )
	{
		DNVNode tempNode;
		for( int i = 0; i < nodes.size(); i++ )
		{
			tempNode = nodes.get( i );
			map.put( tempNode.getId(), tempNode );
		}
	}

	/**
	 * Creates the parent node.
	 * 
	 * @param child
	 *            the child
	 * @param newLevel
	 *            the new level
	 * @param graph
	 *            the graph
	 * @return the dNV node
	 */
	public static DNVNode createParentNode( DNVNode child, Integer newLevel, DNVGraph graph )
	{
		DNVNode parent = new DNVNode( new Vector2D( child.getPosition() ), graph );
		parent.setColor( child.getColor() );
		parent.setLevel( newLevel.intValue() );
		parent.setFirstChild( child );
		child.setParentNode( parent );
		graph.addNode( newLevel, parent );

		return parent;
	}
}
