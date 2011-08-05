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
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

import net.wigis.graph.ImageRenderer;
import net.wigis.graph.PaintBean;
import net.wigis.graph.dnv.utilities.Vector2D;
import net.wigis.graph.dnv.utilities.Vector3D;

// TODO: Auto-generated Javadoc
/**
 * The Class Circle.
 * 
 * @author Brynjar Gretarsson
 */
public class Circle extends Geometric
{

	/** The center. */
	private Vector2D center;

	/** The inner radius. */
	private float innerRadius;

	/** The outer radius. */
	private float outerRadius;

	/** The color. */
	private Vector3D color;

	/** The thickness. */
	private float thickness;

	/** The fill. */
	private boolean fill;

	private boolean isScreenPosition;

	/** The alpha. */
	private float alpha;

	/**
	 * Instantiates a new circle.
	 * 
	 * @param center
	 *            the center
	 * @param innerRadius
	 *            the inner radius
	 * @param outerRadius
	 *            the outer radius
	 * @param thickness
	 *            the thickness
	 * @param color
	 *            the color
	 * @param alpha
	 *            the alpha
	 * @param fill
	 *            the fill
	 */
	public Circle( Vector2D center, float innerRadius, float outerRadius, float thickness, Vector3D color, float alpha, boolean fill, boolean isScreenPosition )
	{
		this.center = new Vector2D( center );
		this.innerRadius = innerRadius;
		this.outerRadius = outerRadius;
		this.thickness = thickness;
		this.color = new Vector3D( color );
		this.fill = fill;
		this.alpha = alpha;
		this.isScreenPosition = isScreenPosition;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.wigis.graph.dnv.geometry.Geometric#draw(java.awt.Graphics2D,
	 * net.wigis.graph.PaintBean, double, double, double, double, double,
	 * double, double, double, double, double, boolean)
	 */
	@Override
	public void draw( Graphics2D g2d, PaintBean pb, double minXPercent, double maxXPercent, double minYPercent, double maxYPercent, double globalMinX, double globalMaxX,
			double globalMinY, double globalMaxY, double width, double height, boolean overview )
	{
		Vector2D screenCenter;
		if( isScreenPosition )
		{
			screenCenter = center;
		}
		else
		{
			screenCenter = ImageRenderer.transformPosition( globalMinX, globalMaxX, globalMinY, globalMaxY, minXPercent, maxXPercent,
				minYPercent, maxYPercent, width, height, center );
		}

		Color oldColor = g2d.getColor();
		Stroke oldStroke = g2d.getStroke();
		g2d.setColor( new Color( color.getX(), color.getY(), color.getZ(), alpha ) );
		g2d.setStroke( new BasicStroke( thickness ) );
		int x = (int)Math.round( screenCenter.getX() );
		int y = (int)Math.round( screenCenter.getY() );
		int outerCircleWidth = (int)Math.round( outerRadius * 2.0f );
		int innerCircleWidth = (int)Math.round( innerRadius * 2.0f );
		Ellipse2D.Float outerCircle = new Ellipse2D.Float( x - ( outerCircleWidth / 2.0f ), y - ( outerCircleWidth / 2.0f ), outerCircleWidth,
				outerCircleWidth );
		Ellipse2D.Float innerCircle = new Ellipse2D.Float( x - ( innerCircleWidth / 2.0f ), y - ( innerCircleWidth / 2.0f ), innerCircleWidth,
				innerCircleWidth );
		Area innerCircleArea = new Area( innerCircle );
		Area ring = new Area( outerCircle );
		ring.subtract( innerCircleArea );
		if( fill )
		{
			g2d.fill( ring );
		}
		else
		{
			g2d.draw( ring );
		}

		g2d.setColor( oldColor );
		g2d.setStroke( oldStroke );

	}

	/**
	 * Gets the center.
	 * 
	 * @return the center
	 */
	public Vector2D getCenter()
	{
		return center;
	}

	/**
	 * Sets the center.
	 * 
	 * @param center
	 *            the new center
	 */
	public void setCenter( Vector2D center )
	{
		this.center = center;
	}

	/**
	 * Sets the center.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 */
	public void setCenter( float x, float y )
	{
		this.center.set( x, y );
	}

}
