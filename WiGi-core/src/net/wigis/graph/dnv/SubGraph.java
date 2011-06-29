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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.wigis.graph.dnv.layout.implementations.Springs;

// TODO: Auto-generated Javadoc
/**
 * The Class SubGraph.
 * 
 * @author Brynjar Gretarsson
 */
public class SubGraph
{

	/** The nodes. */
	private Map<Integer, DNVNode> nodes = new HashMap<Integer, DNVNode>( 0 );

	/** The edges. */
	private Map<Integer, DNVEdge> edges = new HashMap<Integer, DNVEdge>( 0 );

	/** The sorted nodes. */
	private List<DNVNode> sortedNodes = null;

	/** The sorted must draw label nodes. */
	private List<DNVNode> sortedMustDrawLabelNodes = null;
	// private List<DNVNode> nodesList = new ArrayList<DNVNode>(0);
	/** The super graph. */
	private DNVGraph superGraph;

	/** The level. */
	private int level;

	/**
	 * Instantiates a new sub graph.
	 * 
	 * @param superGraph
	 *            the super graph
	 * @param level
	 *            the level
	 */
	public SubGraph( DNVGraph superGraph, int level )
	{
		this.level = level;
		this.superGraph = superGraph;
	}

	/**
	 * Adds the.
	 * 
	 * @param node
	 *            the node
	 */
	public void add( DNVNode node )
	{
		nodes.put( node.getId(), node );
	}

	/**
	 * Adds the all.
	 * 
	 * @param nodesToAdd
	 *            the nodes to add
	 */
	public void addAll( Collection<DNVNode> nodesToAdd )
	{
		Iterator<DNVNode> i = nodesToAdd.iterator();
		DNVNode tempNode;
		while( i.hasNext() )
		{
			tempNode = i.next();
			add( tempNode );
			addAllEdges( tempNode.getFromEdges( true ) );
			addAllEdges( tempNode.getToEdges( true ) );
		}
	}

	/**
	 * Adds the all nodes.
	 * 
	 * @param nodesToAdd
	 *            the nodes to add
	 */
	public void addAllNodes( Collection<DNVNode> nodesToAdd )
	{
		Iterator<DNVNode> i = nodesToAdd.iterator();
		DNVNode tempNode;
		while( i.hasNext() )
		{
			tempNode = i.next();
			if( tempNode.isVisible() )
			{
				add( tempNode );
			}
		}
	}

	/**
	 * Adds the edge.
	 * 
	 * @param edge
	 *            the edge
	 */
	public void addEdge( DNVEdge edge )
	{
		edges.put( edge.getId(), edge );
	}

	/**
	 * Adds the all edges.
	 * 
	 * @param edgesToAdd
	 *            the edges to add
	 */
	public void addAllEdges( List<DNVEdge> edgesToAdd )
	{
		for( int i = 0; i < edgesToAdd.size(); i++ )
		{
			if( edgesToAdd.get( i ).isVisible() )
			{
				addEdge( edgesToAdd.get( i ) );
			}
		}
	}

	/**
	 * Accumulate forces.
	 */
	public void accumulateForces()
	{
		if( nodes.size() > 0 )
		{
			// Springs.prepareOctree( superGraph, level );
			Iterator<DNVNode> i = nodes.values().iterator();
			Springs.accumulateForces( superGraph, i, level );
		}
	}

	/**
	 * Apply forces.
	 */
	public void applyForces()
	{
		Iterator<DNVNode> i = nodes.values().iterator();
		Springs.applyForces( i );
	}

	/**
	 * Gets the nodes list.
	 * 
	 * @return the nodes list
	 */
	public List<DNVNode> getNodesList()
	{
		if( nodes != null )
		{
			Collection<DNVNode> collection = nodes.values();
			if( collection instanceof List<?> )
				return (List<DNVNode>)collection;

			return new ArrayList<DNVNode>( collection );
		}

		return new ArrayList<DNVNode>();
	}

	/**
	 * Sets the nodes.
	 * 
	 * @param nodes
	 *            the nodes
	 */
	public void setNodes( Map<Integer, DNVNode> nodes )
	{
		this.nodes = nodes;
	}

	/**
	 * Gets the edges.
	 * 
	 * @return the edges
	 */
	public Map<Integer, DNVEdge> getEdges()
	{
		return edges;
	}

	/**
	 * Sets the edges.
	 * 
	 * @param edges
	 *            the edges
	 */
	public void setEdges( Map<Integer, DNVEdge> edges )
	{
		this.edges = edges;
	}

	/**
	 * Gets the level.
	 * 
	 * @return the level
	 */
	public int getLevel()
	{
		return level;
	}

	/**
	 * Sets the level.
	 * 
	 * @param level
	 *            the new level
	 */
	public void setLevel( int level )
	{
		this.level = level;
	}

	/**
	 * Gets the nodes.
	 * 
	 * @return the nodes
	 */
	public Map<Integer, DNVNode> getNodes()
	{
		return nodes;
	}
	
	public List<DNVNode> getVisibleNodes()
	{
		List<DNVNode> visibleNodes = new ArrayList<DNVNode>();
		for( DNVNode node : nodes.values() )
		{
			if( node.isVisible() )
			{
				visibleNodes.add( node );
			}
		}
		
		return visibleNodes;
	}

	public List<DNVEdge> getVisibleEdges()
	{
		List<DNVEdge> visibleEdges = new ArrayList<DNVEdge>();
		for( DNVEdge edge : edges.values() )
		{
			if( edge.isVisible() )
			{
				visibleEdges.add( edge );
			}
		}
		
		return visibleEdges;
	}

	/**
	 * Gets the super graph.
	 * 
	 * @return the super graph
	 */
	public DNVGraph getSuperGraph()
	{
		return superGraph;
	}

	/**
	 * Sets the super graph.
	 * 
	 * @param superGraph
	 *            the new super graph
	 */
	public void setSuperGraph( DNVGraph superGraph )
	{
		this.superGraph = superGraph;
	}

	/**
	 * Sets the sorted nodes.
	 * 
	 * @param sortedNodes
	 *            the new sorted nodes
	 */
	public void setSortedNodes( List<DNVNode> sortedNodes )
	{
		this.sortedNodes = sortedNodes;
	}

	/**
	 * Gets the sorted nodes.
	 * 
	 * @return the sorted nodes
	 */
	public List<DNVNode> getSortedNodes()
	{
		return sortedNodes;
	}

	/**
	 * Sets the sorted must draw label nodes.
	 * 
	 * @param sortedMustDrawLabelNodes
	 *            the new sorted must draw label nodes
	 */
	public void setSortedMustDrawLabelNodes( List<DNVNode> sortedMustDrawLabelNodes )
	{
		this.sortedMustDrawLabelNodes = sortedMustDrawLabelNodes;
	}

	/**
	 * Gets the sorted must draw label nodes.
	 * 
	 * @return the sorted must draw label nodes
	 */
	public List<DNVNode> getSortedMustDrawLabelNodes()
	{
		return sortedMustDrawLabelNodes;
	}
}
