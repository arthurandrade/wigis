package net.wigis.graph.dnv.clustering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.utilities.Vector2D;
import net.wigis.graph.dnv.utilities.Vector3D;
import net.wigis.stats.Dk1Calc;

public class DK1Clustering {
	/** The multicolor. */
	private static boolean multicolor = true;
	
	public static void cluster( DNVGraph graph)
	{
		Dk1Calc dk1Calc = new Dk1Calc(graph);
		Hashtable<Integer,ArrayList<DNVNode>> degreeNodeTableDK1 = dk1Calc.getDegreeNodeTableDK1();
		List degreeOrderedListDK1 = dk1Calc.getDegreeOrderedListDK1();
		
		ArrayList<Integer> gaps = findGaps(degreeOrderedListDK1);
		for(Integer gap : gaps){
			System.out.println("\t" + gap);
		}
		System.out.println("finish printing gaps");		
		System.out.println("	prepair finished");
		
		HashSet<DNVNode> traveledNodes = new HashSet<DNVNode>();
		HashSet<DNVEdge> traveledEdges = new HashSet<DNVEdge>();
		HashSet<DNVNode> nodes = new HashSet<DNVNode>();
		HashSet<DNVEdge> edges = new HashSet<DNVEdge>();
		
		HashMap<Integer, Integer> nodeIdToLevel = new HashMap<Integer, Integer>();
		HashMap<Integer, ArrayList<DNVNode>> levelToNodeList = new HashMap<Integer, ArrayList<DNVNode>>();
		
		int highestLevel = gaps.size();
		int currentLevel = highestLevel;
		int lastVistedIndex = 0;
		
		int nodeCnt = 0;
		
		
		//map each nodeId to appropriate level
		for(int index : gaps){			
			Vector3D color = new Vector3D( (float)Math.max( 0.3, Math.random() ), (float)Math.max( 0.3, Math.random() ), (float)Math.max( 0.3, Math
					.random() ) );
			ArrayList<DNVNode> levelNodes = new ArrayList<DNVNode>();
			for(int i = lastVistedIndex; i < index; i++){
				Integer degree = (Integer) degreeOrderedListDK1.get(i);
				for(DNVNode node : degreeNodeTableDK1.get(degree)){
					nodeIdToLevel.put(node.getId(), currentLevel);
					levelNodes.add(node);
					node.setColor(color);
				}
			}
			levelToNodeList.put(currentLevel, levelNodes);
			System.out.println("gap" + index + "current level " + currentLevel + " number of nodes " + levelNodes.size());
			nodeCnt += levelNodes.size();
			currentLevel--;
			lastVistedIndex = index;
		}
		Vector3D color = new Vector3D( (float)Math.max( 0.3, Math.random() ), (float)Math.max( 0.3, Math.random() ), (float)Math.max( 0.3, Math
				.random() ) );
		ArrayList<DNVNode> levelNodes = new ArrayList<DNVNode>();
		for(int i = lastVistedIndex; i < degreeOrderedListDK1.size(); i++){
			Integer degree = (Integer) degreeOrderedListDK1.get(i);
			for(DNVNode node : degreeNodeTableDK1.get(degree)){
				nodeIdToLevel.put(node.getId(), currentLevel);
				levelNodes.add(node);
				node.setColor(color);
			}		
		}
		nodeCnt += levelNodes.size();
		levelToNodeList.put(currentLevel, levelNodes);
		System.out.println("current level " + currentLevel + " number of nodes " + levelNodes.size());
		System.out.println("nodeCnt " + nodeCnt);
		
		
		
	}
	
	/**
	 * Creates the parent node.
	 * 
	 * @param tempNode
	 *            the temp node
	 * @param newLevel
	 *            the new level
	 * @param graph
	 *            the graph
	 * @param nodeDistance
	 *            the node distance
	 * @param nodeToParent
	 *            the node to parent
	 * @param parentToNodes
	 *            the parent to nodes
	 * @return the dNV node
	 */
	public static DNVNode createParentNode( DNVNode tempNode, Integer newLevel, DNVGraph graph, Map<Integer, Integer> nodeDistance,
			Map<Integer, DNVNode> nodeToParent, Map<Integer, List<DNVNode>> parentToNodes )
	{
		DNVNode newNode = new DNVNode( new Vector2D( tempNode.getPosition() ), graph );
		newNode.setColor( tempNode.getColor() );
		newNode.setLevel( newLevel.intValue() );
		newNode.setFirstChild( tempNode );
		graph.addNode( newLevel, newNode );
		if( multicolor && newLevel == 1 )
		{
			Vector3D color = new Vector3D( (float)Math.max( 0.3, Math.random() ), (float)Math.max( 0.3, Math.random() ), (float)Math.max( 0.3, Math
					.random() ) );
			newNode.setColor( color );
		}
		else
		{
			Vector3D color = new Vector3D( tempNode.getColor() );
			newNode.setColor( color );
		}

		nodeDistance.put( tempNode.getId(), Integer.valueOf( 0 ) );
		nodeToParent.put( tempNode.getId(), newNode );
		List<DNVNode> tempList = new ArrayList<DNVNode>();
		tempList.add( tempNode );
		parentToNodes.put( newNode.getId(), tempList );
		return newNode;
	}
	
	public static ArrayList<Integer> findGaps(List degreeOrderedList){
		ArrayList<Integer> gaps = new ArrayList<Integer>();
		int numberOfElements = degreeOrderedList.size();
		Integer[] elements = new Integer[numberOfElements];
		degreeOrderedList.toArray(elements);
		Integer firstElm = elements[0];
		for(int i = 1; i < numberOfElements; i++){
			
			if(elements[i] != 0 && firstElm /elements[i] >= 2){
				gaps.add(i);
				firstElm = elements[i];
			}
		}
		
		return gaps;
	}
	/**
	 * Handle neighbors.
	 * 
	 * @param distance
	 *            the distance
	 * @param neighbors
	 *            the neighbors
	 * @param parentNode
	 *            the parent node
	 * @param nodeDistance
	 *            the node distance
	 * @param nodeToParent
	 *            the node to parent
	 */
	private static void handleNeighbors( int distance, List<DNVNode> neighbors, DNVNode parentNode, Map<Integer, Integer> nodeDistance,
			Map<Integer, DNVNode> nodeToParent )
	{
		DNVNode tempNode;
		Integer tempDistance;
		for( int i = 0; i < neighbors.size(); i++ )
		{
			tempNode = neighbors.get( i );
			if( multicolor && parentNode.getLevel() == 1 )
				tempNode.setColor( parentNode.getColor() );
			tempDistance = nodeDistance.get( tempNode.getId() );
			if( tempDistance == null || tempDistance > distance )
			{
				nodeDistance.put( tempNode.getId(), Integer.valueOf( distance ) );
				nodeToParent.put( tempNode.getId(), parentNode );
				handleNeighbors( distance + 1, tempNode.getNeighbors(), parentNode, nodeDistance, nodeToParent );
			}
			else
			{
				// System.out.println( "Stopping expansion at node " +
				// tempNode.getId() + " distance " + tempDistance +
				// " for parent node " + parentNode.getId() );
			}
		}
	}
}
