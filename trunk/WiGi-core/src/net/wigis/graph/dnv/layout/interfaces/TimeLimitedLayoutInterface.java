package net.wigis.graph.dnv.layout.interfaces;

import net.wigis.graph.dnv.DNVGraph;

public interface TimeLimitedLayoutInterface extends LayoutInterface
{
	public void runLayout( DNVGraph graph, int level, double maxSeconds, boolean initializePositions, boolean layoutAllLevels );
}
