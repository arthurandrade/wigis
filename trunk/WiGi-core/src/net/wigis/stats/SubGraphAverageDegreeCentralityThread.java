package net.wigis.stats;

import net.wigis.graph.PaintBean;

public class SubGraphAverageDegreeCentralityThread extends Thread{

	PaintBean pb = null;
	StatsBean sb = null;
	public SubGraphAverageDegreeCentralityThread(PaintBean pb, StatsBean sb){
		this.pb = pb;
		this.sb = sb;
	}
	
	@Override
	public void run(){
		sb.subGraphAverageDegreeCentrality = SubGraphStatistics
		.computeAverageDegreeCentrality(pb.getGraph());
		
		sb.subGraphAverageDegreeCentralityEnded = false;
	}
	
}
