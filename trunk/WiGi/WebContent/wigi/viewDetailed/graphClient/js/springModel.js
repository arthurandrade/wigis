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



// window
var win;
var winW, winH;
var winX1, winX2, winY1, winY2;	   //left, right, top, bottom

//=======================================
// SpringNode
//=======================================
// This is essentially the constructor for a SpringNode object
function SpringNode( xpos, ypos, id, radius, imgId )
{
	this.xpos = xpos;
	this.ypos = ypos;
	this.id = id;
	this.radius = radius;
	this.force = [0.0, 0.0];
	this.fromEdges = new Array();
	this.toEdges = new Array();
	this.neighbors = new Array();
	this.acceleration = [0.0,0.0];
	this.velocity = [0.0,0.0];
	this.imgId = imgId;
	this.dragging = false;
	this.positioned = false;
	this.interpolationWeight = 0;
	this.distanceFromSelectedNode = Number.MAX_VALUE;
	this.img = document.getElementById( imgId );
	
	this.move = function( x, y )
	{
		this.setPosition( this.xpos - (this.img.width/2) + x,this.ypos - (this.img.height/2) + y );
	}
	
	this.setPosition = function( x, y )
	{
		// keep node in window
        var xy = keepInWindow(x, y, this.img, this.img.width, this.img.height, false);
        x = xy.x;
        y = xy.y;

		this.xpos = x+this.img.width/2;
		this.ypos = y+this.img.width/2;

		this.img.style.top = Math.round(y) + "px";
		this.img.style.left = Math.round(x) + "px";
	    //_____________
		// move label
		//_____________
		label_setPosition(this.img);
		//_____________
		
		this.setEdgesAffected();
		
		this.positioned = true;
	};
	
	this.setEdgesAffected = function()
	{
		for( var i = 0; i < this.fromEdges.length; i++ )
		{
			setEdgeAffected( this.fromEdges[i].visIndex );
		}
		
		for( var i = 0; i < this.toEdges.length; i++ )
		{
			setEdgeAffected( this.toEdges[i].visIndex );
		}
	};

	this.applyForce = function()
	{
	    if( this.dragging != true )
	    {
		    var magnitude = Math.sqrt( this.force[0] * this.force[0] + this.force[1] * this.force[1] );
		    if( magnitude > 10 )
		    {
			    this.force[0] = 10 * this.force[0] / magnitude;
			    this.force[1] = 10 * this.force[1] / magnitude;
		    }
		    this.acceleration[0] = this.force[0];
		    this.acceleration[1] = this.force[1];
		    this.velocity[0] += this.acceleration[0];
		    this.velocity[1] += this.acceleration[1];
		    this.xpos += this.velocity[0];
		    this.ypos += this.velocity[1];
		    this.velocity[0] *= 0.7;
		    this.velocity[1] *= 0.7;
		    		
		    img = document.getElementById( imgId );
		    var realid = img.getAttribute('realId');

		    // keep node in window
            var xy = keepInWindow(this.xpos-img.width/2, this.ypos-img.height/2, img, img.width, img.height, false);
            this.xpos = xy.x+img.width/2;
            this.ypos = xy.y+img.height/2;		    

		    img.style.top = Math.round(xy.y) + "px";
		    img.style.left = Math.round(xy.x) + "px";
		    //_____________
			// move label
			//_____________
			label_setPosition(img);
			//_____________
			
			this.setEdgesAffected();

		    this.force[0] = 0.0;
		    this.force[1] = 0.0;
	    }
	};
}

//=======================================
// SpringEdge
//=======================================
// This is essentially the constructor for a SpringNode object
function SpringEdge( fromId, toId, length, visIndex )
{
	this.fromId = fromId;
	this.toId = toId;
	this.length = length;
	this.visIndex = visIndex;
}

//=======================================
// getNode
//=======================================
// Returns the node with the given id from the list of nodes
// (Should be improved by changing to a binary search)
function getNode( id )
{
	return nodes[id];
}

//=======================================
// binarySearchNode
//=======================================
function binarySearchNode( id, begin, end )
{
	var size = end - begin;
	// Sequential search when the space has become small enough
	if( size < 7 )
	{
		for( var i = begin; i <= end; i++ )
		{
			if( nodes[i].id == id )
				return nodes[i];
		}
	}

	// find the midpoint
	var mid = (begin + end) / 2;

	// recursion
	if( !(nodes.mid) || nodes[mid].id > id )
	{
		binarySearchNode( id, begin, mid );
	}
	else if( nodes[mid].id < id )
	{
		binarySearchNode( id, mid, end );
	}
	else
	{
		return nodes[mid];
	}
	
	return null;
}

//=======================================
// nodes + edges
//=======================================
var nodes = new Array();    // Contains all the nodes in the graph
var edges = new Array();    // Contains all the edges in the graph

//=======================================
// getSpringForce
//=======================================
// Defines the stiffness of the springs
var springConstant = 0.003;
// Returns the spring force between the two connected nodes
function getSpringForce( nodeFrom, nodeTo, restingDistance )
{
	var force = [0.0, 0.0];
	var xdiff = nodeFrom.xpos - nodeTo.xpos;
	var ydiff = nodeFrom.ypos - nodeTo.ypos;
	var distanceSq = xdiff * xdiff + ydiff * ydiff;
	var distance = Math.sqrt( distanceSq );
	if( distance > 0 )
	{
		force[0] = xdiff / distance;
		force[1] = ydiff / distance;
		var delta = distance - restingDistance;
		var intensity = springConstant * delta;
		force[0] *= intensity;
		force[1] *= intensity;
	}

	return force;
}

