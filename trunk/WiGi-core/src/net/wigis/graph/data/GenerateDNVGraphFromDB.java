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

package net.wigis.graph.data;

import java.sql.ResultSet;
import java.sql.SQLException;

import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.clustering.KMostConnected;
import net.wigis.graph.dnv.utilities.GraphFunctions;
import net.wigis.graph.dnv.utilities.Vector2D;

// TODO: Auto-generated Javadoc
/**
 * The Class GenerateDNVGraphFromDB.
 * 
 * @author Brynjar Gretarsson
 */
public class GenerateDNVGraphFromDB
{

	/** The Constant DIVIDER. */
	private static final int DIVIDER = 3;

	/**
	 * Logger.
	 * 
	 * @param graphName
	 *            the graph name
	 * @param generateMultipleLevels
	 *            the generate multiple levels
	 * @return the dNV graph
	 */
	// // private static Log logger = LogFactory.getLog(
	// GenerateDNVGraphFromDB.class );

	public static DNVGraph generateGraph( String graphName, boolean generateMultipleLevels )
	{
		DNVGraph graph = new DNVGraph();

		try
		{
			DBManager dbm = new DBManager( "brynj001_graphs" );
			String query = "SELECT * FROM graphs WHERE name = '" + graphName + "'";
			ResultSet results = dbm.getResults( query );
			results.first();
			int idGraphs = results.getInt( "idGraphs" );
			query = "SELECT * FROM nodes WHERE idGraphs = " + idGraphs + ";";
			results = dbm.getResults( query );
			results.last();
			System.out.println( "Processing " + results.getRow() + " nodes" );
			results.beforeFirst();
			DNVNode newNode;
			Vector2D position;
			float r, g = 0, b = 0;
			r = graphName.charAt( 0 ) / 120.0f;

			if( graphName.length() > 1 )
				g = graphName.charAt( graphName.length() / 2 ) / 150.0f;
			if( graphName.length() > 2 )
				b = graphName.charAt( graphName.length() - 1 ) / 100.0f;
			while( results.next() )
			{
				position = new Vector2D( 100.0f * (float)Math.random(), 100.0f * (float)Math.random() );
				// position = new Vector2D( results.getFloat( "xPos" ),
				// results.getFloat( "yPos" ) );
				newNode = new DNVNode( position, graph );
				newNode.setColor( r, g, b );
				newNode.setPosition( position );
				newNode.setRadius( results.getFloat( "radius" ) );
				newNode.setMass( results.getFloat( "mass" ) );
				newNode.setLevel( 0 );
				newNode.setId( results.getInt( "idNodes" ) );
				graph.addNode( 0, newNode );
			}

			query = "SELECT * FROM edges WHERE idGraphs = " + idGraphs + ";";
			System.out.println( query );
			results = dbm.getResults( query );
			results.last();
			System.out.println( "Processing " + results.getRow() + " edges" );
			results.beforeFirst();
			DNVEdge newEdge;
			DNVNode fromNode;
			DNVNode toNode;
			while( results.next() )
			{
				fromNode = (DNVNode)graph.getNodeById( results.getInt( "fromNodeId" ) );
				toNode = (DNVNode)graph.getNodeById( results.getInt( "toNodeId" ) );
				newEdge = new DNVEdge( 0, DNVEdge.DEFAULT_RESTING_DISTANCE, false, fromNode, toNode, graph );
				if( fromNode != null && toNode != null )
				{
					fromNode.addFromEdge( newEdge );
					toNode.addToEdge( newEdge );
					graph.addNode( 0, newEdge );
				}
				else
					System.out.println( "Error with a node for edge " + results.getInt( "fromNodeId" ) + "->" + results.getInt( "toNodeId" ) );

			}

			if( generateMultipleLevels )
			{
				System.out.println( "Creating multilevels" );
				int level = 0;

				int numberOfClusters = graph.getGraphSize( level ) / DIVIDER;
				while( numberOfClusters > 0 )
				{
					KMostConnected.cluster( graph, numberOfClusters, level, true );
					level++;
					numberOfClusters = ( graph.getGraphSize( level ) - graph.getNumberOfDisconnectedNodes( level ) ) / DIVIDER;
				}

				System.out.println( "Initializing positions" );
				GraphFunctions.initializePositions( graph );
			}

			System.out.println( "Done" );

			dbm.closeConnection();
		}
		catch( SQLException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return graph;
	}

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main( String args[] )
	{
		DNVGraph graph = generateGraph( "uk", true );
		graph.writeGraph( "C:\\BLACKBOOK\\bball\\SOA\\3.dnv" );
	}
}
