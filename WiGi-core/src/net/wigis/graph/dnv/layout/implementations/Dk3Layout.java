package net.wigis.graph.dnv.layout.implementations;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import net.wigis.graph.PaintBean;
import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.layout.interfaces.SimpleLayoutInterface;
import net.wigis.graph.dnv.utilities.Timer;
import net.wigis.stats.Dk3Calc;
import net.wigis.yun.hashTableSort;

public class Dk3Layout implements SimpleLayoutInterface {
	/** The Constant FRUCHTERMAN_REINGOLD_LAYOUT. */
	public static final String LABEL = "DK3-Layout";
	
	
	public static final float WIDTH = (float) 100.0;
	public static final float HEIGHT = (float) 100.0;
	public static final float COOLING_FACTOR = (float) 0.1;
	
	private BufferedWriter writer;
	@Override
	public void setOutputWriter(BufferedWriter writer) {
		// TODO Auto-generated method stub
		this.writer = writer;
	}
	
	@Override
	public String getLabel()
	{
		return LABEL;
	}

	@Override
	public void runLayout(DNVGraph graph, int level) {
		// TODO Auto-generated method stub
		System.out.println("running " + LABEL);
		Dk1Layout.randomizePosition(graph);
		graph.clearNodesByDKTime();
		
		FruchtermanReingold FR = new FruchtermanReingold(false);
		
		Dk3Calc dk3Calc = new Dk3Calc(graph);
		
		Hashtable<Integer, HashSet<DNVNode>> tableNodes = dk3Calc.getTableNodes();
		Hashtable<Integer, HashSet<DNVEdge>> tableEdges = dk3Calc.getTableEdges();
		List degreeOrderedListDK3 = hashTableSort.sortByKeyDesc(tableNodes);
		
		Timer timer = new Timer( Timer.MILLISECONDS );
		timer.setStart();
		
		int inc = 1;
		while(!degreeOrderedListDK3.isEmpty()){
			
			
			HashSet<DNVNode> nodes = new HashSet<DNVNode>();
			HashSet<DNVEdge> edges = new HashSet<DNVEdge>();
			
			while(!degreeOrderedListDK3.isEmpty() && nodes.size() <= 10 * inc && edges.size() <= 10 * inc){
				Object key = degreeOrderedListDK3.get(0);
				degreeOrderedListDK3.remove(0);
				
				nodes.addAll(tableNodes.get(key));
				edges.addAll(tableEdges.get(key));
			}

			
			FR.runLayout(WIDTH,HEIGHT,nodes,edges,COOLING_FACTOR,false);
			for(DNVNode n : nodes){
				n.setProperty("pinned", "true");
			}
			for(DNVEdge e: edges){
				e.setProperty("pinned", "true");
			}
			inc++;
		}
		
		timer.setEnd();
		System.out.println( "Dk3 Layout took " + timer.getLastSegment( Timer.SECONDS ) + " seconds." );
		if(writer != null){
			try{
				int n = graph.getNodes(0).size();
				int e = graph.getEdges().size();
				double time = timer.getTimeSinceStart(Timer.SECONDS);;
				writer.write(time + "\t" + time/n + "\t" + time/e + "\t" + time/(n+e) + "\t" + time/(e/n) + "\n");
				//writer.write(timer.getLastSegment( Timer.SECONDS ) + "\n");
				//writer.write(LABEL + " finished in " + timer.getLastSegment( Timer.SECONDS ) + " seconds.\n\n");
			}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		//List<DNVNode> nodesList = new ArrayList<DNVNode>(graph.getVisibleNodes(0).values());
		//List<DNVEdge> edgesList = new ArrayList<DNVEdge>(graph.getVisibleEdges(0).values());
		
		List<DNVNode> nodesList = graph.getNodes(0);
		List<DNVEdge> edgesList = graph.getEdges(0);
		
		for(int x=0;x<nodesList.size();x++){
			nodesList.get(x).setProperty("pinned", "false");
		}
		for(int x=0;x<edgesList.size();x++){
			edgesList.get(x).setProperty("pinner", "false");
		}
	}

}
