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

package net.wigis.web;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.wigis.graph.ImageCacher;
import net.wigis.graph.ImageRenderer;
import net.wigis.graph.PaintBean;
import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.layout.FruchtermanReingold;
import net.wigis.graph.dnv.layout.HopDistanceLayout;
import net.wigis.graph.dnv.layout.RecommendationLayoutInterface;
import net.wigis.graph.dnv.utilities.GraphFunctions;
import net.wigis.graph.dnv.utilities.InterpolationMethod;
import net.wigis.graph.dnv.utilities.SortByLabelSize;
import net.wigis.graph.dnv.utilities.Timer;
import net.wigis.graph.dnv.utilities.Vector2D;
import net.wigis.settings.Settings;

// TODO: Auto-generated Javadoc
/**
 * Servlet implementation class GraphServlet.
 * 
 * @author Brynjar Gretarsson
 */
public class GraphServlet extends HttpServlet
{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Logger.
	 */
	// // private static Log logger = LogFactory.getLog( GraphServlet.class );

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GraphServlet()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Do get.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @throws ServletException
	 *             the servlet exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
	{
		drawGraph( request, response );
	}

	/**
	 * Do post.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @throws ServletException
	 *             the servlet exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
	{
		drawGraph( request, response );
	}

	/** The recommendation layout. */
	private RecommendationLayoutInterface recommendationLayout;

