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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import net.wigis.graph.ImageRenderer;
import net.wigis.graph.PaintBean;
import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVEntity;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.geometry.Text;
import net.wigis.graph.dnv.utilities.Vector2D;
import net.wigis.graph.dnv.utilities.Vector3D;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

/**
 * @author brynjar
 *
 */
public class RecursiveEdgeAnimation extends Animation
{
	private DNVNode startingNode;
	private DNVEdge edge;
	private Map<Integer,DNVEntity> handledEntities;
	private int currentDistance = 0;
	private Map<Integer,Boolean> handledDistances;
	
	public RecursiveEdgeAnimation( int numberOfFrames, DNVNode startingNode, DNVEdge edge, Map<Integer,DNVEntity> handledEntities, Map<Integer,Boolean> handledDistances, int currentDistance )
	{
		super( numberOfFrames );
		this.startingNode = startingNode;
		this.edge = edge;
		this.handledEntities = handledEntities;
		this.handledEntities.put( startingNode.getId(), startingNode );
		this.handledEntities.put( edge.getId(), edge );
		this.currentDistance = currentDistance;
		this.handledDistances = handledDistances;
	}
	
	private static AudioStream nodeHitAudio;
//	private static AudioStream edgeMoveAudio;
	
	/* (non-Javadoc)
	 * @see net.wigis.graph.dnv.animations.Animation#paint(java.awt.Graphics, net.wigis.graph.PaintBean)
	 */
	@Override
	public void paint( Graphics g, PaintBean pb )
	{
		Vector2D startPos;
		Vector2D endPos;
		Vector2D currentPos = new Vector2D();
		DNVNode endNode;
		if( this.edge.getFrom().equals( startingNode ) )
		{
			startPos = this.edge.getFrom().getPosition( true );
			endPos = this.edge.getTo().getPosition( true );
			endNode = this.edge.getTo();
		}
		else
		{
			startPos = this.edge.getTo().getPosition( true );
			endPos = this.edge.getFrom().getPosition( true );
			endNode = this.edge.getFrom();
		}
		handledEntities.put( endNode.getId(), endNode );
		currentPos.setX( startPos.getX() + ((endPos.getX() - startPos.getX()) * shownFrames / (float)numberOfFrames) );
		currentPos.setY( startPos.getY() + ((endPos.getY() - startPos.getY()) * shownFrames / (float)numberOfFrames) );
		
		Vector2D screenPos = ImageRenderer.transformPosition( pb.getGlobalMinX(), pb.getGlobalMaxX(), pb.getGlobalMinY(), pb.getGlobalMaxY(), pb.getMinX(), pb.getMaxX(), pb.getMinY(), pb.getMaxY(), pb.getWidth(), pb.getHeight(), currentPos );
		if( edge.isSelected() || ( (pb.isHighlightEdges() || pb.isHighlightNeighbors()) && ( edge.getFrom().isSelected() || edge.getTo().isSelected() ) ) )
		{
			g.setColor( Color.red );
		}
		else if( edge.getColor() != null && edge.hasSetColor() && !edge.getColor().equals( DNVEntity.NO_COLOR) )
		{
			g.setColor( new Color( edge.getColor().getX(), edge.getColor().getY(), edge.getColor().getZ() ) );
		}
		else
		{
			g.setColor( new Color( (float)pb.getEdgeColor(), (float)pb.getEdgeColor(), (float)pb.getEdgeColor() ) );
		}
		
		float size = edge.getThickness() * 8;
		g.fillOval( Math.round( screenPos.getX() - (size/2f) ), Math.round( screenPos.getY() - (size/2f) ), Math.round( size ), Math.round( size ) );
		
		shownFrames++;
		
		if( this.hasCompleted() )
		{
			Vector3D color = endNode.getColor();
			if( endNode.isSelected() )
			{
				color = new Vector3D( 1, 0, 0 );
			}
			if( color == null )
			{
				color = new Vector3D( 0, 0, 0 );
			}
			int radius = pb.getNodeWidthOnScreen( endNode ) / 2;
			Text t = null;
			boolean playSound = false;
			synchronized( handledDistances )
			{
				if( handledDistances.get( currentDistance ) == null )
				{
					handledDistances.put( currentDistance, true );
					t = new Text( "Dist " + currentDistance, new Vector2D( 45, pb.getHeightInt() - 30 ), new Vector3D(1,1,1), endNode.getColor(), 15, true, true, true, false, false, false, true );
					Animation a = new NodeGlowAnimation( 10, color, t.getNode(), radius, radius + 40, 2, true, t );
					endNode.getGraph().addAnimation( a );
					if( pb.isPlaySound() )
					{
						try
						{
							if( nodeHitAudio != null )
							{
								AudioPlayer.player.stop( nodeHitAudio );
							}
							nodeHitAudio = new AudioStream( new FileInputStream( "audio/drum.wav" ) );
							AudioPlayer.player.start( nodeHitAudio );
							playSound = true;
						}
						catch( FileNotFoundException e )
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
			}
			Animation a = new NodeGlowAnimation( 10, color, endNode, radius, radius + 40, 2, false );
			endNode.getGraph().addAnimation( a );
			for( DNVEdge fromEdge : endNode.getFromEdges( true ) )
			{
				if( !handledEntities.containsKey( fromEdge.getTo().getId() ) && !handledEntities.containsKey( fromEdge.getId() ) && fromEdge.isVisible() )
				{
					a = new RecursiveEdgeAnimation( 10, endNode, fromEdge, handledEntities, handledDistances, currentDistance + 1 );
					endNode.getGraph().addAnimation( a );
					playSound = playSound( playSound );
				}
			}
			for( DNVEdge toEdge : endNode.getToEdges( true ) )
			{
				if( !handledEntities.containsKey( toEdge.getFrom().getId() ) && !handledEntities.containsKey( toEdge.getId() )  && toEdge.isVisible() )
				{
					a = new RecursiveEdgeAnimation( 10, endNode, toEdge, handledEntities, handledDistances, currentDistance + 1 );
					endNode.getGraph().addAnimation( a );
					playSound = playSound( playSound );
				}
			}
		}
	}

	private boolean playSound( boolean playSound )
	{
		if( playSound )
		{
//			try
//			{
//				if( edgeMoveAudio != null )
//				{
//					AudioPlayer.player.stop( edgeMoveAudio );
//				}
//				edgeMoveAudio = new AudioStream( new FileInputStream( "audio/edgeSound.wav" ) );
//				AudioPlayer.player.start( edgeMoveAudio );
//				playSound = false;
//			}
//			catch( FileNotFoundException e )
//			{
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			catch( IOException e )
//			{
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
		return playSound;
	}

}
