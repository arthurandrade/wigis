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

import java.awt.Font;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;

// TODO: Auto-generated Javadoc
/**
 * The Class TextStroke.
 * 
 * @author Brynjar Gretarsson
 */
public class TextStroke implements Stroke
{

	/** The text. */
	private String text;

	/** The font. */
	private Font font;

	/** The stretch to fit. */
	private boolean stretchToFit = false;

	/** The repeat. */
	private boolean repeat = false;

	/** The t. */
	private AffineTransform t = new AffineTransform();

	/** The Constant FLATNESS. */
	private static final float FLATNESS = 1;

	/**
	 * Instantiates a new text stroke.
	 * 
	 * @param text
	 *            the text
	 * @param font
	 *            the font
	 */
	public TextStroke( String text, Font font )
	{
		this( text, font, false, true );
	}

	/**
	 * Instantiates a new text stroke.
	 * 
	 * @param text
	 *            the text
	 * @param font
	 *            the font
	 * @param stretchToFit
	 *            the stretch to fit
	 * @param repeat
	 *            the repeat
	 */
	public TextStroke( String text, Font font, boolean stretchToFit, boolean repeat )
	{
		this.text = text;
		this.font = font;
		this.stretchToFit = stretchToFit;
		this.repeat = repeat;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Stroke#createStrokedShape(java.awt.Shape)
	 */
	public Shape createStrokedShape( Shape shape )
	{
		FontRenderContext frc = new FontRenderContext( null, true, true );
		GlyphVector glyphVector = font.createGlyphVector( frc, text );

		GeneralPath result = new GeneralPath();
		PathIterator it = new FlatteningPathIterator( shape.getPathIterator( null ), FLATNESS );
		float points[] = new float[6];
		float moveX = 0, moveY = 0;
		float lastX = 0, lastY = 0;
		float thisX = 0, thisY = 0;
		int type = 0;
		// boolean first = false;
		float next = 0;
		int currentChar = 0;
		int length = glyphVector.getNumGlyphs();

		if( length == 0 )
			return result;

		float factor = stretchToFit ? measurePathLength( shape ) / (float)glyphVector.getLogicalBounds().getWidth() : 1.0f;
		float nextAdvance = 0;

		while( currentChar < length && !it.isDone() )
		{
			type = it.currentSegment( points );
			switch( type )
			{
				case PathIterator.SEG_MOVETO:
					moveX = lastX = points[0];
					moveY = lastY = points[1];
					result.moveTo( moveX, moveY );
					// first = true;
					nextAdvance = glyphVector.getGlyphMetrics( currentChar ).getAdvance() * 0.5f;
					next = nextAdvance;
					break;

				case PathIterator.SEG_CLOSE:
					points[0] = moveX;
					points[1] = moveY;
					// Fall into....

				case PathIterator.SEG_LINETO:
					thisX = points[0];
					thisY = points[1];
					float dx = thisX - lastX;
					float dy = thisY - lastY;
					float distance = (float)Math.sqrt( dx * dx + dy * dy );
					if( distance >= next )
					{
						float r = 1.0f / distance;
						float angle = (float)Math.atan2( dy, dx );
						while( currentChar < length && distance >= next )
						{
							Shape glyph = glyphVector.getGlyphOutline( currentChar );
							Point2D p = glyphVector.getGlyphPosition( currentChar );
							float px = (float)p.getX();
							float py = (float)p.getY();
							float x = lastX + next * dx * r;
							float y = lastY + next * dy * r;
							float advance = nextAdvance;
							nextAdvance = currentChar < length - 1 ? glyphVector.getGlyphMetrics( currentChar + 1 ).getAdvance() * 0.5f : 0;
							t.setToTranslation( x, y );
							t.rotate( angle );
							t.translate( -px - advance, -py );
							result.append( t.createTransformedShape( glyph ), false );
							next += ( advance + nextAdvance ) * factor;
							currentChar++;
							if( repeat )
								currentChar %= length;
						}
					}
					next -= distance;
					// first = false;
					lastX = thisX;
					lastY = thisY;
					break;
			}
			it.next();
		}

		return result;
	}

	/**
	 * Measure path length.
	 * 
	 * @param shape
	 *            the shape
	 * @return the float
	 */
	public float measurePathLength( Shape shape )
	{
		PathIterator it = new FlatteningPathIterator( shape.getPathIterator( null ), FLATNESS );
		float points[] = new float[6];
		float moveX = 0, moveY = 0;
		float lastX = 0, lastY = 0;
		float thisX = 0, thisY = 0;
		int type = 0;
		float total = 0;

		while( !it.isDone() )
		{
			type = it.currentSegment( points );
			switch( type )
			{
				case PathIterator.SEG_MOVETO:
					moveX = lastX = points[0];
					moveY = lastY = points[1];
					break;

				case PathIterator.SEG_CLOSE:
					points[0] = moveX;
					points[1] = moveY;
					// Fall into....

				case PathIterator.SEG_LINETO:
					thisX = points[0];
					thisY = points[1];
					float dx = thisX - lastX;
					float dy = thisY - lastY;
					total += (float)Math.sqrt( dx * dx + dy * dy );
					lastX = thisX;
					lastY = thisY;
					break;
			}
			it.next();
		}

		return total;
	}
}
