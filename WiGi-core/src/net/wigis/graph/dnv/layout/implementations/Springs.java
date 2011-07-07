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

package net.wigis.graph.dnv.layout.implementations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.layout.interfaces.TimeLimitedLayoutInterface;
import net.wigis.graph.dnv.utilities.GraphFunctions;
import net.wigis.graph.dnv.utilities.Statistics;
import net.wigis.graph.dnv.utilities.Timer;
import net.wigis.graph.dnv.utilities.Vector2D;

// TODO: Auto-generated Javadoc
/**
 * The Class Springs.
 * 
 * @author Brynjar Gretarsson
 */
public class Springs implements TimeLimitedLayoutInterface
{

	/** The graph. */
	private DNVGraph graph;

	/** The level. */
	private Integer level;

	/** The active. */
	private boolean active = true;

	/** The pull to original position. */
	private static boolean pullToOriginalPosition = false;

	/**
	 * Logger.
	 * 
	 * @param graph
	 *            the graph
	 * @param level
	 *            the level
	 * @param initializePositions
	 *            the initialize positions
	 */
	// // private static Log logger = LogFactory.getLog( Springs.class );

	public Springs()
	{		
	}
	
	public Springs( DNVGraph graph, Integer level, boolean initializePositions )
	{
		this.graph = graph;
		setLevel( level );
		graph.setAllNodesActive();
		if( initializePositions )
		{
			initializePositions();
		}
	}

	/**
	 * Toggle active.
	 */
	public void toggleActive()
	{
		active = !active;
	}

	/**
	 * Initialize positions.
	 */
	private void initializePositions()
	{
		GraphFunctions.initializePositions( graph );
	}

	// private static float boundingBoxSize = 1000000;
	// private static BoundingBox bb = new BoundingBox( -boundingBoxSize,
	// -boundingBoxSize, -boundingBoxSize, boundingBoxSize, boundingBoxSize,
	// boundingBoxSize );
	// private int count = 0;

	// private Vector2D previousLastAverage = new Vector2D();
	// private Vector2D lastAverage = new Vector2D();
	// private float lastAverage2 = 0;
	// private int detail = 8;
	/** The min overall force. */
	private float minOverallForce = Float.POSITIVE_INFINITY;

	/** The overall force. */
	private float overallForce;

	/** The automatic level jump. */
	private boolean automaticLevelJump = false;

	/** The coll. */
	private Collection<DNVNode> coll = new ArrayList<DNVNode>();

	// private Collection<DNVNode> coll2 = new ArrayList<DNVNode>();

	/**
	 * Move.
	 */
	public void move()
	{
		if( active )
		{
			synchronized( graph )
			{
				// previousLastAverage = lastAverage;
				// lastAverage = forceStrength.getVectorSum();
				// forceStrength.reset();
				// lastAverage2 = numberRepelled.getAverage();
				// numberRepelled.reset();

				// octreeTimer.setStart();
				// prepareOctree( graph, level );
				// octreeTimer.setEnd();

				// DNVNode tempDNV;
				// int graphSize = graph.getGraphSize( level );
				// centerOfGravity = GraphFunctions.getCenterOfGravity(
				// graph.getNodes( level ).iterator() );
				// System.out.println( centerOfGravity );

				// float tempDiff = 0;
				float maxDiff = 0;
				// totalAccumulateTimer.setStart();
				coll.clear();
				coll.addAll( graph.getActiveNodes( level ) );
				Iterator<DNVNode> nodes = coll.iterator();
				accumulateForces( graph, nodes, level );
				// totalAccumulateTimer.setEnd();

				// totalApplyTimer.setStart();
				nodes = coll.iterator();
				applyForces( nodes );
				// totalApplyTimer.setEnd();

				// Do one iteration of the layout algorithm for the subnodes of
				// a random node
				int random = (int)( Math.random() * graph.getGraphSize( level ) );
				DNVNode tempNode = graph.getNodes( level ).get( random );
				tempNode.iterateSubGraph();

				overallForce = forceStrength.getVectorSum().length();
				if( overallForce < minOverallForce )
					minOverallForce = overallForce;
				if( automaticLevelJump && graph.getActiveNodes( level ).size() < graph.getGraphSize( level ) / 5.0 )
				{
					if( level > 0 )
					{
						resetTimers();
					}
					else
					{
						// active = false;
						// display.setInformation( "  Layout complete" );
					}
				}

				if( graph.getActiveNodes( level ).size() > 0 )
					updateConditions( maxDiff );
			}
		}
	}

