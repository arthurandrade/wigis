package net.wigis.graph.dnv.layout.implementations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.layout.helpers.Grid;
import net.wigis.graph.dnv.layout.interfaces.SimpleLayoutInterface;
import net.wigis.graph.dnv.utilities.Vector2D;

public class TouchGraphLayout implements SimpleLayoutInterface
{
	public static final String LABEL = "TouchGraph Layout";

	@Override
	public String getLabel()
	{
		return LABEL;
	}

	private double damper = 0;
	private double maxMotion = 0;
	private double lastMaxMotion = 0;
	private double motionRatio = 0;
	private boolean damping = true;
	private float rigidity = 1;
	private float newRigidity = 1;

	private DNVNode dragNode = null;

	public void setRigidity( float r )
	{
		newRigidity = r; // update rigidity at the end of the relax() thread
	}

	void setDragNode( DNVNode n )
	{
		dragNode = n;
	}

	private synchronized void relaxEdges( List<DNVEdge> edges )
	{
		for( DNVEdge e : edges )
		{
			float vx = e.getTo().getPosition().getX() - e.getFrom().getPosition().getX();
			float vy = e.getTo().getPosition().getY() - e.getFrom().getPosition().getY();
			float len = (float)Math.sqrt( vx * vx + vy * vy );

//			System.out.println( "len:" + len );
			
			float dx = vx * rigidity; // rigidity makes edges tighter
			float dy = vy * rigidity;

			dx /= ( e.getRestingDistance() * 100 );
			dy /= ( e.getRestingDistance() * 100 );

			// Edges pull directly in proportion to the distance between the
			// nodes. This is good,
			// because we want the edges to be stretchy. The edges are ideal
			// rubberbands. They
			// They don't become springs when they are too short. That only
			// causes the graph to
			// oscillate.

//			System.out.println( "edge move " + dx * len + ", " + dy * len );
			
//			System.out.println( "dx before: " + e.getTo().getAttribute( "dx" ) );
			if( e.getTo().justMadeLocal || !e.getFrom().justMadeLocal )
			{
				e.getTo().dx -= dx * len;
				e.getTo().dy -= dy * len;
//				e.getTo().setAttribute( "dx", (Double)e.getTo().getAttribute( "dx" ) - dx * len );
//				e.getTo().setAttribute( "dy", (Double)e.getTo().getAttribute( "dy" ) - dy * len );
			}
			else
			{
				e.getTo().dx -= dx * len / 10;
				e.getTo().dy -= dy * len / 10;
//				e.getTo().setAttribute( "dx", ( (Double)e.getTo().getAttribute( "dx" ) - dx * len ) / 10 );
//				e.getTo().setAttribute( "dy", ( (Double)e.getTo().getAttribute( "dy" ) - dy * len ) / 10 );
			}
			if( e.getFrom().justMadeLocal || !e.getTo().justMadeLocal )
			{
				e.getFrom().dx += dx * len;
				e.getFrom().dy += dy * len;
//				e.getFrom().setAttribute( "dx", (Double)e.getFrom().getAttribute( "dx" ) + dx * len );
//				e.getFrom().setAttribute( "dy", (Double)e.getFrom().getAttribute( "dy" ) + dy * len );
			}
			else
			{
				e.getFrom().dx += dx * len / 10;
				e.getFrom().dy += dy * len / 10;
//				e.getFrom().setAttribute( "dx", ( (Double)e.getFrom().getAttribute( "dx" ) + dx * len ) / 10 );
//				e.getFrom().setAttribute( "dy", ( (Double)e.getFrom().getAttribute( "dy" ) + dy * len ) / 10 );
			}
//			System.out.println( "dx after: " + e.getTo().getAttribute( "dx" ) );
		}
	}

