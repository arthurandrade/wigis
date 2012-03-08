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
 * The Class Line.
 * 
 * @author Brynjar Gretarsson
 */
public class Line extends Geometric
{

	/** The start. */
	private Vector2D start;

	/** The end. */
	private Vector2D end;

	/** The color. */
	private Vector3D color;

	/** The thickness. */
	private float thickness;

	/**
	 * Instantiates a new line.
	 * 
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 * @param thickness
	 *            the thickness
	 * @param color
	 *            the color
	 */
	public Line( Vector2D start, Vector2D end, float thickness, Vector3D color )
	{
		this.start = start;
		this.end = end;
		this.thickness = thickness;
		this.color = color;
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
		Color oldColor = g2d.getColor();
		Stroke oldStroke = g2d.getStroke();
		g2d.setColor( new Color( color.getX(), color.getY(), color.getZ() ) );
		Vector2D screenStart = ImageRenderer.transformPosition( globalMinX, globalMaxX, globalMinY, globalMaxY, minXPercent, maxXPercent,
				minYPercent, maxYPercent, width, height, start );
		Vector2D screenEnd = ImageRenderer.transformPosition( globalMinX, globalMaxX, globalMinY, globalMaxY, minXPercent, maxXPercent, minYPercent,
				maxYPercent, width, height, end );
		g2d.setStroke( new BasicStroke( thickness ) );
		g2d.drawLine( (int)Math.round( screenStart.getX() ), (int)Math.round( screenStart.getY() ), (int)Math.round( screenEnd.getX() ), (int)Math
				.round( screenEnd.getY() ) );
		g2d.setStroke( oldStroke );
		g2d.setColor( oldColor );
	}

	/**
	 * Gets the start.
	 * 
	 * @return the start
	 */
	public Vector2D getStart()
	{
		return start;
	}

	/**
	 * Sets the start.
	 * 
	 * @param start
	 *            the new start
	 */
	public void setStart( Vector2D start )
	{
		this.start = start;
	}

	/**
	 * Sets the start.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 */
	public void setStart( float x, float y )
	{
		this.start.set( x, y );
	}

	/**
	 * Gets the end.
	 * 
	 * @return the end
	 */
	public Vector2D getEnd()
	{
		return end;
	}

	/**
	 * Sets the end.
	 * 
	 * @param end
	 *            the new end
	 */
	public void setEnd( Vector2D end )
	{
		this.end = end;
	}

	/**
	 * Sets the end.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 */
	public void setEnd( float x, float y )
	{
		this.end.set( x, y );
	}
}
