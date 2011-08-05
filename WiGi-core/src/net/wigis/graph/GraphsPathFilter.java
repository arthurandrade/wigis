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
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import net.wigis.settings.Settings;

// TODO: Auto-generated Javadoc
/**
 * The Class GraphsPathFilter.
 * 
 * @author Brynjar Gretarsson
 */
public class GraphsPathFilter implements Filter
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy()
	{
	// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
	 * javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter( ServletRequest arg0, ServletResponse arg1, FilterChain arg2 ) throws IOException, ServletException
	{
	// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init( FilterConfig arg0 ) throws ServletException
	{
		String jbossDir = System.getProperty( "jboss.server.home.dir" );
		if( jbossDir == null )
		{
			jbossDir = "";
		}
		else
		{
			jbossDir += "/conf/";
		}

		File settings = new File( jbossDir + "wigi-settings.txt" );
		if( settings.exists() )
		{
			FileReader fr = null;
			BufferedReader br = null;
			try
			{
				fr = new FileReader( settings );
				br = new BufferedReader( fr );

				String line;
				while( ( line = br.readLine() ) != null )
				{
					line = line.trim();
					if( line.startsWith( "GRAPHS_PATH=" ) )
					{
						Settings.GRAPHS_PATH = line.substring( 12 );
						if( !Settings.GRAPHS_PATH.endsWith( "\\" ) && !Settings.GRAPHS_PATH.endsWith( "/" ) )
						{
							Settings.GRAPHS_PATH += "/";
						}
						System.out.println( "Using graphs path: " + Settings.GRAPHS_PATH );
						return;
					}
				}
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
			finally
			{
				try
				{
					br.close();
					fr.close();
				}
				catch( IOException e )
				{
					e.printStackTrace();
				}
			}
		}

		if( File.separator.equals( "/" ) )
		{
			Settings.GRAPHS_PATH = Settings.MAC_GRAPHS_PATH;
		}
		else
		{
			Settings.GRAPHS_PATH = Settings.PC_GRAPHS_PATH;
		}

		System.out.println( "Using graphs path: " + Settings.GRAPHS_PATH );

//		__Print.header( "server init done" );
	}

	/**
	 * Inits the.
	 */
	public static void init()
	{
		GraphsPathFilter gpf = new GraphsPathFilter();
		try
		{
			gpf.init( null );
		}
		catch( ServletException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
