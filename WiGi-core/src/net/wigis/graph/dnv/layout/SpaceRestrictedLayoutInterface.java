package net.wigis.graph.dnv.layout;

import java.util.Collection;

import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;

public interface SpaceRestrictedLayoutInterface extends LayoutInterface
{
	public void runLayout( float width, float height, Collection<DNVNode> nodes, Collection<DNVEdge> edges, float coolingFactor,
			boolean useNumberOfSubnodes, boolean useEdgeRestingDistance, boolean useNodeSize );
	public void runLayout( float width, float height, Collection<DNVNode> nodes, Collection<DNVEdge> edges, float coolingFactor,
			boolean useNumberOfSubnodes );
	public void runLayout( float width, float height, DNVGraph graph, float coolingFactor, int level, boolean layoutAllLevels,
			boolean useNumberOfSubnodes, boolean useEdgeRestingDistance, boolean useNodeSize );
	public void runLayout( float width, float height, DNVGraph graph, float coolingFactor, int level, boolean layoutAllLevels,
			boolean useNumberOfSubnodes, boolean useEdgeRestingDistance );
	public void runLayout( float width, float height, DNVGraph graph, float coolingFactor, int level, boolean layoutAllLevels,
			boolean useNumberOfSubnodes );

}
