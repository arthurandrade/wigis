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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.faces.context.FacesContext;

import net.wigis.graph.TopicVisualizationBean;
import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVEntity;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.layout.DocumentTopicsCircularLayout;
import net.wigis.graph.dnv.layout.RandomLayout;
import net.wigis.graph.dnv.utilities.DocumentNodeExpand;
import net.wigis.graph.dnv.utilities.GraphFunctions;
import net.wigis.graph.dnv.utilities.SizeOfContentSort;
import net.wigis.graph.dnv.utilities.SortByFloatProperty;
import net.wigis.graph.dnv.utilities.Timer;
import net.wigis.graph.dnv.utilities.Vector3D;
import net.wigis.web.ContextLookup;

// TODO: Auto-generated Javadoc
/**
 * The Class CreateDNVFromDocumentTopics.
 * 
 * @author Brynjar Gretarsson
 */
public class CreateDNVFromDocumentTopics
{

	/**
	 * Creates the.
	 * 
	 * @param directory
	 *            the directory
	 * @param documentsFile
	 *            the documents file
	 * @param topicsFile
	 *            the topics file
	 * @param outputGraphName
	 *            the output graph name
	 * @param threshold
	 *            the threshold
	 * @param oneTopicPerDocument
	 *            the one topic per document
	 * @param runLayout
	 *            the run layout
	 * @param topicNodeColor
	 *            the topic node color
	 * @param topicEdgeColor
	 *            the topic edge color
	 * @param documentStartColor
	 *            the document start color
	 * @param documentEndColor
	 *            the document end color
	 * @param maxWordsPerTopic
	 *            the max words per topic
	 * @param forceDocumentLabels
	 *            the force document labels
	 * @param maxThreshold
	 *            the max threshold
	 * @param createDocumentEdges
	 *            the create document edges
	 * @param docTopicCount
	 *            the doc topic count
	 * @param docTotalCount
	 *            the doc total count
	 * @param topicTotalCount
	 *            the topic total count
	 * @param timelineVisualization
	 *            the timeline visualization
	 * @param numberOfDocuments
	 *            the number of documents
	 * @param labelScaler
	 *            the label scaler
	 * @param excludeEmptyDocuments
	 *            the exclude empty documents
	 * @param dissimilarityMatrix
	 *            the dissimilarity matrix
	 * @param authorToColor
	 *            the author to color
	 * @param colorTopicsBasedOnDocuments
	 *            the color topics based on documents
	 * @param colorTopicEdgesBasedOnDocuments
	 *            the color topic edges based on documents
	 * @return the dNV graph
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static DNVGraph create( String directory, String documentsFile, String topicsFile, String outputGraphName, float threshold,
			boolean oneTopicPerDocument, boolean runLayout, String topicNodeColor, String topicEdgeColor, String documentStartColor,
			String documentEndColor, int maxWordsPerTopic, boolean forceDocumentLabels, float maxThreshold, boolean createDocumentEdges,
			Map<String, Float> docTopicCount, Map<Integer, Float> docTotalCount, Map<Integer, Float> topicTotalCount, boolean timelineVisualization,
			float numberOfDocuments, float labelScaler, boolean excludeEmptyDocuments, double[][] dissimilarityMatrix,
			Map<String, String> authorToColor, boolean colorTopicsBasedOnDocuments, boolean colorTopicEdgesBasedOnDocuments ) throws IOException
	{
		DNVGraph graph = create( directory, documentsFile, topicsFile, threshold, oneTopicPerDocument, runLayout, topicNodeColor, topicEdgeColor,
				documentStartColor, documentEndColor, maxWordsPerTopic, forceDocumentLabels, maxThreshold, createDocumentEdges, docTopicCount,
				docTotalCount, topicTotalCount, timelineVisualization, numberOfDocuments, labelScaler, excludeEmptyDocuments, dissimilarityMatrix,
				authorToColor, colorTopicsBasedOnDocuments, colorTopicEdgesBasedOnDocuments, true, false );

		if( !outputGraphName.endsWith( ".dnv" ) )
		{
			outputGraphName += ".dnv";
		}

		if( !directory.endsWith( "/" ) && !directory.endsWith( "\\" ) )
		{
			directory += "/";
		}

		graph.writeGraph( directory + outputGraphName );

		return graph;
	}

	/**
	 * Creates the.
	 * 
	 * @param directory
	 *            the directory
	 * @param documentsFile
	 *            the documents file
	 * @param topicsFile
	 *            the topics file
	 * @param threshold
	 *            the threshold
	 * @param oneTopicPerDocument
	 *            the one topic per document
	 * @param runLayout
	 *            the run layout
	 * @param topicNodeColor
	 *            the topic node color
	 * @param topicEdgeColor
	 *            the topic edge color
	 * @param documentStartColor
	 *            the document start color
	 * @param documentEndColor
	 *            the document end color
	 * @param maxWordsPerTopic
	 *            the max words per topic
	 * @param forceDocumentLabels
	 *            the force document labels
	 * @param maxThreshold
	 *            the max threshold
	 * @param createDocumentEdges
	 *            the create document edges
	 * @param docTopicCount
	 *            the doc topic count
	 * @param docTotalCount
	 *            the doc total count
	 * @param topicTotalCount
	 *            the topic total count
	 * @param timelineVisualization
	 *            the timeline visualization
	 * @param numberOfDocuments
	 *            the number of documents
	 * @param labelScaler
	 *            the label scaler
	 * @param excludeEmptyDocuments
	 *            the exclude empty documents
	 * @param dissimilarityMatrix
	 *            the dissimilarity matrix
	 * @param authorToColor
	 *            the author to color
	 * @param colorTopicsBasedOnDocuments
	 *            the color topics based on documents
	 * @param colorTopicEdgesBasedOnDocuments
	 *            the color topic edges based on documents
	 * @param hideLabelBackgroundForDocuments
	 *            the hide label background for documents
	 * @param useCurvedLabelsForDocuments
	 *            the use curved labels for documents
	 * @return the dNV graph
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static DNVGraph create( String directory, String documentsFile, String topicsFile, float threshold, boolean oneTopicPerDocument,
			boolean runLayout, String topicNodeColor, String topicEdgeColor, String documentStartColor, String documentEndColor,
			int maxWordsPerTopic, boolean forceDocumentLabels, float maxThreshold, boolean createDocumentEdges, Map<String, Float> docTopicCount,
			Map<Integer, Float> docTotalCount, Map<Integer, Float> topicTotalCount, boolean timelineVisualization, float numberOfDocuments,
			float labelScaler, boolean excludeEmptyDocuments, double[][] dissimilarityMatrix, Map<String, String> authorToColor,
			boolean colorTopicsBasedOnDocuments, boolean colorTopicEdgesBasedOnDocuments, boolean hideLabelBackgroundForDocuments,
			boolean useCurvedLabelsForDocuments ) throws FileNotFoundException, IOException
	{
		DNVGraph graph = new DNVGraph();

		if( !directory.endsWith( "/" ) && !directory.endsWith( "\\" ) )
		{
			directory += "/";
		}

		// float numberOfDocuments = getNumberOfDocuments( directory,
		// documentsFile );
		Timer timer = new Timer( Timer.MILLISECONDS );
		timer.setStart();
		generateAllDocumentNodes( directory, documentsFile, graph, documentStartColor, documentEndColor, forceDocumentLabels, createDocumentEdges,
				numberOfDocuments, "doc", null, timelineVisualization, hideLabelBackgroundForDocuments, useCurvedLabelsForDocuments );
		timer.setEnd();
		System.out.println( "Generating document nodes took " + timer.getLastSegment( Timer.SECONDS ) + " seconds." );
		timer.setStart();
		generateAllTopicNodes( directory, topicsFile, graph, topicNodeColor, maxWordsPerTopic );
		timer.setEnd();
		System.out.println( "Generating topic nodes took " + timer.getLastSegment( Timer.SECONDS ) + " seconds." );

		// generateDissimilarityEdges( graph, dissimilarityMatrix );

		timer.setStart();
		if( oneTopicPerDocument )
		{
			generateOneTopicEdgePerDocument( graph, topicEdgeColor, maxThreshold, docTopicCount );
		}
		else
		{
			generateTopicEdgesBasedOnThreshold( graph, threshold, topicEdgeColor, "doc", docTopicCount, docTotalCount, topicTotalCount );
		}
		timer.setEnd();
		System.out.println( "Generating edges took " + timer.getLastSegment( Timer.SECONDS ) + " seconds." );

		ScaleDocumentsThread sdt = new ScaleDocumentsThread( graph, labelScaler );
		sdt.start();

		timer.setStart();
		scaleTopicNodes( graph, labelScaler );
		timer.setEnd();
		System.out.println( "Scaling topic nodes took " + timer.getLastSegment( Timer.SECONDS ) + " seconds." );

		TopicVisualizationBean tvb = (TopicVisualizationBean)ContextLookup.lookup( "topicVisualizationBean", FacesContext.getCurrentInstance() );
		if( excludeEmptyDocuments )
		{
			synchronized( graph )
			{
				List<DNVEntity> nodes = new ArrayList<DNVEntity>( graph.getNodesByType( 0, "document" ).values() );
				for( DNVEntity node : nodes )
				{
					if( node.getProperty( "Reading Failed" ) != null )
					{
						graph.removeNode( node );
						if( tvb != null )
						{
							tvb.removeAuthorDocumentMapping( node.getProperty( "Author" ), (DNVNode)node );
						}
					}
				}
			}
		}
		List<DNVNode> removedNodes = graph.removeIsolatedNodes();
		for( DNVNode node : removedNodes )
		{
			if( node.getType() != null && node.getType().equals( "document" ) )
			{
				String author = node.getProperty( "Author" );
				if( author != null && tvb != null )
				{
					tvb.removeAuthorDocumentMapping( author, node );
				}
			}
		}

		if( colorTopicsBasedOnDocuments )
		{
			updateColors( graph, authorToColor, colorTopicEdgesBasedOnDocuments );
		}
		// graph.removeIsolatedNodesByType( 0, "topic" );

		timer.setStart();
		RandomLayout.runLayout( graph, 0, 100 );
		timer.setEnd();
		System.out.println( "Randomizing node positions took " + timer.getLastSegment( Timer.SECONDS ) + " seconds." );

		if( runLayout )
		{
			timer.setStart();
			// Double width = MDSTopicsLayout.getWidth(
			// tvb.getDissimilarityMatrix() );
			DocumentTopicsCircularLayout.runLayout( graph, 0, 0, 0.1f, "doc", null, 1, timelineVisualization,
					( timelineVisualization || createDocumentEdges ), false, dissimilarityMatrix );
			timer.setEnd();
			// System.out.println( "Laying out the graph took " +
			// timer.getLastSegment( Timer.SECONDS ) + " seconds." );
		}

		System.out.println( "Entire generation of Document-Topics DNV file took " + timer.getTotalTime( Timer.SECONDS ) + " seconds." );

		return graph;
	}

	/**
	 * Update colors.
	 * 
	 * @param graph
	 *            the graph
	 * @param authorToColor
	 *            the author to color
	 * @param colorEdges
	 *            the color edges
	 */
	public static void updateColors( DNVGraph graph, Map<String, String> authorToColor, boolean colorEdges )
	{
		ensureAllDocumentContentsAreRead( graph );
		System.out.println( "Updating colors " );
		for( DNVEntity docNode : graph.getNodesByType( 0, "document" ).values() )
		{
			String author = docNode.getProperty( "Author" );
			// System.out.println( "Author for " + docNode.getLabel() + " is " +
			// author );
			if( author != null && authorToColor.get( author ) != null )
			{
				Vector3D color = GraphFunctions.convertColor( authorToColor.get( author ) );
				// System.out.println( "Setting " + author + " as " + color );
				if( color != null )
				{
					docNode.setColor( color );
					docNode.setLabelColor( color );
				}
			}
		}

		for( DNVEntity entity : graph.getNodesByType( 0, "topic" ).values() )
		{
			DNVNode topicNode = (DNVNode)entity;
			Vector3D color = new Vector3D( 0, 0, 0 );
			float count = 0;
			Vector3D tempColor = new Vector3D();
			for( DNVNode docNode : topicNode.getNeighborMap().values() )
			{
				count++;
				color.add( docNode.getColor() );
				if( colorEdges )
				{
					DNVEdge edge = (DNVEdge)graph.getNodeByBbId( docNode.getBbId() + "->" + topicNode.getBbId() );
					if( edge == null )
					{
						edge = (DNVEdge)graph.getNodeByBbId( topicNode.getBbId() + "->" + docNode.getBbId() );
					}
					if( edge != null )
					{
						tempColor.set( docNode.getColor() );
						tempColor.setX( Math.min( 0.8f, tempColor.getX() + 0.3f ) );
						tempColor.setY( Math.min( 0.8f, tempColor.getY() + 0.3f ) );
						tempColor.setZ( Math.min( 0.8f, tempColor.getZ() + 0.3f ) );
						edge.setColor( tempColor );
						edge.setLabelColor( tempColor );
					}
				}
			}
			color.dotProduct( 1.0f / count );
			topicNode.setColor( color );
			topicNode.setLabelColor( color );
		}
	}