	/**
	 * Draw graph.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 */
	private void drawGraph( HttpServletRequest request, HttpServletResponse response )
	{
		try
		{
			PaintBean pb = (PaintBean)ContextLookup.lookup( "paintBean", request );
			if( pb == null )
			{
				System.out.println( "paintBean is null" );
				return;
			}

			ImageCacher.updateConstants( request );

			DNVGraph graph = pb.getGraph();
			int level = (int)pb.getLevel();

			// pb.setHasBeenDisplayed( true );

			int width = -1;
			width = getWidth( request, width );
			int height = -1;
			height = getHeight( request, height );

			boolean overview = false;
			overview = getOverview( request, overview );

			if( width == -1 )
				width = (int)pb.getWidth();

			if( height == -1 )
				height = (int)pb.getHeight();

			double minX = 0;
			double minY = 0;
			double maxX = 1;
			double maxY = 1;

			String minXStr = request.getParameter( "minX" );
			if( minXStr != null && !minXStr.equals( "" ) )
			{
				minX = getMinX( request, minX );
				pb.setMinX( minX );
			}

			String minYStr = request.getParameter( "minY" );
			if( minYStr != null && !minYStr.equals( "" ) )
			{
				minY = getMinY( request, minY );
				pb.setMinY( minY );
			}

			String maxXStr = request.getParameter( "maxX" );
			if( maxXStr != null && !maxXStr.equals( "" ) )
			{
				maxX = getMaxX( request, maxX );
				pb.setMaxX( maxX );
			}

			String maxYStr = request.getParameter( "maxY" );
			if( maxYStr != null && !maxYStr.equals( "" ) )
			{
				maxY = getMaxY( request, maxY );
				pb.setMaxY( maxY );
			}

			String renderingStr = request.getParameter( "r" );
			int rendering = BufferedImage.TYPE_BYTE_INDEXED;
			if( renderingStr != null && renderingStr.equals( "qual" ) )
			{
				rendering = BufferedImage.TYPE_INT_RGB;
			}

			Timer pickingTimer = new Timer( Timer.MILLISECONDS );
			// ------------------------------------
			// interaction with static image
			// ------------------------------------
			String mouseDownXstr = request.getParameter( "mouseDownX" );
			// boolean mouseDown = false;
			if( mouseDownXstr != null && !mouseDownXstr.equals( "" ) )
			{
				// mouseDown = true;
				pickingTimer.setStart();
				// drag closest node to this position
				int mouseDownX = Integer.parseInt( mouseDownXstr );
				int mouseDownY = Integer.parseInt( request.getParameter( "mouseDownY" ) );

				// drag it to here
				int mouseUpX = Integer.parseInt( request.getParameter( "mouseUpX" ) );
				int mouseUpY = Integer.parseInt( request.getParameter( "mouseUpY" ) );

				boolean sameNode = Boolean.parseBoolean( request.getParameter( "sameNode" ) );
				boolean ctrlPressed = Boolean.parseBoolean( request.getParameter( "ctrlPressed" ) );

				// - - - - - - - - - - -
				// find closest node
				// - - - - - - - - - - -
				// float maxDepth = Integer.MAX_VALUE;

				double globalMinX = GraphFunctions.getMinXPosition( graph, level, true );
				double globalMaxX = GraphFunctions.getMaxXPosition( graph, level, true );
				double globalMinY = GraphFunctions.getMinYPosition( graph, level, true );
				double globalMaxY = GraphFunctions.getMaxYPosition( graph, level, true );
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
				double yBuffer = ( globalMaxY - globalMinY ) * pb.getWhiteSpaceBuffer();
				double xBuffer = ( globalMaxX - globalMinX ) * pb.getWhiteSpaceBuffer();
				DNVNode selectedNode = null;
				globalMaxY += yBuffer;
				globalMinY -= yBuffer;
				globalMaxX += xBuffer;
				globalMinX -= xBuffer;

				if( !sameNode )
				{
					List<DNVNode> nodes = graph.getNodes( level );
					SortByLabelSize sbls = new SortByLabelSize( pb.isHighlightNeighbors() );
					Collections.sort( nodes, sbls );
					DNVNode node;
					Vector2D screenPosition;
					double distance;
					double minDistance = Integer.MAX_VALUE;
					int nodeI = -1;
					int distX = 0; // dist b/w this node and mouse click
					int distY = 0;

					// Check if user clicked on a solid node label
					for( int i = nodes.size() - 1; i >= 0; i-- )
					{
						node = nodes.get( i );
						if( node.isVisible() && ( node.isForceLabel() || pb.isShowLabels() ) && node.getProperty( "faded" ) == null )
						{
							screenPosition = ImageRenderer.transformPosition( globalMinX, globalMaxX, globalMinY, globalMaxY, minX, maxX, minY, maxY,
									width, height, node.getPosition( true ) );
							ImageRenderer.Rectangle boundingRectangle = ImageRenderer.getRectangleBoundingTheLabel( node, screenPosition, null,
									(int)Math.round( pb.getNodeSize() * node.getRadius() ), node.getLabel( pb.isInterpolationLabels() ), pb
											.isCurvedLabels()
											|| node.isCurvedLabel(), pb.getLabelSize(), minX, maxX, width / pb.getWidth(), pb.isScaleLabels(), pb
											.getMaxLabelLength(), pb.getCurvedLabelAngle(), pb.isBoldLabels(), false, false );
							if( mouseDownX >= boundingRectangle.left() && mouseDownX <= boundingRectangle.right()
									&& mouseDownY <= boundingRectangle.bottom() && mouseDownY >= boundingRectangle.top() )
							{
								distX = (int)( mouseDownX - screenPosition.getX() );
								distY = (int)( mouseDownY - screenPosition.getY() );
								node.setProperty( "distX", "" + distX );
								node.setProperty( "distY", "" + distY );
								minDistance = 0;
								nodeI = i;
								break;
							}
						}
					}

					if( nodeI == -1 )
					{
						// loop thru all nodes to find closest node
						for( int i = nodes.size() - 1; i >= 0; i-- )
						{
							node = nodes.get( i );
							if( node.isVisible() )
							{
								screenPosition = ImageRenderer.transformPosition( globalMinX, globalMaxX, globalMinY, globalMaxY, minX, maxX, minY,
										maxY, width, height, node.getPosition( true ) );

								// find node closest to mouseDown
								distX = (int)( mouseDownX - screenPosition.getX() );
								distY = (int)( mouseDownY - screenPosition.getY() );

								distance = distX * distX + distY * distY;

								if( distance < minDistance )
								{
									node.setProperty( "distX", "" + distX );
									node.setProperty( "distY", "" + distY );

									minDistance = distance;
									nodeI = i;
								}
							}
						}
					}

					if( nodes.size() > 0 && nodeI != -1 )
					{
						node = nodes.get( nodeI );

						double nodeWidth;
						nodeWidth = getNodeWidth( pb, width, minX, maxX, node.getRadius() );
						// check if selected node is close enough to mouseDown
						if( Settings.DEBUG )
							System.out.println( "Minimum distance was " + Math.sqrt( minDistance ) );

						if( Math.sqrt( minDistance ) >= nodeWidth )
						{
							// Still no node selected so check nodes with faded
							// labels
							for( int i = nodes.size() - 1; i >= 0; i-- )
							{
								node = nodes.get( i );
								if( node.isVisible() && ( node.isForceLabel() || pb.isShowLabels() ) && node.getProperty( "faded" ) != null
										&& Float.parseFloat( node.getProperty( "faded" ) ) > 0.1 )
								{
									screenPosition = ImageRenderer.transformPosition( globalMinX, globalMaxX, globalMinY, globalMaxY, minX, maxX,
											minY, maxY, width, height, node.getPosition( true ) );
									ImageRenderer.Rectangle boundingRectangle = ImageRenderer.getRectangleBoundingTheLabel( node, screenPosition,
											null, (int)Math.round( pb.getNodeSize() * node.getRadius() ),
											node.getLabel( pb.isInterpolationLabels() ), pb.isCurvedLabels() || node.isCurvedLabel(), pb
													.getLabelSize(), minX, maxX, width / pb.getWidth(), pb.isScaleLabels(), pb.getMaxLabelLength(),
											pb.getCurvedLabelAngle(), pb.isBoldLabels(), false, false );
									if( mouseDownX >= boundingRectangle.left() && mouseDownX <= boundingRectangle.right()
											&& mouseDownY <= boundingRectangle.bottom() && mouseDownY >= boundingRectangle.top() )
									{
										distX = (int)( mouseDownX - screenPosition.getX() );
										distY = (int)( mouseDownY - screenPosition.getY() );
										node.setProperty( "distX", "" + distX );
										node.setProperty( "distY", "" + distY );
										minDistance = 0;
										nodeI = i;
										break;
									}
								}
							}
						}

						node = nodes.get( nodeI );

						nodeWidth = getNodeWidth( pb, width, minX, maxX, node.getRadius() );
						// check if selected node is close enough to mouseDown
						if( Settings.DEBUG )
							System.out.println( "Minimum distance was " + Math.sqrt( minDistance ) );
						if( Math.sqrt( minDistance ) < nodeWidth )
						{
							// if( node.isSelected() )
							// {
							// sameNode = true;
							// }
							pb.setSelectedNode( node, ctrlPressed );
							selectedNode = node;
						}
						else
						{
							if( pb.getSelectedNode() != null )
							{
								pb.setSelectedNode( null, ctrlPressed );
//								runDocumentTopicsCircularLayout( request, pb, graph, level );
							}
						}
					}

					if( selectedNode == null )
					{
						minDistance = Integer.MAX_VALUE;
						List<DNVEdge> edges = graph.getEdges( level );
						DNVEdge edge;
						Vector2D screenPosition2;
						int edgeI = 0;
						for( int i = 0; i < edges.size(); i++ )
						{
							edge = edges.get( i );
							if( edge.isVisible() )
							{
								screenPosition = ImageRenderer.transformPosition( globalMinX, globalMaxX, globalMinY, globalMaxY, minX, maxX, minY,
										maxY, width, height, edge.getFrom().getPosition( true ) );
								screenPosition2 = ImageRenderer.transformPosition( globalMinX, globalMaxX, globalMinY, globalMaxY, minX, maxX, minY,
										maxY, width, height, edge.getTo().getPosition( true ) );
								distance = getPointLineDistance( screenPosition, screenPosition2, mouseDownX, mouseDownY );
								if( distance < minDistance )
								{
									minDistance = distance;
									edgeI = i;
								}
							}
						}

						if( edges.size() > 0 )
						{
							edge = edges.get( edgeI );

							double edgeWidth = Math.max( edge.getThickness(), 4 );
							// check if selected node is close enough to
							// mouseDown
							if( Settings.DEBUG )
								System.out.println( "Minimum distance was " + Math.sqrt( minDistance ) );
							if( Math.sqrt( minDistance ) < edgeWidth / 2.0 )
							{
								if( edge.isSelected() )
								{
									sameNode = true;
								}
								pb.setSelectedEdge( edge, ctrlPressed );
							}
							else
							{
								pb.setSelectedEdge( null, ctrlPressed );
							}
						}
					}
				}

				pickingTimer.setEnd();
				if( Settings.DEBUG )
					System.out.println( "Picking took " + pickingTimer.getLastSegment( Timer.SECONDS ) + " seconds." );

				String releasedStr = request.getParameter( "released" );
				boolean released = false;
				if( releasedStr != null )
				{
					try
					{
						released = Boolean.parseBoolean( releasedStr );
					}
					catch( Exception e )
					{}
				}
				
				moveSelectedNode( request, pb, graph, level, width, height, minX, minY, maxX, maxY, mouseUpX, mouseUpY, sameNode, globalMinX,
						globalMaxX, globalMinY, globalMaxY, selectedNode, recommendationLayout, released );
			}

			// ------------------------------------

			Timer paintTimer = new Timer( Timer.MILLISECONDS );
			paintTimer.setStart();
			response.setContentType( "image/gif" );
			pb.paint( response.getOutputStream(), width, height, overview, rendering );
			paintTimer.setEnd();

			if( Settings.DEBUG && !overview && !pb.isRenderJS() )
				System.out.println( "Drawing took " + paintTimer.getLastSegment( Timer.SECONDS ) + " seconds." );
		}
		catch( IOException e )
		{
			// e.printStackTrace();
		}
		catch( NullPointerException npe )
		{
			npe.printStackTrace();
		}
	}

