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
 * A 3D vector (or point). 
 * Also used for color. (x=red,y=green,z=blue)
 * 
 * @author Brynjar Gretarsson
 */
public class Vector3D implements Serializable
{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 9006032193061364325L;

	/** The Constant ZERO. */
	public static final Vector3D ZERO = new Vector3D( 0, 0, 0 );

	/** The x. */
	private float x;

	/** The y. */
	private float y;

	/** The z. */
	private float z;

	/**
	 * Instantiates a new vector3 d.
	 */
	public Vector3D()
	{
		x = y = z = 0;
	}

	/**
	 * Instantiates a new vector3 d.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @param z
	 *            the z
	 */
	public Vector3D( float x, float y, float z )
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Instantiates a new vector3 d.
	 * 
	 * @param copy
	 *            the copy
	 */
	public Vector3D( Vector3D copy )
	{
		if( copy != null )
		{
			this.x = copy.getX();
			this.y = copy.getY();
			this.z = copy.getZ();
		}
	}

	/**
	 * Instantiates a new vector3 d.
	 * 
	 * @param position
	 *            the position
	 */
	public Vector3D( String position )
	{
		int index1 = position.indexOf( "[" );
		int index2 = position.indexOf( "," );
		int index3 = position.indexOf( ",", index2 + 1 );
		int index4 = position.indexOf( "]", index3 + 1 );

		x = Float.parseFloat( position.substring( index1 + 1, index2 ) );
		y = Float.parseFloat( position.substring( index2 + 1, index3 ) );
		z = Float.parseFloat( position.substring( index3 + 1, index4 ) );
	}

	/**
	 * Gets the x.
	 * 
	 * @return the x
	 */
	public float getX()
	{
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
		this.x = x;
	}

	/**
	 * Gets the y.
	 * 
	 * @return the y
	 */
	public float getY()
	{
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
		this.y = y;
	}

	/**
	 * Adds the.
	 * 
	 * @param addition
	 *            the addition
	 * @return the vector3 d
	 */
	public Vector3D add( Vector3D addition )
	{
		x += addition.getX();
		y += addition.getY();
		z += addition.getZ();
		return this;
	}

	/**
	 * Gets the z.
	 * 
	 * @return the z
	 */
	public float getZ()
	{
		return z;
	}

	/**
	 * Sets the z.
	 * 
	 * @param z
	 *            the new z
	 */
	public void setZ( float z )
	{
		this.z = z;
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
		array[2] = z;

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
		array[2] = z;

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
		array[2] = z;

		return array;
	}

	/**
	 * To float array4.
	 * 
	 * @return the float[]
	 */
	public float[] toFloatArray4()
	{
		float[] array = new float[4];

		array[0] = x;
		array[1] = y;
		array[2] = z;
		array[3] = 1;

		return array;
	}

	/**
	 * Length.
	 * 
	 * @return the float
	 */
	public float length()
	{
		return (float)Math.sqrt( ( x * x ) + ( y * y ) + ( z * z ) );
	}

	/**
	 * Normalize.
	 * 
	 * @return the vector3 d
	 */
	public Vector3D normalize()
	{
		float length = length();
		x = x / length;
		y = y / length;
		z = z / length;
		return this;
	}

	/**
	 * Cross product.
	 * 
	 * @param b
	 *            the b
	 * @return the vector3 d
	 */
	public Vector3D crossProduct( Vector3D b )
	{
		float tempX = y * b.getZ() - z * b.getY();
		float tempY = z * b.getX() - x * b.getZ();
		float tempZ = x * b.getY() - y * b.getX();

		x = tempX;
		y = tempY;
		z = tempZ;

		return this;
	}

	/**
	 * Dot product.
	 * 
	 * @param scalar
	 *            the scalar
	 * @return the vector3 d
	 */
	public Vector3D dotProduct( float scalar )
	{
		x *= scalar;
		y *= scalar;
		z *= scalar;

		return this;
	}

	/**
	 * Dot product.
	 * 
	 * @param b
	 *            the b
	 * @return the float
	 */
	public float dotProduct( Vector3D b )
	{
		return x * b.getX() + y * b.getY() + z * b.getZ();
	}

	/**
	 * Angle.
	 * 
	 * @param b
	 *            the b
	 * @return the float
	 */
	public float angle( Vector3D b )
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
	 * @return the vector3 d
	 */
	public Vector3D subtract( Vector3D b )
	{
		x -= b.getX();
		y -= b.getY();
		z -= b.getZ();

		return this;
	}

	/**
	 * Distance.
	 * 
	 * @param b
	 *            the b
	 * @return the float
	 */
	public float distance( Vector3D b )
	{
		return (float)Math.sqrt( Math.pow( x - b.getX(), 2 ) + Math.pow( y - b.getY(), 2 ) + Math.pow( z - b.getZ(), 2 ) );
	}

	/**
	 * Sets the.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @param z
	 *            the z
	 */
	public void set( float x, float y, float z )
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Sets the.
	 * 
	 * @param value
	 *            the value
	 */
	public void set( Vector3D value )
	{
		x = value.getX();
		y = value.getY();
		z = value.getZ();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "[" + x + " , " + y + " , " + z + "]";
	}

	/**
	 * Clear.
	 */
	public void clear()
	{
		x = y = z = 0.0f;
	}

	/**
	 * Equals.
	 * 
	 * @param compare
	 *            the compare
	 * @return true, if successful
	 */
	public boolean equals( Vector3D compare )
	{
		return compare.getX() == x && compare.getY() == y && compare.getZ() == z;
	}

	/**
	 * To hex color.
	 * 
	 * @return the string
	 */
	public String toHexColor()
	{
		String output = "#";
		String temp = Integer.toHexString( (int)( x * 255 ) );
		temp = fixHexString( temp );
		output += temp;
		temp = Integer.toHexString( (int)( y * 255 ) );
		temp = fixHexString( temp );
		output += temp;
		temp = Integer.toHexString( (int)( z * 255 ) );
		temp = fixHexString( temp );
		output += temp;

		System.out.println( toString() + " -> " + output );

		return output;
	}

	/**
	 * Fix hex string.
	 * 
	 * @param temp
	 *            the temp
	 * @return the string
	 */
	private String fixHexString( String temp )
	{
		if( temp.length() == 0 )
		{
			temp = "00";
		}
		else if( temp.length() == 1 )
		{
			temp = "0" + temp;
		}
		else if( temp.length() > 2 )
		{
			temp = temp.substring( temp.length() - 2 );
		}
		return temp;
	}

}
