package net.wigis.graph.dnv.utilities;

import java.util.ArrayList;
import java.util.List;

import net.wigis.graph.GraphsPathFilter;
import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.settings.Settings;

public class GenerateWiGisGraph
{
	public static void main( String args[] )
	{
		GraphsPathFilter.init();
		generate();
	}

	/**
	 * 
	 */
	private static void generate()
	{
		DNVGraph graph = new DNVGraph();
		
		String nodeColor = "#6666FF";
		
		makeLetter( graph, nodeColor, "W", wPositions );
		makeLetter( graph, nodeColor, "i1", i1Positions );
		makeLetter( graph, nodeColor, "i1_dot", i1_dotPositions );
		makeLetter( graph, nodeColor, "i2", i2Positions );
		makeLetter( graph, nodeColor, "i2_dot", i2_dotPositions );
		makeLetter( graph, nodeColor, "s", sPositions );
		List<DNVNode> nodes = generateSeries( graph, 7, nodeColor, "G" );
		DNVNode node = addNode( graph, nodeColor, "G", nodes );
		addEdge( graph, "G", nodes.get( 5 ), node );
		applyPositions( gPositions, nodes );
		
		graph.writeGraph( Settings.GRAPHS_PATH + "WiGis.dnv" );
	}

	private static Vector2D wPositions[] = {
		new Vector2D( 0, 20 ),
		new Vector2D( 5, 100 ),
		new Vector2D( 10, 50 ),
		new Vector2D( 15, 100 ),
		new Vector2D( 20, 20 )
	};
	private static Vector2D i1Positions[] = {
		new Vector2D( 25, 100 ),
		new Vector2D( 25, 60 )
	};
	private static Vector2D i1_dotPositions[] = {
		new Vector2D( 25, 50 )
	};
	private static Vector2D gPositions[] = {
		new Vector2D( 40, 37 ),
		new Vector2D( 32, 35 ),
		new Vector2D( 30, 60 ),
		new Vector2D( 32, 100 ),
		new Vector2D( 40, 100 ),
		new Vector2D( 40, 70 ),
		new Vector2D( 35, 70 ),		
		new Vector2D( 45, 70 )		
	};
	private static Vector2D i2Positions[] = {
		new Vector2D( 50, 100 ),
		new Vector2D( 50, 60 )
	};
	private static Vector2D i2_dotPositions[] = {
		new Vector2D( 50, 50 )
	};
	private static Vector2D sPositions[] = {
		new Vector2D( 65, 50 ),
		new Vector2D( 65, 40 ),
		new Vector2D( 55, 42 ),
		new Vector2D( 55, 70 ),
		new Vector2D( 65, 90 ),
		new Vector2D( 65, 100 ),
		new Vector2D( 55, 100 ),
		new Vector2D( 55, 90 )
	};
	
	private static void makeLetter( DNVGraph graph, String nodeColor, String type, Vector2D[] positions )
	{
		List<DNVNode> nodes = generateSeries( graph, positions.length, nodeColor, type );
		applyPositions( positions, nodes );
	}

	/**
	 * @param positions
	 * @param nodes
	 */
	private static void applyPositions( Vector2D[] positions, List<DNVNode> nodes )
	{
		for( int i = 0; i < positions.length; i++ )
		{
			nodes.get( i ).setPosition( positions[i] );
		}
	}

	/**
	 * @param graph
	 * @param length
	 */
	private static List<DNVNode> generateSeries( DNVGraph graph, int length, String nodeColor, String type )
	{
		DNVNode previousNode = null;
		List<DNVNode> list = new ArrayList<DNVNode>();
		for( int i = 0; i < length; i++ )
		{
			DNVNode node = addNode( graph, nodeColor, type, list );
			if( previousNode != null )
			{
				addEdge( graph, type, previousNode, node );
			}
			
			previousNode = node;
		}
		
		return list;
	}

	/**
	 * @param graph
	 * @param nodeColor
	 * @param type
	 * @param list
	 * @return
	 */
	private static DNVNode addNode( DNVGraph graph, String nodeColor, String type, List<DNVNode> list )
	{
		DNVNode node = new DNVNode( graph );
		node.setColor( nodeColor );
		node.setPosition( (float)Math.random(), (float)Math.random() );
		node.setType( type );
		graph.addNode( 0, node );
		list.add( node );
		return node;
	}

	/**
	 * @param graph
	 * @param type
	 * @param previousNode
	 * @param node
	 */
	private static void addEdge( DNVGraph graph, String type, DNVNode previousNode, DNVNode node )
	{
		DNVEdge edge = new DNVEdge( previousNode, node, graph );
		edge.setThickness( 4 );
		edge.setType( type );
		graph.addEdge( 0, edge );
	}
}
