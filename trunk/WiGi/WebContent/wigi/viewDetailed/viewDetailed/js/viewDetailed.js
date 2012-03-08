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



var viewDetailed;
var viewDetailed_img;
// var divGraphics;
// =======================================
// main
// =======================================
function main_viewDetailed()
{
    viewDetailed = document.getElementById("viewDetailed");
    viewDetailed_img = document.getElementById("viewDetailed_img");   
        
    // mouse
    mouseDown_viewDetailed();
// mouseUp_viewDetailed();
    
    mouseDown_viewDetailed_img();
    
    mouseWheel_add(viewDetailed, mouseWheel_viewDetailed);
    
    // *************
    // div graphics
    // *************
    // divGraphics = new jsGraphics("viewDetailed");
    
    // *************
    
    // Initialize timer
    resetServerTimer();
    
    // *************
    // AJAX
    // *************
// getImage_viewDetailed(
// 0,
// width(viewOverview),
// 0,
// height(viewOverview)
// );
   
    // *************
}

//=======================================
// get image
// =======================================
function getImage_viewDetailedDefault()
{
    getImage_viewDetailed(
	    	left(zoom) - getElementX(viewOverview),
	        top(zoom) - getElementY(viewOverview),
	        left(zoom) - getElementX(viewOverview) + width(zoom),
	        top(zoom) - getElementY(viewOverview) + height(zoom));
}
var minX = 0;
var minY = 0;
var maxX = 1;
var maxY = 1;
var rendering = "qual";
function getImage_viewDetailed(x1, y1, x2, y2, mouseDownX, mouseDownY, mouseUpX, mouseUpY, sameNode, released)
{        
    serverTimer.setStart();
	minX = x1/width(viewOverview);
	minY = y1/height(viewOverview);
	maxX = x2/width(viewOverview);
	maxY = y2/height(viewOverview);
	
//	alert( mouseDownX + ":" + mouseDownY + " - " + mouseUpX + ":" + mouseUpY );

	if( minX >= 0 && minX <= 1 )
	{
	    var minXelm = document.getElementById("minX");
	    if( minXelm != null )
	    {
	    	minXelm.value = minX;
	    }
	    var maxXelm = document.getElementById("maxX");
	    if( maxXelm != null )
	    {
	    	maxXelm.value = maxX;
	    }
	    var minYelm = document.getElementById("minY");
	    if( minYelm != null )
	    {
	    	minYelm.value = minY;
	    }
	    var maxYelm = document.getElementById("maxY");
	    if( maxYelm != null )
	    {
	    	maxYelm.value = maxY;
	    }
	}
	
	var imgSrc = webPath + "GraphServlet?a=" + new Date().getTime()
    			+ "&minX=" + minX    // all values 0 to 1
    			+ "&minY=" + minY
    			+ "&maxX=" + maxX
    			+ "&maxY=" + maxY
    			+ "&r=" + rendering
    			;
	
	// if interaction with static image
	if (mouseDownX)
	{
		imgSrc += "&mouseDownX=" + mouseDownX
				+ "&mouseDownY=" + mouseDownY
				+ "&mouseUpX=" + mouseUpX
				+ "&mouseUpY=" + mouseUpY
				;
		if( sameNode )
		{
			imgSrc += "&sameNode=true";
		}
		
		imgSrc += "&ctrlPressed=" + ctrl_pressed;
		imgSrc += "&released=" + released;
	}
	
//	alert( imgSrc );
	
	viewDetailed_img.src = imgSrc;
}

//=======================================
// mouse
// =======================================
function mouseDown_viewDetailed_img()
{
    viewDetailed_img.onmousedown = function(ev)
    {
    	// disable image drag in mozilla
    	ev.preventDefault();
    }
}

var sameNode = false;
var refreshOverview = true;
var buttonDown = false;
function mouseDown_viewDetailed()
{
    viewDetailed.onmousedown = function(ev)
    {
    	mouseDown_detailed( this, false, ev );
    };
}

var ctrl_pressed = false;
function mouseDown_detailed( item, sNode, ev )
{
	buttonDown = true;
    dragObject = item;
    dragObject.type = 'viewDetailed';
    newImage = true;

    mouseDownPos = mousePos;
    sameNode = sNode;
    refreshOverview = false;
    ctrl_pressed = isCtrlPressed( ev );

    resetServerTimer();
    
    rendering = "speed";
    
    if( dragObject != selectedNode )
		deselectNode( selectedNode );	
}

