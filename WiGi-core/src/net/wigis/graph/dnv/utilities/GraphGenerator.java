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

package net.wigis.graph.dnv.utilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.wigis.graph.GraphsPathFilter;
import net.wigis.graph.data.utilities.RandomNames;
import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.layout.implementations.FruchtermanReingold;
import net.wigis.settings.Settings;

// TODO: Auto-generated Javadoc
/**
 * The Class GraphGenerator.
 * 
 * @author Brynjar Gretarsson
 */
public class GraphGenerator
{

	/** The TYP e_ circle. */
	public static int TYPE_CIRCLE = 0;

	/** The TYP e_ random. */
	public static int TYPE_RANDOM = 1;

	/**
	 * Logger.
	 * 
	 * @param numberOfVertices
	 *            the number of vertices
	 * @param numberOfEdges
	 *            the number of edges
	 * @return the string
	 */
	// // private static Log logger = LogFactory.getLog( GraphGenerator.class );

	public static String generateRandomGraphToFile( int numberOfVertices, int numberOfEdges )
	{
		String fileName = Settings.GRAPHS_PATH + "Random_" + numberOfVertices + "_" + numberOfEdges + ".dnv";

		DNVGraph graph = new DNVGraph();
		List<DNVNode> unconnectedNodes = new ArrayList<DNVNode>( 1000000 );

		Timer timer = new Timer( Timer.MILLISECONDS );
		DNVNode tempNode;
		Vector3D color = new Vector3D( 1, 0, 1 );
		timer.setStart();
		for( int i = 0; i < numberOfVertices; i++ )
		{
			tempNode = new DNVNode( new Vector2D( (float)Math.random(), (float)Math.random() ), graph );
			tempNode.setColor( color );
			unconnectedNodes.add( tempNode );
			graph.addNode( 0, tempNode );
			if( ( i + 1 ) % 10000 == 0 )
			{
				timer.setEnd();
				System.out.println( "node " + ( i + 1 ) + " - Total time: " + timer.getTotalTime( Timer.SECONDS ) + " seconds. Last segment: "
						+ timer.getLastSegment( Timer.SECONDS ) + " seconds." );
				timer.setStart();
			}
		}

		DNVEdge tempEdge;
		DNVNode tempNode2;
		int tempIndex;
		for( int i = 0; i < numberOfEdges; i++ )
		{
			tempIndex = (int)( Math.random() * graph.getGraphSize( 0 ) );
			tempNode = graph.getNodes( 0 ).get( tempIndex );
			if( unconnectedNodes.size() > 0 )
			{
				tempIndex = (int)( Math.random() * unconnectedNodes.size() );
				tempNode2 = unconnectedNodes.get( tempIndex );
				while( tempNode2.equals( tempNode ) )
				{
					tempIndex = (int)( Math.random() * unconnectedNodes.size() );
					tempNode2 = unconnectedNodes.get( tempIndex );
				}
				unconnectedNodes.remove( tempIndex );
			}
			else
			{
				tempIndex = (int)( Math.random() * graph.getGraphSize( 0 ) );
				tempNode2 = graph.getNodes( 0 ).get( tempIndex );
				while( tempNode2.equals( tempNode ) )
				{
					tempIndex = (int)( Math.random() * unconnectedNodes.size() );
					tempNode2 = graph.getNodes( 0 ).get( tempIndex );
				}
			}

			tempEdge = new DNVEdge( 0, DNVEdge.DEFAULT_RESTING_DISTANCE, false, tempNode, tempNode2, graph );
			graph.addNode( 0, tempEdge );
			if( ( i + 1 ) % 10000 == 0 )
			{
				timer.setEnd();
				System.out.println( "edge " + ( i + 1 ) + " - Total time: " + timer.getTotalTime( Timer.SECONDS ) + " seconds. Last segment: "
						+ timer.getLastSegment( Timer.SECONDS ) + " seconds." );
				timer.setStart();
			}
		}

		graph.writeGraph( fileName );

		return fileName;
	}

