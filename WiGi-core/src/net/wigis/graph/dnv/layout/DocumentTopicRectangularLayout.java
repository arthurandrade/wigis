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

import java.util.List;
import java.util.Map;

import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVEntity;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.utilities.GraphFunctions;
import net.wigis.graph.dnv.utilities.Vector2D;

// TODO: Auto-generated Javadoc
/**
 * The Class DocumentTopicRectangularLayout.
 * 
 * @author Brynjar Gretarsson
 */
public class DocumentTopicRectangularLayout
{

	/**
	 * Run layout.
	 * 
	 * @param graph
	 *            the graph
	 * @param level
	 *            the level
	 */
	public static void runLayout( DNVGraph graph, int level )
	{
		Map<Integer, DNVEntity> documentNodes = graph.getNodesByType( level, "document" );
		// int width = (int)Math.round( Math.sqrt( documentNodes.size() ) );
		int width = (int)Math.round( documentNodes.size() / 4.0 );

		for( int i = 0; i < documentNodes.values().size(); i++ )
		{
			DNVNode documentNode = (DNVNode)graph.getNodeByBbId( "doc" + ( i + 1 ) );
			documentNode.setPosition( getPosition( i, width ) );
		}

		Map<Integer, DNVEntity> topicNodes = graph.getNodesByType( level, "topic" );
		int topicWidth = (int)Math.round( topicNodes.size() / 4.0 );
		int i = 0;
		for( DNVEntity topicNode : topicNodes.values() )
		{
			( (DNVNode)topicNode ).setPosition( getPosition( i, topicWidth ) );
			i++;
		}

		layoutLevel( width, width, graph, 0.005f, level, true );
	}

	/**
	 * Gets the position.
	 * 
	 * @param i
	 *            the i
	 * @param width
	 *            the width
	 * @return the position
	 */
	private static Vector2D getPosition( float i, int width )
	{
		float mod_4 = (float)Math.floor( i / width ) % 4;
		float x_pos = 0;
		float y_pos = 0;
		float halfWidth = width / 2.0f;
		if( mod_4 == 0 )
		{
			y_pos = -halfWidth;
			x_pos = i - halfWidth;
		}
		else if( mod_4 == 1 )
		{
			y_pos = ( i % width ) - halfWidth;
			x_pos = halfWidth;
		}
		else if( mod_4 == 2 )
		{
			y_pos = halfWidth;
			x_pos = halfWidth - ( i % width );
		}
		else if( mod_4 == 3 )
		{
			y_pos = halfWidth - ( i % width );
			x_pos = -halfWidth;
		}
		/*
		 * float y_pos = (float)Math.floor( i / width ); float oddOrEven = y_pos
		 * % 2; float x_pos; if( oddOrEven == 0 ) { x_pos = i % width; } else {
		 * x_pos = width - (i % width); }
		 */
		// System.out.println( "y_pos : " + y_pos );
		// System.out.println( "x_pos : " + x_pos );
		// System.out.println( "mod_4 : " + mod_4 );

		return new Vector2D( x_pos, y_pos );
	}

	/**
	 * Run iteration.
	 * 
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @param graph
	 *            the graph
	 * @param level
	 *            the level
	 * @param temperature
	 *            the temperature
	 * @param counter
	 *            the counter
	 * @param circle
	 *            the circle
	 */
	public static void runIteration( float width, float height, DNVGraph graph, int level, float temperature, int counter, boolean circle )
	{
		float area = width * height;
		float k = (float)Math.sqrt( area / graph.getGraphSize( level ) );
		float kPower2 = k * k;

		// System.out.println( "Temperature : " + temperature + " Level : " +
		// level + " Number of Nodes: " + graph.getGraphSize( level ) );

		Vector2D difference = new Vector2D();
		float length;

		Grid grid = new Grid( k * 2, graph, level );
		List<DNVNode> potentialNodes;

		// float k2;
		// repulsive forces
		for( DNVNode v : graph.getNodes( level ) )
		{
			v.setForce( 0, 0 );
			potentialNodes = grid.getPotentialNodes( v );
			for( DNVNode u : potentialNodes )
			{
				if( u != v )
				{
					difference.set( v.getPosition() );
					difference.subtract( u.getPosition() );
					length = difference.length();
					if( length < k * 2 )
					{
						difference.normalize();
						difference.dotProduct( repel( length, kPower2 * u.getRadius() ) );
						v.getForce().add( difference );
					}
				}
			}
		}

		// attractive forces
		for( DNVEdge e : graph.getEdges( level ) )
		{
			difference.set( e.getFrom().getPosition() );
			difference.subtract( e.getTo().getPosition() );
			length = difference.length();
			difference.normalize();
			difference.dotProduct( attract( length, k ) );
			e.getFrom().getForce().subtract( difference );
			e.getTo().getForce().add( difference );
		}

		// apply the forces
		for( DNVNode v : graph.getNodes( level ) )
		{
			difference.set( v.getForce() );
			length = difference.length();
			difference.normalize();
			difference.dotProduct( Math.min( length, temperature ) );
			v.move( difference, true, false );
		}

		if( counter % 5 == 0 )
		{
			for( int i = 1; i <= graph.getNodesByType( level, "document" ).size(); i++ )
			{
				DNVNode v = (DNVNode)graph.getNodeByBbId( "doc" + i );
				forceToShape( graph, v );
			}
		}

	}

