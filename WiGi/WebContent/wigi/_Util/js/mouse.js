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



var TBMouse;

//=======================================
// mouse events - attach functions
//=======================================
document.onmousedown = mouseDown;
document.onmousemove = mouseMove;
document.onmouseup   = mouseUp;
document.ontouchstart = mouseDown;
document.ontouchend  = mouseUp;
document.ontouchmove = mouseMove;



//=======================================
// dragged object
//=======================================
var dragObject = null;     // object being dragged, get assigned on mouse down

//=======================================
// mouse
//=======================================

var mousePos;              // current position
var mouseDownPos = {x:0,y:0};   // where we clicked on image first (when we start dragging)

//-----------------------------
// main
//-----------------------------
function main_mouse()
{
    TBMouse = document.getElementById('MousePosition');
//	var mouseImg = document.createElement("img");
//	mouseImg.setAttribute("src", "viewDetailed/graphClient/images/circle_30.png");
//	mouseImg.setAttribute("id", "mouseImg");
//	document.body.appendChild(mouseImg);
}

//-----------------------------
// mouse coords
//-----------------------------
function mouseCoords(ev)
{
	var xAddition = 0;
	var yAddition = 0
/*
	if( BrowserDetect.browser == "Safari" || BrowserDetect.browser == "Chrome" )
	{
		var targ;
		if (!ev) var ev = window.event;
		if (ev.target) targ = ev.target;
		else if (ev.srcElement) targ = ev.srcElement;
		if (targ.nodeType == 3) // defeat Safari bug
			targ = targ.parentNode;

		xAddition = getElementX(targ);
		yAddition = getElementY(targ);
	}
*/
	
	return mouseCoords(ev,xAddition,yAddition);
}

function mouseCoords(ev, xAddition, yAddition )
{
	if( xAddition == null )
		xAddition = 0;
	if( yAddition == null )
		yAddition = 0;
	
    ev = ev || window.event;	// window.event - stores mouse events	// ev now contains the event object (not needed in Firefox)
  
	if (ev.pageX || ev.pageY)
	{
		return {x:ev.pageX + xAddition, y:ev.pageY + yAddition};
	}
	
	return {x:ev.clientX + document.body.scrollLeft - document.body.clientLeft + xAddition, y:ev.clientY + document.body.scrollTop  - document.body.clientTop + yAddition};
}

//-----------------------------
// mouse down
//-----------------------------
function mouseDown(ev)
{
    // find where we start dragging (mouse down)
//	mouseDownPos = mouseCoords(ev);
//	out( mouseDownPos.x + " , " + mouseDownPos.y );
}
		

function mouseMoveSVG(ev)
{
//	var embed = document.getElementById("embed_SVG");
	var viewDetailed = document.getElementById( "viewDetailed" );
	var xAddition = getElementX(viewDetailed);
	var yAddition = getElementY(viewDetailed);
	doMouseMove(ev,xAddition,yAddition);
}

function mouseUpSVG(ev)
{
	mouseUp(ev);
}

function mouseDownSVG(ev)
{
	if( dragObject != selectedNode )
	{
		deselectNode( selectedNode );
	}
}

//-----------------------------
// mouse move
//-----------------------------
function mouseMove(ev)
{
	doMouseMove( ev, 0, 0 );
}

function doMouseMove( ev, xAddition, yAddition )
{
	if( !testing )
		mousePos = mouseCoords(ev, xAddition, yAddition);
	
	// show mouse position
	if( TBMouse != null )
		TBMouse.value = mousePos.x + ", " + mousePos.y;
//	var mouseImg = document.getElementById("mouseImg");
//	mouseImg.style.cssText =  "position:absolute;left:" + mousePos.x + "px;top:" + mousePos.y + "px;";
//	mouseImg.style.position = "absolute";
//	mouseImg.style.top = mousePos.y + "px";
//	mouseImg.style.left = mousePos.x + "px";
	

	if(dragObject)  // if not null
	{
	    if (dragObject.type == 'node')
	        mouseMove_node(ev);
	    else if (dragObject.type == 'zoom')
	        mouseMove_zoom(ev);
	    else if (dragObject.type == 'viewDetailed')
	        mouseMove_viewDetailed(ev);
	    else if (dragObject.type == 'zoom_corner')
	        mouseMove_zoomCorner(ev);
	    else if (dragObject.type == 'zoom')
	    	mouseMove_zoom(ev);
	    /*//^^^^^^^^^^^^^^^    
	    else if (dragObject.type == 'nodeSVG')
	        mouseMove_nodeSVG(ev);
	    //^^^^^^^^^^^^^^^*/
	}
}

//-----------------------------
// mouse up
//-----------------------------
function mouseUp(ev)
{   
    //.. THIS WASNT NECESSARY BEFORE
    if(!dragObject) return;
    
    if (dragObject.type == 'node')
        mouseUp_node(ev);
    else if (dragObject.type == 'zoom_corner')
        mouseUp_zoomCorner(ev);
    else if (dragObject.type == 'zoom')
    	mouseUp_zoom(ev);
    else if (dragObject.type == 'viewDetailed')
    	mouseUp_viewDetailed(ev);


	dragObject = null;
}

//-----------------------------
// mouse wheel
//-----------------------------
function mouseWheel_add (element, fn)
{
    if (element.addEventListener)    // DOMMouseScroll is for mozilla
        element.addEventListener('DOMMouseScroll', fn, false);
    element.onmousewheel = fn;   // IE/Opera.
}

function mouseWheel_getDelta (event)
{
    var delta = 0;
    
    if (!event) /* For IE. */
        event = window.event;
        
    if (event.wheelDelta) /* IE/Opera. */
    { 
        delta = event.wheelDelta/120;
        /** In Opera 9, delta differs in sign as compared to IE.
         */
        if (window.opera)
                delta = -delta;
    } 
    else if (event.detail) /** Mozilla case. */
    { 
        /** In Mozilla, sign of delta is different than in IE.
         * Also, delta is multiple of 3.
         */
        delta = -event.detail/3;
    }
    /** If delta is nonzero, handle it.
     * Basically, delta is now positive if wheel was scrolled up,
     * and negative, if wheel was scrolled down.
     */
    //if (delta)
        //handle(delta);
    /** Prevent default actions caused by mouse wheel.
     * That might be ugly, but we handle scrolls somehow
     * anyway, so don't bother here..
     */
    if (event.preventDefault)
        event.preventDefault();
        
	return delta;
}

//-----------------------------