	/**
	 * Move node.
	 * 
	 * @param selectedNode
	 *            the selected node
	 * @param request
	 *            the request
	 * @param pb
	 *            the pb
	 * @param graph
	 *            the graph
	 * @param level
	 *            the level
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @param minX
	 *            the min x
	 * @param minY
	 *            the min y
	 * @param maxX
	 *            the max x
	 * @param maxY
	 *            the max y
	 * @param mouseUpX
	 *            the mouse up x
	 * @param mouseUpY
	 *            the mouse up y
	 * @param sameNode
	 *            the same node
	 * @param globalMinX
	 *            the global min x
	 * @param globalMaxX
	 *            the global max x
	 * @param globalMinY
	 *            the global min y
	 * @param globalMaxY
	 *            the global max y
	 * @param recommendationLayout
	 *            the recommendation layout
	 */
	public static void moveNode( DNVNode selectedNode, HttpServletRequest request, PaintBean pb, DNVGraph graph, int level, int width, int height,
			double minX, double minY, double maxX, double maxY, int mouseUpX, int mouseUpY, boolean sameNode, double globalMinX, double globalMaxX,
			double globalMinY, double globalMaxY, RecommendationLayoutInterface recommendationLayout, boolean released )
	{
		if( selectedNode != null )
		{
			if( selectedNode.hasProperty( "distX" ) )
			{
				mouseUpX -= Integer.parseInt( selectedNode.getProperty( "distX" ) );
			}
			if( selectedNode.hasProperty( "distY" ) )
			{
				mouseUpY -= Integer.parseInt( selectedNode.getProperty( "distY" ) );
			}
		}

		if( pb.getInteractionMethod().equals( Settings.INTERPOLATION_INTERACTION ) )
		{
//			if( !sameNode && selectedNode != null )
//			{
//				runDocumentTopicsCircularLayout( request, pb, graph, level );
//			}
			performInterpolation( pb, graph, width, height, minX, minY, maxX, maxY, mouseUpX, mouseUpY, sameNode, level, globalMinX, globalMaxX,
					globalMinY, globalMaxY, selectedNode );
		}
		else if( pb.getInteractionMethod().equals( Settings.PEERCHOOSER_INTERACTION ) )
		{
			performPeerchooserMovement( pb, graph, width, height, minX, minY, maxX, maxY, mouseUpX, mouseUpY, sameNode, level, globalMinX,
					globalMaxX, globalMinY, globalMaxY, selectedNode, released );
		}
		else if( pb.getInteractionMethod().equals( Settings.FACEBOOK_RECOMMENDATION_INTERACTION ) )
		{
			if( selectedNode != null )
			{
				String hopDistanceStr = selectedNode.getProperty( "hopDistance" );
				if( hopDistanceStr != null )
				{
					float hopDistance = Float.parseFloat( hopDistanceStr );
					String centralNodeIdStr = graph.getProperty( "centralNodeId" );
					if( centralNodeIdStr != null )
					{
						DNVNode centralNode = (DNVNode)graph.getNodeById( Integer.parseInt( centralNodeIdStr ) );
						recommendationLayout.moveNode( graph, selectedNode, centralNode, ImageRenderer.transformScreenToWorld( mouseUpX, mouseUpY,
								minX, maxX, minY, maxY, globalMinX, globalMaxX, globalMinY, globalMaxY, width, height ), hopDistance, pb
								.isRecommendationCircle(), request, sameNode );
					}
				}
			}
		}
		else if( pb.getInteractionMethod().equals( Settings.TOPIC_INTERACTION ) )
		{
			if( selectedNode != null && !sameNode )
			{
				new HopDistanceLayout().runLayout( graph, selectedNode, level, false );
			}
		}
		else if( pb.getInteractionMethod().equals( Settings.SPRING_INTERACTION ) )
		{
			if( selectedNode != null )
			{
				performSpringInteraction( selectedNode, graph, level, width, height, minX, minY, maxX, maxY, mouseUpX, mouseUpY, globalMinX,
						globalMaxX, globalMinY, globalMaxY );
			}
		}
		else if( pb.getInteractionMethod().equals( Settings.INTERPOLATION_WITH_SPRING ) )
		{
			if( selectedNode != null )
			{				
				pb.setInteractionMethod( Settings.INTERPOLATION_INTERACTION );
				moveNode( selectedNode, request, pb, graph, level, width, height, minX, minY, maxX, maxY, mouseUpX, mouseUpY, sameNode, globalMinX, globalMaxX, globalMinY, globalMaxY, recommendationLayout, released );
				pb.setInteractionMethod( Settings.INTERPOLATION_WITH_SPRING );
				
				selectedNode.setProperty( "fixed", "true" );
				
				for( int i = 5; i > 0; i-- )
				{
					FruchtermanReingold.runIteration( 80, 80, graph, level, 0.1f * i, false, false, false );
				}
	
				selectedNode.removeProperty( "fixed" );
			}
		}
	}

