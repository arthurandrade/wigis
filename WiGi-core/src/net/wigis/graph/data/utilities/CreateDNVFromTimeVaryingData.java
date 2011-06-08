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
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.wigis.graph.GraphsPathFilter;
import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVEntity;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.settings.Settings;

// TODO: Auto-generated Javadoc
/**
 * The Class CreateDNVFromTimeVaryingData.
 * 
 * @author Brynjar Gretarsson
 */
public class CreateDNVFromTimeVaryingData
{

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main( String args[] )
	{
		GraphsPathFilter.init();

		String inputFile = Settings.GRAPHS_PATH + "userReverts.txt";

		DNVGraph graph = createGraph( inputFile );

		graph.writeGraph( inputFile + ".dnv" );
	}

	/**
	 * Creates the graph.
	 * 
	 * @param inputFile
	 *            the input file
	 * @return the dNV graph
	 */
	private static DNVGraph createGraph( String inputFile )
	{
		File file = new File( inputFile );

		DNVGraph graph = null;
		if( file.exists() )
		{
			graph = new DNVGraph();

			FileReader fr;
			try
			{
				fr = new FileReader( file );
				BufferedReader br = new BufferedReader( fr );
				String line;
				String lineArray[];
				Map<Long, List<String>> timeToEdges = new HashMap<Long, List<String>>();
				List<String> tempEdges;
				Long time;
				while( ( line = br.readLine() ) != null )
				{
					lineArray = line.split( "\t" );
					time = Long.parseLong( lineArray[2] );
					tempEdges = timeToEdges.get( time );
					if( tempEdges == null )
					{
						tempEdges = new LinkedList<String>();
						timeToEdges.put( time, tempEdges );
					}
					tempEdges.add( lineArray[0] + "->" + lineArray[1] );
				}

				br.close();
				fr.close();

				List<Long> keys = new ArrayList<Long>( timeToEdges.keySet() );
				Collections.sort( keys );

				List<String> edgesToCreate;

				int level = 0;
				for( Long t : keys )
				{
					edgesToCreate = timeToEdges.get( t );
					createEdges( graph, level, edgesToCreate, t );
					// level++;
				}
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
		}

		return graph;
	}

	/**
	 * Creates the edges.
	 * 
	 * @param graph
	 *            the graph
	 * @param level
	 *            the level
	 * @param edgesToCreate
	 *            the edges to create
	 * @param time
	 *            the time
	 */
	private static void createEdges( DNVGraph graph, int level, List<String> edgesToCreate, long time )
	{
		System.out.println( "Creating edges for level " + level + " and time " + time );
		if( level > 0 )
		{
			List<DNVEntity> nodesAndEdges = graph.getNodesAndEdges( level - 1 );
			for( DNVEntity entity : nodesAndEdges )
			{
				graph.addNode( level, entity );
			}
		}
		String color = "#22AAFF";
		for( String edgeStr : edgesToCreate )
		{
			String nodes[] = edgeStr.split( "->" );
			DNVNode node1 = (DNVNode)graph.getNodeByBbId( nodes[0] );
			if( node1 == null )
			{
				node1 = new DNVNode( graph );
				node1.setLabel( nodes[0] );
				node1.setBbId( nodes[0] );
				node1.setColor( color );
				node1.setProperty( "time", "" + time );
				graph.addNode( level, node1 );
			}
			DNVNode node2 = (DNVNode)graph.getNodeByBbId( nodes[1] );
			if( node2 == null )
			{
				node2 = new DNVNode( graph );
				node2.setLabel( nodes[1] );
				node2.setBbId( nodes[1] );
				node2.setColor( color );
				node2.setProperty( "time", "" + time );
				graph.addNode( level, node2 );
			}

			DNVEdge edge = new DNVEdge( graph );
			edge.setFrom( node1 );
			edge.setTo( node2 );
			edge.setBbId( edgeStr );
			edge.setProperty( "time", "" + time );
			graph.addNode( level, edge );
		}
	}
}
