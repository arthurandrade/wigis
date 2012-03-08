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

package net.wigis.graph;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import net.wigis.graph.dnv.DNVEntity;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.settings.Settings;

// TODO: Auto-generated Javadoc
/**
 * The Class ImageGetter.
 * 
 * @author Brynjar Gretarsson
 */
public class ImageGetter extends Thread
{

	/** The url_part1. */
	private static String url_part1 = "http://boss.yahooapis.com/ysearch/images/v1/";

	/** The url_part2. */
	private static String url_part2 = "?appid=0OwlocbV34FPPmxJm7R8qmWhSBRZ6C4gcPHC66zD0I4wpZb1SvKLbN4_r2n3MQ--&filter=yes&count=5&format=xml";

	/** The query to result. */
	private static Map<String, String> queryToResult = loadQueryToResultMap();

	/** The node. */
	private DNVNode node;

	/** The query. */
	private String query;

	/**
	 * Instantiates a new image getter.
	 * 
	 * @param node
	 *            the node
	 * @param query
	 *            the query
	 */
	public ImageGetter( DNVNode node, String query )
	{
		this.node = node;
		this.query = query;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run()
	{
		String url = getImage( query );
		node.setIcon( url );
	}

	/**
	 * Gets the image.
	 * 
	 * @param query
	 *            the query
	 * @return the image
	 */
	public static String getImage( String query )
	{
		String imageUrl = null;

		query = cleanQuery( query );

		synchronized( queryToResult )
		{
			imageUrl = queryToResult.get( query );
			if( imageUrl != null )
			{
				return imageUrl;
			}

			imageUrl = "";

			String url = url_part1 + query + url_part2;

			String response = sendGetRequest( url );
			String startTag = "<thumbnail_url>";
			String endTag = "</thumbnail_url>";

			while( response != null && response.contains( startTag ) && response.contains( endTag ) )
			{
				imageUrl = response.substring( response.indexOf( startTag ) + startTag.length() );
				imageUrl = imageUrl.substring( 0, imageUrl.indexOf( endTag ) );

				try
				{
					if( ImageIO.read( new URL( imageUrl ) ) != null )
					{
						queryToResult.put( query, imageUrl );
						if( !Settings.DISABLE_IMAGES )
						{
							ImageCacher ic = new ImageCacher( imageUrl, query );
							ic.start();
						}
						break;
					}
				}
				catch( MalformedURLException e )
				{
					e.printStackTrace();
				}
				catch( IOException e )
				{
					e.printStackTrace();
				}

				response = response.substring( response.indexOf( "</result>" ) + 9 );
			}
		}

		return imageUrl;
	}

	/**
	 * Clean query.
	 * 
	 * @param query
	 *            the query
	 * @return the string
	 */
	public static String cleanQuery( String query )
	{
		query = query.trim();
		query = query.replaceAll( " ", "+" );
		query = DNVEntity.removeSpecialCharacters( query );
		return query;
	}

	/**
	 * Removes the.
	 * 
	 * @param query
	 *            the query
	 */
	public static void remove( String query )
	{
		synchronized( queryToResult )
		{
			queryToResult.remove( query );
		}
	}

	/**
	 * Load query to result map.
	 * 
	 * @return the map
	 */
	@SuppressWarnings("unchecked")
	private static Map<String, String> loadQueryToResultMap()
	{
		Map<String, String> queryToResult = new HashMap<String, String>();

		try
		{
			/* Open the file and set to read objects from it. */
			FileInputStream istream = new FileInputStream( Settings.GRAPHS_PATH + "queryToResult.dat" );

			ObjectInputStream q = new ObjectInputStream( istream );

			queryToResult = (HashMap<String, String>)q.readObject();
		}
		catch( FileNotFoundException e )
		{
			e.printStackTrace();
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		catch( ClassNotFoundException e )
		{
			e.printStackTrace();
		}

		System.out.println( "Query to Results size : " + queryToResult.size() );

		return queryToResult;
	}

	/**
	 * Save query to result map.
	 */
	public static void saveQueryToResultMap()
	{
		synchronized( queryToResult )
		{
			try
			{
				FileOutputStream ostream = new FileOutputStream( Settings.GRAPHS_PATH + "queryToResult.dat" );

				/* Create the output stream */
				ObjectOutputStream p = new ObjectOutputStream( ostream );

				p.writeObject( queryToResult );
				p.flush();
				ostream.close(); // close the file.
			}
			catch( FileNotFoundException e )
			{
				e.printStackTrace();
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * Sends an HTTP GET request to a url.
	 * 
	 * @param urlStr
	 *            the url str
	 * @return - The response from the end point
	 */
	public static String sendGetRequest( String urlStr )
	{
		String result = null;
		if( urlStr.startsWith( "http://" ) )
		{
			// Send a GET request to the servlet
			try
			{
				// Send data
				URL url = new URL( urlStr );
				URLConnection conn = url.openConnection();

				// Get the response
				BufferedReader rd = new BufferedReader( new InputStreamReader( conn.getInputStream() ) );
				StringBuilder sb = new StringBuilder();
				String line;
				while( ( line = rd.readLine() ) != null )
				{
					sb.append( line );
				}
				rd.close();
				result = sb.toString();
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
		}

		return result;
	}

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main( String args[] )
	{
		String url;
		url = getImage( "Forrest Gump" );
		System.out.println( url );
		url = getImage( "Metallica" );
		System.out.println( url );
		url = getImage( "The killers" );
		System.out.println( url );
	}
}
