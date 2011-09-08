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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import net.wigis.graph.data.utilities.FileOperations;
import net.wigis.graph.data.utilities.StringUtils;
import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVEntity;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.SubGraph;
import net.wigis.graph.dnv.clustering.ConnectedClustering;
import net.wigis.graph.dnv.clustering.KMostConnected;
import net.wigis.graph.dnv.clustering.SolarSystemClustering;
import net.wigis.graph.dnv.clustering.StructuralEquivalenceClustering;
import net.wigis.graph.dnv.geometry.Text;
import net.wigis.graph.dnv.interaction.implementations.InterpolationMethod;
import net.wigis.graph.dnv.interaction.interfaces.InteractionInterface;
import net.wigis.graph.dnv.layout.implementations.CircularLayout;
import net.wigis.graph.dnv.layout.implementations.RandomLayout;
import net.wigis.graph.dnv.layout.interfaces.CenterPositionFixedLayoutInterface;
import net.wigis.graph.dnv.layout.interfaces.LayoutInterface;
import net.wigis.graph.dnv.layout.interfaces.MDSLayoutInterface;
import net.wigis.graph.dnv.layout.interfaces.RadiusRestrictedLayoutInterface;
import net.wigis.graph.dnv.layout.interfaces.RecommendationLayoutInterface;
import net.wigis.graph.dnv.layout.interfaces.SimpleLayoutInterface;
import net.wigis.graph.dnv.layout.interfaces.SortingLayoutInterface;
import net.wigis.graph.dnv.layout.interfaces.SpaceRestrictedLayoutInterface;
import net.wigis.graph.dnv.layout.interfaces.TimeLimitedLayoutInterface;
import net.wigis.graph.dnv.utilities.DescendingSort;
import net.wigis.graph.dnv.utilities.GenerateHistogramOfConnectivity;
import net.wigis.graph.dnv.utilities.GraphFunctions;
import net.wigis.graph.dnv.utilities.NumberOfNeighborSort;
import net.wigis.graph.dnv.utilities.Timer;
import net.wigis.graph.dnv.utilities.Vector2D;
import net.wigis.graph.dnv.utilities.Vector3D;
import net.wigis.settings.Settings;
import net.wigis.web.ContextLookup;
import net.wigis.web.Request;

// TODO: Auto-generated Javadoc
/**
 * The Class PaintBean.
 * 
 * @author Brynjar Gretarsson
 */
public class PaintBean
{

	/** The graph. */
	private DNVGraph graph = new DNVGraph();

	/** The sub graph. */
	private SubGraph subGraph = new SubGraph( graph, 0 );

	/** The selected file. */
	private String selectedFile = Settings.GRAPHS_PATH + Settings.DEFAULT_GRAPH;

	/** The level. */
	private int level = 0;

	/** The selected node. */
	private DNVNode selectedNode = null;

	/** The selected edge. */
	private DNVEdge selectedEdge = null;

	/** The use only materialized list. */
	private boolean useOnlyMaterializedList = true;

	/** The refresh overview. */
	private boolean refreshOverview = false;

	/** The sort nodes. */
	private boolean sortNodes = false;

	/** The highlight neighbors. */
	private boolean highlightNeighbors = Settings.DEFAULT_HIGHLIGHT_NEIGHBORS;

	private Vector3D neighborHighlightColor = null;

	/** The highlight edges. */
	private boolean highlightEdges = true;

	/** The width. */
	private int width = Settings.WIDTH;

	/** The height. */
	private int height = Settings.HEIGHT;

	/** Defines the left border of the viewed portion within the (0,0) to (1,1) space*/
	private double minX = 0;

	/** Defines the top border of the viewed portion within the (0,0) to (1,1) space*/
	private double minY = 0;

	/** defines the right border of the viewed portion within the (0,0) to (1,1) space*/
	private double maxX = 1;

	/** Defines the bottom border of the viewed portion within the (0,0) to (1,1) space*/
	private double maxY = 1;

	/** The global min x. */
	private double globalMinX = 0;

	/** The global max x. */
	private double globalMaxX = 0;

	/** The global min y. */
	private double globalMinY = 0;

	/** The global max y. */
	private double globalMaxY = 0;

	/** The js threshold. */
	private double jsThreshold = Settings.DEFAULT_JS_THRESHOLD;

	// Defines the how local the effects of interaction are
	/** The curve min. */
	private double curveMin = 0;

	/** The curve max. */
	private double curveMax = 1;

	/** The number affected. */
	private double numberAffected = 5;

	/** The last used number affected. */
	private double lastUsedNumberAffected = -1;

	// Defines the minimum size of nodes
	/** The node size. */
	private double nodeSize = 5;

	/** The scale nodes on zoom. */
	private boolean scaleNodesOnZoom = true;

	/** The edge thickness. */
	private double edgeThickness = 1;

	/** The edge color. */
	private double edgeColor = 0.4;

	/** The panel width. */
	private int panelWidth = 233;

	/** The show labels. */
	private boolean showLabels = Settings.DEFAULT_SHOW_LABELS;

	/** The curved labels. */
	private boolean curvedLabels = false;

	/** The outlined labels. */
	private boolean outlinedLabels = true;

	/** The bold labels. */
	private boolean boldLabels = true;

	/** The draw label box. */
	private boolean drawLabelBox = true;

	/** The hide conflicting labels. */
	private boolean hideConflictingLabels = Settings.DEFAULT_HIDE_CONFLICTING_LABELS;

	/** The draw watermark. */
	private boolean drawWatermark = true;
	
	/** Determines if should draw a box around the neighbors of selected nodes */
	private boolean drawNeighborHighlight = false;

	private boolean drawNumberOfNodesInBox = true;

	private boolean drawAllNeighborsHighlight = false;

	private boolean drawNeighborHighlightAsBoxes = true;

	private boolean alignBoxInfoRelativeToBox = true;

	private boolean drawAxis = false;

	/** The fade factor. */
	private float fadeFactor = 5.0f;

	/** The label size. */
	private double labelSize = Settings.DEFAULT_LABEL_SIZE;

	/** The scale labels. */
	private boolean scaleLabels = false;

	/** The max label length. */
	private int maxLabelLength = 20;

	/** The curved label angle. */
	private int curvedLabelAngle = 130;

	/** The show icons. */
	private boolean showIcons = Settings.DEFAULT_SHOW_ICONS;
	
	/** Show a big reload button below the graph */
	private boolean showReloadButton = Settings.DEFAULT_SHOW_RELOAD_BUTTON;

	/** The cooling factor. */
	private float coolingFactor = 0.1f;

	/** The layout all levels. */
	private boolean layoutAllLevels = false;

	/** The cluster before layout. */
	private boolean clusterBeforeLayout = false;

	/** The recommendation circle. */
	private boolean recommendationCircle = false;

	/** The fr use edge resting distance. */
	private boolean frUseEdgeRestingDistance = false;

	/** The fr use node size. */
	private boolean frUseNodeSize = false;
	
	private boolean enableAnimation = true;

	// Keep the state of the settings panels
	/** The selected node details expanded. */
	private boolean selectedNodeDetailsExpanded = Settings.DEFAULT_SELECTED_NODE_DETAILS_EXPANDED;

	/** The search expanded. */
	private boolean searchExpanded = true;

	/** The appearance expanded. */
	private boolean appearanceExpanded = false;

	/** The labels expanded. */
	private boolean labelsExpanded = false;

	/** The client server expanded. */
	private boolean clientServerExpanded = false;

	/** The client side expanded. */
	private boolean clientSideExpanded = false;

	/** The server side expanded. */
	private boolean serverSideExpanded = false;

	/** The clustering expanded. */
	private boolean clusteringExpanded = false;

	/** The interaction expanded. */
	private boolean interactionExpanded = false;

	/** The time expanded. */
	private boolean timeExpanded = false;

	/** The data expanded. */
	private boolean dataExpanded = false;

	/** The facebook expanded. */
	private boolean facebookExpanded = false;

	/** The white space buffer. */
	private double whiteSpaceBuffer = 0.08;

	/** The seconds per animation. */
	private float secondsPerAnimation = 5;

	/** The scale positions. */
	private boolean scalePositions = false;

	/** The min time. */
	private int minTime = 0;

	/** The max time. */
	private int maxTime = 0;

	/** The all times. */
	private List<Long> allTimes = new ArrayList<Long>();

	// private boolean hasBeenDisplayed = false;

	/** The interpolation labels. */
	private boolean interpolationLabels = Settings.INTERPOLATION_LABELING;

	/** The max layout time. */
	private double maxLayoutTime = 5;

	/** The number of clusters. */
	private int numberOfClusters = 1;

	/** The layout method list. */
	private List<SelectItem> layoutMethodList = new ArrayList<SelectItem>();

	private Map<String,LayoutInterface> layoutMethods = new HashMap<String,LayoutInterface>();
	
	/** The layout method. */	
	private LayoutInterface layoutMethod = Settings.LAYOUT_ALGORITHMS[0];

	/** The circular layout sort list. */
	private List<SelectItem> circularLayoutSortList = new ArrayList<SelectItem>();

	/** The circular layout sort. */
	private String circularLayoutSort = CircularLayout.RANDOM_SORT;

	/** The clustering method list. */
	private List<SelectItem> clusteringMethodList = new ArrayList<SelectItem>();

	/** The clustering method. */
	private String clusteringMethod = Settings.CLUSTERING_ALGORITHMS[0];

	/** The interaction method list. */
	private List<SelectItem> interactionMethodList = new ArrayList<SelectItem>();
	private Map<String,InteractionInterface> interactionMethods = new HashMap<String,InteractionInterface>();

	/** The interaction method. */
	private InteractionInterface interactionMethod = Settings.INTERACTION_ALGORITHMS[0];

	/** The interpolation method use actual edge distance. */
	private boolean interpolationMethodUseActualEdgeDistance = false;

	/** The interpolation method use whole graph. */
	private boolean interpolationMethodUseWholeGraph = false;

	// show error messages for 10 seconds.
	/** The Constant ERROR_MSG_LIFE. */
	private static final int ERROR_MSG_LIFE = 5000;

	/** The Constant MAX_DRAG_NODE. */
	public static final int MAX_DRAG_NODE = 10;

	/** The Constant STATUS_MSG_LIFE. */
	private static final long STATUS_MSG_LIFE = 5000;

	/** The Constant UNSAVED_CHANGES_MSG. */
	public static final String UNSAVED_CHANGES_MSG = "You have unsaved changes";

	/** The error time map. */
	private Map<String, Long> errorTimeMap = new Hashtable<String, Long>();

	/** The error override map. */
	private Map<String, Boolean> errorOverrideMap = new Hashtable<String, Boolean>();

	/** The error msg list. */
	private ArrayList<String> errorMsgList = new ArrayList<String>();

	/** The status msg map. */
	private Map<String, Long> statusMsgMap = new Hashtable<String, Long>();

	/** The status msg list. */
	private ArrayList<String> statusMsgList = new ArrayList<String>();

	/** The graph changed. */
	private boolean graphChanged = false;
	
	/** Transition back when mouse released (only used in desktop version so far) */
	private boolean transitionBack = false;
	
	private boolean noAlpha = false;

	private boolean fixedZoom = false;

	/**
	 * Checks if is interpolation method use whole graph.
	 * 
	 * @return true, if is interpolation method use whole graph
	 */
	public boolean isInterpolationMethodUseWholeGraph()
	{
		Boolean interpolationMethodUseWholeGraph = Request.getBooleanParameter( "wholeGraph" );
		if( interpolationMethodUseWholeGraph != null )
		{
			this.interpolationMethodUseWholeGraph = interpolationMethodUseWholeGraph;
		}

		return this.interpolationMethodUseWholeGraph;
	}

	/**
	 * Sets the interpolation method use whole graph.
	 * 
	 * @param interpolationMethodUseWholeGraph
	 *            the new interpolation method use whole graph
	 */
	public void setInterpolationMethodUseWholeGraph( boolean interpolationMethodUseWholeGraph )
	{
		this.interpolationMethodUseWholeGraph = interpolationMethodUseWholeGraph;
	}

	/** The ajax components. */
	private String ajaxComponents = "viewDetailedPanel, overviewPanel, mainJSPanel, graphInfo, zoomStatus, statsPanel";

	/**
	 * Paint. 
	 * 
	 * @param out
	 *            the out
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @param overview
	 *            the overview
	 * @param rendering
	 *            the rendering
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void paint( OutputStream out, int width, int height, boolean overview, int rendering ) throws IOException
	{
		Timer createImageTimer = new Timer( Timer.MILLISECONDS );
		Timer writeImageTimer = new Timer( Timer.MILLISECONDS );
		createImageTimer.setStart();
		if( width == 0 )
		{
			width = getWidthInt();
		}
		if( height == 0 )
		{
			height = getHeightInt();
		}
		BufferedImage img = new BufferedImage( width, height, rendering );
		createImageTimer.setEnd();
		Graphics2D graphics2D = img.createGraphics();
		paint( graphics2D, width, height, overview, sortNodes && ( rendering != BufferedImage.TYPE_BYTE_INDEXED ) );
		writeImageTimer.setStart();
		try
		{
			ImageIO.write( img, "gif", out );
		}
		catch( Exception e )
		{}
		out.flush();
		writeImageTimer.setEnd();
		if( Settings.DEBUG && !overview )
		{
			System.out.println( "Creating image took " + createImageTimer.getLastSegment( Timer.SECONDS ) + " seconds." );
			System.out.println( "Writing image took  " + writeImageTimer.getLastSegment( Timer.SECONDS ) + " seconds." );
		}
	}

	/**
	 * Paint.
	 * 
	 * @param g2d
	 *            the g2d
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @param overview
	 *            the overview
	 * @param sortNodes
	 *            the sort nodes
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void paint( Graphics2D g2d, int width, int height, boolean overview, boolean sortNodes ) throws IOException
	{
		long startTime = System.currentTimeMillis();
		synchronized( graph )
		{
			if( graph.hasStoredPosition() )
			{
				graph.interpolateToNewPosition( level, getSecondsPerAnimation() );
			}

			int level = (int)getLevel();

			if( width == -1 )
				width = (int)getWidth();

			if( height == -1 )
				height = (int)getHeight();

			double minX = getMinX();
			double minY = getMinY();
			double maxX = getMaxX();
			double maxY = getMaxY();

			findSubGraph();
			SubGraph subGraph = this.subGraph;
			int edgeThickness = (int)this.edgeThickness;
			if( overview )
			{
				edgeThickness = 1;
				level = 0;
				minX = 0;
				minY = 0;
				maxX = 1;
				maxY = 1;
				subGraph = SubGraphFinder.getSubGraphWithin( graph, 0, minX, minY, maxX, maxY, whiteSpaceBuffer );
			}

			g2d.setColor( Color.white );
			g2d.fillRect( 0, 0, width, height );

			Timer globalBoundariesTimer = new Timer( Timer.MILLISECONDS );

			if( isRenderImage() || overview )
			{
				if( graph != null )
				{
					globalBoundariesTimer.setStart();
					refreshGlobalBoundaries( level );
					globalBoundariesTimer.setEnd();
					boolean drawLabels = ( !overview && showLabels );
					Timer drawTimer = new Timer( Timer.MILLISECONDS );
					drawTimer.setStart();
					int maxDistanceToHighlight = (int)numberAffected;
					if( !(interactionMethod instanceof InterpolationMethod) || selectedNode == null )
					{
						// Don't highlight nodes if no nodes is selected or not
						// using interpolation method
						maxDistanceToHighlight = -1;
					}
					else if( interpolationMethodUseWholeGraph )
					{
						// Highlight all nodes if useWholeGraph
						maxDistanceToHighlight = graph.getNodes( level ).size();
					}
					ImageRenderer.drawGraph( subGraph, g2d, this, (int)getNodeSize(), width, height, minX, minY, maxX, maxY, width / getWidth(),
							edgeThickness, edgeColor, drawLabels, curvedLabels, outlinedLabels, labelSize, interpolationLabels,
							showSearchSelectedLabels, showIcons, globalMinX, globalMaxX, globalMinY, globalMaxY, overview, level, scaleNodesOnZoom,
							sortNodes, highlightNeighbors, highlightEdges, maxLabelLength, curvedLabelAngle, scaleLabels, hideConflictingLabels,
							drawLabelBox, boldLabels, fadeFactor, maxNumberOfSelectedLabels, maxDistanceToHighlight, drawWatermark,
							drawNeighborHighlight, drawNumberOfNodesInBox, drawNeighborHighlightAsBoxes, drawAllNeighborsHighlight,
							alignBoxInfoRelativeToBox, timeText, drawAxis, neighborHighlightColor );
					drawTimer.setEnd();
					if( Settings.DEBUG && !overview )
					{
						System.out.println( "Refreshing global boundaries took " + globalBoundariesTimer.getLastSegment( Timer.SECONDS )
								+ " seconds." );
						System.out.println( "Pure drawing took " + drawTimer.getLastSegment( Timer.SECONDS ) + " seconds." );
					}
				}
			}
		}
		printTime( "paint(graph2d)", startTime );
	}

	/**
	 * Gets the max label.
	 * 
	 * @return the max label
	 */
	public String getMaxLabel()
	{
		return graph.getMaxLabel();
	}

	/**
	 * Save graph.
	 */
	public void saveGraph()
	{
		Timer timer = new Timer( Timer.MILLISECONDS );
		timer.setStart();
		graph.writeGraph( selectedFile );
		timer.setEnd();
		System.out.println( "Saving " + selectedFile + " took " + timer.getLastSegment( Timer.SECONDS ) + " seconds." );
		GraphsBean gb = (GraphsBean)ContextLookup.lookup( "graphsBean", FacesContext.getCurrentInstance() );
		if( gb != null )
		{
			gb.buildFileList();
		}
	}

