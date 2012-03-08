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

import net.wigis.graph.dnv.DNVEntity;

// TODO: Auto-generated Javadoc
/**
 * The Class SortByFloatProperty.
 * 
 * @author Brynjar Gretarsson
 */
public class SortByFloatProperty implements Comparator<DNVEntity>
{

	/** The property. */
	private String property;

	/** The descending. */
	private boolean descending;

	/**
	 * Instantiates a new sort by float property.
	 * 
	 * @param property
	 *            the property
	 * @param descending
	 *            the descending
	 */
	public SortByFloatProperty( String property, boolean descending )
	{
		this.property = property;
		this.descending = descending;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare( DNVEntity node0, DNVEntity node1 )
	{
		try
		{
			String number0str = node0.getProperty( property );
			String number1str = node1.getProperty( property );

			if( number0str == null && number1str == null )
			{
				return 0;
			}
			else
			{
				if( number0str == null || number1str == null )
				{
					if( descending )
					{
						if( number0str == null )
						{
							return 1;
						}
						else
						{
							return -1;
						}
					}
					else
					{
						if( number0str == null )
						{
							return -1;
						}
						else
						{
							return 1;
						}
					}
				}

				float number0 = Float.parseFloat( number0str );
				float number1 = Float.parseFloat( number1str );

				if( descending )
				{
					if( number0 < number1 )
						return 1;

					if( number0 > number1 )
						return -1;
				}
				else
				{
					if( number0 < number1 )
						return -1;

					if( number0 > number1 )
						return 1;
				}
			}
		}
		catch( NullPointerException npe )
		{
			npe.printStackTrace();
		}
		catch( NumberFormatException nfe )
		{
			nfe.printStackTrace();
		}

		return 0;
	}

}
