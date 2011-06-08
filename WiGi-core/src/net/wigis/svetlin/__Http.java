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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;

// TODO: Auto-generated Javadoc
/**
 * The Class Http.
 * 
 * @author Svetlin Bostandjiev
 */
public class __Http
{
	public static void p(Object o)
	{
		System.out.println(o);
	}
	
	public static void pe(Object o)
	{
		System.err.println(o);
	}
	
	
	//===============================================
	// send GET requests in parallel
	//===============================================
	// jetty help: http://wiki.eclipse.org/Jetty/Tutorial/HttpClient
	
	public static boolean lastRequestSent = false;
	public static int nTotalRequests = 0;
	public static int nRequestsSent = 0;
	public static int nResponsesReceived = 0;
	public static ArrayList<String> httpResponses = new ArrayList<String>();
	public static boolean runAtServer = false;
	
	public static ArrayList<String> sendGetRequestsInParallel (ArrayList<String> urls)
	{
		httpResponses = new ArrayList<String>();

		nTotalRequests = urls.size();
		
		System.out.print("http requests in parallel: " + nTotalRequests + " - ");

		lastRequestSent = false;
		nRequestsSent = 0;
		nResponsesReceived = 0;
		
		runAtServer = __jsf.isRunAtServer();
		
		HttpClient client = new HttpClient();
		client.setConnectorType(HttpClient.CONNECTOR_SELECT_CHANNEL);
		try
		{
			client.start();
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
		}
		
		System.out.print("[");
		
		for (int i = 0; i < urls.size(); i++)
		{
			try
			{
				String url = urls.get(i);
				
				ContentExchange exchange = new ContentExchange(true)
				{
					//----------------------
					// RECEIVE
					//----------------------
				    protected void onResponseComplete() throws IOException
				    {				    					    					
				        if (getResponseStatus() == 200)
				        {
				            //pe("success");
				        }
				        else
				        {
				        	net.wigis.svetlin.__Print.ERROR("jetty http error: " + getResponseStatus() + ": \n" + getResponseContent());
				        }
				        
				        String httpResponse = getResponseContent();
				        
				    	httpResponses.add(httpResponse);
				    	
				    	//p("response #" + (nResponsesReceived + 1) + " of " + nTotalRequests + ": " + httpResponse.length() + " bytes");
				    	
						// ------------------------------
				    	// print
						// ------------------------------
				    	//if (nResponsesReceived == 0)
				    		//System.out.print("[");
				    	if (!runAtServer)
				    		System.out.print(".");				    	
				    	if ((nResponsesReceived + 1) % 10 == 0)
				    		System.out.print("(." + (nResponsesReceived + 1) + ")");
				    	if ((nResponsesReceived + 1) == nTotalRequests)
				    		System.out.println("]");
						// ------------------------------
						
						// LAST STATEMENT 		// this statement should be the last one in this method because of the async
						nResponsesReceived ++;
				    }
				};
				
				exchange.setURL(url);
			
				client.send(exchange);
				
				//exchange.waitForDone();
					
				//p("..." + "request #" + (nRequestsSent + 1) + " of " + nTotalRequests);
				if (!runAtServer)
					System.out.print(">");
				if ((nRequestsSent + 1) % 10 == 0)
		    		System.out.print("(>" + (nRequestsSent + 1) + ")");

				nRequestsSent ++;
				
				// LAST STATEMENT
				if (i == urls.size()-1)
					lastRequestSent = true;
								
			}
			// ------------------------------
			// errors
			// ------------------------------
			catch (Exception e)
			{		
				net.wigis.svetlin.__Print.ERROR("error processing this url: " + urls.get(i));
				
				e.printStackTrace();
				
				return null;
			}
			// ------------------------------
			
		}
		
		// wait for all requests to complete
		while (true)
		{
			if (lastRequestSent 
			 && nRequestsSent == nResponsesReceived)
				break;
		}
		
		return httpResponses;
	}
	
	
	//===============================================
	// send GET request
	//===============================================
	// everything in one url
	public static String sendGetRequest(String url) throws Exception
	{
		return sendGetRequest(url.substring(0, url.indexOf("?")), url.substring(url.indexOf("?") + 1));
	}
	
