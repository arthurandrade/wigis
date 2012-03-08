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

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.wigis.graph.dnv.utilities.TextStroke;

// TODO: Auto-generated Javadoc
/**
 * Servlet implementation class GraphServlet.
 * 
 * @author Brynjar Gretarsson
 */
public class TextToImageServlet extends HttpServlet
{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new text to image servlet.
	 * 
	 * @see HttpServlet#HttpServlet()
	 */
	public TextToImageServlet()
	{
		super();
		// TODO Auto-generated constructor stub
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
		drawText( request, response );
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
		drawText( request, response );
	}

	/**
	 * Draw text.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 */
	private void drawText( HttpServletRequest request, HttpServletResponse response )
	{
		String text = request.getParameter( "text" );
		int width = 100;
		int height = 100;
		BufferedImage img = new BufferedImage( width, width, BufferedImage.TYPE_INT_RGB );
		Graphics2D g2D = img.createGraphics();
		try
		{
			Shape circle = new Ellipse2D.Double( width / 4.0, width / 4.0, width / 4.0 * 3.0, height / 4.0 * 3.0 );
			Stroke stroke = new TextStroke( text, new Font( "SansSerif", Font.PLAIN, 12 ) );
			g2D.setStroke( stroke );
			g2D.draw( circle );
			OutputStream out = response.getOutputStream();
			ImageIO.write( img, "gif", out );
			out.flush();
		}
		catch( IOException ioe )
		{
			// ioe.printStackTrace();
		}

	}
}