	public static void performSpringInteraction( DNVNode selectedNode, DNVGraph graph, int level, int width, int height, double minX, double minY,
			double maxX, double maxY, int mouseUpX, int mouseUpY, double globalMinX, double globalMaxX, double globalMinY, double globalMaxY )
	{
		if( selectedNode != null )
		{
			Vector2D mouseUpWorld = ImageRenderer.transformScreenToWorld( mouseUpX, mouseUpY, minX, maxX, minY, maxY, globalMinX, globalMaxX, globalMinY,
					globalMaxY, width, height );
			Vector2D movement = ImageRenderer.getMovement( selectedNode, mouseUpWorld );
			moveNode( selectedNode, movement );
			
			selectedNode.setProperty( "fixed", "true" );
			
			for( int i = 5; i > 0; i-- )
			{
				FruchtermanReingold.runIteration( (int)(globalMaxX - globalMinX), (int)(globalMaxY - globalMinY), graph, level, 0.1f * i, false, false, false );
			}
	
			selectedNode.removeProperty( "fixed" );
		}
	}

	/**
	 * Move selected node.
	 * 
	 * @param request
	 *            the request
	 * @param pb
	 *            the pb
	 * @param graph
	 *            the graph
	 * @param level
	 *            the level
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @param minX
	 *            the min x
	 * @param minY
	 *            the min y
	 * @param maxX
	 *            the max x
	 * @param maxY
	 *            the max y
	 * @param mouseUpX
	 *            the mouse up x
	 * @param mouseUpY
	 *            the mouse up y
	 * @param sameNode
	 *            the same node
	 * @param globalMinX
	 *            the global min x
	 * @param globalMaxX
	 *            the global max x
	 * @param globalMinY
	 *            the global min y
	 * @param globalMaxY
	 *            the global max y
	 * @param selectedNode
	 *            the selected node
	 * @param recommendationLayout
	 *            the recommendation layout
	 */
	public static void moveSelectedNode( HttpServletRequest request, PaintBean pb, DNVGraph graph, int level, int width, int height, double minX,
			double minY, double maxX, double maxY, int mouseUpX, int mouseUpY, boolean sameNode, double globalMinX, double globalMaxX,
			double globalMinY, double globalMaxY, DNVNode selectedNode, RecommendationLayoutInterface recommendationLayout, boolean released )
	{
		if( selectedNode == null && sameNode )
		{
			selectedNode = pb.getSelectedNode();
		}

		moveNode( selectedNode, request, pb, graph, level, width, height, minX, minY, maxX, maxY, mouseUpX, mouseUpY, sameNode, globalMinX,
				globalMaxX, globalMinY, globalMaxY, recommendationLayout, released );
	}

