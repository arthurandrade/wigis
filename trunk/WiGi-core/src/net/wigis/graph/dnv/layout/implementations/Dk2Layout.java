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
import net.wigis.stats.Dk2Calc;
import net.wigis.yun.Pair;
import net.wigis.yun.Tuple;
import net.wigis.yun.hashTableSort;

public class Dk2Layout implements SimpleLayoutInterface{
	/** The Constant FRUCHTERMAN_REINGOLD_LAYOUT. */
	public static final String LABEL = "DK2-Layout";
	
	
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
		
		Dk2Calc dk2Calc = new Dk2Calc(graph);
		Hashtable<Pair<Integer, Integer>, ArrayList<Tuple<Integer, Integer, DNVEdge>>> degreeNodeEdgeIndexTableDK2 = dk2Calc.getDegreeNodeEdgeIndexTableDK2();
		List degreeOrderedListDK2 = dk2Calc.getDegreeOrderedListDK2();
		
		/*dk2Calc.saveDk2Results(graph);
		Hashtable<Integer, HashSet<DNVNode>> tableNodes = (Hashtable<Integer, HashSet<DNVNode>>) graph.getAttribute("Dk2ResultsNodes");
		Hashtable<Integer, HashSet<DNVEdge>> tableEdges = (Hashtable<Integer, HashSet<DNVEdge>>) graph.getAttribute("Dk2ResultsEdges");
		List degreeOrderedListDK2 = hashTableSort.sortByKey(tableNodes);*/
		
		System.out.println("	prepair finished");
				
		
		//HashSet<DNVNode> traveledNodes = new HashSet<DNVNode>();
		//HashSet<DNVEdge> traveledEdges = new HashSet<DNVEdge>();
				

		Timer timer = new Timer( Timer.MILLISECONDS );
		timer.setStart();
		

		while(!degreeOrderedListDK2.isEmpty()){
			
			
			HashSet<DNVNode> impNodes = new HashSet<DNVNode>();
			HashSet<DNVNode> nodes = new HashSet<DNVNode>();
			HashSet<DNVEdge> edges = new HashSet<DNVEdge>();
			
			//for(int i = 0; i < iterateDecrease; i++){
			while(!degreeOrderedListDK2.isEmpty() && impNodes.size() <= 10 && edges.size() <= 10){
				Object key = degreeOrderedListDK2.get(0);
				degreeOrderedListDK2.remove(0);
				
				ArrayList<Tuple<Integer, Integer, DNVEdge>> nodeEdgeIndex = degreeNodeEdgeIndexTableDK2.get(key);
				for(Tuple<Integer, Integer, DNVEdge> tuple : nodeEdgeIndex){
					DNVNode node1 = graph.getNode(0, tuple.getLeft());
					impNodes.add(node1);
					DNVNode node2 = graph.getNode(0, tuple.getMiddle());
					nodes.add(node1);
					nodes.add(node2);					
					//nodes.add(node2);
					DNVEdge edge = tuple.getRight();
					edges.add(edge);		
					//traveledEdges.add(edge);		
				}
				
				/*for(DNVNode node : tableNodes.get(key)){
					traveledNodes.add(node);
					nodes.add(node);
									
				}
				for(DNVEdge edge : tableEdges.get(key)){
					
					edges.add(edge);		
					traveledEdges.add(edge);
					
				}*/
			}
			/*if(iterateDecrease > 2){
				iterateDecrease--;
			}*/

			
			FR.runLayout(WIDTH,HEIGHT,nodes,edges,COOLING_FACTOR,false);
			for(DNVNode n : impNodes){
				//n.setVisible(true);	
				n.setProperty("pinned", "true");
			}
			for(DNVEdge e: edges){
				//e.setVisible(true);
				e.setProperty("pinned", "true");
			}

		}
		
		timer.setEnd();
		System.out.println( "Dk2 Layout took " + timer.getLastSegment( Timer.SECONDS ) + " seconds." );
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
