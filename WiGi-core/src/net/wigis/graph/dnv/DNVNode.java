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

package net.wigis.graph.dnv;

import java.awt.Image;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.context.FacesContext;

import net.wigis.graph.PaintBean;
import net.wigis.graph.RecommendationBean;
import net.wigis.graph.dnv.layout.Springs;
import net.wigis.graph.dnv.utilities.SortByFloatProperty;
import net.wigis.graph.dnv.utilities.Vector2D;
import net.wigis.graph.dnv.utilities.Vector3D;
import net.wigis.stats.StatsBean;
import net.wigis.svetlin.__Math;
import net.wigis.svetlin.__String;
import net.wigis.svetlin.__jsf;
import net.wigis.web.ContextLookup;
import blackbook.service.api.ResourceDecorator;

import com.google.code.facebookapi.FacebookException;

// TODO: Auto-generated Javadoc
/**
 * A node representation.
 * 
 * @author Brynjar Gretarsson
 * 
 */
public class DNVNode extends DNVEntity implements Serializable, Comparable<Object>
{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -702075942060618434L;

	/** The Constant MAX_FORCE. */
	private static final float MAX_FORCE = 5;

	/** The MI n_ force. */
	public static float MIN_FORCE = 0.005f;

	/** The HIGHE r_ min. */
	public static float HIGHER_MIN = 0.1f;

	// private static final float THRESHOLD = 2;

	/** The Constant NODE_SIZE. */
	public static final float NODE_SIZE = 1.0f;

	/** The Constant MAX_NODE_SIZE. */
	public static final float MAX_NODE_SIZE = 15.0f;

	// private Vector2D previousPosition = new Vector2D();
	/** The position. */
	private Vector2D position = new Vector2D();

	/** The original position. */
	private Vector2D originalPosition = null;

	/** The display position. */
	private Vector2D displayPosition = null;

	/** The icon. */
	private String icon = "";

	/** The icon image. */
	private Map<Integer, Image> iconImage = new HashMap<Integer, Image>();

	// private List<BBNode> neighborList = new ArrayList<BBNode>();
	/** The sub graph. */
	private SubGraph subGraph;

	/** The sub node ids. */
	private List<Integer> subNodeIds = new ArrayList<Integer>();

	/** The previous force strength. */
	private float previousForceStrength = 0;

	/** The force. */
	protected Vector2D force = new Vector2D( 0, 0 );
	// private Vector2D oldForce = new Vector2D( 0, 0 );
	/** The acceleration. */
	private Vector2D acceleration = new Vector2D( 0, 0 );

	/** The velocity. */
	private Vector2D velocity = new Vector2D( 0, 0 );

	/** The Constant DEFAULT_TIMESTEP. */
	private static final float DEFAULT_TIMESTEP = 0.6f;

	/** The time step. */
	private float timeStep[] = { DEFAULT_TIMESTEP / 2.0f, DEFAULT_TIMESTEP };

	/** The mass. */
	private float mass = 1.0f;

	// private boolean anchor = false;
	// private boolean selected = false;
	/** The positioned. */
	private boolean positioned = false;

	/** The fixed. */
	private boolean fixed = false;

	/** The expandable. */
	private boolean expandable = false;

	/** The hide label background. */
	private boolean hideLabelBackground = false;

	/** The curved label. */
	private boolean curvedLabel = false;

	/** The radius. */
	private float radius = NODE_SIZE;

	/** The distance from cluster. */
	private int distanceFromCluster = Integer.MAX_VALUE;

	// Interpolation method variables
	// This is the actual distance based on the edge lengths
	/** The actual distance from selected node. */
	private float actualDistanceFromSelectedNode = Integer.MAX_VALUE;
	// This is the number of hops
	/** The distance from selected node. */
	private int distanceFromSelectedNode = Integer.MAX_VALUE;

	/** The distance from node with id. */
	private Map<Integer, Integer> distanceFromNodeWithId = new HashMap<Integer, Integer>();

	/** The nodes at distance. */
	private Map<Integer, Map<Integer, DNVNode>> nodesAtDistance = new HashMap<Integer, Map<Integer, DNVNode>>();

	/** The interpolation weight. */
	private Map<Integer, Float> interpolationWeight = new HashMap<Integer, Float>();

	/** The parent node. */
	private DNVNode parentNode = null;

	/** The first child. */
	private DNVNode firstChild = null;

	/** The facebook can add. */
	private boolean facebookCanAdd = false;

	// Used for the facebook layout
	/** The distance from center node. */
	private float distanceFromCenterNode = Float.POSITIVE_INFINITY;

	/** The diameter of subnodes. */
	private float diameterOfSubnodes = 1;

	private Map<Integer, DNVEdge> edgesByNeighborId = new HashMap<Integer, DNVEdge>();

	/** The neighbors. */
	protected Map<Integer, DNVNode> neighbors = new HashMap<Integer, DNVNode>();

	/** The from edges. */
	protected Map<Integer, DNVEdge> fromEdges = new HashMap<Integer, DNVEdge>();

	/** The to edges. */
	protected Map<Integer, DNVEdge> toEdges = new HashMap<Integer, DNVEdge>();

	/** The selected neighbors. */
	protected Map<Integer, DNVNode> selectedNeighbors = new HashMap<Integer, DNVNode>();

	/** The visible neighbors. */
	protected Map<Integer, DNVNode> visibleNeighbors = new HashMap<Integer, DNVNode>();

	/** The visible from edges. */
	protected Map<Integer, DNVEdge> visibleFromEdges = new HashMap<Integer, DNVEdge>();

	/** The visible to edges. */
	protected Map<Integer, DNVEdge> visibleToEdges = new HashMap<Integer, DNVEdge>();

	// private Map<Integer, DNVNode> repelledNodes = new HashMap<Integer,
	// DNVNode>();

	// blackbook id - added by alex
	/** The decorator. */
	private ResourceDecorator decorator;

	/** The details expanded. */
	private boolean detailsExpanded = true;

	/** The position change callback. */
	private PositionChangeCallBack positionChangeCallback = null;

	/**
	 * Logger.
	 * 
	 * @param graph
	 *            the graph
	 */
	// // private static Log logger = LogFactory.getLog( DNVNode.class );

	
	public static void p(Object o)
	{
		System.out.println(o);
	}
	
	public static void pe(Object o)
	{
		System.err.println(o);
	}
	// alex
	public DNVNode()
	{
		super();
	}

	public DNVNode( DNVGraph graph )
	{
		super( graph );
		initialize( position, "", graph );
	}

	/**
	 * Instantiates a new dNV node.
	 * 
	 * @param decorator
	 *            the decorator
	 * @param graph
	 *            the graph
	 */
	public DNVNode( ResourceDecorator decorator, DNVGraph graph )
	{
		super( graph );
		this.decorator = decorator;
		setColor( decorator.getDatasourceColor() );
		this.bbId = decorator.getUri();
		initialize( position, decorator.getLabel(), graph );
	}

	/**
	 * Instantiates a new dNV node.
	 * 
	 * @param position
	 *            the position
	 * @param graph
	 *            the graph
	 */
	public DNVNode( Vector2D position, DNVGraph graph )
	{
		super( graph );
		initialize( position, "", graph );
	}

	/**
	 * Instantiates a new dNV node.
	 * 
	 * @param position
	 *            the position
	 * @param label
	 *            the label
	 * @param graph
	 *            the graph
	 */
	public DNVNode( Vector2D position, String label, DNVGraph graph )
	{
		super( graph );
		initialize( position, label, graph );
	}

	/**
	 * Instantiates a new dNV node.
	 * 
	 * @param level
	 *            the level
	 * @param size
	 *            the size
	 * @param position
	 *            the position
	 * @param graph
	 *            the graph
	 */
	public DNVNode( String level, String size, Vector2D position, DNVGraph graph )
	{
		super( graph );
		this.level = Integer.parseInt( level );
		this.position = position;
		this.radius = Float.parseFloat( size );
		this.mass = this.radius;

		initialize( this.position, "", graph );
	}

	/**
	 * Instantiates a new dNV node.
	 * 
	 * @param level
	 *            the level
	 * @param size
	 *            the size
	 * @param graph
	 *            the graph
	 */
	public DNVNode( String level, String size, DNVGraph graph )
	{
		super( graph );
		this.level = Integer.parseInt( level );
		this.radius = Float.parseFloat( size );
		this.mass = this.radius;

		initialize( this.position, "", graph );
	}

	/**
	 * Instantiates a new dNV node.
	 * 
	 * @param color
	 *            the color
	 * @param level
	 *            the level
	 * @param size
	 *            the size
	 * @param graph
	 *            the graph
	 */
	public DNVNode( Vector3D color, String level, String size, DNVGraph graph )
	{
		super( graph );
		this.level = Integer.parseInt( level );
		this.color = color;
		this.radius = Float.parseFloat( size );
		this.mass = this.radius;

		initialize( this.position, "", graph );
	}

	/**
	 * Instantiates a new dNV node.
	 * 
	 * @param color
	 *            the color
	 * @param level
	 *            the level
	 * @param size
	 *            the size
	 * @param position
	 *            the position
	 * @param graph
	 *            the graph
	 */
	public DNVNode( Vector3D color, String level, String size, Vector2D position, DNVGraph graph )
	{
		super( graph );
		this.level = Integer.parseInt( level );
		this.position = position;
		this.color = color;
		this.radius = Float.parseFloat( size );
		this.mass = this.radius;

		initialize( this.position, "", graph );
	}

	/**
	 * Instantiates a new dNV node.
	 * 
	 * @param line
	 *            the line
	 * @param graph
	 *            the graph
	 */
	public DNVNode( String line, DNVGraph graph )
	{
		super( graph );
		this.graph = graph;
		this.subGraph = new SubGraph( graph, level - 1 );
		deserialize( line );
	}

	/**
	 * Instantiates a new dNV node.
	 * 
	 * @param copy
	 *            the copy
	 * @param graph
	 *            the graph
	 */
	public DNVNode( DNVNode copy, DNVGraph graph )
	{
		super( graph );
		this.setLabel( copy.getLabel() );
		this.setBbId( copy.getBbId() );
		this.setRadius( copy.getRadius() );
		this.setColor( copy.getColor() );
		this.setOutlineColor( copy.getOutlineColor() );
		if( copy.getProperties() != null )
		{
			for( String key : copy.getProperties().keySet() )
			{
				this.setProperty( key, copy.getProperties().get( key ) );
			}
		}
	}

