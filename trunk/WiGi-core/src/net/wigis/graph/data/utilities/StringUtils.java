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

package net.wigis.graph.data.utilities;

// TODO: Auto-generated Javadoc
/**
 * The Class StringUtils.
 * 
 * @author Brynjar Gretarsson
 */
public class StringUtils
{

	/**
	 * Replace all.
	 * 
	 * @param temp
	 *            the temp
	 * @param removeToken
	 *            the remove token
	 * @param replaceToken
	 *            the replace token
	 * @return the string
	 */
	public static String replaceAll( String temp, String removeToken, String replaceToken )
	{
		while( temp.indexOf( removeToken ) != -1 )
		{
			StringBuilder buffer = new StringBuilder();
			buffer.append( temp.substring( 0, temp.indexOf( removeToken ) ) );
			buffer.append( replaceToken );
			buffer.append( temp.substring( temp.indexOf( removeToken ) + 1 ) );
			temp = buffer.toString();
		}

		return temp;
	}

	/**
	 * Count instances of.
	 * 
	 * @param content
	 *            the content
	 * @param search
	 *            the search
	 * @return the int
	 */
	public static int countInstancesOf( String content, String search )
	{
		int ctr = -1;
		int total = 0;
		while( true )
		{
			if( ctr == -1 )
				ctr = content.indexOf( search );
			else
				ctr = content.indexOf( search, ctr );

			if( ctr == -1 )
			{
				break;
			}
			else
			{
				total++;
				ctr += search.length();
			}
		}
		return total;
	}
}
