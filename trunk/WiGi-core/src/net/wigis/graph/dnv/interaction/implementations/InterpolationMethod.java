/******************************************************************************************************
 * Copyright (c) 2010, University of California, Santa Barbara All rights
 * reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. * Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 *****************************************************************************************************/

package net.wigis.graph.dnv.interaction.implementations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.wigis.graph.ImageRenderer;
import net.wigis.graph.PaintBean;
import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.interaction.helpers.InteractionFunctions;
import net.wigis.graph.dnv.interaction.interfaces.SimpleInteractionInterface;
import net.wigis.graph.dnv.utilities.GraphFunctions;
import net.wigis.graph.dnv.utilities.Timer;
import net.wigis.graph.dnv.utilities.Vector2D;
import net.wigis.settings.Settings;

// TODO: Auto-generated Javadoc
/**
 * The Class InterpolationMethod.
 * 
 * @author Brynjar Gretarsson
 */
public final class InterpolationMethod implements SimpleInteractionInterface
{

	/**
	 * Logger.
	 * 
	 * @param graph
	 *            the graph
	 * @param level
	 *            the level
	 */
	// // private static Log logger = LogFactory.getLog(
	// InterpolationMethod.class );

	public static void resetInterpolationData( DNVGraph graph, Integer level )
	{
		List<DNVNode> nodes = graph.getNodes( level );
		graph.clearInterpolationList( level );
		for( int i = 0; i < nodes.size(); i++ )
		{
			nodes.get( i ).resetInterpolationData();
		}
	}

	/**
	 * Reset interpolation weight.
	 * 
	 * @param graph
	 *            the graph
	 * @param level
	 *            the level
	 */
	public static void resetInterpolationWeight( DNVGraph graph, Integer level )
	{
		List<DNVNode> nodes = graph.getNodes( level );
		for( int i = 0; i < nodes.size(); i++ )
		{
			nodes.get( i ).resetInterpolationWeights();
		}
	}

	// public static int performBFSOld( DNVNode selectedNode )
	// {
	// return performBFSOld( selectedNode, Integer.MAX_VALUE );
	// }
	//
	// public static int performBFSOld( DNVNode selectedNode, int maxDepth )
	// {
	// Timer bfsTimer = new Timer( Timer.MILLISECONDS );
	// bfsTimer.setStart();
	// selectedNode.setDistanceFromSelectedNode( 0 );
	// int returnValue = 0;
	// if( maxDepth > 0 )
	// {
	// returnValue = handleList( selectedNode.getNeighbors(), 1, maxDepth );
	// }
	// bfsTimer.setEnd();
	// if( Settings.DEBUG )
	// {
	// System.out.println( "Interpolation BFS took " + bfsTimer.getLastSegment(
	// Timer.SECONDS ) + " seconds." );
	// }
	//
	// return returnValue;
	// }

	// private static int handleList( List<DNVNode> nodesToHandle, int distance,
	// int maxDepth )
	// {
	// DNVNode tempNode;
	// List<DNVNode> nextList = new ArrayList<DNVNode>();
	// for( int i = 0; i < nodesToHandle.size(); i++ )
	// {
	// tempNode = nodesToHandle.get( i );
	// if( tempNode.getDistanceFromSelectedNode() > distance )
	// {
	// tempNode.setDistanceFromSelectedNode( distance );
	// nextList.addAll( tempNode.getNeighbors() );
	// }
	// }
	//
	// if( !nextList.isEmpty() && distance < maxDepth )
	// {
	// return handleList( nextList, distance + 1, maxDepth );
	// }
	//
	// return distance - 1;
	// }

