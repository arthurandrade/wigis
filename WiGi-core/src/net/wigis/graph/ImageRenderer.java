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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.LineMetrics;
import java.awt.geom.Arc2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVEntity;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.SubGraph;
import net.wigis.graph.dnv.animations.Animation;
import net.wigis.graph.dnv.geometry.Geometric;
import net.wigis.graph.dnv.geometry.Text;
import net.wigis.graph.dnv.utilities.GraphFunctions;
import net.wigis.graph.dnv.utilities.SortByLabelSize;
import net.wigis.graph.dnv.utilities.TextStroke;
import net.wigis.graph.dnv.utilities.Timer;
import net.wigis.graph.dnv.utilities.Vector2D;
import net.wigis.graph.dnv.utilities.Vector3D;
import net.wigis.settings.Settings;

import org.jfree.text.TextUtilities;
import org.jfree.ui.TextAnchor;

// TODO: Auto-generated Javadoc
/**
 * The Class ImageRenderer.
 * 
 * @author Brynjar Gretarsson
 */
public class ImageRenderer
{
	// public static final double WHITESPACE_BUFFER = 0.08;
	/** The icon map. */
	private static HashMap<String, Image> iconMap = new HashMap<String, Image>();

	/** The icon width. */
	private static HashMap<String, Integer> iconWidth = new HashMap<String, Integer>();

	/** The icon height. */
	private static HashMap<String, Integer> iconHeight = new HashMap<String, Integer>();

	/** Logger. */
	// // private static Log logger = LogFactory.getLog( ImageRenderer.class );

	private static Text watermark = new Text( "www.WiGis.net", new Vector2D( 0, 0 ), new Vector3D( 0.7f, 0.7f, 0.9f ),
			new Vector3D( 0.9f, 0.9f, 1f ), 18, true, true, false, false, false, false, true );

//	private static final Vector3D DARK_GREEN = new Vector3D( 0.0f, 0.7f, 0.0f );
	private static final Vector3D LIGHT_GREEN = new Vector3D( 0.5f, 1.0f, 0.5f );
//	private static final Vector3D DARK_YELLOW = new Vector3D( 0.7f, 0.7f, 0.0f );
//	private static final Vector3D LIGHT_YELLOW = new Vector3D( 1f, 1f, 0.5f );
//	private static final Vector3D DARK_RED = new Vector3D( 0.7f, 0.0f, 0.0f );
	private static final Vector3D LIGHT_RED = new Vector3D( 1.0f, 0.5f, 0.5f );
	
	private static final Vector3D SELECTED_HIGHLIGHT_COLOR = new Vector3D( 0.9f, 0.0f, 0.0f );
	/**
	 * Draw graph.
	 * 
	 * @param subgraph
	 *            the subgraph
	 * @param g
	 *            the g
	 * @param pb
	 *            the pb
	 * @param nodeSize
	 *            the node size
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @param minXPercent
	 *            the min x percent
	 * @param minYPercent
	 *            the min y percent
	 * @param maxXPercent
	 *            the max x percent
	 * @param maxYPercent
	 *            the max y percent
	 * @param ratio
	 *            the ratio
	 * @param edgeThickness
	 *            the edge thickness
	 * @param edgeColor
	 *            the edge color
	 * @param drawLabels
	 *            the draw labels
	 * @param curvedLabels
	 *            the curved labels
	 * @param outlinedLabels
	 *            the outlined labels
	 * @param labelSize
	 *            the label size
	 * @param interpolationLabels
	 *            the interpolation labels
	 * @param showSearchSelectedLabels
	 *            the show search selected labels
	 * @param showIcons
	 *            the show icons
	 * @param minX
	 *            the min x
	 * @param maxX
	 *            the max x
	 * @param minY
	 *            the min y
	 * @param maxY
	 *            the max y
	 * @param overview
	 *            the overview
	 * @param level
	 *            the level
	 * @param scaleNodesOnZoom
	 *            the scale nodes on zoom
	 * @param sortNodes
	 *            the sort nodes
	 * @param highlightNeighbors
	 *            the highlight neighbors
	 * @param highlightEdges
	 *            the highlight edges
	 * @param maxLabelLength
	 *            the max label length
	 * @param curvedLabelAngle
	 *            the curved label angle
	 * @param scaleLabels
	 *            the scale labels
	 * @param hideConflictingLabels
	 *            the hide conflicting labels
	 * @param drawLabelBox
	 *            the draw label box
	 * @param boldLabels
	 *            the bold labels
	 * @param fadeFactor
	 *            the fade factor
	 * @param maxNumberOfSelectedLabels
	 *            the max number of selected labels
	 * @param maxDistanceToHighlight
	 *            the max distance to highlight
	 * @param drawWatermark
	 *            the draw watermark
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void drawGraph( SubGraph subgraph, Graphics2D g2d, PaintBean pb, int nodeSize, int width, int height, double minXPercent,
			double minYPercent, double maxXPercent, double maxYPercent, double ratio, int edgeThickness, double edgeColor, boolean drawLabels,
			boolean curvedLabels, boolean outlinedLabels, double labelSize, boolean interpolationLabels, boolean showSearchSelectedLabels,
			boolean showIcons, double minX, double maxX, double minY, double maxY, boolean overview, int level, boolean scaleNodesOnZoom,
			boolean sortNodes, boolean highlightNeighbors, boolean highlightEdges, int maxLabelLength, int curvedLabelAngle, boolean scaleLabels,
			boolean hideConflictingLabels, boolean drawLabelBox, boolean boldLabels, float fadeFactor, int maxNumberOfSelectedLabels, int maxDistanceToHighlight,
			boolean drawWatermark, boolean drawNeighborArea, boolean drawNumberOfNodesInBox, boolean drawNeighborHighlightAsBoxes, boolean drawAllNeighborsHighlight,
			boolean alignBoxInfoRelativeToBox, Text timeText )
			throws IOException
	{
		g2d.setColor( Color.white );
		g2d.fillRect( 0, 0, width, height );
		Color standardColor = new Color( (float)edgeColor, (float)edgeColor, (float)edgeColor );

		int type = prepareRendering( subgraph, edgeThickness, g2d );

		synchronized( subgraph )
		{
			int nodeWidth;
			nodeWidth = getNodeWidth( nodeSize, minXPercent, maxXPercent, ratio, scaleNodesOnZoom );
			List<DNVNode> selectedNodes = new ArrayList<DNVNode>( subgraph.getSuperGraph().getSelectedNodes( level ).values() );

			if( drawNeighborArea )
			{
				int maxDistance = 10;
				Map<String,Boolean> headerAlignment = new HashMap<String,Boolean>();
				Map<String,Integer> drawnHeadings = new HashMap<String,Integer>();
				for( int distance = maxDistance; distance > 0; distance-- )
				{
					int i = 0;
					boolean anyNodes = false;
					for( DNVNode node : selectedNodes )
					{
						Map<Integer,DNVNode> nodes = node.getNodesAtDistance( distance );
						if( nodes != null && nodes.size() > 0 )
						{
							anyNodes = true; 
							Vector3D color = new Vector3D( LIGHT_GREEN );
							Vector3D difference = new Vector3D( LIGHT_RED );
							difference.subtract( color );
							difference.dotProduct( (float)(distance-1)/(float)maxDistance );
							color.add( difference );
							Vector3D outlineColor = new Vector3D(color);
							outlineColor.setX( (float)Math.max( 0, outlineColor.getX()-0.3 ) );
							outlineColor.setY( (float)Math.max( 0, outlineColor.getY()-0.3 ) );
							outlineColor.setZ( (float)Math.max( 0, outlineColor.getZ()-0.3 ) );
							i += drawEllipseAround( distance, subgraph.getNodes(), nodes, g2d, width, height, minXPercent, minYPercent, maxXPercent, maxYPercent, minX, maxX, minY,
									maxY, nodeWidth, color, outlineColor, pb, overview, node, drawnHeadings, drawNumberOfNodesInBox, drawNeighborHighlightAsBoxes,
									drawAllNeighborsHighlight, alignBoxInfoRelativeToBox, headerAlignment );
						}
					}
					if( !anyNodes )
					{
						maxDistance--;
					}
				}
			}
			
			// ------------------------------
			// watermark
			// ------------------------------
			if( drawWatermark )
			{
//				watermark.setPosition( (float)( ( maxX + minX ) / 2.0f + ( ( maxX - minX ) / 3.0f ) ),
//						(float)( ( maxY + minY ) / 2.0f + ( ( maxY - minY ) / 2.1f ) ) );
				watermark.setPosition( width - 80 , height - 18 );
//				watermark.setLabelSize( 18 );
				watermark.draw( g2d, pb, minXPercent, maxXPercent, minYPercent, maxYPercent, minX, maxX, minY, maxY, width, height, overview );
			}
			
			if( timeText != null )
			{
				timeText.draw( g2d, pb, minXPercent, maxXPercent, minYPercent, maxYPercent, minX, maxX, minY, maxY, width, height, overview );
			}


			// ------------------------------
			// geometric objects
			// ------------------------------
			List<Geometric> geometricObjects = subgraph.getSuperGraph().getGeometricObjects( level );
			if( geometricObjects != null )
			{
				for( Geometric object : geometricObjects )
				{
					object.draw( g2d, pb, minXPercent, maxXPercent, minYPercent, maxYPercent, minX, maxX, minY, maxY, width, height, overview );
				}
			}
			

			// ------------------------------
			// edges
			// ------------------------------
			Timer edgesTimer = new Timer( Timer.MILLISECONDS );
			edgesTimer.setStart();
			drawEdges( subgraph, g2d, nodeSize, width, height, minXPercent, minYPercent, maxXPercent, maxYPercent, ratio,
					edgeThickness, drawLabels, outlinedLabels, labelSize, minX, maxX, minY, maxY, overview, scaleNodesOnZoom, highlightNeighbors
							|| highlightEdges, standardColor, g2d, boldLabels, nodeWidth );
			edgesTimer.setEnd();

			// ------------------------------
			// nodes
			// ------------------------------
			g2d.setStroke( new BasicStroke( 1 ) );

			List<DNVNode> nodes;

			Timer transformTimer = new Timer( Timer.MILLISECONDS );
			Timer nodesTimer = new Timer( Timer.MILLISECONDS );
			Timer drawNodeTimer = new Timer( Timer.MILLISECONDS );
			SortByLabelSize sortByLabelSize = new SortByLabelSize( highlightNeighbors );
			if( subgraph != null && subgraph.getNodes() != null && subgraph.getNodes().values() != null )
			{				
				nodesTimer.setStart();
				nodes = drawNodes( subgraph, g2d, width, height, minXPercent, minYPercent, maxXPercent, maxYPercent, showIcons, minX, maxX, minY, maxY,
						highlightNeighbors, type, nodeWidth, selectedNodes, transformTimer, drawNodeTimer, sortByLabelSize, hideConflictingLabels
								&& drawLabels, maxDistanceToHighlight );
				nodesTimer.setEnd();
				
				
				// ------------------------------
				// Store Bounding Boxes
				// ------------------------------
				if( !overview )
				{
					float maxSize = 0;
					for( DNVNode node : nodes )
					{
						maxSize = Math.max( maxSize, nodeWidth * node.getRadius() );
						double tempLabelSize = getLabelSize( node, labelSize, minXPercent, maxXPercent, ratio, scaleLabels );
						maxSize = (float)Math.max( maxSize, tempLabelSize * 1.6 );
					}
					subgraph.getSuperGraph().setAttribute( "maxSize", (int)Math.round( maxSize ) );
					Map<Integer,Rectangle> boundingRectangles = new HashMap<Integer,Rectangle>();
					Map<Integer,List<DNVNode>> nodesByYPos = new HashMap<Integer,List<DNVNode>>();
					Map<Integer,Map<Integer,Integer>> nodeAndKeyToIndex = new HashMap<Integer,Map<Integer,Integer>>();
					getNodesByYPos( nodes, g2d, nodeWidth, interpolationLabels, curvedLabels, labelSize, minX, maxX, minY, maxY, minXPercent, maxXPercent,
							minYPercent, maxYPercent, width, height, ratio, scaleLabels, maxLabelLength, curvedLabelAngle, boldLabels, subgraph.getSuperGraph(), boundingRectangles,
							nodesByYPos, nodeAndKeyToIndex, true, (int)Math.round( maxSize ) );
				}

				
				// ------------------------------
				// Animations
				// ------------------------------
				if( !overview )
				{
					Animation animation;
					List<Animation> animations = subgraph.getSuperGraph().getAnimations();
					for( int i = 0; i < animations.size(); i++ )
					{
						animation = animations.get( i );
						animation.paint( g2d, pb );
						if( animation.hasCompleted() )
						{
							animations.remove( i );
							i--;
						}
					}
				}

				// ------------------------------
				// labels
				// ------------------------------
				Timer labelsTimer = new Timer( Timer.MILLISECONDS );
				labelsTimer.setStart();
				drawLabels( subgraph, width, height, minXPercent, minYPercent, maxXPercent, maxYPercent, ratio, drawLabels, curvedLabels,
						outlinedLabels, labelSize, interpolationLabels, showSearchSelectedLabels, minX, maxX, minY, maxY, overview, level,
						highlightNeighbors, maxLabelLength, curvedLabelAngle, scaleLabels, hideConflictingLabels, drawLabelBox, g2d, nodeWidth,
						nodes, selectedNodes, sortByLabelSize, boldLabels, fadeFactor, maxNumberOfSelectedLabels );
				labelsTimer.setEnd();
				// ------------------------------
				if( Settings.DEBUG && !overview )
				{
					System.out.println( "Edges took " + edgesTimer.getLastSegment( Timer.SECONDS ) + " seconds." );
					System.out.println( "Labels took " + labelsTimer.getLastSegment( Timer.SECONDS ) + " seconds." );
					System.out.println( "Nodes took " + nodesTimer.getLastSegment( Timer.SECONDS ) + " seconds." );
					System.out.println( "Transforming node positions took " + transformTimer.getTotalTime( Timer.SECONDS ) + " seconds." );
					System.out.println( "drawNode took " + drawNodeTimer.getTotalTime( Timer.SECONDS ) + " seconds." );
				}
			}

			// Fix colors of nodes based on distance
			if( !drawNeighborHighlightAsBoxes )
			{
				for( DNVNode node : subgraph.getNodesList() )
				{
					if( node.hasProperty( "originalColor" ) )
					{
						node.setColor( node.getProperty( "originalColor" ) );
						node.removeProperty( "originalColor" );
					}
				}
			}
		}
	}

	public static int getNodeWidth( int nodeSize, double minXPercent, double maxXPercent, double ratio, boolean scaleNodesOnZoom )
	{
		int nodeWidth;
		if( scaleNodesOnZoom )
		{
			nodeWidth = getNodeWidth( nodeSize, minXPercent, maxXPercent, ratio );
		}
		else
		{
			nodeWidth = getNodeWidth( nodeSize, 0, 1, ratio );
		}
		return nodeWidth;
	}

	/**
	 * @param nodesAtDistance
	 * @param g2d
	 * @param width
	 * @param height
	 * @param minXPercent
	 * @param minYPercent
	 * @param maxXPercent
	 * @param maxYPercent
	 * @param minX
	 * @param maxX
	 * @param minY
	 * @param maxY
	 */
	private static int drawEllipseAround( int hops, Map<Integer,DNVNode> allNodes, Map<Integer, DNVNode> nodes, Graphics2D g2d, int width, int height, double minXPercent,
			double minYPercent, double maxXPercent, double maxYPercent, double minX, double maxX, double minY, double maxY, float nodeWidth, Vector3D fillColor,
			Vector3D outlineColor, PaintBean pb, boolean overview, DNVNode selectedNode, Map<String, Integer> drawnHeadings, boolean drawNumberOfNodesInBox,
			boolean drawNeighborHighlightAsBoxes, boolean drawAllNeighborsHighlight, boolean alignBoxInfoRelativeToBox, Map<String, Boolean> headerAlignment )
	{
		if( nodes != null )
		{
			Vector2D maxPos = new Vector2D(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY );
			Vector2D minPos = new Vector2D(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY );
			float maxRadius = Float.NEGATIVE_INFINITY;
			synchronized( nodes )
			{
				for( DNVNode node : nodes.values() )
				{
					if( node.getPosition( true ).getX() > maxPos.getX() )
					{
						maxPos.setX( node.getPosition( true ).getX() );
					}
					if( node.getPosition( true ).getY() > maxPos.getY() )
					{
						maxPos.setY( node.getPosition( true ).getY() );
					}
		
					if( node.getPosition( true ).getX() < minPos.getX() )
					{
						minPos.setX( node.getPosition( true ).getX() );
					}
					if( node.getPosition( true ).getY() < minPos.getY() )
					{
						minPos.setY( node.getPosition( true ).getY() );
					}
					
					if( node.getRadius() > maxRadius )
					{
						maxRadius = node.getRadius();
					}

				}
			}
				
			Vector2D maxPosScreen = transformPosition( minX, maxX, minY, maxY, minXPercent, maxXPercent, minYPercent, maxYPercent, width, height, maxPos );
			Vector2D minPosScreen = transformPosition( minX, maxX, minY, maxY, minXPercent, maxXPercent, minYPercent, maxYPercent, width, height, minPos );
			maxPosScreen.setX( maxPosScreen.getX() + maxRadius * nodeWidth );
			maxPosScreen.setY( maxPosScreen.getY() + maxRadius * nodeWidth );
			
			minPosScreen.setX( minPosScreen.getX() - maxRadius * nodeWidth );
			minPosScreen.setY( minPosScreen.getY() - maxRadius * nodeWidth );
	

			for( DNVNode node : allNodes.values() )
			{
				if( nodes.get( node.getId() ) == null )
				{
					// This is not one of the nodes inside the box
					Vector2D screenPos = transformPosition( minX, maxX, minY, maxY, minXPercent, maxXPercent, minYPercent, maxYPercent, width, height, node.getPosition() );
					float x = screenPos.getX();
					float y = screenPos.getY();
					if( x >= minPosScreen.getX() && x <= maxPosScreen.getX() && y >= minPosScreen.getY() && y <= maxPosScreen.getY() )
					{
						// this node overlaps with the box, so don't draw it
						if( !drawAllNeighborsHighlight )
						{
							return 0;
						}
					}
				}
			}
			
			
			if( drawNeighborHighlightAsBoxes )
			{
				g2d.setColor( new Color( fillColor.getX(), fillColor.getY(), fillColor.getZ(), 0.7f ) );
				g2d.fillRoundRect( (int)Math.round( minPosScreen.getX() ), (int)Math.round( minPosScreen.getY() ), (int)Math.round( maxPosScreen.getX() - minPosScreen.getX() ),
						(int)Math.round( maxPosScreen.getY() - minPosScreen.getY() ), 10, 10 );
				g2d.setStroke( new BasicStroke( 3 ) );
				g2d.setColor( new Color( outlineColor.getX(), outlineColor.getY(), outlineColor.getZ(), 0.7f ) );
				g2d.drawRoundRect( (int)Math.round( minPosScreen.getX() ), (int)Math.round( minPosScreen.getY() ), (int)Math.round( maxPosScreen.getX() - minPosScreen.getX() ),
						(int)Math.round( maxPosScreen.getY() - minPosScreen.getY() ), 10, 10 );
			}
			else
			{
				for( DNVNode node : nodes.values() )
				{
					Vector3D color;
					if( !node.hasProperty( "originalColor" ) )
					{
						node.setProperty( "originalColor", node.getColor().toString() );
						color = fillColor;
					}
					else
					{
						color = new Vector3D( node.getColor() );
						color.add( fillColor );
						color.dotProduct( 0.5f );
					}
					node.setColor( color );
				}
			}
			
			Vector2D position = new Vector2D();
			Vector2D selectedNodeScreenPos = transformPosition( minX, maxX, minY, maxY, minXPercent, maxXPercent, minYPercent, maxYPercent, width, height, selectedNode.getPosition() );
			float xDiff = Math.abs( selectedNodeScreenPos.getX() - (minPosScreen.getX() + (maxPosScreen.getX() - minPosScreen.getX()) / 2.0f) );
			float yDiff = Math.abs( selectedNodeScreenPos.getY() - (minPosScreen.getY() + (maxPosScreen.getY() - minPosScreen.getY()) / 2.0f) );
			
			String key;
			if( alignBoxInfoRelativeToBox )
			{
				if( headerAlignment.containsKey( "X-align" + selectedNode.getId() ) || ( !headerAlignment.containsKey( "Y-align" + selectedNode.getId() ) && xDiff >= yDiff ) )
				{
					headerAlignment.put( "X-align" + selectedNode.getId(), true );
					position = new Vector2D( minPosScreen.getX() + ( maxPosScreen.getX() - minPosScreen.getX() ) / 2.0f, 14 );
					key = "X" + hops;
					if( !drawnHeadings.containsKey( key ) )
					{
						Text t = new Text( "Dist " + hops, position, new Vector3D( 1, 1, 1 ), fillColor, 12, true, true, true, false, false, false, true );
						t.draw( g2d, pb, minXPercent, maxXPercent, minYPercent, maxYPercent, minX, maxX, minY, maxY, nodeWidth, height, overview );
						drawnHeadings.put( key, 1 );
					}
					else
					{
						drawnHeadings.put( key, drawnHeadings.get( key ) + 1 );
					}
				}
				else
				{
					headerAlignment.put( "Y-align" + selectedNode.getId(), true );
					position = new Vector2D( 35, minPosScreen.getY() + ( maxPosScreen.getY() - minPosScreen.getY() ) / 2.0f );
					key = "Y" + hops;
					if( !drawnHeadings.containsKey( key ) )
					{
						Text t = new Text( "Dist " + hops, position, new Vector3D( 1, 1, 1 ), fillColor, 12, true, true, true, false, false, false, true );
						t.draw( g2d, pb, minXPercent, maxXPercent, minYPercent, maxYPercent, minX, maxX, minY, maxY, nodeWidth, height, overview );
						drawnHeadings.put( key, 1 );
					}
					else
					{
						drawnHeadings.put( key, drawnHeadings.get( key ) + 1 );
					}
				}
			}
			else
			{
				position = new Vector2D( 70 * hops, 14 );
				key = "Align" + hops;
				if( !drawnHeadings.containsKey( key ) )
				{
					Text t = new Text( "Dist " + hops, position, new Vector3D( 1, 1, 1 ), fillColor, 12, true, true, true, false, false, false, true );
					t.draw( g2d, pb, minXPercent, maxXPercent, minYPercent, maxYPercent, minX, maxX, minY, maxY, nodeWidth, height, overview );
					drawnHeadings.put( key, 1 );
				}
				else
				{
					drawnHeadings.put( key, drawnHeadings.get( key ) + 1 );
				}
			}

			position.setY( position.getY() + ( drawnHeadings.get( key ) * 20 ) );
			if( drawNumberOfNodesInBox )
			{
				Text t = new Text( nodes.size() + " nodes", position, new Vector3D( 1, 1, 1 ), fillColor, 12, true, true, true, false, false, false, true );
				t.draw( g2d, pb, minXPercent, maxXPercent, minYPercent, maxYPercent, minX, maxX, minY, maxY, nodeWidth, height, overview );
			}
			
			return 1;
		}
		
		return 0;
	}