function isCtrlPressed(e)
{
	 var ctrlPressed=0;

	 if (parseInt(navigator.appVersion)>3) {

		 var evt = navigator.appName=="Netscape" ? e:event;

		 if (navigator.appName=="Netscape" && parseInt(navigator.appVersion)==4) 
		 {
			 // NETSCAPE 4 CODE
			 var mString =(e.modifiers+32).toString(2).substring(3,6);
			 ctrlPressed =(mString.charAt(1)=="1");
		 }
		 else 
		 {
			 // NEWER BROWSERS [CROSS-PLATFORM]
			 if( BrowserDetect.OS == "Mac" )
			 {
				 ctrlPressed = evt.metaKey;
			 }
			 else
			 {
				 ctrlPressed = evt.ctrlKey;
			 }
		 }
		 if (ctrlPressed) 
		 {
			 return true;
		 }
	 }
	 
	 return false;
}

var serverTimer = new Timer();
function resetServerTimer()
{
	serverTimer = new Timer();
}

function getNewImage()
{
	getImage_viewDetailedDefault();
}

function mouseMove_viewDetailed(ev)
{
    //*************
    // AJAX
    // *************
	if( newImage )
	{
	    newImage = false;
	    
//	    alert( "MouseMove" );

	    getImage_viewDetailed(
	    	left(zoom) - getElementX(viewOverview),
	        top(zoom) - getElementY(viewOverview),
	        left(zoom) - getElementX(viewOverview) + width(zoom),
	        top(zoom) - getElementY(viewOverview) + height(zoom),	
	    		
	        (mouseDownPos.x - getElementX(viewDetailed)),
	        (mouseDownPos.y - getElementY(viewDetailed)),
	        (mousePos.x - getElementX(viewDetailed)),
	        (mousePos.y - getElementY(viewDetailed)),
	        sameNode,
	        false
	    );
	    
//	    alert( "MouseMove" );
	    mouseDownPos = mousePos;

	    // *************
	    // div graphics
	    // *************
// divGraphics.clear();
// divGraphics.setColor("#f00");
// divGraphics.drawLine(mouseDownPos.x,mouseDownPos.y,mousePos.x,mousePos.y);
// divGraphics.paint();
	    
	    // *************
	}
    //*************
    

    sameNode = true;
}

function mouseUp_viewDetailed()
{
//    viewDetailed.onmouseup = function (ev)
// {
		buttonDown = false;
    	refreshOverview = true;
        // *************
        // div graphics
        // *************
        // divGraphics.clear();
    
        // *************
        // AJAX
        // *************
	    serverTimer.setStart();
	    rendering = "qual";
        getImage_viewDetailed(
        	left(zoom) - getElementX(viewOverview),
            top(zoom) - getElementY(viewOverview),
            left(zoom) - getElementX(viewOverview) + width(zoom),
            top(zoom) - getElementY(viewOverview) + height(zoom),	
        		
            (mouseDownPos.x - getElementX(viewDetailed)),
            (mouseDownPos.y - getElementY(viewDetailed)),
            (mousePos.x - getElementX(viewDetailed)),
            (mousePos.y - getElementY(viewDetailed)),
            sameNode,
            true
        );
        // *************
// }
}

// continuos zoom
var newL, oldL, newW, oldW;
var newT, oldT, newH, oldH;