	/**
	 * Perform bfs.
	 * 
	 * @param selectedNode
	 *            the selected node
	 * @param maxDepth
	 *            the max depth
	 * @param useActualDistance
	 *            the use actual distance
	 * @return the float
	 */
	public static float performBFS( DNVNode selectedNode, float maxDepth, boolean useActualDistance )
	{
		Timer bfsTimer = new Timer( Timer.MILLISECONDS );
		List<DNVNode> queue = new ArrayList<DNVNode>();
		bfsTimer.setStart();
		selectedNode.setDistanceFromSelectedNode( 0 );
		selectedNode.setActualDistanceFromSelectedNode( 0 );
		selectedNode.setDistanceFromNode( selectedNode, 0 );
		Iterator<DNVEdge> edges;
		queue.add( selectedNode );
		DNVNode tempNode;
		float actualDistance;
		int maxDistance = 0;
		int distance;
		float maxActualDistance = 0;
		DNVEdge tempEdge;
		if( maxDepth > 0 )
		{
			while( queue.size() > 0 )
			{
				tempNode = queue.remove( 0 );
				distance = tempNode.getDistanceFromNodeWithId( selectedNode.getId() );
				actualDistance = tempNode.getActualDistanceFromSelectedNode();
				if( actualDistance > maxActualDistance )
				{
					maxActualDistance = actualDistance;
				}

				if( distance > maxDistance )
				{
					maxDistance = distance;
				}

				if( distance < maxDepth )
				{
					// From edges
					edges = tempNode.getFromEdges().iterator();
					while( edges.hasNext() )
					{
						tempEdge = edges.next();
						if( tempEdge.isVisible() )
						{
							addNode( selectedNode, distance + 1, actualDistance + tempEdge.getRestingDistance(), queue, tempEdge.getTo() );
						}
					}

					// To edges
					edges = tempNode.getToEdges().iterator();
					while( edges.hasNext() )
					{
						tempEdge = edges.next();
						if( tempEdge.isVisible() )
						{
							addNode( selectedNode, distance + 1, actualDistance + tempEdge.getRestingDistance(), queue, tempEdge.getFrom() );
						}
					}
				}
			}
		}
		bfsTimer.setEnd();
		if( Settings.DEBUG )
		{
			System.out.println( "Interpolation BFS took " + bfsTimer.getLastSegment( Timer.SECONDS ) + " seconds." );
		}

		// if( useActualDistance )
		// return Math.round( maxActualDistance );

		return maxDistance;
	}

	/**
	 * Adds the node.
	 * 
	 * @param selectedNode
	 *            the selected node
	 * @param distance
	 *            the distance
	 * @param actualDistance
	 *            the actual distance
	 * @param queue
	 *            the queue
	 * @param node
	 *            the node
	 */
	private static void addNode( DNVNode selectedNode, int distance, float actualDistance, List<DNVNode> queue, DNVNode node )
	{
		if( node.getDistanceFromNodeWithId( selectedNode.getId() ) > distance )
		{
			node.setDistanceFromSelectedNode( distance );
			node.setDistanceFromNode( selectedNode, distance );
			node.setActualDistanceFromSelectedNode( actualDistance );
			queue.add( node );
		}
	}

	/** The scalar1. */
	private static float scalar1 = 3;

	/** The scalar2. */
	private static float scalar2 = 2;

	/** The s curve low end. */
	private static float sCurveLowEnd = 0;

	/** The s curve high end. */
	private static float sCurveHighEnd = 1;

	/**
	 * Sets the weights.
	 * 
	 * @param graph
	 *            the graph
	 * @param level
	 *            the level
	 * @param maxDistance
	 *            the max distance
	 * @param useActualDistance
	 *            the use actual distance
	 * @param selectedNode
	 *            the selected node
	 */
	public static void setWeights( DNVGraph graph, Integer level, float maxDistance, boolean useActualDistance, DNVNode selectedNode )
	{
		setWeights( graph, level, maxDistance, scalar1, scalar2, sCurveLowEnd, sCurveHighEnd, useActualDistance, selectedNode );
	}

	/**
	 * Sets the weights.
	 * 
	 * @param graph
	 *            the graph
	 * @param level
	 *            the level
	 * @param maxDistance
	 *            the max distance
	 * @param sCurveLowEnd
	 *            the s curve low end
	 * @param sCurveHighEnd
	 *            the s curve high end
	 * @param useActualDistance
	 *            the use actual distance
	 * @param selectedNode
	 *            the selected node
	 */
	public static void setWeights( DNVGraph graph, Integer level, float maxDistance, float sCurveLowEnd, float sCurveHighEnd, boolean useActualDistance, DNVNode selectedNode )
	{
		setWeights( graph, level, maxDistance, scalar1, scalar2, sCurveLowEnd, sCurveHighEnd, useActualDistance, selectedNode );
	}

