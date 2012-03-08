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

import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The Class Html.
 * 
 * @author Svetlin Bostandjiev
 */
public class __Html
{
	public static void p(Object o)
	{
		System.out.println(o);
	}
	
	public static void pe(Object o)
	{
		System.err.println(o);
	}
	
	// ------------------------------
	// parse tag
	// ------------------------------
	public static ArrayList<String> getTagText(String html, String tag)
	{
		// p(html);
		// p(tag);
		
		ArrayList<String> data = new ArrayList<String>();
		
		int tagStart = 0;
		int tagEnd = 0;
		
		while (true)
		{
			tagStart = html.indexOf("<" + tag, tagEnd);
			tagStart = html.indexOf(">", tagStart);
			// p(tagStart + " " + tagEnd);
			
			// if made a whole loop
			if (tagStart < 0 || tagStart < tagEnd)
				break;
			
			tagEnd = html.indexOf("</" + tag, tagStart);
			// p(tagStart + " " + tagEnd);
			
			// if 0 tags
			if (tagEnd <= 0)
				break;
			
			// add 1 for >
			String value = html.substring(tagStart + 1, tagEnd);
			
			data.add(value);
		}
		
		return data;
	}
	
	// ------------------------------
	// parse hyperlinks
	// ------------------------------
	public static ArrayList<String> parseUniqueHyperlinks(String html, boolean returnOnlyUnique)
	{
		ArrayList<String> data = new ArrayList<String>();
		
		int tdStart = 0;
		int tdEnd = 0;
		
		while (true)
		{
			tdStart = html.indexOf("<a href", tdEnd);
			tdStart = html.indexOf("\"", tdStart);
			
			// if made a whole loop
			if (tdStart < 0 || tdStart < tdEnd)
				break;
			
			tdEnd = html.indexOf("\"", tdStart + 2);
			
			// p(tdStart + " " + tdEnd);
			
			// add 1 for >
			String value = html.substring(tdStart + 1, tdEnd);
			
			// add if unique
			if (returnOnlyUnique)
			{
				int i = 0;
				for (i = 0; i < data.size(); i++)
					if (data.get(i).equals(value))
						break;
				if (i == data.size())
					data.add(value);
			}
			else
				data.add(value);
		}
		
		return data;
	}
	
	// ------------------------------
	// parse tag
	// ------------------------------
	public static int frequency(String html, String s)
	{
		if (html == "" || s == "")
			return 0;
		
		int count = 0;
		int current = 0;
		
		while (true)
		{
			current = html.indexOf(s, current);
			
			if (current >= 0)
				count++;
			else
				break;
			
			current++;
		}
		
		return count;
	}
	
	// ------------------------------
	// remove tags and their content
	// ------------------------------
	public static String removeTagsAndTheirContent(String html, String tag)
	{
		while (true)
		{
			int indexStart = html.indexOf("<" + tag); 
			
			if (indexStart < 0)
				break;
			
			int indexEnd = html.indexOf("</" + tag + ">");
			
			html = html.replace(html.substring(indexStart, indexEnd + ("</" + tag + ">").length()), "");
			
		}
		
		return html;
	}
}
