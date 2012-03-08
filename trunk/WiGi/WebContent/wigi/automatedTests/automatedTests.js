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


function Timer()
{
	this.start = 0;
	this.end = 0;
	this.totalTime = 0;
	this.numberOfSegments = 0;
	this.lastSegment = 0;
	
	this.setStart = function()
	{
		this.start = (new Date()).valueOf();
	};
	
	this.setEnd = function()
	{
		this.end = (new Date()).valueOf();
		this.lastSegment = (this.end-this.start);
		this.totalTime += this.lastSegment;
		this.numberOfSegments++;
	};
	
	this.getAverageTime = function()
	{
		return this.getTotalTime() / this.numberOfSegments;
	};
	
	this.getTotalTime = function()
	{
		return this.totalTime;
	};
	
	this.getLastSegment = function()
	{
		return this.lastSegment;
	}
}

function simulateNodeSelection( node, maxDepth )
{
	selectedNode = node;

	resetInterpolationData();			
	
	performInterpolationMethodBFS( node.node, maxDepth );
	
	setInterpolationMethodWeights( maxDepth );
}

function performInterpolationTest()
{
	// Settings for the test
	var maxDepths = [1,2,3,4,1000];
	var numberOfIterations = 20;

	// The actual code for the test
	var viewDetailed_img = document.getElementById("viewDetailed_img");
	var maxX = viewDetailed_img.width;
	var maxY = viewDetailed_img.height;
	var maxDepth;
	var tempNodeImg;
	var tempNode;
	var selectionTimer;
	var movementTimer;
	var xMove;
	var yMove;
	var output = "<table><tr><td>Max Depth</td><td>Selection Time</td><td>Movement Time</td><td>Total Time</td><td>Number of Iterations</td></tr>";
	var index;
	for( var i = 0; i < maxDepths.length; i++ )
	{
		maxDepth = maxDepths[i];
		selectionTimer = new Timer();
		movementTimer = new Timer();
		for( var j = 0; j < numberOfIterations; j++ )
		{
			index = parseInt(Math.random() * nNodes);
			tempNodeImg = nodesVis[index];
			selectionTimer.setStart();
			simulateNodeSelection( tempNodeImg, maxDepth );
			selectionTimer.setEnd();
			
			xMove = randomSign() * maxX / 3;
			yMove = randomSign() * maxY / 3;
			movementTimer.setStart();
			simulateMovement( tempNodeImg, xMove, yMove );
			movementTimer.setEnd();
			
			deselectNode( tempNodeImg );
		}
		
		output += "<tr><td>" + maxDepth + "</td><td>" + selectionTimer.getAverageTime() + "</td><td>" + movementTimer.getAverageTime() + "</td><td>" + (selectionTimer.getAverageTime() + movementTimer.getAverageTime()) + "</td><td>" + numberOfIterations + "</td></tr>";
	}
	
	output += "</table>";
	var testOutput = document.getElementById( "testOutput" );
	testOutput.innerHTML = output;
	
}

function simulateMovement( selectedNode, movementX, movementY )
{
	var tempNode;
	var tempMoveX;
	var tempMoveY;
	for( var i = 0; i < nodesVis.length; i++ )
	{
		tempNode = nodesVis[i].node;
		if( tempNode != selectedNode && tempNode.interpolationWeight > 0 )
		{
			tempMoveX = movementX * tempNode.interpolationWeight;
			tempMoveY = movementY * tempNode.interpolationWeight;
			
			tempNode.xpos += tempMoveX;
			tempNode.ypos += tempMoveY;
		}
			
	}
}

function randomSign()
{
	if( Math.random() > 0.5 )
		return 1;
	
	return -1;
}

var movementTimer;
function performImageMovingTest()
{
	movementTimer = new Timer();

	moveNodes( 0 );
}

