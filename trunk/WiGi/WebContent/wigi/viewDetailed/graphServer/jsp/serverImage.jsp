
<%@ 
	page import="
	java.util.*, 
	java.text.*, 
	java.awt.*, 
	java.awt.image.*, 
	java.io.*, 
	javax.imageio.ImageIO" 
%>

<%!	//----------------------
	// DECLARATIONS
	//----------------------
	
%>

<%	//----------------------
	// MAIN
	//----------------------			
    int width = 600;
    int height = 600;        
        
    // fastest types found so far: TYPE_BYTE_INDEXED, TYPE_INT_RGB
     BufferedImage buffer = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_INDEXED);
    Graphics g = buffer.createGraphics();
    
    // background
    g.setColor(Color.white);
    g.fillRect(0,0,width,height);
        
    // lines
    g.setColor(Color.decode("#aaaaaa"));
    for (int i=0;i<100;i++)
		g.drawLine((int)(Math.random()*width),(int)(Math.random()*width),(int)(Math.random()*width),(int)(Math.random()*width));
	
	// - - - - - - - -
	// parameter passing
	if (request.getParameter("x1") != null)
	{		
		int x1 = (int)(Double.parseDouble(request.getParameter("x1")) * width);
		int y1 = (int)(Double.parseDouble(request.getParameter("y1")) * height);
		int x2 = (int)(Double.parseDouble(request.getParameter("x2")) * width);
		int y2 = (int)(Double.parseDouble(request.getParameter("y2")) * height);
		//out.print (x1);
		g.setColor(Color.decode("#ff00ff"));
		
		g.fillOval(x1-5, y1-5, 10, 10);
		g.drawLine(x1, y1, x2, y2);	
		g.fillOval(x2-5, y2-5, 10, 10);	
	}
	// - - - - - - - -
	
	// dots
	g.setColor(Color.blue);
	int x, y;
	for (int i=0;i<100;i++)
	{
		x = (int)(Math.random()*width);
		y = (int)(Math.random()*width);
		g.drawRect(x, y, 10, 10);			// find method to draw a dot
	}



/*
// REMOVE THIS  
// CLEAR IMAGE
	buffer = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_INDEXED);
	g = buffer.createGraphics();
	g.setColor(Color.white);
    g.fillRect(0,0,width,height);
*/




	//----------------------
    // output
    //----------------------
    response.setContentType("image/gif");
    ImageIO.write(buffer, "gif", response.getOutputStream());	// convert to byte output stream
    //os.close();
    
    //response.getWriter().write("asd");	// to try

	
%>