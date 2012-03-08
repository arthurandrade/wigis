package net.wigis.stats;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.wigis.graph.PaintBean;
import net.wigis.graph.dnv.DNVNode;

public class ShortestPathThread extends Thread{

	PaintBean pb = null;
	StatsBean sb = null;
	public ShortestPathThread(PaintBean pb, StatsBean sb){
		this.pb = pb;
		this.sb = sb;
	}
	
	@Override
	public void run(){
		
		Map<Integer, DNVNode> nodesMap = pb.getGraph().getSelectedNodes(0);
		Collection<DNVNode> c = nodesMap.values();
		List<DNVNode> nodesList = new ArrayList<DNVNode>(c);

		if (nodesList.size() == 2) {
			sb.shortestPath = PairStatistics.computeShortestPath(pb.getGraph(),
					nodesList.get(0), nodesList.get(1));
			sb.shortestPathSentence = "Shortest path distance";
			if (sb.shortestPath
					.compareToIgnoreCase("Please select nodes from the same cluster") != 0) {
				sb.deHighLightPanelRendered = true;
			}
		}
		
		sb.SPEnded = false;
		
	}
	
}
