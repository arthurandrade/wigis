package net.wigis.stats;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;

import java.util.HashMap;

import net.wigis.graph.PaintBean;
import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.utilities.Vector3D;
import net.wigis.svetlin.__Color;
import net.wigis.svetlin.__jsf;

public class StatsBean {

	// ======================================
	// VARIABLES
	// ======================================
	String chartDegreeDistributionsImgSrc = "";
	int chartW = 185;
	int chartH = 100;
	String graphSize = "";
	String nodeTypes = "";
	String connectedComponents = "";
	int MAX_CONNECTED_COMPONENTS_SHOWN = 10;
	private final String DEFAULT_STATS_MESSAGE = "not available";
	private boolean statsExpanded = true;

	int numGraphNodes;
	private int previousId0 = -1;
	private int previousId1 = -1;

	PaintBean pb;
	DNVGraph graph;

	/**
	 * Yun's variables
	 */
	Dk1Calc dk1Calc;
	Dk2Calc dk2Calc;
	Dk3Calc dk3Calc;
	List<SelectItem> graphPropertyList = new ArrayList<SelectItem>();
	List<SelectItem> filterPropertyList = new ArrayList<SelectItem>();
	List<SelectItem> filterPropertyValueList = new ArrayList<SelectItem>();
	private String buildingEdgeProperty = null;
	private String filterProperty = null;
	private String filterPropertyValue = null;

	// end Yun's variables
	public List<SelectItem> getGraphPropertyList() {
		synchronized (graph) {
			String properties = graph.getProperty("propertyList");
			String[] propertiesArr = properties.split("\t");
			graphPropertyList.clear();
			SelectItem propertyItem;
			for (int i = 0; i < propertiesArr.length; i++) {
				propertyItem = new SelectItem(propertiesArr[i],
						propertiesArr[i]);
				graphPropertyList.add(propertyItem);
			}
			return graphPropertyList;
		}
	}

	public List<SelectItem> getFilterPropertyList() {
		synchronized (graph) {
			String properties = graph.getProperty("propertyList");
			String[] propertiesArr = properties.split("\t");
			filterPropertyList.clear();
			SelectItem propertyItem;
			for (int i = 0; i < propertiesArr.length; i++) {
				if (graph.getProperty(propertiesArr[i]) != null) {
					propertyItem = new SelectItem(propertiesArr[i],
							propertiesArr[i]);
					filterPropertyList.add(propertyItem);
				}
			}
			return filterPropertyList;
		}
	}

	public List<SelectItem> getFilterPropertyValueList() {
		synchronized (graph) {
			if (filterProperty == null) {
				String properties = graph.getProperty("propertyList");
				String[] propertiesArr = properties.split("\t");
				filterProperty = propertiesArr[0];
			}
			String values = graph.getProperty(filterProperty);
			String[] valuesArr = values.split("\t");
			filterPropertyValueList.clear();
			SelectItem valueItem;
			for (int i = 0; i < valuesArr.length; i++) {
				// System.out.println("\t" + valuesArr[i]);
				if (valuesArr[i] != null) {
					valueItem = new SelectItem(valuesArr[i], valuesArr[i]);
					filterPropertyValueList.add(valueItem);
				}
			}
			return filterPropertyValueList;
		}
	}

	public String getBuildingEdgeProperty() {
		return buildingEdgeProperty;
	}

	public void setBuildingEdgeProperty(String buildingEdgeProperty) {
		this.buildingEdgeProperty = buildingEdgeProperty;
	}

	public String getFilterProperty() {
		return filterProperty;
	}

	public void setFilterProperty(String filterProperty) {
		this.filterProperty = filterProperty;
	}

	public String getFilterPropertyValue() {
		return filterPropertyValue;
	}

	public void setFilterPropertyValue(String filterPropertyValue) {
		this.filterPropertyValue = filterPropertyValue;
	}

	public void buildEdgesForSameProperty() {
		graph.buildEdgesForSameProperty(buildingEdgeProperty);
	}

	public void deleteEdgesForSameProperty() {
		graph.deleteEdgesForSameProperty(buildingEdgeProperty);
	}

	public void addFilter() {
		graph.showEdgesWithProperty(filterProperty, filterPropertyValue);
	}

	public void removeFilter() {
		graph.hideEdgesWithProperty(filterProperty, filterPropertyValue);
	}

	private String filterStr = "Current Filters:\n";

	public String getFilterStr() {
		HashMap<String, String> filterHash = graph.getFilters();
		filterStr = "Current Filters:\n";
		for (String key : filterHash.keySet()) {
			filterStr += ">> " + key + " (" + filterHash.get(key) + ")\n";
		}
		return filterStr;
	}

	/*
	 * public void setFilterStr(){ HashMap<String, String> filterHash =
	 * graph.getFilters(); filterStr = "Current Filters:\t"; for(String key :
	 * filterHash.keySet()){ filterStr += ">> " + key + " (" +
	 * filterHash.get(key) + ")\t"; } }
	 */

	public static String STYLE_STATS_FONT = "statsFont";

	/** Average path length => for the whole graph */
	String averagePathLength = DEFAULT_STATS_MESSAGE;

	/** Average degree distribution => for the whole graph */
	String averageDegreeDistribution = DEFAULT_STATS_MESSAGE;

