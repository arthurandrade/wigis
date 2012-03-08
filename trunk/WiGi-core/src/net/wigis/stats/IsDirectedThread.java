package net.wigis.stats;

import java.util.ArrayList;

import net.wigis.graph.PaintBean;
import net.wigis.graph.dnv.DNVEdge;

public class IsDirectedThread extends Thread{
	
	PaintBean pb = null;
	StatsBean sb = null;
	public IsDirectedThread(PaintBean pb, StatsBean sb){
		this.pb = pb;
		this.sb = sb;
	}
	
	@Override
	public void run(){
		String temp = pb.getGraph().getProperty("isDirected");

		if (temp == null) {
			Boolean directed = false;
			ArrayList<DNVEdge> list = new ArrayList<DNVEdge>(pb.getGraph()
					.getVisibleEdges(0).values());
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).isDirectional()) {
					directed = true;
				}
			}
			pb.getGraph().setProperty("isDirected", directed.toString());
			sb.isDirected = directed;
		}

		else {
			sb.isDirected = Boolean.parseBoolean(temp);
		}
		sb.isDirectedEnded = false;
	}

}
