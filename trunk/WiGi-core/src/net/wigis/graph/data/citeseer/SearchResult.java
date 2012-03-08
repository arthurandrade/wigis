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
 * The Class SearchResult.
 * 
 * @author johno
 */
public class SearchResult
{

	/** The author string. */
	String authorString = "not set";

	/** The authors. */
	ArrayList<String> authors;

	/** The title. */
	String title;

	/** The selected. */
	boolean selected = false;

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
	 * Instantiates a new search result.
	 * 
	 * @param a
	 *            the a
	 * @param t
	 *            the t
	 */
	public SearchResult( ArrayList<String> a, String t )
	{
		Logger.write( "in constr of searchresult" + a );
		this.authors = a;
		this.title = t;
	}

	/*
	 * to add authors incrementally
	 */
	/**
	 * Instantiates a new search result.
	 * 
	 * @param t
	 *            the t
	 */
	public SearchResult( String t )
	{
		this.title = t;
		this.authors = new ArrayList<String>();
	}

	/*
	 * get returns the author array as a string, for richfaces datatable
	 */
	/**
	 * Gets the authors.
	 * 
	 * @return the authors
	 */
	public String getAuthors()
	{
		return "";
	}

	/**
	 * Gets the author string.
	 * 
	 * @return the author string
	 */
	public String getAuthorString()
	{
		String s = "";
		for( int i = 0; i < authors.size(); i++ )
		{
			s = s + authors.get( i ) + ", ";
		}
		// Logger.write( "author string is: " + s);
		s = s.substring( 0, s.length() - 1 );
		return s;

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

	/**
	 * Adds the author.
	 * 
	 * @param a
	 *            the a
	 */
	public void addAuthor( String a )
	{
		authors.add( a );
		// Logger.write( "added author: " +a );
	}

	/**
	 * Sets the authors.
	 * 
	 * @param a
	 *            the new authors
	 */
	public void setAuthors( ArrayList<String> a )
	{
		this.authors = a;
	}

	/**
	 * Gets the title.
	 * 
	 * @return the title
	 */
	public String getTitle()
	{
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

}