	/**
	 * Gets the node width.
	 * 
	 * @param pb
	 *            the pb
	 * @param width
	 *            the width
	 * @param minX
	 *            the min x
	 * @param maxX
	 *            the max x
	 * @param node
	 *            the node
	 * @return the node width
	 */
	public static double getNodeWidth( PaintBean pb, int width, double minX, double maxX, float nodeRadius )
	{
		double nodeWidth;
		if( pb.isScaleNodesOnZoom() )
		{
			nodeWidth = Math.max( ImageRenderer.getNodeWidth( pb.getNodeSize(), minX, maxX, width / pb.getWidth() ) * nodeRadius, 10 ) / 2.0 + 3;
		}
		else
		{
			nodeWidth = Math.max( ImageRenderer.getNodeWidth( pb.getNodeSize(), 0, 1, width / pb.getWidth() ) * nodeRadius, 10 ) / 2.0 + 3;
		}

		return nodeWidth;
	}

//	/**
//	 * Run document topics circular layout.
//	 * 
//	 * @param request
//	 *            the request
//	 * @param pb
//	 *            the pb
//	 * @param graph
//	 *            the graph
//	 * @param level
//	 *            the level
//	 */
//	public static void runDocumentTopicsCircularLayout( HttpServletRequest request, PaintBean pb, DNVGraph graph, int level )
//	{
//		TopicVisualizationBean tvb = (TopicVisualizationBean)ContextLookup.lookup( "topicVisualizationBean", request );
//		if( tvb != null )
//		{
//			if( pb.getLayoutMethod().equals( Settings.DOCUMENT_TOPIC_CIRCULAR_LAYOUT ) && pb.isDocumentTopicsCircularLayoutOnlyDeformSelectedTopics() )
//			{
//				// Double circularWidth = MDSTopicsLayout.getWidth(
//				// tvb.getDissimilarityMatrix() );
//				DocumentTopicsCircularLayout.runLayout( graph, level, (float)pb.getCircularLayoutBuffer(), 0.1f, pb
//						.getDocumentTopicsCircularLayoutDocIdPrefix(), null, pb.getDocumentTopicsCircularLayoutWidthMultiplier(), tvb
//						.isForceTopicsToCircle(), tvb.isCreateDocumentEdges() || tvb.isTimelineVisualization(), true, tvb.getDissimilarityMatrix() );
//			}
//		}
//	}

