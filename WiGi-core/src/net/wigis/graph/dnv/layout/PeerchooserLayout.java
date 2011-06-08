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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVEntity;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.utilities.GraphFunctions;
import net.wigis.graph.dnv.utilities.NumberOfNeighborSort;
import net.wigis.graph.dnv.utilities.Vector2D;

// TODO: Auto-generated Javadoc
/**
 * The Class PeerchooserLayout.
 * 
 * @author Brynjar Gretarsson
 */
public class PeerchooserLayout
{

	/**
	 * Run layout.
	 * 
	 * @param graph
	 *            the graph
	 * @param level
	 *            the level
	 * @param centerX
	 *            the center x
	 * @param centerY
	 *            the center y
	 */
	public static void runLayout( DNVGraph graph, int level, float centerX, float centerY )
	{
		initializePositions( level, graph, centerX, centerY );
		// RandomLayout.runLayout( graph, level, 1 );
		Map<Integer, DNVEntity> activeUsers = graph.getNodesByType( level, "Active_user" );
		DNVNode activeUser = (DNVNode)activeUsers.values().iterator().next();
		activeUser.setPosition( centerX, centerY );
		activeUser.setFixed( true );
		runLayout( graph, activeUser, level, centerX, centerY, true );
		// Springs.runlayout( graph, level, 5, false, false );
	}

	/**
	 * Collection as list.
	 * 
	 * @param c
	 *            the c
	 * @return the list
	 */
	// private static List<DNVNode> collectionAsList( Collection<DNVNode> c )
	// {
	// if( c instanceof List<?> )
	// {
	// return (List<DNVNode>)c;
	// }
	//
	// return new ArrayList<DNVNode>( c );
	// }

	/**
	 * Initialize positions.
	 * 
	 * @param level
	 *            the level
	 * @param graph
	 *            the graph
	 * @param centerX
	 *            the center x
	 * @param centerY
	 *            the center y
	 */
	private static void initializePositions( Integer level, DNVGraph graph, float centerX, float centerY )
	{
		int i = 0;
		// int size = ((BBNodeIterator)nodeIterator).getSize();
		// int graphSize = GraphFunctions.getBBGraphSize( graph );
		double smallRadius = DNVEdge.DEFAULT_RESTING_DISTANCE;
		double bigRadius = smallRadius * 1.5;
		// Vector3D position;
		Vector2D zero = new Vector2D( centerX, centerY );

		DNVNode tempDNV;

		DNVNode activeUser = GraphFunctions.getNodeByType( "Active_user", graph, level );

		if( activeUser != null )
		{
			activeUser.setPosition( centerX, centerY );
			activeUser.setFixed( true );

			Map<Integer, DNVEntity> genreNodes = graph.getNodesByType( level, "genre" );
			if( genreNodes.size() > 0 )
			{
				List<DNVNode> nodeList = new ArrayList<DNVNode>();
				for( DNVEntity entity : genreNodes.values() )
				{
					nodeList.add( (DNVNode)entity );
				}
				NumberOfNeighborSort nons = new NumberOfNeighborSort();
				Collections.sort( nodeList, nons );
				Iterator<DNVNode> nodeIterator = nodeList.iterator();
				// List<DNVNode> neighbors;

				while( nodeIterator.hasNext() )
				{
					tempDNV = nodeIterator.next();
					tempDNV.setFixed( true );
					// neighbors = tempDNV.getNeighbors();

					if( tempDNV.getPosition().equals( Vector2D.ZERO ) )
					{
						placeNode( tempDNV, i++, genreNodes.size(), bigRadius, zero );
					}

					/*
					 * for( int j = 0; j < neighbors.size(); j++ ) { tempDNV =
					 * neighbors.get( j ); placeNode( tempDNV, (double)i +
					 * ((double)j) / neighbors.size(), genreNodes.size(),
					 * smallRadius, zero ); }
					 */
				}

				Map<Integer, DNVEntity> userNodes = graph.getNodesByType( level, "user" );
				if( userNodes.size() > 0 )
				{
					Iterator<DNVEntity> entityIterator = userNodes.values().iterator();
					i = 0;
					while( entityIterator.hasNext() )
					{
						tempDNV = (DNVNode)entityIterator.next();
						placeNode( tempDNV, i++, userNodes.size(), smallRadius, zero );
					}
				}
			}
		}
	}

