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

package net.wigis.graph.dnv.layout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import net.wigis.graph.RecommendationBean;
import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVEntity;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.geometry.Circle;
import net.wigis.graph.dnv.geometry.Geometric;
import net.wigis.graph.dnv.geometry.Line;
import net.wigis.graph.dnv.geometry.Rectangle;
import net.wigis.graph.dnv.geometry.Text;
import net.wigis.graph.dnv.utilities.GraphFunctions;
import net.wigis.graph.dnv.utilities.SortByFloatProperty;
import net.wigis.graph.dnv.utilities.Timer;
import net.wigis.graph.dnv.utilities.Vector2D;
import net.wigis.graph.dnv.utilities.Vector3D;
import net.wigis.settings.Settings;
import net.wigis.web.ContextLookup;

// TODO: Auto-generated Javadoc
/**
 * The Class RecommendationLayout.
 * 
 * @author Brynjar Gretarsson
 */
public class RecommendationLayout
{

	/**
	 * Run layout.
	 * 
	 * @param graph
	 *            the graph
	 * @param centralNode
	 *            the central node
	 * @param level
	 *            the level
	 * @param centerX
	 *            the center x
	 * @param centerY
	 *            the center y
	 * @param circle
	 *            the circle
	 */
	public static void runLayout( DNVGraph graph, DNVNode centralNode, int level, float centerX, float centerY, boolean circle )
	{
		RecommendationBean fb = (RecommendationBean)ContextLookup.lookup( "facebookBean", FacesContext.getCurrentInstance() );
		boolean usePearson = false;
		if( fb != null )
		{
			usePearson = fb.isUsePearson();
		}
		float maxDistance = initialize( graph, centralNode, level, centerX, centerY, usePearson );

		layoutLevel( 80, 80, graph, 0.01f, level, centralNode.getPosition(), maxDistance * 1.1f, circle );

		updateLayerLabels( graph, level, circle, new Vector2D( centerX, centerY ) );
	}

	/**
	 * Update layer labels.
	 * 
	 * @param graph
	 *            the graph
	 * @param level
	 *            the level
	 * @param circle
	 *            the circle
	 * @param center
	 *            the center
	 */
	private static void updateLayerLabels( DNVGraph graph, int level, boolean circle, Vector2D center )
	{
		List<Geometric> geometricObjects = new LinkedList<Geometric>();
		if( !circle )
		{
			float maxYPos = GraphFunctions.getMaxYPosition( graph, level, false );
			float minYPos = GraphFunctions.getMinYPosition( graph, level, false );
			float lineTopY = minYPos - ( ( maxYPos - minYPos ) * 0.12f );
			float lineBottomY = maxYPos + ( ( maxYPos - minYPos ) * 0.12f );
			Rectangle rect;
			rect = new Rectangle( new Vector2D( -1.25f, lineTopY ), new Vector2D( 0.25f, lineBottomY ), 1, BLUE, 0.2f, true );
			geometricObjects.add( rect );
			rect = new Rectangle( new Vector2D( 0.25f, lineTopY ), new Vector2D( 1.25f, lineBottomY ), 1, GREEN, 0.2f, true );
			geometricObjects.add( rect );
			rect = new Rectangle( new Vector2D( 1.25f, lineTopY ), new Vector2D( 2.625f, lineBottomY ), 1, BLUE, 0.2f, true );
			geometricObjects.add( rect );
			rect = new Rectangle( new Vector2D( 2.625f, lineTopY ), new Vector2D( 4f, lineBottomY ), 1, YELLOW, 0.2f, true );
			geometricObjects.add( rect );
			rect = new Rectangle( new Vector2D( 4f, lineTopY ), new Vector2D( 5f, lineBottomY ), 1, RED, 0.2f, true );
			geometricObjects.add( rect );

			Line line = new Line( new Vector2D( 0.25f, lineTopY ), new Vector2D( 0.25f, lineBottomY ), 2, DARK_GREY );
			geometricObjects.add( line );
			line = new Line( new Vector2D( 1.25f, lineTopY ), new Vector2D( 1.25f, lineBottomY ), 2, DARK_GREY );
			geometricObjects.add( line );
			line = new Line( new Vector2D( 2.625f, lineTopY ), new Vector2D( 2.625f, lineBottomY ), 2, DARK_GREY );
			geometricObjects.add( line );
			line = new Line( new Vector2D( 4f, lineTopY ), new Vector2D( 4f, lineBottomY ), 2, DARK_GREY );
			geometricObjects.add( line );

			int labelSize = 18;
			boolean bold = true;
			boolean outlined = true;
			boolean drawBackground = false;
			boolean scaleOnZoom = true;
			boolean curvedLabel = false;
			float labelYPos = minYPos - ( ( maxYPos - minYPos ) * 0.05f );
			Text text = new Text( "You", new Vector2D( 0, labelYPos ), BLACK, PURPLE, labelSize, bold, outlined, drawBackground, scaleOnZoom,
					curvedLabel, false );
			geometricObjects.add( text );
			text = new Text( "Your Items", new Vector2D( 0.75f, labelYPos ), BLACK, GREEN, labelSize, bold, outlined, drawBackground, scaleOnZoom,
					curvedLabel, false );
			geometricObjects.add( text );
			text = new Text( "Friends Who Like\nYour Items", new Vector2D( 1.9375f, labelYPos ), BLACK, PURPLE, labelSize, bold, outlined,
					drawBackground, scaleOnZoom, curvedLabel, false );
			geometricObjects.add( text );
			text = new Text( "Your\nRecommendations", new Vector2D( 3.3125f, labelYPos ), BLACK, YELLOW, labelSize, bold, outlined, drawBackground,
					scaleOnZoom, curvedLabel, false );
			geometricObjects.add( text );
			text = new Text( "Other\nFriends\nand Items", new Vector2D( 4.4f, labelYPos ), BLACK, RED, labelSize, bold, outlined, drawBackground,
					scaleOnZoom, curvedLabel, false );
			geometricObjects.add( text );
		}
		else
		{
			Circle disc;
			disc = new Circle( center, 0, (float)scaleForCircle( 0.25f ), 1, BLUE, 0.2f, true );
			geometricObjects.add( disc );
			disc = new Circle( center, (float)scaleForCircle( 0.25f ), (float)scaleForCircle( 1.25f ), 1, GREEN, 0.2f, true );
			geometricObjects.add( disc );
			disc = new Circle( center, (float)scaleForCircle( 1.25f ), (float)scaleForCircle( 2.625f ), 1, BLUE, 0.2f, true );
			geometricObjects.add( disc );
			disc = new Circle( center, (float)scaleForCircle( 2.625f ), (float)scaleForCircle( 4 ), 1, YELLOW, 0.2f, true );
			geometricObjects.add( disc );
			disc = new Circle( center, (float)scaleForCircle( 4 ), (float)scaleForCircle( 5 ), 1, RED, 0.2f, true );
			geometricObjects.add( disc );

			disc = new Circle( center, 0, (float)scaleForCircle( 0.25f ), 2, DARK_GREY, 1, false );
			geometricObjects.add( disc );
			disc = new Circle( center, (float)scaleForCircle( 0.25f ), (float)scaleForCircle( 1.25f ), 2, DARK_GREY, 1, false );
			geometricObjects.add( disc );
			disc = new Circle( center, (float)scaleForCircle( 1.25f ), (float)scaleForCircle( 2.625f ), 2, DARK_GREY, 1, false );
			geometricObjects.add( disc );
			disc = new Circle( center, (float)scaleForCircle( 2.625f ), (float)scaleForCircle( 4 ), 2, DARK_GREY, 1, false );
			geometricObjects.add( disc );
			disc = new Circle( center, (float)scaleForCircle( 4 ), (float)scaleForCircle( 5 ), 2, DARK_GREY, 1, false );
			geometricObjects.add( disc );
		}

		graph.setGeometricObjects( level, geometricObjects );
	}

