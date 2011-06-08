/******************************************************************************************************
 * Copyright (c) 2010, University of California, Santa Barbara
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are 
 * permitted provided that the following conditions are met:
 * 
 *    * Redistributions of source code must retain the above copyright notice, this list of
 *      conditions and the following disclaimer.
 *    * Redistributions in binary form must reproduce the above copyright notice, this list of
 *      conditions and the following disclaimer in the documentation and/or other materials 
 *      provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 *****************************************************************************************************/

package net.wigis.graph.solr.faceted;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.wigis.graph.solr.SolrBean;

import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.common.SolrDocumentList;

import blackbook.service.api.ResourceDecorator;

// TODO: Auto-generated Javadoc
/**
 * This managed bean handles the faceted area.
 * 
 * @author Thomas Kulish
 * 
 * @todo
 */
public class FacetedBean extends SolrBean
{

	/** The expand search. */
	private boolean expandSearch = false;

	/** The facet info. */
	private String facetInfo = "";

	/** The categories. */
	private List<Category> categories = null;

	// These two variables are used for facet selection process on the
	// facetedSearch.xhtml
	/** The facet query. */
	private String facetQuery = null;

	/** The facet category. */
	private String facetCategory = null;

	// This variable is used to store the ID that is to be removed.
	/** The selected_faceted id. */
	private String selected_facetedID = null;

	/** The breadcrumb. */
	private List<String> breadcrumb = null; // Can a breadcumb LIST just put
	// pushed back as a String with the
	// get function?

	/** The current_selected_facet_field. */
	private String current_selected_facet_field = null;

	// testing the ability to render information
	/** The display facet info. */
	private boolean displayFacetInfo = false;

	/**
	 * Gets the categories.
	 * 
	 * @return the categories
	 */
	public List<Category> getCategories()
	{
		if( categories != null )
		{
			return categories;
		}
		else
		{
			return null;
		}
	}

	/**
	 * Checks if is display facet info.
	 * 
	 * @return true, if is display facet info
	 */
	public boolean isDisplayFacetInfo()
	{
		return displayFacetInfo;
	}

	/**
	 * Sets the display facet info.
	 * 
	 * @param displayFacetInfo
	 *            the new display facet info
	 */
	public void setDisplayFacetInfo( boolean displayFacetInfo )
	{
		this.displayFacetInfo = displayFacetInfo;
	}

	/**
	 * Checks if is expand search.
	 * 
	 * @return true, if is expand search
	 */
	public boolean isExpandSearch()
	{
		return expandSearch;
	}

	/**
	 * Sets the expand search.
	 * 
	 * @param expandSearch
	 *            the new expand search
	 */
	public void setExpandSearch( final boolean expandSearch )
	{
		if( this.expandSearch != expandSearch )
		{
			this.expandSearch = expandSearch;

		}
	}

	/**
	 * Sets the selected_ faceted id.
	 * 
	 * @param selected_facetedID
	 *            the new selected_ faceted id
	 */
	public void setSelected_FacetedID( String selected_facetedID )
	{
		this.selected_facetedID = selected_facetedID;
	}

	/**
	 * Gets the selected_ faceted id.
	 * 
	 * @return the selected_ faceted id
	 */
	public String getSelected_FacetedID()
	{
		return selected_facetedID;
	}

	/**
	 * Gets the breadcrumb.
	 * 
	 * @return the breadcrumb
	 */
	public List<String> getBreadcrumb()
	{
		if( breadcrumb == null )
		{
			return null;
		}
		else
		{
			return breadcrumb;
		}
	}

	/**
	 * Sets the facet category.
	 * 
	 * @param facetCategory
	 *            the new facet category
	 */
	public void setFacetCategory( final String facetCategory )
	{
		this.facetCategory = facetCategory;
	}

	/**
	 * Gets the facet category.
	 * 
	 * @return the facet category
	 */
	public String getFacetCategory()
	{
		return facetCategory;
	}

