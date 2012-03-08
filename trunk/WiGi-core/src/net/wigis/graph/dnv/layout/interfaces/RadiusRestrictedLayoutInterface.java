package net.wigis.graph.dnv.layout.interfaces;

import net.wigis.graph.dnv.DNVGraph;

public interface RadiusRestrictedLayoutInterface extends LayoutInterface
{
	public void runLayout( DNVGraph graph, int level, float maxRadius );
}