	/** Average path length => for the sub graph */
	String subGraphAveragePathLength = DEFAULT_STATS_MESSAGE;

	/** Average degree distribution => for the sub graph */
	String subGraphAverageDegreeDistribution = DEFAULT_STATS_MESSAGE;

	/** ShortestPath => pair graph */
	String shortestPath = "";

	/** ShortestPath => pair graph */
	String shortestPathPath = "";

	/** ShortestPath sentence */
	String shortestPathSentence = "";

	/** Average degree Centrality => graph */
	String averageDegreeCentrality = "";

	/** Average degree Centrality => subgraph */
	String subGraphAverageDegreeCentrality = "";

	/** Average in-degree => graph */
	String averageInDegree = DEFAULT_STATS_MESSAGE;

	/** Average out-degree => graph */
	String averageOutDegree = DEFAULT_STATS_MESSAGE;

	/** true if the graph is directed => graph */
	boolean isDirected = false;

	/** True if the node panel has to be rendered */
	boolean nodePanelRendered;

	/** True if the pair panel has to be rendered */
	boolean pairPanelRendered;

	/** True if the subGraph panel has to be rendered */
	boolean subGraphPanelRendered;

	/** Node types - tested with the graph :jod-pubs */
	int MAX_NODE_TYPES_SHOWN = 7;

	/** Node types - tested with the graph :jod-pubs */
	int MAX_NODE_TYPES_FOR_VERTICAL_DISPLAY = 3;

	/** Onload variable */
	String onload = "";

	/** True if the de-highlight button is rendered */
	boolean deHighLightPanelRendered = false;

	/** Sentence for the in-degree => node */
	String inDegreeSentence = "In-degree";

	/** Sentence for the out-degree => node */
	String outDegreeSentence = "Out-degree";

	/** True is the dk panel has to be expanded */
	Boolean dkExpanded = false;

	/** degree distribution for dk1 */
	String dk1 = "";

	/** degree distribution for dk2 */
	String dk2 = "";

	/** degree distribution for dk3 */
	String dk3 = "";

	// ======================================
	// GETTERS & SETTERS
	// ======================================

	/**
	 * Getter of the nodePanelRendered variable
	 */
	public boolean isNodePanelRendered() {
		getGraph();

		return graph.getSelectedNodes(0).size() > 0;
	}

	/**
	 * Setter of the nodePanelRendered variable
	 */
	public void setNodePanelRendered(boolean nodePanelRendered) {
		this.nodePanelRendered = nodePanelRendered;
	}