	/**
	 * Gets the point line distance.
	 * 
	 * @param lineEnd1
	 *            the line end1
	 * @param lineEnd2
	 *            the line end2
	 * @param pointX
	 *            the point x
	 * @param pointY
	 *            the point y
	 * @return the point line distance
	 */
	public static double getPointLineDistance( Vector2D lineEnd1, Vector2D lineEnd2, int pointX, int pointY )
	{
		double u = ( ( pointX - lineEnd1.getX() ) * ( lineEnd2.getX() - lineEnd1.getX() ) + ( pointY - lineEnd1.getY() )
				* ( lineEnd2.getY() - lineEnd1.getY() ) )
				/ Math.pow( GraphFunctions.getDistance( lineEnd1, lineEnd2 ), 2 );

		float x = (float)( lineEnd1.getX() + u * ( lineEnd2.getX() - lineEnd1.getX() ) );
		float y = (float)( lineEnd1.getY() + u * ( lineEnd2.getY() - lineEnd1.getY() ) );

		if( ( x < lineEnd1.getX() && x < lineEnd2.getX() ) || ( x > lineEnd1.getX() && x > lineEnd2.getX() )
				|| ( y < lineEnd1.getY() && y < lineEnd2.getY() ) || ( y > lineEnd1.getY() && y > lineEnd2.getY() ) )
		{
			// outside the line segment representing the edge

			return Double.MAX_VALUE;
		}

		double distance = GraphFunctions.getDistance( x, y, pointX, pointY );

		return distance;
	}

	/**
	 * Perform peerchooser movement.
	 * 
	 * @param pb
	 *            the pb
	 * @param graph
	 *            the graph
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @param minX
	 *            the min x
	 * @param minY
	 *            the min y
	 * @param maxX
	 *            the max x
	 * @param maxY
	 *            the max y
	 * @param mouseUpX
	 *            the mouse up x
	 * @param mouseUpY
	 *            the mouse up y
	 * @param sameNode
	 *            the same node
	 * @param level
	 *            the level
	 * @param globalMinX
	 *            the global min x
	 * @param globalMaxX
	 *            the global max x
	 * @param globalMinY
	 *            the global min y
	 * @param globalMaxY
	 *            the global max y
	 * @param selectedNode
	 *            the selected node
	 * @param released
	 *            the released
	 */
	public static void performPeerchooserMovement( PaintBean pb, DNVGraph graph, int width, int height, double minX, double minY, double maxX,
			double maxY, int mouseUpX, int mouseUpY, boolean sameNode, int level, double globalMinX, double globalMaxX, double globalMinY,
			double globalMaxY, DNVNode selectedNode, boolean released )
	{
		// transform mouseUp from screen to world (new x = 0)
		Vector2D mouseUpWorld = ImageRenderer.transformScreenToWorld( mouseUpX, mouseUpY, minX, maxX, minY, maxY, globalMinX, globalMaxX, globalMinY,
				globalMaxY, width, height );

		Vector2D movement = new Vector2D( 0, 0 );
		if( selectedNode == null && sameNode )
		{
			selectedNode = pb.getSelectedNode();
		}

		if( selectedNode != null )
		{
			if( !selectedNode.getType().equals( "Active_user" ) )
			{
				movement = ImageRenderer.getMovement( selectedNode, mouseUpWorld );

				if( selectedNode.getType().equals( "genre" ) )
				{
					DNVNode activeUser = GraphFunctions.getNodeByType( "Active_user", graph, level );

					if( activeUser != null )
					{
						double oldDistance = GraphFunctions.getDistance( activeUser, selectedNode, true );
						Vector2D newPosition = new Vector2D( selectedNode.getPosition( true ) );
						newPosition.add( movement );
						double newDistance = GraphFunctions.getDistance( activeUser.getPosition( true ), newPosition );
						double ratio = newDistance / oldDistance;

						List<DNVNode> neighbors = selectedNode.getNeighbors();
						DNVNode neighbor;
						Vector2D tempMovement = new Vector2D();

						for( int i = 0; i < neighbors.size(); i++ )
						{
							neighbor = neighbors.get( i );
							oldDistance = GraphFunctions.getDistance( activeUser, neighbor, true );
							newDistance = oldDistance * ratio;
							newPosition.set( neighbor.getPosition( true ) );
							newPosition.normalize();
							newPosition.dotProduct( (float)newDistance );
							newPosition.add( activeUser.getPosition( true ) );

							tempMovement.set( newPosition );
							tempMovement.subtract( neighbor.getPosition( true ) );
							neighbor.move( tempMovement, false, false );
						}
					}
				}

				selectedNode.move( movement, false, false );
			}
		}

		String number = graph.getProperty( "numberOfUsersRecommendation" );
		int numberOfUsersRecommendation = 20;
		if( number != null )
		{
			try
			{
				numberOfUsersRecommendation = Integer.parseInt( number );
			}
			catch( NumberFormatException nfe )
			{}
		}

		if( released )
			GraphFunctions.colorKNearestNodes( graph, level, numberOfUsersRecommendation, "Active_user", "user" );
	}

