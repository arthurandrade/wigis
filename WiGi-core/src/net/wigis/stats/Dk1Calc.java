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
import net.wigis.graph.dnv.utilities.Timer;
import net.wigis.yun.hashTableSort;

public class Dk1Calc {
	private Hashtable<Integer, ArrayList<DNVNode>> degreeNodeTableDK1;
	private Hashtable<Integer, Integer> degreeOccurTableDK1;
	private List occurOrderedListDK1;	
	private List degreeOrderedListDK1;
	private DNVGraph mGraph;
	private int level;
	
	public Dk1Calc(DNVGraph graph){
		mGraph = graph;
		level = 0;
		prepairDK1Table(graph, level);		
	}
	public Dk1Calc(DNVGraph graph, int level){
		mGraph = graph;
		this.level = level;
		prepairDK1Table(graph, this.level);		
	}
	private void prepairDK1Table(DNVGraph graph, int level){
		Timer timer = new Timer();
		timer.setStart();
		degreeNodeTableDK1 = new Hashtable<Integer, ArrayList<DNVNode>>();
		degreeOccurTableDK1 = new Hashtable<Integer, Integer>();
		
		for(DNVNode node : graph.getNodes(level)){
			int degree = node.getConnectivity();
			
			if(degreeNodeTableDK1.containsKey(degree)){
				degreeNodeTableDK1.get(degree).add(node);
				degreeOccurTableDK1.put(degree, degreeOccurTableDK1.get(degree) + 1);
			}else{
				degreeNodeTableDK1.put(degree, new ArrayList<DNVNode>());
				degreeNodeTableDK1.get(degree).add(node);
				degreeOccurTableDK1.put(degree, 1);
			}
		}
		occurOrderedListDK1 = hashTableSort.sortByValueDesc(degreeOccurTableDK1);
		degreeOrderedListDK1 = hashTableSort.sortByKeyDesc(degreeNodeTableDK1);	
		timer.setEnd();
		System.out.println("	computing dk1 took " + timer.getLastSegment(Timer.SECONDS) + " seconds");
	}
	public Hashtable<Integer, ArrayList<DNVNode>> getDegreeNodeTableDK1(){
		return degreeNodeTableDK1;
	}
	public Hashtable<Integer, Integer> getDegreeOccurTableDK1(){
		return degreeOccurTableDK1;
	}
	public List getOccurOrderedListDK1(){
		return occurOrderedListDK1;
	}
	public List getDegreeOrderedListDK1(){
		return degreeOrderedListDK1;
	}
	/**
	 * Method called to create a link using a4j.ajax.submit 
	 * @param text, link text
	 * @param formId, form id for the .xhtml page
	 * @param hiddenButtonId, button id for the .xhtml page
	 * @param params, arrayList of the dk1 results
	 * @return String, link
	 */
	public String createLinkDk1(String text, String formId, String hiddenButtonId, ArrayList<DNVNode> params)
	{
	
		PaintBean pb = PaintBean.getCurrentInstance();
		StringBuilder sb = new StringBuilder();
		sb.append("<a href='#' onclick=\"").append("A4J.AJAX.Submit('_viewRoot','" + formId + "',event,{'similarityGroupingId':'" + formId + ":" + hiddenButtonId + "','parameters': {");
		for(int x=0;x<params.size();x++){
			sb.append("'node").append(x).append("':'").append(params.get(x).getId()).append("',");
		}
		sb.append("'"+formId + ":" + hiddenButtonId + "':'" + formId + ":" + hiddenButtonId).
			append("'},'actionUrl':'" + pb.getContextPath() + "/wigi/WiGiViewerPanel.faces'} );").
			append("\">" + text + "</a>");
			
		return sb.toString();
	}
	/**
	 * Method used to get the parameters of a dk1 result link 
	 * @return arrayliList<Integer>, list of all nodes id part of a link
	 */
	
	public ArrayList<Integer> getNodesDk1()
	{
		
		Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		ArrayList<Integer> results = new ArrayList<Integer>();
		int numberOfParams = params.size()-5;
		String paramString = "";
		
		for(int x=0;x<numberOfParams;x++){
			paramString = "node"+x;
			results.add(Integer.parseInt(params.get(paramString)));
		}
		return results;
	}
	
