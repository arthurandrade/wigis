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

package net.wigis.graph;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import net.wigis.graph.data.uploader.TopicFileUploadBean;
import net.wigis.graph.data.utilities.CreateDNVFromDocumentTopics;
import net.wigis.graph.data.utilities.DetermineAuthorExpertise;
import net.wigis.graph.data.utilities.StringUtils;
import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVEntity;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.utilities.GraphFunctions;
import net.wigis.graph.dnv.utilities.SortByFloatProperty;
import net.wigis.graph.dnv.utilities.Timer;
import net.wigis.graph.dnv.utilities.Vector2D;
import net.wigis.graph.dnv.utilities.Vector3D;
import net.wigis.settings.Settings;
import net.wigis.web.ContextLookup;
import net.wigis.web.Request;
import au.com.bytecode.opencsv.CSVReader;

// TODO: Auto-generated Javadoc
/**
 * The Class TopicVisualizationBean.
 * 
 * @author Brynjar Gretarsson
 */
public class TopicVisualizationBean
{

	/** The folder list. */
	private List<SelectItem> folderList = new ArrayList<SelectItem>();

	/** The selected folder. */
	private String selectedFolder = "";

	/** The topic settings expanded. */
	private boolean topicSettingsExpanded = true;

	/** The stopwords editor expanded. */
	private boolean stopwordsEditorExpanded = true;

	/** The Constant DOCS. */
	private static final String DOCS = "docs.txt";

	/** The Constant METADOCS. */
	private static final String METADOCS = "metaDocs.txt";

	/** The documents file. */
	private String documentsFile = METADOCS;

	/** The topics file. */
	private String topicsFile = "topics.txt";

	/** The doc topics file. */
	private String docTopicsFile = "doc.topic.count.txt";

	/** The doc topics file2. */
	private String docTopicsFile2 = "Ndt.txt";

	/** The one topic per document. */
	private boolean oneTopicPerDocument = false;

	/** The threshold. */
	private float threshold = 10;

	/** The max threshold. */
	private float maxThreshold = 100;

	/** The max words per topic. */
	private int maxWordsPerTopic = 2;

	/** The topic node color. */
	private String topicNodeColor = "#027536";

	/** The topic edge color. */
	private String topicEdgeColor = "#CCE3C7";

	/** The document start color. */
	private String documentStartColor = "#c9d400";

	/** The document end color. */
	private String documentEndColor = "#993b96";

	/** The extracted property color. */
	private String extractedPropertyColor = "#f2db09";

	/** The hide label background for documents. */
	private boolean hideLabelBackgroundForDocuments = false;

	/** The use curved labels for documents. */
	private boolean useCurvedLabelsForDocuments = false;

	/** The document alpha. */
	private float documentAlpha = 0.8f;

	/** The force document labels. */
	private boolean forceDocumentLabels = false;

	/** The Constant DEFAULT_CREATE_DOCUMENT_EDGES. */
	private static final boolean DEFAULT_CREATE_DOCUMENT_EDGES = true;

	/** The Constant DEFAULT_TIMELINE_VISUALIZATION. */
	private static final boolean DEFAULT_TIMELINE_VISUALIZATION = false;

	/** The create document edges. */
	private boolean createDocumentEdges = DEFAULT_CREATE_DOCUMENT_EDGES;

	/** The timeline visualization. */
	private boolean timelineVisualization = DEFAULT_TIMELINE_VISUALIZATION;

	/** The force topics to circle. */
	private boolean forceTopicsToCircle = true;

	/** The topic alpha. */
	private float topicAlpha = 0.7f;

	/** The label scaler. */
	private float labelScaler = 2;

	/** The stopwords. */
	private String stopwords = "";

	/** The default folder. */
	private String defaultFolder = "NSF";

	/** The exclude empty documents. */
	private boolean excludeEmptyDocuments = true;

	/** The number of topics. */
	private int numberOfTopics = 50;

	/** The use mpi. */
	private boolean useMPI = true;

	/** The number of processors. */
	private int numberOfProcessors = 4;

	/** The author to document nodes. */
	private Map<String, List<DNVNode>> authorToDocumentNodes = new HashMap<String, List<DNVNode>>();

	/** The dissimilarity matrix. */
	private double[][] dissimilarityMatrix;

	/** The dissimilarity file. */
	private String dissimilarityFile = "matrixKL.txt";

	/** The use stress minimization. */
	private boolean useStressMinimization = true;

	/** The color topics based on documents. */
	private boolean colorTopicsBasedOnDocuments = true;

	/** The color topic edges based on documents. */
	private boolean colorTopicEdgesBasedOnDocuments = true;

	/** The only topic model visible documents. */
	private boolean onlyTopicModelVisibleDocuments = false;

	/** The timestamp. */
	private String timestamp = "";

	/**
	 * Instantiates a new topic visualization bean.
	 */
	public TopicVisualizationBean()
	{
		PaintBean pb = (PaintBean)ContextLookup.lookup( "paintBean", FacesContext.getCurrentInstance() );
		if( pb != null )
		{
			pb.setInterpolationMethodUseWholeGraph( true );
			String[] layoutMethods = { Settings.DOCUMENT_TOPIC_RECTANGULAR_LAYOUT, Settings.DOCUMENT_TOPIC_CIRCULAR_LAYOUT,
					Settings.DOCUMENT_TOPIC_SPIRAL_LAYOUT, Settings.FRUCHTERMAN_REINGOLD_LAYOUT, Settings.DOCUMENT_TOPIC_MDS_LAYOUT };
			pb.buildLayoutMethodList( layoutMethods );
			pb.setLayoutMethod( Settings.DOCUMENT_TOPIC_CIRCULAR_LAYOUT );
			String[] interactionMethods = { Settings.TOPIC_INTERACTION, Settings.INTERPOLATION_INTERACTION };
			pb.buildInteractionMethodList( interactionMethods );
			pb.setInteractionMethod( Settings.INTERPOLATION_INTERACTION );
			pb.setNumberAffected( 1 );
			pb.setSortNodes( true );
			pb.setShowLabels( false );
			pb.setHighlightNeighbors( false );
		}
		readSettings();
		buildFolderList();
		String dataStr = Request.getStringParameter( "data" );
		String selectedFolder = Settings.GRAPHS_PATH + "topicVisualizations" + File.separator + defaultFolder + "/";
		if( dataStr != null )
		{
			if( !selectedFolder.equals( Settings.GRAPHS_PATH + "topicVisualizations" + File.separator + dataStr + "/" ) )
			{
				if( new File( Settings.GRAPHS_PATH + "topicVisualizations" + File.separator + dataStr + "/" ).exists() )
				{
					selectedFolder = Settings.GRAPHS_PATH + "topicVisualizations" + File.separator + dataStr + "/";
				}
			}
		}
		setSelectedFolder( selectedFolder );
	}

