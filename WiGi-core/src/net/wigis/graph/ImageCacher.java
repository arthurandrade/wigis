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

import java.awt.Image;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

// TODO: Auto-generated Javadoc
/**
 * The Class ImageCacher.
 * 
 * @author Brynjar Gretarsson
 */
public class ImageCacher extends Thread
{

	/** The url. */
	private String url;

	/** The query. */
	private String query = "";

	/**
	 * Instantiates a new image cacher.
	 * 
	 * @param url
	 *            the url
	 */
	public ImageCacher( String url )
	{
		this.url = url;
	}

	/**
	 * Instantiates a new image cacher.
	 * 
	 * @param url
	 *            the url
	 * @param query
	 *            the query
	 */
	public ImageCacher( String url, String query )
	{
		this.url = url;
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
		URL iconUrl;
		try
		{
			String tempUrl = url;
			if( !url.startsWith( "http" ) && constantsSet )
			{
				tempUrl = protocol + "://" + server + ":" + port + "/WiGi" + url;
			}
			Image image = ImageRenderer.getIcon( url );
			if( image == null )
			{
				iconUrl = new URL( tempUrl );
				image = ImageIO.read( iconUrl );
				if( image != null )
				{
					ImageRenderer.setIcon( url, image );
				}
				else if( query != null && !query.equals( "" ) )
				{
					System.out.println( "Image is null for " + query + "... url : " + url );
					ImageGetter.remove( query );
				}
			}
			ImageGetter.saveQueryToResultMap();
		}
		catch( MalformedURLException e )
		{
			e.printStackTrace();
		}
		catch( IOException e )
		{
			// e.printStackTrace();
		}
	}

	/** The server. */
	private static String server = "localhost";

	/** The port. */
	private static int port = 8080;

	/** The protocol. */
	private static String protocol = "http";

	/** The constants set. */
	private static boolean constantsSet = false;

	/**
	 * Update constants.
	 * 
	 * @param request
	 *            the request
	 */
	public static void updateConstants( HttpServletRequest request )
	{
		server = request.getServerName();
		port = request.getServerPort();
		StringBuffer url = request.getRequestURL();
		protocol = url.substring( 0, url.indexOf( ":" ) );
		constantsSet = true;
	}
}
