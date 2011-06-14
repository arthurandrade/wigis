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

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.wigis.graph.GraphsPathFilter;
import net.wigis.graph.ImageGetter;
import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVEntity;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.layout.FruchtermanReingold;
import net.wigis.graph.dnv.utilities.Vector3D;
import net.wigis.settings.Settings;
import au.com.bytecode.opencsv.CSVReader;

// TODO: Auto-generated Javadoc
/**
 * The Class ConvertWCToDNV.
 * 
 * @author Brynjar Gretarsson
 */
public class ConvertWCToDNV
{

	/** Logger. */
	// // private static Log logger = LogFactory.getLog(
	// ConvertDNVToTomSawyer.class );

	private static Map<String, String[]> countryToColor = new HashMap<String, String[]>();

	/**
	 * Creates the country to color map.
	 */
	private static void createCountryToColorMap()
	{
		countryToColor.put( "Angola", new String[] { "fa1b20", "#fcc165" } );
		countryToColor.put( "Ecuador", new String[] { "#f1df13", "#253f7c" } );
		countryToColor.put( "France", new String[] { "#314294", "#e82d24" } );
		countryToColor.put( "Mexico", new String[] { "#01966e", "#972f24" } );
		countryToColor.put( "Argentina", new String[] { "#bdd7e8", "#f7f7f7" } );
		countryToColor.put( "Nigeria", new String[] { "#008477", "#ebecf0" } );
		countryToColor.put( "Chile", new String[] { "#c52328", "#202139" } );
		countryToColor.put( "Australia", new String[] { "#f4ba3c", "#597056" } );
		countryToColor.put( "Zaire", new String[] { "#009632", "#efd400" } );
		countryToColor.put( "Yugoslavia", new String[] { "#0c1a61", "#c1c4c9" } );
		countryToColor.put( "Saudi Arabia", new String[] { "#f0f0f2", "#216a61" } );
		countryToColor.put( "Brazil", new String[] { "#f9e000", "#0237a1" } );
		countryToColor.put( "Bolivia", new String[] { "#56a35b", "#c31c26" } );
		countryToColor.put( "No. Ireland", new String[] { "#578e57", "#161b1f" } );
		countryToColor.put( "Peru", new String[] { "#eef0ef", "#e42127" } );
		countryToColor.put( "Romania", new String[] { "#e2b133", "#091b78" } );
		countryToColor.put( "Uruguay", new String[] { "#96b8de", "#201d2e" } );
		countryToColor.put( "USSR", new String[] { "#c8243c", "#fbfaf8" } );
		countryToColor.put( "Belgium", new String[] { "#e41e2d", "#fae616" } );
		countryToColor.put( "Morocco", new String[] { "#0e5e45", "#e00523" } );
		countryToColor.put( "USA", new String[] { "#f6fafb", "#1e2432" } );
		countryToColor.put( "Paraguay", new String[] { "#dd2032", "#fcfefb" } );
		countryToColor.put( "New Zealand", new String[] { "#0f0f0f", "#fbfbfb" } );
		countryToColor.put( "Wales", new String[] { "#b9120c", "#e1e0e6" } );
		countryToColor.put( "Germany", new String[] { "#f1e7e6", "#27332f" } );
		countryToColor.put( "Denmark", new String[] { "#cf3630", "#cdd7e3" } );
		countryToColor.put( "Colombia", new String[] { "#f9e337", "#2c1985" } );
		countryToColor.put( "Switzerland", new String[] { "#d9292b", "#dbd2c9" } );
		countryToColor.put( "Slovenia", new String[] { "#f1f0f5", "#2a3a3a" } );
		countryToColor.put( "Sweden", new String[] { "#f4c541", "#1b3252" } );
		countryToColor.put( "Czechoslovakia", new String[] { "#e4413c", "#685283" } );
		countryToColor.put( "Holland", new String[] { "#f37022", "#620000" } );
		countryToColor.put( "Hungary", new String[] { "#d01c25", "#efe6e1" } );
		countryToColor.put( "Egypt", new String[] { "#fdfdfd", "#211519" } );
		countryToColor.put( "Senegal", new String[] { "#efeff1", "#fcc04e" } );
		countryToColor.put( "Austria", new String[] { "#e12c25", "#fffbea" } );
		countryToColor.put( "Italy", new String[] { "#2f439a", "#fffdf8" } );
		countryToColor.put( "Spain", new String[] { "#c02a36", "#efcc8c" } );
		countryToColor.put( "Canada", new String[] { "#fc2014", "#ffdbc8" } );
		countryToColor.put( "Bulgaria", new String[] { "#efeff1", "#002f29" } );
		countryToColor.put( "Russia", new String[] { "#edf1f2", "#0d62cf" } );
		countryToColor.put( "Haiti", new String[] { "#d6021b", "#ffffff" } );
		countryToColor.put( "China", new String[] { "#fa4e4a", "#f7ffff" } );
		countryToColor.put( "Greece", new String[] { "#335bb2", "#fffef8" } );
		countryToColor.put( "Cuba", new String[] { "#e32702", "#376aa9" } );
		countryToColor.put( "Iraq", new String[] { "#3f9663", "#f2ffff" } );
		countryToColor.put( "Neth. East Indies", new String[] { "#db2220", "#fdfeff" } );
		countryToColor.put( "Poland", new String[] { "#f1f2f7", "#d05b75" } );
		countryToColor.put( "Norway", new String[] { "#e02a37", "#e4e4ec" } );
		countryToColor.put( "Ukraine", new String[] { "#ffe81c", "#295bca" } );
		countryToColor.put( "Japan", new String[] { "#3047a5", "#f7c435" } );
		countryToColor.put( "Jamaica", new String[] { "#57a86e", "#352118" } );
		countryToColor.put( "England", new String[] { "#f9f8f4", "#f63526" } );
		countryToColor.put( "Croatia", new String[] { "#d11c23", "#f6f7fb" } );
		countryToColor.put( "Tunisia", new String[] { "#bdbcc2", "#dc4031" } );
		countryToColor.put( "Portugal", new String[] { "#d31f2a", "#015144" } );
		countryToColor.put( "Iran", new String[] { "#d5d8e7", "#1b2d31" } );
		countryToColor.put( "Trinidad and Tobago", new String[] { "#da3e32", "#d3e4ec" } );
		countryToColor.put( "Ireland", new String[] { "#018148", "#eb8652" } );
		countryToColor.put( "Honduras", new String[] { "#d3d3db", "#5b74b6" } );
		countryToColor.put( "W. Germany", new String[] { "#f1e7e6", "#27332f" } );
		countryToColor.put( "Turkey", new String[] { "#cb2026", "#f9f9fb" } );
		countryToColor.put( "Kuwait", new String[] { "#02298e", "#efefe5" } );
		countryToColor.put( "So. Korea", new String[] { "#e52530", "#ebfaff" } );
		countryToColor.put( "No. Korea", new String[] { "#d62829", "#ecd4d2" } );
		countryToColor.put( "Czech Republic", new String[] { "#e4413c", "#685283" } );
		countryToColor.put( "So. Africa", new String[] { "#f0d554", "#327645" } );
		countryToColor.put( "Cameroon", new String[] { "#156b3e", "#c6202a" } );
		countryToColor.put( "Ghana", new String[] { "#cac8cd", "#121214" } );
		countryToColor.put( "Scotland", new String[] { "#15375d", "#cac3ca" } );
		countryToColor.put( "Togo", new String[] { "#f9e258", "#4e8b46" } );
		countryToColor.put( "Ivory Coast", new String[] { "#f08f1e", "#20643d" } );
		countryToColor.put( "Israel", new String[] { "#2862b4", "#d2d1d7" } );
		countryToColor.put( "Serbia and Montenegro", new String[] { "#01539f", "#d9dde6" } );
		countryToColor.put( "UAE", new String[] { "#ecedf1", "#8e271e" } );
		countryToColor.put( "Costa Rica", new String[] { "#f05246", "#0460a9" } );
		countryToColor.put( "El Salvador", new String[] { "#757ed9", "#e6e8fd" } );
		countryToColor.put( "Algeria", new String[] { "#469d01", "#e5e9e8" } );
		countryToColor.put( "E. Germany", new String[] { "#000000", "#FFFF00" } );
	}

