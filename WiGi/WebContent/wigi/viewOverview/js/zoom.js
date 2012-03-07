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



// zoom box inside the viewOverview
var zoom;

//=======================================
// init
//=======================================
function init_zoom()
{
    zoom = document.getElementById("viewOverview:zoom");
    
    zoom.style.display = "block";
        
    // position - in the middle
    //zoom.style.left = viewOverview.x1 + width(viewOverview)/2 - width(zoom)/2;
    //zoom.style.top = viewOverview.y1 + height(viewOverview)/2 - height(zoom)/2;
    
    var wholeWidth = 200;
    var wholeHeight = 200;
    
    var minXelm = document.getElementById("minX");
    if( minXelm != null )
    {
    	minX = parseFloat(minXelm.value);
    }
    var maxXelm = document.getElementById("maxX");
    if( maxXelm != null )
    {
    	maxX = parseFloat(maxXelm.value);
    }
    var minYelm = document.getElementById("minY");
    if( minYelm != null )
    {
    	minY = parseFloat(minYelm.value);
    }
    var maxYelm = document.getElementById("maxY");
    if( maxYelm != null )
    {
    	maxY = parseFloat(maxYelm.value);
    }
    
    var left = Math.round(viewOverview.x1 + (minX * wholeWidth));
    var width = Math.round((maxX - minX) * wholeWidth);
    var top = Math.round(viewOverview.y1 + (minY * wholeHeight));
    var height = Math.round((maxY - minY) * wholeWidth);
    
    // position - whole image
    zoom.style.left = left + "px";   
    zoom.style.width = width + "px";
    zoom.style.top = top + "px";
    zoom.style.height = height + "px";
    
    // zoom - mouse
    mouseDown_zoom();
    doubleClick_zoom();
    //mouseUp_zoom();
    
   
}

//=======================================
// keep in window
//=======================================
function keepInWindow_zoom ()
{    
    // keep zoom window within image window
    x = Math.max(left(zoom), viewOverview.x1);
    x = Math.min(x, viewOverview.x2 - width(zoom));

    y = Math.max(top(zoom), viewOverview.y1);
    y = Math.min(y, viewOverview.y2 - height(zoom));

    zoom.style.left = x + "px";
    zoom.style.top = y + "px";   
    zoom.x = x;
    zoom.y = y;

    move_zoomCorners();
}

//=======================================
// mouse
//=======================================
function mouseDown_zoom()   // when u click in the zoom box
{
	zoom.onmousedown = function(ev)
	{	    
		dragObject = this;
		dragObject.type = 'zoom';
		
		if( updating )
		{
			updateStartStop();
		}

		// find where we start dragging
		zoom.mouseDownPos = getPosition(zoom, ev);
		//out( zoom.mouseDownPos.x + "," + zoom.mouseDownPos.y );
	}
}

function doubleClick_zoom()
{
	zoom.ondblclick = function(ev)
	{
	    // position - whole image
	    zoom.style.left = viewOverview.x1 + "px";   
	    zoom.style.width = 200 + "px";
	    zoom.style.top = viewOverview.y1 + "px";
	    zoom.style.height = 200 + "px";
	    
	    move_zoomCorners();

	    getImage_viewDetailedDefault();        
            
    	A4J.AJAX.Submit('_viewRoot','viewOverview',ev,{'ignoreDupResponses':true,'parameters':{'viewOverview:j_id16':'viewOverview:j_id16'} ,'eventsQueue':'viewOverview:j_id16','actionUrl': webPage} );
	}
}

function mouseMove_zoom (ev)
{           
    // position - keep relative to mouse
    zoom.style.left = (mousePos.x - zoom.mouseDownPos.x) + "px";
    zoom.style.top = (mousePos.y - zoom.mouseDownPos.y) + "px";
    
    keepInWindow_zoom();
    
    move_zoomCorners();
}

function mouseUp_zoom(event)
{
//	zoom.onmouseup = function(event)
//	{
	    //out(Math.random());
	    
	    //*************
        // image static - AJAX
        //*************
        getImage_viewDetailedDefault();
        
        A4J.AJAX.Submit('_viewRoot','viewOverview',event,{'ignoreDupResponses':true,'implicitEventsQueue':'viewOverview:j_id15','similarityGroupingId':'viewOverview:j_id15','parameters':{'viewOverview:j_id15':'viewOverview:j_id15'} ,'actionUrl': webPage} );
//		A4J.AJAX.Submit('_viewRoot','viewOverview',event,{'parameters':{'viewOverview:zoom':'viewOverview:zoom'} ,'actionUrl': webPath + 'WiGiViewerPanel.faces'} );
//        A4J.AJAX.Submit('_viewRoot','viewOverview',event,{'ignoreDupResponses':true,'parameters':{'viewOverview:j_id16':'viewOverview:j_id16'} ,'eventsQueue':'viewOverview:j_id16','actionUrl': webPath + 'WiGiViewerPanel.faces'} );
//        A4J.AJAX.Submit('j_id_jsp_1349198683_0','viewOverview',event,{'ignoreDupResponses':true,'parameters':{'viewOverview:j_id_jsp_1349198683_21':'viewOverview:j_id_jsp_1349198683_21'} ,'eventsQueue':'viewOverview:j_id_jsp_1349198683_21','actionUrl':'/WebGraph/faces/withZooming.jspx'} );
   
        //*************
//	}
}

function test1()
{
	//document.getElementById("Peak oil").value = "asd";
	alert("asd");
}



