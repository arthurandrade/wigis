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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The Class APLookup.
 * 
 * @author johno
 */
public class APLookup
{

	/**
	 * Gets the publication id from title.
	 * 
	 * @param title
	 *            the title
	 * @return the publication id from title
	 */
	public static Integer getPublicationIdFromTitle( String title )
	{
		DataManager dbm = DataManager.getInstance();

		String query = "select * from titleidmap where title like '%" + title + "%';";
		ResultSet res = dbm.getResults( query );
		Integer id;
		try
		{
			res.first();
			id = new Integer( res.getInt( "id" ) );
		}
		catch( Exception e )
		{
			Logger.write( "Error looking up Publication id from title" );
			Logger.write( "db query was:  " + query );
			// e.printStackTrace();
			return null;
		}
		return id;
	}

	/**
	 * Gets the publication title from id.
	 * 
	 * @param id
	 *            the id
	 * @return the publication title from id
	 */
	public static String getPublicationTitleFromId( int id )
	{
		DataManager dbm = DataManager.getInstance();
		String query = "select * from titleidmap where id=" + id + ";";
		ResultSet res = dbm.getResults( query );
		String title = "";
		try
		{
			res.first();
			title = res.getString( "title" );
		}
		catch( Exception e )
		{
			Logger.write( "Error looking up title from Publication id" );
			Logger.write( "db query was:  " + query );
			// e.printStackTrace();
			return null;
		}
		return title;
	}

	/**
	 * Gets the author name from id.
	 * 
	 * @param id
	 *            the id
	 * @return the author name from id
	 */
	public static String getAuthorNameFromId( int id )
	{
		DataManager dbm = DataManager.getInstance();
		String query = "select * from authoridmap where id=" + id + ";";
		ResultSet res = dbm.getResults( query );
		String name = "";
		try
		{
			res.first();
			name = res.getString( "author" );
		}
		catch( Exception e )
		{
			Logger.write( "Error looking up author name from id" );
			Logger.write( "db query was:  " + query );
			// e.printStackTrace();
			return null;
		}
		return name;
	}

	/**
	 * Gets the author id from author name.
	 * 
	 * @param name
	 *            the name
	 * @return the author id from author name
	 */
	public static Integer getAuthorIdFromAuthorName( String name )
	{
		DataManager dbm = DataManager.getInstance();
		String query = "select * from authoridmap where author='" + name + "';";
		ResultSet res = dbm.getResults( query );
		Integer id;
		try
		{
			res.first();
			id = new Integer( res.getInt( "id" ) );
		}
		catch( Exception e )
		{
			Logger.write( "Error looking up id from author name" );
			Logger.write( "db query was:  " + query );
			// e.printStackTrace();
			return null;
		}
		return id;
	}

	/**
	 * Gets the co authors by author id.
	 * 
	 * @param id
	 *            the id
	 * @return the co authors by author id
	 */
	public static ArrayList<Author> getCoAuthorsByAuthorId( int id )
	{
		ArrayList<Author> co = null;

		return co;
	}

	/**
	 * Gets the all authors from publication id.
	 * 
	 * @param id
	 *            the id
	 * @return the all authors from publication id
	 */
	public static ArrayList<Author> getAllAuthorsFromPublicationId( int id )
	{
		ArrayList<Author> a = new ArrayList<Author>();
		DataManager dbm = DataManager.getInstance();
		// get all titles an author was on
		String query = "select authorid from titleauthormap where titleid='" + id + "';";
		ResultSet res = dbm.getResults( query );
		try
		{
			Author author = null;
			res.beforeFirst();
			while( res.next() )
			{
				try
				{

					author = new Author( res.getInt( "authorid" ) );
					// Logger.write( "author is: " + author.getName() );
					a.add( author );
				}
				catch( Exception e )
				{
					Logger.write( "Error getting all authors from publication id" );
					Logger.write( "db query was:  " + query );
					// e.printStackTrace();
					return null;
				}
			}

		}
		catch( SQLException e1 )
		{
			// TODO Auto-generated catch block
			// e1.printStackTrace();
		}
		return a;
	}

	/**
	 * Gets the all publications from author id.
	 * 
	 * @param id
	 *            the id
	 * @return the all publications from author id
	 */
	public static ArrayList<Publication> getAllPublicationsFromAuthorId( int id )
	{
		ArrayList<Publication> pubs = new ArrayList<Publication>();
		DataManager dbm = DataManager.getInstance();
		String query = "select * from titleauthormap where authorid='" + id + "';";
		ResultSet res = dbm.getResults( query );
		try
		{
			Publication p = null;
			res.beforeFirst();
			while( res.next() )
			{
				try
				{

					p = new Publication( res.getInt( "titleid" ) );
					// p.setTitle( res.getString( "title" ) );
					// Logger.write( "author is: " + author.getName() );
					p.setAuthors();
					String title = getPublicationTitleFromId( p.getId() );
					p.setTitle( title );
					p.setSelected( true );
					pubs.add( p );

				}
				catch( Exception e )
				{
					Logger.write( "Error getting all publications from author id" );
					Logger.write( "db query was:  " + query );
					// e.printStackTrace();
					return null;
				}
			}

		}
		catch( SQLException e1 )
		{
			// TODO Auto-generated catch block
			Logger.write( "Some Really Strange APLookup Error Occurred" );
			Logger.write( "db query was:  " + query );
			e1.printStackTrace();
		}
		return pubs;
	}

}
