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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.wigis.graph.TopicVisualizationBean;
import net.wigis.graph.dnv.DNVNode;

// TODO: Auto-generated Javadoc
/**
 * The Class ReadFileIntoNodeContents.
 * 
 * @author Brynjar Gretarsson
 */
public class ReadFileIntoNodeContents extends Thread
{

	/** The max size. */
	private int maxSize = 50;

	/** The nodes. */
	private List<DNVNode> nodes = new ArrayList<DNVNode>();
	// private List<String> files = new ArrayList<String>();
	/** The tvb. */
	private TopicVisualizationBean tvb;

	/**
	 * Instantiates a new read file into node contents.
	 * 
	 * @param maxSize
	 *            the max size
	 * @param tvb
	 *            the tvb
	 */
	public ReadFileIntoNodeContents( int maxSize, TopicVisualizationBean tvb )
	{
		this.maxSize = maxSize;
		this.tvb = tvb;
	}

	/**
	 * Adds the.
	 * 
	 * @param node
	 *            the node
	 * @param file
	 *            the file
	 */
	public void add( DNVNode node, String file )
	{
		node.setProperty( "contentsFile", file );
		nodes.add( node );
		// files.add( file );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run()
	{
		if( nodes.size() > maxSize )
		{
			System.out.println( nodes.size() + " > " + maxSize );
			ReadFileIntoNodeContents rfinc1 = new ReadFileIntoNodeContents( maxSize, tvb );
			ReadFileIntoNodeContents rfinc2 = new ReadFileIntoNodeContents( maxSize, tvb );
			List<DNVNode> nodes1 = nodes.subList( 0, nodes.size() / 2 );
			List<DNVNode> nodes2 = nodes.subList( nodes.size() / 2, nodes.size() );
			// List<String> files1 = files.subList( 0, files.size() / 2 );
			// List<String> files2 = files.subList( files.size() / 2,
			// files.size() );
			// System.out.println( "1 gets " + nodes1.size() + " nodes and " +
			// files1.size() + " files." );
			// System.out.println( "2 gets " + nodes2.size() + " nodes and " +
			// files2.size() + " files." );
			// rfinc1.setNodesAndFiles( nodes1, files1 );
			// rfinc2.setNodesAndFiles( nodes2, files2 );
			rfinc1.setNodes( nodes1 );
			rfinc2.setNodes( nodes2 );
			rfinc1.start();
			rfinc2.start();
		}
		else
		{
			System.out.println( "Reading " + nodes.size() + " files." );
			for( int i = 0; i < nodes.size(); i++ )
			{
				read( nodes.get( i ), nodes.get( i ).getProperty( "contentsFile" ), tvb );
			}
			System.out.println( "Reading finished!" );
		}
	}

	/** The Constant headers. */
	private static final String headers[] = { "AWARDEE", "DOING_BUSINESS_AS_NAME", "PI_NAME", "COPI", "ESTIMATED_TOTAL_AWARD_AMOUNT",
			"AWARD_START_DATE", "AWARD_EXPIRATION_DATE", "TRANSACTION_TYPE", "AGENCY", "CFDA_NUMBER", "PRIMARY_PROGRAM_SOURCE",
			"AWARD_TITLE_OR_DESCRIPTION", "FEDERAL_AWARD_ID_NUMBER", "DUNS_ID", "PARENT_DUNS_ID", "PROGRAM_NAME", "PROGRAM_OFFICER_EMAIL",
			"PROGRAM_OFFICER_NAME", "AWARDEE_CITY", "AWARDEE_STATE", "AWARDEE_COUNTY", "AWARDEE_COUNTRY", "AWARDEE_CONG_DISTRICT",
			"PERFORMING_ORG_NAME", "PERFORMING_CITY", "PERFORMING_STATE", "PERFORMING_COUNTY", "PERFORMING_COUNTRY", "PERFORMING_CONG_DISTRICT",
			"ABSTRACT_AT_TIME_OF_AWARD" };

	/** The Constant extractableHeaders. */
	private static final String extractableHeaders[] = { "AWARDEE", "DOING_BUSINESS_AS_NAME", "PI_NAME", "COPI", "TRANSACTION_TYPE", "AGENCY",
			"CFDA_NUMBER", "PRIMARY_PROGRAM_SOURCE", "PROGRAM_NAME", "PROGRAM_OFFICER_EMAIL", "PROGRAM_OFFICER_NAME", "AWARDEE_CITY",
			"AWARDEE_STATE", "AWARDEE_COUNTY", "AWARDEE_COUNTRY", "AWARDEE_CONG_DISTRICT", "PERFORMING_ORG_NAME", "PERFORMING_CITY",
			"PERFORMING_STATE", "PERFORMING_COUNTY", "PERFORMING_COUNTRY", "PERFORMING_CONG_DISTRICT" };

	/** The is extractable. */
	private static Map<String, Boolean> isExtractable = new HashMap<String, Boolean>();

	/** The Constant authorHeader. */
	private static final String authorHeader = "PROGRAM_NAME";

	/**
	 * Read.
	 * 
	 * @param node
	 *            the node
	 * @param file
	 *            the file
	 * @param tvb
	 *            the tvb
	 */
	public static void read( DNVNode node, String file, TopicVisualizationBean tvb )
	{
		FileReader fr;
		String contents = "";
		StringBuilder contentBuffer = new StringBuilder( 5000 );
		node.setProperty( "reading", "true" );
		try
		{
			fr = new FileReader( file );
			BufferedReader br = new BufferedReader( fr );
			String line;

			while( ( line = br.readLine() ) != null )
			{
				line = StringUtils.replaceAll( line, "\"", "'" );

				if( line.startsWith( "<PMID>" ) )
				{
					node.setProperty( "PMID", line.substring( 6, line.indexOf( "</PMID>" ) ) );
				}
				else if( line.startsWith( "ZZYEAR" ) )
				{
					node.setProperty( "year", line.substring( 6 ).trim() );
				}
				else if( line.startsWith( "ZZTITLE" ) )
				{
					node.setProperty( "title", line.substring( 7 ) );
				}
				for( int i = 0; i < headers.length; i++ )
				{
					if( line.startsWith( "<" + headers[i] + ">" ) )
					{
						if( line.contains( "</" + headers[i] + ">" ) )
						{
							node.setProperty( headers[i], line.substring( headers[i].length() + 2, line.indexOf( "</" + headers[i] + ">" ) ) );
							if( isHeaderExtractable( headers[i] ) )
							{
								node.setPropertyExtractable( headers[i] );
							}
						}
						else
						{
							StringBuilder value = new StringBuilder();
							value.append( line.substring( headers[i].length() + 2 ) );
							contentBuffer.append( line ).append( "<br />" );
							while( ( line = br.readLine() ) != null && !line.contains( "</" + headers[i] + ">" ) )
							{
								value.append( line );
								contentBuffer.append( line ).append( "<br />" );
							}
							if( line != null )
							{
								value.append( line.substring( 0, line.indexOf( "</" + headers[i] + ">" ) ) );
							}
							node.setProperty( headers[i], value.toString() );
						}

						if( headers[i].equals( authorHeader ) )
						{
							node.setProperty( "Author", node.getProperty( headers[i] ) );
							if( tvb != null )
							{
								tvb.addAuthorDocumentMapping( node.getProperty( "Author" ), node );
							}
						}
					}
				}

				contentBuffer.append( line ).append( "<br />" );
			}

			String flag = "harvested_papers/";
			if( file.contains( flag ) )
			{
				String author = file.substring( file.indexOf( flag ) + flag.length(), file.lastIndexOf( "/" ) );
				node.setProperty( "Author", author );
				contentBuffer.append( "Author : " ).append( author ).append( "<br />" );
				if( tvb != null )
				{
					tvb.addAuthorDocumentMapping( author, node );
				}
			}
			
			flag = "KDD Year 1 Base Dataset 1/";
			if( file.contains( flag ) )
			{
				String author = file.substring( file.indexOf( flag ) + flag.length() );
				author = author.substring( 0, author.indexOf( "/" ) );
				node.setProperty( "Author", author );
				contentBuffer.append( "Author : " ).append( author ).append( "<br />" );
				if( tvb != null )
				{
					tvb.addAuthorDocumentMapping( author, node );
				}
			}

			flag = "KDD Year 1 Base Dataset 3/";
			if( file.contains( flag ) )
			{
				String author = file.substring( file.indexOf( flag ) + flag.length() );
				author = author.substring( 0, author.indexOf( "/" ) );
				node.setProperty( "Author", author );
				contentBuffer.append( "Author : " ).append( author ).append( "<br />" );
				if( tvb != null )
				{
					tvb.addAuthorDocumentMapping( author, node );
				}
			}

			flag = "_-_";
			if( file.contains( flag ) )
			{
				String author = file.substring( file.lastIndexOf( "/" ) + 1, file.indexOf( flag ) );
				node.setProperty( "Author", author );
				contentBuffer.append( "Artist : " ).append( author ).append( "<br />" );
				if( tvb != null )
				{
					tvb.addAuthorDocumentMapping( author, node );
				}
			}

			flag = "data/infovis";
			if( file.contains( flag ) )
			{
				String author = "infovis";
				node.setProperty( "Author", author );
				if( tvb != null )
				{
					tvb.addAuthorDocumentMapping( author, node );
				}
			}

			flag = "data/vis";
			if( file.contains( flag ) )
			{
				String author = "vis";
				node.setProperty( "Author", author );
				if( tvb != null )
				{
					tvb.addAuthorDocumentMapping( author, node );
				}
			}

			flag = "data/vast";
			if( file.contains( flag ) )
			{
				String author = "vast";
				node.setProperty( "Author", author );
				if( tvb != null )
				{
					tvb.addAuthorDocumentMapping( author, node );
				}
			}
			br.close();
			fr.close();
		}
		catch( IOException e )
		{
			// e.printStackTrace();
			contents = "Reading contents failed";
			node.setProperty( "Reading Failed", "true" );
		}

		contents = contentBuffer.toString();

		node.removeProperty( "reading" );
		node.setProperty( "Contents", contents );

	}

	/**
	 * Checks if is header extractable.
	 * 
	 * @param header
	 *            the header
	 * @return true, if is header extractable
	 */
	private static boolean isHeaderExtractable( String header )
	{
		Boolean answer = isExtractable.get( header );
		if( answer == null )
		{
			for( int i = 0; i < extractableHeaders.length; i++ )
			{
				if( extractableHeaders[i].equals( header ) )
				{
					answer = true;
					isExtractable.put( header, answer );
					return answer;
				}
			}

			answer = false;
			isExtractable.put( header, answer );
		}

		return answer;
	}

	/**
	 * Sets the nodes.
	 * 
	 * @param nodes
	 *            the new nodes
	 */
	public void setNodes( List<DNVNode> nodes )
	{
		this.nodes = nodes;
	}

	// public void setNodesAndFiles( List<DNVNode> nodes, List<String> files )
	// {
	// if( nodes.size() != files.size() )
	// {
	// throw new IllegalArgumentException( "Lists must have same size." );
	// }
	//		
	// this.nodes = nodes;
	// this.files = files;
	// }
}
