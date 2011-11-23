package net.wigis.stats;

import net.wigis.graph.PaintBean;

public class AverageDegreeCentralityThread extends Thread{

	PaintBean pb = null;
	StatsBean sb = null;
	public AverageDegreeCentralityThread(PaintBean pb, StatsBean sb){
		this.pb = pb;
		this.sb = sb;
	}
	
	@Override
	public void run(){
		
		String temp = pb.getGraph().getProperty("averageDegreeCentrality");
		
		if (temp == null) {
			String res = GraphStatistics.computeAverageDegreeCentrality(pb.getGraph());
			sb.setAverageDegreeCentrality(res);
			pb.getGraph().setProperty("averageDegreeCentrality", res);
		} else {
			sb.setAverageDegreeCentrality(temp);
		}
		sb.ADCEnded = false;
	}
	
}
