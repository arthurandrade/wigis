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

package net.wigis.graph;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import net.wigis.settings.Settings;
import net.wigis.web.ContextLookup;

// TODO: Auto-generated Javadoc
/**
 * The Class GraphsBean.
 * 
 * @author Brynjar Gretarsson
 */
public class GraphsBean
{

	/** The file list. */
	List<SelectItem> fileList = new ArrayList<SelectItem>();

	/**
	 * Instantiates a new graphs bean.
	 */
	public GraphsBean()
	{
		buildFileList();
	}

	/**
	 * Builds the file list.
	 */
	public void buildFileList()
	{
		fileList.clear();

		File directory = new File( 
				Settings.GRAPHS_PATH 						// default
				//_GLOBALS.GRAPH_PATH_FACEBOOK + "/music" 	// alex
				);
		
		FilenameFilter filter = new FilenameFilter()
		{
			public boolean accept( File dir, String filename )
			{
				if( filename.toLowerCase().endsWith( ".dnv" ) )
					return true;

				return false;
			}
		};

		File[] files = directory.listFiles( filter );
		SelectItem tempItem;
		File tempFile;
		for( int i = 0; i < files.length; i++ )
		{
			tempFile = files[i];
			tempItem = new SelectItem( tempFile.getAbsolutePath(), tempFile.getName() );
			fileList.add( tempItem );
		}
		
		PaintBean pb = (PaintBean)ContextLookup.lookup( "paintBean", FacesContext.getCurrentInstance() );
		if( pb != null )
		{
			String selectedFile = pb.getSelectedFile();
			boolean found = false;
			for( SelectItem file : fileList )
			{
				if( file.getValue().equals( selectedFile ) )
				{
					found = true;
					break;
				}
			}
			if( !found )
			{
				SelectItem item = new SelectItem( selectedFile, selectedFile.substring( Settings.GRAPHS_PATH.length() ) );
				fileList.add( item );
			}
		}
	}

	/**
	 * Gets the file list.
	 * 
	 * @return the file list
	 */
	public List<SelectItem> getFileList()
	{
		return fileList;
	}

	/**
	 * Sets the file list.
	 * 
	 * @param fileList
	 *            the new file list
	 */
	public void setFileList( List<SelectItem> fileList )
	{
		this.fileList = fileList;
	}

}
