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

// TODO: Auto-generated Javadoc
/**
 * The Class Statistics.
 * 
 * @author Brynjar Gretarsson
 */
public class Statistics
{

	/** The sum. */
	private double sum = 0;

	/** The number of items. */
	private double numberOfItems = 0;

	/** The vector sum. */
	private Vector2D vectorSum = new Vector2D( 0, 0 );

	/** The number of vectors. */
	private double numberOfVectors = 0;

	/**
	 * Adds the.
	 * 
	 * @param number
	 *            the number
	 */
	public void add( double number )
	{
		sum += number;
		numberOfItems++;
	}

	/**
	 * Adds the.
	 * 
	 * @param vector
	 *            the vector
	 */
	public void add( Vector2D vector )
	{
		vectorSum.add( vector );
		numberOfVectors++;
	}

	/**
	 * Gets the vector sum.
	 * 
	 * @return the vector sum
	 */
	public Vector2D getVectorSum()
	{
		return vectorSum;
	}

	/**
	 * Gets the average.
	 * 
	 * @return the average
	 */
	public double getAverage()
	{
		return sum / numberOfItems;
	}

	/**
	 * Gets the sum.
	 * 
	 * @return the sum
	 */
	public double getSum()
	{
		return sum;
	}

	/**
	 * Gets the number of items.
	 * 
	 * @return the number of items
	 */
	public double getNumberOfItems()
	{
		return numberOfItems;
	}

	/**
	 * Reset.
	 */
	public void reset()
	{
		sum = 0;
		numberOfItems = 0;
		vectorSum.set( 0, 0 );
		numberOfVectors = 0;
	}
}