	/**
	 * Force to shape.
	 * 
	 * @param graph
	 *            the graph
	 * @param v
	 *            the v
	 */
	private static void forceToShape( DNVGraph graph, DNVNode v )
	{
		int width = (int)Math.round( graph.getNodesByType( 0, "document" ).size() / 4.0 );
		String bbid = v.getBbId();
		float buffer = width / 5.0f;
		if( bbid.startsWith( "doc" ) )
		{
			int id = Integer.parseInt( bbid.substring( 3 ) );
			// v.setPosition( getPosition( id-1, width ) );
			int idBefore = id - 1;
			float halfWidth = width / 2.0f;

			DNVNode nodeBefore = (DNVNode)graph.getNodeByBbId( "doc" + idBefore );
			Vector2D nodeBeforePosition;
			if( nodeBefore != null )
			{
				nodeBeforePosition = nodeBefore.getPosition();
			}
			else
			{
				nodeBeforePosition = new Vector2D( -halfWidth, -halfWidth );
			}

			Vector2D position = v.getPosition();
			float x_pos = position.getX();
			float y_pos = position.getY();
			float x_before = nodeBeforePosition.getX();
			float y_before = nodeBeforePosition.getY();
			float mod_4 = (float)Math.floor( id / width ) % 4;
			if( mod_4 == 0 )
			{
				if( x_pos < -halfWidth + ( id % width ) - buffer )
				{
					x_pos = -halfWidth + ( id % width ) - buffer;
				}
				else if( x_pos > -halfWidth + ( id % width ) + buffer )
				{
					x_pos = -halfWidth + ( id % width ) + buffer;
				}
				if( y_pos < -halfWidth - buffer )
				{
					y_pos = -halfWidth - buffer;
				}
				else if( y_pos > -halfWidth )
				{
					y_pos = -halfWidth;
				}
				if( x_pos <= x_before )
				{
					x_pos = x_before + 1;
				}
			}
			else if( mod_4 == 1 )
			{
				if( x_pos < halfWidth )
				{
					x_pos = halfWidth;
				}
				else if( x_pos > halfWidth + buffer )
				{
					x_pos = halfWidth + buffer;
				}
				if( y_pos < -halfWidth + ( id % width ) - buffer )
				{
					y_pos = -halfWidth + ( id % width ) - buffer;
				}
				else if( y_pos > -halfWidth + ( id % width ) + buffer )
				{
					y_pos = -halfWidth + ( id % width ) + buffer;
				}
				if( y_pos <= y_before )
				{
					y_pos = y_before + 1;
				}
			}
			else if( mod_4 == 2 )
			{
				if( x_pos < halfWidth - ( id % width ) - buffer )
				{
					x_pos = halfWidth - ( id % width ) - buffer;
				}
				else if( x_pos > halfWidth - ( id % width ) + buffer )
				{
					x_pos = halfWidth - ( id % width ) + buffer;
				}
				if( y_pos < halfWidth )
				{
					y_pos = halfWidth;
				}
				else if( y_pos > halfWidth + buffer )
				{
					y_pos = halfWidth + buffer;
				}
				if( x_pos >= x_before )
				{
					x_pos = x_before - 1;
				}
			}
			else if( mod_4 == 3 )
			{
				if( x_pos < -halfWidth - buffer )
				{
					x_pos = -halfWidth - buffer;
				}
				else if( x_pos > -halfWidth )
				{
					x_pos = -halfWidth;
				}
				if( y_pos < halfWidth - ( id % width ) - buffer )
				{
					y_pos = halfWidth - ( id % width ) - buffer;
				}
				else if( y_pos > halfWidth - ( id % width ) + buffer )
				{
					y_pos = halfWidth - ( id % width ) + buffer;
				}
				if( y_pos >= y_before )
				{
					y_pos = y_before - 1;
				}
			}
			/*
			 * System.out.println( "halfWidth : " + halfWidth );
			 * System.out.println( "x_pos : " + x_pos ); System.out.println(
			 * "y_pos : " + y_pos );
			 */
			position.set( x_pos, y_pos );
		}
	}

	/**
	 * Attract.
	 * 
	 * @param x
	 *            the x
	 * @param k
	 *            the k
	 * @return the float
	 */
	private static float attract( float x, float k )
	{
		return ( x * x ) / k;
	}

	/**
	 * Repel.
	 * 
	 * @param x
	 *            the x
	 * @param kPower2TimesRadiusU
	 *            the k power2 times radius u
	 * @return the float
	 */
	private static float repel( float x, float kPower2TimesRadiusU )
	{
		return kPower2TimesRadiusU / x;
	}

	/**
	 * Cool.
	 * 
	 * @param temperature
	 *            the temperature
	 * @param coolingFactor
	 *            the cooling factor
	 * @return the float
	 */
	private static float cool( float temperature, float coolingFactor )
	{
		if( temperature > 0.01 )
			return Math.max( 0, temperature * ( 1 - coolingFactor ) );

		return 0;
	}

	/**
	 * Layout level.
	 * 
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @param graph
	 *            the graph
	 * @param coolingFactor
	 *            the cooling factor
	 * @param level
	 *            the level
	 * @param circle
	 *            the circle
	 */
	private static void layoutLevel( float width, float height, DNVGraph graph, float coolingFactor, int level, boolean circle )
	{
		float size = Math.max( GraphFunctions.getGraphWidth( graph, level, false ), width );
		size = Math.max( size, Math.max( GraphFunctions.getGraphHeight( graph, level, false ), height ) );
		float temperature = size / 3;

		int counter = 0;
		while( temperature > 0 )
		{
			runIteration( width, height, graph, level, temperature, counter, circle );
			temperature = cool( temperature, coolingFactor );
			counter++;
		}

		for( DNVNode v : graph.getNodes( level ) )
		{
			forceToShape( graph, v );
		}
	}
}
