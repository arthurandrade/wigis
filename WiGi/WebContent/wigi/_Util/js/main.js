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

//=======================================
// Image Onload
//=======================================
function viewDetailed_onload(event)
{
	newImage = true;
    viewDetailedWidthOver2 = viewDetailed_img.width / 2;
    viewDetailedHeightOver2 = viewDetailed_img.height / 2;
	//out(viewDetailedWidthOver2 + "," + viewDetailedHeightOver2 );
    if( serverTimer.start > 0 )
    {
    	serverTimer.setEnd();
    	out( "SS: " + serverTimer.getLastSegment() + " , " + serverTimer.getAverageTime() );
    }

	if( refreshOverview )
	{
		viewOverview.style.backgroundImage = "url(" + webPath + "GraphServlet?width=200&height=200&overview=true&r=qual&time=" + new Date().getTime() + ")";
	}

	A4J.AJAX.Submit('_viewRoot','viewOverview',event,{'ignoreDupResponses':true,'implicitEventsQueue':'viewOverview:mouseUpSupport','similarityGroupingId':'viewOverview:mouseUpSupport','parameters':{'viewOverview:mouseUpSupport':'viewOverview:mouseUpSupport'} ,'actionUrl':webPage} );
//	A4J.AJAX.Submit('_viewRoot','viewOverview',event,{'ignoreDupResponses':true,'implicitEventsQueue':'viewOverview:j_id15','similarityGroupingId':'viewOverview:j_id15','parameters':{'viewOverview:j_id15':'viewOverview:j_id15'} ,'actionUrl':webPage} );
//	A4J.AJAX.Submit('_viewRoot',null,event,{'parameters':{'_id58':'_id58'} ,'actionUrl': webPath + 'WiGiViewer.faces?javax.portlet.faces.DirectLink=true'} );
//	A4J.AJAX.Submit('_viewRoot',null,event,{'parameters':{'_id58':'_id58'} ,'actionUrl': webPath + 'WiGiViewerPanel.faces?javax.portlet.faces.DirectLink=true'} );
//	A4J.AJAX.Submit('_viewRoot','viewOverview',event,{'ignoreDupResponses':true,'parameters':{'viewOverview:_id62':'viewOverview:_id62'} ,'eventsQueue':'viewOverview:_id62','actionUrl': webPath + 'WiGiViewerPanel.faces?javax.portlet.faces.DirectLink=true'} );
//	newImage = true;	
}


//=======================================
// MAIN
//=======================================
var webPath;
var webPage;
var newImage = true;
function main()
{
	main_out();
	main_labels();
	main_viewOverview();
	main_mouse();
	main_viewDetailed();
	init_zoom();
//	updateButtonText();
	getImage_viewDetailedDefault();        
	webPath = document.getElementById('settings:webPath').value;
	webPage = this.location.href;
	
	window.onload = function(event)
	{
		getImage_viewDetailedDefault();
	    
	    // refresh the javascript and graph info
//    	A4J.AJAX.Submit('_viewRoot','viewOverview',event,{'ignoreDupResponses':true,'parameters':{'viewOverview:_id62':'viewOverview:_id62'} ,'eventsQueue':'viewOverview:_id62','actionUrl': webPath + 'WiGiViewer.faces?javax.portlet.faces.DirectLink=true'} );
    	A4J.AJAX.Submit('_viewRoot','viewOverview',event,{'ignoreDupResponses':true,'parameters':{'viewOverview:_id62':'viewOverview:_id62'} ,'eventsQueue':'viewOverview:_id62','actionUrl': webPage} );
	};
	
    viewDetailed_img.onload = viewDetailed_onload;
    
    setEdgePositions();
    
    document.onselectstart = function() {return false;};
}