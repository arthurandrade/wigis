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

package net.wigis.graph.dnv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.wigis.graph.PaintBean;
import net.wigis.graph.dnv.animations.Animation;
import net.wigis.graph.dnv.geometry.Geometric;
import net.wigis.graph.dnv.utilities.GraphFunctions;
import net.wigis.graph.dnv.utilities.Timer;
import net.wigis.graph.dnv.utilities.Vector2D;
import blackbook.ejb.client.visualization.proxy.EdgeDecorator;
import blackbook.ejb.client.visualization.proxy.ResourceDecorator;

// TODO: Auto-generated Javadoc
/**
 * The Class DNVGraph.
 * 
 * @author Brynjar Gretarssons
 */
public class DNVGraph implements Serializable
{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -6506405005877102440L;

	/** The id generator. */
	private IDGenerator idGenerator = new IDGenerator();

	// Integer is the node id
	/** The all nodes by id. */
	private Map<Integer, DNVEntity> allNodesById = new HashMap<Integer, DNVEntity>();

	/** The all nodes by bb id. */
	private Map<String, DNVEntity> allNodesByBbId = new HashMap<String, DNVEntity>();

	// Integer is the level and the Map contains all active nodes at the given
	// level, where the Integer is the node id
	/** The active nodes. */
	private Map<Integer, Map<Integer, DNVNode>> activeNodes = new HashMap<Integer, Map<Integer, DNVNode>>();

	// Integer is the level and the Map contains all selected nodes at the given
	// level, where the Integer is the node id
	/** The selected nodes. */
	private Map<Integer, Map<Integer, DNVNode>> selectedNodes = new HashMap<Integer, Map<Integer, DNVNode>>();

	// Integer is the level and the Map contains all selected nodes at the given
	// level, where the Integer is the node id
	/** The selected edges. */
	private Map<Integer, Map<Integer, DNVEdge>> selectedEdges = new HashMap<Integer, Map<Integer, DNVEdge>>();

	// Integer is the level and the Map contains all selected nodes at the given
	// level, where the Integer is the node id
	/** The selected nodes. */
	private Map<Integer, Map<Integer, DNVNode>> visibleNodes = new HashMap<Integer, Map<Integer, DNVNode>>();

	// Integer is the level and the Map contains all selected nodes at the given
	// level, where the Integer is the node id
	/** The selected edges. */
	private Map<Integer, Map<Integer, DNVEdge>> visibleEdges = new HashMap<Integer, Map<Integer, DNVEdge>>();

	// Integer is the level and the inner Map contains the nodes identified by
	// their id at the given level
	/** The nodes. */
	// Alex - changed to protected to be able to get nodes to Flash
	protected Map<Integer, Map<Integer, DNVNode>> nodes = new HashMap<Integer, Map<Integer, DNVNode>>();
	// Integer is the level and the inner Map contains the edges identified by
	// their id at the given level
	/** The edges. */
	private Map<Integer, Map<Integer, DNVEdge>> edges = new HashMap<Integer, Map<Integer, DNVEdge>>();
	// Integer is the level and the inner Map contains the nodes and edges
	// identified by their id at the given level
	/** The nodes and edges. */
	private Map<Integer, Map<Integer, DNVEntity>> nodesAndEdges = new HashMap<Integer, Map<Integer, DNVEntity>>();

	/** The interpolation nodes. */
	private Map<Integer, Map<Integer, DNVNode>> interpolationNodes = new HashMap<Integer, Map<Integer, DNVNode>>();

	/** The geometric objects. */
	private Map<Integer, List<Geometric>> geometricObjects = new HashMap<Integer, List<Geometric>>();

	// First integer is the level, the string is the type, inner integer is the
	// node id
	/** The nodes by type. */
	private Map<Integer, Map<String, Map<Integer, DNVEntity>>> nodesByType = new HashMap<Integer, Map<String, Map<Integer, DNVEntity>>>();

	/** The nodes by time. */
	private Map<Integer, Map<Long, Map<Integer, DNVEntity>>> nodesByTime = new HashMap<Integer, Map<Long, Map<Integer, DNVEntity>>>();

	/** The maximum pairwise shortest paths. */
	private Map<Integer, Integer> maximumPairwiseShortestPaths = new HashMap<Integer, Integer>();

	/** The must draw labels. */
	private Map<Integer, Map<Integer, DNVEntity>> mustDrawLabels = new HashMap<Integer, Map<Integer, DNVEntity>>();

	/** The properties. */
	private Map<String, String> properties = new HashMap<String, String>();
	
	private List<Animation> animations = new ArrayList<Animation>();

	/** The center of gravity. */
	private Vector2D centerOfGravity = new Vector2D( 0, 0 );

	/** The filename. */
	private String filename;

	/** The name. */
	private String name;

	/** The updating. */
	private boolean updating = false;

	/** The max label length. */
	private int maxLabelLength = -1;

	/** The paint bean. */
	private PaintBean paintBean = null;

	public static void p(Object o)
	{
		System.out.println(o);
	}
	
	public static void pe(Object o)
	{
		System.err.println(o);
	}
	
	/**
	 * Logger.
	 */
	// // private static Log logger = LogFactory.getLog( DNVGraph.class );

	public DNVGraph()
	{}

	/**
	 * Sets initial capacity of lists and maps according to the given
	 * parameters.
	 * 
	 * @param filename
	 *            the filename
	 */
	public DNVGraph( String filename )
	{
		loadGraphFromFile( filename );
	}

	/**
	 * Gets the active nodes.
	 * 
	 * @param level
	 *            the level
	 * @return the active nodes
	 */
	public Collection<DNVNode> getActiveNodes( Integer level )
	{
		Map<Integer, DNVNode> map = activeNodes.get( level );
		if( map == null )
		{
			map = new HashMap<Integer, DNVNode>();
			activeNodes.put( level, map );
		}

		return map.values();
	}

	/**
	 * Sets the all nodes active.
	 */
	public void setAllNodesActive()
	{
		int maxLevel = getMaxLevel();
		for( Integer i = 0; i <= maxLevel; i++ )
		{
			setAllNodesActive( i );
		}
	}

	/**
	 * Sets the all nodes active.
	 * 
	 * @param level
	 *            the new all nodes active
	 */
	public void setAllNodesActive( Integer level )
	{
		Map<Integer, DNVNode> nodeList = nodes.get( level );
		if( nodeList != null )
		{
			Iterator<DNVNode> nodesAtLevel = nodeList.values().iterator();
			Map<Integer, DNVNode> hash = activeNodes.get( level );
			if( hash == null )
			{
				hash = new HashMap<Integer, DNVNode>();
				activeNodes.put( level, hash );
			}

			DNVNode tempNode;
			while( nodesAtLevel.hasNext() )
			{
				tempNode = nodesAtLevel.next();
				hash.put( tempNode.getId(), tempNode );
			}
		}
	}

	/**
	 * Sets the node inactive.
	 * 
	 * @param level
	 *            the level
	 * @param id
	 *            the id
	 */
	public void setNodeInactive( Integer level, Integer id )
	{
		try
		{
			if( activeNodes.get( level ).size() > 100 )
				activeNodes.get( level ).remove( id );
		}
		catch( NullPointerException ignore )
		{}
	}

	/**
	 * Sets the node active.
	 * 
	 * @param level
	 *            the level
	 * @param id
	 *            the id
	 * @param node
	 *            the node
	 */
	public void setNodeActive( Integer level, Integer id, DNVNode node )
	{
		Map<Integer, DNVNode> map = activeNodes.get( level );
		if( map == null )
			map = new HashMap<Integer, DNVNode>();
		map.put( id, node );
		activeNodes.put( level, map );
	}

	/**
	 * Checks if is node active.
	 * 
	 * @param level
	 *            the level
	 * @param id
	 *            the id
	 * @return true, if is node active
	 */
	public boolean isNodeActive( Integer level, Integer id )
	{
		try
		{
			if( activeNodes.get( level ).get( id ) != null )
				return true;
		}
		catch( NullPointerException npe )
		{
			return false;
		}

		return false;
	}

	/**
	 * Clear all.
	 */
	private void clearAll()
	{
		allNodesById.clear();
		nodes.clear();
		edges.clear();
		nodesAndEdges.clear();
		activeNodes.clear();
	}

	/** The current level. */
	Integer currentLevel = null;

	/** The current node. */
	DNVNode currentNode = null;

	/** The current edge. */
	DNVEdge currentEdge = null;

	/**
	 * Load graph from file.
	 * 
	 * @param filename
	 *            the filename
	 */
	public void loadGraphFromFile( String filename )
	{
		clearAll();
		this.filename = filename;
		File file = new File( filename );
		if( file.exists() )
		{
			String line;
			FileReader fr;
			try
			{
				fr = new FileReader( file );
				BufferedReader br = new BufferedReader( fr );
				line = br.readLine();
				while( line != null )
				{
					handleLine( line );
					line = br.readLine();
				}
				br.close();
				fr.close();
			}
			catch( FileNotFoundException e )
			{
				e.printStackTrace();
			}
			catch( IOException ioe )
			{
				ioe.printStackTrace();
			}
		}
	}