	/**
	 * Place node.
	 * 
	 * @param node
	 *            the node
	 * @param i
	 *            the i
	 * @param size
	 *            the size
	 * @param bigRadius
	 *            the big radius
	 * @param center
	 *            the center
	 */
	private static void placeNode( DNVNode node, double i, int size, double bigRadius, Vector2D center )
	{
		double radians = 0.2 * i;
		// double radius = size / 10.0;
		float z_pos = (float)( 1.0 * i );

		double bigRadians = 2.0 * Math.PI / size * i;

		float y_pos = (float)Math.sin( radians );// * bigRadius;
		// float x_pos = (float)Math.cos( radians );// * bigRadius;

		z_pos = (float)( Math.cos( bigRadians ) * bigRadius );
		y_pos += Math.sin( bigRadians ) * bigRadius;

		if( node.getType().equals( "Active_user" ) )
		{
			node.setPosition( center.getX(), center.getY() );
			node.setFixed( true );
		}
		else
		{
			node.setPosition( center.getX() + z_pos, center.getY() + y_pos );
		}
	}

	/**
	 * Run layout.
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
	 * @param circle
	 *            the circle
	 */
	public static void runLayout( DNVGraph graph, DNVNode centralNode, int level, float centerX, float centerY, boolean circle )
	{
		// Map<Float,List<DNVNode>> distanceToNodes = new
		// HashMap<Float,List<DNVNode>>();
		List<DNVNode> nodes = graph.getNodes( level );
		float distance;
		float maxDistance = -1;
		List<DNVNode> genreNodes = new ArrayList<DNVNode>();
		for( DNVNode tempNode : nodes )
		{
			distance = GraphFunctions.findShortestPathDistance( centralNode, tempNode );
			if( tempNode.getType().equals( "genre" ) )
			{
				// handle the genre nodes after we know the max distance
				genreNodes.add( tempNode );
				continue;
				// distance = (float)(DNVEdge.DEFAULT_RESTING_DISTANCE*1.5/2.0);
			}

			if( distance > maxDistance && distance < Float.POSITIVE_INFINITY )
			{
				maxDistance = distance;
			}

			// List<DNVNode> nodesAtDistance = distanceToNodes.get( distance );
			// if( nodesAtDistance == null )
			// {
			// nodesAtDistance = new ArrayList<DNVNode>();
			// distanceToNodes.put( distance, nodesAtDistance );
			// }

			tempNode.setDistanceFromCenterNode( distance );
			// nodesAtDistance.add( tempNode );
		}

		// Set the genre nodes' distance
		distance = maxDistance * 1.5f;
		// List<DNVNode> nodesAtDistance = distanceToNodes.get( distance );
		// if( nodesAtDistance == null )
		// {
		// nodesAtDistance = new ArrayList<DNVNode>();
		// distanceToNodes.put( distance, nodesAtDistance );
		// }
		for( DNVNode tempNode : genreNodes )
		{
			tempNode.setDistanceFromCenterNode( distance );
			// nodesAtDistance.add( tempNode );
		}

		centralNode.setPosition( centerX, centerY );
		float width = DNVEdge.DEFAULT_RESTING_DISTANCE;
		layoutLevel( width, width, graph, 0.001f, level, centralNode.getPosition(), maxDistance * 1.1f, circle );
	}