	/**
	 * Sets the weights.
	 * 
	 * @param graph
	 *            the graph
	 * @param level
	 *            the level
	 * @param maxDistance
	 *            the max distance
	 * @param scalar1
	 *            the scalar1
	 * @param scalar2
	 *            the scalar2
	 * @param sCurveLowEnd
	 *            the s curve low end
	 * @param sCurveHighEnd
	 *            the s curve high end
	 * @param useActualDistance
	 *            the use actual distance
	 * @param selectedNode
	 *            the selected node
	 */
	public static void setWeights( DNVGraph graph, Integer level, float maxDistance, float scalar1, float scalar2, float sCurveLowEnd, float sCurveHighEnd,
			boolean useActualDistance, DNVNode selectedNode )
	{
		Timer weightsTimer = new Timer( Timer.MILLISECONDS );
		DNVNode tempNode;
		float tempWeight;
		weightsTimer.setStart();
		Iterator<DNVNode> nodes = graph.getNodes( level ).iterator();
		float tempDistance;
		while( nodes.hasNext() )
		{
			tempNode = nodes.next();
			if( useActualDistance )
				tempDistance = tempNode.getActualDistanceFromSelectedNode();
			else
				tempDistance = tempNode.getDistanceFromNodeWithId( selectedNode.getId() );

			if( tempDistance != Integer.MAX_VALUE )
			{
				graph.addInterpolationNode( tempNode, level );
				tempWeight = 1.0f - ( ( tempDistance ) / ( maxDistance ) );
				// tempWeight = 1.0f / (tempDistance + 1);
				// System.out.println( "1.0 - (" +
				// tempNode.getDistanceFromSelectedNode() + " / " + maxDistance
				// + ") = " + tempWeight );
				if( tempWeight > sCurveLowEnd )
				{
					// tempWeight = ( tempWeight - sCurveLowEnd ) / (
					// sCurveHighEnd - sCurveLowEnd );
					// tempWeight = scalar1 * tempWeight * tempWeight - scalar2
					// * tempWeight * tempWeight * tempWeight;
					tempNode.setInterpolationWeight( selectedNode.getId(), tempWeight );
				}
			}
			else
			{
				tempWeight = 0;
				tempNode.setInterpolationWeight( selectedNode.getId(), tempWeight );
			}
		}

		weightsTimer.setEnd();
		if( Settings.DEBUG )
		{
			System.out.println( "Interpolation set weights took " + weightsTimer.getLastSegment( Timer.SECONDS ) + " seconds." );
		}
	}

