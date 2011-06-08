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

package net.wigis.svetlin;

import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

// TODO: Auto-generated Javadoc
/**
 * The Class jsf.
 * 
 * @author Svetlin Bostandjiev
 */
public class __jsf
{
	public static void p(Object o)
	{
		System.out.println(o);
	}
	
	public static void pe(Object o)
	{
		System.err.println(o);
	}
	
	public static boolean isRunAtServer ()
	{
		if (net.wigis.svetlin.__jsf.getServerName() != null)
			return true;
		else
			return false;
	}
	
	public static boolean isServerLocalhost ()
	{
		return isRunAtServer() && net.wigis.svetlin.__jsf.getServerName().contains("localhost");
	}
	
	
	
	
	
	public static String getServerName()
	{
		String stringValue = null;
		FacesContext fc = FacesContext.getCurrentInstance();
		if (fc != null)
		{
			HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
			stringValue = request.getServerName();
		}
		
		return stringValue;
	}
	
	public static String getServerPath()
	{
		String stringValue = null;
		FacesContext fc = FacesContext.getCurrentInstance();
		if (fc != null)
		{
			HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
			stringValue = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
		}
		
		return stringValue;
	}
	
	public static boolean isFacesContextExists()
	{
		if (FacesContext.getCurrentInstance() != null)
			return true;
		else
			return false;
	}
	
	public static String getClientIP()
	{
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
		String remoteIP = request.getRemoteAddr();
		
		return remoteIP;
	}
	
	//=======================================
	// call_Java_bean_from_JavaScript_with_param
	//=======================================
	public static String PARAM_NAME = "param";	
	
	public static String getHtml_forHyperlink_toCall_JavaBean(String text, String formId, String hiddenButtonId, String param)
	{
		return 
			"<a href='#' " +
			"	onclick=\"" +
			"		A4J.AJAX.Submit('_viewRoot','" + formId + "',event,{'similarityGroupingId':'" + formId + ":" + hiddenButtonId + "','parameters':{'" + PARAM_NAME + "':'" + param + "','" + formId + ":" + hiddenButtonId + "':'" + formId + ":" + hiddenButtonId + "'} ,'actionUrl':'/WiGi/wigi/WiGiViewerPanel.faces'} );" +
			"\">" +
				text + 
			"</a>";
		
//		return "<a href='#' onclick=\"" +
//		"A4J.AJAX.Submit('_viewRoot','statsForm',event,{'similarityGroupingId':'statsForm:bbb','parameters':{'action':'delete2444444444','statsForm:bbb':'statsForm:bbb'} ,'actionUrl':'/WiGi/wigi/WiGiViewerPanel.faces'} );" +
//		"\">" +
//		"asdasdasd</a>";
	}
	
	public static String getParam()
	{
		Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		
		return params.get(__jsf.PARAM_NAME);
	}
	
	//=======================================
}