	/**
	 * Adds the from edge.
	 * 
	 * @param edge
	 *            the edge
	 */
	public void addFromEdge( DNVEdge edge )
	{
		// TODO: Changed this for better performance
		if( fromEdges.get( edge.getId() ) == null )
		{
			fromEdges.put( edge.getId(), edge );
		}
		if( edge.getTo() != null )
		{
			edgesByNeighborId.put( edge.getTo().getId(), edge );
		}
		if( edge.isVisible() )
		{
			visibleFromEdges.put( edge.getId(), edge );
			if( neighbors.get( edge.getTo().getId() ) == null )
			{
				neighbors.put( edge.getTo().getId(), edge.getTo() );
				// edgesByNeighborId.put( edge.getTo().getId(), edge );
			}
			if( edge.getTo().isVisible() )
			{
				visibleNeighbors.put( edge.getTo().getId(), edge.getTo() );
			}
		}
		// TODO: This is the more correct way
		/*
		 * if( !fromEdges.contains( edge ) ) { fromEdges.add( edge );
		 * 
		 * if( !neighbors.contains( edge.getTo() ) ) { neighbors.add(
		 * edge.getTo() ); edgesByNeighborId.put( edge.getTo().getId(), edge );
		 * } }
		 */
	}

	/**
	 * Adds the to edge.
	 * 
	 * @param edge
	 *            the edge
	 */
	public void addToEdge( DNVEdge edge )
	{
		// TODO: Changed this for better performance
		if( toEdges.get( edge.getId() ) == null )
		{
			toEdges.put( edge.getId(), edge );
		}
		if( edge.getFrom() != null )
		{
			edgesByNeighborId.put( edge.getFrom().getId(), edge );
		}
		if( edge.isVisible() )
		{
			visibleToEdges.put( edge.getId(), edge );
			if( neighbors.get( edge.getFrom().getId() ) == null )
			{
				neighbors.put( edge.getFrom().getId(), edge.getFrom() );
				// edgesByNeighborId.put( edge.getFrom().getId(), edge );
			}
			if( edge.getFrom().isVisible() )
			{
				visibleNeighbors.put( edge.getFrom().getId(), edge.getFrom() );
			}
		}
		// TODO: This is the more correct way
		/*
		 * if( !toEdges.contains( edge ) ) { toEdges.add( edge );
		 * 
		 * if( !neighbors.contains( edge.getFrom() ) ) { neighbors.add(
		 * edge.getFrom() ); edgesByNeighborId.put( edge.getFrom().getId(), edge
		 * ); } }
		 */
	}

	/**
	 * Initialize.
	 * 
	 * @param position
	 *            the position
	 * @param label
	 *            the label
	 * @param graph
	 *            the graph
	 */
	private void initialize( Vector2D position, String label, DNVGraph graph )
	{
		setPosition( position );
		setLabel( label );
		setGraph( graph );
		setVisible( true );
		// this.subGraph = new SubGraph( graph, level - 1 );
	}

	/**
	 * Gets the label.
	 * 
	 * @param distanceLabel
	 *            the distance label
	 * @return the label
	 */
	public String getLabel( boolean distanceLabel )
	{
		if( distanceLabel )
		{
			if( distanceFromSelectedNode != Integer.MAX_VALUE )
				return "" + distanceFromSelectedNode;

			return "";
		}

		// if( distanceFromSelectedNode >= 3 )
		// return "";

		return label;
	}

	/**
	 * Store current position.
	 */
	public void storeCurrentPosition()
	{
		displayPosition = new Vector2D( position );
		originalPosition = new Vector2D( position );
	}

	/**
	 * Gets the position.
	 * 
	 * @return the position
	 */
	public Vector2D getPosition()
	{
		return getPosition( false );
	}

	/**
	 * Gets the position.
	 * 
	 * @param display
	 *            the display
	 * @return the position
	 */
	public Vector2D getPosition( boolean display )
	{
		if( display && displayPosition != null && graph.isDisplayStoredPosition() )
			return displayPosition;

		return position;
	}

	/**
	 * Sets the position.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 */
	public void setPosition( float x, float y )
	{
		if( positionChangeCallback != null )
		{
			positionChangeCallback.moved( this.position.getX(), this.position.getY(), x, y );
		}

		position.setX( x );
		position.setY( y );

		if( originalPosition == null )
			originalPosition = new Vector2D( position );

		// handlePreviousPosition();
	}

	/**
	 * Sets the position.
	 * 
	 * @param position
	 *            the new position
	 */
	public void setPosition( Vector2D position )
	{
		setPosition( position.getX(), position.getY() );
	}

	/**
	 * Update color.
	 * 
	 * @param recursive
	 *            the recursive
	 */
	public void updateColor( boolean recursive )
	{
		color.setX( 0 );
		color.setY( 0 );
		color.setZ( 0 );
		List<DNVNode> subNodes = subGraph.getNodesList();
		for( int i = 0; i < subNodes.size(); i++ )
		{
			color.add( subNodes.get( i ).getColor() );
		}

		color.dotProduct( 1.0f / (float)subNodes.size() );

		if( recursive && parentNode != null )
			parentNode.updateColor( recursive );
	}

	/**
	 * Sets the color.
	 * 
	 * @param color
	 *            the color
	 * @param colorSubNodes
	 *            the color sub nodes
	 * @param colorParentNodes
	 *            the color parent nodes
	 */
	public void setColor( Vector3D color, boolean colorSubNodes, boolean colorParentNodes )
	{
		setColor( color );
		if( colorSubNodes )
		{
			List<DNVNode> subNodes = subGraph.getNodesList();
			for( int i = 0; i < subNodes.size(); i++ )
			{
				subNodes.get( i ).setColor( color, true, false );
			}
		}

		if( colorParentNodes && parentNode != null )
		{
			parentNode.updateColor( true );
		}
	}

	/*
	 * public void addNeighbor( BBNode node ) { if( !neighborList.contains( node
	 * ) ) neighborList.add( node ); }
	 * 
	 * public List<BBNode> getNeighborList() { return neighborList; }
	 */
	/**
	 * Sets the force.
	 * 
	 * @param force
	 *            the new force
	 */
	public void setForce( Vector2D force )
	{
		this.force.setX( force.getX() );
		this.force.setY( force.getY() );
	}

	/**
	 * Sets the force.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 */
	public void setForce( float x, float y )
	{
		this.force.setX( x );
		this.force.setY( y );
	}

	/**
	 * Gets the acceleration.
	 * 
	 * @return the acceleration
	 */
	public Vector2D getAcceleration()
	{
		return acceleration;
	}

	/**
	 * Gets the velocity.
	 * 
	 * @return the velocity
	 */
	public Vector2D getVelocity()
	{
		return velocity;
	}

	/*
	 * public void updateTimeStep() { if( timeStep < DEFAULT_TIMESTEP ) timeStep
	 * /= timeStepUpdate; }
	 */
	/**
	 * Move.
	 * 
	 * @param movement
	 *            the movement
	 * @param applyToSubnodes
	 *            the apply to subnodes
	 * @param applyToParent
	 *            the apply to parent
	 */
	public void move( Vector2D movement, boolean applyToSubnodes, boolean applyToParent )
	{
		setPosition( position.getX() + movement.getX(), position.getY() + movement.getY() );

		if( applyToSubnodes )
		{
			List<DNVNode> subNodes = subGraph.getNodesList();
			for( int i = 0; i < subNodes.size(); i++ )
			{
				subNodes.get( i ).move( movement, true, false );
			}
		}

		if( applyToParent && parentNode != null )
		{
			parentNode.reposition( true );
		}

	}

	/**
	 * Reposition.
	 * 
	 * @param recursive
	 *            the recursive
	 */
	public void reposition( boolean recursive )
	{
		Vector2D newPos = new Vector2D();
		List<DNVNode> subNodes = subGraph.getNodesList();
		for( int i = 0; i < subNodes.size(); i++ )
		{
			newPos.add( subNodes.get( i ).getPosition() );
		}

		newPos.dotProduct( 1.0f / (float)subNodes.size() );
		setPosition( newPos );

		if( recursive && parentNode != null )
			parentNode.reposition( recursive );

		if( originalPosition == null )
			originalPosition = new Vector2D( position );
	}

	/**
	 * Place node.
	 * 
	 * @param index
	 *            the index
	 * @param size
	 *            the size
	 * @param bigRadius
	 *            the big radius
	 * @param center
	 *            the center
	 * @param placeSubNodes
	 *            the place sub nodes
	 * @param maxLevel
	 *            the max level
	 */
	public void placeNode( int index, int size, float bigRadius, Vector2D center, boolean placeSubNodes, int maxLevel )
	{
		// float radians = 0.2 * index;
		// float radius = size / 10.0;
		// float z_pos = 1.0f * index;

		float bigRadians = 2.0f * (float)Math.PI / (float)size * index;

		float y_pos = 0;// Math.sin( radians );// * bigRadius;
		float x_pos = 0;// Math.cos( radians );// * bigRadius;

		// z_pos = (float)Math.cos( bigRadians ) * bigRadius;
		y_pos += Math.sin( bigRadians ) * bigRadius;

		Vector2D difference = new Vector2D( -position.getX(), -position.getY() );

		// if( level < maxLevel )
		setPosition( center.getX() + x_pos, center.getY() + y_pos );

		difference.add( position );

		if( placeSubNodes )
		{
			List<DNVNode> subNodes = subGraph.getNodesList();
			for( int i = 0; i < subNodes.size(); i++ )
			{
				subNodes.get( i ).move( difference, true, false );
			}
		}
	}