	/**
	 * Perform interpolation.
	 * 
	 * @param pb
	 *            the pb
	 * @param graph
	 *            the graph
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @param minX
	 *            the min x
	 * @param minY
	 *            the min y
	 * @param maxX
	 *            the max x
	 * @param maxY
	 *            the max y
	 * @param mouseUpX
	 *            the mouse up x
	 * @param mouseUpY
	 *            the mouse up y
	 * @param sameNode
	 *            the same node
	 * @param level
	 *            the level
	 * @param globalMinX
	 *            the global min x
	 * @param globalMaxX
	 *            the global max x
	 * @param globalMinY
	 *            the global min y
	 * @param globalMaxY
	 *            the global max y
	 * @param selectedNode
	 *            the selected node
	 */
	public static void performInterpolation( PaintBean pb, DNVGraph graph, int width, int height, double minX, double minY, double maxX, double maxY,
			int mouseUpX, int mouseUpY, boolean sameNode, int level, double globalMinX, double globalMaxX, double globalMinY, double globalMaxY,
			DNVNode selectedNode )
	{
		Timer interpolationTimer = new Timer( Timer.MILLISECONDS );
		interpolationTimer.setStart();
		Map<Integer, DNVNode> selectedNodes = graph.getSelectedNodes( level );

		// transform mouseUp from screen to world (new x = 0)
		Vector2D mouseUpWorld = ImageRenderer.transformScreenToWorld( mouseUpX, mouseUpY, minX, maxX, minY, maxY, globalMinX, globalMaxX, globalMinY,
				globalMaxY, width, height );

		Vector2D zeroPixels = ImageRenderer.transformScreenToWorld( 0, 0, minX, maxX, minY, maxY, globalMinX, globalMaxX, globalMinY, globalMaxY,
				width, height );

		Vector2D fivePixels = ImageRenderer.transformScreenToWorld( 5, 5, minX, maxX, minY, maxY, globalMinX, globalMaxX, globalMinY, globalMaxY,
				width, height );

		fivePixels.subtract( zeroPixels );

		Vector2D movement = new Vector2D( 0, 0 );
		if( selectedNode == null && sameNode )
		{
			selectedNode = pb.getSelectedNode();
		}

		if( selectedNode != null )
		{
			movement = ImageRenderer.getMovement( selectedNode, mouseUpWorld );

			// Get rid of old interpolation data
			if( !sameNode || pb.getNumberAffected() != pb.getLastUsedNumberAffected() )
			{
				InterpolationMethod.resetInterpolationData( graph, level );
			}
		}

		synchronized( graph )
		{
			for( DNVNode node : selectedNodes.values() )
			{
				if( node != null )
				{
					// - - - - - - - - - - -
					// drag node - use peterson's interpolation method
					// - - - - - - - - - - -
					if( !sameNode || selectedNodes.size() > 1 )
					{
						selectNode( pb, graph, Integer.MAX_VALUE, level, node );
					}

					moveNode( node, movement );
				}
			}

			pb.setLastUsedNumberAffected( pb.getNumberAffected() );
		}

		InterpolationMethod.applyFunction( pb.getSelectedNode(), pb, graph, movement, level, Math.abs( fivePixels.getX() ) );
		pb.forceSubgraphRefresh();
		pb.findSubGraph();
		interpolationTimer.setEnd();
		if( Settings.DEBUG )
		{
			System.out.println( "Interpolation took " + interpolationTimer.getLastSegment( Timer.SECONDS ) + " seconds." );
		}
	}

	/**
	 * Move node.
	 * 
	 * @param node
	 *            the node
	 * @param movement
	 *            the movement
	 */
	public static void moveNode( DNVNode node, Vector2D movement )
	{
		// Perform the movement
		node.moveRelatedNodes( movement, true, true );
		node.move( movement, true, true );
	}