	/** The Constant itemScoreSort. */
	private static final ItemScoreSort itemScoreSort = new ItemScoreSort();

	/** The Constant userSimilaritySort. */
	private static final UserSimilaritySort userSimilaritySort = new UserSimilaritySort();

	/** The Constant GREEN. */
	private static final Vector3D GREEN = new Vector3D( 0, 0.6f, 0 );

	/** The Constant YELLOW. */
	private static final Vector3D YELLOW = new Vector3D( 0.9f, 0.9f, 0 );

	/** The Constant RED. */
	private static final Vector3D RED = new Vector3D( 0.8f, 0.2f, 0 );

	/** The Constant PURPLE. */
	private static final Vector3D PURPLE = new Vector3D( 0.4f, 0.4f, 1 );

	/** The Constant BLUE. */
	private static final Vector3D BLUE = new Vector3D( 0.2f, 0.2f, 0.8f );

	/** The Constant WHITE. */
	// private static final Vector3D WHITE = new Vector3D( 1, 1, 1 );

	/** The Constant BLACK. */
	private static final Vector3D BLACK = new Vector3D( 0, 0, 0 );

	/** The Constant DARK_GREY. */
	private static final Vector3D DARK_GREY = new Vector3D( 0.3f, 0.3f, 0.3f );

	/** The Constant LIGHT_GREY. */
	// private static final Vector3D LIGHT_GREY = new Vector3D( 0.8f, 0.8f, 0.8f
	// );

	/** The Constant BLUEISH_GREY. */
	// private static final Vector3D BLUEISH_GREY = new Vector3D( 0.8f, 0.8f,
	// 1.0f );

	/**
	 * Scale for circle.
	 * 
	 * @param distance
	 *            the distance
	 * @return the float
	 */
	public static float scaleForCircle( float distance )
	{
		return (float)Math.log1p( distance );
	}

	/**
	 * Inverse scale for circle.
	 * 
	 * @param distance
	 *            the distance
	 * @return the float
	 */
	public static float inverseScaleForCircle( float distance )
	{
		return (float)Math.pow( Math.E, distance ) - 1;
	}