	/**
	 * Gets the overall area.
	 * 
	 * @param maxValues
	 *            the max values
	 * @param minValues
	 *            the min values
	 * @param recursive
	 *            the recursive
	 * @return the overall area
	 */
	public void getOverallArea( Vector2D maxValues, Vector2D minValues, boolean recursive )
	{
		DNVNode tempNode;
		Vector2D tempPosition;
		List<DNVNode> subNodes = subGraph.getNodesList();
		for( int i = 0; i < subNodes.size(); i++ )
		{
			tempNode = subNodes.get( i );
			tempPosition = tempNode.getPosition();
			if( tempPosition.getX() > maxValues.getX() )
				maxValues.setX( tempPosition.getX() );
			if( tempPosition.getY() > maxValues.getY() )
				maxValues.setY( tempPosition.getY() );

			if( tempPosition.getX() < minValues.getX() )
				minValues.setX( tempPosition.getX() );
			if( tempPosition.getY() < minValues.getY() )
				minValues.setY( tempPosition.getY() );

			if( recursive )
				tempNode.getOverallArea( maxValues, minValues, recursive );
		}
	}

	/**
	 * Gets the overall area.
	 * 
	 * @return the overall area
	 */
	public Vector2D getOverallArea()
	{
		Vector2D area = new Vector2D();
		Vector2D maxValues = new Vector2D( Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY );
		Vector2D minValues = new Vector2D( Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY );

		getOverallArea( maxValues, minValues, true );

		area.set( maxValues.subtract( minValues ) );

		return area;
	}

	/**
	 * Update edgelengths and mass.
	 * 
	 * @param processedEdges
	 *            the processed edges
	 * @param updateSubNodes
	 *            the update sub nodes
	 */
	public void updateEdgelengthsAndMass( Map<Integer, Float> processedEdges, boolean updateSubNodes )
	{
		float length;
		if( getTotalNumberOfSubNodes() > 1 )
		{
			Vector2D area = getOverallArea();
			length = area.length();// + getTotalNumberOfSubNodes() / 10.0;
		}
		else
		{
			length = DNVEdge.DEFAULT_RESTING_DISTANCE;
		}

		for( int i = 0; i < fromEdges.size(); i++ )
		{
			processEdge( processedEdges, length, fromEdges.get( i ) );
		}

		for( int i = 0; i < toEdges.size(); i++ )
		{
			processEdge( processedEdges, length, toEdges.get( i ) );
		}

		updateMassBasedOnDistance( length, Springs.getMIN_STRENGTH() + 0.3f );
		// setRadius( mass );
		// mass = Math.max( temp, mass );
	}

	/**
	 * Update mass based on distance.
	 * 
	 * @param distance
	 *            the distance
	 * @param intensity
	 *            the intensity
	 */
	public void updateMassBasedOnDistance( float distance, float intensity )
	{
		// update mass, which in turn affects the repelling force
		mass = (float)Math.pow( distance, Springs.getDistance_power() ) * intensity / Springs.getRepelling_intensity();
		// System.out.println( "Set mass of " + id + " to " + mass );
	}

	/**
	 * Process edge.
	 * 
	 * @param processedEdges
	 *            the processed edges
	 * @param length
	 *            the length
	 * @param tempEdge
	 *            the temp edge
	 * @return the float
	 */
	private float processEdge( Map<Integer, Float> processedEdges, float length, DNVEdge tempEdge )
	{
		Float previousLength;
		previousLength = processedEdges.get( tempEdge.getId() );
		if( previousLength != null )
		{
			tempEdge.setRestingDistance( ( length + previousLength ) / 2.0f );
		}
		else
			tempEdge.setRestingDistance( length );

		processedEdges.put( tempEdge.getId(), tempEdge.getRestingDistance() );
		return length;
	}

	/**
	 * Apply force.
	 * 
	 * @param energy_loss
	 *            the energy_loss
	 * @param applyToSubnodes
	 *            the apply to subnodes
	 * @return the float
	 */
	public float applyForce( float energy_loss, boolean applyToSubnodes )
	{
		return applyForce( energy_loss, applyToSubnodes, false );
	}

	/** The revive. */
	private int revive = 0;

	/** The Constant REVIVE_LIMIT. */
	private static final int REVIVE_LIMIT = 5;

	/**
	 * Apply force.
	 * 
	 * @param energy_loss
	 *            the energy_loss
	 * @param applyToSubnodes
	 *            the apply to subnodes
	 * @param rnv
	 *            the rnv
	 * @return the float
	 */
	public float applyForce( float energy_loss, boolean applyToSubnodes, boolean rnv )
	{
		float difference = 0;
		if( force != null && !fixed )
		{
			// tempPosition.set( position );

			/*
			 * if( force.length() > MAX_FORCE ) { force.normalize();
			 * force.dotProduct( MAX_FORCE ); }
			 */

			// getPositionIncrement( 0 );
			// getPositionIncrement( 1 );
			float strength = force.length();
			difference = MIN_FORCE - strength;
			if( !rnv && ( strength < MIN_FORCE || ( strength < HIGHER_MIN && Math.abs( strength - previousForceStrength ) < 0.001 ) ) )
			{
				graph.setNodeInactive( level, id );
				revive = 0;
			}
			previousForceStrength = strength;

			acceleration = force;
			// acceleration.dotProduct( Math.min( 100.0 / mass, 1.0 ) );
			acceleration.dotProduct( timeStep[0] );
			if( acceleration.length() > MAX_FORCE )
				acceleration.normalize().dotProduct( MAX_FORCE );
			Vector2D tempVelocity[] = { new Vector2D( 0, 0 ), new Vector2D( 0, 0 ) };
			Vector2D tempPositionIncrement[] = { new Vector2D( 0, 0 ), new Vector2D( 0, 0 ) };
			Vector2D tempVector = new Vector2D();
			tempVelocity[0].set( velocity );
			tempVector.set( acceleration );
			tempVector.dotProduct( timeStep[0] );
			tempVelocity[0].add( tempVector );
			tempVector.set( tempVelocity[0] );
			tempVector.dotProduct( timeStep[0] );
			tempPositionIncrement[0].set( tempVector );
			tempPositionIncrement[0].dotProduct( timeStep[0] );

			tempVelocity[1].set( tempVelocity[0] );
			tempVector.set( acceleration );
			tempVector.dotProduct( timeStep[1] );
			tempVelocity[1].add( tempVector );
			tempVector.set( tempVelocity[1] );
			tempVector.dotProduct( timeStep[1] );
			tempPositionIncrement[1].set( tempVector );
			tempPositionIncrement[1].dotProduct( timeStep[1] );

			// tempVelocity[1].add( tempVelocity[0].dotProduct( timeStep[0] ) );
			tempPositionIncrement[1].add( tempPositionIncrement[0] );

			velocity.set( tempVelocity[1] );
			position.add( tempPositionIncrement[1] );
			velocity.dotProduct( energy_loss );

			if( applyToSubnodes )
			{
				DNVNode tempNode;
				List<DNVNode> subNodes = subGraph.getNodesList();
				for( int i = 0; i < subNodes.size(); i++ )
				{
					tempNode = subNodes.get( i );
					tempNode.move( tempPositionIncrement[1], true, false );
				}

				// if( isFirstChild() )
				// parentNode.move( tempPositionIncrement[1], false, false,
				// use3D );
				// if( parentNode != null )
				// parentNode.reposition( true );
			}

			force.clear();
		}

		return difference;
	}

	/**
	 * Checks if is first child.
	 * 
	 * @return true, if is first child
	 */
	public boolean isFirstChild()
	{
		if( parentNode != null )
		{
			if( parentNode.getFirstChild() == this )
				return true;

			return false;
		}

		return true;
	}

	/*
	 * private void getPositionIncrement( int i ) { acceleration = force; //
	 * acceleration.dotProduct( 1.0 / mass ); acceleration.dotProduct(
	 * timeStep[i] ); tempVelocity[i].set( velocity ); tempVelocity[i].add(
	 * acceleration ); tempVelocity[i].dotProduct( timeStep[i] );
	 * tempPositionIncrement[i].set( tempVelocity[i] );
	 * tempPositionIncrement[i].dotProduct( timeStep[i] ); }
	 */

	// public boolean isAnchor()
	// {
	// return anchor;
	// }
	//
	// public void setAnchor( boolean anchor )
	// {
	// this.anchor = anchor;
	// if( anchor )
	// {
	// velocity.set( Vector2D.ZERO );
	// graph.addAnchorNode( this, level );
	// }
	// else
	// {
	// graph.removeAnchor( this, level );
	// if( !( this instanceof DNVEdge ) )
	// {
	// graph.setNodeActive( level, id, this );
	// }
	// }
	// }

	/**
	 * Checks if is positioned.
	 * 
	 * @return true, if is positioned
	 */
	public boolean isPositioned()
	{
		return positioned;
	}

