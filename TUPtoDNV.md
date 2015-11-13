# Convert a TUP file to a DNV file #
This page will explain the correct structure of a .tup file to be uploaded to the application.

## TUP structure ##
The .tup structure is one of the easiest one. All you have to do is list edges as follow :
```
0,1
1,2
2,3
3,0
1,3
0,2
```
For the above example, six edges are listed with for each, an id corresponding to a 'from' node (first row) and an id corresponding to a 'to' node (second row). The uploader will automatically create all nodes and link them by their corresponding edge.
This technique is easier to implement but unfortunately less customizable (no label, color or size).

## Upload ##
  * An uploader as been implemented in order to allow users to visualize their own data. The uploader can be found at the following URL : http://eire.mat.ucsb.edu:8080/WiGi/upload.faces
  * You might now visualize the following :
<img src='http://wigis.googlecode.com/files/upload.png' alt='upload' />
  * Simply clic the '+add' button and select your .tup file
  * The uploader will automatically upload your .tup file onto our server and when you are ready to visualize it, clic the 'I'm feeling visual' button. According to your uploaded file, it might take time to convert the .tup file to a .DNV but once it is done, you will automatically be redirect to the WiGis home page and your graph will be loaded as follow :
<img src='http://wigis.googlecode.com/files/upload_tup.png' alt='upload tup' />