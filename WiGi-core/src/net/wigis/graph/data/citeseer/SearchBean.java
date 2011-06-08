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

import java.io.IOException;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import net.wigis.graph.GraphsBean;
import net.wigis.graph.PaintBean;
import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.utilities.Vector3D;
import net.wigis.settings.Settings;
import net.wigis.web.ContextLookup;

// TODO: Auto-generated Javadoc
/**
 * The Class SearchBean.
 * 
 * @author johno
 */
public class SearchBean
{

	/** The OUTPU t_ fil e_ path. */
	private final String OUTPUT_FILE_PATH = Settings.GRAPHS_PATH;

	/** The OUTPU t_ fil e_ extension. */
	private final String OUTPUT_FILE_EXTENSION = ".dnv";

	/** The IF v_ url. */
	private final String IFV_URL = "/WiGi/wigi/WiGiViewerPanel.faces";

	/** The PUBLICATIO n_ nod e_ radius. */
	private final int PUBLICATION_NODE_RADIUS = 3;

	/** The CITESEE r_ searc h_ prefix. */
	private final String CITESEER_SEARCH_PREFIX = "Experimental_Citeseer_";

	/** The AUTHO r_ nod e_ radius. */
	private final int AUTHOR_NODE_RADIUS = 2;

	/** The search query. */
	public String searchQuery = "";

	/** The DEFAUL t_ ma x_ results. */
	private final int DEFAULT_MAX_RESULTS = 150;

	/** The DEFAUL t_ degre e_ o f_ seperation. */
	private final int DEFAULT_DEGREE_OF_SEPERATION = 1;

	/** The MA x_ searc h_ results. */
	private final int MAX_SEARCH_RESULTS = 20;

	/** The max results. */
	private int maxResults = DEFAULT_MAX_RESULTS;

	/** The article check box. */
	public boolean articleCheckBox = false;

	/** The author check box. */
	public boolean authorCheckBox = true;

	/** The abstract check box. */
	public boolean abstractCheckBox = true;

	/** The deg sep. */
	public int degSep = 0;

	/** The render result table. */
	public boolean renderResultTable = false;

	/** The found. */
	private ArrayList<String> found;

	/** The degrees of separation. */
	public int degreesOfSeparation = DEFAULT_DEGREE_OF_SEPERATION;

	/** The message. */
	public String message = "";

	/** The show tool tips. */
	public boolean showToolTips = true;

	/**
	 * Checks if is show tool tips.
	 * 
	 * @return true, if is show tool tips
	 */
	public boolean isShowToolTips()
	{
		return showToolTips;
	}

	/**
	 * Sets the show tool tips.
	 * 
	 * @param showToolTips
	 *            the new show tool tips
	 */
	public void setShowToolTips( boolean showToolTips )
	{
		this.showToolTips = showToolTips;
	}

	/**
	 * Gets the message.
	 * 
	 * @return the message
	 */
	public String getMessage()
	{
		return message;
	}

	/**
	 * Sets the message.
	 * 
	 * @param message
	 *            the new message
	 */
	public void setMessage( String message )
	{
		this.message = message;
	}

	/**
	 * Gets the degrees of separation.
	 * 
	 * @return the degrees of separation
	 */
	public int getDegreesOfSeparation()
	{
		return degreesOfSeparation;
	}

	/**
	 * Sets the degrees of separation.
	 * 
	 * @param degreesOfSeparation
	 *            the new degrees of separation
	 */
	public void setDegreesOfSeparation( int degreesOfSeparation )
	{
		this.degreesOfSeparation = degreesOfSeparation;
	}

	/**
	 * Checks if is render result table.
	 * 
	 * @return true, if is render result table
	 */
	public boolean isRenderResultTable()
	{
		return renderResultTable;
	}

	/**
	 * Sets the render result table.
	 * 
	 * @param renderResultTable
	 *            the new render result table
	 */
	public void setRenderResultTable( boolean renderResultTable )
	{
		this.renderResultTable = renderResultTable;
	}

	/*
	 * list to hold search results
	 */
	/** The search results. */
	public ArrayList<Publication> searchResults = new ArrayList<Publication>();

	/*
	 * singleton db manager instance
	 */

	/*
	 * init sample data on construct
	 */
	/**
	 * Instantiates a new search bean.
	 */
	public SearchBean()
	{

	}