	/**
	 * Generate watts and strogatz graph to file.
	 * 
	 * @param numberOfNodes
	 *            the number of nodes
	 * @param rewireProbability
	 *            the rewire probability
	 */
	public static void generateWattsAndStrogatzGraphToFile( int numberOfNodes, double rewireProbability )
	{
		int meanDegree = (int)Math.round( Math.log( numberOfNodes ) * 1.33 );
		if( meanDegree % 2 == 1 )
		{
			meanDegree++;
		}
		generateWattsAndStrogatzGraphToFile( numberOfNodes, meanDegree, rewireProbability );
	}

	/**
	 * Generate watts and strogatz graph to file.
	 * 
	 * @param numberOfNodes
	 *            the number of nodes
	 * @param meanDegree
	 *            the mean degree
	 * @param rewireProbability
	 *            the rewire probability
	 */
	public static void generateWattsAndStrogatzGraphToFile( int numberOfNodes, int meanDegree, double rewireProbability )
	{
		System.out.println( "Generating Watts and Strogatz graph with " + numberOfNodes + " nodes and " + meanDegree + " mean degree." );
		System.out.println( "Rewiring probability is " + rewireProbability );
		DNVGraph graph = new DNVGraph();
		DNVNode tempNode;
		for( int i = 0; i < numberOfNodes; i++ )
		{
			tempNode = new DNVNode( new Vector2D( (float)Math.random(), (float)Math.random() ), graph );
			tempNode.setId( i );
			graph.addNode( 0, tempNode );
		}

		int degreeBy2 = meanDegree / 2;
		DNVNode tempNode2;
		DNVEdge tempEdge;
		int edgeId = numberOfNodes;
		HashMap<String, DNVEdge> edgeMap = new HashMap<String, DNVEdge>();
		System.out.println( "Generating nodes and edges..." );
		for( int i = 0; i < numberOfNodes; i++ )
		{
			if( i % 10000 == 0 )
			{
				System.out.println( 1 + " : " + i );
			}
			tempNode = (DNVNode)graph.getNodeById( i );
			for( int j = i - degreeBy2; j <= i + degreeBy2; j++ )
			{
				int index = j % numberOfNodes;
				if( index < 0 )
				{
					index += numberOfNodes;
				}
				if( index != i )
				{
					tempNode2 = (DNVNode)graph.getNodeById( index );
					tempEdge = edgeMap.get( i + "_" + index );
					if( tempEdge == null )
					{
						tempEdge = edgeMap.get( index + "_" + i );
						if( tempEdge == null )
						{
							tempEdge = new DNVEdge( tempNode, tempNode2, graph );
							tempEdge.setId( edgeId++ );
							graph.addNode( 0, tempEdge );
							edgeMap.put( i + "_" + index, tempEdge );
							edgeMap.put( index + "_" + i, tempEdge );
						}
					}
				}
			}
		}

		System.out.println( "Rewiring edges..." );
		for( int i = 0; i < numberOfNodes; i++ )
		{
			if( i % 10000 == 0 )
			{
				System.out.println( 2 + " : " + i );
			}
			tempNode = (DNVNode)graph.getNodeById( i );
			for( int j = i + 1; j < numberOfNodes; j++ )
			{
				if( Math.random() < rewireProbability )
				{
					tempEdge = edgeMap.remove( i + "_" + j );
					edgeMap.remove( j + "_" + i );
					if( tempEdge != null )
					{
						graph.removeNode( 0, tempEdge );
						int index = (int)( Math.random() * numberOfNodes );
						while( index == i )
							index = (int)( Math.random() * numberOfNodes );
						tempNode2 = (DNVNode)graph.getNodeById( index );
						tempEdge = new DNVEdge( tempNode, tempNode2, graph );
						tempEdge.setId( edgeId++ );
						graph.addNode( 0, tempEdge );
					}
				}
			}
		}

		graph.writeGraph( Settings.GRAPHS_PATH + "WS_" + numberOfNodes + "_" + graph.getEdges( 0 ).size() + "_" + rewireProbability + ".dnv" );
	}