	/**
	 * Handle line.
	 * 
	 * @param line
	 *            the line
	 */
	private void handleLine( String line )
	{
		if( line.indexOf( "<DNVGRAPH>" ) != -1 )
		{
			// ignore
		}
		else if( line.indexOf( "<Level" ) != -1 )
		{
			handleLevel( line );
		}
		else if( line.indexOf( "<DNVNODE" ) != -1 )
		{
			handleNodeStart( line );
		}
		else if( line.indexOf( "</DNVNODE>" ) != -1 )
		{
			handleNodeEnd();
		}
		else if( line.indexOf( "<DNVEDGE" ) != -1 )
		{
			handleEdgeStart( line );
		}
		else if( line.indexOf( "</DNVEDGE>" ) != -1 )
		{
			handleEdgeEnd();
		}
		else if( line.indexOf( "</Level>" ) != -1 )
		{
			handleEndLevel();
		}
		else if( line.indexOf( "<SUBNODE" ) != -1 )
		{
			handleSubNode( line );
		}
		else if( line.indexOf( "</DNVGRAPH>" ) != -1 )
		{
			handleEndGraph();
		}
		else if( line.indexOf( "<NODEPROPERTY" ) != -1 )
		{
			handleNodeProperty( line );
		}
		else if( line.indexOf( "<EDGEPROPERTY" ) != -1 )
		{
			handleEdgeProperty( line );
		}
	}

	/**
	 * Handle edge property.
	 * 
	 * @param line
	 *            the line
	 */
	private void handleEdgeProperty( String line )
	{
		line = line.substring( line.indexOf( "Key=\"" ) + 5 );
		String key = line.substring( 0, line.indexOf( "\"" ) );
		line = line.substring( line.indexOf( "Value=\"" ) + 7 );
		String value = line.substring( 0, line.indexOf( "\"" ) );
		currentEdge.setProperty( key, value );
	}

	/**
	 * Handle node property.
	 * 
	 * @param line
	 *            the line
	 */
	private void handleNodeProperty( String line )
	{
		line = line.substring( line.indexOf( "Key=\"" ) + 5 );
		String key = line.substring( 0, line.indexOf( "\"" ) );
		line = line.substring( line.indexOf( "Value=\"" ) + 7 );
		String value = line.substring( 0, line.indexOf( "\"" ) );
		currentNode.setProperty( key, value );
		if( line.contains( "Extractable=\"" ) )
		{
			line = line.substring( line.indexOf( "Extractable=\"" ) + 13 );
			String bool = line.substring( 0, line.indexOf( "\"" ) );
			if( bool.equalsIgnoreCase( "True" ) )
			{
				currentNode.setPropertyExtractable( key );
			}
		}
	}

	/**
	 * Handle end graph.
	 */
	private void handleEndGraph()
	{
		Iterator<DNVEntity> allNodes = allNodesById.values().iterator();
		DNVEntity tempEntity;
		DNVNode tempNode;
		Iterator<Integer> ids;
		DNVNode tempNode2;
		Integer id;
		while( allNodes.hasNext() )
		{
			tempEntity = allNodes.next();
			if( tempEntity instanceof DNVNode )
			{
				tempNode = (DNVNode)tempEntity;
				ids = tempNode.getSubNodeIds().iterator();
				while( ids.hasNext() )
				{
					id = ids.next();
					tempNode2 = (DNVNode)allNodesById.get( id );
					if( tempNode2 == null )
					{
						System.out.println( "Node with id=" + id + " does not exist." );
					}
					else
					{
						if( tempNode.getFirstChild() == null )
							tempNode.setFirstChild( tempNode2 );
						tempNode.addSubNode( tempNode2 );
						tempNode2.setParentNode( tempNode );
					}
				}

				// This list of ids only needed while loading.
				// After loading it is maintained in a class called SubGraph.
				tempNode.clearSubNodeIds();
			}
		}

	}

	/**
	 * Handle end level.
	 */
	private void handleEndLevel()
	{
		Iterator<DNVEdge> currentEdges = edges.get( currentLevel ).values().iterator();
		DNVEdge tempEdge;
		DNVNode from;
		DNVNode to;
		while( currentEdges.hasNext() )
		{
			tempEdge = currentEdges.next();
			try
			{
				from = (DNVNode)allNodesById.get( Integer.valueOf( tempEdge.getFromId() ) );
				to = (DNVNode)allNodesById.get( Integer.valueOf( tempEdge.getToId() ) );
				tempEdge.setFrom( from );
				tempEdge.setTo( to );
				from.addFromEdge( tempEdge );
				to.addToEdge( tempEdge );
			}
			catch( ClassCastException e )
			{
				System.out.println( "From id: " + tempEdge.getFromId() + " To id: " + tempEdge.getToId() );
				e.printStackTrace();
			}
		}
	}

	/**
	 * Handle edge start.
	 * 
	 * @param line
	 *            the line
	 */
	private void handleEdgeStart( String line )
	{
		DNVEdge newEdge = new DNVEdge( line, this );
		currentEdge = newEdge;
		if( line.trim().endsWith( "/>" ) )
		{
			handleEdgeEnd();
		}
	}

	/**
	 * Handle edge end.
	 */
	private void handleEdgeEnd()
	{
		addNode( currentLevel, currentEdge );
	}

	/**
	 * Handle node start.
	 * 
	 * @param line
	 *            the line
	 */
	private void handleNodeStart( String line )
	{
		DNVNode newNode = new DNVNode( line, this );
		/*
		 * nodes.get( currentLevel ).put( newNode.getId(), newNode );
		 * nodesAndEdges.get( currentLevel ).put( newNode.getId(), newNode );
		 * Integer currentId = Integer.valueOf( newNode.getId() );
		 * allNodesById.put( currentId, newNode );
		 */
		currentNode = newNode;

		if( line.trim().endsWith( "/>" ) )
		{
			handleNodeEnd();
		}
	}

	/**
	 * Handle node end.
	 */
	private void handleNodeEnd()
	{
		addNode( currentLevel, currentNode );
	}

	/**
	 * Handle level.
	 * 
	 * @param line
	 *            the line
	 */
	private void handleLevel( String line )
	{
		if( line.contains( "value=\"" ) )
		{
			line = line.substring( line.indexOf( "value=\"" ) + 7 );
			String levelStr = line.substring( 0, line.indexOf( "\"" ) );
			currentLevel = Integer.valueOf( levelStr );
		}
		else
		{
			currentLevel = 0;
		}

		String temp = "maximumPairwiseShortestPath=\"";
		if( line.contains( temp ) )
		{
			line = line.substring( line.indexOf( temp ) + temp.length() );
			String mpspStr = line.substring( 0, line.indexOf( "\"" ) );
			int mpsp = Integer.parseInt( mpspStr );
			maximumPairwiseShortestPaths.put( currentLevel, mpsp );
		}

		nodes.put( currentLevel, new HashMap<Integer, DNVNode>() );
		edges.put( currentLevel, new HashMap<Integer, DNVEdge>() );
		nodesAndEdges.put( currentLevel, new HashMap<Integer, DNVEntity>() );
	}

	/**
	 * Handle sub node.
	 * 
	 * @param line
	 *            the line
	 */
	private void handleSubNode( String line )
	{
		line = line.substring( line.indexOf( "id=\"" ) + 4 );
		Integer id = Integer.valueOf( line.substring( 0, line.indexOf( "\"" ) ) );
		currentNode.addSubNodeId( id );
	}

	/**
	 * Removes the isolated nodes.
	 * 
	 * @return the list
	 */
	public List<DNVNode> removeIsolatedNodes()
	{
		return removeIsolatedNodes( 0 );
	}

	/**
	 * Removes the isolated nodes.
	 * 
	 * @param level
	 *            the level
	 * @return the list
	 */
	public List<DNVNode> removeIsolatedNodes( int level )
	{
		List<DNVNode> removedNodes = new ArrayList<DNVNode>();
		List<DNVNode> nodeList = new ArrayList<DNVNode>( getVisibleNodes( level ).values() );
		DNVNode tempNode;
		for( int i = 0; i < nodeList.size(); i++ )
		{
			tempNode = nodeList.get( i );
			if( tempNode.getNeighbors( true ).size() == 0 )
			{
				removeNode( level, tempNode );
				removedNodes.add( tempNode );
			}
		}

		return removedNodes;
	}

	/**
	 * Removes the isolated nodes by type.
	 * 
	 * @param level
	 *            the level
	 * @param type
	 *            the type
	 */
	public void removeIsolatedNodesByType( int level, String type )
	{
		List<DNVNode> nodeList = getNodes( level );
		DNVNode tempNode;
		for( int i = 0; i < nodeList.size(); i++ )
		{
			tempNode = nodeList.get( i );
			if( tempNode.getType().equals( type ) )
			{
				if( tempNode.getNeighbors().size() == 0 )
				{
					removeNode( level, tempNode );
				}
			}
		}
	}