	/*
	 * public static void prepareOctree( DNVGraph graph, int level ) {
	 * Map<Integer,Octree> octrees = graph.getOctrees(); Octree octree =
	 * octrees.get( level ); if( octree == null ) { octree = new Octree( null );
	 * octrees.put( level, octree ); }
	 * 
	 * if( !octree.updateTree() ) { octree.buildTree( bb, 8, graph.getNodes(
	 * level ), 2.0f ); } }
	 */
	/**
	 * Apply forces.
	 * 
	 * @param nodes
	 *            the nodes
	 */
	public static void applyForces( Iterator<DNVNode> nodes )
	{
		DNVNode tempDNV;
		// float tempDiff;
		while( nodes.hasNext() )
		{
			tempDNV = nodes.next();
			forceStrength.add( tempDNV.getForce() );
			if( !tempDNV.isSelected() )
			{
				tempDNV.applyForce( 0.7f, true );
			}
		}
	}

	/**
	 * Accumulate forces.
	 * 
	 * @param graph
	 *            the graph
	 * @param nodes
	 *            the nodes
	 * @param level
	 *            the level
	 */
	public static void accumulateForces( DNVGraph graph, Iterator<DNVNode> nodes, int level )
	{
		DNVNode tempDNV;
		while( nodes.hasNext() )
		{
			tempDNV = nodes.next();
			accumulateForce( graph, tempDNV, level );
		}
	}

	/** The gravity_intensity. */
	private static float gravity_intensity = 0.0001f;
	// private static Timer octreeTimer = new Timer( Timer.NANOSECONDS );
	// private static Timer totalAccumulateTimer = new Timer( Timer.NANOSECONDS
	// );
	// private static Timer totalApplyTimer = new Timer( Timer.NANOSECONDS );
	// private static Timer accumulateTimer = new Timer( Timer.NANOSECONDS );
	// private static Timer springForceTimer = new Timer( Timer.NANOSECONDS );
	// private static Timer repellingTimer = new Timer( Timer.NANOSECONDS );
	// private static Timer repellingOctreeTimer = new Timer( Timer.NANOSECONDS
	// );
	// private static Timer centerTimer = new Timer( Timer.NANOSECONDS );
	// private static Timer parentTimer = new Timer( Timer.NANOSECONDS );
	/** The number repelled. */
	private static Statistics numberRepelled = new Statistics();

	/** The force strength. */
	private static Statistics forceStrength = new Statistics();

	/** The MI n_ strength. */
	private static float MIN_STRENGTH = 1.3f;

	/** The MI n_ sprin g_ strength. */
	private static float MIN_SPRING_STRENGTH = 0.5f;
	// private static final int REPEL_SIBLINGS_ONLY = 0;
	/** The Constant REPEL_ALL. */
	private static final int REPEL_ALL = 1;

	/** The Constant repellingMethods. */
	private static final String[] repellingMethods = { "Siblings only", "All nodes" };

	/** The repelling method. */
	private static int repellingMethod = REPEL_ALL;

