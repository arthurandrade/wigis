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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.wigis.graph.ImageRenderer;
import net.wigis.graph.PaintBean;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.utilities.Vector2D;
import net.wigis.graph.dnv.utilities.Vector3D;

// TODO: Auto-generated Javadoc
/**
 * The Class NodeIconServlet.
 * 
 * @author Brynjar Gretarsson
 */
public class NodeIconServlet extends HttpServlet
{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new node icon servlet.
	 * 
	 * @see HttpServlet#HttpServlet()
	 */
	public NodeIconServlet()
	{
		super();
	}

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
		drawNode( request, response );
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
		drawNode( request, response );
	}

	/**
	 * Draw node.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 */
	private void drawNode( HttpServletRequest request, HttpServletResponse response )
	{
		PaintBean pb = (PaintBean)ContextLookup.lookup( "paintBean", request );
		if( pb == null )
			return;

		int id = ServletHelper.getIntParameter( "id", request );
		DNVNode node = (DNVNode)pb.getGraph().getNodeById( id );
		if( node == null )
			return;

		float red = node.getColor().getX();
		float green = node.getColor().getY();
		float blue = node.getColor().getZ();
		String label = node.getLabel();

		Vector3D color = new Vector3D( red, green, blue );
		int size = ServletHelper.getIntParameter( "s", request );
		String icon = node.getIcon();
		boolean showIcons = true;
		if( icon == null || icon.equals( "" ) )
		{
			showIcons = false;
		}

		if( pb != null )
		{
			boolean showLabels = pb.isShowLabels() && pb.isCurvedLabels();
			int imageSize = size + 2;
			if( label == null || label.equals( "" ) )
			{
				showLabels = false;
				label = "";
			}
			else if( showLabels )
			{
				imageSize = getImageSize( label, size, pb );
			}

			BufferedImage img = new BufferedImage( imageSize, imageSize, BufferedImage.TYPE_INT_ARGB );
			Graphics g = img.createGraphics();
			Graphics2D g2d = (Graphics2D)g;
			g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

			// g.clearRect( 0, 0, size + 1, size + 1 );
			Vector2D pos = new Vector2D( imageSize / 2, imageSize / 2 );
			size /= node.getRadius();
			try
			{
				ImageRenderer.drawLabel( g2d, node, pos, size, label, pb.isShowLabels(), pb.isCurvedLabels(), pb.isOutlinedLabels(),
						pb.getLabelSize(), 1, 1, 1, pb.isScaleNodesOnZoom(), pb.isHighlightNeighbors(), pb.getMaxLabelLength(),
						pb.getCurvedLabelAngle(), pb.isDrawLabelBox(), pb.isBoldLabels(),
						ImageRenderer.highlightNode( pb.isHighlightNeighbors(), node ), null );
				ImageRenderer.drawNode( g2d, showIcons, node, icon, pos, color, size, ImageRenderer.CIRCLE, -1 );
				response.setContentType( "image/png" );
				ImageIO.write( img, "png", response.getOutputStream() );
			}
			catch( MalformedURLException e )
			{
				e.printStackTrace();
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * Gets the image size.
	 * 
	 * @param label
	 *            the label
	 * @param size
	 *            the size
	 * @param pb
	 *            the pb
	 * @return the image size
	 */
	private int getImageSize( String label, int size, PaintBean pb )
	{
		int imageSize;
		BufferedImage img = new BufferedImage( 1, 1, BufferedImage.TYPE_INT_ARGB );
		Graphics g = img.createGraphics();
		Graphics2D g2d = (Graphics2D)g;
		double labelSize = pb.getLabelSize() * size / 4.0;
		if( labelSize > ImageRenderer.MAX_FONT_SIZE )
		{
			labelSize = ImageRenderer.MAX_FONT_SIZE;
		}
		double diameter = size + labelSize;
		double angle = ImageRenderer.FULL_CIRCLE_DEGREES;
		while( angle > ImageRenderer.MAX_ANGLE )
		{
			angle = ImageRenderer.computeAngle( (int)pb.getLabelSize(), label, g2d, diameter, pb.getMaxLabelLength(), pb.isBoldLabels() );
			diameter++;
		}
		imageSize = (int)( diameter + labelSize * 2.0 ) + 4;
		return imageSize;
	}

}
