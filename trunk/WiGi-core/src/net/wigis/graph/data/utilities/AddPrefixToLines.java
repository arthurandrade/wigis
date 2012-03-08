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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.wigis.settings.Settings;

// TODO: Auto-generated Javadoc
/**
 * The Class AddPrefixToLines.
 * 
 * @author Brynjar Gretarsson
 */
public class AddPrefixToLines
{

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main( String args[] )
	{
		String directory = Settings.GRAPHS_PATH + "topicVisualizations" + "/" + "NIPS" + "/";
		String prefix = "files/";
		String file = "docs.txt";

		try
		{
			FileReader fr = new FileReader( directory + file );
			BufferedReader br = new BufferedReader( fr );
			List<String> newLines = new ArrayList<String>();
			String line;
			while( ( line = br.readLine() ) != null )
			{
				newLines.add( prefix + line );
			}

			br.close();
			fr.close();

			FileWriter fw = new FileWriter( directory + file );
			BufferedWriter bw = new BufferedWriter( fw );
			for( int i = 0; i < newLines.size(); i++ )
			{
				line = newLines.get( i );
				bw.write( line + "\n" );
			}

			bw.close();
			fw.close();
		}
		catch( IOException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