	/**
	 * Gets the search query.
	 * 
	 * @return the search query
	 */
	public String getSearchQuery()
	{
		return searchQuery;
	}

	/**
	 * Sets the search query.
	 * 
	 * @param searchQuery
	 *            the new search query
	 */
	public void setSearchQuery( String searchQuery )
	{
		this.searchQuery = searchQuery;
	}

	/**
	 * Checks if is article check box.
	 * 
	 * @return true, if is article check box
	 */
	public boolean isArticleCheckBox()
	{
		return articleCheckBox;
	}

	/**
	 * Sets the article check box.
	 * 
	 * @param articleCheckBox
	 *            the new article check box
	 */
	public void setArticleCheckBox( boolean articleCheckBox )
	{
		this.articleCheckBox = articleCheckBox;
	}

	/**
	 * Checks if is author check box.
	 * 
	 * @return true, if is author check box
	 */
	public boolean isAuthorCheckBox()
	{
		return authorCheckBox;
	}

	/**
	 * Sets the author check box.
	 * 
	 * @param authorCheckBox
	 *            the new author check box
	 */
	public void setAuthorCheckBox( boolean authorCheckBox )
	{
		this.authorCheckBox = authorCheckBox;
	}

	/**
	 * Checks if is abstract check box.
	 * 
	 * @return true, if is abstract check box
	 */
	public boolean isAbstractCheckBox()
	{
		return abstractCheckBox;
	}

	/**
	 * Sets the abstract check box.
	 * 
	 * @param abstractCheckBox
	 *            the new abstract check box
	 */
	public void setAbstractCheckBox( boolean abstractCheckBox )
	{
		this.abstractCheckBox = abstractCheckBox;
	}

	/**
	 * Gets the deg sep.
	 * 
	 * @return the deg sep
	 */
	public int getDegSep()
	{
		return degSep;
	}

	/**
	 * Sets the deg sep.
	 * 
	 * @param degSep
	 *            the new deg sep
	 */
	public void setDegSep( int degSep )
	{
		this.degSep = degSep;
	}

	/** The dbm. */
	DataManager dbm = DataManager.getInstance();

	/**
	 * Gets the search query.
	 * 
	 * @return the search query
	 */
	public String getsearchQuery()
	{
		// loadDataList();
		return searchQuery;
	}

	/**
	 * Sets the search query.
	 * 
	 * @param default_search_query
	 *            the new search query
	 */
	public void setsearchQuery( String default_search_query )
	{
		searchQuery = default_search_query;
	}

	/**
	 * Search.
	 */
	public void search()
	{
		// clear the old search
		// can add functionality to "append" and build searches here
		this.searchResults = new ArrayList<Publication>();
		found = new ArrayList<String>();
		showMessage( "" );

		if( articleCheckBox )
		{
			searchByTitle();
		}
		if( authorCheckBox )
			searchByAuthor();

		found = null;
		System.gc();

	}

	/**
	 * Search by author.
	 */
	public void searchByAuthor()
	{
		Publication p = null;

		String query = "";

		// handle more than one word (currently only handles 2. This should be
		// put in a loop to handle N query terms)
		if( searchQuery.indexOf( ' ' ) > -1 )
		{
			String q1 = searchQuery.substring( 0, searchQuery.indexOf( ' ' ) );
			String q2 = searchQuery.substring( searchQuery.indexOf( ' ' ), searchQuery.length() );
			query = "select * from authoridmap where author like '%" + q1.trim() + "%' and author like '%" + q2.trim() + "%'  limit "
					+ MAX_SEARCH_RESULTS + ";";
		}
		else
		{
			query = "select * from authoridmap where author like '%" + searchQuery + "%' limit " + MAX_SEARCH_RESULTS + ";";
		}

		log( query );
		ResultSet res = dbm.getResults( query );
		try
		{
			// Author author = null;
			String title = "";
			res.beforeFirst();
			while( res.next() )
			{ // for all the authors in the result list
				// author = new Author( res.getString( "author" ) );
				// get the author id
				String id = res.getString( "id" );
				// get all the publications this author wrote
				query = "select titleid from titleauthormap where authorid = '" + id + "';";
				log( query );
				ResultSet res2 = dbm.getResults( query );
				res2.beforeFirst();
				while( res2.next() )
				{ // for every publication the current author wrote
					id = res2.getString( "titleid" );
					// if we haven't already added it
					if( !found.contains( id ) )
					{
						query = "select title from titleidmap where id = '" + id + "';";
						log( query );
						ResultSet res3 = dbm.getResults( query );
						res3.first();
						title = res3.getString( "title" );
						// get title of that publication and create a new
						// publication instance
						p = new Publication( title );
						p.setAuthors();
						found.add( id );
						this.searchResults.add( p );
					}
				}

			}
			// loadDataList();
		}
		catch( Exception e )
		{
			log( "in catch of search().  Problem reading result set: " + e );
		}
		// display the result table
		renderResultTable = true;
	}

