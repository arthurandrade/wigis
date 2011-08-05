package net.wigis.graph.ui;

import java.awt.Component;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;

import net.wigis.graph.ImageRenderer;
import net.wigis.graph.PaintBean;
import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVEntity;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.animations.Animation;
import net.wigis.graph.dnv.animations.RecursiveEdgeAnimation;
import net.wigis.graph.dnv.utilities.SortByLabelSize;
import net.wigis.graph.dnv.utilities.Vector2D;
import net.wigis.graph.dnv.utilities.Vector3D;
import net.wigis.settings.Settings;
import net.wigis.web.GraphServlet;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

public class WiGiGUIHandler
{
	private PaintBean pb;
	private JFrame overviewFrame;
	
	public WiGiGUIHandler( PaintBean pb, JFrame overviewFrame )
	{
		this.pb = pb;
		this.overviewFrame = overviewFrame;
		initializeAudio();
	}
	
	private static String wigiIconImagePath = "resources/wigis.gif";
	private Image wigiIconImage = null;

	public void setWiGiDockIcon()
	{
		setDockIcon( getWigiIconImage() );
	}

	private void generateWiGiIconImage()
	{
		URL url = net.wigis.graph.ui.WiGiGUI.class.getResource( wigiIconImagePath );
		Toolkit kit = Toolkit.getDefaultToolkit();
		wigiIconImage = kit.createImage( url );
	}
	
	public Image getWigiIconImage()
	{
		if( wigiIconImage == null )
		{
			generateWiGiIconImage();
		}
		return wigiIconImage;
	}

	public void setWigiIconImage( Image wigiIconImage )
	{
		this.wigiIconImage = wigiIconImage;
	}