	/**
	 * Instantiates a new paint bean.
	 */
	public PaintBean()
	{
		long startTime = System.currentTimeMillis();
		isInterpolationMethodUseWholeGraph();
		getNodeSize();
		loadGraph();
		buildClusteringMethodList();
		buildLayoutMethodList();
		buildCircularLayoutSortList();
		buildInteractionMethodList();
		printTime( "PainBean()", startTime );
	}
	
	/**
	 * Load graph.
	 */
	public void loadGraph()
	{
		long startTime = System.currentTimeMillis();
		synchronized( graph )
		{
			Timer loadTimer = new Timer( Timer.MILLISECONDS );
			loadTimer.setStart();
			graph = new DNVGraph( getSelectedFile() );
			graph.setPaintBean( this );
			loadTimer.setEnd();
			System.out.println( "Loading '" + selectedFile + "' took " + loadTimer.getLastSegment( Timer.SECONDS ) + " seconds." );
			int maxLevel = graph.getMaxLevel();
			if( level > maxLevel )
				level = maxLevel;

			resetNumberOfClusters();
			updateNumberAffected();
			updateNodeSize();
			resetSearchResults();
			clearHistory();

			if( fixedZoom )
			{
				fixedZoom = false;
				refreshGlobalBoundaries( level );
				fixedZoom = true;
			}

			allTimes = new ArrayList<Long>( graph.getTimes( level ) );
			Collections.sort( allTimes );
			setMinTime( 0 );
			setMaxTime( Math.max( 0, allTimes.size() - 1 ) );
			if( !isShowTimeSelector() )
			{
				timeText = null;
			}

			findSubGraph();
//			if( subGraph.getNodesList().size() > 1000 )
//			{
//				setShowLabels( false );
//			}
			
			resetZoomWindow();
		}
		printTime( "loadGrpah()", startTime );
	}

	/**
	 * Sets the graph.
	 * 
	 * @param graph
	 *            the new graph
	 */
	public void setGraph( DNVGraph graph )
	{
		this.graph = graph;
		graph.setPaintBean( this );
		setSelectedNode( null, false );
		setSelectedEdge( null, false );
		int maxLevel = graph.getMaxLevel();
		if( level > maxLevel )
			level = maxLevel;

		resetNumberOfClusters();
		updateNumberAffected();
		updateNodeSize();

		allTimes = new ArrayList<Long>( graph.getTimes( level ) );
		Collections.sort( allTimes );
		setMinTime( 0 );
		setMaxTime( allTimes.size() - 1 );

		findSubGraph();
		if( subGraph.getNodesList().size() > 1000 )
		{
			setShowLabels( false );
		}
	}

	/**
	 * Update node size.
	 */
	public void updateNodeSize()
	{
		if( graph.getGraphSize( 0 ) > 0 )
		{
			nodeSize = Math.sqrt( getWidth() * getHeight() / graph.getGraphSize( 0 ) ) * 0.2;
			if( nodeSize < 1 )
				nodeSize = 1;
			else if( nodeSize > 30 )
				nodeSize = 30;

//			System.out.println( "Node size set to " + nodeSize );
		}
	}

	/**
	 * Reset number of clusters.
	 */
	public void resetNumberOfClusters()
	{
		numberOfClusters = (int)( graph.getGraphSize( level ) / 3.0 );
		clusterBeforeLayout = true;
	}

	/**
	 * Gets the max level.
	 * 
	 * @return the max level
	 */
	public double getMaxLevel()
	{
		return graph.getMaxLevel();
	}

	/**
	 * Gets the number of nodes.
	 * 
	 * @return the number of nodes
	 */
	public int getNumberOfNodes()
	{
		return graph.getGraphSize( level );
	}

	/**
	 * Gets the number of edges.
	 * 
	 * @return the number of edges
	 */
	public int getNumberOfEdges()
	{
		return graph.getEdges( 0 ).size();
	}

	/**
	 * Gets the objects.
	 * 
	 * @return the objects
	 * @throws MalformedURLException
	 *             the malformed url exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public String getObjects() throws MalformedURLException, IOException
	{
		String value = "";
		synchronized( graph )
		{
			refreshGlobalBoundaries( 0 );

			findSubGraph();

			try
			{
				if( isRenderJS() )
				{
					value = "getWindowPosition();"; // needed for calculation of
					// label positions
					Collection<DNVNode> nodes = subGraph.getNodes().values();
					HashMap<Integer, Integer> realIdToId = new HashMap<Integer, Integer>();
					DNVNode tempNode = new DNVNode( graph );
					Vector2D position;
					int radius = ImageRenderer.getNodeWidth( getNodeSize(), getMinX(), getMaxX(), 1 );
					int j = 0;
					String imageFile;
					int tempRadius;
					String label;
					for( Iterator<DNVNode> i = nodes.iterator(); i.hasNext(); )
					{
						tempNode = i.next();
						label = tempNode.getLabel( interpolationLabels ).replaceAll( "'", "`" ).replaceAll( "\n", " " ).replaceAll( "\r", " " )
								.trim();
						position = ImageRenderer.transformPosition( globalMinX, globalMaxX, globalMinY, globalMaxY, getMinX(), getMaxX(), getMinY(),
								getMaxY(), getWidth(), getHeight(), tempNode.getPosition( true ) );
						tempRadius = (int)( radius * tempNode.getRadius() );

						imageFile = ImageRenderer.getURL( tempRadius, tempNode.getIcon(), tempNode.getColor(), showIcons, tempNode.isSelected(),
								tempNode.getId() );
						if( isShowLabels() && isCurvedLabels() )
						{
							imageFile += "&l=" + label + "&ls=" + labelSize + "&sl=" + curvedLabels + "&o=" + outlinedLabels;
						}
						value += "addNode(" + (int)position.getX() + "," + (int)position.getY() + "," + tempRadius + "," + tempNode.getId() + "," + j
								+ ",'" + imageFile + "','" + label + "'," + ( showLabels && !curvedLabels ) + "," + tempNode.isSelected() + ");";
						realIdToId.put( tempNode.getId(), j );
						j++;
					}
					value += "nNodes = " + j + ";";

					Collection<DNVEdge> edges = subGraph.getEdges().values();
					DNVEdge tempEdge;
					DNVNode fromNode;
					DNVNode toNode;
					int fromId;
					int toId;
					j = 0;
					position = new Vector2D( tempNode.getPosition( true ) );
					Vector2D originalPosition = ImageRenderer.transformPosition( globalMinX, globalMaxX, globalMinY, globalMaxY, getMinX(),
							getMaxX(), getMinY(), getMaxY(), getWidth(), getHeight(), position );
					position.setX( position.getX() + DNVEdge.DEFAULT_RESTING_DISTANCE );
					position = ImageRenderer.transformPosition( globalMinX, globalMaxX, globalMinY, globalMaxY, getMinX(), getMaxX(), getMinY(),
							getMaxY(), getWidth(), getHeight(), position );

					position = position.subtract( originalPosition );
					// float ratio = position.length() /
					// DNVEdge.DEFAULT_RESTING_DISTANCE;

					// value += "repellingIntensity = " +
					// Springs.getRepelling_intensity() * ratio + ";";
					// value += "springConstant = " + DNVEdge.DEFAULT_K / (ratio
					// *
					// ratio) + ";";
					for( Iterator<DNVEdge> i = edges.iterator(); i.hasNext(); )
					{
						tempEdge = i.next();
						fromNode = tempEdge.getFrom();
						toNode = tempEdge.getTo();
						fromId = realIdToId.get( fromNode.getId() );
						toId = realIdToId.get( toNode.getId() );
						value += "addEdge(" + j + "," + fromId + "," + toId + "," + position.length() + ");";
						j++;
					}

					value += "nEdges = " + j + ";";
					value += "createNodePosUpdater();";
					value += "graphLoaded = true;";
					// value += "if( !updating ) updateStartStop();";
					value += "var button = document.getElementById('clientSideControls');";
					value += "if( button != null ) button.disabled = false;";
				}
				else
				{
					value += "if( updating ) updateStartStop();";
					value += "var button = document.getElementById('clientSideControls');";
					value += "if( button != null ) button.disabled = true;";
				}
			}
			catch( NullPointerException npe )
			{
				npe.printStackTrace();
			}
		}

		// System.out.println( "returning getObjects of: " + value );
		return value;
	}

	/**
	 * Refresh global boundaries.
	 * 
	 * @param level
	 *            the level
	 */
	public void refreshGlobalBoundaries( int level )
	{
		if( !fixedZoom )
		{
			globalMaxY = GraphFunctions.getMaxYPosition( graph, level, true );
			globalMinY = GraphFunctions.getMinYPosition( graph, level, true );
			globalMaxX = GraphFunctions.getMaxXPosition( graph, level, true );
			globalMinX = GraphFunctions.getMinXPosition( graph, level, true );
			if( scalePositions )
			{
				GraphFunctions.scalePositions( graph, level, 100, 100, globalMaxX - globalMinX, globalMaxY - globalMinY );

				globalMaxY = GraphFunctions.getMaxYPosition( graph, level, true );
				globalMinY = GraphFunctions.getMinYPosition( graph, level, true );
				globalMaxX = GraphFunctions.getMaxXPosition( graph, level, true );
				globalMinX = GraphFunctions.getMinXPosition( graph, level, true );
			}
			// System.out.println( "width : " + (globalMaxX - globalMinX) +
			// " height : " + (globalMaxY - globalMinY) );

			if( globalMinY == globalMaxY )
			{
				globalMinY -= 10;
				globalMaxY += 10;
			}
			if( globalMinX == globalMaxX )
			{
				globalMinX -= 10;
				globalMaxX += 10;
			}

			// double height = globalMaxY - globalMinY;
			// double width = globalMaxX - globalMinX;
			// if( height > width )
			// {
			// double difference = height - width;
			// globalMaxX += difference / 2.0;
			// globalMinX -= difference / 2.0;
			// }
			// else if( width > height )
			// {
			// double difference = width - height;
			// globalMaxY += difference / 2.0;
			// globalMinY -= difference / 2.0;
			// }

			double yBuffer = ( globalMaxY - globalMinY ) * whiteSpaceBuffer;
			double xBuffer = ( globalMaxX - globalMinX ) * whiteSpaceBuffer;
			globalMaxY += yBuffer;
			globalMinY -= yBuffer;
			globalMaxX += xBuffer;
			globalMinX -= xBuffer;
		}
	}

	/**
	 * Gets the graph info.
	 * 
	 * @return the graph info
	 */
	public String getGraphInfo()
	{
		String returnValue = "This view contains ";
		if( subGraph != null )
		{
			try
			{
				if( subGraph.getNodes() != null && subGraph.getNodes().values() != null )
				{
					returnValue += subGraph.getNodes().values().size() + " nodes";
				}
				else
				{
					returnValue += "0 nodes";
				}
				if( subGraph.getEdges() != null && subGraph.getEdges().values() != null )
				{
					returnValue += " and " + subGraph.getEdges().values().size() + " edges.";
				}
				else
				{
					returnValue += " and 0 edges.";
				}
			}
			catch( NullPointerException npe )
			{
				npe.printStackTrace();
			}
		}
		if( selectedNode != null && !selectedNode.getLabel( interpolationLabels ).equals( "" ) )
		{
			returnValue += "  Selected node = " + selectedNode.getLabel( interpolationLabels );
		}

		return returnValue;
	}

	/**
	 * Checks if is render js.
	 * 
	 * @return true, if is render js
	 */
	public boolean isRenderJS()
	{
		findSubGraph();
		try
		{
			if( subGraph != null && subGraph.getNodes() != null && subGraph.getNodes().values().size() < getJsThreshold() )
			{
				return true;
			}
		}
		catch( NullPointerException npe )
		{
			npe.printStackTrace();
		}

		return false;
	}

	/**
	 * Sets the selected node.
	 * 
	 * @param node
	 *            the node
	 * @param ctrlPressed
	 *            the ctrl pressed
	 */
	public void setSelectedNode( DNVNode node, boolean ctrlPressed )
	{
		long startTime = System.currentTimeMillis();
		if( node == null || !node.isSelected() )
		{
			if( !ctrlPressed )
			{
				// deselect
				unselectAllNodesAndEdges();

				if( this.selectedNode != null )
				{
					this.selectedNode.setSelected( false );
				}
			}

			if( node != null )
			{
				node.setSelected( true );
				// System.out.println( "selecting node: " + node.getBbId() );
				Set<String> selectedNodes = new HashSet<String>();
				selectedNodes.add( node.getBbId() );
			}
		}

		this.selectedNode = node;
		setGraphChanged( true );
		printTime( "setSelectedNode", startTime );
	}

	/**
	 * Sets the selected edge.
	 * 
	 * @param edge
	 *            the edge
	 * @param ctrlPressed
	 *            the ctrl pressed
	 */
	public void setSelectedEdge( DNVEdge edge, boolean ctrlPressed )
	{
		if( edge == null || !edge.isSelected() )
		{
			if( !ctrlPressed )
			{
				// deselect
				unselectAllNodesAndEdges();

				if( this.selectedEdge != null )
				{
					this.selectedEdge.setSelected( false );
					// System.out.println( "deselecting node: " +
					// selectedNode.getBbId() );
				}
			}

			if( edge != null )
			{
				edge.setSelected( true );
				// System.out.println( "selecting node: " + node.getBbId() );
				Set<String> selectedEdges = new HashSet<String>();
				selectedEdges.add( edge.getBbId() );
			}
		}

		this.selectedEdge = edge;
		setGraphChanged( true );
	}

	/**
	 * Selected edge.
	 * 
	 * @return the dNV edge
	 */
	public DNVEdge selectedEdge()
	{
		return selectedEdge;
	}

	/**
	 * Gets the selected node.
	 * 
	 * @return the selected node
	 */
	public DNVNode getSelectedNode()
	{
		return selectedNode;
	}