	/**
	 * Search by title.
	 */
	public void searchByTitle()
	{

		String query = "select * from titleidmap where title like '%" + searchQuery + "%' limit " + MAX_SEARCH_RESULTS + ";";
		// handle more than one word (currently only handles 2. This should be
		// put in a loop to handle N query terms)
		if( searchQuery.indexOf( ' ' ) > -1 )
		{
			String q1 = searchQuery.substring( 0, searchQuery.indexOf( ' ' ) );
			String q2 = searchQuery.substring( searchQuery.indexOf( ' ' ), searchQuery.length() );
			query = "select * from titleidmap where title like '%" + q1.trim() + "%' and title like '%" + q2.trim() + "%'  limit "
					+ MAX_SEARCH_RESULTS + ";";
		}

		ResultSet res = dbm.getResults( query );

		try
		{
			String title = "";
			res.beforeFirst();
			Publication p = null;
			while( res.next() )
			{
				String id = res.getString( "id" );
				if( !found.contains( id ) )
				{
					title = res.getString( "title" );
					// log( title );
					p = null;
					p = new Publication( title );
					p.setAuthors();
					this.searchResults.add( p );
					found.add( id );
				}
			}
		}
		catch( Exception e )
		{
			log( "in catch of search().  Problem reading result set: " );
			// e.printStackTrace();
		}
		// display the result table
		renderResultTable = true;
	}

	/**
	 * Gets the search results.
	 * 
	 * @return the search results
	 */
	public List<Publication> getSearchResults()
	{
		// if( FacesContext.getCurrentInstance().getRenderResponse() )
		// {
		// loadDataList(); // Reload to get most recent data.
		// }
		return searchResults;
	}

	/**
	 * Sets the search results.
	 * 
	 * @param searchResults
	 *            the new search results
	 */
	public void setSearchResults( ArrayList<Publication> searchResults )
	{
		this.searchResults = searchResults;
	}

	/** The Constant DATE_FORMAT_NOW. */
	private static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH-mm-ss";

	/**
	 * Now.
	 * 
	 * @return the string
	 */
	private static String now()
	{
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat( DATE_FORMAT_NOW );
		return sdf.format( cal.getTime() );
	}

	/**
	 * Removes the non selected nodes.
	 */
	public void removeNonSelectedNodes()
	{
		ArrayList<Publication> temp = new ArrayList<Publication>();
		Publication p = null;
		Iterator<Publication> pubIterator = this.searchResults.iterator();
		while( pubIterator.hasNext() )
		{
			p = pubIterator.next();
			if( p.selected )
			{
				temp.add( p );
			}
		}
		this.searchResults = temp;
	}

