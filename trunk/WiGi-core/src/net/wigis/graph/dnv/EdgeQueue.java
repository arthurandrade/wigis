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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.wigis.graph.dnv.utilities.Timer;

// TODO: Auto-generated Javadoc
/**
 * The Class EdgeQueue.
 * 
 * @author Brynjar Gretarsson
 */
public class EdgeQueue
{

	/** The queue. */
	private List<EdgeNodeAndValue> queue = new LinkedList<EdgeNodeAndValue>();

	/** The contains. */
	private Map<String, Boolean> contains = new HashMap<String, Boolean>();

	/** The max size. */
	public int maxSize = 0;

	/**
	 * Logger.
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
	// // private static Log logger = LogFactory.getLog( EdgeQueue.class );

	public void add( Map<Integer, Float> path, DNVNode node, DNVEdge edge, float value )
	{
		Float oldValue = path.get( node.getId() );
		if( oldValue == null || oldValue > value )
		{
			EdgeNodeAndValue enav = new EdgeNodeAndValue( path, node, edge, value );
			Boolean cont = contains.get( enav.getIdentifyingString() );
			if( cont == null || cont == false )
			{
				addTimer.setStart();
				add( enav, 0, queue.size() - 1 );
				if( queue.size() > maxSize )
					maxSize = queue.size();
				addTimer.setEnd();
			}
		}
	}

	/**
	 * Clear.
	 */
	public void clear()
	{
		queue.clear();
		addTimer.reset();
		maxSize = 0;
		contains.clear();
	}

	/**
	 * Checks if is empty.
	 * 
	 * @return true, if is empty
	 */
	public boolean isEmpty()
	{
		return queue.isEmpty();
	}

	/**
	 * Adds the all.
	 * 
	 * @param path
	 *            the path
	 * @param node
	 *            the node
	 * @param edges
	 *            the edges
	 * @param valueSoFar
	 *            the value so far
	 * @param useActualEdgeDistance
	 *            the use actual edge distance
	 */
	public void addAll( Map<Integer, Float> path, DNVNode node, List<DNVEdge> edges, float valueSoFar, boolean useActualEdgeDistance )
	{
		DNVEdge tempEdge;
		float addition;
		for( int i = 0; i < edges.size(); i++ )
		{
			tempEdge = edges.get( i );
			addition = 1;
			if( useActualEdgeDistance )
				addition = tempEdge.getRestingDistance();

			add( path, node, tempEdge, valueSoFar + addition );
		}
	}

	/** The add timer. */
	public Timer addTimer = new Timer( Timer.NANOSECONDS );

	/**
	 * Adds the.
	 * 
	 * @param enav
	 *            the enav
	 * @param min
	 *            the min
	 * @param max
	 *            the max
	 */
	private void add( EdgeNodeAndValue enav, int min, int max )
	{
		if( queue.size() == 0 )
		{
			contains.put( enav.getIdentifyingString(), true );
			queue.add( enav );
			return;
		}

		if( max - min <= 20 )
		{
			for( int i = min; i <= max; i++ )
			{
				if( queue.get( i ).getValue() >= enav.getValue() )
				{
					contains.put( enav.getIdentifyingString(), true );
					queue.add( i, enav );
					return;
				}
			}

			queue.add( queue.size(), enav );
			return;
		}

		int mid = ( min + max ) / 2;
		if( queue.get( mid ).getValue() < enav.getValue() )
		{
			add( enav, mid, max );
		}
		else
		{
			add( enav, min, mid );
		}
	}

	/**
	 * Pop.
	 * 
	 * @return the edge node and value
	 */
	public EdgeNodeAndValue pop()
	{
		return queue.remove( 0 );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		String string = "";
		for( int i = 0; i < queue.size(); i++ )
		{
			string += "[" + queue.get( i ).getValue() + "]";
		}

		return string;
	}

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main( String args[] )
	{
		EdgeQueue q = new EdgeQueue();

		q.add( null, null, null, 5.3f );
		q.add( null, null, null, 56.3f );
		q.add( null, null, null, 5.3987f );
		q.add( null, null, null, 2.3f );
		q.add( null, null, null, 5.3f );
		q.add( null, null, null, 78.3f );
		q.add( null, null, null, 5.653f );
		q.add( null, null, null, 5.31f );
		q.add( null, null, null, 5.301f );
		q.add( null, null, null, 3.3f );
		q.add( null, null, null, 2.3f );
		q.add( null, null, null, 3.3f );
		q.add( null, null, null, 6.3f );
		q.add( null, null, null, 1.3f );
		q.add( null, null, null, 6.3f );
		q.add( null, null, null, 5.301f );
		q.add( null, null, null, 3.3f );
		q.add( null, null, null, 2.3f );
		q.add( null, null, null, 3.3f );
		q.add( null, null, null, 6.3f );
		q.add( null, null, null, 10.3f );
		q.add( null, null, null, 6.3f );

		System.out.println( q );

	}
}
