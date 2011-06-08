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

import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.utilities.GraphFunctions;
import net.wigis.graph.dnv.utilities.SortByXPosition;
import net.wigis.graph.dnv.utilities.SortByYPosition;

// TODO: Auto-generated Javadoc
/**
 * The Class TwoDTreeNode.
 * 
 * @author Brynjar Gretarsson
 */
public class TwoDTreeNode
{
	// B = bucket size
	/** The Constant B. */
	private static final int B = 50;

	/** The leaf. */
	private boolean leaf = false;

	/** The nodes. */
	private List<DNVNode> nodes = new ArrayList<DNVNode>();

	/** The center. */
	// private Vector2D center = new Vector2D();

	/** The radius. */
	// private float radius;

	/** The x sorted nodes. */
	private List<DNVNode> xSortedNodes = new ArrayList<DNVNode>();

	/** The y sorted nodes. */
	private List<DNVNode> ySortedNodes = new ArrayList<DNVNode>();

	/** The child1. */
	// private TwoDTreeNode child1;

	/** The child2. */
	// private TwoDTreeNode child2;

	/** The sbxp. */
	private static SortByXPosition sbxp = new SortByXPosition();

	/** The sbyp. */
	private static SortByYPosition sbyp = new SortByYPosition();

	/**
	 * Instantiates a new two d tree node.
	 * 
	 * @param nodes
	 *            the nodes
	 */
	public TwoDTreeNode( List<DNVNode> nodes )
	{
		if( nodes.size() < B )
		{
			this.leaf = true;
			this.nodes.addAll( nodes );
		}
		else
		{
			xSortedNodes.addAll( nodes );
			Collections.sort( xSortedNodes, sbxp );

			ySortedNodes.addAll( nodes );
			Collections.sort( ySortedNodes, sbyp );

			float width = GraphFunctions.getGraphWidth( nodes, false );
			float height = GraphFunctions.getGraphHeight( nodes, false );

			List<DNVNode> l1x = new ArrayList<DNVNode>();
			List<DNVNode> l2x = new ArrayList<DNVNode>();
			List<DNVNode> l1y = new ArrayList<DNVNode>();
			List<DNVNode> l2y = new ArrayList<DNVNode>();
			if( width > height )
			{
				float xSplitPosition = 0;
				for( int i = 0; i < xSortedNodes.size(); i++ )
				{
					if( i < xSortedNodes.size() / 2 )
					{
						l1x.add( xSortedNodes.get( i ) );
						xSplitPosition = xSortedNodes.get( i ).getPosition().getX();
					}
					else
					{
						l2x.add( xSortedNodes.get( i ) );
					}
				}

				DNVNode tempNode;
				for( int i = 0; i < ySortedNodes.size(); i++ )
				{
					tempNode = ySortedNodes.get( i );
					if( tempNode.getPosition().getX() <= xSplitPosition )
					{
						l1y.add( tempNode );
					}
					else
					{
						l2y.add( tempNode );
					}
				}
			}
			else
			{
				float ySplitPosition = 0;
				for( int i = 0; i < ySortedNodes.size(); i++ )
				{
					if( i < ySortedNodes.size() / 2 )
					{
						l1y.add( ySortedNodes.get( i ) );
						ySplitPosition = ySortedNodes.get( i ).getPosition().getY();
					}
					else
					{
						l2y.add( ySortedNodes.get( i ) );
					}
				}

				DNVNode tempNode;
				for( int i = 0; i < xSortedNodes.size(); i++ )
				{
					tempNode = xSortedNodes.get( i );
					if( tempNode.getPosition().getY() <= ySplitPosition )
					{
						l1x.add( tempNode );
					}
					else
					{
						l2x.add( tempNode );
					}
				}

			}
		}
	}

	/**
	 * Checks if is leaf.
	 * 
	 * @return true, if is leaf
	 */
	public boolean isLeaf()
	{
		return leaf;
	}

	/**
	 * Sets the leaf.
	 * 
	 * @param leaf
	 *            the new leaf
	 */
	public void setLeaf( boolean leaf )
	{
		this.leaf = leaf;
	}

}