	// add node
	/**
	 * Adds the node.
	 * 
	 * @param graph
	 *            the graph
	 * @param nodesByConnectivity
	 *            the nodes by connectivity
	 * @return the dNV node
	 */
	private static DNVNode addNode( DNVGraph graph, Map<Integer, List<DNVNode>> nodesByConnectivity )
	{
		DNVNode n = new DNVNode( graph );
		n.setPosition( (float)Math.random(), (float)Math.random() );
		n.setLevel( 0 );
		n.setColor( 0.5f, 0.5f, 1.0f );
		n.setLabel( RandomNames.getRandomName() );
		graph.addNode( 0, n );

		addNodeToConnectivityMap( nodesByConnectivity, n );

		return n;
	}

	/**
	 * Removes the node from connectivity map.
	 * 
	 * @param nodesByConnectivity
	 *            the nodes by connectivity
	 * @param n
	 *            the n
	 */
	private static void removeNodeFromConnectivityMap( Map<Integer, List<DNVNode>> nodesByConnectivity, DNVNode n )
	{
		List<DNVNode> nodeList = nodesByConnectivity.get( n.getConnectivity() );
		if( nodeList != null )
		{
			nodeList.remove( n );
			if( nodeList.size() == 0 )
				nodesByConnectivity.remove( n.getConnectivity() );
		}
		else
		{
			System.out.println( "No node with connectivity " + n.getConnectivity() );
		}
	}

	/**
	 * Adds the node to connectivity map.
	 * 
	 * @param nodesByConnectivity
	 *            the nodes by connectivity
	 * @param n
	 *            the n
	 */
	private static void addNodeToConnectivityMap( Map<Integer, List<DNVNode>> nodesByConnectivity, DNVNode n )
	{
		List<DNVNode> nodeList = nodesByConnectivity.get( n.getConnectivity() );
		if( nodeList == null )
		{
			nodeList = new ArrayList<DNVNode>();
		}

		nodeList.add( n );
		nodesByConnectivity.put( n.getConnectivity(), nodeList );
	}

	// add edge
	/**
	 * Adds the edge.
	 * 
	 * @param nFrom
	 *            the n from
	 * @param nTo
	 *            the n to
	 * @param graph
	 *            the graph
	 * @param totalConnectivity
	 *            the total connectivity
	 * @param nodesByConnectivity
	 *            the nodes by connectivity
	 * @return the double
	 */
	private static double addEdge( DNVNode nFrom, DNVNode nTo, DNVGraph graph, double totalConnectivity,
			Map<Integer, List<DNVNode>> nodesByConnectivity )
	{
		removeNodeFromConnectivityMap( nodesByConnectivity, nFrom );
		removeNodeFromConnectivityMap( nodesByConnectivity, nTo );
		DNVEdge e = new DNVEdge( nFrom, nTo, graph );
		e.setLevel( 0 );
		e.setRestingDistance( (float)Math.random() * 10.0f );
		graph.addNode( 0, e );

		addNodeToConnectivityMap( nodesByConnectivity, nFrom );
		addNodeToConnectivityMap( nodesByConnectivity, nTo );

		return totalConnectivity += 2;
	}

	
	public static DNVGraph generateSmallworldGraph( int nNodes, int nEdges, boolean writeCSV) throws IOException
	{
		return generateSmallworldGraph( nNodes, nEdges, writeCSV, new DNVGraph(), true );
	}
	
