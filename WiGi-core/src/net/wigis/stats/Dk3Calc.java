package net.wigis.stats;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import net.wigis.graph.PaintBean;
import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.utilities.Pair;
import net.wigis.graph.dnv.utilities.Timer;
import net.wigis.graph.dnv.utilities.Tuple;
import net.wigis.graph.dnv.utilities.hashTableSort;

public class Dk3Calc {
	private Hashtable<Tuple<Integer,Integer,Integer>, ArrayList<Pair<DNVEdge, DNVEdge>>> degreeEdgeTableDK3;
	private Hashtable<Tuple<Integer,Integer,Integer>, HashSet<DNVEdge>> degreeUniqueEdgeTableDK3;
	private Hashtable<Tuple<Integer,Integer,Integer>, HashSet<DNVNode>> degreeUniqueNodeTableDK3;
	
	private Hashtable<Tuple<Integer,Integer,Integer>, Integer> degreeOccurTableDK3;
	private List occurOrderedListDK3;
	private List degreeOrderedListDK3;
	private DNVGraph mGraph;
	private int level;
	public Dk3Calc(DNVGraph graph){
		mGraph = graph;
		level = 0;
		prepairDK3Table(graph);
	}
	
	public Dk3Calc(DNVGraph graph, int level){
		mGraph = graph;
		this.level = level;
		prepairDK3Table(graph);
	}
	