	/**
	 * Serialize.
	 * 
	 * @param writer
	 *            the writer
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void serialize( Writer writer ) throws IOException
	{
		Iterator<Integer> keys = nodes.keySet().iterator();
		Integer level;
		List<DNVEntity> nodeList;
		DNVEntity tempEntity;
		writer.write( "<DNVGRAPH>\n" );
		// for( int i = maxLevel.intValue(); i >= 0; i-- )
		while( keys.hasNext() )
		{
			// level = Integer.valueOf( i );
			level = keys.next();
			int numberOfNodes = 0;
			int numberOfEdges = 0;
			try
			{
				numberOfNodes = nodes.get( level ).size();
			}
			catch( NullPointerException npe )
			{}
			try
			{
				numberOfEdges = edges.get( level ).size();
			}
			catch( NullPointerException npe )
			{}
			writer.write( "\t<Level value=\"" + level + "\" numberOfNodes=\"" + numberOfNodes + "\" numberOfEdges=\"" + numberOfEdges + "\"" );
			Integer mpsp = maximumPairwiseShortestPaths.get( level );
			if( mpsp != null )
			{
				writer.write( " maximumPairwiseShortestPath=\"" + maximumPairwiseShortestPaths.get( level ) + "\">\n" );
			}
			else
			{
				writer.write( ">\n" );
			}
			nodeList = GraphFunctions.convertCollectionToList( nodesAndEdges.get( level ).values() );

			for( int j = 0; j < nodeList.size(); j++ )
			{
				tempEntity = nodeList.get( j );
				writer.write( tempEntity.serialize() );
			}
			writer.write( "\t</Level>\n" );
		}
		writer.write( "</DNVGRAPH>\n" );
	}

	/**
	 * Serialize.
	 * 
	 * @return the string
	 */
	public String serialize()
	{
		StringBuilder value = new StringBuilder( 10000 );
		Iterator<Integer> keys = nodes.keySet().iterator();
		Integer level;
		List<DNVEntity> nodeList;
		DNVEntity tempEntity;
		value.append( "<DNVGRAPH>\n" );
		// for( int i = maxLevel.intValue(); i >= 0; i-- )
		while( keys.hasNext() )
		{
			// level = Integer.valueOf( i );
			level = keys.next();
			int numberOfNodes = 0;
			int numberOfEdges = 0;
			try
			{
				numberOfNodes = nodes.get( level ).size();
			}
			catch( NullPointerException npe )
			{}
			try
			{
				numberOfEdges = edges.get( level ).size();
			}
			catch( NullPointerException npe )
			{}
			value.append( "\t<Level value=\"" + level + "\" numberOfNodes=\"" + numberOfNodes + "\" numberOfEdges=\"" + numberOfEdges + "\">\n" );
			nodeList = GraphFunctions.convertCollectionToList( nodesAndEdges.get( level ).values() );

			for( int j = 0; j < nodeList.size(); j++ )
			{
				tempEntity = nodeList.get( j );
				value.append( tempEntity.serialize() );
			}
			value.append( "\t</Level>\n" );
		}
		value.append( "</DNVGRAPH>\n" );

		return value.toString();
	}

	/**
	 * Gets the max level.
	 * 
	 * @return the max level
	 */
	public Integer getMaxLevel()
	{
		Iterator<Integer> i = nodes.keySet().iterator();
		Integer max = 0;
		Integer temp;
		while( i.hasNext() )
		{
			temp = i.next();
			if( temp > max && nodes.get( temp ).size() >= Math.max( getNumberOfDisconnectedNodes( temp ), 2 ) )
				max = temp;
		}

		return max;
	}

	/**
	 * Gets the node by id.
	 * 
	 * @param id
	 *            the id
	 * @return the node by id
	 */
	public DNVEntity getNodeById( Integer id )
	{
		return allNodesById.get( id );
	}

	/**
	 * Gets the node by bb id.
	 * 
	 * @param id
	 *            the id
	 * @return the node by bb id
	 */
	public DNVEntity getNodeByBbId( String id )
	{
		return allNodesByBbId.get( id );
	}
	
	//=====================
	// alex
	//=====================
	public DNVEntity getNodeByProperty(String label)
	{
		for (DNVNode n : getNodes())
			if (n.getLabel().equals(label))
				return n;
		
		return null;
	}
	
	//=====================

	/**
	 * Adds the entity.
	 * 
	 * @param level
	 *            the level
	 * @param entity
	 *            the entity
	 */
	public void addEntity( Integer level, DNVEntity entity )
	{
		addNode( level, entity );
	}

	/**
	 * This method should be used both to add nodes and edges.
	 * 
	 * @param level
	 *            the level
	 * @param entity
	 *            the entity
	 */
	public void addNode( Integer level, DNVEntity entity )
	{
		synchronized( this )
		{
			boolean selected = entity.isSelected();
			boolean visible = entity.isVisible();
			entity.setGraph( this );
			entity.setLevel( level );
			if( idGenerator.getCurrentId() <= entity.getId() )
			{
				idGenerator.setNextId( entity.getId() + 1 );
			}
			if( entity instanceof DNVEdge )
			{
				Map<Integer, DNVEdge> edgeList = edges.get( level );
				if( edgeList == null )
					edgeList = new HashMap<Integer, DNVEdge>();

				DNVEdge edge = (DNVEdge)entity;
				DNVNode from = edge.getFrom();
				if( from != null )
				{
					from.addFromEdge( edge );
				}
				DNVNode to = edge.getTo();
				if( to != null )
				{
					to.addToEdge( edge );
				}

				if( edgeList.get( entity.getId() ) == null )
				{
					edgeList.put( entity.getId(), (DNVEdge)entity );
					edges.put( level, edgeList );
				}
				else
				{
					System.out.println( "Edge with id " + entity.getId() + " already exists in graph." );
				}
			}
			else
			{
				Map<Integer, DNVNode> nodeList = nodes.get( level );
				if( nodeList == null )
					nodeList = new HashMap<Integer, DNVNode>();

				if( nodeList.get( entity.getId() ) != null )
				{
					entity.setId( idGenerator.getNextId() );
				}
				if( nodeList.get( entity.getId() ) == null )
				{
					nodeList.put( entity.getId(), (DNVNode)entity );
					nodes.put( level, nodeList );
				}
				else
				{
					System.out.println( "Node with id " + entity.getId() + " already exists in graph." );
				}
			}

			// Add node by type
			if( entity.getType() != null && !entity.getType().equals( "" ) )
			{
				Map<String, Map<Integer, DNVEntity>> types = nodesByType.get( level );
				if( types == null )
				{
					types = new HashMap<String, Map<Integer, DNVEntity>>();
					nodesByType.put( level, types );
				}
				Map<Integer, DNVEntity> entityList = types.get( entity.getType() );
				if( entityList == null )
				{
					entityList = new HashMap<Integer, DNVEntity>();
					types.put( entity.getType(), entityList );
				}
				entityList.put( entity.getId(), entity );
			}

			// Add node by time
			if( entity.getProperty( "time" ) != null && !entity.getProperty( "time" ).equals( "" ) )
			{
				Map<Long, Map<Integer, DNVEntity>> times = nodesByTime.get( level );
				if( times == null )
				{
					times = new HashMap<Long, Map<Integer, DNVEntity>>();
					nodesByTime.put( level, times );
				}
				Map<Integer, DNVEntity> entityList = times.get( entity.getProperty( "time" ) );
				if( entityList == null )
				{
					entityList = new HashMap<Integer, DNVEntity>();
					times.put( Long.parseLong( entity.getProperty( "time" ) ), entityList );
				}
				entityList.put( entity.getId(), entity );
			}

			Map<Integer, DNVEntity> nodeList = nodesAndEdges.get( level );
			if( nodeList == null )
				nodeList = new HashMap<Integer, DNVEntity>();

			if( nodeList.get( entity.getId() ) == null )
			{
				nodeList.put( entity.getId(), entity );
				nodesAndEdges.put( level, nodeList );
			}

			allNodesById.put( Integer.valueOf( entity.getId() ), entity );
			allNodesByBbId.put( entity.getBbId(), entity );
			
			entity.setSelected( selected );
			entity.setVisible( visible );
		}
	}

	/**
	 * Removes the node.
	 * 
	 * @param entity
	 *            the entity
	 */
	public void removeNode( DNVEntity entity )
	{
		removeNode( entity.getLevel(), entity );
	}

	/**
	 * Removes the node.
	 * 
	 * @param level
	 *            the level
	 * @param entity
	 *            the entity
	 */
	public void removeNode( Integer level, DNVEntity entity )
	{
		synchronized( this )
		{
			if( entity instanceof DNVNode )
			{
				Map<Integer, DNVNode> nodeList = nodes.get( level );
				if( nodeList != null )
				{
					nodeList.remove( entity.getId() );
				}
				
				nodeList = getSelectedNodes( level );
				nodeList.remove( entity.getId() );
				
				nodeList = getVisibleNodes( level );
				nodeList.remove( entity.getId() );
				
//				((DNVNode)entity).tellNeighborsRemoved();
				
				// Also remove all edges connected to this node
				List<DNVEntity> edges = new ArrayList<DNVEntity>();
				edges.addAll( ( (DNVNode)entity ).getFromEdges() );
				edges.addAll( ( (DNVNode)entity ).getToEdges() );
				removeNodes( edges );
			}
			else
			{
				Map<Integer, DNVEdge> nodeList = edges.get( level );
				if( nodeList != null )
				{
					nodeList.remove( entity.getId() );
				}
				
				nodeList = getSelectedEdges( level );
				nodeList.remove( entity.getId() );
				
				nodeList = getVisibleEdges( level );
				nodeList.remove( entity.getId() );
				
				DNVEdge tempEdge = (DNVEdge)entity;
				DNVNode fromNode = tempEdge.getFrom();
				DNVNode toNode = tempEdge.getTo();
				fromNode.removeFromEdge( tempEdge );
				toNode.removeToEdge( tempEdge );
			}

			Map<String, Map<Integer, DNVEntity>> types = nodesByType.get( level );
			if( types != null )
			{
				Map<Integer, DNVEntity> entityList = types.get( entity.getType() );
				if( entityList != null )
				{
					entityList.remove( entity.getId() );
				}
			}

			Map<Long, Map<Integer, DNVEntity>> times = nodesByTime.get( level );
			if( times != null )
			{
				if( entity.hasProperty( "time" ) )
				{
					Map<Integer, DNVEntity> entityList = times.get( Long.parseLong( entity.getProperty( "time" ) ) );
					if( entityList != null )
					{
						entityList.remove( entity.getId() );
					}
				}
			}

			Map<Integer, DNVEntity> nodeList = nodesAndEdges.get( level );
			if( nodeList != null )
			{
				nodeList.remove( entity.getId() );
			}

			// nodeList = anchorNodes.get( level );
			// if( nodeList != null )
			// {
			// nodeList.remove( node.getId() );
			// }

			allNodesById.remove( entity.getId() );
			allNodesByBbId.remove( entity.getBbId() );
			Map<Integer, DNVNode> active = activeNodes.get( level );
			if( active != null )
			{
				active.remove( entity.getId() );
			}

			Map<Integer, DNVNode> interpolation = interpolationNodes.get( level );
			if( interpolation != null )
			{
				interpolation.remove( entity.getId() );
			}

			Map<Integer, DNVEntity> labels = mustDrawLabels.get( level );
			if( labels != null )
			{
				labels.remove( entity.getId() );
			}
		}
	}