	/**
	 * Generate smallworld graph.
	 * 
	 * @param nNodes
	 *            the n nodes
	 * @param nEdges
	 *            the n edges
	 * @param writeCSV
	 *            the write csv
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static DNVGraph generateSmallworldGraph( int nNodes, int nEdges, boolean writeCSV, DNVGraph dnvGraph, boolean runLayout ) throws IOException
	{
		System.out.println( "Starting generation of a small world graph with " + nNodes + " nodes and " + nEdges + " edges." );
		// init
		String graphName = "smallworld";
		double totalConnectivity = 0;

		// dnv
		HashMap<Integer, List<DNVNode>> nodesByConnectivity = new HashMap<Integer, List<DNVNode>>();
		for( DNVNode node : dnvGraph.getNodes( 0 ) )
		{
			addNodeToConnectivityMap( nodesByConnectivity, node );
		}
		totalConnectivity = dnvGraph.getEdges( 0 ).size() * 2;
		int edgesPerNewNode = (int)Math.round( (double)nEdges / (double)nNodes );

		int startingChainSize = nEdges / nNodes + 1;
		if( startingChainSize >= dnvGraph.getGraphSize( 0 ) )
		{
			DNVNode currentNode;
			DNVNode previousNode = addNode( dnvGraph, nodesByConnectivity );
			int initialGraphSize = dnvGraph.getGraphSize( 0 );
			for( int i = 1; i < startingChainSize - initialGraphSize; i++ )
			{
				currentNode = addNode( dnvGraph, nodesByConnectivity );
				totalConnectivity = addEdge( previousNode, currentNode, dnvGraph, totalConnectivity, nodesByConnectivity );
				previousNode = currentNode;
			}
		}
		
		System.out.println( "Starting chain size " + startingChainSize );
		int edgesPerNewNode_assigned;
		int currentConnectivity = -1;
		List<DNVNode> tempList;
		boolean connected;
		Timer timer = new Timer( Timer.MILLISECONDS );
		timer.setStart();
		Timer edgeTimer = new Timer( Timer.MILLISECONDS );
		Timer keysTimer = new Timer( Timer.MILLISECONDS );
		int addedEdge = 0;
		DNVNode node1;
		DNVNode node2;
		for( int i = startingChainSize; dnvGraph.getGraphSize( 0 ) < nNodes; i++ )
		{
			if( i % 10000 == 0 )
			{
				timer.setEnd();
				System.gc();
				System.out.println( i + " : " + timer.getLastSegment( Timer.SECONDS ) + " seconds." );
				System.out.println( "Edgetimer : " + edgeTimer.getNumberOfSegments() + " Average time : " + edgeTimer.getAverageTime( Timer.SECONDS )
						+ " seconds." );
				System.out.println( "KeysTimer : " + keysTimer.getNumberOfSegments() + " Average time : " + keysTimer.getAverageTime( Timer.SECONDS )
						+ " seconds." );
				System.out.println( "Added Edge : " + addedEdge );
				edgeTimer.reset();
				keysTimer.reset();
				timer.setStart();
				addedEdge = 0;
			}
			node1 = addNode( dnvGraph, nodesByConnectivity );

			// add edges
			edgesPerNewNode_assigned = 0;
			edgeTimer.setStart();
			while( edgesPerNewNode_assigned < edgesPerNewNode )
			{
//				System.out.println( edgesPerNewNode_assigned + " < " + edgesPerNewNode );
				currentConnectivity = findCorrectConnectivity( totalConnectivity, nodesByConnectivity, node1, keysTimer );
//				System.out.println( currentConnectivity );
				// Get the list of nodes with given connectivity
				tempList = nodesByConnectivity.get( currentConnectivity );

//				System.out.println( tempList.size() );
				// add edge to a node of the given connectivity
				connected = false;
				for( int k = 0; k < tempList.size() && !connected; k++ )
				{
					node2 = tempList.get( k );

					if( !node1.equals( node2 ) && !node1.isNeighbor( node2 ) )
					{
						addedEdge++;
						totalConnectivity = addEdge( node1, node2, dnvGraph, totalConnectivity, nodesByConnectivity );

						edgesPerNewNode_assigned++;

						connected = true;
					}
				}
			}
			edgeTimer.setEnd();
		}
		
		// Add edges until we have the specified number of edges
		List<DNVNode> dnvNodes = dnvGraph.getNodes( 0 );
		while( totalConnectivity / 2.0 < nEdges )
		{
			System.out.println( totalConnectivity / 2.0 + " < " + nEdges );

			int i = 0;
			node1 = dnvNodes.get( i );
			while( node1.getConnectivity() > edgesPerNewNode * 2 )
				node1 = dnvNodes.get( i = ( i + 1 ) % dnvNodes.size() );
			connected = false;
			edgesPerNewNode_assigned = 0;
			while( !connected )
			{
				currentConnectivity = findCorrectConnectivity( totalConnectivity, nodesByConnectivity, node1, keysTimer );
				// Get the list of nodes with given connectivity
				tempList = nodesByConnectivity.get( currentConnectivity );

				// add edge to a node of the given connectivity
				connected = false;
				for( int k = 0; k < tempList.size() && !connected; k++ )
				{
					node2 = tempList.get( k );

					if( !node1.equals( node2 ) && node1.isNeighbor( node2 ) )
					{
						addedEdge++;
						totalConnectivity = addEdge( node1, node2, dnvGraph, totalConnectivity, nodesByConnectivity );

						edgesPerNewNode_assigned++;

						connected = true;
					}
				}
			}
		}

		nEdges = dnvGraph.getEdges( 0 ).size();
		nNodes = dnvGraph.getNodes( 0 ).size();
		if( runLayout )
		{
			new FruchtermanReingold().runLayout( 100, 100, dnvGraph, 0.1f, 0, false, false );
		}
		dnvGraph.writeGraph( Settings.GRAPHS_PATH + graphName + "_" + nNodes + "_" + nEdges + ".dnv" );

		// csv
		if( writeCSV )
		{
			FileWriter fstream = new FileWriter( Settings.GRAPHS_PATH + "touchgraph/" + graphName + "_" + nNodes + "_" + String.valueOf( nEdges )
					+ "_EDGES" + ".csv" );
			BufferedWriter out = new BufferedWriter( fstream );
			if( writeCSV )
				out.write( "id" + "\n" );

			DNVNode dnvNode;
			List<DNVNode> nodes = dnvGraph.getNodes( 0 );
			for( int i = 0; i < nodes.size(); i++ )
			{
				// dnv
				dnvNode = nodes.get( i );

				// csv
				out.write( dnvNode.getId() + "\n" );
			}

			// csv
			out.close();
			fstream = new FileWriter( Settings.GRAPHS_PATH + "touchgraph/" + graphName + "_" + nNodes + "_" + String.valueOf( nEdges ) + "_NODES"
					+ ".csv" );
			out = new BufferedWriter( fstream );
			if( writeCSV )
				out.write( "from,to" + "\n" );

			DNVEdge dnvEdge;
			List<DNVEdge> edges = dnvGraph.getEdges( 0 );
			for( int i = 0; i < edges.size(); i++ )
			{
				dnvEdge = edges.get( i );

				// csv
				out.write( dnvEdge.getFromId() + "," );
				out.write( dnvEdge.getToId() + "\n" );
			}

			// csv
			out.close();
		}

		System.out.println( "nNodes: " + nNodes );
		System.out.println( "nEdges: " + nEdges );
		
		return dnvGraph;
	}

	/**
	 * Find correct connectivity.
	 * 
	 * @param totalConnectivity
	 *            the total connectivity
	 * @param nodesByConnectivity
	 *            the nodes by connectivity
	 * @param node1
	 *            the node1
	 * @param keysTimer
	 *            the keys timer
	 * @return the int
	 */
	private static int findCorrectConnectivity( double totalConnectivity, HashMap<Integer, List<DNVNode>> nodesByConnectivity, DNVNode node1,
			Timer keysTimer )
	{
		Iterator<Integer> keys;
		List<DNVNode> tempList;
		double random;
		double currentProbability;
		double lastProbability;
		int currentConnectivity = -1;
		keys = nodesByConnectivity.keySet().iterator();
		random = Math.random();
		keysTimer.setStart();
		lastProbability = 0;
		int currentSize;
		// Find the correct connectivity
		while( keys.hasNext() )
		{
			currentConnectivity = keys.next();
			currentSize = nodesByConnectivity.get( currentConnectivity ).size();
			currentProbability = lastProbability + ( ( currentConnectivity * currentSize ) / totalConnectivity );
			if( random < currentProbability )
			{
				tempList = nodesByConnectivity.get( currentConnectivity );
				// More than one node so we can continue
				if( tempList.size() > 1 )
				{
					break;
				}

				// Node not trying to link to self so we can continue
				if( !tempList.get( 0 ).equals( node1 ) )
					break;
			}

			lastProbability = currentProbability;
		}
		keysTimer.setEnd();
		return currentConnectivity;
	}

