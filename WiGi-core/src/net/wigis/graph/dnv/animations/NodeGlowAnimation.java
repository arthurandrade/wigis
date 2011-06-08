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
package net.wigis.graph.dnv.animations;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import net.wigis.graph.ImageRenderer;
import net.wigis.graph.PaintBean;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.geometry.Geometric;
import net.wigis.graph.dnv.utilities.Vector2D;
import net.wigis.graph.dnv.utilities.Vector3D;

/**
 * @author brynjar
 *
 */
public class NodeGlowAnimation extends Animation
{

	private Vector3D color;
	private DNVNode node;
	private int currentRadius;
	private double radiusIncrement;
	private int strokeWidth;
	private float currentAlpha = 1.0f;
	private Geometric geometric;
	private boolean screenPosition;
	
	public NodeGlowAnimation( int numberOfFrames, Vector3D color, DNVNode node, int startRadius, int endRadius, int strokeWidth, boolean screenPosition )
	{
		super(numberOfFrames);
		initialize( numberOfFrames, color, node, startRadius, endRadius, strokeWidth, screenPosition, null );
	}

	public NodeGlowAnimation( int numberOfFrames, Vector3D color, DNVNode node, int startRadius, int endRadius, int strokeWidth, boolean screenPosition, Geometric geometric )
	{
		super(numberOfFrames);
		initialize( numberOfFrames, color, node, startRadius, endRadius, strokeWidth, screenPosition, geometric );
	}

	private void initialize( int numberOfFrames, Vector3D color, DNVNode node, int startRadius, int endRadius, int strokeWidth, boolean screenPosition, Geometric geometric )
	{
		this.color = color;
		this.node = node;
		currentRadius = startRadius;
		radiusIncrement = (endRadius-startRadius) / (double)numberOfFrames; 
		this.strokeWidth = strokeWidth;
		this.geometric = geometric;
		this.screenPosition = screenPosition;
	}
	
	/* (non-Javadoc)
	 * @see net.wigis.graph.dnv.animations.GeneralAnimation#paint(java.awt.Graphics)
	 */
	@Override
	public void paint( Graphics g, PaintBean pb )
	{
		if( !hasCompleted() )
		{
			Vector2D screenPos;
			if( screenPosition )
			{
				screenPos = node.getPosition( true );
			}
			else
			{
				screenPos = ImageRenderer.transformPosition( pb.getGlobalMinX(), pb.getGlobalMaxX(), pb.getGlobalMinY(), pb.getGlobalMaxY(), pb.getMinX(), pb.getMaxX(), pb.getMinY(), pb.getMaxY(), pb.getWidth(), pb.getHeight(), node.getPosition( true ) );
			}
			
			currentRadius += radiusIncrement;
			currentAlpha -= 1.0f / (float)numberOfFrames;
			if( currentAlpha < 0 )
			{
				currentAlpha = 0;
			}
			g.setColor( new Color(color.getX(), color.getY(), color.getZ(), currentAlpha ) );
			((Graphics2D)g).setStroke( new BasicStroke( strokeWidth ) );
			g.drawArc( (int)screenPos.getX()-currentRadius, (int)screenPos.getY()-currentRadius, currentRadius*2, currentRadius*2, 0, 360 );
//			StarPolygon p = new StarPolygon( (int)screenPos.getX(), (int)screenPos.getY(), currentRadius*2, currentRadius, 5 );
//			((Graphics2D)g).draw( p );
			
			if( geometric != null )
			{
				geometric.draw( ((Graphics2D)g), pb, pb.getMinX(), pb.getMaxX(), pb.getMinY(), pb.getMaxY(), pb.getGlobalMinX(), pb.getGlobalMaxX(), pb.getGlobalMinY(), pb.getGlobalMaxY(), pb.getWidth(), pb.getHeight(), false );
			}
			
			shownFrames++;
		}
	}
}
