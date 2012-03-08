package net.wigis.graph.jgrapht;

import org.jgrapht.graph.DefaultEdge;

public class JGraphTEdge<T> extends DefaultEdge
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8721991919029651081L;

	@SuppressWarnings("unchecked")
	public T getSource()
	{
		return (T)super.getSource();
	}
	
	@SuppressWarnings("unchecked")
	public T getTarget()
	{
		return (T)super.getTarget();
	}
}