	/**
	 * Graph it.
	 */
	public void graphIt()
	{
		showMessage( "Generating publication graph to " + degreesOfSeparation + " degrees of spearation." );
		Publication p = null;
		DNVGraph g = new DNVGraph();
		int count = 100000;
		log( "searchResults length is: " + searchResults.size() );
		// p = generateGraphFromPublicationsArray( p, g, count );
		DOSExpander dos = new DOSExpander();
		log( "max number results to return: " + maxResults );
		removeNonSelectedNodes();
		this.searchResults = dos.expand( g, searchResults, degreesOfSeparation, maxResults );
		p = generateGraphFromPublicationsArray( p, g, count );

		// generate a unique id for the graph
		int firstSpaceIndex = searchQuery.indexOf( " " );
		if( firstSpaceIndex == -1 )
			firstSpaceIndex = searchQuery.length();
		String fileName = searchQuery.substring( 0, firstSpaceIndex ) + " " + now();
		String selectedFile = OUTPUT_FILE_PATH + CITESEER_SEARCH_PREFIX + fileName + OUTPUT_FILE_EXTENSION;
		// this.searchResults = null;
		g.writeGraph( selectedFile );
		FacesContext fc = FacesContext.getCurrentInstance();
		PaintBean pb = (PaintBean)ContextLookup.lookup( "paintBean", fc );
		GraphsBean gb = (GraphsBean)ContextLookup.lookup( "graphsBean", fc );
		if( gb != null )
			gb.buildFileList();
		// set the graph value here
		pb.setSelectedFile( selectedFile );
		// Lay out the graph
		pb.runLayout();
		// Save the graph again
		pb.saveGraph();
		// Clear the original search info
		newSearch();
		// return the paintBean to the external context
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext ec = facesContext.getExternalContext();
		ec.getRequestMap().put( "paintBean", p );

		// redirect to visualizer
		try
		{
			showMessage( "" );
			ec.redirect( IFV_URL );

		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
	}

	/**
	 * Gets the max results.
	 * 
	 * @return the max results
	 */
	public int getMaxResults()
	{
		return maxResults;
	}

	/**
	 * Sets the max results.
	 * 
	 * @param maxResults
	 *            the new max results
	 */
	public void setMaxResults( int maxResults )
	{
		this.maxResults = maxResults;
	}

	/**
	 * Generate graph from publications array.
	 * 
	 * @param p
	 *            the p
	 * @param g
	 *            the g
	 * @param count
	 *            the count
	 * @return the publication
	 */
	private Publication generateGraphFromPublicationsArray( Publication p, DNVGraph g, int count )
	{
		Author a = null;
		DNVNode pub = null;
		DNVNode aut = null;
		DNVEdge edge = null;
		Iterator<Publication> iterator = this.searchResults.iterator();
		while( iterator.hasNext() )
		{
			p = iterator.next();

			if( p.selected )
			{
				pub = new DNVNode( g );
				pub.setLabel( p.getTitle() );
				pub.setRadius( PUBLICATION_NODE_RADIUS );
				pub.setPosition( (float)Math.random(), (float)Math.random() );
				pub.setColor( new Vector3D( 0.2f, 0.2f, 0.8f ) );
				g.addNode( 0, pub );

				// iterate for all authors in this result
				Iterator<Author> aiterator = p.getAuthors().iterator();
				aut = null;
				while( aiterator.hasNext() )
				{
					// check if the author already exists in the graph
					a = aiterator.next();
					if( g.getNodeById( a.getId() ) != null )
					{
						aut = (DNVNode)g.getNodeById( a.getId() );
					}
					else
					{
						aut = new DNVNode( g );
						aut.setId( a.getId() );
						aut.setLabel( a.getName() );
						aut.setRadius( AUTHOR_NODE_RADIUS );
						aut.setPosition( (float)Math.random(), (float)Math.random() );
						aut.setColor( new Vector3D( 0.5f, 0.7f, 0.4f ) );
						g.addNode( 0, aut );
					}

					edge = new DNVEdge( 0, DNVEdge.DEFAULT_RESTING_DISTANCE, false, aut, pub, g );
					edge.setId( count );
					g.addNode( 0, edge );
					log( "adding node and title: " + pub.getLabel() + " author: " + aut.getLabel() );
					count++;
				}
			}
		}
		return p;
	}

	/*
	 * displays a message in the interface grabs the external context so can be
	 * called from anywhere
	 */
	/**
	 * Show message.
	 * 
	 * @param s
	 *            the s
	 */
	public static void showMessage( String s )
	{
		log( s );
		FacesContext fc = FacesContext.getCurrentInstance();
		SearchBean sb = (SearchBean)ContextLookup.lookup( "searchBean", fc );
		sb.setMessage( s );

	}

	/**
	 * New search.
	 */
	public void newSearch()
	{
		this.searchResults = new ArrayList<Publication>();
		setRenderResultTable( false );

	}

	/**
	 * Select all.
	 */
	public void selectAll()
	{
		log( "selected all nodes in list" );

		Iterator<Publication> iterator = this.searchResults.iterator();
		while( iterator.hasNext() )
		{
			Publication p = iterator.next();
			if( p.selected == true )
				p.selected = false;
			else
				p.selected = true;

		}
	}

	/**
	 * Log.
	 * 
	 * @param s
	 *            the s
	 */
	private static void log( String s )
	{
	// System.out.println("BibtexToSQL:  "+s);
	}
}