	/**
	 * Initialize.
	 * 
	 * @param graph
	 *            the graph
	 * @param centralNode
	 *            the central node
	 * @param level
	 *            the level
	 * @param centerX
	 *            the center x
	 * @param centerY
	 *            the center y
	 * @param usePearson
	 *            the use pearson
	 * @return the float
	 */
	public static float initialize( DNVGraph graph, DNVNode centralNode, int level, float centerX, float centerY, boolean usePearson )
	{
		Map<Float, List<DNVNode>> distanceToNodes = new HashMap<Float, List<DNVNode>>();
		Map<Float, List<DNVNode>> hopDistanceToNodes = new HashMap<Float, List<DNVNode>>();
		List<DNVNode> nodes = graph.getNodes( level );
		float distance;
		float maxDistance = -1;
		float userSimilarity;

		Timer computeHopDistance = new Timer( Timer.MILLISECONDS );
		Timer hopDistance2 = new Timer( Timer.MILLISECONDS );
		Timer hopDistance3 = new Timer( Timer.MILLISECONDS );
		Timer normalization = new Timer( Timer.MILLISECONDS );

		graph.setProperty( "centralNodeId", "" + centralNode.getId() );

		// Compute hop distance from center for all nodes
		computeHopDistance.setStart();
		GraphFunctions.findShortestPathToAllNodesInNumberOfHops( centralNode );
		centralNode.setForceLabel( true );

		for( DNVNode tempNode : nodes )
		{
			distance = GraphFunctions.getHopDistance( tempNode );
			if( distance == Float.POSITIVE_INFINITY )
			{
				graph.removeNode( tempNode );
				continue;
			}
			List<DNVNode> nodesAtDistance = hopDistanceToNodes.get( distance );
			if( nodesAtDistance == null )
			{
				nodesAtDistance = new ArrayList<DNVNode>();
				hopDistanceToNodes.put( distance, nodesAtDistance );
			}

			nodesAtDistance.add( tempNode );
			tempNode.setProperty( "hopDistance", "" + distance );

			if( distance == 1 )
			{
				float itemWeight = getItemWeight( tempNode );
				distance = ( 9 - itemWeight ) / 8.0f;
				tempNode.setRadius( itemWeight );
				tempNode.setForceLabel( true );
				tempNode.setColor( GREEN );
				addNodeAtDistance( distanceToNodes, distance, tempNode );
			}
			else if( distance == 0 )
			{
				tempNode.setColor( PURPLE );
				addNodeAtDistance( distanceToNodes, distance, tempNode );
			}
			else if( distance > 3 )
			{
				if( distance % 2 == 1 )
				{
					tempNode.setColor( RED );
					tempNode.setForceLabel( false );
					tempNode.setRadius( 1 );
				}
				else
				{
					tempNode.setColor( PURPLE );
					tempNode.setRadius( 1 );
				}
				addNodeAtDistance( distanceToNodes, 4 + ( ( distance - 3 ) * 0.1f ), tempNode );
			}

			if( distance > maxDistance && distance < Float.POSITIVE_INFINITY )
			{
				maxDistance = distance;
			}
		}
		computeHopDistance.setEnd();

		// Handle nodes at hop distance 2
		hopDistance2.setStart();
		nodes = hopDistanceToNodes.get( 2.0f );
		if( nodes != null )
		{
			for( DNVNode tempNode : nodes )
			{
				distance = Float.parseFloat( tempNode.getProperty( "hopDistance" ) );
				if( distance == 2 )
				{
					if( usePearson )
					{
						userSimilarity = getPearsonUserSimlarity( graph, centralNode, distance, tempNode );
					}
					else
					{
						userSimilarity = getUserSimilarity( centralNode, distance, tempNode );
					}

					tempNode.setProperty( "userSimilarity", "" + userSimilarity );
					tempNode.setForceLabel( false );

					distance = distance + 0.5f - userSimilarity;
					tempNode.setRadius( 0.5f + userSimilarity * 5 );
					updateUserWeightColor( tempNode, getUserWeight( tempNode ) );

					if( userSimilarity > 0.75 )
					{
						tempNode.setForceLabel( true );
					}

					addNodeAtDistance( distanceToNodes, distance, tempNode );
				}
			}

			Collections.sort( nodes, userSimilaritySort );
			float previousScore = 2;
			int rank = 1;
			int counter = 0;
			for( DNVNode userNode : nodes )
			{
				counter++;
				userSimilarity = Float.parseFloat( userNode.getProperty( "userSimilarity" ) );
				if( userSimilarity < previousScore )
				{
					previousScore = userSimilarity;
					rank = counter;
				}
				userNode.setProperty( "rank", "" + rank );
				highlightRank( rank, userNode, 5, 14, 12 );
			}
		}
		hopDistance2.setEnd();

		// Handle nodes at hop distance 3
		nodes = hopDistanceToNodes.get( 3.0f );
		if( nodes != null )
		{
			hopDistance3.setStart();
			float itemScore = 0;
			float maxItemScore = 0;
			float predictionWeight = 1;
			for( DNVNode itemNode : nodes )
			{
				itemScore = 0;
				distance = Float.parseFloat( itemNode.getProperty( "hopDistance" ) );
				if( distance == 3 )
				{
					List<DNVNode> friendNodes = hopDistanceToNodes.get( 2.0f );
					for( DNVNode friendNode : friendNodes )
					{
						if( GraphFunctions.areConnected( itemNode, friendNode ) )
						{
							itemScore += Float.parseFloat( friendNode.getProperty( "userSimilarity" ) );
						}
					}

					itemNode.setProperty( "originalItemScore", "" + itemScore );

					predictionWeight = getPredictionWeight( itemNode );
					itemScore *= predictionWeight;

					if( itemScore > maxItemScore )
					{
						maxItemScore = itemScore;
					}

					itemNode.setProperty( "itemScore", "" + itemScore );
					itemNode.setColor( YELLOW );
				}
			}

			Collections.sort( nodes, itemScoreSort );
			float previousScore = 2;
			int rank = 1;
			int counter = 0;
			for( DNVNode itemNode : nodes )
			{
				counter++;
				itemScore = Float.parseFloat( itemNode.getProperty( "itemScore" ) );
				if( itemScore < previousScore )
				{
					previousScore = itemScore;
					rank = counter;
				}
				itemNode.setProperty( "rank", "" + rank );
			}

			// Generate string containing the recommendations list
			StringBuilder recommendations = new StringBuilder();
			recommendations.append( "<table border=\"0\" style=\"font-size:small\" width=\"400px\">\n" );
			recommendations.append( "<tr><td>Rank</td><td>Title</td><td>Type</td><td>Score</td></tr>\n" );
			SortByFloatProperty sbfp = new SortByFloatProperty( "rank", false );
			Collections.sort( nodes, sbfp );
			String tempScore;
			for( DNVNode node : nodes )
			{
				recommendations.append( "<tr>" );
				recommendations.append( "<td>" + node.getProperty( "rank" ) + "</td>" );
				recommendations.append( "<td>" + node.getLabel() + "</td>" );
				recommendations.append( "<td>" + node.getType() + "</td>" );
				tempScore = node.getProperty( "itemScore" );
				if( tempScore != null )
				{
					itemScore = Float.parseFloat( tempScore );
					itemScore = Math.round( itemScore * 1000.0f ) / 1000.0f;
					recommendations.append( "<td>" + itemScore + "</td>" );
				}
				recommendations.append( "</tr>\n" );
			}

			recommendations.append( "</table>\n\n" );
			graph.setProperty( "Recommendations", recommendations.toString() );

			// System.out.println( "Recommendations:" );
			// System.out.println( recommendations.toString() );
			hopDistance3.setEnd();

			// Normalize item scores
			normalization.setStart();
			for( DNVNode itemNode : nodes )
			{
				itemScore = Float.parseFloat( itemNode.getProperty( "itemScore" ) );
				itemScore /= maxItemScore;
				itemNode.setProperty( "itemScore", "" + itemScore );
				itemNode.setForceLabel( false );

				distance = Float.parseFloat( itemNode.getProperty( "hopDistance" ) );
				distance = distance + 1 - itemScore;
				itemNode.setRadius( 0.5f + itemScore * 3 );
				rank = Integer.parseInt( itemNode.getProperty( "rank" ) );
				highlightRank( rank, itemNode, 5, 16, 12 );
				addNodeAtDistance( distanceToNodes, distance, itemNode );
			}
			normalization.setEnd();
		}

		centralNode.setPosition( centerX, centerY );

		if( Settings.DEBUG )
		{
			System.out.println( "computeHopDistance took " + computeHopDistance.getLastSegment( Timer.SECONDS ) + " seconds." );
			System.out.println( "hopDistance2 took       " + hopDistance2.getLastSegment( Timer.SECONDS ) + " seconds." );
			System.out.println( "hopDistance3 took       " + hopDistance3.getLastSegment( Timer.SECONDS ) + " seconds." );
			System.out.println( "normalization took      " + normalization.getLastSegment( Timer.SECONDS ) + " seconds." );
		}

		return maxDistance;
	}

