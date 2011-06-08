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

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

// TODO: Auto-generated Javadoc
// Live Search API 2.0 code sample demonstrating the use of the
// Web SourceType over the XML Protocol.
/**
 * The Class Bing.
 * 
 * @author Svetlin Bostandjiev
 */
public class __Bing
{
	public static void p(Object o)
	{
		System.out.println(o);
	}
	
	public static void pe(Object o)
	{
		System.err.println(o);
	}
	
	static XPathFactory factory = null;
	static XPath xpath = null;
	static XPathExpression expr = null;
	
	public static String getFirstImage(String query) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException
	{
		// Build the request.
		String requestURL = BuildRequest(net.wigis.svetlin.__Url.encodeUrl(query), "Image");
		
		//p(requestURL);
		
		// Send the request to the Live Search Service and get the response.
		Document doc = GetResponse(requestURL);
		
		if (doc != null)
		{
			factory = XPathFactory.newInstance();
			xpath = factory.newXPath();
			xpath.setNamespaceContext(new __BingAPINameSpaceContext());
			
			NodeList results = (NodeList) xpath.evaluate("//mms:Image/mms:Results/mms:ImageResult", doc, XPathConstants.NODESET);
			
			if (results.getLength() == 0)
				return null;
			
			for (int i = 0; i < results.getLength(); i++)
			{
				NodeList childNodes = results.item(i).getChildNodes();
				
				for (int j = 0; j < childNodes.getLength(); j++)
				{
					String fieldName = childNodes.item(j).getLocalName();
					
					if (fieldName.equalsIgnoreCase("DateTime"))
						fieldName = "Last Crawled";
					
					if (fieldName.equals("Thumbnail"))
						return childNodes.item(j).getTextContent();
				}
			}
		}
		
		return null;
	}
	
	public static String getFirstWikipediaResult(String query) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException
	{
		// Build the request.
		String requestURL = BuildRequest(net.wigis.svetlin.__Url.encodeUrl(query) + " site:wikipedia.org", "Web");
		
		//p(requestURL);
		
		// Send the request to the Live Search Service and get the response.
		Document doc = GetResponse(requestURL);
		
		if (doc != null)
		{
			factory = XPathFactory.newInstance();
			xpath = factory.newXPath();
			xpath.setNamespaceContext(new __BingAPINameSpaceContext());
			
			NodeList results = (NodeList) xpath.evaluate("//web:Web/web:Results/web:WebResult", doc, XPathConstants.NODESET);
			
			if (results.getLength() == 0)
				return null;
			
			for (int i = 0; i < results.getLength(); i++)
			{
				NodeList childNodes = results.item(i).getChildNodes();
				
				for (int j = 0; j < childNodes.getLength(); j++)
				{
					if (!childNodes.item(j).getLocalName().equalsIgnoreCase("DisplayUrl"))
					{
						String fieldName = childNodes.item(j).getLocalName();
						
						if (fieldName.equalsIgnoreCase("DateTime"))
							fieldName = "Last Crawled";
						
						if (fieldName.equals("Url"))
						{
							String wikiUrl = childNodes.item(j).getTextContent();
							
							if (wikiUrl.contains("wikipedia.org"))
							{
								//***************************************
								//* FIX
								//wikiUrl = wikiUrl.replace("Zepelin", "Zeppelin");
								//***************************************
								
								wikiUrl = cleanupUrl(wikiUrl);
								
								return wikiUrl;
							}
							else
								return null;
						}
						
						// p(fieldName + ":" +
						// childNodes.item(j).getTextContent());
					}
				}
				
				// p("\n");
			}
		}
		
		return null;
	}
	
	private static String cleanupUrl(String url)
	{
		return url.replace("?redirect=no", "");
	}
	
