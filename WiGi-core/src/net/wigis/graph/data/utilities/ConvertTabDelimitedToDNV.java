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
import java.io.FileReader;
import java.io.IOException;

import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVEntity;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.layout.FruchtermanReingold;
import net.wigis.settings.Settings;

// TODO: Auto-generated Javadoc
/**
 * The Class ConvertTabDelimitedToDNV.
 * 
 * @author johno
 */
public class ConvertTabDelimitedToDNV
{

	/**
	 * Convert.
	 * 
	 * @param filename
	 *            the filename
	 * @return the dNV graph
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static DNVGraph convert( String filename ) throws IOException
	{
		FileReader fr = new FileReader( filename );
		BufferedReader br = new BufferedReader( fr );

		String line;
		String from;
		String to;

		DNVGraph graph = new DNVGraph();
		DNVNode fromNode;
		DNVNode toNode;
		DNVEdge edge;

		while( ( line = br.readLine() ) != null )
		{
			from = line.substring( 0, line.indexOf( "\t" ) ).trim();
			to = line.substring( line.indexOf( "\t" ) + 2 ).trim();
			fromNode = (DNVNode)getNode( from, graph );
			toNode = (DNVNode)getNode( to, graph );
			edge = new DNVEdge( graph );
			edge.setFrom( fromNode );
			edge.setTo( toNode );
			edge.setDirectional( true );
			graph.addNode( 0, edge );
		}

		new FruchtermanReingold().runLayout( 100, 100, graph, 0.1f, 0, false, false );
		return graph;
	}

	/**
	 * Gets the node.
	 * 
	 * @param bbid
	 *            the bbid
	 * @param graph
	 *            the graph
	 * @return the node
	 */
	private static DNVEntity getNode( String bbid, DNVGraph graph )
	{
		DNVEntity node = graph.getNodeByBbId( bbid );
		if( node == null )
		{
			node = new DNVNode( graph );
			node.setLabel( bbid );
			node.setBbId( bbid );
			node.setColor( "#33FF33" );
			( (DNVNode)node ).setPosition( (float)( Math.random() * 100.0 ), (float)( Math.random() * 100.0 ) );
			graph.addNode( 0, node );
		}
		return node;
	}

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void main( String args[] ) throws IOException
	{
		DNVGraph graph = convert( Settings.GRAPHS_PATH + "dep.csv" );
		graph.writeGraph( Settings.GRAPHS_PATH + "dep.dnv" );
	}
}