	/**
	 * Sets the facet query.
	 * 
	 * @param facetQuery
	 *            the new facet query
	 */
	public void setFacetQuery( final String facetQuery )
	{
		this.facetQuery = facetQuery;
	}

	/**
	 * Gets the facet query.
	 * 
	 * @return the facet query
	 */
	public String getFacetQuery()
	{
		return this.facetQuery;
	}

	/**
	 * Sets the facet info.
	 * 
	 * @param facetInfo
	 *            the new facet info
	 */
	public void setFacetInfo( final String facetInfo )
	{
		this.facetInfo = facetInfo;
	}

	/**
	 * Gets the facet info.
	 * 
	 * @return the facet info
	 */
	public String getFacetInfo()
	{
		return this.facetInfo;
	}

	/**
	 * Expand search.
	 */
	public void expandSearch()
	{
		setExpandSearch( true );
	}

	/**
	 * Collapse search.
	 */
	public void collapseSearch()
	{
		setExpandSearch( false );
	}

	/**
	 * add_item_to_breadcrumb Adds the current item to the breadcrumb.
	 * 
	 * @param facetQuery
	 *            the facet query
	 */
	private void add_item_to_breadcrumb( String facetQuery )
	{
		System.err.println( "\n\n\nadd_item_to_breadcrumb" );
		if( breadcrumb == null )
		{
			breadcrumb = new ArrayList<String>();
			current_selected_facet_field = facetQuery;
		}

		breadcrumb.add( facetQuery );
	}

	/**
	 * getCurrentQuery Returns the Solr Query from the current breadcrumb list.
	 * 
	 * @return the current query
	 */
	private String getCurrentQuery()
	{
		String str_query = null;
		if( breadcrumb != null && !breadcrumb.isEmpty() )
		{
			for( String facet : breadcrumb )
			{
				if( str_query == null )
				{
					str_query = facet;
				}
				else
				{
					str_query += " AND " + facet;
				}
			}
			return str_query;
		}
		else
		{
			return "*:*";
		}

	}

	/**
	 * remove_breadcrumb() This functions removes a item from the breadcrumb
	 * based on the selected_facetedID, redraws the faceted area.
	 * 
	 */
	public void remove_breadcrumb()
	{
		// The ID (name) is stored in selected_facetedID
		breadcrumb.remove( selected_facetedID );
		redraw_facet_list();
	}

	/**
	 * add_facet_to_list Takes the current facetCategory and facetQuery selected
	 * from the faceted area, adds them to the breadcrumb, redraws the area.
	 * 
	 * 
	 */
	public void add_facet_to_list()
	{
		add_item_to_breadcrumb( facetCategory + ":\"" + facetQuery + "\"" );
		redraw_facet_list();
	}

	/**
	 * redraw_facet_list Redraws the current facet list. It uses the breadcrumb
	 * area to decide where to get the select statements from.
	 * 
	 */
	public void redraw_facet_list()
	{
		try
		{
			System.err.println( "Redraw the Facet list with this Selection" );
			String facetQ = "";

			// FacetQ should be built from the breadcrumb...
			// facetQ = facetCategory + ":" + facetQuery;
			facetQ = getCurrentQuery();
			SolrDocumentList templist = server.query( facetQ );

			// find all the facet field names
			store_facet_field_names( templist );

			Map<String, ResourceDecorator> solr_map = create_map( templist );
			loadGraph( solr_map );

			// active_facet_field_names = new HashSet<String>();

			// active_facet_field_names.add(facetQuery);
			List<FacetField> faceted_fields = server.faceted_query( facetQ, active_facet_field_names );

			setFacetListC( faceted_fields, true );

		}
		catch( Exception ie )
		{
			ie.printStackTrace();
		}
	}

