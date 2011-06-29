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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.clustering.ConnectedClustering;
import net.wigis.graph.dnv.layout.interfaces.SimpleLayoutInterface;
import net.wigis.graph.dnv.utilities.GraphFunctions;
import net.wigis.graph.dnv.utilities.NumberOfSubnodesSort;
import net.wigis.graph.dnv.utilities.Vector2D;
import net.wigis.settings.Settings;

// TODO: Auto-generated Javadoc
/**
 * The Class DisjointGraphLayout.
 * 
 * @author Brynjar Gretarsson
 */
public class DisjointGraphLayout implements SimpleLayoutInterface
{

	/**
	 * Layout.
	 * 
	 * @param filename
	 *            the filename
	 */
	public static void runLayout( String filename )
	{
		System.out.println( "Starting layout for " + filename );

		DNVGraph graph = new DNVGraph( filename );

		runLayout( graph );

		graph.writeGraph( filename.substring( 0, filename.length() - 4 ) + "_laid_out.dnv" );

		System.out.println( "Done with layout for " + filename );
	}

	/**
	 * Layout.
	 * 
	 * @param graph
	 *            the graph
	 */
	public static void runLayout( DNVGraph graph )
	{
		DisjointGraphLayout layout = new DisjointGraphLayout();
		layout.runLayout( graph, 0 );
	}

	/**
	 * Layout subnodes.
	 * 
	 * @param graph
	 *            the graph
	 * @param level
	 *            the level
	 */
	private static void layoutSubnodes( DNVGraph graph, int level )
	{
		List<DNVNode> supernodes = graph.getNodes( level );
		for( DNVNode supernode : supernodes )
		{
			layoutSubgraph( supernode.getNumberOfSubNodes(), supernode );
		}

	}

	/*
	 * private static void layoutSubnodesLargerSpace( DNVGraph graph, int level,
	 * float multiplyer ) { List<DNVNode> supernodes = graph.getNodes( level );
	 * for( DNVNode supernode : supernodes ) { layoutSubgraph( graph, (float)(
	 * graph.getGraphSize( 0 ) * multiplyer ), supernode ); }
	 * 
	 * }
	 */
	/**
	 * Layout subgraph.
	 * 
	 * @param width
	 *            the width
	 * @param supernode
	 *            the supernode
	 */
	private static void layoutSubgraph( float width, DNVNode supernode )
	{
		float minX;
		float maxX;
		float minY;
		float maxY;
		new FruchtermanReingold()
				.runLayout( width, width, supernode.getSubGraph().getNodesList(), supernode.getSubGraph().getEdges().values(), 0.1f, false );

		minX = Integer.MAX_VALUE;
		maxX = Integer.MIN_VALUE;
		minY = Integer.MAX_VALUE;
		maxY = Integer.MIN_VALUE;
		// Tell nodes that they should belong to the original graph and find
		// boundaries
		for( DNVNode subNode : supernode.getSubGraph().getNodesList() )
		{
			if( subNode.getPosition().getX() < minX )
			{
				minX = subNode.getPosition().getX();
			}
			if( subNode.getPosition().getX() > maxX )
			{
				maxX = subNode.getPosition().getX();
			}
			if( subNode.getPosition().getY() < minY )
			{
				minY = subNode.getPosition().getY();
			}
			if( subNode.getPosition().getY() > maxY )
			{
				maxY = subNode.getPosition().getY();
			}
		}

		width = maxX - minX;
		float height = maxY - minY;
		supernode.setDiameterOfSubnodes( Math.max( Math.max( width, height ), 5 ) );
		supernode.setPosition( supernode.getFirstChild().getPosition().getX(), supernode.getFirstChild().getPosition().getY() );
	}

	/** The number of subnodes sort. */
	private static NumberOfSubnodesSort numberOfSubnodesSort = new NumberOfSubnodesSort();