	/**
	 * Accumulate force.
	 * 
	 * @param graph
	 *            the graph
	 * @param dnvNode
	 *            the dnv node
	 * @param level
	 *            the level
	 */
	private static void accumulateForce( DNVGraph graph, DNVNode dnvNode, Integer level )
	{
		// accumulateTimer.setStart();
		if( pullToOriginalPosition )
		{
			dnvNode.generateForceToOriginalPosition();
		}
		/*
		 * else if( dnvNode.getParentNode().getFirstChild() == dnvNode ) {
		 * dnvNode.setPosition( dnvNode.getParentNode().getPosition(), use3D );
		 * }
		 */else
		{

//			if( dnvNode.getSiblings() != null && dnvNode.getSiblings().size() == 1 )
//			{
//				dnvNode.setForce( new Vector2D( 0, 0 ) );
//				if( dnvNode.getParentNode() != null )
//					dnvNode.setPosition( dnvNode.getParentNode().getPosition() );
//				graph.setNodeInactive( level, dnvNode.getId() );
//			}

			List<DNVEdge> fromEdges = dnvNode.getFromEdges();
			List<DNVEdge> toEdges = dnvNode.getToEdges();
			// DNVNode tempNeighbor;
			Vector2D tempForce = null;
			Vector2D source = dnvNode.getPosition();

			// Accumulate Spring Force
			// springForceTimer.setStart();
			tempForce = accumulateEdgeForce( graph, dnvNode, level, fromEdges, toEdges, tempForce, source );
			// springForceTimer.setEnd();

			List<DNVNode> allNodes;

			// Accumulate Repelling Force
			// repellingTimer.setStart();

			// if( graphSize > 2 )
			// {
			// if( repellingTimer.getAverageTime( Timer.MILLISECONDS ) > 0.3 &&
			// repellingMethod == REPEL_ALL )
			// toggleRepelMethod();

			if( repellingMethod == REPEL_ALL || level.equals( graph.getMaxLevel() ) )
			{
				// repellingOctreeTimer.setStart();
				// dnvNode.updateRepelledNodes();
				// numberRepelled.add( dnvNode.getRepelledNodes().size() );
				// repellingOctreeTimer.setEnd();
				// allNodes = dnvNode.getRepelledNodes();
				allNodes = graph.getNodes( level );
			}
			else
			{
				// if( repellingMethod == REPEL_SIBLINGS_ONLY )
				allNodes = dnvNode.getSiblings();
			}
			
			tempForce = accumulateRepellingForce( graph, dnvNode, level, tempForce, source, allNodes );
			// }

			// repellingTimer.setEnd();

			Vector2D centerOfGravity = graph.getCenterOfGravity();
			// accumulate force towards center of gravity
			// centerTimer.setStart();
			accumulateForceToCenterOfGravity( dnvNode, tempForce, source, centerOfGravity );
			// centerTimer.setEnd();

			// accumulate force towards the position of parent node
			/*
			 * parentTimer.setStart(); DNVNode tempNode =
			 * dnvNode.getParentNode(); if( tempNode != null &&
			 * dnvNode.getLevel() < graph.getMaxLevel() ) { Vector2D center =
			 * tempNode.getPosition(); tempForce.setX( center.getX() -
			 * source.getX() ); tempForce.setY( center.getY() - source.getY() );
			 * tempForce.setZ( center.getZ() - source.getZ() ); float distance =
			 * tempForce.length(); // tempForce.dotProduct( gravity_intensity *
			 * 100.0 * dnvNode.getNeighbors().size() ); tempForce.dotProduct(
			 * gravity_intensity * 2.0f * distance ); dnvNode.addForce(
			 * tempForce ); } parentTimer.setEnd();
			 */
		}

		// accumulateTimer.setEnd();
	}

	/**
	 * Accumulate force to center of gravity.
	 * 
	 * @param dnvNode
	 *            the dnv node
	 * @param tempForce
	 *            the temp force
	 * @param source
	 *            the source
	 * @param centerOfGravity
	 *            the center of gravity
	 */
	private static void accumulateForceToCenterOfGravity( DNVNode dnvNode, Vector2D tempForce, Vector2D source, Vector2D centerOfGravity )
	{
		if( tempForce == null )
			tempForce = new Vector2D();

		tempForce.setX( centerOfGravity.getX() - source.getX() );
		tempForce.setY( centerOfGravity.getY() - source.getY() );

		tempForce.dotProduct( gravity_intensity * tempForce.length() );
		dnvNode.addForce( tempForce );
	}

