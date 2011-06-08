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

package net.wigis.svetlin;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Iterator;

// TODO: Auto-generated Javadoc
/*
 * USAGE
 * 
 * svetlin.FileBig file = new FileBig("c:/...");
 * 
 * for (String line : file) System.out.println(line);
 */

/**
 * The Class FileBig.
 * 
 * @author Svetlin Bostandjiev
 */
public class __FileBig implements Iterable<String>
{

	/** The _reader. */
	private BufferedReader _reader;

	/**
	 * Instantiates a new file big.
	 * 
	 * @param filePath
	 *            the file path
	 * @throws Exception
	 *             the exception
	 */
	public __FileBig( String filePath ) throws Exception
	{
		_reader = new BufferedReader( new FileReader( filePath ) );
	}

	/**
	 * Close.
	 */
	public void Close()
	{
		try
		{
			_reader.close();
		}
		catch( Exception ex )
		{}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<String> iterator()
	{
		return new FileIterator();
	}

	/**
	 * The Class FileIterator.
	 */
	private class FileIterator implements Iterator<String>
	{

		/** The _current line. */
		private String _currentLine;

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext()
		{
			try
			{
				_currentLine = _reader.readLine();
			}
			catch( Exception ex )
			{
				_currentLine = null;
				ex.printStackTrace();
			}

			return _currentLine != null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Iterator#next()
		 */
		public String next()
		{
			return _currentLine;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Iterator#remove()
		 */
		public void remove()
		{}
	}
}