	private static String BuildRequest(String query, String bingSource)
	{
		// Replace the following string with the AppId you received from the
		// Live Search Developer Center.
		String AppId = "7CD944C4B3277887F6DA9C9B85B78C50A5C188A2";
		String requestString = "http://api.search.live.net/xml.aspx?"

		// Common request fields (required)
				+ "AppId=" + AppId + "&Query=" + query + "&Sources=" + bingSource

		// Common request fields (optional)
		// + "&Version=2.0"
		// + "&Market=en-us"
		// + "&Adult=Moderate"
		//
		// // Web-specific request fields (optional)
		// + "&Web.Count=10"
		// + "&Web.Offset=0"
		// + "&Web.FileType=DOC"
		// + "&Web.Options=DisableHostCollapsing+DisableQueryAlterations"
		
		;
		
		return requestString;
	}
	
	private static Document GetResponse(String requestURL) throws ParserConfigurationException, SAXException, IOException
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		Document doc = null;
		DocumentBuilder db = dbf.newDocumentBuilder();
		
		if (db != null)
		{
			doc = db.parse(requestURL);
		}
		
		return doc;
	}
	
	@SuppressWarnings("unused")
	private static void DisplayResponse(Document doc) throws XPathExpressionException
	{
		factory = XPathFactory.newInstance();
		xpath = factory.newXPath();
		xpath.setNamespaceContext(new __BingAPINameSpaceContext());
		NodeList errors = (NodeList) xpath.evaluate("//api:Error", doc, XPathConstants.NODESET);
		
		if (errors != null && errors.getLength() > 0)
		{
			// There are errors in the response. Display error details.
			DisplayErrors(errors);
		}
		else
		{
			DisplayResults(doc);
		}
	}
	
	private static void DisplayResults(Document doc) throws XPathExpressionException
	{
		String version = (String) xpath.evaluate("//@Version", doc, XPathConstants.STRING);
		String searchTerms = (String) xpath.evaluate("//api:SearchTerms", doc, XPathConstants.STRING);
		int total = Integer.parseInt((String) xpath.evaluate("//web:Web/web:Total", doc, XPathConstants.STRING));
		int offset = Integer.parseInt((String) xpath.evaluate("//web:Web/web:Offset", doc, XPathConstants.STRING));
		NodeList results = (NodeList) xpath.evaluate("//web:Web/web:Results/web:WebResult", doc, XPathConstants.NODESET);
		
		// Display the results header.
		System.out.println("Live Search API Version " + version);
		System.out.println("Web results for " + searchTerms);
		System.out.println("Displaying " + (offset + 1) + " to " + (offset + results.getLength()) + " of " + total + " results ");
		System.out.println();
		
		// Display the Web results.
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < results.getLength(); i++)
		{
			NodeList childNodes = results.item(i).getChildNodes();
			
			for (int j = 0; j < childNodes.getLength(); j++)
			{
				if (!childNodes.item(j).getLocalName().equalsIgnoreCase("DisplayUrl"))
				{
					String fieldName = childNodes.item(j).getLocalName();
					
					if (fieldName.equalsIgnoreCase("DateTime"))
					{
						fieldName = "Last Crawled";
					}
					
					builder.append(fieldName + ":" + childNodes.item(j).getTextContent());
					builder.append("\n");
				}
			}
			
			builder.append("\n");
		}
		
		System.out.println(builder.toString());
	}
	
	private static void DisplayErrors(NodeList errors)
	{
		System.out.println("Live Search API Errors:");
		System.out.println();
		
		for (int i = 0; i < errors.getLength(); i++)
		{
			NodeList childNodes = errors.item(i).getChildNodes();
			
			for (int j = 0; j < childNodes.getLength(); j++)
			{
				System.out.println(childNodes.item(j).getLocalName() + ":" + childNodes.item(j).getTextContent());
			}
			
			System.out.println();
		}
	}
	
	// =========================================
	// MAIN
	// =========================================
	public static void main(String[] args) throws Exception
	{
		net.wigis.svetlin.__DateTime time = new net.wigis.svetlin.__DateTime("main");
		// ------------------------------
		
		p(getFirstWikipediaResult("britney spears"));
		p(getFirstWikipediaResult("u2"));
		
		p(getFirstImage("metallica"));
		
		// ------------------------------
		// end
		// ------------------------------
		net.wigis.svetlin.__Print.header("end");
		time.endTimer();
	}
}