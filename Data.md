# Data #

This page will be split in two parts, the first one will define the structure of a .DNV file and list all of its arguments and the second one will explain the WiGis uploader.

# DNV Structure #
The WiGi application allow you to visualize graphs saved as .dnv files. A folder called 'graphs' contains three examples of graphs for the application and is available at the following address : http://code.google.com/p/wigis/downloads/detail?name=graphs.zip&can=2&q=#makechanges.

This page explains how a graph is stored into a .dnv file. The example below presents a basic graph composed of two nodes linked by one edge. The aim of this example is to explain how you can modify graphs or create your own graphs.
```
<DNVGRAPH>
	<Level value="0" numberOfNodes="2" numberOfEdges="1">
		<DNVNODE Id="1" Level="0" Position="[82.33818 , 55.25211]" Color="[0.84166664 , 0.73333335 , 1.0]" Size="1.0" Label="" Mass="1.0" Fixed="false" Icon="" Type="" BBid="" ForceLabel="false" Alpha="1.0" LabelColor="null" LabelOutlineColor="null" OutlineColor="null" LabelSize="null" Expandable="false" HideLabelBackground="false" CurvedLabel="false" Visible="true" Selected="false">
		</DNVNODE>
		<DNVNODE Id="2" Level="0" Position="[53.328995 , 6.9600196]" Color="[0.84166664 , 0.73333335 , 1.0]" Size="1.0" Label="" Mass="1.0" Fixed="false" Icon="" Type="" BBid="" ForceLabel="false" Alpha="1.0" LabelColor="null" LabelOutlineColor="null" OutlineColor="null" LabelSize="null" Expandable="false" HideLabelBackground="false" CurvedLabel="false" Visible="true" Selected="false">
		</DNVNODE>
		<DNVEDGE Id="3" Level="0" Length="10.0" K="0.3" From="1" To="2" Label="" Directional="false" BBid="" Color="[-1.0 , -1.0 , -1.0]" Type="" HasSetColor="true" Alpha="1.0" LabelColor="null" LabelOutlineColor="null" LabelSize="null" Thickness="1.0" Visible="true">
		</DNVEDGE>
	</Level>
</DNVGRAPH>
```

The above code give the following result :
<img src='http://wigis.googlecode.com/files/data.png' alt='data result' />

The code structure is simple to understand and works like an XML file (by using tags). There is four different types of tags :
  * 

&lt;DNVGRAPH&gt;



&lt;/DNVGRAPH&gt;

 delimit the graph, everything between the beginning and ending tags will be part of the graph
  * 

&lt;Level&gt;



&lt;/Level&gt;

 a graph can be a multi layered graph with several levels, all nodes and edges between these two tags will consist of one graph layer. In case of a simple one layer graph, there will be only one level section.
  * 

&lt;DNVNODE&gt;



&lt;/DNVNODE&gt;

 create a new node with several informations (more details on the arguments below).
  * 

&lt;DNVEDGE&gt;



&lt;/DNVEDGE&gt;

 create a new edge, connecting two nodes together (more details on the arguments below).

## Arguments ##
### Level tag ###
As explained above, the level tag is used to define several layers. Each layer will have the following arguments :
  * value : layer number
  * numberOfNodes : number of nodes in that layer
  * numberOfEdges : number of edges in that layer

### DNV Node ###
For each nodes part of a level or layer, you can specify a lot of arguments
  * id : unique number assigned to a node or edge
  * level : used for clustering, each level represents a layer of abstraction
  * position : coordinates of the node, the first value define its position on the X-axis and the second one on the Y-axis
  * color : define the node color, for those of you who are not familiar with LSL, the three number represent the Red, Green and Blue colors. Each value is the percentage of color, for example 50% of Red will be 0.5.
  * size : a node can have a different size according to its importance in the graph, its size is a float number so you can specify one number after the come such as : 2,3 or 8,1
  * label : it's always good to know which node is which, so for this assign the node a label that will be display on it
  * mass : used to change the way the layout algorithms act on the node (only used by some layout algorithms)
  * fixed : some nodes can be at a fixed position, so according its type assign it a "true" or "false" value
  * icon : images can be rendered into the node, it can either be an image stored on your computer or a link towards the web
  * type : only used by TopicNets, can be useful if you need to get all nodes of a certain type, because the nodes are stored in a Hashtable by type
  * BBid : the regular id is an integer, but this one is a string. Can be useful if you need to be able to quickly access a node based on a string id.
  * force label : whether or not the label is forced to be displayed
  * alpha : transparency setting
  * label color : change the label color, same LSL process as for color and "null" value if no color
  * label outline color : change the label outline color, LSL technology for the color and "null" value otherwise
  * outline color : change the node outline color, LSL and "null" otherwise
  * label size : if you need bigger labels, edit the font height
  * expandable : clustering argument
  * hide label background : boolean value to hide label background
  * curved label : you might need a curved label, boolean value
  * visible : a node can be part on a graph or layer and be invisible, for this set its visible value to false
  * selected : if true, the node will be selected when the graph is loaded

### DNV Edge ###
For each nodes part of a level or layer, you can specify a lot of arguments
  * id : unique number assigned to a node or edge
  * level : used for clustering, each level represents a layer of abstraction
  * length : specify the edge length
  * k : clustering argument for the K-most clustering
  * from : departure node
  * to : arrival node
  * label : it's always good to know which node is which, so for this assign the node a label that will be display on it
  * bidirectional : boolean value in order to set the edge as bi-directional
  * BBid : the regular id is an integer, but this one is a string. Can be useful if you need to be able to quickly access a node based on a string id.
  * color : set the edge color, LSL technology
  * force label : whether or not the label is forced to be displayed
  * type : only used by TopicNets, can be useful if you need to get all nodes of a certain type, because the nodes are stored in a Hashtable by type
  * hassetcolor : boolean, true if a color is already set for the edge
  * alpha : transparency setting
  * label color : change the label color, same LSL process as for color and "null" value if no color
  * label outline color : change the label outline color, LSL technology for the color and "null" value otherwise
  * label size : if you need bigger labels, edit the font height
  * thickness : modify the edge thickness
  * visible : a node can be part on a graph or layer and be invisible, for this set its visible value to false

_Always be careful with the ids, each object (either nodes or edges) has its own id so don't restart from 0 or 1 when you create the edges or you will get an error message._

# Uploader #
As mentioned on the main page, an uploader has been implemented in order to ease you to visualize your datasets. The uploader will transform your dataset file into a .DNV file and will automatically be uploaded on the application, therefore you will visualize your datas right after the upload. Three types of files are allowed (please clic on any link below to get more informations about the file structure accepted by the uploader :
  * CSV files, for more informations on the CSV file structure accepted by the uploader clic on the following link [CSVtoDNV](CSVtoDNV.md)
  * TUP files, for more informations on the TUP file structure accepted by the uploader clic on the following link [TUPtoDNV](TUPtoDNV.md)

## How to upload a file ##
  1. Once you have your file ready to be uploaded on our server, go to the following URL : http://eire.mat.ucsb.edu:8080/WiGi/upload.faces.
You will arrive on the upload page, see image below :
<img src='http://wigis.googlecode.com/files/upload.png' alt='upload' />
  1. Simply clic on the +add button and select the file to convert and upload.
  1. Finally, once the upload is done, clic the "I'm feeling visual button", the file will be converted into a .DNV file and you will automatically be redirected to the application with your graph loaded as shown below.
<img src='http://wigis.googlecode.com/files/upload_done.png' alt='upload done' />