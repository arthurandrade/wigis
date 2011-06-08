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

package net.wigis.graph.solr;

// import blackbook.web.wigi.graph.PaintBean;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.wigis.graph.BlackbookBean;
import net.wigis.graph.PaintBean;
import net.wigis.graph.solr.faceted.FacetedBean;

import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import blackbook.service.api.ResourceDecorator;

// TODO: Auto-generated Javadoc
// import blackbook.web.wigi.graph.AuthorizationPluginException;

/**
 * A managed bean that controls the instance of the Solr Server.
 * 
 * @author tkulish
 * @todo
 */
public class SolrBean
{

	/** The server. */
	protected static SolrServerJ server;

	/** The solr local. */
	private boolean solrLocal = false;

	/** The solr url. */
	private String solrURL = "http://127.0.0.1:8080/solr";

	/** The blackbook bean. */
	private static BlackbookBean blackbookBean;

	/** The faceted bean. */
	private FacetedBean facetedBean;

	/** The active_facet_field_names. */
	protected Collection<String> active_facet_field_names = null;

	// Solr Settings Page
	/** The expand solr. */
	private boolean expandSolr = false;

	/**
	 * Gets the faceted bean.
	 * 
	 * @return the faceted bean
	 */
	public FacetedBean getFacetedBean()
	{
		return facetedBean;
	}

	/**
	 * Sets the faceted bean.
	 * 
	 * @param facetedBean
	 *            the new faceted bean
	 */
	public void setFacetedBean( FacetedBean facetedBean )
	{
		this.facetedBean = facetedBean;
	}

	/**
	 * Gets the blackbook bean.
	 * 
	 * @return the blackbook bean
	 */
	public BlackbookBean getBlackbookBean()
	{
		return blackbookBean;
	}

	/**
	 * Sets the blackbook bean.
	 * 
	 * @param blackbookBean
	 *            the new blackbook bean
	 */
	public void setBlackbookBean( BlackbookBean blackbookBean )
	{
		SolrBean.blackbookBean = blackbookBean;
	}

	/**
	 * Instantiates a new solr bean.
	 */
	public SolrBean()
	{
	// long startTime = System.currentTimeMillis();
	}

	/**
	 * Checks if is solr local.
	 * 
	 * @return true, if is solr local
	 */
	public boolean isSolrLocal()
	{
		return solrLocal;
	}

	/**
	 * Sets the solr local.
	 * 
	 * @param solrLocal
	 *            the new solr local
	 */
	public void setSolrLocal( final boolean solrLocal )
	{
		if( this.solrLocal != solrLocal )
		{
			this.solrLocal = solrLocal;
			// loadBlackbookSDK();
		}
	}

	/**
	 * Checks if is expand solr.
	 * 
	 * @return true, if is expand solr
	 */
	public boolean isExpandSolr()
	{
		return expandSolr;
	}

	/**
	 * Sets the expand solr.
	 * 
	 * @param expandSolr
	 *            the new expand solr
	 */
	public void setExpandSolr( final boolean expandSolr )
	{
		if( this.expandSolr != expandSolr )
		{
			this.expandSolr = expandSolr;

		}
	}

	/**
	 * Expand solr.
	 */
	public void expandSolr()
	{
		setExpandSolr( true );
	}

	/**
	 * Collapse solr.
	 */
	public void collapseSolr()
	{
		setExpandSolr( false );
	}

	/**
	 * Takes a SolrDocumentList and turns it into a Map that contains
	 * ResourceDecortor.
	 * 
	 * @param temp_list
	 *            the temp_list
	 * @return Map<String, Resource Decorator> -
	 * @todo 1) Map we create is very basic.
	 */
	protected Map<String, ResourceDecorator> create_map( SolrDocumentList temp_list )
	{
		Map<String, ResourceDecorator> solr_map = new HashMap<String, ResourceDecorator>();

		Iterator<SolrDocument> iterator = temp_list.iterator();

		while( iterator.hasNext() )
		{
			SolrDocument doc = iterator.next();
			String id = doc.getFieldValue( "id" ).toString();
			String name = doc.getFieldValue( "name" ).toString();
			System.err.println( "SolrDocument: " + doc.toString() + " : OBJECT: " + name );
			ResourceDecorator rd = new ResourceDecorator();
			rd.setUri( name );
			rd.setLabel( name );

			solr_map.put( id, rd );
		}

		return solr_map;
	}

