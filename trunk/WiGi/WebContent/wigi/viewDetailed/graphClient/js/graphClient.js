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



// imgs
var imgNode = "";

// img sizes
var nodeW = getImgSize(imgNode);
var nodeWover2 = nodeW/2;

// nodes
var nodesVis = [];

// edges
var edgesVis = [];

// update
var updating = false;

// html tags - lines svg 
var lines_SVG = [];

var affectedLines = new Hashtable();

// nNodes, nEdges
var nNodes = 50;
var nEdges = nNodes;

/*
//=======================================
// frame rate - time
//=======================================
var prevMS = 0; // miliseconds
var nowMS;
var date;
function frameRateUpdate()
{
    date = new Date();

    nowMS = date.getMilliseconds();

    if (nowMS > prevMS)
        document.getElementById('fps').value = parseInt(1000/(nowMS - prevMS));

    prevMS = nowMS;
}
*/

//=======================================
// keep in window
//=======================================
function keepInWindow (x, y, object, width, height)
{
	var hidden = false;
    
	if( x + width / 2 < winX1 )
	{
		object.style.visibility = "hidden";
		hidden = true;
	}
	else if( x + width / 2 > winX2 )
    {
		object.style.visibility = "hidden";
		hidden = true;
    }
	else// if( object.style.visibility == "hidden" )
	{
		object.style.visibility = "";
	}
		
	if( y + height / 2 < winY1 )
	{
		object.style.visibility = "hidden";
		hidden = true;
	}
	else if( y + height / 2 > winY2 )
	{
		object.style.visibility = "hidden";
		hidden = true;
	}
	else if( !hidden )//&& object.style.visibility == "hidden" )
	{
		object.style.visibility = "";
	}
	
    return {x: x, y: y};
}

//=======================================
// drag 
//=======================================
var moveTimer = new Timer();
function mouseMove_node(ev)
{
    // frame rate
    //frameRateUpdate();    // want to measure when dragging too
	if( moveTimer.start != 0 )
	{
		moveTimer.setEnd();
		out( "CS: " + moveTimer.getLastSegment() + " , " + moveTimer.getAverageTime() );
	}
	moveTimer.setStart();

	var x = mousePos.x - mouseDownPos.x;
	var y = mousePos.y - mouseDownPos.y;

    // keep node in window
    var xy = keepInWindow(x, y, dragObject, dragObject.width, dragObject.height);
    x = xy.x;
    y = xy.y;
	//******************************
	// Interpolation method
	//******************************
    var node = dragObject.node;
    var movementX = x - (node.xpos - (dragObject.width / 2));
    var movementY = y - (node.ypos - (dragObject.height / 2));
    //******************************
    
    // move dragged object
	dragObject.style.top  = Math.round(y) + "px";     // move object to mouse pos minus position where we started dragging
	dragObject.style.left = Math.round(x) + "px";
	
	// move label
	label_setPosition(dragObject);
	
	//******************************
	// spring model - pass drag pos
	//******************************
	dragObject.node.xpos = x + dragObject.width / 2;
	dragObject.node.ypos = y + dragObject.height / 2;
	
	//******************************

	//******************************
	// Interpolation method
	//******************************
	applyInterpolationMethod( node, movementX, movementY );
    //******************************
	
	// move edges
	setEdgePositions();
	
	return false;
}

function resetTimers()
{
	moveTimer = new Timer();
}

function mouseUp_node(ev)
{
    //******************************
	// spring model - pass drag pos
	//******************************
//	var mousePos = mouseCoords(ev);
	if(dragObject)  // if not null
	{
		updateNodePos.onreadystatechange=state_Change;
		updateServerSide();
    }
    //******************************	
}

function updateServerNodePosition(iNode)
{
	var viewDetailed = document.getElementById( "viewDetailed" );
	var x = iNode.node.xpos - getElementX( viewDetailed );
	var y = iNode.node.ypos - getElementY( viewDetailed );
	iNode.node.dragging = false;
	var realid = iNode.getAttribute( "realId" );
	var selected = false;
	if( selectedNode == iNode )
	{
		selected = true;
	}

    // Tell the server about the new position
    var url = webPath + "NodePosServlet?minX=" + minX + "&maxX=" + maxX + "&minY=" + minY + "&maxY=" + maxY;
    url += "&id_0=" + realid + "&x_0=" + x + "&y_0=" + y + "&s_0=" + selected;
	if( updateNodePos != null )
	{
		try
		{
    		updateNodePos.abort();
    		updateNodePos.open('GET',url,true);
    		updateNodePos.send(null);
		}
		catch( exception )
		{
		}
	}

    viewOverview.style.backgroundImage = "url(" + webPath + "GraphServlet?width=200&height=200&overview=true&r=qual&time=" + new Date().getTime() + ")"; 
}

