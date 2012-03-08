package net.wigis.graph.dnv.layout.interfaces;

import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;

public interface NodeCentralizedLayoutInterface extends LayoutInterface
{
	public void runLayout( DNVGraph graph, DNVNode centralNode, int level, boolean circle );
}
