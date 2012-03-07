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



// generate a gif image from a jsp file, with ajax


//-------------------------------
// get XMLHttpRequest object
//-------------------------------
var xmlHttp;
function GetXmlHttpObject()
{            
    var xmlHttp=null;
    
    try
    {
        // Firefox, Opera 8.0+, Safari
        xmlHttp = new XMLHttpRequest();
    }
    catch (e)
    {
        // IE 6.0+
        try
        {
            xmlHttp = new ActiveXObject("Msxml2.XMLHTTP");
        }
        catch (e)
        {
            try
            {
                xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");   // IE 5.5+
            }
            catch (e)
            {
                alert("Your browser does not support AJAX!");
                return false;
            }
        }
    }
    
    return xmlHttp;
}

//-------------------------------
// after request this function receives data from server
//-------------------------------
function stateChanged()
{
    // readyState - holds the server response status
    // each time readyState changes - onreadystatechange will execute
    // 0	The request is not initialized
    // 1	The request has been set up
    // 2	The request has been sent
    // 3	The request is in process
    // 4	The request is complete
    
    if (xmlHttp.readyState == 4)
    {
        // responseText - stores data return from server
        //document.myForm.time.value = xmlHttp.responseText;
        //document.getElementById("div1").innerHTML = xmlHttp.responseText;
    }
}

//-------------------------------
// image
//-------------------------------
function getImage()
{
    for (var i=0;i<10;i++)
    {    
        // asynchronous: false - wait for previous GET to complete, in order to show every frame
//        xmlHttp.open("GET", "http://localhost:8080/WebGraph/faces/GraphServlet", false);
//        xmlHttp.send(null);
        
        // refresh image src
        //document.getElementById("viewOverview").src = "viewDetailed/graphServer/jsp/serverImage.jsp?a=" + new Date().getTime();
//        viewDetailed.style.backgroundImage = "url(http://localhost:8080/WebGraph/faces/GraphServlet?a=" + new Date().getTime() + ")";
        
        out (i);
    }
}

//-------------------------------
// main
//-------------------------------
function main_ajax()
{
    xmlHttp = GetXmlHttpObject();    
    xmlHttp.onreadystatechange = stateChanged;
}