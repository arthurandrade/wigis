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

import java.io.File;
import java.util.ArrayList;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;

// TODO: Auto-generated Javadoc
/**
 * The Class Xml.
 * 
 * @author Svetlin Bostandjiev
 */
public class __Xml
{

	/**
	 * P.
	 * 
	 * @param o
	 *            the o
	 */
	public static void p( Object o )
	{
		System.out.println( o );
	}

	/**
	 * Pe.
	 * 
	 * @param o
	 *            the o
	 */
	public static void pe( Object o )
	{
		System.err.println( o );
	}

	// ===============================
	// parse tag
	// ===============================
	/**
	 * Gets the text out of tag.
	 * 
	 * @param xmlString
	 *            the xml string
	 * @param tag
	 *            the tag
	 * @return the text out of tag
	 */
	public static ArrayList<String> getTextOutOfTag( String xmlString, String tag )
	{
		// does the same as the html tag parser
		return net.wigis.svetlin.__Html.getTagText( xmlString, tag );
	}

	// ===============================
	// cleanup Tag
	// ===============================
	/**
	 * Cleanup tag.
	 * 
	 * @param tag
	 *            the tag
	 * @return the string
	 */
	public static String cleanupTag( String tag )
	{
		if( tag.indexOf( " " ) > 0 )
			tag = tag.substring( 0, tag.indexOf( " " ) );

		return tag;
	}

	// ===============================
	// get children
	// ===============================
	/**
	 * Gets the children.
	 * 
	 * @param xmlString
	 *            the xml string
	 * @return the children
	 */
	public static ArrayList<__Pair> getChildren( String xmlString )
	{
		ArrayList<__Pair> a = new ArrayList<__Pair>();

		int startTagStartIndex = 0;
		int startTagEndIndex = 0;
		int endTagStartIndex = 0;
		int endTagEndIndex = 0;

		// ignore <?xml version="1.0" encoding="utf-8"?>
		endTagEndIndex = xmlString.indexOf( "?>" ) + 2;
		// p(endTagEndIndex);

		while( true )
		{
			startTagStartIndex = xmlString.indexOf( "<", endTagEndIndex );
			startTagEndIndex = xmlString.indexOf( ">", startTagStartIndex );
			// p(startTagStartIndex + " " + startTagEndIndex);

			if( startTagStartIndex < 0 )
				break;

			String tag = xmlString.substring( startTagStartIndex + 1, startTagEndIndex );
			// p(tag);

			__Pair pair = new __Pair( "", "" );

			// check for <..../>
			if( tag.contains( "/" ) )
			{
				tag = cleanupTag( tag );

				pair = new __Pair( tag, "" );

				endTagEndIndex = startTagEndIndex;
			}
			else
			{
				tag = cleanupTag( tag );

				endTagStartIndex = xmlString.indexOf( "</" + tag + ">", startTagEndIndex );
				endTagEndIndex = xmlString.indexOf( ">", endTagStartIndex );

				pair = new __Pair( tag, xmlString.substring( startTagEndIndex + 1, endTagStartIndex ) );
			}

			a.add( pair );
		}

		return a;
	}

	// ===============================
	// get children by tag name
	// ===============================
	/**
	 * Gets the children by tag.
	 * 
	 * @param xmlString
	 *            the xml string
	 * @param tag
	 *            the tag
	 * @return the children by tag
	 */
	public static ArrayList<__Pair> getChildrenByTag( String xmlString, String tag )
	{
		ArrayList<__Pair> a = getChildren( xmlString );
		ArrayList<__Pair> a2 = new ArrayList<__Pair>();

		for( __Pair p : a )
			if( p.object.equals( tag ) )
				a2.add( p );

		return a2;
	}

	// ===============================
	// convert xml to tree of children
	// ===============================
	/**
	 * Xml to dom document.
	 * 
	 * @param xmlString
	 *            the xml string
	 * @return the document
	 * @throws Exception
	 *             the exception
	 */
	@SuppressWarnings("deprecation")
	public static Document xmlToDOMDocument( String xmlString ) throws Exception
	{
		String filePath = "c:\\temp\\temp.xml";

		net.wigis.svetlin.__File.saveStringToFile( filePath, xmlString );

		File file = new File( filePath );

		DOMParser parser = new DOMParser();
		parser.parse( file.toURL().toString() );
		Document doc = parser.getDocument();

		return doc;
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
	public static void main( String[] args )
	{

	}

}
