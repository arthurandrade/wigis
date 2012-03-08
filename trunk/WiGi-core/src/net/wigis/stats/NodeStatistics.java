package net.wigis.stats;

import java.util.ArrayList;

import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVNode;

public class NodeStatistics {
	
	
	/**
	 * 
	 * Compute the degree centrality of a node
	 * 
	 * @param node
	 * @return
	 */
	public static int computeNodeDegreeCentrality(DNVNode node){	
		int degreeCentrality = node.getNeighbors(true).size();
		return degreeCentrality;
	}
	
	/**
	 * 
	 * Compute the in and out degree for a node
	 * 
	 * @param node
	 * @return
	 */
	static String computeNodeInOutDegree(ArrayList<DNVEdge> edgesList, DNVNode node){
		
		int inDegree = 0;
		int outDegree = 0;
		String res = "";
		
		
		for(int i=0;i<edgesList.size();i++){
			DNVNode to = edgesList.get(i).getTo();
			DNVNode from = edgesList.get(i).getFrom();
			if(to.toString().compareToIgnoreCase(node.toString()) == 0){
				inDegree++;
			}else if(from.toString().compareToIgnoreCase(node.toString()) == 0){
				outDegree++;
			}
		}
		
		res = Integer.toString(inDegree)+"?"+Integer.toString(outDegree);
		return res;
	}

	
}