	private synchronized void avoidLabels( Collection<DNVNode> nodes, Collection<DNVNode> allNodes )
	{
		Grid grid = new Grid( 150, allNodes );
		
		List<DNVNode> nodesList = new ArrayList<DNVNode>( nodes );
		
		for( DNVNode n1 : nodesList )
		{
//			System.out.println( nodesList.size() + " vs. " + grid.getPotentialNodes( n1 ).size() );
			for( DNVNode n2 : grid.getPotentialNodes( n1 ) )
			{
				if( n1 != n2 )
				{
					double dx = 0;
					double dy = 0;
					double vx = n1.getPosition().getX() - n2.getPosition().getX();
					double vy = n1.getPosition().getY() - n2.getPosition().getY();
					double len = vx * vx + vy * vy; // so it's length squared
					if( len == 0 )
					{
						dx = Math.random(); // If two nodes are right on top of
											// each other, randomly separate
						dy = Math.random();
					}
					else if( len < 600 * 600 )
					{ // 600, because we don't want deleted nodes to fly too far
						// away
						dx = vx / len; // If it was sqrt(len) then a single node
										// surrounded by many others will
						dy = vy / len; // always look like a circle. This might
										// look good at first, but I think
										// it makes large graphs look ugly + it
										// contributes to oscillation. A
										// linear function does not fall off
										// fast enough, so you get rough edges
										// in the 'force field'
					}

					int repSum = n1.repulsion * n2.repulsion / 100;

//					System.out.println( "repel move " + dx * repSum * rigidity + ", " + dy * repSum * rigidity );

//					System.out.println( "dx before: " + n1.getAttribute( "dx" ) );
					if( n1.justMadeLocal || !n2.justMadeLocal )
					{
						n1.dx += dx * repSum * rigidity;
						n1.dy += dy * repSum * rigidity;
//						n1.setAttribute( "dx", (Double)n1.getAttribute( "dx" ) + dx * repSum * rigidity );
//						n1.setAttribute( "dy", (Double)n1.getAttribute( "dy" ) + dy * repSum * rigidity );
					}
					else
					{
						n1.dx += dx * repSum * rigidity / 10;
						n1.dy += dy * repSum * rigidity / 10;
//						n1.setAttribute( "dx", ( (Double)n1.getAttribute( "dx" ) + dx * repSum * rigidity ) / 10 );
//						n1.setAttribute( "dy", ( (Double)n1.getAttribute( "dy" ) + dy * repSum * rigidity ) / 10 );
					}
//					System.out.println( "dx after: " + n1.getAttribute( "dx" ) );
					if( n2.justMadeLocal || !n1.justMadeLocal )
					{
						n2.dx -= dx * repSum * rigidity;
						n2.dy -= dy * repSum * rigidity;
//						n2.setAttribute( "dx", (Double)n2.getAttribute( "dx" ) - dx * repSum * rigidity );
//						n2.setAttribute( "dy", (Double)n2.getAttribute( "dy" ) - dy * repSum * rigidity );
					}
					else
					{
						n2.dx -= dx * repSum * rigidity / 10;
						n2.dy -= dy * repSum * rigidity / 10;
//						n2.setAttribute( "dx", ( (Double)n2.getAttribute( "dx" ) - dx * repSum * rigidity ) / 10 );
//						n2.setAttribute( "dy", ( (Double)n2.getAttribute( "dy" ) - dy * repSum * rigidity ) / 10 );
					}
					
//					System.out.println( "len:" + len );
//					if( len >= 100000 && n2.canRevive() )
//					{
//						n2.setActive();
//					}
				}
			}
		}
	}

	public void startDamper()
	{
		damping = true;
	}

	public void stopDamper()
	{
		damping = false;
		damper = 1.0; // A value of 1.0 means no damping
	}

	public void resetDamper()
	{ // reset the damper, but don't keep damping.
		damping = true;
		damper = 1.0;
	}

	public void stopMotion()
	{ // stabilize the graph, but do so gently by setting the damper to a low
		// value
		damping = true;
		if( damper > 0.3 )
			damper = 0.3;
		else
			damper = 0;
	}

