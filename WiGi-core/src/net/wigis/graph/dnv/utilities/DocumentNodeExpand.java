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

import java.io.File;
import java.io.IOException;

import javax.faces.context.FacesContext;

import net.wigis.graph.PaintBean;
import net.wigis.graph.TopicVisualizationBean;
import net.wigis.graph.data.utilities.CreateDNVFromDocumentTopics;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.ExpandNode;
import net.wigis.web.ContextLookup;

// TODO: Auto-generated Javadoc
/**
 * The Class DocumentNodeExpand.
 * 
 * @author Brynjar Gretarsson
 */
public class DocumentNodeExpand extends ExpandNode
{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The directory. */
	private String directory;

	/** The documents file. */
	private String documentsFile;

	/** The start color str. */
	private String startColorStr;

	/** The end color str. */
	private String endColorStr;

	/** The force labels. */
	private boolean forceLabels;

	/** The number of documents. */
	private float numberOfDocuments;

	/** The doc topics file. */
	private String docTopicsFile;

	/** The threshold. */
	private float threshold;

	/** The topic edge color. */
	private String topicEdgeColor;

	/** The max threshold. */
	private float maxThreshold;

	/** The label scaler. */
	private float labelScaler;

	/** The tvb. */
	private TopicVisualizationBean tvb;

	/**
	 * Instantiates a new document node expand.
	 * 
	 * @param node
	 *            the node
	 * @param directory
	 *            the directory
	 * @param documentsFile
	 *            the documents file
	 * @param startColorStr
	 *            the start color str
	 * @param endColorStr
	 *            the end color str
	 * @param forceLabels
	 *            the force labels
	 * @param numberOfDocuments
	 *            the number of documents
	 * @param docTopicsFile
	 *            the doc topics file
	 * @param threshold
	 *            the threshold
	 * @param topicEdgeColor
	 *            the topic edge color
	 * @param maxThreshold
	 *            the max threshold
	 * @param labelScaler
	 *            the label scaler
	 * @param tvb
	 *            the tvb
	 */
	public DocumentNodeExpand( DNVNode node, String directory, String documentsFile, String startColorStr, String endColorStr, boolean forceLabels,
			float numberOfDocuments, String docTopicsFile, float threshold, String topicEdgeColor, float maxThreshold, float labelScaler,
			TopicVisualizationBean tvb )
	{
		super( node );
		this.directory = directory;
		this.documentsFile = documentsFile;
		this.startColorStr = startColorStr;
		this.endColorStr = endColorStr;
		this.forceLabels = forceLabels;
		this.numberOfDocuments = numberOfDocuments;
		this.docTopicsFile = docTopicsFile;
		this.threshold = threshold;
		this.topicEdgeColor = topicEdgeColor;
		this.maxThreshold = maxThreshold;
		this.labelScaler = labelScaler;
		this.tvb = tvb;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.wigis.graph.dnv.ExpandNode#expand()
	 */
	@Override
	public void expand()
	{
		if( !directory.endsWith( "\\" ) && !directory.endsWith( "/" ) )
		{
			directory += "/";
		}
		File file = new File( directory + docTopicsFile );
		if( file.exists() )
		{
			tvb.addToHistory();
			String parentBbid = node.getBbId();
			DNVGraph graph = node.getGraph();
			try
			{
				CreateDNVFromDocumentTopics.generateAllDocumentNodes( directory, documentsFile, graph, startColorStr, endColorStr, forceLabels, true,
						numberOfDocuments, parentBbid + "sec", node, false, tvb.isHideLabelBackgroundForDocuments(), tvb
								.isUseCurvedLabelsForDocuments() );
				CreateDNVFromDocumentTopics.generateTopicEdgesBasedOnParent( node, directory, docTopicsFile, graph, threshold, topicEdgeColor,
						maxThreshold, parentBbid + "sec" );
				CreateDNVFromDocumentTopics.scaleDocumentNodes( graph, labelScaler );
				PaintBean pb = (PaintBean)ContextLookup.lookup( "paintBean", FacesContext.getCurrentInstance() );
				if( pb != null )
				{
					pb.setDocumentTopicsCircularLayoutDocIdPrefix( parentBbid + "sec" );
					pb.setDocumentTopicsCircularLayoutWidthMultiplier( 0.7f );
					pb.runLayout();
					pb.setDocumentTopicsCircularLayoutDocIdPrefix( "doc" );
					pb.setDocumentTopicsCircularLayoutWidthMultiplier( 1 );
				}
				node.setExpandable( false );
			}
			catch( IOException ioe )
			{
				ioe.printStackTrace();
			}
		}
	}

}
