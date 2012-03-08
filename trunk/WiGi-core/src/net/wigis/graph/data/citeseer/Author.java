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

// TODO: Auto-generated Javadoc
/**
 * The Class Author.
 * 
 * @author johno
 */
public class Author
{

	/** The coauthors. */
	private ArrayList<Author> coauthors;

	/** The name. */
	private String name;

	/** The id. */
	private int id;

	/** The publications. */
	private ArrayList<Publication> publications;

	/**
	 * Instantiates a new author.
	 * 
	 * @param id
	 *            the id
	 */
	public Author( int id )
	{
		this.id = id;
		this.name = APLookup.getAuthorNameFromId( id );
	}

	/**
	 * Instantiates a new author.
	 * 
	 * @param name
	 *            the name
	 */
	public Author( String name )
	{
		this.name = name;
		this.id = APLookup.getAuthorIdFromAuthorName( name );
	}

	/**
	 * Gets the coauthors.
	 * 
	 * @return the coauthors
	 */
	public ArrayList<Author> getCoauthors()
	{
		return coauthors;
	}

	/**
	 * Sets the coauthors.
	 * 
	 * @param coauthors
	 *            the new coauthors
	 */
	public void setCoauthors( ArrayList<Author> coauthors )
	{
		this.coauthors = coauthors;
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
	 * Gets the id.
	 * 
	 * @return the id
	 */
	public int getId()
	{
		return id;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id
	 *            the new id
	 */
	public void setId( int id )
	{
		this.id = id;
	}

	/**
	 * Gets the publications.
	 * 
	 * @return the publications
	 */
	public ArrayList<Publication> getPublications()
	{
		return publications;
	}

	/**
	 * Sets the publications.
	 * 
	 * @param publications
	 *            the new publications
	 */
	public void setPublications( ArrayList<Publication> publications )
	{
		this.publications = publications;
	}

	/**
	 * Adds the publication.
	 * 
	 * @param p
	 *            the p
	 */
	public void addPublication( Publication p )
	{
		if( this.publications == null )
		{
			this.publications = new ArrayList<Publication>();
		}
		this.publications.add( p );
	}

}