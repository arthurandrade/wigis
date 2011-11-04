package net.wigis.greg;

import net.wigis.graph.dnv.DNVNode;

public class NextNode {

	
	//Variables
	private	DNVNode node;
	private	int weight;
	
	/**
	 * Constructor method
	 * @param n, the DNVNode
	 * @param w, the node weight
	 */
	public NextNode(DNVNode n, int w)
	{
		setNode(n);
		setWeight(w);
	}

	/**
	 * Node getter
	 * @return node
	 */
	public DNVNode getNode() {
		return node;
	}

	/**
	 * Node setter
	 * @param node
	 */
	public void setNode(DNVNode node) {
		this.node = node;
	}

	/**
	 * Weight getter
	 * @return weight
	 */
	public int getWeight() {
		return weight;
	}
	
	/**
	 * Weight setter
	 * @param weight
	 */
	public void setWeight(int weight) {
		this.weight = weight;
	}
}
