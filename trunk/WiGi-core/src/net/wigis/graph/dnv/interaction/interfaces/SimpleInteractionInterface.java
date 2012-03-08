package net.wigis.graph.dnv.interaction.interfaces;

import net.wigis.graph.PaintBean;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.utilities.Vector2D;

public interface SimpleInteractionInterface extends InteractionInterface
{
	public Vector2D performInteraction( PaintBean pb, DNVGraph graph, int width, int height, double minX, double minY, double maxX, double maxY, int mouseUpX, int mouseUpY, boolean sameNode, int level, double globalMinX, double globalMaxX,
			double globalMinY, double globalMaxY, DNVNode selectedNode, boolean released );

}
