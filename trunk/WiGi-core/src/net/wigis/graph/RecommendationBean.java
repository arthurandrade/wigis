package net.wigis.graph;

import net.wigis.graph.dnv.DNVNode;

public interface RecommendationBean
{
	/** The Constant USER. */
	public static final String USER = "user";

	/** The Constant FRIEND. */
	public static final String FRIEND = "friend";

	/** The Constant MUSIC. */
	public static final String MUSIC = "music";

	/** The Constant BOOKS. */
	public static final String BOOKS = "books";

	/** The Constant MOVIES. */
	public static final String MOVIES = "movies";

	/** The Constant ITEM_TYPES. */
	public static final String[] ITEM_TYPES = { MUSIC, BOOKS, MOVIES };

	public abstract boolean isUsePearson();
	
	public abstract void removeFromUserProfile( DNVNode node, boolean runLayout );
	public abstract void addToUserProfile( DNVNode node, boolean runLayout );
}
