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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collection;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.request.DirectXmlRequest;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocumentList;

// TODO: Auto-generated Javadoc
/**
 * Creates a instance of the SolrServer that is implemented in
 * org.apache.solr.client.solrj.SolrServer
 * 
 * 
 * @author tkulish
 * @todo 1) Add StreamingUpdateSolrServer ability for large dataset 2) Close
 *       server. 3) Get status.
 */
public class SolrServerJ
{

	/** The url. */
	String url;

	/** The server. */
	SolrServer server;

	/** The port. */
	private int port = 8180;

	/**
	 * SolrServerJ Contructor Set the URL to default: http://127.0.0.1:8080/solr
	 * 
	 */
	SolrServerJ()
	{
		url = "http://127.0.0.1:8080/solr/";
	}

	/**
	 * SolServerJ Constructor that sets the URL to one provided.
	 * 
	 * @param url
	 *            the url
	 */
	SolrServerJ( String url )
	{
		this.url = url;
	}

	/**
	 * Sets the url.
	 * 
	 * @param url
	 *            the new url
	 */
	public void setUrl( String url )
	{
		this.url = url;
	}

	/**
	 * Gets the url.
	 * 
	 * @return the url
	 */
	public String getUrl()
	{
		return url;
	}

	/**
	 * Connects to the solr instance provided in the url field in the class.
	 * 
	 * @return boolean on a valid connection or not.
	 */
	public boolean connect()
	{
		try
		{
			server = new CommonsHttpSolrServer( url );
			return true;
		}
		catch( Exception e )
		{
			System.err.println( "Cannot get local Solr Server Setup correctly: " + e.getMessage() );
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Deletes everything in the Solr index. Use for clearing all data before
	 * loading.
	 */
	public void deleteEverything()
	{
		try
		{
			server.deleteByQuery( "*:*" );
		}
		catch( Exception e )
		{
			System.err.println( "Error trying to delete everything in Solr: " + e.getMessage() );
		}
	}

	/**
	 * Faceted_query.
	 * 
	 * @param the_query
	 *            the the_query
	 * @param facet_fields
	 *            the facet_fields
	 * @return the list
	 */
	public List<FacetField> faceted_query( String the_query, Collection<String> facet_fields )
	{
		try
		{

			// First we need to find out all the fields we "could" have.
			SolrQuery q = new SolrQuery().setQuery( the_query ).setFacet( true ).setFacetMinCount( 1 );

			// Loop through facet_fields and add them to the query:
			for( String field_name : facet_fields )
			{
				q.addFacetField( field_name );
			}

			QueryResponse rspx = new QueryResponse();

			rspx = server.query( q );

			return rspx.getFacetFields();

		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Queries the Solr DB with a provided query.
	 * 
	 * @param the_query
	 *            - a valid Solr query
	 * @return SolrDocumentList - the results from the query specified.
	 * @todo 1) Review queries that might need to be returned. It might not only
	 *       be a SolrDocumentList.
	 */
	public SolrDocumentList query( String the_query )
	{
		try
		{
			SolrQuery q = new SolrQuery();
			QueryResponse rsp = new QueryResponse();

			q.setQuery( the_query );
			q.addSortField( "object", SolrQuery.ORDER.asc );
			rsp = server.query( q );

			SolrDocumentList docs = rsp.getResults();

			// Should I iterate through the SolrDocumentList and return it in a
			// Map format.
			// No... lets keep this contained.
			return docs;
		}
		catch( Exception e )
		{
			System.err.println( "Query Error: " + e.getMessage() );
		}

		return null;
	}

	/**
	 * Adds XML Data from a file name into the Solr server.
	 * 
	 * @param filename
	 *            - a valid filename and path that contains valid solr data.
	 * @return boolean - wether the data was added correctly.
	 * 
	 */
	public boolean addXMLData( String filename )
	{
		String xml = readfile( filename );

		DirectXmlRequest request = new DirectXmlRequest( "/update", xml );
		try
		{
			@SuppressWarnings("unused")
			UpdateResponse response = request.process( server );
			response = server.commit();
		}
		catch( SolrServerException e )
		{
			e.printStackTrace();
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * Adds XML Data from a URL name into the Solr server.
	 * 
	 * @param uri
	 *            the uri
	 * @return boolean - wether the data was added correctly.
	 */
	public boolean addXMLData( URL uri )
	{
		String xml = readURL( uri );

		DirectXmlRequest request = new DirectXmlRequest( "/update", xml );
		try
		{
			@SuppressWarnings("unused")
			UpdateResponse response = request.process( server );
			response = server.commit();
		}
		catch( SolrServerException e )
		{
			e.printStackTrace();
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * Reads in a XML HTTP file and returns its contents.
	 * 
	 * @param uri
	 *            - valid URI
	 * @return String that contains the contents of the URI link
	 */
	private String readURL( URL uri )
	{
		StringBuilder contents = new StringBuilder();

		try
		{
			URL the_file = uri;

			BufferedReader br = new BufferedReader( new InputStreamReader( the_file.openStream() ) );

			String line = null;
			while( null != ( line = br.readLine() ) )
			{
				contents.append( line );
				contents.append( System.getProperty( "line.separator" ) );
			}
			br.close();
		}
		catch( Exception ie )
		{
			ie.printStackTrace();
		}
		return contents.toString();

	}

	/**
	 * Reads in a file, returns a String that contains its contents.
	 * 
	 * @param filename
	 *            - valid filename and path.
	 * @return String that contains the contents of the file.
	 */
	private String readfile( String filename )
	{
		StringBuilder contents = new StringBuilder();

		try
		{
			// read one line at a time.
			BufferedReader input = new BufferedReader( new FileReader( filename ) );
			try
			{
				String line = null;
				while( ( line = input.readLine() ) != null )
				{
					contents.append( line );
					contents.append( System.getProperty( "line.separator" ) );
				}
			}
			finally
			{
				input.close();
			}
		}
		catch( IOException ie )
		{
			ie.printStackTrace();
		}
		return contents.toString();
	}

	public void setPort( int port )
	{
		this.port = port;
	}

	public int getPort()
	{
		return port;
	}
}
