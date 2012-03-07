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



webPage = this.location.href;
//webPage = webPage.substring( webPage.lastIndexOf( "/" ) + 1 );
/******************************************
 * Code to generate the nodes, edges, etc.
 *****************************************/
var selectedNode;
function addNode( x, y, radius, realId, id, src, label, showLabel, selected )
{
	var jsGraph = document.getElementById( "jsGraph" );
	var newImage = document.createElement( "img" );
	var viewDetailed = document.getElementById( "viewDetailed" );
	var parentX = getElementX( viewDetailed );
	var parentY = getElementY( viewDetailed );
	newImage.setAttribute( "src", src );
	var xpos = (x+parentX);
	var ypos = (y+parentY);
	newImage.setAttribute( "realId", realId );
	newImage.setAttribute( "id", "INode" + id );
	newImage.setAttribute( "radius", radius );
	newImage.setAttribute( "x", xpos);
	newImage.setAttribute( "y", ypos);
	newImage.setAttribute( "style", "position:absolute;left:" + xpos + "px;top:" + ypos + "px;z-index:100" );
//	newImage.setAttribute( "label", label );
	newImage.setAttribute( "alt", label );
	newImage.setAttribute( "title", label );
	if( selected )
	{
		selectedNode = newImage;
	}
	jsGraph.appendChild( newImage );
	keepInWindow(xpos, ypos, newImage, newImage.width, newImage.height, false);
	
	// label
	if( showLabel )
	{
		label_initLabel(newImage);
	}
}

function addEdge( id, from, to, length )
{
	var jsGraph = document.getElementById( "jsGraph" );
	var newEdge = document.createElement( "div" );
	newEdge.id = "IEdge" + id;
	newEdge.style.display = "none";
	newEdge.n1 = from;
	newEdge.n2 = to;
	newEdge.length = length;
	jsGraph.appendChild( newEdge );
}

var updateNodePos;
var updateNodePosComplete = true;
function createNodePosUpdater()
{
	if( updateNodePos == null )
	{
		try 
		{
			updateNodePos = new XMLHttpRequest();
		} 
		catch (e) 
		{
			try 
			{
				updateNodePos = new ActiveXObject('Msxml2.XMLHTTP');
			} 
			catch (e)
			{
				try 
				{
					updateNodePos = new ActiveXObject('Microsoft.XMLHTTP');
				} 
				catch (e)
				{
					alert('XMLHttpRequest not supported'); 
				}
			}
		}
	}
}

function state_Change()
{
	if (updateNodePos.readyState==4)
	{// 4 = "loaded"
//		if (updateNodePos.status==200)
//		{// 200 = OK
			updateNodePosComplete = true;
			viewOverview.style.backgroundImage = "url(" + webPath + "GraphServlet?width=200&height=200&overview=true&r=qual&time=" + new Date().getTime() + ")";
			updateNodePos.onreadystatechange=null;
			A4J.AJAX.Submit();
//		}
//		else
//		{
//			alert("Problem retrieving XML data");
//		}
	}
}


var embedLoaded = false;
var graphLoaded = false;
function createEmbedSVG( width, height, webPath )
{
	var jsGraph = document.getElementById( "embedPanel" );
	var embed = document.createElement( "embed" );
	var viewDetailed = document.getElementById( "viewDetailed" );
	var viewX = getElementX( viewDetailed );
	var viewY = getElementY( viewDetailed );
	embed.setAttribute( "id", "embed_SVG" );
	embed.setAttribute( "src", webPath + "viewDetailed/graphClient/svg/svg.svg" );
//	embed.setAttribute( "src", "svg.jsp?numberOfLines=" + nEdges );
	embed.setAttribute( "width", width + "px" );
	embed.setAttribute( "height", height + "px" );
	embed.setAttribute( "style", "position:absolute; -moz-user-select: none;left:" + viewX + "px;top:" + viewY + "px;width:" + width + "px;height:" + height + "px;" );
	embed.setAttribute( "type", "image/svg+xml" );
	embed.setAttribute( "pluginspage", "http://www.adobe.com/svg/viewer/install/" );
	embed.addEventListener( "load", function(evt) {
		embedReady();
	},false);
	jsGraph.appendChild( embed );
}


function embedReady()
{
	embedLoaded = true;
	
	if( graphLoaded == false )
	{
		setTimeout("embedReady()", 100);
		return;
	}
	
	visMode = "client";
	main_graphClient();
}
