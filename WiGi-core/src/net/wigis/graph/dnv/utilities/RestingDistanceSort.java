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

import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVNode;

// TODO: Auto-generated Javadoc
/**
 * The Class RestingDistanceSort.
 * 
 * @author Brynjar Gretarsson
 */
public class RestingDistanceSort implements Comparator<DNVNode>
{

	/**
	 * Instantiates a new resting distance sort.
	 */
	public RestingDistanceSort()
	{}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare( DNVNode node0, DNVNode node1 )
	{
		DNVEdge dnvEdge0 = GraphFunctions.getEdgeToNodeOfType( node0.getGraph(), node0, "Active_user" );
		double distance0;
		if( dnvEdge0 != null )
			distance0 = dnvEdge0.getRestingDistance();
		else
			distance0 = Double.POSITIVE_INFINITY;

		DNVEdge dnvEdge1 = GraphFunctions.getEdgeToNodeOfType( node1.getGraph(), node1, "Active_user" );
		double distance1;
		if( dnvEdge1 != null )
			distance1 = dnvEdge1.getRestingDistance();
		else
			distance1 = Double.POSITIVE_INFINITY;

		if( distance0 < distance1 )
			return -1;

		if( distance0 > distance1 )
			return 1;

		return 0;
	}
}