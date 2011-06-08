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

package net.wigis.graph.data.uploader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;

import net.wigis.graph.GraphsPathFilter;
import net.wigis.graph.TopicVisualizationBean;
import net.wigis.graph.data.citeseer.Logger;
import net.wigis.graph.data.utilities.GenerateDocsFileForDirectory;
import net.wigis.graph.data.utilities.RemoveLatexTags;
import net.wigis.graph.data.utilities.SplitAndFormatLatexFile;
import net.wigis.graph.data.utilities.SplitAndFormatTextFile;
import net.wigis.graph.data.utilities.Unzip;
import net.wigis.graph.data.utilities.UpdateTopicModelingFilesFromTemplate;
import net.wigis.graph.dnv.utilities.Commands;
import net.wigis.graph.dnv.utilities.Timer;
import net.wigis.settings.Settings;
import net.wigis.web.ContextLookup;

import org.apache.commons.io.FileUtils;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

// TODO: Auto-generated Javadoc
/**
 * The Class TopicFileUploadBean.
 * 
 * @author Brynjar Gretarsson
 */
public class TopicFileUploadBean
{

	/*
	 * this should be set from the properties class
	 */
	/** The Constant uploadPath. */
	public static final String uploadPath = Settings.GRAPHS_PATH + "topicVisualizations" + "/";

	/** The file. */
	File file;

	/** The upload. */
	UploadItem upload;

	/** The data. */
	private List<Object> data = new ArrayList<Object>();

	/** The lines per file. */
	private int linesPerFile = 100;

	/**
	 * Sets the data.
	 * 
	 * @param data
	 *            the new data
	 */
	public void setData( List<Object> data )
	{
		this.data = data;
	}

	/** The flag. */
	private boolean flag;

	/** The use flash. */
	private boolean useFlash = false;

	/** The run topic modeling. */
	private boolean runTopicModeling = false;

	/** The uploads available. */
	private int uploadsAvailable = 1;

	/**
	 * Checks if is use flash.
	 * 
	 * @return true, if is use flash
	 */
	public boolean isUseFlash()
	{
		return useFlash;
	}

	/**
	 * Sets the use flash.
	 * 
	 * @param useFlash
	 *            the new use flash
	 */
	public void setUseFlash( boolean useFlash )
	{
		this.useFlash = useFlash;
	}

	/** The max files. */
	private Integer maxFiles = 2;

	/** The width. */
	private String width = "100%";

	/** The height. */
	private String height = "50px";

	/**
	 * Checks if is flag.
	 * 
	 * @return true, if is flag
	 */
	public boolean isFlag()
	{
		return flag;
	}

	/**
	 * Gets the file list.
	 * 
	 * @return the file list
	 */
	public List<Object> getFileList()
	{
		return data;
	}

	/**
	 * Sets the flag.
	 * 
	 * @param flag
	 *            the new flag
	 */
	public void setFlag( boolean flag )
	{
		this.flag = flag;
	}

	/**
	 * Listener.
	 * 
	 * @param event
	 *            the event
	 */
	public void listener( UploadEvent event )
	{

		UploadItem item = event.getUploadItem();
		Logger.write( "File : '" + item.getFileName() + "' was uploaded" );
		// if (item.isTempFile()) {
		// String file = item.getFileName();

		// logger.write("Absolute Path : '" + file + "'!");
		// file.delete();
		// }else {

		UploadItem ui = null;
		try
		{

			ui = (UploadItem)data.get( data.size() - 1 );
			Logger.write( "filename is: " + ui.getFileName() );
			Logger.write( "file size is: " + ui.getFileSize() );
			Logger.write( "the uploaded file is: " + ui.getFile() );
		}
		catch( Exception e )
		{
			Logger.write( "problem with file upload: " );
			e.printStackTrace();
		}

		writeToStore( ui );
	}

	/**
	 * Gets the uploads available.
	 * 
	 * @return the uploads available
	 */
	public int getUploadsAvailable()
	{
		return uploadsAvailable;
	}

	/**
	 * Sets the uploads available.
	 * 
	 * @param uploadsAvailable
	 *            the new uploads available
	 */
	public void setUploadsAvailable( int uploadsAvailable )
	{
		this.uploadsAvailable = uploadsAvailable;
	}

	/** The last file. */
	private String lastFile = "";

