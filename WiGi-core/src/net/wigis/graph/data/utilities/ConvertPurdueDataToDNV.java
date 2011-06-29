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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.wigis.graph.GraphsPathFilter;
import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.layout.implementations.FruchtermanReingold;
import net.wigis.graph.dnv.utilities.GraphFunctions;
import net.wigis.graph.dnv.utilities.SortByFloatProperty;
import net.wigis.graph.dnv.utilities.Vector3D;
import net.wigis.settings.Settings;
import au.com.bytecode.opencsv.CSVReader;

/**
 * @author brynjar
 *
 */
public class ConvertPurdueDataToDNV
{
	
	private static final Vector3D LIGHT_GREEN = new Vector3D( 0.5f, 1.0f, 0.5f );
	private static final Vector3D LIGHT_RED = new Vector3D( 1.0f, 0f, 0f );

	
	public static void main( String args[] ) throws IOException
	{
		GraphsPathFilter.init();
		String path = Settings.GRAPHS_PATH + "PADA/";
		String nodesFile = "people-w-attributes.csv";
		String co_occurenceFile = "co-occurences-filter-ts2.csv";
		
		DNVGraph graph = new DNVGraph();
		
		File file = new File( path, nodesFile );
		FileReader fr = new FileReader( file );
		CSVReader csv = new CSVReader( fr );
		
		String[] line;
		DNVNode node;
		while( (line=csv.readNext()) != null )
		{
			if( !line[0].equals( "" ) )
			{
				node = (DNVNode)graph.getNodeByBbId( line[0] );
				if( node == null )
				{
	//				if( line[3].trim().equals( "1" ) )
	//				{
						node = new DNVNode( graph );
						node.setBbId( line[0] );
						node.setProperty( "numDocuments", line[1] );
						node.setProperty( "numFamilyRelations", line[2] );
						node.setProperty( "hasFamilyRelation", line[3] );
						if( line[3].trim().equals( "1" ) )
						{
							node.setProperty( "Contents", "hasFamilyRelation" );
						}
						node.setPosition( (float)Math.random(), (float)Math.random() );
						node.setLabel( line[4] );
						
						graph.addNode( 0, node );
	//				}
				}
				else
				{
					System.out.println( "Multiple occurances of same node: " + line[0] );
				}
			}	
		}
		csv.close();
		fr.close();
		System.out.println( "Added " + graph.getGraphSize( 0 ) + " nodes to the graph." );
		
		file = new File( path, co_occurenceFile );
		fr = new FileReader( file );
		csv = new CSVReader( fr );
		DNVNode node1;
		DNVNode node2;
		DNVEdge edge;
		long time;
		while( (line=csv.readNext()) != null )
		{
			if( !line[0].equals( "" ) )
			{
				node1 = (DNVNode)graph.getNodeByBbId( line[0] );
				node2 = (DNVNode)graph.getNodeByBbId( line[1] );
				if( !node1.getNeighborMap().containsKey( node2.getId() ) )
				{
					edge = new DNVEdge( node1, node2, graph );
					edge.setBbId( node1.getBbId() + "->" + node2.getBbId() );
					time = convertTimeStampToLong( line[3] );
					if( time != -1 )
					{
						edge.setProperty( "time", "" + time );
					}
					
					graph.addEntity( 0, edge );
				}
			}
		}
		
		GraphFunctions.removeIsolatedNodes( graph, 0 );
		
		List<DNVNode> nodes = graph.getNodes( 0 );
		
		for( DNVNode tempNode : nodes )
		{
			List<DNVEdge> edges = new ArrayList<DNVEdge>( tempNode.getFromEdges() );
			edges.addAll( tempNode.getToEdges() );
			SortByFloatProperty sbfp = new SortByFloatProperty( "time", true );
			Collections.sort( edges, sbfp );
			tempNode.setProperty( "maxTime", edges.get( 0 ).getProperty( "time" ) );
			tempNode.setProperty( "minTime", edges.get( edges.size()-1 ).getProperty( "time" ) );
		}
		
		
		SortByFloatProperty sbfp = new SortByFloatProperty( "numDocuments", true );
		Collections.sort( nodes, sbfp );
		node = nodes.get( 0 );
		int maxNumDocuments = Integer.parseInt( node.getProperty( "numDocuments" ) );
		System.out.println( "Max number of docs " + maxNumDocuments );
		for( DNVNode tempNode : nodes )
		{
			Vector3D color = new Vector3D( LIGHT_GREEN );
			if( tempNode.hasProperty( "numDocuments" ) ) 
			{
				Vector3D difference = new Vector3D( LIGHT_RED );
				difference.subtract( color );
				difference.dotProduct( (float)(Integer.parseInt( tempNode.getProperty( "numDocuments" ) ) )/(float)maxNumDocuments );
				color.add( difference );
				color.setX( (float)Math.max( 0, color.getX() ) );
				color.setY( (float)Math.max( 0, color.getY() ) );
				color.setZ( (float)Math.max( 0, color.getZ() ) );
				color.setX( (float)Math.min( 1, color.getX() ) );
				color.setY( (float)Math.min( 1, color.getY() ) );
				color.setZ( (float)Math.min( 1, color.getZ() ) );
			}
			tempNode.setColor( color );
		}
		
		new FruchtermanReingold().runLayout( 100, 100, graph, 0.01f, 0, false, false );

		System.out.println( "Graph has " + graph.getGraphSize( 0 ) + " nodes and " + graph.getEdgeMap( 0 ).size() + " edges." );

		graph.writeGraph( Settings.GRAPHS_PATH + "PADA/" + nodesFile + ".dnv" );
	}

