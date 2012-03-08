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

package net.wigis.graph.data.uploader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import net.wigis.graph.data.citeseer.Logger;
import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.utilities.Vector2D;

// TODO: Auto-generated Javadoc
/**
 * The Class SimpleEdgeTuplesToDNVGraph.
 * 
 * @author johno
 */
public class SimpleEdgeTuplesToDNVGraph
{

	/**
	 * Logger.
	 * 
	 * @param o
	 *            the o
	 */
	// // private static Log logger = LogFactory.getLog(
	// SimpleEdgeTuplesToDNVGraph.class );

	// ==============================
	// print
	// ==============================
	public static void print( Object o )
	{
		System.out.println( o );
	}
	// filename without extension
	/** The Constant FILENAME. */
	public static final String FILENAME = "iris_data";

	/** The Constant OFFSET_DIFFERENCE. */
	static final int OFFSET_DIFFERENCE = 1;

	/*
	 * main class- for testing only
	 */
	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void main( String[] args ) throws IOException
	{

		System.out.println( "-----------Parser Test------------" );
		System.out.println( "start..." );
		// String bztestdata = "C:\\Users\\jod\\Desktop\\ben-facebook-data\\" +
		// FILENAME + ".txt";
		String bstestdata = "C:\\Users\\jod\\Desktop\\coverage-graphiris.data.tup";
		DNVGraph g = SimpleEdgeTuplesToDNVGraph.read( bstestdata, " " );
		// note- just for testing. writing is usually done elsewhere
		g.writeGraph( "C:\\graphs\\BSMYTH-" + FILENAME + ".dnv" );
		System.out.println( "...done.   check DNV file." );
	}

	/**
	 * Read.
	 * 
	 * @param csvPath
	 *            the csv path
	 * @param delim
	 *            the delim
	 * @return the dNV graph
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static DNVGraph read( String csvPath, String delim ) throws IOException
	{
		BufferedReader br = new BufferedReader( new FileReader( csvPath ) );

		String line = "";

		// --------------------------
		// generate a dnvgraph object
		// --------------------------

		DNVGraph dnvGraph = new DNVGraph();
		DNVNode fromNode;
		DNVNode toNode;
		// int id = 0;
		int edge_id = OFFSET_DIFFERENCE;
		try
		{
			line = br.readLine();
			while( line != null )
			{
				edge_id += OFFSET_DIFFERENCE;
				String[] a = line.split( "[\\s,',']+" );

				// check type- we accept int or hex ids
				int fromId = 0;
				int toId = 0;
				if( a[0].trim().length() == 32 )
				{ // check for a 32 bit string
					fromId = Math.abs( hex2decimal( a[0] ) );
					toId = Math.abs( hex2decimal( a[1] ) );
				}
				else
				{
					fromId = Math.abs( Integer.parseInt( a[0] ) );
					toId = Math.abs( Integer.parseInt( a[1] ) );
				}
				// check if the node already exists in the graph
				fromNode = (DNVNode)dnvGraph.getNodeById( fromId );
				if( fromNode == null )
				{
					fromNode = new DNVNode( new Vector2D( (float)Math.random(), (float)Math.random() ), dnvGraph );
					fromNode.setLevel( 0 );
					fromNode.setId( new Integer( fromId ) );
					fromNode.setLabel( "" + fromId );
					dnvGraph.addNode( 0, fromNode );
				}
				toNode = (DNVNode)dnvGraph.getNodeById( toId );
				if( toNode == null )
				{
					toNode = new DNVNode( new Vector2D( (float)Math.random(), (float)Math.random() ), dnvGraph );
					toNode.setLevel( 0 );
					toNode.setId( new Integer( toId ) );
					toNode.setLabel( "" + toId );
					dnvGraph.addNode( 0, toNode );
				}

				DNVEdge dnvEdge = new DNVEdge( dnvGraph );
				dnvEdge.setFrom( fromNode );
				dnvEdge.setId( new Integer( edge_id + 10000000 ) );
				dnvEdge.setTo( toNode );
				dnvGraph.addNode( 0, dnvEdge );
				line = br.readLine();
			}
		}
		catch( NullPointerException npe )
		{
			Logger.write( "File not correctly formatted." );
			// npe.printStackTrace();
			// return null;
		}

		return dnvGraph;
	}

	/**
	 * Hex2decimal.
	 * 
	 * @param s
	 *            the s
	 * @return the int
	 */
	public static int hex2decimal( String s )
	{
		String digits = "0123456789ABCDEF";
		s = s.toUpperCase();
		int val = 0;
		for( int i = 0; i < s.length(); i++ )
		{
			char c = s.charAt( i );
			int d = digits.indexOf( c );
			val = 16 * val + d;
		}
		return val;
	}
}
