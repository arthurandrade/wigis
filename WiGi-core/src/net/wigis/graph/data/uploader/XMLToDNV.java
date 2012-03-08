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

import java.util.HashMap;
import java.util.Map;

import net.wigis.graph.data.citeseer.Logger;
import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.utilities.Vector3D;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;

// TODO: Auto-generated Javadoc
/**
 * The Class XMLToDNV.
 * 
 * @author johno
 */
public class XMLToDNV
{

	/**
	 * Logger.
	 * 
	 * @param filetoparse
	 *            the filetoparse
	 * @return the dNV graph
	 */
	// // private static Log logger = LogFactory.getLog( XMLToDNV.class );

	/**
	 * @param args
	 */
	public static DNVGraph read( String filetoparse )
	{

		final int EDGE_ID_OFFSET = 1000000;

		Map<String, DNVNode> nodeIdMap = new HashMap<String, DNVNode>();
		// create a DNV graph to store the citeseer data

		DNVGraph g = new DNVGraph();

		// TODO Auto-generated method stub
		try
		{
			DOMParser parser = new DOMParser();
			parser.parse( filetoparse );
			Document doc = parser.getDocument();

			NodeList nodes = doc.getElementsByTagName( "object" );
			System.out.println( "There are " + nodes.getLength() + "  nodes." );
			NodeList nl = doc.getElementsByTagName( "*" );
			Node n;
			String fromid = "";
			String toid = "";
			int edgeID = EDGE_ID_OFFSET;
			String uuid = "";
			DNVNode dnvnode = null;
			for( int i = 0; i < nl.getLength(); i++ )
			{
				n = nl.item( i );

				if( n.getNodeName().equals( "uuid" ) )
				{
					dnvnode = new DNVNode( g );
					uuid = clean( n.getTextContent() );
					g.addNode( 0, dnvnode );
					nodeIdMap.put( uuid, dnvnode );
				}
				// transformation case
				else if( n.getNodeName().equals( "name" ) )
				{
					System.out.println( n.getNodeName() + " " );
					dnvnode.setLabel( "TRANS: " + clean( n.getTextContent() ) );
					dnvnode.setRadius( 2 );
					dnvnode.setPosition( (float)Math.random(), (float)Math.random() );
					dnvnode.setColor( new Vector3D( 0.1f, 0.5f, 0.9f ) );
					System.out.println( n.getTextContent() + " " );
				}
				// file case
				else if( n.getNodeName().equals( "localId" ) )
				{
					System.out.println( n.getNodeName() + " " );
					dnvnode.setLabel( "FILE: " + clean( n.getTextContent() ) );
					dnvnode.setRadius( 3 );
					dnvnode.setPosition( (float)Math.random(), (float)Math.random() );
					dnvnode.setColor( new Vector3D( 0.0f, 0.9f, 0.1f ) );
					System.out.println( n.getTextContent() + " " );
				}

				else if( n.getNodeName().equals( "from" ) )
				{
					fromid = clean( n.getTextContent() );
				}
				else if( n.getNodeName().equals( "to" ) )
				{
					toid = clean( n.getTextContent() );
					Logger.write( "Fromid is: " + fromid );
					Logger.write( "toid is: " + toid );
					DNVEdge dnvedge = new DNVEdge( 0, 1.0f, false, ( nodeIdMap.get( fromid ) ), nodeIdMap.get( toid ), g );
					dnvedge.setId( edgeID );
					edgeID++;
					g.addNode( 0, dnvedge );
				}

			}

		}
		catch( Exception e )
		{
			e.printStackTrace();
		}

		return g;

	}

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main( String[] args )
	{
		// XMLToDNV xmldnv = new XMLToDNV();
		XMLToDNV.read( "C:\\Users\\jod\\Desktop\\frew\\SimpleWorkflow.xml" );

	}

	/**
	 * Clean.
	 * 
	 * @param s
	 *            the s
	 * @return the string
	 */
	public static String clean( String s )
	{

		s = s.trim();
		s = s.replaceAll( "\t", "" );
		s = s.replaceAll( "\n", "" );
		s = s.replaceAll( "\r", "" );

		return s;
	}
}
