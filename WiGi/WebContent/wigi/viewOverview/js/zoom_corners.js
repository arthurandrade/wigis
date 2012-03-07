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


var zoom_corner;

var zoom_corner_BR; // for resizing the zoom box
var zoom_corner_TR;
var zoom_corner_BL;
var zoom_corner_TL;

// =======================================
// init
// =======================================
function init_zoomCorners() {
	// top left
	/*
	 * TL = zoom_corner = document.createElement('div');
	 * zoom_corner.setAttribute('id', 'zoom_corner_TL');
	 * zoom_corner.setAttribute('class', 'zoom_corners');
	 * document.body.appendChild(zoom_corner);
	 * 
	 * mouseDown_zoomCorner(zoom_corner);
	 */
	zoom_corner_BR = document.getElementById("viewOverview:zoom_corner_BR");
	zoom_corner_BR.style.display = "block";

	zoom_corner_TR = document.getElementById("viewOverview:zoom_corner_TR");
	zoom_corner_TR.style.display = "block";
	
	zoom_corner_BL = document.getElementById("viewOverview:zoom_corner_BL");
	zoom_corner_BL.style.display = "block";
	
	zoom_corner_TL = document.getElementById("viewOverview:zoom_corner_TL");
	zoom_corner_TL.style.display = "block";

	// position
	move_zoomCorners();

	mouseDown_zoomCorner(zoom_corner_BR);
	mouseDown_zoomCorner(zoom_corner_TR);
	mouseDown_zoomCorner(zoom_corner_BL);
	mouseDown_zoomCorner(zoom_corner_TL);

	// mouseUp_zoomCorner(zoom_corner);
}

// =======================================
// move
// =======================================
function move_zoomCorners() {
	var w = 8; // should be the same as in zoom.css

	/*
	 * zoom_corner = document.getElementById('zoom_corner_TL'); left =
	 * removePX(zoom.style.left) - w/2; top = removePX(zoom.style.top) - w/2;
	 * zoom_corner.setAttribute('style', 'left:' + left + '; top:' + top + ';');
	 */

	zoom_corner_BR.style.left = (left(zoom) + width(zoom)) + "px";
	zoom_corner_BR.style.top = (top(zoom) + height(zoom)) + "px";

	zoom_corner_TR.style.left = (left(zoom) + width(zoom)) + "px";
	zoom_corner_TR.style.top = (top(zoom)) - w/2 + "px";
	
	zoom_corner_BL.style.left = (left(zoom)) - w/2 + "px";
	zoom_corner_BL.style.top = (top(zoom) + height(zoom)) + "px";
	
	zoom_corner_TL.style.left = (left(zoom)) - w/2 + "px";
	zoom_corner_TL.style.top = (top(zoom)) - w/2 + "px";
}

// =======================================
// mouse
// =======================================
function mouseDown_zoomCorner(zoom_corner) // when u click in the zoom box
{
	zoom_corner.onmousedown = function(ev) 
	{
		dragObject = this;
		dragObject.type = 'zoom_corner';

		if (updating) 
		{
			updateStartStop();
		}

		// find where we start dragging (mouse down)
		mouseDownPos = getPosition(zoom, ev);
		//out(mouseDownPos.x + " , " + mouseDownPos.y);
	}
}

var span;
function mouseMove_zoomCorner(ev) 
{
	if (dragObject == zoom_corner_BR)
	{
		span = Math.min(mousePos.x - left(zoom), mousePos.y - top(zoom));
		
		span = Math.min(span, viewOverview.x2 - left(zoom));
		span = Math.min(span, viewOverview.y2 - top(zoom));
	}
	else if (dragObject == zoom_corner_TR)
	{
		span = Math.min(mousePos.x - left(zoom), bottom(zoom) - mousePos.y);
		
		span = Math.min(span, viewOverview.x2 - left(zoom));
		span = Math.min(span, bottom(zoom) - viewOverview.y1);
		
		zoom.style.top = Math.max(mousePos.y, bottom(zoom) - span) + "px";
	}
	else if (dragObject == zoom_corner_BL)
	{
		span = Math.min(right(zoom) - mousePos.x, mousePos.y - top(zoom));
		
		span = Math.min(span, right(zoom) - viewOverview.x1);
		span = Math.min(span, viewOverview.y2 - top(zoom));
		
		zoom.style.left = Math.max(mousePos.x, right(zoom) - span) + "px";
	}
	else if (dragObject == zoom_corner_TL)
	{
		span = Math.min(right(zoom) - mousePos.x, bottom(zoom) - mousePos.y);		
		
		span = Math.min(span, right(zoom) - viewOverview.x1);
		span = Math.min(span, bottom(zoom) - viewOverview.y1);
		
		zoom.style.left = Math.max(mousePos.x, right(zoom) - span) + "px";
		zoom.style.top = Math.max(mousePos.y, bottom(zoom) - span) + "px";
	}	
	
	zoom.style.width = span + "px";
	zoom.style.height = span + "px";

	// keep zoom in window
	keepInWindow_zoom();

	move_zoomCorners();
}

function mouseUp_zoomCorner(event) // when u click in the zoom box
{
	// zoom_corner.onmouseup = function(event)
	// {
	// *************
	// image static - AJAX
	// *************
	getImage_viewDetailedDefault();

    A4J.AJAX.Submit('_viewRoot','viewOverview',event,{'ignoreDupResponses':true,'implicitEventsQueue':'viewOverview:j_id15','similarityGroupingId':'viewOverview:j_id15','parameters':{'viewOverview:j_id15':'viewOverview:j_id15'} ,'actionUrl': webPage} );
//	A4J.AJAX.Submit('_viewRoot','viewOverview',event,{'parameters':{'viewOverview:zoom':'viewOverview:zoom'} ,'actionUrl': webPath + 'WiGiViewerPanel.faces'} );
//	A4J.AJAX.Submit('_viewRoot','viewOverview',event,{'ignoreDupResponses':true,'parameters':{'viewOverview:j_id16':'viewOverview:j_id16'} ,'eventsQueue':'viewOverview:j_id16','actionUrl': webPath + 'WiGiViewerPanel.faces'} );
	// *************
	// }
}