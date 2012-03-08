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
import java.util.List;
import java.util.StringTokenizer;

import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.settings.Settings;
import au.com.bytecode.opencsv.CSVReader;

// TODO: Auto-generated Javadoc
/**
 * The Class ConvertPNNLDataToDNV.
 * 
 * @author Brynjar Gretarsson
 */
public class ConvertPNNLDataToDNV
{

	/**
	 * Logger.
	 * 
	 * @param graphFileName
	 *            the graph file name
	 * @param nodeFileName
	 *            the node file name
	 * @param linkFileName
	 *            the link file name
	 * @param dnvFileName
	 *            the dnv file name
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	// // private static Log logger = LogFactory.getLog(
	// ConvertDNVToTomSawyer.class );

	public static void convert( String graphFileName, String nodeFileName, String linkFileName, String dnvFileName ) throws IOException
	{
		File graphFile = new File( graphFileName );
		File nodeFile = new File( nodeFileName );
		File linkFile = new File( linkFileName );

		CSVReader nodeFileReader = new CSVReader( new FileReader( nodeFile ) );
		List<?> nodeFileList = nodeFileReader.readAll();

		CSVReader linkFileReader = new CSVReader( new FileReader( linkFile ) );
		List<?> linkFileList = linkFileReader.readAll();

		BufferedReader graphFileReader = new BufferedReader( new FileReader( graphFile ) );
		String line = graphFileReader.readLine();

		DNVGraph graph = new DNVGraph();

		if( line != null )
		{
			StringTokenizer lineTokenizer = new StringTokenizer( line );
			int numberOfNodes = Integer.parseInt( lineTokenizer.nextToken() );
			int numberOfLinks = Integer.parseInt( lineTokenizer.nextToken() );

			if( numberOfNodes != nodeFileList.size() )
			{
				System.out.println( "WARNING: graph file does not match node file. graph file number of nodes = " + numberOfNodes
						+ ". node file number of nodes = " + nodeFileList.size() );
			}
			if( numberOfLinks != linkFileList.size() )
			{
				System.out.println( "WARNING: graph file does not match link file. graph file number of links = " + numberOfLinks
						+ ". link file number of links = " + linkFileList.size() );
			}

			int from;
			int to;
			String[] fromNodeInfo;
			String[] toNodeInfo;
			String[] edgeInfo;
			DNVEdge edge;
			DNVNode fromNode;
			DNVNode toNode;
			int lineIndex = 0;
			line = graphFileReader.readLine();
			while( line != null && !line.equals( "" ) )
			{
				lineTokenizer = new StringTokenizer( line );
				from = Integer.parseInt( lineTokenizer.nextToken() );
				to = Integer.parseInt( lineTokenizer.nextToken() );

				fromNodeInfo = (String[])nodeFileList.get( from - 1 );
				fromNode = (DNVNode)graph.getNodeByBbId( fromNodeInfo[1] );
				if( fromNode == null )
				{
					fromNode = new DNVNode( graph );
					fromNode.setLabel( fromNodeInfo[0] );
					fromNode.setBbId( fromNodeInfo[1] );
					fromNode.setPosition( (float)Math.random(), (float)Math.random() );
					graph.addNode( 0, fromNode );
				}

				toNodeInfo = (String[])nodeFileList.get( to - 1 );
				toNode = (DNVNode)graph.getNodeByBbId( toNodeInfo[1] );
				if( toNode == null )
				{
					toNode = new DNVNode( graph );
					toNode.setLabel( toNodeInfo[0] );
					toNode.setBbId( toNodeInfo[1] );
					toNode.setPosition( (float)Math.random(), (float)Math.random() );
					graph.addNode( 0, toNode );
				}

				edgeInfo = (String[])linkFileList.get( lineIndex );
				edge = new DNVEdge( fromNode, toNode, graph );
				edge.setLabel( edgeInfo[1] + ", " + edgeInfo[3] + ", " + edgeInfo[5] + ", " + edgeInfo[6] + ", " + edgeInfo[7] );
				graph.addNode( 0, edge );

				line = graphFileReader.readLine();
				lineIndex++;
			}

			graph.writeGraph( dnvFileName );
		}
		else
		{
			System.out.println( "WARNING: graph file has no data" );
		}
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
		ConvertPNNLDataToDNV.convert( "/graphs/PNNL/authors_graph_50k.net", "/graphs/PNNL/authors_graph_50k.nmv",
				"/graphs/PNNL/authors_graph_50k.lmv", Settings.GRAPHS_PATH + "authors_graph_50k.dnv" );
	}
}
