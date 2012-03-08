package net.wigis.stats;

import net.wigis.graph.PaintBean;

public class AverageDegreeDistributionThread extends Thread{

	PaintBean pb = null;
	StatsBean sb = null;
	public AverageDegreeDistributionThread(PaintBean pb, StatsBean sb){
		this.pb = pb;
		this.sb = sb;
	}
	
	@Override
	public void run(){
				String temp = "";

		// APL & ADD init
		temp = pb.getGraph().getProperty("averagePathLength");
		if ((temp == null) || (temp.compareToIgnoreCase("Refresh please") == 0)) {

			String res = GraphStatistics.computeAveragePathLength(pb.getGraph());
			int index = res.indexOf("?");

			String APL = res.substring(0, index);
			String ADD = res.substring(index + 1, res.length());

			sb.averagePathLength = APL;
			sb.averageDegreeDistribution = ADD;

			pb.getGraph().setProperty("averagePathLength", APL);
			pb.getGraph().setProperty("averageDegreeDistribution", ADD);
		} else {
			sb.averagePathLength = temp;
			sb.averageDegreeDistribution = pb.getGraph()
					.getProperty("averageDegreeDistribution");
		}
		sb.ADDEnded = false;
	}
	
}
