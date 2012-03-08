package net.wigis.graph.dnv.interaction.implementations;

import net.wigis.graph.ImageRenderer;
import net.wigis.graph.PaintBean;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.interaction.helpers.InteractionFunctions;
import net.wigis.graph.dnv.interaction.interfaces.SimpleInteractionInterface;
import net.wigis.graph.dnv.utilities.Vector2D;

public class SimpleInteraction implements SimpleInteractionInterface
{
	@Override
	public Vector2D performInteraction( PaintBean pb, DNVGraph graph, int width, int height, double minX, double minY, double maxX, double maxY, int mouseUpX, int mouseUpY,
			boolean sameNode, int level, double globalMinX, double globalMaxX, double globalMinY, double globalMaxY, DNVNode selectedNode, boolean released )
	{
		Vector2D mouseUpWorld = ImageRenderer.transformScreenToWorld( mouseUpX, mouseUpY, pb.getMinX(), pb.getMaxX(), pb.getMinY(), pb.getMaxY(), pb.getGlobalMinX(), pb.getGlobalMaxX(), pb.getGlobalMinY(),
				pb.getGlobalMaxY(), pb.getWidthInt(), pb.getHeightInt() );
		Vector2D movement = ImageRenderer.getMovement( selectedNode, mouseUpWorld );
		for( DNVNode node : pb.getGraph().getSelectedNodes( (int)pb.getLevel() ).values() )
		{
			if( node != null )
			{
				synchronized( pb.getGraph() )
				{
					InteractionFunctions.moveNode( node, movement );
				}
			}
		}

		return movement;
	}

	public static final String LABEL = "Simple Interaction";
	
	@Override
	public String getLabel()
	{
		return LABEL;
	}

}
