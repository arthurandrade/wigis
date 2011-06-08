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
 * The Class DocumentTopicSpiralLayout.
 * 
 * @author Brynjar Gretarsson
 */
public class DocumentTopicSpiralLayout
{

	/**
	 * Run layout.
	 * 
	 * @param graph
	 *            the graph
	 * @param level
	 *            the level
	 * @param maxRadius
	 *            the max radius
	 */
	public static void runLayout( DNVGraph graph, int level, float maxRadius )
	{
		float width = maxRadius * 2;
		float radius = maxRadius;
		float minRadius = 0.2f * maxRadius;
		Vector2D center = new Vector2D( 0, 0 );
		Map<Integer, DNVEntity> documentNodes = graph.getNodesByType( level, "document" );
		int sizeBy = documentNodes.values().size() / 7;
		float subtraction = ( maxRadius - minRadius ) / documentNodes.values().size();
		int j;
		float index;
		for( int i = 1; i <= documentNodes.values().size(); i++ )
		{
			DNVNode documentNode = (DNVNode)graph.getNodeByBbId( "doc" + i );
			documentNode.setPosition( getPosition( i, 0, sizeBy, radius, center ) );
			documentNode.setDistanceFromCenterNode( radius );
			radius -= subtraction;
			j = 1;
			for( DNVNode neighbor : documentNode.getNeighbors() )
			{
				if( neighbor.getType().equals( "topic" ) && !neighbor.isPositioned() )
				{
					if( i > documentNodes.values().size() / 2 )
						index = radius + ( ( i - documentNodes.values().size() - j ) * subtraction );
					else
						index = radius + ( ( i + j ) * subtraction );
					neighbor.setPosition( getPosition( i, 0, sizeBy, index, center ) );
					// neighbor.setDistanceFromCenterNode( index );
					neighbor.setPositioned( true );
					j++;
				}
			}
		}

		/*
		 * This was supposed to highlight the most highly connected topics, but
		 * the labels are too long to display
		 * 
		 * Map<Integer,DNVNode> topicNodes = graph.getNodesByType( level,
		 * "topic" ); List<DNVNode> topicNodesList = new ArrayList<DNVNode>(
		 * topicNodes.values() ); NumberOfNeighborSort nons = new
		 * NumberOfNeighborSort(); Collections.sort( topicNodesList, nons ); int
		 * numberOfNeighbors; int previousNumberOfNeighbors = Integer.MAX_VALUE;
		 * int rank = 1; int counter = 0; for( DNVNode topicNode :
		 * topicNodesList ) { counter++; numberOfNeighbors =
		 * topicNode.getNeighbors().size(); if( numberOfNeighbors <
		 * previousNumberOfNeighbors ) { previousNumberOfNeighbors =
		 * numberOfNeighbors; rank = counter; } topicNode.setProperty( "rank",
		 * "" + rank ); if( rank <= 5 ) { topicNode.setForceLabel( true ); } }
		 */
		layoutLevel( width, width, graph, 0.01f, level, true );
	}

	/**
	 * Gets the position.
	 * 
	 * @param i
	 *            the i
	 * @param startAt
	 *            the start at
	 * @param size
	 *            the size
	 * @param bigRadius
	 *            the big radius
	 * @param center
	 *            the center
	 * @return the position
	 */
	private static Vector2D getPosition( double i, double startAt, int size, double bigRadius, Vector2D center )
	{
		float z_pos = (float)( 1.0 * i );

		double bigRadians = 2.0 * Math.PI / size * i;

		float y_pos = 0;

		z_pos = (float)( Math.cos( bigRadians ) * bigRadius );
		y_pos += Math.sin( bigRadians ) * bigRadius;

		return new Vector2D( center.getX() + z_pos, center.getY() + y_pos );
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
				forceToShape( circle, graph, v );
			}
		}
	}

	/**
	 * Force to shape.
	 * 
	 * @param circle
	 *            the circle
	 * @param graph
	 *            the graph
	 * @param v
	 *            the v
	 */
	private static void forceToShape( boolean circle, DNVGraph graph, DNVNode v )
	{
		if( circle )
			forceToCircle( graph, v );
		else
			forceToLine( graph, v );
	}

	/**
	 * Force to circle.
	 * 
	 * @param graph
	 *            the graph
	 * @param v
	 *            the v
	 */
	private static void forceToCircle( DNVGraph graph, DNVNode v )
	{
		float radius;
		String bbid = v.getBbId();
		if( bbid.startsWith( "doc" ) )
		{
			int id = Integer.parseInt( bbid.substring( 3 ) );
			int idBefore = id - 1;
			int id2Before = id - 2;
			DNVNode t = (DNVNode)graph.getNodeByBbId( "doc" + id2Before );
			DNVNode u = (DNVNode)graph.getNodeByBbId( "doc" + idBefore );
			if( u != null && t != null )
			{
				Vector2D tPosition = t.getPosition();
				Vector2D uPosition = u.getPosition();
				Vector2D vPosition = v.getPosition();

				float m2 = ( uPosition.getY() - tPosition.getY() ) / ( uPosition.getX() - tPosition.getX() );
				float m3 = ( vPosition.getY() - tPosition.getY() ) / ( vPosition.getX() - tPosition.getX() );

				float angle = (float)( Math.atan( ( m3 - m2 ) / ( 1 + m3 * m2 ) ) * 180 / Math.PI );

				// System.out.println( "nodes [" + id2Before + ", " + idBefore +
				// ", " + id + "]" );
				// System.out.println( "angle is " + angle + " degrees" );
				if( angle < 0 )
				{
					float xDiff = uPosition.getX() - tPosition.getX();
					float yDiff = uPosition.getY() - tPosition.getY();

					vPosition.set( uPosition.getX() + xDiff, uPosition.getY() + yDiff );
				}
			}
		}

		radius = v.getDistanceFromCenterNode();
		if( radius != Float.POSITIVE_INFINITY )
		{
			v.getPosition().normalize().dotProduct( radius );
		}
	}

	/**
	 * Force to line.
	 * 
	 * @param graph
	 *            the graph
	 * @param v
	 *            the v
	 */
	private static void forceToLine( DNVGraph graph, DNVNode v )
	{
		float radius;
		radius = v.getDistanceFromCenterNode();
		if( radius != Float.POSITIVE_INFINITY )
		{
			v.getPosition().setX( radius );
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
		float temperature = size;

		int counter = 0;
		while( temperature > 0 )
		{
			runIteration( width, height, graph, level, temperature, counter, circle );
			temperature = cool( temperature, coolingFactor );
			counter++;
		}

		for( DNVEntity v : graph.getNodesByType( level, "document" ).values() )
		{
			forceToShape( circle, graph, (DNVNode)v );
		}
	}
}