	public void damp()
	{
		if( damping )
		{
			if( motionRatio <= 0.001 )
			{ // This is important. Only damp when the graph starts to move
				// faster
				// When there is noise, you damp roughly half the time. (Which
				// is a lot)
				//
				// If things are slowing down, then you can let them do so on
				// their own,
				// without damping.

				// If max motion<0.2, damp away
				// If by the time the damper has ticked down to 0.9, maxMotion
				// is still>1, damp away
				// We never want the damper to be negative though
				if( ( maxMotion < 0.2 || ( maxMotion > 1 && damper < 0.9 ) ) && damper > 0.01 )
					damper -= 0.01;
				// If we've slowed down significanly, damp more aggresively
				// (then the line two below)
				else if( maxMotion < 0.4 && damper > 0.003 )
					damper -= 0.003;
				// If max motion is pretty high, and we just started damping,
				// then only damp slightly
				else if( damper > 0.0001 )
					damper -= 0.0001;
			}
		}
		if( maxMotion < 0.001 && damping )
		{
			damper = 0;
		}
	}

	private synchronized void moveNodes( Collection<DNVNode> nodes )
	{
		lastMaxMotion = maxMotion;
		final double[] maxMotionA = new double[1];
		maxMotionA[0] = 0;

		List<DNVNode> nodesList = new ArrayList<DNVNode>( nodes );
		
		for( DNVNode n : nodesList )
		{
			float dx = n.dx;
			float dy = n.dy;
//			System.out.println( "dx:" + dx );
//			System.out.println( "dy:" + dy );
			
			dx *= damper; // The damper slows things down. It cuts down jiggling
							// at the last moment, and optimizes
			dy *= damper; // layout. As an experiment, get rid of the damper in
							// these lines, and make a
							// long straight line of nodes. It wiggles too much
							// and doesn't straighten out.

			n.dx = dx / 2; // Slow down, but don't stop. Nodes
											// in motion store momentum. This
											// helps when the force
			n.dy = dy / 2; // on a node is very low, but you
											// still want to get optimal layout.

			double distMoved = Math.sqrt( dx * dx + dy * dy ); // how far did
																// the node
																// actually
																// move?

//			if( distMoved < DNVNode.MIN_FORCE )
//			{
//				n.setInActive();
//			}
//			System.out.println( "distMoved:" + distMoved );
			
			if( !n.isFixed() && !( n == dragNode ) )
			{
				Vector2D movement = new Vector2D( (float)Math.max( -30, Math.min( 30, dx ) ), (float)Math.max( -30, Math.min( 30, dy ) ) );
//				System.out.println( "Moving " + n.getLabel() + " by " + movement );
				n.move( movement, false, false );
			}
			maxMotionA[0] = Math.max( distMoved, maxMotionA[0] );
		}

		maxMotion = maxMotionA[0];
		if( maxMotion > 0 )
		{
			motionRatio = lastMaxMotion / maxMotion - 1; // subtract 1 to make a
															// positive value
															// mean that
		}
		else
		{
			motionRatio = 0; // things are moving faster
		}

		damp();
	}

	public synchronized void relax( DNVGraph graph, int level )
	{
		for( int i = 0; i < 10; i++ )
		{
			synchronized( graph )
			{
				relaxEdges( graph.getEdges( level ) );
				avoidLabels( graph.getActiveNodes( level ), graph.getNodes( level ) );
				moveNodes( graph.getActiveNodes( level ) );
			}
			try
			{
				Thread.sleep( 10 );
			}
			catch( InterruptedException e )
			{
				break;
			}
		}
		if( rigidity != newRigidity )
		{
			rigidity = newRigidity; // update rigidity
		}
	}

	@Override
	public void runLayout( DNVGraph graph, int level )
	{
		graph.removeStoredPosition( level );
//		System.out.println("damping " + damping + " damp " + damper + "  maxM " + maxMotion + "  motR " + motionRatio );
//		int i = 0;
		damper = 1;
		while(damper>=0.1 || !damping || maxMotion>=0.001)
		{
			synchronized( graph )
			{
				relax( graph, level );
			}
//			System.out.println("damping " + damping + " damp " + damper + "  maxM " + maxMotion + "  motR " + motionRatio );
//			i++;
			try
			{
				Thread.sleep( 20 ); // Delay to wait for the prior repaint
									// command to finish.
			}
			catch( InterruptedException e )
			{
				break;
			}
		}
	}

}