	/**
	 * Accumulate repelling force.
	 * 
	 * @param graph
	 *            the graph
	 * @param dnvNode
	 *            the dnv node
	 * @param level
	 *            the level
	 * @param tempForce
	 *            the temp force
	 * @param source
	 *            the source
	 * @param allNodes
	 *            the all nodes
	 * @return the vector2 d
	 */
	private static Vector2D accumulateRepellingForce( DNVGraph graph, DNVNode dnvNode, Integer level, Vector2D tempForce, Vector2D source,
			List<DNVNode> allNodes )
	{
		DNVNode tempNode;
		for( int i = 0; i < allNodes.size(); i++ )
		{
			tempNode = allNodes.get( i );
			if( tempNode != dnvNode /* && !tempNode.isAnchor() */)
			{
				tempForce = getRepellingForce( tempNode.getPosition(), source );
				tempForce.dotProduct( tempNode.getMass() );
				if( tempForce.length() > MIN_STRENGTH && tempNode.canRevive() )
					graph.setNodeActive( level, tempNode.getId(), tempNode );
				dnvNode.addForce( tempForce );
			}
		}
		return tempForce;
	}

	/**
	 * Accumulate edge force.
	 * 
	 * @param graph
	 *            the graph
	 * @param dnvNode
	 *            the dnv node
	 * @param level
	 *            the level
	 * @param fromEdges
	 *            the from edges
	 * @param toEdges
	 *            the to edges
	 * @param tempForce
	 *            the temp force
	 * @param source
	 *            the source
	 * @return the vector2 d
	 */
	private static Vector2D accumulateEdgeForce( DNVGraph graph, DNVNode dnvNode, Integer level, List<DNVEdge> fromEdges, List<DNVEdge> toEdges,
			Vector2D tempForce, Vector2D source )
	{
		DNVNode tempNeighbor;
		DNVEdge tempEdge;
		for( int i = 0; i < fromEdges.size(); i++ )
		{
			tempEdge = fromEdges.get( i );
			tempNeighbor = tempEdge.getTo();
			tempForce = getSpringForce( tempNeighbor.getPosition(), source, tempEdge.getRestingDistance(), tempEdge.getK() );
			if( tempForce.length() > MIN_SPRING_STRENGTH && tempNeighbor.canRevive() )
				graph.setNodeActive( level, tempNeighbor.getId(), tempNeighbor );
			dnvNode.addForce( tempForce );
		}

		for( int i = 0; i < toEdges.size(); i++ )
		{
			tempEdge = toEdges.get( i );
			tempNeighbor = tempEdge.getFrom();
			tempForce = getSpringForce( tempNeighbor.getPosition(), source, tempEdge.getRestingDistance(), tempEdge.getK() );
			if( tempForce.length() > MIN_SPRING_STRENGTH && tempNeighbor.canRevive() )
				graph.setNodeActive( level, tempNeighbor.getId(), tempNeighbor );
			dnvNode.addForce( tempForce );
		}
		return tempForce;
	}

	// private float round( float number )
	// {
	// return round( number, 3 );
	// }

	// private float round( float number, int decimals )
	// {
	// float divider = (float)Math.pow( 10.0f, decimals );
	// return Math.round( number * divider ) / divider;
	// }