//=======================================
// get image size
//=======================================
function getImgSize(imgName)
{
    var img = new Image();
	img.src = imgName;
    return img.width;
}

//=======================================
// node
//=======================================
function initNode(i)
{
    // html generate
    //document.write("<img id='INode" + i + "'/>");
    
    var imgTagNode = document.getElementById( 'INode' + i );
    if( imgTagNode == null )
    {
    	imgTagNode = document.createElement('img');
    	imgTagNode.setAttribute('id', 'INode' + i);
    	document.body.appendChild(imgTagNode);
    }
    var width = imgTagNode.width;
    var height = imgTagNode.height;
    
    // html tag
	nodesVis[i] = document.getElementById('INode' + i);
	nodesVis[i].setAttribute( "x", nodesVis[i].x - width/2 );
	nodesVis[i].setAttribute( "y", nodesVis[i].y - height/2 );

    //*****************
    // spring model
    //*****************
    var newNode = new SpringNode( nodesVis[i].x, nodesVis[i].y, i, nodesVis[i].radius, 'INode' + i );
	nodes[i] = newNode;
	//*****************

	// src
	if( nodesVis[i].src == null || nodesVis[i].src == "" )
		nodesVis[i].src = imgNode;

	// position
	nodesVis[i].style.position = 'absolute';
	nodesVis[i].style.left = Math.round(newNode.xpos-nodesVis[i].width/2) + "px";
	nodesVis[i].style.top = Math.round(newNode.ypos-nodesVis[i].height/2) + "px";

	// z
	nodesVis[i].style.zIndex = 100;

	// draggable
	makeDraggable_node(nodesVis[i]);

	//*****************
    // spring model
    //*****************
	nodesVis[i].node = newNode;
	//*****************
}

function deselectNode( node )
{
	if( node != null )
	{
		var source = node.src;
		source = setURLParameter( source, "sel", "false" );
		node.src = source;

		var graphInfoElm = document.getElementById("graphInformation");
		if( graphInfoElm != null )
		{
			graphInfoElm.innerHTML = oldGraphInfo;
		}

		node.style.zIndex = 100;

		label = node.labelVis;
		if( label != null )
		{
			label.style.color = 'gray';
		}
		selectedNode = null;

		resetInterpolationData();

		node.node.setEdgesAffected();
		setEdgePositions();

		updateNodePos.onreadystatechange=state_Change;
		updateServerSide();
	}
}

var oldGraphInfo;
function selectNode( node )
{		
	if( selectedNode != node )
	{
		selectedNode = node;
		var source = node.src;
		source = setURLParameter( source, "sel", "true" );
		source = setURLParameter( source, "id", node.getAttribute( "realId") );
		node.src = source;
		
		var graphInfoElm = document.getElementById("graphInformation");
		if( graphInfoElm != null && node.title != "" )
		{
			oldGraphInfo = graphInfoElm.innerHTML;
			graphInfoElm.innerHTML = oldGraphInfo + "  Selected node = " + node.title;
		}
		
		node.style.zIndex = 101;
		
		label = node.labelVis;
		if( label != null )
		{
			label.style.color = 'red';
		}
		
		initializeInterpolationMethod( node );
		
		setEdgePositions();
	}	
}

var lastMaxDepth = -1;
var lastWholeGraph = -1;
function makeDraggable_node(item)
{
	if(!item) return;

	item.onmousedown = function(ev)
	{
		// find where we start dragging (mouse down)
		mouseDownPos = getPosition(item, ev);
		mouseDown_node( item, mouseDownPos.x, mouseDownPos.y );
		
		return false;
	};
}   