	private void prepairDK3Table(DNVGraph graph){
		Timer timer = new Timer();
		timer.setStart();
		
		Dk2Calc dk2Calc = new Dk2Calc(graph, level);
		degreeEdgeTableDK3 = new Hashtable<Tuple<Integer,Integer,Integer>, ArrayList<Pair<DNVEdge, DNVEdge>>>();
		degreeUniqueEdgeTableDK3 = new Hashtable<Tuple<Integer,Integer,Integer>, HashSet<DNVEdge>>();
		degreeUniqueNodeTableDK3 = new Hashtable<Tuple<Integer,Integer,Integer>, HashSet<DNVNode>>();
		degreeOccurTableDK3 = new Hashtable<Tuple<Integer,Integer,Integer>, Integer>();		
		
		int cnt = 0;
		HashSet<Integer> traveledNodesId = new HashSet<Integer>();
		for(Object key : dk2Calc.getDegreeOrderedListDK2()){
			Pair<Integer, Integer> degreePair = (Pair<Integer, Integer>)key;
			Integer highDegree = degreePair.getFirst();
			Integer lowDegree = degreePair.getSecond();
			ArrayList<Tuple<Integer, Integer, DNVEdge>> dk2EdgeTuple = dk2Calc.getDegreeNodeEdgeIndexTableDK2().get(degreePair);
			for(Tuple<Integer, Integer, DNVEdge> tuple : dk2EdgeTuple){
				Integer highDegreeNodeId = tuple.getLeft();
				Integer lowDegreeNodeId = tuple.getMiddle();
				//first add all the tuple nodes with the higher degree node in the center, no redundancy
				if(!traveledNodesId.contains(highDegreeNodeId)){
					traveledNodesId.add(highDegreeNodeId);
					DNVNode middlenode = graph.getNode(level, highDegreeNodeId);
					//List<DNVNode> neighbors = middlenode.getNeighbors();
					
					//get all the neighbors of the higher degree node which haven't been traveled
					ArrayList<DNVNode> neighbors = new ArrayList<DNVNode>();
					for(DNVEdge edge : middlenode.getFromEdges()){
						if(!traveledNodesId.contains(edge.getToId())){
							neighbors.add(graph.getNode(level, edge.getToId()));
						}
					}
					for(DNVEdge edge : middlenode.getToEdges()){
						if(!traveledNodesId.contains(edge.getFromId())){
							neighbors.add(graph.getNode(level, edge.getFromId()));
						}
					}
					
					for(int i = 0; i < neighbors.size(); i++){
						for(int j = i + 1; j < neighbors.size(); j++){
							Integer degree1 = neighbors.get(i).getConnectivity();
							Integer degree2 = neighbors.get(j).getConnectivity();							
							Integer leftNodeId, rightNodeId;
							Tuple<Integer, Integer, Integer> degreeTuple; 
							//for each node tuple, the left node degree should be no smaller than the right one
							if(degree1 >= degree2){
								leftNodeId = neighbors.get(i).getId();
								rightNodeId = neighbors.get(j).getId();
								degreeTuple = new Tuple<Integer, Integer, Integer>(degree1, highDegree,degree2);
							}else{
								leftNodeId = neighbors.get(j).getId();
								rightNodeId = neighbors.get(i).getId();
								degreeTuple = new Tuple<Integer, Integer, Integer>(degree2, highDegree,degree1);
							}
							
							if(!degreeEdgeTableDK3.containsKey(degreeTuple)){
								degreeEdgeTableDK3.put(degreeTuple, new ArrayList<Pair<DNVEdge, DNVEdge>>());
								degreeOccurTableDK3.put(degreeTuple, 0);	
								degreeUniqueEdgeTableDK3.put(degreeTuple, new HashSet<DNVEdge>());
								degreeUniqueNodeTableDK3.put(degreeTuple, new HashSet<DNVNode>());
							}
							degreeEdgeTableDK3.get(degreeTuple).add(new Pair<DNVEdge,DNVEdge>(middlenode.getEdgeToNeighbor(leftNodeId),middlenode.getEdgeToNeighbor(rightNodeId)));
							degreeOccurTableDK3.put(degreeTuple, degreeOccurTableDK3.get(degreeTuple) + 1);
							
							degreeUniqueEdgeTableDK3.get(degreeTuple).add(middlenode.getEdgeToNeighbor(leftNodeId));
							degreeUniqueEdgeTableDK3.get(degreeTuple).add(middlenode.getEdgeToNeighbor(rightNodeId));
							degreeUniqueNodeTableDK3.get(degreeTuple).add(middlenode);
							degreeUniqueNodeTableDK3.get(degreeTuple).add(neighbors.get(i));
							degreeUniqueNodeTableDK3.get(degreeTuple).add(neighbors.get(j));
							cnt++;
						}
					}
				}
				
				if(traveledNodesId.contains(lowDegreeNodeId)){
					continue;
				}
				//next get all the tuple nodes with the lower degree node in the center and the higher degree node at one end
				DNVNode middlenode = graph.getNode(level, lowDegreeNodeId);
				
				//get all the neighbors of the lower degree node which haven't been traveled
				ArrayList<DNVNode> neighbors = new ArrayList<DNVNode>();
				for(DNVEdge edge : middlenode.getFromEdges()){
					if(!traveledNodesId.contains(edge.getToId())){
						neighbors.add(graph.getNode(level, edge.getToId()));
					}
				}
				for(DNVEdge edge : middlenode.getToEdges()){
					if(!traveledNodesId.contains(edge.getFromId())){
						neighbors.add(graph.getNode(level, edge.getFromId()));
					}
				}
				for(DNVNode neighbor : neighbors){
					Integer neighborId = neighbor.getId();
					Integer neighborDegree = neighbor.getConnectivity();
					//if(neighborDegree <= highDegree){
					Tuple<Integer, Integer, Integer> degreeTuple = new Tuple<Integer, Integer, Integer>(highDegree, lowDegree, neighborDegree);
					if(!degreeEdgeTableDK3.containsKey(degreeTuple)){
						degreeEdgeTableDK3.put(degreeTuple, new ArrayList<Pair<DNVEdge, DNVEdge>>());
						degreeOccurTableDK3.put(degreeTuple, 0);	
						degreeUniqueEdgeTableDK3.put(degreeTuple, new HashSet<DNVEdge>());
						degreeUniqueNodeTableDK3.put(degreeTuple, new HashSet<DNVNode>());
					}
					degreeEdgeTableDK3.get(degreeTuple).add(new Pair<DNVEdge,DNVEdge>(tuple.getRight(),middlenode.getEdgeToNeighbor(neighborId)));
					degreeOccurTableDK3.put(degreeTuple, degreeOccurTableDK3.get(degreeTuple) + 1);

					degreeUniqueEdgeTableDK3.get(degreeTuple).add(tuple.getRight());
					degreeUniqueEdgeTableDK3.get(degreeTuple).add(middlenode.getEdgeToNeighbor(neighborId));
					degreeUniqueNodeTableDK3.get(degreeTuple).add(middlenode);
					degreeUniqueNodeTableDK3.get(degreeTuple).add(graph.getNode(level, highDegreeNodeId));
					degreeUniqueNodeTableDK3.get(degreeTuple).add(neighbor);
					cnt++;
				}
			}
		}
		occurOrderedListDK3 = hashTableSort.sortByValue(degreeOccurTableDK3);
		degreeOrderedListDK3 = hashTableSort.sortByKey(degreeOccurTableDK3);
		
		timer.setEnd();
		System.out.println("	computing dk3 took " + timer.getLastSegment(Timer.SECONDS) + " seconds number of tuples " + cnt);
	}
	
