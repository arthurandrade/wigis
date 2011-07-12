package net.wigis.graph.dnv.interaction.interfaces;

import javax.servlet.http.HttpServletRequest;

import net.wigis.graph.PaintBean;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;

public interface RecommendationInteractionInterface extends InteractionInterface
{
	public void performInteraction( PaintBean pb, DNVGraph graph, int width, int height, double minX, double minY, double maxX, double maxY, int mouseUpX, int mouseUpY, boolean sameNode, int level, double globalMinX, double globalMaxX,
			double globalMinY, double globalMaxY, DNVNode selectedNode, boolean released, HttpServletRequest request );

}