	/** The Constant DATE_FORMAT_NOW. */
	public static final String DATE_FORMAT_NOW = "yyyy-MM-dd-HH-mm-ss";

	/**
	 * Now.
	 * 
	 * @return the string
	 */
	public static String now()
	{
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat( DATE_FORMAT_NOW );
		return sdf.format( cal.getTime() );

	}

	/**
	 * Write to store.
	 * 
	 * @param ui
	 *            the ui
	 */
	private void writeToStore( UploadItem ui )
	{
		try
		{
			FileInputStream fis = new FileInputStream( ui.getFile() );

			lastFile = ui.getFileName();
			String fullPath = uploadPath + lastFile;
			String directory = "";
			if( lastFile.endsWith( ".txt" ) || lastFile.endsWith( ".tex" ) )
			{
				directory = uploadPath + lastFile.substring( 0, lastFile.length() - 4 );
				File newDirectory = new File( directory );
				if( newDirectory.exists() )
				{
					directory += now();
					newDirectory = new File( directory );
				}
				newDirectory.mkdirs();

				fullPath = directory + "/" + lastFile;
			}
			FileOutputStream fos = new FileOutputStream( new File( fullPath ) );
			int part = fis.read();
			while( part != -1 )
			{
				fos.write( part );
				part = fis.read();
			}
			fos.close();
			FacesContext fc = FacesContext.getCurrentInstance();
			TopicVisualizationBean tvb = (TopicVisualizationBean)ContextLookup.lookup( "topicVisualizationBean", fc );

			if( lastFile.endsWith( ".zip" ) || lastFile.endsWith( ".tar.gz" ) )
			{
				String newFolder;
				if( lastFile.endsWith( ".zip" ) )
				{
					newFolder = lastFile.substring( 0, lastFile.lastIndexOf( ".zip" ) ) + "/";
				}
				else
				{
					newFolder = lastFile.substring( 0, lastFile.lastIndexOf( ".tar.gz" ) ) + "/";
				}
				directory = uploadPath + newFolder;
				File f = new File( directory );
				f.mkdirs();
				f.setWritable( true );
				Unzip.extract( uploadPath + lastFile, directory );
				GenerateDocsFileForDirectory.processDirectory( directory, ".", "", "docs.txt" );
				copyTopicDetectionCode( uploadPath, directory );
				runTopicDetection( directory, 20, true, 4 );
				if( tvb != null )
				{
					tvb.setColorTopicsBasedOnDocuments( false );
					tvb.setColorTopicEdgesBasedOnDocuments( false );
					tvb.setCreateDocumentEdges( false );
				}
			}
			else if( lastFile.endsWith( ".txt" ) )
			{
				SplitAndFormatTextFile.splitAndFormat( directory, lastFile, lastFile.substring( 0, lastFile.length() - 4 ), "docs.txt", linesPerFile );
				copyTopicDetectionCode( uploadPath, directory );
				runTopicDetection( directory, 20, true, 4 );
			}
			else if( lastFile.endsWith( ".tex" ) )
			{
				System.out.println( ".tex file has been uploaded..." );
				SplitAndFormatLatexFile.splitAndFormat( directory, lastFile, "docs.txt" );
				RemoveLatexTags.remove( new File( directory, "docs.txt" ) );
				copyTopicDetectionCode( uploadPath, directory );
				runTopicDetection( directory, 20, true, 4 );
			}

			if( tvb != null )
			{
				tvb.buildFolderList();
				tvb.setSelectedFolder( directory );
//				ExternalContext ec = fc.getExternalContext();
//				ec.redirect( "/WiGi/topic/topicWiGi.faces" );
			}
		}
		catch( FileNotFoundException e )
		{
			Logger.write( "File Not found.." );
			e.printStackTrace();
		}
		catch( IOException e )
		{
			Logger.write( "Input/output exception .." );
			e.printStackTrace();
		}

	}

	/**
	 * Test.
	 */
	public void test()
	{
		main( null );
	}

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main( String args[] )
	{
		GraphsPathFilter gpf = new GraphsPathFilter();
		try
		{
			gpf.init( null );
		}
		catch( ServletException e )
		{
			e.printStackTrace();
		}
		String directory = Settings.GRAPHS_PATH + "topicVisualizations/test/";
		copyTopicDetectionCode( uploadPath, directory );
		runTopicDetection( directory, 45, true, 2 );
	}