	// private static Vector2D getPosition( Vector2D center, float radius, int
	// index, int totalNumberOfNodes )
	// {
	// Vector2D position = new Vector2D( center );
	//
	// float angle = 2.0f * (float)Math.PI / totalNumberOfNodes * index;
	//
	// float x = position.getX() + (float)Math.cos( angle ) * radius;
	// float y = position.getY() + (float)Math.sin( angle ) * radius;
	//
	// position.setX( x );
	// position.setY( y );
	//
	// return position;
	// }

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
	 * @param center
	 *            the center
	 * @param maxRadius
	 *            the max radius
	 * @param counter
	 *            the counter
	 * @param circle
	 *            the circle
	 */
	public static void runIteration( float width, float height, DNVGraph graph, int level, float temperature, Vector2D center, float maxRadius,
			int counter, boolean circle )
	{
		float area = width * height;
		float k = (float)Math.sqrt( area / graph.getGraphSize( level ) );
		float kPower2 = k * k;

		// "Temperature : " + temperature + " Level : " + level +
		// " Number of Nodes: " + graph.getGraphSize( level ) );

		Vector2D difference = new Vector2D();
		float length;
		// float k2;

		Grid grid = new Grid( k * 2, graph, level );
		List<DNVNode> potentialNodes;

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
			if( counter % 5 == 0 )
			{
				forceToShape( maxRadius, circle, v );
			}
		}
	}

	/**
	 * Force to shape.
	 * 
	 * @param maxRadius
	 *            the max radius
	 * @param circle
	 *            the circle
	 * @param v
	 *            the v
	 */
	private static void forceToShape( float maxRadius, boolean circle, DNVNode v )
	{
		if( circle )
			forceToCircle( maxRadius, v );
		else
			forceToLine( maxRadius, v );
	}

	/**
	 * Force to circle.
	 * 
	 * @param maxRadius
	 *            the max radius
	 * @param v
	 *            the v
	 */
	private static void forceToCircle( float maxRadius, DNVNode v )
	{
		float radius;
		radius = v.getDistanceFromCenterNode();
		if( radius > Float.POSITIVE_INFINITY )
		{
			radius = maxRadius;
		}
		v.getPosition().normalize().dotProduct( radius );
	}

	/**
	 * Force to line.
	 * 
	 * @param maxRadius
	 *            the max radius
	 * @param v
	 *            the v
	 */
	private static void forceToLine( float maxRadius, DNVNode v )
	{
		float radius;
		radius = v.getDistanceFromCenterNode();
		if( radius == Float.POSITIVE_INFINITY )
		{
			radius = maxRadius;
		}
		v.getPosition().setX( radius );
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
	 * @param center
	 *            the center
	 * @param maxRadius
	 *            the max radius
	 * @param circle
	 *            the circle
	 */
	private static void layoutLevel( float width, float height, DNVGraph graph, float coolingFactor, int level, Vector2D center, float maxRadius,
			boolean circle )
	{
		float size = Math.max( GraphFunctions.getGraphWidth( graph, level, false ), width );
		size = Math.max( size, Math.max( GraphFunctions.getGraphHeight( graph, level, false ), height ) );
		float temperature = size;

		layoutLevel( width, height, graph, coolingFactor, level, center, maxRadius, circle, temperature );
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
	 * @param center
	 *            the center
	 * @param maxRadius
	 *            the max radius
	 * @param circle
	 *            the circle
	 * @param temperature
	 *            the temperature
	 */
	public static void layoutLevel( float width, float height, DNVGraph graph, float coolingFactor, int level, Vector2D center, float maxRadius,
			boolean circle, float temperature )
	{
		int counter = 0;
		while( temperature > 0 )
		{
			runIteration( width, height, graph, level, temperature, center, maxRadius, counter, circle );
			temperature = cool( temperature, coolingFactor );
			counter++;
		}

		for( DNVNode v : graph.getNodes( level ) )
		{
			forceToShape( maxRadius, circle, v );
		}
	}
}
