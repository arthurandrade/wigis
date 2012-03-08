package net.wigis.greg;

import net.wigis.graph.dnv.DNVNode;

public class ShortestPath {

	
	private DNVNode source;
	private DNVNode PreviousNode;
	private int distance;
	public DNVNode getSource() {
		return source;
	}
	public void setSource(DNVNode source) {
		this.source = source;
	}
	public DNVNode getPreviousNode() {
		return PreviousNode;
	}
	public void setPreviousNode(DNVNode previousNode) {
		PreviousNode = previousNode;
	}
	public int getDistance() {
		return distance;
	}
	public void setDistance(int distance) {
		this.distance = distance;
	}
	
	
}
