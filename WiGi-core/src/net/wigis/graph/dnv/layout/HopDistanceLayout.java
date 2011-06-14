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

import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.utilities.GraphFunctions;
import net.wigis.graph.dnv.utilities.Vector2D;

// TODO: Auto-generated Javadoc
/**
 * The Class HopDistanceLayout.
 * 
 * @author Brynjar Gretarsson
 */
public class HopDistanceLayout implements NodeCentralizedLayoutInterface
{

	/**
	 * Run layout.
	 * 
	 * @param graph
	 *            the graph
	 * @param centralNode
	 *            the central node
	 * @param level
	 *            the level
	 * @param circle
	 *            the circle
	 */
	@Override
	public void runLayout( DNVGraph graph, DNVNode centralNode, int level, boolean circle )
	{
		for( DNVNode node : graph.getNodes( level ) )
		{
			node.removeProperty( "hopDistance" );
		}
		GraphFunctions.findShortestPathToAllNodesInNumberOfHops( centralNode );
		float maxDistance = 0;
		String hopDistance;
		float tempDistance;
		for( DNVNode node : graph.getNodes( level ) )
		{
			hopDistance = node.getProperty( "hopDistance" );
			if( hopDistance != null )
			{
				tempDistance = Float.parseFloat( hopDistance );
				if( tempDistance > maxDistance )
				{
					maxDistance = tempDistance;
				}
			}
		}
		maxDistance++;
		float width = GraphFunctions.getGraphWidth( graph, level, false );
		float height = GraphFunctions.getGraphHeight( graph, level, false );

		layoutLevel( width, height, graph, 0.01f, level, centralNode.getPosition(), circle, maxDistance );
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
	 * @param circle
	 *            the circle
	 * @param maxDistance
	 *            the max distance
	 */
	private static void layoutLevel( float width, float height, DNVGraph graph, float coolingFactor, int level, Vector2D center, boolean circle,
			float maxDistance )
	{
		float size = Math.max( GraphFunctions.getGraphWidth( graph, level, false ), width );
		size = Math.max( size, Math.max( GraphFunctions.getGraphHeight( graph, level, false ), height ) );
		float temperature = size / 4.0f;

		int counter = 0;
		while( temperature > 0 )
		{
			runIteration( width, height, graph, level, temperature, center, counter, circle, maxDistance );
			temperature = cool( temperature, coolingFactor );
			counter++;
		}

		for( DNVNode v : graph.getNodes( level ) )
		{
			forceToShape( v, center, circle, maxDistance );
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
	 * Force to circle.
	 * 
	 * @param v
	 *            the v
	 * @param center
	 *            the center
	 * @param maxDistance
	 *            the max distance
	 */
	private static void forceToCircle( DNVNode v, Vector2D center, float maxDistance )
	{
		float radius;
		try
		{
			String hopDistance = v.getProperty( "hopDistance" );
			if( hopDistance == null || hopDistance.equals( "" ) )
			{
				radius = maxDistance;
			}
			else
			{
				radius = Float.parseFloat( hopDistance );
			}
			v.getPosition().normalize().dotProduct( radius ).add( center );
			// if( v.getPosition().getX() < center.getX() )
			// {
			// v.getPosition().setX( center.getX() );
			// }
		}
		catch( NullPointerException npe )
		{
			npe.printStackTrace();
		}
	}

	/**
	 * Force to shape.
	 * 
	 * @param v
	 *            the v
	 * @param center
	 *            the center
	 * @param circle
	 *            the circle
	 * @param maxDistance
	 *            the max distance
	 */
	private static void forceToShape( DNVNode v, Vector2D center, boolean circle, float maxDistance )
	{
		if( circle )
			forceToCircle( v, center, maxDistance );
		else
			forceToLine( v, center, maxDistance );
	}

	/**
	 * Force to line.
	 * 
	 * @param v
	 *            the v
	 * @param center
	 *            the center
	 * @param maxDistance
	 *            the max distance
	 */
	private static void forceToLine( DNVNode v, Vector2D center, float maxDistance )
	{
		float radius;
		String hopDistance = v.getProperty( "hopDistance" );
		if( hopDistance == null || hopDistance.equals( "" ) )
		{
			radius = maxDistance;
		}
		else
		{
			radius = Float.parseFloat( hopDistance );
		}
		v.getPosition().setX( radius + center.getX() );
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
	 * @param center
	 *            the center
	 * @param counter
	 *            the counter
	 * @param circle
	 *            the circle
	 * @param maxDistance
	 *            the max distance
	 */
	public static void runIteration( float width, float height, DNVGraph graph, int level, float temperature, Vector2D center, int counter,
			boolean circle, float maxDistance )
	{
		synchronized( graph )
		{

			float area = width * height;
			float k = (float)Math.sqrt( area / graph.getGraphSize( level ) );
			float kPower2 = k * k;

			// System.out.println( "Temperature : " + temperature + " Level : "
			// + level + " Number of Nodes: " + graph.getGraphSize( level ) );

			Vector2D difference = new Vector2D();
			float length;

			Grid grid = new Grid( k * 2, graph, level );
			List<DNVNode> potentialNodes;

			// float maxYAtDistance1 = Float.NEGATIVE_INFINITY;
			// float minYAtDistance1 = Float.POSITIVE_INFINITY;
			// float tempY;
			// float distance;
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
						if( length == 0 )
						{
							difference.set( (float)Math.random() - 0.5f, (float)Math.random() - 0.5f );
							length = difference.length();
						}
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
					forceToShape( v, center, circle, maxDistance );
				}
			}
		}
	}

	@Override
	public String getLabel()
	{
		return "Hop Distance Layout";
	}

}
