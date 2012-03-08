package net.wigis.graph.dnv.layout.implementations;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

import net.wigis.graph.PaintBean;
import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.layout.interfaces.SimpleLayoutInterface;
import net.wigis.graph.dnv.utilities.Timer;
import net.wigis.stats.Dk1Calc;

public class Dk1Layout implements SimpleLayoutInterface {
	/** The Constant FRUCHTERMAN_REINGOLD_LAYOUT. */
	public static final String LABEL = "DK1-Layout";
	
	
	public static float WIDTH = (float) 100.0;
	public static float HEIGHT = (float) 100.0;
	public static final float COOLING_FACTOR = (float) 0.1;
	private BufferedWriter writer;
	@Override
	public void setOutputWriter(BufferedWriter writer) {
		// TODO Auto-generated method stub
		this.writer = writer;
	}
	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return LABEL;
	}
	public ArrayList<Integer> findGaps(List degreeOrderedList){
		ArrayList<Integer> gaps = new ArrayList<Integer>();
		int numberOfElements = degreeOrderedList.size();
		Integer[] elements = new Integer[numberOfElements];
		degreeOrderedList.toArray(elements);
		Integer firstElm = elements[0];
		for(int i = 1; i < numberOfElements; i++){
			
			if(firstElm /elements[i] >= 2){
				gaps.add(i);
				firstElm = elements[i];
			}
		}
		
		return gaps;
	}
	
	@Override
	public void runLayout(DNVGraph graph, int level) {
		// TODO Auto-generated method stub
		System.out.println("running " + LABEL);
		randomizePosition(graph);
		graph.clearNodesByDKTime();
				
		FruchtermanReingold FR = new FruchtermanReingold(false);
		
		Dk1Calc dk1Calc = new Dk1Calc(graph);
		Hashtable<Integer,ArrayList<DNVNode>> degreeNodeTableDK1 = dk1Calc.getDegreeNodeTableDK1();
		List degreeOrderedListDK1 = dk1Calc.getDegreeOrderedListDK1();
		
		WIDTH = (float) 100.0;
		HEIGHT = (float) 100.0;
		
		/*System.out.println("printing degree");
		for(Object degree : degreeOrderedListDK1){
			System.out.println("\t" + degree);
		}
		System.out.println("finish printing degree");
		
		System.out.println("printing gaps");*/
		ArrayList<Integer> gaps = findGaps(degreeOrderedListDK1);
		for(Integer gap : gaps){
			System.out.println("\t" + gap);
		}
		System.out.println("finish printing gaps");
		
		System.out.println("	prepair finished");
		
		Timer timer = new Timer( Timer.MILLISECONDS );
		timer.setStart();
		int index = 0;
		
		HashSet<DNVNode> traveledNodes = new HashSet<DNVNode>();
		HashSet<DNVEdge> traveledEdges = new HashSet<DNVEdge>();
		HashSet<DNVNode> nodes = new HashSet<DNVNode>();
		HashSet<DNVEdge> edges = new HashSet<DNVEdge>();
		while(!gaps.isEmpty()){
			index = gaps.get(0);
			gaps.remove(0);
			for(int i = 0; i < index; i++){
				nodes.addAll(degreeNodeTableDK1.get(degreeOrderedListDK1.get(i)));
				traveledNodes.addAll(degreeNodeTableDK1.get(degreeOrderedListDK1.get(i)));
			}
			
			for(DNVNode node : traveledNodes){
				for(DNVEdge edge : node.getFromEdges()){
					if(!traveledEdges.contains(edge) && traveledNodes.contains(edge.getTo())){
						traveledEdges.add(edge);
						edges.add(edge);
						nodes.add(edge.getTo());
						nodes.add(edge.getFrom());
					}
				}
				for(DNVEdge edge : node.getToEdges()){
					if(!traveledEdges.contains(edge) && traveledNodes.contains(edge.getFrom())){
						traveledEdges.add(edge);
						edges.add(edge);
						nodes.add(edge.getTo());
						nodes.add(edge.getFrom());
					}
				}
			}
			FR.runLayout(WIDTH,HEIGHT, nodes,edges, COOLING_FACTOR,false);
			for(DNVNode n : nodes){			
				n.setProperty("pinned", "true");
			}
			for(DNVEdge e: edges){
				e.setProperty("pinned", "true");
			}
			WIDTH *= 0.9;
			HEIGHT *= 0.9;
			edges = new HashSet<DNVEdge>();
			nodes = new HashSet<DNVNode>();
		}
		for(int i = index; i < degreeOrderedListDK1.size(); i++){
			nodes.addAll(degreeNodeTableDK1.get(degreeOrderedListDK1.get(i)));
			traveledNodes.addAll(degreeNodeTableDK1.get(degreeOrderedListDK1.get(i)));
		}
		for(DNVNode node : traveledNodes){
			for(DNVEdge edge : node.getFromEdges()){
				if(!traveledEdges.contains(edge) && traveledNodes.contains(edge.getTo())){
					traveledEdges.add(edge);
					edges.add(edge);
					nodes.add(edge.getTo());
					nodes.add(edge.getFrom());
				}
			}
			for(DNVEdge edge : node.getToEdges()){
				if(!traveledEdges.contains(edge) && traveledNodes.contains(edge.getFrom())){
					traveledEdges.add(edge);
					edges.add(edge);
					nodes.add(edge.getTo());
					nodes.add(edge.getFrom());
				}
			}
		}
		FR.runLayout(WIDTH,HEIGHT, nodes,edges, COOLING_FACTOR,false);
		for(DNVNode n : nodes){			
			n.setProperty("pinned", "true");
		}
		for(DNVEdge e: edges){
			e.setProperty("pinned", "true");
		}
		
		/*HashSet<DNVNode> traveledNodes = new HashSet<DNVNode>();
		HashSet<DNVEdge> traveledEdges = new HashSet<DNVEdge>();
		HashSet<DNVNode> nodes = new HashSet<DNVNode>();
		HashSet<DNVEdge> edges = new HashSet<DNVEdge>();
		while(!degreeOrderedListDK1.isEmpty()){
			Object key = degreeOrderedListDK1.get(0);
			degreeOrderedListDK1.remove(0);
			nodes.addAll(degreeNodeTableDK1.get(key));
			traveledNodes.addAll(degreeNodeTableDK1.get(key));
			for(DNVNode node : traveledNodes){
				for(DNVEdge edge : node.getFromEdges()){
					if(!traveledEdges.contains(edge) && traveledNodes.contains(edge.getTo())){
						traveledEdges.add(edge);
						edges.add(edge);
						nodes.add(edge.getTo());
						nodes.add(edge.getFrom());
					}
				}
				for(DNVEdge edge : node.getToEdges()){
					if(!traveledEdges.contains(edge) && traveledNodes.contains(edge.getFrom())){
						traveledEdges.add(edge);
						edges.add(edge);
						nodes.add(edge.getTo());
						nodes.add(edge.getFrom());
					}
				}
			}
			if(edges.size() < 10 && !degreeOrderedListDK1.isEmpty()){
				continue;
			}
			FR.runLayout(WIDTH,HEIGHT, nodes,edges, COOLING_FACTOR,false);
			WIDTH *= 0.8;
			HEIGHT *= 0.8;
			for(DNVNode n : nodes){			
				n.setProperty("pinned", "true");
			}
			for(DNVEdge e: edges){
				e.setProperty("pinned", "true");
			}
			edges = new HashSet<DNVEdge>();
			nodes = new HashSet<DNVNode>();
		}*/
		/*while(!degreeOrderedListDK1.isEmpty()){		
			HashSet<DNVNode> tmpNodes = new HashSet<DNVNode>();
			HashSet<DNVEdge> edges = new HashSet<DNVEdge>();
			while(!degreeOrderedListDK1.isEmpty() && tmpNodes.size() <= 10 && edges.size() <= 10){
				Object key = degreeOrderedListDK1.get(0);
				degreeOrderedListDK1.remove(0);
				ArrayList<DNVNode> nodes = degreeNodeTableDK1.get(key);
				for(DNVNode node : nodes){	
					for(DNVEdge edge : node.getFromEdges()){
						if(edge.getTo().getConnectivity() >= node.getConnectivity()){
							tmpNodes.add(edge.getTo());
							edges.add(edge);
						}
					}
					for(DNVEdge edge : node.getToEdges()){
						if(edge.getFrom().getConnectivity() >= node.getConnectivity()){
							tmpNodes.add(edge.getFrom());
							edges.add(edge);
						}
					}
				}
				tmpNodes.addAll(nodes);
			}
			
			traveledNodes.addAll(tmpNodes);
			traveledEdges.addAll(edges);			
			FR.runLayout(WIDTH,HEIGHT, tmpNodes,edges, COOLING_FACTOR,false);
			for(DNVNode n : tmpNodes){			
				n.setProperty("pinned", "true");
			}
			for(DNVEdge e: edges){
				e.setProperty("pinned", "true");
			}
		}*/
		
		timer.setEnd();
		System.out.println( "Dk1 Layout took " + timer.getLastSegment( Timer.SECONDS ) + " seconds." );
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

		List<DNVNode> nodesList = graph.getNodes(0);
		List<DNVEdge> edgesList = graph.getEdges(0);
		
		for(int x=0;x<nodesList.size();x++){
			nodesList.get(x).setProperty("pinned", "false");
		}
		for(int x=0;x<edgesList.size();x++){
			edgesList.get(x).setProperty("pinner", "false");
		}

		
	}
	/**
	 * Hide all visible nodes and edges from the graph
	 * @param graph
	 */
	public static void hideNodesAndEdges(DNVGraph graph)
	{
		//List<DNVNode> nodesList = new ArrayList<DNVNode>(graph.getVisibleNodes(0).values());
		//List<DNVEdge> edgesList = new ArrayList<DNVEdge>(graph.getVisibleEdges(0).values());
		
		List<DNVNode> nodesList = graph.getNodes(0);
		List<DNVEdge> edgesList = graph.getEdges(0);
		
		
		for(int x=0;x<nodesList.size();x++){
			nodesList.get(x).setVisible(false);
		}
		for(int x=0;x<edgesList.size();x++){
			edgesList.get(x).setVisible(false);
		}
	}
	public static void randomizePosition(DNVGraph graph){
		Random generator = new Random();
		for(DNVNode node : graph.getNodes(0)){
			node.setPosition(generator.nextFloat() * 2 - 1, generator.nextFloat() * 2 - 1);
		}
	}
}
