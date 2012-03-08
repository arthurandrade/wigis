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

package net.wigis.web;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// TODO: Auto-generated Javadoc
/**
 * The Class ServletHelper.
 * 
 * @author Brynjar Gretarsson
 */
public final class ServletHelper
{

	/**
	 * Write string to response.
	 * 
	 * @param response
	 *            the response
	 * @param string
	 *            the string
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void writeStringToResponse( HttpServletResponse response, String string ) throws IOException
	{
		Writer writer = new OutputStreamWriter( response.getOutputStream() );
		writer.write( string );
		writer.flush();
		writer.close();
	}

	/**
	 * Gets the int parameter.
	 * 
	 * @param parameterName
	 *            the parameter name
	 * @param request
	 *            the request
	 * @return the int parameter
	 */
	public static int getIntParameter( String parameterName, HttpServletRequest request )
	{
		String parameterStr = request.getParameter( parameterName );
		int result = 1;
		try
		{
			result = Integer.parseInt( parameterStr );
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Gets the double parameter.
	 * 
	 * @param parameterName
	 *            the parameter name
	 * @param request
	 *            the request
	 * @return the double parameter
	 */
	public static double getDoubleParameter( String parameterName, HttpServletRequest request )
	{
		String parameterStr = request.getParameter( parameterName );
		double result = 1;
		try
		{
			result = Double.parseDouble( parameterStr );
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Gets the float parameter.
	 * 
	 * @param parameterName
	 *            the parameter name
	 * @param request
	 *            the request
	 * @return the float parameter
	 */
	public static float getFloatParameter( String parameterName, HttpServletRequest request )
	{
		String parameterStr = request.getParameter( parameterName );
		float result = 1;
		try
		{
			result = Float.parseFloat( parameterStr );
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Gets the boolean parameter.
	 * 
	 * @param parameterName
	 *            the parameter name
	 * @param request
	 *            the request
	 * @return the boolean parameter
	 */
	public static boolean getBooleanParameter( String parameterName, HttpServletRequest request )
	{
		String parameterStr = request.getParameter( parameterName );
		boolean result = false;
		try
		{
			result = Boolean.parseBoolean( parameterStr );
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		return result;
	}
}
