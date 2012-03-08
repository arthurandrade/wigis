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

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

// TODO: Auto-generated Javadoc
/**
 * The Class Request.
 * 
 * @author Brynjar Gretarsson
 */
public class Request
{

	/**
	 * Gets the boolean parameter.
	 * 
	 * @param parameterName
	 *            the parameter name
	 * @return the boolean parameter
	 */
	public static Boolean getBooleanParameter( String parameterName )
	{
		Boolean value = null;
		String stringValue = getStringParameter( parameterName );
		if( stringValue != null && !stringValue.equals( "" ) )
		{
			try
			{
				value = Boolean.parseBoolean( stringValue );
			}
			catch( NumberFormatException nfe )
			{
				nfe.printStackTrace();
			}
		}

		return value;
	}

	/**
	 * Gets the integer parameter.
	 * 
	 * @param parameterName
	 *            the parameter name
	 * @return the integer parameter
	 */
	public static Integer getIntegerParameter( String parameterName )
	{
		Integer value = null;
		String stringValue = getStringParameter( parameterName );
		if( stringValue != null && !stringValue.equals( "" ) )
		{
			try
			{
				value = Integer.parseInt( stringValue );
			}
			catch( NumberFormatException nfe )
			{
				nfe.printStackTrace();
			}
		}

		return value;
	}

	/**
	 * Gets the double parameter.
	 * 
	 * @param parameterName
	 *            the parameter name
	 * @return the double parameter
	 */
	public static Double getDoubleParameter( String parameterName )
	{
		Double value = null;
		String stringValue = getStringParameter( parameterName );
		if( stringValue != null && !stringValue.equals( "" ) )
		{
			try
			{
				value = Double.parseDouble( stringValue );
			}
			catch( NumberFormatException nfe )
			{
				nfe.printStackTrace();
			}
		}

		return value;
	}

	/**
	 * Gets the float parameter.
	 * 
	 * @param parameterName
	 *            the parameter name
	 * @return the float parameter
	 */
	public static Float getFloatParameter( String parameterName )
	{
		Float value = null;
		String stringValue = getStringParameter( parameterName );
		if( stringValue != null && !stringValue.equals( "" ) )
		{
			try
			{
				value = Float.parseFloat( stringValue );
			}
			catch( NumberFormatException nfe )
			{
				nfe.printStackTrace();
			}
		}

		return value;
	}

	/**
	 * Gets the string parameter.
	 * 
	 * @param parameterName
	 *            the parameter name
	 * @return the string parameter
	 */
	public static String getStringParameter( String parameterName )
	{
		String stringValue = null;
		FacesContext fc = FacesContext.getCurrentInstance();
		if( fc != null )
		{
			HttpServletRequest request = (HttpServletRequest)fc.getExternalContext().getRequest();
			stringValue = request.getParameter( parameterName );
		}
		else
		{
			// System.out.println( "WARNING: FacesContext is null!" );
		}

		return stringValue;
	}

	/**
	 * Gets the server name.
	 * 
	 * @return the server name
	 */
	public static String getServerName()
	{
		String stringValue = null;
		FacesContext fc = FacesContext.getCurrentInstance();
		if( fc != null )
		{
			HttpServletRequest request = (HttpServletRequest)fc.getExternalContext().getRequest();
			stringValue = request.getServerName();
		}

		return stringValue;
	}

	/**
	 * Gets the server port.
	 * 
	 * @return the server port
	 */
	public static Integer getServerPort()
	{
		Integer port = null;
		FacesContext fc = FacesContext.getCurrentInstance();
		if( fc != null )
		{
			HttpServletRequest request = (HttpServletRequest)fc.getExternalContext().getRequest();
			port = request.getServerPort();
		}

		return port;

	}
	
	public static String[] getParameterValues(String name) {
		String[] parameterValues = null;
		FacesContext fc = FacesContext.getCurrentInstance();
		if (fc != null) {
			HttpServletRequest request = (HttpServletRequest) fc
					.getExternalContext().getRequest();
			parameterValues = request.getParameterValues(name);
		}
		return parameterValues;
	}
}