	/**
	 * @param string
	 * @return
	 */
	private static long convertTimeStampToLong( String timestamp )
	{
		if( timestamp == null || timestamp.trim().equals( "" ) )
		{
			return -1;
		}
		
//		System.out.println( "timestamp:" + timestamp );
		String month = timestamp.substring( 0, timestamp.indexOf( ' ' ) );
		if( month.length() == 3 )
		{
			if( month.equals( "Jan" ) )
			{
				month = "01";
			}
			else if( month.equals( "Feb" ) )
			{
				month = "02";
			}
			else if( month.equals( "Mar" ) )
			{
				month = "03";
			}
			else if( month.equals( "Apr" ) )
			{
				month = "04";
			}
			else if( month.equals( "May" ) )
			{
				month = "05";
			}
			else if( month.equals( "Jun" ) )
			{
				month = "06";
			}
			else if( month.equals( "Jul" ) )
			{
				month = "07";
			}
			else if( month.equals( "Aug" ) )
			{
				month = "08";
			}
			else if( month.equals( "Sep" ) )
			{
				month = "09";
			}
			else if( month.equals( "Oct" ) )
			{
				month = "10";
			}
			else if( month.equals( "Nov" ) )
			{
				month = "11";
			}
			else if( month.equals( "Dec" ) )
			{
				month = "12";
			}
		}
		if( month.length() == 1 )
		{
			month = "0" + month;
		}
		timestamp = timestamp.substring( timestamp.indexOf( ' ' ) + 1 );
		String date = timestamp.substring( 0, timestamp.indexOf( ' ' ) );
		if( date.length() == 1 )
		{
			date = "0" + month;
		}
		timestamp = timestamp.substring( timestamp.indexOf( ' ' ) + 1 );
		String hour = timestamp.substring( 0, timestamp.indexOf( ':' ) );
		if( hour.length() == 1 )
		{
			hour = "0" + hour;
		}
		timestamp = timestamp.substring( timestamp.indexOf( ':' ) + 1 );
		String minutes = timestamp.substring( 0, timestamp.indexOf( ':' ) );
		if( minutes.length() == 1 )
		{
			minutes = "0" + minutes;
		}
		timestamp = timestamp.substring( timestamp.indexOf( ':' ) + 1 );
		String seconds = timestamp.substring( 0, timestamp.indexOf( ' ' ) );
		if( seconds.length() == 1 )
		{
			seconds = "0" + seconds;
		}
		timestamp = timestamp.substring( timestamp.indexOf( ' ' ) + 1 );		
		String year = timestamp;
		if( year.length() == 2 )
		{
			if( Integer.parseInt( year ) < 30 )
			{
				year = "20" + year;
			}
			else
			{
				year = "19" + year;
			}
		}

		String value = year + month + date;// + hour + minutes + seconds;
		
		
		return Long.parseLong( value );
	}
}