	/**
	 * Prints the timers.
	 */
	public void printTimers()
	{
	// float divideBy = round( accumulateTimer.getAverageTime(
	// Timer.MILLISECONDS ) );
	// float numberOfNodes = graph.getGraphSize( level );
	// System.out.println();
	// System.out.println( "Octree      : Average time: " + round(
	// octreeTimer.getAverageTime( Timer.MILLISECONDS ) ) + " ms. Total time: "
	// + round( octreeTimer.getTotalTime( Timer.MILLISECONDS ) ) +
	// " ms. Number of nodes:" + numberOfNodes + " Average/nodes: " + round(
	// octreeTimer.getAverageTime( Timer.MILLISECONDS ) / numberOfNodes ) );
	// System.out.println( "Total Accumu: Average time: " + round(
	// totalAccumulateTimer.getAverageTime( Timer.MILLISECONDS ) ) +
	// " ms. Total time: " + round( totalAccumulateTimer.getTotalTime(
	// Timer.MILLISECONDS ) ) + " ms. Number of nodes:" + numberOfNodes +
	// " Average/nodes: " + round( totalAccumulateTimer.getAverageTime(
	// Timer.MILLISECONDS ) / numberOfNodes ) );
	// System.out.println( "Total Apply : Average time: " + round(
	// totalApplyTimer.getAverageTime( Timer.MILLISECONDS ) ) +
	// " ms. Total time: " + round( totalApplyTimer.getTotalTime(
	// Timer.MILLISECONDS ) ) + " ms. Number of nodes:" + numberOfNodes +
	// " Average/nodes: " + round( totalApplyTimer.getAverageTime(
	// Timer.MILLISECONDS ) / numberOfNodes ) );
	// System.out.println();
	// System.out.println( "Accumulate  : Average time: " + round(
	// accumulateTimer.getAverageTime( Timer.MILLISECONDS ) ) +
	// " ms. Total time: " + round( accumulateTimer.getTotalTime(
	// Timer.MILLISECONDS ) ) + " ms. Number of calls:" + round(
	// accumulateTimer.getNumberOfSegments() ) + " Percentage: " +
	// accumulateTimer.getAverageTime( Timer.MILLISECONDS ) / divideBy );
	// System.out.println( "Spring Force: Average time: " + round(
	// springForceTimer.getAverageTime( Timer.MILLISECONDS ) ) +
	// " ms. Total time: " + round( springForceTimer.getTotalTime(
	// Timer.MILLISECONDS ) ) + " ms. Number of calls:" + round(
	// springForceTimer.getNumberOfSegments() ) + " Percentage: " +
	// springForceTimer.getAverageTime( Timer.MILLISECONDS ) / divideBy );
	// System.out.println( "Repelling   : Average time: " + round(
	// repellingTimer.getAverageTime( Timer.MILLISECONDS ) ) +
	// " ms. Total time: " + round( repellingTimer.getTotalTime(
	// Timer.MILLISECONDS ) ) + " ms. Number of calls:" + round(
	// repellingTimer.getNumberOfSegments() ) + " Percentage: " +
	// repellingTimer.getAverageTime( Timer.MILLISECONDS ) / divideBy );
	// System.out.println( "Repel Octree: Average time: " + round(
	// repellingOctreeTimer.getAverageTime( Timer.MILLISECONDS ) ) +
	// " ms. Total time: " + round( repellingOctreeTimer.getTotalTime(
	// Timer.MILLISECONDS ) ) + " ms. Number of calls:" + round(
	// repellingOctreeTimer.getNumberOfSegments() ) + " Percentage: " +
	// repellingOctreeTimer.getAverageTime( Timer.MILLISECONDS ) / divideBy );
	// System.out.println( "Center      : Average time: " + round(
	// centerTimer.getAverageTime( Timer.MILLISECONDS ) ) + " ms. Total time: "
	// + round( centerTimer.getTotalTime( Timer.MILLISECONDS ) ) +
	// " ms. Number of calls:" + round( centerTimer.getNumberOfSegments() ) +
	// " Percentage: " + centerTimer.getAverageTime( Timer.MILLISECONDS ) /
	// divideBy );
	// System.out.println( "Parent      : Average time: " + round(
	// parentTimer.getAverageTime( Timer.MILLISECONDS ) ) + " ms. Total time: "
	// + round( parentTimer.getTotalTime( Timer.MILLISECONDS ) ) +
	// " ms. Number of calls:" + round( parentTimer.getNumberOfSegments() ) +
	// " Percentage: " + parentTimer.getAverageTime( Timer.MILLISECONDS ) /
	// divideBy );
	// System.out.println( "Repelled    : Average number of nodes: " + round(
	// (float)numberRepelled.getAverage() ) + " percentage : " + round(
	// (float)numberRepelled.getAverage() / graph.getGraphSize( level ) ) );
	// System.out.println( "Forces      : Average strength       : " + round(
	// overallForce ) + " min overall: " + round( minOverallForce ) );
	}

	/**
	 * Reset timers.
	 */
	public void resetTimers()
	{
		// octreeTimer.reset();
		// totalAccumulateTimer.reset();
		// totalApplyTimer.reset();
		// accumulateTimer.reset();
		// springForceTimer.reset();
		// repellingTimer.reset();
		// repellingOctreeTimer.reset();
		// centerTimer.reset();
		// parentTimer.reset();
		numberRepelled.reset();
		forceStrength.reset();
		minOverallForce = Float.POSITIVE_INFINITY;
	}

	/** The Constant repelling_intensity. */
	private static final float repelling_intensity = 1;

	/** The Constant distance_power. */
	private static final float distance_power = 2;