//=======================================
// getRepellingForce
//=======================================
// Defines the intensity of the repelling forces
var defaultRepellingIntensity = 1000;
var repellingIntensity = defaultRepellingIntensity; 
// Returns the repelling force between the two given nodes
function getRepellingForce( nodeFrom, nodeTo )
{
	var force;
	var xdiff;
	var ydiff;
	var distanceSq;

	force = [0.0,0.0];
	xdiff = nodeFrom.xpos - nodeTo.xpos;
	ydiff = nodeFrom.ypos - nodeTo.ypos;
	distanceSq = xdiff * xdiff + ydiff * ydiff;
	var distance = Math.sqrt( distanceSq );
	if( distanceSq > 0 )
	{
		var intensity = repellingIntensity / distanceSq;
		force[0] = xdiff * intensity / distance;
		force[1] = ydiff * intensity / distance;
	}

	return force;
}

var frame = 0;
var viewDetailedWidthOver2;
var viewDetailedHeightOver2;
//=======================================
// updatePositions
//=======================================
// This function runs one iteration of the spring algorithm
function updatePositions()
{	
	var edge;
	var nodeFrom;
	var nodeTo;
	var length;
	var force;
	var fromEdges;
	var toEdges;
	var node;	

	frame++;


	for( var i = 0; i < nodes.length; i++ )
	{
		node = nodes[i];
		if( node.img.style.visibility == "hidden" )
		{
			continue;
		}
		
		for( var j = 0; j < node.fromEdges.length; j++ )
		{
			edge = node.fromEdges[j];
			nodeTo = getNode(edge.toId);
			length = edge.length;
			force = getSpringForce( nodeTo, node, length );
			node.force[0] += force[0];
			node.force[1] += force[1];
		}

		for( var j = 0; j < node.toEdges.length; j++ )
		{
			edge = node.toEdges[j];
			nodeFrom = getNode(edge.fromId);
			length = edge.length;
			force = getSpringForce( nodeFrom, node, length );
			node.force[0] += force[0];
			node.force[1] += force[1];
		}

		for( var j = 0; j < nodes.length; j++ )
		{
			if( j != i )
			{
				force = getRepellingForce( node, nodes[j] );
				node.force[0] += force[0];
				node.force[1] += force[1];
				force = getGravityForce( node );
				node.force[0] += force[0];
				node.force[1] += force[1];
			}
		}
	}
	
    // Update the node position on the server
	// Don't update if the zoom is being changed
	if( dragObject == null || (dragObject.type != "zoom" && dragObject.type != "zoom_corner" ) )
	{
		// Make sure that our XMLHttpRequest object is not null
	    if( updateNodePos != null )
		{
			updateNodePos.onreadystatechange=null;
	    	updateServerSide();
			var refreshOverview = document.getElementById("refreshOverview");
			if( frame % 10 == 0 && refreshOverview != null && refreshOverview.checked )
			{
				viewOverview.style.backgroundImage = "url(" + webPath + "GraphServlet?width=200&height=200&overview=true&r=qual&time=" + new Date().getTime() + ")";
			}
		}
	}
}

var gravity_intensity = 0.00005;
function getGravityForce( node )
{
	var force = [0.0,0.0];
	var xDiff = viewDetailedWidthOver2 - node.xpos;
	var yDiff = viewDetailedHeightOver2 - node.ypos;
	force[0] = xDiff * gravity_intensity;
	force[1] = yDiff * gravity_intensity;
	
	return force;
}

function updateServerSide()
{
	var url = webPath + "NodePosServlet";
    var params = "minX=" + minX + "&maxX=" + maxX + "&minY=" + minY + "&maxY=" + maxY;
	var img;
	var realid;
	var x;
	var y;
	var viewDetailed = document.getElementById( "viewDetailed" );
	var view_x = getElementX( viewDetailed );
	var view_y = getElementY( viewDetailed );
	
	for( var i = 0; i < nodes.length; i++ )
	{
		node = nodes[i];
		if( node.img.style.visibility == "hidden" )
		{
		}
		else
		{
			node.applyForce();			
		}
	
		img = document.getElementById(node.imgId);
		realid = img.getAttribute("realId");
		x = node.xpos - view_x;
		y = node.ypos - view_y;
		params += "&id_" + i + "=" + realid + "&x_" + i + "=" + x +"&y_" + i + "=" + y;
		if( img == selectedNode )
		{
			params += "&s_" + i + "=true";
		}
	}
	
	postToUrl( url, params );
}

function postToUrl( url, params )
{
	try
	{
		if( !updateNodePosComplete )
			updateNodePos.abort();
		
		updateNodePosComplete = false;
		updateNodePos.open('POST',url,true);
		updateNodePos.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
		updateNodePos.setRequestHeader("Content-length", params.length);
		updateNodePos.setRequestHeader("Connection", "close");
		updateNodePos.send(params);
	}
	catch( exception ) {}
}