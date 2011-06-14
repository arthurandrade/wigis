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

import java.util.Collections;
import java.util.List;

import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.utilities.GraphFunctions;
import net.wigis.graph.dnv.utilities.NumberOfNeighborSort;
import net.wigis.graph.dnv.utilities.Randomize;
import net.wigis.graph.dnv.utilities.SortByLongProperty;
import net.wigis.graph.dnv.utilities.Vector2D;
import net.wigis.settings.Settings;

// TODO: Auto-generated Javadoc
/**
 * The Class CircularLayout.
 * 
 * @author Brynjar Gretarsson
 */
public class CircularLayout implements SortingLayoutInterface
{

	/** The Constant NO_SORT. */
	public static final String NO_SORT = "No sort";

	/** The Constant RANDOM_SORT. */
	public static final String RANDOM_SORT = "Randomized";

	/** The Constant TIME_SORT. */
	public static final String TIME_SORT = "Time sort";

	/** The Constant NUMBER_OF_EDGES_SORT. */
	public static final String NUMBER_OF_EDGES_SORT = "Number of edges sort";

	/** The Constant FORCE_DIRECTED_SORT. */
	public static final String FORCE_DIRECTED_SORT = "Force directed";

	/** The Constant SORT_TYPES. */
	public static final String[] SORT_TYPES = { NO_SORT, RANDOM_SORT, TIME_SORT, NUMBER_OF_EDGES_SORT, FORCE_DIRECTED_SORT };

	/** The random. */
	private static Randomize random = new Randomize();

	/** The nons. */
	private static NumberOfNeighborSort nons = new NumberOfNeighborSort();

	/** The time sort. */
	private static SortByLongProperty timeSort = new SortByLongProperty( "time", false );

	/** The Constant DEG_TO_RAD. */
	private static final double DEG_TO_RAD = Math.PI / 180.0;

	/** The Constant START_AT. */
	private static final double START_AT = 225 * DEG_TO_RAD;

	/**
	 * Run layout.
	 * 
	 * @param graph
	 *            the graph
	 * @param level
	 *            the level
	 * @param sort
	 *            the sort
	 */
	@Override
	public void runLayout( DNVGraph graph, int level, String sort )
	{
		List<DNVNode> nodes = graph.getNodes( level );
		Vector2D center = new Vector2D( 0, 0 );

		if( sort.equals( RANDOM_SORT ) )
		{
			Collections.sort( nodes, random );
		}
		else if( sort.equals( NUMBER_OF_EDGES_SORT ) )
		{
			Collections.sort( nodes, nons );
		}
		else if( sort.equals( TIME_SORT ) )
		{
			Collections.sort( nodes, timeSort );
		}

		int i = 0;
		for( DNVNode node : nodes )
		{
			node.setPosition( getPosition( i, START_AT, nodes.size(), 50, center ) );
			i++;
		}

		if( sort.equals( FORCE_DIRECTED_SORT ) )
		{
			List<DNVEdge> edges = graph.getEdges( level );
			runForceDirectedLayout( nodes, edges, center, 50 );
		}
	}

	/**
	 * Run force directed layout.
	 * 
	 * @param nodes
	 *            the nodes
	 * @param edges
	 *            the edges
	 * @param center
	 *            the center
	 * @param radius
	 *            the radius
	 */
	private static void runForceDirectedLayout( List<DNVNode> nodes, List<DNVEdge> edges, Vector2D center, int radius )
	{
		float size = Math.max( GraphFunctions.getGraphWidth( nodes, false ), radius * 2 );
		size = Math.max( size, Math.max( GraphFunctions.getGraphHeight( nodes, false ), radius * 2 ) );
		float temperature = size / 3;
		int counter = 0;
		while( temperature > 0 )
		{
			FruchtermanReingold.runIteration( radius * 2, radius * 2, nodes, edges, temperature, false, false, false );
			temperature = FruchtermanReingold.cool( temperature, 0.01f );
			if( counter % 5 == 0 )
			{
				forceToCircle( nodes, center, radius );
			}
			counter++;
			// System.out.println( "DocumentTopicsCircularLayout iteration " +
			// counter + " temperature " + temperature );
		}

		forceToCircle( nodes, center, radius );
	}

	/**
	 * Force to circle.
	 * 
	 * @param nodes
	 *            the nodes
	 * @param center
	 *            the center
	 * @param radius
	 *            the radius
	 */
	private static void forceToCircle( List<DNVNode> nodes, Vector2D center, int radius )
	{
		for( DNVNode v : nodes )
		{
			Vector2D newPos = new Vector2D( v.getPosition() );
			newPos.subtract( center );
			newPos.normalize();
			newPos.dotProduct( radius );
			newPos.add( center );
			v.setPosition( newPos );
		}
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
	 * @return the position
	 */
	private static Vector2D getPosition( double i, double startAt, int size, double radius, Vector2D center )
	{
		double radians = startAt + 2.0 * Math.PI / size * i;

		float x_pos = (float)( Math.cos( radians ) * radius );
		float y_pos = (float)( Math.sin( radians ) * radius );

		return new Vector2D( center.getX() + x_pos, center.getY() + y_pos );
	}

	public static final String LABEL = "Circular Layout";
	
	@Override
	public String getLabel()
	{
		return LABEL;
	}
}