	/**
	 * Convert.
	 * 
	 * @param filename
	 *            the filename
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void convert( String filename ) throws IOException
	{
		createCountryToColorMap();

		FileReader fr = new FileReader( filename );
		CSVReader reader = new CSVReader( fr );

		String[] line;
		DNVGraph yearGraph = null;
		DNVGraph totalGraph = new DNVGraph();
		String currentYear = "";

		while( ( line = reader.readNext() ) != null )
		{
			// printArray( line );
			if( line[0].trim().startsWith( "YEAR" ) )
			{
				writeGraphs( yearGraph, totalGraph );
				yearGraph = new DNVGraph();
				yearGraph.setName( line[0].trim() );
				currentYear = line[0].trim().substring( line[0].trim().indexOf( "YEAR" ) + 5 );
			}
			else if( ( line.length == 1 && line[0].trim().equals( "" ) ) || line.length == 0 )
			{
				// ignore empty lines
			}
			else
			{
				// Handle edge
				addEdge( line, yearGraph, currentYear, "" );
				addEdge( line, totalGraph, currentYear, currentYear + "   " );
			}
		}

		reader.close();
		fr.close();

		List<DNVNode> nodes = totalGraph.getNodes( 0 );
		for( DNVNode node : nodes )
		{
			System.out.println( node.getLabel() );
		}

		writeGraphs( yearGraph, totalGraph );
	}

	/**
	 * Write graphs.
	 * 
	 * @param yearGraph
	 *            the year graph
	 * @param totalGraph
	 *            the total graph
	 */
	private static void writeGraphs( DNVGraph yearGraph, DNVGraph totalGraph )
	{
		if( yearGraph != null )
		{
			FruchtermanReingold fr = new FruchtermanReingold();
			fr.runLayout( 100, 100, yearGraph, 0.1f, 0, false, false, true );
			yearGraph.writeGraph( Settings.GRAPHS_PATH + "WC_" + yearGraph.getName() + ".dnv" );
			fr.runLayout( 100, 100, totalGraph, 0.1f, 0, false, false, true );
			totalGraph.writeGraph( Settings.GRAPHS_PATH + "WC_until_" + yearGraph.getName() + ".dnv" );
		}
	}