	/**
	 * Getter of the pairPanelRendered variable
	 * 
	 * @return
	 */
	public boolean isPairPanelRendered() {
		getGraph();
		if (graph.getSelectedNodes(0).size() == 2) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Setter of the pairPanelRendered
	 * 
	 * @param pairPanelRendered
	 */
	public void setPairPanelRendered(boolean pairPanelRendered) {
		this.pairPanelRendered = pairPanelRendered;
	}

	/**
	 * Getter of the subGraphPanelRendered
	 * 
	 * @return
	 */
	public boolean isSubGraphPanelRendered() {
		getGraph();
		if (graph.getSelectedNodes(0).size() >= 2) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Setter of the subGraphPanelRendered
	 * 
	 * @param subGraphPanelRendered
	 */
	public void setSubGraphPanelRendered(boolean subGraphPanelRendered) {
		this.subGraphPanelRendered = subGraphPanelRendered;
	}

	/**
	 * Getter of the connectedComponents variable
	 * 
	 * @return
	 */
	public String getConnectedComponents() {
		getGraph();

		String s = Integer.toString(graph.getNumConnectedComponents());

		if (graph.getNumConnectedComponents() > 1) {
			s += " (sizes: ";

			for (ArrayList<DNVNode> a : graph.getConnectedComponents()) {
				int size = a.size();

				if (graph.getConnectedComponents().indexOf(a) >= MAX_CONNECTED_COMPONENTS_SHOWN) {
					s += " and "
							+ (graph.getConnectedComponents().size() - graph
									.getConnectedComponents().indexOf(a))
							+ " more";

					break;
				}

				s += __jsf.getHtml_forHyperlink_toCall_JavaBean(size + "",
						"selectComponentForm", "BSelectComponent", graph
								.getConnectedComponents().indexOf(a) + "");

				if (graph.getConnectedComponents().indexOf(a) != graph.numConnectedComponents - 1)
					s += ", ";
			}

			s += ")";
		}

		return s;
	}

	/**
	 * Setter of the connectedComponents variable
	 * 
	 * @param connectedComponents
	 */
	public void setConnectedComponents(String connectedComponents) {
		this.connectedComponents = connectedComponents;
	}

	/**
	 * Getter of the NodeTypes variable
	 * 
	 * @return
	 */
	public String getNodeTypes() {
		ArrayList<Vector3D> colors = new ArrayList<Vector3D>();
		ArrayList<Integer> frequencies = new ArrayList<Integer>();

		for (DNVNode n : graph.getVisibleNodes(0).values()) {
			int colorContainsIndex = colors_contains_color(colors, n.getColor());

			if (colorContainsIndex == -1) {
				colors.add(n.getColor());
				frequencies.add(1);
			} else
				frequencies.set(colorContainsIndex,
						frequencies.get(colorContainsIndex) + 1);
		}

		nodeTypes = "<table border='0' cellspacing='0'>"
				+ "	<tr style='font-size:10px;'>";

		for (Vector3D v : colors) {
			if (colors.indexOf(v) >= MAX_NODE_TYPES_SHOWN) {
				nodeTypes += "<span style='font-size:10px;'> + "
						+ (colors.size() - colors.indexOf(v)) + " more </span>";

				break;
			}

			nodeTypes += "		<td align='center'>" + "			"
					+ frequencies.get(colors.indexOf(v));

			if (frequencies.size() <= MAX_NODE_TYPES_FOR_VERTICAL_DISPLAY)
				nodeTypes += "		</td>" + "		<td align='center'>";

			nodeTypes += "			<div style='background-color:"
					+ __Color.getColorHtmlFromVector3D(v)
					+ "; width:7px; height:7px; border:1px solid #aaaaaa' ></div>"
					+ "		</td>";

			if (frequencies.size() <= MAX_NODE_TYPES_FOR_VERTICAL_DISPLAY)
				if (colors.indexOf(v) != colors.size() - 1)
					nodeTypes += "		<td>" + "			, " + "		</td>";
		}
		nodeTypes += "	</tr>" + "</table>";

		return nodeTypes;
	}

	/**
	 * Setter got the nodeTypes variable
	 * 
	 * @param nodeTypes
	 */
	public void setNodeTypes(String nodeTypes) {
		this.nodeTypes = nodeTypes;
	}

	/**
	 * Getter for the graphSize variable
	 * */
	public String getGraphSize() {
		getGraph();

		graphSize = graph.getVisibleNodes(0).size() + " nodes, "
				+ graph.getVisibleEdges(0).size() + " edges";

		return graphSize;
	}

	/**
	 * Setter for the graphSize variable
	 * 
	 * @param graphSize
	 */
	public void setGraphSize(String graphSize) {
		this.graphSize = graphSize;
	}

	/**
	 * Getter returning the chartURL
	 * 
	 * @return String url of the degree distribution chart
	 */
	public String getChartURL() {
		return "/wigi/LineChartServlet?version=" + Math.random();
	}

	/**
	 * Getter returning the chartURL
	 * 
	 * @return String url of the degree distribution chart
	 */
	public String getSubGraphChartURL() {
		return "/wigi/SubGraphStatisticsServlet?version=" + Math.random();
	}

	/**
	 * Getter for the averageDegreeCentrality variable
	 * */
	public String getAverageDegreeCentrality() {

		String temp = graph.getProperty("averageDegreeCentrality");
		if (temp == null) {
			String res = GraphStatistics.computeAverageDegreeCentrality(graph);

			averageDegreeCentrality = res;
			graph.setProperty("averageDegreeCentrality", res);
		} else {
			averageDegreeCentrality = temp;
		}

		return averageDegreeCentrality;
	}

	/**
	 * Setter for the averageDegreeCentrality variable
	 * 
	 * @param ADC
	 */
	public void setAverageDegreeCentrality(String ADC) {
		this.averageDegreeCentrality = ADC;
	}

	/**
	 * Get the chart width
	 * 
	 * @return
	 */
	public int getChartW() {
		return chartW;
	}

	/**
	 * Set the chart width
	 * 
	 * @param chartW
	 */
	public void setChartW(int chartW) {
		this.chartW = chartW;
	}

	/**
	 * Get the chart height
	 * 
	 * @return
	 */
	public int getChartH() {
		return chartH;
	}

	/**
	 * Set the chart height
	 * 
	 * @param chartH
	 */
	public void setChartH(int chartH) {
		this.chartH = chartH;
	}

	/**
	 * Set expandStats true
	 */
	public void expandStats() {
		statsExpanded = true;
	}

	/**
	 * Set collaspeStats false
	 */
	public void collapseStats() {
		statsExpanded = false;
	}

	/**
	 * Getter of the statsExpanded variable
	 * 
	 * @return
	 */
	public boolean isStatsExpanded() {
		return statsExpanded;
	}

	public void setStatsExpanded(boolean expanded) {
		statsExpanded = expanded;
	}

	public boolean getStatsExpanded() {
		return statsExpanded;
	}

	/**
	 * Getter of the isDirected variable
	 * 
	 * @return
	 */
	public boolean getIsDirected() {

		String temp = graph.getProperty("isDirected");

		if (temp == null) {
			Boolean directed = false;
			ArrayList<DNVEdge> list = new ArrayList<DNVEdge>(graph
					.getVisibleEdges(0).values());
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).isDirectional()) {
					directed = true;
				}
			}
			graph.setProperty("isDirected", directed.toString());
			isDirected = directed;
		}

		else {
			isDirected = Boolean.parseBoolean(temp);
		}