	/**
	 * Apply function.
	 * 
	 * @param selectedNode
	 *            the selected node
	 * @param pb
	 *            the pb
	 * @param graph
	 *            the graph
	 * @param movement
	 *            the movement
	 * @param level
	 *            the level
	 * @param temperature
	 *            the temperature
	 */
	public static void applyFunction( DNVNode selectedNode, PaintBean pb, DNVGraph graph, Vector2D movement, Integer level, float temperature )
	{
		Timer applyTimer = new Timer( Timer.MILLISECONDS );
		applyTimer.setStart();
		Timer moveTimer = new Timer( Timer.MILLISECONDS );
		if( selectedNode != null )
		{
			List<DNVNode> nodes = graph.getInterpolationList( level );
			DNVNode tempNode;
			Vector2D tempMove = new Vector2D();
			for( int i = 0; i < nodes.size(); i++ )
			{
				tempNode = nodes.get( i );
				if( tempNode.getDistanceFromNodeWithId( selectedNode.getId() ) != Integer.MAX_VALUE && !tempNode.isSelected() && selectedNode != null )
				{
					tempMove.set( movement );
					if( tempNode != null && selectedNode != null )
					{
						tempMove.dotProduct( tempNode.getInterpolationWeight( selectedNode.getId() ) );
					}

					// System.out.println(
					// tempNode.getDistanceFromSelectedNode() +
					// " : " + tempNode.getInterpolationWeight() + " : " +
					// tempMove
					// + " : " + tempMove.length() );
					moveTimer.setStart();
					tempNode.move( tempMove, true, false );
					moveTimer.setEnd();
				}
			}

			if( pb.isAvoidNodeOverlap() )
			{
//				int count = 0;
				float xRatio = (float)( pb.getWidth() / GraphFunctions.getGraphWidth( nodes, true ) );
				float yRatio = (float)( pb.getHeight() / GraphFunctions.getGraphHeight( nodes, true ) );
				// System.out.println( "cursors " + activeCursors.size() );
				// double nodeSize = 36;
				double nodeSize = ImageRenderer.getNodeWidth( pb.getNodeSize(), pb.getMinX(), pb.getMaxX(), 1 ) * 2;
				// System.out.println( "nodeSize" + nodeSize );
				Vector2D lastMove = movement;

				if( selectedNode != null && lastMove.length() > 0 )
				{
					// System.out.println( "distances " +
					// selectedNode.getDistances().size() );
					for( Integer distance : selectedNode.getDistances() )
					{
						Map<Integer, DNVNode> nodesAtDistance = selectedNode.getNodesAtDistance( distance );

						if( nodesAtDistance.size() > 0 )
						{
							Map<String, List<DNVNode>> nodesAtPosition = new HashMap<String, List<DNVNode>>();

							for( DNVNode node : nodesAtDistance.values() )
							{
								String key = getKey( node, nodeSize, pb );
								List<DNVNode> theNodes = nodesAtPosition.get( key );
								if( theNodes == null )
								{
									theNodes = new ArrayList<DNVNode>();
									nodesAtPosition.put( key, theNodes );
								}
								theNodes.add( node );
							}

							for( DNVNode node : nodesAtDistance.values() )
							{
								if( node != selectedNode )
								{
									for( DNVNode node2 : nodesAtPosition.get( getKey( node, nodeSize, pb ) ) )
									{
										if( node2 != node )
										{
//											count++;

											int nodeWidth = ImageRenderer.getNodeWidth( pb.getNodeSize(), pb.getMinX(), pb.getMaxX(), 1 );
											Vector2D difference = pb.getScreenPosDifference( node, node2 );
											float randomSign = getRandomSign();
											Vector2D lastMoveRotated = new Vector2D( randomSign * -1 * lastMove.getY(), randomSign * lastMove.getX() );
											if( difference.length() == 0 )
											{
												difference.set( lastMoveRotated );
												difference.normalize();
											}
											float overlap = pb.getOverlapAmount( node, node2, nodeWidth, difference );
											if( overlap > 0 )
											{
												difference.normalize();
												Vector2D direction = difference.dotProduct( overlap ).dotProduct( 0.1f );

												float dotProduct = direction.dotProduct( lastMoveRotated ) / lastMoveRotated.length();

												Vector2D projection = lastMoveRotated.normalize().dotProduct( dotProduct );

												projection.setX( projection.getX() / xRatio );
												projection.setY( projection.getY() / yRatio );

												if( node.hasAttribute( "align_movement" ) )
												{
													Vector2D previousMovement = (Vector2D)node.getAttribute( "align_movement" );
													if( previousMovement != null )
													{
														projection.add( previousMovement );
													}
												}
												node.setAttribute( "align_movement", projection );
											}
										}
									}
								}
							}
						}
					}
				}

				// System.out.println( "graph size:" + graph.getNodes( level
				// ).size() );
				// System.out.println( "iterations:" + count );
				for( DNVNode node : graph.getNodes( level ) )
				{
					node.removeAttribute( "screenPosition" );
					if( node.hasAttribute( "align_movement" ) )
					{
						movement = (Vector2D)node.getAttribute( "align_movement" );
						// movement = ImageRenderer.transformScreenToWorld(
						// movement, pb );
						node.move( movement, false, false );
						node.removeAttribute( "align_movement" );
					}
				}
			}
		}

		applyTimer.setEnd();
		if( Settings.DEBUG )
		{
			System.out.println( "Interpolation apply function took " + applyTimer.getLastSegment( Timer.SECONDS ) + " seconds." );
			System.out.println( "Interpolation move node took " + moveTimer.getTotalTime( Timer.SECONDS ) + " seconds." );
		}
	}

	private static String getKey( DNVNode node, double nodeSize, PaintBean pb )
	{
		Vector2D screenPosition = getScreenPosition( node, pb );

		return "" + (int)Math.round( screenPosition.getX() / nodeSize ) + "," + (int)Math.round( screenPosition.getY() / nodeSize );
	}

	private static Vector2D getScreenPosition( DNVNode node, PaintBean pb )
	{
		Vector2D screenPos;
		if( node.hasAttribute( "screenPosition" ) )
		{
			screenPos = (Vector2D)node.getAttribute( "screenPosition" );
		}
		else
		{
			screenPos = ImageRenderer.transformPosition( pb, node.getPosition() );
			node.setAttribute( "screenPosition", screenPos );
		}

		return screenPos;
	}

	private static float getRandomSign()
	{
		if( Math.random() < 0.5 )
			return -1;

		return 1;
	}

	/**
	 * Gets the scalar1.
	 * 
	 * @return the scalar1
	 */
	public static float getScalar1()
	{
		return scalar1;
	}

