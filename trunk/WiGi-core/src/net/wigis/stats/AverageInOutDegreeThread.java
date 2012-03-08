package net.wigis.stats;

import net.wigis.graph.PaintBean;

public class AverageInOutDegreeThread extends Thread{

	
	PaintBean pb = null;
	StatsBean sb = null;
	public AverageInOutDegreeThread(PaintBean pb, StatsBean sb){
		this.pb = pb;
		this.sb = sb;
	}
	
	@Override
	public void run(){

		String temp = pb.getGraph().getProperty("inDegree");
		String temp2 = pb.getGraph().getProperty("outDegree");

		if (temp == null || temp2 == null) {
			String res = GraphStatistics.computeInOutDegree(PaintBean.getCurrentInstance().getGraph());
			sb.averageInOutDegree = res;

			pb.getGraph().setProperty("inDegree", sb.averageInOutDegree);
		} else {
			sb.averageInOutDegree = temp;
		}
	
	sb.AIODEnded = false;
	}
}