	public static DNVGraph mergeGraphs( DNVGraph graph1, DNVGraph graph2 )
	{
		return mergeGraphs( 0, graph1, graph2 );
	}
	public static DNVGraph mergeGraphs( int level, DNVGraph graph1, DNVGraph graph2 )
	{
		DNVGraph newGraph = new DNVGraph();
		
		addAllNodesAndEdges( level, graph1, newGraph );
		addAllNodesAndEdges( level, graph2, newGraph );
		
		return newGraph;
	}

	private static void addAllNodesAndEdges( int level, DNVGraph oldGraph, DNVGraph newGraph )
	{
		for( DNVNode node : oldGraph.getNodes( level ) )
		{
			newGraph.addNode( level, node );
		}
		for( DNVEdge edge : oldGraph.getEdges( level ) )
		{
			newGraph.addEntity( level, edge );
		}
	}
	
	private static class GraphProperties
	{
		private String filename;
		private int numberOfClusters;
		private int minNodesPerCluster;
		private int edgesPerNode;
		private float clusterConnectionFactor;
		
		public static final int SMALL = 1;
		public static final int MEDIUM = 2;
		public static final int LARGE = 3;
		
		public GraphProperties( String filename, int type )
		{
			if( type == SMALL )
			{
				initialize( filename, 3, 10, 4, 1.3f );
			}
			if( type == MEDIUM )
			{
				initialize( filename, 4, 35, 4, 1.2f );
			}
			if( type == LARGE )
			{
				initialize( filename, 6, 60, 4, 1.05f );
			}
		}
		
//		public GraphProperties( String filename, int numberOfClusters, int minNodesPerCluster, int edgesPerNode, float clusterConnectionFactor )
//		{
//			initialize( filename, numberOfClusters, minNodesPerCluster, edgesPerNode, clusterConnectionFactor );
//		}

