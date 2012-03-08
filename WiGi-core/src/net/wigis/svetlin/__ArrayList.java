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
import java.util.Collections;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class ArrayList_.
 * 
 * @author Svetlin Bostandjiev
 */
public class __ArrayList
{
	public static double getMin(ArrayList<Double> a)
	{
		double min = Double.MAX_VALUE;
		
		for (Double d : a)
			if (d < min)
				min = d;
		
		return min;
	}
	
	public static double getMax(ArrayList<Double> a)
	{
		double max = Double.MIN_VALUE;
		
		for (Double d : a)
			if (d > max)
				max = d;
		
		return max;
	}
	
	public static boolean ArrayList_String_Contains_String(ArrayList<String> a, String s)
	{
		for (String as : a)
			if (as.equals(s))
				return true;
		
		return false;
	}
	
	public static ArrayList<String> ArrayString_To_ArrayListString(String[] sa)
	{
		ArrayList<String> a = new ArrayList<String>();
		
		for (String s : sa)
			a.add(s);
		
		return a;
	}
	
	public static ArrayList<Object> List_To_ArrayList(List<Object> l)
	{
		return (ArrayList<Object>)l;
	}
	
	public static ArrayList<String> removeDuplicates (ArrayList<String> a)
	{
		if (a == null || a.size() == 0)
			return null;
		
		ArrayList<String> a2 = new ArrayList<String>();
		
		a2.add(a.get(0));
		
		for (int i=0; i<a.size(); i++)
			for (int j=0; j<a2.size(); j++)
			{
				if (a.get(i).equals(a2.get(j)))
					break;
				
				if (j == a2.size()-1)
					a2.add(a.get(i));
			}
				
		return a2;
					
	}
	
	public static void sort(ArrayList<String> a)
	{
		if (a == null || a.size() == 0)
			return;
		
		Collections.sort(a);
	}
}
