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
import java.util.List;
import java.util.Map;

import net.wigis.graph.data.utilities.CreateDNVFromDocumentTopics;
import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVEntity;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.utilities.GraphFunctions;
import net.wigis.graph.dnv.utilities.SortByFloatProperty;
import net.wigis.graph.dnv.utilities.Timer;
import net.wigis.graph.dnv.utilities.Vector2D;

// TODO: Auto-generated Javadoc
/**
 * The Class DocumentTopicsCircularLayout.
 * 
 * @author Brynjar Gretarsson
 */
public class DocumentTopicsCircularLayout
{

	/**
	 * Run layout.
	 * 
	 * @param graph
	 *            the graph
	 * @param level
	 *            the level
	 * @param percentBuffer
	 *            the percent buffer
	 * @param coolingFactor
	 *            the cooling factor
	 * @param docIdPrefix
	 *            the doc id prefix
	 * @param widthDouble
	 *            the width double
	 * @param widthMultiplier
	 *            the width multiplier
	 * @param forceTopicNodesToCircle
	 *            the force topic nodes to circle
	 * @param keepDocumentNodesInOrder
	 *            the keep document nodes in order
	 * @param onlyDeformSelectedTopics
	 *            the only deform selected topics
	 * @param dissimilarityMatrix
	 *            the dissimilarity matrix
	 */
	public static void runLayout( DNVGraph graph, int level, float percentBuffer, float coolingFactor, String docIdPrefix, Double widthDouble,
			float widthMultiplier, boolean forceTopicNodesToCircle, boolean keepDocumentNodesInOrder, boolean onlyDeformSelectedTopics,
			double[][] dissimilarityMatrix )
	{
		System.out.println( "keep document nodes in order : " + keepDocumentNodesInOrder );

		Timer timer = new Timer( Timer.MILLISECONDS );
		timer.setStart();
		CreateDNVFromDocumentTopics.generateDissimilarityEdges( graph, dissimilarityMatrix, 0.3f );
		Map<Integer, DNVEntity> nodes = graph.getNodesByType( level, "document" );
		List<DNVNode> nodeList = new ArrayList<DNVNode>();
		for( DNVEntity node : nodes.values() )
		{
			if( node.getBbId().startsWith( docIdPrefix ) )
			{
				nodeList.add( (DNVNode)node );
			}
		}

		List<DNVNode> docNodes = new ArrayList<DNVNode>();
		for( DNVEntity node : graph.getNodesByType( level, "document" ).values() )
		{
			if( !node.getBbId().contains( "sec" ) && node.getBbId().startsWith( "doc" ) )
			{
				docNodes.add( (DNVNode)node );
			}
		}
		float width;
		// if( widthDouble == null )
		// {
		width = GraphFunctions.getGraphWidth( docNodes, false );
		// }
		// else
		// {
		// width = widthDouble.floatValue();
		// }

		width *= widthMultiplier;
		float radius = width / 2.0f;
		float buffer = percentBuffer * radius;
		float subtract = 1.0f / 25.0f * radius / nodeList.size();
		Vector2D center = new Vector2D( GraphFunctions.getAverageXPosition( docNodes.iterator() ), GraphFunctions.getAverageYPosition( docNodes
				.iterator() ) );
		Vector2D center2 = new Vector2D( center );
		double degToRad = Math.PI / 180.0;
		double startAt = 225 * degToRad;
		if( docIdPrefix.contains( "sec" ) )
		{
			String tempStr = docIdPrefix.substring( 0, docIdPrefix.indexOf( "sec" ) );
			DNVNode parentNode = (DNVNode)graph.getNodeByBbId( tempStr );
			if( parentNode != null )
			{
				String angleStr = parentNode.getProperty( "angle" );
				if( angleStr != null && !angleStr.equals( "" ) )
				{
					startAt = Double.parseDouble( parentNode.getProperty( "angle" ) );
					startAt += 180 * degToRad;
				}
				center2.set( parentNode.getPosition() );
				center2.subtract( center );
				center2.normalize();
				center2.dotProduct( radius );
				center2.add( parentNode.getPosition() );

				nodeList.add( parentNode );
			}
		}

		SortByFloatProperty sbfp = new SortByFloatProperty( "index", false );
		Collections.sort( nodeList, sbfp );
		int j = 1;

		for( DNVNode node : nodeList )
		{
			node.setPosition( getPosition( j, startAt, nodeList.size(), radius, center2, node ) );
			j++;
			radius -= subtract;
		}

		radius -= nodeList.size() * subtract;
		layoutLevel( width, width, graph, coolingFactor, level, buffer, center, radius, forceTopicNodesToCircle, keepDocumentNodesInOrder,
				onlyDeformSelectedTopics );

		CreateDNVFromDocumentTopics.removeDissimilarityEdges( graph );
		timer.setEnd();

		System.out.println( "=========================================================================" );
		System.out.println( "Circular Layout took " + timer.getLastSegment( Timer.SECONDS ) + " seconds." );
		System.out.println( "=========================================================================" );
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
	 * @param buffer
	 *            the buffer
	 * @param center
	 *            the center
	 * @param radius
	 *            the radius
	 * @param forceTopicNodesToCircle
	 *            the force topic nodes to circle
	 * @param keepDocumentNodesInOrder
	 *            the keep document nodes in order
	 * @param onlyDeformSelectedTopics
	 *            the only deform selected topics
	 */
	private static void layoutLevel( float width, float height, DNVGraph graph, float coolingFactor, int level, float buffer, Vector2D center,
			float radius, boolean forceTopicNodesToCircle, boolean keepDocumentNodesInOrder, boolean onlyDeformSelectedTopics )
	{
		float size = Math.max( GraphFunctions.getGraphWidth( graph, level, false ), width );
		size = Math.max( size, Math.max( GraphFunctions.getGraphHeight( graph, level, false ), height ) );
		float temperature = size / 3;

		List<DNVNode> nodes;
		if( buffer == 0 && keepDocumentNodesInOrder )
		{
			System.out.println( "DocumentTopicsCircularLayout - only moving topic nodes." );
			nodes = new ArrayList<DNVNode>();
			for( DNVEntity entity : graph.getNodesByType( level, "topic" ).values() )
			{
				nodes.add( (DNVNode)entity );
			}
		}
		else
		{
			System.out.println( "DocumentTopicsCircularLayout - moving all nodes." );
			nodes = graph.getNodes( level );
		}

		System.out.println( "Force topic nodes to circle : " + forceTopicNodesToCircle );

		int counter = 0;
		while( temperature > 0 )
		{
			runIteration( width, height, graph, nodes, level, temperature, counter, buffer, center, radius, forceTopicNodesToCircle,
					keepDocumentNodesInOrder, onlyDeformSelectedTopics );
			temperature = cool( temperature, coolingFactor );
			counter++;
			// System.out.println( "DocumentTopicsCircularLayout iteration " +
			// counter + " temperature " + temperature );
		}

		for( DNVEntity v : graph.getNodesByType( level, "document" ).values() )
		{
			forceToShape( (DNVNode)v, buffer, center, keepDocumentNodesInOrder, onlyDeformSelectedTopics );
		}

		if( forceTopicNodesToCircle )
		{
			for( DNVEntity v : graph.getNodesByType( level, "topic" ).values() )
			{
				forceToShape( center, radius, (DNVNode)v, buffer );
			}
		}
	}

	/**
	 * Force to shape.
	 * 
	 * @param center
	 *            the center
	 * @param radius
	 *            the radius
	 * @param v
	 *            the v
	 * @param buffer
	 *            the buffer
	 */
	private static void forceToShape( Vector2D center, float radius, DNVNode v, float buffer )
	{
		// if( v.getNeighbors().size() > 1 )
		// {
		float distance = GraphFunctions.getDistance( v.getPosition(), center );
		if( distance > radius - buffer )
		{
			Vector2D newPos = new Vector2D( v.getPosition() );
			newPos.subtract( center );
			newPos.normalize();
			newPos.dotProduct( radius - buffer );
			newPos.add( center );
			v.setPosition( newPos );
		}
		// }
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
	 * @param nodes
	 *            the nodes
	 * @param level
	 *            the level
	 * @param temperature
	 *            the temperature
	 * @param counter
	 *            the counter
	 * @param buffer
	 *            the buffer
	 * @param center
	 *            the center
	 * @param radius
	 *            the radius
	 * @param forceTopicNodesToCircle
	 *            the force topic nodes to circle
	 * @param keepDocumentNodesInOrder
	 *            the keep document nodes in order
	 * @param onlyDeformSelectedTopics
	 *            the only deform selected topics
	 */
	public static void runIteration( float width, float height, DNVGraph graph, List<DNVNode> nodes, int level, float temperature, int counter,
			float buffer, Vector2D center, float radius, boolean forceTopicNodesToCircle, boolean keepDocumentNodesInOrder,
			boolean onlyDeformSelectedTopics )
	{
		float area = width * height;
		float k = (float)Math.sqrt( area / graph.getGraphSize( level ) );
		float kTimes2 = k * 2;
		float kPower2 = k * k;

		Vector2D difference = new Vector2D();
		float length;

		Grid grid = new Grid( kTimes2, nodes );
		List<DNVNode> potentialNodes;

		// float k2;

		// repulsive forces
		for( DNVNode v : nodes )
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
					if( length < kTimes2 )
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
			length = length - e.getRestingDistance();
			length = length * e.getK();
			difference.normalize();
			difference.dotProduct( length );
			// difference.dotProduct( attract( length, k ) );
			e.getFrom().getForce().subtract( difference );
			e.getTo().getForce().add( difference );
		}

		// apply the forces
		for( DNVNode v : nodes )
		{
			difference.set( v.getForce() );
			length = difference.length();
			difference.normalize();
			difference.dotProduct( Math.min( length, temperature ) );
			v.move( difference, true, false );
		}

		if( counter % 5 == 0 )
		{
			for( DNVEntity v : graph.getNodesByType( level, "document" ).values() )
			{
				if( v != null )
				{
					forceToShape( (DNVNode)v, buffer, center, keepDocumentNodesInOrder, onlyDeformSelectedTopics );
				}
			}
			if( forceTopicNodesToCircle )
			{
				for( DNVEntity v : graph.getNodesByType( level, "topic" ).values() )
				{
					forceToShape( center, radius, (DNVNode)v, buffer );
				}
			}
		}

	}

	/**
	 * Force to shape.
	 * 
	 * @param v
	 *            the v
	 * @param buffer
	 *            the buffer
	 * @param center
	 *            the center
	 * @param keepDocumentNodesInOrder
	 *            the keep document nodes in order
	 * @param onlyDeformSelectedTopics
	 *            the only deform selected topics
	 */
	private static void forceToShape( DNVNode v, float buffer, Vector2D center, boolean keepDocumentNodesInOrder, boolean onlyDeformSelectedTopics )
	{
		if( onlyDeformSelectedTopics )
		{
			boolean found = false;
			for( DNVNode neighbor : v.getNeighbors() )
			{
				if( neighbor.getType().equals( "topic" ) && neighbor.isSelected() )
				{
					found = true;
				}
			}
			if( !found )
			{
				buffer = 0;
			}
		}
		if( keepDocumentNodesInOrder )
		{
			String angleStr = v.getProperty( "angle" );
			if( angleStr != null && !angleStr.equals( "" ) )
			{
				float angle = Float.parseFloat( angleStr );
				float xDirection = (float)Math.cos( angle );
				float yDirection = (float)Math.sin( angle );
				forceToShape( v, buffer, center, xDirection, yDirection );
			}
		}
		else
		{
			Vector2D center2 = getCenter( v, center );
			Vector2D direction = new Vector2D( v.getPosition() );
			direction.subtract( center2 );
			direction.normalize();
			forceToShape( v, buffer, center, direction.getX(), direction.getY() );
		}
	}

	/**
	 * Force to shape.
	 * 
	 * @param v
	 *            the v
	 * @param buffer
	 *            the buffer
	 * @param center
	 *            the center
	 * @param xDirection
	 *            the x direction
	 * @param yDirection
	 *            the y direction
	 */
	private static void forceToShape( DNVNode v, float buffer, Vector2D center, float xDirection, float yDirection )
	{
		String radiusStr = v.getProperty( "radius" );
		if( radiusStr != null && !radiusStr.equals( "" ) )
		{
			float radius = Float.parseFloat( radiusStr );
			Vector2D center2 = getCenter( v, center );
			Vector2D position = v.getPosition();
			float distance = GraphFunctions.getDistance( center2, position );

			if( distance < radius - buffer )
			{
				distance = radius - buffer;
			}
			else if( distance > radius + buffer )
			{
				distance = radius + buffer;
			}

			float x_pos = center2.getX() + (float)( xDirection * distance );
			float y_pos = center2.getY() + (float)( yDirection * distance );

			position.setX( x_pos );
			position.setY( y_pos );
		}
	}

	/**
	 * Gets the center.
	 * 
	 * @param v
	 *            the v
	 * @param center
	 *            the center
	 * @return the center
	 */
	private static Vector2D getCenter( DNVNode v, Vector2D center )
	{
		String centerStr = v.getProperty( "center" );
		Vector2D center2;
		if( centerStr != null && !centerStr.equals( "" ) )
		{
			center2 = new Vector2D( centerStr );
		}
		else
		{
			center2 = center;
		}
		return center2;
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
	// private static float attract( float x, float k )
	// {
	// return ( x * x ) / k;
	// }

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
	 * Gets the position.
	 * 
	 * @param i
	 *            the i
	 * @param startAt
	 *            the start at
	 * @param size
	 *            the size
	 * @param radius
	 *            the radius
	 * @param center
	 *            the center
	 * @param node
	 *            the node
	 * @return the position
	 */
	private static Vector2D getPosition( double i, double startAt, int size, double radius, Vector2D center, DNVNode node )
	{
		double radians = startAt + 2.0 * Math.PI / size * i;

		node.setProperty( "angle", "" + radians );
		node.setProperty( "radius", "" + radius );
		node.setProperty( "center", center.toString() );

		float x_pos = (float)( Math.cos( radians ) * radius );
		float y_pos = (float)( Math.sin( radians ) * radius );

		return new Vector2D( center.getX() + x_pos, center.getY() + y_pos );
	}
}
