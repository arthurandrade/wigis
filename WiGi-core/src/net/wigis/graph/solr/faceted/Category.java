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

package net.wigis.graph.solr.faceted;

import java.util.ArrayList;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class Category.
 * 
 * @author Lance Byrd
 */
public class Category
{

	/** The name. */
	private String name;

	/** The facets. */
	private List<Facet> facets = null;

	/**
	 * Instantiates a new category.
	 */
	Category()
	{
		name = "not assigned";
		facets = new ArrayList<Facet>();
	}

	/**
	 * Instantiates a new category.
	 * 
	 * @param name
	 *            the name
	 */
	Category( String name )
	{
		this.name = name;
		facets = new ArrayList<Facet>();
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
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String getname()
	{
		return name;
	}

	/**
	 * Gets the facets.
	 * 
	 * @return the facets
	 */
	public List<Facet> getFacets()
	{
		if( facets != null )
		{
			return facets;
		}
		else
		{
			System.err.println( name + " facets is NULL" );
			return null;
		}
	}

	/**
	 * Adds the facet.
	 * 
	 * @param newFacet
	 *            the new facet
	 */
	public void addFacet( Facet newFacet )
	{
		facets.add( newFacet );
	}
}
