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

package net.wigis.web;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.wigis.graph.ImageRenderer;
import net.wigis.graph.PaintBean;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.utilities.Vector2D;

// TODO: Auto-generated Javadoc
/**
 * The Class NodePosServlet.
 * 
 * @author Brynjar Gretarsson
 */
public class NodePosServlet extends HttpServlet
{

	/** Logger. */
	// // private static Log logger = LogFactory.getLog( NodePosServlet.class );

	/**
	 * 
	 */
	private static final long serialVersionUID = -5400767179073247118L;

	/**
	 * Do get.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @throws ServletException
	 *             the servlet exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
	{
		setPosition( request, response );
	}

	/**
	 * Do post.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @throws ServletException
	 *             the servlet exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
	{
		setPosition( request, response );
	}

	/**
	 * The Class Node.
	 */
	private class Node
	{

		/** The id. */
		private int id;

		/** The x. */
		private double x;

		/** The y. */
		private double y;

		/** The selected. */
		private boolean selected = false;

		/**
		 * Instantiates a new node.
		 */
		public Node()
		{}

		/**
		 * Gets the id.
		 * 
		 * @return the id
		 */
		public int getId()
		{
			return id;
		}

		/**
		 * Sets the id.
		 * 
		 * @param id
		 *            the new id
		 */
		public void setId( int id )
		{
			this.id = id;
		}

		/**
		 * Gets the x.
		 * 
		 * @return the x
		 */
		public double getX()
		{
			return x;
		}

		/**
		 * Sets the x.
		 * 
		 * @param x
		 *            the new x
		 */
		public void setX( double x )
		{
			this.x = x;
		}

		/**
		 * Gets the y.
		 * 
		 * @return the y
		 */
		public double getY()
		{
			return y;
		}

		/**
		 * Sets the y.
		 * 
		 * @param y
		 *            the new y
		 */
		public void setY( double y )
		{
			this.y = y;
		}

		/**
		 * Checks if is selected.
		 * 
		 * @return true, if is selected
		 */
		public boolean isSelected()
		{
			return selected;
		}

		/**
		 * Sets the selected.
		 * 
		 * @param selected
		 *            the new selected
		 */
		public void setSelected( boolean selected )
		{
			this.selected = selected;
		}
	}

	/**
	 * Gets the index.
	 * 
	 * @param parameterName
	 *            the parameter name
	 * @return the index
	 */
	private int getIndex( String parameterName )
	{
		String indexStr = parameterName.substring( parameterName.indexOf( "_" ) + 1 );
		return Integer.parseInt( indexStr );
	}

	/**
	 * Sets the position.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void setPosition( HttpServletRequest request, HttpServletResponse response ) throws IOException
	{
		PaintBean pb = (PaintBean)ContextLookup.lookup( "paintBean", request );
		Enumeration<?> names = request.getParameterNames();
		HashMap<Integer, Node> nodes = new HashMap<Integer, Node>();
		Node tempNode;
		String parameterName;
		int index;
		int id = 0;
		double x = 0;
		double y = 0;
		double minX = 0;
		double maxX = 1;
		double minY = 0;
		double maxY = 1;
		boolean selected = false;
		boolean noOneSelected = true;

		while( names.hasMoreElements() )
		{
			parameterName = (String)names.nextElement();
			if( parameterName.equals( "minX" ) )
			{
				minX = ServletHelper.getDoubleParameter( "minX", request );
			}
			else if( parameterName.equals( "maxX" ) )
			{
				maxX = ServletHelper.getDoubleParameter( "maxX", request );
			}
			else if( parameterName.equals( "minY" ) )
			{
				minY = ServletHelper.getDoubleParameter( "minY", request );
			}
			else if( parameterName.equals( "maxY" ) )
			{
				maxY = ServletHelper.getDoubleParameter( "maxY", request );
			}
			else
			{
				index = getIndex( parameterName );
				tempNode = nodes.get( index );

				if( tempNode == null )
				{
					tempNode = new Node();
				}

				if( parameterName.startsWith( "id_" ) )
				{
					id = ServletHelper.getIntParameter( parameterName, request );
					tempNode.setId( id );
				}
				else if( parameterName.startsWith( "x_" ) )
				{
					x = ServletHelper.getDoubleParameter( parameterName, request );
					tempNode.setX( x );
				}
				else if( parameterName.startsWith( "y_" ) )
				{
					y = ServletHelper.getDoubleParameter( parameterName, request );
					tempNode.setY( y );
				}
				else if( parameterName.startsWith( "s_" ) )
				{
					selected = ServletHelper.getBooleanParameter( parameterName, request );
					tempNode.setSelected( selected );
				}

				nodes.put( index, tempNode );
			}
		}

		// int level = (int)pb.getLevel();
		DNVGraph graph = pb.getGraph();

		Vector2D nodeWorldPos;

		Iterator<Node> nodeIterator = nodes.values().iterator();
		while( nodeIterator.hasNext() )
		{
			tempNode = nodeIterator.next();
			x = tempNode.getX();
			y = tempNode.getY();
			id = tempNode.getId();
			selected = tempNode.isSelected();
			nodeWorldPos = ImageRenderer.transformScreenToWorld( x, y, minX, maxX, minY, maxY, pb.getGlobalMinX(), pb.getGlobalMaxX(), pb
					.getGlobalMinY(), pb.getGlobalMaxY(), pb.getWidth(), pb.getHeight() );
			DNVNode node = (DNVNode)graph.getNodeById( id );
			if( node != null )
			{
				Vector2D movement = ImageRenderer.getMovement( node, nodeWorldPos );
				node.moveRelatedNodes( movement, true, false );
				node.setPosition( nodeWorldPos );
				if( selected )
				{
					pb.setSelectedNode( node, false );
					noOneSelected = false;
				}
			}
			else
			{
				System.out.println( "Unable to find node with id=[" + id + "]" );
			}
		}

		if( noOneSelected )
		{
			pb.setSelectedNode( null, false );
		}

		OutputStreamWriter writer = new OutputStreamWriter( response.getOutputStream() );
		writer.write( "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
				+ "<html><div>Done!</div></html>" );
		writer.flush();
		writer.close();
	}

}
