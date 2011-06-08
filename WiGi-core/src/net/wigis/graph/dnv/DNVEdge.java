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

import java.io.Serializable;

import net.wigis.graph.dnv.utilities.Vector3D;
import blackbook.service.api.EdgeDecorator;

// TODO: Auto-generated Javadoc
/**
 * The Class DNVEdge.
 * 
 * @author Brynjar Gretarsson
 */
public class DNVEdge extends DNVEntity implements Serializable
{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The DEFAUL t_ k. */
	public static float DEFAULT_K = 1;

	/** The DEFAUL t_ restin g_ distance. */
	public static float DEFAULT_RESTING_DISTANCE = 0f;

	/** The K. */
	private float K = DEFAULT_K;

	/** The resting distance. */
	private float restingDistance = DEFAULT_RESTING_DISTANCE;

	/** The from. */
	private DNVNode from;

	/** The to. */
	private DNVNode to;

	/** The from id. */
	private int fromId;

	/** The to id. */
	private int toId;

	/** The edge decorator. */
	private EdgeDecorator edgeDecorator;

	/** The thickness. */
	private float thickness = 1;

	/** The directional. */
	private boolean directional = false;

	/**
	 * Logger.
	 * 
	 * @param graph
	 *            the graph
	 */
	// // private static Log logger = LogFactory.getLog( DNVEdge.class );

	public DNVEdge( DNVGraph graph )
	{
		super( graph );
		setVisible( true );
	}

	/**
	 * Instantiates a new dNV edge.
	 * 
	 * @param line
	 *            the line
	 * @param graph
	 *            the graph
	 */
	public DNVEdge( String line, DNVGraph graph )
	{
		super( graph );
		deserialize( line );
	}

	/**
	 * Instantiates a new dNV edge.
	 * 
	 * @param from
	 *            the from
	 * @param to
	 *            the to
	 * @param graph
	 *            the graph
	 */
	public DNVEdge( DNVNode from, DNVNode to, DNVGraph graph )
	{
		super( graph );
		if( to == from )
			System.out.println( "Can't link to self" );
		initialize( from, to );
	}

	/**
	 * Instantiates a new dNV edge.
	 * 
	 * @param level
	 *            the level
	 * @param length
	 *            the length
	 * @param rigid
	 *            the rigid
	 * @param from
	 *            the from
	 * @param to
	 *            the to
	 * @param graph
	 *            the graph
	 */
	public DNVEdge( int level, float length, boolean rigid, DNVNode from, DNVNode to, DNVGraph graph )
	{
		super( graph );
		if( to == from )
		{
			System.out.println( "Can't link to self (level = " + level + ")" + " fromId[" + from.getId() + "] toId[" + to.getId() + "] " + from );
			// throw new IllegalArgumentException();
		}
		initialize( from, to );
		restingDistance = length;
		if( rigid )
			K = 1;
		setLevel( level );
	}

