package net.wigis.yun;

import java.util.ArrayList;

import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVNode;

@SuppressWarnings("rawtypes")
public class Line implements Comparable{
	public ArrayList<DNVNode> vertices;

	public Tuple<DNVNode, DNVNode, DNVNode> nodes;
	public ArrayList<DNVEdge> edges;
	public Line(DNVNode n1, DNVNode n2, DNVNode n3, DNVEdge e1, DNVEdge e2){
		nodes = new Tuple<DNVNode, DNVNode, DNVNode>(n1, n2, n3);
		edges = new ArrayList<DNVEdge>();
		vertices = new ArrayList<DNVNode>();
		vertices.add(n1);
		vertices.add(n2);
		vertices.add(n3);
		edges.add(e1);
		edges.add(e2);
	}
	@Override
	public int hashCode() {
        return nodes.hashCode();
        //return (hashLeft + hashMiddle + hashRight) * hashMiddle + (hashLeft + hashRight) * hashRight + hashLeft;
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
	
	@Override
	public int compareTo(Object other) throws ClassCastException{
		// TODO Auto-generated method stub
		if(!(other instanceof Line)){
			throw new ClassCastException("A Line object expected.");
		}else{
			return nodes.compareTo(((Line)other).nodes);
		}
		
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Line other = (Line) obj;
		return this.nodes.equals(other.nodes);
	}
}
