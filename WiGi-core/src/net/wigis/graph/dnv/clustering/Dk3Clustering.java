package net.wigis.graph.dnv.clustering;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.wigis.graph.GraphsPathFilter;
import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.settings.Settings;
import net.wigis.stats.Dk3Calc;
import net.wigis.yun.*;

public class Dk3Clustering {
	@SuppressWarnings("rawtypes")
	public static void cluster( DNVGraph graph, int level)
	{
		List<DNVNode> untraveledNode = graph.getNodes(level);
		List<DNVEdge> untraveledEdge = graph.getEdges(level);
		
		Dk3Calc dk3Calc = new Dk3Calc(graph, level);
		DNVGraph compressedGraph = new DNVGraph();
		
		List triangles = hashTableSort.sortKeyDesc(dk3Calc.getTriangles());
		List lines = hashTableSort.sortKeyDesc(dk3Calc.getLines());
		
		
		HashMap<DNVNode, Triangle> parentToTriangle = new HashMap<DNVNode, Triangle>();
		HashMap<DNVNode, Line> parentToLine = new HashMap<DNVNode, Line>();
		
		HashMap<Triangle, DNVNode> triangleToParent = new HashMap<Triangle, DNVNode>();
		HashMap<Line, DNVNode> lineToParent = new HashMap<Line, DNVNode>();
		
		HashMap<DNVNode, ArrayList<Triangle>> nodeToTriangles = new HashMap<DNVNode, ArrayList<Triangle>>();
		HashMap<DNVNode, ArrayList<Line>> nodeToLines = new HashMap<DNVNode, ArrayList<Line>>();
		
//		Ideally each node can have multiple parents to average its initial position		
//		HashMap<DNVNode, ArrayList<DNVNode>> nodeToParent = new HashMap<DNVNode, ArrayList<DNVNode>>();
		
		HashMap<DNVNode, DNVNode> nodeToParent = new HashMap<DNVNode, DNVNode>();
		
		for(Object obj : triangles){			
			Triangle tri = (Triangle)obj;
			untraveledEdge.removeAll(tri.edges);
			boolean buildNewNode = false;
			//map each node to the triangle it belongs to and see if need to build a new node
			for(DNVNode node : tri.vertices){
				if(!nodeToTriangles.containsKey(node)){
					nodeToTriangles.put(node, new ArrayList<Triangle>());
				}
				nodeToTriangles.get(node).add(tri);
				if(!nodeToParent.containsKey(node)){
					buildNewNode = true;
				}
			}
			//build a new node, assign parent to the nodes in the triangle
			if(buildNewNode){
				DNVNode parent = new DNVNode(compressedGraph);
				parentToTriangle.put(parent, tri);
				triangleToParent.put(tri,parent);
				compressedGraph.addNode(0, parent);
				for(DNVNode node : tri.vertices){
					nodeToParent.put(node, parent);
					untraveledNode.remove(node);
				}
			}
			
		}
		//do the same thing for lines
		for(Object obj : lines){
			Line line = (Line)obj;
			untraveledEdge.removeAll(line.edges);
			//map each node to the triangle it belongs to and see if need to build a new node
			for(DNVNode node : line.vertices){
				if(!nodeToLines.containsKey(node)){
					nodeToLines.put(node, new ArrayList<Line>());
				}
				nodeToLines.get(node).add(line);
				untraveledNode.remove(node);
			}
			//build a new node, assign parent to the nodes in the triangle
			if(!nodeToParent.containsKey(line.nodes.getMiddle())){
				//System.out.println("add new node");
				DNVNode parent = new DNVNode(compressedGraph);
				parentToLine.put(parent, line);
				lineToParent.put(line,parent);
				compressedGraph.addNode(0, parent);
				for(DNVNode node : line.vertices){
					nodeToParent.put(node, parent);				
				}
			}else{
				DNVNode nodeLeft = line.nodes.getLeft();
				DNVNode nodeRight = line.nodes.getRight();
				DNVNode middleParent = nodeToParent.get(line.nodes.getMiddle());
				if(!nodeToParent.containsKey(nodeLeft) && nodeLeft.getConnectivity() == 1){
					nodeToParent.put(nodeLeft, middleParent);		
					untraveledNode.remove(nodeLeft);
				}
				if(!nodeToParent.containsKey(nodeRight) && nodeRight.getConnectivity() == 1){
					nodeToParent.put(nodeRight, middleParent);	
					untraveledNode.remove(nodeRight);
				}
			}
		}
		//start building edges for the parent nodes
		/*List<DNVNode> newNodes = compressedGraph.getNodes(0);
		for(int i = 0; i < newNodes.size(); i++){
			for(int j = 1; j < newNodes.size(); j++){				
				DNVNode p1 = newNodes.get(i);
				DNVNode p2 = newNodes.get(j);
				if(p1.getNeighbors().contains(p2) || p2.getNeighbors().contains(p1)){
					continue;
				}
				Triangle p1Tri = parentToTriangle.get(p1);
				Triangle p2Tri = parentToTriangle.get(p2);
				Line p1Line = parentToLine.get(p1);
				Line p2Line = parentToLine.get(p2);
				if(p1Tri != null){
					if(p2Tri != null){
						if(p1Tri.share(p2Tri)){
							DNVEdge edge = new DNVEdge(compressedGraph);
							edge.setTo(p1);
							edge.setFrom(p2);
							compressedGraph.addEdge(0, edge);
							continue;
						}
					}
					if(p2Line != null){
						if(p1Tri.share(p2Line)){
							DNVEdge edge = new DNVEdge(compressedGraph);
							edge.setTo(p1);
							edge.setFrom(p2);
							compressedGraph.addEdge(0, edge);
							continue;
						}
					}
				}
				
				if(p1Line != null){
					if(p2Tri != null){
						if(p1Line.share(p2Tri)){
							DNVEdge edge = new DNVEdge(compressedGraph);
							edge.setTo(p1);
							edge.setFrom(p2);
							compressedGraph.addEdge(0, edge);
							continue;
						}
					}
					if(p2Line != null){
						if(p1Line.share(p2Line)){
							DNVEdge edge = new DNVEdge(compressedGraph);
							edge.setTo(p1);
							edge.setFrom(p2);
							compressedGraph.addEdge(0, edge);
							continue;
						}
					}
				}
			}
		}*/
		for(DNVNode parent : compressedGraph.getNodes(0)){
			Triangle tri = parentToTriangle.get(parent);
			if(tri != null){
				for(DNVNode node : tri.vertices){
					if(nodeToTriangles.get(node) != null){
						for(Triangle newTri : nodeToTriangles.get(node)){
							if(newTri != tri){
								DNVNode otherNode = triangleToParent.get(newTri);
								if(otherNode != null && !parent.getAllNeighbors().contains(otherNode) && !parent.shareNeighbors(otherNode)){
									DNVEdge edge = new DNVEdge(compressedGraph);
									edge.setTo(parent);
									edge.setFrom(otherNode);
									compressedGraph.addEdge(0, edge);
								}
							}
						}
					}
					if(nodeToLines.get(node) != null){
						for(Line newLine: nodeToLines.get(node)){
							DNVNode otherNode = lineToParent.get(newLine);
							if(otherNode != null && !parent.getAllNeighbors().contains(otherNode) && !parent.shareNeighbors(otherNode)){
								DNVEdge edge = new DNVEdge(compressedGraph);
								edge.setTo(parent);
								edge.setFrom(otherNode);
								compressedGraph.addEdge(0, edge);
							}
						}
					}
				}
			}
			
			Line line = parentToLine.get(parent);
			if(line != null){
				for(DNVNode node : line.vertices){
					if(nodeToTriangles.get(node) != null){
						for(Triangle newTri : nodeToTriangles.get(node)){
							DNVNode otherNode = triangleToParent.get(newTri);
							if(otherNode != null && !parent.getAllNeighbors().contains(otherNode) && !parent.shareNeighbors(otherNode)){
								DNVEdge edge = new DNVEdge(compressedGraph);
								edge.setTo(parent);
								edge.setFrom(otherNode);
								compressedGraph.addEdge(0, edge);
							}
						}
					}
					if(nodeToLines.get(node) != null){
						for(Line newLine: nodeToLines.get(node)){
							if(newLine != line){
								DNVNode otherNode = lineToParent.get(newLine);
								if(otherNode != null && !parent.getAllNeighbors().contains(otherNode) && !parent.shareNeighbors(otherNode)){
									//if(!parent.getNeighbors().contains(otherNode) && !otherNode.getNeighbors().contains(parent)){
										DNVEdge edge = new DNVEdge(compressedGraph);
										edge.setTo(parent);
										edge.setFrom(otherNode);
										compressedGraph.addEdge(0, edge);
								}
							}
						}
					}
				}
			}
		}
		//put in the disconnected nodes
		for(DNVNode node : untraveledNode){
			if(node.getConnectivity() > 1){
				System.out.println("connectivity > 1, shouldn't be happening");
			}
			DNVNode parent = new DNVNode(compressedGraph);
			compressedGraph.addNode(0, parent);
			nodeToParent.put(node, parent);
		}
		for(DNVEdge edge : untraveledEdge){
			if(! (edge.getFrom().getConnectivity() == 1 && edge.getTo().getConnectivity() == 1)){
				System.out.println("weird edge");
				continue;
			}
			DNVNode p1 = nodeToParent.get(edge.getFrom());
			DNVNode p2 = nodeToParent.get(edge.getTo());
			if(p1 == null){
				System.out.println("p1 is null");
			}
			if(p2 == null){
				System.out.println("p2 is null " + edge.getFrom().getConnectivity() + " " + edge.getTo().getConnectivity());
				DNVNode node = edge.getTo();
				if(nodeToTriangles.get(node) == null){
					System.out.println("no triangle");
				}else{
					System.out.println("belong to triangle");
				}
				if(nodeToLines.get(node) == null){
					System.out.println("no line");
				}else{
					System.out.println("belong to line");
				}
			}
			DNVEdge pedge = new DNVEdge(compressedGraph);
			pedge.setFrom(p1);
			pedge.setTo(p2);
			compressedGraph.addEdge(0, pedge);
		}
		compressedGraph.writeGraph(Settings.GRAPHS_PATH + "fb1000_level1.dnv");
		System.out.println("this level contains " + compressedGraph.getGraphSize(0) + " nodes " + compressedGraph.getEdges(0).size() + " edges");
	}
	public static void main(String[] args){
		GraphsPathFilter.init();
		DNVGraph graph = new DNVGraph( Settings.GRAPHS_PATH + "fb1000.dnv" );
		System.out.println("this level contains " + graph.getGraphSize(0) + " nodes " + graph.getEdges(0).size() + " edges");
		cluster(graph, 0);
	}
}
