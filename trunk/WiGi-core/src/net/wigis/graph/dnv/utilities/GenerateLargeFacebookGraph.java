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
package net.wigis.graph.dnv.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.wigis.graph.GraphsPathFilter;
import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVEntity;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.layout.implementations.FruchtermanReingold;
import net.wigis.settings.Settings;

/**
 * @author brynjar
 *
 */
public class GenerateLargeFacebookGraph
{
	
	/**
	 * @param fileListFile
	 * @return
	 * @throws IOException 
	 */
	private static List<String> readFileToList( String filename ) throws IOException
	{
		File file = new File( filename );
		List<String> list = new ArrayList<String>();
		String line = "";
		
		FileReader fr = new FileReader( file );
		BufferedReader br = new BufferedReader( fr );
		
		line = br.readLine();
		while( line != null )
		{
			list.add( line );
			line = br.readLine();
		}
		
		br.close();
		fr.close();
		
		return list;
	}

	
	
	public static void main( String args[] ) throws IOException
	{
		GraphsPathFilter.init();
		String directory = Settings.GRAPHS_PATH + "facebook/";
		String fileListFile = directory + "allDnvFiles.txt";
		boolean generateFriendEdges = false;
		boolean ignoreItems = false;
		String outputFileName = "largeMoviesGraph";
		if( ignoreItems )
		{
			outputFileName += "WithoutItems";
		}
		if( generateFriendEdges )
		{
			outputFileName += "WithFriendEdges";
		}
		outputFileName += ".dnv";
		List<String> dnvFiles = readFileToList( fileListFile );
		DNVGraph graph = new DNVGraph();
		
		for( String filename : dnvFiles )
		{
			System.out.println();
			System.out.println( filename );
			DNVGraph tempGraph = new DNVGraph( directory + filename );
			List<DNVNode> nodes = tempGraph.getNodes( 0 );
			DNVNode newNode;
			for( DNVNode node : nodes )
			{
				if( node.getBbId() != null && !node.getBbId().equals("") )
				{
					System.out.println( "Friend" );
					System.out.println( node.getLabel() );
					if( graph.getNodeByBbId( node.getLabel() ) == null )
					{
						newNode = new DNVNode(graph);
						newNode.setLabel( node.getLabel() );
						newNode.setBbId( node.getLabel() );
						newNode.setColor( 0.5f, 0.5f, 1 );
						newNode.setPosition( (float)Math.random(), (float)Math.random() );
						newNode.setType( "person" );
						graph.addNode( 0, newNode );
					}
				}
				else
				{
					if( !ignoreItems )
					{
						System.out.println( "Item" );
						System.out.println( node.getLabel() );
						if( graph.getNodeByBbId( node.getLabel() ) == null )
						{
							newNode = new DNVNode(graph);
							newNode.setLabel( node.getLabel() );
							newNode.setBbId( node.getLabel() );
							newNode.setColor( 1.0f, 1.0f, 0 );
							newNode.setPosition( (float)Math.random(), (float)Math.random() );
							newNode.setType( "item" );
							graph.addNode( 0, newNode );
						}
					}
				}
			}
			
			List<DNVEdge> edges = tempGraph.getEdges( 0 );
			for( DNVEdge edge : edges )
			{
				System.out.println( edge.getFrom().getLabel() + "->" + edge.getTo().getLabel() );
				if( graph.getNodeByBbId( edge.getFrom().getLabel() + "->" + edge.getTo().getLabel() ) == null )
				{
					DNVNode fromNode = (DNVNode)graph.getNodeByBbId( edge.getFrom().getLabel() );
					DNVNode toNode = (DNVNode)graph.getNodeByBbId( edge.getTo().getLabel() );
					if( fromNode != null && toNode != null )
					{
						DNVEdge newEdge = new DNVEdge( fromNode, toNode, graph );
						newEdge.setBbId( fromNode.getLabel() + "->" + toNode.getLabel() );
						newEdge.setType( "likes" );
						graph.addEntity( 0, newEdge );
					}
				}
			}
			
			if( generateFriendEdges )
			{
				DNVNode tempUserNode = (DNVNode)tempGraph.getNodesByType( 0, "user" ).get( 0 );
				if( tempUserNode != null )
				{
					DNVNode userNode = (DNVNode)graph.getNodeByBbId( tempUserNode.getLabel() );
					Map<Integer,DNVEntity> friends = tempGraph.getNodesByType( 0, "friend" );
					for( DNVEntity friend : friends.values() )
					{
						DNVNode friendNode = (DNVNode)graph.getNodeByBbId( friend.getLabel() );
						DNVEdge newEdge = new DNVEdge( userNode, friendNode, graph );
						newEdge.setBbId( userNode.getLabel() + "->" + friendNode.getLabel() );
						newEdge.setType( "friendEdge" );
						graph.addEntity( 0, newEdge );
					}
				}
			}
		}
		
		graph.removeIsolatedNodes();
		new FruchtermanReingold().runLayout( 80, 80, graph, 0.1f, 0, false, false );
		graph.writeGraph( directory + outputFileName );			
	}
}
