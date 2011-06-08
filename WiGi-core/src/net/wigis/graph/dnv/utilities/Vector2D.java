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

package net.wigis.graph.dnv.utilities;

import java.io.Serializable;

// TODO: Auto-generated Javadoc
/**
 * A 2D vector (or point).
 * 
 * @author Brynjar Gretarsson
 */
public class Vector2D implements Serializable
{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6343394948970930468L;

	/** The Constant ZERO. */
	public static final Vector2D ZERO = new Vector2D( 0, 0 );

	/** The x. */
	private float x;

	/** The y. */
	private float y;

	/**
	 * Instantiates a new vector2 d.
	 */
	public Vector2D()
	{
		x = y = 0;
	}

	/**
	 * Instantiates a new vector2 d.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 */
	public Vector2D( float x, float y )
	{
		this.x = x;
		this.y = y;
	}

	/**
	 * Instantiates a new vector2 d.
	 * 
	 * @param copy
	 *            the copy
	 */
	public Vector2D( Vector2D copy )
	{
		if( copy != null )
		{
			this.x = copy.getX();
			this.y = copy.getY();
		}
	}

	/**
	 * Instantiates a new vector2 d.
	 * 
	 * @param position
	 *            the position
	 */
	public Vector2D( String position )
	{
		int index1 = position.indexOf( "[" );
		int index2 = position.indexOf( "," );
		int index3 = position.indexOf( ",", index2 + 1 );
		// Special case since we used to use 3D
		if( index3 != -1 )
		{
			int index4 = position.indexOf( "]", index3 + 1 );

			// Get the x from the Z coordinate (that's how we used to store it
			// in the 3D vector)
			// x = Float.parseFloat( position.substring( index1 + 1, index2 ) );
			y = Float.parseFloat( position.substring( index2 + 1, index3 ) );
			x = Float.parseFloat( position.substring( index3 + 1, index4 ) );
		}
		else
		{
			index3 = position.indexOf( "]", index2 + 1 );

			x = Float.parseFloat( position.substring( index1 + 1, index2 ) );
			y = Float.parseFloat( position.substring( index2 + 1, index3 ) );
		}
	}

	/**
	 * Gets the x.
	 * 
	 * @return the x
	 */
	public float getX()
	{
		if( Float.isNaN( x ) )
			x = 0;
		return x;
	}

	/**
	 * Sets the x.
	 * 
	 * @param x
	 *            the new x
	 */
	public void setX( float x )
	{
		if( Float.isNaN( x ) )
			x = 0;
		this.x = x;
	}

	/**
	 * Gets the y.
	 * 
	 * @return the y
	 */
	public float getY()
	{
		if( Float.isNaN( y ) )
			y = 0;
		return y;
	}

	/**
	 * Sets the y.
	 * 
	 * @param y
	 *            the new y
	 */
	public void setY( float y )
	{
		if( Float.isNaN( y ) )
			y = 0;
		this.y = y;
	}

	/**
	 * Adds the.
	 * 
	 * @param addition
	 *            the addition
	 * @return the vector2 d
	 */
	public Vector2D add( Vector2D addition )
	{
		return add( addition.getX(), addition.getY() );
	}

	/**
	 * Adds the.
	 * 
	 * @param x_addition
	 *            the x_addition
	 * @param y_addition
	 *            the y_addition
	 * @return the vector2 d
	 */
	public Vector2D add( float x_addition, float y_addition )
	{
		x += x_addition;
		y += y_addition;
		if( Float.isNaN( x ) )
			x = (float)Math.random();
		if( Float.isNaN( y ) )
			y = (float)Math.random();

		return this;
	}

	/**
	 * To array.
	 * 
	 * @return the float[]
	 */
	public float[] toArray()
	{
		float[] array = new float[3];

		array[0] = x;
		array[1] = y;

		return array;
	}

	/**
	 * To float array.
	 * 
	 * @return the float[]
	 */
	public float[] toFloatArray()
	{
		float[] array = new float[3];

		array[0] = x;
		array[1] = y;

		return array;
	}

	/**
	 * To float array.
	 * 
	 * @param array
	 *            the array
	 * @return the float[]
	 */
	public float[] toFloatArray( float[] array )
	{
		array[0] = x;
		array[1] = y;

		return array;
	}

	/**
	 * Length.
	 * 
	 * @return the float
	 */
	public float length()
	{
		return (float)Math.sqrt( ( x * x ) + ( y * y ) );
	}

	/**
	 * Normalize.
	 * 
	 * @return the vector2 d
	 */
	public Vector2D normalize()
	{
		float length = length();
		if( length != 0 )
		{
			x = x / length;
			y = y / length;
		}

		return this;
	}

	/**
	 * Cross product.
	 * 
	 * @param b
	 *            the b
	 * @return the float
	 */
	public float crossProduct( Vector2D b )
	{
		return x * b.getY() - y * b.getX();
	}

	/**
	 * Dot product.
	 * 
	 * @param scalar
	 *            the scalar
	 * @return the vector2 d
	 */
	public Vector2D dotProduct( float scalar )
	{
		x *= scalar;
		y *= scalar;

		return this;
	}

	/**
	 * Dot product.
	 * 
	 * @param b
	 *            the b
	 * @return the float
	 */
	public float dotProduct( Vector2D b )
	{
		return x * b.getX() + y * b.getY();
	}

	/**
	 * Angle.
	 * 
	 * @param b
	 *            the b
	 * @return the float
	 */
	public float angle( Vector2D b )
	{
		float product = dotProduct( b );
		product = product / length() / b.length();

		return (float)Math.acos( product );
	}

	/**
	 * Subtract.
	 * 
	 * @param b
	 *            the b
	 * @return the vector2 d
	 */
	public Vector2D subtract( Vector2D b )
	{
		x -= b.getX();
		y -= b.getY();

		return this;
	}

	/**
	 * Distance.
	 * 
	 * @param b
	 *            the b
	 * @return the float
	 */
	public float distance( Vector2D b )
	{
		return (float)Math.sqrt( Math.pow( x - b.getX(), 2 ) + Math.pow( y - b.getY(), 2 ) );
	}

	/**
	 * Sets the.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 */
	public void set( float x, float y )
	{
		this.x = x;
		this.y = y;
	}

	/**
	 * Sets the.
	 * 
	 * @param value
	 *            the value
	 */
	public void set( Vector2D value )
	{
		x = value.getX();
		y = value.getY();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "[" + x + " , " + y + "]";
	}

	/**
	 * Clear.
	 */
	public void clear()
	{
		x = y = 0.0f;
	}

	/**
	 * Equals.
	 * 
	 * @param compare
	 *            the compare
	 * @return true, if successful
	 */
	public boolean equals( Vector2D compare )
	{
		return compare.getX() == x && compare.getY() == y;
	}

}
