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
import java.util.ArrayList;
import java.util.StringTokenizer;

import net.wigis.graph.data.citeseer.Logger;
import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.utilities.Vector2D;
import net.wigis.graph.dnv.utilities.Vector3D;

// TODO: Auto-generated Javadoc
/**
 * The Class ReadCSVtoDNVGraph.
 * 
 * @author Brynjar Gretarsson
 */
public class ReadCSVtoDNVGraph
{

	/**
	 * Logger.
	 * 
	 * @param o
	 *            the o
	 */
	// // private static Log logger = LogFactory.getLog( ReadCSVtoDNVGraph.class
	// );

	// ==============================
	// print
	// ==============================
	public static void print( Object o )
	{
		System.out.println( o );
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

		ArrayList<ArrayList<String>> allChunks = new ArrayList<ArrayList<String>>();
		ArrayList<String> chunkTypes = new ArrayList<String>();
		ArrayList<Vector3D> chunkColors = new ArrayList<Vector3D>();
		ArrayList<Double> chunkSizes = new ArrayList<Double>();
		ArrayList<String> currentChunk = null;
		ArrayList<Integer> offsets = new ArrayList<Integer>();
		int currentOffset = 0;
		Vector3D currentColor;
		double currentSize;
		final int OFFSET_DIFFERENCE = 100000;
		String line = "";
		try
		{
			line = br.readLine();
			while( line != null )
			{
				if( line.startsWith( "@NODES" ) || line.startsWith( "@EDGES" ) )
				{
					currentColor = extractColor( line );
					chunkColors.add( currentColor );
					currentSize = extractSize( line );
					chunkSizes.add( currentSize );
					currentChunk = new ArrayList<String>();
					allChunks.add( currentChunk );
					chunkTypes.add( line.substring( 0, 6 ) );
					offsets.add( currentOffset );
					currentOffset += OFFSET_DIFFERENCE;
				}
				else if( !Character.isDigit( line.trim().charAt( 0 ) ) )
				{
					// Skip this line
				}
				else
				{
					currentChunk.add( line );
				}

				line = br.readLine();
			}
		}
		catch( NullPointerException npe )
		{
			Logger.write( "File not correctly formatted." );
			// npe.printStackTrace();
			// return null;
		}

		// --------------------------
		// line
		// --------------------------
		ArrayList<String> currentLineArray;
		String currentLine;
		String currentChunkType;
		DNVGraph dnvGraph = new DNVGraph();
		int fromId;
		int toId;
		DNVNode fromNode;
		DNVNode toNode;
		for( int i = 0; i < allChunks.size(); i++ )
		{
			currentChunk = allChunks.get( i );
			currentChunkType = chunkTypes.get( i );
			currentOffset = offsets.get( i );
			currentColor = chunkColors.get( i );
			currentSize = chunkSizes.get( i );

			for( int j = 0; j < currentChunk.size(); j++ )
			{
				currentLine = currentChunk.get( j );
				currentLineArray = parseLine( currentLine, delim );
				if( currentChunkType.equals( "@NODES" ) )
				{
					DNVNode dnvNode = new DNVNode( new Vector2D( (float)Math.random(), (float)Math.random() ), dnvGraph );
					dnvNode.setLevel( 0 );
					dnvNode.setId( Integer.parseInt( currentLineArray.get( 0 ) ) );
					dnvNode.setLabel( currentLineArray.get( 1 ) );
					dnvNode.setColor( currentColor );
					dnvNode.setRadius( (float)currentSize );
					dnvGraph.addNode( 0, dnvNode );
				}
				else if( currentChunkType.equals( "@EDGES" ) )
				{
					fromId = Integer.parseInt( currentLineArray.get( 0 ) );
					toId = Integer.parseInt( currentLineArray.get( 1 ) );
					DNVEdge dnvEdge = new DNVEdge( dnvGraph );
					dnvEdge.setLevel( 0 );
					dnvEdge.setId( currentOffset + j );
					fromNode = (DNVNode)dnvGraph.getNodeById( fromId );
					toNode = (DNVNode)dnvGraph.getNodeById( toId );
					dnvEdge.setFrom( fromNode );
					dnvEdge.setTo( toNode );
					dnvGraph.addNode( 0, dnvEdge );
				}
			}
		}

		return dnvGraph;
	}

	/** The Constant DEFAULT_SIZE. */
	private static final double DEFAULT_SIZE = 1;

	/**
	 * Extract size.
	 * 
	 * @param line
	 *            the line
	 * @return the double
	 */
	private static double extractSize( String line )
	{
		if( line.contains( "@SIZE" ) )
		{
			String workString = line.substring( line.indexOf( "@SIZE" ) );
			workString = workString.substring( workString.indexOf( "[" ) + 1 );
			workString = workString.substring( 0, workString.indexOf( "]" ) );
			workString = workString.trim();
			return Double.parseDouble( workString );
		}

		return DEFAULT_SIZE;
	}

	/** The Constant DEFAULT_COLOR. */
	private static final Vector3D DEFAULT_COLOR = new Vector3D( 0, 1, 0 );

	/**
	 * Extract color.
	 * 
	 * @param line
	 *            the line
	 * @return the vector3 d
	 */
	private static Vector3D extractColor( String line )
	{
		if( line.contains( "@COLOR" ) )
		{
			String workString = line.substring( line.indexOf( "@COLOR" ) );
			workString = workString.substring( workString.indexOf( "[" ) );
			workString = workString.substring( 0, workString.indexOf( "]" ) + 1 );
			workString = workString.trim();
			return new Vector3D( workString );
		}

		return DEFAULT_COLOR;
	}

	/**
	 * Parses the line.
	 * 
	 * @param line
	 *            the line
	 * @param delim
	 *            the delim
	 * @return the array list
	 */
	public static ArrayList<String> parseLine( String line, String delim )
	{
		ArrayList<String> returnList = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer( line, delim );
		while( st.hasMoreTokens() )
		{
			returnList.add( st.nextToken().trim() );
		}

		return returnList;
	}
}
