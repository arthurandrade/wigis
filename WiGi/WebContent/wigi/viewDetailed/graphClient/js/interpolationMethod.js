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


function initializeInterpolationMethod( node )
{
	var maxDepth = Number.MAX_VALUE;
	var wholeGraph = document.getElementById("interactionSettingsForm:interpolationMethodUseWholeGraph").checked;
	if( !wholeGraph )
	{
		maxDepth = Number(document.getElementById("interactionSettingsForm:numberAffectedInput").value) + 1;
	}
	
	var maxD = performInterpolationMethodBFS( node.node, maxDepth );
	if( wholeGraph )
	{
		maxDepth = maxD;
	}
	
	setInterpolationMethodWeights( maxDepth );

	node.node.setEdgesAffected();
}

function performInterpolationMethodBFS( node, maxDepth )
{
	node.distanceFromSelectedNode = 0;
	if( maxDepth > 0 )
	{
		return IM_handleList( node.neighbors, 1, maxDepth );
	}
	
	return 0;
}

function IM_handleList( nodesToHandle, distance, maxDepth )
{
	var tempNode;
	var nextList = new Array();
	for( var i = 0; i < nodesToHandle.length; i++ )
	{
		tempNode = nodesToHandle[i];
		if( tempNode.distanceFromSelectedNode > distance )
		{
			tempNode.distanceFromSelectedNode = distance;
			for( var j = 0; j < tempNode.neighbors.length; j++ )
			{
				nextList[nextList.length] = tempNode.neighbors[j];
			}
		}
	}
	
	if( nextList.length > 0 && distance < maxDepth )
	{
		return IM_handleList( nextList, distance + 1, maxDepth );
	}
	
	return distance - 1;
}

function resetInterpolationData()
{
	for( var i = 0; i < nodesVis.length; i++ )
	{
		nodesVis[i].node.distanceFromSelectedNode = Number.MAX_VALUE;
		nodesVis[i].node.interpolationWeight = 0;
	}		
}

var scalar1 = 3;
var scalar2 = 2;
var sCurveLowEnd = 0;
var sCurveHighEnd = 1;
function setInterpolationMethodWeights( maxDistance )
{
	var tempNode;
	var tempWeight;
	for( var i = 0; i < nodesVis.length; i++ )
	{
		tempNode = nodesVis[i].node;
		if( tempNode.distanceFromSelectedNode != Number.MAX_VALUE )
		{
			tempWeight = 1 - (tempNode.distanceFromSelectedNode / maxDistance);
			if( tempWeight > sCurveLowEnd )
			{
				tempWeight = (tempWeight - sCurveLowEnd) / (sCurveHighEnd - sCurveLowEnd);
				tempWeight = scalar1 * tempWeight * tempWeight - scalar2 * tempWeight * tempWeight * tempWeight;
			}
			else
			{
				tempWeight = 0;
			}

			tempNode.interpolationWeight = tempWeight;	
		}
	}
}

function applyInterpolationMethod( selectedNode, movementX, movementY )
{
	var tempNode;
	var tempMoveX;
	var tempMoveY;
	for( var i = 0; i < nodesVis.length; i++ )
	{
		tempNode = nodesVis[i].node;
		if( tempNode == selectedNode )
		{
			tempNode.setEdgesAffected();
		}
		else if( tempNode.interpolationWeight > 0 )
		{
			tempMoveX = movementX * tempNode.interpolationWeight;
			tempMoveY = movementY * tempNode.interpolationWeight;
			
			tempNode.setPosition( tempMoveX + tempNode.xpos - (tempNode.img.width / 2), tempMoveY + tempNode.ypos - (tempNode.img.height / 2) );
		}			
	}
}