	/**
	 * Sets the positioned.
	 * 
	 * @param positioned
	 *            the new positioned
	 */
	public void setPositioned( boolean positioned )
	{
		this.positioned = positioned;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo( Object arg0 )
	{
		if( arg0 == null )
		{
			throw new NullPointerException();
		}
		if( !( arg0 instanceof DNVNode ) && !( arg0 instanceof DNVEdge ) )
		{
			throw new IllegalArgumentException( "Can only compare Nodes to other Nodes. [" + arg0 + "]" );
		}

		DNVNode parm = (DNVNode)arg0;

		if( bbId != null && !bbId.equals( "" ) && bbId.equals( parm.getBbId() ) )
			return 0;

		if( id < parm.id )
			return -1;
		else if( id > parm.id )
			return 1;
		else
			return 0;
	}

	public boolean equals( DNVNode parm )
	{
		if( id == parm.id )
			return true;

		if( bbId != null && !bbId.equals( "" ) && bbId.equals( parm.getBbId() ) )
			return true;

		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals( Object arg0 )
	{
//		if( arg0 == null )
//		{
//			throw new NullPointerException();
//		}
//		if( !( arg0 instanceof DNVNode ) && !( arg0 instanceof DNVEdge ) )
//		{
//			throw new IllegalArgumentException( "Can only compare Nodes to other Nodes. [" + arg0 + "]" );
//		}

		DNVNode parm = (DNVNode)arg0;

		if( id == parm.id )
			return true;

		if( bbId != null && !bbId.equals( "" ) && bbId.equals( parm.getBbId() ) )
			return true;

		return false;
	}

	/**
	 * Move x.
	 * 
	 * @param value
	 *            the value
	 */
	public void moveX( float value )
	{
		setPosition( position.getX() + value, position.getY() );
	}

	/**
	 * Move y.
	 * 
	 * @param value
	 *            the value
	 */
	public void moveY( float value )
	{
		setPosition( position.getX(), position.getY() + value );
	}

	/**
	 * Gets the radius.
	 * 
	 * @return the radius
	 */
	public float getRadius()
	{
		return radius;
	}

	/**
	 * Sets the radius.
	 * 
	 * @param radius
	 *            the new radius
	 */
	public void setRadius( float radius )
	{
		this.radius = radius;
	}

	/**
	 * Adds the sub node.
	 * 
	 * @param node
	 *            the node
	 */
	public void addSubNode( DNVNode node )
	{
		if( subGraph == null )
		{
			subGraph = new SubGraph( graph, level - 1 );
		}

		subGraph.add( node );
	}

	/**
	 * Sets the sub nodes.
	 * 
	 * @param subnodes
	 *            the new sub nodes
	 */
	public void setSubNodes( List<DNVNode> subnodes )
	{
		if( subGraph == null )
		{
			subGraph = new SubGraph( graph, level - 1 );
		}
		subGraph.addAll( subnodes );
		float total = getTotalNumberOfSubNodes();
		// color.setX( 0 );
		// color.setY( 0 );
		// color.setZ( 0 );
		DNVNode subNode;
		float sum = 0;
		float count = 0;
		float temp;
		// Vector3D tempColor;
		Vector2D tempPosition;
		position = new Vector2D();
		List<DNVNode> subNodes = subGraph.getNodesList();
		for( int i = 0; i < subNodes.size(); i++ )
		{
			subNode = subNodes.get( i );
			temp = subNode.getTotalNumberOfSubNodes();
			sum += temp;
			count++;
			// tempColor = new Vector3D( subNode.getColor() );
			// tempColor.dotProduct( temp );
			tempPosition = subNode.getPosition();
			if( tempPosition == null )
				System.out.println( subNode );
			else
				position.add( tempPosition );
			// color.add( tempColor );
			subNode.setParentNode( this );
		}

		// color.dotProduct( 1.0f / sum );
		position.dotProduct( 1.0f / count );
		updateRadius( total );
	}

	/**
	 * Update radius.
	 * 
	 * @param total
	 *            the total
	 */
	private void updateRadius( float total )
	{
		radius = NODE_SIZE * total * 0.04f;
		if( radius < NODE_SIZE )
			radius += NODE_SIZE;
		if( radius > MAX_NODE_SIZE )
			radius = MAX_NODE_SIZE;
		// mass = total;
	}

	/** The total number of subnodes. */
	private float totalNumberOfSubnodes = 0;

	/**
	 * Gets the total number of sub nodes.
	 * 
	 * @return the total number of sub nodes
	 */
	public float getTotalNumberOfSubNodes()
	{
		if( totalNumberOfSubnodes != 0 )
			return totalNumberOfSubnodes;

		List<DNVNode> subNodes = subGraph.getNodesList();
		for( int i = 0; i < subNodes.size(); i++ )
		{
			totalNumberOfSubnodes += subNodes.get( i ).getTotalNumberOfSubNodes();
		}

		if( totalNumberOfSubnodes == 0 )
			return 1;

		return totalNumberOfSubnodes;
	}

	// public void addSubNode( DNVNode node )
	// {
	// float total = getTotalNumberOfSubNodes();
	// if( total == 1 )
	// total = 0;
	// float subTotal = node.getTotalNumberOfSubNodes();
	// color.dotProduct( total );
	// Vector3D tempColor = new Vector3D( node.getColor() );
	// tempColor.dotProduct( subTotal );
	// color.add( tempColor );
	// color.dotProduct( 1.0f / (float)( total + subTotal ) );
	// subNodes.add( node );
	// node.setParentNode( this );
	// updateRadiusAndMass( total + subTotal );
	// }

	/**
	 * Gets the sub nodes.
	 * 
	 * @return the sub nodes
	 */
	public List<DNVNode> getSubNodes()
	{
		return subGraph.getNodesList();
	}

	/**
	 * Gets the number of sub nodes.
	 * 
	 * @return the number of sub nodes
	 */
	public int getNumberOfSubNodes()
	{
		return subGraph.getNodes().size();
	}

	/**
	 * Gets the distance from cluster.
	 * 
	 * @return the distance from cluster
	 */
	public int getDistanceFromCluster()
	{
		return distanceFromCluster;
	}

	/**
	 * Gets the mass.
	 * 
	 * @return the mass
	 */
	public float getMass()
	{
		return mass;
	}

	/**
	 * Sets the mass.
	 * 
	 * @param mass
	 *            the new mass
	 */
	public void setMass( float mass )
	{
		this.mass = mass;
	}

	/**
	 * Gets the force.
	 * 
	 * @return the force
	 */
	public Vector2D getForce()
	{
		return force;
	}

	/**
	 * Adds the force.
	 * 
	 * @param force
	 *            the force
	 */
	public void addForce( Vector2D force )
	{
		this.force.add( force );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.wigis.graph.dnv.DNVEntity#setLevel(int)
	 */
	public void setLevel( int level )
	{
		this.level = level;
		if( subGraph == null )
			subGraph = new SubGraph( graph, level - 1 );
		else
			subGraph.setLevel( level - 1 );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.wigis.graph.dnv.DNVEntity#serialize()
	 */
	public String serialize()
	{
		String value = "\t\t<DNVNODE Id=\"" + id + "\" Level=\"" + level + "\" Position=\"" + position.toString() + "\" Color=\"" + color
				+ "\" Size=\"" + radius + "\" Label=\"" + label + "\" Mass=\"" + mass + "\" Fixed=\"" + fixed + "\" Icon=\"" + icon + "\" Type=\""
				+ type + "\" BBid=\"" + bbId + "\" ForceLabel=\"" + isForceLabel() + "\" Alpha=\"" + alpha + "\" LabelColor=\"" + labelColor
				+ "\" LabelOutlineColor=\"" + labelOutlineColor + "\" OutlineColor=\"" + outlineColor + "\" LabelSize=\"" + labelSize
				+ "\" Expandable=\"" + expandable + "\" HideLabelBackground=\"" + hideLabelBackground + "\" CurvedLabel=\"" + curvedLabel
				+ "\" Visible=\"" + isVisible() + "\" Selected=\"" + isSelected() + "\">\n";

		initProperties();
		synchronized( properties )
		{
			for( String key : properties.keySet() )
			{
				String extractable = "False";
				try
				{
					if( graph.getExtractableProperties().get( key ).get( getProperty( key ) ).get( id ).equals( this ) )
					{
						extractable = "True";
					}
				}
				catch( NullPointerException npe )
				{}
				value += "\t\t\t<NODEPROPERTY Key=\"" + key + "\" Value=\"" + getProperty( key ) + "\" Extractable=\"" + extractable + "\" />\n";
			}
		}
		List<DNVNode> subNodes = subGraph.getNodesList();
		for( int i = 0; i < subNodes.size(); i++ )
		{
			value += "\t\t\t<SUBNODE id=\"" + subNodes.get( i ).getId() + "\" />\n";
		}
		value += "\t\t</DNVNODE>\n";

		return value;
	}

	@Override
	public void setId( Integer id )
	{
		super.setId( id );
		for( DNVEdge fromEdge : fromEdges.values() )
		{
			fromEdge.setFromId( id );
		}
		for( DNVEdge toEdge : toEdges.values() )
		{
			toEdge.setToId( id );
		}
	}

	/**
	 * Deserialize.
	 * 
	 * @param line
	 *            the line
	 */
	private void deserialize( String line )
	{
		String tempLine;
		String checkString = " Id=\"";
		if( line.contains( checkString ) )
		{
			tempLine = line.substring( line.indexOf( checkString ) + checkString.length() );
			setId( Integer.parseInt( tempLine.substring( 0, tempLine.indexOf( "\"" ) ) ) );
		}
		checkString = " Level=\"";
		if( line.contains( checkString ) )
		{
			tempLine = line.substring( line.indexOf( checkString ) + checkString.length() );
			level = Integer.parseInt( tempLine.substring( 0, tempLine.indexOf( "\"" ) ) );
			setLevel( level );
		}
		checkString = " Position=\"";
		if( line.contains( checkString ) )
		{
			tempLine = line.substring( line.indexOf( checkString ) + checkString.length() );
			position = new Vector2D( tempLine.substring( 0, tempLine.indexOf( "\"" ) ) );
			originalPosition = new Vector2D( position );
		}
		checkString = " Color=\"";
		if( line.contains( checkString ) )
		{
			tempLine = line.substring( line.indexOf( checkString ) + checkString.length() );
			setColor( tempLine.substring( 0, tempLine.indexOf( "\"" ) ) );
		}
		checkString = " Size=\"";
		if( line.contains( checkString ) )
		{
			tempLine = line.substring( line.indexOf( checkString ) + checkString.length() );
			radius = Float.parseFloat( tempLine.substring( 0, tempLine.indexOf( "\"" ) ) );
		}
		checkString = " Label=\"";
		if( line.contains( checkString ) )
		{
			tempLine = line.substring( line.indexOf( checkString ) + checkString.length() );
			label = tempLine.substring( 0, tempLine.indexOf( "\"" ) );
		}
		checkString = " Mass=\"";
		if( line.contains( checkString ) )
		{
			tempLine = line.substring( line.indexOf( checkString ) + checkString.length() );
			mass = Float.parseFloat( tempLine.substring( 0, tempLine.indexOf( "\"" ) ) );
		}
		checkString = " Fixed=\"";
		if( line.contains( checkString ) )
		{
			tempLine = line.substring( line.indexOf( checkString ) + checkString.length() );
			fixed = Boolean.parseBoolean( tempLine.substring( 0, tempLine.indexOf( "\"" ) ) );
		}
		checkString = " Icon=\"";
		if( line.contains( checkString ) )
		{
			tempLine = line.substring( line.indexOf( checkString ) + checkString.length() );
			icon = tempLine.substring( 0, tempLine.indexOf( "\"" ) );
			if( icon.equals( "-1" ) )
			{
				icon = "";
			}
		}
		checkString = " Type=\"";
		if( line.contains( checkString ) )
		{
			tempLine = line.substring( line.indexOf( checkString ) + checkString.length() );
			type = tempLine.substring( 0, tempLine.indexOf( "\"" ) );
		}
		checkString = " BBid=\"";
		if( line.contains( checkString ) )
		{
			tempLine = line.substring( line.indexOf( checkString ) + checkString.length() );
			bbId = tempLine.substring( 0, tempLine.indexOf( "\"" ) );
		}
		checkString = " ForceLabel=\"";
		if( line.contains( checkString ) )
		{
			tempLine = line.substring( line.indexOf( checkString ) + checkString.length() );
			setForceLabel( Boolean.parseBoolean( tempLine.substring( 0, tempLine.indexOf( "\"" ) ) ) );
		}
		checkString = " Alpha=\"";
		if( line.contains( checkString ) )
		{
			tempLine = line.substring( line.indexOf( checkString ) + checkString.length() );
			setAlpha( Float.parseFloat( tempLine.substring( 0, tempLine.indexOf( "\"" ) ) ) );
		}
		checkString = " LabelColor=\"";
		if( line.contains( checkString ) )
		{
			tempLine = line.substring( line.indexOf( checkString ) + checkString.length() );
			tempLine = tempLine.substring( 0, tempLine.indexOf( "\"" ) );
			if( !tempLine.equals( "null" ) )
			{
				labelColor = new Vector3D( tempLine );
				if( labelColor.getX() > 1 )
					labelColor.setX( 1 );
				if( labelColor.getY() > 1 )
					labelColor.setY( 1 );
				if( labelColor.getZ() > 1 )
					labelColor.setZ( 1 );
				if( labelColor.getX() < 0 )
					labelColor.setX( 0 );
				if( labelColor.getY() < 0 )
					labelColor.setY( 0 );
				if( labelColor.getZ() < 0 )
					labelColor.setZ( 0 );
			}
		}
		checkString = " LabelOutlineColor=\"";
		if( line.contains( checkString ) )
		{
			tempLine = line.substring( line.indexOf( checkString ) + checkString.length() );
			tempLine = tempLine.substring( 0, tempLine.indexOf( "\"" ) );
			if( !tempLine.equals( "null" ) )
			{
				labelOutlineColor = new Vector3D( tempLine );
				if( labelOutlineColor.getX() > 1 )
					labelOutlineColor.setX( 1 );
				if( labelOutlineColor.getY() > 1 )
					labelOutlineColor.setY( 1 );
				if( labelOutlineColor.getZ() > 1 )
					labelOutlineColor.setZ( 1 );
				if( labelOutlineColor.getX() < 0 )
					labelOutlineColor.setX( 0 );
				if( labelOutlineColor.getY() < 0 )
					labelOutlineColor.setY( 0 );
				if( labelOutlineColor.getZ() < 0 )
					labelOutlineColor.setZ( 0 );
			}
		}
		checkString = " OutlineColor=\"";
		if( line.contains( checkString ) )
		{
			tempLine = line.substring( line.indexOf( checkString ) + checkString.length() );
			tempLine = tempLine.substring( 0, tempLine.indexOf( "\"" ) );
			if( !tempLine.equals( "null" ) )
			{
				outlineColor = new Vector3D( tempLine );
				if( outlineColor.getX() > 1 )
					outlineColor.setX( 1 );
				if( outlineColor.getY() > 1 )
					outlineColor.setY( 1 );
				if( outlineColor.getZ() > 1 )
					outlineColor.setZ( 1 );
				if( outlineColor.getX() < 0 )
					outlineColor.setX( 0 );
				if( outlineColor.getY() < 0 )
					outlineColor.setY( 0 );
				if( outlineColor.getZ() < 0 )
					outlineColor.setZ( 0 );
			}
		}
		checkString = " LabelSize=\"";
		if( line.contains( checkString ) )
		{
			tempLine = line.substring( line.indexOf( checkString ) + checkString.length() );
			tempLine = tempLine.substring( 0, tempLine.indexOf( "\"" ) );
			if( !tempLine.equals( "null" ) )
			{
				labelSize = Integer.parseInt( tempLine );
			}
		}

		checkString = " Expandable=\"";
		if( line.contains( checkString ) )
		{
			tempLine = line.substring( line.indexOf( checkString ) + checkString.length() );
			tempLine = tempLine.substring( 0, tempLine.indexOf( "\"" ) );
			if( !tempLine.equals( "null" ) )
			{
				expandable = Boolean.parseBoolean( tempLine );
			}
		}

		checkString = " HideLabelBackground=\"";
		if( line.contains( checkString ) )
		{
			tempLine = line.substring( line.indexOf( checkString ) + checkString.length() );
			tempLine = tempLine.substring( 0, tempLine.indexOf( "\"" ) );
			if( !tempLine.equals( "null" ) )
			{
				hideLabelBackground = Boolean.parseBoolean( tempLine );
			}
		}

		checkString = " CurvedLabel=\"";
		if( line.contains( checkString ) )
		{
			tempLine = line.substring( line.indexOf( checkString ) + checkString.length() );
			tempLine = tempLine.substring( 0, tempLine.indexOf( "\"" ) );
			if( !tempLine.equals( "null" ) )
			{
				curvedLabel = Boolean.parseBoolean( tempLine );
			}
		}

		checkString = " Visible=\"";
		if( line.contains( checkString ) )
		{
			tempLine = line.substring( line.indexOf( checkString ) + checkString.length() );
			setVisible( Boolean.parseBoolean( tempLine.substring( 0, tempLine.indexOf( "\"" ) ) ) );
		}
		else
		{
			setVisible( true );
		}

		checkString = " Selected=\"";
		if( line.contains( checkString ) )
		{
			tempLine = line.substring( line.indexOf( checkString ) + checkString.length() );
			tempLine = tempLine.substring( 0, tempLine.indexOf( "\"" ) );
			if( !tempLine.equals( "null" ) )
			{
				boolean selected = Boolean.parseBoolean( tempLine );
				setSelected( selected );
				if( selected )
				{
					System.out.println( label + " is selected: " + isSelected() );
				}
			}
		}
	}

	public void tellNeighborsVisible( boolean visible )
	{
		if( neighbors.size() > 0 )
		{
			for( DNVNode node : neighbors.values() )
			{
				DNVEdge edge = getEdgeToNeighbor( node.getId() );
				if( edge.isVisible() )
				{
					node.setNeighborVisible( this, visible );
				}
			}
		}
	}

	/**
	 * @param dnvNode
	 * @param visible
	 */
	private void setNeighborVisible( DNVNode dnvNode, boolean visible )
	{
		if( visible )
		{
			visibleNeighbors.put( dnvNode.getId(), dnvNode );
		}
		else
		{
			visibleNeighbors.remove( dnvNode.getId() );
		}
	}

	public void setFromEdgeVisible( DNVEdge edge, boolean visible )
	{
		if( visible )
		{
			visibleFromEdges.put( edge.getId(), edge );
		}
		else
		{
			visibleFromEdges.remove( edge.getId() );
		}
		edge.getTo().setNeighborVisible( this, visible && isVisible() );
	}

	public void setToEdgeVisible( DNVEdge edge, boolean visible )
	{
		if( visible )
		{
			visibleToEdges.put( edge.getId(), edge );
		}
		else
		{
			visibleToEdges.remove( edge.getId() );
		}
		edge.getFrom().setNeighborVisible( this, visible && isVisible() );
	}

	/**
	 * Gets the neighbors.
	 * 
	 * @return the neighbors
	 */
	public List<DNVNode> getNeighbors()
	{
		return getNeighbors( false );
	}

	public List<DNVNode> getNeighbors( boolean visibleOnly )
	{
		if( visibleOnly )
		{
			return new ArrayList<DNVNode>( visibleNeighbors.values() );
		}
		else
		{
			return new ArrayList<DNVNode>( neighbors.values() );
		}
	}

	public Map<Integer, DNVNode> getNeighborMap()
	{
		return getNeighborMap( false );
	}

	/**
	 * Gets the neighbor map.
	 * 
	 * @return the neighbor map
	 */
	public Map<Integer, DNVNode> getNeighborMap( boolean visibleOnly )
	{
		if( visibleOnly )
		{
			return visibleNeighbors;
		}
		else
		{
			return neighbors;
		}
	}

	/**
	 * Gets the from edges.
	 * 
	 * @return the from edges
	 */
	public List<DNVEdge> getFromEdges()
	{
		return getFromEdges( false );
	}

	public List<DNVEdge> getFromEdges( boolean visibleOnly )
	{
		if( visibleOnly )
		{
			Collection<DNVEdge> collection = visibleFromEdges.values();
			if( collection instanceof List<?> )
				return (List<DNVEdge>)collection;

			return new ArrayList<DNVEdge>( collection );
		}
		else
		{
			Collection<DNVEdge> collection = fromEdges.values();
			if( collection instanceof List<?> )
				return (List<DNVEdge>)collection;

			return new ArrayList<DNVEdge>( collection );
		}
	}

	/**
	 * Gets the to edges.
	 * 
	 * @return the to edges
	 */
	public List<DNVEdge> getToEdges()
	{
		return getToEdges( false );
	}

	public List<DNVEdge> getToEdges( boolean visibleOnly )
	{
		if( visibleOnly )
		{
			Collection<DNVEdge> collection = visibleToEdges.values();
			if( collection instanceof List<?> )
				return (List<DNVEdge>)collection;

			return new ArrayList<DNVEdge>( collection );
		}
		else
		{
			Collection<DNVEdge> collection = toEdges.values();
			if( collection instanceof List<?> )
				return (List<DNVEdge>)collection;

			return new ArrayList<DNVEdge>( collection );
		}
	}

	/**
	 * Gets the parent node.
	 * 
	 * @return the parent node
	 */
	public DNVNode getParentNode()
	{
		return parentNode;
	}

	/**
	 * Sets the parent node.
	 * 
	 * @param parentNode
	 *            the new parent node
	 */
	public void setParentNode( DNVNode parentNode )
	{
		this.parentNode = parentNode;
	}

	// public void setParentNodeAnchor( boolean anchor, boolean recursive )
	// {
	// if( parentNode != null )
	// {
	// parentNode.setAnchor( anchor );
	// if( recursive )
	// {
	// parentNode.setParentNodeAnchor( anchor, recursive );
	// }
	// }
	// }
	//
	// public void setSubNodesAnchor( boolean anchor, boolean recursive )
	// {
	// DNVNode tempNode;
	// List<DNVNode> subNodes = subGraph.getNodesList();
	// for( int i = 0; i < subNodes.size(); i++ )
	// {
	// tempNode = subNodes.get( i );
	// tempNode.setAnchor( anchor );
	// if( recursive )
	// {
	// tempNode.setSubNodesAnchor( anchor, recursive );
	// }
	// }
	// }

	// public Octree getOctreeNode()
	// {
	// return octreeNode;
	// }
	//
	// public void setOctreeNode( Octree octreeNode )
	// {
	// this.octreeNode = octreeNode;
	// }

	/**
	 * Checks if is leaf node.
	 * 
	 * @return true, if is leaf node
	 */
	public boolean isLeafNode()
	{
		if( neighbors.size() == 1 )
			return true;

		return false;
	}

	/*
	 * public Collection<DNVNode> getRepelledNodes() { return
	 * repelledNodes.values(); }
	 * 
	 * public void addRepelledNodes( Collection<DNVNode> repelledNodes ) {
	 * DNVNode tempNode; for( Iterator<DNVNode> i = repelledNodes.iterator();
	 * i.hasNext(); ) { tempNode = i.next(); this.repelledNodes.put(
	 * tempNode.getId(), tempNode ); } }
	 */
	// public void updateRepelledNodes()
	// {
	// if( octreeNode != null )
	// {
	// Octree parent = octreeNode.getParent();
	// if( parent != null )
	// {
	// /*
	// * Octree grandParent = parent.getParent(); if( grandParent !=
	// * null ) grandParent.getLeaf_nodes( repelledNodes ); else
	// */parent.getLeaf_nodes( repelledNodes );
	// }
	// else
	// {
	// octreeNode.getLeaf_nodes( repelledNodes );
	// }
	// }
	// else
	// {
	// System.out.println( "WARNING: Octree node is null for node " + id + " " +
	// this );
	// }
	// }

	/**
	 * Move related nodes.
	 * 
	 * @param movement
	 *            the movement
	 * @param applyToSubnodes
	 *            the apply to subnodes
	 * @param applyToParent
	 *            the apply to parent
	 */
	public void moveRelatedNodes( Vector2D movement, boolean applyToSubnodes, boolean applyToParent )
	{
		if( applyToSubnodes )
		{
			List<DNVNode> subNodes = subGraph.getNodesList();
			for( int i = 0; i < subNodes.size(); i++ )
			{
				subNodes.get( i ).move( movement, true, false );
			}
		}

		if( applyToParent && parentNode != null )
		{
			parentNode.reposition( true );
		}
	}

	/**
	 * Gets the siblings.
	 * 
	 * @return the siblings
	 */
	public List<DNVNode> getSiblings()
	{
		if( parentNode != null )
		{
			return parentNode.getSubNodes();
		}

		List<DNVNode> list = new ArrayList<DNVNode>();
		list.add( this );
		return list;
	}

	/**
	 * Can revive.
	 * 
	 * @return true, if successful
	 */
	public boolean canRevive()
	{
		revive++;
		if( revive >= REVIVE_LIMIT )
		{
			revive = 0;
			return true;
		}

		return false;
	}

	/**
	 * Generate force to original position.
	 */
	public void generateForceToOriginalPosition()
	{
		if( force == null )
			force = new Vector2D();
		if( originalPosition != null )
		{
			force.setX( originalPosition.getX() - position.getX() );
			force.setY( originalPosition.getY() - position.getY() );
		}
	}

	/**
	 * Checks if is neighbor.
	 * 
	 * @param node
	 *            the node
	 * @return true, if is neighbor
	 */
	public boolean isNeighbor( DNVNode node )
	{
		return ( neighbors.get( node.getId() ) != null );
	}

	public DNVEdge getEdgeToNeighbor( Integer neighborId )
	{
		return edgesByNeighborId.get( neighborId );
	}

	/**
	 * Gets the first sibling.
	 * 
	 * @return the first sibling
	 */
	public DNVNode getFirstSibling()
	{
		if( parentNode == null )
			return null;

		return parentNode.getFirstChild();
	}

	/**
	 * Gets the first child.
	 * 
	 * @return the first child
	 */
	public DNVNode getFirstChild()
	{
		return firstChild;
	}

	/**
	 * Sets the first child.
	 * 
	 * @param firstChild
	 *            the new first child
	 */
	public void setFirstChild( DNVNode firstChild )
	{
		this.firstChild = firstChild;
	}

	/**
	 * Iterate sub graph.
	 */
	public void iterateSubGraph()
	{
		subGraph.accumulateForces();
		subGraph.applyForces();
	}

	/**
	 * Gets the distance from selected node.
	 * 
	 * @return the distance from selected node
	 */
	// public int getDistanceFromSelectedNode()
	// {
	// return distanceFromSelectedNode;
	// }

	/**
	 * Sets the distance from selected node.
	 * 
	 * @param distanceFromSelectedNode
	 *            the new distance from selected node
	 */
	public void setDistanceFromSelectedNode( int distanceFromSelectedNode )
	{
		this.distanceFromSelectedNode = distanceFromSelectedNode;
		// this.label = "" + distanceFromSelectedNode + " - " +
		// interpolationWeight;
	}

	/**
	 * Gets the distance from node with id.
	 * 
	 * @param id
	 *            the id
	 * @return the distance from node with id
	 */
	public Integer getDistanceFromNodeWithId( int id )
	{
		Integer distance = distanceFromNodeWithId.get( id );
		if( distance == null )
		{
			distance = Integer.MAX_VALUE;
		}

		return distance;
	}

	/**
	 * Sets the distance from node.
	 * 
	 * @param node
	 *            the node
	 * @param distance
	 *            the distance
	 */
	public void setDistanceFromNode( DNVNode node, int distance )
	{
		setDistanceFromNodeWithId( node.getId(), distance );
		node.addNodeAtDistance( distance, this );
	}

	/**
	 * Sets the distance from node with id.
	 * 
	 * @param id
	 *            the id
	 * @param distance
	 *            the distance
	 */
	public void setDistanceFromNodeWithId( int id, int distance )
	{
		distanceFromNodeWithId.put( id, distance );
	}

	/**
	 * Adds the node at distance.
	 * 
	 * @param distance
	 *            the distance
	 * @param node
	 *            the node
	 */
	public void addNodeAtDistance( int distance, DNVNode node )
	{
		Map<Integer, DNVNode> nodes = nodesAtDistance.get( distance );
		if( nodes == null )
		{
			nodes = new HashMap<Integer, DNVNode>();
			nodesAtDistance.put( distance, nodes );
		}

		nodes.put( node.getId(), node );
	}

	/**
	 * Gets the nodes at distance.
	 * 
	 * @param distance
	 *            the distance
	 * @return the nodes at distance
	 */
	public Map<Integer, DNVNode> getNodesAtDistance( int distance )
	{
		return nodesAtDistance.get( distance );
	}

	/**
	 * Gets the distances.
	 * 
	 * @return the distances
	 */
	public Set<Integer> getDistances()
	{
		return nodesAtDistance.keySet();
	}

	/**
	 * Reset interpolation data.
	 */
	public void resetInterpolationData()
	{
		setActualDistanceFromSelectedNode( Integer.MAX_VALUE );
		setDistanceFromSelectedNode( Integer.MAX_VALUE );
		distanceFromNodeWithId.clear();
		resetInterpolationWeights();
	}

	/**
	 * Reset interpolation weight.
	 * 
	 * @param nodeId
	 *            the node id
	 */
	public void resetInterpolationWeight( int nodeId )
	{
		interpolationWeight.remove( nodeId );
	}

	/**
	 * Reset interpolation weights.
	 */
	public void resetInterpolationWeights()
	{
		interpolationWeight.clear();
	}

	/**
	 * Gets the interpolation weight.
	 * 
	 * @param nodeId
	 *            the node id
	 * @return the interpolation weight
	 */
	public float getInterpolationWeight( int nodeId )
	{
		Float weight = interpolationWeight.get( nodeId );
		if( weight == null )
		{
			weight = 0f;
		}

		return weight;
	}

	/**
	 * Sets the interpolation weight.
	 * 
	 * @param nodeId
	 *            the node id
	 * @param interpolationWeight
	 *            the interpolation weight
	 */
	public void setInterpolationWeight( int nodeId, float interpolationWeight )
	{
		this.interpolationWeight.put( nodeId, interpolationWeight );
		// this.label = "" + distanceFromSelectedNode + " - " +
		// interpolationWeight;
	}

	/**
	 * Gets the decorator.
	 * 
	 * @return the decorator
	 */
	public ResourceDecorator getDecorator()
	{
		return decorator;
	}

	/**
	 * Gets the icon.
	 * 
	 * @return the icon
	 */
	public String getIcon()
	{
		if( ( icon == null || icon.equals( "" ) ) && decorator != null )
		{
			icon = decorator.getIcon();
			// TODO lance did this
			// icon = icon.substring( icon.lastIndexOf( "/" ) + 1 );
		}

		return icon;
	}

	/**
	 * Gets the connectivity.
	 * 
	 * @return the connectivity
	 */
	public int getConnectivity()
	{
		return getConnectivity( false );
	}

	public int getConnectivity( boolean visibleOnly )
	{
		if( visibleOnly )
		{
			return visibleFromEdges.size() + visibleToEdges.size();
		}
		else
		{
			return fromEdges.size() + toEdges.size();
		}
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
	 * Adds the sub node id.
	 * 
	 * @param id
	 *            the id
	 */
	public void addSubNodeId( Integer id )
	{
		subNodeIds.add( id );
	}

	/**
	 * Gets the sub node ids.
	 * 
	 * @return the sub node ids
	 */
	public Collection<Integer> getSubNodeIds()
	{
		if( subNodeIds != null )
			return subNodeIds;

		return subGraph.getNodes().keySet();
	}

	/**
	 * Clear sub node ids.
	 */
	public void clearSubNodeIds()
	{
		subNodeIds.clear();
		subNodeIds = null;
	}

	/**
	 * Sets the acceleration.
	 * 
	 * @param acceleration
	 *            the new acceleration
	 */
	protected void setAcceleration( Vector2D acceleration )
	{
		this.acceleration = acceleration;
	}

	/**
	 * Sets the velocity.
	 * 
	 * @param velocity
	 *            the new velocity
	 */
	protected void setVelocity( Vector2D velocity )
	{
		this.velocity = velocity;
	}

	/**
	 * Removes the edge.
	 * 
	 * @param edge
	 *            the edge
	 */
	public void removeEdge( DNVEdge edge )
	{
		if( edge.getFrom() == this )
			removeFromEdge( edge );
		else if( edge.getTo() == this )
			removeToEdge( edge );
	}

	/**
	 * Removes the from edge.
	 * 
	 * @param edge
	 *            the edge
	 */
	public void removeFromEdge( DNVEdge edge )
	{
		fromEdges.remove( edge.getId() );
		visibleFromEdges.remove( edge.getId() );
		neighbors.remove( edge.getTo().getId() );
		visibleNeighbors.remove( edge.getTo().getId() );
	}

	/**
	 * Removes the to edge.
	 * 
	 * @param edge
	 *            the edge
	 */
	public void removeToEdge( DNVEdge edge )
	{
		toEdges.remove( edge.getId() );
		visibleToEdges.remove( edge.getId() );
		neighbors.remove( edge.getFrom().getId() );
		visibleNeighbors.remove( edge.getFrom().getId() );
	}

	/**
	 * Gets the actual distance from selected node.
	 * 
	 * @return the actual distance from selected node
	 */
	public float getActualDistanceFromSelectedNode()
	{
		return actualDistanceFromSelectedNode;
	}

	/**
	 * Sets the actual distance from selected node.
	 * 
	 * @param actualDistanceFromSelectedNode
	 *            the new actual distance from selected node
	 */
	public void setActualDistanceFromSelectedNode( float actualDistanceFromSelectedNode )
	{
		this.actualDistanceFromSelectedNode = actualDistanceFromSelectedNode;
	}

	/**
	 * Toggle selected.
	 */
	public void toggleSelected()
	{
		if( isSelected() )
		{
			setSelected( false );
		}
		else
		{
			setSelected( true );
			PaintBean pb = (PaintBean)ContextLookup.lookup( "paintBean", FacesContext.getCurrentInstance() );
			pb.setSelectedNode( this, true );
		}
	}

	/**
	 * Sets the icon.
	 * 
	 * @param icon
	 *            the new icon
	 */
	public void setIcon( String icon )
	{
		if( icon != null )
			this.icon = icon;
	}

	/**
	 * Gets the icon image.
	 * 
	 * @param size
	 *            the size
	 * @return the icon image
	 */
	public Image getIconImage( int size )
	{
		return iconImage.get( size );
	}

	/**
	 * Sets the icon image.
	 * 
	 * @param size
	 *            the size
	 * @param iconImage
	 *            the icon image
	 */
	public void setIconImage( int size, Image iconImage )
	{
		if( !this.iconImage.containsKey( size ) )
		{
			if( this.iconImage.size() > 5 )
			{
				this.iconImage.remove( this.iconImage.keySet().iterator().next() );
			}
			this.iconImage.put( size, iconImage );
		}
	}

	/**
	 * Gets the distance from center node.
	 * 
	 * @return the distance from center node
	 */
	public float getDistanceFromCenterNode()
	{
		return distanceFromCenterNode;
	}

	/**
	 * Sets the distance from center node.
	 * 
	 * @param distanceFromCenterNode
	 *            the new distance from center node
	 */
	public void setDistanceFromCenterNode( float distanceFromCenterNode )
	{
		this.distanceFromCenterNode = distanceFromCenterNode;
	}

	/**
	 * Checks if is neighbor selected.
	 * 
	 * @return true, if is neighbor selected
	 */
	public boolean isNeighborSelected()
	{
		if( selectedNeighbors.size() > 0 )
		{
			return true;
		}

		return false;
	}

	/**
	 * Checks if is fixed.
	 * 
	 * @return true, if is fixed
	 */
	public boolean isFixed()
	{
		return fixed;
	}

	/**
	 * Sets the fixed.
	 * 
	 * @param fixed
	 *            the new fixed
	 */
	public void setFixed( boolean fixed )
	{
		this.fixed = fixed;
	}

	/**
	 * Gets the diameter of subnodes.
	 * 
	 * @return the diameter of subnodes
	 */
	public float getDiameterOfSubnodes()
	{
		return diameterOfSubnodes;
	}

	/**
	 * Sets the diameter of subnodes.
	 * 
	 * @param diameterOfSubnodes
	 *            the new diameter of subnodes
	 */
	public void setDiameterOfSubnodes( float diameterOfSubnodes )
	{
		this.diameterOfSubnodes = diameterOfSubnodes;
	}

	/**
	 * Gets the image url.
	 * 
	 * @return the image url
	 */
	public String getImageUrl()
	{
		if( icon != null && !icon.equals( "" ) )
			return "/wigi/NodeIconServlet?s=100&id=" + id;

		return "/wigi/NodeIconServlet?s=0&id=" + id;
	}

	/**
	 * Gets the details.
	 * 
	 * @return the details
	 */
	public String getDetails()
	{
		StringBuilder details = new StringBuilder( 1000 );

		// details +=
		// "<img src=\"http://localhost:8080/WiGi/wigi/NodeIconServlet?s=30&id="
		// + id + "\" /><br />";

		if( decorator != null )
		{
			// if( decorator.getIcon() != null )
			// {
			// details += "<img src=\"" + decorator.getIcon() + "\" /><br />";
			// }
			if( decorator.getDescription() != null )
			{
				details.append( decorator.getDescription() );
			}
			details.append( "<br /><br />" );
			details.append( "<table border=\"0\" style=\"font-size:small\" width=\"200px\">" );
			if( decorator.getLatitude() != null )
			{
				details.append( "<tr><td>Latitude:</td><td>" ).append( decorator.getLatitude() ).append( "</td></tr>" );
			}
			if( decorator.getLongitude() != null )
			{
				details.append( "<tr><td>Longitude:</td><td>" ).append( decorator.getLongitude() ).append( "</td></tr>" );
			}
			if( decorator.getDate() != null )
			{
				details.append( "<tr><td>Date:</td><td>" ).append( decorator.getDate() ).append( "</td></tr>" );
			}
			if( decorator.getDetails() != null )
			{
				details.append( "<tr><td>Details:</td><td>" ).append( decorator.getDetails() ).append( "</td></tr>" );
			}
			details.append( "</table>" );
		}

		String value;
		initProperties();
		synchronized( properties )
		{
			details.append( "<table border=\"1\" cellspacing=\"0\" style=\"font-size:small\">" );
			for( String key : properties.keySet() )
			{
				if( !key.equals( "Contents" ) )
				{
					value = getProperty( key );
					if( value != null && value.startsWith( "http://" ) )
					{
						key = "<a href=\"" + value + "\" target=\"_blank\">" + key + "</a>";
						details.append( "<tr><td colspan='2'>" ).append( key ).append( ":</td></tr>" );
					}
					else
					{
						details.append( "<tr><td>" ).append( key ).append( ":</td><td>" ).append( value ).append( "</td></tr>" );
					}
				}
				else
				{
					value = "<a href=\"/WiGi/wigi/components/viewContents.faces?id=" + id + "\" target=\"_blank\">" + "View contents" + "</a>";
					details.append( "<tr><td>" ).append( key ).append( ":</td><td>" ).append( value ).append( "</td></tr>" );
				}
			}
			details.append( "<tr><td>bbid:</td><td>" ).append( bbId ).append( "</td></tr>" );
			details.append( "<tr><td>type:</td><td>" ).append( type ).append( "</td></tr>" );
			details.append( "</table>" );

			details.append( "<br />" );
			details.append( "<table border=\"1\" cellspacing=\"0\">" );
			details.append( "<tr><td style=\"font-size:large\" colspan=\"2\">Neighbor Nodes:</td></tr>" );

			List<DNVNode> nodes = getNeighbors( true );
			SortByFloatProperty sbfp = new SortByFloatProperty( "year", true );
			Collections.sort( nodes, sbfp );

			for( DNVNode neighbor : nodes )
			{
				value = "";
				if( neighbor.getProperty( "Contents" ) != null )
				{
					value = "<td><a href=\"/WiGi/wigi/components/viewContents.faces?id=" + neighbor.getId() + "\" target=\"_blank\">"
							+ neighbor.getLabel() + "</a></td>";
				}
				else
				{
					value = "<td>" + neighbor.getLabel() + "</td>";
				}
				details.append( "<tr style=\"font-size:small\">" ).append( value ).append( "</tr>" );
			}
			details.append( "</table>" );
		}
		// System.out.println( "details : \n" + details );

		return details.toString();
	}

	/**
	 * Checks if is details expanded.
	 * 
	 * @return true, if is details expanded
	 */
	public boolean isDetailsExpanded()
	{
		return detailsExpanded;
	}

	/**
	 * Sets the details expanded.
	 * 
	 * @param detailsExpanded
	 *            the new details expanded
	 */
	public void setDetailsExpanded( boolean detailsExpanded )
	{
		this.detailsExpanded = detailsExpanded;
	}

	/**
	 * Expand details.
	 */
	public void expandDetails()
	{
		setDetailsExpanded( true );
	}

	/**
	 * Collapse details.
	 */
	public void collapseDetails()
	{
		setDetailsExpanded( false );
	}

	public void selectNeighbors()
	{
		selectNeighbors( true );
	}
	
	/**
	 * Select neighbors.
	 */
	public void selectNeighbors( boolean visibleOnly )
	{
		Collection<DNVNode> nodes;
		if( visibleOnly )
		{
			nodes = visibleNeighbors.values();
		}
		else
		{
			nodes = neighbors.values();
		}
		
		for( DNVNode neighbor : nodes )
		{
			if( !neighbor.isSelected() )
			{
				neighbor.setSelected( true );
			}
		}
	}

	/**
	 * Delete node.
	 */
	public void deleteNode()
	{
		if( graph.getPaintBean() != null )
		{
			graph.getPaintBean().addToHistory();
		}
		setSelected( false );
		graph.removeNode( this );
		List<DNVEdge> edges = this.getFromEdges();
		for( int i = 0; i < edges.size(); i++ )
		{
			graph.removeNode( edges.get( i ) );
		}
		edges = this.getToEdges();
		for( int i = 0; i < edges.size(); i++ )
		{
			graph.removeNode( edges.get( i ) );
		}

		try
		{
			PaintBean pb = (PaintBean)ContextLookup.lookup( "paintBean", FacesContext.getCurrentInstance() );
			if( pb != null )
			{
				pb.forceSubgraphRefresh();
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}

	/**
	 * Checks if is facebook can add.
	 * 
	 * @return true, if is facebook can add
	 */
	public boolean isFacebookCanAdd()
	{
		return facebookCanAdd;
	}

	/**
	 * Sets the facebook can add.
	 * 
	 * @param facebookCanAdd
	 *            the new facebook can add
	 */
	public void setFacebookCanAdd( boolean facebookCanAdd )
	{
		this.facebookCanAdd = facebookCanAdd;
	}

	/**
	 * Facebook add to profile.
	 * 
	 * @throws FacebookException
	 *             the facebook exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void facebookAddToProfile() throws FacebookException, IOException
	{
		if( facebookCanAdd )
		{
			RecommendationBean fb = (RecommendationBean)ContextLookup.lookup( "facebookBean", FacesContext.getCurrentInstance() );
			fb.addToUserProfile( this, true );
			facebookCanAdd = false;
		}
	}

	/**
	 * Removes the stored position.
	 */
	public void removeStoredPosition()
	{
		displayPosition = null;
		originalPosition = null;
	}

	/**
	 * Gets the original position.
	 * 
	 * @return the original position
	 */
	public Vector2D getOriginalPosition()
	{
		return originalPosition;
	}

	/**
	 * Checks if is expandable.
	 * 
	 * @return true, if is expandable
	 */
	public boolean isExpandable()
	{
		return expandable;
	}

	/**
	 * Sets the expandable.
	 * 
	 * @param expandable
	 *            the new expandable
	 */
	public void setExpandable( boolean expandable )
	{
		this.expandable = expandable;
	}

	/** The expand. */
	private ExpandNode expand = null;

	/**
	 * Sets the expander.
	 * 
	 * @param expand
	 *            the new expander
	 */
	public void setExpander( ExpandNode expand )
	{
		this.expand = expand;
	}

	/**
	 * Expand.
	 */
	public void expand()
	{
		if( expand != null )
		{
			expand.expand();
		}
	}

	private boolean edgeSelected = false;

	public void setEdgeSelected( boolean edgeSelected )
	{
		this.edgeSelected = edgeSelected;
	}

	/**
	 * Checks if is edge selected.
	 * 
	 * @return true, if is edge selected
	 */
	public boolean isEdgeSelected()
	{
		return edgeSelected;
	}

	/**
	 * Adds the force label.
	 */
	public void addForceLabel()
	{
		setForceLabel( true );
	}

	/**
	 * Removes the force label.
	 */
	public void removeForceLabel()
	{
		setForceLabel( false );
	}

	/**
	 * Sets the property extractable.
	 * 
	 * @param property
	 *            the new property extractable
	 */
	public void setPropertyExtractable( String property )
	{
		if( graph != null )
		{
			graph.setPropertyExtractable( property, this );
		}
		else
		{
			System.out.println( "WARNING: graph is null for node " + id + " : " + label );
		}
	}

	/**
	 * Sets the hide label background.
	 * 
	 * @param hideLabelBackground
	 *            the new hide label background
	 */
	public void setHideLabelBackground( boolean hideLabelBackground )
	{
		this.hideLabelBackground = hideLabelBackground;
	}

	/**
	 * Checks if is hide label background.
	 * 
	 * @return true, if is hide label background
	 */
	public boolean isHideLabelBackground()
	{
		return hideLabelBackground;
	}

	/**
	 * Sets the curved label.
	 * 
	 * @param curvedLabel
	 *            the new curved label
	 */
	public void setCurvedLabel( boolean curvedLabel )
	{
		this.curvedLabel = curvedLabel;
	}

	/**
	 * Checks if is curved label.
	 * 
	 * @return true, if is curved label
	 */
	public boolean isCurvedLabel()
	{
		return curvedLabel;
	}

	/**
	 * Sets the position change callback.
	 * 
	 * @param positionChangeCallback
	 *            the new position change callback
	 */
	public void setPositionChangeCallback( PositionChangeCallBack positionChangeCallback )
	{
		this.positionChangeCallback = positionChangeCallback;
	}
	
	public void verifyNeighbors()
	{
		for( DNVNode neighbor : getNeighbors() )
		{
			if( neighbor.getGraph() != getGraph() )
			{
				removeNeighbor( neighbor );
			}
		}
	}
	 
	public void removeNeighbor( DNVNode node )
	{
		System.out.println( "Removing neighbor " + node.getId() + " - " + node.getLabel() );
		neighbors.remove( node.getId() );
		visibleNeighbors.remove( node.getId() );
	}


	//========================================
	// STATS - Alex
	//========================================
	public String labelNonEmpty = "";
	public int degree = 0;
	public double degreeCentrality = 0;
	public String neighborsHtmlList = "";
	public String propertiesHtml = "";

	//-------------------------------
	// label non empty
	//-------------------------------
	public String getLabelNonEmpty()
	{
		if (__String.isNullOrEmpty(getLabel()))
			labelNonEmpty = "(unnamed)";
		else
			labelNonEmpty = getLabel();
		
		return labelNonEmpty;
	}

	public void setLabelNonEmpty(String labelNonEmpty)
	{
		this.labelNonEmpty = labelNonEmpty;
	}

	//-------------------------------
	// properties
	//-------------------------------
	public String getPropertiesHtml()
	{
		propertiesHtml = "";
		
		String value;
		initProperties();

		propertiesHtml += "<table class='" + StatsBean.STYLE_STATS_FONT + "' border='1' cellspacing='0'>";
		
		for (String key : properties.keySet())
		{
			if (!key.equals("Contents"))
			{
				value = getProperty(key);
				if (value != null && value.startsWith("http://"))
				{
					key = "<a href=\"" + value + "\" target=\"_blank\">" + key + "</a>";
					propertiesHtml += "<tr><td colspan='2'>" + key + "</td></tr>";
				}
				else
				{
					propertiesHtml += "<tr><td>" + key + "</td><td>" + value + "</td></tr>";
				}
			}
			else
			{
				value = "<a href=\"/WiGi/wigi/components/viewContents.faces?id=" + id + "\" target=\"_blank\">" + "View contents" + "</a>";
				propertiesHtml += "<tr><td>" + key + "</td><td>" + value + "</td></tr>";
			}
		}
		
		propertiesHtml += "</table>";
		
		return propertiesHtml;
	}

	public void setPropertiesHtml(String propertiesHtml)
	{
		this.propertiesHtml = propertiesHtml;
	}

	//-------------------------------
	// neighbors
	//-------------------------------
	public String getNeighborsHtmlList()
	{
		List<DNVNode> neighbors = getNeighbors( true );
		
		neighborsHtmlList = "";
		
		//neighborsHtmlList += neighbors.size() + "<br/>";
		
		neighborsHtmlList += "<table border='0' cellspacing='1'>";
		
		for (DNVNode n : neighbors)
		{
			neighborsHtmlList += "<tr><td class='" + StatsBean.STYLE_STATS_FONT + "'>";
			
			if (neighbors.indexOf(n) >= 10)
			{
				neighborsHtmlList += "... and " + (neighbors.size() - neighbors.indexOf(n)) + " more";
			
				neighborsHtmlList += "</td></tr>";
				
				break;
			}
			else
			{
				neighborsHtmlList += __jsf.getHtml_forHyperlink_toCall_JavaBean(n.getLabelNonEmpty(), "selectNodeForm", "BSelectNode", graph.getNodes(0).indexOf(n) + "");
				
				neighborsHtmlList += "</td></tr>";
			}
		}
		
		neighborsHtmlList += "</table>";
		
		return neighborsHtmlList;
	}

	public void setNeighborsHtmlList(String neighborsHtmlList)
	{
		this.neighborsHtmlList = neighborsHtmlList;
	}

	//-------------------------------
	// degree
	//-------------------------------
	public int getDegree()
	{
		return getConnectivity( true );
	}

	public void setDegree( int degree )
	{
		this.degree = degree;
	}

	public double getDegreeCentrality()
	{
		return __Math.setPrecision( ".##", (double)getDegree() / ( graph.getNodes( 0 ).size() - 1 ) );
	}

	public void setDegreeCentrality(double degreeCentrality)
	{
		 this.degreeCentrality = degreeCentrality;
	}

	/**
	 * @param selected
	 */
	public void tellNeighborsSelected( boolean selected )
	{
		if( neighbors.size() > 0 )
		{
			for( DNVNode node : neighbors.values() )
			{
				DNVEdge edge = getEdgeToNeighbor( node.getId() );
				if( edge.isVisible() )
				{
					node.setNeighborSelected( this, selected );
				}
			}
		}

	}

	/**
	 * @param dnvNode
	 * @param selected
	 */
	private void setNeighborSelected( DNVNode dnvNode, boolean selected )
	{
		if( selected )
		{
			selectedNeighbors.put( dnvNode.getId(), dnvNode );
		}
		else
		{
			selectedNeighbors.remove( dnvNode.getId() );
		}	
	}

	/**
	 * 
	 */
	public void setInActive()
	{
		if( graph != null )
		{
			graph.setNodeInactive( level, id );
		}
	}

	/**
	 * 
	 */
	public void setActive()
	{
		if( graph != null )
		{
			graph.setNodeActive( level, id, this );
		}
	}
}