	/**
	 * Sets the scalar1.
	 * 
	 * @param scalar1
	 *            the new scalar1
	 */
	public static void setScalar1( float scalar1 )
	{
		InterpolationMethod.scalar1 = scalar1;
	}

	/**
	 * Gets the scalar2.
	 * 
	 * @return the scalar2
	 */
	public static float getScalar2()
	{
		return scalar2;
	}

	/**
	 * Sets the scalar2.
	 * 
	 * @param scalar2
	 *            the new scalar2
	 */
	public static void setScalar2( float scalar2 )
	{
		InterpolationMethod.scalar2 = scalar2;
	}

	/**
	 * Gets the s curve low end.
	 * 
	 * @return the s curve low end
	 */
	public static float getSCurveLowEnd()
	{
		return sCurveLowEnd;
	}

	/**
	 * Sets the s curve low end.
	 * 
	 * @param curveLowEnd
	 *            the new s curve low end
	 */
	public static void setSCurveLowEnd( float curveLowEnd )
	{
		sCurveLowEnd = curveLowEnd;
	}

	/**
	 * Gets the s curve high end.
	 * 
	 * @return the s curve high end
	 */
	public static float getSCurveHighEnd()
	{
		return sCurveHighEnd;
	}

	/**
	 * Sets the s curve high end.
	 * 
	 * @param curveHighEnd
	 *            the new s curve high end
	 */
	public static void setSCurveHighEnd( float curveHighEnd )
	{
		sCurveHighEnd = curveHighEnd;
	}

	/**
	 * Increase s curve low end.
	 */
	public static void increaseSCurveLowEnd()
	{
		sCurveLowEnd += 0.01;
		if( sCurveLowEnd > sCurveHighEnd )
			sCurveLowEnd -= 0.01;

		System.out.println( "SCurve Low End: " + sCurveLowEnd );
	}

	/**
	 * Decrease s curve low end.
	 */
	public static void decreaseSCurveLowEnd()
	{
		sCurveLowEnd -= 0.01;
		if( sCurveLowEnd < 0 )
			sCurveLowEnd = 0;

		System.out.println( "SCurve Low End: " + sCurveLowEnd );
	}

	/**
	 * Increase s curve high end.
	 */
	public static void increaseSCurveHighEnd()
	{
		sCurveHighEnd += 0.01;
		if( sCurveHighEnd > 1 )
			sCurveHighEnd = 1;

		System.out.println( "SCurve High End: " + sCurveHighEnd );
	}

	/**
	 * Decrease s curve high end.
	 */
	public static void decreaseSCurveHighEnd()
	{
		sCurveHighEnd -= 0.01;
		if( sCurveHighEnd < sCurveLowEnd )
			sCurveHighEnd += 0.01;

		System.out.println( "SCurve High End: " + sCurveHighEnd );
	}

	@Override
	public Vector2D performInteraction( PaintBean pb, DNVGraph graph, int width, int height, double minX, double minY, double maxX, double maxY, int mouseUpX, int mouseUpY,
			boolean sameNode, int level, double globalMinX, double globalMaxX, double globalMinY, double globalMaxY, DNVNode selectedNode, boolean released )
	{
		Timer interpolationTimer = new Timer( Timer.MILLISECONDS );
		interpolationTimer.setStart();
		Map<Integer, DNVNode> selectedNodes = graph.getSelectedNodes( level );

		// transform mouseUp from screen to world (new x = 0)
		Vector2D mouseUpWorld = ImageRenderer.transformScreenToWorld( mouseUpX, mouseUpY, minX, maxX, minY, maxY, globalMinX, globalMaxX, globalMinY, globalMaxY, width, height );

		Vector2D zeroPixels = ImageRenderer.transformScreenToWorld( 0, 0, minX, maxX, minY, maxY, globalMinX, globalMaxX, globalMinY, globalMaxY, width, height );

		Vector2D fivePixels = ImageRenderer.transformScreenToWorld( 5, 5, minX, maxX, minY, maxY, globalMinX, globalMaxX, globalMinY, globalMaxY, width, height );

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

					InteractionFunctions.moveNode( node, movement );
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

		return movement;
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

		InterpolationMethod.setWeights( graph, level, maxDepth, (float)pb.getCurveMin(), (float)pb.getCurveMax(), pb.isInterpolationMethodUseActualEdgeDistance(), node );
	}

	public static final String LABEL = "Interpolation Method";

	@Override
	public String getLabel()
	{
		return LABEL;
	}
}