	/**
	 * Select node.
	 * 
	 * @param pb
	 *            the pb
	 * @param graph
	 *            the graph
	 * @param maxDepth
	 *            the max depth
	 * @param level
	 *            the level
	 * @param node
	 *            the node
	 */
	public static void selectNode( PaintBean pb, DNVGraph graph, float maxDepth, int level, DNVNode node )
	{
		if( !pb.isInterpolationMethodUseWholeGraph() )
		{
			maxDepth = (int)pb.getNumberAffected() + 1;
			if( pb.isInterpolationMethodUseActualEdgeDistance() )
			{
				maxDepth *= DNVEdge.DEFAULT_RESTING_DISTANCE;
			}
		}

		// Perform the BFS
		float maxD = InterpolationMethod.performBFS( node, maxDepth, pb.isInterpolationMethodUseActualEdgeDistance() );

		// Need to use the value returned by the BFS if we are using whole graph
		// (otherwise we use the value given by the user)
		if( pb.isInterpolationMethodUseWholeGraph() )
		{
			maxDepth = maxD;
			if( pb.isInterpolationMethodUseActualEdgeDistance() )
			{
				maxDepth *= DNVEdge.DEFAULT_RESTING_DISTANCE;
			}
		}

		InterpolationMethod.setWeights( graph, level, maxDepth, (float)pb.getCurveMin(), (float)pb.getCurveMax(), pb
				.isInterpolationMethodUseActualEdgeDistance(), node );
	}

	/**
	 * Gets the overview.
	 * 
	 * @param request
	 *            the request
	 * @param overview
	 *            the overview
	 * @return the overview
	 */
	private boolean getOverview( HttpServletRequest request, boolean overview )
	{
		String overviewStr = request.getParameter( "overview" );
		if( overviewStr != null )
		{
			overview = Boolean.parseBoolean( overviewStr );
		}

		return overview;
	}

	/**
	 * Gets the max y.
	 * 
	 * @param request
	 *            the request
	 * @param maxY
	 *            the max y
	 * @return the max y
	 */
	private double getMaxY( HttpServletRequest request, double maxY )
	{
		String maxYstr = request.getParameter( "maxY" );
		if( maxYstr != null && !maxYstr.equals( "" ) )
		{
			try
			{
				maxY = Double.parseDouble( maxYstr );
			}
			catch( NumberFormatException nfe )
			{}
		}
		return maxY;
	}

	/**
	 * Gets the max x.
	 * 
	 * @param request
	 *            the request
	 * @param maxX
	 *            the max x
	 * @return the max x
	 */
	private double getMaxX( HttpServletRequest request, double maxX )
	{
		String maxXstr = request.getParameter( "maxX" );
		if( maxXstr != null && !maxXstr.equals( "" ) )
		{
			try
			{
				maxX = Double.parseDouble( maxXstr );
			}
			catch( NumberFormatException nfe )
			{}
		}
		return maxX;
	}

	/**
	 * Gets the min y.
	 * 
	 * @param request
	 *            the request
	 * @param minY
	 *            the min y
	 * @return the min y
	 */
	private double getMinY( HttpServletRequest request, double minY )
	{
		String minYstr = request.getParameter( "minY" );
		if( minYstr != null && !minYstr.equals( "" ) )
		{
			try
			{
				minY = Double.parseDouble( minYstr );
			}
			catch( NumberFormatException nfe )
			{}
		}
		return minY;
	}

	/**
	 * Gets the min x.
	 * 
	 * @param request
	 *            the request
	 * @param minX
	 *            the min x
	 * @return the min x
	 */
	private double getMinX( HttpServletRequest request, double minX )
	{
		String minXstr = request.getParameter( "minX" );
		if( minXstr != null && !minXstr.equals( "" ) )
		{
			try
			{
				minX = Double.parseDouble( minXstr );
			}
			catch( NumberFormatException nfe )
			{}
		}
		return minX;
	}

	/**
	 * Gets the height.
	 * 
	 * @param request
	 *            the request
	 * @param height
	 *            the height
	 * @return the height
	 */
	private int getHeight( HttpServletRequest request, int height )
	{
		String heightStr = request.getParameter( "height" );
		if( heightStr != null && !heightStr.equals( "" ) )
		{
			try
			{
				height = Integer.parseInt( heightStr );
			}
			catch( NumberFormatException nfe )
			{}
		}

		return height;
	}

	/**
	 * Gets the width.
	 * 
	 * @param request
	 *            the request
	 * @param width
	 *            the width
	 * @return the width
	 */
	private int getWidth( HttpServletRequest request, int width )
	{
		String widthStr = request.getParameter( "width" );
		if( widthStr != null && !widthStr.equals( "" ) )
		{
			try
			{
				width = Integer.parseInt( widthStr );
			}
			catch( NumberFormatException nfe )
			{}
		}

		return width;
	}
}
