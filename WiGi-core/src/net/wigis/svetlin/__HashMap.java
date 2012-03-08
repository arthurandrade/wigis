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

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

// TODO: Auto-generated Javadoc
/**
 * The Class HashMap_.
 * 
 * @author Svetlin Bostandjiev
 */
public class __HashMap
{
	public static void p(Object o)
	{
		System.out.println(o);
	}

	public static void pe(Object o)
	{
		System.err.println(o);
	}
	
	// -------------------------------
	// sort by value
	// -------------------------------
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static HashMap sortByValue(HashMap map, boolean ascending)
	{
		List list = new LinkedList(map.entrySet());
		
		if (ascending)
		{
			Collections.sort(list, new Comparator()
			{
				public int compare(Object o1, Object o2)
				{
					return ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
				}
			});
		}
		else
		{
			Collections.sort(list, new Comparator()
			{
				public int compare(Object o1, Object o2)
				{
					return 0 - ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
				}
			});
		}
		
		HashMap result = new LinkedHashMap();
		
		for (Iterator it = list.iterator(); it.hasNext();)
		{
			Map.Entry entry = (Map.Entry) it.next();
			result.put(entry.getKey(), entry.getValue());
		}
		
		return result;
	}
	
	@SuppressWarnings("rawtypes")
	public static void print (HashMap hm)
	{
		print (hm, hm.size());
	}
	
	@SuppressWarnings("rawtypes")
	public static void print (HashMap hm, int maxRowsToPrint)
	{
		p("[---------------HashMap------------------- (" + maxRowsToPrint + " of " + hm.size() + ")");
		
		int count = 0;
		
		for (Object key : hm.keySet())
		{
			count ++;
			
			if (count > maxRowsToPrint)
				break;
			
			p(__String.getFixedLengthString(key.toString(), 40) + hm.get(key));
		}
			
		p("-----------------------------------]");
	}
	
	
	
	// =========================================
	// MAIN
	// =========================================
	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args)
	{
		HashMap<String, Integer> hm = new HashMap<String, Integer>();
		hm.put("two", 2);
		hm.put("one", 1);
		hm.put("three", 3);
		
		p(hm);
		
		hm = (HashMap<String, Integer>) sortByValue(hm, true);
		p(hm);
		
		hm = (HashMap<String, Integer>) sortByValue(hm, false);
		p(hm);
	}
}