	/**
	 * create an hmtl array with for each dk result, a link
	 * @param list, list of the dk1 results
	 * @return String, array 
	 */
	public String toStringDk1Linked(){
		String res = "<table>";
    	String text = "";
    	String formId = "highlightNodesForDk1Form";
    	String hiddenButtonId = "BHighlightNodesForDk1";
    	
    	int index = 0;
    	for(Object key : degreeOrderedListDK1){
    	//for(int i=0;i<list.size();i++){
    		text = "&lt;" + (Integer)key + "," + degreeOccurTableDK1.get(key) + "&gt;";
    		res += "<tr style='font-size:11px;'><td>";
    		
    		res += createLinkDk1(text,formId,hiddenButtonId, degreeNodeTableDK1.get(key));
    		
    		res += "</td></tr>";
    		
    		index++;
    		if(index >= 10){
    			break;
    		}
    	}
    	res += "</table>";
    	
    	return res;
	}
	public void saveDk1Results(DNVGraph graph){
		graph.setAttribute("Dk1Results", degreeNodeTableDK1);
		
		String res = "";
		
		for(Integer key : degreeNodeTableDK1.keySet()){
            //ArrayList<DNVNode> results = new ArrayList<DNVNode>(table.get(key));
    		res += "[Degree:"+key+"{Nodes:";
    		for(DNVNode node : degreeNodeTableDK1.get(key)){
    		//for(int x=0;x<results.size();x++){
    			res += node.getId() + ",";
    		}
    		res = res.substring(0, res.length() - 1);
    		res += "}]";
    	}
    		
    	graph.setProperty("dk1Layout", res);
	}
	public void Dk1Analysis(){
		mGraph.clearNodesByDKTime();
		PaintBean.getCurrentInstance().refreshDKTime();
		
		//System.out.println("running dk1 anlaysis degreeOrdererdListDK1 length " + degreeOrderedListDK1.size());
		
		int step = 1;	
		HashSet<DNVNode> traveledNodes = new HashSet<DNVNode>();
		HashSet<DNVEdge> traveledEdges = new HashSet<DNVEdge>();
		for(Object key : degreeOrderedListDK1){
			HashSet<DNVEdge> edges = new HashSet<DNVEdge>();
			ArrayList<DNVNode> nodes = degreeNodeTableDK1.get(key);
			//HashSet<DNVNode> neighbors = new HashSet<DNVNode>();
			/*for(DNVNode node : nodes){	
				for(DNVEdge edge : node.getFromEdges()){
					if(edge.getTo().getConnectivity() <= node.getConnectivity()){
						neighbors.add(edge.getTo());
						edges.add(edge);
					}
				}
				for(DNVEdge edge : node.getToEdges()){
					if(edge.getFrom().getConnectivity() <= node.getConnectivity()){
						neighbors.add(edge.getFrom());
						edges.add(edge);
					}
				}
			}*/
			traveledNodes.addAll(nodes);
			//nodes.addAll(neighbors);			
			for(DNVNode n : nodes){
				//if(n.getProperty("time") == null){
					n.setProperty("dktime", step+"");
					n.setProperty("minDKTime", "0");
					n.setProperty("maxDKTime", "100000");
					n.updateEntityDKTimeInGraph();
				//}
			}
			for(DNVNode node : traveledNodes){
				for(DNVEdge edge : node.getFromEdges()){
					if(!traveledEdges.contains(edge) && traveledNodes.contains(edge.getTo())){
						traveledEdges.add(edge);
						edges.add(edge);
					}
				}
				for(DNVEdge edge : node.getToEdges()){
					if(!traveledEdges.contains(edge) && traveledNodes.contains(edge.getFrom())){
						traveledEdges.add(edge);
						edges.add(edge);
					}
				}
			}
			for(DNVEdge e: edges){
				//if(e.getProperty("time") == null){
					e.setProperty("dktime", step+"");
					e.setProperty("minDKTime", "0");
					e.setProperty("maxDKTime", "100000");
					e.updateEntityDKTimeInGraph();
				//}
			}
			step++;
		}
				
		PaintBean.getCurrentInstance().refreshDKTime();
	}
}