	public Hashtable<Tuple<Integer,Integer,Integer>, ArrayList<Pair<DNVEdge, DNVEdge>>> getDegreeEdgeTableDK3(){
		return degreeEdgeTableDK3;
	}
	public Hashtable<Tuple<Integer,Integer,Integer>, HashSet<DNVEdge>> getDegreeUniqueEdgeTableDK3(){
		return degreeUniqueEdgeTableDK3;
	}
	public Hashtable<Tuple<Integer,Integer,Integer>, HashSet<DNVNode>> getDegreeUniqueNodeTableDK3(){
		return degreeUniqueNodeTableDK3;
	}
	
	public Hashtable<Tuple<Integer,Integer,Integer>, Integer> getDegreeOccurTableDK3(){
		return degreeOccurTableDK3;
	}
	public List getOccurOrderedListDK3(){
		return occurOrderedListDK3;
	}
	public List getDegreeOrderedListDK3(){
		return degreeOrderedListDK3;
	}
	
	/**
	 * create an hmtl array with for each dk result, a link
	 * @param list, list of the dk3 results
	 * @return String, array 
	 */
	public String toStringDk3Linked(){
		String res = "<table>";
    	String text = "";
    	String formId = "highlightNodesForDk3Form";
    	String hiddenButtonId = "BHighlightNodesForDk3";
    	System.out.println("	starting to generate string  " + occurOrderedListDK3.size());
    	int index = 0;
    	for(Object key: degreeOrderedListDK3){
    		Tuple<Integer, Integer, Integer> degreeTuple = (Tuple<Integer, Integer, Integer>)key;
    		text = "&lt;("+degreeTuple.getLeft() + "," + degreeTuple.getMiddle() + "," + degreeTuple.getRight() +")," + degreeOccurTableDK3.get(degreeTuple) + "&gt;";
    		res += "<tr style='font-size:11px;'><td>";
    		
    		//System.out.println("calling  createLinkDk3 " + index++);
    		res += createLinkDk3(text,formId,hiddenButtonId, degreeUniqueNodeTableDK3.get(degreeTuple), degreeUniqueEdgeTableDK3.get(degreeTuple));
    		
    		res += "</td></tr>";
    		
    		index++;
    		if(index >= 10){
    			break;
    		}
    	}
    	res += "</table>";
    	
    	System.out.println("	string finished");
    	return res;
	}
	
