package net.wigis.stats;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.greg.NextNode;

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

public class SubGraphStatistics {

	/**
	 * Average path length method modified to work for a set of nodes passed as parameters.
	 * 
	 * @return string averagePathLength, the result
	 */
	public static String computeAveragePathLength(List<DNVNode> selectedNodes)
	{	
		ArrayList<DNVNode> nodeList = new ArrayList<DNVNode>(selectedNodes);
		DNVNode nodeBeingVisited = new DNVNode();
		double averagePathLength = 0.0;
		
		int nodeBeingVisitedWeight = 0;
		int temp = 0;
		
		ArrayList<DNVNode> tmp = new ArrayList<DNVNode>();
		ArrayList<NextNode> nextNode = new ArrayList<NextNode>();
		Hashtable<DNVNode, Integer> visitedNode = new Hashtable<DNVNode, Integer>();
		
		Enumeration e;
		String key = "";
		String result = "";
		Double counter = 0.0;
		Double sum = 0.0;
		Double degree = 0.0;
		
		int values = 0;
		int size = 0;
		
		//For all nodes from the node list
		for(int n=0;n<nodeList.size();n++)
		{
			visitedNode = new Hashtable<DNVNode, Integer>();
			nextNode = new ArrayList<NextNode>();
			
			//get the current node being visited
			nodeBeingVisited = nodeList.get(n);
			
			nodeBeingVisitedWeight = 0;

			//get all of the current node neighbors
			tmp = (ArrayList<DNVNode>) nodeBeingVisited.getNeighbors(true);
			
			temp = nodeBeingVisitedWeight+1;
			for(int i=0;i<tmp.size();i++)
			{
				for(int y=0;y<selectedNodes.size();y++){
					if(tmp.get(i).equals(selectedNodes.get(y))){
						nextNode.add(new NextNode(tmp.get(i),temp));
					}
				}
			}
			
			//Add the current node to the visited node list
			visitedNode.put(nodeBeingVisited,nodeBeingVisitedWeight);
			
			//While the next node list is not empty
			while(nextNode.isEmpty() == false)
			{
				//Get the current node being visited
				nodeBeingVisited = nextNode.get(0).getNode();
				
				//if the node hasn't been visited yet
				if(visitedNode.containsKey(nodeBeingVisited) == false)
				{
					nodeBeingVisitedWeight = nextNode.get(0).getWeight();
					
					//get its neighbors & add them at the end of the next node list
					tmp = (ArrayList<DNVNode>) nodeBeingVisited.getNeighbors(true);
					temp = nodeBeingVisitedWeight + 1;
					for(int x=0;x<tmp.size();x++)
					{
						for(int y=0;y<selectedNodes.size();y++){
							if(tmp.get(x).equals(selectedNodes.get(y))){
								nextNode.add(new NextNode(tmp.get(x),temp));
							}
						}
					}
					
					//add the current node into the visited node list
					visitedNode.put(nodeBeingVisited,nodeBeingVisitedWeight);
				}
				//remove the node from the next node list
				nextNode.remove(0);
			}
			
			values = 0;
			e = visitedNode.keys();
			
			//get all the weights ans add them up together
		    while (e.hasMoreElements()){
		      Object str = e.nextElement();
		      values += visitedNode.get(str);
		    }
		    
			size = visitedNode.size();
			
			//get the APL for the current node
			sum += Double.parseDouble(Integer.toString(values))/Double.parseDouble(Integer.toString(size));
			
			counter++;
			
			degree += nodeBeingVisited.getDegree();
			
			
			
		}
		
		//get the total APL
		averagePathLength = sum/counter;

		//convert it into a string and reduce the number of numbers after the dot
		result = Double.toString(averagePathLength);
		int dot = result.indexOf(".");
		
		if (result.substring(dot, result.length()).length() >3 ){
			result = result.substring(0, dot+3);
		}
		
		//get the average degree distribution
		double averageDegree = degree/nodeList.size();
		
		//convert it into a string and reduce the number of numbers after the dot
		String result2 = Double.toString(averageDegree);
		dot = result2.indexOf(".");
		
		if (result2.substring(dot, result2.length()).length() >3 ){
			result2 = result2.substring(0, dot+3);
		}
		
		
		result = result + "?" + result2;

		return result;
		
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
    public static BufferedImage LineChart(int height, int width, ArrayList<Double> points)
    {
        XYDataset dataset = createDataset(points);
        BufferedImage chart = createChart(dataset, width, height); 
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
    private static XYDataset createDataset( ArrayList<Double> points) {

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
        
        return dataset;
        
    }
    
    /**
     * Creates a chart.
     * 
     * @param dataset  the data for the chart.
     * 
     * @return BufferedImage of the chart.
     */
    private static BufferedImage createChart(final XYDataset dataset, int width, int height) {
        
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
        

        // change the auto tick unit selection to integer units only...
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        
        //change the chart to a bufferedImage
        ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());
        BufferedImage image = chart.createBufferedImage(width, height, info);
        
        return image;
    }
    
    
    public static String computeAverageDegreeCentrality(DNVGraph graph){
		String SBADC = "";
		Double sum = 0.0;
		int index = -1;
		
		Map<Integer, DNVNode> nodesMap = graph.getSelectedNodes(0);
		Collection<DNVNode> c = nodesMap.values();
		List<DNVNode> nodesList = new ArrayList<DNVNode>( c );
		
		for(int i=0;i<nodesList.size();i++){
			sum += Double.parseDouble(Integer.toString(NodeStatistics.computeNodeDegreeCentrality(nodesList.get(i))));
		}
		
		
		
		SBADC = Double.toString(sum/nodesList.size());
		index = SBADC.indexOf(".");
		if(index != -1){
			SBADC = SBADC.substring(0, index+2);
		}
		
		
		return SBADC;
    }
	
	
	
	
}
