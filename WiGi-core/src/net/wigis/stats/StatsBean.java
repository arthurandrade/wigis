package net.wigis.stats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;

import net.wigis.graph.PaintBean;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.utilities.Vector3D;
import net.wigis.svetlin.__Color;
import net.wigis.svetlin.__jsf;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


//import com.googlecode.charts4j.Color;


public class StatsBean
{
	public static void p(Object o)
	{
		System.out.println(o);
	}
	
	public static void pe(Object o)
	{
		System.err.println(o);
	}
	
	PaintBean pb;
	DNVGraph graph;
	public static String STYLE_STATS_FONT = "statsFont";
		
	
	
	//======================================
	// node
	//======================================
	boolean nodePanelRendered;
	
	public boolean isNodePanelRendered()
	{
		getGraph();
		
		return graph.getSelectedNodes(0).size() > 0;
	}

	public void setNodePanelRendered(boolean nodePanelRendered)
	{
		this.nodePanelRendered = nodePanelRendered;
	}

	//======================================
	// graph
	//======================================
	String chartDegreeDistributionsImgSrc = "";
	int chartW = 185;
	int chartH = 100;
	String graphSize = ""; 
	String nodeTypes = "";
	String connectedComponents = "";
	int MAX_CONNECTED_COMPONENTS_SHOWN = 10;
	
	
	//--------------------------------
	// invalidate data
	//--------------------------------
	public void invalidateData()
	{
		graph.setNumConnectedComponents(0);
	}
	
	//--------------------------------
	// connected components
	//--------------------------------
	public String getConnectedComponents()
	{
		getGraph();
		
		String s = Integer.toString(graph.getNumConnectedComponents());
		
		if (graph.getNumConnectedComponents() > 1)
		{
			s += " (sizes: ";
			
			for (ArrayList<DNVNode> a : graph.getConnectedComponents())
			{
				int size = a.size();
				
				if (graph.getConnectedComponents().indexOf(a) >= MAX_CONNECTED_COMPONENTS_SHOWN)
				{
					s += " and " + (graph.getConnectedComponents().size() - graph.getConnectedComponents().indexOf(a)) + " more";
					
					break;
				}
				
				s += __jsf.getHtml_forHyperlink_toCall_JavaBean(size + "", "selectComponentForm", "BSelectComponent", graph.getConnectedComponents().indexOf(a) + "");
				
				if (graph.getConnectedComponents().indexOf(a) != graph.numConnectedComponents - 1)
					s += ", "; 
			}
			
			s += ")";
		}
		
		return s;
	}

	public void setConnectedComponents(String connectedComponents)
	{
		this.connectedComponents = connectedComponents;
	}
	
	//--------------------------------
	// select component
	//--------------------------------
	public void selectComponent()
	{
		String param = __jsf.getParam();
		
		graph.unsellectAllNodes();
		
		for (DNVNode n : graph.getConnectedComponents().get(Integer.parseInt(param)))
			n.setSelected(true);
	}

	//--------------------------------
	// node types
	//--------------------------------
	// test with graph: jod-pubs
	int MAX_NODE_TYPES_SHOWN = 7;
	int MAX_NODE_TYPES_FOR_VERTICAL_DISPLAY = 3;
	
	public String getNodeTypes()
	{
		ArrayList<Vector3D> colors = new ArrayList<Vector3D>();
		ArrayList<Integer> frequencies = new ArrayList<Integer>();
		
		for (DNVNode n : graph.getVisibleNodes(0).values())
		{
			int colorContainsIndex = colors_contains_color(colors, n.getColor()); 
			
			if (colorContainsIndex == -1)
			{
				colors.add(n.getColor());
				frequencies.add(1);
			}
			else
				frequencies.set(colorContainsIndex, frequencies.get(colorContainsIndex) + 1);
		}

		nodeTypes = 	
			"<table border='0' cellspacing='0'>" +
			"	<tr style='font-size:10px;'>";
		
		for (Vector3D v : colors)
		{
			if (colors.indexOf(v) >= MAX_NODE_TYPES_SHOWN)
			{
				nodeTypes += "<span style='font-size:10px;'> + " + (colors.size() - colors.indexOf(v)) + " more </span>";
				
				break;
			}
			
			nodeTypes +=
				"		<td align='center'>" +
				"			" + frequencies.get(colors.indexOf(v));
			
			if (frequencies.size() <= MAX_NODE_TYPES_FOR_VERTICAL_DISPLAY)
				nodeTypes +=
					"		</td>" + 
					"		<td align='center'>";
			
			nodeTypes +=
				"			<div style='background-color:" + __Color.getColorHtmlFromVector3D(v) + "; width:7px; height:7px; border:1px solid #aaaaaa' ></div>" +
				"		</td>";
			
			if (frequencies.size() <= MAX_NODE_TYPES_FOR_VERTICAL_DISPLAY)
				if (colors.indexOf(v) != colors.size() - 1)
					nodeTypes +=
						"		<td>" +
						"			, " +
						"		</td>";
		}
		nodeTypes += 	
			"	</tr>" +
			"</table>";
		
		return nodeTypes;
	}
	