	/**
	 * Highlight rank.
	 * 
	 * @param rank
	 *            the rank
	 * @param itemNode
	 *            the item node
	 * @param limit
	 *            the limit
	 * @param bigFontSize
	 *            the big font size
	 * @param smallFontSize
	 *            the small font size
	 */
	private static void highlightRank( int rank, DNVNode itemNode, int limit, int bigFontSize, int smallFontSize )
	{
		if( rank <= limit )
		{
			itemNode.setForceLabel( true );
			// itemNode.setLabelOutlineColor( "#000000" );
			// itemNode.setLabelColor( "#FFFFFF" );
			itemNode.setLabelSize( bigFontSize );
		}
		else
		{
			// Vector3D temp = null ;
			// itemNode.setLabelOutlineColor( temp );
			// itemNode.setLabelColor( temp );
			itemNode.setForceLabel( false );
			itemNode.setLabelSize( smallFontSize );
		}
	}

	/**
	 * Gets the pearson user simlarity.
	 * 
	 * @param graph
	 *            the graph
	 * @param centralNode
	 *            the central node
	 * @param distance
	 *            the distance
	 * @param tempNode
	 *            the temp node
	 * @return the pearson user simlarity
	 */
	private static float getPearsonUserSimlarity( DNVGraph graph, DNVNode centralNode, float distance, DNVNode tempNode )
	{
		float userSimilarity;
		float x_total;
		float y_total;
		float global_total;
		float x_bar;
		float y_bar;
		float sx;
		float sy;
		float n;

		Map<Integer, DNVEntity> tempMap = graph.getNodesByType( 0, RecommendationBean.MUSIC );
		if( tempMap == null || tempMap.size() == 0 )
		{
			tempMap = graph.getNodesByType( 0, RecommendationBean.MOVIES );
		}
		if( tempMap == null || tempMap.size() == 0 )
		{
			tempMap = graph.getNodesByType( 0, RecommendationBean.BOOKS );
		}
		List<DNVNode> allItems = new ArrayList<DNVNode>();
		for( DNVEntity entity : tempMap.values() )
		{
			allItems.add( (DNVNode)entity );
		}

		x_total = getTotalItemWeight( centralNode.getNeighbors() );
		y_total = tempNode.getNeighbors().size();
		global_total = getTotalItemWeight( allItems );
		n = allItems.size();

		x_bar = x_total / global_total;
		y_bar = y_total / n;

		sx = getStdDev( allItems, centralNode, x_bar, true );
		sy = getStdDev( allItems, tempNode, y_bar, false );

		float xi;
		float yi;
		float topPart = 0;
		for( DNVNode item : allItems )
		{
			if( item.getNeighborMap().containsKey( centralNode.getId() ) )
			{
				xi = getItemWeight( item );
			}
			else
			{
				xi = 0;
			}

			if( item.getNeighborMap().containsKey( tempNode.getId() ) )
			{
				yi = 1;
			}
			else
			{
				yi = 0;
			}

			topPart += ( xi - x_bar ) * ( yi - y_bar );
		}

		float bottomPart = ( n - 1 ) * sx * sy;
		float pearson = topPart / bottomPart;
		float pearson_scaled = ( pearson + 1 ) / 2.0f;

		userSimilarity = getUserWeight( tempNode ) * pearson_scaled;

		if( userSimilarity > 1 )
		{
			userSimilarity = 1;
		}
		else if( userSimilarity <= 0 )
		{
			userSimilarity = 0.001f;
		}

		return userSimilarity;
	}