	/**
	 * Read settings.
	 */
	private void readSettings()
	{
		File file = new File( TopicFileUploadBean.uploadPath, "settings.txt" );
		try
		{
			BufferedReader br = new BufferedReader( new FileReader( file ) );
			String line;
			String checkString;
			while( ( line = br.readLine() ) != null )
			{
				checkString = "numberOfProcessors=";
				if( line.contains( checkString ) )
				{
					numberOfProcessors = Integer.parseInt( line.substring( line.indexOf( checkString ) + checkString.length() ).trim() );
				}
			}
		}
		catch( FileNotFoundException e )
		{
			e.printStackTrace();
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
	}

	/**
	 * Builds the folder list.
	 */
	public void buildFolderList()
	{
		folderList.clear();

		File directory = new File( Settings.GRAPHS_PATH + "topicVisualizations" + File.separator );
		FileFilter filter = new FileFilter()
		{
			@Override
			public boolean accept( File file )
			{
				if( file.isDirectory() && !file.getName().equals( ".svn" ) && !file.getName().equals( "TEMPLATE" )
						&& !file.getName().endsWith( ".app" ) )
					return true;

				return false;
			}
		};

		File[] files = directory.listFiles( filter );
		SelectItem tempItem;
		File tempFile;
		for( int i = 0; i < files.length; i++ )
		{
			tempFile = files[i];
			tempItem = new SelectItem( tempFile.getAbsolutePath() + "/", tempFile.getName() );
			folderList.add( tempItem );
		}
	}

	/**
	 * Gets the folder list.
	 * 
	 * @return the folder list
	 */
	public List<SelectItem> getFolderList()
	{
		return folderList;
	}

	/**
	 * Sets the folder list.
	 * 
	 * @param folderList
	 *            the new folder list
	 */
	public void setFolderList( List<SelectItem> folderList )
	{
		this.folderList = folderList;
	}

	/**
	 * Checks if is topic settings expanded.
	 * 
	 * @return true, if is topic settings expanded
	 */
	public boolean isTopicSettingsExpanded()
	{
		return topicSettingsExpanded;
	}

	/**
	 * Sets the topic settings expanded.
	 * 
	 * @param topicSettingsExpanded
	 *            the new topic settings expanded
	 */
	public void setTopicSettingsExpanded( boolean topicSettingsExpanded )
	{
		this.topicSettingsExpanded = topicSettingsExpanded;
	}

	/**
	 * Expand topic settings.
	 */
	public void expandTopicSettings()
	{
		setTopicSettingsExpanded( true );
	}

	/**
	 * Collapse topic settings.
	 */
	public void collapseTopicSettings()
	{
		setTopicSettingsExpanded( false );
	}

	/**
	 * Gets the selected folder.
	 * 
	 * @return the selected folder
	 */
	public String getSelectedFolder()
	{
		String dataStr = Request.getStringParameter( "data" );
		if( dataStr != null )
		{
			if( !selectedFolder.equals( Settings.GRAPHS_PATH + "topicVisualizations" + File.separator + dataStr + "/" ) )
			{
				if( new File( Settings.GRAPHS_PATH + "topicVisualizations" + File.separator + dataStr + "/" ).exists() )
				{
					setSelectedFolder( Settings.GRAPHS_PATH + "topicVisualizations" + File.separator + dataStr + "/" );
				}
			}
		}

		return selectedFolder;
	}

	/**
	 * Sets the selected folder.
	 * 
	 * @param selectedFolder
	 *            the new selected folder
	 */
	public void setSelectedFolder( String selectedFolder )
	{
		if( !selectedFolder.endsWith( "\\" ) && !selectedFolder.endsWith( "/" ) )
		{
			selectedFolder += "/";
		}
		if( !this.selectedFolder.equals( selectedFolder ) )
		{
			this.selectedFolder = selectedFolder;
			timestamp = "";
			loadSettings();
			System.gc();
			regenerateGraph( true );
		}
	}

	/**
	 * Regenerate graph.
	 * 
	 * @param updateThreshold
	 *            the update threshold
	 */
	private void regenerateGraph( boolean updateThreshold )
	{
		clearHistory();
		updateMaxThreshold( updateThreshold );
		readDissimilarityMatrix();
		readStopwordsFile();
		readKeyToColorFile();
		readDepartmentFile();
		buildGraph( updateThreshold );
	}

	/**
	 * Rebuild graph.
	 */
	public void rebuildGraph()
	{
		File file = new File( selectedFolder, getOutputFilename() );
		if( file.exists() )
		{
			file.delete();
		}
		regenerateGraph( false );
	}

	/**
	 * Reload graph.
	 */
	public void reloadGraph()
	{
		regenerateGraph( true );
	}

	/**
	 * The Class SaveThread.
	 */
	private class SaveThread extends Thread
	{

		/** The graph. */
		private DNVGraph graph;

		/** The filename. */
		private String filename;

		/**
		 * Instantiates a new save thread.
		 * 
		 * @param graph
		 *            the graph
		 * @param filename
		 *            the filename
		 */
		public SaveThread( DNVGraph graph, String filename )
		{
			this.graph = graph;
			this.filename = filename;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		public void run()
		{
			Timer timer = new Timer( Timer.MILLISECONDS );
			timer.setStart();
			graph.writeGraph( filename );
			timer.setEnd();
			System.out.println( "Saving " + filename + " took " + timer.getLastSegment( Timer.SECONDS ) + " seconds." );
		}
	}

	/**
	 * Builds the graph.
	 * 
	 * @param updateForceDocumentLables
	 *            the update force document lables
	 */
	public void buildGraph( boolean updateForceDocumentLables )
	{
		try
		{
			System.out.println( "Selected folder : '" + selectedFolder + "'" );
			String outputFilename = getOutputFilename();
			PaintBean pb = (PaintBean)ContextLookup.lookup( "paintBean", FacesContext.getCurrentInstance() );
			Timer timer = new Timer( Timer.MILLISECONDS );
			if( !( new File( selectedFolder, outputFilename ).exists() ) )
			{
				timer.setStart();

				boolean runLayout = false;
				if( pb != null )
				{
					runLayout = ( pb.getCircularLayoutBuffer() != 0 ) && ( timelineVisualization || createDocumentEdges )
							&& !pb.isDocumentTopicsCircularLayoutOnlyDeformSelectedTopics();
				}

				determineDocsFile();

				DNVGraph graph = CreateDNVFromDocumentTopics.create( selectedFolder, documentsFile, timestamp + topicsFile, threshold,
						oneTopicPerDocument, runLayout, topicNodeColor, topicEdgeColor, documentStartColor, documentEndColor, maxWordsPerTopic,
						forceDocumentLabels, maxThreshold, createDocumentEdges, docTopicCount, docTotalCount, topicTotalCount, timelineVisualization,
						numberOfDocuments, labelScaler, excludeEmptyDocuments, dissimilarityMatrix, authorToColor, colorTopicsBasedOnDocuments,
						colorTopicEdgesBasedOnDocuments, hideLabelBackgroundForDocuments, useCurvedLabelsForDocuments );
				timer.setEnd();
				System.out.println( "=========================================================================" );
				System.out.println( "Generating topics graph took " + timer.getLastSegment( Timer.SECONDS ) + " seconds." );
				System.out.println( "=========================================================================" );

				if( pb != null )
				{
					pb.setGraph( graph );
					updateTopicSizes();
					if( !pb.getLayoutMethod().equals( Settings.FRUCHTERMAN_REINGOLD_LAYOUT )
							&& !pb.getLayoutMethod().equals( Settings.DOCUMENT_TOPIC_CIRCULAR_LAYOUT )
							&& !pb.getLayoutMethod().equals( Settings.DOCUMENT_TOPIC_RECTANGULAR_LAYOUT )
							&& !pb.getLayoutMethod().equals( Settings.DOCUMENT_TOPIC_MDS_LAYOUT ) )
					{
						pb.setLayoutMethod( Settings.DOCUMENT_TOPIC_CIRCULAR_LAYOUT );
					}
					if( updateForceDocumentLables )
					{
						updateForceDocumentLabels();
					}
					timer.setStart();
					pb.runLayout();
					timer.setEnd();
					System.out.println( "Laying out the graph took " + timer.getLastSegment( Timer.SECONDS ) + " seconds." );
					saveGraph( graph );
				}
				else
				{
					System.out.println( "WARNING: PaintBean is null" );
				}
			}
			else
			{
				if( pb != null )
				{
					String folder = selectedFolder;
					if( !folder.endsWith( "\\" ) && !folder.endsWith( "/" ) )
					{
						folder += "/";
					}
					pb.setSelectedFile( folder + Settings.DEFAULT_GRAPH );
					pb.setSelectedFile( folder + outputFilename );
					if( !pb.getLayoutMethod().equals( Settings.FRUCHTERMAN_REINGOLD_LAYOUT )
							&& !pb.getLayoutMethod().equals( Settings.DOCUMENT_TOPIC_CIRCULAR_LAYOUT )
							&& !pb.getLayoutMethod().equals( Settings.DOCUMENT_TOPIC_RECTANGULAR_LAYOUT )
							&& !pb.getLayoutMethod().equals( Settings.DOCUMENT_TOPIC_MDS_LAYOUT ) )
					{
						pb.setLayoutMethod( Settings.DOCUMENT_TOPIC_CIRCULAR_LAYOUT );
					}
					for( DNVEntity docNode : pb.getGraph().getNodesByType( 0, "document" ).values() )
					{
						if( docNode.hasProperty( "Author" ) )
						{
							addAuthorDocumentMapping( docNode.getProperty( "Author" ), (DNVNode)docNode );
						}
					}
				}
			}

			buildExtractablePropertiesList( getGraph() );

			if( pb != null )
			{
				ReadAuthorNames ran = new ReadAuthorNames();
				if( updateForceDocumentLables )
				{
					updateForceDocumentLabels();
				}
				ran.start();
				DNVGraph graph = pb.getGraph();
				CreateDNVFromDocumentTopics.createExpanders( graph, selectedFolder, threshold, topicEdgeColor, documentStartColor, documentEndColor,
						forceDocumentLabels, maxThreshold, labelScaler, this );
			}
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
	}

	/**
	 * Determine docs file.
	 */
	private void determineDocsFile()
	{
		if( new File( selectedFolder, timestamp + METADOCS ).exists() )
		{
			documentsFile = timestamp + METADOCS;
		}
		else
		{
			documentsFile = timestamp + DOCS;
		}
	}

	/**
	 * Checks if is color topic edges based on documents.
	 * 
	 * @return true, if is color topic edges based on documents
	 */
	public boolean isColorTopicEdgesBasedOnDocuments()
	{
		return colorTopicEdgesBasedOnDocuments;
	}

	/**
	 * Sets the color topic edges based on documents.
	 * 
	 * @param colorTopicEdgesBasedOnDocuments
	 *            the new color topic edges based on documents
	 */
	public void setColorTopicEdgesBasedOnDocuments( boolean colorTopicEdgesBasedOnDocuments )
	{
		if( this.colorTopicEdgesBasedOnDocuments != colorTopicEdgesBasedOnDocuments )
		{
			this.colorTopicEdgesBasedOnDocuments = colorTopicEdgesBasedOnDocuments;
			if( colorTopicEdgesBasedOnDocuments )
			{
				CreateDNVFromDocumentTopics.updateColors( getGraph(), authorToColor, colorTopicEdgesBasedOnDocuments );
			}
			else
			{
				DNVGraph graph = getGraph();
				if( graph != null )
				{
					for( DNVEntity edge : graph.getNodesByType( 0, "topicEdge" ).values() )
					{
						edge.setColor( topicEdgeColor );
					}
				}
			}
		}

	}

	/**
	 * Toggle color topic edges based on documents.
	 */
	public void toggleColorTopicEdgesBasedOnDocuments()
	{
		setColorTopicEdgesBasedOnDocuments( !isColorTopicEdgesBasedOnDocuments() );
	}

	/**
	 * Save graph.
	 */
	public void saveGraph()
	{
		DNVGraph graph = getGraph();
		if( graph != null )
		{
			saveGraph( graph );
		}
	}

	/**
	 * Save graph.
	 * 
	 * @param graph
	 *            the graph
	 */
	private void saveGraph( DNVGraph graph )
	{
		SaveThread st = new SaveThread( graph, selectedFolder + getOutputFilename() );
		st.start();
	}

	/**
	 * Checks if is one topic per document.
	 * 
	 * @return true, if is one topic per document
	 */
	public boolean isOneTopicPerDocument()
	{
		return oneTopicPerDocument;
	}

	/**
	 * Sets the one topic per document.
	 * 
	 * @param oneTopicPerDocument
	 *            the new one topic per document
	 */
	public void setOneTopicPerDocument( boolean oneTopicPerDocument )
	{
		if( this.oneTopicPerDocument != oneTopicPerDocument )
		{
			this.oneTopicPerDocument = oneTopicPerDocument;
			buildGraph( false );
		}
	}

	/**
	 * Toggle one topic per document.
	 */
	public void toggleOneTopicPerDocument()
	{
		setOneTopicPerDocument( !this.oneTopicPerDocument );
	}

	/**
	 * Gets the threshold.
	 * 
	 * @return the threshold
	 */
	public float getThreshold()
	{
		return threshold;
	}

	/**
	 * Sets the threshold.
	 * 
	 * @param threshold
	 *            the new threshold
	 */
	public void setThreshold( float threshold )
	{
		if( this.threshold != threshold )
		{
			this.threshold = threshold;
			buildGraph( false );
		}
	}

	/** The all counts. */
	private List<Float> allCounts = new ArrayList<Float>();

	/** The doc total count. */
	private Map<Integer, Float> docTotalCount = new HashMap<Integer, Float>();

	/** The doc topic count. */
	private Map<String, Float> docTopicCount = new HashMap<String, Float>();

	/** The topic total count. */
	private Map<Integer, Float> topicTotalCount = new HashMap<Integer, Float>();

	/** The topic max percent. */
	private Map<Integer, Float> topicMaxPercent = new HashMap<Integer, Float>();

	/** The number of documents. */
	private float numberOfDocuments = 0;

	/**
	 * Gets the number of documents.
	 * 
	 * @return the number of documents
	 */
	public float getNumberOfDocuments()
	{
		return numberOfDocuments;
	}

	/**
	 * Gets the doc total count.
	 * 
	 * @return the doc total count
	 */
	public Map<Integer, Float> getDocTotalCount()
	{
		return docTotalCount;
	}

	/**
	 * Gets the doc topic count.
	 * 
	 * @return the doc topic count
	 */
	public Map<String, Float> getDocTopicCount()
	{
		return docTopicCount;
	}

	/**
	 * Gets the topic total count.
	 * 
	 * @return the topic total count
	 */
	public Map<Integer, Float> getTopicTotalCount()
	{
		return topicTotalCount;
	}

	/**
	 * Update max threshold.
	 * 
	 * @param updateThreshold
	 *            the update threshold
	 */
	private void updateMaxThreshold( boolean updateThreshold )
	{
		try
		{
			allCounts.clear();
			docTotalCount.clear();
			docTopicCount.clear();
			topicTotalCount.clear();
			topicMaxPercent.clear();
			authorToDocumentNodes.clear();

			String folder = selectedFolder;
			if( !folder.endsWith( "\\" ) && !folder.endsWith( "/" ) )
			{
				folder += File.separator;
			}

			File file = new File( folder + timestamp + docTopicsFile );
			String dtf;
			if( file.exists() )
			{
				dtf = timestamp + docTopicsFile;
			}
			else
			{
				dtf = timestamp + docTopicsFile2;
			}

			FileReader fr = new FileReader( folder + dtf );
			BufferedReader br = new BufferedReader( fr );
			String line;
			float count;
			String[] lineArray;
			Integer doc;
			Integer topic;

			line = br.readLine();
			numberOfDocuments = Float.parseFloat( line );
			line = br.readLine();
			numberOfTopics = Integer.parseInt( line );

			Float totalCount;
			Float topicCount;
			while( ( line = br.readLine() ) != null )
			{
				lineArray = line.split( " " );
				if( lineArray.length == 3 )
				{
					doc = Integer.parseInt( lineArray[0] );
					topic = Integer.parseInt( lineArray[1] );
					count = (float)Double.parseDouble( lineArray[2] );
					docTopicCount.put( doc + "->" + topic, count );

					totalCount = docTotalCount.get( doc );
					if( totalCount == null )
					{
						totalCount = 0.0f;
					}
					docTotalCount.put( doc, totalCount + count );

					topicCount = topicTotalCount.get( topic );
					if( topicCount == null )
					{
						topicCount = 0.0f;
					}
					topicTotalCount.put( topic, topicCount + count );
				}
			}

			float percent;
			Float topicPercent;
			for( String key : docTopicCount.keySet() )
			{
				doc = Integer.parseInt( key.substring( 0, key.indexOf( "->" ) ) );
				topic = Integer.parseInt( key.substring( key.indexOf( "->" ) + 2 ).trim() );
				totalCount = docTotalCount.get( doc );
				count = docTopicCount.get( key );
				if( totalCount >= 10 )
				{
					percent = count / (float)totalCount;
				}
				else
				{
					percent = 0;
				}
				allCounts.add( percent * 100 );

				topicPercent = topicMaxPercent.get( topic );
				if( topicPercent == null )
				{
					topicPercent = 0.0f;
				}
				if( percent * 100 > topicPercent )
				{
					topicMaxPercent.put( topic, percent * 100 );
				}
			}

			br.close();
			fr.close();

			Collections.sort( allCounts );

			if( updateThreshold )
			{
				float minPercent = 100.0f;
				for( Float tempPercent : topicMaxPercent.values() )
				{
					if( tempPercent < minPercent )
					{
						minPercent = tempPercent;
					}
				}
				threshold = (float)Math.floor( minPercent / 2.0f * 10.0f ) / 10.0f;
			}
			maxThreshold = (float)Math.floor( allCounts.get( allCounts.size() - 1 ) * 10.0 ) / 10.0f;
		}
		catch( IOException ioe )
		{
			ioe.printStackTrace();
		}
	}

	/**
	 * Gets the max threshold.
	 * 
	 * @return the max threshold
	 */
	public float getMaxThreshold()
	{
		return maxThreshold;
	}

	/**
	 * Sets the max threshold.
	 * 
	 * @param maxThreshold
	 *            the new max threshold
	 */
	public void setMaxThreshold( float maxThreshold )
	{
		this.maxThreshold = maxThreshold;
	}

	/**
	 * Gets the topic node color.
	 * 
	 * @return the topic node color
	 */
	public String getTopicNodeColor()
	{
		return topicNodeColor;
	}

	/**
	 * Sets the topic node color.
	 * 
	 * @param topicNodeColor
	 *            the new topic node color
	 */
	public void setTopicNodeColor( String topicNodeColor )
	{
		if( !this.topicNodeColor.equals( topicNodeColor ) )
		{
			this.topicNodeColor = topicNodeColor;
			updateTopicNodeColor();
		}
	}

	/**
	 * Update topic node color.
	 */
	private void updateTopicNodeColor()
	{
		DNVGraph graph = getGraph();
		if( graph != null )
		{
			Map<Integer, DNVEntity> nodes = graph.getNodesByType( 0, "topic" );
			for( DNVEntity node : nodes.values() )
			{
				node.setColor( topicNodeColor );
				node.setLabelColor( topicNodeColor );
			}

			for( DNVEntity entity : graph.getNodesByType( 0, "topicEdge" ).values() )
			{
				entity.setColor( topicEdgeColor );
				entity.setLabelColor( topicEdgeColor );
			}
		}
	}

	/**
	 * Gets the topic edge color.
	 * 
	 * @return the topic edge color
	 */
	public String getTopicEdgeColor()
	{
		return topicEdgeColor;
	}

	/**
	 * Sets the topic edge color.
	 * 
	 * @param topicEdgeColor
	 *            the new topic edge color
	 */
	public void setTopicEdgeColor( String topicEdgeColor )
	{
		if( !this.topicEdgeColor.equalsIgnoreCase( topicEdgeColor ) )
		{
			this.topicEdgeColor = topicEdgeColor;
			PaintBean pb = (PaintBean)ContextLookup.lookup( "paintBean", FacesContext.getCurrentInstance() );
			if( pb != null )
			{
				DNVGraph graph = pb.getGraph();
				List<DNVEdge> edges = graph.getEdges( 0 );
				for( DNVEdge edge : edges )
				{
					if( edge.getType() != null && edge.getType().equals( "topicEdge" ) )
					{
						edge.setColor( topicEdgeColor );
					}
				}
			}
		}
	}

	/**
	 * Gets the document start color.
	 * 
	 * @return the document start color
	 */
	public String getDocumentStartColor()
	{
		return documentStartColor;
	}

	/**
	 * Sets the document start color.
	 * 
	 * @param documentStartColor
	 *            the new document start color
	 */
	public void setDocumentStartColor( String documentStartColor )
	{
		if( !this.documentStartColor.equalsIgnoreCase( documentStartColor ) )
		{
			this.documentStartColor = documentStartColor;
			updateDocumentNodeColors();
		}
	}

	/**
	 * Update document node colors.
	 */
	private void updateDocumentNodeColors()
	{
		PaintBean pb = (PaintBean)ContextLookup.lookup( "paintBean", FacesContext.getCurrentInstance() );
		if( pb != null )
		{
			DNVGraph graph = pb.getGraph();
			List<DNVEntity> documentNodes = new ArrayList<DNVEntity>( graph.getNodesByType( 0, "document" ).values() );
			float numberOfDocuments = documentNodes.size();
			Vector3D currentColor = GraphFunctions.convertColor( this.documentStartColor );
			Vector3D endColor = GraphFunctions.convertColor( this.documentEndColor );
			float colorIncrementRed = ( endColor.getX() - currentColor.getX() ) / numberOfDocuments;
			float colorIncrementGreen = ( endColor.getY() - currentColor.getY() ) / numberOfDocuments;
			float colorIncrementBlue = ( endColor.getZ() - currentColor.getZ() ) / numberOfDocuments;
			Vector3D colorIncrement = new Vector3D( colorIncrementRed, colorIncrementGreen, colorIncrementBlue );
			DNVEntity lastNode = null;
			DNVEdge edge;
			SortByFloatProperty sbfp = new SortByFloatProperty( "index", false );
			Collections.sort( documentNodes, sbfp );
			Vector3D colorUsed = new Vector3D( currentColor );
			for( DNVEntity documentNode : documentNodes )
			{
				if( timelineVisualization && lastNode != null && lastNode.getProperty( "year" ).equals( documentNode.getProperty( "year" ) ) )
				{
					colorUsed = lastNode.getColor();
				}
				else
				{
					colorUsed = currentColor;
				}
				documentNode.setColor( colorUsed );
				documentNode.setLabelColor( colorUsed );
				if( lastNode != null )
				{
					edge = (DNVEdge)graph.getNodeByBbId( lastNode.getBbId() + "->" + documentNode.getBbId() );
					if( edge != null )
					{
						edge.setColor( colorUsed );
					}
					else
					{
						System.out.println( "Couldn't find edge with id '" + lastNode.getBbId() + "->" + documentNode.getBbId() + "'" );
					}
				}
				currentColor.add( colorIncrement );
				lastNode = documentNode;
			}
		}
	}

	/**
	 * Gets the document end color.
	 * 
	 * @return the document end color
	 */
	public String getDocumentEndColor()
	{
		return documentEndColor;
	}

	/**
	 * Sets the document end color.
	 * 
	 * @param documentEndColor
	 *            the new document end color
	 */
	public void setDocumentEndColor( String documentEndColor )
	{
		if( !this.documentEndColor.equalsIgnoreCase( documentEndColor ) )
		{
			this.documentEndColor = documentEndColor;
			updateDocumentNodeColors();
		}
	}

	/**
	 * Gets the max words per topic.
	 * 
	 * @return the max words per topic
	 */
	public int getMaxWordsPerTopic()
	{
		return maxWordsPerTopic;
	}

	/**
	 * Sets the max words per topic.
	 * 
	 * @param maxWordsPerTopic
	 *            the new max words per topic
	 */
	public void setMaxWordsPerTopic( int maxWordsPerTopic )
	{
		if( this.maxWordsPerTopic != maxWordsPerTopic )
		{
			this.maxWordsPerTopic = maxWordsPerTopic;
			DNVGraph graph = getGraph();
			if( graph != null )
			{
				for( DNVEntity topicNode : graph.getNodesByType( 0, "topic" ).values() )
				{
					if( topicNode.getProperty( "manualLabel" ) == null )
					{
						String fullLabel = topicNode.getProperty( "Full Label" );
						if( fullLabel != null )
						{
							String label = CreateDNVFromDocumentTopics.getLabel( maxWordsPerTopic, fullLabel );
							topicNode.setLabel( label );
							if( maxWordsPerTopic <= 3 )
							{
								topicNode.setForceLabel( true );
							}
							else
							{
								topicNode.setForceLabel( false );
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Gets the document alpha.
	 * 
	 * @return the document alpha
	 */
	public float getDocumentAlpha()
	{
		return documentAlpha;
	}

	/**
	 * Sets the document alpha.
	 * 
	 * @param documentAlpha
	 *            the new document alpha
	 */
	public void setDocumentAlpha( float documentAlpha )
	{
		if( this.documentAlpha != documentAlpha )
		{
			this.documentAlpha = documentAlpha;
			PaintBean pb = (PaintBean)ContextLookup.lookup( "paintBean", FacesContext.getCurrentInstance() );
			if( pb != null )
			{
				DNVGraph graph = pb.getGraph();
				Map<Integer, DNVEntity> nodes = graph.getNodesByType( 0, "document" );
				for( DNVEntity node : nodes.values() )
				{
					node.setAlpha( documentAlpha );
				}
			}
		}
	}

	/**
	 * Gets the topic alpha.
	 * 
	 * @return the topic alpha
	 */
	public float getTopicAlpha()
	{
		return topicAlpha;
	}

	/**
	 * Sets the topic alpha.
	 * 
	 * @param topicAlpha
	 *            the new topic alpha
	 */
	public void setTopicAlpha( float topicAlpha )
	{
		if( this.topicAlpha != topicAlpha )
		{
			this.topicAlpha = topicAlpha;
			PaintBean pb = (PaintBean)ContextLookup.lookup( "paintBean", FacesContext.getCurrentInstance() );
			if( pb != null )
			{
				DNVGraph graph = pb.getGraph();
				Map<Integer, DNVEntity> nodes = graph.getNodesByType( 0, "topic" );
				for( DNVEntity node : nodes.values() )
				{
					node.setAlpha( topicAlpha );
				}
			}
		}
	}

	/**
	 * Checks if is force document labels.
	 * 
	 * @return true, if is force document labels
	 */
	public boolean isForceDocumentLabels()
	{
		return forceDocumentLabels;
	}

	/**
	 * Sets the force document labels.
	 * 
	 * @param forceDocumentLabels
	 *            the new force document labels
	 */
	public void setForceDocumentLabels( boolean forceDocumentLabels )
	{
		if( this.forceDocumentLabels != forceDocumentLabels )
		{
			this.forceDocumentLabels = forceDocumentLabels;
			PaintBean pb = (PaintBean)ContextLookup.lookup( "paintBean", FacesContext.getCurrentInstance() );
			if( pb != null )
			{
				DNVGraph graph = pb.getGraph();
				Map<Integer, DNVEntity> documentNodes = graph.getNodesByType( 0, "document" );
				for( DNVEntity node : documentNodes.values() )
				{
					node.setForceLabel( forceDocumentLabels );
				}
			}
		}
	}

	/** The Constant MAX_DOCUMENT_LABELS. */
	// private static final int MAX_DOCUMENT_LABELS = 50;

	/** The Constant MIN_DOCUMENT_LABELS. */
	// private static final int MIN_DOCUMENT_LABELS = 20;

	/**
	 * Update force document labels.
	 */
	private void updateForceDocumentLabels()
	{
	// DNVGraph graph = getGraph();
	// if( graph != null )
	// {
	// if( forceDocumentLabels && graph.getNodesByType( 0, "document" ).size() >
	// MAX_DOCUMENT_LABELS )
	// {
	// setForceDocumentLabels( false );
	// }
	// else if( !forceDocumentLabels && graph.getNodesByType( 0, "document"
	// ).size() < MIN_DOCUMENT_LABELS )
	// {
	// setForceDocumentLabels( true );
	// }
	// }
	}

	/**
	 * Toggle force document labels.
	 */
	public void toggleForceDocumentLabels()
	{
		setForceDocumentLabels( !forceDocumentLabels );
	}

	/**
	 * Checks if is stopwords editor expanded.
	 * 
	 * @return true, if is stopwords editor expanded
	 */
	public boolean isStopwordsEditorExpanded()
	{
		return stopwordsEditorExpanded;
	}

	/**
	 * Sets the stopwords editor expanded.
	 * 
	 * @param stopwordsEditorExpanded
	 *            the new stopwords editor expanded
	 */
	public void setStopwordsEditorExpanded( boolean stopwordsEditorExpanded )
	{
		this.stopwordsEditorExpanded = stopwordsEditorExpanded;
	}

	/**
	 * Expand stopwords editor.
	 */
	public void expandStopwordsEditor()
	{
		setStopwordsEditorExpanded( true );
	}

	/**
	 * Collapse stopwords editor.
	 */
	public void collapseStopwordsEditor()
	{
		setStopwordsEditorExpanded( false );
	}

	/**
	 * Gets the stopwords.
	 * 
	 * @return the stopwords
	 */
	public String getStopwords()
	{
		return stopwords;
	}

	/**
	 * Write stopwords to file.
	 */
	private void writeStopwordsToFile()
	{
		if( !selectedFolder.endsWith( "\\" ) && !selectedFolder.endsWith( "/" ) )
		{
			selectedFolder += "/";
		}
		File stopwordsFile = new File( selectedFolder + "stopwords.txt" );
		try
		{
			FileWriter fw = new FileWriter( stopwordsFile, false );
			BufferedWriter bw = new BufferedWriter( fw );
			bw.write( stopwords );
			bw.close();
			fw.close();
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
	}

	/** The department to color. */
	Map<String, String> departmentToColor = new HashMap<String, String>();

	/** The department to field. */
	Map<String, String> departmentToField = new HashMap<String, String>();

	/** The field to color. */
	Map<String, String> fieldToColor = new HashMap<String, String>();

	/**
	 * Read key to color file.
	 */
	private void readKeyToColorFile()
	{
		departmentToColor.clear();
		departmentToField.clear();
		fieldToColor.clear();
		File departmentFile = new File( selectedFolder, "departmentToColorMapping.txt" );
		if( departmentFile.exists() )
		{
			try
			{
				CSVReader csv = new CSVReader( new FileReader( departmentFile ) );
				String[] line;
				csv.readNext();
				while( ( line = csv.readNext() ) != null )
				{
					if( line.length >= 3 )
					{
						departmentToColor.put( line[0], line[2] );
						departmentToField.put( line[0], line[1] );
						fieldToColor.put( line[1], line[2] );
					}
				}

				csv.close();
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
		}
	}

	/** The author to color. */
	Map<String, String> authorToColor = new HashMap<String, String>();

	/** The author to department. */
	Map<String, String> authorToDepartment = new HashMap<String, String>();

	/** The department to school. */
	Map<String, String> departmentToSchool = new HashMap<String, String>();

	/** The department to campus. */
	Map<String, String> departmentToCampus = new HashMap<String, String>();

	/**
	 * Read department file.
	 */
	private void readDepartmentFile()
	{
		authorToColor.clear();
		authorToDepartment.clear();
		departmentToSchool.clear();
		departmentToCampus.clear();
		File departmentFile = new File( selectedFolder, "department.txt" );
		if( departmentFile.exists() )
		{
			try
			{
				CSVReader csv = new CSVReader( new FileReader( departmentFile ) );
				String[] line;
				csv.readNext();
				while( ( line = csv.readNext() ) != null )
				{
					if( line.length >= 3 )
					{
						authorToColor.put( line[0], departmentToColor.get( line[2] ) );
						authorToDepartment.put( line[0], line[2] );
						if( line.length >= 4 )
						{
							departmentToSchool.put( line[2], line[3] );
							if( line.length >= 5 )
							{
								departmentToCampus.put( line[2], line[4] );
							}
						}
					}
				}

				csv.close();
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * The Class SortDepartments.
	 */
	private class SortDepartments implements Comparator<Department>
	{

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare( Department o1, Department o2 )
		{
			try
			{
				int compare = o1.getCampus().compareTo( o2.getCampus() );
				if( compare != 0 )
				{
					return compare;
				}

				compare = o1.getSchool().compareTo( o2.getSchool() );
				if( compare != 0 )
				{
					return compare;
				}

				compare = o1.getDepartment().compareTo( o2.getDepartment() );

				return compare;
			}
			catch( NullPointerException e )
			{}

			return 0;
		}
	}

	/**
	 * The Class Department.
	 */
	public class Department
	{

		/** The department. */
		String department;

		/** The field. */
		String field;

		/**
		 * Instantiates a new department.
		 * 
		 * @param department
		 *            the department
		 * @param field
		 *            the field
		 */
		public Department( String department, String field )
		{
			this.department = department;
			this.field = field;
		}

		/**
		 * Gets the department.
		 * 
		 * @return the department
		 */
		public String getDepartment()
		{
			return department;
		}

		/**
		 * Gets the field.
		 * 
		 * @return the field
		 */
		public String getField()
		{
			return field;
		}

		/**
		 * Gets the school.
		 * 
		 * @return the school
		 */
		public String getSchool()
		{
			return departmentToSchool.get( department );
		}

		/**
		 * Gets the campus.
		 * 
		 * @return the campus
		 */
		public String getCampus()
		{
			return departmentToCampus.get( department );
		}

		/**
		 * Gets the color.
		 * 
		 * @return the color
		 */
		public String getColor()
		{
			return departmentToColor.get( department );
		}

		/**
		 * Sets the field.
		 * 
		 * @param field
		 *            the new field
		 */
		public void setField( String field )
		{
			this.field = field;
			departmentToField.put( department, field );
			departmentToColor.put( department, fieldToColor.get( field ) );
			Field theField = new Field( field, null );
			theField.setColor( departmentToColor.get( department ) );
		}
	}

	/**
	 * Checks if is render field mappings.
	 * 
	 * @return true, if is render field mappings
	 */
	public boolean isRenderFieldMappings()
	{
		return colorTopicsBasedOnDocuments && authorToColor.size() > 0;
	}

	/**
	 * Gets the departments.
	 * 
	 * @return the departments
	 */
	public List<Department> getDepartments()
	{
		List<Department> departments = new ArrayList<Department>();

		for( String department : departmentToField.keySet() )
		{
			Department dep = new Department( department, departmentToField.get( department ) );
			departments.add( dep );
		}

		SortDepartments sd = new SortDepartments();
		Collections.sort( departments, sd );

		return departments;
	}

	/**
	 * Gets the field list.
	 * 
	 * @return the field list
	 */
	public List<SelectItem> getFieldList()
	{
		List<SelectItem> fieldList = new ArrayList<SelectItem>();

		for( String field : fieldToColor.keySet() )
		{
			SelectItem fieldSelect = new SelectItem( field );
			fieldList.add( fieldSelect );
		}

		return fieldList;
	}

	/**
	 * The Class Field.
	 */
	public class Field
	{

		/** The field. */
		String field;

		/** The color. */
		String color;

		/**
		 * Instantiates a new field.
		 * 
		 * @param field
		 *            the field
		 * @param color
		 *            the color
		 */
		public Field( String field, String color )
		{
			this.field = field;
			this.color = color;
		}

		/**
		 * Gets the field.
		 * 
		 * @return the field
		 */
		public String getField()
		{
			return field;
		}

		/**
		 * Gets the color.
		 * 
		 * @return the color
		 */
		public String getColor()
		{
			return color;
		}

		/**
		 * Sets the color.
		 * 
		 * @param color
		 *            the new color
		 */
		public void setColor( String color )
		{
			this.color = color;
			fieldToColor.put( field, color );
			for( String department : departmentToField.keySet() )
			{
				if( departmentToField.get( department ).equals( field ) )
				{
					departmentToColor.put( department, color );
					for( String author : authorToDepartment.keySet() )
					{
						if( authorToDepartment.get( author ).equals( department ) )
						{
							authorToColor.put( author, color );
						}
					}
				}
			}

			CreateDNVFromDocumentTopics.updateColors( getGraph(), authorToColor, colorTopicEdgesBasedOnDocuments );
		}
	}

	/**
	 * Gets the fields.
	 * 
	 * @return the fields
	 */
	public List<Field> getFields()
	{
		List<Field> fields = new ArrayList<Field>();
		for( String field : fieldToColor.keySet() )
		{
			Field newField = new Field( field, fieldToColor.get( field ) );
			fields.add( newField );
		}

		return fields;
	}

	/**
	 * Read stopwords file.
	 */
	private void readStopwordsFile()
	{
		if( !selectedFolder.endsWith( "\\" ) && !selectedFolder.endsWith( "/" ) )
		{
			selectedFolder += "/";
		}
		File stopwordsFile = new File( selectedFolder + "stopwords.txt" );
		if( stopwordsFile.exists() )
		{
			try
			{
				FileReader fr = new FileReader( stopwordsFile );
				BufferedReader br = new BufferedReader( fr );
				String line;
				stopwords = "";
				StringBuilder sb = new StringBuilder();
				while( ( line = br.readLine() ) != null )
				{
					sb.append( line ).append( "\n" );
				}
				stopwords = sb.toString();
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * Sets the stopwords.
	 * 
	 * @param stopwords
	 *            the new stopwords
	 */
	public void setStopwords( String stopwords )
	{
		if( !this.stopwords.equals( stopwords ) )
		{
			this.stopwords = stopwords;
			writeStopwordsToFile();
		}
	}

	/**
	 * Checks if is allow topic detection.
	 * 
	 * @return true, if is allow topic detection
	 */
	public boolean isAllowTopicDetection()
	{
		return new File( selectedFolder, "go" ).exists();
	}

	/**
	 * Run topic detection.
	 */
	public void runTopicDetection()
	{
		if( onlyTopicModelVisibleDocuments )
		{
			timestamp = "" + System.currentTimeMillis();
			boolean worked = generateNewDocsFile( timestamp, getGraph() );
			if( !worked )
			{
				return;
			}
		}
		else
		{
			timestamp = "";
		}
		TopicFileUploadBean.runTopicDetection( selectedFolder, numberOfTopics, useMPI, numberOfProcessors, timestamp );
		regenerateGraph( !onlyTopicModelVisibleDocuments );
	}

	/**
	 * Generate new docs file.
	 * 
	 * @param timestamp
	 *            the timestamp
	 * @param graph
	 *            the graph
	 * @return true, if successful
	 */
	private boolean generateNewDocsFile( String timestamp, DNVGraph graph )
	{
		if( graph == null )
		{
			System.out.println( "Cannot topic model visual nodes only - graph is null" );
			return false;
		}

		File file = new File( selectedFolder, timestamp + DOCS );
		File metaFile = new File( selectedFolder, timestamp + METADOCS );
		String docFile;
		int metaCount = 0;
		try
		{
			BufferedWriter bw = new BufferedWriter( new FileWriter( file ) );
			BufferedWriter bw2 = new BufferedWriter( new FileWriter( metaFile ) );
			for( DNVEntity docNode : graph.getNodesByType( 0, "document" ).values() )
			{
				docFile = docNode.getProperty( "contentsFile" );
				if( docFile.endsWith( "_meta.txt" ) )
				{
					metaCount++;
					bw2.write( docFile + "\n" );
					docFile = docFile.replaceAll( "_meta.txt", ".txt" );
				}
				bw.write( docFile + "\n" );
			}
			bw.close();
			bw2.close();
			if( metaCount == 0 && metaFile.exists() )
			{
				metaFile.delete();
			}
		}
		catch( IOException e )
		{
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * Gets the graph.
	 * 
	 * @return the graph
	 */
	private DNVGraph getGraph()
	{
		PaintBean pb = (PaintBean)ContextLookup.lookup( "paintBean", FacesContext.getCurrentInstance() );
		if( pb != null )
		{
			return pb.getGraph();
		}

		return null;
	}

	/**
	 * Gets the paint bean.
	 * 
	 * @return the paint bean
	 */
	private PaintBean getPaintBean()
	{
		PaintBean pb = (PaintBean)ContextLookup.lookup( "paintBean", FacesContext.getCurrentInstance() );
		return pb;
	}

	/**
	 * Gets the graph description.
	 * 
	 * @return the graph description
	 */
	public String getGraphDescription()
	{
		String description = "";
		DNVGraph graph = getGraph();
		if( graph != null )
		{
			try
			{
				int docNodes = graph.getNodesByType( 0, "document" ).size();
				int topicNodes = graph.getNodesByType( 0, "topic" ).size();
				int topicEdges = graph.getNodesByType( 0, "topicEdge" ).size();

				description += "Graph contains: <br /> ";
				description += docNodes + " document nodes, <br />";
				description += topicNodes + " topic nodes, and <br />";
				description += topicEdges + " document -> topic edges.";
			}
			catch( NullPointerException npe )
			{}
		}

		return description;
	}

	/**
	 * Checks if is creates the document edges.
	 * 
	 * @return true, if is creates the document edges
	 */
	public boolean isCreateDocumentEdges()
	{
		return createDocumentEdges;
	}

	/**
	 * Sets the creates the document edges.
	 * 
	 * @param createDocumentEdges
	 *            the new creates the document edges
	 */
	public void setCreateDocumentEdges( boolean createDocumentEdges )
	{
		if( this.createDocumentEdges != createDocumentEdges )
		{
			this.createDocumentEdges = createDocumentEdges;
			if( timelineVisualization && createDocumentEdges )
			{
				timelineVisualization = false;
			}

			buildGraph( false );
		}
	}

	/**
	 * Save graph and settings.
	 */
	public void saveGraphAndSettings()
	{
		saveSettings();
		saveGraph();
	}

	/**
	 * Gets the output filename.
	 * 
	 * @return the output filename
	 */
	private String getOutputFilename()
	{
		String outputFilename = null;
		PaintBean pb = (PaintBean)ContextLookup.lookup( "paintBean", FacesContext.getCurrentInstance() );
		if( pb != null )
		{
			if( selectedFolder.endsWith( "\\" ) || selectedFolder.endsWith( "/" ) )
			{
				outputFilename = selectedFolder.substring( 0, selectedFolder.length() - 1 );
			}
			else
			{
				outputFilename = selectedFolder;
			}
			if( outputFilename.contains( "\\" ) )
			{
				outputFilename = outputFilename.substring( outputFilename.lastIndexOf( "\\" ) + 1 );
			}
			if( outputFilename.contains( "/" ) )
			{
				outputFilename = outputFilename.substring( outputFilename.lastIndexOf( "/" ) + 1 );
			}
			if( oneTopicPerDocument )
			{
				outputFilename = outputFilename + "_" + maxWordsPerTopic + "_" + createDocumentEdges + "_" + timelineVisualization + "_"
						+ pb.getCircularLayoutBuffer() + "_" + excludeEmptyDocuments + "_" + forceDocumentLabels + "_" + forceTopicsToCircle + ".dnv";
			}
			else
			{
				outputFilename = outputFilename + "_" + threshold + "_" + maxWordsPerTopic + "_" + createDocumentEdges + "_" + timelineVisualization
						+ "_" + pb.getCircularLayoutBuffer() + "_" + excludeEmptyDocuments + "_" + forceDocumentLabels + "_" + forceTopicsToCircle
						+ ".dnv";
			}
		}

		return timestamp + outputFilename;
	}

	/**
	 * Save settings.
	 */
	public void saveSettings()
	{
		File settingsFile = new File( selectedFolder + "settings.txt" );
		try
		{
			FileWriter fw = new FileWriter( settingsFile );
			BufferedWriter bw = new BufferedWriter( fw );
			bw.write( "createDocumentEdges=" + createDocumentEdges + "\n" );
			bw.write( "timelineVisualization=" + timelineVisualization + "\n" );
			bw.write( "maxWordsPerTopic=" + maxWordsPerTopic + "\n" );
			bw.write( "excludeEmptyDocuments=" + excludeEmptyDocuments + "\n" );
			bw.write( "forceTopicsToCircle=" + forceTopicsToCircle + "\n" );
			bw.write( "numberOfTopics=" + numberOfTopics + "\n" );
			bw.write( "colorTopicsBasedOnDocuments=" + colorTopicsBasedOnDocuments + "\n" );
			bw.write( "documentStartColor=" + documentStartColor + "\n" );
			bw.write( "documentEndColor=" + documentEndColor + "\n" );
			PaintBean pb = (PaintBean)ContextLookup.lookup( "paintBean", FacesContext.getCurrentInstance() );
			if( pb != null )
			{
				bw.write( "onlyDeformSelectedTopics=" + pb.isDocumentTopicsCircularLayoutOnlyDeformSelectedTopics() + "\n" );
				;
				bw.write( "circularLayoutBuffer=" + pb.getCircularLayoutBuffer() + "\n" );
			}
			bw.close();
			fw.close();
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
	}

	/**
	 * Load settings.
	 */
	private void loadSettings()
	{
		File settingsFile = new File( selectedFolder + "settings.txt" );
//		createDocumentEdges = DEFAULT_CREATE_DOCUMENT_EDGES;
//		timelineVisualization = DEFAULT_TIMELINE_VISUALIZATION;
		try
		{
			FileReader fr;
			fr = new FileReader( settingsFile );
			BufferedReader br = new BufferedReader( fr );
			String line;
			String checkString;
			while( ( line = br.readLine() ) != null )
			{
				checkString = "createDocumentEdges=";
				if( line.startsWith( checkString ) )
				{
					boolean temp = Boolean.parseBoolean( line.substring( line.indexOf( checkString ) + checkString.length() ) );
					if( temp != createDocumentEdges )
					{
						createDocumentEdges = temp;
					}
				}
				checkString = "timelineVisualization=";
				if( line.startsWith( checkString ) )
				{
					boolean temp = Boolean.parseBoolean( line.substring( line.indexOf( checkString ) + checkString.length() ) );
					if( temp != timelineVisualization )
					{
						timelineVisualization = temp;
					}
				}
				checkString = "maxWordsPerTopic=";
				if( line.startsWith( checkString ) )
				{
					int temp = Integer.parseInt( line.substring( line.indexOf( checkString ) + checkString.length() ) );
					if( temp != maxWordsPerTopic )
					{
						maxWordsPerTopic = temp;
					}
				}
				checkString = "excludeEmptyDocuments=";
				if( line.startsWith( checkString ) )
				{
					boolean temp = Boolean.parseBoolean( line.substring( line.indexOf( checkString ) + checkString.length() ) );
					if( temp != excludeEmptyDocuments )
					{
						excludeEmptyDocuments = temp;
					}
				}
				checkString = "forceTopicsToCircle=";
				if( line.startsWith( checkString ) )
				{
					boolean temp = Boolean.parseBoolean( line.substring( line.indexOf( checkString ) + checkString.length() ) );
					if( temp != forceTopicsToCircle )
					{
						forceTopicsToCircle = temp;
					}
				}
				checkString = "numberOfTopics=";
				if( line.startsWith( checkString ) )
				{
					int temp = Integer.parseInt( line.substring( line.indexOf( checkString ) + checkString.length() ) );
					if( temp != numberOfTopics )
					{
						numberOfTopics = temp;
					}
				}
				checkString = "colorTopicsBasedOnDocuments=";
				if( line.startsWith( checkString ) )
				{
					boolean temp = Boolean.parseBoolean( line.substring( line.indexOf( checkString ) + checkString.length() ) );
					if( temp != colorTopicsBasedOnDocuments )
					{
						colorTopicsBasedOnDocuments = temp;
					}
				}

				checkString = "documentStartColor=";
				if( line.startsWith( checkString ) )
				{
					documentStartColor = line.substring( line.indexOf( checkString ) + checkString.length() );
				}

				checkString = "documentEndColor=";
				if( line.startsWith( checkString ) )
				{
					documentEndColor = line.substring( line.indexOf( checkString ) + checkString.length() );
				}

				PaintBean pb = (PaintBean)ContextLookup.lookup( "paintBean", FacesContext.getCurrentInstance() );
				if( pb != null )
				{
					checkString = "circularLayoutBuffer=";
					if( line.startsWith( checkString ) )
					{
						double circularLayoutBuffer = Double.parseDouble( line.substring( line.indexOf( checkString ) + checkString.length() ) );
						if( circularLayoutBuffer != pb.getCircularLayoutBuffer() )
						{
							pb.setCircularLayoutBuffer( circularLayoutBuffer );
						}
					}
					checkString = "onlyDeformSelectedTopics=";
					if( line.startsWith( checkString ) )
					{
						boolean onlyDeformSelectedTopics = Boolean
								.parseBoolean( line.substring( line.indexOf( checkString ) + checkString.length() ) );
						if( onlyDeformSelectedTopics != pb.isDocumentTopicsCircularLayoutOnlyDeformSelectedTopics() )
						{
							pb.setDocumentTopicsCircularLayoutOnlyDeformSelectedTopics( onlyDeformSelectedTopics );
						}
					}
				}
			}

			if( numberOfTopics == 50 )
			{
				numberOfTopics = (int)CreateDNVFromDocumentTopics.getNumberOfTopics( selectedFolder, "Ndt.txt" );
			}
			System.out.println( "Current settings : " );
			System.out.println( "createDocumentEdges=" + createDocumentEdges );
			System.out.println( "timelineVisualization=" + timelineVisualization );
			System.out.println( "maxWordsPerTopic=" + maxWordsPerTopic );
			System.out.println( "excludeEmptyDocuments=" + excludeEmptyDocuments );
			System.out.println( "forceTopicsToCircle=" + forceTopicsToCircle );
			System.out.println( "numberOfTopics=" + numberOfTopics );
			System.out.println( "colorTopicsBasedOnDocuments=" + colorTopicsBasedOnDocuments );
			PaintBean pb = (PaintBean)ContextLookup.lookup( "paintBean", FacesContext.getCurrentInstance() );
			if( pb != null )
			{
				System.out.println( "onlyDeformSelectedTopics=" + pb.isDocumentTopicsCircularLayoutOnlyDeformSelectedTopics() );
				System.out.println( "circularLayoutBuffer=" + pb.getCircularLayoutBuffer() );
			}
		}
		catch( IOException e )
		{
			e.printStackTrace();
			saveSettings();
		}
	}

	/**
	 * Toggle create document edges.
	 */
	public void toggleCreateDocumentEdges()
	{
		setCreateDocumentEdges( !isCreateDocumentEdges() );
	}

	/**
	 * Checks if is timeline visualization.
	 * 
	 * @return true, if is timeline visualization
	 */
	public boolean isTimelineVisualization()
	{
		return timelineVisualization;
	}

	/**
	 * Sets the timeline visualization.
	 * 
	 * @param timelineVisualization
	 *            the new timeline visualization
	 */
	public void setTimelineVisualization( boolean timelineVisualization )
	{
		if( this.timelineVisualization != timelineVisualization )
		{
			this.timelineVisualization = timelineVisualization;
			if( timelineVisualization && createDocumentEdges )
			{
				createDocumentEdges = false;
			}

			PaintBean pb = (PaintBean)ContextLookup.lookup( "paintBean", FacesContext.getCurrentInstance() );
			if( pb != null )
			{
				DNVGraph graph = pb.getGraph();
				CreateDNVFromDocumentTopics.updateTimelineVisualization( graph, documentStartColor, documentEndColor, timelineVisualization );
				pb.runLayout();
			}
		}
	}

	/**
	 * Toggle timeline visualization.
	 */
	public void toggleTimelineVisualization()
	{
		setTimelineVisualization( !isTimelineVisualization() );
	}

	/**
	 * Undo.
	 */
	public void undo()
	{
		PaintBean pb = (PaintBean)ContextLookup.lookup( "paintBean", FacesContext.getCurrentInstance() );
		if( pb != null && isShowUndo() )
		{
			DNVGraph graph = getGraph();

			if( authorCollapseHistoryIndex == historyIndex )
			{
				authorCollapseHistoryIndex = -1;
				if( graph != null )
				{
					synchronized( graph )
					{
						List<DNVEntity> entities = new ArrayList<DNVEntity>( graph.getNodesByType( 0, "document" ).values() );
						for( DNVEntity entity : entities )
						{
							graph.removeNode( entity );
						}
					}
				}
			}
			else if( history.size() == historyIndex )
			{
				graph = getGraph();
				if( graph != null )
				{
					List<DNVEntity> nodesAndEdges = graph.getNodesAndEdges( 0 );
					history.add( nodesAndEdges );
				}
			}

			DNVGraph oldGraph = getGraph();
			graph = new DNVGraph();
			if( oldGraph != null )
			{
				graph.setExtractableProperties( oldGraph.getExtractableProperties() );
			}
			historyIndex--;
			List<DNVEntity> nodesAndEdges = history.get( historyIndex );
			for( DNVEntity entity : nodesAndEdges )
			{
				graph.addNode( 0, entity );
				if( !( entity instanceof DNVEdge )
						&& ( entity.getType().equals( "topic" ) || ( forceDocumentLabels && entity.getType().equals( "document" ) ) || ( !entity
								.getType().equals( "document" ) && !entity.getType().equals( "topic" ) ) ) )
				{
					entity.setForceLabel( true );
				}
			}

			pb.setGraph( graph );

			updateScaleAndLayout( graph );
			pb.forceSubgraphRefresh();
			pb.updateNodeSize();
		}
	}

	/**
	 * Redo.
	 */
	public void redo()
	{
		PaintBean pb = (PaintBean)ContextLookup.lookup( "paintBean", FacesContext.getCurrentInstance() );
		if( pb != null && isShowRedo() )
		{
			DNVGraph oldGraph = getGraph();
			DNVGraph graph = new DNVGraph();
			if( oldGraph != null )
			{
				graph.setExtractableProperties( oldGraph.getExtractableProperties() );
			}

			historyIndex++;
			List<DNVEntity> nodesAndEdges = history.get( historyIndex );
			for( DNVEntity entity : nodesAndEdges )
			{
				graph.addNode( 0, entity );
				if( entity.getType().equals( "topic" ) || ( forceDocumentLabels && entity.getType().equals( "document" ) ) )
				{
					entity.setForceLabel( true );
				}
			}

			pb.setGraph( graph );

			updateScaleAndLayout( graph );
			pb.forceSubgraphRefresh();
			pb.updateNodeSize();
		}
	}

	/**
	 * Checks if is show undo.
	 * 
	 * @return true, if is show undo
	 */
	public boolean isShowUndo()
	{
		return historyIndex > 0;
	}

	/**
	 * Checks if is show redo.
	 * 
	 * @return true, if is show redo
	 */
	public boolean isShowRedo()
	{
		return historyIndex < history.size() - 1;
	}

	/**
	 * Clear history.
	 */
	private void clearHistory()
	{
		history.clear();
		historyIndex = 0;
		authorCollapseHistoryIndex = -1;
	}

	/** The history. */
	private List<List<DNVEntity>> history = new ArrayList<List<DNVEntity>>();

	/** The history index. */
	private int historyIndex = 0;

	/**
	 * Visualize selected nodes only.
	 */
	public void visualizeSelectedNodesOnly()
	{
		DNVGraph graph = getGraph();
		if( graph != null )
		{
			if( graph.getSelectedNodes( 0 ).size() > 0 )
			{
				addToHistory();

				List<DNVNode> nodes = graph.getNodes( 0 );
				for( DNVNode node : nodes )
				{
					if( !node.isSelected() && !node.isNeighborSelected() )
					{
						graph.removeNode( node );
					}
				}

				updateScaleAndLayout( graph );
				PaintBean pb = (PaintBean)ContextLookup.lookup( "paintBean", FacesContext.getCurrentInstance() );
				pb.forceSubgraphRefresh();
				pb.setSelectAllCheckBox( false );
			}
		}
	}

	/**
	 * Adds the to history.
	 */
	public void addToHistory()
	{
		DNVGraph graph = getGraph();
		if( graph != null )
		{
			List<DNVEntity> nodesAndEdges = graph.getNodesAndEdges( 0 );
			while( history.size() > historyIndex )
			{
				history.remove( history.size() - 1 );
			}
			history.add( nodesAndEdges );
			historyIndex++;
		}
	}

	/**
	 * Update scale and layout.
	 * 
	 * @param graph
	 *            the graph
	 */
	private void updateScaleAndLayout( DNVGraph graph )
	{
		CreateDNVFromDocumentTopics.updateTimelineVisualization( graph, documentStartColor, documentEndColor, timelineVisualization );
		CreateDNVFromDocumentTopics.scaleDocumentNodes( graph, labelScaler );
		CreateDNVFromDocumentTopics.scaleTopicNodes( graph, labelScaler );
		graph.deselectAllNodes( 0 );
		updateForceDocumentLabels();
		updateTopicSizes();

		PaintBean pb = (PaintBean)ContextLookup.lookup( "paintBean", FacesContext.getCurrentInstance() );
		if( pb != null )
		{
			pb.forceSubgraphRefresh();
			pb.updateNodeSize();
			pb.runLayout();
		}
	}

	/**
	 * Gets the label scaler.
	 * 
	 * @return the label scaler
	 */
	public float getLabelScaler()
	{
		return labelScaler;
	}

	/**
	 * Sets the label scaler.
	 * 
	 * @param labelScaler
	 *            the new label scaler
	 */
	public void setLabelScaler( float labelScaler )
	{
		if( this.labelScaler != labelScaler )
		{
			this.labelScaler = labelScaler;
			DNVGraph graph = getGraph();
			if( graph != null )
			{
				CreateDNVFromDocumentTopics.scaleTopicNodes( graph, labelScaler );
				CreateDNVFromDocumentTopics.scaleDocumentNodes( graph, labelScaler );
			}
		}
	}

	/**
	 * Sets the exclude empty documents.
	 * 
	 * @param excludeEmptyDocuments
	 *            the new exclude empty documents
	 */
	public void setExcludeEmptyDocuments( boolean excludeEmptyDocuments )
	{
		if( this.excludeEmptyDocuments != excludeEmptyDocuments )
		{
			this.excludeEmptyDocuments = excludeEmptyDocuments;
			buildGraph( false );
		}
	}

	/**
	 * Checks if is exclude empty documents.
	 * 
	 * @return true, if is exclude empty documents
	 */
	public boolean isExcludeEmptyDocuments()
	{
		return excludeEmptyDocuments;
	}

	/**
	 * Toggle exclude empty documents.
	 */
	public void toggleExcludeEmptyDocuments()
	{
		setExcludeEmptyDocuments( !isExcludeEmptyDocuments() );
	}

	/**
	 * Adds the author document mapping.
	 * 
	 * @param author
	 *            the author
	 * @param documentNode
	 *            the document node
	 */
	public void addAuthorDocumentMapping( String author, DNVNode documentNode )
	{
		List<DNVNode> nodes = authorToDocumentNodes.get( author );
		if( nodes == null )
		{
			nodes = new ArrayList<DNVNode>();
			authorToDocumentNodes.put( author, nodes );
		}

		nodes.add( documentNode );
	}

	/**
	 * Removes the author document mapping.
	 * 
	 * @param author
	 *            the author
	 * @param documentNode
	 *            the document node
	 */
	public void removeAuthorDocumentMapping( String author, DNVNode documentNode )
	{
		if( documentNode != null )
		{
			List<DNVNode> nodes = authorToDocumentNodes.get( author );
			if( nodes == null )
			{
				nodes = new ArrayList<DNVNode>();
				authorToDocumentNodes.put( author, nodes );
			}

			nodes.remove( documentNode );
		}
	}

	/**
	 * Checks if is allow author collapsing.
	 * 
	 * @return true, if is allow author collapsing
	 */
	public boolean isAllowAuthorCollapsing()
	{
		DNVGraph graph = getGraph();
		if( graph != null )
		{
			return authorToDocumentNodes.size() > 0 && authorCollapseHistoryIndex == -1;
		}

		return false;
	}

	/**
	 * The Class ReadAuthorNames.
	 */
	private class ReadAuthorNames extends Thread
	{

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		public void run()
		{
			authorToLabel.clear();
			File namesFile = new File( selectedFolder, "names.txt" );
			if( namesFile.exists() )
			{
				try
				{
					FileReader fr = new FileReader( namesFile );
					BufferedReader br = new BufferedReader( fr );
					String line;
					String[] lineArray;
					String lastPart = "";
					while( ( line = br.readLine() ) != null )
					{
						lineArray = line.split( "," );
						if( lineArray[0] != null )
						{
							lineArray[0] = StringUtils.replaceAll( lineArray[0], "\"", "" );
							if( lineArray[1] != null )
							{
								lineArray[1] = StringUtils.replaceAll( lineArray[1], "\"", "" );
								if( lineArray.length >= 3 && lineArray[2] != null )
								{
									lastPart = StringUtils.replaceAll( lineArray[2], "\"", "" );
									lastPart += " ";
								}
								else
								{
									lastPart = "";
								}
								authorToLabel.put( lineArray[0], lastPart + lineArray[1] );
							}
						}
					}
				}
				catch( IOException ioe )
				{
					ioe.printStackTrace();
				}
			}
		}
	}

	/** The author to label. */
	private Map<String, String> authorToLabel = new HashMap<String, String>();

	/** The author collapse history index. */
	private int authorCollapseHistoryIndex = -1;

	/**
	 * Collapse authors.
	 */
	public void collapseAuthors()
	{
		if( isAllowAuthorCollapsing() )
		{
			DNVGraph graph = getGraph();
			if( graph != null )
			{
				CreateDNVFromDocumentTopics.ensureAllDocumentContentsAreRead( graph );
				addToHistory();
				authorCollapseHistoryIndex = historyIndex;
				List<DNVNode> documentNodes;
				Map<Integer, DNVNode> fromNeighbors = new HashMap<Integer, DNVNode>();
				Map<Integer, DNVNode> toNeighbors = new HashMap<Integer, DNVNode>();
				Map<Integer, Float> idToPercent = new HashMap<Integer, Float>();
				for( String author : authorToDocumentNodes.keySet() )
				{
					if( author != null && !author.equals( "null" ) )
					{
						documentNodes = authorToDocumentNodes.get( author );
						System.out.println( author + " has " + documentNodes.size() + " documents." );
						fromNeighbors.clear();
						toNeighbors.clear();
						Vector2D position = new Vector2D( 0, 0 );
						Vector3D color = new Vector3D( 0, 0, 0 );
						Float percent;
						int bufferSize = 0;
						for( DNVNode docNode : documentNodes )
						{
							bufferSize += docNode.getProperty( "Contents" ).length();
						}

						StringBuilder contentBuffer = new StringBuilder( bufferSize + 100 );
						List<DNVNode> docsToRemove = new ArrayList<DNVNode>();
						for( DNVNode docNode : documentNodes )
						{
							if( graph.getNodeById( docNode.getId() ) != null )
							{
								color.add( docNode.getColor() );
								position.add( docNode.getPosition() );
								contentBuffer.append( docNode.getProperty( "Contents" ) );
								for( DNVEdge fromEdge : docNode.getFromEdges( true ) )
								{
									toNeighbors.put( fromEdge.getTo().getId(), fromEdge.getTo() );
									percent = idToPercent.get( fromEdge.getTo().getId() );
									if( percent == null )
									{
										percent = 0f;
									}
									percent += Float.parseFloat( fromEdge.getProperty( "percent" ) );
									if( percent > 1 )
									{
										percent = 1f;
									}
									idToPercent.put( fromEdge.getTo().getId(), percent );
								}
								for( DNVEdge toEdge : docNode.getToEdges( true ) )
								{
									fromNeighbors.put( toEdge.getTo().getId(), toEdge.getTo() );
									percent = idToPercent.get( toEdge.getFrom().getId() );
									if( percent == null )
									{
										percent = 0f;
									}
									percent += Float.parseFloat( toEdge.getProperty( "percent" ) );
									if( percent > 1 )
									{
										percent = 1f;
									}
									idToPercent.put( toEdge.getFrom().getId(), percent );
								}

								graph.removeNode( docNode );
							}
							else
							{
								docsToRemove.add( docNode );
							}
						}
						for( DNVNode docNode : docsToRemove )
						{
							removeAuthorDocumentMapping( author, docNode );
						}
						// DNVEntity.ID = DNVEntity.ID * 2;
						color.dotProduct( 1.0f / (float)documentNodes.size() );
						position.dotProduct( 1.0f / (float)documentNodes.size() );
						DNVNode node = new DNVNode( graph );
						node.setProperty( "Author", author );
						node.setProperty( "Number of Documents", "" + documentNodes.size() );
						String label = authorToLabel.get( author );
						if( label == null )
						{
							label = author;
						}
						node.setLabel( label );
						node.setBbId( "doc_" + author );
						node.setType( "document" );
						node.setProperty( "Contents", contentBuffer.toString() );
						node.setColor( color );
						node.setLabelColor( color );
						node.setPosition( position );
						graph.addNode( 0, node );
						for( DNVNode fromNeighbor : fromNeighbors.values() )
						{
							DNVEdge newEdge = new DNVEdge( graph );
							newEdge.setType( "topicEdge" );
							newEdge.setFrom( fromNeighbor );
							newEdge.setTo( node );
							newEdge.setBbId( fromNeighbor.getBbId() + "->" + node.getBbId() );
							newEdge.setDirectional( true );
							newEdge.setColor( topicEdgeColor );
							percent = idToPercent.get( fromNeighbor.getId() );
							newEdge.setRestingDistance( 0 );
							newEdge.setK( 2 );
							newEdge.setThickness( 0.5f + 1.5f * percent );
							graph.addNode( 0, newEdge );
						}
						for( DNVNode toNeighbor : toNeighbors.values() )
						{
							DNVEdge newEdge = new DNVEdge( graph );
							newEdge.setType( "topicEdge" );
							newEdge.setFrom( node );
							newEdge.setTo( toNeighbor );
							newEdge.setBbId( node.getBbId() + "->" + toNeighbor.getBbId() );
							newEdge.setDirectional( true );
							newEdge.setColor( topicEdgeColor );
							percent = idToPercent.get( toNeighbor.getId() );
							newEdge.setRestingDistance( 0 );
							newEdge.setK( 2 );
							newEdge.setThickness( 0.5f + 1.5f * percent );
							graph.addNode( 0, newEdge );
						}
					}
				}
				graph.removeIsolatedNodes();
				CreateDNVFromDocumentTopics.updateColors( graph, authorToColor, colorTopicEdgesBasedOnDocuments );
				updateScaleAndLayout( graph );
			}
		}
	}

	/**
	 * Sets the force topics to circle.
	 * 
	 * @param forceTopicsToCircle
	 *            the new force topics to circle
	 */
	public void setForceTopicsToCircle( boolean forceTopicsToCircle )
	{
		if( this.forceTopicsToCircle != forceTopicsToCircle )
		{
			this.forceTopicsToCircle = forceTopicsToCircle;
			PaintBean pb = (PaintBean)ContextLookup.lookup( "paintBean", FacesContext.getCurrentInstance() );
			if( pb != null )
			{
				pb.runLayout();
			}
		}
	}

	/**
	 * Checks if is force topics to circle.
	 * 
	 * @return true, if is force topics to circle
	 */
	public boolean isForceTopicsToCircle()
	{
		return forceTopicsToCircle;
	}

	/**
	 * Toggle force topics to circle.
	 */
	public void toggleForceTopicsToCircle()
	{
		setForceTopicsToCircle( !isForceTopicsToCircle() );
	}

	/**
	 * Gets the number of topics.
	 * 
	 * @return the number of topics
	 */
	public int getNumberOfTopics()
	{
		return numberOfTopics;
	}

	/**
	 * Read dissimilarity matrix.
	 */
	private void readDissimilarityMatrix()
	{
		try
		{
			dissimilarityMatrix = readMatrix( selectedFolder, timestamp + dissimilarityFile );
		}
		catch( IOException ioe )
		{
			// ioe.printStackTrace();
			dissimilarityMatrix = null;
		}

		PaintBean pb = (PaintBean)ContextLookup.lookup( "paintBean", FacesContext.getCurrentInstance() );
		if( pb != null )
		{
			if( dissimilarityMatrix != null && !createDocumentEdges && !timelineVisualization )
			{
				pb.setLayoutMethod( Settings.DOCUMENT_TOPIC_MDS_LAYOUT );
			}
			else
			{
				pb.setLayoutMethod( Settings.DOCUMENT_TOPIC_CIRCULAR_LAYOUT );
			}
		}
	}

	/**
	 * Read matrix.
	 * 
	 * @param directory
	 *            the directory
	 * @param file
	 *            the file
	 * @return the double[][]
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static double[][] readMatrix( String directory, String file ) throws IOException
	{
		FileReader fr = new FileReader( directory + file );
		BufferedReader br = new BufferedReader( fr );
		String line;
		String[] lineArray;
		double[][] doubleArray;
		line = br.readLine();
		lineArray = line.split( " " );
		int counter = 0;
		doubleArray = new double[lineArray.length][lineArray.length];
		for( int i = 0; i < lineArray.length; i++ )
		{
			doubleArray[counter][i] = Double.parseDouble( lineArray[i] );
		}
		counter++;
		while( ( line = br.readLine() ) != null )
		{
			lineArray = line.split( " " );
			for( int i = 0; i < lineArray.length; i++ )
			{
				doubleArray[counter][i] = Double.parseDouble( lineArray[i] );
			}
			counter++;
		}

		br.close();
		fr.close();

		return doubleArray;
	}

	/**
	 * Sets the number of topics.
	 * 
	 * @param numberOfTopics
	 *            the new number of topics
	 */
	public void setNumberOfTopics( int numberOfTopics )
	{
		this.numberOfTopics = numberOfTopics;
	}

	/**
	 * Gets the selected nodes.
	 * 
	 * @return the selected nodes
	 */
	public Map<Integer, DNVNode> getSelectedNodes()
	{
		DNVGraph graph = getGraph();
		if( graph != null )
		{
			Map<Integer, DNVNode> selectedNodes = graph.getSelectedNodes( 0 );
			if( selectedNodes != null )
			{
				return selectedNodes;
			}
		}

		return new HashMap<Integer, DNVNode>();
	}

	/**
	 * Expand selection.
	 */
	public void expandSelection()
	{
		Map<Integer, DNVNode> selectedMap = getSelectedNodes();
		List<DNVNode> selectedNodes = new ArrayList<DNVNode>( selectedMap.values() );
		System.out.println( "expanding selection of " + selectedNodes.size() + " nodes." );
		for( DNVNode node : selectedNodes )
		{
			node.selectNeighbors( true );
		}

		System.out.println( "expandSelection finished" );
	}

	/**
	 * Gets the dissimilarity matrix.
	 * 
	 * @return the dissimilarity matrix
	 */
	public double[][] getDissimilarityMatrix()
	{
		return dissimilarityMatrix;
	}

	/**
	 * Sets the dissimilarity matrix.
	 * 
	 * @param dissimilarityMatrix
	 *            the new dissimilarity matrix
	 */
	public void setDissimilarityMatrix( double[][] dissimilarityMatrix )
	{
		this.dissimilarityMatrix = dissimilarityMatrix;
	}

	/**
	 * Checks if is use stress minimization.
	 * 
	 * @return true, if is use stress minimization
	 */
	public boolean isUseStressMinimization()
	{
		return useStressMinimization;
	}

	/**
	 * Sets the use stress minimization.
	 * 
	 * @param useStressMinimization
	 *            the new use stress minimization
	 */
	public void setUseStressMinimization( boolean useStressMinimization )
	{
		if( this.useStressMinimization != useStressMinimization )
		{
			this.useStressMinimization = useStressMinimization;
			PaintBean pb = (PaintBean)ContextLookup.lookup( "paintBean", FacesContext.getCurrentInstance() );
			if( pb != null )
			{
				if( pb.getLayoutMethod().equals( Settings.DOCUMENT_TOPIC_MDS_LAYOUT ) )
				{
					pb.runLayout();
				}
			}
		}
	}

	/**
	 * Toggle use stress minimization.
	 */
	public void toggleUseStressMinimization()
	{
		setUseStressMinimization( !isUseStressMinimization() );
	}

	/**
	 * Update topic sizes.
	 */
	public void updateTopicSizes()
	{
		if( authorCollapseHistoryIndex == -1 )
		{
			DNVGraph graph = getGraph();
			int docId;
			int topicId;
			Float temp;
			if( graph != null )
			{
				int count = 0;
				for( DNVEntity topicNode : graph.getNodesByType( 0, "topic" ).values() )
				{
					count = 0;
					topicId = Integer.parseInt( topicNode.getBbId().substring( 1 ) );
					for( DNVEntity docNode : graph.getNodesByType( 0, "document" ).values() )
					{
						try
						{
							docId = Integer.parseInt( docNode.getBbId().substring( 3 ) );
							temp = docTopicCount.get( docId + "->" + topicId );
							if( temp != null )
							{
								count += temp;
							}
						}
						catch( NumberFormatException nfe )
						{}
					}

					topicNode.setProperty( "weight", "" + count );
				}
				CreateDNVFromDocumentTopics.scaleTopicNodes( graph, labelScaler );
			}
		}
	}

	/** The department field expanded. */
	private boolean departmentFieldExpanded = false;

	/**
	 * Checks if is department field expanded.
	 * 
	 * @return true, if is department field expanded
	 */
	public boolean isDepartmentFieldExpanded()
	{
		return departmentFieldExpanded;
	}

	/**
	 * Sets the department field expanded.
	 * 
	 * @param departmentFieldExpanded
	 *            the new department field expanded
	 */
	public void setDepartmentFieldExpanded( boolean departmentFieldExpanded )
	{
		this.departmentFieldExpanded = departmentFieldExpanded;
	}

	/**
	 * Collapse department field.
	 */
	public void collapseDepartmentField()
	{
		setDepartmentFieldExpanded( false );
	}

	/**
	 * Expand department field.
	 */
	public void expandDepartmentField()
	{
		setDepartmentFieldExpanded( true );
	}

	/** The field color expanded. */
	private boolean fieldColorExpanded = true;

	/**
	 * Checks if is field color expanded.
	 * 
	 * @return true, if is field color expanded
	 */
	public boolean isFieldColorExpanded()
	{
		return fieldColorExpanded;
	}

	/**
	 * Sets the field color expanded.
	 * 
	 * @param fieldColorExpanded
	 *            the new field color expanded
	 */
	public void setFieldColorExpanded( boolean fieldColorExpanded )
	{
		this.fieldColorExpanded = fieldColorExpanded;
	}

	/**
	 * Collapse field color.
	 */
	public void collapseFieldColor()
	{
		setFieldColorExpanded( false );
	}

	/**
	 * Expand field color.
	 */
	public void expandFieldColor()
	{
		setFieldColorExpanded( true );
	}

	/**
	 * Save colors and fields.
	 */
	public void saveColorsAndFields()
	{
		File file = new File( selectedFolder, "departmentToColorMapping.txt" );
		try
		{
			Writer br = new BufferedWriter( new FileWriter( file ) );
			br.write( "All Departments:,Field Mapping:,Color Mapping\n" );
			for( String department : departmentToColor.keySet() )
			{
				br.write( "\"" + department + "\",\"" + departmentToField.get( department ) + "\",\"" + departmentToColor.get( department ) + "\"\n" );
			}

			br.close();

		}
		catch( IOException ioe )
		{
			ioe.printStackTrace();
		}
	}

	/**
	 * Reset colors and fields.
	 */
	public void resetColorsAndFields()
	{
		readKeyToColorFile();
		for( String author : authorToColor.keySet() )
		{
			authorToColor.put( author, departmentToColor.get( authorToDepartment.get( author ) ) );
		}

		CreateDNVFromDocumentTopics.updateColors( getGraph(), authorToColor, colorTopicEdgesBasedOnDocuments );

	}

	/**
	 * Sets the color topics based on documents.
	 * 
	 * @param colorTopicsBasedOnDocuments
	 *            the new color topics based on documents
	 */
	public void setColorTopicsBasedOnDocuments( boolean colorTopicsBasedOnDocuments )
	{
		this.colorTopicsBasedOnDocuments = colorTopicsBasedOnDocuments;
		if( colorTopicsBasedOnDocuments )
		{
			CreateDNVFromDocumentTopics.updateColors( getGraph(), authorToColor, colorTopicEdgesBasedOnDocuments );
		}
		else
		{
			updateTopicNodeColor();
		}
	}

	/**
	 * Checks if is color topics based on documents.
	 * 
	 * @return true, if is color topics based on documents
	 */
	public boolean isColorTopicsBasedOnDocuments()
	{
		return colorTopicsBasedOnDocuments;
	}

	/**
	 * Toggle color topics based on documents.
	 */
	public void toggleColorTopicsBasedOnDocuments()
	{
		setColorTopicsBasedOnDocuments( !isColorTopicsBasedOnDocuments() );
	}

	/**
	 * Determine expertise and set colors.
	 */
	public void determineExpertiseAndSetColors()
	{
		DetermineAuthorExpertise.determineExpertise( selectedFolder, getGraph(), "departmentToColorMapping.txt" );
		resetColorsAndFields();
	}

	/** The extracted property. */
	private String extractedProperty = "";

	/**
	 * Gets the extracted property.
	 * 
	 * @return the extracted property
	 */
	public String getExtractedProperty()
	{
		return extractedProperty;
	}

	/**
	 * Sets the extracted property.
	 * 
	 * @param extractedProperty
	 *            the new extracted property
	 */
	public void setExtractedProperty( String extractedProperty )
	{
		this.extractedProperty = extractedProperty;
	}

	/**
	 * Extract property.
	 */
	public void extractProperty()
	{
		addToHistory();
		System.out.println( "Extracting property : " + extractedProperty );
		List<DNVNode> newNodes = new LinkedList<DNVNode>();
		float maxRadius = 0;
		int maxLabelSize = 0;
		if( extractedProperty != null && !extractedProperty.equals( "" ) )
		{
			DNVGraph graph = getGraph();
			if( graph != null )
			{
				synchronized( graph.getExtractableProperties() )
				{
					Map<String, Map<Integer, DNVNode>> values = graph.getExtractableProperties().get( extractedProperty );
					if( values != null )
					{
						for( String value : values.keySet() )
						{
							// ignore empty strings
							if( !value.trim().equals( "" ) )
							{
								DNVNode node = (DNVNode)graph.getNodeByBbId( value );
								if( node == null )
								{
									node = new DNVNode( graph );
									node.setLabel( value );
									node.setBbId( value );
									node.setForceLabel( true );
									graph.addNode( 0, node );
								}
								String color = authorToColor.get( value );
								if( color == null )
								{
									color = departmentToColor.get( value );
									if( color == null )
									{
										color = extractedPropertyColor;
									}
								}
								node.setColor( color );
								node.setAlpha( documentAlpha );
								Vector2D position = node.getPosition();
								int counter = 0;
								if( values.get( value ) != null )
								{
									for( DNVNode neighbor : values.get( value ).values() )
									{
										if( graph.containsNode( neighbor ) )
										{
											counter++;
											DNVEdge edge = new DNVEdge( neighbor, node, graph );
											edge.setLabel( extractedProperty );
											edge.setForceLabel( false );
											edge.setColor( neighbor.getColor() );
											graph.addNode( 0, edge );
											position.add( neighbor.getPosition() );
										}
									}
								}
								if( counter == 0 )
								{
									graph.removeNode( node );
								}
								else
								{
									System.out.println( node.getLabel() + " has " + counter + " neighbors." );
									System.out.println( "Setting radius for " + node.getLabel() + " as " + ( 1 + counter * 0.1f ) );
									node.setRadius( 1 + counter * 0.1f );
									System.out.println( "Setting label size for " + node.getLabel() + " as " + ( 12 + counter * 0.1 ) );
									node.setLabelSize( (int)Math.round( 12 + counter * 0.1 ) );
									position.dotProduct( 1.0f / (float)counter );
									node.setPosition( position );
									maxRadius = Math.max( node.getRadius(), maxRadius );
									maxLabelSize = Math.max( node.getLabelSize(), maxLabelSize );
									newNodes.add( node );
								}
							}
						}
					}
				}

				for( DNVNode newNode : newNodes )
				{
					newNode.setRadius( 1 + 2.0f * newNode.getRadius() / maxRadius );
					newNode.setLabelSize( Math.round( CreateDNVFromDocumentTopics.MIN_LABEL_SIZE + newNode.getRadius() * labelScaler ) );
				}

				PaintBean pb = getPaintBean();
				if( pb != null )
				{
					pb.updateNodeSize();
					pb.runLayout();
				}
			}
		}
	}

	/** The extractable properties list. */
	private List<SelectItem> extractablePropertiesList = new ArrayList<SelectItem>();

	/**
	 * Gets the extractable properties list.
	 * 
	 * @return the extractable properties list
	 */
	public List<SelectItem> getExtractablePropertiesList()
	{
		return extractablePropertiesList;
	}

	/**
	 * Sets the extractable properties list.
	 * 
	 * @param extractablePropertiesList
	 *            the new extractable properties list
	 */
	public void setExtractablePropertiesList( List<SelectItem> extractablePropertiesList )
	{
		this.extractablePropertiesList = extractablePropertiesList;
	}

	/**
	 * Refresh extractable properties list.
	 */
	public void refreshExtractablePropertiesList()
	{
		buildExtractablePropertiesList( getGraph() );
	}

	/**
	 * Builds the extractable properties list.
	 * 
	 * @param graph
	 *            the graph
	 */
	public void buildExtractablePropertiesList( DNVGraph graph )
	{
		synchronized( extractablePropertiesList )
		{
			extractablePropertiesList.clear();
			SelectItem extractablePropertyItem;
			String label;
			if( graph != null )
			{
				synchronized( graph.getExtractableProperties() )
				{
					for( String property : graph.getExtractableProperties().keySet() )
					{
						label = property;
						label += " (" + graph.getExtractableProperties().get( property ).size() + " unique values)";
						// System.out.println( "Adding " + label + " to list."
						// );
						extractablePropertyItem = new SelectItem( property, label );
						extractablePropertiesList.add( extractablePropertyItem );
					}
				}
			}
		}
	}

	/**
	 * Checks if is show extractable properties.
	 * 
	 * @return true, if is show extractable properties
	 */
	public boolean isShowExtractableProperties()
	{
		return extractablePropertiesList.size() > 0;
	}

	/**
	 * Gets the extracted property color.
	 * 
	 * @return the extracted property color
	 */
	public String getExtractedPropertyColor()
	{
		return extractedPropertyColor;
	}

	/**
	 * Sets the extracted property color.
	 * 
	 * @param extractedPropertyColor
	 *            the new extracted property color
	 */
	public void setExtractedPropertyColor( String extractedPropertyColor )
	{
		this.extractedPropertyColor = extractedPropertyColor;
	}

	/**
	 * Checks if is use mpi.
	 * 
	 * @return true, if is use mpi
	 */
	public boolean isUseMPI()
	{
		return useMPI;
	}

	/**
	 * Sets the use mpi.
	 * 
	 * @param useMPI
	 *            the new use mpi
	 */
	public void setUseMPI( boolean useMPI )
	{
		this.useMPI = useMPI;
	}

	/**
	 * Toggle use mpi.
	 */
	public void toggleUseMPI()
	{
		setUseMPI( !isUseMPI() );
	}

	/**
	 * Gets the number of processors.
	 * 
	 * @return the number of processors
	 */
	public int getNumberOfProcessors()
	{
		return numberOfProcessors;
	}

	/**
	 * Sets the number of processors.
	 * 
	 * @param numberOfProcessors
	 *            the new number of processors
	 */
	public void setNumberOfProcessors( int numberOfProcessors )
	{
		this.numberOfProcessors = numberOfProcessors;
	}

	/**
	 * Sets the only topic model visible documents.
	 * 
	 * @param onlyTopicModelVisibleDocuments
	 *            the new only topic model visible documents
	 */
	public void setOnlyTopicModelVisibleDocuments( boolean onlyTopicModelVisibleDocuments )
	{
		this.onlyTopicModelVisibleDocuments = onlyTopicModelVisibleDocuments;
	}

	/**
	 * Checks if is only topic model visible documents.
	 * 
	 * @return true, if is only topic model visible documents
	 */
	public boolean isOnlyTopicModelVisibleDocuments()
	{
		return onlyTopicModelVisibleDocuments;
	}

	/**
	 * Toggle only topic model visible documents.
	 */
	public void toggleOnlyTopicModelVisibleDocuments()
	{
		setOnlyTopicModelVisibleDocuments( !isOnlyTopicModelVisibleDocuments() );
	}

	/**
	 * Sets the hide label background for documents.
	 * 
	 * @param hideLabelBackgroundForDocuments
	 *            the new hide label background for documents
	 */
	public void setHideLabelBackgroundForDocuments( boolean hideLabelBackgroundForDocuments )
	{
		if( this.hideLabelBackgroundForDocuments != hideLabelBackgroundForDocuments )
		{
			this.hideLabelBackgroundForDocuments = hideLabelBackgroundForDocuments;
			DNVGraph graph = getGraph();
			if( graph != null )
			{
				for( DNVEntity doc : graph.getNodesByType( 0, "document" ).values() )
				{
					( (DNVNode)doc ).setHideLabelBackground( hideLabelBackgroundForDocuments );
				}
			}
		}
	}

	/**
	 * Checks if is hide label background for documents.
	 * 
	 * @return true, if is hide label background for documents
	 */
	public boolean isHideLabelBackgroundForDocuments()
	{
		return hideLabelBackgroundForDocuments;
	}

	/**
	 * Toggle hide label background for documents.
	 */
	public void toggleHideLabelBackgroundForDocuments()
	{
		setHideLabelBackgroundForDocuments( !isHideLabelBackgroundForDocuments() );
	}

	/**
	 * Sets the use curved labels for documents.
	 * 
	 * @param useCurvedLabelsForDocuments
	 *            the new use curved labels for documents
	 */
	public void setUseCurvedLabelsForDocuments( boolean useCurvedLabelsForDocuments )
	{
		if( this.useCurvedLabelsForDocuments != useCurvedLabelsForDocuments )
		{
			this.useCurvedLabelsForDocuments = useCurvedLabelsForDocuments;
			DNVGraph graph = getGraph();
			if( graph != null )
			{
				for( DNVEntity doc : graph.getNodesByType( 0, "document" ).values() )
				{
					( (DNVNode)doc ).setCurvedLabel( useCurvedLabelsForDocuments );
				}
			}
		}
	}

	/**
	 * Checks if is use curved labels for documents.
	 * 
	 * @return true, if is use curved labels for documents
	 */
	public boolean isUseCurvedLabelsForDocuments()
	{
		return useCurvedLabelsForDocuments;
	}

	/**
	 * Toggle use curved labels for documents.
	 */
	public void toggleUseCurvedLabelsForDocuments()
	{
		setUseCurvedLabelsForDocuments( !isUseCurvedLabelsForDocuments() );
	}
}