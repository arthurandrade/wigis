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

package net.wigis.graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.SubGraph;
import net.wigis.graph.dnv.utilities.GraphFunctions;
import net.wigis.graph.dnv.utilities.Vector2D;

// TODO: Auto-generated Javadoc
/**
 * The Class SubGraphFinder.
 * 
 * @author Brynjar Gretarsson
 */
public final class SubGraphFinder
{

	/**
	 * Gets the sub graph within.
	 * 
	 * @param graph
	 *            the graph
	 * @param level
	 *            the level
	 * @param minXPercent
	 *            the min x percent
	 * @param minYPercent
	 *            the min y percent
	 * @param maxXPercent
	 *            the max x percent
	 * @param maxYPercent
	 *            the max y percent
	 * @param whiteSpaceBuffer
	 *            the white space buffer
	 * @return the sub graph within
	 */
	public static SubGraph getSubGraphWithin( DNVGraph graph, int level, double minXPercent, double minYPercent, double maxXPercent,
			double maxYPercent, double whiteSpaceBuffer )
	{
		double originalMinX = GraphFunctions.getMinXPosition( graph, level, true );
		double originalMinY = GraphFunctions.getMinYPosition( graph, level, true );
		double originalMaxX = GraphFunctions.getMaxXPosition( graph, level, true );
		double originalMaxY = GraphFunctions.getMaxYPosition( graph, level, true );
		double yBuffer = ( originalMaxY - originalMinY ) * whiteSpaceBuffer;
		double xBuffer = ( originalMaxX - originalMinX ) * whiteSpaceBuffer;
		originalMaxY += yBuffer;
		originalMinY -= yBuffer;
		originalMaxX += xBuffer;
		originalMinX -= xBuffer;
		double height = originalMaxY - originalMinY;
		double width = originalMaxX - originalMinX;
		double minX = minXPercent * width + originalMinX;
		double maxX = maxXPercent * width + originalMinX;
		double minY = minYPercent * height + originalMinY;
		double maxY = maxYPercent * height + originalMinY;
		SubGraph subGraph = new SubGraph( graph, level );
		if( minXPercent == 0 && minYPercent == 0 && maxXPercent == 1 && maxYPercent == 1 )
		{
			subGraph.setEdges( graph.getVisibleEdges( level ) );
			subGraph.setNodes( graph.getVisibleNodes( level ) );
			return subGraph;
		}

		List<DNVNode> nodes = new ArrayList<DNVNode>( graph.getVisibleNodes( level ).values() );
		DNVNode tempNode;
		if( nodes != null )
		{
			for( Iterator<DNVNode> i = nodes.iterator(); i.hasNext(); )
			{
				tempNode = i.next();
				if( tempNode.isVisible() && isNodeWithin( tempNode, minX, minY, maxX, maxY ) )
				{
					// Add the node
					subGraph.add( tempNode );
					// Add all the neighbor nodes
					subGraph.addAllNodes( tempNode.getNeighbors( true ) );
					// Add the edges to all the neighbors
					subGraph.addAllEdges( tempNode.getFromEdges( true ) );
					subGraph.addAllEdges( tempNode.getToEdges( true ) );
				}
			}
		}

		return subGraph;
	}

	// Checks if the node is within the given boundary
	/**
	 * Checks if is node within.
	 * 
	 * @param tempNode
	 *            the temp node
	 * @param minX
	 *            the min x
	 * @param minY
	 *            the min y
	 * @param maxX
	 *            the max x
	 * @param maxY
	 *            the max y
	 * @return true, if is node within
	 */
	public static boolean isNodeWithin( DNVNode tempNode, double minX, double minY, double maxX, double maxY )
	{
		Vector2D position = tempNode.getPosition();

		if( position.getY() >= minY && position.getY() <= maxY && position.getX() >= minX && position.getX() <= maxX )
		{
			return true;
		}

		return false;
	}
}
