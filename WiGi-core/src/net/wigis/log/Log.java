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

package net.wigis.log;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class Log.
 */
public class Log
{

	/** The logger. */
	Logger logger;

	/**
	 * Instantiates a new log.
	 */
	public Log()
	{
		try
		{
			logger = Logger.getLogger( "main" );

			// Create an appending file handler
			boolean append = true;
			FileHandler handler = new FileHandler( "logs/" + today() + ".log", append );
			logger.addHandler( handler );
		}
		catch( IOException e )
		{
			System.out.print( e );
		}
	}

	/**
	 * Out.
	 * 
	 * @param o
	 *            the o
	 */
	public void out( Object o )
	{
		logger.severe( o.toString() );

		// Log a few message at different severity levels
		logger.severe( "my severe message" );
		logger.warning( "my warning message" );
		logger.info( "my info message" );
		logger.config( "my config message" );
		logger.fine( "my fine message" );
		logger.finer( "my finer message" );
		logger.finest( "my finest message" );
	}

	/**
	 * Today.
	 * 
	 * @return the string
	 */
	private String today()
	{
		String DATE_FORMAT = "yyyy-MM-dd";
		SimpleDateFormat sdf = new SimpleDateFormat( DATE_FORMAT );
		Calendar c1 = Calendar.getInstance(); // today

		return sdf.format( c1.getTime() );
	}
}
