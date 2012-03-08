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
 * The Class String_.
 * 
 * @author Svetlin Bostandjiev
 */
public class __String
{
	
	public static void p(Object o)
	{
		System.out.println(o);
	}
	
	public static void pe(Object o)
	{
		System.err.println(o);
	}
	
	public static String cleanup(String s)
	{
		// sql
		s = s.replace("&quot;", "\'");
		s = s.replace("&#39;", "\'");
		
		// html
		s = s.replace("&lt;", "<");
		
		return s;
	}
	
	public static String extractText(String s, String startString, String endString)
	{
		int indexInfoboxStart = s.indexOf(startString);
		int indexInfoboxEnd = s.indexOf(endString, indexInfoboxStart);
		
		return s.substring(indexInfoboxStart, indexInfoboxEnd + endString.length());
	}
	
	public static ArrayList<String> getLines(String multiLineString)
	{
		ArrayList<String> a = new ArrayList<String>();
		
		int indexLineStart = 0;
		int indexLineEnd = multiLineString.indexOf("\n");
		
		while (true)
		{
			if (indexLineStart < 0 || indexLineStart > multiLineString.length())
				break;
			
			// if last line
			if (indexLineEnd < 0)
				indexLineEnd = multiLineString.length();
			
			a.add(multiLineString.substring(indexLineStart, indexLineEnd));
			
			indexLineStart = indexLineEnd + 1;
			indexLineEnd = multiLineString.indexOf("\n", indexLineStart);
		}
		
		return a;
	}
	
	public static boolean StringArrayContainsString(String[] a, String s)
	{
		for (String as : a)
			if (as.equals(s))
				return true;
		
		return false;
	}
	
	public static int countCharOccurance(String s, char lookFor)
	{
		if (s == null)
			return -1;
		
		int count = 0;
		
		for (int i = 0; i < s.length(); i++)
		{
			final char c = s.charAt(i);
			
			if (c == lookFor)
				count++;
		}
		
		return count;
	}
	
	public static String getEverythingAfterTheLastOccurenceOf(String s, String delim)
	{
		if (s.contains(delim))
			return s.substring(s.lastIndexOf(delim) + 1, s.length());
		else
			return s;
	}
	
	public static String getAlphaNumeric(String s)
	{
		String s2 = "";
		
		for (int i = 0; i < s.length(); i++)
		{
			char c = s.charAt(i);
			
			// alpha numeric
			if (((c >= 'a') && (c <= 'z')) || ((c >= 'A') && (c <= 'Z')) || ((c >= '0') && (c <= '9')))
				s2 += c;
			else
				s2 += " ";
		}
		
		return s2;
	}
	
	public static boolean isAlphaNumeric(String s)
	{
		for (int i = 0; i < s.length(); i++)
		{
			char c = s.charAt(i);
			
			// alpha numeric
			if (((c >= 'a') && (c <= 'z')) || ((c >= 'A') && (c <= 'Z')) || ((c >= '0') && (c <= '9')))
			{
			}
			else
				return false;
		}
		
		return true;
	}
	
	public static boolean isAlphaNumericOrUnderscore(String s)
	{
		s = s.replace("_", "");
		
		if (!isAlphaNumeric(s))
			return false;
		
		return true;
	}
	
	public static String insertAt(String s, String sToInsert, int index)
	{
		return new StringBuffer(s).insert(index, sToInsert).toString();
	}
	
	public static String getFixedLengthString (int i, int length)
	{
		return getFixedLengthString ("" + i, length);
	}
	
	public static String getFixedLengthString (String s, int length)
	{
		String result = "";
		
		if (s == null)
		{
			for (int i = 0; i < length; i++)
				result += " ";
		}
		else
		{
			result += s;
		
			for (int i = 0; i < length - s.length(); i++)
				result += " ";
			
			if (result.length() > length)
				result = result.substring(0, length - 2) + "..";
		}
		
		return result;
	}
	
	public static boolean isNullOrEmpty(String str)
	{
		 int strLen;
		 
		 if (str == null || (strLen = str.length()) == 0) 
			 return true;

		 for (int i = 0; i < strLen; i++) 
	    	 if ((Character.isWhitespace(str.charAt(i)) == false)) 
	    		 return false;
	     
	     return true;
	}
	
	
	
	
	// =========================================
	// MAIN
	// =========================================
	public static void main(String[] args) throws Exception
	{
		net.wigis.svetlin.__DateTime time = new net.wigis.svetlin.__DateTime("main");
		// ------------------------------
		
		p(getFixedLengthString("123456789012345678", 15) + "äsd");
		
		// ------------------------------
		// end
		// ------------------------------
		net.wigis.svetlin.__Print.header("end");
		time.endTimer();
	}
}