	/**
	 * selectFacetList
	 * 
	 * THIS FUNCTION IS NOT IN USE.
	 */
	public void selectFacetList()
	{
		try
		{

			String facetQ = "";
			boolean main_selection = false;
			displayFacetInfo = true;
			System.err.println( "selectFacetList called" );
			System.err.println( "Dive down into: " + facetQuery );

			if( current_selected_facet_field == null || current_selected_facet_field == "" )
			{
				main_selection = true;
			}

			add_item_to_breadcrumb( facetQuery );

			// Build the Facet Query
			// TODO: Need to make it so when you dive down ON the more dive down
			// I.E. City --> Annapolis --> It needs to display those 2.
			//		

			// Take the Dive Down and add it to the Query
			// Query Solr, add items to graph
			if( main_selection )
			{
				facetQ = facetQuery + ":*";
			}
			else
			{
				facetQ = current_selected_facet_field + ":" + facetQuery;
			}

			SolrDocumentList templist = server.query( facetQ );

			// find all the facet field names
			store_facet_field_names( templist );

			Map<String, ResourceDecorator> solr_map = create_map( templist );
			loadGraph( solr_map );

			active_facet_field_names = new HashSet<String>();

			active_facet_field_names.add( facetQuery );
			List<FacetField> faceted_fields = server.faceted_query( facetQ, active_facet_field_names );

			setFacetListC( faceted_fields, true );

		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}

	/**
	 * Creates the Categories AND adds the facets to those categories.
	 * 
	 * @param facet_list
	 *            A List with facet fields that will be set to the categories
	 *            get_facet_data Tells the setFacetList Whether to set the data
	 *            or return the field name.
	 * @param get_facet_data
	 *            the get_facet_data
	 */
	public void setFacetListC( List<FacetField> facet_list, Boolean get_facet_data )
	{
		try
		{
			System.err.println( "setFacetListC: Clear Category" );
			categories = new ArrayList<Category>();

			displayFacetInfo = true;
			Iterator<FacetField> it = facet_list.iterator();

			while( it.hasNext() )
			{
				FacetField tempfield = it.next();
				System.err.println( "Facet Field Name: " + tempfield.getName() );
				List<FacetField.Count> facet_field_count = tempfield.getValues();
				Iterator<FacetField.Count> itc = facet_field_count.iterator();

				Category new_category = new Category( tempfield.getName() );
				while( itc.hasNext() )
				{
					FacetField.Count item = itc.next();
					System.err.println( item.getName() + " = " + item.getCount() );
					if( get_facet_data )
					{
						Facet newFacet = new Facet( item.getName(), (int)item.getCount() );
						new_category.addFacet( newFacet );
					}
				}
				categories.add( new_category );
			}
		}
		catch( Exception ie )
		{
			ie.printStackTrace();
		}
	}

	/**
	 * Sets the FacetList to the mainSubject Area of the Faceted Drawing.
	 * 
	 * @param facet_list
	 *            A List with facet fields that will be set on the mainSubject
	 *            for display. get_facet_data Tells the setFacetList Whether to
	 *            set the data or return the field name.
	 */
	/*
	 * public void setFacetList(List<FacetField> facet_list, Boolean
	 * get_facet_data) { try {
	 * System.err.println("setFacetList: Clear mainSubject"); mainSubject = new
	 * ArrayList<Facet>();
	 * 
	 * displayFacetInfo = true; Iterator<FacetField> it = facet_list.iterator();
	 * 
	 * while(it.hasNext()) { FacetField tempfield = it.next();
	 * System.err.println("Facet Field Name: " + tempfield.getName());
	 * List<FacetField.Count> facet_field_count = tempfield.getValues();
	 * Iterator<FacetField.Count> itc = facet_field_count.iterator();
	 * 
	 * while(itc.hasNext()) { FacetField.Count item = itc.next();
	 * System.err.println(item.getName() + " = " + item.getCount());
	 * if(get_facet_data) { Facet newFacet = new Facet(item.getName(),
	 * (int)item.getCount()); mainSubject.add(newFacet); } } if(!get_facet_data)
	 * { Facet newFacet = new Facet(tempfield.getName(),
	 * facet_field_count.size()); mainSubject.add(newFacet); } }
	 * 
	 * } catch (Exception e) { e.printStackTrace(); } }
	 */

	/*
	 * public void setFacetList(List<FacetField> facet_list) {
	 * setFacetList(facet_list, false); }
	 */
}
