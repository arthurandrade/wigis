package net.wigis.graph.dnv.layout.implementations;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.FM3JNILib;
import net.wigis.graph.data.utilities.ConvertDNVTOGML;
import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.layout.interfaces.SimpleLayoutInterface;
import net.wigis.settings.Settings;

public class FM3Layout implements SimpleLayoutInterface{
	public static final String LABEL = "FM3 Layout";
	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return LABEL;
	}
	
	@Override
	public void runLayout(DNVGraph graph, int level) {
		// TODO Auto-generated method stub
		System.out.println( "Running " + LABEL);
		
		Map<Integer,Integer> idToIndex = new HashMap<Integer,Integer>();
		List<DNVNode> nodes = graph.getNodes(level);
		for(int index = 0; index < nodes.size(); index++){
			idToIndex.put(nodes.get(index).getId(), index);
		}
		List<DNVEdge> edges = graph.getEdges(level);
		
		int[] edgeIndex = new int[edges.size() * 2];
		int index = 0;
		for(DNVEdge edge : edges){	
			edgeIndex[index * 2] = idToIndex.get(edge.getFrom().getId());
			edgeIndex[index * 2 + 1] = idToIndex.get(edge.getTo().getId());
			index++;
		}
		
		//ConvertDNVTOGML.convert(graph,Settings.GRAPHS_PATH + "testgraph.gml");
		System.out.println("finish mapping edges");
		
		
		try{
			long startTime = System.currentTimeMillis();
			float[] position = FM3JNILib.runFM3(nodes.size(), edgeIndex, edges.size());
			
			if(position != null){
				long endTime = System.currentTimeMillis();
				//System.out.println("get result " + position.length);
				index = 0;
				for(DNVNode node : nodes){
					node.setPosition(position[index * 2], position[index * 2 + 1]);
					index++;
				}
				System.out.println("layout finished in " + (endTime - startTime) + " milliseconds");
				
			}else{
				System.out.println("out of memory");		
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
		
	

}
