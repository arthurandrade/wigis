/*===========================================================================================================

Copyright (c) 2010, University of California, Santa Barbara
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted
provided that the following conditions are met:

   * Redistributions of source code must retain the above copyright notice, this list of
conditions and the following disclaimer.
   * Redistributions in binary form must reproduce the above copyright notice, this list of
conditions and the following disclaimer in the documentation and/or other materials provided with
the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
=============================================================================================================*/



var viewOverview;

//=======================================
// main
//=======================================
function main_viewOverview()
{
    init_viewOverview();
    
    init_zoom();
    
    init_zoomCorners(); 
}

//=======================================
// init
//=======================================
function init_viewOverview()
{
    viewOverview = document.getElementById("viewOverview:viewOverview");
    
    // background image
    //viewOverview.style.backgroundImage = "url(serverImage/jsp/serverImage.jsp?a=" + new Date().getTime() + ")";
  
    
    // boundaries
    viewOverview.x1 = getElementX(viewOverview);
    viewOverview.x2 = viewOverview.x1 + width(viewOverview);
    viewOverview.y1 = getElementY(viewOverview);
    viewOverview.y2 = viewOverview.y1 + height(viewOverview);
    
    // img overview - clickable
    mouseDown_viewOverview();
}

//=======================================
// mouse
//=======================================
function mouseDown_viewOverview()
{	   
	viewOverview.onmousedown = function(ev)
	{   			
        // center at mouse cursor
//        zoom.style.left = (mousePos.x - width(zoom)/2.0) + "px";
//        zoom.style.top = (mousePos.y - height(zoom)/2.0) + "px";

        keepInWindow_zoom();
        
        move_zoomCorners();
	}
}