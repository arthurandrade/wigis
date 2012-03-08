package net.wigis.graph.jgrapht.converter;

import java.util.Collection;
import java.util.Set;

import net.wigis.graph.GraphsPathFilter;
import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.jgrapht.JGraphTEdge;
import net.wigis.settings.Settings;

import org.jgrapht.Graph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.BiconnectivityInspector;
import org.jgrapht.alg.BronKerboschCliqueFinder;
import org.jgrapht.graph.SimpleGraph;

public class JGraphTConverter
{
	@SuppressWarnings("unchecked")
	public static UndirectedGraph<DNVNode, JGraphTEdge<DNVNode>> convertDNVToUndirectedJGraphT( DNVGraph graph, int level )
	{
		UndirectedGraph<DNVNode, JGraphTEdge<DNVNode>> g = new SimpleGraph<DNVNode, JGraphTEdge<DNVNode>>((Class<? extends JGraphTEdge<DNVNode>>)JGraphTEdge.class );
		for( DNVNode node : graph.getNodes( level ) )
		{
			g.addVertex( node );
		}

		for( DNVEdge edge : graph.getEdges( level ) )
		{
			g.addEdge( edge.getFrom(), edge.getTo() );
		}

		return g;
	}

	public static void main( String args[] )
	{
		GraphsPathFilter.init();
		DNVGraph graph = new DNVGraph( Settings.GRAPHS_PATH + "UserStudy/testGraphs/graph2large.dnv" );
		UndirectedGraph<DNVNode, JGraphTEdge<DNVNode>> g = convertDNVToUndirectedJGraphT( graph, 0 );
		BiconnectivityInspector<DNVNode, JGraphTEdge<DNVNode>> inspector = new BiconnectivityInspector<DNVNode, JGraphTEdge<DNVNode>>( g );
		System.out.println( inspector.isBiconnected() );
		Set<Set<DNVNode>> components = inspector.getBiconnectedVertexComponents();
		System.out.println( "graph contains " + components.size() + " biconnected components" );
		for( Set<DNVNode> component : components )
		{
			System.out.println( "component of size " + component.size() + " contains:" );
			for( DNVNode node : component )
			{
				System.out.println( "\t" + node.getLabel() );
			}
		}

		BronKerboschCliqueFinder<DNVNode, JGraphTEdge<DNVNode>> cliqueFinder = new BronKerboschCliqueFinder<DNVNode, JGraphTEdge<DNVNode>>( g );
		Collection<Set<DNVNode>> cliques = cliqueFinder.getAllMaximalCliques();
		printCliques( cliques );
		cliques = cliqueFinder.getBiggestMaximalCliques();
		printCliques( cliques );
	}

	public static void printCliques( Collection<Set<DNVNode>> cliques )
	{
		System.out.println( "graph contains " + cliques.size() + " cliques" );
		int counter = 0;
		for( Set<DNVNode> clique : cliques )
		{
			if( clique.size() >= 4 )
			{
				counter++;
				System.out.println( "clique of size " + clique.size() + " contains:" );
				for( DNVNode node : clique )
				{
					System.out.println( "\t" + node.getLabel() );
				}
			}
		}
		System.out.println( counter + " cliques of size >= 4" );
	}

	public static DNVGraph convertJGraphTToDNVGraph( Graph<DNVNode, JGraphTEdge<DNVNode>> g )
	{
		return null;
	}
}