	// private static void printArray( String[] line )
	// {
	// for( int i = 0; i < line.length; i++ )
	// {
	// System.out.println( i + " : " + line[i] );
	// }
	//
	// }

	/**
	 * Adds the edge.
	 * 
	 * @param line
	 *            the line
	 * @param graph
	 *            the graph
	 * @param currentYear
	 *            the current year
	 * @param labelPrefix
	 *            the label prefix
	 */
	private static void addEdge( String[] line, DNVGraph graph, String currentYear, String labelPrefix )
	{
		DNVEntity fromNode = getNode( line, graph, 0, currentYear );
		DNVEntity toNode = getNode( line, graph, 1, currentYear );
		DNVEdge edge = new DNVEdge( graph );
		edge.setFrom( (DNVNode)fromNode );
		edge.setTo( (DNVNode)toNode );
		edge.setLabel( labelPrefix + line[2].trim() );
		String score[] = line[2].trim().split( ":" );
		if( !score[0].equals( score[1] ) )
		{
			edge.setDirectional( true );
			if( !almostWhite( edge.getFrom().getColor() ) )
			{
				edge.setColor( edge.getFrom().getColor() );
			}
			else
			{
				edge.setColor( edge.getFrom().getOutlineColor() );
			}
			edge.setThickness( 3 );
			edge.setLabelColor( edge.getFrom().getLabelColor() );
			edge.setLabelOutlineColor( edge.getFrom().getLabelOutlineColor() );
			edge.getFrom().setRadius( edge.getFrom().getRadius() + 0.02f );
		}
		else
		{
			edge.setDirectional( false );
		}
		edge.setProperty( "time", currentYear );
		graph.addNode( 0, edge );
	}

	/**
	 * Almost white.
	 * 
	 * @param color
	 *            the color
	 * @return true, if successful
	 */
	private static boolean almostWhite( Vector3D color )
	{
		return color.getX() > 0.9 && color.getY() > 0.9 && color.getZ() > 0.9;
	}

	/**
	 * Gets the node.
	 * 
	 * @param line
	 *            the line
	 * @param graph
	 *            the graph
	 * @param index
	 *            the index
	 * @param currentYear
	 *            the current year
	 * @return the node
	 */
	private static DNVEntity getNode( String[] line, DNVGraph graph, int index, String currentYear )
	{
		DNVEntity node = graph.getNodeByBbId( line[index].trim() );
		if( node == null )
		{
			node = new DNVNode( graph );
			node.setLabel( line[index].trim() );
			node.setBbId( line[index].trim() );
			( (DNVNode)node ).setPosition( (float)( Math.random() * 100.0 ), (float)( Math.random() * 100.0 ) );
			node.setProperty( "time", currentYear );
			( (DNVNode)node ).setRadius( 1 );
			ImageGetter ig = new ImageGetter( (DNVNode)node, line[index].trim() + " flag" );
			ig.run();
			String[] colors = countryToColor.get( line[index].trim() );
			if( colors != null && colors.length >= 2 )
			{
				node.setColor( colors[0] );
				node.setOutlineColor( colors[1] );
				node.setLabelColor( colors[1] );
				node.setLabelOutlineColor( colors[0] );
			}
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
		GraphsPathFilter.init();
		convert( Settings.GRAPHS_PATH + "WCEdges2.txt" );
	}
}
