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

package net.wigis.graph.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLJPanel;
import javax.swing.JFrame;

import net.wigis.graph.GraphsPathFilter;
import net.wigis.graph.ImageRenderer;
import net.wigis.graph.PaintBean;
import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.utilities.GraphFunctions;
import net.wigis.graph.dnv.utilities.SortByLabelSize;
import net.wigis.graph.dnv.utilities.Timer;
import net.wigis.graph.dnv.utilities.Vector2D;
import net.wigis.settings.Settings;
import net.wigis.web.GraphServlet;

// TODO: Auto-generated Javadoc
/**
 * The Class WiGiGUI.
 * 
 * This class runs a local GUI version of WiGis without many of the features from the full web system.
 * 
 * @author Brynjar Gretarsson
 */
public class WiGiGUI extends GLJPanel implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener, ComponentListener, FocusListener
{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The pb. */
	private PaintBean pb;
	
	private JFrame overviewFrame;

	/**
	 * Instantiates a new wi gi gui.
	 * 
	 * @param pb
	 *            the pb
	 */
	public WiGiGUI( GLCapabilities caps, PaintBean pb, JFrame overviewFrame )
	{
		super( caps );
		this.pb = pb;
		this.overviewFrame = overviewFrame;
	}

	private Timer timer = new Timer( Timer.NANOSECONDS );
	private boolean printFPS = false;
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	public void paint( Graphics g )
	{
		if( printFPS )
		{
			timer.setEnd();
			float segment = timer.getLastSegment( Timer.SECONDS );
			if( segment != 0 )
			{
				System.out.println( "FPS:" + (1.0f/segment) );
			}
			timer.setStart();
		}
		try
		{
			pb.paint( (Graphics2D)g, getWidth(), getHeight(), false, true );
			overviewFrame.repaint();
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
	}

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void main( String args[] ) throws IOException
	{
		GraphsPathFilter.init();
		PaintBean pb = new PaintBean();
		pb.setSelectedFile( Settings.GRAPHS_PATH + "UserStudy/testGraphs/graph1small.dnv" );
		pb.setWhiteSpaceBuffer( 0.14f );
		pb.setDrawNeighborHighlight( true );
		pb.setInterpolationMethodUseWholeGraph( true );
		pb.setScalePositions( true );
		pb.setWidth( 800 );
		pb.setHeight( 800 );
		JFrame frame = new JFrame( "WiGi - GUI" );
		frame.setSize( pb.getWidthInt(), pb.getHeightInt() );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		GLCapabilities caps = new GLCapabilities();
		caps.setDoubleBuffered( true );
		JFrame overviewFrame = new JFrame("Overview");
		overviewFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		overviewFrame.setUndecorated( true );
		overviewFrame.setSize( WiGiOverviewPanel.OVERVIEW_SIZE, WiGiOverviewPanel.OVERVIEW_SIZE );
		overviewFrame.setResizable( false );
		WiGiOverviewPanel overviewPanel = new WiGiOverviewPanel( pb );
		overviewFrame.getContentPane().add( overviewPanel );
		WiGiGUI canvas = new WiGiGUI( caps, pb, overviewFrame );
		overviewPanel.setRenderComponent( canvas );
		canvas.setBounds( 0, 0, pb.getWidthInt(), pb.getHeightInt() );
//		canvas.setDoubleBuffered( true );
		canvas.addMouseListener( canvas );
		canvas.addMouseMotionListener( canvas );
		canvas.addMouseWheelListener( canvas );
		canvas.addKeyListener( canvas );
		frame.addComponentListener( canvas );
		frame.add( canvas );
		frame.setVisible( true );
//		moveOverview();
		overviewFrame.setBounds( frame.getX() + frame.getWidth() + 10, frame.getY(), WiGiOverviewPanel.OVERVIEW_SIZE, WiGiOverviewPanel.OVERVIEW_SIZE );
		overviewFrame.setVisible(true);
		
//		frame.createBufferStrategy( 2 );
//		BufferStrategy strategy = frame.getBufferStrategy();
		
	}

	
	@Override
	public void mouseWheelMoved( MouseWheelEvent e )
	{
		int x = e.getX();
		int y = e.getY();
		
		int amount = e.getWheelRotation();
		
		zoom( x, y, amount );
		
		this.repaint();
	}

	public void zoom( int x, int y, float amount )
	{
//		System.out.println( "Zooming " + x + ", " + y + " : " + amount );
		int zoomOut = 0;
		if( amount > 0 )
			zoomOut = 1;
		else if( amount < 0 )
			zoomOut = -1;
		
		int minX = (int)Math.round( WiGiOverviewPanel.OVERVIEW_SIZE * pb.getMinX() );
		int maxX = (int)Math.round( WiGiOverviewPanel.OVERVIEW_SIZE * pb.getMaxX() );
		int minY = (int)Math.round( WiGiOverviewPanel.OVERVIEW_SIZE * pb.getMinY() );
		int maxY = (int)Math.round( WiGiOverviewPanel.OVERVIEW_SIZE * pb.getMaxY() );

		int width = maxX - minX;
		int height = maxY - minY;
		
		if( zoomOut != 1 && width <= 10 )
		{
			return;
		}
		
		int offset;
		if( zoomOut == 1 )
		{
			offset = width / 3;
		}
		else
		{
			offset = width / 4;
		}

//	    if ( width + zoomOut*offset > 10)
//   	    {
   	        // zoom like in google maps, depends on where the mouse is at the time of the mouse wheel scroll
   	        double percent = x / pb.getWidth();
   	        
   	        int zoomNewW = width + zoomOut*offset;
   	        double zoomNewL = (percent*width + minX) - percent*zoomNewW;
   	        pb.setMinX( zoomNewL / (double)WiGiOverviewPanel.OVERVIEW_SIZE );
   	        pb.setMaxX( (zoomNewL+zoomNewW) / (double)WiGiOverviewPanel.OVERVIEW_SIZE );
   	        
   	        percent = y / pb.getHeight();
   	        int zoomNewH = height + zoomOut*offset;
   	        double zoomNewT = (percent*height + minY) - percent*zoomNewH;
   	        pb.setMinY( zoomNewT / (double)WiGiOverviewPanel.OVERVIEW_SIZE );
   	        pb.setMaxY( (zoomNewT+zoomNewH ) / (double)WiGiOverviewPanel.OVERVIEW_SIZE );
//   	    }
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyPressed( KeyEvent e )
	{
	// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyReleased( KeyEvent e )
	{
	// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyTyped( KeyEvent e )
	{
	// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked( MouseEvent e )
	{
		if( e.getClickCount() == 2 )
		{
			pb.setMinX( 0 );
			pb.setMaxX( 1 );
			pb.setMinY( 0 );
			pb.setMaxY( 1 );
			this.repaint();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered( MouseEvent e )
	{
	// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited( MouseEvent e )
	{
	// TODO Auto-generated method stub

	}

	/** The mouse down x. */
	int mouseDownX = -1;

	/** The mouse down y. */
	int mouseDownY = -1;

	/** The selected node. */
	DNVNode selectedNode = null;

	/** The ctrl pressed. */
	boolean ctrlPressed = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed( MouseEvent e )
	{
		mouseDownX = e.getPoint().x;
		mouseDownY = e.getPoint().y;

		DNVGraph graph = pb.getGraph();
		int level = (int)pb.getLevel();

		double globalMinX = GraphFunctions.getMinXPosition( graph, level, true );
		double globalMaxX = GraphFunctions.getMaxXPosition( graph, level, true );
		double globalMinY = GraphFunctions.getMinYPosition( graph, level, true );
		double globalMaxY = GraphFunctions.getMaxYPosition( graph, level, true );
		if( globalMinY == globalMaxY )
		{
			globalMinY -= 10;
			globalMaxY += 10;
		}
		if( globalMinX == globalMaxX )
		{
			globalMinX -= 10;
			globalMaxX += 10;
		}
		double yBuffer = ( globalMaxY - globalMinY ) * pb.getWhiteSpaceBuffer();
		double xBuffer = ( globalMaxX - globalMinX ) * pb.getWhiteSpaceBuffer();
		globalMaxY += yBuffer;
		globalMinY -= yBuffer;
		globalMaxX += xBuffer;
		globalMinX -= xBuffer;

		List<DNVNode> nodes = graph.getNodes( level );
		SortByLabelSize sbls = new SortByLabelSize( pb.isHighlightNeighbors() );
		Collections.sort( nodes, sbls );
		DNVNode node;
		Vector2D screenPosition;
		double distance;
		double minDistance = Integer.MAX_VALUE;
		int nodeI = -1;
		int distX = 0; // dist b/w this node and mouse click
		int distY = 0;

		// Check if user clicked on a solid node label
		for( int i = nodes.size() - 1; i >= 0; i-- )
		{
			node = nodes.get( i );
			if( node.isVisible() && ( node.isForceLabel() || pb.isShowLabels() ) && node.getProperty( "faded" ) == null )
			{
				screenPosition = ImageRenderer.transformPosition( globalMinX, globalMaxX, globalMinY, globalMaxY, pb.getMinX(), pb.getMaxX(), pb
						.getMinY(), pb.getMaxY(), getWidth(), getHeight(), node.getPosition( true ) );
				ImageRenderer.Rectangle boundingRectangle = ImageRenderer.getRectangleBoundingTheLabel( node, screenPosition, null, (int)Math
						.round( pb.getNodeSize() * node.getRadius() ), node.getLabel( pb.isInterpolationLabels() ), pb.isCurvedLabels()
						|| node.isCurvedLabel(), pb.getLabelSize(), pb.getMinX(), pb.getMaxX(), getWidth() / pb.getWidth(), pb.isScaleLabels(), pb
						.getMaxLabelLength(), pb.getCurvedLabelAngle(), pb.isBoldLabels(), false );
				if( mouseDownX >= boundingRectangle.left() && mouseDownX <= boundingRectangle.right() && mouseDownY <= boundingRectangle.bottom()
						&& mouseDownY >= boundingRectangle.top() )
				{
					distX = (int)( mouseDownX - screenPosition.getX() );
					distY = (int)( mouseDownY - screenPosition.getY() );
					node.setProperty( "distX", "" + distX );
					node.setProperty( "distY", "" + distY );
					minDistance = 0;
					nodeI = i;
					break;
				}
			}
		}

		if( nodeI == -1 )
		{
			// loop thru all nodes to find closest node
			for( int i = nodes.size() - 1; i >= 0; i-- )
			{
				node = nodes.get( i );
				if( node.isVisible() )
				{
					screenPosition = ImageRenderer.transformPosition( globalMinX, globalMaxX, globalMinY, globalMaxY, pb.getMinX(), pb.getMaxX(), pb
							.getMinY(), pb.getMaxY(), getWidth(), getHeight(), node.getPosition( true ) );

					// find node closest to mouseDown
					distX = (int)( mouseDownX - screenPosition.getX() );
					distY = (int)( mouseDownY - screenPosition.getY() );

					distance = distX * distX + distY * distY;

					if( distance < minDistance )
					{
						node.setProperty( "distX", "" + distX );
						node.setProperty( "distY", "" + distY );

						minDistance = distance;
						nodeI = i;
					}
				}
			}
		}

		if( nodes.size() > 0 )
		{
			node = nodes.get( nodeI );

			double nodeWidth;
			nodeWidth = GraphServlet.getNodeWidth( pb, getWidth(), pb.getMinX(), pb.getMaxX(), node.getRadius() );
			// check if selected node is close enough to mouseDown
			if( Settings.DEBUG )
				System.out.println( "Minimum distance was " + Math.sqrt( minDistance ) );

			if( Math.sqrt( minDistance ) >= nodeWidth )
			{
				// Still no node selected so check nodes with faded labels
				for( int i = nodes.size() - 1; i >= 0; i-- )
				{
					node = nodes.get( i );
					if( node.isVisible() && ( node.isForceLabel() || pb.isShowLabels() ) && node.getProperty( "faded" ) != null
							&& Float.parseFloat( node.getProperty( "faded" ) ) > 0.1 )
					{
						screenPosition = ImageRenderer.transformPosition( globalMinX, globalMaxX, globalMinY, globalMaxY, pb.getMinX(), pb.getMaxX(),
								pb.getMinY(), pb.getMaxY(), getWidth(), getHeight(), node.getPosition( true ) );
						ImageRenderer.Rectangle boundingRectangle = ImageRenderer.getRectangleBoundingTheLabel( node, screenPosition, null, (int)Math
								.round( pb.getNodeSize() * node.getRadius() ), node.getLabel( pb.isInterpolationLabels() ), pb.isCurvedLabels()
								|| node.isCurvedLabel(), pb.getLabelSize(), pb.getMinX(), pb.getMaxX(), getWidth() / pb.getWidth(), pb
								.isScaleLabels(), pb.getMaxLabelLength(), pb.getCurvedLabelAngle(), pb.isBoldLabels(), false );
						if( mouseDownX >= boundingRectangle.left() && mouseDownX <= boundingRectangle.right() && mouseDownY <= boundingRectangle.bottom()
								&& mouseDownY >= boundingRectangle.top() )
						{
							distX = (int)( mouseDownX - screenPosition.getX() );
							distY = (int)( mouseDownY - screenPosition.getY() );
							node.setProperty( "distX", "" + distX );
							node.setProperty( "distY", "" + distY );
							minDistance = 0;
							nodeI = i;
							break;
						}
					}
				}
			}

			node = nodes.get( nodeI );

			nodeWidth = GraphServlet.getNodeWidth( pb, getWidth(), pb.getMinX(), pb.getMaxX(), node.getRadius() );
			// check if selected node is close enough to mouseDown
			if( Settings.DEBUG )
				System.out.println( "Minimum distance was " + Math.sqrt( minDistance ) );
			if( Math.sqrt( minDistance ) < nodeWidth )
			{
				// if( node.isSelected() )
				// {
				// sameNode = true;
				// }
				pb.setSelectedNode( node, ctrlPressed );
				selectedNode = node;
			}
			else
			{
				if( pb.getSelectedNode() != null )
				{
					pb.setSelectedNode( null, ctrlPressed );
					// GraphServlet.runDocumentTopicsCircularLayout( null, pb,
					// graph, level );
				}
			}
			if( selectedNode == null )
			{
				minDistance = Integer.MAX_VALUE;
				List<DNVEdge> edges = graph.getEdges( level );
				DNVEdge edge;
				Vector2D screenPosition2;
				int edgeI = 0;
				for( int i = 0; i < edges.size(); i++ )
				{
					edge = edges.get( i );
					if( edge.isVisible() )
					{
						screenPosition = ImageRenderer.transformPosition( globalMinX, globalMaxX, globalMinY, globalMaxY, pb.getMinX(), pb.getMaxX(),
								pb.getMinY(), pb.getMaxY(), getWidth(), getHeight(), edge.getFrom().getPosition( true ) );
						screenPosition2 = ImageRenderer.transformPosition( globalMinX, globalMaxX, globalMinY, globalMaxY, pb.getMinX(),
								pb.getMaxX(), pb.getMinY(), pb.getMaxY(), getWidth(), getHeight(), edge.getTo().getPosition( true ) );
						distance = GraphServlet.getPointLineDistance( screenPosition, screenPosition2, mouseDownX, mouseDownY );
						if( distance < minDistance )
						{
							minDistance = distance;
							edgeI = i;
						}
					}
				}

				if( edges.size() > 0 )
				{
					edge = edges.get( edgeI );

					double edgeWidth = Math.max( edge.getThickness(), 4 );
					// check if selected node is close enough to mouseDown
					if( Settings.DEBUG )
						System.out.println( "Minimum distance was " + Math.sqrt( minDistance ) );
					if( Math.sqrt( minDistance ) < edgeWidth / 2.0 )
					{
						if( edge.isSelected() )
						{
							// sameNode = true;
						}
						pb.setSelectedEdge( edge, ctrlPressed );
					}
					else
					{
						pb.setSelectedEdge( null, ctrlPressed );
					}
				}
			}
		}

		this.repaint();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased( MouseEvent e )
	{
		moveNode( e.getPoint().x, e.getPoint().y );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent
	 * )
	 */
	@Override
	public void mouseDragged( MouseEvent e )
	{
		moveNode( e.getPoint().x, e.getPoint().y );
	}

	/**
	 * Move node.
	 * 
	 * @param mouseUpX
	 *            the mouse up x
	 * @param mouseUpY
	 *            the mouse up y
	 */
	public void moveNode( int mouseUpX, int mouseUpY )
	{
		DNVGraph graph = pb.getGraph();
		int level = (int)pb.getLevel();

		double globalMinX = GraphFunctions.getMinXPosition( graph, level, true );
		double globalMaxX = GraphFunctions.getMaxXPosition( graph, level, true );
		double globalMinY = GraphFunctions.getMinYPosition( graph, level, true );
		double globalMaxY = GraphFunctions.getMaxYPosition( graph, level, true );
		if( globalMinY == globalMaxY )
		{
			globalMinY -= 10;
			globalMaxY += 10;
		}
		if( globalMinX == globalMaxX )
		{
			globalMinX -= 10;
			globalMaxX += 10;
		}
		double yBuffer = ( globalMaxY - globalMinY ) * pb.getWhiteSpaceBuffer();
		double xBuffer = ( globalMaxX - globalMinX ) * pb.getWhiteSpaceBuffer();
		globalMaxY += yBuffer;
		globalMinY -= yBuffer;
		globalMaxX += xBuffer;
		globalMinX -= xBuffer;

		GraphServlet.moveSelectedNode( null, pb, graph, level, getWidth(), getHeight(), pb.getMinX(), pb.getMinY(), pb.getMaxX(), pb.getMaxY(),
				mouseUpX, mouseUpY, false, globalMinX, globalMaxX, globalMinY, globalMaxY, selectedNode, null );

		repaint();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseMoved( MouseEvent e )
	{
	// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seejava.awt.event.ComponentListener#componentHidden(java.awt.event.
	 * ComponentEvent)
	 */
	@Override
	public void componentHidden( ComponentEvent e )
	{
	// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent
	 * )
	 */
	@Override
	public void componentMoved( ComponentEvent e )
	{
//		System.out.println( "Component Moved" );
		overviewFrame.setBounds( e.getComponent().getX() + e.getComponent().getWidth() + 10, e.getComponent().getY(), WiGiOverviewPanel.OVERVIEW_SIZE, WiGiOverviewPanel.OVERVIEW_SIZE );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seejava.awt.event.ComponentListener#componentResized(java.awt.event.
	 * ComponentEvent)
	 */
	@Override
	public void componentResized( ComponentEvent e )
	{
		pb.setWidth( e.getComponent().getWidth() );
		pb.setHeight( e.getComponent().getHeight() );
		repaint();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ComponentListener#componentShown(java.awt.event.ComponentEvent
	 * )
	 */
	@Override
	public void componentShown( ComponentEvent e )
	{
	// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
	 */
	@Override
	public void focusGained( FocusEvent e )
	{
	// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
	 */
	@Override
	public void focusLost( FocusEvent e )
	{
	// TODO Auto-generated method stub

	}
}
