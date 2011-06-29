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

package net.wigis.graph.dnv.layout.implementations;

import java.util.Collection;
import java.util.List;

import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.layout.helpers.Grid;
import net.wigis.graph.dnv.layout.interfaces.SpaceRestrictedLayoutInterface;
import net.wigis.graph.dnv.utilities.GraphFunctions;
import net.wigis.graph.dnv.utilities.Vector2D;
import net.wigis.settings.Settings;

// TODO: Auto-generated Javadoc
/**
 * The Class FruchtermanReingold.
 * 
 * @author Brynjar Gretarsson
 */
public class FruchtermanReingold implements SpaceRestrictedLayoutInterface
{

	/**
	 * Logger.
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
	 * @param useNumberOfSubnodes
	 *            the use number of subnodes
	 * @param useRestingDistance
	 *            the use resting distance
	 * @param useNodeSize
	 *            the use node size
	 */
	// // private static Log logger = LogFactory.getLog(
	// FruchtermanReingold.class );

	public static void runIteration( float width, float height, DNVGraph graph, int level, float temperature, boolean useNumberOfSubnodes,
			boolean useRestingDistance, boolean useNodeSize )
	{
		runIteration( width, height, graph.getNodes( level ), graph.getEdges( level ), temperature, useNumberOfSubnodes, useRestingDistance,
				useNodeSize );
	}

