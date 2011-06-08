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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.utilities.Vector2D;

// TODO: Auto-generated Javadoc
/**
 * The Class Grid.
 * 
 * @author Brynjar Gretarsson
 */
public class Grid
{

	/** The box size. */
	private float boxSize;

	/** The boxes. */
	private Map<String, List<DNVNode>> boxes = new HashMap<String, List<DNVNode>>();

	/**
	 * Instantiates a new grid.
	 * 
	 * @param boxSize
	 *            the box size
	 * @param graph
	 *            the graph
	 * @param level
	 *            the level
	 */
	public Grid( float boxSize, DNVGraph graph, int level )
	{
		initialize( boxSize, graph.getNodes( level ) );
	}

	/**
	 * Instantiates a new grid.
	 * 
	 * @param boxSize
	 *            the box size
	 * @param nodes
	 *            the nodes
	 */
	public Grid( float boxSize, Collection<DNVNode> nodes )
	{
		initialize( boxSize, nodes );
	}

	/**
	 * Initialize.
	 * 
	 * @param boxSize
	 *            the box size
	 * @param nodes
	 *            the nodes
	 */
	private void initialize( float boxSize, Collection<DNVNode> nodes )
	{
		this.boxSize = boxSize;

		List<DNVNode> box;
		String key;
		for( DNVNode n : nodes )
		{
			key = getKey( n.getPosition() );
			box = boxes.get( key );
			if( box == null )
			{
				box = new ArrayList<DNVNode>();
				boxes.put( key, box );
			}

			box.add( n );
		}
	}

	/**
	 * Gets the key.
	 * 
	 * @param position
	 *            the position
	 * @return the key
	 */
	private String getKey( Vector2D position )
	{
		int x = getKeyX( position.getX() );
		int y = getKeyY( position.getY() );

		return x + "," + y;
	}

	/**
	 * Gets the key x.
	 * 
	 * @param x
	 *            the x
	 * @return the key x
	 */
	private int getKeyX( float x )
	{
		return (int)( x / boxSize );
	}

	/**
	 * Gets the key y.
	 * 
	 * @param y
	 *            the y
	 * @return the key y
	 */
	private int getKeyY( float y )
	{
		return (int)( y / boxSize );
	}

	/**
	 * Gets the box.
	 * 
	 * @param node
	 *            the node
	 * @return the box
	 */
	public List<DNVNode> getBox( DNVNode node )
	{
		List<DNVNode> box = boxes.get( getKey( node.getPosition() ) );
		if( box == null )
		{
			box = new ArrayList<DNVNode>();
		}

		return box;
	}

	/**
	 * Gets the potential nodes.
	 * 
	 * @param node
	 *            the node
	 * @return the potential nodes
	 */
	public List<DNVNode> getPotentialNodes( DNVNode node )
	{
		List<DNVNode> nodes = new ArrayList<DNVNode>();
		List<DNVNode> tempNodes;

		int x = getKeyX( node.getPosition().getX() );
		int y = getKeyY( node.getPosition().getY() );

		for( int i = x - 1; i <= x + 1; i++ )
		{
			for( int j = y - 1; j <= y + 1; j++ )
			{
				tempNodes = boxes.get( i + "," + j );
				if( tempNodes != null )
				{
					nodes.addAll( tempNodes );
				}
			}
		}

		return nodes;
	}
	
	public void printInfo()
	{
		System.out.println( "Grid has " + boxes.size() + " boxes." );
		for( String key : boxes.keySet() )
		{
			List<DNVNode> nodes = boxes.get( key );
			System.out.println( "Box " + key + " has " + nodes.size() + " nodes." );
		}
	}
}
