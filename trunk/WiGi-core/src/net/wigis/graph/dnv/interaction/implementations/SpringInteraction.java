package net.wigis.graph.dnv.interaction.implementations;

import net.wigis.graph.ImageRenderer;
import net.wigis.graph.PaintBean;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.interaction.helpers.InteractionFunctions;
import net.wigis.graph.dnv.interaction.interfaces.SimpleInteractionInterface;
import net.wigis.graph.dnv.layout.implementations.FruchtermanReingold;
import net.wigis.graph.dnv.utilities.Vector2D;

public class SpringInteraction implements SimpleInteractionInterface
{

	@Override
	public Vector2D performInteraction( PaintBean pb, DNVGraph graph, int width, int height, double minX, double minY, double maxX, double maxY, int mouseUpX, int mouseUpY,
			boolean sameNode, int level, double globalMinX, double globalMaxX, double globalMinY, double globalMaxY, DNVNode selectedNode, boolean released )
	{
		if( selectedNode != null )
		{
			Vector2D mouseUpWorld = ImageRenderer.transformScreenToWorld( mouseUpX, mouseUpY, minX, maxX, minY, maxY, globalMinX, globalMaxX, globalMinY,
					globalMaxY, width, height );
			Vector2D movement = ImageRenderer.getMovement( selectedNode, mouseUpWorld );
			InteractionFunctions.moveNode( selectedNode, movement );
			
			selectedNode.setProperty( "fixed", "true" );
			
			for( int i = 5; i > 0; i-- )
			{
				FruchtermanReingold.runIteration( (int)(globalMaxX - globalMinX), (int)(globalMaxY - globalMinY), graph, level, 0.1f * i, false, false, false );
			}
	
			selectedNode.removeProperty( "fixed" );
			
			return movement;
		}
		
		return null;
	}

	public static final String LABEL = "Spring Interaction";
	
	@Override
	public String getLabel()
	{
		return LABEL;
	}

}