		private void initialize( String filename, int numberOfClusters, int minNodesPerCluster, int edgesPerNode, float clusterConnectionFactor )
		{
			this.filename = filename;
			this.numberOfClusters = numberOfClusters;
			this.minNodesPerCluster = minNodesPerCluster;
			this.edgesPerNode = edgesPerNode;
			this.clusterConnectionFactor = clusterConnectionFactor;
		}

		public String getFilename()
		{
			return filename;
		}

		public int getNumberOfClusters()
		{
			return numberOfClusters;
		}

		public int getMinNodesPerCluster()
		{
			return minNodesPerCluster;
		}

		public int getEdgesPerNode()
		{
			return edgesPerNode;
		}

		public float getClusterConnectionFactor()
		{
			return clusterConnectionFactor;
		}
	}

	public static void generateUserStudyGraphs() throws IOException
	{
		GraphProperties graphProperties[] = {
			// Graphs for first interaction method
			new GraphProperties( "graph1fam.dnv", GraphProperties.SMALL ),	
			new GraphProperties( "graph1small.dnv", GraphProperties.SMALL ),	
			new GraphProperties( "graph1medium.dnv", GraphProperties.MEDIUM ),	
			new GraphProperties( "graph1large.dnv", GraphProperties.LARGE ),

			// Graphs for second interaction method
			new GraphProperties( "graph2fam.dnv", GraphProperties.SMALL ),	
			new GraphProperties( "graph2small.dnv", GraphProperties.SMALL ),	
			new GraphProperties( "graph2medium.dnv", GraphProperties.MEDIUM ),	
			new GraphProperties( "graph2large.dnv", GraphProperties.LARGE ),

			// Graphs for third interaction method
			new GraphProperties( "graph3fam.dnv", GraphProperties.SMALL ),	
			new GraphProperties( "graph3small.dnv", GraphProperties.SMALL ),	
			new GraphProperties( "graph3medium.dnv", GraphProperties.MEDIUM ),	
			new GraphProperties( "graph3large.dnv", GraphProperties.LARGE ),

			// Graphs for fourth interaction method
			new GraphProperties( "graph4fam.dnv", GraphProperties.SMALL ), 
			new GraphProperties( "graph4small.dnv", GraphProperties.SMALL ),
			new GraphProperties( "graph4medium.dnv", GraphProperties.MEDIUM ),
			new GraphProperties( "graph4large.dnv", GraphProperties.LARGE ),
		};
		
		for( int i = 0; i < graphProperties.length; i++ )
		{
			GraphProperties gp = graphProperties[i];
			String path = Settings.GRAPHS_PATH; // + "/UserStudy/testGraphs/";
			if( !( new File( path + gp.getFilename() ) ).exists() )
			{
				DNVGraph entireGraph = new DNVGraph();
				DNVGraph tempGraph;
				for( int j = 0; j < gp.getNumberOfClusters(); j++ )
				{
					tempGraph = new DNVGraph();
					tempGraph.getIdGenerator().setNextId( entireGraph.getIdGenerator().getNextId() );
					int numberOfNodes = (j+1) * gp.getMinNodesPerCluster();
					int numberOfEdges = numberOfNodes * gp.getEdgesPerNode();
					tempGraph = generateSmallworldGraph( numberOfNodes, numberOfEdges, false, tempGraph, true );
					entireGraph = mergeGraphs( tempGraph, entireGraph );
					System.out.println( "SIZE: " + entireGraph.getGraphSize( 0 ) );
				}
				int totalNumberOfNodes = Math.round(entireGraph.getGraphSize( 0 ) * gp.getClusterConnectionFactor() );
				entireGraph = generateSmallworldGraph( totalNumberOfNodes, totalNumberOfNodes*2, false, entireGraph, true );
				entireGraph.writeGraph( path + gp.getFilename() );
				HashMap<Integer, Integer> histogram = GenerateHistogramOfConnectivity.generateHistogram( entireGraph, 0 );
				GenerateHistogramOfConnectivity.writeHistogram( path + "/histograms/" + gp.getFilename() + ".csv", false, histogram );
			}
			else
			{
				System.out.println( path + gp.getFilename() + " already exists. Delete first if you want to replace it." );
			}
		}
	}
	