	/**
	 * Run topic detection.
	 * 
	 * @param directory
	 *            the directory
	 * @param numberOfTopics
	 *            the number of topics
	 * @param useMPI
	 *            the use mpi
	 * @param numberOfProcessors
	 *            the number of processors
	 */
	public static void runTopicDetection( String directory, int numberOfTopics, boolean useMPI, int numberOfProcessors )
	{
		runTopicDetection( directory, numberOfTopics, useMPI, numberOfProcessors, "" );
	}

	/**
	 * Run topic detection.
	 * 
	 * @param directory
	 *            the directory
	 * @param numberOfTopics
	 *            the number of topics
	 * @param useMPI
	 *            the use mpi
	 * @param numberOfProcessors
	 *            the number of processors
	 * @param timeStamp
	 *            the time stamp
	 */
	public static void runTopicDetection( String directory, int numberOfTopics, boolean useMPI, int numberOfProcessors, String timeStamp )
	{
		Timer timer = new Timer( Timer.MILLISECONDS );
		timer.setStart();
		String cmdPrefix = "./";
		String command = "go";
		if( useMPI )
		{
			command += "_mpi";
		}
		if( File.separator.equals( "\\" ) )
		{
			cmdPrefix = "cmd /c c:\\cygwin\\bin\\bash ";
			if( useMPI )
			{
				command += "_win";
			}
		}
		String wholeCommand;
		if( useMPI )
		{
			int numberOfIterations = ( 100 + (int)( 15 * Math.sqrt( numberOfTopics ) ) ) / 4;
			wholeCommand = cmdPrefix + command + " " + numberOfTopics + " " + numberOfIterations + " " + numberOfProcessors + " " + timeStamp;
		}
		else
		{
			wholeCommand = cmdPrefix + command + " " + numberOfTopics + " " + timeStamp;
		}
		Commands.runSystemCommand( wholeCommand, directory );
		waitForMatlab( directory );
		deleteMatlabFlag( directory );
		timer.setEnd();
		System.out.println( "Entire topic modeling process took " + timer.getLastSegment( Timer.SECONDS ) + " seconds." );
	}

	/**
	 * Wait for matlab.
	 * 
	 * @param directory
	 *            the directory
	 * @return true, if successful
	 */
	public static boolean waitForMatlab( String directory )
	{
		if( !directory.endsWith( "\\" ) && !directory.endsWith( "/" ) )
		{
			directory += "/";
		}
		File file = new File( directory + "done.txt" );
		Timer timer = new Timer( Timer.MILLISECONDS );
		timer.setStart();
		boolean exists = FileUtils.waitFor( file, 10 );
		timer.setEnd();

		System.out.println( timer.getTotalTime( Timer.SECONDS ) + " - " + directory + "done.txt exists : " + file.exists() );

		return exists;
	}

	/**
	 * Delete matlab flag.
	 * 
	 * @param directory
	 *            the directory
	 */
	public static void deleteMatlabFlag( String directory )
	{
		if( !directory.endsWith( "\\" ) && !directory.endsWith( "/" ) )
		{
			directory += "/";
		}
		File file = new File( directory, "done.txt" );
		if( file.exists() )
		{
			file.delete();
		}
	}

	/**
	 * Copy topic detection code.
	 * 
	 * @param uploadPath
	 *            the upload path
	 * @param directory
	 *            the directory
	 */
	public static void copyTopicDetectionCode( String uploadPath, String directory )
	{
		String copyCmd = "cp ";
		if( File.separator.equals( "\\" ) )
		{
			copyCmd = "cmd /c c:\\cygwin\\bin\\cp ";
		}
		if( !directory.endsWith( "/" ) )
		{
			directory += "/";
		}
		File dir = new File( uploadPath + "TEMPLATE" );
		if( dir.isDirectory() )
		{
			String[] fileList = dir.list();
			for( int i = 0; i < fileList.length; i++ )
			{
				Commands.runSystemCommand( copyCmd + uploadPath + "TEMPLATE" + "/" + fileList[i] + " " + directory + fileList[i] );
			}
		}
	}

	/**
	 * Update topic modeling code.
	 */
	public void updateTopicModelingCode()
	{
		UpdateTopicModelingFilesFromTemplate.update( uploadPath, runTopicModeling );
	}

