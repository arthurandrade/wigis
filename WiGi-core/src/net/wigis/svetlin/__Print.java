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
 * The Class Print.
 * 
 * @author Svetlin Bostandjiev
 */
public class __Print
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
	// print header
	// ------------------------------
	public static void header(String string)
	{
		p("");
		lineDouble();
		p("\t" + string.toUpperCase());
		lineDouble();
	}
	
	public static void headerSmall(String string)
	{
		line();
		p(" " + string);
		line();
	}
	
	public static void headerSmall_HalfLine(String string)
	{
		lineHalf();
		p(" " + string);
		lineHalf();
	}
	
	// ------------------------------
	// print a line
	// ------------------------------
	public static void line()
	{
		p("-----------------------------------");
	}
	
	public static void lineHalf()
	{
		p("-------------");
	}
	
	public static void lineDouble()
	{
		p("=============================================");
	}
	
	public static void lineStars()
	{
		p("******************************************");
	}
	
	// ------------------------------
	// print ArrayList
	// ------------------------------
	@SuppressWarnings("unchecked")
	public static void print(ArrayList a)
	{
		line();
		
		for (int i = 0; i < a.size(); i++)
			p(i + ":\t" + a.get(i));
		
		line();
	}
	
	// ------------------------------
	// print String[]
	// ------------------------------
	public static void print(String[] a)
	{
		line();
		
		for (int i = 0; i < a.length; i++)
			p(i + ":\t" + a[i]);
		
		line();
	}
	
	// ------------------------------
	// ERROR
	// ------------------------------
	public static void ERROR(String s)
	{
		pe("ERROR (Alex): " + s);
	}
	
	// ------------------------------
	// WARNING
	// ------------------------------
	public static void WARNING(String s)
	{
		pe("WARNING (Alex): " + s);
	}
}
