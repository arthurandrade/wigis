# About the WiGis Project #

## UPDATE 2013:  THIS PROJECT IS NO LONGER ACTIVELY MAINTAINED.  PLEASE CONTACT JOD AT CS.UCSB.EDU FOR INFORMATION ##
Based at the Four Eyes Lab, Dept. of Computer Science, University of California Santa Barbara, the WiGis project centers around visualization of large-scale, highly interactive graphs in a user's web browser. Our software is delivered natively in your web browser and does not require any plug-ins or add-ons. Our method produces clean, smooth animation in a browser through asynchronous data transfer (AJAX), and access to rich server side resources without the need for technologies such as Flash, Java Applets, Flex or Silverlight. We believe that our new techniques have broad reaching potential across the web.

# Uses #
This software is useful for exploring any connected network data.  The core code has been extended to work with many different applications, from interactive interfaces for recommendation systems like Amazon's, to visualization of semantic web data in Wikis like this one.  The software has also been used in intelligence analysis and for visualization of data in biological fields such as gene sequencing which frequently require large scale connectivity analysis.

# Download and Setup Instructions #
The following tutorials explain how to setup and configure your environment to run the application, the first tutorial is for developers:
[DevelopmentInstructions](DevelopmentInstructions.md) and the second one is for those who just want to run the application locally, without a dev environment : [UsersInstructions](UsersInstructions.md).

# Data #
In order to easily visualize your sets of data, an uploader has been implemented to help you convert your datasets into readable files for the application. Our uploader can convert three types of files :
  * .csv files,
  * .tup files.
For more informations about the structure of a .DNV file please have a look at the following page [Data](Data.md).

For more informations on the upload, conversion and structure of a .csv file, go to the following page [CSVtoDNV](CSVtoDNV.md).
For more informations on the upload, conversion and structure of a .csv file, go to the following page [TUPtoDNV](TUPtoDNV.md).



# Features and Architecture #
<img src='http://wigis.googlecode.com/files/Architecture.png' alt='Logo' />

# Demos #
Online demos of WiGis can usually be seen at www.wigis.net.  This is a dev site and does have occasional downtime.

# Quick start #
## For developers: ##
**WiGi and WiGi-core are two Eclipse Java Web projects.** Instructions for checking out the latest versions can be found under the "source:" tab in the horizontal menu bar above.
**The system front end is is coded using a JSF framework, and these components are contained in the WiGi Eclipse project.**WiGis project relies on the core libraries (such as layout, clustering, analysis, statistics and other algorithms) which are contained in WiGi-core.
**You will need to place WiGi-core on the build path of your WiGi project.**A JBoss runtime environment and JBossTools is required to run directly from Eclipse


## To Run: ##
#  Install JBoss.  We are currently using version 5.1.
#  Deploy the application file WiGi.war to the JBoss server.  (usually to JBOSS\_HOME/server/default/deploy)
#  if you have JBoss configured with Eclipse, this can be done by right-clicking on the server tab.  You can also deploy by  manually copying the war file to the server.  In some cases a server restart may be required.
#  In any standard web browser, navigate to the following url:
`http://localhost:8080/WiGi/wigi/WiGiViewerPanel.faces'
# The system has been tested in recent versions of Chrome, Safari and Firefox.  In some browsers, a pop-up box may appear to say that the system is not supported.