	/**
	 * Draw labels.
	 * 
	 * @param subgraph
	 *            the subgraph
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @param minXPercent
	 *            the min x percent
	 * @param minYPercent
	 *            the min y percent
	 * @param maxXPercent
	 *            the max x percent
	 * @param maxYPercent
	 *            the max y percent
	 * @param ratio
	 *            the ratio
	 * @param drawLabels
	 *            the draw labels
	 * @param curvedLabels
	 *            the curved labels
	 * @param outlinedLabels
	 *            the outlined labels
	 * @param labelSize
	 *            the label size
	 * @param interpolationLabels
	 *            the interpolation labels
	 * @param showSearchSelectedLabels
	 *            the show search selected labels
	 * @param minX
	 *            the min x
	 * @param maxX
	 *            the max x
	 * @param minY
	 *            the min y
	 * @param maxY
	 *            the max y
	 * @param overview
	 *            the overview
	 * @param level
	 *            the level
	 * @param highlightNeighbors
	 *            the highlight neighbors
	 * @param maxLabelLength
	 *            the max label length
	 * @param curvedLabelAngle
	 *            the curved label angle
	 * @param scaleLabels
	 *            the scale labels
	 * @param hideConflictingLabels
	 *            the hide conflicting labels
	 * @param drawLabelBox
	 *            the draw label box
	 * @param g2d
	 *            the g2d
	 * @param nodeWidth
	 *            the node width
	 * @param nodes
	 *            the nodes
	 * @param selectedNodes
	 *            the selected nodes
	 * @param sortByLabelSize
	 *            the sort by label size
	 * @param boldLabels
	 *            the bold labels
	 * @param fadeFactor
	 *            the fade factor
	 * @param maxNumberOfSelectedLabels
	 *            the max number of selected labels
	 * @throws MalformedURLException
	 *             the malformed url exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private static void drawLabels( SubGraph subgraph, int width, int height, double minXPercent, double minYPercent, double maxXPercent,
			double maxYPercent, double ratio, boolean drawLabels, boolean curvedLabels, boolean outlinedLabels, double labelSize,
			boolean interpolationLabels, boolean showSearchSelectedLabels, double minX, double maxX, double minY, double maxY, boolean overview,
			int level, boolean highlightNeighbors, int maxLabelLength, int curvedLabelAngle, boolean scaleLabels, boolean hideConflictingLabels,
			boolean drawLabelBox, Graphics2D g2d, int nodeWidth, List<DNVNode> nodes, List<DNVNode> selectedNodes, SortByLabelSize sortByLabelSize,
			boolean boldLabels, float fadeFactor, int maxNumberOfSelectedLabels ) throws MalformedURLException, IOException
	{
		DNVNode tempNode;
		drawMustDrawLabels( subgraph, width, height, minXPercent, minYPercent, maxXPercent, maxYPercent, ratio, curvedLabels, outlinedLabels,
				labelSize, interpolationLabels, minX, maxX, minY, maxY, overview, level, highlightNeighbors, maxLabelLength, curvedLabelAngle,
				scaleLabels, hideConflictingLabels, drawLabelBox, g2d, nodeWidth, sortByLabelSize, boldLabels, fadeFactor );
		
		if( drawLabels )
		{
			if( hideConflictingLabels )
			{
				nodes = getNodesWithoutOverlappingLabels( nodes, g2d, nodeWidth, interpolationLabels, curvedLabels, labelSize, minX, maxX, minY,
						maxY, minXPercent, maxXPercent, minYPercent, maxYPercent, width, height, ratio, scaleLabels, maxLabelLength,
						curvedLabelAngle, boldLabels, fadeFactor, subgraph.getSuperGraph() );
			}
			for( int i = 0; i < nodes.size(); i++ )
			{
				tempNode = nodes.get( i );
//				if( tempNode.isVisible() )
//				{
					boolean highlighted = highlightNode( highlightNeighbors, tempNode );
					drawLabel( width, height, minXPercent, minYPercent, maxXPercent, maxYPercent, ratio, drawLabels, curvedLabels, outlinedLabels,
							labelSize, interpolationLabels, minX, maxX, minY, maxY, highlightNeighbors, maxLabelLength, curvedLabelAngle,
							scaleLabels, drawLabelBox, tempNode, g2d, nodeWidth, boldLabels, highlighted );
//				}
			}
		}
		else if( showSearchSelectedLabels 
				&& !overview
				&& 	( subgraph.getSuperGraph().getSelectedNodes( level ).size() > 0 
					|| subgraph.getSuperGraph().getSelectedEdges( level ).size() > 0 ) )
		{
			// int maxNumberLabelsShown = 200;
			// int numberLabelsShown = 0;
			Map<Integer, DNVNode> neighborSelectedNodes = new HashMap<Integer, DNVNode>();
			for( DNVNode selectedNode : selectedNodes )
			{
				neighborSelectedNodes.put( selectedNode.getId(), selectedNode );
				for( DNVNode neighbor : selectedNode.getNeighborMap( true ).values() )
				{
					neighborSelectedNodes.put( neighbor.getId(), neighbor );
				}
			}
			nodes = new ArrayList<DNVNode>( neighborSelectedNodes.values() );

			if( hideConflictingLabels )
			{
				nodes = getNodesWithoutOverlappingLabels( nodes, g2d, nodeWidth, interpolationLabels, curvedLabels, labelSize, minX, maxX, minY,
						maxY, minXPercent, maxXPercent, minYPercent, maxYPercent, width, height, ratio, scaleLabels, maxLabelLength,
						curvedLabelAngle, boldLabels, fadeFactor, subgraph.getSuperGraph() );
			}
			for( int i = 0; i < nodes.size(); i++ )
			{
				tempNode = nodes.get( i );
				if( tempNode.getLabel() != null && !tempNode.getLabel().trim().equals( "" )/* && tempNode.isVisible()*/ )
				{
					if( tempNode.isNeighborSelected() || tempNode.isEdgeSelected() || tempNode.isSelected() )
					{
						boolean selected = highlightNode( highlightNeighbors, tempNode );
						boolean temp = tempNode.isSelected();
						tempNode.setSelected( selected );
						drawLabel( width, height, minXPercent, minYPercent, maxXPercent, maxYPercent, ratio, selected, curvedLabels,
								outlinedLabels, labelSize, interpolationLabels, minX, maxX, minY, maxY, highlightNeighbors, maxLabelLength,
								curvedLabelAngle, scaleLabels, drawLabelBox, tempNode, g2d, nodeWidth, boldLabels, highlightNode( highlightNeighbors, tempNode )  );
						tempNode.setSelected( temp );
					}
				}
			}

		}
	}

	/**
	 * @param highlightNeighbors
	 * @param tempNode
	 * @return
	 */
	public static boolean highlightNode( boolean highlightNeighbors, DNVNode tempNode )
	{
		boolean highlighted = tempNode.isSelected() || ( tempNode.isHighlighted() && tempNode.getHighlightColor() == null )
				|| ( highlightNeighbors && ( tempNode.isNeighborSelected() || tempNode.isEdgeSelected() ) );
		return highlighted;
	}

	/**
	 * Draw must draw labels.
	 * 
	 * @param subgraph
	 *            the subgraph
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @param minXPercent
	 *            the min x percent
	 * @param minYPercent
	 *            the min y percent
	 * @param maxXPercent
	 *            the max x percent
	 * @param maxYPercent
	 *            the max y percent
	 * @param ratio
	 *            the ratio
	 * @param curvedLabels
	 *            the curved labels
	 * @param outlinedLabels
	 *            the outlined labels
	 * @param labelSize
	 *            the label size
	 * @param interpolationLabels
	 *            the interpolation labels
	 * @param minX
	 *            the min x
	 * @param maxX
	 *            the max x
	 * @param minY
	 *            the min y
	 * @param maxY
	 *            the max y
	 * @param overview
	 *            the overview
	 * @param level
	 *            the level
	 * @param highlightNeighbors
	 *            the highlight neighbors
	 * @param maxLabelLength
	 *            the max label length
	 * @param curvedLabelAngle
	 *            the curved label angle
	 * @param scaleLabels
	 *            the scale labels
	 * @param hideConflictingLabels
	 *            the hide conflicting labels
	 * @param drawLabelBox
	 *            the draw label box
	 * @param g2d
	 *            the g2d
	 * @param nodeWidth
	 *            the node width
	 * @param sortByLabelSize
	 *            the sort by label size
	 * @param boldLabels
	 *            the bold labels
	 * @param fadeFactor
	 *            the fade factor
	 * @throws MalformedURLException
	 *             the malformed url exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private static void drawMustDrawLabels( SubGraph subgraph, int width, int height, double minXPercent, double minYPercent, double maxXPercent,
			double maxYPercent, double ratio, boolean curvedLabels, boolean outlinedLabels, double labelSize, boolean interpolationLabels,
			double minX, double maxX, double minY, double maxY, boolean overview, int level, boolean highlightNeighbors, int maxLabelLength,
			int curvedLabelAngle, boolean scaleLabels, boolean hideConflictingLabels, boolean drawLabelBox, Graphics2D g2d, int nodeWidth,
			SortByLabelSize sortByLabelSize, boolean boldLabels, float fadeFactor ) throws MalformedURLException, IOException
	{
		if( !overview && subgraph.getSuperGraph().getMustDrawLabels( level ).size() > 0 )
		{
			List<DNVNode> mustDrawNodes = subgraph.getSortedMustDrawLabelNodes();
			if( mustDrawNodes == null )
			{
				List<DNVEntity> entities = new ArrayList<DNVEntity>( subgraph.getSuperGraph().getMustDrawLabels( level ).values() );
				mustDrawNodes = new ArrayList<DNVNode>();
				DNVNode node;
				for( DNVEntity entity : entities )
				{
					if( entity instanceof DNVNode )
					{
						node = (DNVNode)entity;
						if( !( node.isSelected() || ( highlightNeighbors && ( node.isEdgeSelected() || node.isNeighborSelected() ) ) ) )
						{
							mustDrawNodes.add( node );
						}
					}
				}
				Collections.sort( mustDrawNodes, sortByLabelSize );
				subgraph.setSortedMustDrawLabelNodes( mustDrawNodes );
			}
			if( hideConflictingLabels )
			{
				mustDrawNodes = getNodesWithoutOverlappingLabels( mustDrawNodes, g2d, nodeWidth, interpolationLabels, curvedLabels, labelSize, minX,
						maxX, minY, maxY, minXPercent, maxXPercent, minYPercent, maxYPercent, width, height, ratio, scaleLabels, maxLabelLength,
						curvedLabelAngle, boldLabels, fadeFactor, subgraph.getSuperGraph() );
			}
			for( DNVNode node : mustDrawNodes )
			{
				drawLabel( width, height, minXPercent, minYPercent, maxXPercent, maxYPercent, ratio, true, curvedLabels, outlinedLabels, labelSize,
						interpolationLabels, minX, maxX, minY, maxY, highlightNeighbors, maxLabelLength, curvedLabelAngle, scaleLabels, drawLabelBox,
						node, g2d, nodeWidth, boldLabels, highlightNode( highlightNeighbors, node ) );
			}
		}
	}

	/**
	 * Draw nodes.
	 * 
	 * @param subgraph
	 *            the subgraph
	 * @param g
	 *            the g
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @param minXPercent
	 *            the min x percent
	 * @param minYPercent
	 *            the min y percent
	 * @param maxXPercent
	 *            the max x percent
	 * @param maxYPercent
	 *            the max y percent
	 * @param showIcons
	 *            the show icons
	 * @param minX
	 *            the min x
	 * @param maxX
	 *            the max x
	 * @param minY
	 *            the min y
	 * @param maxY
	 *            the max y
	 * @param highlightNeighbors
	 *            the highlight neighbors
	 * @param type
	 *            the type
	 * @param nodeWidth
	 *            the node width
	 * @param selectedNodes
	 *            the selected nodes
	 * @param transformTimer
	 *            the transform timer
	 * @param drawNodeTimer
	 *            the draw node timer
	 * @param sortByLabelSize
	 *            the sort by label size
	 * @param sortNodes
	 *            the sort nodes
	 * @param maxDistanceToHighlight
	 *            the max distance to highlight
	 * @return the list
	 * @throws MalformedURLException
	 *             the malformed url exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private static List<DNVNode> drawNodes( SubGraph subgraph, Graphics2D g, int width, int height, double minXPercent, double minYPercent,
			double maxXPercent, double maxYPercent, boolean showIcons, double minX, double maxX, double minY, double maxY,
			boolean highlightNeighbors, int type, int nodeWidth, List<DNVNode> selectedNodes, Timer transformTimer, Timer drawNodeTimer,
			SortByLabelSize sortByLabelSize, boolean sortNodes, int maxDistanceToHighlight ) throws MalformedURLException, IOException
	{
		DNVNode tempNode;
		Vector2D tempPos;
		List<DNVNode> nodes;
		Vector3D color;
		nodes = subgraph.getSortedNodes();
		if( nodes == null )
		{
			nodes = subgraph.getNodesList();
			if( sortNodes )
			{
				Collections.sort( nodes, sortByLabelSize );
				subgraph.setSortedNodes( nodes );
			}
		}
		for( int i = 0; i < nodes.size(); i++ )
		{
			tempNode = nodes.get( i );
			color = tempNode.getColor();
			transformTimer.setStart();
			tempPos = transformPosition( minX, maxX, minY, maxY, minXPercent, maxXPercent, minYPercent, maxYPercent, width, height, tempNode
					.getPosition( true ) );
			transformTimer.setEnd();
			drawNodeTimer.setStart();
			try
			{
				drawNode( g, showIcons, tempNode, tempNode.getIcon(), tempPos, color, nodeWidth, type, maxDistanceToHighlight );
			}
			catch( IOException ioe )
			{
				ioe.printStackTrace();
			}
			drawNodeTimer.setEnd();
		}

		return nodes;
	}

	/**
	 * Draw edges.
	 * 
	 * @param subgraph
	 *            the subgraph
	 * @param g
	 *            the g
	 * @param nodeSize
	 *            the node size
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @param minXPercent
	 *            the min x percent
	 * @param minYPercent
	 *            the min y percent
	 * @param maxXPercent
	 *            the max x percent
	 * @param maxYPercent
	 *            the max y percent
	 * @param ratio
	 *            the ratio
	 * @param edgeThickness
	 *            the edge thickness
	 * @param drawLabels
	 *            the draw labels
	 * @param outlinedLabels
	 *            the outlined labels
	 * @param labelSize
	 *            the label size
	 * @param minX
	 *            the min x
	 * @param maxX
	 *            the max x
	 * @param minY
	 *            the min y
	 * @param maxY
	 *            the max y
	 * @param overview
	 *            the overview
	 * @param scaleNodesOnZoom
	 *            the scale nodes on zoom
	 * @param highlightNeighbors
	 *            the highlight neighbors
	 * @param standardColor
	 *            the standard color
	 * @param g2d
	 *            the g2d
	 * @param boldLabels
	 *            the bold labels
	 * @return the int
	 */
	private static int drawEdges( SubGraph subgraph, Graphics g, int nodeSize, int width, int height, double minXPercent, double minYPercent,
			double maxXPercent, double maxYPercent, double ratio, int edgeThickness, boolean drawLabels, boolean outlinedLabels, double labelSize,
			double minX, double maxX, double minY, double maxY, boolean overview, boolean scaleNodesOnZoom, boolean highlightNeighbors,
			Color standardColor, Graphics2D g2d, boolean boldLabels, int nodeWidth )
	{
		Collection<DNVEdge> edges;
		if( subgraph.getEdges() != null )
		{
			edges = subgraph.getEdges().values();
		}
		else
		{
			edges = null;
		}
		List<DNVEdge> selectedEdges = new ArrayList<DNVEdge>();

		if( edges != null )
		{
			synchronized( edges )
			{
				for( DNVEdge tempEdge : edges )
				{
					if( tempEdge.getFrom() != null && tempEdge.getTo() != null )
					{
						if( tempEdge.isSelected() || tempEdge.isHighlighted() || ( highlightNeighbors && ( tempEdge.getFrom().isSelected() || tempEdge.getTo().isSelected() ) ) )
						{
							selectedEdges.add( tempEdge );
						}
						else
						{
							drawEdge( g2d, width, height, minXPercent, minYPercent, maxXPercent, maxYPercent, minX, maxX, minY, maxY, standardColor,
 tempEdge, drawLabels,
									outlinedLabels, (int)labelSize, overview, nodeWidth, edgeThickness, boldLabels );
						}
					}
				}

				// Draw the selected edges last
				Color selectedEdgeColor = new Color( SELECTED_HIGHLIGHT_COLOR.getX(), SELECTED_HIGHLIGHT_COLOR.getY(), SELECTED_HIGHLIGHT_COLOR.getZ() );
				for( DNVEdge tempEdge : selectedEdges )
				{
					drawEdge( g2d, width, height, minXPercent, minYPercent, maxXPercent, maxYPercent, minX, maxX, minY, maxY, selectedEdgeColor, tempEdge,
 true, outlinedLabels,
							(int)labelSize, overview, nodeWidth, edgeThickness, boldLabels );
				}
			}
		}
		return nodeWidth;
	}

	/**
	 * Prepare rendering.
	 * 
	 * @param subgraph
	 *            the subgraph
	 * @param edgeThickness
	 *            the edge thickness
	 * @param g2d
	 *            the g2d
	 * @return the int
	 */
	private static int prepareRendering( SubGraph subgraph, int edgeThickness, Graphics2D g2d )
	{
		g2d.setStroke( new BasicStroke( edgeThickness ) );
		try
		{
			if( subgraph.getEdges().values().size() < 1000 )
			{
				g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
			}
		}
		catch( NullPointerException npe )
		{}
		g2d.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED );
		g2d.setRenderingHint( RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF );
		g2d.setRenderingHint( RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED );
		g2d.setRenderingHint( RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE );
		g2d.setRenderingHint( RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED );
		g2d.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF );
		g2d.setRenderingHint( RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF );
		g2d.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR );

		int type = CIRCLE;
		if( subgraph != null && subgraph.getNodes() != null && subgraph.getNodes().values() != null && subgraph.getNodes().values().size() > 1000 )
		{
			type = RECTANGLE;
		}
		return type;
	}

	/**
	 * Draw label.
	 * 
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @param minXPercent
	 *            the min x percent
	 * @param minYPercent
	 *            the min y percent
	 * @param maxXPercent
	 *            the max x percent
	 * @param maxYPercent
	 *            the max y percent
	 * @param ratio
	 *            the ratio
	 * @param drawLabels
	 *            the draw labels
	 * @param curvedLabels
	 *            the curved labels
	 * @param outlinedLabels
	 *            the outlined labels
	 * @param labelSize
	 *            the label size
	 * @param interpolationLabels
	 *            the interpolation labels
	 * @param minX
	 *            the min x
	 * @param maxX
	 *            the max x
	 * @param minY
	 *            the min y
	 * @param maxY
	 *            the max y
	 * @param highlightNeighbors
	 *            the highlight neighbors
	 * @param maxLabelLength
	 *            the max label length
	 * @param curvedLabelAngle
	 *            the curved label angle
	 * @param scaleLabels
	 *            the scale labels
	 * @param drawLabelBox
	 *            the draw label box
	 * @param tempNode
	 *            the temp node
	 * @param g2d
	 *            the g2d
	 * @param nodeWidth
	 *            the node width
	 * @param boldLabels
	 *            the bold labels
	 * @throws MalformedURLException
	 *             the malformed url exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private static void drawLabel( int width, int height, double minXPercent, double minYPercent, double maxXPercent, double maxYPercent,
			double ratio, boolean drawLabels, boolean curvedLabels, boolean outlinedLabels, double labelSize, boolean interpolationLabels,
			double minX, double maxX, double minY, double maxY, boolean highlightNeighbors, int maxLabelLength, int curvedLabelAngle,
			boolean scaleLabels, boolean drawLabelBox, DNVNode tempNode, Graphics2D g2d, int nodeWidth, boolean boldLabels, boolean highlighted )
			throws MalformedURLException, IOException
	{
		Vector2D tempPos;
		tempPos = transformPosition( minX, maxX, minY, maxY, minXPercent, maxXPercent, minYPercent, maxYPercent, width, height, tempNode
				.getPosition( true ) );
		drawLabel( g2d, tempNode, tempPos, nodeWidth, tempNode.getLabel( interpolationLabels ), drawLabels, curvedLabels, outlinedLabels, labelSize,
				minXPercent, maxXPercent, ratio, scaleLabels, highlightNeighbors, maxLabelLength, curvedLabelAngle, drawLabelBox, boldLabels, highlighted );
	}

	
	public static final int DEFAULT_LABEL_HEIGHT = 10;
	/**
	 * Gets the nodes without overlapping labels.
	 * 
	 * @param nodes
	 *            the nodes
	 * @param g
	 *            the g
	 * @param nodeWidth
	 *            the node width
	 * @param interpolationLabels
	 *            the interpolation labels
	 * @param curvedLabels
	 *            the curved labels
	 * @param labelSize
	 *            the label size
	 * @param minX
	 *            the min x
	 * @param maxX
	 *            the max x
	 * @param minY
	 *            the min y
	 * @param maxY
	 *            the max y
	 * @param minXPercent
	 *            the min x percent
	 * @param maxXPercent
	 *            the max x percent
	 * @param minYPercent
	 *            the min y percent
	 * @param maxYPercent
	 *            the max y percent
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @param ratio
	 *            the ratio
	 * @param scaleLabels
	 *            the scale labels
	 * @param maxLabelLength
	 *            the max label length
	 * @param curvedLabelAngle
	 *            the curved label angle
	 * @param boldLabels
	 *            the bold labels
	 * @param fadeFactor
	 *            the fade factor
	 * @return the nodes without overlapping labels
	 */
	private static List<DNVNode> getNodesWithoutOverlappingLabels( List<DNVNode> nodes, Graphics2D g, int nodeWidth, boolean interpolationLabels,
			boolean curvedLabels, double labelSize, double minX, double maxX, double minY, double maxY, double minXPercent, double maxXPercent,
			double minYPercent, double maxYPercent, int width, int height, double ratio, boolean scaleLabels, int maxLabelLength,
			int curvedLabelAngle, boolean boldLabels, float fadeFactor, DNVGraph graph )
	{
		List<DNVNode> goodNodes = new ArrayList<DNVNode>();
//		DNVNode node;
		DNVNode node2;
		Rectangle boundingRectangle;
		Rectangle boundingRectangle2;
		float overlap = 1;
		Map<Integer,Rectangle> boundingRectangles = new HashMap<Integer,Rectangle>();
		Map<Integer,List<DNVNode>> nodesByYPos = new HashMap<Integer,List<DNVNode>>();
		Map<Integer,Map<Integer,Integer>> nodeAndKeyToIndex = new HashMap<Integer,Map<Integer,Integer>>();
		getNodesByYPos( nodes, g, nodeWidth, interpolationLabels, curvedLabels, labelSize, minX, maxX, minY, maxY, minXPercent, maxXPercent,
				minYPercent, maxYPercent, width, height, ratio, scaleLabels, maxLabelLength, curvedLabelAngle, boldLabels, graph, boundingRectangles,
				nodesByYPos, nodeAndKeyToIndex, false, DEFAULT_LABEL_HEIGHT );
		
		for( DNVNode node : nodes )
		{
//			node = nodes.get( i );
			if( node.getLabel() != null && !node.getLabel().equals( "" )/* && node.isVisible()*/ )
			{
				overlap = 1;
				boundingRectangle = boundingRectangles.get( node.getId() );
				Integer key = getKey( boundingRectangle.positionY, DEFAULT_LABEL_HEIGHT );
				List<DNVNode> nodes2 = nodesByYPos.get( key );
				try
				{
					int end = nodeAndKeyToIndex.get( key ).get( node.getId() );
					for( int j = nodes2.size() - 1; j > end; j-- )
					{
						node2 = nodes2.get( j );
						if( !node2.getLabel().equals( "" )/* && node2.isVisible()*/ /*&& !node2.hasProperty( "faded" )*/ )
						{
							boundingRectangle2 = boundingRectangles.get( node2.getId() );
		
							if( overlap( boundingRectangle, boundingRectangle2 ) )
							{
								overlap /= fadeFactor;
								if( overlap < 0.01f )
								{
									break;
								}
							}
						}
					}
				}
				catch( NullPointerException npe )
				{
					System.out.println( nodeAndKeyToIndex.get( key ) + " for key " + key );
					System.out.println( "node:" + node );
					System.out.println( "node id:" + node.getId() );
					System.out.println( "value we got:" + nodeAndKeyToIndex.get( key ).get( node.getId() ) );
					System.out.println( "Keys:" );
					for( Integer tempKey : nodeAndKeyToIndex.keySet() )
					{
						System.out.println( tempKey );
					}
					npe.printStackTrace();
				}
				if( overlap < 1 )
				{
					node.setProperty( "faded", "" + overlap );
				}
				else
				{
					node.removeProperty( "faded" );
				}
			}
			
			goodNodes.add( node );
		}
	
		return goodNodes;
	}

	/**
	 * @param nodes
	 * @param g
	 * @param nodeWidth
	 * @param interpolationLabels
	 * @param curvedLabels
	 * @param labelSize
	 * @param minX
	 * @param maxX
	 * @param minY
	 * @param maxY
	 * @param minXPercent
	 * @param maxXPercent
	 * @param minYPercent
	 * @param maxYPercent
	 * @param width
	 * @param height
	 * @param ratio
	 * @param scaleLabels
	 * @param maxLabelLength
	 * @param curvedLabelAngle
	 * @param boldLabels
	 * @param graph
	 * @param boundingRectangles
	 * @param nodesByYPos
	 * @param nodeAndKeyToIndex
	 */
	private static void getNodesByYPos( List<DNVNode> nodes, Graphics2D g, int nodeWidth, boolean interpolationLabels, boolean curvedLabels,
			double labelSize, double minX, double maxX, double minY, double maxY, double minXPercent, double maxXPercent, double minYPercent,
			double maxYPercent, int width, int height, double ratio, boolean scaleLabels, int maxLabelLength, int curvedLabelAngle,
			boolean boldLabels, DNVGraph graph, Map<Integer, Rectangle> boundingRectangles, Map<Integer, List<DNVNode>> nodesByYPos,
			Map<Integer, Map<Integer, Integer>> nodeAndKeyToIndex, boolean includeNodeBounds, int areaHeight )
	{
		Vector2D tempPos;
		Rectangle boundingRectangle;
		for( int i = 0; i < nodes.size(); i++ )
		{
			DNVNode tempNode = nodes.get( i );
			tempPos = transformPosition( minX, maxX, minY, maxY, minXPercent, maxXPercent, minYPercent, maxYPercent, width, height, tempNode.getPosition( true ) );
			boundingRectangle = getRectangleBoundingTheLabel( tempNode, tempPos, g, nodeWidth, tempNode.getLabel( interpolationLabels ), curvedLabels,
					labelSize, minXPercent, maxXPercent, ratio, scaleLabels, maxLabelLength, curvedLabelAngle, boldLabels, nodes.size() > 1000, includeNodeBounds );
			boundingRectangles.put( tempNode.getId(), boundingRectangle );
			Integer key = getKey( boundingRectangle.positionY, areaHeight );
			addByKey( nodesByYPos, nodeAndKeyToIndex, tempNode, key );
			addByKey( nodesByYPos, nodeAndKeyToIndex, tempNode, key-1 );
			addByKey( nodesByYPos, nodeAndKeyToIndex, tempNode, key+1 );
		}
		
		graph.setAttribute( "nodesByYPos_"+includeNodeBounds, nodesByYPos );
	}

	public static Integer getKey( float y, float maxHeight )
	{
		Integer key = (int)(y / maxHeight );
		return key;
	}

	private static void addByKey( Map<Integer, List<DNVNode>> nodesByYPos, Map<Integer,Map<Integer,Integer>> nodeAndKeyToIndex, DNVNode tempNode, Integer key )
	{
		List<DNVNode> currentYNodes = nodesByYPos.get( key );
		if( currentYNodes == null )
		{
			currentYNodes = new ArrayList<DNVNode>();
			nodesByYPos.put( key, currentYNodes );
		}
		Map<Integer,Integer> nodeToIndex = nodeAndKeyToIndex.get( key );
		if( nodeToIndex == null )
		{
			nodeToIndex = new HashMap<Integer,Integer>();
			nodeAndKeyToIndex.put( key, nodeToIndex );
		}
		nodeToIndex.put( tempNode.getId(), currentYNodes.size() );
		currentYNodes.add( tempNode );
	}

	/**
	 * Draw curved bounding rectangle.
	 * 
	 * @param g2d
	 *            the g2d
	 * @param node
	 *            the node
	 * @param boundingRectangle
	 *            the bounding rectangle
	 * @param alpha
	 *            the alpha
	 */
	private static void drawCurvedBoundingRectangle( Graphics2D g2d, DNVNode node, Rectangle boundingRectangle, float alpha, boolean highlighted )
	{
		Vector3D color = node.getColor();
		// if( color == null )
		// {
		// color = node.getColor();
		// }
		int left = (int)Math.round( boundingRectangle.left() );
		int right = (int)Math.round( boundingRectangle.right() );
		int top = (int)Math.round( boundingRectangle.top() );
		// int bottom = (int)Math.round( boundingRectangle.bottom );
		int width = right - left;
		int height = (int)Math.round( boundingRectangle.bottom() - boundingRectangle.top() );

		float r = Math.min( 1, color.getX() + 0.2f );
		float g = Math.min( 1, color.getY() + 0.2f );
		float b = Math.min( 1, color.getZ() + 0.2f );
		if( ( node.isSelected() || highlighted || node.isHighlighted() ) && alpha == 1 )
		{
			int shadowOffset = 7;

			// Draw a drop shadow of selected node labels
			g2d.setColor( new Color( 0, 0, 0, 0.4f ) );
			g2d.fillRoundRect( left + shadowOffset - ( height / 2 ), top + shadowOffset, width + height, height, height, height );
		}

		g2d.setColor( new Color( r, g, b, alpha ) );
		g2d.fillRoundRect( left - ( height / 2 ), top, width + height, height, height, height );
		int strokeWidth = 2;
		if( node.isSelected() || ( node.isHighlighted() && node.getHighlightColor() == null ) )
		{
			// Draw red outline of selected nodes
			color = new Vector3D( SELECTED_HIGHLIGHT_COLOR );
			strokeWidth = 3;
		}
		else if( node.isHighlighted() && node.getHighlightColor() != null )
		{
			color = new Vector3D( node.getHighlightColor() );
			strokeWidth = 3;
		}
		r = Math.max( 0, color.getX() - 0.2f );
		g = Math.max( 0, color.getY() - 0.2f );
		b = Math.max( 0, color.getZ() - 0.2f );
		g2d.setColor( new Color( r, g, b, alpha ) );
		g2d.setStroke( new BasicStroke( strokeWidth ) );
		g2d.drawRoundRect( left - ( height / 2 ), top, width + height, height, height, height );
	}

	/**
	 * Overlap.
	 * 
	 * @param r1
	 *            the r1
	 * @param r2
	 *            the r2
	 * @return true, if successful
	 */
	private static boolean overlap( Rectangle r1, Rectangle r2 )
	{
		return !( r2.left() > r1.right() || r2.right() < r1.left() || r2.top() > r1.bottom() || r2.bottom() < r1.top() );
	}

	/**
	 * The Class Rectangle.
	 */
	public static class Rectangle
	{
		// Position of center of the rectangle
		private float positionX = 0;
		private float positionY = 0;
		
		private float width = 0;		
		private float height = 0;
		
		private float top = 0;
		private float bottom = 0;
		private float left = 0;
		private float right = 0;
		
		public void setPosition( float x, float y )
		{
			positionX = x;
			positionY = y;
			updateTopBottom();
			updateLeftRight();			
		}
		
		public void setPosition( Vector2D position )
		{
			setPosition( position.getX(), position.getY() );
		}

		private void updateTopBottom()
		{
			float heightBy2 = height / 2.0f;
			top = positionY - heightBy2;	
			bottom = positionY + heightBy2;
		}
		
		private void updateLeftRight()
		{
			float widthBy2 = width / 2.0f;
			left = positionX - widthBy2;
			right = positionX + widthBy2;
		}
		
		public void setWidth( float width )
		{
			this.width = width;
			updateLeftRight();
		}
		
		public void setHeight( float height )
		{
			this.height = height;
			updateTopBottom();
		}
		
		/** The top. */
		public float top()
		{
			return top;
		}

		/** The bottom. */
		public float bottom()
		{
			return bottom;
		}

		/** The left. */
		public float left()
		{
			return left;
		}

		/** The right. */
		public float right()
		{
			return right;
		}
	}

	/** The label to line metrics. */
	private static Map<String, LineMetrics> labelToLineMetrics = new HashMap<String, LineMetrics>();

	/** The label to font metrics. */
	private static Map<String, FontMetrics> labelToFontMetrics = new HashMap<String, FontMetrics>();

	/**
	 * Gets the rectangle bounding the label.
	 * 
	 * @param tempNode
	 *            the temp node
	 * @param tempPos
	 *            the temp pos
	 * @param g
	 *            the g
	 * @param nodeWidth
	 *            the node width
	 * @param label
	 *            the label
	 * @param curvedLabels
	 *            the curved labels
	 * @param labelSize
	 *            the label size
	 * @param minXPercent
	 *            the min x percent
	 * @param maxXPercent
	 *            the max x percent
	 * @param ratio
	 *            the ratio
	 * @param scaleLabels
	 *            the scale labels
	 * @param maxLabelLength
	 *            the max label length
	 * @param curvedLabelAngle
	 *            the curved label angle
	 * @param boldLabels
	 *            the bold labels
	 * @return the rectangle bounding the label
	 */
	public static Rectangle getRectangleBoundingTheLabel( DNVNode tempNode, Vector2D tempPos, Graphics g, int nodeWidth, String label,
			boolean curvedLabels, double labelSize, double minXPercent, double maxXPercent, double ratio, boolean scaleLabels, int maxLabelLength,
			int curvedLabelAngle, boolean boldLabels, boolean roughEstimate, boolean includeNodeBounds )
	{
		labelSize = getLabelSize( tempNode, labelSize, minXPercent, maxXPercent, ratio, scaleLabels );
		Rectangle r = (Rectangle)tempNode.getAttribute( DNVEntity.LABEL_RECTANGLE+includeNodeBounds+labelSize );
		if( r != null )
		{
			r.setPosition( tempPos );
			return r;
		}
		r = new Rectangle();

		nodeWidth *= tempNode.getRadius();
		int x = (int)tempPos.getX() - nodeWidth / 2;
		int y = (int)tempPos.getY() - nodeWidth / 2;
		Graphics2D g2d = (Graphics2D)g;
		if( label == null || label.equals( "" ) )
		{
//			r.left = r.right = x;
//			r.top = r.bottom = y;
			r.setPosition( x, y );
			int size = 0;
			if( includeNodeBounds )
			{
				size = nodeWidth;
			}
			r.setWidth( size );
			r.setHeight( size ); 
			return r;
		}
		if( curvedLabels )
		{
			if( label.length() > maxLabelLength )
			{
				label = label.substring( 0, maxLabelLength - 3 ) + "...";
			}

			double diameter = nodeWidth + labelSize;
			double angle = FULL_CIRCLE_DEGREES;
			double maxAngle = MAX_ANGLE - curvedLabelAngle;
			while( angle > maxAngle )
			{
				angle = computeAngle( (int)labelSize, label, g2d, diameter, maxLabelLength, boldLabels );
				diameter++;
			}

			r.setWidth( (float)diameter );
			r.setHeight( (float)diameter );
			r.setPosition( tempPos );
//			r.left = tempPos.getX() - (float)diameter / 2.0f;
//			r.right = tempPos.getX() + (float)diameter / 2.0f;
//			r.top = tempPos.getY() - (float)diameter / 2.0f;
//			r.bottom = tempPos.getY() + (float)diameter / 2.0f;
		}
		else
		{
			float fontHeight = 0;
			int width = 0;
			float height = 0;
			if( roughEstimate )
			{
				fontHeight = (float)(labelSize * 0.8f);
				width = label.length() * 7;
//				r.left = tempPos.getX() - ( width / 2.0f );
//				r.right = tempPos.getX() + ( width / 2.0f );
//				r.top = tempPos.getY() - fontHeight;
//				r.bottom = tempPos.getY() + fontHeight;
			}
			else
			{
				FontMetrics fm = getFontMetrics( g, labelSize, boldLabels );
				if( fm != null )
				{
					LineMetrics lm = getLineMetrics( g, labelSize, label, fm );
					int extraHeight = 0;
					if( File.separator.equals( "\\" ) )
					{
						extraHeight = 4;
					}
	
					fontHeight = Math.abs( lm.getStrikethroughOffset() * 2 + lm.getStrikethroughThickness() ) + extraHeight;
					width = fm.stringWidth( label );
//					System.out.println( "=====================" );
//					System.out.println( "boldLabels:" + boldLabels );
//					System.out.println( "labelSize:" + labelSize );
//					System.out.println( "fontHeight:" + fontHeight );
//					System.out.println( "labelLength:" + label.length() );
//					System.out.println( "width:" + width );
//					System.out.println( "=====================" );
//					r.left = tempPos.getX() - ( width / 2.0f );
//					r.right = tempPos.getX() + ( width / 2.0f );
//					r.top = tempPos.getY() - fontHeight;
//					r.bottom = tempPos.getY() + fontHeight;
				}
			}
			height = fontHeight * 2;
			if( includeNodeBounds )
			{
				width = Math.max( nodeWidth, width );
				height = Math.max( nodeWidth, height );
			}
			r.setWidth( width );
			r.setHeight( height );
			r.setPosition( tempPos );
		}

		tempNode.setAttribute( DNVEntity.LABEL_RECTANGLE+includeNodeBounds+labelSize, r );
		
		return r;
	}

	/**
	 * Gets the line metrics.
	 * 
	 * @param g
	 *            the g
	 * @param labelSize
	 *            the label size
	 * @param label
	 *            the label
	 * @param fm
	 *            the fm
	 * @return the line metrics
	 */
	private static LineMetrics getLineMetrics( Graphics g, double labelSize, String label, FontMetrics fm )
	{
		StringBuilder sb = new StringBuilder(label);
		sb.append( "_" );
		sb.append( labelSize );
		String key = sb.toString();
		LineMetrics lm = labelToLineMetrics.get( key );
		if( lm == null )
		{
			lm = fm.getLineMetrics( label, g );
			labelToLineMetrics.put( key, lm );
		}
		return lm;
	}
	
	/**
	 * Gets the font metrics.
	 * 
	 * @param g
	 *            the g
	 * @param labelSize
	 *            the label size
	 * @param boldLabels
	 *            the bold labels
	 * @return the font metrics
	 */
	private static FontMetrics getFontMetrics( Graphics g, double labelSize, boolean boldLabels )
	{
		StringBuilder sb = new StringBuilder();
		sb.append( labelSize );
		sb.append( "_" );
		sb.append( boldLabels );
		String key = sb.toString();
		FontMetrics fm = labelToFontMetrics.get( key );
		if( fm == null )
		{
			if( g != null )
			{
				if( boldLabels )
				{
					g.setFont( BOLD_FONTS[(int)labelSize] );
				}
				else
				{
					g.setFont( FONTS[(int)labelSize] );
				}
				fm = g.getFontMetrics();
			}
			labelToFontMetrics.put( key, fm );
		}
		return fm;
	}

	/** The Constant phi. */
	private static final double phi = Math.toRadians( 20 );

	/** The Constant MAX_ARROWHEAD_LENGTH. */
	private static final double MAX_ARROWHEAD_LENGTH = 18;

	/** The Constant MIN_ARROWHEAD_LENGTH. */
	private static final double MIN_ARROWHEAD_LENGTH = 10;

	/**
	 * Draw edge.
	 * 
	 * @param g
	 *            the g
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @param minXPercent
	 *            the min x percent
	 * @param minYPercent
	 *            the min y percent
	 * @param maxXPercent
	 *            the max x percent
	 * @param maxYPercent
	 *            the max y percent
	 * @param minX
	 *            the min x
	 * @param maxX
	 *            the max x
	 * @param minY
	 *            the min y
	 * @param maxY
	 *            the max y
	 * @param color
	 *            the color
	 * @param g2d
	 *            the g2d
	 * @param tempEdge
	 *            the temp edge
	 * @param drawLabels
	 *            the draw labels
	 * @param outlinedLabels
	 *            the outlined labels
	 * @param fontSize
	 *            the font size
	 * @param overview
	 *            the overview
	 * @param nodeWidth
	 *            the node width
	 * @param edgeSelected
	 *            the edge selected
	 * @param edgeThickness
	 *            the edge thickness
	 * @param boldLabels
	 *            the bold labels
	 */
	private static void drawEdge( Graphics2D g2d, int width, int height, double minXPercent, double minYPercent, double maxXPercent, double maxYPercent, double minX, double maxX,
			double minY, double maxY, Color color, DNVEdge tempEdge, boolean drawLabels, boolean outlinedLabels, int fontSize, boolean overview, int nodeWidth, int edgeThickness,
			boolean boldLabels )
	{
		if( tempEdge.isSelected() || tempEdge.isHighlighted() || edgeThickness != tempEdge.getThickness() )
		{
			float thickness = tempEdge.getThickness();
			if( tempEdge.isSelected() || tempEdge.isHighlighted() && !overview )
			{
				thickness *= 2;
			}
			g2d.setStroke( new BasicStroke( thickness ) );
		}
		else
		{
			g2d.setStroke( new BasicStroke( edgeThickness ) );
		}

		Vector2D tempPos;
		Vector2D tempPos2;
		tempPos = transformPosition( minX, maxX, minY, maxY, minXPercent, maxXPercent, minYPercent, maxYPercent, width, height, tempEdge.getFrom()
				.getPosition( true ) );
//		if( tempEdge.getFrom().hasAttribute( "screenPositionOffset" ) && !overview )
//		{
//			Vector2D screenPositionOffset = (Vector2D)tempEdge.getFrom().getAttribute( "screenPositionOffset" );
//			tempPos.setX( tempPos.getX() + screenPositionOffset.getX() );
//			tempPos.setY( tempPos.getY() + screenPositionOffset.getY() );
//		}
		tempPos2 = transformPosition( minX, maxX, minY, maxY, minXPercent, maxXPercent, minYPercent, maxYPercent, width, height, tempEdge.getTo()
				.getPosition( true ) );
//		if( tempEdge.getTo().hasAttribute( "screenPositionOffset" ) && !overview  )
//		{
//			Vector2D screenPositionOffset = (Vector2D)tempEdge.getTo().getAttribute( "screenPositionOffset" );
//			tempPos2.setX( tempPos2.getX() + screenPositionOffset.getX() );
//			tempPos2.setY( tempPos2.getY() + screenPositionOffset.getY() );
//		}

		Vector3D edgeColor = tempEdge.getColor();
		if( tempEdge.isSelected() || ( tempEdge.isHighlighted() && tempEdge.getHighlightColor() == null ) || edgeColor == null || edgeColor.equals( DNVEntity.NO_COLOR ) )
		{
			g2d.setColor( color );
		}
		else if( tempEdge.isHighlighted() && tempEdge.getHighlightColor() != null )
		{
			g2d.setColor( new Color( tempEdge.getHighlightColor().getX(), tempEdge.getHighlightColor().getY(), tempEdge.getHighlightColor().getZ() ) );
		}
		else
		{
			g2d.setColor( new Color( edgeColor.getX(), edgeColor.getY(), edgeColor.getZ(), tempEdge.getAlpha() ) );
		}

		int pivotX = getPivot( width, (int)tempPos.getX(), (int)tempPos2.getX() );
		int pivotY = getPivot( height, (int)tempPos.getY(), (int)tempPos2.getY() );
		int temp = getPivot( width, pivotX, pivotY );
		if( temp == pivotX )
		{
			if( pivotX == (int)tempPos.getX() )
			{
				pivotY = (int)tempPos2.getY();
			}
			else
			{
				pivotY = (int)tempPos.getY();
			}
		}
		else
		{
			if( pivotY == (int)tempPos.getY() )
			{
				pivotX = (int)tempPos2.getX();
			}
			else
			{
				pivotX = (int)tempPos.getX();
			}				
		}

		if( tempEdge.isDirectional() || tempEdge.getTo().getAlpha() != 1 || tempEdge.getFrom().getAlpha() != 1 )
		{
			double theta = Math.atan2( tempPos2.getY() - tempPos.getY(), tempPos2.getX() - tempPos.getX() );
			double arrowHeadLength = GraphFunctions.getDistance( tempPos, tempPos2 ) * 0.1;
			if( arrowHeadLength > MAX_ARROWHEAD_LENGTH )
				arrowHeadLength = MAX_ARROWHEAD_LENGTH;
			if( !overview && arrowHeadLength < MIN_ARROWHEAD_LENGTH )
				arrowHeadLength = MIN_ARROWHEAD_LENGTH;
			double angle1 = theta + Math.PI - phi;
			double angle2 = theta + Math.PI + phi;

			int x1 = (int)Math.round( tempPos2.getX() - tempEdge.getTo().getRadius() * nodeWidth / 2.0 * Math.cos( theta ) );
			int y1 = (int)Math.round( tempPos2.getY() - tempEdge.getTo().getRadius() * nodeWidth / 2.0 * Math.sin( theta ) );
			int x2 = (int)Math.round( tempPos.getX() + tempEdge.getFrom().getRadius() * nodeWidth / 2.0 * Math.cos( theta ) );
			int y2 = (int)Math.round( tempPos.getY() + tempEdge.getFrom().getRadius() * nodeWidth / 2.0 * Math.sin( theta ) );				
			
			float direction1 = tempPos2.getX() - tempPos.getX();
			float direction2 = x1 - x2;
			if( direction1 == 0 && direction2 == 0 )
			{
				direction1 = tempPos2.getY() - tempPos.getY();
				direction2 = y1 - y2;
			}
			
//			g2d.fillRect( pivotX, pivotY, 50, 50 );

			if( ( direction1 > 0 && direction2 > 0 ) || ( direction1 < 0 && direction2 < 0 ) )
			{
				if( Settings.CURVED_EDGES )
				{
					QuadCurve2D quadCurve = new QuadCurve2D.Float( (int)tempPos2.getX(), (int)tempPos2.getY(), pivotX, pivotY, (int)tempPos.getX(), (int)tempPos.getY());
					g2d.draw( quadCurve );
				}
				else
				{
					g2d.drawLine( x2, y2, x1, y1 );					
				}
			}
			else
			{
				if( Settings.CURVED_EDGES )
				{
					QuadCurve2D quadCurve = new QuadCurve2D.Float( (int)tempPos2.getX(), (int)tempPos2.getY(), pivotX, pivotY, (int)tempPos.getX(), (int)tempPos.getY());
					g2d.draw( quadCurve );
				}
				else
				{
					g2d.drawLine( x2, y2, x1, y1 );					
				}
			}

			if( tempEdge.isDirectional() )
			{
				int x[] = { x1, (int)Math.round( x1 + arrowHeadLength * Math.cos( angle1 ) ),
						(int)Math.round( x1 + arrowHeadLength * Math.cos( angle2 ) ) };
				int y[] = { y1, (int)Math.round( y1 + arrowHeadLength * Math.sin( angle1 ) ),
						(int)Math.round( y1 + arrowHeadLength * Math.sin( angle2 ) ) };
				g2d.fillPolygon( x, y, 3 );
			}
		}
		else
		{
			if( Settings.CURVED_EDGES )
			{
				QuadCurve2D quadCurve = new QuadCurve2D.Float( (int)tempPos.getX(), (int)tempPos.getY(), pivotX, pivotY, (int)tempPos2.getX(), (int)tempPos2.getY() );
				g2d.draw( quadCurve );
			}
			else
			{
				g2d.drawLine( (int)tempPos.getX(), (int)tempPos.getY(), (int)tempPos2.getX(), (int)tempPos2.getY() );				
			}
		}

		if( edgeThickness != tempEdge.getThickness() )
		{
			g2d.setStroke( new BasicStroke( edgeThickness ) );
		}

		if( !overview && ( drawLabels || tempEdge.isForceLabel() ) && tempEdge.getLabel() != null && !tempEdge.getLabel().trim().equals( "" ) )
		{
			drawEdgeLabel( color, g2d, tempEdge, fontSize, tempPos, tempPos2, outlinedLabels, boldLabels );
		}
	}

	private static int getPivot( int width, int x1, int x2 )
	{
		int distanceFromCenter1 = Math.abs( (width/2) - x1 );
		int distanceFromCenter2 = Math.abs( (width/2) - x2 );
		
		if( distanceFromCenter1 > distanceFromCenter2 )
		{
			return x1;
		}
		else
		{
			return x2;
		}
	}

	/**
	 * Draw edge label.
	 * 
	 * @param edgeColor
	 *            the edge color
	 * @param g2d
	 *            the g2d
	 * @param tempEdge
	 *            the temp edge
	 * @param fontSize
	 *            the font size
	 * @param tempPos
	 *            the temp pos
	 * @param tempPos2
	 *            the temp pos2
	 * @param outlinedLabels
	 *            the outlined labels
	 * @param boldLabels
	 *            the bold labels
	 */
	private static void drawEdgeLabel( Color edgeColor, Graphics2D g2d, DNVEdge tempEdge, int fontSize, Vector2D tempPos, Vector2D tempPos2,
			boolean outlinedLabels, boolean boldLabels )
	{
		String label = tempEdge.getLabel();
		Font font;
		if( boldLabels )
		{
			font = BOLD_FONTS[fontSize];
		}
		else
		{
			font = FONTS[fontSize];
		}

		Color labelColor = edgeColor;
		if( tempEdge.getLabelColor() != null )
		{
			labelColor = new Color( tempEdge.getLabelColor().getX(), tempEdge.getLabelColor().getY(), tempEdge.getLabelColor().getZ() );
		}
		Color outlineColor = Color.white;
		if( tempEdge.getLabelOutlineColor() != null )
		{
			outlineColor = new Color( tempEdge.getLabelOutlineColor().getX(), tempEdge.getLabelOutlineColor().getY(), tempEdge.getLabelOutlineColor()
					.getZ() );
		}

		FontMetrics fm = g2d.getFontMetrics( font );
		int labelLength = fm.stringWidth( label );
		int edgeLength = (int)Math.round( GraphFunctions.getDistance( tempPos, tempPos2 ) );
		if( labelLength > edgeLength || File.separator.equals( "/" ) )
		{
			// If edge is shorter than the lable we must use this method of
			// rendering the label
			// even though it's a little slower than the other method.
			// Also use this method on a Mac, because other method produces messed up labels.
			Stroke oldStroke = g2d.getStroke();
			Stroke stroke = new TextStroke( label, font, false, false );
			g2d.setStroke( stroke );
			int x1 = (int)tempPos.getX();
			int y1 = (int)tempPos.getY();
			int x2 = (int)tempPos2.getX();
			int y2 = (int)tempPos2.getY();

			int temp;
			if( x1 > x2 )
			{
				temp = x1;
				x1 = x2;
				x2 = temp;

				temp = y1;
				y1 = y2;
				y2 = temp;
			}

			if( edgeLength > labelLength )
			{
				int differenceBy2 = ( edgeLength - labelLength ) / 2;
				double theta = Math.atan2( y1 - y2, x1 - x2 );
				x1 = (int)Math.round( x1 - differenceBy2 * Math.cos( theta ) );
				y1 = (int)Math.round( y1 - differenceBy2 * Math.sin( theta ) );
				x2 = (int)Math.round( x2 - differenceBy2 * Math.cos( theta ) );
				y2 = (int)Math.round( y2 - differenceBy2 * Math.sin( theta ) );
			}

			if( outlinedLabels )
			{
				g2d.setColor( outlineColor );
				g2d.drawLine( x1 - 1, y1 - 1, x2 - 1, y2 - 1 );
				g2d.drawLine( x1 - 1, y1 + 1, x2 - 1, y2 + 1 );
				g2d.drawLine( x1 + 1, y1 + 1, x2 + 1, y2 + 1 );
				g2d.drawLine( x1 + 1, y1 - 1, x2 + 1, y2 - 1 );
			}

			g2d.setColor( labelColor );
			g2d.drawLine( x1, y1, x2, y2 );
			g2d.setStroke( oldStroke );
		}
		else
		{
			// Use a faster method if the label is shorter than the edge
			float xPos = ( tempPos.getX() + tempPos2.getX() ) / 2.0f;
			float yPos = ( tempPos.getY() + tempPos2.getY() ) / 2.0f;
			g2d.setFont( font );
			double angle = Math.atan2( tempPos.getY() - tempPos2.getY(), tempPos.getX() - tempPos2.getX() );
			if( angle > Math.PI / 2.0 )
			{
				angle -= Math.PI;
			}
			else if( angle < -Math.PI / 2.0 )
			{
				angle += Math.PI;
			}

			if( outlinedLabels )
			{
				g2d.setColor( outlineColor );
				TextUtilities.drawRotatedString( tempEdge.getLabel(), g2d, xPos - 1, yPos - 1, TextAnchor.CENTER, angle, xPos - 1, yPos - 1 );
				TextUtilities.drawRotatedString( tempEdge.getLabel(), g2d, xPos - 1, yPos + 1, TextAnchor.CENTER, angle, xPos - 1, yPos + 1 );
				TextUtilities.drawRotatedString( tempEdge.getLabel(), g2d, xPos + 1, yPos + 1, TextAnchor.CENTER, angle, xPos + 1, yPos + 1 );
				TextUtilities.drawRotatedString( tempEdge.getLabel(), g2d, xPos + 1, yPos - 1, TextAnchor.CENTER, angle, xPos + 1, yPos - 1 );
			}
			g2d.setColor( labelColor );
			TextUtilities.drawRotatedString( tempEdge.getLabel(), g2d, xPos, yPos, TextAnchor.CENTER, angle, xPos, yPos );
		}
	}

	/** The Constant basicStroke. */
	private static final Stroke basicStroke = new BasicStroke( 1 );

	/** The Constant basicStroke2. */
	private static final Stroke basicStroke2 = new BasicStroke( 2 );

	/** The Constant CIRCLE. */
	public static final int CIRCLE = 0;

	/** The Constant RECTANGLE. */
	public static final int RECTANGLE = 1;

	/**
	 * Draw node.
	 * 
	 * @param g
	 *            the g
	 * @param showIcons
	 *            the show icons
	 * @param tempNode
	 *            the temp node
	 * @param iconName
	 *            the icon name
	 * @param tempPos
	 *            the temp pos
	 * @param color
	 *            the color
	 * @param nodeWidth
	 *            the node width
	 * @param type
	 *            the type
	 * @param maxDistanceToHighlight
	 *            the max distance to highlight
	 * @throws MalformedURLException
	 *             the malformed url exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void drawNode( Graphics2D g2d, boolean showIcons, DNVNode tempNode, String iconName, Vector2D tempPos, Vector3D color, int nodeWidth,
			int type, int maxDistanceToHighlight ) throws MalformedURLException, IOException
	{
		Image icon;

		g2d.setColor( new Color( color.getX(), color.getY(), color.getZ(), tempNode.getAlpha() ) );

		nodeWidth *= tempNode.getRadius();
		g2d.setStroke( basicStroke );
		Color outlineColor = Color.darkGray;
		if( tempNode.isSelected() || ( tempNode.isHighlighted() && tempNode.getHighlightColor() == null ) )
		{
			outlineColor = Color.red;
			g2d.setStroke( basicStroke2 );
		}
		else if( tempNode.isHighlighted() && tempNode.getHighlightColor() != null )
		{
			outlineColor = new Color( tempNode.getHighlightColor().getX(), tempNode.getHighlightColor().getY(), tempNode.getHighlightColor().getZ() );
			g2d.setStroke( basicStroke2 );
		}
		else if( tempNode.getOutlineColor() != null )
		{
			outlineColor = new Color( tempNode.getOutlineColor().getX(), tempNode.getOutlineColor().getY(), tempNode.getOutlineColor().getZ(),
					tempNode.getAlpha() );
		}
		// if( tempNode.getDistanceFromSelectedNode() <= maxDistanceToHighlight
		// )
		// {
		// // float width = 3.0f - 1.0f *
		// (tempNode.getDistanceFromSelectedNode() /
		// (float)(maxDistanceToHighlight+1));
		// // g2d.setStroke( new BasicStroke( width ) );
		// g2d.setStroke( basicStroke2 );
		// }
		int x = (int)tempPos.getX() - nodeWidth / 2;
		int y = (int)tempPos.getY() - nodeWidth / 2;
//		if( tempNode.hasAttribute( "screenPositionOffset" ) && !overview )
//		{
//			Vector2D screenPositionOffset = (Vector2D)tempNode.getAttribute( "screenPositionOffset" );
//			x += screenPositionOffset.getX();
//			y += screenPositionOffset.getY();
//		}
		if( nodeWidth < 5 && type == RECTANGLE )
		{
			g2d.fillRect( x, y, nodeWidth, nodeWidth );
			if( nodeWidth > 3 || tempNode.isSelected() || tempNode.isHighlighted() )
			{
				g2d.setColor( outlineColor );
				g2d.drawRect( x, y, nodeWidth, nodeWidth );
			}
		}
		else
		{
			g2d.fillOval( x, y, nodeWidth, nodeWidth );
			if( nodeWidth > 3 || tempNode.isSelected() || tempNode.isHighlighted() )
			{
				g2d.setColor( outlineColor );
				g2d.drawOval( x, y, nodeWidth, nodeWidth );
			}
		}
		if( showIcons && iconName != null && !iconName.equals( "" ) )
		{
			icon = tempNode.getIconImage( nodeWidth );
			int width;
			int height;
			if( icon == null )
			{
				icon = getIcon( tempNode.getIcon() );
				if( icon == null )
				{
					if( !Settings.DISABLE_IMAGES && !iconMap.containsKey( tempNode.getIcon() ) )
					{
						iconMap.put( tempNode.getIcon(), null );
						ImageCacher ic = new ImageCacher( tempNode.getIcon() );
						ic.start();
					}
					icon = new BufferedImage( 1, 1, BufferedImage.TYPE_INT_ARGB );
				}
				double originalHeight = getIconHeight( tempNode.getIcon() );// icon.getHeight(
				// null
				// );
				double originalWidth = getIconWidth( tempNode.getIcon() );// icon.getWidth(
				// null
				// );
				double originalDiagonal = Math.sqrt( ( originalHeight * originalHeight ) + ( originalWidth * originalWidth ) );
				double ratio = originalDiagonal / nodeWidth;

				width = (int)Math.max( 1, Math.round( originalWidth / ratio ) );
				height = (int)Math.max( 1, Math.round( originalHeight / ratio ) );
				icon = icon.getScaledInstance( width, height, Image.SCALE_FAST );
				if( originalHeight > 1 && originalWidth > 1 )
				{
					tempNode.setIconImage( nodeWidth, icon );
				}
			}
			else
			{
				width = icon.getWidth( null );
				height = icon.getHeight( null );
			}

			if( width >= 5 && height >= 5 )
			{
				int xOffset = (int)Math.round( ( nodeWidth - width ) / 2.0 );
				int yOffset = (int)Math.round( ( nodeWidth - height ) / 2.0 );
				g2d.drawImage( icon, x + xOffset, y + yOffset, width, height, null );
			}
		}

	}

	/**
	 * Draw label.
	 * 
	 * @param g
	 *            the g
	 * @param tempNode
	 *            the temp node
	 * @param tempPos
	 *            the temp pos
	 * @param nodeWidth
	 *            the node width
	 * @param label
	 *            the label
	 * @param showLabels
	 *            the show labels
	 * @param curvedLabels
	 *            the curved labels
	 * @param outlinedLabels
	 *            the outlined labels
	 * @param labelSize
	 *            the label size
	 * @param minXPercent
	 *            the min x percent
	 * @param maxXPercent
	 *            the max x percent
	 * @param ratio
	 *            the ratio
	 * @param scaleLabels
	 *            the scale labels
	 * @param highlightNeighbors
	 *            the highlight neighbors
	 * @param maxLabelLength
	 *            the max label length
	 * @param curvedLabelAngle
	 *            the curved label angle
	 * @param drawLabelBox
	 *            the draw label box
	 * @param boldLabels
	 *            the bold labels
	 * @throws MalformedURLException
	 *             the malformed url exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void drawLabel( Graphics2D g2d, DNVNode tempNode, Vector2D tempPos, int nodeWidth, String label, boolean showLabels,
			boolean curvedLabels, boolean outlinedLabels, double labelSize, double minXPercent, double maxXPercent, double ratio,
			boolean scaleLabels, boolean highlightNeighbors, int maxLabelLength, int curvedLabelAngle, boolean drawLabelBox, boolean boldLabels, boolean highlighted )
			throws MalformedURLException, IOException
	{
		labelSize = getLabelSize( tempNode, labelSize, minXPercent, maxXPercent, ratio, scaleLabels );

		nodeWidth *= tempNode.getRadius();
		int x = (int)tempPos.getX() - nodeWidth / 2;
		int y = (int)tempPos.getY() - nodeWidth / 2;
//		if( tempNode.hasAttribute( "screenPositionOffset" ) )
//		{
//			Vector2D screenPositionOffset = (Vector2D)tempNode.getAttribute( "screenPositionOffset" );
//			x += screenPositionOffset.getX();
//			y += screenPositionOffset.getY();
//			tempPos = new Vector2D( tempPos.getX() + screenPositionOffset.getX(), tempPos.getY() + screenPositionOffset.getY() );
//		}
		Color color;
		Color outlineColor;
		if( label != null && !label.trim().equals( "" ) )
		{
			if( showLabels )
			{
				float alpha = 1;
				if( tempNode.hasProperty( "faded" ) /* && !tempNode.isSelected() */)
				{
					alpha = Float.parseFloat( tempNode.getProperty( "faded" ) );
					if( alpha < 0.01f )
					{
						return;
					}
				}
				if( tempNode.isSelected() || highlighted || ( tempNode.isHighlighted() && tempNode.getHighlightColor() == null ) )
				{
					color = new Color( SELECTED_HIGHLIGHT_COLOR.getX(), SELECTED_HIGHLIGHT_COLOR.getY(), SELECTED_HIGHLIGHT_COLOR.getZ(), alpha );
					outlineColor = new Color( 1, 1, 1, alpha );
				}
				else if( tempNode.isHighlighted() && tempNode.getHighlightColor() != null )
				{
					color = new Color( tempNode.getHighlightColor().getX(), tempNode.getHighlightColor().getY(), tempNode.getHighlightColor().getZ(), alpha );
					outlineColor = new Color( 1, 1, 1, alpha );
				}
				else
				{
					if( tempNode.getLabelColor() == null
							|| ( drawLabelBox && !tempNode.isHideLabelBackground() && tempNode.getLabelColor().equals( tempNode.getColor() ) ) )
					{
						color = new Color( 0, 0, 0, alpha );
					}
					else
					{
						color = new Color( tempNode.getLabelColor().getX(), tempNode.getLabelColor().getY(), tempNode.getLabelColor().getZ(), alpha );
					}
					outlineColor = new Color( 1, 1, 1, alpha );

					if( tempNode.getLabelOutlineColor() != null )
					{
						outlineColor = new Color( tempNode.getLabelOutlineColor().getX(), tempNode.getLabelOutlineColor().getY(), tempNode
								.getLabelOutlineColor().getZ(), alpha );
					}
				}

				g2d.setColor( color );

				if( curvedLabels || tempNode.isCurvedLabel() )
				{
					if( label.length() > maxLabelLength )
					{
						label = label.substring( 0, maxLabelLength - 3 ) + "...";
					}
					// labelSize *= nodeWidth / 4.0;
					double diameter = nodeWidth + labelSize;
					double angle = FULL_CIRCLE_DEGREES;
					double maxAngle = MAX_ANGLE - curvedLabelAngle;
					while( angle > maxAngle )
					{
						angle = computeAngle( (int)labelSize, label, g2d, diameter, maxLabelLength, boldLabels );
						diameter++;
					}

					double startAngle = computeStartAngle( angle, curvedLabelAngle );
					Font font;
					if( boldLabels )
					{
						font = BOLD_FONTS[(int)labelSize];
					}
					else
					{
						font = FONTS[(int)labelSize];
					}
					double offset = ( diameter - nodeWidth ) / 2;
					if( outlinedLabels )
					{
						drawOutlinedCurvedString( g2d, label, font, outlineColor, color, x - offset, y - offset, diameter, startAngle, angle );
					}
					else
					{
						drawCurvedString( g2d, label, color, font, x - offset, y - offset, diameter, startAngle, angle );
					}
				}
				else
				{
					if( drawLabelBox && !tempNode.isHideLabelBackground() )
					{
						Rectangle boundingRectangle = getRectangleBoundingTheLabel( tempNode, tempPos, g2d, nodeWidth, label, curvedLabels, labelSize,
								minXPercent, maxXPercent, ratio, scaleLabels, maxLabelLength, curvedLabelAngle, boldLabels, false, false );
						drawCurvedBoundingRectangle( g2d, tempNode, boundingRectangle, Math.min( tempNode.getAlpha(), alpha ), highlighted );
						g2d.setColor( color );
					}

					if( boldLabels )
					{
						g2d.setFont( BOLD_FONTS[(int)labelSize] );
					}
					else
					{
						g2d.setFont( FONTS[(int)labelSize] );
					}
					FontMetrics fm = getFontMetrics( g2d, labelSize, boldLabels );
					LineMetrics lm = getLineMetrics( g2d, labelSize, label, fm );
					float fontHeight = lm.getStrikethroughOffset() + lm.getStrikethroughThickness() / 2.0f;
					int width = fm.stringWidth( label );
					int offset = 0;
					if( File.separator.equals( "\\" ) )
					{
						offset = 1;
					}
					if( outlinedLabels )
					{
						drawOutlinedString( g2d, label, outlineColor, color, (int)Math.round( tempPos.getX() - width / 2.0 ), (int)Math.round( tempPos
								.getY()
								- ( fontHeight ) + offset ) );
					}
					else
					{
						g2d.drawString( label, (int)Math.round( tempPos.getX() - width / 2.0 ), (int)Math.round( tempPos.getY() - ( fontHeight )
								+ offset ) );
					}
				}
			}
		}

		// tempNode.removeProperty( "faded" );
	}

	/**
	 * Gets the label size.
	 * 
	 * @param tempNode
	 *            the temp node
	 * @param labelSize
	 *            the label size
	 * @param minXPercent
	 *            the min x percent
	 * @param maxXPercent
	 *            the max x percent
	 * @param ratio
	 *            the ratio
	 * @param scaleLabels
	 *            the scale labels
	 * @return the label size
	 */
	public static double getLabelSize( DNVNode tempNode, double labelSize, double minXPercent, double maxXPercent, double ratio, boolean scaleLabels )
	{
		if( tempNode.getLabelSize() != null )
		{
			labelSize = tempNode.getLabelSize();
		}
		if( scaleLabels )
		{
			labelSize = getNodeWidth( labelSize, minXPercent, maxXPercent, ratio );
		}
		if( labelSize > MAX_FONT_SIZE )
		{
			labelSize = MAX_FONT_SIZE;
		}
		else if( labelSize < MIN_FONT_SIZE )
		{
			labelSize = MIN_FONT_SIZE;
		}
		return labelSize;
	}

	/**
	 * Draw curved string.
	 * 
	 * @param g2d
	 *            the g2d
	 * @param string
	 *            the string
	 * @param textColor
	 *            the text color
	 * @param font
	 *            the font
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @param diameter
	 *            the diameter
	 * @param startAngle
	 *            the start angle
	 * @param angle
	 *            the angle
	 */
	private static void drawCurvedString( Graphics2D g2d, String string, Color textColor, Font font, double x, double y, double diameter,
			double startAngle, double angle )
	{
		Stroke stroke = new TextStroke( string, font, false, false );
		g2d.setStroke( stroke );
		g2d.setColor( textColor );
		Shape arc;
		arc = new Arc2D.Double( new Rectangle2D.Double( x, y, diameter, diameter ), startAngle, -angle, Arc2D.OPEN );
		g2d.draw( arc );
	}

	/**
	 * Draw outlined curved string.
	 * 
	 * @param g2d
	 *            the g2d
	 * @param string
	 *            the string
	 * @param font
	 *            the font
	 * @param outlineColor
	 *            the outline color
	 * @param textColor
	 *            the text color
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @param diameter
	 *            the diameter
	 * @param startAngle
	 *            the start angle
	 * @param angle
	 *            the angle
	 */
	private static void drawOutlinedCurvedString( Graphics2D g2d, String string, Font font, Color outlineColor, Color textColor, double x, double y,
			double diameter, double startAngle, double angle )
	{
		Stroke stroke = new TextStroke( string, font, false, false );
		g2d.setStroke( stroke );
		g2d.setColor( outlineColor );
		Shape arc;
		arc = new Arc2D.Double( new Rectangle2D.Double( x - 1, y - 1, diameter, diameter ), startAngle, -angle, Arc2D.OPEN );
		g2d.draw( arc );
		arc = new Arc2D.Double( new Rectangle2D.Double( x - 1, y + 1, diameter, diameter ), startAngle, -angle, Arc2D.OPEN );
		g2d.draw( arc );
		arc = new Arc2D.Double( new Rectangle2D.Double( x + 1, y + 1, diameter, diameter ), startAngle, -angle, Arc2D.OPEN );
		g2d.draw( arc );
		arc = new Arc2D.Double( new Rectangle2D.Double( x + 1, y - 1, diameter, diameter ), startAngle, -angle, Arc2D.OPEN );
		g2d.draw( arc );
		g2d.setColor( textColor );
		arc = new Arc2D.Double( new Rectangle2D.Double( x, y, diameter, diameter ), startAngle, -angle, Arc2D.OPEN );
		g2d.draw( arc );
	}

	/**
	 * Draw outlined string.
	 * 
	 * @param g
	 *            the g
	 * @param string
	 *            the string
	 * @param outlineColor
	 *            the outline color
	 * @param textColor
	 *            the text color
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 */
	private static void drawOutlinedString( Graphics g, String string, Color outlineColor, Color textColor, int x, int y )
	{
		g.setColor( outlineColor );
		g.drawString( string, x - 1, y - 1 );
		g.drawString( string, x - 1, y + 1 );
		g.drawString( string, x + 1, y + 1 );
		g.drawString( string, x + 1, y - 1 );
		g.setColor( textColor );
		g.drawString( string, x, y );
	}

	// public static final int MAX_LABEL_LENGTH = 20;
	/** The Constant MAX_ANGLE. */
	public static final double MAX_ANGLE = 360;

	/** The Constant MAX_START_ANGLE. */
	public static final double MAX_START_ANGLE = 270;

	/** The Constant FULL_CIRCLE_DEGREES. */
	public static final double FULL_CIRCLE_DEGREES = 360;

	/**
	 * Compute start angle.
	 * 
	 * @param angleSize
	 *            the angle size
	 * @param curvedLabelAngle
	 *            the curved label angle
	 * @return the double
	 */
	public static double computeStartAngle( double angleSize, int curvedLabelAngle )
	{
		return ( MAX_START_ANGLE - curvedLabelAngle / 2.0 ) - ( ( ( MAX_ANGLE - curvedLabelAngle ) - angleSize ) / 2.0 );
	}

	/**
	 * Compute angle.
	 * 
	 * @param labelSize
	 *            the label size
	 * @param label
	 *            the label
	 * @param g2d
	 *            the g2d
	 * @param diameter
	 *            the diameter
	 * @param maxLabelLength
	 *            the max label length
	 * @param boldLabels
	 *            the bold labels
	 * @return the double
	 */
	public static double computeAngle( int labelSize, String label, Graphics2D g2d, double diameter, int maxLabelLength, boolean boldLabels )
	{
		if( label.length() > maxLabelLength )
		{
			label = label.substring( 0, maxLabelLength - 3 ) + "...";
		}
		FontMetrics fm = getFontMetrics( g2d, labelSize, boldLabels );
		int labelLength = fm.stringWidth( label );

		return labelLength * 360.0 / Math.PI / diameter;
	}

	/** The Constant FONTS. */
	public static final Font[] FONTS = { new Font( "Arial", Font.PLAIN, 0 ), new Font( "Arial", Font.PLAIN, 1 ), new Font( "Arial", Font.PLAIN, 2 ),
			new Font( "Arial", Font.PLAIN, 3 ), new Font( "Arial", Font.PLAIN, 4 ), new Font( "Arial", Font.PLAIN, 5 ),
			new Font( "Arial", Font.PLAIN, 6 ), new Font( "Arial", Font.PLAIN, 7 ), new Font( "Arial", Font.PLAIN, 8 ),
			new Font( "Arial", Font.PLAIN, 9 ), new Font( "Arial", Font.PLAIN, 10 ), new Font( "Arial", Font.PLAIN, 11 ),
			new Font( "Arial", Font.PLAIN, 12 ), new Font( "Arial", Font.PLAIN, 13 ), new Font( "Arial", Font.PLAIN, 14 ),
			new Font( "Arial", Font.PLAIN, 15 ), new Font( "Arial", Font.PLAIN, 16 ), new Font( "Arial", Font.PLAIN, 17 ),
			new Font( "Arial", Font.PLAIN, 18 ), new Font( "Arial", Font.PLAIN, 19 ), new Font( "Arial", Font.PLAIN, 20 ),
			new Font( "Arial", Font.PLAIN, 21 ), new Font( "Arial", Font.PLAIN, 22 ), new Font( "Arial", Font.PLAIN, 23 ),
			new Font( "Arial", Font.PLAIN, 24 ), new Font( "Arial", Font.PLAIN, 25 ), new Font( "Arial", Font.PLAIN, 26 ),
			new Font( "Arial", Font.PLAIN, 27 ), new Font( "Arial", Font.PLAIN, 28 ), new Font( "Arial", Font.PLAIN, 29 ),
			new Font( "Arial", Font.PLAIN, 30 ), new Font( "Arial", Font.PLAIN, 31 ), new Font( "Arial", Font.PLAIN, 32 ),
			new Font( "Arial", Font.PLAIN, 33 ), new Font( "Arial", Font.PLAIN, 34 ), new Font( "Arial", Font.PLAIN, 35 ),
			new Font( "Arial", Font.PLAIN, 36 ), new Font( "Arial", Font.PLAIN, 37 ), new Font( "Arial", Font.PLAIN, 38 ),
			new Font( "Arial", Font.PLAIN, 39 ), new Font( "Arial", Font.PLAIN, 40 ), new Font( "Arial", Font.PLAIN, 41 ),
			new Font( "Arial", Font.PLAIN, 42 ), new Font( "Arial", Font.PLAIN, 43 ), new Font( "Arial", Font.PLAIN, 44 ),
			new Font( "Arial", Font.PLAIN, 45 ), new Font( "Arial", Font.PLAIN, 46 ), new Font( "Arial", Font.PLAIN, 47 ),
			new Font( "Arial", Font.PLAIN, 48 ), new Font( "Arial", Font.PLAIN, 49 ), new Font( "Arial", Font.PLAIN, 50 ),
			new Font( "Arial", Font.PLAIN, 51 ), new Font( "Arial", Font.PLAIN, 52 ), new Font( "Arial", Font.PLAIN, 53 ),
			new Font( "Arial", Font.PLAIN, 54 ), new Font( "Arial", Font.PLAIN, 55 ), new Font( "Arial", Font.PLAIN, 56 ),
			new Font( "Arial", Font.PLAIN, 57 ), new Font( "Arial", Font.PLAIN, 58 ), new Font( "Arial", Font.PLAIN, 59 ),
			new Font( "Arial", Font.PLAIN, 60 ), new Font( "Arial", Font.PLAIN, 61 ), new Font( "Arial", Font.PLAIN, 62 ),
			new Font( "Arial", Font.PLAIN, 63 ), new Font( "Arial", Font.PLAIN, 64 ), new Font( "Arial", Font.PLAIN, 65 ),
			new Font( "Arial", Font.PLAIN, 66 ), new Font( "Arial", Font.PLAIN, 67 ), new Font( "Arial", Font.PLAIN, 68 ),
			new Font( "Arial", Font.PLAIN, 69 ), new Font( "Arial", Font.PLAIN, 70 ), new Font( "Arial", Font.PLAIN, 71 ),
			new Font( "Arial", Font.PLAIN, 72 ), new Font( "Arial", Font.PLAIN, 73 ), new Font( "Arial", Font.PLAIN, 74 ),
			new Font( "Arial", Font.PLAIN, 75 ), new Font( "Arial", Font.PLAIN, 76 ), new Font( "Arial", Font.PLAIN, 77 ),
			new Font( "Arial", Font.PLAIN, 78 ), new Font( "Arial", Font.PLAIN, 79 ), new Font( "Arial", Font.PLAIN, 80 ),
			new Font( "Arial", Font.PLAIN, 81 ), new Font( "Arial", Font.PLAIN, 82 ), new Font( "Arial", Font.PLAIN, 83 ),
			new Font( "Arial", Font.PLAIN, 84 ), new Font( "Arial", Font.PLAIN, 85 ), new Font( "Arial", Font.PLAIN, 86 ),
			new Font( "Arial", Font.PLAIN, 87 ), new Font( "Arial", Font.PLAIN, 88 ), new Font( "Arial", Font.PLAIN, 89 ),
			new Font( "Arial", Font.PLAIN, 90 ), new Font( "Arial", Font.PLAIN, 91 ), new Font( "Arial", Font.PLAIN, 92 ),
			new Font( "Arial", Font.PLAIN, 93 ), new Font( "Arial", Font.PLAIN, 94 ), new Font( "Arial", Font.PLAIN, 95 ),
			new Font( "Arial", Font.PLAIN, 96 ), new Font( "Arial", Font.PLAIN, 97 ), new Font( "Arial", Font.PLAIN, 98 ),
			new Font( "Arial", Font.PLAIN, 99 ), new Font( "Arial", Font.PLAIN, 100 ) };

	/** The Constant BOLD_FONTS. */
	public static final Font[] BOLD_FONTS = { new Font( "Arial", Font.BOLD, 0 ), new Font( "Arial", Font.BOLD, 1 ),
			new Font( "Arial", Font.BOLD, 2 ), new Font( "Arial", Font.BOLD, 3 ), new Font( "Arial", Font.BOLD, 4 ),
			new Font( "Arial", Font.BOLD, 5 ), new Font( "Arial", Font.BOLD, 6 ), new Font( "Arial", Font.BOLD, 7 ),
			new Font( "Arial", Font.BOLD, 8 ), new Font( "Arial", Font.BOLD, 9 ), new Font( "Arial", Font.BOLD, 10 ),
			new Font( "Arial", Font.BOLD, 11 ), new Font( "Arial", Font.BOLD, 12 ), new Font( "Arial", Font.BOLD, 13 ),
			new Font( "Arial", Font.BOLD, 14 ), new Font( "Arial", Font.BOLD, 15 ), new Font( "Arial", Font.BOLD, 16 ),
			new Font( "Arial", Font.BOLD, 17 ), new Font( "Arial", Font.BOLD, 18 ), new Font( "Arial", Font.BOLD, 19 ),
			new Font( "Arial", Font.BOLD, 20 ), new Font( "Arial", Font.BOLD, 21 ), new Font( "Arial", Font.BOLD, 22 ),
			new Font( "Arial", Font.BOLD, 23 ), new Font( "Arial", Font.BOLD, 24 ), new Font( "Arial", Font.BOLD, 25 ),
			new Font( "Arial", Font.BOLD, 26 ), new Font( "Arial", Font.BOLD, 27 ), new Font( "Arial", Font.BOLD, 28 ),
			new Font( "Arial", Font.BOLD, 29 ), new Font( "Arial", Font.BOLD, 30 ), new Font( "Arial", Font.BOLD, 31 ),
			new Font( "Arial", Font.BOLD, 32 ), new Font( "Arial", Font.BOLD, 33 ), new Font( "Arial", Font.BOLD, 34 ),
			new Font( "Arial", Font.BOLD, 35 ), new Font( "Arial", Font.BOLD, 36 ), new Font( "Arial", Font.BOLD, 37 ),
			new Font( "Arial", Font.BOLD, 38 ), new Font( "Arial", Font.BOLD, 39 ), new Font( "Arial", Font.BOLD, 40 ),
			new Font( "Arial", Font.BOLD, 41 ), new Font( "Arial", Font.BOLD, 42 ), new Font( "Arial", Font.BOLD, 43 ),
			new Font( "Arial", Font.BOLD, 44 ), new Font( "Arial", Font.BOLD, 45 ), new Font( "Arial", Font.BOLD, 46 ),
			new Font( "Arial", Font.BOLD, 47 ), new Font( "Arial", Font.BOLD, 48 ), new Font( "Arial", Font.BOLD, 49 ),
			new Font( "Arial", Font.BOLD, 50 ), new Font( "Arial", Font.BOLD, 51 ), new Font( "Arial", Font.BOLD, 52 ),
			new Font( "Arial", Font.BOLD, 53 ), new Font( "Arial", Font.BOLD, 54 ), new Font( "Arial", Font.BOLD, 55 ),
			new Font( "Arial", Font.BOLD, 56 ), new Font( "Arial", Font.BOLD, 57 ), new Font( "Arial", Font.BOLD, 58 ),
			new Font( "Arial", Font.BOLD, 59 ), new Font( "Arial", Font.BOLD, 60 ), new Font( "Arial", Font.BOLD, 61 ),
			new Font( "Arial", Font.BOLD, 62 ), new Font( "Arial", Font.BOLD, 63 ), new Font( "Arial", Font.BOLD, 64 ),
			new Font( "Arial", Font.BOLD, 65 ), new Font( "Arial", Font.BOLD, 66 ), new Font( "Arial", Font.BOLD, 67 ),
			new Font( "Arial", Font.BOLD, 68 ), new Font( "Arial", Font.BOLD, 69 ), new Font( "Arial", Font.BOLD, 70 ),
			new Font( "Arial", Font.BOLD, 71 ), new Font( "Arial", Font.BOLD, 72 ), new Font( "Arial", Font.BOLD, 73 ),
			new Font( "Arial", Font.BOLD, 74 ), new Font( "Arial", Font.BOLD, 75 ), new Font( "Arial", Font.BOLD, 76 ),
			new Font( "Arial", Font.BOLD, 77 ), new Font( "Arial", Font.BOLD, 78 ), new Font( "Arial", Font.BOLD, 79 ),
			new Font( "Arial", Font.BOLD, 80 ), new Font( "Arial", Font.BOLD, 81 ), new Font( "Arial", Font.BOLD, 82 ),
			new Font( "Arial", Font.BOLD, 83 ), new Font( "Arial", Font.BOLD, 84 ), new Font( "Arial", Font.BOLD, 85 ),
			new Font( "Arial", Font.BOLD, 86 ), new Font( "Arial", Font.BOLD, 87 ), new Font( "Arial", Font.BOLD, 88 ),
			new Font( "Arial", Font.BOLD, 89 ), new Font( "Arial", Font.BOLD, 90 ), new Font( "Arial", Font.BOLD, 91 ),
			new Font( "Arial", Font.BOLD, 92 ), new Font( "Arial", Font.BOLD, 93 ), new Font( "Arial", Font.BOLD, 94 ),
			new Font( "Arial", Font.BOLD, 95 ), new Font( "Arial", Font.BOLD, 96 ), new Font( "Arial", Font.BOLD, 97 ),
			new Font( "Arial", Font.BOLD, 98 ), new Font( "Arial", Font.BOLD, 99 ), new Font( "Arial", Font.BOLD, 100 ) };

	/** The Constant MIN_FONT_SIZE. */
	public static final int MIN_FONT_SIZE = 4;

	/** The Constant MAX_FONT_SIZE. */
	public static final int MAX_FONT_SIZE = 100;

	/**
	 * Gets the font size.
	 * 
	 * @param nodeWidth
	 *            the node width
	 * @param g2d
	 *            the g2d
	 * @param text
	 *            the text
	 * @param labelSize
	 *            the label size
	 * @param boldLabels
	 *            the bold labels
	 * @return the font size
	 */
	public static int getFontSize( int nodeWidth, Graphics2D g2d, String text, double labelSize, boolean boldLabels )
	{
		int fontSize = MAX_FONT_SIZE;
		Font font;
		if( boldLabels )
		{
			font = BOLD_FONTS[fontSize];
		}
		else
		{
			font = FONTS[fontSize];
		}
		FontMetrics fm = g2d.getFontMetrics( font );
		while( fm.stringWidth( text ) > nodeWidth * 3.5 * labelSize && fontSize > MIN_FONT_SIZE )
		{
			fontSize--;
			if( boldLabels )
			{
				font = BOLD_FONTS[fontSize];
			}
			else
			{
				font = FONTS[fontSize];
			}
			fm = g2d.getFontMetrics( font );
		}

		return fontSize;
	}

	/**
	 * Gets the node width.
	 * 
	 * @param nodeSize
	 *            the node size
	 * @param minX
	 *            the min x
	 * @param maxX
	 *            the max x
	 * @param ratio
	 *            the ratio
	 * @return the node width
	 */
	public static int getNodeWidth( double nodeSize, double minX, double maxX, double ratio )
	{
		return (int)Math.round( nodeSize / ( maxX - minX ) * ratio );
	}

	/**
	 * Gets the uRL.
	 * 
	 * @param radius
	 *            the radius
	 * @param iconName
	 *            the icon name
	 * @param color
	 *            the color
	 * @param showIcons
	 *            the show icons
	 * @param isSelected
	 *            the is selected
	 * @param id
	 *            the id
	 * @return the uRL
	 */
	public static String getURL( int radius, String iconName, Vector3D color, boolean showIcons, boolean isSelected, int id )
	{
		String url;
		String webPath = "/WiGi/wigi/";
		PaintBean pb = PaintBean.getCurrentInstance();
		if( pb != null )
		{
			webPath = pb.getWebPath();
		}
		if( !showIcons )
			url = webPath + "NodeIconServlet?s=" + radius + "&r=" + color.getX() + "&g=" + color.getY() + "&b=" + color.getZ() + "&id=" + id;
		else
			url = webPath + "NodeIconServlet?s=" + radius + "&r=" + color.getX() + "&g=" + color.getY() + "&b=" + color.getZ() + "&id=" + id
					+ "&i=" + iconName;

		if( isSelected )
		{
			url += "&sel=true";
		}

		return url;
	}

	public static Vector2D transformPosition( PaintBean pb, Vector2D oldPosition )
	{
		return transformPosition( pb.getGlobalMinX(), pb.getGlobalMaxX(), pb.getGlobalMinY(), pb.getGlobalMaxY(), pb.getMinX(), pb.getMaxX(),
				pb.getMinY(), pb.getMaxY(), pb.getWidth(), pb.getHeight(), oldPosition );
	}
	/**
	 * Transform position.
	 * 
	 * @param minX
	 *            the min x
	 * @param maxX
	 *            the max x
	 * @param minY
	 *            the min y
	 * @param maxY
	 *            the max y
	 * @param minXPercent
	 *            the min x percent
	 * @param maxXPercent
	 *            the max x percent
	 * @param minYPercent
	 *            the min y percent
	 * @param maxYPercent
	 *            the max y percent
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @param oldPosition
	 *            the old position
	 * @return the vector2 d
	 */
	public static Vector2D transformPosition( double minX, double maxX, double minY, double maxY, double minXPercent, double maxXPercent,
			double minYPercent, double maxYPercent, double width, double height, Vector2D oldPosition )
	{
		// Transform from world to image coordinates
		double x = ( oldPosition.getX() - minX ) / ( maxX - minX );
		double y = ( oldPosition.getY() - minY ) / ( maxY - minY );

		// Zoom transformation
		x = ( x - minXPercent ) / ( maxXPercent - minXPercent ) * width;
		y = ( y - minYPercent ) / ( maxYPercent - minYPercent ) * height;

		return new Vector2D( (float)x, (float)y );
	}

	/**
	 * Transform screen to world.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @param minX
	 *            the min x
	 * @param maxX
	 *            the max x
	 * @param minY
	 *            the min y
	 * @param maxY
	 *            the max y
	 * @param globalMinX
	 *            the global min x
	 * @param globalMaxX
	 *            the global max x
	 * @param globalMinY
	 *            the global min y
	 * @param globalMaxY
	 *            the global max y
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @return the vector2 d
	 */
	public static Vector2D transformScreenToWorld( double x, double y, double minX, double maxX, double minY, double maxY, double globalMinX,
			double globalMaxX, double globalMinY, double globalMaxY, double width, double height )
	{
		x = x / width * ( maxX - minX ) + minX;
		y = y / height * ( maxY - minY ) + minY;

		float newX = (float)( x * ( globalMaxX - globalMinX ) + globalMinX );
		float newY = (float)( y * ( globalMaxY - globalMinY ) + globalMinY );

		return new Vector2D( newX, newY );
	}

	public static Vector2D transformScreenToWorld( Vector2D screenPos, PaintBean pb )
	{
		return transformScreenToWorld( screenPos.getX(), screenPos.getY(), pb.getMinX(), pb.getMaxX(), pb.getMinY(), pb.getMaxY(), pb.getGlobalMinX(), pb.getGlobalMaxX(),
				pb.getGlobalMinY(), pb.getGlobalMaxY(), pb.getWidth(), pb.getHeight() );
	}

	/**
	 * Gets the movement.
	 * 
	 * @param node
	 *            the node
	 * @param newPosition
	 *            the new position
	 * @return the movement
	 */
	public static Vector2D getMovement( DNVNode node, Vector2D newPosition )
	{
		Vector2D tempPosition = new Vector2D(0,0);
		if( node != null )
		{
			tempPosition = new Vector2D( node.getPosition( true ) );
			tempPosition.setX( -tempPosition.getX() );
			tempPosition.setY( -tempPosition.getY() );
			tempPosition.add( newPosition );
		}
		return tempPosition;
	}

	/**
	 * Gets the icon.
	 * 
	 * @param url
	 *            the url
	 * @return the icon
	 */
	public static Image getIcon( String url )
	{
		synchronized( iconMap )
		{
			return iconMap.get( url );
		}
	}

	/**
	 * Gets the icon width.
	 * 
	 * @param url
	 *            the url
	 * @return the icon width
	 */
	public static Integer getIconWidth( String url )
	{
		synchronized( iconMap )
		{
			Integer width = iconWidth.get( url );
			if( width == null )
			{
				Image icon = getIcon( url );
				if( icon != null )
				{
					width = icon.getWidth( null );
					iconWidth.put( url, width );
				}
				else
				{
					width = 1;
				}
			}

			return width;
		}
	}

	/**
	 * Gets the icon height.
	 * 
	 * @param url
	 *            the url
	 * @return the icon height
	 */
	public static Integer getIconHeight( String url )
	{
		synchronized( iconMap )
		{
			Integer height = iconHeight.get( url );
			if( height == null )
			{
				Image icon = getIcon( url );
				if( icon != null )
				{
					height = icon.getHeight( null );
					iconHeight.put( url, height );
				}
				else
				{
					height = 0;
				}
			}

			return height;
		}
	}

	/**
	 * Sets the icon.
	 * 
	 * @param url
	 *            the url
	 * @param icon
	 *            the icon
	 */
	public static void setIcon( String url, Image icon )
	{
		synchronized( iconMap )
		{
			iconMap.put( url, icon );
			iconWidth.put( url, icon.getWidth( null ) );
			iconHeight.put( url, icon.getHeight( null ) );
		}
	}
}