	/** The Constant TOLERANCE. */
	private static final float TOLERANCE = 0.001f;

	/**
	 * Gets the repelling force.
	 * 
	 * @param target
	 *            the target
	 * @param source
	 *            the source
	 * @return the repelling force
	 */
	private static Vector2D getRepellingForce( Vector2D target, Vector2D source )
	{
		float dx = source.getX() - target.getX();
		float dy = source.getY() - target.getY();

		Vector2D repellingForce = new Vector2D();
		repellingForce.setX( dx );
		repellingForce.setY( dy );
		float distance = repellingForce.length();

		if( distance < TOLERANCE )
		{
			repellingForce.setX( (float)Math.random() );
			repellingForce.setY( (float)Math.random() );
		}
		else
		{
			float intensity = repelling_intensity / ( distance * distance );
			// Normalize and Multiply by the intensity
			repellingForce.dotProduct( intensity / distance );
		}

		return repellingForce;
	}

	/**
	 * Gets the spring force.
	 * 
	 * @param source
	 *            the source
	 * @param target
	 *            the target
	 * @param restingDistance
	 *            the resting distance
	 * @param k
	 *            the k
	 * @return the spring force
	 */
	private static Vector2D getSpringForce( Vector2D source, Vector2D target, float restingDistance, float k )
	{
		float dx = source.getX() - target.getX();
		float dy = source.getY() - target.getY();

		Vector2D springForce = new Vector2D();
		springForce.setX( dx );
		springForce.setY( dy );
		float distance = springForce.length();

		if( distance < TOLERANCE )
		{
			// Set force to 0
			springForce.setX( (float)Math.random() );
			springForce.setY( (float)Math.random() );
		}
		else
		{
			springForce.normalize();
			float delta;
			float intensity;
			delta = distance;// - restingDistance;
			intensity = k * delta;
			springForce.dotProduct( intensity );
		}

		return springForce;
	}

	/** The processed edges. */
	Map<Integer, Float> processedEdges = new HashMap<Integer, Float>();

