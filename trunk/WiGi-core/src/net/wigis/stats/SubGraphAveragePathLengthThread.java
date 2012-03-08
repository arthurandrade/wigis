package net.wigis.stats;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.wigis.graph.PaintBean;
import net.wigis.graph.dnv.DNVNode;

	public class SubGraphAveragePathLengthThread extends Thread{

	PaintBean pb = null;
	StatsBean sb = null;
	public SubGraphAveragePathLengthThread(PaintBean pb, StatsBean sb){
		this.pb = pb;
		this.sb = sb;
	}
	
	@Override
	public void run(){
		
		Map<Integer, DNVNode> nodesMap = pb.getGraph().getSelectedNodes(0);
		Collection<DNVNode> c = nodesMap.values();
		List<DNVNode> nodesList = new ArrayList<DNVNode>(c);

		String res = SubGraphStatistics.computeAveragePathLength(nodesList);
		int index = res.indexOf("?");

		String SGAPL = res.substring(0, index);
		String SGADD = res.substring(index + 1, res.length());

		sb.subGraphAveragePathLength = SGAPL;
		sb.subGraphAverageDegreeDistribution = SGADD;
		
		sb.subGraphAveragePathLengthEnded = false;
	}
	
}
