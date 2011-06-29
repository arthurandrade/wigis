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
import net.wigis.graph.dnv.interaction.interfaces.InteractionInterface;
import net.wigis.graph.dnv.interaction.interfaces.RecommendationInteractionInterface;
import net.wigis.graph.dnv.interaction.interfaces.SimpleInteractionInterface;
import net.wigis.graph.dnv.layout.interfaces.RecommendationLayoutInterface;
import net.wigis.graph.dnv.utilities.GraphFunctions;
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
						globalMaxX, globalMinY, globalMaxY, selectedNode, released );
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
			double globalMinY, double globalMaxY, boolean released )
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

		InteractionInterface interactionMethod = pb.getInteractionMethod();
		if( interactionMethod instanceof SimpleInteractionInterface )
		{
			((SimpleInteractionInterface)interactionMethod).performInteraction( pb, graph, width, height, minX, minY, maxX, maxY, mouseUpX, mouseUpY, sameNode, level, globalMinX, globalMaxX, globalMinY, globalMaxY, selectedNode, released );
		}
		else if( interactionMethod instanceof RecommendationInteractionInterface )
		{
			((RecommendationInteractionInterface)interactionMethod).performInteraction( pb, graph, width, height, minX, minY, maxX, maxY, mouseUpX, mouseUpY, sameNode, level, globalMinX, globalMaxX, globalMinY, globalMaxY, selectedNode, released, request );			
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
			double globalMinY, double globalMaxY, DNVNode selectedNode, boolean released )
	{
		if( selectedNode == null && sameNode )
		{
			selectedNode = pb.getSelectedNode();
		}

		moveNode( selectedNode, request, pb, graph, level, width, height, minX, minY, maxX, maxY, mouseUpX, mouseUpY, sameNode, globalMinX,
				globalMaxX, globalMinY, globalMaxY, released );
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
