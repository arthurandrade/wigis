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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.utilities.Vector2D;

// TODO: Auto-generated Javadoc
/**
 * The Class FacebookLayout.
 * 
 * @author Brynjar Gretarsson
 */
public class FacebookLayout
{

	/**
	 * Logger.
	 * 
	 * @param graph
	 *            the graph
	 * @param centralNode
	 *            the central node
	 * @param level
	 *            the level
	 * @param centerX
	 *            the center x
	 * @param centerY
	 *            the center y
	 */
	// // private static Log logger = LogFactory.getLog( FacebookLayout.class );

	public static void runLayout( DNVGraph graph, DNVNode centralNode, int level, float centerX, float centerY )
	{
		Map<Integer, List<DNVNode>> distanceToNodes = new HashMap<Integer, List<DNVNode>>();
		Map<Integer, Boolean> visitedNodes = new HashMap<Integer, Boolean>();
		List<DNVNode> nodes = new ArrayList<DNVNode>();
		visitedNodes.put( centralNode.getId(), true );
		nodes.add( centralNode );
		int distance = 0;

		Iterator<DNVNode> i;
		DNVNode tempNode;
		List<DNVNode> neighbors;

		while( nodes.size() > 0 )
		{
			distanceToNodes.put( distance, nodes );
			i = nodes.iterator();

			nodes = new ArrayList<DNVNode>();

			while( i.hasNext() )
			{
				tempNode = i.next();
				neighbors = tempNode.getNeighbors();
				for( int j = 0; j < neighbors.size(); j++ )
				{
					if( !visitedNodes.containsKey( neighbors.get( j ).getId() ) )
					{
						visitedNodes.put( neighbors.get( j ).getId(), true );
						nodes.add( neighbors.get( j ) );
						System.out.println( "Adding node '" + neighbors.get( j ).getLabel() + "' at distance " + ( distance + 1 ) );
					}
				}
			}

			distance++;
		}

		centralNode.setPosition( centerX, centerY );

		Vector2D position;
		for( int j = 1; j < distanceToNodes.size(); j++ )
		{
			nodes = distanceToNodes.get( j );
			if( nodes != null )
			{
				System.out.println( "processing " + nodes.size() + " nodes at distance " + j );
				for( int k = 0; k < nodes.size(); k++ )
				{
					tempNode = nodes.get( k );
					position = getPosition( centralNode.getPosition(), DNVEdge.DEFAULT_RESTING_DISTANCE * j, k, nodes.size() );
					tempNode.setPosition( position );
				}
			}
		}
	}

	/**
	 * Gets the position.
	 * 
	 * @param center
	 *            the center
	 * @param radius
	 *            the radius
	 * @param index
	 *            the index
	 * @param totalNumberOfNodes
	 *            the total number of nodes
	 * @return the position
	 */
	private static Vector2D getPosition( Vector2D center, float radius, int index, int totalNumberOfNodes )
	{
		Vector2D position = new Vector2D( center );

		float angle = 2.0f * (float)Math.PI / totalNumberOfNodes * index;

		float x = position.getX() + (float)Math.cos( angle ) * radius;
		float y = position.getY() + (float)Math.sin( angle ) * radius;

		position.setX( x );
		position.setY( y );

		return position;
	}
}