	/**
	 * Gets the std dev.
	 * 
	 * @param allItems
	 *            the all items
	 * @param currentNode
	 *            the current node
	 * @param x_bar
	 *            the x_bar
	 * @param useWeight
	 *            the use weight
	 * @return the std dev
	 */
	private static float getStdDev( List<DNVNode> allItems, DNVNode currentNode, float x_bar, boolean useWeight )
	{
		float variance = 0;
		float xi;

		for( DNVNode item : allItems )
		{
			if( item.getNeighborMap().containsKey( currentNode.getId() ) )
			{
				if( useWeight )
				{
					xi = getItemWeight( item );
				}
				else
				{
					xi = 1;
				}
			}
			else
			{
				xi = 0;
			}

			variance += Math.pow( xi - x_bar, 2 );
		}

		variance /= allItems.size();

		return (float)Math.sqrt( variance );
	}

	/**
	 * Gets the user similarity.
	 * 
	 * @param centralNode
	 *            the central node
	 * @param distance
	 *            the distance
	 * @param tempNode
	 *            the temp node
	 * @return the user similarity
	 */
	private static float getUserSimilarity( DNVNode centralNode, float distance, DNVNode tempNode )
	{
		List<DNVNode> neighbors;
		float totalItemScore;
		float userSimilarity;

		neighbors = tempNode.getNeighbors();
		totalItemScore = getTotalItemScore( distance, neighbors );

		userSimilarity = getUserWeight( tempNode )
				* ( totalItemScore / (float)Math.sqrt( getTotalItemWeight( neighbors ) * getTotalItemWeight( centralNode.getNeighbors() ) ) );

		if( userSimilarity > 1 )
		{
			userSimilarity = 1;
		}
		else if( userSimilarity <= 0 )
		{
			userSimilarity = 0.001f;
		}

		return userSimilarity;
	}

	/**
	 * Gets the user weight.
	 * 
	 * @param node
	 *            the node
	 * @return the user weight
	 */
	private static float getUserWeight( DNVNode node )
	{
		return getFloatProperty( node, "userWeight", 1 );
	}

	/**
	 * Gets the manual user weight.
	 * 
	 * @param node
	 *            the node
	 * @return the manual user weight
	 */
	private static float getManualUserWeight( DNVNode node )
	{
		return getFloatProperty( node, "manualUserWeight", 1 );
	}

	/**
	 * Gets the item weight.
	 * 
	 * @param node
	 *            the node
	 * @return the item weight
	 */
	private static float getItemWeight( DNVNode node )
	{
		return getFloatProperty( node, "itemWeight", 1 );
	}

	/**
	 * Gets the prediction weight.
	 * 
	 * @param node
	 *            the node
	 * @return the prediction weight
	 */
	private static float getPredictionWeight( DNVNode node )
	{
		return getFloatProperty( node, "predictionWeight", 1 );
	}

	/**
	 * Gets the float property.
	 * 
	 * @param node
	 *            the node
	 * @param property
	 *            the property
	 * @param defaultValue
	 *            the default value
	 * @return the float property
	 */
	private static float getFloatProperty( DNVNode node, String property, float defaultValue )
	{
		String weightStr;
		float weight;
		weightStr = node.getProperty( property );
		if( weightStr == null )
		{
			weight = defaultValue;
			node.setProperty( property, "" + weight );
		}
		else
		{
			weight = Float.parseFloat( weightStr );
		}
		return weight;
	}

	/**
	 * Gets the total item weight.
	 * 
	 * @param nodes
	 *            the nodes
	 * @return the total item weight
	 */
	private static float getTotalItemWeight( List<DNVNode> nodes )
	{
		float weight = 0;
		for( DNVNode node : nodes )
		{
			weight += getItemWeight( node );
		}

		return weight;
	}

	/**
	 * Adds the node at distance.
	 * 
	 * @param distanceToNodes
	 *            the distance to nodes
	 * @param distance
	 *            the distance
	 * @param tempNode
	 *            the temp node
	 */
	private static void addNodeAtDistance( Map<Float, List<DNVNode>> distanceToNodes, float distance, DNVNode tempNode )
	{
		List<DNVNode> nodesAtDistance = distanceToNodes.get( distance );
		if( nodesAtDistance == null )
		{
			nodesAtDistance = new ArrayList<DNVNode>();
			distanceToNodes.put( distance, nodesAtDistance );
		}

		tempNode.setDistanceFromCenterNode( distance );
		nodesAtDistance.add( tempNode );
	}