function mouseDown_node(item, x, y)
{
	mouseDownPos.x = x;
	mouseDownPos.y = y;

	dragObject = item;
	dragObject.type = 'node';
	var wholeGraph = document.getElementById("interactionSettingsForm:interpolationMethodUseWholeGraph").checked;
	var numberAffectedElm = document.getElementById("interactionSettingsForm:numberAffectedInput");
	var maxDepth = -1;
	if( numberAffectedElm != null )
		maxDepth = Number(numberAffectedElm.value) + 1;

	resetTimers();
	if( selectedNode != dragObject )
	{
		deselectNode( selectedNode );
		selectNode( dragObject );
	}
	else
	{
		// Make sure that new settings are reflected
		if( wholeGraph != lastWholeGraph || maxDepth != lastMaxDepth )
		{
			initializeInterpolationMethod( dragObject );
			lastMaxDepth = maxDepth;
			lastWholeGraph = wholeGraph;
		}
	}

	//******************************
    // spring model - which node is dragged
    //******************************
	dragObject.node.dragging = true;
	//******************************

}

function setURLParameter( URL, parameter, value )
{
	var index = URL.indexOf( "&" + parameter + "=" );
	var length = parameter.length + 2;
	var symbol = "&";
	if( index == -1 )
	{
		index = URL.indexOf( "?" + parameter + "=" );
		if( index == -1 )
		{
			index = URL.length;
			length = 1;
			if( URL.indexOf("?") == -1 )
			{
				symbol = "?";
			}
		}
	}

	var firstPart = URL.substring( 0, index ) + symbol + parameter + "=" + value;
	var secondPart = URL.substring( index + length );
	if( secondPart.indexOf( "&" ) != -1 )
		secondPart = secondPart.substring( secondPart.indexOf( "&" ) );
	else
		secondPart = "";
	
	return firstPart + secondPart;
}

//=======================================
// edge
//=======================================
function initEdge(n1, n2, length)
{
    // random edge
    if (n1 == null || n1 == -1)
    { 
        n1 = parseInt(Math.random()*nNodes);
        n2 = parseInt(Math.random()*nNodes);
    }

    // add at the end of the array - go to last one
    i = edgesVis.length;
    
    edge = new Object();

    edge.node1 = nodesVis[n1];
    edge.node2 = nodesVis[n2];

    edgesVis[i] = edge;

    //*****************
    // spring model
    //*****************
    var newEdge = new SpringEdge(n1, n2, length, i );
	edges[i] = newEdge;
	var fromNode = getNode( newEdge.fromId );
	var toNode = getNode( newEdge.toId );

	fromNode.fromEdges[fromNode.fromEdges.length] = newEdge;
	toNode.toEdges[toNode.toEdges.length] = newEdge;
	fromNode.neighbors[fromNode.neighbors.length] = toNode;
	toNode.neighbors[toNode.neighbors.length] = fromNode;
	//*****************    
}

var line;
var svgDocument;
var embed_x;
var embed_y;
var edgeColor = 99;
var strokeWidth = 2;
function setEdgePositions()
{           
//	var embed_SVG = document.getElementById( "embed_SVG" );
	var viewDetailed = document.getElementById( "viewDetailed" );
	embed_x = getElementX( viewDetailed );
	embed_y = getElementY( viewDetailed );
//	var embed_x = getElementX(embed_SVG);
//	var embed_y = getElementY(embed_SVG);
	
	var edgeColorElm = document.getElementById("appearanceSettingsForm:edgeColorInput");
	edgeColor = 99;
	if( edgeColorElm != null )
	{
		edgeColor = Math.round(edgeColorElm.value * 255);
	}
	
	var strokeWidthElm = document.getElementById( "appearanceSettingsForm:edgeThicknessInput" );
	strokeWidth = 2;
	if( strokeWidthElm != null )
	{
		strokeWidth = Number(strokeWidthElm.value);
	}
	
	
    // edges
	var affectedIds = affectedLines.keys();
    for (var i=0; i<affectedIds.length; i++)
    {
    	setEdgePosition( affectedIds[i] );
    }
    
	affectedLines.clear();
}

