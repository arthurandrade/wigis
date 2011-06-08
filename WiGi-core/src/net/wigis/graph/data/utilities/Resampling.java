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

package net.wigis.graph.data.utilities;

import java.io.File;

import net.wigis.graph.dnv.utilities.Commands;

// TODO: Auto-generated Javadoc
/**
 * The Class Resampling.
 * 
 * @author Brynjar Gretarsson
 */
public class Resampling extends Thread
{

	/** The directory. */
	private String directory;

	/** The output directory. */
	private String outputDirectory;

	/** The number of topics. */
	private int numberOfTopics;

	/**
	 * Instantiates a new resampling.
	 * 
	 * @param directory
	 *            the directory
	 * @param outputDirectory
	 *            the output directory
	 * @param numberOfTopics
	 *            the number of topics
	 */
	public Resampling( String directory, String outputDirectory, int numberOfTopics )
	{
		this.directory = directory;
		this.numberOfTopics = numberOfTopics;
		this.outputDirectory = outputDirectory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run()
	{
		String cmdPrefix = "./";
		if( File.separator.equals( "\\" ) )
		{
			cmdPrefix = "cmd /c c:\\cygwin\\bin\\bash ";
		}

		Commands.runSystemCommand( cmdPrefix + "resample " + outputDirectory + " " + numberOfTopics, directory );
	}
}
