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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import net.wigis.graph.data.uploader.TopicFileUploadBean;
import net.wigis.settings.Settings;

// TODO: Auto-generated Javadoc
/**
 * The Class UpdateTopicModelingFilesFromTemplate.
 * 
 * @author Brynjar Gretarsson
 */
public class UpdateTopicModelingFilesFromTemplate extends Thread
{

	/** The root directory. */
	private String rootDirectory;

	/** The current directory. */
	private String currentDirectory;

	/** The subdir. */
	private File subdir;

	/** The run topic modeling. */
	private boolean runTopicModeling;

	/**
	 * Instantiates a new update topic modeling files from template.
	 * 
	 * @param rootDirectory
	 *            the root directory
	 * @param currentDirectory
	 *            the current directory
	 * @param subdir
	 *            the subdir
	 * @param runTopicModeling
	 *            the run topic modeling
	 */
	public UpdateTopicModelingFilesFromTemplate( String rootDirectory, String currentDirectory, File subdir, boolean runTopicModeling )
	{
		this.rootDirectory = rootDirectory;
		this.currentDirectory = currentDirectory;
		this.subdir = subdir;
		this.runTopicModeling = runTopicModeling;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run()
	{
		update( rootDirectory, currentDirectory, subdir, runTopicModeling );
	}

	/**
	 * Update.
	 * 
	 * @param rootDirectory
	 *            the root directory
	 * @param runTopicModeling
	 *            the run topic modeling
	 */
	public static void update( String rootDirectory, boolean runTopicModeling )
	{
		File dir = new File( rootDirectory );
		String[] directories = dir.list();
		for( int i = 0; i < directories.length; i++ )
		{
			File subdir = new File( rootDirectory + directories[i] );
			if( subdir.isDirectory() )
			{
				if( fileExists( subdir, "go" ) )
				{
					if( fileExists( subdir, "settings.txt" ) )
					{
						UpdateTopicModelingFilesFromTemplate update = new UpdateTopicModelingFilesFromTemplate( rootDirectory, directories[i],
								subdir, runTopicModeling );
						update.start();
					}
				}
			}
		}
	}

	/**
	 * Update.
	 * 
	 * @param rootDirectory
	 *            the root directory
	 * @param currentDirectory
	 *            the current directory
	 * @param subdir
	 *            the subdir
	 * @param runTopicModeling
	 *            the run topic modeling
	 */
	private static void update( String rootDirectory, String currentDirectory, File subdir, boolean runTopicModeling )
	{
		int numberOfTopics;
		numberOfTopics = getNumberOfTopics( rootDirectory + currentDirectory + "/settings.txt" );
		TopicFileUploadBean.copyTopicDetectionCode( rootDirectory, subdir.getAbsolutePath() );
		if( runTopicModeling )
		{
			TopicFileUploadBean.runTopicDetection( rootDirectory + currentDirectory + "/", numberOfTopics, true, 4 );
		}
	}

	/**
	 * Gets the number of topics.
	 * 
	 * @param settingsFile
	 *            the settings file
	 * @return the number of topics
	 */
	private static int getNumberOfTopics( String settingsFile )
	{
		File file = new File( settingsFile );
		int numberOfTopics = 50;
		try
		{
			FileReader fr = new FileReader( file );
			BufferedReader br = new BufferedReader( fr );
			String line;
			String checkString = "numberOfTopics=";
			while( ( line = br.readLine() ) != null )
			{
				if( line.startsWith( checkString ) )
				{
					int temp = Integer.parseInt( line.substring( line.indexOf( checkString ) + checkString.length() ) );
					numberOfTopics = temp;
					break;
				}

			}

			br.close();
			fr.close();
		}
		catch( IOException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return numberOfTopics;
	}

	/**
	 * File exists.
	 * 
	 * @param directory
	 *            the directory
	 * @param filename
	 *            the filename
	 * @return true, if successful
	 */
	private static boolean fileExists( File directory, String filename )
	{
		String[] files = directory.list();
		for( int i = 0; i < files.length; i++ )
		{
			if( files[i].equals( filename ) )
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main( String[] args )
	{
		String directory = Settings.GRAPHS_PATH + "topicVisualizations/";
		update( directory, false );
	}
}