	/**
	 * Generate dissimilarity edges.
	 * 
	 * @param graph
	 *            the graph
	 * @param dissimilarityMatrix
	 *            the dissimilarity matrix
	 * @param k
	 *            the k
	 */
	public static void generateDissimilarityEdges( DNVGraph graph, double[][] dissimilarityMatrix, float k )
	{
		if( dissimilarityMatrix != null )
		{
			for( int i = 0; i < dissimilarityMatrix.length; i++ )
			{
				for( int j = 0; j < i; j++ )
				{
					DNVEdge edge = new DNVEdge( graph );
					DNVNode from = (DNVNode)graph.getNodeByBbId( "t" + ( i + 1 ) );
					DNVNode to = (DNVNode)graph.getNodeByBbId( "t" + ( j + 1 ) );
					if( from != null && to != null )
					{
						edge.setFrom( from );
						edge.setTo( to );
						edge.setRestingDistance( (float)dissimilarityMatrix[i][j] );
						edge.setK( k );
						edge.setVisible( false );
						edge.setType( "topicDissimilarityEdge" );
						graph.addNode( 0, edge );
					}
				}
			}
		}
	}

	/**
	 * Removes the dissimilarity edges.
	 * 
	 * @param graph
	 *            the graph
	 */
	public static void removeDissimilarityEdges( DNVGraph graph )
	{
		List<DNVEntity> edges = new ArrayList<DNVEntity>( graph.getNodesByType( 0, "topicDissimilarityEdge" ).values() );
		for( DNVEntity edge : edges )
		{
			graph.removeNode( edge );
		}
	}

