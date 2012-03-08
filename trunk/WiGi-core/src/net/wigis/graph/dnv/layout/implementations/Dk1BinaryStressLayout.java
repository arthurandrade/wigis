package net.wigis.graph.dnv.layout.implementations;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.layout.interfaces.SimpleLayoutInterface;
import net.wigis.graph.dnv.utilities.Timer;
import net.wigis.stats.Dk1Calc;

public class Dk1BinaryStressLayout implements SimpleLayoutInterface {
	public static final String LABEL = "DK1-BinaryStressLayout";
	private BinaryStressCalc BS;
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

	@Override
	public void runLayout(DNVGraph graph, int level) {
		// TODO Auto-generated method stub
		System.out.println("running " + LABEL);
		
		//graph.removeStoredPosition(0);
		Dk1Layout.hideNodesAndEdges(graph);
		Dk1Layout.randomizePosition(graph);

				
		Dk1Calc dk1Calc = new Dk1Calc(graph);
		Hashtable<Integer,ArrayList<DNVNode>> degreeNodeTablDK1 = dk1Calc.getDegreeNodeTableDK1();
		List degreeOrderedListDK1 = dk1Calc.getDegreeOrderedListDK1();


		System.out.println("	prepair finished");
		
		Timer timer = new Timer( Timer.MILLISECONDS );
		timer.setStart();
		
		HashSet<DNVNode> traveledNodes = new HashSet<DNVNode>();
		HashSet<DNVEdge> traveledEdges = new HashSet<DNVEdge>();
		
		
		int step = 10;
		//while(!degreeOrderedListDK1.isEmpty()){
			ArrayList<DNVNode> nodesForOneIteration = new ArrayList<DNVNode>();
			ArrayList<DNVNode> centralNodes = new ArrayList<DNVNode>();
			for(int i = 0 ;i < 3; i++){
				Object key = degreeOrderedListDK1.get(0);
				degreeOrderedListDK1.remove(0);						
				ArrayList<DNVNode> nodes = degreeNodeTablDK1.get(key);
				for(DNVNode node : nodes){
					if(!traveledNodes.contains(node)){
						node.setVisible(true);
						centralNodes.add(node);
						traveledNodes.add(node);						
						node.setProperty("time", step+"");
						node.setProperty("minTime", "0");
						node.setProperty("maxTime", "100000");
						node.updateEntityTimeInGraph();
					}					
				}
				nodesForOneIteration.addAll(centralNodes);
				for(DNVNode node : centralNodes){
					node.setVisible(true);
					for(DNVEdge edge : node.getFromEdges()){
						DNVNode toNode = edge.getTo();
						if(!traveledEdges.contains(edge) && traveledNodes.contains(toNode)){
							traveledEdges.add(edge);
							edge.setProperty("pinned", "true");
							edge.setProperty("time", step+"");
							edge.setProperty("minTime", "0");
							edge.setProperty("maxTime", "100000");
							edge.updateEntityTimeInGraph();
							edge.setVisible(true);
						}else if(!traveledNodes.contains(toNode)){
							toNode.setVisible(true);
							nodesForOneIteration.add(toNode);
							traveledNodes.add(toNode);
						}
					}
					for(DNVEdge edge : node.getToEdges()){
						DNVNode fromNode = edge.getFrom();
						if(!traveledEdges.contains(edge) && traveledNodes.contains(fromNode)){
							traveledEdges.add(edge);
							edge.setVisible(true);
							edge.setProperty("pinned", "true");
							edge.setProperty("time", step+"");
							edge.setProperty("minTime", "0");
							edge.setProperty("maxTime", "100000");
							edge.updateEntityTimeInGraph();
						}else if(!traveledNodes.contains(fromNode)){
							fromNode.setVisible(true);
							nodesForOneIteration.add(fromNode);
							traveledNodes.add(fromNode);
						}
					}
				}
			}
			//BS = new BinaryStressCalc(nodesForOneIteration);
			//BS.runBSMLayout();
			/*for(DNVNode node : nodesForOneIteration){
				node.setVisible(true);
				for(DNVEdge edge : node.getFromEdges()){
					if(!traveledEdges.contains(edge) && traveledNodes.contains(edge.getTo())){
						//edge.setVisible(true);
						traveledEdges.add(edge);
					}
				}
				for(DNVEdge edge : node.getToEdges()){
					if(!traveledEdges.contains(edge) && traveledNodes.contains(edge.getFrom())){
						//edge.setVisible(true);
						traveledEdges.add(edge);
					}
				}
			}*/
		//}
		
		timer.setEnd();
		System.out.println( "Dk1 Layout took " + timer.getLastSegment( Timer.SECONDS ) + " seconds." );
	}

}