function setEdgePosition( i )
{
    line = lines_SVG[i];
    
    if( edgesVis[i].node1 == selectedNode || edgesVis[i].node2 == selectedNode )
    {
    	line.setAttribute("style", "stroke:rgb(255,0,0); stroke-width:" + strokeWidth);
    }
    else
    {
    	line.setAttribute("style", "stroke:rgb(" + edgeColor + "," + edgeColor + "," + edgeColor + "); stroke-width:" + strokeWidth);
    }
    
    line.setAttribute("x1", Math.round(edgesVis[i].node1.node.xpos - embed_x));
    line.setAttribute("y1", Math.round(edgesVis[i].node1.node.ypos - embed_y));
    line.setAttribute("x2", Math.round(edgesVis[i].node2.node.xpos - embed_x));
    line.setAttribute("y2", Math.round(edgesVis[i].node2.node.ypos - embed_y));

}

function setEdgeAffected( i )
{
	line = lines_SVG[i];
	affectedLines.put( i, line );
}

//=======================================
// animation
//=======================================
function update()
{
    //^^^^^^^^^^^^
    if (visMode != 'client')
    {
        nodesVis = [];
        edgesVis = [];
        edges = [];
        nodes = [];
    
        return;
    }
    //^^^^^^^^^^^^

    // frame rate
    //frameRateUpdate();

    // update graph
    if (updating)
    {
    	
        //*****************
        // spring model
        //*****************
	    updatePositions();
	    //*****************

	    setEdgePositions();

	    // recurse
		setTimeout("update()",1);
	}
}

function updateButtonText()
{
	var button = document.getElementById( "BStartStop" );
    if (updating)
    {
    	if( button != null )
    		button.innerHTML = "Stop";
    }
    else
    {
    	if( button != null )
    		button.innerHTML = "Start";
    }
}

function updateStartStop()
{
    updating = !updating;

    updateButtonText();
    
    if( updating )
    {
    	update();
    }
    else
    {
    	getImage_viewDetailedDefault();        
    }
    
//    out("updating: " + updating);
}

//=======================================
// get window position
//=======================================
function getWindowPosition()
{
    var viewDetailed = document.getElementById('viewDetailed');
    
    winW = width(viewDetailed);
    winH = height(viewDetailed);
    
    winX1 = getElementX(viewDetailed);
    winX2 = winX1 + winW - nodeW;
    
    winY1 = getElementY(viewDetailed);
    winY2 = winY1 + winH - nodeW;
}

//=======================================
// init graph
//=======================================
function init_graphClient()
{
    getWindowPosition();		                

    nodesVis = [];
    edgesVis = [];
    edges = [];
    nodes = [];
    
    // nNodes, nEdges - might need to change in svg.svg (5000 now)

    // nodes
    for (var i=0; i<nNodes; i++)	
        initNode(i);

    var tempEdge;
    // edges                
    for (var i=0; i<nEdges; i++)
    {
    	tempEdge = document.getElementById( "IEdge" + i );
    	if( tempEdge == null )
    	{
    		initEdge(-1,-1,100);
    	}
    	else
    	{
    		initEdge(tempEdge.n1, tempEdge.n2, tempEdge.length);
    	}
        
        lines_SVG[i] = svgDocument.getElementById("line" + i);
        setEdgeAffected(i);
    }
    
    // allow graphs with more than nEdges edges
    for( var i = nEdges; i < 2000; i++ )
    {
    	lines_SVG[i] = svgDocument.getElementById("line" + i);
    }
    
    // first time draw edges
    setEdgePositions();
    
    // recursive
	update();

	// Keep track of the graph information
	var graphInfoElm = document.getElementById("graphInformation");
	if( graphInfoElm != null )
	{
		oldGraphInfo = graphInfoElm.innerHTML;
		var index = oldGraphInfo.indexOf( "." );
		if( index != -1 )
			oldGraphInfo = oldGraphInfo.substring( 0, index + 1 );
	}
	
	resetTimers();
}

//=======================================
// MAIN
//=======================================
function main_graphClient()
{	    
	var viewDetailed = document.getElementById("viewDetailed");
	var embed_SVG = document.getElementById("embed_SVG");
	
	// embed position
	embed_SVG.style.left = getElementX(viewDetailed) + "px";
	embed_SVG.style.top = getElementY(viewDetailed) + "px";
	
	// svg init
	svgDocument = embed_SVG.getSVGDocument();
	svgDocument.onmousemove = mouseMoveSVG; // to be able to get mousePos when over svg area   
	svgDocument.onmouseup = mouseUpSVG;
	svgDocument.onmousedown = mouseDownSVG;
		        
	nodeW = getImgSize(imgNode);
	nodeWover2 = nodeW / 2;
	
	init_graphClient();
}