	/**
	 * @param img
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void setDockIcon( Image img )
	{
		if( System.getProperty("os.name").equals( "Mac OS X" ) )
		{
			try
			{
				Class appc = Class.forName("com.apple.eawt.Application");
				Method m = appc.getMethod( "getApplication" );
				Object app = m.invoke( appc );
				Method setDockIconImage = appc.getMethod( "setDockIconImage", Image.class );
				setDockIconImage.invoke( app, img );
			}
			catch( ClassNotFoundException e )
			{
				e.printStackTrace();
			}
			catch( SecurityException e )
			{
				e.printStackTrace();
			}
			catch( NoSuchMethodException e )
			{
				e.printStackTrace();
			}
			catch( IllegalArgumentException e )
			{
				e.printStackTrace();
			}
			catch( IllegalAccessException e )
			{
				e.printStackTrace();
			}
			catch( InvocationTargetException e )
			{
				e.printStackTrace();
			}
			catch( NullPointerException e )
			{
				e.printStackTrace();
			}
		}
	}

	public void moveOverview( Component c )
	{
		overviewFrame.setBounds( c.getX() + c.getWidth() + 10, c.getY(), WiGiOverviewPanel.OVERVIEW_SIZE, WiGiOverviewPanel.OVERVIEW_SIZE );
	}

	private List<AudioStream> audioStreams = new ArrayList<AudioStream>();
	private String[] audioFiles = { "button-50.wav", "button-28.wav", "tada.wav" };
	
	private void initializeAudio()
	{
		try
		{
			audioStreams.clear();
			for( int i = 0; i < audioFiles.length; i++ )
			{
				AudioStream tempAudioStream = new AudioStream( new FileInputStream( "audio/" + audioFiles[i] ) );
				audioStreams.add( tempAudioStream );
			}
		}
		catch( FileNotFoundException e )
		{
//			e.printStackTrace();
		}
		catch( IOException e )
		{
//			e.printStackTrace();
		}
	}

	public void playSound( int index )
	{
		if( pb.isPlaySound() )
		{
			if( index < audioStreams.size() )
			{
				AudioPlayer.player.stop( audioStreams.get( index ) );
				initializeAudio();
				AudioPlayer.player.start( audioStreams.get( index ) );
			}
		}
	}

	public DNVNode picking( int mouseDownX, int mouseDownY, int selectionBuffer, boolean ctrlPressed, boolean setSelected )
	{
//		System.out.println( "Pick node at " + mouseDownX + ", " + mouseDownY );
		double minX = pb.getMinX();
		double maxX = pb.getMaxX();
		double minY = pb.getMinY();
		double maxY = pb.getMaxY();
		
		double globalMinX = pb.getGlobalMinX();
		double globalMaxX = pb.getGlobalMaxX();
		double globalMinY = pb.getGlobalMinY();
		double globalMaxY = pb.getGlobalMaxY();
		
		DNVGraph graph = pb.getGraph();
		int level = (int)pb.getLevel();
		int width = pb.getWidthInt();
		int height = pb.getHeightInt();
		
		List<DNVNode> nodes = new ArrayList<DNVNode>();
		if( graph.hasAttribute( "nodesByYPos_true" ) )
		{
			Integer maxSize = (Integer)graph.getAttribute( "maxSize" );
			if( maxSize != null )
			{
				@SuppressWarnings("unchecked")
				Map<Integer,List<DNVNode>> nodesByYPos = (Map<Integer,List<DNVNode>>)graph.getAttribute( "nodesByYPos_true" );
				nodes = nodesByYPos.get( ImageRenderer.getKey( mouseDownY, maxSize ) );
				if( nodes == null )
				{
					nodes = new ArrayList<DNVNode>();
				}
			}
		}
		else
		{
			nodes = graph.getNodes( level );
		}
		if( nodes.size() < 1000 )
		{
			SortByLabelSize sbls = new SortByLabelSize( pb.isHighlightNeighbors() );
			Collections.sort( nodes, sbls );
		}
		DNVNode node = null;
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
			if( node.isVisible() && (node.isForceLabel() || pb.isShowLabels() ) && node.getProperty( "faded" ) == null )
			{
				screenPosition = ImageRenderer.transformPosition( globalMinX, globalMaxX, globalMinY, globalMaxY, minX, maxX, minY, maxY, width, height, node.getPosition( true ) );
				ImageRenderer.Rectangle boundingRectangle = ImageRenderer.getRectangleBoundingTheLabel( node, screenPosition, null, (int)Math.round(pb.getNodeSize()*node.getRadius()), node.getLabel( pb.isInterpolationLabels() ), pb.isCurvedLabels() || node.isCurvedLabel(), pb.getLabelSize(),
						minX, maxX, width / pb.getWidth(), pb.isScaleLabels(), pb.getMaxLabelLength(), pb.getCurvedLabelAngle(), pb.isBoldLabels(), nodes.size() > 1000, false );
				if( mouseDownX >= boundingRectangle.left() &&
					mouseDownX <= boundingRectangle.right() &&
					mouseDownY <= boundingRectangle.bottom() && 
					mouseDownY >= boundingRectangle.top() )
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
					screenPosition = ImageRenderer.transformPosition( globalMinX, globalMaxX, globalMinY,
							globalMaxY, minX, maxX, minY, maxY, width, height, node.getPosition( true ) );

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
			nodeWidth = GraphServlet.getNodeWidth( pb, width, minX, maxX, node.getRadius() ) + selectionBuffer;
			// check if selected node is close enough to mouseDown
			if( Settings.DEBUG )
				System.out.println( "Minimum distance was " + Math.sqrt( minDistance ) );
			
			if( Math.sqrt( minDistance ) >= nodeWidth )
			{
				// Still no node selected so check nodes with faded labels
				for( int i = nodes.size() - 1; i >= 0; i-- )
				{
					node = nodes.get( i );
					if( node.isVisible() && (node.isForceLabel() || pb.isShowLabels() ) && node.getProperty( "faded" ) != null && Float.parseFloat( node.getProperty( "faded" ) ) > 0.1  )
					{
						screenPosition = ImageRenderer.transformPosition( globalMinX, globalMaxX, globalMinY, globalMaxY, minX, maxX, minY, maxY, width, height, node.getPosition( true ) );
						ImageRenderer.Rectangle boundingRectangle = ImageRenderer.getRectangleBoundingTheLabel( node, screenPosition, null, (int)Math.round(pb.getNodeSize()*node.getRadius()), node.getLabel( pb.isInterpolationLabels() ), pb.isCurvedLabels() || node.isCurvedLabel(), pb.getLabelSize(),
								minX, maxX, width / pb.getWidth(), pb.isScaleLabels(), pb.getMaxLabelLength(), pb.getCurvedLabelAngle(), pb.isBoldLabels(), nodes.size() > 1000, false );
						if( mouseDownX >= boundingRectangle.left() &&
							mouseDownX <= boundingRectangle.right() &&
							mouseDownY <= boundingRectangle.bottom() && 
							mouseDownY >= boundingRectangle.top() )
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

			nodeWidth = GraphServlet.getNodeWidth( pb, width, minX, maxX, node.getRadius() ) + selectionBuffer;
			// check if selected node is close enough to mouseDown
			if( Settings.DEBUG )
				System.out.println( "Minimum distance was " + Math.sqrt( minDistance ) + " node width=" + nodeWidth );
			if( Math.sqrt( minDistance ) < nodeWidth )
			{
//				if( node.isSelected() )
//				{
//					sameNode = true;
//				}
				if( setSelected )
				{
					pb.setSelectedNode( node, ctrlPressed );
				}
//				System.out.println( "Selected " + node.getLabel() + " " + node.getId() );
				return node;
			}
//			else if( activeCursors.size() <= 1 )
//			{
//				if( pb.getSelectedNode() != null )
//				{
//					pb.setSelectedNode( null, false );
////					System.out.println( "Deselecting all nodes." );
////					GraphServlet.runDocumentTopicsCircularLayout( null, pb, graph, level );
//				}
//			}
		}
		
		return null;
	}
	
	
	public static final int NO_ACTION = 0;
	public static final int EDGE_ANIMATION = 1;
	public static final int ZOOM_RESET = 2;
	public int handleDoubleClick()
	{
		DNVNode node = pb.getSelectedNode();
		if( node != null )
		{
			if( pb.isEnableAnimation() )
			{
				// Show recursive animation along all edges
//						System.out.println( "Double tap - adding animations" );
				createEdgeAnimation( node );
				
				return EDGE_ANIMATION;
			}
		}	
		else
		{
			// Reset zoom
			pb.setMinX( 0 );
			pb.setMinY( 0 );
			pb.setMaxX( 1 );
			pb.setMaxY( 1 );
			
			return ZOOM_RESET;
		}
		
		return NO_ACTION;
	}

	public static void createEdgeAnimation( DNVNode node )
	{
		Map<Integer,DNVEntity> handledEntities = new HashMap<Integer,DNVEntity>();
		Map<Integer,Boolean> handledDistances = new HashMap<Integer,Boolean>();
		for( DNVEdge edge : node.getFromEdges() )
		{
			if( edge.isVisible() )
			{
				Animation a = new RecursiveEdgeAnimation( 10, node, edge, handledEntities, handledDistances, 1 );
				node.getGraph().addAnimation( a );
			}
		}
		for( DNVEdge edge : node.getToEdges() )
		{
			if( edge.isVisible() )
			{
				Animation a = new RecursiveEdgeAnimation( 10, node, edge, handledEntities, handledDistances, 1 );
				node.getGraph().addAnimation( a );
			}
		}
	}
	
	public void performPanning( double movementX, double movementY )
	{
		pb.setMinX( pb.getMinX() + movementX/WiGiOverviewPanel.OVERVIEW_SIZE );
		pb.setMaxX( pb.getMaxX() + movementX/WiGiOverviewPanel.OVERVIEW_SIZE );
		pb.setMinY( pb.getMinY() + movementY/WiGiOverviewPanel.OVERVIEW_SIZE );
		pb.setMaxY( pb.getMaxY() + movementY/WiGiOverviewPanel.OVERVIEW_SIZE );
	}
	
	private DNVNode hoveredNode = null;
	private boolean hoveredNodeWasHighlighted = false;
	private Vector3D hoveredNodeOldColor = null;
	private DNVNode lastHoveredNode = null;
	private Vector3D RED = new Vector3D( 1, 0, 0 );
	public DNVNode getLastHoveredNode()
	{
		return lastHoveredNode;
	}
	
	public DNVNode getHoveredNode()
	{
		return hoveredNode;
	}

	public int hovering( int x, int y )
	{
		synchronized( pb.getGraph() )
		{
			DNVNode node = picking( x, y, 0, false, false );
			return setHoveredNode( node );
		}

	}

	public static final int NO_NODE_HOVERED = 0;
	public static final int HOVERED_NODE_RESTORED = 1;
	public static final int HOVERED_NODE_SET = 2;
	public static final int HOVERED_NODE_CHANGED = 3;
	public int setHoveredNode( DNVNode node )
	{
		if( node == null )
		{
			if( hoveredNode != null && hoveredNode != node )
			{
				restoreHoveredNode();
				return HOVERED_NODE_RESTORED;
			}
		}
		else if( node != null && hoveredNode == null )
		{
			hoveredNode = node;
			if( hoveredNode != null )
			{
				highlightHoveredNode();
				return HOVERED_NODE_SET;
			}
		}
		else if( node != null && hoveredNode != null && node != hoveredNode )
		{
			restoreHoveredNode();
			hoveredNode = node;
			if( hoveredNode != null )
			{
				highlightHoveredNode();
				return HOVERED_NODE_CHANGED;
			}
		}
		
		return NO_NODE_HOVERED;
	}
	
	private void highlightHoveredNode()
	{
		hoveredNodeWasHighlighted = hoveredNode.isHighlighted();
		hoveredNodeOldColor = hoveredNode.getHighlightColor();
		hoveredNode.setHighlighted( true );
		hoveredNode.setHighlightColor( RED );
	}

	private void restoreHoveredNode()
	{
		hoveredNode.setHighlighted( hoveredNodeWasHighlighted );
		hoveredNode.setHighlightColor( hoveredNodeOldColor );
		lastHoveredNode = hoveredNode;
		hoveredNode = null;
	}

}
