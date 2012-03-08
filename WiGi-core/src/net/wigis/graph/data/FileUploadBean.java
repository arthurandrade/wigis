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

package net.wigis.graph.data;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import net.wigis.settings.Settings;

import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

// TODO: Auto-generated Javadoc
/**
 * The Class FileUploadBean.
 * 
 * @author johno
 */
public class FileUploadBean
{

	/** The files. */
	private ArrayList<FileInfo> files = new ArrayList<FileInfo>();

	/**
	 * Logger.
	 * 
	 * @return the size
	 */
	// // private static Log logger = LogFactory.getLog( FileUploadBean.class );

	public int getSize()
	{
		if( getFiles().size() > 0 )
		{
			return getFiles().size();
		}
		else
		{
			return 0;
		}
	}

	/**
	 * Instantiates a new file upload bean.
	 */
	public FileUploadBean()
	{}

	/**
	 * Clear upload data.
	 */
	public void clearUploadData()
	{
		files.clear();
	}

	/**
	 * Listener.
	 * 
	 * @param event
	 *            the event
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void listener( UploadEvent event ) throws IOException
	{
		UploadItem item = event.getUploadItem();
		System.out.println( "FileUploadBean.listener()" );

		// Store the file info
		FileInfo fileInfo = new FileInfo();
		fileInfo.setName( item.getFileName() );
		fileInfo.setLength( item.getFileSize() );
		fileInfo.setData( item.getData() );
		files.add( fileInfo );

		// Write the file to disk
		try
		{
			File inputFile = item.getFile();
			File outputFile = new File( Settings.GRAPHS_PATH + item.getFileName() );
			FileReader in = new FileReader( inputFile );
			FileWriter out = new FileWriter( outputFile );
			int c;
			while( ( c = in.read() ) != -1 )
				out.write( c );

			in.close();
			out.close();
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}

	/**
	 * Paint.
	 * 
	 * @param outputStream
	 *            the output stream
	 * @param obj
	 *            the obj
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void paint( OutputStream outputStream, Object obj ) throws IOException
	{
		outputStream.write( (byte[])obj );
		System.out.println( "FileUploadBean.paint()" );
	}

	/**
	 * Gets the files.
	 * 
	 * @return the files
	 */
	public ArrayList<FileInfo> getFiles()
	{
		return files;
	}

	/**
	 * Sets the files.
	 * 
	 * @param files
	 *            the new files
	 */
	public void setFiles( ArrayList<FileInfo> files )
	{
		this.files = files;
	}

}