	/**
	 * Layout based on number of subnodes.
	 * 
	 * @param graph
	 *            the graph
	 * @param level
	 *            the level
	 */
	public static void layoutBasedOnNumberOfSubnodes( DNVGraph graph, int level )
	{
		List<DNVNode> nodes = new ArrayList<DNVNode>( graph.getNodes( level ) );
		Collections.sort( nodes, numberOfSubnodesSort );
		Vector2D position;
		float radius = 0;
		int lastNumberOfSubnodes = -1;
		Vector2D movement = new Vector2D();
		Vector2D center = new Vector2D( 0, 0 );
		Map<Integer, Integer> histogram = generateHistogramOfSubnodes( graph, level );
		int i = 0;
		// float lastRadius = 0;
		// float allowedWidth = 0;
		int counter = 0;
		for( DNVNode node : nodes )
		{
			counter++;
			if( lastNumberOfSubnodes == node.getNumberOfSubNodes() )
			{
				i++;
			}
			else
			{
				// System.out.println( "number of subnodes " +
				// node.getNumberOfSubNodes() );
				// lastRadius = radius;
				radius += node.getDiameterOfSubnodes();
				lastNumberOfSubnodes = node.getNumberOfSubNodes();
				i = 0;
				// sqrt( (area of outer disk - area of inner disk ) / number of
				// nodes in outer disk )
				// allowedWidth = (float)Math.sqrt( ( Math.PI * radius * radius
				// - Math.PI * lastRadius * lastRadius ) / (float)histogram.get(
				// lastNumberOfSubnodes ) );
				// allowedWidth = radius - lastRadius;
				// System.out.println( "Allowed width for " +
				// lastNumberOfSubnodes + " is " + allowedWidth );
			}

			// System.out.println( "Counter : " + counter +
			// " - lastNumberOfSubnodes : " + lastNumberOfSubnodes +
			// " - numberOfClusters : " + histogram.get( lastNumberOfSubnodes )
			// );

			if( counter == 1 && histogram.get( lastNumberOfSubnodes ) == 1 )
			{
				// System.out.println( "Putting " + lastNumberOfSubnodes +
				// " node cluster in the center." );
				position = new Vector2D( 0, 0 );
			}
			else
			{
				position = getPosition( i, lastNumberOfSubnodes, histogram.get( lastNumberOfSubnodes ), radius, center );
			}
			movement.setX( position.getX() - node.getPosition().getX() );
			movement.setY( position.getY() - node.getPosition().getY() );
			node.move( movement, true, false );
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
	 * @param bigRadius
	 *            the big radius
	 * @param center
	 *            the center
	 * @return the position
	 */
	private static Vector2D getPosition( double i, double startAt, int size, double bigRadius, Vector2D center )
	{
		// double radians = 0.2 * i;
		// double radius = size / 10.0;
		float z_pos = (float)( 1.0 * i );

		double bigRadians = 2.0 * Math.PI / size * i;

		float y_pos = 0;// (float)Math.sin( radians );// * bigRadius;
		// float x_pos = (float)Math.cos( radians );// * bigRadius;

		z_pos = (float)( Math.cos( bigRadians ) * bigRadius );
		y_pos += Math.sin( bigRadians ) * bigRadius;

		return new Vector2D( center.getX() + z_pos, center.getY() + y_pos );
	}

	/**
	 * Generate histogram of subnodes.
	 * 
	 * @param graph
	 *            the graph
	 * @param level
	 *            the level
	 * @return the map
	 */
	private static Map<Integer, Integer> generateHistogramOfSubnodes( DNVGraph graph, int level )
	{
		HashMap<Integer, Integer> histogram = new HashMap<Integer, Integer>();
		List<DNVNode> nodes = graph.getNodes( level );
		DNVNode tempNode;
		Integer numberOfSubnodes;
		Integer counter;
		for( int i = 0; i < nodes.size(); i++ )
		{
			tempNode = nodes.get( i );
			numberOfSubnodes = tempNode.getNumberOfSubNodes();
			counter = histogram.get( numberOfSubnodes );
			if( counter == null )
			{
				counter = 0;
			}
			counter++;
			histogram.put( numberOfSubnodes, counter );
		}

		return histogram;
	}

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main( String[] args )
	{
		runLayout( Settings.GRAPHS_PATH + "_bb_al qaeda.dnv" );
		// layout( Settings.GRAPHS_PATH + "authors_graph_50k.dnv" );
	}

	@Override
	public void runLayout( DNVGraph graph, int level )
	{
		// System.out.println( "Clustering..." );
		GraphFunctions.clearHigherLevels( level, graph );
		ConnectedClustering.cluster( graph, level );
		// System.out.println( "Laying out subnodes." );
		layoutSubnodes( graph, level+1 );
		// System.out.println( "Laying out based on subnodes." );
		layoutBasedOnNumberOfSubnodes( graph, level+1 );
	}

	public static final String LABEL = "Disjoint Graph Layout";
	
	@Override
	public String getLabel()
	{
		return LABEL;
	}

}
