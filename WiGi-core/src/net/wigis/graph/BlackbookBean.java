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

import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.context.FacesContext;

import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.web.Request;
import blackbook.service.api.BlackbookServiceBrokerException;
import blackbook.service.api.BlackbookServiceBrokerIfc;
import blackbook.service.api.EdgeDecorator;
import blackbook.service.api.ResourceDecorator;
import blackbook.service.factory.BlackbookServiceBrokerFactory;
import blackbook.service.soap.BlackbookServiceBrokerSOAP;
import blackbook.service.test.BlackbookServiceBrokerStub;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

// TODO: Auto-generated Javadoc
/**
 * The Class BlackbookBean.
 * 
 * @author Brynjar Gretarsson
 */
public class BlackbookBean
{

	/** The paint bean. */
	private PaintBean paintBean;

	/** The blackbook page size. */
	private int blackbookPageSize = 200;

	/** The blackbook data source. */
	private String blackbookDataSource;

	/** The blackbook base url. */
	private String blackbookBaseUrl;

	/** The blackbook local. */
	private boolean blackbookLocal = false;

	/** The blackbook expanded. */
	private boolean blackbookExpanded = false;

	/** The blackbook nodes to add. */
	private Set<String> blackbookNodesToAdd = new HashSet<String>();

	/** The blackbook nodes to update. */
	private Set<String> blackbookNodesToUpdate = new HashSet<String>();

	/** The blackbook page polling enabled. */
	private boolean blackbookPagePollingEnabled = false;

	/** The blackbook paint polling enabled. */
	private boolean blackbookPaintPollingEnabled = false;

	/** The blackbook page poll interval. */
	private int blackbookPagePollInterval = 1000;

	/** The blackbook paint poll interval. */
	private int blackbookPaintPollInterval = 2000;

	// private long timeToPage = blackbookPagePollInterval;

	// private int lastPageSize = blackbookPageSize;

	// private long timeToPaint = blackbookPaintPollInterval;

	// needs to be the same length as PAGE_THROTTLE_HISTORY_SIZE
	// private float[] pageThrottleHistory = {1.0f,1.0f,1.0f};

	// needs to be the same length as PAINT_THROTTLE_HISTORY_SIZE
	// private double[] paintThrottleHistory = {1.0,1.0,1.0,1.0,1.0};

	/** The bb. */
	private BlackbookServiceBrokerIfc bb = null;

	/** The Constant MAX_PAGE_SIZE. */
	public static final int MAX_PAGE_SIZE = 500000;

	/** The Constant MAX_FULLY_DECORATED_LABELS. */
	public static final int MAX_FULLY_DECORATED_LABELS = 300;

	/** The Constant MAX_NODES_LAYOUT. */
	public static final int MAX_NODES_LAYOUT = 1000;

	/** The Constant FANCY_DECORATION_LEVEL. */
	public static final int FANCY_DECORATION_LEVEL = 0;

	/** The Constant PLAIN_DECORATION_LEVEL. */
	public static final int PLAIN_DECORATION_LEVEL = 1;

	/** The label level. */
	private int labelLevel = FANCY_DECORATION_LEVEL;

	// synchronize/persist to blackbook when told (not automatically).
	/** The lazy synchronization. */
	private boolean lazySynchronization = true;

	/**
	 * Checks if is lazy synchronization.
	 * 
	 * @return true, if is lazy synchronization
	 */
	public boolean isLazySynchronization()
	{
		return lazySynchronization;
	}

	/**
	 * Sets the lazy synchronization.
	 * 
	 * @param lazySynchronization
	 *            the new lazy synchronization
	 */
	public void setLazySynchronization( boolean lazySynchronization )
	{
		this.lazySynchronization = lazySynchronization;
	}

	/**
	 * Gets the paint bean.
	 * 
	 * @return the paint bean
	 */
	public PaintBean getPaintBean()
	{
		return paintBean;
	}

	/**
	 * Sets the paint bean.
	 * 
	 * @param paintBean
	 *            the new paint bean
	 */
	public void setPaintBean( PaintBean paintBean )
	{
		this.paintBean = paintBean;
		paintBean.setCurvedLabels( false );
		paintBean.setHideConflictingLabels( false );
		paintBean.setShowLabels( false );
		paintBean.setBoldLabels( false );
		paintBean.setOutlinedLabels( false );
		paintBean.setHighlightNeighbors( false );
		paintBean.setDrawLabelBox( false );
		paintBean.setNumberAffected( 0 );
		paintBean.setWidth( 800 );
		paintBean.setHeight( 400 );
		paintBean.expandAppearance();
		paintBean.expandLabels();
		expandBlackbook();
		paintBean.expandServerSide();
		paintBean.expandInteraction();
	}

