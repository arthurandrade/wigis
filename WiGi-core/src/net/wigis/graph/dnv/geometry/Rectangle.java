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

package net.wigis.graph.dnv.geometry;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;

import net.wigis.graph.ImageRenderer;
import net.wigis.graph.PaintBean;
import net.wigis.graph.dnv.utilities.Vector2D;
import net.wigis.graph.dnv.utilities.Vector3D;

// TODO: Auto-generated Javadoc
/**
 * The Class Rectangle.
 * 
 * @author Brynjar Gretarsson
 */
public class Rectangle extends Geometric
{

	/** The top left. */
	private Vector2D topLeft;

	/** The bottom right. */
	private Vector2D bottomRight;

	/** The color. */
	private Vector3D color;

	/** The thickness. */
	private float thickness;

	/** The fill. */
	private boolean fill;

	/** The alpha. */
	private float alpha;

	/**
	 * Instantiates a new rectangle.
	 * 
	 * @param topLeft
	 *            the top left
	 * @param bottomRight
	 *            the bottom right
	 * @param thickness
	 *            the thickness
	 * @param color
	 *            the color
	 * @param alpha
	 *            the alpha
	 * @param fill
	 *            the fill
	 */
	public Rectangle( Vector2D topLeft, Vector2D bottomRight, float thickness, Vector3D color, float alpha, boolean fill )
	{
		this.topLeft = new Vector2D( topLeft );
		this.bottomRight = new Vector2D( bottomRight );
		this.thickness = thickness;
		this.color = new Vector3D( color );
		this.fill = fill;
		this.alpha = alpha;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.wigis.graph.dnv.geometry.Geometric#draw(java.awt.Graphics2D,
	 * net.wigis.graph.PaintBean, double, double, double, double, double,
	 * double, double, double, double, double, boolean)
	 */
	@Override
	public void draw( Graphics2D g2d, PaintBean pb, double minXPercent, double maxXPercent, double minYPercent, double maxYPercent,
			double globalMinX, double globalMaxX, double globalMinY, double globalMaxY, double width, double height, boolean overview )
	{
		Vector2D screenTopLeft = ImageRenderer.transformPosition( globalMinX, globalMaxX, globalMinY, globalMaxY, minXPercent, maxXPercent,
				minYPercent, maxYPercent, width, height, topLeft );
		Vector2D screenBottomRight = ImageRenderer.transformPosition( globalMinX, globalMaxX, globalMinY, globalMaxY, minXPercent, maxXPercent,
				minYPercent, maxYPercent, width, height, bottomRight );
		Color oldColor = g2d.getColor();
		Stroke oldStroke = g2d.getStroke();
		g2d.setColor( new Color( color.getX(), color.getY(), color.getZ(), alpha ) );
		g2d.setStroke( new BasicStroke( thickness ) );
		int x = (int)Math.round( screenTopLeft.getX() );
		int y = (int)Math.round( screenTopLeft.getY() );
		int rectWidth = (int)Math.round( screenBottomRight.getX() - screenTopLeft.getX() );
		int rectHeight = (int)Math.round( screenBottomRight.getY() - screenTopLeft.getY() );
		if( x < 0 )
		{
			rectWidth += x;
			x = 0;
		}
		if( y < 0 )
		{
			rectHeight += y;
			y = 0;
		}
		if( x + rectWidth > width )
		{
			rectWidth = (int)width - x - 1;
		}
		if( y + rectHeight > height )
		{
			rectHeight = (int)height - y - 1;
		}
		if( fill )
		{
			g2d.fillRect( x, y, rectWidth, rectHeight );
		}
		else
		{
			g2d.drawRect( x, y, rectWidth, rectHeight );
		}
		// if( !overview )
		// {
		// System.out.println( "Rectangle left: " + x );
		// System.out.println( "Rectangle top: " + y );
		// System.out.println( "Rectangle width: " + rectWidth );
		// System.out.println( "Rectangle height: " + rectHeight );
		// System.out.println( "Rectangle color: " + g2d.getColor() );
		// System.out.println( "----------------------------------------" );
		// }

		// g2d.setColor( Color.black );
		// g2d.drawRect( x, y, rectWidth, rectHeight );

		g2d.setColor( oldColor );
		g2d.setStroke( oldStroke );
	}

	/**
	 * Gets the top left.
	 * 
	 * @return the top left
	 */
	public Vector2D getTopLeft()
	{
		return topLeft;
	}

	/**
	 * Sets the top left.
	 * 
	 * @param topLeft
	 *            the new top left
	 */
	public void setTopLeft( Vector2D topLeft )
	{
		this.topLeft = topLeft;
	}

	/**
	 * Sets the top left.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 */
	public void setTopLeft( float x, float y )
	{
		this.topLeft.set( x, y );
	}

	/**
	 * Gets the bottom right.
	 * 
	 * @return the bottom right
	 */
	public Vector2D getBottomRight()
	{
		return bottomRight;
	}

	/**
	 * Sets the bottom right.
	 * 
	 * @param bottomRight
	 *            the new bottom right
	 */
	public void setBottomRight( Vector2D bottomRight )
	{
		this.bottomRight = bottomRight;
	}

	/**
	 * Sets the bottom right.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 */
	public void setBottomRight( float x, float y )
	{
		this.bottomRight.set( x, y );
	}

}