	/**
	 * Sends an HTTP GET request to a url.
	 * 
	 * @param endpoint
	 *            - The URL of the server. (Example:
	 *            " http://www.yahoo.com/search")
	 * @param requestParameters
	 *            - all the request parameters (Example:
	 *            "param1=val1&param2=val2"). Note: This method will add the
	 *            question mark (?) to the request - DO NOT add it yourself
	 * @return - The response from the end point
	 * @throws Exception
	 *             the exception
	 */
	public static String sendGetRequest(String endpoint, String requestParameters) throws Exception
	{
		String result = null;
		
		if (endpoint.startsWith("http://") || endpoint.startsWith("https://") )
		{
			// Send a GET request to the servlet
			try
			{
				//********************
				// alex added this
				if (requestParameters == null)
					requestParameters = "";
				
				requestParameters = requestParameters.replace(" ", "%20");
				//********************
				
				// Send data
				String urlStr = endpoint;
				if (requestParameters != null && requestParameters.length() > 0)
					urlStr += "?" + requestParameters;
				
				URL url = new URL(urlStr);
				URLConnection conn = url.openConnection();
				
//				for (int i=0;i<20;i++)				
//					p(conn.getHeaderField(i));
				
				// Get the response
				BufferedReader rd = new BufferedReader(new InputStreamReader((InputStream)conn.getContent()));
				StringBuilder sb = new StringBuilder();
				String line;
				
				while ((line = rd.readLine()) != null)
				{
					sb.append(line);
					
					// *alex: append new line
					sb.append("\n");
				}
				
				rd.close();
				result = sb.toString();
				
				// *alex: cleanup
				result = net.wigis.svetlin.__String.cleanup(result);
			}
			catch (Exception e)
			{
				__Print.ERROR("HTTP ERROR");
				
				e.printStackTrace();
				
				throw e;
			}
		}
		
		return result;
	}
	
	//===============================================
	// send POST request
	//===============================================
	/**
	 * Reads data from the data reader and posts it to a server via POST
	 * request. data - The data you want to send endpoint - The server's address
	 * output - writes the server's response to output
	 * 
	 * @param data
	 *            the data
	 * @param endpoint
	 *            the endpoint
	 * @param output
	 *            the output
	 * @throws Exception
	 *             the exception
	 */
	public static void sendPostRequest(Reader data, URL endpoint, Writer output) throws Exception
	{
		HttpURLConnection urlc = null;
		try
		{
			urlc = (HttpURLConnection) endpoint.openConnection();
			try
			{
				urlc.setRequestMethod("POST");
			}
			catch (ProtocolException e)
			{
				throw new Exception("Shouldn't happen: HttpURLConnection doesn't support POST??", e);
			}
			urlc.setDoOutput(true);
			urlc.setDoInput(true);
			urlc.setUseCaches(false);
			urlc.setAllowUserInteraction(false);
			urlc.setRequestProperty("Content-type", "text/xml; charset=" + "UTF-8");
			OutputStream out = urlc.getOutputStream();
			try
			{
				Writer writer = new OutputStreamWriter(out, "UTF-8");
				pipe(data, writer);
				writer.close();
			}
			catch (IOException e)
			{
				throw new Exception("IOException while posting data", e);
			}
			finally
			{
				if (out != null)
					out.close();
			}
			InputStream in = urlc.getInputStream();
			try
			{
				Reader reader = new InputStreamReader(in);
				pipe(reader, output);
				reader.close();
			}
			catch (IOException e)
			{
				throw new Exception("IOException while reading response", e);
			}
			finally
			{
				if (in != null)
					in.close();
			}
		}
		catch (IOException e)
		{
			throw new Exception("Connection error (is server running at " + endpoint + " ?): " + e);
		}
		finally
		{
			if (urlc != null)
				urlc.disconnect();
		}
	}
	
	/**
	 * Pipes everything from the reader to the writer via a buffer.
	 * 
	 * @param reader
	 *            the reader
	 * @param writer
	 *            the writer
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private static void pipe(Reader reader, Writer writer) throws IOException
	{
		char[] buf = new char[1024];
		int read = 0;
		while ((read = reader.read(buf)) >= 0)
		{
			writer.write(buf, 0, read);
		}
		writer.flush();
	}
}
