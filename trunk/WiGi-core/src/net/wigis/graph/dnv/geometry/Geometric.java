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

import net.wigis.graph.PaintBean;

// TODO: Auto-generated Javadoc
/**
 * The Class Geometric.
 * 
 * @author Brynjar Gretarsson
 */
public abstract class Geometric
{

	/**
	 * Draw.
	 * 
	 * @param g2d
	 *            the g2d
	 * @param pb
	 *            the pb
	 * @param minXPercent
	 *            the min x percent
	 * @param maxXPercent
	 *            the max x percent
	 * @param minYPercent
	 *            the min y percent
	 * @param maxYPercent
	 *            the max y percent
	 * @param globalMinX
	 *            the global min x
	 * @param globalMaxX
	 *            the global max x
	 * @param globalMinY
	 *            the global min y
	 * @param globalMaxY
	 *            the global max y
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @param overview
	 *            the overview
	 */
	public abstract void draw( Graphics2D g2d, PaintBean pb, double minXPercent, double maxXPercent, double minYPercent, double maxYPercent,
			double globalMinX, double globalMaxX, double globalMinY, double globalMaxY, double width, double height, boolean overview );
}