	/**
	 * Gets the number of documents.
	 * 
	 * @param directory
	 *            the directory
	 * @param dtf
	 *            the dtf
	 * @return the number of documents
	 */
	public static float getNumberOfDocuments( String directory, String dtf )
	{
		try
		{
			FileReader fr;
			fr = new FileReader( directory + dtf );
			BufferedReader br = new BufferedReader( fr );
			String line = br.readLine();
			br.close();
			fr.close();
			return Float.parseFloat( line );
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * Gets the number of topics.
	 * 
	 * @param directory
	 *            the directory
	 * @param dtf
	 *            the dtf
	 * @return the number of topics
	 */
	public static float getNumberOfTopics( String directory, String dtf )
	{
		try
		{
			FileReader fr;
			fr = new FileReader( directory + dtf );
			BufferedReader br = new BufferedReader( fr );
			String line = br.readLine();
			line = br.readLine();
			br.close();
			fr.close();
			return Float.parseFloat( line );
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		return 0;
	}

	/** The Constant MIN_LABEL_SIZE. */
	public static final int MIN_LABEL_SIZE = 10;

	// private static final int LABEL_SCALER = 6;

	/**
	 * Scale document nodes.
	 * 
	 * @param graph
	 *            the graph
	 * @param labelScaler
	 *            the label scaler
	 */
	public static void scaleDocumentNodes( DNVGraph graph, float labelScaler )
	{
		SizeOfContentSort socs = new SizeOfContentSort();
		List<DNVNode> documentNodesList = ensureAllDocumentContentsAreRead( graph );

		Collections.sort( documentNodesList, socs );

		if( documentNodesList.size() > 0 )
		{
			float maxSize = documentNodesList.get( 0 ).getProperty( "Contents" ).length();
			if( maxSize == 0 )
			{
				maxSize = 1;
			}

			Timer timer = new Timer( Timer.MILLISECONDS );
			timer.setStart();
			for( DNVNode node : documentNodesList )
			{
				float size = 1 + node.getProperty( "Contents" ).length() * 2 / maxSize;
				node.setRadius( size );
				node.setProperty( "Size", "" + size );
				node.setLabelSize( Math.round( MIN_LABEL_SIZE + node.getRadius() * labelScaler ) );
			}
			timer.setEnd();
			System.out.println( "scaleDocumentNodes second loop took " + timer.getLastSegment( Timer.SECONDS ) + " seconds." );
		}
	}

	/**
	 * Scale topic nodes.
	 * 
	 * @param graph
	 *            the graph
	 * @param labelScaler
	 *            the label scaler
	 */
	public static void scaleTopicNodes( DNVGraph graph, float labelScaler )
	{
		Map<Integer, DNVEntity> topicNodes = graph.getNodesByType( 0, "topic" );
		List<DNVNode> topicNodesList = new ArrayList<DNVNode>();
		for( DNVEntity entity : topicNodes.values() )
		{
			topicNodesList.add( (DNVNode)entity );
		}
		float weight = 0;

		SortByFloatProperty sbfp = new SortByFloatProperty( "weight", true );
		Collections.sort( topicNodesList, sbfp );

		if( topicNodesList.size() > 0 )
		{
			float maxWeight = Float.parseFloat( topicNodesList.get( 0 ).getProperty( "weight" ) );
			for( DNVNode node : topicNodesList )
			{
				weight = Float.parseFloat( node.getProperty( "weight" ) );
				weight = weight / maxWeight;
				node.setRadius( weight * 3 );
				node.setLabelSize( Math.round( MIN_LABEL_SIZE + node.getRadius() * labelScaler ) );
			}
		}
	}

	// private static int maxDocumentsPerThread = 10;
	// private static int maxDocumentsBeforeStartingThread = 10;
	/**
	 * Generate all document nodes.
	 * 
	 * @param directory
	 *            the directory
	 * @param documentsFile
	 *            the documents file
	 * @param graph
	 *            the graph
	 * @param startColorStr
	 *            the start color str
	 * @param endColorStr
	 *            the end color str
	 * @param forceLabels
	 *            the force labels
	 * @param createDocumentEdges
	 *            the create document edges
	 * @param numberOfDocuments
	 *            the number of documents
	 * @param docIdPrefix
	 *            the doc id prefix
	 * @param parentDoc
	 *            the parent doc
	 * @param timelineVisualization
	 *            the timeline visualization
	 * @param hideLabelBackgroundForDocuments
	 *            the hide label background for documents
	 * @param useCurvedLabelsForDocuments
	 *            the use curved labels for documents
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void generateAllDocumentNodes( String directory, String documentsFile, DNVGraph graph, String startColorStr, String endColorStr,
			boolean forceLabels, boolean createDocumentEdges, float numberOfDocuments, String docIdPrefix, DNVNode parentDoc,
			boolean timelineVisualization, boolean hideLabelBackgroundForDocuments, boolean useCurvedLabelsForDocuments )
			throws FileNotFoundException, IOException
	{
		// float numberOfDocuments = countLines( directory + documentsFile );

		if( !directory.endsWith( "\\" ) && !directory.endsWith( "/" ) )
		{
			directory += "/";
		}

		FileReader fr = new FileReader( directory + documentsFile );
		BufferedReader br = new BufferedReader( fr );
		String line;

		DNVNode tempNode = null;
		DNVNode lastNode = null;
		DNVEdge tempEdge;

		int id = 1;
		Vector3D currentColor = GraphFunctions.convertColor( startColorStr );
		Vector3D endColor = GraphFunctions.convertColor( endColorStr );

		float colorIncrementRed = ( endColor.getX() - currentColor.getX() ) / numberOfDocuments;
		float colorIncrementGreen = ( endColor.getY() - currentColor.getY() ) / numberOfDocuments;
		float colorIncrementBlue = ( endColor.getZ() - currentColor.getZ() ) / numberOfDocuments;
		Vector3D colorIncrement = new Vector3D( colorIncrementRed, colorIncrementGreen, colorIncrementBlue );
		TopicVisualizationBean tvb = (TopicVisualizationBean)ContextLookup.lookup( "topicVisualizationBean", FacesContext.getCurrentInstance() );
		ReadFileIntoNodeContents rfinc = new ReadFileIntoNodeContents( (int)numberOfDocuments, tvb );
		int counter = 0;
		int maxDocumentsBeforeStartingThread = (int)( numberOfDocuments );
		while( ( line = br.readLine() ) != null )
		{
			lastNode = tempNode;
			tempNode = new DNVNode( graph );
			tempNode.setHideLabelBackground( hideLabelBackgroundForDocuments );
			tempNode.setCurvedLabel( useCurvedLabelsForDocuments );
			tempNode.setIcon( "/topic/icons/document.png" );
			String label = line;
			if( label.endsWith( ".txt" ) )
			{
				label = label.substring( 0, label.lastIndexOf( ".txt" ) );
			}
			if( label.contains( "/" ) )
			{
				label = label.substring( label.lastIndexOf( "/" ) + 1 );
			}
			if( label.contains( "\\" ) )
			{
				label = label.substring( label.lastIndexOf( "\\" ) + 1 );
			}
			if( label.endsWith( "_meta" ) )
			{
				label = label.substring( 0, label.length() - 5 );
			}
			tempNode.setLabel( label );
			File checker = new File( directory + "split" + "/" + label + "/" + "Ndt.txt" );
			if( checker.exists() )
			{
				tempNode.setExpandable( true );
			}
			checker = new File( line );
			if( !checker.exists() && !line.startsWith( directory ) )
			{
				line = directory + line;
			}
			rfinc.add( tempNode, line );
			counter++;
			if( counter == maxDocumentsBeforeStartingThread )
			{
				rfinc.start();
				rfinc = new ReadFileIntoNodeContents( (int)numberOfDocuments, tvb );
				counter = 0;
			}
			// ReadFileIntoNodeContents.read( tempNode, line,
			// timelineVisualization );

			tempNode.setBbId( docIdPrefix + id );
			tempNode.setProperty( "index", "" + id );
			tempNode.setType( "document" );
			tempNode.setColor( currentColor );
			tempNode.setAlpha( 0.8f );
			tempNode.setLabelColor( currentColor );
			tempNode.setRadius( 1 );
			tempNode.setForceLabel( forceLabels );
			graph.addNode( 0, tempNode );

			if( parentDoc != null )
			{
				tempNode.setPosition( parentDoc.getPosition() );
				if( id == 1 )
				{
					tempEdge = new DNVEdge( graph );
					tempEdge.setFrom( parentDoc );
					tempEdge.setTo( tempNode );
					tempEdge.setDirectional( true );
					tempEdge.setRestingDistance( 0 );
					tempEdge.setK( 1 );
					tempEdge.setColor( parentDoc.getColor() );
					tempEdge.setLabelColor( parentDoc.getColor() );
					tempEdge.setBbId( parentDoc.getBbId() + "->" + tempNode.getBbId() );
					tempEdge.setThickness( 3.0f );
					graph.addNode( 0, tempEdge );
				}
			}
			id++;

			if( createDocumentEdges && !timelineVisualization && lastNode != null )
			{
				tempEdge = new DNVEdge( graph );
				tempEdge.setFrom( lastNode );
				tempEdge.setTo( tempNode );
				tempEdge.setDirectional( true );
				tempEdge.setRestingDistance( 0 );
				tempEdge.setK( 1 );
				tempEdge.setColor( currentColor );
				tempEdge.setLabelColor( currentColor );
				tempEdge.setBbId( lastNode.getBbId() + "->" + tempNode.getBbId() );
				tempEdge.setThickness( 3.0f );
				graph.addNode( 0, tempEdge );
			}

			currentColor.add( colorIncrement );
		}
		if( counter > 0 )
		{
			rfinc.start();
		}

		if( parentDoc != null )
		{
			tempEdge = new DNVEdge( graph );
			tempEdge.setFrom( tempNode );
			tempEdge.setTo( parentDoc );
			tempEdge.setDirectional( true );
			tempEdge.setRestingDistance( 0 );
			tempEdge.setK( 1 );
			tempEdge.setColor( parentDoc.getColor() );
			tempEdge.setLabelColor( parentDoc.getColor() );
			tempEdge.setBbId( tempNode.getBbId() + "->" + parentDoc.getBbId() );
			tempEdge.setType( "documentEdge" );
			tempEdge.setThickness( 3.0f );
			graph.addNode( 0, tempEdge );
		}

		br.close();
		fr.close();

		updateTimelineVisualization( graph, startColorStr, endColorStr, timelineVisualization );
	}

	/**
	 * Update timeline visualization.
	 * 
	 * @param graph
	 *            the graph
	 * @param startColorStr
	 *            the start color str
	 * @param endColorStr
	 *            the end color str
	 * @param timelineVisualization
	 *            the timeline visualization
	 */
	public static void updateTimelineVisualization( DNVGraph graph, String startColorStr, String endColorStr, boolean timelineVisualization )
	{
		DNVNode lastNode;
		Vector3D currentColor;
		Vector3D endColor;
		if( timelineVisualization )
		{
			List<DNVNode> docNodes = ensureAllDocumentContentsAreRead( graph );
			SortByFloatProperty sbfp = new SortByFloatProperty( "year", false );
			Collections.sort( docNodes, sbfp );
			lastNode = null;
			int i = 1;
			currentColor = GraphFunctions.convertColor( startColorStr );
			endColor = GraphFunctions.convertColor( endColorStr );
			Vector3D colorUsed = new Vector3D( currentColor );
			float numberOfDocuments = graph.getNodesByType( 0, "document" ).size();
			float colorIncrementRed = ( endColor.getX() - currentColor.getX() ) / numberOfDocuments;
			float colorIncrementGreen = ( endColor.getY() - currentColor.getY() ) / numberOfDocuments;
			float colorIncrementBlue = ( endColor.getZ() - currentColor.getZ() ) / numberOfDocuments;
			Vector3D colorIncrement = new Vector3D( colorIncrementRed, colorIncrementGreen, colorIncrementBlue );

			for( DNVNode node : docNodes )
			{
				if( lastNode != null && lastNode.getProperty( "year" ) != null && lastNode.getProperty( "year" ).equals( node.getProperty( "year" ) ) )
				{
					colorUsed = lastNode.getColor();
				}
				else
				{
					colorUsed = currentColor;
					node.setForceLabel( true );
				}

				node.setColor( colorUsed );
				node.setLabelColor( colorUsed );

				currentColor.add( colorIncrement );
				node.setProperty( "index", "" + i );
				lastNode = node;
				i++;
			}
		}
	}

	/**
	 * Ensure all document contents are read.
	 * 
	 * @param graph
	 *            the graph
	 * @return the list
	 */
	public static List<DNVNode> ensureAllDocumentContentsAreRead( DNVGraph graph )
	{
		System.out.println( "START - ensureAllDocumentContentsAreRead" );
		Map<Integer, DNVEntity> documentNodes = graph.getNodesByType( 0, "document" );
		List<DNVNode> documentNodesList = new ArrayList<DNVNode>();
		synchronized( graph )
		{
			for( DNVEntity node : documentNodes.values() )
			{
				while( !node.hasProperty( "Contents" ) )
				{
					// loop until contents have been written for every node
				}

				documentNodesList.add( (DNVNode)node );
			}
		}

		System.out.println( "END   - ensureAllDocumentContentsAreRead" );

		return documentNodesList;
	}

	/*
	 * private static float countLines( String filename ) throws IOException {
	 * FileReader fr = new FileReader( filename ); BufferedReader br = new
	 * BufferedReader( fr );
	 * 
	 * int count = 0; while( br.readLine() != null ) { count++; }
	 * 
	 * return count; }
	 */
	/** The Constant LABELS_FILE. */
	private static final String LABELS_FILE = "labels.txt";

	/**
	 * Generate all topic nodes.
	 * 
	 * @param directory
	 *            the directory
	 * @param topicsFile
	 *            the topics file
	 * @param graph
	 *            the graph
	 * @param topicNodeColor
	 *            the topic node color
	 * @param maxWords
	 *            the max words
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private static void generateAllTopicNodes( String directory, String topicsFile, DNVGraph graph, String topicNodeColor, int maxWords )
			throws FileNotFoundException, IOException
	{
		File labelsFile = new File( directory, LABELS_FILE );
		Map<Integer, String> topicIdToLabel = new HashMap<Integer, String>();
		if( labelsFile.exists() )
		{
			FileReader fr = new FileReader( labelsFile );
			BufferedReader br = new BufferedReader( fr );
			String line;
			int id;
			String label;
			while( ( line = br.readLine() ) != null )
			{
				id = Integer.parseInt( line.substring( 0, line.indexOf( "," ) ) );
				label = line.substring( line.indexOf( "\"" ) + 1, line.lastIndexOf( "\"" ) );
				topicIdToLabel.put( id, label );
			}
		}

		FileReader fr;
		BufferedReader br;
		String line;
		DNVNode tempNode;

		fr = new FileReader( directory + topicsFile );
		br = new BufferedReader( fr );

		String topicId;
		String topicLabel;
		int id;
		Map<String, DNVNode> labelToNode = new HashMap<String, DNVNode>();
		while( ( line = br.readLine() ) != null )
		{
			tempNode = new DNVNode( graph );
			tempNode.setIcon( "/topic/icons/topic.png" );
			topicId = line.substring( line.indexOf( "[" ) + 1, line.indexOf( "]" ) );
			id = Integer.parseInt( topicId.substring( 1 ) );
			topicLabel = line.substring( line.indexOf( "]" ) + 1 );
			if( topicLabel.trim().startsWith( "(" ) )
			{
				topicLabel = topicLabel.substring( topicLabel.indexOf( ")" ) + 1 );
			}
			if( topicLabel.trim().startsWith( "[" ) )
			{
				topicIdToLabel.put( id, topicLabel.substring( topicLabel.indexOf( "[" ) + 1, topicLabel.indexOf( "]" ) ) );
				topicLabel = topicLabel.substring( topicLabel.indexOf( "]" ) + 1 );
			}
			tempNode.setProperty( "Full Label", topicLabel );
			if( topicIdToLabel.get( id ) != null )
			{
				// Use the manual label from the labels file
				topicLabel = topicIdToLabel.get( id );
				tempNode.setProperty( "manualLabel", "true" );
				tempNode.setForceLabel( true );
			}
			else
			{
				// Only include the first maxWords words of the topic label
				topicLabel = getLabel( maxWords, topicLabel );
				if( maxWords <= 3 )
				{
					tempNode.setForceLabel( true );
				}
				else
				{
					tempNode.setForceLabel( false );
				}
			}

			// if( labelToNode.get( topicLabel ) != null )
			// {
			// tempNode = labelToNode.get( topicLabel );
			// }
			labelToNode.put( topicLabel, tempNode );
			if( topicLabel != null && !topicLabel.equals( "" ) )
			{
				tempNode.setLabel( "T - " + topicLabel );
			}
			else
			{
				tempNode.setLabel( "" );
			}
			tempNode.setBbId( topicId );
			tempNode.setType( "topic" );
			tempNode.setColor( topicNodeColor );
			tempNode.setAlpha( 0.7f );
			tempNode.setLabelColor( topicNodeColor );
			tempNode.setRadius( 2 );
			graph.addNode( 0, tempNode );
		}

		br.close();
		fr.close();
	}

	/**
	 * Update label.
	 * 
	 * @param maxWords
	 *            the max words
	 * @param node
	 *            the node
	 * @param fullLabel
	 *            the full label
	 */
	public static void updateLabel( int maxWords, DNVNode node, String fullLabel )
	{
		fullLabel = getLabel( maxWords, fullLabel );
		if( maxWords <= 3 )
		{
			node.setForceLabel( true );
		}
		else
		{
			node.setForceLabel( false );
		}
		node.setLabel( fullLabel );

	}

	/**
	 * Gets the label.
	 * 
	 * @param maxWords
	 *            the max words
	 * @param fullLabel
	 *            the full label
	 * @return the label
	 */
	public static String getLabel( int maxWords, String fullLabel )
	{
		StringTokenizer toki;
		int i;
		toki = new StringTokenizer( fullLabel );
		if( toki.hasMoreTokens() )
			fullLabel = toki.nextToken();
		i = 1;
		while( toki.hasMoreTokens() && i < maxWords )
		{
			fullLabel += " " + toki.nextToken();
			i++;
		}

		return fullLabel;
	}

	/**
	 * Generate topic edges based on threshold.
	 * 
	 * @param graph
	 *            the graph
	 * @param threshold
	 *            the threshold
	 * @param topicEdgeColor
	 *            the topic edge color
	 * @param docIdPrefix
	 *            the doc id prefix
	 * @param docTopicCount
	 *            the doc topic count
	 * @param docTotalCount
	 *            the doc total count
	 * @param topicTotalCount
	 *            the topic total count
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void generateTopicEdgesBasedOnThreshold( DNVGraph graph, float threshold, String topicEdgeColor, String docIdPrefix,
			Map<String, Float> docTopicCount, Map<Integer, Float> docTotalCount, Map<Integer, Float> topicTotalCount ) throws FileNotFoundException,
			IOException
	{
		float count;
		DNVNode documentNode;
		DNVNode topicNode;
		DNVEdge edge;
		Integer topic;
		Integer doc;
		Float totalCount;
		for( DNVEntity node : graph.getNodesByType( 0, "topic" ).values() )
		{
			String bbid = node.getBbId();
			topic = Integer.parseInt( bbid.substring( bbid.indexOf( "t" ) + 1 ) );
			node.setProperty( "weight", "" + topicTotalCount.get( topic ) );
		}

		float percent;
		for( String docTopic : docTopicCount.keySet() )
		{
			doc = Integer.parseInt( docTopic.substring( 0, docTopic.indexOf( "->" ) ) );
			topic = Integer.parseInt( docTopic.substring( docTopic.indexOf( "->" ) + 2 ) );
			count = docTopicCount.get( docTopic );
			totalCount = docTotalCount.get( doc );
			if( totalCount >= 10 )
			{
				percent = count / (float)totalCount;
			}
			else
			{
				percent = 0;
			}
			if( percent * 100 >= threshold )
			{
				documentNode = (DNVNode)graph.getNodeByBbId( docIdPrefix + doc );
				topicNode = (DNVNode)graph.getNodeByBbId( "t" + topic );
				if( documentNode != null && topicNode != null )
				{
					edge = (DNVEdge)graph.getNodeByBbId( documentNode.getBbId() + "->" + topicNode.getBbId() );
					if( edge == null )
					{
						edge = new DNVEdge( graph );
						edge.setFrom( documentNode );
						edge.setTo( topicNode );
						edge.setBbId( documentNode.getBbId() + "->" + topicNode.getBbId() );
					}
					edge.setType( "topicEdge" );
					edge.setDirectional( true );
					// edge.setRestingDistance( 0.5f - (0.5f * percent) );
					edge.setRestingDistance( 0 );
					edge.setK( 1 );
					edge.setColor( topicEdgeColor );
					edge.setLabelColor( topicEdgeColor );
					edge.setProperty( "count", "" + count );
					edge.setProperty( "percent", "" + percent * 100 );
					edge.setThickness( 0.5f + 1.5f * percent );
					graph.addNode( 0, edge );
				}
			}
		}
	}

	/**
	 * Generate topic edges based on parent.
	 * 
	 * @param parent
	 *            the parent
	 * @param directory
	 *            the directory
	 * @param docTopicsFile
	 *            the doc topics file
	 * @param graph
	 *            the graph
	 * @param threshold
	 *            the threshold
	 * @param topicEdgeColor
	 *            the topic edge color
	 * @param maxThreshold
	 *            the max threshold
	 * @param docIdPrefix
	 *            the doc id prefix
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void generateTopicEdgesBasedOnParent( DNVNode parent, String directory, String docTopicsFile, DNVGraph graph, float threshold,
			String topicEdgeColor, float maxThreshold, String docIdPrefix ) throws FileNotFoundException, IOException
	{
		Map<String, DNVNode> topicNodesOfInterest = new HashMap<String, DNVNode>();
		for( DNVNode neighbor : parent.getNeighbors() )
		{
			if( neighbor.getType().equals( "topic" ) )
			{
				topicNodesOfInterest.put( neighbor.getBbId(), neighbor );
			}
		}

		FileReader fr;
		BufferedReader br;
		String line;
		fr = new FileReader( directory + docTopicsFile );
		br = new BufferedReader( fr );

		String[] lineArray;
		int count;
		DNVNode documentNode;
		DNVNode topicNode;
		DNVEdge edge;
		Integer topic;
		Integer doc;
		Map<Integer, Integer> docTotalCount = new HashMap<Integer, Integer>();
		Map<String, Integer> docTopicCount = new HashMap<String, Integer>();
		Integer totalCount;
		while( ( line = br.readLine() ) != null )
		{
			lineArray = line.split( " " );
			if( lineArray.length == 3 )
			{
				doc = Integer.parseInt( lineArray[0] );
				topic = Integer.parseInt( lineArray[1] );
				count = Integer.parseInt( lineArray[2] );
				docTopicCount.put( doc + "->" + topic, count );
				totalCount = docTotalCount.get( doc );
				if( totalCount == null )
				{
					totalCount = 0;
				}
				docTotalCount.put( doc, totalCount + count );
			}
		}

		float percent;
		while( topicNodesOfInterest.size() > 0 && threshold >= 0 )
		{
			for( String docTopic : docTopicCount.keySet() )
			{
				doc = Integer.parseInt( docTopic.substring( 0, docTopic.indexOf( "->" ) ) );
				topic = Integer.parseInt( docTopic.substring( docTopic.indexOf( "->" ) + 2 ) );
				count = docTopicCount.get( docTopic );
				totalCount = docTotalCount.get( doc );
				percent = count / (float)totalCount;
				if( percent * 100 >= threshold )
				{
					documentNode = (DNVNode)graph.getNodeByBbId( docIdPrefix + doc );
					topicNode = (DNVNode)graph.getNodeByBbId( "t" + topic );
					if( documentNode != null && topicNode != null )
					{
						edge = (DNVEdge)graph.getNodeByBbId( documentNode.getBbId() + "->" + topicNode.getBbId() );
						if( edge != null )
						{
							// count += Integer.parseInt( edge.getProperty(
							// "count" ) );
						}
						else
						{
							edge = graph.addEdge( 0, documentNode, topicNode, null );
						}
						edge.setType( "topicEdge" );
						edge.setDirectional( true );
						edge.setColor( topicEdgeColor );
						edge.setRestingDistance( 0 );
						edge.setK( 1 );
						edge.setProperty( "count", "" + count );
						edge.setProperty( "percent", "" + percent * 100 );
						edge.setThickness( 0.5f + 1.5f * count / maxThreshold );
						topicNodesOfInterest.remove( topicNode.getBbId() );
					}
				}
			}

			threshold--;
		}
		br.close();
		fr.close();
	}

	/**
	 * Generate one topic edge per document.
	 * 
	 * @param graph
	 *            the graph
	 * @param topicEdgeColor
	 *            the topic edge color
	 * @param maxThreshold
	 *            the max threshold
	 * @param docTopicCount
	 *            the doc topic count
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private static void generateOneTopicEdgePerDocument( DNVGraph graph, String topicEdgeColor, float maxThreshold, Map<String, Float> docTopicCount )
			throws FileNotFoundException, IOException
	{
		float count;
		DNVNode documentNode;
		DNVNode topicNode;
		DNVEdge edge;
		Integer topic;
		Float maxCount;
		Integer doc;
		HashMap<Integer, Float> maxCountMap = new HashMap<Integer, Float>();
		HashMap<Integer, Integer> maxCountIdMap = new HashMap<Integer, Integer>();
		for( String key : docTopicCount.keySet() )
		{
			doc = Integer.parseInt( key.substring( 0, key.indexOf( "->" ) ) );
			topic = Integer.parseInt( key.substring( key.indexOf( "->" ) + 2 ) );
			count = docTopicCount.get( key );
			maxCount = maxCountMap.get( doc );
			if( maxCount == null || maxCount < count )
			{
				maxCountMap.put( doc, count );
				maxCountIdMap.put( doc, topic );
			}
		}

		for( Integer docId : maxCountIdMap.keySet() )
		{
			documentNode = (DNVNode)graph.getNodeByBbId( "doc" + docId );
			topicNode = (DNVNode)graph.getNodeByBbId( "t" + maxCountIdMap.get( docId ) );
			edge = graph.addEdge( 0, documentNode, topicNode, null );
			edge.setType( "topicEdge" );
			edge.setDirectional( true );
			edge.setColor( topicEdgeColor );
			edge.setRestingDistance( 0 );
			edge.setK( 1 );
			edge.setProperty( "count", "" + maxCountMap.get( docId ) );
			edge.setThickness( 3.0f * maxCountMap.get( docId ) / maxThreshold );
		}
	}

	// public static void main( String args[] ) throws IOException
	// {
	// String directory = Settings.GRAPHS_PATH + "topicVisualizations/calit2/";
	// String documentsFile = "docs.txt";
	// String topicsFile = "topics.txt";
	// // String docTopicsFile = "doc.topic.count.txt";
	// // String outputGraphName = "MobyDick.dnv";
	// // boolean oneTopicPerDocument = false;
	// // int threshold = 250;
	//
	// String topicNodeColor = "#0000FF";
	// String topicEdgeColor = "#FF9999";
	//		
	// TopicVisualizationBean tvb = new TopicVisualizationBean();
	// tvb.setSelectedFolder( directory );
	//		
	// Timer timer = new Timer( Timer.MILLISECONDS );
	//		
	// File file = new File( directory, "test.csv" );
	// FileWriter fw = new FileWriter( file );
	// BufferedWriter bw = new BufferedWriter( fw );
	// String line =
	// "maxDocumentsPerThread, maxDocumentsBeforeStaringThread, Time \n";
	// bw.write( line );
	// System.out.print( line );
	//		
	// int stepsize = 100;
	//		
	// for( maxDocumentsPerThread = (int)tvb.getNumberOfDocuments();
	// maxDocumentsPerThread >= (int)tvb.getNumberOfDocuments();
	// maxDocumentsPerThread -= stepsize )
	// {
	// for( maxDocumentsBeforeStartingThread = 1500;
	// maxDocumentsBeforeStartingThread > 0; maxDocumentsBeforeStartingThread -=
	// stepsize )
	// {
	// timer.setStart();
	// create( directory, documentsFile, topicsFile, tvb.getThreshold(), false,
	// false, topicNodeColor, topicEdgeColor, tvb.getDocumentStartColor(),
	// tvb.getDocumentEndColor(), tvb.getMaxWordsPerTopic(), false,
	// tvb.getMaxThreshold(), false, tvb.getDocTopicCount(),
	// tvb.getDocTotalCount(), tvb.getTopicTotalCount(), false,
	// tvb.getNumberOfDocuments(), tvb.getLabelScaler() );
	// timer.setEnd();
	// line = maxDocumentsPerThread + ", " + maxDocumentsBeforeStartingThread +
	// ", " + timer.getLastSegment( Timer.SECONDS ) + "\n";
	// bw.write( line );
	// System.out.print( line );
	// }
	// }
	//		
	// bw.close();
	// fw.close();
	// }

	/**
	 * Creates the expanders.
	 * 
	 * @param graph
	 *            the graph
	 * @param selectedFolder
	 *            the selected folder
	 * @param threshold
	 *            the threshold
	 * @param topicEdgeColor
	 *            the topic edge color
	 * @param documentStartColor
	 *            the document start color
	 * @param documentEndColor
	 *            the document end color
	 * @param forceDocumentLabels
	 *            the force document labels
	 * @param maxThreshold
	 *            the max threshold
	 * @param labelScaler
	 *            the label scaler
	 * @param tvb
	 *            the tvb
	 */
	public static void createExpanders( DNVGraph graph, String selectedFolder, float threshold, String topicEdgeColor, String documentStartColor,
			String documentEndColor, boolean forceDocumentLabels, float maxThreshold, float labelScaler, TopicVisualizationBean tvb )
	{
		String dtf = "Ndt.txt";

		for( DNVNode node : graph.getNodes( 0 ) )
		{
			if( node.isExpandable() )
			{
				String directory = selectedFolder + "split" + File.separator + node.getLabel() + File.separator;
				float numberOfDocuments = getNumberOfDocuments( directory, dtf );
				DocumentNodeExpand dne = new DocumentNodeExpand( node, directory, "currentDoc.txt", documentStartColor, documentEndColor,
						forceDocumentLabels, numberOfDocuments, dtf, threshold, topicEdgeColor, maxThreshold, labelScaler, tvb );
				node.setExpander( dne );
			}
		}
	}
}