	/**
	 * Method called to create a link using a4j.ajax.submit 
	 * @param text, link text
	 * @param formId, form id for the .xhtml page
	 * @param hiddenButtonId, button id for the .xhtml page
	 * @param params, arrayList of the dk1 results
	 * @return String, link
	 */
	public static String createLinkDk3(String text, String formId, String hiddenButtonId, HashSet<DNVNode> nodes, HashSet<DNVEdge> edges)
	{
	
		PaintBean pb = PaintBean.getCurrentInstance();
		StringBuilder sb = new StringBuilder();
		sb.append( "<a href='#' onclick=\"" ).append("A4J.AJAX.Submit('_viewRoot','" + formId + "',event,{'similarityGroupingId':'" + formId + ":" + hiddenButtonId + "','parameters': {");
		int index = 0;
		for(DNVNode node : nodes){
			sb.append("'node").append(index).append("':'").append(node.getId()).append("',");
			index++;
		}
		index = 0;
		for(DNVEdge edge : edges){
			sb.append("'edge").append(index).append("':'").append(edge.getId()).append("',");
			index++;
		}
		sb.append("'"+formId + ":" + hiddenButtonId + "':'" + formId + ":" + hiddenButtonId).
		append("'},'actionUrl':'" + pb.getContextPath() + "/wigi/WiGiViewerPanel.faces'} );").
		append("\">" + text + "</a>");
	
		return sb.toString();
	}

	
	/**
	 * Method used to return the nodes passed as parameters for a link
	 * @return arraylist of these nodes' id
	 */
	public ArrayList<Integer> getNodesDk3()
	{
		
		Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		ArrayList<Integer> results = new ArrayList<Integer>();
		int numberOfParams = params.size()-5;
		
		int temp = numberOfParams/5;
		
		numberOfParams = temp*3;
		
		
		String paramString = "";
		
		for(int x=0;x<numberOfParams;x++){
			paramString = "node"+x;
			if(params.get(paramString) != null){
				results.add(Integer.parseInt(params.get(paramString)));
			}
		}
		return results;
	}
	/**
	 * Method used to return the edges passed as parameters for a link
	 * @return arraylist of these edges' id
	 */
	public ArrayList<Integer> getEdgesDk3()
	{
		
		Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		ArrayList<Integer> results = new ArrayList<Integer>();
		int numberOfParams = params.size()-5;
		numberOfParams = numberOfParams/5;
		numberOfParams = numberOfParams*2;
		
		
		String paramString = "";
		
		for(int x=0;x<numberOfParams;x++){
			paramString = "edge"+x;
			if(params.get(paramString) != null){
				results.add(Integer.parseInt(params.get(paramString)));
			}
		}
		return results;
	}
	
