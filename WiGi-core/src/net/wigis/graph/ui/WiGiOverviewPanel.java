package net.wigis.graph.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;

import javax.swing.JPanel;

import net.wigis.graph.PaintBean;
import net.wigis.graph.dnv.utilities.GraphFunctions;
import net.wigis.graph.dnv.utilities.Vector3D;

public class WiGiOverviewPanel extends JPanel implements MouseListener, MouseMotionListener
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4906054488479204347L;

	
	private PaintBean pb;
	
	public static final int OVERVIEW_SIZE = 250;
	
	private Component renderComponent = null;
	
	public WiGiOverviewPanel( PaintBean pb )
	{
		this.pb = pb;
		this.addMouseListener( this );
		this.addMouseMotionListener( this );
		this.setToolTipText( "Double click to reset zoom." );
	}
	
	public void paint( Graphics g )
	{
		super.paint( g );
		Graphics2D g2d = (Graphics2D)g;
		try
		{
			pb.paint( g2d, OVERVIEW_SIZE, OVERVIEW_SIZE, true, true );
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		
		int minX = (int)Math.round( OVERVIEW_SIZE * pb.getMinX() );
		int maxX = (int)Math.round( OVERVIEW_SIZE * pb.getMaxX() );
		int minY = (int)Math.round( OVERVIEW_SIZE * pb.getMinY() );
		int maxY = (int)Math.round( OVERVIEW_SIZE * pb.getMaxY() );
		
		g2d.setStroke( new BasicStroke( 2 ) );
		Vector3D color = GraphFunctions.convertColor( "#4a75b5" );
		g2d.setColor( new Color( color.getX(), color.getY(), color.getZ() ) );
		g2d.drawRect( minX, minY, maxX - minX - 1, maxY - minY - 1 );
		
		g2d.drawLine( maxX-10, maxY-1, maxX-1, maxY-10 );
		g2d.drawLine( maxX-6, maxY-1, maxX-1, maxY-6 );
		
		g2d.drawLine( maxX-10, minY, maxX, minY+10 );
		g2d.drawLine( maxX-6, minY, maxX, minY+6 );
		
		g2d.drawLine( minX+10, minY, minX, minY+10 );
		g2d.drawLine( minX+6, minY, minX, minY+6 );
		
		g2d.drawLine( minX+10, maxY, minX, maxY-10 );
		g2d.drawLine( minX+6, maxY, minX, maxY-6 );
////		g2d.drawRect( maxX-5, maxY-5, 10, 10 );
//		g2d.drawRect( maxX-5, minY-5, 10, 10 );
//		g2d.drawRect( minX-5, minY-5, 10, 10 );
//		g2d.drawRect( minX-5, maxY-5, 10, 10 );
	}

	private boolean draggingZoom = false;
	private boolean draggingSECorner = false;
	private boolean draggingNECorner = false;
	private boolean draggingNWCorner = false;
	private boolean draggingSWCorner = false;
	private int mouseDownX = 0;
	private int mouseDownY = 0;
	
	
	@Override
	public void mouseClicked( MouseEvent arg0 )
	{
		if( arg0.getClickCount() == 2 )
		{
			pb.setMinX( 0 );
			pb.setMaxX( 1 );
			pb.setMinY( 0 );
			pb.setMaxY( 1 );
			if( renderComponent != null )
			{
				renderComponent.repaint();
			}
		}
	}
	
	@Override
	public void mouseEntered( MouseEvent arg0 )
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited( MouseEvent arg0 )
	{
		this.setCursor( DEFAULT_CURSOR );		
	}

	private static final int INSIDE_BUFFER = 10;
	private static final int OUTSIDE_BUFFER = 2;
	
	@Override
	public void mousePressed( MouseEvent arg0 )
	{
		int x = arg0.getX();
		int y = arg0.getY();
		mouseDownX = x;
		mouseDownY = y;

		int minX = (int)Math.round( OVERVIEW_SIZE * pb.getMinX() );
		int maxX = (int)Math.round( OVERVIEW_SIZE * pb.getMaxX() );
		int minY = (int)Math.round( OVERVIEW_SIZE * pb.getMinY() );
		int maxY = (int)Math.round( OVERVIEW_SIZE * pb.getMaxY() );
		
		if( x >= maxX - INSIDE_BUFFER && x <= maxX + OUTSIDE_BUFFER && y >= maxY - INSIDE_BUFFER && y <= maxY + OUTSIDE_BUFFER )
		{
			draggingSECorner = true;
			return;
		}
		
		if( x >= maxX - INSIDE_BUFFER && x <= maxX + OUTSIDE_BUFFER && y >= minY - OUTSIDE_BUFFER && y <= minY + INSIDE_BUFFER )
		{
			draggingNECorner = true;
			return;
		}

		if( x >= minX - OUTSIDE_BUFFER && x <= minX + INSIDE_BUFFER && y >= minY - OUTSIDE_BUFFER && y <= minY + INSIDE_BUFFER )
		{
			draggingNWCorner = true;
			return;
		}
		
		if( x >= minX - OUTSIDE_BUFFER && x <= minX + INSIDE_BUFFER && y >= maxY - INSIDE_BUFFER && y <= maxY + OUTSIDE_BUFFER )
		{
			draggingSWCorner = true;
			return;
		}

		if( x >= minX && x <= maxX && y >= minY && y <= maxY )
		{
			draggingZoom = true;
		}
	}

	@Override
	public void mouseReleased( MouseEvent arg0 )
	{
		draggingZoom = false;
		draggingSECorner = false;
		draggingNECorner = false;
		draggingNWCorner = false;
		draggingSWCorner = false;
		if( renderComponent != null )
		{
			renderComponent.repaint();
		}
	}

	@Override
	public void mouseDragged( MouseEvent arg0 )
	{
		int x = arg0.getX();
		int y = arg0.getY();
		
		double xMove = (mouseDownX - x) / (double)OVERVIEW_SIZE;
		double yMove = (mouseDownY - y) / (double)OVERVIEW_SIZE;
		if( draggingZoom )
		{			
			pb.setMinX( pb.getMinX() - xMove );
			pb.setMaxX( pb.getMaxX() - xMove );
			pb.setMinY( pb.getMinY() - yMove );
			pb.setMaxY( pb.getMaxY() - yMove );
		}
		else if( draggingSECorner )
		{
			pb.setMaxX( pb.getMaxX() - Math.min( xMove, yMove) );
			pb.setMaxY( pb.getMaxY() - Math.min( xMove, yMove) );			
		}
		else if( draggingNECorner )
		{
			if( xMove > 0 )
			{
				pb.setMaxX( pb.getMaxX() + Math.min( xMove, yMove) );
				pb.setMinY( pb.getMinY() - Math.min( xMove, yMove) );
			}
			else
			{
				pb.setMaxX( pb.getMaxX() - Math.min( xMove, yMove) );
				pb.setMinY( pb.getMinY() + Math.min( xMove, yMove) );				
			}
		}
		else if( draggingNWCorner )
		{
			pb.setMinX( pb.getMinX() - Math.min( xMove, yMove) );
			pb.setMinY( pb.getMinY() - Math.min( xMove, yMove) );			
		}
		else if( draggingSWCorner )
		{
			if( xMove < 0 )
			{
				pb.setMinX( pb.getMinX() - Math.min( xMove, yMove) );
				pb.setMaxY( pb.getMaxY() + Math.min( xMove, yMove) );
			}
			else
			{
				pb.setMinX( pb.getMinX() + Math.min( xMove, yMove) );
				pb.setMaxY( pb.getMaxY() - Math.min( xMove, yMove) );
			}
		}

		mouseDownX = x;
		mouseDownY = y;
		if( renderComponent != null )
		{
			renderComponent.repaint();
		}
	}
	
	private static final Cursor SE_RESIZE_CURSOR = new Cursor( Cursor.SE_RESIZE_CURSOR ); 
	private static final Cursor NE_RESIZE_CURSOR = new Cursor( Cursor.NE_RESIZE_CURSOR ); 
	private static final Cursor NW_RESIZE_CURSOR = new Cursor( Cursor.NW_RESIZE_CURSOR ); 
	private static final Cursor SW_RESIZE_CURSOR = new Cursor( Cursor.SW_RESIZE_CURSOR ); 
	private static final Cursor MOVE_CURSOR = new Cursor( Cursor.HAND_CURSOR ); 
	private static final Cursor DEFAULT_CURSOR = new Cursor( Cursor.DEFAULT_CURSOR ); 

	@Override
	public void mouseMoved( MouseEvent arg0 )
	{
		int x = arg0.getX();
		int y = arg0.getY();

		int minX = (int)Math.round( OVERVIEW_SIZE * pb.getMinX() );
		int maxX = (int)Math.round( OVERVIEW_SIZE * pb.getMaxX() );
		int minY = (int)Math.round( OVERVIEW_SIZE * pb.getMinY() );
		int maxY = (int)Math.round( OVERVIEW_SIZE * pb.getMaxY() );

		if( x >= maxX - INSIDE_BUFFER && x <= maxX + OUTSIDE_BUFFER && y >= maxY - INSIDE_BUFFER && y <= maxY + OUTSIDE_BUFFER )
		{
			// Set cursor
			this.setCursor( SE_RESIZE_CURSOR );
			return;
		}
		
		if( x >= maxX - INSIDE_BUFFER && x <= maxX + OUTSIDE_BUFFER && y >= minY - OUTSIDE_BUFFER && y <= minY + INSIDE_BUFFER )
		{
			// Set cursor
			this.setCursor( NE_RESIZE_CURSOR );
			return;
		}

		if( x >= minX - OUTSIDE_BUFFER && x <= minX + INSIDE_BUFFER && y >= minY - OUTSIDE_BUFFER && y <= minY + INSIDE_BUFFER )
		{
			// Set cursor
			this.setCursor( NW_RESIZE_CURSOR );
			return;
		}
		
		if( x >= minX - OUTSIDE_BUFFER && x <= minX + INSIDE_BUFFER && y >= maxY - INSIDE_BUFFER && y <= maxY + OUTSIDE_BUFFER )
		{
			// Set cursor
			this.setCursor( SW_RESIZE_CURSOR );
			return;
		}

		if( x >= minX && x <= maxX && y >= minY && y <= maxY )
		{
			// Set cursor
			this.setCursor( MOVE_CURSOR );
			return;
		}

		this.setCursor( DEFAULT_CURSOR );
	}

	public void setRenderComponent( Component renderComponent )
	{
		this.renderComponent = renderComponent;
	}

	public Component getRenderComponent()
	{
		return renderComponent;
	}
}
