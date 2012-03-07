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



//======================================
// add item
//======================================
function contextMenu_addItem(contextMenuId, text, func) 
{
    var ul = document.getElementById(contextMenuId);
    var li = document.createElement('li');

    var txt = document.createTextNode(text);
    li.appendChild(txt);

    li.onmousedown = function() { func() };

    ul.appendChild(li);
}

//======================================
// CONTEXT MENU
//======================================
var ContextMenu =
{ 
	// private attributes
	_menus : new Array,
	_attachedElement : null,
	_menuElement : null,
	_preventDefault : true,
	_preventForms : true,

	//-------------------------------------------
	// public method. Sets up whole context menu stuff..
	//-------------------------------------------
	setup: function(conf) 
	{
		if ( document.all && document.getElementById && !window.opera ) {
			ContextMenu.IE = true;
		}
 
		if ( !document.all && document.getElementById && !window.opera ) {
			ContextMenu.FF = true;
		}
 
		if ( document.all && document.getElementById && window.opera ) {
			ContextMenu.OP = true;
		}
 
		if ( ContextMenu.IE || ContextMenu.FF ) {
 
			document.oncontextmenu = ContextMenu._show;
			document.onclick = ContextMenu._hide;
 
			if (conf && typeof(conf.preventDefault) != "undefined") {
				ContextMenu._preventDefault = conf.preventDefault;
			}
 
			if (conf && typeof(conf.preventForms) != "undefined") {
				ContextMenu._preventForms = conf.preventForms;
			}
 
		}
 
	},

	//-------------------------------------------
	// public method. Attaches context menus to specific class names
	//-------------------------------------------
	attach: function(classNames, menuId) 
	{
		if (typeof(classNames) == "string") {
			ContextMenu._menus[classNames] = menuId;
		}
 
		if (typeof(classNames) == "object") {
			for (x = 0; x < classNames.length; x++) {
				ContextMenu._menus[classNames[x]] = menuId;
			}
		}
	},

	//-------------------------------------------
	// private method. Get which context menu to show
	//-------------------------------------------
	_getMenuElementId: function(e) 
	{
 
		if (ContextMenu.IE) {
			ContextMenu._attachedElement = event.srcElement;
		} else {
			ContextMenu._attachedElement = e.target;
		}
 
		while(ContextMenu._attachedElement != null) {
			var className = ContextMenu._attachedElement.className;
 
			if (typeof(className) != "undefined") {
				className = className.replace(/^\s+/g, "").replace(/\s+$/g, "")
				var classArray = className.split(/[ ]+/g);
 
				for (i = 0; i < classArray.length; i++) {
					if (ContextMenu._menus[classArray[i]]) {
						return ContextMenu._menus[classArray[i]];
					}
				}
			}
 
			if (ContextMenu.IE) {
				ContextMenu._attachedElement = ContextMenu._attachedElement.parentElement;
			} else {
				ContextMenu._attachedElement = ContextMenu._attachedElement.parentNode;
			}
		}
 
		return null;
 
	},

	//-------------------------------------------
	// private method. Shows context menu
	//-------------------------------------------
	_getReturnValue: function(e) 
	{ 
		var returnValue = true;
		var evt = ContextMenu.IE ? window.event : e;
 
		if (evt.button != 1) {
			if (evt.target) {
				var el = evt.target;
			} else if (evt.srcElement) {
				var el = evt.srcElement;
			}
 
			var tname = el.tagName.toLowerCase();
 
			if ((tname == "input" || tname == "textarea")) {
				if (!ContextMenu._preventForms) {
					returnValue = true;
				} else {
					returnValue = false;
				}
			} else {
				if (!ContextMenu._preventDefault) {
					returnValue = true;
				} else {
					returnValue = false;
				}
			}
		}
 
		return returnValue;
 
	},

	//-------------------------------------------
	// private method. Shows context menu
	//-------------------------------------------
	_show: function(e) 
	{
		ContextMenu._hide();
		var menuElementId = ContextMenu._getMenuElementId(e);
 
		if (menuElementId) {
			var m = ContextMenu._getMousePosition(e);
			var s = ContextMenu._getScrollPosition(e);
 
			ContextMenu._menuElement = document.getElementById(menuElementId);
			ContextMenu._menuElement.style.left = m.x + s.x + 'px';
			ContextMenu._menuElement.style.top = m.y + s.y + 'px';
			ContextMenu._menuElement.style.display = 'block';
			return false;
		}
 
		return ContextMenu._getReturnValue(e);
 
	},

	//-------------------------------------------
	// private method. Hides context menu
	//-------------------------------------------
	_hide: function() 
	{
		if (ContextMenu._menuElement) {
			ContextMenu._menuElement.style.display = 'none';
		}
 
	},

	//-------------------------------------------
	// private method. Returns mouse position
	_getMousePosition: function(e) 
	{
 
		e = e ? e : window.event;
		var position = {
			'x' : e.clientX,
			'y' : e.clientY
		}
 
		return position;
 
	},
 
 
    //-------------------------------------------
	// private method. Get document scroll position
	_getScrollPosition: function() 
	{
		var x = 0;
		var y = 0;
 
		if( typeof( window.pageYOffset ) == 'number' ) {
			x = window.pageXOffset;
			y = window.pageYOffset;
		} else if( document.documentElement && ( document.documentElement.scrollLeft || document.documentElement.scrollTop ) ) {
			x = document.documentElement.scrollLeft;
			y = document.documentElement.scrollTop;
		} else if( document.body && ( document.body.scrollLeft || document.body.scrollTop ) ) {
			x = document.body.scrollLeft;
			y = document.body.scrollTop;
		}
 
		var position = {
			'x' : x,
			'y' : y
		}
 
		return position;
 
	}
 
}