	public static void setLabelAsBbId( DNVGraph graph )
	{
		for( DNVNode node : graph.getNodes( 0 ) )
		{
			node.setBbId( node.getLabel() );
		}
	}
	
	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void main( String args[] ) throws IOException
	{
		// System.out.println(10);
		// generateRandomGraphToFile( 10, 13 );
		// System.out.println(100);
		// generateRandomGraphToFile( 100, 130 );
		// System.out.println(1000);
		// generateRandomGraphToFile( 1000, 1300 );
		// for( int i = 1; i < 20; i++ )
		// {
		// System.out.println(10000 * i);
		// generateRandomGraphToFile( 10000 * i, 13000 * i );
		// }

		// generateWattsAndStrogatzGraphToFile( 10000, 0.5 );
		// generateWattsAndStrogatzGraphToFile( 100000, 0.5 );
		// generateWattsAndStrogatzGraphToFile( 1000000, 0.5 );
		//		
		// generateSmallworldGraph( 10, 20, true );
		// generateSmallworldGraph( 100, 200, true );
//		generateSmallworldGraph( 1000, 2000, true );
//		generateSmallworldGraph( 10000, 20000, true );
		// generateSmallworldGraph( 100000, 200000, true );
		// generateSmallworldGraph( 1000000, 2000000, true );

		GraphsPathFilter.init();
//		DNVGraph newGraph = new DNVGraph();
//		for( int i = 0; i < 3; i++ )
//		{
//			List<DNVNode> nodes = new ArrayList<DNVNode>();
//			for( int j = 0; j < 5; j++ )
//			{
//				DNVNode newNode = new DNVNode( new Vector2D( (float)Math.random(), (float)Math.random() ), "" + (i+j), newGraph );
//				newGraph.addNode( 0, newNode );
//				for( DNVNode oldNode : nodes )
//				{
//					DNVEdge newEdge = new DNVEdge( newNode, oldNode, newGraph );
//					newGraph.addEntity( 0, newEdge );
//				}
//				nodes.add( newNode );
//			}
//		}
		
		generateUserStudyGraphs();
//		GraphProperties graphProperties[] = {
//				// Graphs for first interaction method
//				new GraphProperties( "graph1fam.dnv", GraphProperties.SMALL ),	
//				new GraphProperties( "graph1small.dnv", GraphProperties.SMALL ),	
//				new GraphProperties( "graph1medium.dnv", GraphProperties.MEDIUM ),	
//				new GraphProperties( "graph1large.dnv", GraphProperties.LARGE ),
//
//				// Graphs for second interaction method
//				new GraphProperties( "graph2fam.dnv", GraphProperties.SMALL ),	
//				new GraphProperties( "graph2small.dnv", GraphProperties.SMALL ),	
//				new GraphProperties( "graph2medium.dnv", GraphProperties.MEDIUM ),	
//				new GraphProperties( "graph2large.dnv", GraphProperties.LARGE ),
//
//				// Graphs for third interaction method
//				new GraphProperties( "graph3fam.dnv", GraphProperties.SMALL ),	
//				new GraphProperties( "graph3small.dnv", GraphProperties.SMALL ),	
//				new GraphProperties( "graph3medium.dnv", GraphProperties.MEDIUM ),	
//				new GraphProperties( "graph3large.dnv", GraphProperties.LARGE ),
//
//				// Graphs for fourth interaction method
//				new GraphProperties( "graph4fam.dnv", GraphProperties.SMALL ),	
//				new GraphProperties( "graph4small.dnv", GraphProperties.SMALL ),	
//				new GraphProperties( "graph4medium.dnv", GraphProperties.MEDIUM ),	
//				new GraphProperties( "graph4large.dnv", GraphProperties.LARGE ),
//			};
//
//		for( GraphProperties gp : graphProperties )
//		{
//			DNVGraph graph = new DNVGraph(Settings.GRAPHS_PATH + "UserStudy/testGraphs/" + gp.getFilename() );
//			setLabelAsBbId( graph );
//			graph.writeGraph( Settings.GRAPHS_PATH + "UserStudy/testGraphs/" + gp.getFilename() );
//		}
//		generateSmallworldGraph( 267, 2041, false, newGraph );
		System.out.println( "Finished" );
//		GenerateHistogramOfConnectivity.generateHistogram( Settings.GRAPHS_PATH + "smallworld_267_2079.dnv", false, 0 );
		// System.out.println( "Generating histograms" );
		// boolean includeZeros = false;
		// GenerateHistogramOfConnectivity.generateHistogram(
		// Settings.GRAPHS_PATH + "smallworld_10_20.dnv", includeZeros );
		// GenerateHistogramOfConnectivity.generateHistogram(
		// Settings.GRAPHS_PATH + "smallworld_100_200.dnv", includeZeros);
		// GenerateHistogramOfConnectivity.generateHistogram(
		// Settings.GRAPHS_PATH + "smallworld_1000_2000.dnv", includeZeros);
		// GenerateHistogramOfConnectivity.generateHistogram(
		// Settings.GRAPHS_PATH + "smallworld_10000_20000.dnv", includeZeros);
		// GenerateHistogramOfConnectivity.generateHistogram(
		// Settings.GRAPHS_PATH + "smallworld_100000_200000.dnv", includeZeros);
		// GenerateHistogramOfConnectivity.generateHistogram(
		// Settings.GRAPHS_PATH + "smallworld_1000000_2000000.dnv",
		// includeZeros);
	}

}