	/**
	 * Checks if is blackbook local.
	 * 
	 * @return true, if is blackbook local
	 */
	public boolean isBlackbookLocal()
	{
		return blackbookLocal;
	}

	/**
	 * Sets the blackbook local.
	 * 
	 * @param blackbookLocal
	 *            the new blackbook local
	 */
	public void setBlackbookLocal( boolean blackbookLocal )
	{
		if( this.blackbookLocal != blackbookLocal )
		{
			this.blackbookLocal = blackbookLocal;
			loadBlackbookSDK();
		}
	}

	/**
	 * Gets the blackbook page size.
	 * 
	 * @return the blackbook page size
	 */
	public int getBlackbookPageSize()
	{
		return blackbookPageSize;
	}

	/**
	 * Sets the blackbook page size.
	 * 
	 * @param blackbookPageSize
	 *            the new blackbook page size
	 */
	public void setBlackbookPageSize( int blackbookPageSize )
	{
		this.blackbookPageSize = blackbookPageSize;
	}

	/**
	 * Gets the blackbook base url.
	 * 
	 * @return the blackbook base url
	 */
	public String getBlackbookBaseUrl()
	{
		return blackbookBaseUrl;
	}

	/**
	 * Sets the blackbook base url.
	 * 
	 * @param blackbookBaseUrl
	 *            the new blackbook base url
	 */
	public void setBlackbookBaseUrl( String blackbookBaseUrl )
	{
		this.blackbookBaseUrl = blackbookBaseUrl;
	}

	/**
	 * Gets the blackbook data source.
	 * 
	 * @return the blackbook data source
	 */
	public String getBlackbookDataSource()
	{
		return blackbookDataSource;
	}

	/**
	 * Sets the blackbook data source.
	 * 
	 * @param blackbookDataSource
	 *            the new blackbook data source
	 */
	public void setBlackbookDataSource( String blackbookDataSource )
	{
		this.blackbookDataSource = blackbookDataSource;
	}

	/**
	 * Checks if is blackbook expanded.
	 * 
	 * @return true, if is blackbook expanded
	 */
	public boolean isBlackbookExpanded()
	{
		return blackbookExpanded;
	}

	/**
	 * Sets the blackbook expanded.
	 * 
	 * @param blackbookExpanded
	 *            the new blackbook expanded
	 */
	public void setBlackbookExpanded( boolean blackbookExpanded )
	{
		this.blackbookExpanded = blackbookExpanded;
	}

	/**
	 * Expand blackbook.
	 */
	public void expandBlackbook()
	{
		setBlackbookExpanded( true );
	}

	/**
	 * Collapse blackbook.
	 */
	public void collapseBlackbook()
	{
		setBlackbookExpanded( false );
	}

