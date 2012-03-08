package net.wigis.graph.dnv.interaction.implementations;

import net.wigis.graph.PaintBean;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.interaction.interfaces.SimpleInteractionInterface;
import net.wigis.graph.dnv.layout.implementations.FruchtermanReingold;
import net.wigis.graph.dnv.utilities.Vector2D;

public class InterpolationWithSpring implements SimpleInteractionInterface
{
	private InterpolationMethod im = new InterpolationMethod();

	@Override
	public Vector2D performInteraction( PaintBean pb, DNVGraph graph, int width, int height, double minX, double minY, double maxX, double maxY, int mouseUpX, int mouseUpY,
			boolean sameNode, int level, double globalMinX, double globalMaxX, double globalMinY, double globalMaxY, DNVNode selectedNode, boolean released )
	{
		if( selectedNode != null )
		{
			Vector2D movement = im.performInteraction( pb, graph, width, height, minX, minY, maxX, maxY, mouseUpX, mouseUpY, sameNode, level, globalMinX, globalMaxX, globalMinY, globalMaxY, selectedNode, released );
			
			selectedNode.setProperty( "fixed", "true" );
			
			for( int i = 5; i > 0; i-- )
			{
				FruchtermanReingold.runIteration( 80, 80, graph, level, 0.1f * i, false, false, false );
			}

			selectedNode.removeProperty( "fixed" );
			
			return movement;
		}
		
		return null;
	}

	private static final String LABEL = "Interpolation with Spring Interaction";
	
	@Override
	public String getLabel()
	{
		return LABEL;
	}

}
