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
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.settings.Settings;

// TODO: Auto-generated Javadoc
/**
 * The Class ConvertDNVToTomSawyer.
 * 
 * @author Brynjar Gretarsson
 */
public class ConvertDNVToTomSawyer
{

	/** The counter. */
	public static int counter = 10;

	/**
	 * Logger.
	 * 
	 * @param inputFile
	 *            the input file
	 * @param outputFile
	 *            the output file
	 * @param templateFile
	 *            the template file
	 * @param graphNameLocator
	 *            the graph name locator
	 * @param nodesLocator
	 *            the nodes locator
	 * @param edgesLocator
	 *            the edges locator
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	// // private static Log logger = LogFactory.getLog(
	// ConvertDNVToTomSawyer.class );

	public static void convert( String inputFile, String outputFile, String templateFile, String graphNameLocator, String nodesLocator,
			String edgesLocator ) throws IOException
	{
		System.out.println( "Converting " + inputFile + " to Tom Sawyer format." );
		System.out.println( "Output can be found in " + outputFile );
		counter = 10;
		DNVGraph graph = new DNVGraph( inputFile );
		FileReader fr = new FileReader( templateFile );
		BufferedReader br = new BufferedReader( fr );
		FileWriter fw = new FileWriter( outputFile );

		String line = br.readLine();
		while( line != null )
		{
			if( line.contains( graphNameLocator ) )
			{
				line = line.replace( graphNameLocator, "Untitled" );// inputFile.substring(
				// inputFile.lastIndexOf(
				// "\\" ) +
				// 1 ) );
			}

			if( line.contains( nodesLocator ) )
			{
				writeNodes( fw, graph );
			}
			else if( line.contains( edgesLocator ) )
			{
				writeEdges( fw, graph );
			}
			else
			{
				fw.write( line + "\n" );
			}

			line = br.readLine();
		}

		br.close();
		fr.close();

		fw.close();
	}

	/**
	 * Write edges.
	 * 
	 * @param fw
	 *            the fw
	 * @param graph
	 *            the graph
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private static void writeEdges( FileWriter fw, DNVGraph graph ) throws IOException
	{
		Iterator<DNVEdge> edges = graph.getEdges( 0 ).iterator();
		DNVEdge tempEdge;
		while( edges.hasNext() )
		{
			counter++;
			tempEdge = edges.next();
			tempEdge.setId( counter );
			fw.write( "            <edge id=\"" + counter + "\" source=\"" + tempEdge.getFrom().getId() + "\" target=\"" + tempEdge.getTo().getId()
					+ "\">\n" );
			fw.write( "              <attributes>\n" );
			fw.write( "                <attribute index=\"1\" name=\"Thickness\" type=\"Integer\" value=\"1\"/>\n" );
			fw.write( "                <attribute index=\"2\" name=\"Edge_Style\" type=\"Integer\" value=\"0\"/>\n" );
			fw.write( "              </attributes>\n" );
			fw.write( "            </edge>\n" );
		}
	}

	/**
	 * Write nodes.
	 * 
	 * @param fw
	 *            the fw
	 * @param graph
	 *            the graph
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private static void writeNodes( FileWriter fw, DNVGraph graph ) throws IOException
	{
		Iterator<DNVNode> nodes = graph.getNodes( 0 ).iterator();
		DNVNode tempNode;
		while( nodes.hasNext() )
		{
			counter++;
			tempNode = nodes.next();
			tempNode.setId( counter );
			fw.write( "            <node id=\"" + counter + "\">\n" );
			fw.write( "              <attributes>\n" );
			fw.write( "                <attribute index=\"2\" name=\"Background_Color\" type=\"Color\" value=\""
					+ Math.round( 255 * tempNode.getColor().getX() ) + " " + Math.round( 255 * tempNode.getColor().getY() ) + " "
					+ Math.round( 255 * tempNode.getColor().getZ() ) + "\"/>\n" );
			fw.write( "              </attributes>\n" );
			fw.write( "              <center x=\"" + ( tempNode.getPosition().getX() * 800.0 ) + "\" y=\"" + ( tempNode.getPosition().getY() * 800.0 )
					+ "\"/>\n" );
			fw.write( "            </node>\n" );
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
		String inputfiles[] = { Settings.GRAPHS_PATH + "smallworld_10_20.dnv", Settings.GRAPHS_PATH + "smallworld_100_200.dnv",
				Settings.GRAPHS_PATH + "smallworld_1000_2000.dnv", Settings.GRAPHS_PATH + "smallworld_10000_20000.dnv",
				Settings.GRAPHS_PATH + "smallworld_100000_200000.dnv", Settings.GRAPHS_PATH + "smallworld_1000000_2000000.dnv" };

		String outputfiles[] = { Settings.GRAPHS_PATH + "\\tomsawyer\\smallworld_10_20.tsv",
				Settings.GRAPHS_PATH + "\\tomsawyer\\smallworld_100_200.tsv", Settings.GRAPHS_PATH + "\\tomsawyer\\smallworld_1000_2000.tsv",
				Settings.GRAPHS_PATH + "\\tomsawyer\\smallworld_10000_20000.tsv", Settings.GRAPHS_PATH + "\\tomsawyer\\smallworld_100000_200000.tsv",
				Settings.GRAPHS_PATH + "\\tomsawyer\\smallworld_1000000_2000000.tsv", };

		String templateFile = Settings.GRAPHS_PATH + "\\tomsawyer\\template.tsv";
		String graphNameLocator = "[GRAPH_NAME]";
		String nodesLocator = "[NODES]";
		String edgesLocator = "[EDGES]";

		for( int i = 0; i < inputfiles.length; i++ )
		{
			convert( inputfiles[i], outputfiles[i], templateFile, graphNameLocator, nodesLocator, edgesLocator );
		}
	}
}