	/**
	 * Run iteration.
	 * 
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @param graph
	 *            the graph
	 * @param level
	 *            the level
	 * @param temperature
	 *            the temperature
	 * @param center
	 *            the center
	 * @param maxRadius
	 *            the max radius
	 * @param counter
	 *            the counter
	 * @param circle
	 *            the circle
	 */
	public static void runIteration( float width, float height, DNVGraph graph, int level, float temperature, Vector2D center, float maxRadius,
			int counter, boolean circle )
	{
		synchronized( graph )
		{

			float area = width * height;
			float k = (float)Math.sqrt( area / graph.getGraphSize( level ) );
			float kPower2 = k * k;
			// System.out.println( "Temperature : " + temperature + " Level : "
			// + level + " Number of Nodes: " + graph.getGraphSize( level ) );

			Vector2D difference = new Vector2D();
			float length;

			Grid grid = new Grid( k * 2, graph, level );
			List<DNVNode> potentialNodes;

			float maxYAtDistance1 = Float.NEGATIVE_INFINITY;
			float minYAtDistance1 = Float.POSITIVE_INFINITY;
			float tempY;
			float distance;
			// repulsive forces
			for( DNVNode v : graph.getNodes( level ) )
			{
				potentialNodes = grid.getPotentialNodes( v );
				for( DNVNode u : potentialNodes )
				{
					if( u != v )
					{
						difference.set( v.getPosition() );
						difference.subtract( u.getPosition() );
						length = difference.length();
						if( length == 0 )
						{
							difference.set( (float)Math.random() - 0.5f, (float)Math.random() - 0.5f );
							length = difference.length();
						}
						if( length < k * 2 )
						{
							difference.normalize();
							difference.dotProduct( repel( length, kPower2 * u.getRadius() / 4.0f ) );
							v.getForce().add( difference );
						}
					}
				}

				if( !circle )
				{
					distance = GraphFunctions.getHopDistance( v );
					if( distance == 1 )
					{
						tempY = v.getPosition().getY();
						if( tempY > maxYAtDistance1 )
						{
							maxYAtDistance1 = tempY;
						}
						if( tempY < minYAtDistance1 )
						{
							minYAtDistance1 = tempY;
						}
					}
				}
			}

			// attractive forces
			for( DNVEdge e : graph.getEdges( level ) )
			{
				difference.set( e.getFrom().getPosition() );
				difference.subtract( e.getTo().getPosition() );
				length = difference.length();
				difference.normalize();
				difference.dotProduct( attract( length, k ) );
				e.getFrom().getForce().subtract( difference );
				e.getTo().getForce().add( difference );
			}

			// apply the forces
			for( DNVNode v : graph.getNodes( level ) )
			{
				difference.set( v.getForce() );
				length = difference.length();
				difference.normalize();
				difference.dotProduct( Math.min( length, temperature ) );
				v.move( difference, true, false );
				v.setForce( 0, 0 );
				if( circle )
				{
					if( counter % 5 == 0 )
					{
						forceToShape( maxRadius, circle, v, minYAtDistance1, maxYAtDistance1 );
					}
				}
				else
				{
					forceToShape( maxRadius, circle, v, minYAtDistance1, maxYAtDistance1 );
				}
			}
		}
	}

	/**
	 * Force to shape.
	 * 
	 * @param maxRadius
	 *            the max radius
	 * @param circle
	 *            the circle
	 * @param v
	 *            the v
	 * @param minYAtDistance1
	 *            the min y at distance1
	 * @param maxYAtDistance1
	 *            the max y at distance1
	 */
	private static void forceToShape( float maxRadius, boolean circle, DNVNode v, float minYAtDistance1, float maxYAtDistance1 )
	{
		if( circle )
			forceToCircle( maxRadius, v );
		else
			forceToLine( maxRadius, v, minYAtDistance1, maxYAtDistance1 );
	}

	/**
	 * Force to circle.
	 * 
	 * @param maxRadius
	 *            the max radius
	 * @param v
	 *            the v
	 */
	private static void forceToCircle( float maxRadius, DNVNode v )
	{
		float radius;
		radius = v.getDistanceFromCenterNode();
		if( radius == Float.POSITIVE_INFINITY )
		{
			radius = maxRadius;
		}
		radius = (float)scaleForCircle( radius );
		v.getPosition().normalize().dotProduct( radius );
	}

	/** The Constant scale. */
	private static final float scale = 100;

	/**
	 * Force to line.
	 * 
	 * @param maxRadius
	 *            the max radius
	 * @param v
	 *            the v
	 * @param minYAtDistance1
	 *            the min y at distance1
	 * @param maxYAtDistance1
	 *            the max y at distance1
	 */
	private static void forceToLine( float maxRadius, DNVNode v, float minYAtDistance1, float maxYAtDistance1 )
	{
		float radius;
		radius = v.getDistanceFromCenterNode();
		if( radius == Float.POSITIVE_INFINITY )
		{
			radius = maxRadius;
		}
		v.getPosition().setX( radius );
		String hopDistanceStr = v.getProperty( "hopDistance" );
		if( hopDistanceStr != null && Float.parseFloat( hopDistanceStr ) > 1 )
		{
			float maxDistance = maxYAtDistance1 - v.getPosition().getY();
			float minDistance = minYAtDistance1 - v.getPosition().getY();

			if( maxDistance < 0 )
			{
				// v is above top margin
				v.addForce( new Vector2D( 0, scale * maxDistance ) );
			}
			else if( minDistance > 0 )
			{
				// v is below bottom margin
				v.addForce( new Vector2D( 0, scale * minDistance ) );
			}
			else
			{
				// v is within margins
				v.addForce( new Vector2D( 0, scale * 1f / maxDistance ) );
				v.addForce( new Vector2D( 0, scale * 1f / minDistance ) );

			}
		}
	}

	/**
	 * Attract.
	 * 
	 * @param x
	 *            the x
	 * @param k
	 *            the k
	 * @return the float
	 */
	private static float attract( float x, float k )
	{
		return ( x * x ) / k;
	}

	/**
	 * Repel.
	 * 
	 * @param x
	 *            the x
	 * @param k
	 *            the k
	 * @return the float
	 */
	private static float repel( float x, float k )
	{
		return k / x;
	}

	/**
	 * Cool.
	 * 
	 * @param temperature
	 *            the temperature
	 * @param coolingFactor
	 *            the cooling factor
	 * @return the float
	 */
	private static float cool( float temperature, float coolingFactor )
	{
		if( temperature > 0.01 )
			return Math.max( 0, temperature * ( 1 - coolingFactor ) );

		return 0;
	}

