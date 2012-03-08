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


// TODO: Auto-generated Javadoc
/**
 * The Class Browser.
 * 
 * @author Svetlin Bostandjiev
 */
public class __Browser
{
	public static void p(Object o)
	{
		System.out.println(o);
	}
	public static void pe(Object o)
	{
		System.err.println(o);
	}
	
	public static String TEMP_FILE = "c:/temp/temp.html";
	
	public static void openUrl(String url)
	{
		// clean
		//url = url.replace("^", "%5E");
		url = url.replace("|", "%7C");

		try
		{
			java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
		}
		catch (Exception e)
		{
			pe(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static void openHtmlFile(String path)
	{
		net.wigis.svetlin.__File.open(path);
	}
	
	public static void openHtmlString(String html)
	{
		__File.saveStringToFile(TEMP_FILE, "<html><body>" + html + "</body></html>");
		
		net.wigis.svetlin.__File.open(TEMP_FILE);
	}
}
