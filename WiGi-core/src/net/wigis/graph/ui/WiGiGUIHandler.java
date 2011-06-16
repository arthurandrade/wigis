package net.wigis.graph.ui;

import java.awt.Component;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

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
import net.wigis.settings.Settings;
import net.wigis.web.GraphServlet;

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
	
	public void moveOverview( Component c )
	{
		overviewFrame.setBounds( c.getX() + c.getWidth() + 10, c.getY(), WiGiOverviewPanel.OVERVIEW_SIZE, WiGiOverviewPanel.OVERVIEW_SIZE );
	}

	private List<AudioStream> audioStreams = new ArrayList<AudioStream>();
	private String[] audioFiles = {
			"button-50.wav",
			"button-28.wav",	
	};
	
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
			AudioPlayer.player.stop( audioStreams.get( index ) );
			initializeAudio();
			AudioPlayer.player.start( audioStreams.get( index ) );
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
		
		List<DNVNode> nodes = graph.getNodes( level );
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
						minX, maxX, width / pb.getWidth(), pb.isScaleLabels(), pb.getMaxLabelLength(), pb.getCurvedLabelAngle(), pb.isBoldLabels(), nodes.size() > 1000 );
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
								minX, maxX, width / pb.getWidth(), pb.isScaleLabels(), pb.getMaxLabelLength(), pb.getCurvedLabelAngle(), pb.isBoldLabels(), nodes.size() > 1000 );
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
	
	public void handleDoubleClick()
	{
		DNVNode node = pb.getSelectedNode();
		if( node != null )
		{
			if( pb.isEnableAnimation() )
			{
				// Show recursive animation along all edges
//						System.out.println( "Double tap - adding animations" );
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
		}	
		else
		{
			// Reset zoom
			pb.setMinX( 0 );
			pb.setMinY( 0 );
			pb.setMaxX( 1 );
			pb.setMaxY( 1 );
		}
	}
}