function moveNodes( k )
{
	if( movementTimer.start > 0 )
	{
		movementTimer.setEnd();
	}
	var numberOfNodes = nodesVis.length;
	var numberOfIterations = 500;
	var tempNode;
	if( k < numberOfIterations )
	{
		movementTimer.setStart();
		for( var j = 0; j < numberOfNodes; j++ )
		{
			tempNode = nodesVis[j];
			tempNode.style.top = Math.random() * 600 + "px";
			tempNode.style.left = Math.random() * 600 + "px";
//				tempNode.move( xMove, yMove );
		}
		setTimeout( "moveNodes(" + (k+1) + ")", 0 );
	}
	else
	{
		var output = "<table><tr><td>Number of Nodes Moved</td><td>Movement Time</td><td>Number of Iterations</td></tr>";
		output += "<tr><td>" + numberOfNodes + "</td><td>" + movementTimer.getAverageTime() + "</td><td>" + movementTimer.numberOfSegments + "</td></tr>";
		var testOutput = document.getElementById( "testOutput" );
		testOutput.innerHTML = output + "</table>";		
	}
}

var testing = false;
function performClientSideTest()
{
	movementTimer = new Timer();

	var index;
	var numberOfNodes = nodesVis.length;
	index = Math.round(Math.random() * numberOfNodes);
	testing = true;
	clientSideInteraction( 0, index );
}

function clientSideInteraction( k, index )
{
	if( movementTimer.start > 0 )
	{
		movementTimer.setEnd();
	}
	var numberOfIterations = 500;
	var tempNode;
	if( k < numberOfIterations )
	{
		var moveX = randomSign();
		var moveY = randomSign();
		movementTimer.setStart();
		tempNode = nodesVis[index];
		if( k == 0 )
		{
			// Selection
			mouseDown_node( tempNode, 0, 0 );
		}
		
		// Movement
		mousePos.x = tempNode.x + moveX;
		mousePos.y = tempNode.y + moveY;
		mouseMove_node();
		
		
		// Call back
		setTimeout( "clientSideInteraction(" + (k+1) + "," + index + ")", 0 );
	}
	else
	{
		testing = false;
		mouseUp_node();
		var output = "<table><tr><td>Movement Time</td><td>Number of Iterations</td></tr>";
		output += "<tr><td>" + movementTimer.getAverageTime() + "</td><td>" + movementTimer.numberOfSegments + "</td></tr>";
		var testOutput = document.getElementById( "testOutput" );
		testOutput.innerHTML = output + "</table>";		
	}
}

function performServerSideTest()
{
	movementTimer = new Timer();

	testing = true;
	serverSideInteraction( 0 );	
}

function serverSideInteraction( k )
{
	if( newImage )
	{
		if( movementTimer.start > 0 )
		{
			movementTimer.setEnd();
		}
		serverSideK = k;
		var numberOfIterations = 500;
		var tempNode;
		if( k < numberOfIterations )
		{
			mousePos.x = Math.round(Math.random() * 600);
			mousePos.y = Math.round(Math.random() * 600);
			movementTimer.setStart();
			if( k == 0 )
			{
				// Selection
				mouseDown_detailed( viewDetailed, true );
			}
			
			// Movement
			mouseMove_viewDetailed();
			
			
			// Call back
			setTimeout( "serverSideInteraction(" + (k+1) + ")", 0 );
		}
		else
		{
			testing = false;
			var output = "<table><tr><td>Movement Time</td><td>Number of Iterations</td></tr>";
			output += "<tr><td>" + movementTimer.getAverageTime() + "</td><td>" + movementTimer.numberOfSegments + "</td></tr>";
			output += "<tr><td>" + serverTimer.getAverageTime() + "</td><td>" + serverTimer.numberOfSegments + "</td></tr>";
			var testOutput = document.getElementById( "testOutput" );
			testOutput.innerHTML = output + "</table>";		
		}
	}
	else
	{
		setTimeout( "serverSideInteraction(" + k + ")", 0 );
	}
}