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

import java.awt.Graphics2D;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.StringTokenizer;

import net.wigis.graph.ImageRenderer;
import net.wigis.graph.PaintBean;
import net.wigis.graph.data.utilities.StringUtils;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.utilities.Vector2D;
import net.wigis.graph.dnv.utilities.Vector3D;

// TODO: Auto-generated Javadoc
/**
 * The Class Text.
 * 
 * @author Brynjar Gretarsson
 */
public class Text extends Geometric
{

	/** The node. */
	private DNVNode node = new DNVNode( null );

	/** The label size. */
	private double labelSize;

	/** The bold. */
	private boolean bold;

	/** The outlined. */
	private boolean outlined;

	/** The draw background. */
	private boolean drawBackground;

	/** The scale on zoom. */
	private boolean scaleOnZoom;

	/** The curved label. */
	private boolean curvedLabel;

	/** The show on overview. */
	private boolean showOnOverview;
	
	/** Determines if the stored position is screen position or global position */
	private boolean screenPosition;

	/**
	 * Instantiates a new text.
	 * 
	 * @param text
	 *            the text
	 * @param position
	 *            the position
	 * @param outlineColor
	 *            the outline color
	 * @param color
	 *            the color
	 * @param labelSize
	 *            the label size
	 * @param bold
	 *            the bold
	 * @param outlined
	 *            the outlined
	 * @param drawBackground
	 *            the draw background
	 * @param scaleOnZoom
	 *            the scale on zoom
	 * @param curvedLabel
	 *            the curved label
	 * @param showOnOverview
	 *            the show on overview
	 */
	public Text( String text, Vector2D position, Vector3D outlineColor, Vector3D color, double labelSize, boolean bold, boolean outlined,
			boolean drawBackground, boolean scaleOnZoom, boolean curvedLabel, boolean showOnOverview )
	{
		init( text, position, outlineColor, color, labelSize, bold, outlined, drawBackground, scaleOnZoom, curvedLabel, showOnOverview,
				false );		
	}
	public Text( String text, Vector2D position, Vector3D outlineColor, Vector3D color, double labelSize, boolean bold, boolean outlined,
			boolean drawBackground, boolean scaleOnZoom, boolean curvedLabel, boolean showOnOverview, boolean screenPosition )
	{
		init( text, position, outlineColor, color, labelSize, bold, outlined, drawBackground, scaleOnZoom, curvedLabel, showOnOverview,
				screenPosition );
	}
	
	private void init( String text, Vector2D position, Vector3D outlineColor, Vector3D color, double labelSize, boolean bold, boolean outlined,
			boolean drawBackground, boolean scaleOnZoom, boolean curvedLabel, boolean showOnOverview, boolean screenPosition )
	{
		node.setLabel( text );
		node.setPosition( position );
		node.setColor( color );
		node.setLabelColor( color );
		node.setLabelOutlineColor( outlineColor );
		this.labelSize = labelSize;
		this.bold = bold;
		this.outlined = outlined;
		this.drawBackground = drawBackground;
		this.scaleOnZoom = scaleOnZoom;
		this.curvedLabel = curvedLabel;
		this.showOnOverview = showOnOverview;
		this.screenPosition = screenPosition;
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
		if( showOnOverview || !overview )
		{
			try
			{
				double ratio = 1;
				if( pb != null )
				{
					ratio = width / pb.getWidth();
				}
				Vector2D tempPos;
				if( !screenPosition )
				{
					tempPos = ImageRenderer.transformPosition( globalMinX, globalMaxX, globalMinY, globalMaxY, minXPercent, maxXPercent, minYPercent,
							maxYPercent, width, height, node.getPosition( true ) );
				}
				else
				{
					tempPos = node.getPosition( true );
				}
				float yMiddle = tempPos.getY();
				float yPos = yMiddle;
				String label = node.getLabel();
				int lines = StringUtils.countInstancesOf( label, "\n" );
				yPos = yMiddle - ( lines * (float)labelSize / 2.0f );
				StringTokenizer toki = new StringTokenizer( label, "\n" );
				while( toki.hasMoreElements() )
				{
					tempPos.setY( yPos );
					label = toki.nextToken();
					ImageRenderer.drawLabel( g2d, node, tempPos, 1, label, true, curvedLabel, outlined, labelSize, minXPercent, maxXPercent, ratio,
							scaleOnZoom, false, label.length(), 200, drawBackground, bold, false, null );
					yPos += labelSize / ( maxYPercent - minYPercent );
				}
				// ImageRenderer.drawLabel( (int)Math.round( width ),
				// (int)Math.round( height ), minXPercent, minYPercent,
				// maxXPercent, maxYPercent, ratio, true, curvedLabel, outlined,
				// labelSize, false, globalMinX, globalMaxX, globalMinY,
				// globalMaxY, false, node.getLabel().length(), 200,
				// scaleOnZoom, drawBackground, node, g2d, 1, bold );
			}
			catch( MalformedURLException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch( IOException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Gets the position.
	 * 
	 * @return the position
	 */
	public Vector2D getPosition()
	{
		return node.getPosition();
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
		node.setPosition( x, y );
	}

	/**
	 * Gets the label size.
	 * 
	 * @return the label size
	 */
	public double getLabelSize()
	{
		return labelSize;
	}

	/**
	 * Sets the label size.
	 * 
	 * @param labelSize
	 *            the new label size
	 */
	public void setLabelSize( double labelSize )
	{
		this.labelSize = labelSize;
	}
	
	public DNVNode getNode()
	{
		return node;
	}
}