	//--------------------------------
	// connected components
	//--------------------------------
	public void selectNode()
	{
		int index = Integer.parseInt(__jsf.getParam());
		
		DNVNode n = graph.getNodes(0).get(index);
		
		graph.unsellectAllNodes();
		
		n.setSelected(true);
	}
	
	private int colors_contains_color(ArrayList<Vector3D> colors, Vector3D newColor)
	{
		for (Vector3D color : colors)
			if (color.getX() == newColor.getX() && color.getY() == newColor.getY() && color.getZ() == newColor.getZ())
				return colors.indexOf(color);
		
		return -1;
	}
	
	public void setNodeTypes(String nodeTypes)
	{
		this.nodeTypes = nodeTypes;
	}

	//--------------------------------
	// graph size
	//--------------------------------
	public String getGraphSize()
	{
		getGraph();
		
		graphSize = graph.getVisibleNodes(0).size() + " nodes, " + graph.getVisibleEdges(0).size() + " edges";
		
		return graphSize;
	}

	public void setGraphSize(String graphSize)
	{
		this.graphSize = graphSize;
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	
	//--------------------------------
	// degree distribution
	//--------------------------------
	public String getChartDegreeDistributionsImgSrc(OutputStream out)
	{
		getGraph();
		
		ArrayList<Double> points = new ArrayList<Double>();

		List<DNVNode> nodes = new ArrayList<DNVNode>(graph.getVisibleNodes(0).values());
		
		Collections.sort(nodes, new Comparator<DNVNode>() 
		{
			@Override public int compare(DNVNode n1, DNVNode n2) 
			{
				return n2.getDegree() - n1.getDegree();
			}
		});
		
		ArrayList<Integer> selectedIndexes = new ArrayList<Integer>();
		
		// show max 200 nodes
		int iStep = nodes.size() / 200;
		if (iStep == 0)
			iStep = 1;
		
		for (int i=0; i<nodes.size(); i++)
		{
			if (graph.getSelectedNodes(0).containsValue(nodes.get(i)))
				selectedIndexes.add(i/iStep);
			
			if (i%iStep == 0)
				points.add((double)nodes.get(i).getDegree());
		}
		
		
		/*System.out.println("SelectedIndexes : "+selectedIndexes.size());
		
		for (int i=0; i<selectedIndexes.size(); i++){
			System.out.println("value :"+selectedIndexes.get(i));
			System.out.println("\n");
		}
		
		
		//Call to the line LineChart function to create the dataset, create the graph and save it.
		
		
		String path = "";
			//path = __GoogleChart.chart(null, chartW, chartH, points, 1, Color.BLUE, null, 10, selectedIndexes, false);
		
		
		
		//p(points.size());
		
		//__Array.sort(points, false);
		
		
		
		return path;
	}

	public void setChartDegreeDistributionsImgSrc( String graphDegreeDistributionsImgSrc )
	{
		this.chartDegreeDistributionsImgSrc = graphDegreeDistributionsImgSrc;
	}
	*/

	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Create a buffered image of the degree distribution graph 
	 * 
	 * @params	OutputStream out 	the output
	 * 
	 */
	
	public void bufferedImageChart(OutputStream out){
		getGraph();
		
		ArrayList<Double> points = new ArrayList<Double>();

		List<DNVNode> nodes = new ArrayList<DNVNode>(graph.getVisibleNodes(0).values());
		
		Collections.sort(nodes, new Comparator<DNVNode>() 
		{
			@Override public int compare(DNVNode n1, DNVNode n2) 
			{
				return n2.getDegree() - n1.getDegree();
			}
		});
		
		ArrayList<Integer> selectedIndexes = getSelectedIndexes( points, nodes );

		
		//Call to the line LineChart function to create the dataset, create the graph and save it.
		
			BufferedImage chart = LineChart(chartH, chartW, points, selectedIndexes);
			try
			{
				ImageIO.write(chart, "png", out);
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
	}

	private ArrayList<Integer> getSelectedIndexes( ArrayList<Double> points, List<DNVNode> nodes )
	{
		ArrayList<Integer> selectedIndexes = new ArrayList<Integer>();
		
		// show max 200 nodes
		int iStep = nodes.size() / 200;
		if (iStep == 0)
			iStep = 1;
		
		for (int i=0; i<nodes.size(); i++)
		{
			if (graph.getSelectedNodes(0).containsValue(nodes.get(i)))
				selectedIndexes.add(i/iStep);
			
			if (i%iStep == 0)
				points.add((double)nodes.get(i).getDegree());
		}
		return selectedIndexes;
	}
	
	private String getSelectedIndexesString()
	{
		List<DNVNode> nodes = new ArrayList<DNVNode>(graph.getVisibleNodes(0).values());
		List<Integer> selectedIndexes = getSelectedIndexes( new ArrayList<Double>(), nodes );
		StringBuilder sb = new StringBuilder();
		for( Integer i : selectedIndexes )
		{
			sb.append( i ).append( "," );
		}
		
		return sb.toString();
	}
	
	/**
	 * Getter returning the chartURL
	 * 
	 * @return String url of the degree distribution chart
	 * 
	 */
	public String getChartURL()
	{
		return "/wigi/LineChartServlet?graphSize=" + getGraphSize() + "&graph=" + getGraph() + "&selected=" + getSelectedIndexesString();
	}
	
	
	/**
     * Create the chart
     *
     * @param 	height 			the frame height
     * @param 	width  			the frame width
     * @param 	points			Arraylist of the degree distribution points
     * @param 	selectedIndexes	Arraylist of the selected nodes to draw
     * 
     * @return	BufferedImage	The buffered image generated
     * 
     */
    public BufferedImage LineChart(int height, int width, ArrayList<Double> points, ArrayList<Integer> selectedIndexes) {

        XYDataset dataset = createDataset(points, selectedIndexes);
        BufferedImage chart = createChart(dataset, selectedIndexes, width, height); 
    	
        return chart;       
    }
    
    
    /**
     * Creates a sample dataset.
     * degreeDistribution is the degree distribution dataset.
     * Other XYseries (selected indexes) are the selected nodes dataset.
     * 
     * @param	points			Arraylist of the degree distribution points
     * @param	selectedIndexes	Arraylist of the selected nodes
     * 
     * @return a sample dataset.
     * 
     */     
    private XYDataset createDataset( ArrayList<Double> points, ArrayList<Integer> selectedIndexes ) {

    	XYSeriesCollection dataset = new XYSeriesCollection();
    	
    	//Create the degree distribution dataset
    	double maxValue = 0;
        if (points.size() != 0){
        	
        	
        	XYSeries lag = new XYSeries("lag");
        	
        	lag.add(-0.0001, 0);
        	lag.add(0, -0.0001);
        	
        	dataset.addSeries(lag);
        	
        	XYSeries degreeDistribution = new XYSeries("degree distribution");
        	
        	for (int i=0; i<points.size(); i++){
        		degreeDistribution.add(i,points.get(i));
        		if(points.get(i) > maxValue){
        			maxValue = points.get(i);
        		}
        	}
        	dataset.addSeries(degreeDistribution);
        }

        
        //If the selectedIndexes Arraylist isn't empty, creation of the selected nodes dataset
        if(selectedIndexes.size() != 0){
        	for (int i=0; i<selectedIndexes.size(); i++){
        		XYSeries selectedIndexesDataSet = new XYSeries("selected indexes");
        		double value = Double.parseDouble(Integer.toString(selectedIndexes.get(i))); 
        		selectedIndexesDataSet.add(value, 0.0);
        		selectedIndexesDataSet.add(value, maxValue);
        		
        		dataset.addSeries(selectedIndexesDataSet);
        	}
        }
        
        return dataset;
        
    }
    
    /**
     * Creates a chart.
     * 
     * @param dataset  the data for the chart.
     * 
     * @return BufferedImage of the chart.
     */
    private BufferedImage createChart(final XYDataset dataset, ArrayList<Integer> selectedIndexes, int width, int height) {
        
        // create the chart...
        JFreeChart chart = ChartFactory.createXYLineChart(
            "",
            "",                      	// x axis label
            "",	                        // y axis label
            dataset,                    // data
            PlotOrientation.VERTICAL,
            false,                      // include legend
            false,                      // tooltips
            true						// urls
        );

        //set the background chart color
        chart.setBackgroundPaint(Color.white);

        
        // get a reference to the plot for further customisation...
        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.white);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        
        //Line color (blue for the degree distribution line and red for the selectedNodes)
        XYItemRenderer renderer = chart.getXYPlot().getRenderer();
        renderer.setSeriesPaint(0, Color.white);
        renderer.setSeriesPaint(1, Color.blue);
        
        //For each selected Indexes, loop and set the curve color to red.
        for (int i=0; i<selectedIndexes.size(); i++){
        	renderer.setSeriesPaint(i+2,Color.red);
        }
        

        // change the auto tick unit selection to integer units only...
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        
        //change the chart to a bufferedImage
        ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());
        BufferedImage image = chart.createBufferedImage(width, height, info);
        
        return image;
        
    }
    
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Save the chart passed as parameter as "chart.png".
     * 
     * @param chart
     * @param width
     * @param height
     
    private String saveChartAsImage(JFreeChart chart, int width, int height){
    	
    	
    	File f = new File("/Users/gregmeyer/dev/WiGi/WebContent/chart/DegreeDistributionChart.png");

    	try {
			ChartUtilities.saveChartAsPNG(f, chart, width, height);
			
			
			
			BufferedImage image = ImageIO.read(f);
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return "../chart/DegreeDistributionChart.png";
    } 
    
    */
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	// =========================================
	// onload
	// =========================================
	// needs:
	// in xhml: <h:outputText value="#{linkeddataBean.onload}"
	// style="display:none" />
	// and String onload = ""; with getter and setter

	public void onload()
	{
		// get graph
		pb = getPaintBean();
		graph = pb.getGraph();
	}
	
	String onload = "";
	
	public String getOnload()
	{
		return onload;
	}
	
	public void setOnload(String onload)
	{
		this.onload = onload;
	}
	
	// =========================================
	// get paint bean
	// =========================================
	private static PaintBean getPaintBean()
	{
		PaintBean pb = PaintBean.getCurrentInstance();
		
		if (pb == null)
		{
			p("pb is null, creating new instance...");
			pb = new PaintBean();
			HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
			session.setAttribute("paintBean", pb);
		}
		
		return pb;
	}

	// =========================================
	// get graph
	// =========================================
	int numGraphNodes;
	
	public DNVGraph getGraph()
	{
		if (pb == null)
			pb = getPaintBean();
		
		graph = pb.getGraph();
		
		if (numGraphNodes != graph.getVisibleNodes(0).size())
			invalidateData();
		
		numGraphNodes = graph.getVisibleNodes(0).size();	// to track changes in graph
		
		return graph;
	}
	
	public void setGraph(DNVGraph graph)
	{
		this.graph = graph;
	}

	
	
	
	
	
	
	
	
	
	
	
	//=============================================
	// GETTERS AND SETTERS
	//=============================================
	public int getChartW()
	{
		return chartW;
	}

	public void setChartW( int chartW )
	{
		this.chartW = chartW;
	}
	public int getChartH()
	{
		return chartH;
	}

	public void setChartH( int chartH )
	{
		this.chartH = chartH;
	}
	
	private boolean statsExpanded = true;
	
	public void expandStats()
	{
		statsExpanded = true;
	}
	
	public void collapseStats()
	{
		statsExpanded = false;
	}
	
	public boolean isStatsExpanded()
	{
		return statsExpanded;
	}
}