var inc = 1;        // first increment
var incMax = 10;    // how many interpolation points
var wheelEvent;
function mouseWheel_viewDetailed (event)
{	
	delta = mouseWheel_getDelta (event);
	
	// zoom in or out
	var zoomOut = (delta < 0) ? 1:-1;
	
	if( buttonDown )
	{
		// Change the interpolation method parameter
		var numberAffected = document.getElementById("interactionSettingsForm:numberAffectedInput");
		var theValue = parseInt( numberAffected.value );
		if( theValue > 0 || zoomOut == 1 )
		{
			numberAffected.value = theValue + zoomOut;
			A4J.AJAX.Submit('_viewRoot','interactionSettingsForm',event,{'similarityGroupingId':'interactionSettingsForm:j_id311','parameters':{'interactionSettingsForm:j_id311':'interactionSettingsForm:j_id311'} ,'actionUrl':webPage} );
			wheelEvent = event;
			setTimeout( "sameNode = false;mouseMove_viewDetailed(wheelEvent);", 200 );
		}
	}
	else
	{
		// restrict zooming in to this many pixels
		var zoomWidthMin = 10;
		if (zoomOut == -1 && width(zoom) < zoomWidthMin)
		   	return;
		
		viewDetailed_img.initL = left(viewDetailed_img);
	    viewDetailed_img.initW = viewDetailed_img.width;
	    viewDetailed_img.initT = top(viewDetailed_img);
	    viewDetailed_img.initH = viewDetailed_img.height;
	    
	    // send signal to server
	    // get new data as soon as u can, before continuous zoom
	    
	    // -----------------
	    // zoom resize
	    // -----------------         
	    var offset;    // zoom changes by this many pixels
	    if (zoomOut == 1)
	    	offset = width(zoom)/3;	// zoom out by 1/3
	    else
	    	offset = width(zoom)/4;	// zoom in by 1/4 (proportional to 1/3 zoom out)
	    
	    var percent;
	    if (width(zoom) + zoomOut*offset <= width(viewOverview)
	     && width(zoom) + zoomOut*offset > 0)
	    {
	        // zoom like in google maps, depends on where the mouse is at the time of the mouse wheel scroll
	        var mouseX = mousePos.x;    // get it here to keep it fixed
	        percent = (mouseX - getElementX(viewDetailed_img)) / viewDetailed_img.width;
	        
	        var zoomOldW = width(zoom);
	        var zoomNewW = width(zoom) + zoomOut*offset;
	        var zoomOldL = left(zoom);
	        var zoomNewL = (percent*width(zoom) + left(zoom)) - percent*zoomNewW;
	        zoom.style.left = zoomNewL + "px";
	        zoom.style.width = zoomNewW + "px";      
	        
	        var mouseY = mousePos.y;
	        percent = (mouseY - getElementY(viewDetailed_img)) / viewDetailed_img.height;
	        var zoomOldH = height(zoom);
	        var zoomNewH = height(zoom) + zoomOut*offset;
	        var zoomOldT = top(zoom);
	        var zoomNewT = (percent*height(zoom) + top(zoom)) - percent*zoomNewH;
	        zoom.style.top = zoomNewT + "px";
	        zoom.style.height = zoomNewH + "px";
	    }
	    
	    keepInWindow_zoom();
	    
	    // zoom corners
	    move_zoomCorners();
	    
	    /*
	    // -----------------
	    // continuous zoom
	    // -----------------
	    oldL = left(viewDetailed_img);
	    oldW = viewDetailed_img.width;
	    
	    newW = oldW * zoomOldW / zoomNewW;
	    newL =  (zoomOldL - zoomNewL) / zoomOldW * newW;
	        
	    oldT = top(viewDetailed_img);
	    oldH = viewDetailed_img.height;
	    
	    newH = oldH * zoomOldH / zoomNewH;
	    newT =  (zoomOldT - zoomNewT) / zoomOldH * newH;
	    
	    // animate - recursive
	    // inc = 1;
	    // setTimeout(continuousZoomAnimation,0);
	    */
	    
	    // instead of continous zoom just get last image
	    getImage_viewDetailed(
	            left(zoom) - getElementX(viewOverview),
	            top(zoom) - getElementY(viewOverview),
	            left(zoom) - getElementX(viewOverview) + width(zoom),
	            top(zoom) - getElementY(viewOverview) + height(zoom)
	        );
	}
}

/*
function continuousZoomAnimation ()
{
    // interpolate between new and old values
    viewDetailed_img.style.left = (oldL + (inc/incMax)*(newL-oldL)) + "px";
    viewDetailed_img.style.width = (oldW + (inc/incMax)*(newW-oldW)) + "px";    
    viewDetailed_img.style.top = (oldT + (inc/incMax)*(newT-oldT)) + "px";
    viewDetailed_img.style.height = (oldW + (inc/incMax)*(newH-oldH)) + "px"; 
    
    inc += 1;
    
    if (inc > incMax)
    {
        continuousZoom_postback();
        return;
    }
    
    setTimeout(continuousZoomAnimation,0);
}

function continuousZoom_postback ()
{
    // get new image
    // *************
    // AJAX
    // *************
    getImage_viewDetailedDefault(); 

    // *************
    
    // restore image container position
    viewDetailed_img.style.left = viewDetailed_img.initL + "px";
    viewDetailed_img.style.width = viewDetailed_img.initW + "px";
    viewDetailed_img.style.top = viewDetailed_img.initT + "px";
    viewDetailed_img.style.height = viewDetailed_img.initH + "px"; 
}
*/
var continueImageReload = false;
function startImageReload()
{
	continueImageReload = true;
	newImage = true;
	rendering = "speed";
	continuousImageReload();
}

function stopImageReload()
{
	continueImageReload = false;
	refreshOverview = true;
	rendering = "qual";
	keepInWindow_zoom();
	getNewImage();
}

function continuousImageReload()
{
	if( newImage )
	{
		newImage = false;
		refreshOverview = false;
		getImage_viewDetailedDefault();
	}
	
	if( continueImageReload )
		setTimeout( "continuousImageReload()", 0 );
}