	/**
	 * Run iteration.
	 * 
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @param nodes
	 *            the nodes
	 * @param edges
	 *            the edges
	 * @param temperature
	 *            the temperature
	 * @param useNumberOfSubnodes
	 *            the use number of subnodes
	 * @param useRestingDistance
	 *            the use resting distance
	 * @param useNodeSize
	 *            the use node size
	 */
	public static void runIteration( float width, float height, Collection<DNVNode> nodes, Collection<DNVEdge> edges, float temperature,
			boolean useNumberOfSubnodes, boolean useRestingDistance, boolean useNodeSize )
	{
		float area = width * height;
		float k = (float)Math.sqrt( area / nodes.size() );
		float kPower2 = k * k;

		// System.out.println( "Temperature : " + temperature + " Level : " +
		// level + " Number of Nodes: " + graph.getGraphSize( level ) );

		Vector2D difference = new Vector2D();
		float length;

		Grid grid = new Grid( k * 2, nodes );
		List<DNVNode> potentialNodes;

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
					// If two nodes are in exact same position
					// then we need to do something to move them apart.
					// Give them a small repelling force in random direction.
					if( length == 0 )
					{
						difference.set( (float)Math.random(), (float)Math.random() );
						length = difference.length();
					}
					if( length < k * 2 )
					{
						difference.normalize();
						if( useNodeSize )
						{
							difference.dotProduct( repel( length, kPower2 * u.getRadius() ) );
						}
						else
						{
							difference.dotProduct( repel( length, kPower2 ) );
						}
						if( useNumberOfSubnodes )
						{
							difference.dotProduct( Math.max( u.getSubNodes().size(), 1 ) );
							difference.dotProduct( Math.max( v.getSubNodes().size(), 1 ) );
						}
						v.getForce().add( difference );
					}
				}
			}
		}

		// attractive forces
		for( DNVEdge e : edges )
		{
			difference.set( e.getFrom().getPosition() );
			difference.subtract( e.getTo().getPosition() );
			length = difference.length();
			if( useRestingDistance )
			{
				length = length - e.getRestingDistance();
				length = length * e.getK();
				difference.normalize();
				difference.dotProduct( attract( length, k ) );
			}
			else
			{
				difference.dotProduct( attract( length, k ) );
			}
			if( e.getTo().getType().equals( "topic" ) || !e.getFrom().getType().equals( "topic" ) )
				e.getFrom().getForce().subtract( difference );
			if( !e.getTo().getType().equals( "topic" ) || e.getFrom().getType().equals( "topic" ) )
				e.getTo().getForce().add( difference );
		}
		/*
		 * Vector2D center = GraphFunctions.getCenterOfGravity( nodes.iterator()
		 * ); for( DNVNode v : nodes ) { difference.set( center );
		 * difference.subtract( v.getPosition() ); v.getForce().add( difference
		 * ); }
		 */
		// apply the forces
		for( DNVNode v : nodes )
		{
			if( v.getProperty( "pinned" ) == null )
			{
				difference.set( v.getForce() );
				length = difference.length();
				difference.normalize();
				difference.dotProduct( Math.min( length, temperature ) );
				v.move( difference, true, false );
				// v.getPosition().setX( Math.min( width / 2.0f, Math.max(
				// -width / 2.0f, v.getPosition().getX() ) ) );
				// v.getPosition().setY( Math.min( height / 2.0f, Math.max(
				// -height / 2.0f, v.getPosition().getY() ) ) );
			}
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
	public static float repel( float x, float kPower2TimesRadiusU )
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
	public static float cool( float temperature, float coolingFactor )
	{
		if( temperature > 0.01 )
			return Math.max( 0, temperature * ( 1 - coolingFactor ) );

		return 0;
	}

	/**
	 * Run layout.
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
	 * @param layoutAllLevels
	 *            the layout all levels
	 * @param useNumberOfSubnodes
	 *            the use number of subnodes
	 */
	@Override
	public void runLayout( float width, float height, DNVGraph graph, float coolingFactor, int level, boolean layoutAllLevels,
			boolean useNumberOfSubnodes )
	{
		runLayout( width, height, graph, coolingFactor, level, layoutAllLevels, useNumberOfSubnodes, false, false );
	}

	/**
	 * Run layout.
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
	 * @param layoutAllLevels
	 *            the layout all levels
	 * @param useNumberOfSubnodes
	 *            the use number of subnodes
	 * @param useEdgeRestingDistance
	 *            the use edge resting distance
	 */
	@Override
	public void runLayout( float width, float height, DNVGraph graph, float coolingFactor, int level, boolean layoutAllLevels,
			boolean useNumberOfSubnodes, boolean useEdgeRestingDistance )
	{
		runLayout( width, height, graph, coolingFactor, level, layoutAllLevels, useNumberOfSubnodes, useEdgeRestingDistance, false );
	}

	/**
	 * Run layout.
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
	 * @param layoutAllLevels
	 *            the layout all levels
	 * @param useNumberOfSubnodes
	 *            the use number of subnodes
	 * @param useEdgeRestingDistance
	 *            the use edge resting distance
	 * @param useNodeSize
	 *            the use node size
	 */
	@Override
	public void runLayout( float width, float height, DNVGraph graph, float coolingFactor, int level, boolean layoutAllLevels,
			boolean useNumberOfSubnodes, boolean useEdgeRestingDistance, boolean useNodeSize )
	{
		if( layoutAllLevels )
		{
			for( level = graph.getMaxLevel(); level >= 0; level-- )
			{
				System.out.println( "Running Fruchterman-Reingold on level " + level );
				layoutLevel( width, height, graph.getNodes( level ), graph.getEdges( level ), coolingFactor, useNumberOfSubnodes,
						useEdgeRestingDistance, useNodeSize );
			}

			System.out.println( "Layout complete" );
		}
		else
		{
			runLayout( width, height, graph.getNodes( level ), graph.getEdges( level ), coolingFactor, useNumberOfSubnodes, useEdgeRestingDistance,
					useNodeSize );
		}

	}

	/**
	 * Run layout.
	 * 
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @param nodes
	 *            the nodes
	 * @param edges
	 *            the edges
	 * @param coolingFactor
	 *            the cooling factor
	 * @param useNumberOfSubnodes
	 *            the use number of subnodes
	 */
	@Override
	public void runLayout( float width, float height, Collection<DNVNode> nodes, Collection<DNVEdge> edges, float coolingFactor,
			boolean useNumberOfSubnodes )
	{
		runLayout( width, height, nodes, edges, coolingFactor, useNumberOfSubnodes, false, false );
	}

	/**
	 * Run layout.
	 * 
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @param nodes
	 *            the nodes
	 * @param edges
	 *            the edges
	 * @param coolingFactor
	 *            the cooling factor
	 * @param useNumberOfSubnodes
	 *            the use number of subnodes
	 * @param useEdgeRestingDistance
	 *            the use edge resting distance
	 * @param useNodeSize
	 *            the use node size
	 */
	@Override
	public void runLayout( float width, float height, Collection<DNVNode> nodes, Collection<DNVEdge> edges, float coolingFactor,
			boolean useNumberOfSubnodes, boolean useEdgeRestingDistance, boolean useNodeSize )
	{
		layoutLevel( width, height, nodes, edges, coolingFactor, useNumberOfSubnodes, useEdgeRestingDistance, useNodeSize );
	}

	/**
	 * Layout level.
	 * 
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @param nodes
	 *            the nodes
	 * @param edges
	 *            the edges
	 * @param coolingFactor
	 *            the cooling factor
	 * @param useNumberOfSubnodes
	 *            the use number of subnodes
	 * @param useEdgeRestingDistance
	 *            the use edge resting distance
	 * @param useNodeSize
	 *            the use node size
	 */
	private static void layoutLevel( float width, float height, Collection<DNVNode> nodes, Collection<DNVEdge> edges, float coolingFactor,
			boolean useNumberOfSubnodes, boolean useEdgeRestingDistance, boolean useNodeSize )
	{
		float size = Math.max( GraphFunctions.getGraphWidth( nodes, false ), width );
		size = Math.max( size, Math.max( GraphFunctions.getGraphHeight( nodes, false ), height ) );
		float temperature = size / 10.0f;
		if( nodes.size() > 0 )
		{
			DNVGraph graph = nodes.iterator().next().getGraph();

			while( temperature > 0 )
			{
				synchronized( graph )
				{
					runIteration( width, height, nodes, edges, temperature, useNumberOfSubnodes, useEdgeRestingDistance, useNodeSize );
				}
				temperature = cool( temperature, coolingFactor );
				// System.out.println( "Temperature : " + temperature );
			}
		}
	}

	/** The Constant FRUCHTERMAN_REINGOLD_LAYOUT. */
	public static final String LABEL = "Fructerman-Reingold";

	@Override
	public String getLabel()
	{
		return LABEL;
	}
}