	/**
	 * Gets the embed.
	 * 
	 * @return the embed
	 */
	public String getEmbed()
	{
		try
		{
			if( isRenderJS() )
			{
				return "createEmbedSVG(" + width + "," + height + ",'" + getWebPath() + "');";
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}

		return "";
	}

	/**
	 * Checks if is render image.
	 * 
	 * @return true, if is render image
	 */
	public boolean isRenderImage()
	{
		return !isRenderJS();
	}

	/**
	 * Gets the graph.
	 * 
	 * @return the graph
	 */
	public DNVGraph getGraph()
	{
		return graph;
	}

	/**
	 * Gets the sub graph.
	 * 
	 * @return the sub graph
	 */
	public SubGraph getSubGraph()
	{
		return subGraph;
	}

	/** The last graph. */
	private DNVGraph lastGraph = null;

	/** The last min x. */
	private double lastMinX = -1;

	/** The last min y. */
	private double lastMinY = -1;

	/** The last max x. */
	private double lastMaxX = -1;

	/** The last max y. */
	private double lastMaxY = -1;

	/** The last level. */
	private double lastLevel = -1;

	/** The force subgraph refresh. */
	private boolean forceSubgraphRefresh = false;

	/**
	 * Force subgraph refresh.
	 */
	public void forceSubgraphRefresh()
	{
		forceSubgraphRefresh = true;
	}
	
	/**
	 * Find sub graph.
	 */
	public void findSubGraph()
	{
		if( lastGraph == null || lastGraph != graph || minX != lastMinX || minY != lastMinY || maxX != lastMaxX || maxY != lastMaxY
				|| level != lastLevel || forceSubgraphRefresh )
		{
			synchronized( graph )
			{
				setSubGraph( SubGraphFinder.getSubGraphWithin( graph, level, minX, minY, maxX, maxY, whiteSpaceBuffer ) );
			}
			lastGraph = graph;
			lastMinX = minX;
			lastMinY = minY;
			lastMaxX = maxX;
			lastMaxY = maxY;
			lastLevel = level;
			forceSubgraphRefresh = false;
			/*
			 * try { System.out.println( "Subgraph has " +
			 * subGraph.getNodes().size() + " nodes and " +
			 * subGraph.getEdges().size() + " edges." ); } catch(
			 * NullPointerException npe ) {}
			 */
		}

	}

	public boolean isNodeWithin( DNVNode node )
	{
		double globalWidth = globalMaxX - globalMinX;
		double globalHeight = globalMaxY - globalMinY;

		double minXPos = minX * globalWidth + globalMinX;
		double maxXPos = maxX * globalWidth + globalMinX;
		double minYPos = minY * globalHeight + globalMinY;
		double maxYPos = maxY * globalHeight + globalMinY;

		return SubGraphFinder.isNodeWithin( node, minXPos, minYPos, maxXPos, maxYPos );
	}

	/**
	 * Sets the sub graph.
	 * 
	 * @param subGraph
	 *            the new sub graph
	 */
	public void setSubGraph( SubGraph subGraph )
	{
		this.subGraph = subGraph;
	}

	/**
	 * Gets the web path.
	 * 
	 * @return the web path
	 */
	public String getWebPath()
	{
		try
		{
			FacesContext fc = FacesContext.getCurrentInstance();
			String webPath = fc.getExternalContext().getRequestContextPath();
			webPath += "/wigi/";
//			System.out.println( webPath );
			return webPath;
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		
		return "/WiGi/wigi/";
	}
	
	public String getContextPath()
	{
		try
		{
			FacesContext fc = FacesContext.getCurrentInstance();
			String contextPath = fc.getExternalContext().getRequestContextPath();
//			System.out.println( webPath );
			return contextPath;
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		
		return "/WiGi";

	}
	
	public static String getCurrentInstanceContextPath()
	{
		return getCurrentInstance().getContextPath();
	}
	
	public static String getCurrentInstanceContextPath( HttpServletRequest request )
	{
		return getCurrentInstance( request ).getContextPath();
	}

	public static String getCurrentInstanceWebPath()
	{
		return getCurrentInstance().getWebPath();
	}

	public static String getCurrentInstanceWebPath( HttpServletRequest request )
	{
		return getCurrentInstance( request ).getWebPath();
	}
	public static PaintBean getCurrentInstance()
	{
		PaintBean pb =  (PaintBean)ContextLookup.lookup( "paintBean", FacesContext.getCurrentInstance() );
		if( pb == null )
		{
			pb = new PaintBean();
			ContextLookup.setBean( "paintBean", pb, FacesContext.getCurrentInstance() );
		}
		
		return pb;
	}
	
	public static PaintBean getCurrentInstance( HttpServletRequest request )
	{
		PaintBean pb = (PaintBean)ContextLookup.lookup( "paintBean", request );
		if( pb == null )
		{
			pb = new PaintBean();
			ContextLookup.setBean( "paintBean", pb, request.getSession() );
		}
		
		return pb;
	}
	
	// Dummy function to get rid of a warning message
	/**
	 * Sets the web path.
	 * 
	 * @param path
	 *            the new web path
	 */
	public void setWebPath( String path )
	{}

	/**
	 * Gets the time.
	 * 
	 * @return the time
	 */
	public String getTime()
	{
		return System.currentTimeMillis() + "";
	}

	/**
	 * Gets the selected file.
	 * 
	 * @return the selected file
	 */
	public String getSelectedFile()
	{
		String graphStr = Request.getStringParameter( "graph" );
		if( graphStr != null )
		{
			if( !graphStr.endsWith( ".dnv" ) )
			{
				graphStr += ".dnv";
			}

			if( !selectedFile.equals( Settings.GRAPHS_PATH + graphStr ) )
			{
				setSelectedFile( Settings.GRAPHS_PATH + graphStr );
				GraphsBean gb = (GraphsBean)ContextLookup.lookup( "graphsBean", FacesContext.getCurrentInstance() );
				if( gb != null )
				{
					List<SelectItem> files = gb.getFileList();
					boolean found = false;
					for( SelectItem file : files )
					{
						if( file.getLabel().equals( graphStr ) )
						{
							found = true;
							break;
						}
					}
					if( !found )
					{
						SelectItem item = new SelectItem( Settings.GRAPHS_PATH + graphStr, graphStr );
						files.add( item );
					}
				}
			}
		}

		return selectedFile;
	}

	/**
	 * Sets the selected file.
	 * 
	 * @param selectedFile
	 *            the new selected file
	 */
	public void setSelectedFile( String selectedFile )
	{
		if( !selectedFile.startsWith( Settings.GRAPHS_PATH ) )
		{
			selectedFile = Settings.GRAPHS_PATH + selectedFile;
		}
		if( !selectedFile.equals( this.selectedFile ) )
		{
			this.selectedFile = selectedFile;
			File file = new File( selectedFile );
			if( file.exists() )
			{
				loadGraph();
			}
		}
	}

	/**
	 * Gets the display data panel.
	 * 
	 * @return the display data panel
	 */
	public String getDisplayDataPanel()
	{
		String display = Request.getStringParameter( "displayDataPanel" );
		display = "none";
		if( !( display != null && ( display.equals( "false" ) || display.equals( "no" ) || display.equals( "none" ) ) ) )
		{
			display = "";
		}

		return display;
	}

	/**
	 * Gets the display appearance panel.
	 * 
	 * @return the display appearance panel
	 */
	public String getDisplayAppearancePanel()
	{
		String display = Request.getStringParameter( "displayAppearancePanel" );
		if( display != null && ( display.equals( "false" ) || display.equals( "no" ) || display.equals( "none" ) ) )
		{
			display = "none";
		}
		else
		{
			display = "";
		}

		return display;
	}

	/**
	 * Gets the display labels panel.
	 * 
	 * @return the display labels panel
	 */
	public String getDisplayLabelsPanel()
	{
		String display = Request.getStringParameter( "displayLabelsPanel" );
		if( display != null && ( display.equals( "false" ) || display.equals( "no" ) || display.equals( "none" ) ) )
		{
			display = "none";
		}
		else
		{
			display = "";
		}

		return display;
	}

	/**
	 * Gets the width int.
	 * 
	 * @return the width int
	 */
	public int getWidthInt()
	{
		return (int)getWidth();
	}

	/**
	 * Gets the width.
	 * 
	 * @return the width
	 */
	public double getWidth()
	{
		Integer width = Request.getIntegerParameter( "width" );
		if( width != null )
		{
			this.width = width;
		}

		return this.width;
	}

	/**
	 * Sets the width.
	 * 
	 * @param width
	 *            the new width
	 */
	public void setWidth( double width )
	{
		if( this.width != width )
		{
			this.width = (int)width;
			updateNodeSize();
		}
	}

	/**
	 * Gets the height int.
	 * 
	 * @return the height int
	 */
	public int getHeightInt()
	{
		return (int)getHeight();
	}

	/**
	 * Gets the height.
	 * 
	 * @return the height
	 */
	public double getHeight()
	{
		Integer height = Request.getIntegerParameter( "height" );
		if( height != null )
		{
			this.height = height;
		}

		return this.height;
	}

	/**
	 * Sets the height.
	 * 
	 * @param height
	 *            the new height
	 */
	public void setHeight( double height )
	{
		if( this.height != height )
		{
			this.height = (int)height;
			updateNodeSize();
		}
	}

	/**
	 * Gets the level.
	 * 
	 * @return the level
	 */
	public double getLevel()
	{
		return level;
	}

	/**
	 * Sets the level.
	 * 
	 * @param level
	 *            the new level
	 */
	public void setLevel( double level )
	{
		if( this.level != (int)level )
		{
			this.level = (int)level;
			resetNumberOfClusters();
			updateNumberAffected();
		}
	}

	/**
	 * Gets the min x.
	 * 
	 * @return the min x
	 */
	public double getMinX()
	{
		return minX;
	}

	/**
	 * Sets the min x.
	 * 
	 * @param minX
	 *            the new min x
	 */
	public void setMinX( double minX )
	{
		if( this.minX != minX )
		{
			this.minX = minX;
			forceSubgraphRefresh();
		}
	}

	/**
	 * Gets the min y.
	 * 
	 * @return the min y
	 */
	public double getMinY()
	{
		return minY;
	}

	/**
	 * Sets the min y.
	 * 
	 * @param minY
	 *            the new min y
	 */
	public void setMinY( double minY )
	{
		if( this.minY != minY )
		{
			this.minY = minY;
			forceSubgraphRefresh();
		}
	}

	/**
	 * Gets the max x.
	 * 
	 * @return the max x
	 */
	public double getMaxX()
	{
		return maxX;
	}

	/**
	 * Sets the max x.
	 * 
	 * @param maxX
	 *            the new max x
	 */
	public void setMaxX( double maxX )
	{
		if( this.maxX != maxX )
		{
			this.maxX = maxX;
			forceSubgraphRefresh();
		}
	}

	/**
	 * Gets the max y.
	 * 
	 * @return the max y
	 */
	public double getMaxY()
	{
		return maxY;
	}

	/**
	 * Sets the max y.
	 * 
	 * @param maxY
	 *            the new max y
	 */
	public void setMaxY( double maxY )
	{
		if( this.maxY != maxY )
		{
			this.maxY = maxY;
			forceSubgraphRefresh();
		}
	}

	/**
	 * Gets the unique id.
	 * 
	 * @return the unique id
	 */
	public String getUniqueId()
	{
		return UUID.randomUUID().toString();
	}

	/** The js graph count. */
	private static int jsGraphCount = 0;

	/**
	 * Gets the js graph page.
	 * 
	 * @return the js graph page
	 */
	public String getJsGraphPage()
	{
		jsGraphCount = ( jsGraphCount + 1 ) % 2;
		return "jsGraph" + jsGraphCount + ".xhtml";
	}

	/**
	 * Gets the overview page.
	 * 
	 * @return the overview page
	 */
	public String getOverviewPage()
	{
		return getWebPath() + "GraphServlet?width=200&height=200&overview=true&r=qual&UUID=" + UUID.randomUUID();
	}

	/** The embed count. */
	private static int embedCount = 0;

	/**
	 * Gets the embed page.
	 * 
	 * @return the embed page
	 */
	public String getEmbedPage()
	{
		embedCount = ( embedCount + 1 ) % 2;
		return "embed" + embedCount + ".xhtml";
	}

	/** The main js count. */
	private static int mainJSCount = 0;

	/**
	 * Gets the main js page.
	 * 
	 * @return the main js page
	 */
	public String getMainJSPage()
	{
		mainJSCount = ( mainJSCount + 1 ) % 2;
		return "mainJS" + mainJSCount + ".xhtml";
	}

	/**
	 * Gets the js threshold.
	 * 
	 * @return the js threshold
	 */
	public double getJsThreshold()
	{
		return jsThreshold;
	}

	/**
	 * Sets the js threshold.
	 * 
	 * @param jsThreshold
	 *            the new js threshold
	 */
	public void setJsThreshold( double jsThreshold )
	{
		this.jsThreshold = jsThreshold;
	}

	/**
	 * Gets the ajax components.
	 * 
	 * @return the ajax components
	 */
	public String getAjaxComponents()
	{
		return ajaxComponents;
	}

	public void setAjaxComponents( String ajaxComponents )
	{
		this.ajaxComponents = ajaxComponents;
	}
	/**
	 * Gets the curve min.
	 * 
	 * @return the curve min
	 */
	public double getCurveMin()
	{
		return curveMin;
	}

	/**
	 * Sets the curve min.
	 * 
	 * @param curveMin
	 *            the new curve min
	 */
	public void setCurveMin( double curveMin )
	{
		this.curveMin = curveMin;
	}

	/**
	 * Gets the curve max.
	 * 
	 * @return the curve max
	 */
	public double getCurveMax()
	{
		return curveMax;
	}

	/**
	 * Sets the curve max.
	 * 
	 * @param curveMax
	 *            the new curve max
	 */
	public void setCurveMax( double curveMax )
	{
		this.curveMax = curveMax;
	}

	/**
	 * Gets the node size.
	 * 
	 * @return the node size
	 */
	public double getNodeSize()
	{
		Integer nodeSize = Request.getIntegerParameter( "nodeSize" );
		if( nodeSize != null )
		{
			this.nodeSize = nodeSize;
		}

		return this.nodeSize;
	}

	/**
	 * Sets the node size.
	 * 
	 * @param nodeSize
	 *            the new node size
	 */
	public void setNodeSize( double nodeSize )
	{
		this.nodeSize = nodeSize;
	}

	/**
	 * Gets the number affected.
	 * 
	 * @return the number affected
	 */
	public double getNumberAffected()
	{
		synchronized( graph )
		{
			return numberAffected;
		}
	}

	/**
	 * Sets the number affected.
	 * 
	 * @param numberAffected
	 *            the new number affected
	 */
	public void setNumberAffected( double numberAffected )
	{
		synchronized( graph )
		{
			this.numberAffected = numberAffected;
		}
	}

	/**
	 * Gets the max layout time.
	 * 
	 * @return the max layout time
	 */
	public double getMaxLayoutTime()
	{
		return maxLayoutTime;
	}

	/**
	 * Sets the max layout time.
	 * 
	 * @param maxLayoutTime
	 *            the new max layout time
	 */
	public void setMaxLayoutTime( double maxLayoutTime )
	{
		this.maxLayoutTime = maxLayoutTime;
	}

	/**
	 * Checks if is show icons.
	 * 
	 * @return true, if is show icons
	 */
	public boolean isShowIcons()
	{
		return showIcons;
	}

	/**
	 * Sets the show icons.
	 * 
	 * @param showIcons
	 *            the new show icons
	 */
	public void setShowIcons( boolean showIcons )
	{
		this.showIcons = showIcons;
	}

	/**
	 * Checks if is show labels.
	 * 
	 * @return true, if is show labels
	 */
	public boolean isShowLabels()
	{
		String showLabels = Request.getStringParameter( "showLabels" );
		if( showLabels != null )
		{
			if( showLabels.equalsIgnoreCase( "true" ) )
			{
				this.showLabels = true;
			}
			else if( showLabels.equalsIgnoreCase( "false" ) )
			{
				this.showLabels = false;
			}
		}

		return this.showLabels;
	}

	/**
	 * Sets the show labels.
	 * 
	 * @param showLabels
	 *            the new show labels
	 */
	public void setShowLabels( boolean showLabels )
	{
		this.showLabels = showLabels;
	}

	/**
	 * Checks if is curved labels.
	 * 
	 * @return true, if is curved labels
	 */
	public boolean isCurvedLabels()
	{
		return curvedLabels;
	}

	/**
	 * Sets the curved labels.
	 * 
	 * @param curvedLabels
	 *            the new curved labels
	 */
	public void setCurvedLabels( boolean curvedLabels )
	{
		this.curvedLabels = curvedLabels;
	}

	/**
	 * Gets the label size.
	 * 
	 * @return the label size
	 */
	public double getLabelSize()
	{
		return labelSize;
	}

	/**
	 * Sets the label size.
	 * 
	 * @param labelSize
	 *            the new label size
	 */
	public void setLabelSize( double labelSize )
	{
		this.labelSize = labelSize;
	}

	/** The layout running. */
	private boolean layoutRunning = false;

	/** The layout status. */
	private String layoutStatus = "";

	/**
	 * Run layout.
	 */
	public void runLayout()
	{
		layoutRunning = true;

		runLayout( clusterBeforeLayout, layoutAllLevels, clusteringMethod, graph, level, maxLayoutTime, nodeSize, coolingFactor,
				recommendationCircle, layoutMethod, false );

		forceSubgraphRefresh();

		layoutRunning = false;
	}

	/**
	 * Run layout.
	 * 
	 * @param clusterBeforeLayout
	 *            the cluster before layout
	 * @param layoutAllLevels
	 *            the layout all levels
	 * @param clusteringMethod
	 *            the clustering method
	 * @param graph
	 *            the graph
	 * @param level
	 *            the level
	 * @param maxLayoutTime
	 *            the max layout time
	 * @param nodeSize
	 *            the node size
	 * @param coolingFactor
	 *            the cooling factor
	 * @param recommendationCircle
	 *            the recommendation circle
	 * @param layoutMethod
	 *            the layout method
	 * @param useNumberOfSubnodes
	 *            the use number of subnodes
	 */
	public void runLayout( boolean clusterBeforeLayout, boolean layoutAllLevels, String clusteringMethod, DNVGraph graph, int level,
			double maxLayoutTime, double nodeSize, float coolingFactor, boolean recommendationCircle, LayoutInterface layoutMethod, boolean useNumberOfSubnodes )
	{
		long startTime = System.currentTimeMillis();
		if( clusterBeforeLayout && layoutAllLevels )
		{
			synchronized( graph )
			{
				int currentLevel = 0;

				GraphFunctions.clearHigherLevels( currentLevel, graph );
				if( clusteringMethod.equals( Settings.CONNECTED_CLUSTERING ) )
				{
					System.out.println( "Connected Clustering on level " + currentLevel + " and " + graph.getGraphSize( currentLevel ) + " nodes." );
					ConnectedClustering.cluster( graph, currentLevel );
				}
				else if( clusteringMethod.equals( Settings.STRUCTURAL_EQUIVALENCE_CLUSTERING ) )
				{
					System.out.println( "Running Structural Equivalence Clustering on level " + currentLevel + " and "
							+ graph.getGraphSize( currentLevel ) + " nodes." );
					StructuralEquivalenceClustering.cluster( graph, currentLevel );
				}
				else
				{
					DescendingSort decending = new DescendingSort();
					while( ( graph.getGraphSize( currentLevel ) - graph.getNumberOfDisconnectedNodes( currentLevel ) >= 1 )
							&& graph.getGraphSize( currentLevel ) > 20
							&& ( graph.getGraphSize( currentLevel ) < graph.getGraphSize( currentLevel - 1 ) || currentLevel == 0 ) )
					{
						if( clusteringMethod.equals( Settings.K_MOST_CONNECTED_CLUSTERING ) )
						{
							// System.out.println(
							// "Running K-Most Connected Clustering on level " +
							// currentLevel
							// + " and " + graph.getGraphSize( currentLevel ) +
							// " nodes." );
							int numberOfClusters = graph.getGraphSize( currentLevel ) / 3;
							HashMap<Integer, Integer> histogram = GenerateHistogramOfConnectivity.generateHistogram( graph, currentLevel );
							List<Integer> connectivityList = new ArrayList<Integer>( histogram.keySet() );
							Collections.sort( connectivityList, decending );
							int numberOfNodes = 0;
							int totalNumberOfNodes = 0;
							boolean found = false;
							for( Integer connectivity : connectivityList )
							{
								numberOfNodes = histogram.get( connectivity );
								// System.out.println( "For connectivity: " +
								// connectivity );
								// System.out.println( numberOfNodes + " > " +
								// totalNumberOfNodes + "?" );
								if( ( numberOfNodes > totalNumberOfNodes && totalNumberOfNodes != 0 )
										|| totalNumberOfNodes >= graph.getGraphSize( currentLevel ) / 3 )
								{
									numberOfClusters = totalNumberOfNodes;
									found = true;
									break;
								}
								totalNumberOfNodes += numberOfNodes;
							}

							if( !found )
							{
								numberOfClusters = totalNumberOfNodes - numberOfNodes;

							}

							// System.out.println( "Using " + numberOfClusters +
							// " clusters for level " + currentLevel );

							KMostConnected.cluster( graph, numberOfClusters, currentLevel, true );
						}
						else if( clusteringMethod.equals( Settings.SOLAR_SYSTEM_CLUSTERING ) )
						{
							System.out.println( "Running Solar System Clustering on level " + currentLevel + " and "
									+ graph.getGraphSize( currentLevel ) + " nodes." );
							SolarSystemClustering.cluster( graph, currentLevel );
						}

						currentLevel++;
					}
				}
			}
			// clusterBeforeLayout = false;
		}

		graph.storeCurrentPosition( level );

		if( layoutMethod instanceof TimeLimitedLayoutInterface )
		{
			// layoutStatus = "Running spring layout for " + maxLayoutTime +
			// " seconds.";

			((TimeLimitedLayoutInterface)layoutMethod).runLayout( graph, level, maxLayoutTime, false, layoutAllLevels );

			// layoutStatus = "Spring layout complete.";
		}
		else if( layoutMethod instanceof SpaceRestrictedLayoutInterface )
		{
			// layoutStatus = "Running Fructerman-Reingold layout.";

			float width = GraphFunctions.getGraphWidth( graph, 0, false );
			float height = GraphFunctions.getGraphHeight( graph, 0, false );

			((SpaceRestrictedLayoutInterface)layoutMethod).runLayout( width, height, graph, coolingFactor, level, layoutAllLevels, useNumberOfSubnodes,
					frUseEdgeRestingDistance, frUseNodeSize );

			// layoutStatus = "Fructerman-Reingold layout complete.";
		}
		else if( layoutMethod instanceof SortingLayoutInterface )
		{
			// layoutStatus = "Running Fructerman-Reingold layout.";

			((SortingLayoutInterface)layoutMethod).runLayout( graph, level, circularLayoutSort );

			// layoutStatus = "Fructerman-Reingold layout complete.";
		}
		else if( layoutMethod instanceof RecommendationLayoutInterface )
		{
			// layoutStatus = "Running Recommendation layout.";

			Map<Integer, DNVEntity> users = graph.getNodesByType( level, "user" );
			if( users != null && users.size() > 0 )
			{
				DNVEntity userNode = users.values().iterator().next();
				if( userNode != null )
				{
					((RecommendationLayoutInterface)layoutMethod).runLayout( graph, (DNVNode)userNode, level, 0, 0, recommendationCircle );
				}
			}

			// layoutStatus = "Recommendation layout complete.";
		}
		else if( layoutMethod instanceof CenterPositionFixedLayoutInterface )
		{
			// layoutStatus = "Running Peerchooser layout.";

			((CenterPositionFixedLayoutInterface)layoutMethod).runLayout( graph, level, 0, 0 );

			// layoutStatus = "Peerchooser layout complete.";
		}
		else if( layoutMethod instanceof MDSLayoutInterface )
		{
			((MDSLayoutInterface)layoutMethod).runLayout( graph, level, true );
		}
		else if( layoutMethod instanceof SimpleLayoutInterface )
		{
			((SimpleLayoutInterface)layoutMethod).runLayout( graph, level );
		}
		else if( layoutMethod instanceof RadiusRestrictedLayoutInterface )
		{
			((RadiusRestrictedLayoutInterface)layoutMethod).runLayout( graph, level, 400 );
		}

		if( graph.hasStoredPosition() )
		{
			graph.startInterpolation();
		}

		Timer timer = new Timer( Timer.MILLISECONDS );
		timer.setStart();
		while( graph.hasStoredPosition() )
		{
			synchronized( graph )
			{
				timer.setEnd();
				if( timer.getTotalTime( Timer.SECONDS ) > 5 && graph.getCurrentFrame() == 0 )
				{
					break;
				}
				timer.setStart();
			}
		}

		if( graph.hasStoredPosition() )
		{
			graph.removeStoredPosition( level );
		}

		System.out.println( "Layout Finished" );
		printTime( "runLayout(...", startTime );

	}

	/**
	 * Randomize positions.
	 */
	public void randomizePositions()
	{
		long startTime = System.currentTimeMillis();
		new RandomLayout().runLayout( graph, level, 100 );
		printTime( "randomizePositions", startTime );
	}

	/** The clustering running. */
	private boolean clusteringRunning = false;

	/** The clustering status. */
	private String clusteringStatus = "";

	/**
	 * Run clustering.
	 */
	public void runClustering()
	{
		runClustering( level, graph, numberOfClusters, clusteringMethod );
	}

	/**
	 * Run clustering.
	 * 
	 * @param level
	 *            the level
	 * @param graph
	 *            the graph
	 * @param numberOfClusters
	 *            the number of clusters
	 * @param clusteringMethod
	 *            the clustering method
	 */
	public void runClustering( int level, DNVGraph graph, int numberOfClusters, String clusteringMethod )
	{
		clusteringRunning = true;

		GraphFunctions.clearHigherLevels( level, graph );

		if( clusteringMethod.equals( Settings.K_MOST_CONNECTED_CLUSTERING ) )
		{
			KMostConnected.cluster( graph, (int)numberOfClusters, level, true );
		}
		else if( clusteringMethod.equals( Settings.SOLAR_SYSTEM_CLUSTERING ) )
		{
			SolarSystemClustering.cluster( graph, level );
		}
		else if( clusteringMethod.equals( Settings.CONNECTED_CLUSTERING ) )
		{
			ConnectedClustering.cluster( graph, level );
		}
		else if( clusteringMethod.equals( Settings.STRUCTURAL_EQUIVALENCE_CLUSTERING ) )
		{
			StructuralEquivalenceClustering.cluster( graph, level );
		}

		setLevel( level + 1 );

		clusteringRunning = false;
	}

	/**
	 * Gets the global min x.
	 * 
	 * @return the global min x
	 */
	public double getGlobalMinX()
	{
		return globalMinX;
	}

	/**
	 * Sets the global min x.
	 * 
	 * @param globalMinX
	 *            the new global min x
	 */
	public void setGlobalMinX( double globalMinX )
	{
		this.globalMinX = globalMinX;
	}

	/**
	 * Gets the global max x.
	 * 
	 * @return the global max x
	 */
	public double getGlobalMaxX()
	{
		return globalMaxX;
	}

	/**
	 * Sets the global max x.
	 * 
	 * @param globalMaxX
	 *            the new global max x
	 */
	public void setGlobalMaxX( double globalMaxX )
	{
		this.globalMaxX = globalMaxX;
	}

	/**
	 * Gets the global min y.
	 * 
	 * @return the global min y
	 */
	public double getGlobalMinY()
	{
		return globalMinY;
	}

	/**
	 * Sets the global min y.
	 * 
	 * @param globalMinY
	 *            the new global min y
	 */
	public void setGlobalMinY( double globalMinY )
	{
		this.globalMinY = globalMinY;
	}

	/**
	 * Gets the global max y.
	 * 
	 * @return the global max y
	 */
	public double getGlobalMaxY()
	{
		return globalMaxY;
	}

	/**
	 * Sets the global max y.
	 * 
	 * @param globalMaxY
	 *            the new global max y
	 */
	public void setGlobalMaxY( double globalMaxY )
	{
		this.globalMaxY = globalMaxY;
	}

	/**
	 * Checks if is use only materialized list.
	 * 
	 * @return true, if is use only materialized list
	 */
	public boolean isUseOnlyMaterializedList()
	{
		return useOnlyMaterializedList;
	}

	/**
	 * Sets the use only materialized list.
	 * 
	 * @param useOnlyMaterializedList
	 *            the new use only materialized list
	 */
	public void setUseOnlyMaterializedList( boolean useOnlyMaterializedList )
	{
		this.useOnlyMaterializedList = useOnlyMaterializedList;
	}

	/**
	 * Checks if is refresh overview.
	 * 
	 * @return true, if is refresh overview
	 */
	public boolean isRefreshOverview()
	{
		return refreshOverview;
	}

	/**
	 * Sets the refresh overview.
	 * 
	 * @param refreshOverview
	 *            the new refresh overview
	 */
	public void setRefreshOverview( boolean refreshOverview )
	{
		this.refreshOverview = refreshOverview;
	}

	/**
	 * Gets the edge thickness.
	 * 
	 * @return the edge thickness
	 */
	public double getEdgeThickness()
	{
		return edgeThickness;
	}

	/**
	 * Sets the edge thickness.
	 * 
	 * @param edgeThickness
	 *            the new edge thickness
	 */
	public void setEdgeThickness( double edgeThickness )
	{
		this.edgeThickness = edgeThickness;
	}

	/**
	 * Gets the edge color.
	 * 
	 * @return the edge color
	 */
	public double getEdgeColor()
	{
		return edgeColor;
	}

	/**
	 * Sets the edge color.
	 * 
	 * @param edgeColor
	 *            the new edge color
	 */
	public void setEdgeColor( double edgeColor )
	{
		this.edgeColor = edgeColor;
	}

	public void buildLayoutMethodList()
	{
		buildLayoutMethodList( Settings.LAYOUT_ALGORITHMS );
	}
	
	/**
	 * Builds the layout method list.
	 * 
	 * @param algorithms
	 *            the algorithms
	 */
	public void buildLayoutMethodList( LayoutInterface[] algorithms )
	{
		layoutMethodList.clear();
		layoutMethods.clear();
		SelectItem layoutItem;
		for( LayoutInterface layout : algorithms )
		{
			layoutItem = new SelectItem( layout.getLabel(), layout.getLabel() );
			layoutMethodList.add( layoutItem );
			layoutMethods.put( layout.getLabel(), layout );
		}
	}

	/**
	 * Builds the circular layout sort list.
	 */
	private void buildCircularLayoutSortList()
	{
		circularLayoutSortList.clear();
		SelectItem sortItem;
		for( int i = 0; i < CircularLayout.SORT_TYPES.length; i++ )
		{
			sortItem = new SelectItem( CircularLayout.SORT_TYPES[i], CircularLayout.SORT_TYPES[i] );
			circularLayoutSortList.add( sortItem );
		}
	}

	/**
	 * Builds the interaction method list.
	 */
	public void buildInteractionMethodList()
	{
		buildInteractionMethodList( Settings.INTERACTION_ALGORITHMS );
	}

	/**
	 * Builds the interaction method list.
	 * 
	 * @param algorithms
	 *            the algorithms
	 */
	public void buildInteractionMethodList( InteractionInterface[] algorithms )
	{
		interactionMethodList.clear();
		interactionMethods.clear();
		SelectItem interactionItem;
		for( InteractionInterface interaction : algorithms )
		{
			interactionItem = new SelectItem( interaction.getLabel(), interaction.getLabel() );
			interactionMethodList.add( interactionItem );
			interactionMethods.put( interaction.getLabel(), interaction );
		}
	}

	/**
	 * Gets the layout method list.
	 * 
	 * @return the layout method list
	 */
	public List<SelectItem> getLayoutMethodList()
	{
		return layoutMethodList;
	}

	/**
	 * Sets the layout method list.
	 * 
	 * @param layoutMethodList
	 *            the new layout method list
	 */
	public void setLayoutMethodList( List<SelectItem> layoutMethodList )
	{
		this.layoutMethodList = layoutMethodList;
	}

	/**
	 * Builds the clustering method list.
	 */
	public void buildClusteringMethodList()
	{
		buildClusteringMethodList( Settings.CLUSTERING_ALGORITHMS );
	}

	/**
	 * Builds the clustering method list.
	 * 
	 * @param algorithms
	 *            the algorithms
	 */
	public void buildClusteringMethodList( String[] algorithms )
	{
		clusteringMethodList.clear();
		SelectItem clusteringItem;
		for( int i = 0; i < algorithms.length; i++ )
		{
			clusteringItem = new SelectItem( algorithms[i], algorithms[i] );
			clusteringMethodList.add( clusteringItem );
		}
	}

	/**
	 * Gets the clustering method list.
	 * 
	 * @return the clustering method list
	 */
	public List<SelectItem> getClusteringMethodList()
	{
		return clusteringMethodList;
	}

	/**
	 * Sets the clustering method list.
	 * 
	 * @param clusteringMethodList
	 *            the new clustering method list
	 */
	public void setClusteringMethodList( List<SelectItem> clusteringMethodList )
	{
		this.clusteringMethodList = clusteringMethodList;
	}

	/**
	 * Gets the layout method.
	 * 
	 * @return the layout method
	 */
	public String getLayoutMethodLabel()
	{
		return layoutMethod.getLabel();
	}

	public void setLayoutMethodLabel( String label )
	{
		setLayoutMethod( layoutMethods.get( label ) );
	}
	
	/**
	 * Sets the layout method.
	 * 
	 * @param layoutMethod
	 *            the new layout method
	 */
	public void setLayoutMethod( LayoutInterface layoutMethod )
	{
		this.layoutMethod = layoutMethod;
	}
	
	public LayoutInterface getLayoutMethod()
	{
		return layoutMethod;
	}
	
	public LayoutInterface getLayoutMethod( String label )
	{
		return layoutMethods.get( label );
	}

	public boolean isShowCircularLayoutBuffer()
	{
		return getLayoutMethodLabel().equals( Settings.DOCUMENT_TOPIC_CIRCULAR_LAYOUT );
	}
	
	/** The circular layout buffer. */
	private double circularLayoutBuffer = 0;
	/**
	 * Gets the circular layout buffer.
	 * 
	 * @return the circular layout buffer
	 */
	public double getCircularLayoutBuffer()
	{
		return circularLayoutBuffer;
	}

	/** The ignore next circular layout buffer. */
	private boolean ignoreNextCircularLayoutBuffer = false;

	/**
	 * Sets the ignore next circular layout buffer.
	 * 
	 * @param value
	 *            the new ignore next circular layout buffer
	 */
	public void setIgnoreNextCircularLayoutBuffer( boolean value )
	{
		ignoreNextCircularLayoutBuffer = value;
	}

	/**
	 * Sets the circular layout buffer.
	 * 
	 * @param circularLayoutBuffer
	 *            the new circular layout buffer
	 */
	public void setCircularLayoutBuffer( double circularLayoutBuffer )
	{
		if( ignoreNextCircularLayoutBuffer )
		{
			System.out.println( "ignoring layout buffer = " + circularLayoutBuffer );
			ignoreNextCircularLayoutBuffer = false;
			return;
		}
		System.out.println( "setting layout buffer = " + circularLayoutBuffer );
		this.circularLayoutBuffer = circularLayoutBuffer;
	}

	/**
	 * Gets the clustering method.
	 * 
	 * @return the clustering method
	 */
	public String getClusteringMethod()
	{
		return clusteringMethod;
	}

	/**
	 * Sets the clustering method.
	 * 
	 * @param clusteringMethod
	 *            the new clustering method
	 */
	public void setClusteringMethod( String clusteringMethod )
	{
		this.clusteringMethod = clusteringMethod;
	}

	/**
	 * Checks if is layout running.
	 * 
	 * @return true, if is layout running
	 */
	public boolean isLayoutRunning()
	{
		return layoutRunning;
	}

	/**
	 * Sets the layout running.
	 * 
	 * @param layoutRunning
	 *            the new layout running
	 */
	public void setLayoutRunning( boolean layoutRunning )
	{
		this.layoutRunning = layoutRunning;
	}

	/**
	 * Checks if is clustering running.
	 * 
	 * @return true, if is clustering running
	 */
	public boolean isClusteringRunning()
	{
		return clusteringRunning;
	}

	/**
	 * Sets the clustering running.
	 * 
	 * @param clusteringRunning
	 *            the new clustering running
	 */
	public void setClusteringRunning( boolean clusteringRunning )
	{
		this.clusteringRunning = clusteringRunning;
	}

	/**
	 * Gets the number of clusters.
	 * 
	 * @return the number of clusters
	 */
	public int getNumberOfClusters()
	{
		return numberOfClusters;
	}

	/**
	 * Sets the number of clusters.
	 * 
	 * @param numberOfClusters
	 *            the new number of clusters
	 */
	public void setNumberOfClusters( int numberOfClusters )
	{
		this.numberOfClusters = numberOfClusters;
	}

	/**
	 * Gets the layout status.
	 * 
	 * @return the layout status
	 */
	public String getLayoutStatus()
	{
		return layoutStatus;
	}

	/**
	 * Sets the layout status.
	 * 
	 * @param layoutStatus
	 *            the new layout status
	 */
	public void setLayoutStatus( String layoutStatus )
	{
		this.layoutStatus = layoutStatus;
	}

	/**
	 * Gets the clustering status.
	 * 
	 * @return the clustering status
	 */
	public String getClusteringStatus()
	{
		return clusteringStatus;
	}

	/**
	 * Sets the clustering status.
	 * 
	 * @param clusteringStatus
	 *            the new clustering status
	 */
	public void setClusteringStatus( String clusteringStatus )
	{
		this.clusteringStatus = clusteringStatus;
	}

	/**
	 * Gets the client side controls visibility.
	 * 
	 * @return the client side controls visibility
	 */
	public String getClientSideControlsVisibility()
	{
		if( isRenderJS() )
			return "";

		return "hidden";
	}

	/**
	 * Removes the isolated nodes.
	 */
	public void removeIsolatedNodes()
	{
		addToHistory();
		graph.removeIsolatedNodes( level );
		updateNodeSize();
		forceSubgraphRefresh();
	}

	/**
	 * Removes the selected nodes.
	 */
	public void removeSelectedNodes()
	{
		long startTime = System.currentTimeMillis();
		if( graph.getSelectedNodes( level ).size() > 0 )
		{
			addToHistory();

			// Need this list to avoid concurrent modification exception!!
			List<DNVNode> selectedNodes = new ArrayList<DNVNode>(graph.getSelectedNodes( level ).values());
			System.out.println( "Removing " + selectedNodes.size() + " selected nodes." );
			for( DNVNode selectedNode : selectedNodes )
			{
				graph.removeNode( selectedNode );
				List<DNVEdge> edges = selectedNode.getFromEdges();
				for( int i = 0; i < edges.size(); i++ )
				{
					graph.removeNode( edges.get( i ) );
				}
				edges = selectedNode.getToEdges();
				for( int i = 0; i < edges.size(); i++ )
				{
					graph.removeNode( edges.get( i ) );
				}
			}

			graph.getSelectedNodes( level ).clear();

			updateNodeSize();
			forceSubgraphRefresh();
		}
		setGraphChanged( true );
		printTime( "removeSelectedNodes()", startTime );
	}
	
	private boolean keepNeighborsOfSelectedNodes = true;
	
	public boolean isKeepNeighborsOfSelectedNodes()
	{
		return keepNeighborsOfSelectedNodes;
	}

	public void setKeepNeighborsOfSelectedNodes( boolean keepNeighborsOfSelectedNodes )
	{
		this.keepNeighborsOfSelectedNodes = keepNeighborsOfSelectedNodes;
	}

	public void toggleKeepNeighborsOfSelectedNodes()
	{
		setKeepNeighborsOfSelectedNodes( !isKeepNeighborsOfSelectedNodes() );
	}
	
	public void removeUnselectedNodes()
	{
		DNVGraph graph = getGraph();
		if( graph != null )
		{
			if( graph.getSelectedNodes( level ).size() > 0 )
			{
				addToHistory();

				// Need this list to avoid concurrent modification exception
				List<DNVNode> visibleNodes = new ArrayList<DNVNode>(graph.getVisibleNodes( level ).values());
				for( DNVNode node : visibleNodes )
				{
					if( !node.isSelected() && ( !node.isNeighborSelected() || !keepNeighborsOfSelectedNodes ) )
					{
						graph.removeNode( node );
					}
				}

				graph.deselectAllNodes( level );
				updateNodeSize();
				forceSubgraphRefresh();
				setSelectAllCheckBox( false );
			}
		}
	}
	
	public void expandSelection()
	{
		Map<Integer, DNVNode> selectedMap = graph.getSelectedNodes( level );
		List<DNVNode> selectedNodes = new ArrayList<DNVNode>( selectedMap.values() );
		for( DNVNode node : selectedNodes )
		{
			node.selectNeighbors( true );
		}
	}
	
	public void invertSelection()
	{
		DNVGraph graph = getGraph();
		if( graph != null )
		{
			for( DNVNode node : graph.getVisibleNodes( level ).values() )
			{
				node.setSelected( !node.isSelected() );
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
			List<DNVEntity> nodesAndEdges = graph.getNodesAndEdges( level );
			while( history.size() > historyIndex )
			{
				history.remove( history.size() - 1 );
			}
			history.add( nodesAndEdges );
			historyIndex++;
		}
	}

	/**
	 * Undo.
	 */
	public void undo()
	{
		if( isShowUndo() )
		{
			DNVGraph graph = getGraph();
			if( history.size() == historyIndex )
			{
				if( graph != null )
				{
					List<DNVEntity> nodesAndEdges = graph.getNodesAndEdges( level );
					history.add( nodesAndEdges );
				}
			}

			graph = new DNVGraph();
			historyIndex--;
			List<DNVEntity> nodesAndEdges = history.get( historyIndex );
			for( DNVEntity entity : nodesAndEdges )
			{
				boolean visible = entity.isVisible();
				entity.setGraph( graph );
				graph.addNode( level, entity );
				entity.setVisible( visible );
			}

			for( DNVNode node : graph.getNodes( level ) )
			{
				node.updateNeighbors();
			}
			
			for( DNVNode node : graph.getNodes( level ) )
			{
				node.verifyNeighbors();
			}
			int minTime = this.minTime;
			int maxTime = this.maxTime;
			setGraph( graph );
			this.minTime = minTime;
			this.maxTime = maxTime;

			updateNodesWithinTimeframe();
			forceSubgraphRefresh();
			updateNodeSize();
		}
	}

	/**
	 * Redo.
	 */
	public void redo()
	{
		if( isShowRedo() )
		{
			DNVGraph graph = new DNVGraph();
			historyIndex++;
			List<DNVEntity> nodesAndEdges = history.get( historyIndex );
			for( DNVEntity entity : nodesAndEdges )
			{
				graph.addNode( level, entity );
			}
						
			for( DNVNode node : graph.getNodes( level ) )
			{
				node.verifyNeighbors();
			}

			int minTime = this.minTime;
			int maxTime = this.maxTime;
			setGraph( graph );
			this.minTime = minTime;
			this.maxTime = maxTime;

			updateNodesWithinTimeframe();
			forceSubgraphRefresh();
			updateNodeSize();
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
	}

	/** The history. */
	private List<List<DNVEntity>> history = new ArrayList<List<DNVEntity>>();

	/** The history index. */
	private int historyIndex = 0;

	/**
	 * Checks if is debug.
	 * 
	 * @return true, if is debug
	 */
	public boolean isDebug()
	{
		return Settings.SHOW_DEBUG_FIELDS;
	}

	/**
	 * Gets the disable client side.
	 * 
	 * @return the disable client side
	 */
	public String getDisableClientSide()
	{
		if( isRenderImage() )
		{
			return "disabled";
		}

		return "";
	}

	/**
	 * Checks if is outlined labels.
	 * 
	 * @return true, if is outlined labels
	 */
	public boolean isOutlinedLabels()
	{
		return outlinedLabels;
	}

	/**
	 * Sets the outlined labels.
	 * 
	 * @param outlinedLabels
	 *            the new outlined labels
	 */
	public void setOutlinedLabels( boolean outlinedLabels )
	{
		this.outlinedLabels = outlinedLabels;
	}

	/**
	 * Checks if is appearance expanded.
	 * 
	 * @return true, if is appearance expanded
	 */
	public boolean isAppearanceExpanded()
	{
		return appearanceExpanded;
	}

	/**
	 * Sets the appearance expanded.
	 * 
	 * @param appearanceExpanded
	 *            the new appearance expanded
	 */
	public void setAppearanceExpanded( boolean appearanceExpanded )
	{
		this.appearanceExpanded = appearanceExpanded;
	}

	/**
	 * Expand appearance.
	 */
	public void expandAppearance()
	{
		setAppearanceExpanded( true );
	}

	/**
	 * Collapse appearance.
	 */
	public void collapseAppearance()
	{
		setAppearanceExpanded( false );
	}

	/**
	 * Checks if is search expanded.
	 * 
	 * @return true, if is search expanded
	 */
	public boolean isSearchExpanded()
	{
		return searchExpanded;
	}

	/**
	 * Sets the search expanded.
	 * 
	 * @param appearanceExpanded
	 *            the new search expanded
	 */
	public void setSearchExpanded( boolean appearanceExpanded )
	{
		this.searchExpanded = appearanceExpanded;
	}

	/**
	 * Expand search.
	 */
	public void expandSearch()
	{
		setSearchExpanded( true );
	}

	/**
	 * Collapse search.
	 */
	public void collapseSearch()
	{
		setSearchExpanded( false );
	}

	/**
	 * Checks if is labels expanded.
	 * 
	 * @return true, if is labels expanded
	 */
	public boolean isLabelsExpanded()
	{
		return labelsExpanded;
	}

	/**
	 * Sets the labels expanded.
	 * 
	 * @param labelsExpanded
	 *            the new labels expanded
	 */
	public void setLabelsExpanded( boolean labelsExpanded )
	{
		this.labelsExpanded = labelsExpanded;
	}

	/**
	 * Expand labels.
	 */
	public void expandLabels()
	{
		setLabelsExpanded( true );
	}

	/**
	 * Collapse labels.
	 */
	public void collapseLabels()
	{
		setLabelsExpanded( false );
	}

	/**
	 * Collapse facebook.
	 */
	public void collapseFacebook()
	{
		setFacebookExpanded( false );
	}

	/**
	 * Checks if is facebook expanded.
	 * 
	 * @return true, if is facebook expanded
	 */
	public boolean isFacebookExpanded()
	{
		return facebookExpanded;
	}

	/**
	 * Sets the facebook expanded.
	 * 
	 * @param facebookExpanded
	 *            the new facebook expanded
	 */
	public void setFacebookExpanded( boolean facebookExpanded )
	{
		this.facebookExpanded = facebookExpanded;
	}

	/**
	 * Expand facebook.
	 */
	public void expandFacebook()
	{
		setFacebookExpanded( true );
	}

	/**
	 * Checks if is client server expanded.
	 * 
	 * @return true, if is client server expanded
	 */
	public boolean isClientServerExpanded()
	{
		return clientServerExpanded;
	}

	/**
	 * Sets the client server expanded.
	 * 
	 * @param clientServerExpanded
	 *            the new client server expanded
	 */
	public void setClientServerExpanded( boolean clientServerExpanded )
	{
		this.clientServerExpanded = clientServerExpanded;
	}

	/**
	 * Expand client server.
	 */
	public void expandClientServer()
	{
		setClientServerExpanded( true );
	}

	/**
	 * Collapse client server.
	 */
	public void collapseClientServer()
	{
		setClientServerExpanded( false );
	}

	/**
	 * Checks if is client side expanded.
	 * 
	 * @return true, if is client side expanded
	 */
	public boolean isClientSideExpanded()
	{
		return clientSideExpanded;
	}

	/**
	 * Sets the client side expanded.
	 * 
	 * @param clientSideExpanded
	 *            the new client side expanded
	 */
	public void setClientSideExpanded( boolean clientSideExpanded )
	{
		this.clientSideExpanded = clientSideExpanded;
	}

	/**
	 * Expand client side.
	 */
	public void expandClientSide()
	{
		setClientSideExpanded( true );
	}

	/**
	 * Collapse client side.
	 */
	public void collapseClientSide()
	{
		setClientSideExpanded( false );
	}

	/**
	 * Checks if is server side expanded.
	 * 
	 * @return true, if is server side expanded
	 */
	public boolean isServerSideExpanded()
	{
		return serverSideExpanded;
	}

	/**
	 * Sets the server side expanded.
	 * 
	 * @param serverSideExpanded
	 *            the new server side expanded
	 */
	public void setServerSideExpanded( boolean serverSideExpanded )
	{
		this.serverSideExpanded = serverSideExpanded;
	}

	/**
	 * Expand server side.
	 */
	public void expandServerSide()
	{
		setServerSideExpanded( true );
	}

	/**
	 * Collapse server side.
	 */
	public void collapseServerSide()
	{
		setServerSideExpanded( false );
	}

	/**
	 * Checks if is clustering expanded.
	 * 
	 * @return true, if is clustering expanded
	 */
	public boolean isClusteringExpanded()
	{
		return clusteringExpanded;
	}

	/**
	 * Sets the clustering expanded.
	 * 
	 * @param clusteringExpanded
	 *            the new clustering expanded
	 */
	public void setClusteringExpanded( boolean clusteringExpanded )
	{
		this.clusteringExpanded = clusteringExpanded;
	}

	/**
	 * Expand clustering.
	 */
	public void expandClustering()
	{
		setClusteringExpanded( true );
	}

	/**
	 * Collapse clustering.
	 */
	public void collapseClustering()
	{
		setClusteringExpanded( false );
	}

	/**
	 * Checks if is interaction expanded.
	 * 
	 * @return true, if is interaction expanded
	 */
	public boolean isInteractionExpanded()
	{
		return interactionExpanded;
	}

	/**
	 * Sets the interaction expanded.
	 * 
	 * @param interactionExpanded
	 *            the new interaction expanded
	 */
	public void setInteractionExpanded( boolean interactionExpanded )
	{
		this.interactionExpanded = interactionExpanded;
	}

	/**
	 * Expand interaction.
	 */
	public void expandInteraction()
	{
		setInteractionExpanded( true );
	}

	/**
	 * Collapse interaction.
	 */
	public void collapseInteraction()
	{
		setInteractionExpanded( false );
	}

	/**
	 * Checks if is data expanded.
	 * 
	 * @return true, if is data expanded
	 */
	public boolean isDataExpanded()
	{
		return dataExpanded;
	}

	/**
	 * Sets the data expanded.
	 * 
	 * @param dataExpanded
	 *            the new data expanded
	 */
	public void setDataExpanded( boolean dataExpanded )
	{
		this.dataExpanded = dataExpanded;
	}

	/**
	 * Expand data.
	 */
	public void expandData()
	{
		setDataExpanded( true );
	}

	/**
	 * Collapse data.
	 */
	public void collapseData()
	{
		setDataExpanded( false );
	}

	/**
	 * Checks if is interpolation test.
	 * 
	 * @return true, if is interpolation test
	 */
	public boolean isInterpolationTest()
	{
		return isRenderJS() && Settings.INTERPOLATION_TEST;
	}

	/**
	 * Checks if is image moving test.
	 * 
	 * @return true, if is image moving test
	 */
	public boolean isImageMovingTest()
	{
		return isRenderJS() && Settings.IMAGE_MOVING_TEST;
	}

	/**
	 * Checks if is client side test.
	 * 
	 * @return true, if is client side test
	 */
	public boolean isClientSideTest()
	{
		return isRenderJS() && Settings.CLIENT_SIDE_TEST;
	}

	/**
	 * Checks if is server side test.
	 * 
	 * @return true, if is server side test
	 */
	public boolean isServerSideTest()
	{
		return isRenderImage() && Settings.SERVER_SIDE_TEST;
	}

	/**
	 * Checks if is interpolation labels.
	 * 
	 * @return true, if is interpolation labels
	 */
	public boolean isInterpolationLabels()
	{
		return interpolationLabels;
	}

	/**
	 * Sets the interpolation labels.
	 * 
	 * @param interpLabels
	 *            the new interpolation labels
	 */
	public void setInterpolationLabels( boolean interpLabels )
	{
		interpolationLabels = interpLabels;
	}

	/**
	 * Checks if is interpolation method use actual edge distance.
	 * 
	 * @return true, if is interpolation method use actual edge distance
	 */
	public boolean isInterpolationMethodUseActualEdgeDistance()
	{
		return interpolationMethodUseActualEdgeDistance;
	}

	/**
	 * Sets the interpolation method use actual edge distance.
	 * 
	 * @param interpolationMethodUseActualEdgeDistance
	 *            the new interpolation method use actual edge distance
	 */
	public void setInterpolationMethodUseActualEdgeDistance( boolean interpolationMethodUseActualEdgeDistance )
	{
		this.interpolationMethodUseActualEdgeDistance = interpolationMethodUseActualEdgeDistance;
	}

	// =========================================
	// SEARCH
	// =========================================
	/** The search input text. */
	private String searchInputText = "";

	/** The search results text. */
	private String searchResultsText = "";

	/** The search results. */
	private ArrayList<DNVNode> searchResults = new ArrayList<DNVNode>();

	/** The res table rendered. */
	private boolean resTableRendered = false;

	/** The show search selected labels. */
	private boolean showSearchSelectedLabels = true;

	/** The max search results shown. */
	private int maxSearchResultsShown = 10;

	/** The select all check box. */
	private boolean selectAllCheckBox = false;

	/** The search include full label. */
	private boolean searchIncludeFullLabel = true;

	/** The search include contents. */
	private boolean searchIncludeContents = true;

	// --------------------
	// getters & setters
	// --------------------
	/**
	 * Checks if is select all check box.
	 * 
	 * @return true, if is select all check box
	 */
	public boolean isSelectAllCheckBox()
	{
		return selectAllCheckBox;
	}

	/**
	 * Sets the select all check box.
	 * 
	 * @param selectAllCheckBox
	 *            the new select all check box
	 */
	public void setSelectAllCheckBox( boolean selectAllCheckBox )
	{
		this.selectAllCheckBox = selectAllCheckBox;
	}

	/**
	 * Toggle select all check box.
	 */
	public void toggleSelectAllCheckBox()
	{
		setSelectAllCheckBox( !isSelectAllCheckBox() );
		selectAllCheckBox_action();
	}

	/**
	 * Gets the max search results shown.
	 * 
	 * @return the max search results shown
	 */
	public int getMaxSearchResultsShown()
	{
		return maxSearchResultsShown;
	}

	/**
	 * Sets the max search results shown.
	 * 
	 * @param maxSearchResultsShown
	 *            the new max search results shown
	 */
	public void setMaxSearchResultsShown( int maxSearchResultsShown )
	{
		this.maxSearchResultsShown = maxSearchResultsShown;
	}

	/**
	 * Checks if is show search selected labels.
	 * 
	 * @return true, if is show search selected labels
	 */
	public boolean isShowSearchSelectedLabels()
	{
		return showSearchSelectedLabels;
	}

	/**
	 * Sets the show search selected labels.
	 * 
	 * @param showSearchSelectedLabels
	 *            the new show search selected labels
	 */
	public void setShowSearchSelectedLabels( boolean showSearchSelectedLabels )
	{
		this.showSearchSelectedLabels = showSearchSelectedLabels;
	}

	/** The max number of selected labels. */
	private int maxNumberOfSelectedLabels = Settings.DEFAULT_MAX_NUMBER_OF_SELECTED_LABELS;

	/**
	 * Gets the max number of selected labels.
	 * 
	 * @return the max number of selected labels
	 */
	public int getMaxNumberOfSelectedLabels()
	{
		return maxNumberOfSelectedLabels;
	}

	/**
	 * Sets the max number of selected labels.
	 * 
	 * @param maxNumberOfSelectedLabels
	 *            the new max number of selected labels
	 */
	public void setMaxNumberOfSelectedLabels( int maxNumberOfSelectedLabels )
	{
		this.maxNumberOfSelectedLabels = maxNumberOfSelectedLabels;
	}

	/**
	 * Gets the search input text.
	 * 
	 * @return the search input text
	 */
	public String getSearchInputText()
	{
		String tempInputText = Request.getStringParameter( "searchQuery" );
		if( tempInputText != null && tempInputText.equals( "" ) )
		{
			graph.deselectAllNodes( level );
		}
		if( tempInputText == null || tempInputText.equals( "" ) )
		{
			String inputFile = Request.getStringParameter( "searchQueryFile" );
			if( inputFile != null && !inputFile.equals( "" ) )
			{
				try
				{
					tempInputText = FileOperations.readFile( inputFile );
				}
				catch( IOException ioe )
				{
					ioe.printStackTrace();
					tempInputText = null;
				}
			}
		}
		if( tempInputText != null && !searchInputText.equals( tempInputText ) )
		{
			searchInputText = tempInputText.replace( "\\n", "\n" );
			search();
		}
		
		return searchInputText;
	}

	/**
	 * Sets the search input text.
	 * 
	 * @param searchInputText
	 *            the new search input text
	 */
	public void setSearchInputText( String searchInputText )
	{
		this.searchInputText = searchInputText;
	}

	/**
	 * Gets the search results text.
	 * 
	 * @return the search results text
	 */
	public String getSearchResultsText()
	{
		return searchResultsText;
	}

	/**
	 * Sets the search results text.
	 * 
	 * @param searchResultsText
	 *            the new search results text
	 */
	public void setSearchResultsText( String searchResultsText )
	{
		this.searchResultsText = searchResultsText;
	}

	/**
	 * Gets the search results.
	 * 
	 * @return the search results
	 */
	public ArrayList<DNVNode> getSearchResults()
	{
		return searchResults;
	}

	/**
	 * Sets the search results.
	 * 
	 * @param searchResults
	 *            the new search results
	 */
	public void setSearchResults( ArrayList<DNVNode> searchResults )
	{
		this.searchResults = searchResults;
	}

	/**
	 * Checks if is res table rendered.
	 * 
	 * @return true, if is res table rendered
	 */
	public boolean isResTableRendered()
	{
		return resTableRendered;
	}

	/**
	 * Sets the res table rendered.
	 * 
	 * @param resTableRendered
	 *            the new res table rendered
	 */
	public void setResTableRendered( boolean resTableRendered )
	{
		this.resTableRendered = resTableRendered;
	}

	// --------------------

	/**
	 * Search.
	 */
	public void search()
	{
		synchronized( graph )
		{
			// long startTime = System.currentTimeMillis();
			System.out.println( "Performing search for '" + searchInputText + "'" );
	
			resetSearchResults();
			resetZoomWindow();
	
			// a temporary hashmap storing results to avoid adding same node twice
			Map<Integer,DNVNode> results = new HashMap<Integer,DNVNode>();
			
			if( !searchInputText.trim().equals( "" ) )
			{
				DNVNode tempNode;
				String label = "";
				String contents;
				String[] splitText = searchInputText.split( "\n" );
				if( splitText.length == 1 )
				{
					splitText = searchInputText.split( " OR " );
					if( splitText.length == 1 )
					{
						splitText = searchInputText.split( " or " );
					}
				}
	
				String originalInput = searchInputText;
				for( int i = 0; i < splitText.length; i++ )
				{
					searchInputText = splitText[i];
					searchInputText = searchInputText.replaceAll( "\n", "" );
					System.out.println( "searching for [" + searchInputText + "]" );
					if( !searchInputText.equals( "" ) )
					{
						for( Iterator<DNVNode> nodes = graph.getNodes( level ).iterator(); nodes.hasNext(); )
						{
							tempNode = nodes.next();
	
							label = tempNode.getLabel();
							if( searchIncludeFullLabel )
							{
								if( tempNode.getProperty( "Full Label" ) != null )
								{
									label += " " + tempNode.getProperty( "Full Label" );
								}
							}
	
							if( label.toLowerCase().contains( searchInputText.toLowerCase() ) )
							{
								tempNode.setSelected( true );
								results.put( tempNode.getId(), tempNode );
							}
							else if( searchIncludeContents )
							{
								contents = tempNode.getProperty( "Contents" );
								if( contents != null && !contents.equals( "" ) )
								{
									if( contents.toLowerCase().contains( searchInputText.toLowerCase() ) )
									{
										tempNode.setSelected( true );
										results.put( tempNode.getId(), tempNode );
									}
								}
							}
						}
					}
				}
				searchInputText = originalInput;
				// System.out.println("\nsearch query: " + searchInputText + ", " +
				// searchResults.size() + " results");
	
				// Add results from hashmap to results list
				for( DNVNode node : results.values() )
				{
					searchResults.add( node );
				}
				
				searchResultsText = searchResults.size() + " results";
	
				// selected node
				if( searchResults.size() > 0 )
				{
					resTableRendered = true;
					selectAllCheckBox = true;
	
					forceSubgraphRefresh();
	
					// searchResults.get( 0 ).setSelected( true );
				}
			}
	
			System.out.println( "Search completed" );
	
			// printTime( "search()", startTime );
		}
	}

	/**
	 * Reset search results.
	 */
	private void resetSearchResults()
	{
		// unselect all nodes
		for( int i = 0; i < searchResults.size(); i++ )
			searchResults.get( i ).setSelected( false );

		selectAllCheckBox = false;

		searchResults.clear();
		searchResultsText = "";
	}

	/**
	 * Select all.
	 */
	public void selectAll()
	{
		long startTime = System.currentTimeMillis();
		HashSet<String> sNodes = new HashSet<String>();
		for( int i = 0; i < searchResults.size(); i++ )
		{
			searchResults.get( i ).setSelected( true );
			sNodes.add( searchResults.get( i ).getBbId() );
			// System.out.println( "selecting node: " + searchResults.get( i
			// ).getBbId() );
		}

		setGraphChanged( true );
		printTime( "selectAll", startTime );
	}

	/**
	 * Unselect all.
	 */
	public void unselectAll()
	{
		long startTime = System.currentTimeMillis();
		System.out.println( "unselecting all search nodes" );
		HashSet<String> sNodes = new HashSet<String>();
		for( int i = 0; i < searchResults.size(); i++ )
		{
			searchResults.get( i ).setSelected( false );
			sNodes.add( searchResults.get( i ).getBbId() );
			// System.out.println( "deselecting node: " + searchResults.get( i
			// ).getBbId() );
		}

		setGraphChanged( true );
		printTime( "unselectAll", startTime );
	}

	/**
	 * Select all check box_action.
	 */
	public void selectAllCheckBox_action()
	{
		if( selectAllCheckBox )
			selectAll();
		else
			unselectAll();
	}

	// =========================================
	/**
	 * Toggle outlined labels.
	 */
	public void toggleOutlinedLabels()
	{
		outlinedLabels = !outlinedLabels;
	}

	/**
	 * Toggle show icons.
	 */
	public void toggleShowIcons()
	{
		showIcons = !showIcons;
	}

	/**
	 * Toggle show labels.
	 */
	public void toggleShowLabels()
	{
		showLabels = !showLabels;
	}

	/**
	 * Toggle curved labels.
	 */
	public void toggleCurvedLabels()
	{
		curvedLabels = !curvedLabels;
	}

	/**
	 * Toggle distance labels.
	 */
	public void toggleDistanceLabels()
	{
		interpolationLabels = !interpolationLabels;
	}

	/**
	 * Toggle interpolation method use actual edge distance.
	 */
	public void toggleInterpolationMethodUseActualEdgeDistance()
	{
		interpolationMethodUseActualEdgeDistance = !interpolationMethodUseActualEdgeDistance;
	}

	/**
	 * Toggle interpolation method use whole graph.
	 */
	public void toggleInterpolationMethodUseWholeGraph()
	{
		interpolationMethodUseWholeGraph = !interpolationMethodUseWholeGraph;
	}

	/**
	 * Checks if is show max layout time.
	 * 
	 * @return true, if is show max layout time
	 */
	public boolean isShowMaxLayoutTime()
	{
		return layoutMethod instanceof TimeLimitedLayoutInterface;
	}

	/**
	 * Checks if is show cooling factor.
	 * 
	 * @return true, if is show cooling factor
	 */
	public boolean isShowCoolingFactor()
	{
		return layoutMethod instanceof SpaceRestrictedLayoutInterface;
	}

	/**
	 * Checks if is layout all levels.
	 * 
	 * @return true, if is layout all levels
	 */
	public boolean isLayoutAllLevels()
	{
		return layoutAllLevels;
	}

	/**
	 * Sets the layout all levels.
	 * 
	 * @param layoutAllLevels
	 *            the new layout all levels
	 */
	public void setLayoutAllLevels( boolean layoutAllLevels )
	{
		this.layoutAllLevels = layoutAllLevels;
	}

	/**
	 * Checks if is cluster before layout.
	 * 
	 * @return true, if is cluster before layout
	 */
	public boolean isClusterBeforeLayout()
	{
		return clusterBeforeLayout;
	}

	/**
	 * Sets the cluster before layout.
	 * 
	 * @param clusterBeforeLayout
	 *            the new cluster before layout
	 */
	public void setClusterBeforeLayout( boolean clusterBeforeLayout )
	{
		this.clusterBeforeLayout = clusterBeforeLayout;
	}

	/**
	 * Toggle layout all levels.
	 */
	public void toggleLayoutAllLevels()
	{
		layoutAllLevels = !layoutAllLevels;
	}

	/**
	 * Toggle cluster before layout.
	 */
	public void toggleClusterBeforeLayout()
	{
		clusterBeforeLayout = !clusterBeforeLayout;
	}

	/**
	 * Gets the cooling factor.
	 * 
	 * @return the cooling factor
	 */
	public float getCoolingFactor()
	{
		return coolingFactor;
	}

	/**
	 * Sets the cooling factor.
	 * 
	 * @param coolingFactor
	 *            the new cooling factor
	 */
	public void setCoolingFactor( float coolingFactor )
	{
		this.coolingFactor = coolingFactor;
	}

	/**
	 * Drag selected nodes.
	 */
	public void dragSelectedNodes()
	{
		if( graph.getSelectedNodes( Integer.valueOf( level + "" ) ).size() > MAX_DRAG_NODE )
		{
			setupErrorMessage( "Please select 10 or fewer nodes to move", true );
			return;
		}

		try
		{
			for( DNVNode selectedNode : graph.getSelectedNodes( level ).values() )
			{
				dragNode( selectedNode );
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
			setupErrorMessage( "Error occured while moving nodes", false );
		}
	}

	/**
	 * Gets the selected nodes.
	 * 
	 * @return the selected nodes
	 */
	public List<DNVNode> getSelectedNodes()
	{
		try
		{
			return new ArrayList<DNVNode>( graph.getSelectedNodes( level ).values() );
		}
		catch( Exception e )
		{
			e.printStackTrace();
			setupErrorMessage( "Error while retrieving selected nodes.", false );
		}
		return new ArrayList<DNVNode>();
	}

	/**
	 * Drag node.
	 * 
	 * @param selectedNode
	 *            the selected node
	 */
	private void dragNode( DNVNode selectedNode )
	{
		Vector2D worldPos = new Vector2D( selectedNode.getPosition() );
		worldPos.setX( worldPos.getX() + 2 * (float)( globalMaxX - globalMinX ) );
		setSelectedNode( selectedNode, true );
		InterpolationMethod.selectNode( this, graph, (float)numberAffected, level, selectedNode );
		Vector2D screenPos = ImageRenderer.transformPosition( globalMinX, globalMaxX, globalMinY, globalMaxY, minX, maxX, minY, maxY, width, height,
				worldPos );
		// System.out.println( "Moving node '" + selectedNode.getLabel() +
		// "' to " + screenPos );
		new InterpolationMethod().performInteraction( this, graph, width, height, minX, minY, maxX, maxY, (int)screenPos.getX(), (int)screenPos.getY(), false,
				level, globalMinX, globalMaxX, globalMinY, globalMaxY, selectedNode, false );
		// GraphServlet.moveNode( graph, level, selectedNode, worldPos );
	}

	/** The number of most highly connected nodes to select. */
	private double numberOfMostHighlyConnectedNodesToSelect = 1;

	/**
	 * Gets the number of most highly connected nodes to select.
	 * 
	 * @return the number of most highly connected nodes to select
	 */
	public double getNumberOfMostHighlyConnectedNodesToSelect()
	{
		return numberOfMostHighlyConnectedNodesToSelect;
	}

	/**
	 * Sets the number of most highly connected nodes to select.
	 * 
	 * @param numberOfMostHighlyConnectedNodesToSelect
	 *            the new number of most highly connected nodes to select
	 */
	public void setNumberOfMostHighlyConnectedNodesToSelect( double numberOfMostHighlyConnectedNodesToSelect )
	{
		this.numberOfMostHighlyConnectedNodesToSelect = numberOfMostHighlyConnectedNodesToSelect;
	}

	/** The number of neighbor sort. */
	private static NumberOfNeighborSort numberOfNeighborSort = new NumberOfNeighborSort();

	/**
	 * Select n most highly connected nodes.
	 */
	public void selectNMostHighlyConnectedNodes()
	{
		long startTime = System.currentTimeMillis();
		graph.deselectAllNodes( level );
		List<DNVNode> nodes = graph.getNodes( level );
		Collections.sort( nodes, numberOfNeighborSort );
		boolean ctrl = false;
		// System.out.println( "Selecting most highly connected nodes" );
		for( int i = 0; i < numberOfMostHighlyConnectedNodesToSelect && i < nodes.size(); i++ )
		{
			if( i == 1 )
			{
				ctrl = true;
			}
			// System.out.println( "Selecting node '" + nodes.get( i
			// ).getLabel() + "'" );
			setSelectedNode( nodes.get( i ), ctrl );
			// dragNode( nodes.get( i ) );
		}
		printTime( "selectMostHighlyConnected", startTime );
	}

	/**
	 * Checks if is recommendation circle.
	 * 
	 * @return true, if is recommendation circle
	 */
	public boolean isRecommendationCircle()
	{
		return recommendationCircle;
	}

	/**
	 * Sets the recommendation circle.
	 * 
	 * @param recommendationCircle
	 *            the new recommendation circle
	 */
	public void setRecommendationCircle( boolean recommendationCircle )
	{
		this.recommendationCircle = recommendationCircle;
	}

	/**
	 * Checks if is show recommendation circle.
	 * 
	 * @return true, if is show recommendation circle
	 */
	public boolean isShowRecommendationCircle()
	{
		return layoutMethod instanceof RecommendationLayoutInterface;
	}

	/**
	 * Toggle recommendation circle.
	 */
	public void toggleRecommendationCircle()
	{
		recommendationCircle = !recommendationCircle;
	}

	/**
	 * Update number affected.
	 */
	public void updateNumberAffected()
	{
	// numberAffected = graph.getMaximumPairwiseShortestPath( level );
	// numberAffected = (int)Math.round(graph.getEdges( level ).size() /
	// graph.getGraphSize( level ));
	}

	/**
	 * Gets the default js threshold.
	 * 
	 * @return the default js threshold
	 */
	public int getDefaultJsThreshold()
	{
		return Settings.DEFAULT_JS_THRESHOLD;
	}

	/**
	 * Select all nodes.
	 */
	public void selectAllNodes()
	{
		long startTime = System.currentTimeMillis();
		try
		{
			Set<String> uris2Select = new HashSet<String>();
			for( DNVNode node : graph.getNodes( level ) )
			{
				node.setSelected( true );
				uris2Select.add( node.getBbId() );
				this.selectedNode = node;
			}
		}
		catch( Exception e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		setGraphChanged( true );
		printTime( "selectAllNodes", startTime );
	}

	/**
	 * Unselect all nodes and edges.
	 */
	public void unselectAllNodesAndEdges()
	{
		long startTime = System.currentTimeMillis();
		try
		{
			graph.deselectAllNodes( level );
			graph.deselectAllEdges( level );
		}
		catch( Exception e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		setGraphChanged( true );
		printTime( "unselectAllNodes", startTime );
	}

	public String getInteractionMethodLabel()
	{
		return interactionMethod.getLabel();
	}
	
	public void setInteractionMethodLabel( String label )
	{
		setInteractionMethod( interactionMethods.get( label ) );
	}
	
	/**
	 * Gets the interaction method.
	 * 
	 * @return the interaction method
	 */
	public InteractionInterface getInteractionMethod()
	{
		return interactionMethod;
	}

	/**
	 * Sets the interaction method.
	 * 
	 * @param interactionMethod
	 *            the new interaction method
	 */
	public void setInteractionMethod( InteractionInterface interactionMethod )
	{
		this.interactionMethod = interactionMethod;
	}

	/**
	 * Checks if is show toggle interpolation actual edge distance.
	 * 
	 * @return true, if is show toggle interpolation actual edge distance
	 */
	public boolean isShowToggleInterpolationActualEdgeDistance()
	{
		return interactionMethod instanceof InterpolationMethod;
	}

	/**
	 * Checks if is show toggle interpolation whole graph.
	 * 
	 * @return true, if is show toggle interpolation whole graph
	 */
	public boolean isShowToggleInterpolationWholeGraph()
	{
		return interactionMethod instanceof InterpolationMethod;
	}

	/**
	 * Checks if is show interpolation distance selector.
	 * 
	 * @return true, if is show interpolation distance selector
	 */
	public boolean isShowInterpolationDistanceSelector()
	{
		return (interactionMethod instanceof InterpolationMethod) && !interpolationMethodUseWholeGraph;
	}

	/**
	 * Gets the interaction method list.
	 * 
	 * @return the interaction method list
	 */
	public List<SelectItem> getInteractionMethodList()
	{
		return interactionMethodList;
	}

	/**
	 * Sets the interaction method list.
	 * 
	 * @param interactionMethodList
	 *            the new interaction method list
	 */
	public void setInteractionMethodList( List<SelectItem> interactionMethodList )
	{
		this.interactionMethodList = interactionMethodList;
	}

	/**
	 * Checks if is need number of clusters.
	 * 
	 * @return true, if is need number of clusters
	 */
	public boolean isNeedNumberOfClusters()
	{
		return !clusteringMethod.equals( Settings.CONNECTED_CLUSTERING ) && !clusteringMethod.equals( Settings.STRUCTURAL_EQUIVALENCE_CLUSTERING );
	}

	/**
	 * Checks if is selected node details expanded.
	 * 
	 * @return true, if is selected node details expanded
	 */
	public boolean isSelectedNodeDetailsExpanded()
	{
		return selectedNodeDetailsExpanded;
	}

	/**
	 * Sets the selected node details expanded.
	 * 
	 * @param selectedNodeDetailsExpanded
	 *            the new selected node details expanded
	 */
	public void setSelectedNodeDetailsExpanded( boolean selectedNodeDetailsExpanded )
	{
		this.selectedNodeDetailsExpanded = selectedNodeDetailsExpanded;
	}

	/**
	 * Expand selected node details.
	 */
	public void expandSelectedNodeDetails()
	{
		this.selectedNodeDetailsExpanded = true;
	}

	/**
	 * Collapse selected node details.
	 */
	public void collapseSelectedNodeDetails()
	{
		this.selectedNodeDetailsExpanded = false;
	}

	/**
	 * Sets the scale nodes on zoom.
	 * 
	 * @param scaleNodesOnZoom
	 *            the new scale nodes on zoom
	 */
	public void setScaleNodesOnZoom( boolean scaleNodesOnZoom )
	{
		this.scaleNodesOnZoom = scaleNodesOnZoom;
	}

	/**
	 * Checks if is scale nodes on zoom.
	 * 
	 * @return true, if is scale nodes on zoom
	 */
	public boolean isScaleNodesOnZoom()
	{
		return scaleNodesOnZoom;
	}

	/**
	 * Toggle scale nodes on zoom.
	 */
	public void toggleScaleNodesOnZoom()
	{
		scaleNodesOnZoom = !scaleNodesOnZoom;
	}

	/**
	 * Gets the panel width.
	 * 
	 * @return the panel width
	 */
	public int getPanelWidth()
	{
		return panelWidth;
	}

	/**
	 * Sets the panel width.
	 * 
	 * @param panelWidth
	 *            the new panel width
	 */
	public void setPanelWidth( int panelWidth )
	{
		this.panelWidth = panelWidth;
	}

	/*
	 * public void setHasBeenDisplayed( boolean hasBeenDisplayed ) {
	 * this.hasBeenDisplayed = hasBeenDisplayed; }
	 */



	/**
	 * Checks if is sort nodes.
	 * 
	 * @return true, if is sort nodes
	 */
	public boolean isSortNodes()
	{
		return sortNodes;
	}

	/**
	 * Sets the sort nodes.
	 * 
	 * @param sortNodes
	 *            the new sort nodes
	 */
	public void setSortNodes( boolean sortNodes )
	{
		this.sortNodes = sortNodes;
	}


	/**
	 * Gets the node contents.
	 * 
	 * @return the node contents
	 */
	public String getNodeContents()
	{
		int id = Request.getIntegerParameter( "id" );
		DNVNode node = (DNVNode)graph.getNodeById( id );
		String value = "";
		if( node != null )
		{
			value = node.getProperty( "Contents" );
			if( value == null )
			{
				value = "";
			}

			value = StringUtils.replaceAll( value, "\"", "'" );
		}

		return value;
	}

	/**
	 * Gets the node label.
	 * 
	 * @return the node label
	 */
	public String getNodeLabel()
	{
		int id = Request.getIntegerParameter( "id" );
		DNVNode node = (DNVNode)graph.getNodeById( id );
		String value = "";
		if( node != null )
		{
			value = node.getLabel();
			if( value == null )
			{
				value = "";
			}
		}

		return value;
	}

	/**
	 * Checks if is highlight neighbors.
	 * 
	 * @return true, if is highlight neighbors
	 */
	public boolean isHighlightNeighbors()
	{
		return highlightNeighbors;
	}

	/**
	 * Sets the highlight neighbors.
	 * 
	 * @param highlightNeighbors
	 *            the new highlight neighbors
	 */
	public void setHighlightNeighbors( boolean highlightNeighbors )
	{
		this.highlightNeighbors = highlightNeighbors;
	}

	/**
	 * Toggle highlight neighbors.
	 */
	public void toggleHighlightNeighbors()
	{
		setHighlightNeighbors( !isHighlightNeighbors() );
	}

	/**
	 * Checks if is search include full label.
	 * 
	 * @return true, if is search include full label
	 */
	public boolean isSearchIncludeFullLabel()
	{
		return searchIncludeFullLabel;
	}

	/**
	 * Sets the search include full label.
	 * 
	 * @param searchIncludeFullLabel
	 *            the new search include full label
	 */
	public void setSearchIncludeFullLabel( boolean searchIncludeFullLabel )
	{
		if( this.searchIncludeFullLabel != searchIncludeFullLabel )
		{
			this.searchIncludeFullLabel = searchIncludeFullLabel;
			search();
		}
	}

	/**
	 * Checks if is search include contents.
	 * 
	 * @return true, if is search include contents
	 */
	public boolean isSearchIncludeContents()
	{
		return searchIncludeContents;
	}

	/**
	 * Sets the search include contents.
	 * 
	 * @param searchIncludeContents
	 *            the new search include contents
	 */
	public void setSearchIncludeContents( boolean searchIncludeContents )
	{
		if( this.searchIncludeContents != searchIncludeContents )
		{
			this.searchIncludeContents = searchIncludeContents;
			search();
		}
	}

	/**
	 * Toggle search include full label.
	 */
	public void toggleSearchIncludeFullLabel()
	{
		setSearchIncludeFullLabel( !isSearchIncludeFullLabel() );
	}

	/**
	 * Toggle search include contents.
	 */
	public void toggleSearchIncludeContents()
	{
		setSearchIncludeContents( !isSearchIncludeContents() );
	}

	/**
	 * Sets the max label length.
	 * 
	 * @param maxLabelLength
	 *            the new max label length
	 */
	public void setMaxLabelLength( int maxLabelLength )
	{
		this.maxLabelLength = maxLabelLength;
	}

	/**
	 * Gets the max label length.
	 * 
	 * @return the max label length
	 */
	public int getMaxLabelLength()
	{
		return maxLabelLength;
	}

	/**
	 * Sets the curved label angle.
	 * 
	 * @param curvedLabelAngle
	 *            the new curved label angle
	 */
	public void setCurvedLabelAngle( int curvedLabelAngle )
	{
		this.curvedLabelAngle = curvedLabelAngle;
	}

	/**
	 * Gets the curved label angle.
	 * 
	 * @return the curved label angle
	 */
	public int getCurvedLabelAngle()
	{
		return curvedLabelAngle;
	}

	/**
	 * Sets the scale labels.
	 * 
	 * @param scaleLabels
	 *            the new scale labels
	 */
	public void setScaleLabels( boolean scaleLabels )
	{
		this.scaleLabels = scaleLabels;
	}

	/**
	 * Checks if is scale labels.
	 * 
	 * @return true, if is scale labels
	 */
	public boolean isScaleLabels()
	{
		return scaleLabels;
	}

	/**
	 * Toggle scale labels.
	 */
	public void toggleScaleLabels()
	{
		setScaleLabels( !isScaleLabels() );
	}

	/**
	 * Sets the draw label box.
	 * 
	 * @param drawLabelBox
	 *            the new draw label box
	 */
	public void setDrawLabelBox( boolean drawLabelBox )
	{
		this.drawLabelBox = drawLabelBox;
	}

	/**
	 * Checks if is draw label box.
	 * 
	 * @return true, if is draw label box
	 */
	public boolean isDrawLabelBox()
	{
		return drawLabelBox;
	}

	/**
	 * Toggle draw label box.
	 */
	public void toggleDrawLabelBox()
	{
		setDrawLabelBox( !isDrawLabelBox() );
	}

	/**
	 * Sets the hide conflicting labels.
	 * 
	 * @param hideConflictingLabels
	 *            the new hide conflicting labels
	 */
	public void setHideConflictingLabels( boolean hideConflictingLabels )
	{
		this.hideConflictingLabels = hideConflictingLabels;
		if( !hideConflictingLabels )
		{
			for( DNVNode node : graph.getNodes( level ) )
			{
				node.removeProperty( "faded" );
			}
		}
	}

	/**
	 * Checks if is hide conflicting labels.
	 * 
	 * @return true, if is hide conflicting labels
	 */
	public boolean isHideConflictingLabels()
	{
		return hideConflictingLabels;
	}

	/**
	 * Toggle hide conflicting labels.
	 */
	public void toggleHideConflictingLabels()
	{
		setHideConflictingLabels( !isHideConflictingLabels() );
	}

	/**
	 * Checks if is bold labels.
	 * 
	 * @return true, if is bold labels
	 */
	public boolean isBoldLabels()
	{
		return boldLabels;
	}

	/**
	 * Sets the bold labels.
	 * 
	 * @param boldLabels
	 *            the new bold labels
	 */
	public void setBoldLabels( boolean boldLabels )
	{
		this.boldLabels = boldLabels;
	}

	/**
	 * Toggle bold labels.
	 */
	public void toggleBoldLabels()
	{
		setBoldLabels( !isBoldLabels() );
	}

	/**
	 * Sets the fr use edge resting distance.
	 * 
	 * @param frUseEdgeRestingDistance
	 *            the new fr use edge resting distance
	 */
	public void setFrUseEdgeRestingDistance( boolean frUseEdgeRestingDistance )
	{
		this.frUseEdgeRestingDistance = frUseEdgeRestingDistance;
	}

	/**
	 * Checks if is fr use edge resting distance.
	 * 
	 * @return true, if is fr use edge resting distance
	 */
	public boolean isFrUseEdgeRestingDistance()
	{
		return frUseEdgeRestingDistance;
	}

	/**
	 * Toggle fr use edge resting distance.
	 */
	public void toggleFrUseEdgeRestingDistance()
	{
		setFrUseEdgeRestingDistance( !isFrUseEdgeRestingDistance() );
	}

	/**
	 * Gets the fade factor.
	 * 
	 * @return the fade factor
	 */
	public float getFadeFactor()
	{
		return fadeFactor;
	}

	/**
	 * Sets the fade factor.
	 * 
	 * @param fadeFactor
	 *            the new fade factor
	 */
	public void setFadeFactor( float fadeFactor )
	{
		this.fadeFactor = fadeFactor;
	}

	/**
	 * Gets the white space buffer.
	 * 
	 * @return the white space buffer
	 */
	public double getWhiteSpaceBuffer()
	{
		return whiteSpaceBuffer;
	}

	/**
	 * Sets the white space buffer.
	 * 
	 * @param whiteSpaceBuffer
	 *            the new white space buffer
	 */
	public void setWhiteSpaceBuffer( double whiteSpaceBuffer )
	{
		this.whiteSpaceBuffer = whiteSpaceBuffer;
	}

	/**
	 * Checks if is fr use node size.
	 * 
	 * @return true, if is fr use node size
	 */
	public boolean isFrUseNodeSize()
	{
		return frUseNodeSize;
	}

	/**
	 * Sets the fr use node size.
	 * 
	 * @param frUseNodeSize
	 *            the new fr use node size
	 */
	public void setFrUseNodeSize( boolean frUseNodeSize )
	{
		this.frUseNodeSize = frUseNodeSize;
	}

	/**
	 * Toggle fr use node size.
	 */
	public void toggleFrUseNodeSize()
	{
		setFrUseNodeSize( !isFrUseNodeSize() );
	}

	/**
	 * Gets the seconds per animation.
	 * 
	 * @return the seconds per animation
	 */
	public float getSecondsPerAnimation()
	{
		return secondsPerAnimation;
	}

	/**
	 * Sets the seconds per animation.
	 * 
	 * @param secondsPerAnimation
	 *            the new seconds per animation
	 */
	public void setSecondsPerAnimation( float secondsPerAnimation )
	{
		this.secondsPerAnimation = secondsPerAnimation;
	}

	/**
	 * Gets the err msg.
	 * 
	 * @return the err msg
	 */
	public String getErrMsg()
	{
		String msg = "";
		try
		{
			for( String m : errorMsgList )
			{
				if( errorTimeMap.get( m ) > System.currentTimeMillis() )
				{
					if( msg.isEmpty() )
					{
						msg = m;
					}
					else
					{
						msg = msg + ", " + m;
					}
				}
				else
				{
					errorTimeMap.remove( m );
					errorMsgList.remove( m );
					errorOverrideMap.remove( m );
				}
			}
		}
		catch( Exception e )
		{
			// noop
		}
		return msg;
	}

	/**
	 * Gets the status msg.
	 * 
	 * @return the status msg
	 */
	public String getStatusMsg()
	{
		String msg = "";
		try
		{
			for( String m : statusMsgList )
			{
				if( statusMsgMap.get( m ) > System.currentTimeMillis() )
				{
					if( msg.isEmpty() )
					{
						msg = m;
					}
					else
					{
						msg = msg + ", " + m;
					}
				}
				else
				{
					statusMsgMap.remove( m );
					statusMsgList.remove( m );
				}
			}
		}
		catch( Exception e )
		{
			// noop
		}

		return msg;
	}

	/**
	 * Checks if is graph changed.
	 * 
	 * @return true, if is graph changed
	 */
	public boolean isGraphChanged()
	{
		return graphChanged;
	}

	// to be called by PaintBean users like BlackbookBean
	/**
	 * Checks if is wigi graph changed.
	 * 
	 * @return true, if is wigi graph changed
	 */
	public boolean isWigiGraphChanged()
	{
		if( graphChanged )
		{
			graphChanged = false;
			return true;
		}
		return false;
	}

	/**
	 * Sets the graph changed.
	 * 
	 * @param graphChanged
	 *            the new graph changed
	 */
	public void setGraphChanged( boolean graphChanged )
	{
		this.graphChanged = graphChanged;
		setupStatusMessage( UNSAVED_CHANGES_MSG );
	}

	/**
	 * Sets the up error message.
	 * 
	 * @param msg
	 *            the new up error message
	 */
	public void setupErrorMessage( String msg )
	{
		setupErrorMessage( msg, ERROR_MSG_LIFE, false );
	}

	/**
	 * Setup error message.
	 * 
	 * @param msg
	 *            the msg
	 * @param time
	 *            the time
	 */
	public void setupErrorMessage( String msg, long time )
	{
		setupErrorMessage( msg, time, false );
	}

	/**
	 * Setup error message.
	 * 
	 * @param msg
	 *            the msg
	 * @param canBeOverridden
	 *            the can be overridden
	 */
	public void setupErrorMessage( String msg, boolean canBeOverridden )
	{
		setupErrorMessage( msg, ERROR_MSG_LIFE, canBeOverridden );
	}

	/**
	 * Setup error message.
	 * 
	 * @param msg
	 *            the msg
	 * @param msgTime
	 *            the msg time
	 * @param canBeOverridden
	 *            the can be overridden
	 */
	public void setupErrorMessage( String msg, long msgTime, boolean canBeOverridden )
	{
		if( errorMsgList.contains( msg ) )
		{
			errorMsgList.remove( msg );
			errorTimeMap.remove( msg );
			errorOverrideMap.remove( msg );
		}

		errorMsgList.add( msg );
		errorTimeMap.put( msg, System.currentTimeMillis() + msgTime );
		errorOverrideMap.put( msg, canBeOverridden );
	}

	/**
	 * Force the removal of a msg before the timer expires.
	 * 
	 * @param msg
	 *            the msg
	 */
	public void removeErrorMessage( String msg )
	{
		if( msg != null )
		{
			errorMsgList.remove( msg );
			errorTimeMap.remove( msg );
			errorOverrideMap.remove( msg );
		}
	}

	/**
	 * Sets the up status message.
	 * 
	 * @param msg
	 *            the new up status message
	 */
	public void setupStatusMessage( String msg )
	{
		setupStatusMessage( msg, PaintBean.STATUS_MSG_LIFE );
	}

	/**
	 * Add a status message to be displayed to the user.
	 * 
	 * @param msg
	 *            - the message to display
	 * @param msgLife
	 *            - the time/date to expire (usually System.currentTimeMillis()
	 *            + some milliseconds).
	 */
	public void setupStatusMessage( String msg, long msgLife )
	{
		if( statusMsgList.contains( msg ) )
		{
			statusMsgList.remove( msg );
			statusMsgMap.remove( msg );
		}

		statusMsgList.add( msg );
		statusMsgMap.put( msg, System.currentTimeMillis() + msgLife );
	}

	/**
	 * Force the removal of a msg before the timer expires.
	 * 
	 * @param msg
	 *            the msg
	 */
	public void removeStatusMessage( String msg )
	{
		if( msg != null )
		{
			statusMsgList.remove( msg );
			statusMsgMap.remove( msg );
		}
	}

	/**
	 * Prints the time.
	 * 
	 * @param methodName
	 *            the method name
	 * @param startTime
	 *            the start time
	 */
	private static void printTime( String methodName, long startTime )
	{
	// System.out.println( methodName + " took " + ( System.currentTimeMillis()
	// - startTime ) / 1000.0 + " sec." );
	}

	/**
	 * Sets the time expanded.
	 * 
	 * @param timeExpanded
	 *            the new time expanded
	 */
	public void setTimeExpanded( boolean timeExpanded )
	{
		this.timeExpanded = timeExpanded;
	}

	/**
	 * Checks if is time expanded.
	 * 
	 * @return true, if is time expanded
	 */
	public boolean isTimeExpanded()
	{
		return timeExpanded;
	}

	/**
	 * Collapse time.
	 */
	public void collapseTime()
	{
		setTimeExpanded( false );
	}

	/**
	 * Expand time.
	 */
	public void expandTime()
	{
		setTimeExpanded( true );
	}

	/**
	 * Checks if is show time selector.
	 * 
	 * @return true, if is show time selector
	 */
	public boolean isShowTimeSelector()
	{
		if( graph.getMaxTime( level ) == Long.MIN_VALUE )
		{
			return false;
		}

		return true;
	}

	/**
	 * Sets the min time.
	 * 
	 * @param minTime
	 *            the new min time
	 */
	public void setMinTime( int minTime )
	{
		if( minTime != this.minTime )
		{
			this.minTime = minTime;
			updateNodesWithinTimeframe();
		}
	}

	private Text timeText;
	/**
	 * Update nodes within timeframe.
	 */
	private void updateNodesWithinTimeframe()
	{
		synchronized( graph )
		{
			String timeStr;
			for( DNVNode node : graph.getNodes( level ) )
			{
				timeStr = node.getProperty( "minTime" );
				if( timeStr != null )
				{
					if( Long.parseLong( timeStr ) < allTimes.get(  minTime ) )
					{
						node.setVisible( false );
						continue;
					}
				}
				timeStr = node.getProperty( "maxTime" );
				if( timeStr != null )
				{
					if( Long.parseLong( timeStr ) > allTimes.get(  maxTime ) )
					{
						node.setVisible( false );
						continue;
					}
				}
				
				timeStr = node.getProperty( "time" );
				if( timeStr == null || timeStr.equals( "" )
						|| ( Long.parseLong( timeStr ) >= allTimes.get( minTime ) && Long.parseLong( timeStr ) <= allTimes.get( maxTime ) ) )
				{
					node.setVisible( true );
				}
				else
				{
					node.setVisible( false );
				}
			}
	
			for( DNVEdge edge : graph.getEdges( level ) )
			{
				timeStr = edge.getProperty( "time" );
				if( timeStr == null || timeStr.equals( "" )
						|| ( Long.parseLong( timeStr ) >= allTimes.get( minTime ) && Long.parseLong( timeStr ) <= allTimes.get( maxTime ) ) )
				{
					edge.setVisible( true );
					edge.getFrom().setVisible( true );
					edge.getTo().setVisible( true );
				}
				else
				{
					edge.setVisible( false );
				}
			}
			
			if( allTimes.size() > 0 )
			{
				timeText = new Text( getMinTimeValue() + " - " + getMaxTimeValue(), new Vector2D( width / 2, 12 ), new Vector3D(1,1,1), new Vector3D(0,0,0), 18, true, true, false, false, false, false, true );
			}
			else
			{
				timeText = null;
			}
			
			forceSubgraphRefresh();
		}
	}

	/**
	 * Gets the min time.
	 * 
	 * @return the min time
	 */
	public int getMinTime()
	{
		return minTime;
	}
	
	public String getMinTimeValue()
	{
		if( allTimes.size() > minTime )
		{
			return "" + allTimes.get( minTime );
		}
		
		return "";
	}
	
	public String getMaxTimeValue()
	{
		if( allTimes.size() > minTime )
		{
			return "" + allTimes.get( maxTime );
		}
		
		return "";
	}

	/**
	 * Sets the max time.
	 * 
	 * @param maxTime
	 *            the new max time
	 */
	public void setMaxTime( int maxTime )
	{
		if( this.maxTime != maxTime )
		{
			this.maxTime = maxTime;
			updateNodesWithinTimeframe();
		}
	}

	/**
	 * Gets the max time.
	 * 
	 * @return the max time
	 */
	public int getMaxTime()
	{
		return maxTime;
	}

	
	public int getTimeWindow()
	{
		return maxTime - minTime + 1;
	}
	
	public void setTimeWindow( int window )
	{
		setMaxTime( Math.min( minTime + window - 1, allTimes.size() - 1 ) );
	}
	
	/**
	 * Gets the all times size.
	 * 
	 * @return the all times size
	 */
	public int getAllTimesSize()
	{
		return allTimes.size();
	}

	public void playTime()
	{
		while( !stopPlayTime && maxTime < allTimes.size() - 1 )
		{
			maxTime += 1;
			minTime += 1;
			updateNodesWithinTimeframe();
			try
			{
				Thread.sleep(300);
			}
			catch( InterruptedException e ){}
		}
		
//		System.out.println( "Stopping at " + minTime + " - " + maxTime );
		stopPlayTime = false;
	}
	
	private boolean stopPlayTime = false;
	public void stopPlayTime()
	{
//		System.out.println( "Stop pressed at " + minTime + " - " + maxTime );
		stopPlayTime = true;
	}
	
	/**
	 * Gets the circular layout sort list.
	 * 
	 * @return the circular layout sort list
	 */
	public List<SelectItem> getCircularLayoutSortList()
	{
		return circularLayoutSortList;
	}

	/**
	 * Sets the circular layout sort list.
	 * 
	 * @param circularLayoutSortList
	 *            the new circular layout sort list
	 */
	public void setCircularLayoutSortList( List<SelectItem> circularLayoutSortList )
	{
		this.circularLayoutSortList = circularLayoutSortList;
	}

	/**
	 * Gets the circular layout sort.
	 * 
	 * @return the circular layout sort
	 */
	public String getCircularLayoutSort()
	{
		return circularLayoutSort;
	}

	/**
	 * Sets the circular layout sort.
	 * 
	 * @param circularLayoutSort
	 *            the new circular layout sort
	 */
	public void setCircularLayoutSort( String circularLayoutSort )
	{
		this.circularLayoutSort = circularLayoutSort;
	}

	/**
	 * Checks if is circular layout selected.
	 * 
	 * @return true, if is circular layout selected
	 */
	public boolean isCircularLayoutSelected()
	{
		return layoutMethod instanceof CircularLayout;
	}

	/**
	 * Sets the highlight edges.
	 * 
	 * @param highlightEdges
	 *            the new highlight edges
	 */
	public void setHighlightEdges( boolean highlightEdges )
	{
		this.highlightEdges = highlightEdges;
	}

	/**
	 * Checks if is highlight edges.
	 * 
	 * @return true, if is highlight edges
	 */
	public boolean isHighlightEdges()
	{
		return highlightEdges;
	}

	/**
	 * Toggle highlight edges.
	 */
	public void toggleHighlightEdges()
	{
		setHighlightEdges( !isHighlightEdges() );
	}

	/**
	 * Gets the last used number affected.
	 * 
	 * @return the last used number affected
	 */
	public double getLastUsedNumberAffected()
	{
		return lastUsedNumberAffected;
	}

	/**
	 * Sets the last used number affected.
	 * 
	 * @param lastUsedNumberAffected
	 *            the new last used number affected
	 */
	public void setLastUsedNumberAffected( double lastUsedNumberAffected )
	{
		this.lastUsedNumberAffected = lastUsedNumberAffected;
	}

	/**
	 * Do nothing.
	 */
	public void doNothing()
	{
	// Nothing
	}

	public boolean doesOverlap( DNVNode node1, DNVNode node2 )
	{
		int nodeWidth = ImageRenderer.getNodeWidth( nodeSize, minX, maxX, 1 );
		Vector2D difference = getScreenPosDifference( node1, node2 );
		float overlap = getOverlapAmount( node1, node2, nodeWidth, difference );

		return overlap > 0;
	}
	/**
	 * Gets the overlap.
	 * 
	 * @param node1
	 *            the node1
	 * @param node2
	 *            the node2
	 * @return the overlap
	 */
	public Vector2D getOverlapVector( DNVNode node1, DNVNode node2 )
	{
		// refreshGlobalBoundaries( level );
		int nodeWidth = ImageRenderer.getNodeWidth( nodeSize, minX, maxX, 1 );
		Vector2D difference = getScreenPosDifference( node1, node2 );
		float overlap = getOverlapAmount( node1, node2, nodeWidth, difference );
		if( overlap > 0 )
		{
			// System.out.println( node1.getLabel() + " - " + node2.getLabel()
			// );
			// System.out.println( "overlap : " + overlap );
			difference.normalize();
			difference.dotProduct( overlap );
			// System.out.println( "Diff:" + difference );
			// Vector2D overlapV = ImageRenderer.transformScreenToWorld(
			// difference.getX(), difference.getY(), minX, maxX, minY, maxY,
			// globalMinX,
			// globalMaxX, globalMinY, globalMaxY, width, height );
			//
			// System.out.println( "OverlapV:" + overlapV );

			return difference;
		}

		return null;
	}

	/**
	 * @param node1
	 * @param node2
	 * @param nodeWidth
	 * @param difference
	 * @return
	 */
	public float getOverlapAmount( DNVNode node1, DNVNode node2, int nodeWidth, Vector2D difference )
	{
		float maxOverlap = nodeWidth * ( node1.getRadius() + node2.getRadius() ) / 2.0f;
		float overlap = maxOverlap - difference.length();
		return overlap;
	}

	/**
	 * @param node1
	 * @param node2
	 * @return
	 */
	public Vector2D getScreenPosDifference( DNVNode node1, DNVNode node2 )
	{
		Vector2D node1ScreenPos;
		Vector2D node2ScreenPos;
		if( node1.hasAttribute( "screenPosition" ) )
		{
			node1ScreenPos = (Vector2D)node1.getAttribute( "screenPosition" );
		}
		else
		{
			node1ScreenPos = ImageRenderer.transformPosition( globalMinX, globalMaxX, globalMinY, globalMaxY, minX, maxX, minY, maxY, width, height, node1.getPosition() );
			node1.setAttribute( "screenPosition", node1ScreenPos );
		}
		if( node2.hasAttribute( "screenPosition" ) )
		{
			node2ScreenPos = (Vector2D)node2.getAttribute( "screenPosition" );
		}
		else
		{
			node2ScreenPos = ImageRenderer.transformPosition( globalMinX, globalMaxX, globalMinY, globalMaxY, minX, maxX, minY, maxY, width, height, node2.getPosition() );
			node2.setAttribute( "screenPosition", node2ScreenPos );
		}

		Vector2D difference = new Vector2D( node1ScreenPos ).subtract( node2ScreenPos );
		return difference;
	}

	// Computes overlap between two nodes in image space and returns the amount
	// of overlap in image space
	public float getOverlap( DNVNode node1, DNVNode node2 )
	{
		Vector2D v = getOverlapVector( node1, node2 );
		if( v != null )
		{
			return v.length();
		}

		return 0;
	}

	/**
	 * Checks if is scale positions.
	 * 
	 * @return true, if is scale positions
	 */
	public boolean isScalePositions()
	{
		return scalePositions;
	}

	/**
	 * Sets the scale positions.
	 * 
	 * @param scalePositions
	 *            the new scale positions
	 */
	public void setScalePositions( boolean scalePositions )
	{
		this.scalePositions = scalePositions;
	}

	/**
	 * Checks if is draw watermark.
	 * 
	 * @return true, if is draw watermark
	 */
	public boolean isDrawWatermark()
	{
		return drawWatermark;
	}

	/**
	 * Sets the draw watermark.
	 * 
	 * @param drawWatermark
	 *            the new draw watermark
	 */
	public void setDrawWatermark( boolean drawWatermark )
	{
		this.drawWatermark = drawWatermark;
	}
	
	/**
	 * @return the showReloadButton
	 */
	public boolean isShowReloadButton()
	{
		return showReloadButton;
	}

	/**
	 * @param showReloadButton the showReloadButton to set
	 */
	public void setShowReloadButton( boolean showReloadButton )
	{
		this.showReloadButton = showReloadButton;
	}
	
	public String getResetZoomWindow()
	{
		resetZoomWindow();
		return "";
	}
	
	public void resetZoomWindow()
	{
		minX = 0;
		maxX = 1;
		minY = 0;
		maxY = 1;
	}
	
	
	/**
	 * A special function created for Enrico Glaab.
	 * 
	 * Returns a link to the Promomer system based on the selected nodes from a search result
	 * 
	 * @return
	 */
	public String getPromomerLink()
	{
		StringBuilder link = new StringBuilder( "http://bar.utoronto.ca/ntools/cgi-bin/BAR_Promomer.cgi?identify_multi_agis=" );
		
		for( DNVNode node : getSelectedNodes() )
		{
			if( node.isSelected() )
			{
				link.append( node.getBbId() ).append( "%0A" );
			}
		}
		
		return link.toString();
	}
	
	/**
	 * A special function created for Enrico Glaab.
	 * 
	 * Returns a link to the Athena system based on the selected nodes from a search result
	 * 
	 * @return
	 */
	public String getAthenaLink()
	{
		StringBuilder link = new StringBuilder( "http://www.bioinformatics2.wsu.edu/cgi-bin/Athena/cgi/visualize_select.pl?gene_text_list=" );
		
		for( DNVNode node : getSelectedNodes() )
		{
			if( node.isSelected() )
			{
				link.append(  node.getBbId() ).append( "%0A" );
			}
		}
			
		return link.toString();
	}

	public boolean isTransitionBack()
	{
		return transitionBack;
	}

	public void setTransitionBack( boolean transitionBack )
	{
		this.transitionBack = transitionBack;
	}
	
	public void toggleTransitionBack()
	{
		setTransitionBack( !isTransitionBack() );
	}

	public boolean isDrawNeighborHighlight()
	{
		return drawNeighborHighlight;
	}

	public void setDrawNeighborHighlight( boolean drawNeighborHighlight )
	{
		this.drawNeighborHighlight = drawNeighborHighlight;
	}

	private boolean playSound = true;
	public void setPlaySound( boolean play )
	{
		playSound = play;
	}

	public boolean isPlaySound()
	{
		return playSound;
	}
	
	public int getNodeWidthOnScreen( DNVNode node )
	{
		int width = ImageRenderer.getNodeWidth( (int)nodeSize, minX, maxX, 1.0, scaleNodesOnZoom );
		
		return Math.round( width * node.getRadius() );
	}

	public boolean isEnableAnimation()
	{
		return enableAnimation;
	}

	public void setEnableAnimation( boolean enableAnimation )
	{
		this.enableAnimation = enableAnimation;
	}

	public boolean isNoAlpha()
	{
		return noAlpha;
	}

	public void setNoAlpha( boolean noAlpha )
	{
		this.noAlpha = noAlpha;
	}

	public boolean isDrawNumberOfNodesInBox()
	{
		return drawNumberOfNodesInBox;
	}

	public void setDrawNumberOfNodesInBox( boolean drawNumberOfNodesInBox )
	{
		this.drawNumberOfNodesInBox = drawNumberOfNodesInBox;
	}

	public boolean isDrawAllNeighborsHighlight()
	{
		return drawAllNeighborsHighlight;
	}

	public void setDrawAllNeighborsHighlight( boolean drawAllNeighborsHighlight )
	{
		this.drawAllNeighborsHighlight = drawAllNeighborsHighlight;
	}

	public boolean isDrawNeighborHighlightAsBoxes()
	{
		return drawNeighborHighlightAsBoxes;
	}

	public void setDrawNeighborHighlightAsBoxes( boolean drawNeighborHighlightAsBoxes )
	{
		this.drawNeighborHighlightAsBoxes = drawNeighborHighlightAsBoxes;
	}

	public boolean isAlignBoxInfoRelativeToBox()
	{
		return alignBoxInfoRelativeToBox;
	}

	public void setAlignBoxInfoRelativeToBox( boolean alignBoxInfoRelativeToBox )
	{
		this.alignBoxInfoRelativeToBox = alignBoxInfoRelativeToBox;
	}

	private boolean avoidNodeOverlap = true;

	public boolean isAvoidNodeOverlap()
	{
		return avoidNodeOverlap;
	}

	public void setAvoidNodeOverlap( boolean avoidNodeOverlap )
	{
		this.avoidNodeOverlap = avoidNodeOverlap;
	}

	/**
	 * @return the drawAxis
	 */
	public boolean isDrawAxis()
	{
		return drawAxis;
	}

	/**
	 * @param drawAxis
	 *            the drawAxis to set
	 */
	public void setDrawAxis( boolean drawAxis )
	{
		this.drawAxis = drawAxis;
	}

	public void setFixedZoom( boolean fixedZoom )
	{
		this.fixedZoom = false;
		refreshGlobalBoundaries( level );
		this.fixedZoom = fixedZoom;
	}

	public boolean isFixedZoom()
	{
		return fixedZoom;
	}

	/**
	 * @return the neighborHighlightColor
	 */
	public Vector3D getNeighborHighlightColor()
	{
		return neighborHighlightColor;
	}

	/**
	 * @param neighborHighlightColor
	 *            the neighborHighlightColor to set
	 */
	public void setNeighborHighlightColor( Vector3D neighborHighlightColor )
	{
		this.neighborHighlightColor = neighborHighlightColor;
	}
	
}