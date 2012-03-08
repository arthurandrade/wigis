package net.wigis.graph.dnv.interaction.helpers;

import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.utilities.Vector2D;

public class InteractionFunctions
{
	/**
	 * Move node.
	 * 
	 * @param node
	 *            the node
	 * @param movement
	 *            the movement
	 */
	public static void moveNode( DNVNode node, Vector2D movement )
	{
		// Perform the movement
		node.moveRelatedNodes( movement, true, true );
		node.move( movement, true, true );
	}

}
