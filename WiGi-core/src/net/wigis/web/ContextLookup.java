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

import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.el.VariableResolver;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

// TODO: Auto-generated Javadoc
/**
 * The Class ContextLookup.
 * 
 * @author Brynjar Gretarsson
 */
@SuppressWarnings("deprecation")
public class ContextLookup
{

	/**
	 * Lookup.
	 * 
	 * @param expression
	 *            the expression
	 * @param c
	 *            the c
	 * @return the object
	 */
	@SuppressWarnings( { "unchecked" })
	public static Object lookup( String expression, Class c )
	{
		try
		{
			expression = expression.replace( "#{", "" );
			expression = expression.replace( "}", "" );
			Application application = FacesContext.getCurrentInstance().getApplication();
			VariableResolver variableResolver = application.getVariableResolver();
			return variableResolver.resolveVariable( FacesContext.getCurrentInstance(), expression );
		}
		catch( NullPointerException npe )
		{
			return null;
		}

		/*
		 * This method should really look like the code here below, but because
		 * Blackbook uses JSF 1.1 I had to change it to use the older method
		 * above.
		 */
		/*
		 * FacesContext facesContext = FacesContext.getCurrentInstance();
		 * ELContext el = facesContext.getELContext(); Application app =
		 * facesContext.getApplication(); ExpressionFactory ef =
		 * app.getExpressionFactory(); ValueExpression ve =
		 * ef.createValueExpression( el, expression, c ); return ve.getValue( el
		 * );
		 */

	}

	/**
	 * Lookup.
	 * 
	 * @param beanName
	 *            the bean name
	 * @param request
	 *            the request
	 * @return the object
	 */
	public static Object lookup( String beanName, HttpServletRequest request )
	{
		if( request != null )
		{
			HttpSession session = request.getSession();
			return lookup( beanName, session );
		}

		return null;
	}

	/**
	 * Lookup.
	 * 
	 * @param beanName
	 *            the bean name
	 * @param session
	 *            the session
	 * @return the object
	 */
	public static Object lookup( String beanName, HttpSession session )
	{
		return session.getAttribute( beanName );

	}

	/**
	 * Lookup.
	 * 
	 * @param beanName
	 *            the bean name
	 * @param fc
	 *            the fc
	 * @return the object
	 */
	public static Object lookup( String beanName, FacesContext fc )
	{
		if( fc != null )
		{
			HttpSession session = (HttpSession)fc.getExternalContext().getSession( true );
			return lookup( beanName, session );
		}

		return null;
	}
}