	/**
	 * Should be called by synchronizeFromWiGi.
	 * 
	 * @param removedNodes
	 *            the removed nodes
	 */
	private void removeBlackbookGraph( Set<String> removedNodes )
	{
		long startTime = System.currentTimeMillis();

		try
		{
			if( removedNodes != null && removedNodes.size() > 0 )
			{
				// EWWWW!!!!
				getBBInterface().deselectAllNodes();
				getBBInterface().selectNodes( removedNodes );
				getBBInterface().deleteSelectedNodes();
			}

			paintBean.setupStatusMessage( "removed " + removedNodes.size() + " from Blackbook" );
		}
		catch( BlackbookServiceBrokerException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		printTime( "removeBlackbookGraph()", startTime );
	}

	/**
	 * Gets the renewed blackbook.
	 * 
	 * @return the renewed blackbook
	 */
	public String getRenewedBlackbook()
	{
		loadFullGraphFromBlackbook();
		return "";
	}

	/**
	 * Get certificate from request if in ssl mode w/ cert.
	 * 
	 * @return the request certificate
	 */
	private static X509Certificate[] getRequestCertificate()
	{
		FacesContext context = FacesContext.getCurrentInstance();
		Object certObj = context.getExternalContext().getRequestMap().get( "javax.servlet.request.X509Certificate" );
		java.security.cert.X509Certificate[] certs = null;
		if( certObj != null )
		{
			certs = (java.security.cert.X509Certificate[])certObj;
		}
		return certs;
	}

	/**
	 * Converts an cert chain of X509 certificates into a base 64 encoded string
	 * in PEM format.
	 * 
	 * @param certs
	 *            the certs
	 * @return String - cert in in base 64, PEM format
	 */
	protected static String toBase64EncodedPublicKey( X509Certificate[] certs )
	{
		StringBuilder base64EncodedPublicKey = new StringBuilder();

		for( X509Certificate certificate : certs )
		{
			base64EncodedPublicKey.append( "-----BEGIN CERTIFICATE-----\n" );

			try
			{
				base64EncodedPublicKey.append( Base64.encode( certificate.getEncoded() ) );
			}
			catch( java.security.cert.CertificateEncodingException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			base64EncodedPublicKey.append( "\n-----END CERTIFICATE-----\n" );

		}

		return base64EncodedPublicKey.toString();
	}

	/**
	 * Expand selected nodes.
	 */
	public void expandSelectedNodes()
	{
		long startTime = System.currentTimeMillis();

		try
		{
			// we haven't synchronized until needed and we need to now
			// because expand is a server operation that requires the client and
			// server
			// graphs to be in-synch
			if( lazySynchronization )
			{
				saveSandbox();
				blackbookNodesToUpdate.addAll( getSelectedNodesFromWiGi() );
			}
			else
			{
				blackbookNodesToUpdate.addAll( bb.getSelectedURIs() );
			}

			// nodes to add to the graph during polling
			blackbookNodesToAdd = getBBInterface().expandSelectedNodes();
			// nodes whose edges will be updated
			blackbookNodesToUpdate.addAll( blackbookNodesToAdd );
			paintBean.setupStatusMessage( "Expand yielded " + blackbookNodesToAdd.size() + " relationships" );

		}
		catch( Exception e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			paintBean.setupErrorMessage( "expand failed, please try again.", false );
		}
		startBlackbookPolling();
		printTime( "expandSelectedNodes", startTime );
	}

	/**
	 * Start blackbook polling.
	 */
	private void startBlackbookPolling()
	{
		// System.err.println("start blackbook polling");
		blackbookPagePollingEnabled = true;
		blackbookPaintPollingEnabled = true;
	}

	/**
	 * Checks if is blackbook page polling enabled.
	 * 
	 * @return true, if is blackbook page polling enabled
	 */
	public boolean isBlackbookPagePollingEnabled()
	{
		return blackbookPagePollingEnabled;
	}

	/**
	 * Checks if is blackbook paint polling enabled.
	 * 
	 * @return true, if is blackbook paint polling enabled
	 */
	public boolean isBlackbookPaintPollingEnabled()
	{
		if( !blackbookPagePollingEnabled && blackbookPaintPollingEnabled )
		{
			blackbookPaintPollingEnabled = false;
			return true;
		}
		return blackbookPaintPollingEnabled;
	}

	/**
	 * Page poll action method.
	 */
	public void pagePollActionMethod()
	{
		// long startTime = System.currentTimeMillis();

		System.out.println( "paging again" );

		if( blackbookPagePollingEnabled )
		{
			if( blackbookNodesToAdd.isEmpty() )
			{
				// FINISHED.
				if( blackbookNodesToUpdate.isEmpty() )
				{
					blackbookPagePollingEnabled = false;
					paintBean.setupStatusMessage( "Finished retrieving results" );
				}
				// keep adding edges.
				else
				{
					blackbookNextPageOfEdges();
				}
			}
			else
			{
				blackbookNextPageOfNodes();
			}
		}
		else
		{
			// System.err.println("paging not enabled");
		}

		// timeToPage = System.currentTimeMillis() - startTime;

		// automatically adjust
		// throttlePageSize();
	}

	/**
	 * Paint poll action method.
	 */
	public void paintPollActionMethod()
	{
		paintBean.forceSubgraphRefresh();
	}

	// private void throttlePageSize() {
	//		
	// float pollInterval = (float)blackbookPagePollInterval;
	// float pollTime = (float)timeToPage;
	// if(pollTime == 0f) {
	// //no real work was done, do not throttle.
	// return;
	// }
	// float newAdjustment = pollInterval/pollTime;
	// System.out.println("------------------------------------------");
	// System.out.println("page poll interval: " + pollInterval);
	// System.out.println("page poll time: " + pollTime);
	// System.out.println("page new adjustment: " + newAdjustment);
	//
	// //the historical adjustments added together
	// float totalAdjustments = newAdjustment;
	//		
	// //the new historical adjustments (the oldest is replaced by the newest)
	// float[] newPgAdjusts = new float[pageThrottleHistory.length];
	// newPgAdjusts[0] = newAdjustment;
	//		
	// //create the new historical adjustments and add them together
	// for(int i = 0; i < pageThrottleHistory.length-1; i++) {
	// newPgAdjusts[i+1] = pageThrottleHistory[i];
	// System.out.println("["+i+"]: " + pageThrottleHistory[i]);
	// totalAdjustments = totalAdjustments + pageThrottleHistory[i];
	// }
	//		
	// System.out.println("page total adjustments: " + totalAdjustments);
	//
	// //avg of the last PAGE_ADJUSTMENT_HISTORY_SIZE adjustments
	// float avgAdjustment = totalAdjustments/pageThrottleHistory.length;
	// System.out.println("page avg adjustment: " + avgAdjustment);
	//		
	// float prevPage = (float)lastPageSize;
	// System.out.println("page prev size: " + prevPage);
	//
	// if((prevPage * avgAdjustment) > 10f) {
	// blackbookPageSize = (int)(prevPage * avgAdjustment);
	// }
	//		
	// if(blackbookPageSize > MAX_PAGE_SIZE) {
	// blackbookPageSize = MAX_PAGE_SIZE;
	// }
	// System.out.println("page new size: " + blackbookPageSize);
	// System.out.println("------------------------------------------");
	//		
	// pageThrottleHistory = newPgAdjusts;
	// }

	// private void throttlePagePollInterval() {
	//		
	// }

	// private void throttlePaintPollInterval() {
	// long currentTime = System.currentTimeMillis();
	// long timeToPage = currentTime - lastPaintPollCompleted;
	//		
	// double percentageAdjustment =
	// (double)(((double)blackbookPaintPollInterval)/((double)timeToPage));
	// System.out.println("\n\n\n\nlatest graph adjustment: " +
	// percentageAdjustment);
	// //the historical adjustments added together
	// double totalAdjustments = percentageAdjustment;
	// //the new historical adjustments (the oldest is replaced by the newest)
	// double[] newPgAdjusts = new double[PAINT_THROTTLE_HISTORY_SIZE];
	// newPgAdjusts[0] = percentageAdjustment;
	//		
	// //create the new historical adjustments and add them together
	// for(int i = 0; i < PAINT_THROTTLE_HISTORY_SIZE-1; i++) {
	// newPgAdjusts[i+1] = paintThrottleHistory[i];
	// System.out.println("previous adjustment: " + paintThrottleHistory[i]);
	// totalAdjustments = totalAdjustments + paintThrottleHistory[i];
	// }
	//
	// //avg of the last PAGE_ADJUSTMENT_HISTORY_SIZE adjustments
	// double actualAdjustment =
	// totalAdjustments/((double)PAINT_THROTTLE_HISTORY_SIZE);
	// System.out.println("actual adjustment: " + actualAdjustment);
	// if((((double)blackbookPaintPollInterval) * actualAdjustment) > 20000) {
	// blackbookPaintPollInterval = (int)(((double)blackbookPaintPollInterval) *
	// actualAdjustment);
	// }
	// System.out.println("new page size: " + blackbookPageSize +"\n\n\n");
	//
	// paintThrottleHistory = newPgAdjusts;
	// }

	/**
	 * Gets the blackbook page poll interval.
	 * 
	 * @return the blackbook page poll interval
	 */
	public int getBlackbookPagePollInterval()
	{
		return blackbookPagePollInterval;
	}

	/**
	 * Sets the blackbook page poll interval.
	 * 
	 * @param blackbookPollInterval
	 *            the new blackbook page poll interval
	 */
	public void setBlackbookPagePollInterval( int blackbookPollInterval )
	{
		this.blackbookPagePollInterval = blackbookPollInterval;
	}

	/**
	 * Gets the blackbook paint poll interval.
	 * 
	 * @return the blackbook paint poll interval
	 */
	public int getBlackbookPaintPollInterval()
	{
		return blackbookPaintPollInterval;
	}

	/**
	 * Sets the blackbook paint poll interval.
	 * 
	 * @param interval
	 *            the new blackbook paint poll interval
	 */
	public void setBlackbookPaintPollInterval( int interval )
	{
		this.blackbookPaintPollInterval = interval;
	}

	/**
	 * paging call to add nodes from blackbook.
	 */
	public void blackbookNextPageOfNodes()
	{
		long startTime = System.currentTimeMillis();

		Set<String> nodesAddedWithSuccess = new HashSet<String>();

		if( blackbookNodesToAdd == null || blackbookNodesToAdd.size() == 0 )
		{
			// do nothing
		}
		else
		{
			try
			{
				Set<String> pageOfUris = new HashSet<String>();
				Iterator<String> uriItr = blackbookNodesToAdd.iterator();

				// these counters allow us to get the next page of results.
				int materialized = paintBean.getNumberOfNodes();
				int totalDesired = materialized + blackbookPageSize;

				// get results a page at a time.
				while( uriItr.hasNext() && materialized < totalDesired )
				{
					String uri = uriItr.next();
					pageOfUris.add( uri );
					materialized++;
					if( pageOfUris.size() % blackbookPageSize == 0 )
					{
						loadResourceDecoratorsIntoWiGi( pageOfUris );
						nodesAddedWithSuccess.addAll( pageOfUris );
						pageOfUris.clear();
					}
				}

				// get the final page of results (should be less than the page
				// size)
				if( !pageOfUris.isEmpty() )
				{
					loadResourceDecoratorsIntoWiGi( pageOfUris );
					nodesAddedWithSuccess.addAll( pageOfUris );
				}
			}
			catch( Exception e )
			{
				System.err.println( "could not add " + blackbookNodesToAdd.size() + " nodes." );
				e.printStackTrace();
			}
		}

		paintBean.setupStatusMessage( "Added " + nodesAddedWithSuccess.size() + " nodes" );
		blackbookNodesToAdd.removeAll( nodesAddedWithSuccess );

		printTime( "blackbookNextPageOfNodes", startTime );
	}

	/**
	 * paging call to add nodes from blackbook.
	 */
	public void blackbookNextPageOfEdges()
	{
		long startTime = System.currentTimeMillis();

		Set<String> nodesUpdatedWithSuccess = new HashSet<String>();

		if( blackbookNodesToUpdate == null || blackbookNodesToUpdate.size() == 0 )
		{
			// do nothing
		}
		else
		{
			try
			{
				Set<String> uriSubSet = new HashSet<String>();
				Iterator<String> uriItr = blackbookNodesToUpdate.iterator();

				// these counters allow us to get the next page of results.
				int materialized = paintBean.getNumberOfNodes();
				int totalDesired = materialized + blackbookPageSize;

				// get results a page at a time.
				while( uriItr.hasNext() && materialized < totalDesired )
				{
					String uri = uriItr.next();
					uriSubSet.add( uri );
					materialized++;
					if( uriSubSet.size() % blackbookPageSize == 0 )
					{
						loadEdgeDecoratorsIntoWiGi( uriSubSet );
						nodesUpdatedWithSuccess.addAll( uriSubSet );
						uriSubSet.clear();
					}
				}

				// get the leftovers, this should be less than the page size
				if( !uriSubSet.isEmpty() )
				{
					loadEdgeDecoratorsIntoWiGi( uriSubSet );
					nodesUpdatedWithSuccess.addAll( uriSubSet );
				}
			}
			catch( Exception e )
			{
				System.err.println( "could not add edges for " + blackbookNodesToUpdate.size() + " nodes." );
				e.printStackTrace();
			}
		}

		paintBean.setupStatusMessage( "Added edges for " + nodesUpdatedWithSuccess.size() + " nodes" );
		blackbookNodesToUpdate.removeAll( nodesUpdatedWithSuccess );

		printTime( "blackbookNextPageOfEdges", startTime );
	}

	/**
	 * Completely load/reload graph from Blackbook.
	 */
	private void loadFullGraphFromBlackbook()
	{
		long startTime = System.currentTimeMillis();

		Set<String> uris = new HashSet<String>();

		try
		{
			uris = getBBInterface().getURIs();
			// seed the initial graph
			if( blackbookLocal )
			{
				if( uris == null || uris.isEmpty() )
				{
					uris = getBBInterface().query( "does not matter" );
				}
			}

			blackbookNodesToAdd.clear();
			blackbookNodesToUpdate.clear();
			paintBean.getGraph().clearLevel( (int)paintBean.getLevel() );

			blackbookNodesToAdd.addAll( uris );
			blackbookNodesToUpdate.addAll( uris );

			startBlackbookPolling();

		}
		catch( Exception e )
		{
			e.printStackTrace();
		}

		printTime( "loadFullGraphFromBlackbook", startTime );
	}

	/**
	 * Load resource decorators into wi gi.
	 * 
	 * @param uriSubSet
	 *            the uri sub set
	 * @throws BlackbookServiceBrokerException
	 *             the blackbook service broker exception
	 */
	private void loadResourceDecoratorsIntoWiGi( Set<String> uriSubSet ) throws BlackbookServiceBrokerException
	{
		long startTime = System.currentTimeMillis();

		Map<String, ResourceDecorator> rDecs = new HashMap<String, ResourceDecorator>();

		if( uriSubSet != null && uriSubSet.size() > 0 )
		{
			rDecs = getBBInterface().getResourceDecorators( uriSubSet );
		}

		paintBean.getGraph().updateNodes( (int)paintBean.getLevel(), rDecs );

		paintBean.removeStatusMessage( PaintBean.UNSAVED_CHANGES_MSG );

		paintBean.setupStatusMessage( "Added " + rDecs.size() + " nodes" );
		printTime( "loadResourceDecorators", startTime );
	}

	/**
	 * Load edge decorators into wi gi.
	 * 
	 * @param uriSubSet
	 *            the uri sub set
	 * @throws BlackbookServiceBrokerException
	 *             the blackbook service broker exception
	 */
	private void loadEdgeDecoratorsIntoWiGi( Set<String> uriSubSet ) throws BlackbookServiceBrokerException
	{
		long startTime = System.currentTimeMillis();

		Map<String, ResourceDecorator> rDecs = new HashMap<String, ResourceDecorator>();

		rDecs = getBBInterface().getResourceDecorators( uriSubSet );

		int numNodesEffected = 0;
		for( String rDecUri : rDecs.keySet() )
		{
			if( rDecUri != null && !rDecUri.trim().isEmpty() )
			{
				ResourceDecorator rDec = rDecs.get( rDecUri );
				Set<EdgeDecorator> edgeDecs = rDec.getEdgeDecorators();
				paintBean.getGraph().updateEdges( (int)paintBean.getLevel(), edgeDecs );
				numNodesEffected++;
			}
		}

		paintBean.setupStatusMessage( "Added edges for " + numNodesEffected + " nodes" );

		printTime( "loadEdgeDecorators", startTime );
	}

	/**
	 * Load blackbook sdk.
	 */
	private void loadBlackbookSDK()
	{
		long startTime = System.currentTimeMillis();

		BlackbookServiceBrokerFactory.setClass( BlackbookServiceBrokerSOAP.class );
		bb = BlackbookServiceBrokerFactory.getInstance();
		String pkey = null;
		try
		{
			if( getRequestCertificate() != null )
			{
				pkey = toBase64EncodedPublicKey( getRequestCertificate() );
			}

			if( blackbookBaseUrl == null || blackbookBaseUrl.trim().isEmpty() )
			{
				blackbookBaseUrl = Request.getStringParameter( "baseURL" );
				blackbookDataSource = Request.getStringParameter( "dataSource" );
				System.out.println( "getting url parameters: " + blackbookBaseUrl + ", " + blackbookDataSource );
			}

			// data source to null if it is empty because this is a special
			// case for the call (create a new data source).
			if( blackbookDataSource != null && blackbookDataSource.trim().isEmpty() )
			{
				blackbookDataSource = null;
			}

			if( blackbookBaseUrl != null && !blackbookBaseUrl.isEmpty() )
			{
				getBBInterface().setConfigurationParameters( blackbookDataSource, blackbookBaseUrl, pkey );
				blackbookLocal = false;
			}
			else
			{
				blackbookLocal = true;
			}

			System.out.println( "sdk configured" );
		}
		catch( Exception e )
		{
			paintBean.setupErrorMessage( "Unable to load live Blackbook data, Using local test data.", false );
			blackbookLocal = true;
		}

		if( blackbookLocal )
		{
			try
			{
				BlackbookServiceBrokerFactory.setClass( BlackbookServiceBrokerStub.class );
				bb = BlackbookServiceBrokerFactory.getInstance();
				// seed graph
				bb.query( "" );
			}
			catch( Exception e )
			{
				paintBean.setupErrorMessage( "Unable to load local test data", false );
			}
		}

		printTime( "loadBlackbookSDK", startTime );
	}

	/**
	 * Revert to blackbook sandbox.
	 */
	public void revertToBlackbookSandbox()
	{
		long startTime = System.currentTimeMillis();
		loadFullGraphFromBlackbook();
		printTime( "revertToBlackbookSandbox", startTime );
	}

	/**
	 * Load blackbook ds.
	 */
	public void loadBlackbookDS()
	{
		long startTime = System.currentTimeMillis();
		loadFullGraphFromBlackbook();
		printTime( "loadBlackbookDS", startTime );
	}

	/**
	 * Checks if is save sandbox to blackbook.
	 * 
	 * @return true, if is save sandbox to blackbook
	 */
	public boolean isSaveSandboxToBlackbook()
	{
		saveSandbox();
		return true;
	}

	/**
	 * Sets the save sandbox to blackbook.
	 * 
	 * @param synch
	 *            the new save sandbox to blackbook
	 */
	public void setSaveSandboxToBlackbook( boolean synch )
	{/* noop */}

	/**
	 * Update the blackbook resource decorators based on the WiGi graph. If
	 * persistToBlackbook is true then the changes will persist to the server.
	 * Iterates over the WiGi model (nodes) detecting changes and then updates
	 * the blackbook nodes appropriately.
	 */
	public void saveSandbox()
	{
		long startTime = System.currentTimeMillis();

		try
		{
			if( paintBean.isWigiGraphChanged() )
			{
				Set<String> bbUris = bb.getURIs();

				System.out.println( "Persisting WiGi changes to Blackbook" );

				// nodes that should be in the graph
				Set<String> nodesPresent = new HashSet<String>();
				// a list of nodes that have been removed (likely)
				Set<String> nodesToRemove = new HashSet<String>();
				Set<String> nodesToSelect = new HashSet<String>();
				Set<String> nodesToDeselect = new HashSet<String>();

				DNVGraph graph = paintBean.getGraph();
				List<DNVNode> nodes = graph.getNodes( (int)paintBean.getLevel() );

				for( DNVNode node : nodes )
				{
					nodesPresent.add( node.getBbId() );

					if( node.isSelected() )
					{
						// add to list of nodes to select on bb server
						nodesToSelect.add( node.getBbId() );
					}
					// need to deselect the node
					else
					{
						// add to list of nodes to deselect on bb server
						nodesToDeselect.add( node.getBbId() );
					}
				}

				for( String bbUri : bbUris )
				{
					if( !nodesPresent.contains( bbUri ) )
					{
						nodesToRemove.add( bbUri );
					}
				}

				// remove nodes that are now missing in WiGi
				if( nodesToRemove.size() > 0 )
				{
					removeBlackbookGraph( nodesToRemove );
				}
				if( nodesToDeselect.size() > 0 )
				{
					getBBInterface().deselectNodes( nodesToDeselect );
					paintBean.setupStatusMessage( "deselected " + nodesToDeselect.size() + " in Blackbook" );
				}
				if( nodesToSelect.size() > 0 )
				{
					getBBInterface().selectNodes( nodesToSelect );
					paintBean.setupStatusMessage( "selected " + nodesToSelect.size() + " in Blackbook" );
				}
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
			paintBean.setupErrorMessage( "Could not save WiGi changes to Blackbook", false );
			return;
		}

		paintBean.removeStatusMessage( PaintBean.UNSAVED_CHANGES_MSG );
		paintBean.setupStatusMessage( "Saved WiGi changes to Blackbook" );
		printTime( "persistGraphToServer", startTime );
	}

	/**
	 * Checks if is validate synchronization from wi gi.
	 * 
	 * @return true, if is validate synchronization from wi gi
	 */
	public boolean isValidateSynchronizationFromWiGi()
	{
		validateSynchronizationFromWiGi();
		return true;
	}

	/**
	 * Sets the validate synchronization from wi gi.
	 * 
	 * @param valSynch
	 *            the new validate synchronization from wi gi
	 */
	public void setValidateSynchronizationFromWiGi( boolean valSynch )
	{/* noop */}

	/**
	 * Validate synchronization from wi gi.
	 */
	public void validateSynchronizationFromWiGi()
	{
		long startTime = System.currentTimeMillis();

		String report = "Validation synchronization from WiGi report: \n";

		try
		{
			Set<String> serverUris = getBBInterface().getURIs();
			Map<String, ResourceDecorator> serverMap = getBBInterface().getResourceDecorators( serverUris );

			Set<String> wigiUris = new HashSet<String>();
			for( DNVNode node : paintBean.getGraph().getNodes( (int)paintBean.getLevel() ) )
			{
				wigiUris.add( node.getBbId() );
			}

			// report sizes
			report = report + "			server uris size is = " + serverUris.size() + "\n";
			report = report + "			wigi uris size is = " + paintBean.getGraph().getNodes( (int)paintBean.getLevel() ).size() + "\n";

			// validate against current blackbook model uri list
			report = report + "			server uris contains all wigi uris? " + serverUris.containsAll( wigiUris ) + "\n";
			report = report + "			wigi uris contains all server uris? " + wigiUris.containsAll( serverUris ) + "\n";

			// validate materializations
			for( DNVNode node : paintBean.getGraph().getNodes( (int)paintBean.getLevel() ) )
			{
				ResourceDecorator serverResDec = serverMap.get( node.getBbId() );
				if( serverResDec == null )
				{
					report = report + "			node list is out of sync, stale node found " + node.getBbId() + "\n";
				}
				else
				{
					if( node.isSelected() != serverResDec.isSelected() )
					{
						report = report + "			selections are out of sync for node: " + node.getBbId() + " whose selection state is: "
								+ node.isSelected() + "\n";
					}
				}
			}

			for( DNVEdge edge : paintBean.getGraph().getEdges( (int)paintBean.getLevel() ) )
			{
				boolean edgeFoundOnServer = false;
				ResourceDecorator serverResDec = serverMap.get( edge.getFrom().getBbId() );
				for( EdgeDecorator edgeDec : serverResDec.getEdgeDecorators() )
				{
					if( edgeDec.getFromUri() == edge.getFrom().getBbId() && edgeDec.getToUri() == edge.getTo().getBbId() )
					{
						edgeFoundOnServer = true;
						break;
					}
				}
				if( !edgeFoundOnServer )
				{
					report = report + "			node associations are out of sync for edge: " + edge.getFrom().getBbId() + "--->" + edge.getTo().getBbId()
							+ "\n";
					report = report + "			server node associations are: " + serverResDec.getAssociatedUris() + "\n";
				}
			}

			// validate against current wigi model
		}
		catch( Exception e )
		{
			e.printStackTrace();
			paintBean.setupErrorMessage( "validation of synchronization failed", false );
		}
		System.out.println( report );
		printTime( "validateSynchronizationFromWigi", startTime );
	}

	// public void adjustLabelSettings() {
	// int currentLevel = labelLevel;
	//
	// if (paintBean.getNumberOfNodes() > MAX_FULLY_DECORATED_LABELS) {
	// paintBean.setHideConflictingLabels(false);
	// paintBean.setShowLabels(false);
	// paintBean.setBoldLabels(false);
	// paintBean.setOutlinedLabels(false);
	// paintBean.setDrawLabelBox(false);
	// } else {
	// paintBean.setHideConflictingLabels(true);
	// paintBean.setShowLabels(true);
	// paintBean.setBoldLabels(true);
	// paintBean.setOutlinedLabels(true);
	// paintBean.setDrawLabelBox(true);
	// }
	//
	// if (currentLevel != labelLevel) {
	// paintBean.setupStatusMessage("Labeling has changed to improve experience.");
	// }
	// }

	/**
	 * Checks if is quick layout.
	 * 
	 * @return true, if is quick layout
	 */
	public boolean isQuickLayout()
	{
		return paintBean.getNumberOfNodes() < MAX_NODES_LAYOUT;
	}

	/**
	 * Sets the quick layout.
	 */
	public void setQuickLayout()
	{
	// noop
	}

	/**
	 * Isolate selected nodes.
	 */
	public void isolateSelectedNodes()
	{
		double numAffected = paintBean.getNumberAffected();
		paintBean.setNumberAffected( 4 );
		paintBean.dragSelectedNodes();
		paintBean.setNumberAffected( numAffected );
	}

	/**
	 * Toggle node labels.
	 */
	public void toggleNodeLabels()
	{
		boolean showLabels = !paintBean.isShowLabels();
		paintBean.setShowLabels( showLabels );
		paintBean.setOutlinedLabels( showLabels );
	}

	/**
	 * Gets the selected nodes from wi gi.
	 * 
	 * @return the selected nodes from wi gi
	 */
	private Set<String> getSelectedNodesFromWiGi()
	{
		Set<String> selectedNodes = new HashSet<String>();
		List<DNVNode> wigiSelectedNodes = paintBean.getSelectedNodes();
		if( wigiSelectedNodes != null && !wigiSelectedNodes.isEmpty() )
		{
			for( DNVNode wNode : wigiSelectedNodes )
			{
				selectedNodes.add( wNode.getBbId() );
			}
		}

		return selectedNodes;
	}

	/**
	 * Gets the bB interface.
	 * 
	 * @return the bB interface
	 */
	private BlackbookServiceBrokerIfc getBBInterface()
	{
		if( bb == null )
		{
			loadBlackbookSDK();
		}
		return bb;
	}

	/**
	 * Prints the time.
	 * 
	 * @param methodName
	 *            the method name
	 * @param startTime
	 *            the start time
	 */
	public static void printTime( String methodName, long startTime )
	{
		System.err.println( methodName + " took " + ( System.currentTimeMillis() - startTime ) / 1000 + " sec." );
	}

	/**
	 * Sets the label level.
	 * 
	 * @param labelLevel
	 *            the new label level
	 */
	public void setLabelLevel( int labelLevel )
	{
		this.labelLevel = labelLevel;
	}

	/**
	 * Gets the label level.
	 * 
	 * @return the label level
	 */
	public int getLabelLevel()
	{
		return labelLevel;
	}

}