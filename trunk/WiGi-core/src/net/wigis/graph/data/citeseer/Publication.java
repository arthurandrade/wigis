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
import java.util.Iterator;

// TODO: Auto-generated Javadoc
/**
 * The Class Publication.
 * 
 * @author johno
 */
public class Publication
{

	/** The id. */
	private Integer id;

	/** The title. */
	private String title;

	/** The authors. */
	private ArrayList<Author> authors;

	/** The selected. */
	boolean selected = false;

	/** The author string. */
	private String authorString;

	/**
	 * Instantiates a new publication.
	 * 
	 * @param id
	 *            the id
	 */
	public Publication( int id )
	{
		this.id = new Integer( id );

	}

	/**
	 * Instantiates a new publication.
	 * 
	 * @param title
	 *            the title
	 */
	public Publication( String title )
	{
		this.id = APLookup.getPublicationIdFromTitle( title );
		this.title = title;
	}

	/**
	 * Checks if is selected.
	 * 
	 * @return true, if is selected
	 */
	public boolean isSelected()
	{
		return selected;
	}

	/**
	 * Sets the selected.
	 * 
	 * @param selected
	 *            the new selected
	 */
	public void setSelected( boolean selected )
	{
		this.selected = selected;
	}

	/**
	 * Gets the title.
	 * 
	 * @return the title
	 */
	public String getTitle()
	{
		if( title == null )
		{
			Logger.write( "Title is null. THis should not happen.   ...id: " + this.id );
			// title = title.replaceAll("\\}","");
			// title = title.replaceAll("\\{","");
		}
		return title;
	}

	/**
	 * Sets the title.
	 * 
	 * @param title
	 *            the new title
	 */
	public void setTitle( String title )
	{
		this.title = title;
	}

	/**
	 * Gets the authors.
	 * 
	 * @return the authors
	 */
	public ArrayList<Author> getAuthors()
	{
		return authors;
	}

	/**
	 * Adds the author.
	 * 
	 * @param a
	 *            the a
	 */
	public void addAuthor( Author a )
	{
		if( authors == null )
		{
			authors = new ArrayList<Author>();
		}
		this.authors.add( a );
		if( this.authorString == null )
			this.authorString = "";
		this.authorString = this.authorString + ", " + a.getName();
	}

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	public Integer getId()
	{
		return id;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id
	 *            the new id
	 */
	public void setId( Integer id )
	{
		this.id = id;
	}

	/**
	 * Gets the author string.
	 * 
	 * @return the author string
	 */
	public String getAuthorString()
	{
		if( authorString.startsWith( "," ) )
			authorString = authorString.substring( 1, authorString.length() );

		return authorString;
	}

	/**
	 * Sets the author string.
	 * 
	 * @param authorString
	 *            the new author string
	 */
	public void setAuthorString( String authorString )
	{
		this.authorString = authorString;
	}

	/*
	 * sets the authors on this object
	 */
	/**
	 * Sets the authors.
	 */
	public void setAuthors()
	{

		this.authors = APLookup.getAllAuthorsFromPublicationId( this.id );
		Iterator<Author> iterator = this.authors.iterator();
		this.authorString = "";
		while( iterator.hasNext() )
		{
			Author a = iterator.next();
			this.authorString = this.authorString + ", " + a.getName();
			// Logger.write( "author string is:  " + authorString);
		}
		// trim leading comma
		if( this.authorString.length() > 0 )
			this.authorString = this.authorString.substring( 1, this.authorString.length() );
	}
}