	/*
	 * this method gets the current instance of a managed bean, given that it
	 * does exist in the scope. Returns null otherwise.
	 */
	/**
	 * Gets the managed bean.
	 * 
	 * @param expression
	 *            the expression
	 * @param type
	 *            the type
	 * @return the managed bean
	 */
	public Object getManagedBean( String expression, Class<?> type )
	{
		try
		{
			FacesContext facesContext = FacesContext.getCurrentInstance();
			ELContext el = facesContext.getELContext();
			Application app = facesContext.getApplication();
			ExpressionFactory ef = app.getExpressionFactory();
			ValueExpression ve = ef.createValueExpression( el, expression, type );
			return ve.getValue( el );
		}
		catch( Exception e )
		{
			Logger.write( "problem getting managed bean instance- probably because it doesn't exist: " );
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Gets the data.
	 * 
	 * @return the data
	 */
	public List<Object> getData()
	{
		return this.data;
	}

	/**
	 * Gets the max files.
	 * 
	 * @return the max files
	 */
	public Integer getMaxFiles()
	{
		return maxFiles;
	}

	/**
	 * Sets the max files.
	 * 
	 * @param maxFiles
	 *            the new max files
	 */
	public void setMaxFiles( Integer maxFiles )
	{
		this.maxFiles = maxFiles;
	}

	/**
	 * Gets the width.
	 * 
	 * @return the width
	 */
	public String getWidth()
	{
		return width;
	}

	/**
	 * Sets the width.
	 * 
	 * @param width
	 *            the new width
	 */
	public void setWidth( String width )
	{
		this.width = width;
	}

	/**
	 * Gets the height.
	 * 
	 * @return the height
	 */
	public String getHeight()
	{
		return height;
	}

	/**
	 * Sets the height.
	 * 
	 * @param height
	 *            the new height
	 */
	public void setHeight( String height )
	{
		this.height = height;
	}

	/**
	 * Gets the file.
	 * 
	 * @return the file
	 */
	public File getFile()
	{
		return file;
	}

	/**
	 * Sets the file.
	 * 
	 * @param file
	 *            the new file
	 */
	public void setFile( File file )
	{
		this.file = file;
	}

	/**
	 * Gets the upload.
	 * 
	 * @return the upload
	 */
	public UploadItem getUpload()
	{
		return upload;
	}

	/**
	 * Sets the upload.
	 * 
	 * @param upload
	 *            the new upload
	 */
	public void setUpload( UploadItem upload )
	{
		this.upload = upload;
	}

	/*
	 * this function is called on uploadComplete the function parses the
	 * uploaded CSV file and produces a DNV file the current default graph is
	 * set to be the new dnv file layout is run automatically (once for 3
	 * seconds) after execution the function redirects the faces view to the
	 * main graph
	 */
	/** The WIG i_ url. */
	private final String WIGI_URL = "/WiGi/topic/topicWiGi.faces";

	/**
	 * Convert.
	 */
	public void convert()
	{
		ExternalContext ec = null;
		Logger.write( "Parsing data from csv files" );
		try
		{
			FacesContext fc = FacesContext.getCurrentInstance();

			ec = fc.getExternalContext();
			// redirect to visualizer
			try
			{
				ec.redirect( WIGI_URL );
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();

		}

		/*
		 * try to fail "gracefully"
		 */
		/*
		 * try { //ec.redirect( "www.google.com" ); ec.redirect(
		 * ERROR_URL+"?msg="+lastFile ); } catch( IOException e ) { // TODO
		 * Auto-generated catch block e.printStackTrace(); } Logger.write(
		 * "problem generating DNV file" + ex );
		 */

	}

	/**
	 * Sets the run topic modeling.
	 * 
	 * @param runTopicModeling
	 *            the new run topic modeling
	 */
	public void setRunTopicModeling( boolean runTopicModeling )
	{
		this.runTopicModeling = runTopicModeling;
	}

	/**
	 * Checks if is run topic modeling.
	 * 
	 * @return true, if is run topic modeling
	 */
	public boolean isRunTopicModeling()
	{
		return runTopicModeling;
	}

	/**
	 * Toggle run topic modeling.
	 */
	public void toggleRunTopicModeling()
	{
		setRunTopicModeling( !isRunTopicModeling() );
	}
}