	/**
	 * Removes the all edges.
	 * 
	 * @param level
	 *            the level
	 */
	public void removeAllEdges( int level )
	{
		Map<Integer, DNVEdge> edgeMap = edges.get( level );
		if( edgeMap != null && edgeMap.size() > 0 )
		{
			List<DNVEdge> edgeList = new ArrayList<DNVEdge>( edgeMap.values() );
			for( DNVEdge tempEdge : edgeList )
			{
				removeNode( level, tempEdge );
			}
		}
	}

	/**
	 * Removes the nodes.
	 * 
	 * @param entities
	 *            the entities
	 */
	private void removeNodes( List<DNVEntity> entities )
	{
		for( DNVEntity e : entities )
		{
			removeNode( e );
		}
	}

	/**
	 * Removes the node by id.
	 * 
	 * @param nodeId
	 *            the node id
	 */
	public void removeNodeById( Integer nodeId )
	{
		DNVEntity entity = allNodesById.get( nodeId );
		removeNode( entity );
	}

	/**
	 * Gets the graph size.
	 * 
	 * @param level
	 *            the level
	 * @return the graph size
	 */
	public int getGraphSize( Integer level )
	{
		Map<Integer, DNVNode> nodeList = nodes.get( level );
		if( nodeList != null )
			return nodeList.size();

		return 0;
	}

	/*
	 * public void replaceLevel( Integer level, List<DNVNode> nodeList,
	 * List<DNVEdge> edgeList ) { nodes.put( level, nodeList ); edges.put(
	 * level, edgeList );
	 * 
	 * List<DNVNode> nodeEdgeList = new ArrayList<DNVNode>();
	 * nodeEdgeList.addAll( nodeList ); nodeEdgeList.addAll( edgeList );
	 * nodesAndEdges.put( level, nodeEdgeList ); }
	 */
	/**
	 * Adds the to level.
	 * 
	 * @param level
	 *            the level
	 * @param nodeList
	 *            the node list
	 */
	public void addToLevel( Integer level, List<DNVNode> nodeList )
	{
		for( Iterator<DNVNode> i = nodeList.iterator(); i.hasNext(); )
		{
			addNode( level, i.next() );
		}
	}

	/**
	 * Gets the nodes map.
	 * 
	 * @param level
	 *            the level
	 * @return the nodes map
	 */
	public Map<Integer, DNVNode> getNodesMap( Integer level )
	{
		return nodes.get( level );
	}

	//=========================
	// alex
	//=========================
	public List<DNVNode> getNodes()
	{
		return getNodes(0);
	}
	
	//=========================
	
	/**
	 * Gets the nodes.
	 * 
	 * @param level
	 *            the level
	 * @return the nodes
	 */
	public List<DNVNode> getNodes( Integer level )
	{
		if( nodes != null && nodes.get( level ) != null )
		{
			Collection<DNVNode> collection = nodes.get( level ).values();
			if( collection instanceof List<?> )
				return (List<DNVNode>)collection;

			return new ArrayList<DNVNode>( collection );
		}

		return new ArrayList<DNVNode>();
	}

	
	//=========================
	// alex
	//=========================
	public List<DNVEdge> getEdges()
	{
		return getEdges(0);
	}
	
	//=========================

	/**
	 * Gets the edges.
	 * 
	 * @param level
	 *            the level
	 * @return the edges
	 */
	public List<DNVEdge> getEdges( Integer level )
	{
		if( edges != null && edges.get( level ) != null )
		{
			Collection<DNVEdge> collection = edges.get( level ).values();
			if( collection instanceof List<?> )
				return (List<DNVEdge>)collection;

			return new ArrayList<DNVEdge>( collection );
		}

		return new ArrayList<DNVEdge>();
	}

	/**
	 * Gets the nodes and edges.
	 * 
	 * @param level
	 *            the level
	 * @return the nodes and edges
	 */
	public List<DNVEntity> getNodesAndEdges( Integer level )
	{
		List<DNVEntity> entityList = new ArrayList<DNVEntity>();
		if( nodesAndEdges != null && nodesAndEdges.get( level ) != null )
		{
			entityList = GraphFunctions.convertCollectionToList( nodesAndEdges.get( level ).values() );
		}

		return entityList;
	}

	/**
	 * Gets the orphant nodes.
	 * 
	 * @param currentLevel
	 *            the current level
	 * @return the orphant nodes
	 */
	public List<DNVNode> getOrphantNodes( Integer currentLevel )
	{
		List<DNVNode> tempList = getNodes( currentLevel );

		for( int i = 0; i < tempList.size(); i++ )
		{
			if( tempList.get( i ).getParentNode() != null )
			{
				tempList.remove( i );
				i--;
			}
		}

		return tempList;
	}

	/**
	 * Gets the number of disconnected nodes.
	 * 
	 * @param level
	 *            the level
	 * @return the number of disconnected nodes
	 */
	public int getNumberOfDisconnectedNodes( Integer level )
	{
		int counter = 0;
		Iterator<DNVNode> nodes = getNodes( level ).iterator();

		while( nodes.hasNext() )
		{
			if( nodes.next().getNeighbors().size() == 0 )
				counter++;
		}

		return counter;
	}

	/**
	 * Fix filename.
	 * 
	 * @param filename
	 *            the filename
	 * @return the string
	 */
	public String fixFilename( String filename )
	{
		if( filename.endsWith( ".bbg" ) )
		{
			filename = filename.replace( ".bbg", ".dnv" );
		}

		return filename;
	}

