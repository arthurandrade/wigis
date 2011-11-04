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

import java.util.HashMap;
import java.util.Map;

import net.wigis.graph.dnv.utilities.GraphFunctions;
import net.wigis.graph.dnv.utilities.Vector3D;

// TODO: Auto-generated Javadoc
/**
 * The Class DNVEntity.
 * 
 * @author Brynjar Gretarsson
 */
public class DNVEntity
{
	// public static int ID = 0;
	public static final String LABEL_RECTANGLE = "LabelRectangle";
	/** The Constant NO_COLOR. */
	public static final Vector3D NO_COLOR = new Vector3D( -1, -1, -1 );

	/** The id. */
	protected Integer id;

	/** The bb id. */
	protected String bbId = "";

	/** The type. */
	protected String type = null;

	/** The label. */
	protected String label = null;

	/** The has set color. */
	protected boolean hasSetColor = false;

	/** The label color. */
	protected Vector3D labelColor = null;

	/** The label outline color. */
	protected Vector3D labelOutlineColor = null;

	protected Vector3D highlightColor = null;

	/** The label size. */
	protected Integer labelSize = null;

	/** The alpha. */
	protected float alpha = 1;

	/** The visible. */
//	protected boolean visible = true;

	// A number representing the level of detail
	/** The level. */
	protected int level = 0;

	// A link to the graph containing the entity
	/** The graph. */
	protected DNVGraph graph;

	/** The color. */
	protected Vector3D color = null;

	/** The outline color. */
	protected Vector3D outlineColor = null;

	// Properties are persisted into DNV file since they are only strings
	/** The properties. */
	protected HashMap<String, String> properties = null;
	// Attributes are Objects and thus are not persisted into DNV file
	/** The attributes. */
	protected HashMap<String, Object> attributes = null;

	// alex
	public DNVEntity( )
	{

	}
	
