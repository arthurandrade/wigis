package net.wigis.graph.dnv;

import net.wigis.graph.GraphsPathFilter;
import net.wigis.settings.Settings;

public class DNVGraphTest
{

	/**
	 * @param args
	 */
	public static void main( String[] args )
	{
		GraphsPathFilter.init();
		DNVGraph graph = new DNVGraph();
		DNVNode node = new DNVNode( graph );
		node.setPosition( (float)Math.random(), (float)Math.random() );
		node.setColor( (float)Math.random(), (float)Math.random(), (float)Math.random() );
		DNVNode node2 = new DNVNode( graph );
		node2.setPosition( (float)Math.random(), (float)Math.random() );
		node2.setColor( (float)Math.random(), (float)Math.random(), (float)Math.random() );
		DNVNode node3 = new DNVNode( graph );
		node3.setPosition( (float)Math.random(), (float)Math.random() );
		node3.setColor( (float)Math.random(), (float)Math.random(), (float)Math.random() );
		DNVNode node4 = new DNVNode( graph );
		node4.setPosition( (float)Math.random(), (float)Math.random() );
		node4.setColor( (float)Math.random(), (float)Math.random(), (float)Math.random() );
		DNVNode node5 = new DNVNode( graph );
		node5.setPosition( (float)Math.random(), (float)Math.random() );
		node5.setColor( (float)Math.random(), (float)Math.random(), (float)Math.random() );
		graph.addNode( 0, node );
		graph.addNode( 0, node2 );
		graph.addNode( 0, node3 );
		graph.addNode( 0, node4 );
		graph.addNode( 0, node5 );
		
		DNVEdge edge = new DNVEdge( node, node2, graph );
		graph.addEntity( 0, edge );
		edge = new DNVEdge( node3, node5, graph );
		graph.addEntity( 0, edge );		
		edge = new DNVEdge( node, node5, graph );
		graph.addEntity( 0, edge );
		edge = new DNVEdge( node2, node4, graph );
		graph.addEntity( 0, edge );
		
		graph.setProperty( "testProperty", "testPropertyValue" );
		
		graph.writeGraph( Settings.GRAPHS_PATH + "testGraph.dnv" );
		
		DNVGraph graph2 = new DNVGraph( Settings.GRAPHS_PATH + "testGraph.dnv" );
		
		String property = graph2.getProperty( "testProperty" );
		System.out.println( "Property value is: " + property );
	}

}