	public Hashtable<Integer, HashSet<DNVNode>> getTableNodes(){
		Hashtable<Integer, HashSet<DNVNode>> tableNodes = new Hashtable<Integer, HashSet<DNVNode>>();
		for(Object key : occurOrderedListDK3){
			Tuple<Integer, Integer, Integer> degreeTuple = (Tuple<Integer, Integer, Integer>)key;
			int sum = degreeTuple.getLeft() + degreeTuple.getMiddle() + degreeTuple.getRight();
			if(!tableNodes.containsKey(sum)){
				tableNodes.put(sum, new HashSet<DNVNode>());
			}
			
			for(DNVNode node : degreeUniqueNodeTableDK3.get(degreeTuple)){
				tableNodes.get(sum).add(node);
			}

		}	
		return tableNodes;
	}
	public Hashtable<Integer, HashSet<DNVEdge>> getTableEdges(){
		Hashtable<Integer, HashSet<DNVEdge>> tableEdges = new Hashtable<Integer, HashSet<DNVEdge>>();
		for(Object key : occurOrderedListDK3){
			Tuple<Integer, Integer, Integer> degreeTuple = (Tuple<Integer, Integer, Integer>)key;
			int sum = degreeTuple.getLeft() + degreeTuple.getMiddle() + degreeTuple.getRight();
			if(!tableEdges.containsKey(sum)){
				tableEdges.put(sum, new HashSet<DNVEdge>());
			}
			
			for(DNVEdge edge : degreeUniqueEdgeTableDK3.get(degreeTuple)){
				tableEdges.get(sum).add(edge);
			}

		}	
		return tableEdges;
	}
	public void saveDk3Results(DNVGraph graph){
		//store the results as a graph property
		Hashtable<Integer, HashSet<DNVNode>> tableNodes = new Hashtable<Integer, HashSet<DNVNode>>();
		Hashtable<Integer, HashSet<DNVEdge>> tableEdges = new Hashtable<Integer, HashSet<DNVEdge>>();
		
		
		for(Object key : occurOrderedListDK3){
			Tuple<Integer, Integer, Integer> degreeTuple = (Tuple<Integer, Integer, Integer>)key;
			int sum = degreeTuple.getLeft() + degreeTuple.getMiddle() + degreeTuple.getRight();
			if(!tableNodes.containsKey(sum)){
				tableNodes.put(sum, new HashSet<DNVNode>());
				tableEdges.put(sum, new HashSet<DNVEdge>());
			}
			
			for(DNVNode node : degreeUniqueNodeTableDK3.get(degreeTuple)){
				tableNodes.get(sum).add(node);
			}
			
			for(DNVEdge edge : degreeUniqueEdgeTableDK3.get(degreeTuple)){
				tableEdges.get(sum).add(edge);
			}
		}		
		
		graph.setAttribute("Dk3ResultsNodes", tableNodes);
		graph.setAttribute("Dk3ResultsEdges", tableEdges);
		
				
		String res = "";
		
		for(Integer key : tableNodes.keySet())
        {
			res += "[Degree:"+key+"{Nodes:";
    		for(DNVNode node : tableNodes.get(key)){
    			res += node.getId() + ",";
    		}
    		res = res.substring(0, res.length() - 1) + "}{Edges:";
    		for(DNVEdge edge : tableEdges.get(key)){
    			res += edge.getId() + ",";
    		}
    		res = res.substring(0, res.length() - 1) + "}]";
    	}
    		
    	graph.setProperty("dk3Layout", res);
	}
	public void Dk3Analysis(){
		mGraph.clearNodesByDKTime();
		PaintBean.getCurrentInstance().refreshDKTime();
		List<DNVNode> nodes = mGraph.getNodes(level);
		List<DNVEdge> edges = mGraph.getEdges(level);
		String maxTime = degreeUniqueNodeTableDK3.size() + 1 + "";
		int step = 1;
		for(Object key : degreeOrderedListDK3){

			for(DNVNode n: degreeUniqueNodeTableDK3.get(key)){
				if(n.getProperty("dktime") == null){
					n.setProperty("dktime", step+"");
					n.setProperty("minDKTime", "0");
					n.setProperty("maxDKTime", maxTime);
					n.updateEntityDKTimeInGraph();
					nodes.remove(n);
				}
			}
			for(DNVEdge e: degreeUniqueEdgeTableDK3.get(key)){
				if(e.getProperty("dktime") == null){
					e.setProperty("dktime", step+"");
					e.setProperty("minDKTime", "0");
					e.setProperty("maxDKTime", maxTime);
					e.updateEntityDKTimeInGraph();
					edges.remove(e);
				}
			}						
			step++;
			
		}
		for(DNVNode n: nodes){
			n.setProperty("dktime", step+"");
			n.setProperty("minDKTime", "0");
			n.setProperty("maxDKTime", maxTime);
			n.updateEntityDKTimeInGraph();
		}
		for(DNVEdge e: edges){
			e.setProperty("dktime", step+"");
			e.setProperty("minDKTime", "0");
			e.setProperty("maxDKTime", maxTime);
			e.updateEntityDKTimeInGraph();
		}
		PaintBean.getCurrentInstance().refreshDKTime();
		
	}
}