	/**
	 * Layout level.
	 * 
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @param graph
	 *            the graph
	 * @param coolingFactor
	 *            the cooling factor
	 * @param level
	 *            the level
	 * @param center
	 *            the center
	 * @param maxRadius
	 *            the max radius
	 * @param circle
	 *            the circle
	 */
	private static void layoutLevel( float width, float height, DNVGraph graph, float coolingFactor, int level, Vector2D center, float maxRadius,
			boolean circle )
	{
		float size = Math.max( GraphFunctions.getGraphWidth( graph, level, false ), width );
		size = Math.max( size, Math.max( GraphFunctions.getGraphHeight( graph, level, false ), height ) );
		float temperature = size / 4.0f;

		int counter = 0;
		while( temperature > 0 )
		{
			runIteration( width, height, graph, level, temperature, center, maxRadius, counter, circle );
			temperature = cool( temperature, coolingFactor );
			counter++;
		}

		for( DNVNode v : graph.getNodes( level ) )
		{
			forceToShape( maxRadius, circle, v, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY );
		}
	}

	/** The last distance. */
	private float lastDistance = 0;

	/**
	 * Move node.
	 * 
	 * @param graph
	 *            the graph
	 * @param selectedNode
	 *            the selected node
	 * @param centralNode
	 *            the central node
	 * @param newPosition
	 *            the new position
	 * @param hopDistance
	 *            the hop distance
	 * @param circle
	 *            the circle
	 * @param request
	 *            the request
	 * @param sameNode
	 *            the same node
	 */
	public void moveNode( DNVGraph graph, DNVNode selectedNode, DNVNode centralNode, Vector2D newPosition, float hopDistance, boolean circle,
			HttpServletRequest request, boolean sameNode )
	{
		Timer moveNodeTimer = new Timer( Timer.MILLISECONDS );
		Timer initializeTimer = new Timer( Timer.MILLISECONDS );
		Timer iterationTimer = new Timer( Timer.MILLISECONDS );
		moveNodeTimer.setStart();
		float distance = getDistance( centralNode, newPosition, circle );
		float currentDistance = distance;
		RecommendationBean fb = (RecommendationBean)ContextLookup.lookup( "facebookBean", request );
		if( fb != null )
		{
			boolean usePearson = fb.isUsePearson();
			if( hopDistance == 1 )
			{
				if( distance > 1.1 && sameNode && distance > lastDistance )
				{
					// Remove from user profile
					try
					{
						fb.removeFromUserProfile( selectedNode, false );
						float hops = GraphFunctions.findShortestPathDistanceInNumberOfHops( centralNode, selectedNode );
						selectedNode.setProperty( "hopDistance", "" + hops );
						lastDistance = currentDistance;
						moveNode( graph, selectedNode, centralNode, newPosition, hops, circle, request, sameNode );
						return;
					}
					catch( NullPointerException npe )
					{
						npe.printStackTrace();
					}
				}

				if( distance < 0.5f )
				{
					distance = 0.5f;
				}
				else if( distance > 1 )
				{
					distance = 1;
				}

				float weight = 9 - ( 8 * distance );
				selectedNode.setProperty( "itemWeight", "" + weight );
				selectedNode.setRadius( weight );
			}
			else if( hopDistance == 2 )
			{
				if( distance < 1.5f )
				{
					distance = 1.5f;
				}
				else if( distance > 2.5f )
				{
					distance = 2.49f;
				}

				float userSimilarity = ( 2.5f - distance );

				float userWeight = 1;
				if( !usePearson )
				{
					List<DNVNode> neighbors = selectedNode.getNeighbors();
					float totalItemScore = getTotalItemScore( hopDistance, neighbors );

					float otherValue = ( totalItemScore / (float)Math.sqrt( getTotalItemWeight( neighbors )
							* getTotalItemWeight( centralNode.getNeighbors() ) ) );
					userWeight = userSimilarity / otherValue;

					if( userWeight > 0.85f && userWeight < 1.15f )
					{
						userWeight = 1;
						userSimilarity = userWeight * otherValue;
						selectedNode.setDistanceFromCenterNode( 2.5f - userSimilarity );
					}
					updateUserWeightColor( selectedNode, userWeight );
				}
				else
				{
					float previousUserSimilarity = Float.parseFloat( selectedNode.getProperty( "userSimilarity" ) );
					float previousUserWeight = Float.parseFloat( selectedNode.getProperty( "userWeight" ) );
					float previousPearson = previousUserSimilarity / previousUserWeight;

					userWeight = userSimilarity / previousPearson;
					if( userWeight > 0.8f && userWeight < 1.2f )
					{
						userWeight = 1;
						userSimilarity = userWeight * previousPearson;
						selectedNode.setDistanceFromCenterNode( 2.5f - userSimilarity );
					}
				}

				float oldUserWeight = getUserWeight( selectedNode );
				selectedNode.setProperty( "userWeight", "" + userWeight );
				if( oldUserWeight != userWeight )
				{
					selectedNode.setProperty( "manualUserWeight", "" + userWeight );
				}
			}
			else if( hopDistance == 3 )
			{
				if( distance < 2.8 && sameNode && distance < lastDistance )
				{
					try
					{
						fb.addToUserProfile( selectedNode, false );
						lastDistance = currentDistance;
						List<DNVNode> neighbors = selectedNode.getNeighbors();
						for( DNVNode neighbor : neighbors )
						{
							if( !neighbor.hasProperty( "manualUserWeight" ) )
							{
								neighbor.setProperty( "userWeight", "1" );
							}
						}
						moveNode( graph, selectedNode, centralNode, newPosition, 1, circle, request, sameNode );
						return;
					}
					catch( NullPointerException npe )
					{
						npe.printStackTrace();
					}
				}
				else
				{
					if( distance < 3 )
						distance = 3;
					else if( distance > 4 )
						distance = 4;

					float newItemScore = 4 - distance;
					String originalScoreStr = selectedNode.getProperty( "originalItemScore" );
					float oldItemScore = newItemScore;
					if( originalScoreStr != null )
					{
						oldItemScore = Float.parseFloat( originalScoreStr );
					}
					float predictionWeight;
					predictionWeight = newItemScore / oldItemScore;

					List<DNVNode> neighbors = selectedNode.getNeighbors();
					float userWeight;
					float manualUserWeight;
					boolean addToProfile = false;
					for( DNVNode neighbor : neighbors )
					{
						userWeight = getUserWeight( neighbor );
						manualUserWeight = getManualUserWeight( neighbor );
						userWeight *= predictionWeight;
						if( userWeight < 0.01f )
						{
							userWeight = 0.01f;
						}
						else if( userWeight > manualUserWeight )
						{
							userWeight = manualUserWeight;
							addToProfile = true;
						}
						neighbor.setProperty( "userWeight", "" + userWeight );
					}

					float distanceFromSelected = getDistance( selectedNode, newPosition, circle );
					float selectedNodeDistanceFromCenter = getDistance( centralNode, selectedNode.getPosition(), circle );
					if( addToProfile && distanceFromSelected > 0.1 && selectedNodeDistanceFromCenter > distance && sameNode
							&& distance < lastDistance )
					{
						try
						{
							fb.addToUserProfile( selectedNode, false );
							lastDistance = currentDistance;
							moveNode( graph, selectedNode, centralNode, newPosition, 1, circle, request, sameNode );
							return;
						}
						catch( NullPointerException npe )
						{
							npe.printStackTrace();
						}

					}

					// selectedNode.setProperty( "predictionWeight", "" +
					// predictionWeight );
				}

			}
			else if( hopDistance % 2 == 1 )
			{
				float distanceFromSelected = getDistance( selectedNode, newPosition, circle );
				float selectedNodeDistanceFromCenter = getDistance( centralNode, selectedNode.getPosition(), circle );
				if( distanceFromSelected > 0.1 && selectedNodeDistanceFromCenter > distance && sameNode && distance < lastDistance )
				{
					try
					{
						fb.addToUserProfile( selectedNode, false );
						lastDistance = currentDistance;
						moveNode( graph, selectedNode, centralNode, newPosition, 1, circle, request, sameNode );
						return;
					}
					catch( NullPointerException npe )
					{
						npe.printStackTrace();
					}
				}
			}

			if( fb != null )
			{
				initializeTimer.setStart();
				float maxRadius = initialize( graph, centralNode, 0, centralNode.getPosition().getX(), centralNode.getPosition().getY(), fb
						.isUsePearson() );
				initializeTimer.setEnd();
				iterationTimer.setStart();
				for( int i = 5; i > 0; i-- )
				{
					runIteration( 80, 80, graph, 0, 0.1f * i, centralNode.getPosition(), maxRadius, 0, circle );
				}
				iterationTimer.setEnd();

				moveNodeTimer.setEnd();

				if( Settings.DEBUG )
				{
					System.out.println( "moveNode took       " + moveNodeTimer.getLastSegment( Timer.SECONDS ) + " seconds." );
					System.out.println( "initialization took " + initializeTimer.getLastSegment( Timer.SECONDS ) + " seconds." );
					System.out.println( "iteration took      " + iterationTimer.getLastSegment( Timer.SECONDS ) + " seconds." );
				}
			}
		}

		lastDistance = currentDistance;

		updateLayerLabels( graph, 0, circle, centralNode.getPosition() );
	}

