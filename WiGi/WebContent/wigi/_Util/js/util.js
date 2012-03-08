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
// get absolute position of an element
//=======================================
function getElementX (oElement)
{
    var iReturnValue = 0;
    
	if( oElement.offsetParent )
	{
		while (oElement != null) 
	    {
	        iReturnValue += oElement.offsetLeft;
	        oElement = oElement.offsetParent;
	    }
	}
	else if( oElement.y )
	{
		iReturnValue = oElement.y;
	}
	
    return iReturnValue;
}

function getElementY (oElement)
{
    var iReturnValue = 0;
    
	if( oElement.offsetParent )
	{
		while (oElement != null) 
	    {
	        iReturnValue += oElement.offsetTop;
	        oElement = oElement.offsetParent;
	    }
	}
	else if( oElement.x )
	{
		iReturnValue = oElement.x;
	}
	
    return iReturnValue;
}

function getElementWidth (element)
{
	return element.offsetWidth;
}

function getElementHeight (element)
{
	return element.offsetHeight;
}

//=======================================
// Remove element by id
//=======================================
function removeElemById(e) 
{
    var _e = document.getElementById(e);
    
    if(_e) 
    {
        _e.parentNode.removeChild(_e);
        return true;
    }
    else 
    {
        return false;
    }
}

//=======================================
// get position
//=======================================
// get coords of item relative to doc, for example where a div starts
function getPosition(item, ev)
{
    var mousePos = mouseCoords(ev);
    return getPositionCoord(item,mousePos);
}

function getPositionCoord(item,mousePos)
{
	var left = 0;
	var top  = 0;

	if( item.offsetParent )
	{
		while (item.offsetParent)
		{
			left += item.offsetLeft;
			top  += item.offsetTop;
			item  = item.offsetParent;
		}
	
		left += item.offsetLeft;
		top  += item.offsetTop;
	}
	else if( item.x )
	{
		left = item.x;
		top = item.y;
	}
	
	return {x:mousePos.x - left, y:mousePos.y - top};
}

//=======================================
// style - left, top, width, height
//=======================================
function left (element)
{
    return removePX(element.style.left);
}

function right (element)
{
	return left(element) + width(element); 
}

function top (element)
{
    return removePX(element.style.top);
}

function bottom (element)
{
	return top(element) + height(element); 
}

function width (element)
{
	if( element != null )
		return removePX(element.style.width);
	
	return 0;
}

function height (element)
{
	if( element != null )
		return removePX(element.style.height);
	
	return 0;
}


//=======================================
// array 2D
//=======================================
function array2D(NumOfRows,NumOfCols)
{
    var k = new Array(NumOfRows);
    for (i = 0; i < k.length; ++i)
    k[i] = new Array (NumOfCols);

    return k;
}