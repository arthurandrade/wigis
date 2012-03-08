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

package net.wigis.graph.data.citeseer;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import net.wigis.graph.dnv.DNVGraph;

// TODO: Auto-generated Javadoc
/**
 * The Class DOSExpander.
 * 
 * @author johno
 */
public class DOSExpander
{

	/*
	 * keeps a map of the nodes to avoid duplicates
	 */
	/** The unique node map. */
	private Map<Integer, Publication> uniqueNodeMap;

	/** The deg sep. */
	private int degSep;

	/** The max results. */
	private int maxResults;

	/** The nodecounter. */
	private int nodecounter = 0;

	/**
	 * Expand.
	 * 
	 * @param g
	 *            the g
	 * @param pubs
	 *            the pubs
	 * @param d
	 *            the d
	 * @param max
	 *            the max
	 * @return the array list
	 */
	public ArrayList<Publication> expand( DNVGraph g, ArrayList<Publication> pubs, int d, int max )
	{
		maxResults = max;
		this.uniqueNodeMap = new Hashtable<Integer, Publication>();
		degSep = d;
		SearchBean.showMessage( "DOSExpander: expanding seeds to " + degSep + " degrees of separation." );
		ArrayList<Publication> expandedList = new ArrayList<Publication>();

		// iterate for n degrees of separation

		ArrayList<Publication> newList = pubs;
		if( degSep == 0 )
			expandedList.addAll( newList );
		int start = degSep;
		int iter = 0;

		while( ( degSep > 0 ) && ( nodecounter < max ) )
		{
			iter = ( start - degSep ) + 1;
			SearchBean.showMessage( "Found " + newList.size() + " new items. Starting Iteration ..." + iter );
			newList = expandPublications( newList );
			expandedList.addAll( newList );
			nodecounter = nodecounter + newList.size();

			degSep--;
		}

		return expandedList;

	}

	/*
	 * this method returns an ArrayList of every publication by every author in
	 * the argument publication
	 */
	/**
	 * Expand publications.
	 * 
	 * @param publist
	 *            the publist
	 * @return the array list
	 */
	public ArrayList<Publication> expandPublications( ArrayList<Publication> publist )
	{
		ArrayList<Publication> expanded = new ArrayList<Publication>();

		// for every publication in this list
		Iterator<Publication> pubIterator = publist.iterator();
		while( pubIterator.hasNext() && nodecounter < maxResults )
		{
			Publication p = pubIterator.next();
			ArrayList<Author> authors = p.getAuthors();
			// iterate over all authors on that publication
			Iterator<Author> authorIterator = authors.iterator();
			// nodecounter = nodecounter+authors.size();
			while( authorIterator.hasNext() )
			{
				// get those author's publications into a list
				ArrayList<Publication> pubs = APLookup.getAllPublicationsFromAuthorId( authorIterator.next().getId() );
				// if( pubs != null )
				// {
				pubs = addToMapAndCheck( pubs );
				expanded.addAll( pubs );
				nodecounter = nodecounter + pubs.size();
				// Logger.write( "nodecounter is: " + nodecounter );
				pubs = null;
				System.gc();

				// }
			}

		}
		return expanded;
	}

	/**
	 * Adds the to map and check.
	 * 
	 * @param pubs
	 *            the pubs
	 * @return the array list
	 */
	public ArrayList<Publication> addToMapAndCheck( ArrayList<Publication> pubs )
	{
		Publication p = null;
		ArrayList<Publication> clean = new ArrayList<Publication>();
		Iterator<Publication> pubIterator = pubs.iterator();
		while( pubIterator.hasNext() )
		{
			p = pubIterator.next();
			if( !uniqueNodeMap.containsKey( p.getId() ) )
			{
				uniqueNodeMap.put( p.getId(), p );
				clean.add( p );
			}
		}
		return clean;
	}

	/**
	 * Gets the selected set.
	 * 
	 * @param pubs
	 *            the pubs
	 * @return the selected set
	 */
	public ArrayList<Publication> getSelectedSet( ArrayList<Publication> pubs )
	{

		Publication p = null;
		ArrayList<Publication> a = new ArrayList<Publication>();
		Iterator<Publication> iterator = pubs.iterator();
		while( iterator.hasNext() )
		{
			p = iterator.next();
			if( p.isSelected() )
				a.add( p );

		}
		return a;
	}
}
