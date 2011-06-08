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
 * A class to record how long time something takes.
 * 
 * @author Brynjar Gretarsson
 * 
 */
public class Timer
{

	/** Used to tell the Timer that time should be represented in seconds. */
	public static final int SECONDS = 0;

	/** Used to tell the Timer that time should be represented in milliseconds. */
	public static final int MILLISECONDS = 1;

	/** Used to tell the Timer that time should be represented in microseconds. */
	public static final int MICROSECONDS = 2;

	/** Used to tell the Timer that time should be represented in nanoseconds. */
	public static final int NANOSECONDS = 3;

	/** The measurement unit. */
	private int measurementUnit = MILLISECONDS;

	/** The total time. */
	private long totalTime = 0;

	/** The start. */
	private long start = 0;

	/** The end. */
	private long end = 0;

	/** The number of segments. */
	private long numberOfSegments = 0;

	/**
	 * Instantiates a new timer.
	 * 
	 * @param measurementUnit
	 *            Tells the Timer which unit to use for the measurement.
	 */
	public Timer( int measurementUnit )
	{
		if( measurementUnit == NANOSECONDS || measurementUnit == MILLISECONDS )
			this.measurementUnit = measurementUnit;

		reset();
	}

	/**
	 * Set the starting time to the current system time.
	 */
	public void setStart()
	{
		if( measurementUnit == MILLISECONDS )
			start = System.currentTimeMillis();
		else if( measurementUnit == NANOSECONDS )
			start = System.nanoTime();
	}

	/**
	 * Gets the time since start.
	 * 
	 * @param unit
	 *            the unit
	 * @return the time since start
	 */
	public float getTimeSinceStart( int unit )
	{
		if( start == 0 )
		{
//			System.out.println( "start not called yet" );
			return 0;
		}
		long current = start;
		if( measurementUnit == MILLISECONDS )
		{
			current = System.currentTimeMillis();
		}
		else if( measurementUnit == NANOSECONDS )
		{
			current = System.nanoTime();
		}
		float divideBy;
		divideBy = getDivide( unit );

		return ( current - start ) / divideBy;
	}

	/**
	 * Set the ending time to the current system time.
	 */
	public void setEnd()
	{
		if( measurementUnit == MILLISECONDS )
			end = System.currentTimeMillis();
		else if( measurementUnit == NANOSECONDS )
			end = System.nanoTime();

		numberOfSegments++;
		totalTime = totalTime + ( end - start );
	}

	/**
	 * Gets the average time.
	 * 
	 * @param unit
	 *            the unit
	 * @return the average time
	 */
	public float getAverageTime( int unit )
	{
		return getTotalTime( unit ) / numberOfSegments;
	}

	/**
	 * Gets the total time.
	 * 
	 * @param unit
	 *            The time unit to use.
	 * @return The time from the first call to setStart() to the last call to
	 *         setEnd().
	 */
	public float getTotalTime( int unit )
	{
		float divideBy;
		divideBy = getDivide( unit );
		float time = totalTime / divideBy;

		return time;
	}

	/**
	 * Gets the last segment.
	 * 
	 * @param unit
	 *            The time unit to use.
	 * @return The time, in seconds, from the last call to setStart() to the
	 *         last call to setEnd().
	 */
	public float getLastSegment( int unit )
	{
		float divideBy;
		divideBy = getDivide( unit );
		float timeSec = ( end - start ) / divideBy;
		return timeSec;
	}

	/**
	 * Gets the divide.
	 * 
	 * @param unit
	 *            the unit
	 * @return the divide
	 */
	private float getDivide( int unit )
	{
		float divideBy;
		if( measurementUnit == MILLISECONDS )
		{
			if( unit == MILLISECONDS )
				divideBy = 1.0f;
			else if( unit == SECONDS )
				divideBy = 1000.0f;
			else if( unit == MICROSECONDS )
				divideBy = 0.001f;
			else
				divideBy = 0.000001f;
		}
		else
		// Nanoseconds
		{
			if( unit == MILLISECONDS )
				divideBy = 1000000.0f;
			else if( unit == SECONDS )
				divideBy = 1000000000.0f;
			else if( unit == MICROSECONDS )
				divideBy = 1000.0f;
			else
				divideBy = 1.0f;
		}

		return divideBy;
	}

	/**
	 * Reset all time variables to 0.
	 */
	public void reset()
	{
		totalTime = 0;
		start = 0;
		end = 0;
		numberOfSegments = 0;
	}

	/**
	 * Gets the number of segments.
	 * 
	 * @return the number of segments
	 */
	public long getNumberOfSegments()
	{
		return numberOfSegments;
	}
}