	/**
	 * Loads the Graph given a Map<,>.
	 * 
	 * @param resultList
	 *            - a valid map containing id string and ResourceDecorator
	 * @todo Right now this is a basic copy from the
	 *       blackbook.web.wigi.graph.BlackbookBean.java loadGraph function.
	 *       Need to change this to implement Solr data correctly.
	 */
	public void loadGraph( Map<String, ResourceDecorator> resultList )
	{
		blackbookBean.getPaintBean().getGraph().removeAllNodes( (int)blackbookBean.getPaintBean().getLevel() );
		blackbookBean.getPaintBean().getGraph().removeAllEdges( (int)blackbookBean.getPaintBean().getLevel() );
		blackbookBean.getPaintBean().getGraph().updateNodes( (int)blackbookBean.getPaintBean().getLevel(), resultList );
		blackbookBean.getPaintBean().removeStatusMessage( PaintBean.UNSAVED_CHANGES_MSG );
		blackbookBean.getPaintBean().setupStatusMessage( "Added " + resultList.size() + " nodes" );
	}

	/**
	 * Store_facet_field_names.
	 * 
	 * @param facet_list
	 *            the facet_list
	 */
	protected void store_facet_field_names( SolrDocumentList facet_list )
	{
		active_facet_field_names = new HashSet<String>();

		for( SolrDocument doc : facet_list )
		{
			System.err.println( " NEW DOCUMENT" );
			System.err.println( "SolrDocument: " + doc.toString() );
			if( doc != null )
			{
				for( String field_name : doc.getFieldNames() )
				{
					active_facet_field_names.add( field_name );
					// System.err.println("FIELD: " + field_name);
				}
			}
		}

		// PRINT HASH SET TO MAKE SURE WE ONLY HAVE ONE OF EACH FIELD NAME
		for( String name : active_facet_field_names )
		{
			System.err.println( "FIELD NAME: " + name );
		}
	}

	/**
	 * This function takes test data stored locally, loads it into the solr
	 * server, queries the solr server for the data, creates a graph from the
	 * data, then builds the faceted search area.
	 * 
	 * @todo 1)
	 */
	public void loadSolrTestData()
	{
		long startTime = System.currentTimeMillis();
		try
		{
			server = new SolrServerJ( solrURL );
			server.connect();
			server.deleteEverything();

			// server.addXMLData("C:\\Users\\ksage\\workspace\\WiGi\\src\\blackbook\\web\\wigi\\graph\\solr\\test\\SimpleTest.xml");
			// String xml_data_path = getServletContext().getRealPath
			/*
			 * Made it so you can grab the xml from a URL. Easy for testing and
			 * moving the code around.
			 */
			// server.addXMLData(new URL(server.getUrl() +
			// "/solr_test/SimpleTest.xml"));
			server.addXMLData( new URL( solrURL + "/solr_test/SimpleTest.xml" ) );
			// Query Solr, ALL items.
			SolrDocumentList templist = server.query( "*:*" );

			// Find all the facet field names, UGLY.
			store_facet_field_names( templist );

			Map<String, ResourceDecorator> solr_map = create_map( templist );
			loadGraph( solr_map );

			// Query Solr: Create Faceted Search Area
			List<FacetField> faceted_fields = server.faceted_query( "*:*", active_facet_field_names );
			facetedBean.setFacetListC( faceted_fields, true );

		}
		catch( Exception ex )
		{
			System.err.println( "\n\n\nCannot get local Solr Server Setup correctly: " + ex.getMessage() );
			ex.printStackTrace();
		}
		printTime( "\n\n\nloadSolrTestData", startTime );
	}

	/**
	 * Gets the solr url.
	 * 
	 * @return the solr url
	 */
	public String getSolrURL()
	{
		return solrURL;
	}

	/**
	 * Sets the solr url.
	 * 
	 * @param solrURL
	 *            the new solr url
	 */
	public void setSolrURL( String solrURL )
	{
		this.solrURL = solrURL;
	}

	/**
	 * Prints the time.
	 * 
	 * @param methodName
	 *            the method name
	 * @param startTime
	 *            the start time
	 */
	private static void printTime( String methodName, long startTime )
	{
		System.err.println( methodName + " took " + ( System.currentTimeMillis() - startTime ) / 1000 + " sec." );
	}
}
