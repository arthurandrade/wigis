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

import java.util.HashMap;
import java.util.Map;

// TODO: Auto-generated Javadoc
/**
 * The Class EdgeNodeAndValue.
 * 
 * @author Brynjar Gretarsson
 */
public class EdgeNodeAndValue
{

	/** The path. */
	private Map<Integer, Float> path = new HashMap<Integer, Float>();

	/** The node. */
	private DNVNode node;

	/** The edge. */
	private DNVEdge edge;

	/** The value. */
	private float value;

	/**
	 * Instantiates a new edge node and value.
	 * 
	 * @param path
	 *            the path
	 * @param node
	 *            the node
	 * @param edge
	 *            the edge
	 * @param value
	 *            the value
	 */
	public EdgeNodeAndValue( Map<Integer, Float> path, DNVNode node, DNVEdge edge, float value )
	{
		this.path.putAll( path );
		this.node = node;
		this.edge = edge;
		this.value = value;
	}

	/**
	 * Gets the node.
	 * 
	 * @return the node
	 */
	public DNVNode getNode()
	{
		return node;
	}

	/**
	 * Gets the other node.
	 * 
	 * @return the other node
	 */
	public DNVNode getOtherNode()
	{
		if( edge.getFrom() == node )
			return edge.getTo();

		return edge.getFrom();
	}

	/**
	 * Gets the edge.
	 * 
	 * @return the edge
	 */
	public DNVEdge getEdge()
	{
		return edge;
	}

	/**
	 * Gets the value.
	 * 
	 * @return the value
	 */
	public float getValue()
	{
		return value;
	}

	/**
	 * Gets the path.
	 * 
	 * @return the path
	 */
	public Map<Integer, Float> getPath()
	{
		return path;
	}

	/**
	 * Gets the identifying string.
	 * 
	 * @return the identifying string
	 */
	public String getIdentifyingString()
	{
		return node.getId() + "->" + getOtherNode().getId();
	}
}