	/**
	 * Sets the level.
	 * 
	 * @param level
	 *            the new level
	 */
	public void setLevel( Integer level )
	{
		this.level = level;
		if( graph.getNodes( level ) != null )
		{
			// Map<Integer,Octree> octrees = graph.getOctrees();
			// octreeTimer.setStart();
			// Octree octree = octrees.get( level );
			// if( octree == null )
			// octree = new Octree( null );
			// octree.buildTree( bb, 8, graph.getNodes( level ), 2.0f );
			// octreeTimer.setEnd();

			Vector2D centerOfGravity = GraphFunctions.getCenterOfGravity( graph.getNodes( level ).iterator() );
			graph.setCenterOfGravity( centerOfGravity );
			initializeConditions();
		}
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
	 * Sets the graph and level.
	 * 
	 * @param graph
	 *            the graph
	 * @param level
	 *            the level
	 * @param initializePositions
	 *            the initialize positions
	 */
	public void setGraphAndLevel( DNVGraph graph, Integer level, boolean initializePositions )
	{
		// Map<Integer,Octree> octrees = new HashMap<Integer,Octree>();
		// graph.setOctrees( octrees );
		this.graph = graph;
		setLevel( level );
		// if( initializePositions )
		// initializePositions();
		resetTimers();
		graph.setAllNodesActive();
	}

	/**
	 * Sets the active.
	 * 
	 * @param active
	 *            the new active
	 */
	public void setActive( boolean active )
	{
		this.active = active;
	}

	/**
	 * Checks if is automatic level jump.
	 * 
	 * @return true, if is automatic level jump
	 */
	public boolean isAutomaticLevelJump()
	{
		return automaticLevelJump;
	}

	/**
	 * Sets the automatic level jump.
	 * 
	 * @param automaticLevelJump
	 *            the new automatic level jump
	 */
	public void setAutomaticLevelJump( boolean automaticLevelJump )
	{
		this.automaticLevelJump = automaticLevelJump;
		// display.setInformation( "  Automatic level jump : " +
		// isAutomaticLevelJump() );

	}

	/**
	 * Gets the repelling_intensity.
	 * 
	 * @return the repelling_intensity
	 */
	public static float getRepelling_intensity()
	{
		return repelling_intensity;
	}

	/**
	 * Gets the distance_power.
	 * 
	 * @return the distance_power
	 */
	public static float getDistance_power()
	{
		return distance_power;
	}

	/**
	 * Gets the mI n_ strength.
	 * 
	 * @return the mI n_ strength
	 */
	public static float getMIN_STRENGTH()
	{
		return MIN_STRENGTH;
	}

	/**
	 * Toggle repel method.
	 */
	public void toggleRepelMethod()
	{
		repellingMethod = ( repellingMethod + 1 ) % 2;
		System.out.println( "Repelling method : " + repellingMethods[repellingMethod] );
	}

	/**
	 * Checks if is pull to original position.
	 * 
	 * @return true, if is pull to original position
	 */
	public boolean isPullToOriginalPosition()
	{
		return pullToOriginalPosition;
	}

	/**
	 * Sets the pull to original position.
	 * 
	 * @param pullToOriginalPosition
	 *            the new pull to original position
	 */
	public void setPullToOriginalPosition( boolean pullToOriginalPosition )
	{
		Springs.pullToOriginalPosition = pullToOriginalPosition;
	}

	/**
	 * Update conditions.
	 * 
	 * @param maxDiff
	 *            the max diff
	 */
	private void updateConditions( float maxDiff )
	{

		if( maxDiff <= 0 )
		{
			if( maxDiff > -1 )
				maxDiff = -1;
			DNVNode.HIGHER_MIN -= 0.001 * maxDiff;
			DNVNode.MIN_FORCE -= 0.001 * maxDiff;
			MIN_STRENGTH -= 0.001 * maxDiff;
			MIN_SPRING_STRENGTH -= 0.001 * maxDiff;
		}
	}

	/**
	 * Initialize conditions.
	 */
	public void initializeConditions()
	{
		DNVNode.HIGHER_MIN = 1.0f;
		DNVNode.MIN_FORCE = 0.5f;
		MIN_STRENGTH = 1.3f;
		MIN_SPRING_STRENGTH = 0.5f;
	}

	/**
	 * Gets the level.
	 * 
	 * @return the level
	 */
	public Integer getLevel()
	{
		return level;
	}

	/**
	 * Runlayout.
	 * 
	 * @param graph
	 *            the graph
	 * @param level
	 *            the level
	 * @param maxSeconds
	 *            the max seconds
	 * @param initializePositions
	 *            the initialize positions
	 * @param layoutAllLevels
	 *            the layout all levels
	 */
	@Override
	public void runLayout( DNVGraph graph, int level, double maxSeconds, boolean initializePositions, boolean layoutAllLevels )
	{
		if( layoutAllLevels )
		{
			for( level = graph.getMaxLevel(); level >= 0; level-- )
			{
				layoutLevel( graph, level, maxSeconds, initializePositions );
			}
		}
		else
		{
			layoutLevel( graph, level, maxSeconds, initializePositions );
		}

		// springs.printTimers();
	}

	/**
	 * Layout level.
	 * 
	 * @param graph
	 *            the graph
	 * @param level
	 *            the level
	 * @param maxSeconds
	 *            the max seconds
	 * @param initializePositions
	 *            the initialize positions
	 */
	public static void layoutLevel( DNVGraph graph, int level, double maxSeconds, boolean initializePositions )
	{
		Springs springs = new Springs( graph, level, initializePositions );
		Timer timer = new Timer( Timer.MILLISECONDS );
		graph.setAllNodesActive();
		float totalTime = timer.getTotalTime( Timer.SECONDS );
		// float graphSize_5 = (graph.getGraphSize( level ) / 5.0f);
		graph.setAllNodesActive( level );
		int activeNodes = graph.getActiveNodes( level ).size();
		while( totalTime < maxSeconds && activeNodes > 0 )
		{
			timer.setStart();
			springs.move();
			timer.setEnd();
			activeNodes = graph.getActiveNodes( level ).size();
			totalTime = timer.getTotalTime( Timer.SECONDS );
		}
	}

	/** The Constant SPRING_LAYOUT. */
	public static final String LABEL = "Spring Layout";

	@Override
	public String getLabel()
	{
		return LABEL;
	}
}