	/**
	 * Update user weight color.
	 * 
	 * @param selectedNode
	 *            the selected node
	 * @param userWeight
	 *            the user weight
	 */
	private static void updateUserWeightColor( DNVNode selectedNode, float userWeight )
	{
		if( userWeight >= 1.15f )
		{
			Vector3D color = new Vector3D( GREEN );
			color.dotProduct( userWeight );
			color.add( PURPLE );
			color.dotProduct( 1.0f / ( userWeight + 1 ) );
			selectedNode.setColor( color );
		}
		else if( userWeight <= 0.85f )
		{
			Vector3D color = new Vector3D( PURPLE );
			color.dotProduct( userWeight );
			color.add( RED );
			color.dotProduct( 1.0f / ( userWeight + 1 ) );
			selectedNode.setColor( color );
		}
		else
		{
			selectedNode.setColor( PURPLE );
		}
	}

	/**
	 * Gets the total item score.
	 * 
	 * @param distance
	 *            the distance
	 * @param neighbors
	 *            the neighbors
	 * @return the total item score
	 */
	private static float getTotalItemScore( float distance, List<DNVNode> neighbors )
	{
		float neighborDistance;
		float weight;
		float totalItemScore = 0;
		for( DNVNode neighbor : neighbors )
		{
			neighborDistance = Float.parseFloat( neighbor.getProperty( "hopDistance" ) );
			if( neighborDistance < distance )
			{
				weight = getItemWeight( neighbor );

				totalItemScore += weight;
			}
		}
		return totalItemScore;
	}

	/**
	 * Gets the distance.
	 * 
	 * @param centralNode
	 *            the central node
	 * @param newPosition
	 *            the new position
	 * @param circle
	 *            the circle
	 * @return the distance
	 */
	private static float getDistance( DNVNode centralNode, Vector2D newPosition, boolean circle )
	{
		float distance;
		if( circle )
		{
			distance = GraphFunctions.getDistance( centralNode.getPosition(), newPosition );
			distance = inverseScaleForCircle( distance );
		}
		else
		{
			distance = Math.abs( newPosition.getX() - centralNode.getPosition().getX() );
		}
		return distance;
	}
}
