package net.wigis.yun;

import java.util.ArrayList;

import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVNode;

@SuppressWarnings("rawtypes")
public class Triangle implements Comparable{
	public ArrayList<DNVNode> vertices;
	public ArrayList<DNVEdge> edges;
	public Triangle(){
		vertices = new ArrayList<DNVNode>();
		edges = new ArrayList<DNVEdge>();
	}
	public Triangle(DNVNode n1, DNVNode n2, DNVNode n3){
		vertices = new ArrayList<DNVNode>();
		edges = new ArrayList<DNVEdge>();
		vertices.add(n1); 
		vertices.add(n2); 
		vertices.add(n3);
	}
	public Triangle(DNVNode n1, DNVNode n2, DNVNode n3, DNVEdge e1, DNVEdge e2, DNVEdge e3){
		vertices = new ArrayList<DNVNode>();
		edges = new ArrayList<DNVEdge>();
		vertices.add(n1); 
		vertices.add(n2); 
		vertices.add(n3);
		edges.add(e1);
		edges.add(e2);
		edges.add(e3);
	}
	public boolean share(Triangle other){	
		for(DNVNode vert : vertices){
			if(other.vertices.contains(vert)){
				return true;
			}
		}
		return false;
	}
	public boolean share(Line other){	
		for(DNVNode vert : vertices){
			if(other.vertices.contains(vert)){
				return true;
			}
		}
		return false;
	}
	public int hashCode() {
		int hash = 0;
		for(DNVNode node : vertices){
			hash += node.hashCode();
		}
        return hash;
    }
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if(other instanceof Triangle){
			if(vertices.size() != ((Triangle)other).vertices.size()){
				return false;
			}else if(!((Triangle)other).vertices.containsAll(vertices)){
				return false;
			}
	        return true;
		}
		return false;
    }
	
	@Override
	public int compareTo(Object other) throws ClassCastException {
		// TODO Auto-generated method stub
		if(!(other instanceof Triangle)){
			throw new ClassCastException("A Triangle object expected.");
		}
		int d1 = 0;
		int d2 = 0;
		for(DNVNode node : vertices){
			d1 += node.getConnectivity();
		}
		for(DNVNode node : ((Triangle)other).vertices){
			d2 += node.getConnectivity();
		}
		return d1 - d2;
	}
	
}