	/**
	 * Write graph.
	 * 
	 * @param filename
	 *            the filename
	 * @return true, if successful
	 */
	public boolean writeGraph( String filename )
	{
		FileWriter outputFileWriter;
		try
		{
			outputFileWriter = new FileWriter( filename );
			BufferedWriter outputWriter = new BufferedWriter( outputFileWriter );
			// outputWriter.write( serialize() );
			serialize( outputWriter );
			outputWriter.close();
			outputFileWriter.close();
		}
		catch( IOException e )
		{
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * Sets the all nodes inactive.
	 * 
	 * @param level
	 *            the new all nodes inactive
	 */
	public void setAllNodesInactive( Integer level )
	{
		Iterator<DNVNode> nodes = getNodes( level ).iterator();
		DNVNode tempNode;
		while( nodes.hasNext() )
		{
			tempNode = nodes.next();
			setNodeInactive( level, tempNode.getId() );
		}
	}

	// public void addAnchorNode( DNVNode node, Integer level )
	// {
	// Map<Integer,DNVNode> anchors = anchorNodes.get( level );
	// if( anchors == null )
	// {
	// anchors = new HashMap<Integer,DNVNode>();
	// }
	//
	// anchors.put( node.getId(), node );
	//
	// anchorNodes.put( level, anchors );
	// }
	//
	// public void removeAnchor( DNVNode node, Integer level )
	// {
	// Map<Integer,DNVNode> anchors = anchorNodes.get( level );
	// if( anchors != null )
	// {
	// anchors.remove( node.getId() );
	// }
	// }
	//
	// public List<DNVNode> getAnchorNodes( Integer level )
	// {
	// Map<Integer,DNVNode> anchors = anchorNodes.get( level );
	// if( anchors == null )
	// {
	// anchors = new HashMap<Integer,DNVNode>();
	// }
	//
	// return GraphFunctions.convertCollectionToList(anchors.values());
	// }
	//
	// public Map<Integer, Map<Integer,DNVNode>> getAnchorNodes()
	// {
	// return anchorNodes;
	// }

	/**
	 * Adds the edge.
	 * 
	 * @param level
	 *            the level
	 * @param from
	 *            the from
	 * @param to
	 *            the to
	 * @param edge
	 *            the edge
	 * @return the dNV edge
	 */
	public DNVEdge addEdge( int level, DNVNode from, DNVNode to, EdgeDecorator edge )
	{
		DNVEdge newEdge = new DNVEdge( level, DNVEdge.DEFAULT_RESTING_DISTANCE, false, from, to, this );
		newEdge.setDirectional( true );
		newEdge.setEdgeDecorator( edge );
		newEdge.setBbId( from.getBbId() + "->" + to.getBbId() );

		// Don't add if it already exists
		if( allNodesByBbId.containsKey( newEdge.getBbId() ) )
			return (DNVEdge)allNodesByBbId.get( newEdge.getBbId() );

		addNode( level, newEdge );
		from.addFromEdge( newEdge );
		to.addToEdge( newEdge );

		return newEdge;
	}

	/**
	 * Adds the edge.
	 * 
	 * @param level
	 *            the level
	 * @param fromUri
	 *            the from uri
	 * @param toUri
	 *            the to uri
	 * @param edge
	 *            the edge
	 * @return the dNV edge
	 */
	public DNVEdge addEdge( int level, String fromUri, String toUri, EdgeDecorator edge )
	{
		DNVNode from = (DNVNode)allNodesByBbId.get( fromUri );
		DNVNode to = (DNVNode)allNodesByBbId.get( toUri );

		// System.out.println( "Trying to add an edge from '" + fromUri + "'=["
		// + from + "] to '" + toUri + "'=[" + to + "]" );

		if( to != null && from != null )
			return addEdge( level, from, to, edge );

		return null;
	}

	/**
	 * Update nodes.
	 * 
	 * @param level
	 *            the level
	 * @param resultMap
	 *            the result map
	 */
	public void updateNodes( int level, Map<String, ResourceDecorator> resultMap )
	{
		updating = true;
		ResourceDecorator tempDecorator;
		String uri;
		DNVNode tempNode;

		// Add all the nodes
		for( Entry<String, ResourceDecorator> entry : resultMap.entrySet() )
		{
			tempDecorator = entry.getValue();
			uri = tempDecorator.getUri();
			if( !allNodesByBbId.containsKey( uri ) )
			{
				tempNode = new DNVNode( tempDecorator, this );
				tempNode.setPosition( (float)( 100.0 * Math.random() ), (float)( 100.0 * Math.random() ) );
				System.out.println( "Adding node to Wigi: " + uri );
				addNode( level, tempNode );
			}
			else
			{
				tempNode = (DNVNode)allNodesByBbId.get( uri );
			}

			tempNode.setSelected( entry.getValue().isSelected() );
		}

		updating = false;

	}

	/**
	 * Update edges.
	 * 
	 * @param level
	 *            the level
	 * @param edgeDecs
	 *            the edge decs
	 */
	public void updateEdges( int level, Set<EdgeDecorator> edgeDecs )
	{
		updating = true;
		Map<String, DNVEntity> allNodesAndEdges = new HashMap<String, DNVEntity>();
		DNVEdge tempEdge = null;
		int numEdges = 0;
		// Add all the edges
		for( EdgeDecorator edgeDec : edgeDecs )
		{
			if( edgeDec.getFromUri() != edgeDec.getToUri() )
			{
				tempEdge = addEdge( level, edgeDec.getFromUri(), edgeDec.getToUri(), edgeDec );
			}
			if( tempEdge != null )
			{
				numEdges++;
				allNodesAndEdges.put( tempEdge.getBbId(), tempEdge );
			}
		}
		updating = false;
	}

	/**
	 * Checks if is updating.
	 * 
	 * @return true, if is updating
	 */
	public boolean isUpdating()
	{
		return updating;
	}

	/**
	 * Sets the updating.
	 * 
	 * @param updating
	 *            the new updating
	 */
	public void setUpdating( boolean updating )
	{
		this.updating = updating;
	}

	/**
	 * Gets the filename.
	 * 
	 * @return the filename
	 */
	public String getFilename()
	{
		return filename;
	}

	/**
	 * Sets the filename.
	 * 
	 * @param filename
	 *            the new filename
	 */
	public void setFilename( String filename )
	{
		this.filename = filename;
	}

	/**
	 * Clear level.
	 * 
	 * @param level
	 *            the level
	 */
	public void clearLevel( int level )
	{
		removeAllEdges( level );
		removeAllNodes( level );

//		List<DNVNode> nodes = getNodes( level );
//		List<DNVEdge> edges = getEdges( level );
//
//		List<DNVNode> tempNodes;
//		if( nodes != null )
//		{
//			tempNodes = new ArrayList<DNVNode>( nodes );
//			nodes.clear();
//		}
//		else
//		{
//			tempNodes = new ArrayList<DNVNode>();
//		}
//
//		List<DNVEdge> tempEdges;
//		if( edges != null )
//		{
//			tempEdges = new ArrayList<DNVEdge>( edges );
//			edges.clear();
//		}
//		else
//		{
//			tempEdges = new ArrayList<DNVEdge>();
//		}
//
//		this.edges.remove( level );
//		this.nodes.remove( level );
//		this.nodesAndEdges.remove( level );
//		this.activeNodes.remove( level );
//
//		int numNodes = tempNodes.size();
//		DNVNode tempNode;
//		for( int i = 0; i < numNodes; i++ )
//		{
//			tempNode = tempNodes.get( i );
//			allNodesById.remove( tempNode.getId() );
//			allNodesByBbId.remove( tempNode.getBbId() );
//		}
//
//		int numEdges = tempEdges.size();
//		DNVEdge tempEdge;
//		for( int i = 0; i < numEdges; i++ )
//		{
//			tempEdge = tempEdges.get( i );
//			allNodesById.remove( tempEdge.getId() );
//			allNodesByBbId.remove( tempEdge.getBbId() );
//		}
	}

	/**
	 * Gets the biggest id.
	 * 
	 * @return the biggest id
	 */
	public Integer getBiggestId()
	{
		Integer biggest = -1;
		List<DNVEntity> nodeList;
		Integer tempId;
		for( int i = 0; i <= getMaxLevel(); i++ )
		{
			nodeList = getNodesAndEdges( i );
			if( nodeList != null )
			{
				for( int j = 0; j < nodeList.size(); j++ )
				{
					tempId = nodeList.get( j ).getId();
					if( tempId > biggest )
					{
						biggest = tempId;
					}
				}
			}
		}

		return biggest;
	}

	/** The max label. */
	private String maxLabel = "";

	/**
	 * Gets the max label.
	 * 
	 * @return the max label
	 */
	public String getMaxLabel()
	{
		getMaxLabelLength();
		return maxLabel;
	}

	/**
	 * Gets the max label length.
	 * 
	 * @return the max label length
	 */
	public int getMaxLabelLength()
	{
		if( maxLabelLength == -1 )
		{
			Collection<DNVEntity> nodes = allNodesById.values();
			Iterator<DNVEntity> i = nodes.iterator();
			int length;
			String label;
			DNVEntity entity;
			DNVNode tempNode;
			while( i.hasNext() )
			{
				entity = i.next();
				if( entity instanceof DNVNode )
				{
					tempNode = (DNVNode)entity;
					label = tempNode.getLabel();
					length = label.length();
					if( length > maxLabelLength )
					{
						maxLabelLength = length;
						maxLabel = label;
					}
				}
			}
		}

		return maxLabelLength;
	}

	/**
	 * Sets the max label length.
	 * 
	 * @param maxLabelLength
	 *            the new max label length
	 */
	public void setMaxLabelLength( int maxLabelLength )
	{
		this.maxLabelLength = maxLabelLength;
	}

	/**
	 * Gets the center of gravity.
	 * 
	 * @return the center of gravity
	 */
	public Vector2D getCenterOfGravity()
	{
		return centerOfGravity;
	}

	/**
	 * Sets the center of gravity.
	 * 
	 * @param centerOfGravity
	 *            the new center of gravity
	 */
	public void setCenterOfGravity( Vector2D centerOfGravity )
	{
		this.centerOfGravity = centerOfGravity;
	}

	/**
	 * Clear interpolation list.
	 * 
	 * @param level
	 *            the level
	 */
	public void clearInterpolationList( Integer level )
	{
		Map<Integer, DNVNode> interpolationList = interpolationNodes.get( level );
		if( interpolationList == null )
		{
			interpolationList = new HashMap<Integer, DNVNode>();
		}
		interpolationList.clear();

		interpolationNodes.put( level, interpolationList );
	}

	/**
	 * Adds the interpolation node.
	 * 
	 * @param node
	 *            the node
	 * @param level
	 *            the level
	 */
	public void addInterpolationNode( DNVNode node, Integer level )
	{
		Map<Integer, DNVNode> interpolationList = interpolationNodes.get( level );
		if( interpolationList == null )
		{
			interpolationList = new HashMap<Integer, DNVNode>();
		}

		interpolationList.put( node.getId(), node );
		interpolationNodes.put( level, interpolationList );
	}

	/**
	 * Gets the interpolation list.
	 * 
	 * @param level
	 *            the level
	 * @return the interpolation list
	 */
	public List<DNVNode> getInterpolationList( Integer level )
	{
		if( interpolationNodes.get( level ) != null )
		{
			Collection<DNVNode> collection = interpolationNodes.get( level ).values();
			if( collection instanceof List<?> )
				return (List<DNVNode>)collection;

			return new ArrayList<DNVNode>( collection );
		}

		return new ArrayList<DNVNode>();
	}

	/**
	 * Gets the node map.
	 * 
	 * @param level
	 *            the level
	 * @return the node map
	 */
	public Map<Integer, DNVNode> getNodeMap( Integer level )
	{
		return nodes.get( level );
	}

	/**
	 * Gets the node.
	 * 
	 * @param level
	 *            the level
	 * @param id
	 *            the id
	 * @return the node
	 */
	public DNVNode getNode( Integer level, Integer id )
	{
		try
		{
			return nodes.get( level ).get( id );
		}
		catch( NullPointerException npe )
		{}

		return null;
	}

	/**
	 * Gets the edge map.
	 * 
	 * @param level
	 *            the level
	 * @return the edge map
	 */
	public Map<Integer, DNVEdge> getEdgeMap( Integer level )
	{
		return edges.get( level );
	}

	/**
	 * Sets the selected.
	 * 
	 * @param entity
	 *            the entity
	 * @param level
	 *            the level
	 * @param selected
	 *            the selected
	 */
	public void setSelected( DNVEntity entity, Integer level, boolean selected )
	{
		if( entity instanceof DNVNode )
		{
			setSelectedNode( (DNVNode)entity, level, selected );
		}
		else if( entity instanceof DNVEdge )
		{
			setSelectedEdge( (DNVEdge)entity, level, selected );
		}
	}

	/**
	 * Sets the selected edge.
	 * 
	 * @param edge
	 *            the edge
	 * @param level
	 *            the level
	 * @param selected
	 *            the selected
	 */
	private void setSelectedEdge( DNVEdge edge, Integer level, boolean selected )
	{
		Map<Integer, DNVEdge> edgesMap = getSelectedEdges( level );
		if( selected )
		{
			edgesMap.put( edge.getId(), edge );
			if( edge.getFrom() != null )
			{
				edge.getFrom().setEdgeSelected( true );
			}
			if( edge.getTo() != null )
			{
				edge.getTo().setEdgeSelected( true );
			}
		}
		else
		{
			edgesMap.remove( edge.getId() );
			if( edge.getFrom() != null )
			{
				edge.getFrom().setEdgeSelected( false );
			}
			if( edge.getTo() != null )
			{
				edge.getTo().setEdgeSelected( false );
			}
		}
	}

	/**
	 * Gets the selected edges.
	 * 
	 * @param level
	 *            the level
	 * @return the selected edges
	 */
	public Map<Integer, DNVEdge> getSelectedEdges( Integer level )
	{
		Map<Integer, DNVEdge> edgesMap = selectedEdges.get( level );
		if( edgesMap == null )
		{
			edgesMap = new HashMap<Integer, DNVEdge>();
			selectedEdges.put( level, edgesMap );
		}

		return edgesMap;
	}

	/**
	 * Sets the selected node.
	 * 
	 * @param node
	 *            the node
	 * @param level
	 *            the level
	 * @param selected
	 *            the selected
	 */
	private void setSelectedNode( DNVNode node, Integer level, boolean selected )
	{
		Map<Integer, DNVNode> nodesMap = getSelectedNodes( level );
		if( selected )
		{
			nodesMap.put( node.getId(), node );
		}
		else
		{
			nodesMap.remove( node.getId() );
		}
		node.tellNeighborsSelected( selected );
		if( paintBean != null )
		{
			paintBean.forceSubgraphRefresh();
		}
	}

	/**
	 * Gets the selected nodes.
	 * 
	 * @param level
	 *            the level
	 * @return the selected nodes
	 */
	public Map<Integer, DNVNode> getSelectedNodes( Integer level )
	{
		Map<Integer, DNVNode> nodesMap = selectedNodes.get( level );
		if( nodesMap == null )
		{
			nodesMap = new HashMap<Integer, DNVNode>();
			selectedNodes.put( level, nodesMap );
		}

		return nodesMap;
	}

	/**
	 * Sets the visible.
	 * 
	 * @param entity
	 *            the entity
	 * @param level
	 *            the level
	 * @param selected
	 *            the visible
	 */
	public void setVisible( DNVEntity entity, Integer level, boolean visible )
	{
		if( entity instanceof DNVNode )
		{
			setVisibleNode( (DNVNode)entity, level, visible );
		}
		else if( entity instanceof DNVEdge )
		{
			setVisibleEdge( (DNVEdge)entity, level, visible );
		}
	}

	/**
	 * Sets the visible edge.
	 * 
	 * @param edge
	 *            the edge
	 * @param level
	 *            the level
	 * @param selected
	 *            the visible
	 */
	private void setVisibleEdge( DNVEdge edge, Integer level, boolean visible )
	{
		Map<Integer, DNVEdge> edgesMap = getVisibleEdges( level );
		if( visible )
		{
			edgesMap.put( edge.getId(), edge );
		}
		else
		{
			edgesMap.remove( edge.getId() );
		}
		if( edge.getFrom() != null )
		{
			edge.getFrom().setFromEdgeVisible( edge, visible );
		}
		if( edge.getTo() != null )
		{
			edge.getTo().setToEdgeVisible( edge, visible );
		}
	}

	/**
	 * Gets the visible edges.
	 * 
	 * @param level
	 *            the level
	 * @return the visible edges
	 */
	public Map<Integer, DNVEdge> getVisibleEdges( Integer level )
	{
		Map<Integer, DNVEdge> edgesMap = visibleEdges.get( level );
		if( edgesMap == null )
		{
			edgesMap = new HashMap<Integer, DNVEdge>();
			visibleEdges.put( level, edgesMap );
		}

		return edgesMap;
	}

	/**
	 * Sets the visible node.
	 * 
	 * @param node
	 *            the node
	 * @param level
	 *            the level
	 * @param selected
	 *            the visible
	 */
	private void setVisibleNode( DNVNode node, Integer level, boolean visible )
	{
		Map<Integer, DNVNode> nodesMap = getVisibleNodes( level );
		if( visible )
		{
			nodesMap.put( node.getId(), node );
		}
		else
		{
			nodesMap.remove( node.getId() );
		}
		node.tellNeighborsVisible( visible );
		if( paintBean != null )
		{
			paintBean.forceSubgraphRefresh();
		}
	}

	/**
	 * Gets the visible nodes.
	 * 
	 * @param level
	 *            the level
	 * @return the visible nodes
	 */
	public Map<Integer, DNVNode> getVisibleNodes( Integer level )
	{
		Map<Integer, DNVNode> nodesMap = visibleNodes.get( level );
		if( nodesMap == null )
		{
			nodesMap = new HashMap<Integer, DNVNode>();
			visibleNodes.put( level, nodesMap );
		}

		return nodesMap;
	}
	
	/**
	 * Deselect all nodes.
	 * 
	 * @param level
	 *            the level
	 */
	public void deselectAllNodes( Integer level )
	{
		Map<Integer, DNVNode> nodesMap = getSelectedNodes( level );
		Collection<DNVNode> c = nodesMap.values();
		List<DNVNode> nodesList = new ArrayList<DNVNode>( c );
		for( int i = 0; i < nodesList.size(); i++ )
		{
			nodesList.get( i ).setSelected( false );
		}
	}

	/**
	 * Deselect all edges.
	 * 
	 * @param level
	 *            the level
	 */
	public void deselectAllEdges( Integer level )
	{
		Map<Integer, DNVEdge> edgesMap = getSelectedEdges( level );
		Collection<DNVEdge> c = edgesMap.values();
		List<DNVEdge> edgesList = new ArrayList<DNVEdge>( c );
		for( int i = 0; i < edgesList.size(); i++ )
		{
			edgesList.get( i ).setSelected( false );
		}
	}

	/**
	 * Gets the nodes by type.
	 * 
	 * @param level
	 *            the level
	 * @param type
	 *            the type
	 * @return the nodes by type
	 */
	public Map<Integer, DNVEntity> getNodesByType( Integer level, String type )
	{
		Map<String, Map<Integer, DNVEntity>> types = nodesByType.get( level );
		if( types == null )
		{
			types = new HashMap<String, Map<Integer, DNVEntity>>();
			nodesByType.put( level, types );
		}

		Map<Integer, DNVEntity> theMap = types.get( type );
		if( theMap == null )
		{
			theMap = new HashMap<Integer, DNVEntity>();
		}

		return theMap;
	}

	/**
	 * Gets the nodes by time.
	 * 
	 * @param level
	 *            the level
	 * @param time
	 *            the time
	 * @return the nodes by time
	 */
	public Map<Integer, DNVEntity> getNodesByTime( Integer level, String time )
	{
		Map<Long, Map<Integer, DNVEntity>> times = nodesByTime.get( level );
		if( times == null )
		{
			times = new HashMap<Long, Map<Integer, DNVEntity>>();
			nodesByTime.put( level, times );
		}

		Map<Integer, DNVEntity> theMap = times.get( time );
		if( theMap == null )
		{
			theMap = new HashMap<Integer, DNVEntity>();
		}

		return theMap;
	}

	/**
	 * Gets the maximum pairwise shortest path.
	 * 
	 * @param level
	 *            the level
	 * @return the maximum pairwise shortest path
	 */
	public int getMaximumPairwiseShortestPath( int level )
	{
		Integer mpsp = maximumPairwiseShortestPaths.get( level );
		if( mpsp == null )
		{
			updateMaximumPairwiseShortestPath( level );
		}

		return maximumPairwiseShortestPaths.get( level );
	}

	/**
	 * Update maximum pairwise shortest path.
	 * 
	 * @param level
	 *            the level
	 */
	public void updateMaximumPairwiseShortestPath( int level )
	{
		System.out.println( "Calculating maximum pairwise shortest path for level " + level );

		int maximumPairwiseShortestPath = GraphFunctions.getMaximumPairwiseShortestPath( this, level );

		System.out.println( "Maximum pairwise shortest path for level " + level + " is " + maximumPairwiseShortestPath );

		maximumPairwiseShortestPaths.put( level, maximumPairwiseShortestPath );
	}

	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name.
	 * 
	 * @param name
	 *            the new name
	 */
	public void setName( String name )
	{
		this.name = name;
	}

	/**
	 * Checks if is selected.
	 * 
	 * @param entity
	 *            the entity
	 * @param level
	 *            the level
	 * @return true, if is selected
	 */
	public boolean isSelected( DNVEntity entity, int level )
	{
		if( entity instanceof DNVNode )
		{
			return isSelected( (DNVNode)entity, level );
		}
		else if( entity instanceof DNVEdge )
		{
			return isSelected( (DNVEdge)entity, level );
		}

		return false;
	}

	/**
	 * Checks if is selected.
	 * 
	 * @param edge
	 *            the edge
	 * @param level
	 *            the level
	 * @return true, if is selected
	 */
	private boolean isSelected( DNVEdge edge, int level )
	{
		try
		{
			return selectedEdges.get( level ).get( edge.getId() ) == edge;
		}
		catch( NullPointerException npe )
		{
			return false;
		}
	}

	/**
	 * Checks if is selected.
	 * 
	 * @param node
	 *            the node
	 * @param level
	 *            the level
	 * @return true, if is selected
	 */
	private boolean isSelected( DNVNode node, int level )
	{
		try
		{
			return selectedNodes.get( level ).containsKey( node.getId() );
		}
		catch( NullPointerException npe )
		{
			return false;
		}
	}

	/**
	 * Checks if is visible.
	 * 
	 * @param entity
	 *            the entity
	 * @param level
	 *            the level
	 * @return true, if is selected
	 */
	public boolean isVisible( DNVEntity entity, int level )
	{
		if( entity instanceof DNVNode )
		{
			return isVisible( (DNVNode)entity, level );
		}
		else if( entity instanceof DNVEdge )
		{
			return isVisible( (DNVEdge)entity, level );
		}

		return false;
	}

	/**
	 * Checks if is visible.
	 * 
	 * @param edge
	 *            the edge
	 * @param level
	 *            the level
	 * @return true, if is selected
	 */
	private boolean isVisible( DNVEdge edge, int level )
	{
		try
		{
			return visibleEdges.get( level ).get( edge.getId() ) == edge;
		}
		catch( NullPointerException npe )
		{
			return false;
		}
	}

	/**
	 * Checks if is visible.
	 * 
	 * @param node
	 *            the node
	 * @param level
	 *            the level
	 * @return true, if is selected
	 */
	private boolean isVisible( DNVNode node, int level )
	{
		try
		{
			return visibleNodes.get( level ).get( node.getId() ) == node;
		}
		catch( NullPointerException npe )
		{
			return false;
		}
	}

	/**
	 * Sets the property.
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public void setProperty( String key, String value )
	{
		properties.put( key, value );
	}

	/**
	 * Gets the property.
	 * 
	 * @param key
	 *            the key
	 * @return the property
	 */
	public String getProperty( String key )
	{
		return properties.get( key );
	}

	/**
	 * Adds the must draw label.
	 * 
	 * @param level
	 *            the level
	 * @param entity
	 *            the entity
	 */
	public void addMustDrawLabel( int level, DNVEntity entity )
	{
		Map<Integer, DNVEntity> mustDrawList = mustDrawLabels.get( level );
		if( mustDrawList == null )
		{
			mustDrawList = new HashMap<Integer, DNVEntity>();
			mustDrawLabels.put( level, mustDrawList );
		}

		synchronized( mustDrawList )
		{
			mustDrawList.put( entity.getId(), entity );
		}

		if( paintBean != null )
		{
			paintBean.forceSubgraphRefresh();
		}
	}

	/**
	 * Removes the must draw label.
	 * 
	 * @param level
	 *            the level
	 * @param entity
	 *            the entity
	 */
	public void removeMustDrawLabel( int level, DNVEntity entity )
	{
		Map<Integer, DNVEntity> mustDrawList = mustDrawLabels.get( level );
		if( mustDrawList == null )
		{
			mustDrawList = new HashMap<Integer, DNVEntity>();
			mustDrawLabels.put( level, mustDrawList );
		}

		synchronized( mustDrawList )
		{
			mustDrawList.remove( entity.getId() );
		}

		if( paintBean != null )
		{
			paintBean.forceSubgraphRefresh();
		}
	}

	/**
	 * Gets the must draw labels.
	 * 
	 * @param level
	 *            the level
	 * @return the must draw labels
	 */
	public Map<Integer, DNVEntity> getMustDrawLabels( int level )
	{
		Map<Integer, DNVEntity> mustDrawList = mustDrawLabels.get( level );
		if( mustDrawList == null )
		{
			mustDrawList = new HashMap<Integer, DNVEntity>();
			mustDrawLabels.put( level, mustDrawList );
		}

		return mustDrawList;
	}

	/** The has stored position. */
	private boolean hasStoredPosition = false;

	/** Should we use the stored position for display */
	private boolean displayStoredPosition = false;

	/** The stored position timer. */
	private Timer storedPositionTimer = new Timer( Timer.NANOSECONDS );

	public void storeCurrentPosition( int level )
	{
		storeCurrentPosition( level, true );
	}

	/**
	 * Store current position.
	 * 
	 * @param level
	 *            the level
	 */
	public void storeCurrentPosition( int level, boolean displayStoredPosition )
	{
		synchronized( this )
		{
			List<DNVNode> nodes = getNodes( level );
			for( DNVNode node : nodes )
			{
				node.storeCurrentPosition();
			}
			hasStoredPosition = true;
			this.displayStoredPosition = displayStoredPosition;
			storedPositionTimer = new Timer( Timer.NANOSECONDS );

//			System.out.println( "Set Stored Position" );
		}
	}

	/**
	 * Removes the stored position.
	 * 
	 * @param level
	 *            the level
	 */
	public void removeStoredPosition( int level )
	{
		synchronized( this )
		{
			List<DNVNode> nodes = getNodes( level );
			for( DNVNode node : nodes )
			{
				node.removeStoredPosition();
			}
			hasStoredPosition = false;
			displayStoredPosition = false;
			storedPositionTimer.setEnd();

			System.out.println( "Removed Stored Position after " + storedPositionTimer.getTotalTime( Timer.SECONDS ) + " seconds and " + currentFrame
					+ " frames." );
		}
	}

	/**
	 * Checks for stored position.
	 * 
	 * @return true, if successful
	 */
	public boolean hasStoredPosition()
	{
		return hasStoredPosition;
	}

	/** The start interpolation. */
	private boolean startInterpolation = false;

	private boolean reverseInterpolation = false;

	public void startInterpolation()
	{
		startInterpolation( false );
	}
	
	public boolean isInterpolating()
	{
		return startInterpolation && hasStoredPosition;
	}

	/**
	 * Start interpolation.
	 */
	public void startInterpolation( boolean reverseInterpolation )
	{
		startInterpolation = true;
		this.reverseInterpolation = reverseInterpolation;
		storedPositionTimer.setStart();
	}

	/** The current frame. */
	private int currentFrame = 0;

	/**
	 * Gets the current frame.
	 * 
	 * @return the current frame
	 */
	public int getCurrentFrame()
	{
		return currentFrame;
	}

	/**
	 * Interpolate to new position.
	 * 
	 * @param level
	 *            the level
	 * @param totalSeconds
	 *            the total seconds
	 */
	public void interpolateToNewPosition( int level, float totalSeconds )
	{
		synchronized( this )
		{
			if( hasStoredPosition && startInterpolation )
			{
//				System.out.println( "interpolation has[" + hasStoredPosition + "] start[" + startInterpolation + "] reverse[" + reverseInterpolation
//						+ "] display[" + displayStoredPosition + "]" );
				Vector2D originalPosition;
				Vector2D displayPosition;
				Vector2D finalPosition;
				float x_diff;
				float y_diff;

				// float frames = 50;
				float seconds;
				// if( currentFrame == 0 )
				// {
				// storedPositionTimer.setStart();
				// }

				seconds = storedPositionTimer.getTimeSinceStart( Timer.SECONDS );

				float ratio = seconds / totalSeconds;
				// System.out.println( "currentFrame : " + currentFrame );
				// System.out.println( "seconds : " + seconds );
				// System.out.println( "ratio : " + ratio );
				if( ratio >= 1 )
				{
					startInterpolation = false;
					if( !reverseInterpolation )
					{
						removeStoredPosition( level );
					}
					currentFrame = 0;
				}
				else
				{
					List<DNVNode> nodes = getNodes( level );
					// int i = 0;
					for( DNVNode node : nodes )
					{
						if( !reverseInterpolation )
						{
							finalPosition = node.getPosition( false );
							displayPosition = node.getPosition( true );
							originalPosition = node.getOriginalPosition();
						}
						else
						{
							finalPosition = node.getOriginalPosition();
							displayPosition = node.getPosition( true );
							originalPosition = node.getPosition( false );
						}
						x_diff = finalPosition.getX() - originalPosition.getX();
						y_diff = finalPosition.getY() - originalPosition.getY();
						x_diff *= ratio;
						y_diff *= ratio;
						x_diff += originalPosition.getX();
						y_diff += originalPosition.getY();

						// if( i == 0 )
						// {
						// System.out.println( "Previous position " +
						// displayPosition );
						// System.out.println( "Final position " + finalPosition
						// );
						// }
						// System.out.println( "Adding " + x_diff + ", " +
						// y_diff );

						displayPosition.set( x_diff, y_diff );

						// if( i == 0 )
						// {
						// System.out.println( "New position " + displayPosition
						// );
						// System.out.println( "Final position " + finalPosition
						// );
						// }
						//						
						// i++;
						currentFrame++;
					}

				}
				// System.out.println( "Finishing frame " + currentFrame );
				// System.out.println( "Total frames " + frames );
				// if( currentFrame == frames )
				// {
				// removeStoredPosition( level );
				// currentFrame = 0;
				// startInterpolation = false;
				// }
			}
		}
	}

	/**
	 * Removes the all nodes.
	 * 
	 * @param level
	 *            the level
	 */
	public void removeAllNodes( int level )
	{
		List<DNVNode> nodes = getNodes( level );
		for( DNVNode node : nodes )
		{
			removeNode( node );
		}
	}

	/**
	 * Gets the id generator.
	 * 
	 * @return the id generator
	 */
	public IDGenerator getIdGenerator()
	{
		if( idGenerator == null )
		{
			idGenerator = new IDGenerator();
		}

		return idGenerator;
	}
	
	/**
	 * Sets the paint bean.
	 * 
	 * @param paintBean
	 *            the new paint bean
	 */
	public void setPaintBean( PaintBean paintBean )
	{
		this.paintBean = paintBean;
	}

	/**
	 * Gets the paint bean.
	 * 
	 * @return the paint bean
	 */
	public PaintBean getPaintBean()
	{
		return paintBean;
	}

	/**
	 * Gets the geometric objects.
	 * 
	 * @param level
	 *            the level
	 * @return the geometric objects
	 */
	public List<Geometric> getGeometricObjects( int level )
	{
		return geometricObjects.get( level );
	}

	/**
	 * Sets the geometric objects.
	 * 
	 * @param level
	 *            the level
	 * @param objects
	 *            the objects
	 */
	public void setGeometricObjects( int level, List<Geometric> objects )
	{
		geometricObjects.put( level, objects );
	}

	/**
	 * Adds the geometric object.
	 * 
	 * @param level
	 *            the level
	 * @param object
	 *            the object
	 */
	public void addGeometricObject( int level, Geometric object )
	{
		List<Geometric> objects = getGeometricObjects( level );
		if( objects == null )
		{
			objects = new ArrayList<Geometric>();
			setGeometricObjects( level, objects );
		}

		objects.add( object );
	}
	
	public void removeGeometricObject( int level, Geometric object )
	{
		List<Geometric> objects = getGeometricObjects( level );
		if( objects != null )
		{
			objects.remove( object );
		}		
	}

	/**
	 * Gets the min time.
	 * 
	 * @param level
	 *            the level
	 * @return the min time
	 */
	public long getMinTime( int level )
	{
		long minTime = Long.MAX_VALUE;

		List<DNVEntity> entities = getNodesAndEdges( level );

		String property;
		long time;
		for( DNVEntity entity : entities )
		{
			property = entity.getProperty( "time" );
			if( property != null && !property.equals( "" ) )
			{
				time = Long.parseLong( property );
				if( time < minTime )
				{
					minTime = time;
				}
			}
		}

		return minTime;
	}

	/**
	 * Gets the max time.
	 * 
	 * @param level
	 *            the level
	 * @return the max time
	 */
	public long getMaxTime( int level )
	{
		long maxTime = Long.MIN_VALUE;

		List<DNVEntity> entities = getNodesAndEdges( level );

		String property;
		long time;
		for( DNVEntity entity : entities )
		{
			property = entity.getProperty( "time" );
			if( property != null && !property.equals( "" ) )
			{
				time = Long.parseLong( property );
				if( time > maxTime )
				{
					maxTime = time;
				}
			}
		}

		return maxTime;
	}

	/**
	 * Gets the number of times.
	 * 
	 * @param level
	 *            the level
	 * @return the number of times
	 */
	public int getNumberOfTimes( int level )
	{
		return nodesByTime.get( level ).size();
	}

	/**
	 * Gets the times.
	 * 
	 * @param level
	 *            the level
	 * @return the times
	 */
	public Collection<Long> getTimes( int level )
	{
		try
		{
			return nodesByTime.get( level ).keySet();
		}
		catch( NullPointerException npe )
		{}

		return new ArrayList<Long>();
	}

	// First key is property name
	// Second key is property value
	// Third key is node id (to make it easier to prevent nodes from being added
	// multiple times)
	/** The extractable properties. */
	private Map<String, Map<String, Map<Integer, DNVNode>>> extractableProperties = new HashMap<String, Map<String, Map<Integer, DNVNode>>>();

	/**
	 * Gets the extractable properties.
	 * 
	 * @return the extractable properties
	 */
	public Map<String, Map<String, Map<Integer, DNVNode>>> getExtractableProperties()
	{
		return extractableProperties;
	}

	/**
	 * Sets the extractable properties.
	 * 
	 * @param extractableProperties
	 *            the extractable properties
	 */
	public void setExtractableProperties( Map<String, Map<String, Map<Integer, DNVNode>>> extractableProperties )
	{
		this.extractableProperties = extractableProperties;
	}

	/**
	 * Checks if is property extractable.
	 * 
	 * @param property
	 *            the property
	 * @return true, if is property extractable
	 */
	public boolean isPropertyExtractable( String property )
	{
		Map<String, Map<Integer, DNVNode>> values = extractableProperties.get( property );
		if( values != null && values.size() > 0 )
		{
			return true;
		}

		return false;
	}

	/**
	 * Sets the property extractable.
	 * 
	 * @param property
	 *            the property
	 * @param dnvNode
	 *            the dnv node
	 */
	public void setPropertyExtractable( String property, DNVNode dnvNode )
	{
		synchronized( extractableProperties )
		{
			// Get all values for the given property
			Map<String, Map<Integer, DNVNode>> values = extractableProperties.get( property );
			if( values == null )
			{
				values = new HashMap<String, Map<Integer, DNVNode>>();
				extractableProperties.put( property, values );
			}
			// else
			// {
			// System.out.println( property + " has " + values.size() +
			// " different values. " );
			// }

			// Get all nodes that have the given value for the given property
			Map<Integer, DNVNode> nodes = values.get( dnvNode.getProperty( property ) );
			if( nodes == null )
			{
				nodes = new HashMap<Integer, DNVNode>();
				values.put( dnvNode.getProperty( property ), nodes );
			}
			// else
			// {
			// System.out.println( "There are " + nodes.size() +
			// " nodes with value " + dnvNode.getProperty( property ) +
			// " for property " + property );
			// }

			nodes.put( dnvNode.getId(), dnvNode );
		}
	}

	/**
	 * Contains node.
	 * 
	 * @param node
	 *            the node
	 * @return true, if successful
	 */
	public boolean containsNode( DNVNode node )
	{
		try
		{
			if( node == getNodeByBbId( node.getBbId() ) )
			{
				return true;
			}
			if( node == getNodeById( node.getId() ) )
			{
				return true;
			}
		}
		catch( NullPointerException npe )
		{
			return false;
		}

		return false;
	}

	/**
	 * @return the displayStoredPosition
	 */
	public boolean isDisplayStoredPosition()
	{
		return displayStoredPosition;
	}

	/**
	 * @param displayStoredPosition
	 *            the displayStoredPosition to set
	 */
	public void setDisplayStoredPosition( boolean displayStoredPosition )
	{
		this.displayStoredPosition = displayStoredPosition;
	}
	
	public void addAnimation( Animation animation )
	{
		animations.add( animation );
	}
	
	public List<Animation> getAnimations()
	{
		return animations;
	}

	public void setAnimations( List<Animation> animations )
	{
		this.animations = animations;
	}

	/**
	 * @param dnvEntity
	 */
	public void updateBbId( DNVEntity dnvEntity )
	{
		allNodesByBbId.put( dnvEntity.getBbId(), dnvEntity );
	}
	
	public void unsellectAllNodes()
	{
		for (DNVNode n : getNodes(0))
			n.setSelected(false);
	}
	
	//======================================
	// connected components - alex
	//======================================
	// test with graph: _bb_al_queda
	public int numConnectedComponents = 0;
	public ArrayList<ArrayList<DNVNode>> connectedComponents = new ArrayList<ArrayList<DNVNode>>();

	String NODE_MARKED = "marked";
	
	public int getNumConnectedComponents()
	{
		if (numConnectedComponents == 0)
		{
//			System.out.println( "Counting components" );
			// unmark all
			for (DNVNode n : getNodes(0))
				n.removeProperty(NODE_MARKED);
			
			connectedComponents = new ArrayList<ArrayList<DNVNode>>();
			
//			int startIndex = 0;
			
			for (DNVNode n : getVisibleNodes(0).values())
				// if new component
				if (n.getProperty(NODE_MARKED) == null)
				{
					numConnectedComponents ++;

					connectedComponents.add(new ArrayList<DNVNode>());
					
					markNeighbors(n);
					
//					startIndex = numMarkedNodes();
				}
			
			// unmark all
			for (DNVNode n : getNodes(0))
				n.removeProperty(NODE_MARKED);
		}
		
		Collections.sort(connectedComponents, new Comparator<ArrayList<DNVNode>>()
		{
		    public int compare(ArrayList<DNVNode> a1, ArrayList<DNVNode> a2) 
		    {
		        return a2.size() - a1.size(); // assumes you want biggest to smallest
		    }
		});

//		System.out.println( connectedComponents.size() + " components" );

		return connectedComponents.size();
	}

	// depth first search
	void markNeighbors(DNVNode n)
	{
		if (n.getProperty(NODE_MARKED) != null)
			return;
		
		n.setProperty(NODE_MARKED, "");
		connectedComponents.get(connectedComponents.size() - 1).add(n);
		
		List<DNVNode> neighbors = n.getNeighbors( true );
		
		if (neighbors != null)
			for (DNVNode neighbor : neighbors)
				markNeighbors(neighbor);
	}
	
	int numMarkedNodes()
	{
		int numMarked = 0;
		
		for (DNVNode n : getNodes(0))
			if (n.getProperty(NODE_MARKED) != "true")
				numMarked ++;
		
		return numMarked;
	}
	
	public void setNumConnectedComponents(int numConnectedComponents)
	{
		this.numConnectedComponents = numConnectedComponents;
	}
		
	public ArrayList<ArrayList<DNVNode>> getConnectedComponents()
	{
		return connectedComponents;
	}

	public void setConnectedComponents(ArrayList<ArrayList<DNVNode>> connectedComponents)
	{
		this.connectedComponents = connectedComponents;
	}
	
	//======================================	
}
