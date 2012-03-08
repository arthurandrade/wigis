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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import net.wigis.graph.GraphsPathFilter;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.settings.Settings;

/**
 * @author brynjar
 * @param <E>
 *
 */
public class OrderedList<E> implements List<E>
{
	private Comparator<E> comparator;
	private List<E> list = new ArrayList<E>();
	
	public OrderedList( Comparator<E> comparator )
	{
		this.comparator = comparator;
	}
	
	public OrderedList( Comparator<E> comparator, Collection<E> collection )
	{
		this.comparator = comparator;
		addAll( collection );
	}
		
	/* (non-Javadoc)
	 * @see java.util.List#add(java.lang.Object)
	 */
	@Override
	public boolean add( E arg0 )
	{
		if( list.size() == 0 )
		{
			list.add( arg0 );
			return true;
		}
		
		if( add( arg0, 0, list.size() - 1 ) )
		{
			return true;
		}
		
		list.add( arg0 );
		return true;
	}
	
	private boolean add( E e, int startIndex, int endIndex )
	{
//		System.out.println( "Adding " + e + " between " + startIndex + " and " + endIndex );
		if( endIndex - startIndex <= 5 )
		{
			for( int i = startIndex; i <= endIndex; i++ )
			{
				if( comparator.compare( list.get( i ), e ) > 0 )
				{
					list.add( i, e );
					return true;
				}
			}
			
			return false;
		}
		else
		{
			int middle = startIndex + (endIndex - startIndex) / 2;
//			System.out.println( "startIndex:" + startIndex );
//			System.out.println( "middle:" + middle );
//			System.out.println( "endIndex:" + endIndex );
			boolean value = add( e, startIndex, middle );
			if( value )
			{
				return value;
			}
			value = add( e, middle + 1, endIndex );
			
			return value;
		}
	}

	/* (non-Javadoc)
	 * @see java.util.List#add(int, java.lang.Object)
	 */
	@Override
	public void add( int arg0, E arg1 )
	{
		list.add( arg0, arg1 );
	}

	/* (non-Javadoc)
	 * @see java.util.List#addAll(java.util.Collection)
	 */
	@Override
	public boolean addAll( Collection<? extends E> arg0 )
	{
		boolean returnValue = true;
		
		for( E e : arg0 )
		{
			returnValue = add( e ) && returnValue;
		}
		
		return returnValue;
	}

	/* (non-Javadoc)
	 * @see java.util.List#addAll(int, java.util.Collection)
	 */
	@Override
	public boolean addAll( int arg0, Collection<? extends E> arg1 )
	{
		return list.addAll( arg0, arg1 );
	}

	/* (non-Javadoc)
	 * @see java.util.List#clear()
	 */
	@Override
	public void clear()
	{
		list.clear();
	}

	/* (non-Javadoc)
	 * @see java.util.List#contains(java.lang.Object)
	 */
	@Override
	public boolean contains( Object arg0 )
	{
		return list.contains( arg0 );
	}

	/* (non-Javadoc)
	 * @see java.util.List#containsAll(java.util.Collection)
	 */
	@Override
	public boolean containsAll( Collection<?> arg0 )
	{
		return list.contains( arg0 );
	}

	/* (non-Javadoc)
	 * @see java.util.List#get(int)
	 */
	@Override
	public E get( int arg0 )
	{
		return list.get( arg0 );
	}

	/* (non-Javadoc)
	 * @see java.util.List#indexOf(java.lang.Object)
	 */
	@Override
	public int indexOf( Object arg0 )
	{
		return list.indexOf( arg0 );
	}

	/* (non-Javadoc)
	 * @see java.util.List#isEmpty()
	 */
	@Override
	public boolean isEmpty()
	{
		return list.isEmpty();
	}

	/* (non-Javadoc)
	 * @see java.util.List#iterator()
	 */
	@Override
	public Iterator<E> iterator()
	{
		return list.iterator();
	}

	/* (non-Javadoc)
	 * @see java.util.List#lastIndexOf(java.lang.Object)
	 */
	@Override
	public int lastIndexOf( Object arg0 )
	{
		return list.lastIndexOf( arg0 );
	}

	/* (non-Javadoc)
	 * @see java.util.List#listIterator()
	 */
	@Override
	public ListIterator<E> listIterator()
	{
		return list.listIterator();
	}

	/* (non-Javadoc)
	 * @see java.util.List#listIterator(int)
	 */
	@Override
	public ListIterator<E> listIterator( int arg0 )
	{
		return list.listIterator( arg0 );
	}

	/* (non-Javadoc)
	 * @see java.util.List#remove(java.lang.Object)
	 */
	@Override
	public boolean remove( Object arg0 )
	{
		return list.remove( arg0 );
	}

	/* (non-Javadoc)
	 * @see java.util.List#remove(int)
	 */
	@Override
	public E remove( int arg0 )
	{
		return list.remove( arg0 );
	}

	/* (non-Javadoc)
	 * @see java.util.List#removeAll(java.util.Collection)
	 */
	@Override
	public boolean removeAll( Collection<?> arg0 )
	{
		return list.remove( arg0 );
	}

	/* (non-Javadoc)
	 * @see java.util.List#retainAll(java.util.Collection)
	 */
	@Override
	public boolean retainAll( Collection<?> arg0 )
	{
		return list.retainAll( arg0 );
	}

	/* (non-Javadoc)
	 * @see java.util.List#set(int, java.lang.Object)
	 */
	@Override
	public E set( int arg0, E arg1 )
	{
		return list.set( arg0, arg1 );
	}

	/* (non-Javadoc)
	 * @see java.util.List#size()
	 */
	@Override
	public int size()
	{
		return list.size();
	}

	/* (non-Javadoc)
	 * @see java.util.List#subList(int, int)
	 */
	@Override
	public List<E> subList( int arg0, int arg1 )
	{
		return list.subList( arg0, arg1 );
	}

	/* (non-Javadoc)
	 * @see java.util.List#toArray()
	 */
	@Override
	public Object[] toArray()
	{
		return list.toArray();
	}

	/* (non-Javadoc)
	 * @see java.util.List#toArray(T[])
	 */
	@Override
	public <T> T[] toArray( T[] arg0 )
	{
		return list.toArray( arg0 );
	}
	
	public static void main( String args[] )
	{
		GraphsPathFilter.init();
		DNVGraph graph = new DNVGraph( Settings.GRAPHS_PATH + "_UCI_venezuela.dnv" );
		List<DNVNode> nodes = graph.getNodes( 0 );
		SortByLabelSize sbls = new SortByLabelSize( true );
		OrderedList<DNVNode> orderedList = new OrderedList<DNVNode>( sbls, nodes );
		Collections.sort( nodes, sbls );
		for( int i = 0; i < nodes.size(); i++ )
		{
			System.out.println( "\t" + i + " : '" + nodes.get( i ).getLabel() + "' vs. '" + orderedList.get( i ).getLabel() + "'" );			
		}
	}

}