	/**
	 * Instantiates a new dNV edge.
	 * 
	 * @param copy
	 *            the copy
	 * @param graph
	 *            the graph
	 */
	public DNVEdge( DNVEdge copy, DNVGraph graph )
	{
		super( graph );
		this.setK( copy.getK() );
		this.setRestingDistance( copy.getRestingDistance() );
		this.setFrom( copy.getFrom() );
		this.setTo( copy.getTo() );
		this.setEdgeDecorator( copy.getEdgeDecorator() );
		this.setDirectional( copy.isDirectional() );
		this.setLabel( copy.getLabel() );
		this.setBbId( copy.getBbId() );
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
	 * Initialize.
	 * 
	 * @param from
	 *            the from
	 * @param to
	 *            the to
	 */
	private void initialize( DNVNode from, DNVNode to )
	{
		this.to = to;
		this.from = from;
		this.toId = to.getId();
		this.fromId = from.getId();
		to.addToEdge( this );
		from.addFromEdge( this );
		setVisible( true );
	}

	/**
	 * Gets the k.
	 * 
	 * @return the k
	 */
	public float getK()
	{
		return K;
	}

	/**
	 * Sets the k.
	 * 
	 * @param k
	 *            the new k
	 */
	public void setK( float k )
	{
		this.K = k;
	}

	/**
	 * Gets the resting distance.
	 * 
	 * @return the resting distance
	 */
	public float getRestingDistance()
	{
		return restingDistance;
	}

	/**
	 * Sets the resting distance.
	 * 
	 * @param restingDistance
	 *            the new resting distance
	 */
	public void setRestingDistance( float restingDistance )
	{
		this.restingDistance = restingDistance;
	}

	/**
	 * Gets the from.
	 * 
	 * @return the from
	 */
	public DNVNode getFrom()
	{
		return from;
	}

	/**
	 * Gets the to.
	 * 
	 * @return the to
	 */
	public DNVNode getTo()
	{
		return to;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.wigis.graph.dnv.DNVEntity#serialize()
	 */
	@Override
	public String serialize()
	{
		String value = "\t\t<DNVEDGE " + "Id=\"" + getId() + "\" Level=\"" + getLevel() + "\" Length=\"" + getRestingDistance() + "\" K=\"" + K
				+ "\" From=\"" + from.getId() + "\" To=\"" + to.getId() + "\" Label=\"" + getLabel() + "\" Directional=\"" + directional
				+ "\" BBid=\"" + bbId + "\" Color=\"" + color + "\" Type=\"" + type + "\" HasSetColor=\"" + hasSetColor + "\" Alpha=\""
				+ alpha + "\" LabelColor=\"" + labelColor + "\" LabelOutlineColor=\"" + labelOutlineColor + "\" LabelSize=\"" + labelSize
				+ "\" Thickness=\"" + thickness + "\" Visible=\"" + isVisible() + "\">\n";

		initProperties();
		synchronized( properties )
		{
			for( String key : properties.keySet() )
			{
				value += "\t\t\t<EDGEPROPERTY Key=\"" + key + "\" Value=\"" + getProperty( key ) + "\" />\n";
			}
		}

		value += "\t\t</DNVEDGE>\n";

		return value;
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
		checkString = " Length=\"";
		if( line.contains( checkString ) )
		{
			tempLine = line.substring( line.indexOf( checkString ) + checkString.length() );
			restingDistance = Float.parseFloat( tempLine.substring( 0, tempLine.indexOf( "\"" ) ) );
		}
		else
		{
			restingDistance = DEFAULT_RESTING_DISTANCE;
		}
		checkString = " K=\"";
		if( line.contains( checkString ) )
		{
			tempLine = line.substring( line.indexOf( checkString ) + checkString.length() );
			K = Float.parseFloat( tempLine.substring( 0, tempLine.indexOf( "\"" ) ) );
		}
		else
		{
			K = DEFAULT_K;
		}
		checkString = " From=\"";
		tempLine = line.substring( line.indexOf( checkString ) + checkString.length() );
		fromId = Integer.parseInt( tempLine.substring( 0, tempLine.indexOf( "\"" ) ) );
		checkString = " To=\"";
		tempLine = line.substring( line.indexOf( checkString ) + checkString.length() );
		toId = Integer.parseInt( tempLine.substring( 0, tempLine.indexOf( "\"" ) ) );
		checkString = " Label=\"";
		if( line.contains( checkString ) )
		{
			tempLine = line.substring( line.indexOf( checkString ) + checkString.length() );
			label = tempLine.substring( 0, tempLine.indexOf( "\"" ) );
			if( label.equals( "" ) )
			{
				label = null;
			}
		}
		checkString = " Directional=\"";
		if( line.contains( checkString ) )
		{
			tempLine = line.substring( line.indexOf( checkString ) + checkString.length() );
			directional = Boolean.parseBoolean( tempLine.substring( 0, tempLine.indexOf( "\"" ) ) );
		}
		checkString = " BBid=\"";
		if( line.contains( checkString ) )
		{
			tempLine = line.substring( line.indexOf( checkString ) + checkString.length() );
			bbId = tempLine.substring( 0, tempLine.indexOf( "\"" ) );
		}
		checkString = " HasSetColor=\"";
		if( line.contains( checkString ) )
		{
			tempLine = line.substring( line.indexOf( checkString ) + checkString.length() );
			hasSetColor = Boolean.parseBoolean( tempLine.substring( 0, tempLine.indexOf( "\"" ) ) );
		}
		checkString = " Color=\"";
		if( line.contains( checkString ) )
		{
			tempLine = line.substring( line.indexOf( checkString ) + checkString.length() );
			setColor( tempLine.substring( 0, tempLine.indexOf( "\"" ) ) );

			if( color != null && color.equals( Vector3D.ZERO ) && !hasSetColor )
			{
				color = null;
			}
		}

		checkString = " Type=\"";
		if( line.contains( checkString ) )
		{
			tempLine = line.substring( line.indexOf( checkString ) + checkString.length() );
			type = tempLine.substring( 0, tempLine.indexOf( "\"" ) );
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

		checkString = " Thickness=\"";
		if( line.contains( checkString ) )
		{
			tempLine = line.substring( line.indexOf( checkString ) + checkString.length() );
			tempLine = tempLine.substring( 0, tempLine.indexOf( "\"" ) );
			if( !tempLine.equals( "null" ) )
			{
				thickness = Float.parseFloat( tempLine );
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
	}

	/**
	 * Gets the from id.
	 * 
	 * @return the from id
	 */
	public int getFromId()
	{
		return fromId;
	}

	/**
	 * Sets the from id.
	 * 
	 * @param fromId
	 *            the new from id
	 */
	public void setFromId( int fromId )
	{
		this.fromId = fromId;
	}

	/**
	 * Gets the to id.
	 * 
	 * @return the to id
	 */
	public int getToId()
	{
		return toId;
	}

	/**
	 * Sets the to id.
	 * 
	 * @param toId
	 *            the new to id
	 */
	public void setToId( int toId )
	{
		this.toId = toId;
	}

	/**
	 * Sets the from.
	 * 
	 * @param from
	 *            the new from
	 */
	public void setFrom( DNVNode from )
	{
		this.from = from;
		this.fromId = from.getId();
	}

	/**
	 * Sets the to.
	 * 
	 * @param to
	 *            the new to
	 */
	public void setTo( DNVNode to )
	{
		this.to = to;
		this.toId = to.getId();
	}

	/**
	 * Checks if is directional.
	 * 
	 * @return true, if is directional
	 */
	public boolean isDirectional()
	{
		return directional;
	}

	/**
	 * Sets the directional.
	 * 
	 * @param directional
	 *            the new directional
	 */
	public void setDirectional( boolean directional )
	{
		this.directional = directional;
	}

	/*
	 * DEBUG: Show resting distance for edges.
	 * 
	 * public String getLabel() { if( super.getLabel().equals( "" ) ) { return
	 * "" + getRestingDistance(); }
	 * 
	 * return super.getLabel(); }
	 */
	/**
	 * Gets the edge decorator.
	 * 
	 * @return the edge decorator
	 */
	public EdgeDecorator getEdgeDecorator()
	{
		return edgeDecorator;
	}

	/**
	 * Sets the edge decorator.
	 * 
	 * @param edgeDecorator
	 *            the new edge decorator
	 */
	public void setEdgeDecorator( EdgeDecorator edgeDecorator )
	{
		this.edgeDecorator = edgeDecorator;
		if( edgeDecorator != null )
		{
			setLabel( edgeDecorator.getLabel() );
			setColor( edgeDecorator.getColor() );
		}
	}

	/**
	 * Gets the thickness.
	 * 
	 * @return the thickness
	 */
	public float getThickness()
	{
		return thickness;
	}

	/**
	 * Sets the thickness.
	 * 
	 * @param thickness
	 *            the new thickness
	 */
	public void setThickness( float thickness )
	{
		this.thickness = thickness;
	}

}
