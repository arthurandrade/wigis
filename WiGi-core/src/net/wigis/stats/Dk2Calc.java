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

public class Dk2Calc {
	private Hashtable<Pair<Integer,Integer>, ArrayList<Tuple<Integer, Integer, DNVEdge>>> degreeNodeEdgeIndexTableDK2;
	private Hashtable<Pair<Integer,Integer>, HashSet<Integer>> degreeNodeIndexTableDK2;
	private Hashtable<Pair<Integer,Integer>, Integer> degreeOccurTableDK2;
	private List occurOrderedListDK2;
	private List degreeOrderedListDK2;
	private DNVGraph mGraph;
	private int level;
	
	public Dk2Calc(DNVGraph graph){
		mGraph = graph;
		level = 0;
		prepairDK2Table(graph);
		
	}
	
	public Dk2Calc(DNVGraph graph, int level){
		mGraph = graph;
		this.level = level;
		prepairDK2Table(graph);
		
	}
	private void prepairDK2Table(DNVGraph graph){
		Timer timer = new Timer();
		timer.setStart();
		Dk1Calc dk1Calc = new Dk1Calc(graph);
		degreeNodeEdgeIndexTableDK2 = new Hashtable<Pair<Integer,Integer>, ArrayList<Tuple<Integer, Integer, DNVEdge>>>();
		degreeNodeIndexTableDK2 = new Hashtable<Pair<Integer,Integer>, HashSet<Integer>>();
		degreeOccurTableDK2 = new Hashtable<Pair<Integer,Integer>, Integer>();		
		
		HashSet<Integer> traveledNodesId = new HashSet<Integer>();
		//starting from the nodes with highest degree, construct DK2 tables
		//for each degree pair, the first degree is no less than the second one
		for(DNVEdge edge : graph.getEdges(level)){
			
			int fromNodeId = edge.getFromId();
			int toNodeId = edge.getToId();
			int fromNodeDegree = edge.getFrom().getConnectivity();
			int toNodeDegree = edge.getTo().getConnectivity();
			int node1Degree, node1Id, node2Degree, node2Id;

			if(fromNodeDegree < toNodeDegree){
				node1Degree = toNodeDegree;
				node1Id = toNodeId;
				node2Degree = fromNodeDegree;
				node2Id = fromNodeId;
			}else{
				node2Degree = toNodeDegree;
				node2Id = toNodeId;
				node1Degree = fromNodeDegree;
				node1Id = fromNodeId;
			}
			Pair<Integer, Integer> degreePair = new Pair<Integer, Integer>(node1Degree, node2Degree);
			if(!degreeOccurTableDK2.containsKey(degreePair)){
				degreeOccurTableDK2.put(degreePair, 0);
				degreeNodeEdgeIndexTableDK2.put(degreePair, new ArrayList<Tuple<Integer, Integer, DNVEdge>>());
				degreeNodeIndexTableDK2.put(degreePair, new HashSet<Integer>());
			}
			degreeOccurTableDK2.put(degreePair, degreeOccurTableDK2.get(degreePair) + 1);
			degreeNodeEdgeIndexTableDK2.get(degreePair).add(new Tuple<Integer,Integer,DNVEdge>(node1Id,node2Id,edge));
			degreeNodeIndexTableDK2.get(degreePair).add(node1Id);
			degreeNodeIndexTableDK2.get(degreePair).add(node2Id);
		}
		/*for(Object key : dk1Calc.getDegreeOrderedListDK1()){
			int degree = (Integer)key;
			//go through the nodes with the same degree
			List<DNVNode> nodeWithSameDegree = dk1Calc.getDegreeNodeTableDK1().get(degree);
			for(DNVNode node : nodeWithSameDegree){
				traveledNodesId.add(node.getId());
				
				//get all neighbors which haven't been traveled (from and to)
				ArrayList<DNVNode> neighbors = new ArrayList<DNVNode>();
				for(DNVEdge edge : node.getFromEdges()){
					if(!traveledNodesId.contains(edge.getToId())){
						neighbors.add(graph.getNode(0, edge.getToId()));
					}
				}
				for(DNVEdge edge : node.getToEdges()){
					if(!traveledNodesId.contains(edge.getFromId())){
						neighbors.add(graph.getNode(0, edge.getFromId()));
					}
				}
				
				for(DNVNode neighbor : neighbors){	
		
					int neighborDegree = neighbor.getConnectivity();					
					DNVEdge edge = node.getEdgeToNeighbor(neighbor.getId());
					
					Pair<Integer, Integer> degreePair = new Pair<Integer, Integer>(degree, neighborDegree);
					if(degreeNodeEdgeIndexTableDK2.containsKey(degreePair)){
						degreeNodeEdgeIndexTableDK2.get(degreePair).add(new Tuple<Integer,Integer,DNVEdge>(node.getId(),neighbor.getId(),edge));
						degreeNodeIndexTableDK2.get(degreePair).add(node.getId());
						degreeNodeIndexTableDK2.get(degreePair).add(neighbor.getId());		
						degreeOccurTableDK2.put(degreePair, degreeOccurTableDK2.get(degreePair) + 1);
					}else{
						degreeNodeEdgeIndexTableDK2.put(degreePair, new ArrayList<Tuple<Integer, Integer, DNVEdge>>());
						degreeNodeEdgeIndexTableDK2.get(degreePair).add(new Tuple<Integer,Integer,DNVEdge>(node.getId(),neighbor.getId(),edge));
						degreeNodeIndexTableDK2.put(degreePair, new HashSet<Integer>());
						degreeNodeIndexTableDK2.get(degreePair).add(node.getId());
						degreeNodeIndexTableDK2.get(degreePair).add(neighbor.getId());	
						degreeOccurTableDK2.put(degreePair, 1);
					}
				}
			}
		}*/
		occurOrderedListDK2 = hashTableSort.sortByValue(degreeOccurTableDK2);
		degreeOrderedListDK2 = hashTableSort.sortByKey(degreeNodeIndexTableDK2);
		timer.setEnd();
		System.out.println("	computing dk2 took " + timer.getLastSegment(Timer.SECONDS) + " seconds");
	}
	public Hashtable<Pair<Integer,Integer>, ArrayList<Tuple<Integer, Integer, DNVEdge>>> getDegreeNodeEdgeIndexTableDK2(){
		return degreeNodeEdgeIndexTableDK2;
	}
	public Hashtable<Pair<Integer,Integer>, HashSet<Integer>> getDegreeNodeIndexTableDK2(){
		return degreeNodeIndexTableDK2;
	}
	public Hashtable<Pair<Integer,Integer>, Integer> getDegreeOccurTableDK2(){
		return degreeOccurTableDK2;
	}
	public List getOccurOrderedListDK2(){
		return occurOrderedListDK2;
	}
	public List getDegreeOrderedListDK2(){
		return degreeOrderedListDK2;
	}
	public String toStringDk2Linked(){
		String res = "<table>";
    	String text = "";
    	String formId = "highlightNodesForDk2Form";
    	String hiddenButtonId = "BHighlightNodesForDk2";
    	int index = 0;
    	for(Object key: degreeOrderedListDK2){
    	//for(int i=0;i<list.size();i++){ degreeNodeEdgeIndexTableDK2.get(degreePair).get()
    		Pair<Integer,Integer> degreePair = (Pair<Integer,Integer>)key;
    		text = "&lt;("+ degreePair.getFirst() +","+ degreePair.getSecond() +"),"+ degreeOccurTableDK2.get(degreePair)+"&gt;";
    		res += "<tr style='font-size:11px;'><td>";
    		
    		res += createLinkDk2(text,formId,hiddenButtonId,degreeNodeEdgeIndexTableDK2.get(degreePair), degreeNodeIndexTableDK2.get(degreePair));
    		
    		res += "</td></tr>";
    		
    		index++;
    		if(index >= 10){
    			break;
    		}
    	}
    	res += "</table>";
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
	public String createLinkDk2(String text, String formId, String hiddenButtonId, ArrayList<Tuple<Integer,Integer,DNVEdge>> nodeNedge, HashSet<Integer> uniqueNodes)
	{
	
		PaintBean pb = PaintBean.getCurrentInstance();
		StringBuilder sb = new StringBuilder();
		sb.append( "<a href='#' onclick=\"" ).append("A4J.AJAX.Submit('_viewRoot','" + formId + "',event,{'similarityGroupingId':'" + formId + ":" + hiddenButtonId + "','parameters': {");
		
		int index = 0;
		for(Integer nodeId : uniqueNodes){
			sb.append("'node").append(index).append("':'").append(nodeId).append("',");
			index++;
		}
		index = 0;
		for(Tuple<Integer,Integer,DNVEdge> tuple : nodeNedge){
			sb.append("'edge").append(index).append("':'").append(tuple.getRight().getId()).append("',");
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
/**
	 * Method used to return the nodes passed as parameters for a link
	 * @return arraylist of these nodes' id
	 */
	public ArrayList<Integer> getNodesDk2()
	{
		
		Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		ArrayList<Integer> results = new ArrayList<Integer>();
		int numberOfParams = params.size()-5;
		
		int temp = numberOfParams/3;
		numberOfParams = numberOfParams - temp;
		
		
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
	public ArrayList<Integer> getEdgesDk2()
	{
		
		Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		ArrayList<Integer> results = new ArrayList<Integer>();
		int numberOfParams = params.size()-5;
		numberOfParams = numberOfParams/3;
		
		
		String paramString = "";
		
		for(int x=0;x<numberOfParams;x++){
			paramString = "edge"+x;
			if(params.get(paramString) != null){
				results.add(Integer.parseInt(params.get(paramString)));
			}
		}
		return results;
	}
	
	public void saveDk2Results(DNVGraph graph){
		//store the results as a graph property
		Hashtable<Integer, HashSet<DNVNode>> tableNodes = new Hashtable<Integer, HashSet<DNVNode>>();
		Hashtable<Integer, HashSet<DNVEdge>> tableEdges = new Hashtable<Integer, HashSet<DNVEdge>>();
		
		
		for(Object key : occurOrderedListDK2){
			Pair<Integer, Integer> degreePair = (Pair<Integer, Integer>)key;
			int sum = degreePair.getFirst() + degreePair.getSecond();
			if(!tableNodes.containsKey(sum)){
				tableNodes.put(sum, new HashSet<DNVNode>());
				tableEdges.put(sum, new HashSet<DNVEdge>());
			}
			//get the nodes list for the current sum, add the new ones and put it back into the hashtable
			for(Integer nodeId : degreeNodeIndexTableDK2.get(degreePair)){
				tableNodes.get(sum).add(graph.getNode(level, nodeId));
			}
			
			//same but for the edges
			for(Tuple<Integer, Integer, DNVEdge> tuple : degreeNodeEdgeIndexTableDK2.get(degreePair)){
				tableEdges.get(sum).add(tuple.getRight());
			}
		}
		
		
		graph.setAttribute("Dk2ResultsNodes", tableNodes);
		graph.setAttribute("Dk2ResultsEdges", tableEdges);
		
		
		/*String res = "";
		
		//Enumeration em = tableNodes.keys();
		for(Integer key : tableNodes.keySet())
        //while(em.hasMoreElements())
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
    		
    	graph.setProperty("dk2Layout", res);*/
	}
	public void Dk2Analysis(){
		mGraph.clearNodesByDKTime();
		PaintBean.getCurrentInstance().refreshDKTime();
		
		int step = 1;
		String maxTime = degreeOrderedListDK2.size() + 1 + "";
		for(Object key : degreeOrderedListDK2){
			HashSet<DNVNode> nodes = new HashSet<DNVNode>();
			HashSet<DNVEdge> edges = new HashSet<DNVEdge>();

			ArrayList<Tuple<Integer, Integer, DNVEdge>> nodeEdgeIndex = degreeNodeEdgeIndexTableDK2.get(key);
			for(Tuple<Integer, Integer, DNVEdge> tuple : nodeEdgeIndex){
				DNVNode node1 = mGraph.getNode(level, tuple.getLeft());
				nodes.add(node1);
				DNVNode node2 = mGraph.getNode(level, tuple.getMiddle());
				nodes.add(node2);
				DNVEdge edge = tuple.getRight();
				edges.add(edge);				
			}

			for(DNVNode n : nodes){
				if(n.getProperty("dktime") == null){
					n.setProperty("dktime", step+"");
					n.setProperty("minDKTime", "0");
					n.setProperty("maxDKTime", maxTime);
					n.updateEntityDKTimeInGraph();
				}
			}
			for(DNVEdge e: edges){
				if(e.getProperty("dktime") == null){
					e.setProperty("dktime", step+"");
					e.setProperty("minDKTime", "0");
					e.setProperty("maxDKTime", maxTime);
					e.updateEntityDKTimeInGraph();
				}
			}				
			
			step++;
		}
		for(DNVNode n: mGraph.getNodes(level)){
			if(n.getConnectivity() == 0){
				n.setProperty("dktime", step+"");
				n.setProperty("minDKTime", "0");
				n.setProperty("maxDKTime", maxTime);
				n.updateEntityDKTimeInGraph();
			}
		}
		PaintBean.getCurrentInstance().refreshDKTime();
	}

	
}