	/**
	 * Instantiates a new dNV entity.
	 * 
	 * @param graph
	 *            the graph
	 */
	public DNVEntity( DNVGraph graph )
	{
		if( graph != null )
		{
			id = graph.getIdGenerator().getNextId();
			setGraph( graph );
		}
		// else
		// {
		// System.out.println( "WARNING - DNVEntity constructur : graph is null"
		// );
		// }
	}

	
	public Map<String,String> getProperties()
	{
		return properties;
	}


	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	public Integer getId()
	{
		return id;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id
	 *            the new id
	 */
	public void setId( Integer id )
	{
		boolean visible = isVisible();
		boolean selected = isSelected();
		boolean highlighted = isHighlighted();
		this.id = id;
		if( graph.getIdGenerator().getCurrentId() <= id )
		{
			graph.getIdGenerator().setNextId( id + 1 );
		}
		setVisible( visible );
		setSelected( selected );
		setHighlighted( highlighted );
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
	 * Sets the graph.
	 * 
	 * @param graph
	 *            the new graph
	 */
	public void setGraph( DNVGraph graph )
	{
		this.graph = graph;
	}

	/**
	 * Gets the level.
	 * 
	 * @return the level
	 */
	public int getLevel()
	{
		return level;
	}

	/**
	 * Sets the level.
	 * 
	 * @param level
	 *            the new level
	 */
	public void setLevel( int level )
	{
		this.level = level;
	}

	/**
	 * Serialize.
	 * 
	 * @return the string
	 */
	public String serialize()
	{
		return "";
	}

	/**
	 * Gets the bb id.
	 * 
	 * @return the bb id
	 */
	public String getBbId()
	{
		return bbId;
	}

	/**
	 * Sets the bb id.
	 * 
	 * @param bbId
	 *            the new bb id
	 */
	public void setBbId( String bbId )
	{
		// if( Settings.BLACKBOOK )
		this.bbId = bbId;
		if( graph != null )
		{
			graph.updateBbId( this );
		}
	}

	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	public String getType()
	{
		if( type == null )
		{
			return "";
		}

		return type;
	}

	/**
	 * Sets the type.
	 * 
	 * @param type
	 *            the new type
	 */
	public void setType( String type )
	{
		this.type = type;
	}

	/**
	 * Gets the label.
	 * 
	 * @return the label
	 */
	public String getLabel()
	{
		if( this.label == null )
		{
			return "";
		}

		return this.label;
	}

	/**
	 * Sets the label.
	 * 
	 * @param label
	 *            the new label
	 */
	public void setLabel( String label )
	{
		if( label != null )
			this.label = removeSpecialCharacters( label ).trim();
	}

	/**
	 * Removes the icelandic characters.
	 * 
	 * @param value
	 *            the value
	 * @return the string
	 */
	public static String removeSpecialCharacters( String value )
	{
		value = value.replaceAll( "á", "a" );
		value = value.replaceAll( "ð", "d" );
		value = value.replaceAll( "é", "e" );
		value = value.replaceAll( "í", "i" );
		value = value.replaceAll( "ó", "o" );
		value = value.replaceAll( "ú", "u" );
		value = value.replaceAll( "ý", "y" );
		value = value.replaceAll( "þ", "th" );
		value = value.replaceAll( "æ", "ae" );
		value = value.replaceAll( "ö", "o" );
		value = value.replaceAll( "Á", "A" );
		value = value.replaceAll( "Ð", "D" );
		value = value.replaceAll( "É", "E" );
		value = value.replaceAll( "Í", "I" );
		value = value.replaceAll( "Ó", "O" );
		value = value.replaceAll( "Ú", "U" );
		value = value.replaceAll( "Ý", "Y" );
		value = value.replaceAll( "Þ", "Th" );
		value = value.replaceAll( "Æ", "AE" );
		value = value.replaceAll( "Ö", "O" );
		value = value.replaceAll( " & ", " and " );
		return value;
	}

	/**
	 * Sets the color.
	 * 
	 * @param datasourceColor
	 *            the new color
	 */
	public void setColor( String colorStr )
	{
		if( colorStr != null )
		{
			if( colorStr.startsWith( "#" ) || colorStr.length() == 6 )
			{
				try
				{
					setColor( GraphFunctions.convertColor( colorStr ) );
				}
				catch( NumberFormatException nfe )
				{
					throw new IllegalArgumentException( "Invalid color string [" + colorStr + "]" );					
				}
			}
			else if( colorStr.startsWith( "[" ) )
			{
				setColor( new Vector3D( colorStr ) );
			}
			else if( colorStr.equals( "null" ) )
			{
				if( this instanceof DNVEdge )
				{
					color =  null;
				}
				else
				{
					setColor( new Vector3D( 0, 0, 0 ) );
				}
			}
			else
			{
				throw new IllegalArgumentException( "Invalid color string [" + colorStr + "]" );
			}
		}
		else
		{
			if( this instanceof DNVEdge )
			{
				color =  null;
			}
			else
			{
				setColor( new Vector3D( 0, 0, 0 ) );				
			}
		}

			
	}

	/**
	 * Gets the color.
	 * 
	 * @return the color
	 */
	public Vector3D getColor()
	{
		return color;
	}

	/**
	 * Sets the color.
	 * 
	 * @param color
	 *            the new color
	 */
	public void setColor( Vector3D color )
	{
		if( color != null )
		{
			hasSetColor = true;
			if( this.color == null )
			{
				this.color = new Vector3D();
			}
			this.color.set( color );
			
			if( color.getX() > 1 )
				color.setX( 1 );
			if( color.getY() > 1 )
				color.setY( 1 );
			if( color.getZ() > 1 )
				color.setZ( 1 );
			if( color.getX() < 0 )
				color.setX( 0 );
			if( color.getY() < 0 )
				color.setY( 0 );
			if( color.getZ() < 0 )
				color.setZ( 0 );
		}
	}

	/**
	 * Sets the color.
	 * 
	 * @param red
	 *            the red
	 * @param green
	 *            the green
	 * @param blue
	 *            the blue
	 */
	public void setColor( float red, float green, float blue )
	{
		hasSetColor = true;
		if( this.color == null )
		{
			this.color = new Vector3D();
		}
		color.setX( red );
		color.setY( green );
		color.setZ( blue );
	}

	/**
	 * Gets the outline color.
	 * 
	 * @return the outline color
	 */
	public Vector3D getOutlineColor()
	{
		return outlineColor;
	}

	/**
	 * Sets the outline color.
	 * 
	 * @param datasourceColor
	 *            the new outline color
	 */
	public void setOutlineColor( String datasourceColor )
	{
		setOutlineColor( GraphFunctions.convertColor( datasourceColor ) );
	}

	/**
	 * Sets the outline color.
	 * 
	 * @param outlineColor
	 *            the new outline color
	 */
	public void setOutlineColor( Vector3D outlineColor )
	{
		if( outlineColor == null )
		{
			this.outlineColor = outlineColor;
		}
		else
		{
			if( this.outlineColor == null )
			{
				this.outlineColor = new Vector3D();
			}
			this.outlineColor.set( outlineColor );
		}
	}

	/**
	 * Checks if is force label.
	 * 
	 * @return true, if is force label
	 */
	public boolean isForceLabel()
	{
		return graph.getMustDrawLabels( level ).containsKey( id );
	}

	/**
	 * Sets the force label.
	 * 
	 * @param forceLabel
	 *            the new force label
	 */
	public void setForceLabel( boolean forceLabel )
	{
		if( isForceLabel() != forceLabel )
		{
			if( forceLabel )
			{
				graph.addMustDrawLabel( level, this );
			}
			else
			{
				graph.removeMustDrawLabel( level, this );
			}
		}
	}

	/**
	 * Gets the label color.
	 * 
	 * @return the label color
	 */
	public Vector3D getLabelColor()
	{
		return labelColor;
	}

	/**
	 * Sets the label color.
	 * 
	 * @param datasourceColor
	 *            the new label color
	 */
	public void setLabelColor( String datasourceColor )
	{
		setLabelColor( GraphFunctions.convertColor( datasourceColor ) );
	}

	/**
	 * Sets the label color.
	 * 
	 * @param labelColor
	 *            the new label color
	 */
	public void setLabelColor( Vector3D labelColor )
	{
		if( labelColor == null )
		{
			this.labelColor = labelColor;
		}
		else
		{
			if( this.labelColor == null )
			{
				this.labelColor = new Vector3D();
			}
			this.labelColor.set( labelColor );
		}
	}

	/**
	 * Gets the label outline color.
	 * 
	 * @return the label outline color
	 */
	public Vector3D getLabelOutlineColor()
	{
		return labelOutlineColor;
	}

	/**
	 * Sets the label outline color.
	 * 
	 * @param datasourceColor
	 *            the new label outline color
	 */
	public void setLabelOutlineColor( String datasourceColor )
	{
		setLabelOutlineColor( GraphFunctions.convertColor( datasourceColor ) );
	}

	/**
	 * Sets the label outline color.
	 * 
	 * @param labelOutlineColor
	 *            the new label outline color
	 */
	public void setLabelOutlineColor( Vector3D labelOutlineColor )
	{
		if( labelOutlineColor == null )
		{
			this.labelOutlineColor = labelOutlineColor;
		}
		else
		{
			if( this.labelOutlineColor == null )
			{
				this.labelOutlineColor = new Vector3D();
			}
			this.labelOutlineColor.set( labelOutlineColor );
		}
	}

	/**
	 * Gets the label size.
	 * 
	 * @return the label size
	 */
	public Integer getLabelSize()
	{
		return labelSize;
	}

	/**
	 * Sets the label size.
	 * 
	 * @param labelSize
	 *            the new label size
	 */
	public void setLabelSize( Integer labelSize )
	{
		if( this.labelSize != labelSize )
		{
			removeAttribute( LABEL_RECTANGLE );
			this.labelSize = labelSize;
		}
	}

	/**
	 * Gets the alpha.
	 * 
	 * @return the alpha
	 */
	public float getAlpha()
	{
		return alpha;
	}

	/**
	 * Sets the alpha.
	 * 
	 * @param alpha
	 *            the new alpha
	 */
	public void setAlpha( float alpha )
	{
		this.alpha = alpha;
	}

	/**
	 * Inits the properties.
	 */
	protected void initProperties()
	{
		if( properties == null )
		{
			properties = new HashMap<String, String>();
		}
	}

	/**
	 * Removes the property.
	 * 
	 * @param key
	 *            the key
	 */
	public void removeProperty( String key )
	{
		initProperties();
		synchronized( properties )
		{
			properties.remove( key );
		}

	}

	/**
	 * Sets the property.
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public void setProperty( String key, String value )
	{
		initProperties();
		synchronized( properties )
		{
			properties.put( key, value );
		}
	}

	/**
	 * Gets the property.
	 * 
	 * @param key
	 *            the key
	 * @return the property
	 */
	public String getProperty( String key )
	{
		initProperties();
		synchronized( properties )
		{
			return properties.get( key );
		}
	}

	/**
	 * Checks for property.
	 * 
	 * @param property
	 *            the property
	 * @return true, if successful
	 */
	public boolean hasProperty( String property )
	{
		initProperties();
		synchronized( properties )
		{
			return properties.containsKey( property );
		}
	}

	/**
	 * Inits the attributes.
	 */
	protected void initAttributes()
	{
		if( attributes == null )
		{
			attributes = new HashMap<String, Object>();
		}
	}

	/**
	 * Removes the attribute.
	 * 
	 * @param key
	 *            the key
	 */
	public void removeAttribute( String key )
	{
		initAttributes();
		synchronized( attributes )
		{
			attributes.remove( key );
		}

	}

	/**
	 * Sets the attribute.
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public void setAttribute( String key, Object value )
	{
		initAttributes();
		synchronized( attributes )
		{
			attributes.put( key, value );
		}
	}

	/**
	 * Gets the attribute.
	 * 
	 * @param key
	 *            the key
	 * @return the attribute
	 */
	public Object getAttribute( String key )
	{
		initAttributes();
		synchronized( attributes )
		{
			return attributes.get( key );
		}
	}

	/**
	 * Checks for attribute.
	 * 
	 * @param attribute
	 *            the attribute
	 * @return true, if successful
	 */
	public boolean hasAttribute( String attribute )
	{
		initAttributes();
		synchronized( attributes )
		{
			return attributes.containsKey( attribute );
		}
	}

	/**
	 * Checks if is selected.
	 * 
	 * @return true, if is selected
	 */
	public boolean isSelected()
	{
		if( graph != null )
			return graph.isSelected( this, level );

		return false;
	}

	/**
	 * Sets the selected.
	 * 
	 * @param selected
	 *            the new selected
	 */
	public void setSelected( boolean selected )
	{
		if( selected )
		{
			if( !isSelected() )
			{
				setProperty( "SelectedTime", "" + System.currentTimeMillis() );
			}
		}
		else
		{
			removeProperty( "SelectedTime" );
		}

		if( graph != null )
		{
			graph.setSelected( this, level, selected );
		}
		else
		{
			System.out.println( "Graph is null for entity with id = " + id );
		}
	}

	public boolean isHighlighted()
	{
		if( graph != null )
			return graph.isHighlighted( this, level );

		return false;
	}
	
	public void setHighlighted( boolean highlighted )
	{
		if( graph != null )
		{
			graph.setHighlighted( this, level, highlighted );
		}
	}
	
	/**
	 * Checks if is visible.
	 * 
	 * @return true, if is visible
	 */
	public boolean isVisible()
	{
		if( graph != null )
			return graph.isVisible( this, level );

		return false;
	}

	/**
	 * Sets the visible.
	 * 
	 * @param visible
	 *            the new visible
	 */
	public void setVisible( boolean visible )
	{
		if( graph != null )
		{
			graph.setVisible( this, level, visible );
		}
		else
		{
//			System.out.println( "Graph is null for entity with id = " + id + " and label '" + label + "'" );
		}
	}
	
	public boolean hasSetColor()
	{
		return hasSetColor;
	}

	public Vector3D getHighlightColor()
	{
		return highlightColor;
	}

	public void setHighlightColor( Vector3D highlightColor )
	{
		this.highlightColor = highlightColor;
	}
	
	public void updateEntityTimeInGraph()
	{
		if( graph != null )
		{
			graph.updateTimeForEntity( level, this );
		}
	}
	
	
	public void updateEntityDKTimeInGraph()
	{
		if( graph != null )
		{
			graph.updateDKTimeForEntity( level, this );
		}
	}
}
