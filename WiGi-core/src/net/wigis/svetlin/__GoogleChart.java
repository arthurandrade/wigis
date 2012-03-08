package net.wigis.svetlin;

import java.util.ArrayList;

import com.googlecode.charts4j.AxisLabels;
import com.googlecode.charts4j.AxisLabelsFactory;
import com.googlecode.charts4j.AxisStyle;
import com.googlecode.charts4j.AxisTextAlignment;
import com.googlecode.charts4j.Color;
import com.googlecode.charts4j.DataUtil;
import com.googlecode.charts4j.GCharts;
import com.googlecode.charts4j.Line;
import com.googlecode.charts4j.LineChart;
import com.googlecode.charts4j.LineStyle;
import com.googlecode.charts4j.Plots;
import com.googlecode.charts4j.Shape;

public class __GoogleChart
{
	// HELP: http://code.google.com/p/charts4j/
	
	public static void p(Object o) { System.out.println(o);	}
	public static void pe(Object o) { System.err.println(o); }
	
	private static String errorChart()
	{
		return "";
	}
	
	public static String chart (
		String title, 
		int w, int h, 
		ArrayList<Double> points, 
		int pointSize,
		Color lineColor, 
		String lineLabel,
		int axisFontSize,
		ArrayList<Integer> selectedIndexes, 
		boolean showInBrowser)
	{
		// puts bottom at 0 in order for grid lines to work
		
		double min = net.wigis.svetlin.__ArrayList.getMin(points);
		double max = net.wigis.svetlin.__ArrayList.getMax(points); 
		
		if (min >= max)
			return errorChart();
		
		// Defining lines
		Line line1;
		if (lineLabel == null)
			line1 = Plots.newLine(DataUtil.scaleWithinRange(min, max, points), lineColor);
		else
			line1 = Plots.newLine(DataUtil.scaleWithinRange(min, max, points), lineColor, lineLabel);
		line1.setLineStyle(LineStyle.newLineStyle(1, 1, 0));
		line1.addShapeMarkers(Shape.CIRCLE, lineColor, pointSize);
		
		// Defining chart.
		LineChart chart = GCharts.newLineChart(line1);
		chart.setSize(w, h);
		if (title != null)
			chart.setTitle(title, Color.BLACK, 20);
		// chart.addHorizontalRangeMarker(40, 60, Color.newColor(Color.RED, 30));
		// chart.addVerticalRangeMarker(70, 90, Color.newColor(Color.GREEN, 30));
		chart.setGrid(points.size(), 100/(max/10), 2, 1);
		
		// selected indexes - vertical lines
		if (selectedIndexes != null)
			for (int i=0; i<selectedIndexes.size(); i++)
			{
				if (selectedIndexes.get(i) >= 0)
				{
					double position = (double)selectedIndexes.get(i)/points.size()*100;

					// to account for rounding errors
					if (position < 1)
						position = 1;

					if (position > 99)
						position = 99;
					
					//p(position);
					
					chart.addVerticalRangeMarker(position, position + 1, Color.RED );	// positions must be in [0,100]
				}
			}
		
		// // Defining axis info and styles
		AxisStyle axisStyle = AxisStyle.newAxisStyle(Color.BLACK, axisFontSize, AxisTextAlignment.CENTER);
		AxisLabels yAxis = AxisLabelsFactory.newNumericRangeAxisLabels(0, max);
		yAxis.setAxisStyle(axisStyle);

		// // Adding axis info to chart.
		chart.addYAxisLabels(yAxis);

		// url
		String url = chart.toURLString();
		
		// browse
		if (showInBrowser)
			net.wigis.svetlin.__Browser.openUrl(url);

		return url;
	}
	
	
	public static void main(String[] args)
	{
		final int NUM_POINTS = 200;
		final double[] points = new double[NUM_POINTS];
		for (int i = 0; i < NUM_POINTS; i++)
			points[i] = Math.random();
		
		__Array.sort(points, false);
		
		int[] selectedIndexes = new int[3];
		selectedIndexes[0] = 1;
		selectedIndexes[1] = 50;
		selectedIndexes[2] = 68;
		
//		p(__GoogleChart.chart(
//				null, 
//				400,200, 
//				points, 
//				Color.DARKBLUE, 
//				null,
//				18,
//				selectedIndexes,
//				true));
		
		//p(__GoogleChart.chart(null, 195, 100, points, 1, Color.BLUE, null, 11, null, true));
	}
}



