		return isDirected;
	}

	/**
	 * Getter of the averageInDegree variable
	 * 
	 * @return
	 */
	public String getAverageInDegree() {

		String temp = graph.getProperty("inDegree");
		String temp2 = graph.getProperty("outDegree");

		if (temp == null || temp2 == null) {
			String res = GraphStatistics.computeInOutDegree(graph);

			int index = res.indexOf("?");
			averageInDegree = res.substring(0, index);
			averageOutDegree = res.substring(index + 1, res.length());

			graph.setProperty("inDegree", averageInDegree);
			graph.setProperty("outDegree", averageOutDegree);
		} else {
			averageInDegree = temp;
			averageOutDegree = temp2;
		}

		return averageInDegree;
	}

	/**
	 * Getter of the averageOutDegree variable
	 * 
	 * @return
	 */
	public String getAverageOutDegree() {

		String temp = graph.getProperty("inDegree");
		String temp2 = graph.getProperty("outDegree");

		if (temp == null || temp2 == null) {
			String res = GraphStatistics.computeInOutDegree(graph);

			int index = res.indexOf("?");
			averageInDegree = res.substring(0, index);
			averageOutDegree = res.substring(index + 1, res.length());

			graph.setProperty("inDegree", averageInDegree);
			graph.setProperty("outDegree", averageOutDegree);
		} else {
			averageInDegree = temp;
			averageOutDegree = temp2;
		}

		return averageOutDegree;
	}

	/**
	 * Getter of the subGraphAverageDegreeCentrality variable
	 * 
	 * @return
	 */
	public String getSubGraphAverageDegreeCentrality() {
		subGraphAverageDegreeCentrality = SubGraphStatistics
				.computeAverageDegreeCentrality(graph);
		return subGraphAverageDegreeCentrality;
	}

	/**
	 * Getter of the paintBean
	 * 
	 * @return
	 */
	private static PaintBean getPaintBean() {
		PaintBean pb = PaintBean.getCurrentInstance();

		if (pb == null) {
			p("pb is null, creating new instance...");
			pb = new PaintBean();
			HttpSession session = (HttpSession) FacesContext
					.getCurrentInstance().getExternalContext().getSession(true);
			session.setAttribute("paintBean", pb);
		}

		return pb;
	}

	/**
	 * Getter of the AveragePathLength variables
	 * 
	 * @return
	 */
	public String getAveragePathLength() {

		String temp = "";

		// APL & ADD init
		temp = graph.getProperty("averagePathLength");
		if ((temp == null) || (temp.compareToIgnoreCase("Refresh please") == 0)) {

			String res = GraphStatistics.computeAveragePathLength(graph);
			int index = res.indexOf("?");

			String APL = res.substring(0, index);
			String ADD = res.substring(index + 1, res.length());

			averagePathLength = APL;
			averageDegreeDistribution = ADD;

			graph.setProperty("averagePathLength", APL);
			graph.setProperty("averageDegreeDistribution", ADD);
		} else {
			averagePathLength = temp;
			averageDegreeDistribution = graph
					.getProperty("averageDegreeDistribution");
		}

		return averagePathLength;
	}

	/**
	 * Getter of the averageDegreeDistribution variable
	 * 
	 * @return
	 */
	public String getAverageDegreeDistribution() {

		String temp = "";

		// APL & ADD init
		temp = graph.getProperty("averagePathLength");
		if ((temp == null) || (temp.compareToIgnoreCase("Refresh please") == 0)) {

			String res = GraphStatistics.computeAveragePathLength(graph);
			int index = res.indexOf("?");

			String APL = res.substring(0, index);
			String ADD = res.substring(index + 1, res.length());

			averagePathLength = APL;
			averageDegreeDistribution = ADD;

			graph.setProperty("averagePathLength", APL);
			graph.setProperty("averageDegreeDistribution", ADD);
		} else {
			averagePathLength = temp;
			averageDegreeDistribution = graph
					.getProperty("averageDegreeDistribution");
		}

		return averageDegreeDistribution;
	}

	/**
	 * Getter of the SubGraphAveragePathLength variable
	 * 
	 * @return
	 */
	public String getSubGraphAveragePathLength() {

		Map<Integer, DNVNode> nodesMap = graph.getSelectedNodes(0);
		Collection<DNVNode> c = nodesMap.values();
		List<DNVNode> nodesList = new ArrayList<DNVNode>(c);

		String res = SubGraphStatistics.computeAveragePathLength(nodesList);

		int index = res.indexOf("?");

		String SGAPL = res.substring(0, index);
		String SGADD = res.substring(index + 1, res.length());

		subGraphAveragePathLength = SGAPL;
		averageDegreeDistribution = SGADD;

		return subGraphAveragePathLength;
	}

	/**
	 * Getter of the subGraphAverageDegreeDistrbution variable
	 * 
	 * @return
	 */
	public String getSubGraphAverageDegreeDistribution() {
		return subGraphAverageDegreeDistribution;
	}

	/**
	 * Getter of the shortest path variable
	 */
	public String getShortestPath() {
		return shortestPath;
	}

	/**
	 * Getter of the shortest path sentence
	 */
	public String getShortestPathSentence() {
		return shortestPathSentence;
	}

	/**
	 * Getter of the dehighlightPanelRendered
	 */
	public boolean isDeHighLightPanelRendered() {
		return deHighLightPanelRendered;
	}

	/**
	 * Getter of the inDegreeSentence
	 */
	public String getInDegreeSentence() {
		return inDegreeSentence;
	}

	/**
	 * Getter of the outDegreeSentence
	 */
	public String getOutDegreeSentence() {
		return outDegreeSentence;
	}

	/**
	 * Getter of the graph variable
	 * 
	 * @return
	 */
	public DNVGraph getGraph() {
		if (pb == null)
			pb = getPaintBean();

		graph = pb.getGraph();

		if (numGraphNodes != graph.getVisibleNodes(0).size())
			invalidateData();

		numGraphNodes = graph.getVisibleNodes(0).size(); // to track changes in
															// graph

		return graph;
	}

	/**
	 * Setter of the Graph variable
	 * 
	 * @param graph
	 */
	public void setGraph(DNVGraph graph) {
		this.graph = graph;
	}

	/**
	 * Getter of the onLoad variable
	 * 
	 * @return
	 */
	public String getOnload() {
		return onload;
	}

	/**
	 * Setter of the onLoad variable
	 * 
	 * @param onload
	 */
	public void setOnload(String onload) {
		this.onload = onload;
	}

	/*
	 * checks if DK button should be shown only shown if there is no DK values
	 * in the DNVGraph object
	 */
	public boolean isRenderDK1Button() {

		String temp1 = graph.getProperty("dk1");
		if (temp1 == null) {
			dk1 = null;
			return true;
		} else {
			// Hashtable<Integer, ArrayList<Integer>> table = new
			// Hashtable<Integer, ArrayList<Integer>>(graph.getDk1Layout());
			// editDk1Layout(table);
			dk1 = temp1;
			return false;
		}

	}

	public boolean isRenderDK2Button() {
		String temp2 = graph.getProperty("dk2");
		if (temp2 == null) {
			dk2 = null;
			return true;
		} else {
			/*
			 * Hashtable<Integer, ArrayList<Integer>> tableNodes = new
			 * Hashtable<Integer, ArrayList<Integer>>(
			 * graph.getDk2LayoutNodes()); Hashtable<Integer,
			 * ArrayList<Integer>> tableEdges = new Hashtable<Integer,
			 * ArrayList<Integer>>( graph.getDk2LayoutEdges());
			 * editDk2OrDk3Layout(tableNodes, tableEdges, "dk2Results");
			 */
			dk2 = temp2;
			return false;
		}
	}

	public boolean isRenderDK3Button() {
		String temp3 = graph.getProperty("dk3");
		if (temp3 == null) {
			dk3 = null;
			return true;
		} else {
			/*
			 * Hashtable<Integer, ArrayList<Integer>> tableNodes = new
			 * Hashtable<Integer, ArrayList<Integer>>(
			 * graph.getDk3LayoutNodes()); Hashtable<Integer,
			 * ArrayList<Integer>> tableEdges = new Hashtable<Integer,
			 * ArrayList<Integer>>( graph.getDk3LayoutEdges());
			 * editDk2OrDk3Layout(tableNodes, tableEdges, "dk3Results");
			 */
			dk3 = temp3;
			return false;
		}
	}

	/*
	 * computes dk1 string called only if the button is clicked in interface
	 * button is never shown if the DNV file contains the dk series
	 */

	public void computeDk1() {
		// synchronized(graph){
		System.out.println("In compute dk1");
		dk1Calc = new Dk1Calc(graph);
		dk1 = dk1Calc.toStringDk1Linked();
		graph.setProperty("dk1", dk1);
		// }

	}

	public void computeDk2() {
		// synchronized(graph){
		System.out.println("In compute dk2");
		dk2Calc = new Dk2Calc(graph);
		dk2 = dk2Calc.toStringDk2Linked();
		graph.setProperty("dk2", dk2);
		// }
	}

	public void computeDk3() {
		// synchronized(graph){
		System.out.println("In compute dk3");
		dk3Calc = new Dk3Calc(graph);
		dk3 = dk3Calc.toStringDk3Linked();
		graph.setProperty("dk3", dk3);
		// }
		// pb.saveGraph();
	}

	public void dk1Analysis() {
		if (dk1 == null) {
			computeDk1();
		}
		dk1Calc.Dk1Analysis();
	}

	public void dk2Analysis() {
		if (dk2 == null) {
			computeDk2();
		}
		dk2Calc.Dk2Analysis();
	}

	public void dk3Analysis() {
		if (dk3 == null) {
			computeDk3();
		}
		dk3Calc.Dk3Analysis();
	}

	/*
	 * accessor for DK1
	 */

	public String getDk1() {
		return dk1;
	}

	public String getDk2() {
		return dk2;
	}

	public String getDk3() {
		return dk3;
	}

	public boolean isDkExpanded() {
		return dkExpanded;
	}

	public void setDkExpanded(boolean d) {
		dkExpanded = d;
	}

	// ===========================================
	// Init methods
	// ===========================================

	// =========================================
	// onload
	// =========================================
	// needs:
	// in xhml: <h:outputText value="#{linkeddataBean.onload}"
	// style="display:none" />
	// and String onload = ""; with getter and setter
	/**
	 * Onload function
	 */
	public void onload() {
		// get graph
		pb = getPaintBean();
		graph = pb.getGraph();

	}

	// /**
	// * Initialization function
	// */
	// public void initStatistics()
	// {
	// String temp = "";
	//
	// //APL & ADD init
	// temp = graph.getProperty("averagePathLength");
	// if(temp == null){
	//
	// String res = GraphStatistics.computeAveragePathLength(graph);
	// int index = res.indexOf("?");
	//
	// String APL = res.substring(0,index);
	// String ADD = res.substring(index+1,res.length());
	//
	// averagePathLength = APL;
	// averageDegreeDistribution = ADD;
	//
	// graph.setProperty("averagePathLength", APL);
	// graph.setProperty("averageDegreeDistribution", ADD);
	// }else{
	// averagePathLength = temp;
	// averageDegreeDistribution =
	// graph.getProperty("averageDegreeDistribution");
	// }
	//
	// //ADC init
	// temp = graph.getProperty("averageDegreeCentrality");
	// if(temp == null){
	// String res = GraphStatistics.computeAverageDegreeCentrality(graph);
	//
	// averageDegreeCentrality = res;
	// graph.setProperty("averageDegreeCentrality", res);
	// }else{
	// averageDegreeCentrality = temp;
	// }
	//
	//
	// //check if the property isDirected is stored, otherwise it is computed
	// temp = graph.getProperty("isDirected");
	//
	// if(temp == null){
	// Boolean directed = false;
	// ArrayList<DNVEdge> list = new
	// ArrayList<DNVEdge>(graph.getVisibleEdges(0).values());
	// for(int i=0;i<list.size();i++){
	// if(list.get(i).isDirectional()){
	// directed = true;
	// }
	// }
	// graph.setProperty("isDirected",directed.toString());
	// }
	//
	// else{
	// isDirected = Boolean.parseBoolean(temp);
	// }
	//
	//
	// //Check if the in-out-degree are stored, otherwise it is computed
	// temp = graph.getProperty("inDegree");
	// String temp2 = graph.getProperty("outDegree");
	//
	// if(temp == null || temp2 == null){
	// String res = GraphStatistics.computeInOutDegree(graph);
	//
	// int index = res.indexOf("?");
	// averageInDegree = res.substring(0,index);
	// averageOutDegree = res.substring(index+1,res.length());
	//
	// graph.setProperty("inDegree",averageInDegree);
	// graph.setProperty("outDegree",averageOutDegree);
	// }
	// else {
	// averageInDegree = temp;
	// averageOutDegree = temp2;
	// }
	//
	// //statistics regarding a subgraph
	// if(isSubGraphPanelRendered()){
	//
	// }
	// }

	// ===========================================
	// Methods
	// ===========================================

	public static void p(Object o) {
		System.out.println(o);
	}

	public static void pe(Object o) {
		System.err.println(o);
	}

	// --------------------------------
	// select component
	// --------------------------------
	public void selectComponent() {
		String param = __jsf.getParam();

		graph.unsellectAllNodes();

		for (DNVNode n : graph.getConnectedComponents().get(
				Integer.parseInt(param)))
			n.setSelected(true);
	}

	// --------------------------------
	// invalidate data
	// --------------------------------
	public void invalidateData() {
		graph.setNumConnectedComponents(0);
	}

	// --------------------------------
	// connected components
	// --------------------------------
	public void selectNode() {
		int index = Integer.parseInt(__jsf.getParam());

		DNVNode n = graph.getNodes(0).get(index);

		graph.unsellectAllNodes();

		n.setSelected(true);
	}

	private int colors_contains_color(ArrayList<Vector3D> colors,
			Vector3D newColor) {
		for (Vector3D color : colors)
			if (color.getX() == newColor.getX()
					&& color.getY() == newColor.getY()
					&& color.getZ() == newColor.getZ())
				return colors.indexOf(color);

		return -1;
	}

	/**
	 * Create a buffered image of the degree distribution graph
	 * 
	 * @params OutputStream out the output
	 * 
	 */

	public void bufferedImageChart(OutputStream out) {
		getGraph();

		ArrayList<Double> points = new ArrayList<Double>();

		List<DNVNode> nodes = new ArrayList<DNVNode>(graph.getVisibleNodes(0)
				.values());

		Collections.sort(nodes, new Comparator<DNVNode>() {
			@Override
			public int compare(DNVNode n1, DNVNode n2) {
				return n2.getDegree() - n1.getDegree();
			}
		});

		ArrayList<Integer> selectedIndexes = getSelectedIndexes(points, nodes);

		// Call to the line LineChart function to create the dataset, create the
		// graph and save it.

		BufferedImage chart = GraphStatistics.LineChart(chartH, chartW, points,
				selectedIndexes);
		try {
			ImageIO.write(chart, "png", out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void bufferedImageSubGraphChart(OutputStream out) {
		getGraph();

		ArrayList<Double> points = new ArrayList<Double>();

		Map<Integer, DNVNode> nodesMap = graph.getSelectedNodes(0);
		Collection<DNVNode> c = nodesMap.values();
		List<DNVNode> nodes = new ArrayList<DNVNode>(c);

		Collections.sort(nodes, new Comparator<DNVNode>() {
			@Override
			public int compare(DNVNode n1, DNVNode n2) {
				return n2.getDegree() - n1.getDegree();
			}
		});

		for (int i = 0; i < nodes.size(); i++) {
			points.add((double) nodes.get(i).getDegree());
		}

		// Call to the line LineChart function to create the dataset, create the
		// graph and save it.

		BufferedImage Chart = SubGraphStatistics.LineChart(chartH, chartW,
				points);
		try {
			ImageIO.write(Chart, "png", out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private ArrayList<Integer> getSelectedIndexes(ArrayList<Double> points,
			List<DNVNode> nodes) {
		ArrayList<Integer> selectedIndexes = new ArrayList<Integer>();

		// show max 200 nodes
		int iStep = nodes.size() / 200;
		if (iStep == 0)
			iStep = 1;

		for (int i = 0; i < nodes.size(); i++) {
			if (graph.getSelectedNodes(0).containsValue(nodes.get(i)))
				selectedIndexes.add(i / iStep);

			if (i % iStep == 0)
				points.add((double) nodes.get(i).getDegree());
		}
		return selectedIndexes;
	}

	public String getReRenderScript() {
		Map<Integer, DNVNode> nodesMap = graph.getSelectedNodes(0);
		Collection<DNVNode> c = nodesMap.values();
		List<DNVNode> nodesList = new ArrayList<DNVNode>(c);

		if (nodesList.size() == 2) {
			if (nodesList.get(0).getId() != previousId0
					&& nodesList.get(1).getId() != previousId1) {
				previousId0 = nodesList.get(0).getId();
				previousId1 = nodesList.get(1).getId();
				return "<script>getImage_viewDetailedDefault();</script>";
			}
		} else {
			previousId0 = -1;
			previousId1 = -1;
		}

		return "";
	}

	public void dehighlightAndDeSelectNodesAndEdges() {
		shortestPathSentence = "";
		shortestPath = "";
		deHighLightPanelRendered = false;

		// Get a list of all nodes and dehighlight them
		ArrayList<DNVNode> nodeList = new ArrayList<DNVNode>(graph.getNodes(0));

		for (int i = 0; i < nodeList.size(); i++) {
			nodeList.get(i).setHighlighted(false);
			nodeList.get(i).setSelected(false);
		}

		ArrayList<DNVEdge> edgeList = new ArrayList<DNVEdge>(graph.getEdges(0));
		for (int i = 0; i < edgeList.size(); i++) {
			edgeList.get(i).setHighlighted(false);
			edgeList.get(i).setSelected(false);
		}

	}

	/**
	 * Compute the shortest path method
	 * 
	 * @return
	 */
	public String computeShortestPath() {
		Map<Integer, DNVNode> nodesMap = graph.getSelectedNodes(0);
		Collection<DNVNode> c = nodesMap.values();
		List<DNVNode> nodesList = new ArrayList<DNVNode>(c);

		if (nodesList.size() == 2) {
			shortestPath = PairStatistics.computeShortestPath(graph,
					nodesList.get(0), nodesList.get(1));
			shortestPathSentence = "Shortest path distance";
			if (shortestPath
					.compareToIgnoreCase("Please select nodes from the same cluster") != 0) {
				deHighLightPanelRendered = true;
			}
		}

		return shortestPath;
	}

	/**
	 * Highlight nodes from a clicked dk1 results link
	 */
	public void highlightDk1Results() {
		ArrayList<Integer> nodes = dk1Calc.getNodesDk1();
		ArrayList<DNVNode> nodesList = new ArrayList<DNVNode>(graph
				.getVisibleNodes(0).values());

		dehighlightAndDeSelectNodesAndEdges();

		// highlight nodes
		for (int x = 0; x < nodes.size(); x++) {
			DNVNode toFind = (DNVNode) graph.getNodeById(nodes.get(x));
			int index = nodesList.indexOf(toFind);
			DNVNode node = nodesList.get(index);
			node.setSelected(true);
		}
	}

	/**
	 * Highlight nodes from a clicked dk2 results link
	 */
	public void highlightDk2Results() {
		ArrayList<Integer> nodes = dk2Calc.getNodesDk2();
		ArrayList<Integer> edges = dk2Calc.getEdgesDk2();
		ArrayList<DNVNode> nodesList = new ArrayList<DNVNode>(graph
				.getVisibleNodes(0).values());
		ArrayList<DNVEdge> edgesList = new ArrayList<DNVEdge>(graph
				.getVisibleEdges(0).values());

		dehighlightAndDeSelectNodesAndEdges();

		// highlight nodes
		for (int x = 0; x < nodes.size(); x++) {
			DNVNode toFind = (DNVNode) graph.getNodeById(nodes.get(x));
			int index = nodesList.indexOf(toFind);
			DNVNode node = nodesList.get(index);
			node.setSelected(true);
		}

		// highlight edges
		for (int x = 0; x < edges.size(); x++) {
			for (int y = 0; y < edgesList.size(); y++) {
				if (edgesList.get(y).getId().toString()
						.compareToIgnoreCase(edges.get(x).toString()) == 0) {
					edgesList.get(y).setSelected(true);
					break;
				}
			}
		}
	}

	/**
	 * Highlight nodes from a clicked dk3 results link
	 */
	public void highlightDk3Results() {
		ArrayList<Integer> nodes = dk3Calc.getNodesDk3();
		ArrayList<Integer> edges = dk3Calc.getEdgesDk3();
		ArrayList<DNVNode> nodesList = new ArrayList<DNVNode>(graph
				.getVisibleNodes(0).values());
		ArrayList<DNVEdge> edgesList = new ArrayList<DNVEdge>(graph
				.getVisibleEdges(0).values());

		dehighlightAndDeSelectNodesAndEdges();

		// highlight nodes
		for (int x = 0; x < nodes.size(); x++) {
			DNVNode toFind = (DNVNode) graph.getNodeById(nodes.get(x));
			int index = nodesList.indexOf(toFind);
			DNVNode node = nodesList.get(index);
			node.setSelected(true);
		}

		// highlight edges
		for (int x = 0; x < edges.size(); x++) {
			for (int y = 0; y < edgesList.size(); y++) {
				if (edgesList.get(y).getId().toString()
						.compareToIgnoreCase(edges.get(x).toString()) == 0) {
					edgesList.get(y).setSelected(true);
					break;
				}
			}
		}
	}

	/**
	 * Reset all statistics value to null.
	 */
	public void emptyStatsProperties() {
		graph.setProperty("dk1", null);
		graph.setProperty("dk2", null);
		graph.setProperty("dk3", null);
		graph.setProperty("dk1Layout", null);
		graph.setProperty("dk2Layout", null);
		graph.setProperty("dk3Layout", null);
		graph.setProperty("averagePathLength", null);
		graph.setProperty("averageDegreeDistribution", null);
		graph.setProperty("averageDegreeCentrality", null);
		if (graph.getProperty("isDirected").compareToIgnoreCase("") != 0) {
			graph.setProperty("inDegreeSentence", null);
			graph.setProperty("outDegreeSentence", null);
		}
		graph.setProperty("isDirected", null);
	}

	public void editDk1Layout(Hashtable<Integer, ArrayList<Integer>> table) {

		Hashtable<Integer, ArrayList<DNVNode>> res = new Hashtable<Integer, ArrayList<DNVNode>>();

		Enumeration em = table.keys();

		while (em.hasMoreElements()) {
			int key = (Integer) em.nextElement();

			ArrayList<Integer> nodesList = new ArrayList<Integer>(
					table.get(key));
			ArrayList<DNVNode> DNVNodeList = new ArrayList<DNVNode>();

			for (int x = 0; x < nodesList.size(); x++) {
				DNVNodeList.add((DNVNode) graph.getNodeById(nodesList.get(x)));
			}
			res.put(key, DNVNodeList);
		}

		graph.setAttribute("Dk1Results", res);
	}

	public void editDk2OrDk3Layout(
			Hashtable<Integer, ArrayList<Integer>> tableNodes,
			Hashtable<Integer, ArrayList<Integer>> tableEdges, String type) {

		Hashtable<Integer, ArrayList<DNVNode>> resNodes = new Hashtable<Integer, ArrayList<DNVNode>>();
		Hashtable<Integer, ArrayList<DNVEdge>> resEdges = new Hashtable<Integer, ArrayList<DNVEdge>>();

		Enumeration em = tableNodes.keys();

		while (em.hasMoreElements()) {
			int key = (Integer) em.nextElement();

			ArrayList<Integer> nodesList = new ArrayList<Integer>(
					tableNodes.get(key));
			ArrayList<DNVNode> DNVNodeList = new ArrayList<DNVNode>();

			for (int x = 0; x < nodesList.size(); x++) {
				DNVNodeList.add((DNVNode) graph.getNodeById(nodesList.get(x)));
			}

			if (resNodes.containsKey(key)) {
				ArrayList<DNVNode> temp = new ArrayList<DNVNode>(
						resNodes.get(key));
				for (DNVNode n : temp) {
					DNVNodeList.add(n);
				}
				resNodes.put(key, DNVNodeList);
			} else {
				resNodes.put(key, DNVNodeList);
			}
		}

		em = tableEdges.keys();

		while (em.hasMoreElements()) {
			int key = (Integer) em.nextElement();

			ArrayList<Integer> edgesList = new ArrayList<Integer>(
					tableEdges.get(key));
			ArrayList<DNVEdge> DNVEdgeList = new ArrayList<DNVEdge>();
			List<DNVEdge> graphEdgesList = new ArrayList<DNVEdge>(
					graph.getEdges());
			for (int x = 0; x < edgesList.size(); x++) {
				for (DNVEdge e : graphEdgesList) {
					if (e.getId().toString()
							.compareToIgnoreCase(edgesList.get(x).toString()) == 0) {
						DNVEdgeList.add(e);
						break;
					}
				}
			}

			if (resEdges.containsKey(key)) {
				ArrayList<DNVEdge> temp = new ArrayList<DNVEdge>(
						resEdges.get(key));
				for (DNVEdge e : temp) {
					DNVEdgeList.add(e);
				}
				resEdges.put(key, DNVEdgeList);
			} else {
				resEdges.put(key, DNVEdgeList);
			}
		}

		if (type.compareToIgnoreCase("dk2Results") == 0) {
			graph.setAttribute("Dk2ResultsNodes", resNodes);
			graph.setAttribute("Dk2ResultsEdges", resEdges);
		} else if (type.compareToIgnoreCase("dk3Results") == 0) {
			graph.setAttribute("Dk3ResultsNodes", resNodes);
			graph.setAttribute("Dk3ResultsEdges", resEdges);
		}
	}

}
