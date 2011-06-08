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

import java.util.Comparator;

import net.wigis.graph.dnv.DNVNode;

// TODO: Auto-generated Javadoc
/**
 * The Class SortByLabelSize.
 * 
 * @author Brynjar Gretarsson
 */
public class SortByLabelSize implements Comparator<DNVNode>
{

	/** The highlight neighbors. */
	private boolean highlightNeighbors;

	/**
	 * Instantiates a new sort by label size.
	 * 
	 * @param highlightNeighbors
	 *            the highlight neighbors
	 */
	public SortByLabelSize( boolean highlightNeighbors )
	{
		this.highlightNeighbors = highlightNeighbors;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare( DNVNode node0, DNVNode node1 )
	{
		// Put selected nodes first
		if( node0.isSelected() && !node1.isSelected() )
		{
			return 1;
		}
		if( !node0.isSelected() && node1.isSelected() )
		{
			return -1;
		}

		// if both selected sort by order selected
		if( node0.isSelected() && node1.isSelected() )
		{
			if( node0.hasProperty( "SelectedTime" ) && node1.hasProperty( "SelectedTime" ) )
			{
				long selectedTime0 = Long.parseLong( node0.getProperty( "SelectedTime" ) );
				long selectedTime1 = Long.parseLong( node1.getProperty( "SelectedTime" ) );

				if( selectedTime0 > selectedTime1 )
				{
					return 1;
				}
				else
				{
					return -1;
				}
			}
		}

		if( highlightNeighbors )
		{
			DNVNode tempNode0 = (DNVNode)node0;
			DNVNode tempNode1 = (DNVNode)node1;
			if( ( tempNode0.isNeighborSelected() || tempNode0.isEdgeSelected() ) && !( tempNode1.isNeighborSelected() || tempNode1.isEdgeSelected() ) )
			{
				return 1;
			}
			if( !( tempNode0.isNeighborSelected() || tempNode0.isEdgeSelected() ) && ( tempNode1.isNeighborSelected() || tempNode1.isEdgeSelected() ) )
			{
				return -1;
			}
		}

		// Put nodes with forceLabel = true first
		if( node0.isForceLabel() && !node1.isForceLabel() )
		{
			return 1;
		}
		if( !node0.isForceLabel() && node1.isForceLabel() )
		{
			return -1;
		}

		// Put nodes with larger label font size first
		Integer number0 = node0.getLabelSize();
		Integer number1 = node1.getLabelSize();
		if( number0 == null )
			number0 = 0;
		if( number1 == null )
			number1 = 0;

		if( number0 > number1 )
			return 1;

		if( number0 < number1 )
			return -1;

		// Put bigger nodes first
//		if( node0 instanceof DNVNode && node1 instanceof DNVNode )
//		{
			float radius0 = ( (DNVNode)node0 ).getRadius();
			float radius1 = ( (DNVNode)node1 ).getRadius();

			if( radius0 > radius1 )
				return 1;

			if( radius0 < radius1 )
				return -1;
//		}

		// Put nodes with more neighbors first
//		if( node0 instanceof DNVNode && node1 instanceof DNVNode )
//		{
			int numberOfNeighbors0 = ( (DNVNode)node0 ).getNeighborMap().size();
			int numberOfNeighbors1 = ( (DNVNode)node1 ).getNeighborMap().size();

			if( numberOfNeighbors0 > numberOfNeighbors1 )
				return 1;

			if( numberOfNeighbors0 < numberOfNeighbors1 )
				return -1;
//		}

		// Put nodes with shorter label first
		number0 = node0.getLabel().length();
		number1 = node1.getLabel().length();

		if( number0 < number1 )
			return 1;

		if( number0 > number1 )
			return -1;

		return 0;
	}

}
