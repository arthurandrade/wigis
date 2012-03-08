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



var visMode;

//==============================================
// main
//==============================================
function main_visMode()
{
    visMode = 'client';    
    change_visMode()
}

//==============================================
// change visualization mode: client <-> server
//==============================================
function change_visMode()
{
    // client
    if (visMode != 'client')
    {
        // change mode
        visMode = 'client';
	    
    	if( outElement != null )
    		outElement.value = "client vis";
	    
	    //--------------------------------
	    // remove server image
	    //--------------------------------
	    document.getElementById('viewDetailed_img').style.display = 'none';
	    
	    //--------------------------------
	    // add client graph vis
	    //--------------------------------
        document.getElementById('embed_SVG').style.display = 'block';
          
        init_graphClient();
	    
	    //--------------------------------
    }
    // server
    else
    {
        // change mode
        visMode = 'server';
        
    	if( outElement != null )
    		outElement.value = "server vis";        
        
        //--------------------------------
        // remove clientgraph vis
        //--------------------------------
        document.getElementById('embed_SVG').style.display = 'none';
        
        for (var i=0; i<nNodes; i++)	
            removeElemById('INode' + i);
            
        //--------------------------------
        // add server image
        //--------------------------------
        document.getElementById('viewDetailed_img').style.display = 'block';
        
        //--------------------------------
    }
}