package net.wigis.graph.dnv.layout;

import javax.servlet.http.HttpServletRequest;

import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.utilities.Vector2D;

public interface RecommendationLayoutInterface extends LayoutInterface
{
	public void runLayout( DNVGraph graph, DNVNode centralNode, int level, float centerX, float centerY, boolean circle );

	public void moveNode( DNVGraph graph, DNVNode selectedNode, DNVNode centralNode, Vector2D transformScreenToWorld, float hopDistance, boolean recommendationCircle,
			HttpServletRequest request, boolean sameNode );
}
