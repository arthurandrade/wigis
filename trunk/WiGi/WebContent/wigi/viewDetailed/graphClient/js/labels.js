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



var charW = 8;		// label character width
var charH = 20;
var showLabels_checkbox;
var curvedLabels_checkbox;
var labelsDiv;

function main_labels()
{	
	labelsDiv = document.getElementById("labels");
	labelsDiv.style.visibility = "hidden";
	
	showLabels_checkbox = document.getElementById("labelsForm:showLabels");
	curvedLabels_checkbox = document.getElementById("labelsForm:curvedLabels");
//	showLabels_checkbox.onclick = showLabels_onclick;
	showLabels_onclick();
}

function label_initLabel(node)
{	
	var label = document.createElement('label');	// try other things
	
	label.setAttribute('id', 'label' + node.id);
	
	label.style.MozUserSelect = 'none';	
	label.style.position = 'absolute';
	if( selectedNode == node )
		label.style.color = 'red';
	else
		label.style.color = 'gray';
	
	label.innerHTML = node.title;
	
	document.getElementById("labels").appendChild(label);
	
	// link label to node
	node.labelVis = document.getElementById('label' + node.id);

	// set position
	label_setPosition(node);
}

function label_setPosition(node)
{		
	//if (document.getElementById("settings:showLabels") != null)
	//if(showLabels_checkbox.checked && !curvedLabels_checkbox.checked)	// do calculations only if showing labels
	if( node.labelVis != null )
	{
		var labelW = (node.labelVis.innerHTML).length * charW;
		
		var labelTop = Math.max(winY1, (node.y - charH));
		labelTop = Math.min((winY2 - charH), labelTop);
		var labelLeft = Math.max(winX1, (node.x - labelW/2));
		labelLeft = Math.min((winX2 - labelW), labelLeft);
		
		node.labelVis.style.top = labelTop + "px";
		node.labelVis.style.left = labelLeft + "px";
	}
}

function showLabels_onclick()
{
	if( showLabels_checkbox != null && curvedLabels_checkbox != null )
		labelsDiv.style.visibility = showLabels_checkbox.checked && !curvedLabels_checkbox.checked ? "" : "hidden";
}


