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
import java.util.ArrayList;
import java.util.List;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import net.wigis.graph.GraphsBean;
import net.wigis.graph.PaintBean;
import net.wigis.graph.data.citeseer.Logger;
import net.wigis.graph.data.utilities.Unzip;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.layout.implementations.FruchtermanReingold;
import net.wigis.settings.Settings;
import net.wigis.web.ContextLookup;

import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

// TODO: Auto-generated Javadoc
/**
 * The Class CSVUploadBean.
 * 
 * @author johno
 */
public class CSVUploadBean
{

	/*
	 * this should be set from the properties class
	 */
	/** The csv path. */
	private final String csvPath = Settings.UPLOADS_DIRECTORY;
	// private final String ERROR_URL =
	// "http://eire.mat.ucsb.edu/reporter/error1.html";
	/** The UPLOA d_ fil e_ prefix. */
	private final String UPLOAD_FILE_PREFIX = "EXPERIMENTAL_";

	/** The file. */
	File file;

	/** The upload. */
	UploadItem upload;

	/** The data. */
	private List<Object> data = new ArrayList<Object>();

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

	/** The auto upload. */
	private boolean autoUpload = false;

	/** The file types. */
	private String fileTypes = "*.csv, .xml";

	/** The max files. */
	private Integer maxFiles = 2;

	/** The width. */
	private String width = "100%";

	/** The height. */
	private String height = "50px";

	// private String uniqueName;

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

		// uniqueName = UUID.randomUUID().toString();
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

	/**
	 * Checks if is auto upload.
	 * 
	 * @return true, if is auto upload
	 */
	public boolean isAutoUpload()
	{
		return autoUpload;
	}

	/**
	 * Sets the auto upload.
	 * 
	 * @param autoUpload
	 *            the new auto upload
	 */
	public void setAutoUpload( boolean autoUpload )
	{
		this.autoUpload = autoUpload;
	}

	/**
	 * Gets the output path.
	 * 
	 * @return the output path
	 */
	public String getOutputPath()
	{
		return csvPath;
	}

	/** The last file. */
	private String lastFile = "";

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
			FileOutputStream fos = new FileOutputStream( new File( csvPath + lastFile ) );
			int part = fis.read();
			while( part != -1 )
			{
				fos.write( part );
				part = fis.read();
			}
			fos.close();

			if( lastFile.endsWith( ".zip" ) || lastFile.endsWith( ".tar.gz" ) )
			{
				Unzip.extract( csvPath + lastFile, Settings.GRAPHS_PATH + "topicVisualizations/" );
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
	 * Gets the file types.
	 * 
	 * @return the file types
	 */
	public String getFileTypes()
	{
		return fileTypes;
	}

	/**
	 * Sets the file types.
	 * 
	 * @param fileTypes
	 *            the new file types
	 */
	public void setFileTypes( String fileTypes )
	{
		this.fileTypes = fileTypes;
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
	private final String WIGI_URL = "/wigi/WiGiViewerPanel.faces";

	/**
	 * Convert.
	 */
	public void convert()
	{
		ExternalContext ec = null;
		Logger.write( "Parsing data from csv files" );
		try
		{
			// handle multiple filetypes here
			if( lastFile.endsWith( ".xml" ) )
			{
				Logger.write( "Format is XML, Invoking XML Parser" );
				DNVGraph graph = XMLToDNV.read( csvPath + lastFile );
				new FruchtermanReingold().runLayout( 80, 80, graph, 0.1f, 0, false, false );
				graph.writeGraph( Settings.GRAPHS_PATH + UPLOAD_FILE_PREFIX + lastFile + ".dnv" );
			}
			else if( lastFile.endsWith( ".csv" ) )
			{
				Logger.write( "Format is CSV, Invoking CSV Parser" );
				DNVGraph graph = ReadCSVtoDNVGraph.read( csvPath + lastFile, "," );
				new FruchtermanReingold().runLayout( 80, 80, graph, 0.1f, 0, false, false );
				graph.writeGraph( Settings.GRAPHS_PATH + UPLOAD_FILE_PREFIX + lastFile + ".dnv" );
			}
			else if( lastFile.endsWith( ".tup" ) )
			{
				Logger.write( "Format is simple tuples, Invoking tuple Parser" );
				DNVGraph graph = SimpleEdgeTuplesToDNVGraph.read( csvPath + lastFile, "," );
				new FruchtermanReingold().runLayout( 80, 80, graph, 0.1f, 0, false, false );
				graph.writeGraph( Settings.GRAPHS_PATH + UPLOAD_FILE_PREFIX + lastFile + ".dnv" );
			}
			else if( lastFile.endsWith( ".dnv" ) )
			{
				Logger.write( "Format is DNV. Verify the file and save it." );
				DNVGraph graph = new DNVGraph( csvPath + lastFile );
				graph.writeGraph( Settings.GRAPHS_PATH + lastFile );
			}
			else
			{
				Logger.write( "File must be one of the accepted formats: XML CSV or TUP.  \n\n" );
				throw new Exception();
			}

			FacesContext fc = FacesContext.getCurrentInstance();
			PaintBean p = (PaintBean)ContextLookup.lookup( "paintBean", fc );
			GraphsBean gb = (GraphsBean)ContextLookup.lookup( "graphsBean", fc );
			if( gb != null )
				gb.buildFileList();
			// set the graph value here
			p.setSelectedFile( Settings.GRAPHS_PATH + UPLOAD_FILE_PREFIX + lastFile + ".dnv" );
			// Lay out the graph
			p.runLayout();
			// Save the graph again
			p.saveGraph();

			ec = fc.getExternalContext();
			// redirect to visualizer
			try
			{
				ec.redirect( p.getContextPath() + WIGI_URL );
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

}
