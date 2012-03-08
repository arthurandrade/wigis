package net.wigis.stats;

import java.util.ArrayList;
import java.util.List;

import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.utilities.Timer;
import net.wigis.greg.ShortestPath;

public class PairStatistics {

	
	/**
     * 
     * Method computing the shortest path of source node
     * 
     * @param graph
     * @param source
     * @return ArrayList of ShortestPath objects
     */
    private static ArrayList<ShortestPath> ShortestPath(ArrayList<DNVNode> list, DNVNode source){
    	
    	ArrayList<ShortestPath> SP = new ArrayList<ShortestPath>();
    	
    	
    	ShortestPath object = new ShortestPath();
    	int index = 0;
    	int alt = 0;
    	boolean end = false;
    	boolean stop = false;
    	
    	ShortestPath node1 = new ShortestPath();
    	ShortestPath node2 = new ShortestPath();
    	
    	//Initialization
    	for(int i=0;i<list.size();i++){
    		object = new ShortestPath();
    		object.setSource(list.get(i));
    		object.setDistance(999999999);
    		SP.add(object);
    	}
    	
    	//get the source index and set its distance to 0
    	for(int i=0;i<SP.size();i++){
    		if(source.toString().compareToIgnoreCase(SP.get(i).getSource().toString()) == 0){
    			SP.get(i).setDistance(0);
    		}
    			
    	}   	
    	   	
    	while(list.isEmpty() == false && end == false){    		
    		//get the node with the lowest distance value
    		node1 = getLowerDistanceValueObject(SP,list);
    		
    		//if the visited node's distance == 9999999999 then we break
    		if(node1.getDistance() != 999999999){
    			
    			list.remove(list.indexOf(node1.getSource()));
        		//get all neighbors of the current node
    			List<DNVNode> tmp = new ArrayList<DNVNode>(node1.getSource().getNeighbors(true));
        		for(int i=0;i<tmp.size();i++){
        			//get the DNVNode source value
        			DNVNode tempNode = new DNVNode();
        			
    				tempNode = tmp.get(i);
        			//change the previous node2 by the new one
        			for(int x=0;x<SP.size();x++){
        				if(SP.get(x).getSource().toString().compareToIgnoreCase(tempNode.toString()) == 0){
        					node2 = SP.get(x);
        				}
        			}     			
        			
        			//get the new distance between both nodes        			
        			alt = node1.getDistance()+1;
        			//if new distance is lower than the previous one, set the new distance and the new previous node
        			if(alt < node2.getDistance()){
        				node2.setDistance(alt);
        				node2.setPreviousNode(node1.getSource());
        			}
        		}
    		}else{
    			end = true;
    		}
    	}
    	
    	return SP;
    }
    
    
    
    
    
    /**
     * 
     * 
     * 
     * @param graph
     * @param source
     * @param destination
     */
    static void highlightPath(ArrayList<ShortestPath> SP, DNVNode source, DNVNode destination){

    	ShortestPath currentNode = new ShortestPath();
    	boolean loop = true;
    	
    	//Look for the destination node into the SP list
    	for (int i=0;i<SP.size();i++){
    		if(SP.get(i).getSource().toString().compareToIgnoreCase(destination.toString()) == 0){
    			currentNode = SP.get(i);
    		}
    	}
    	
    	destination.setHighlighted(true);

    	
    	//Loop until the source node is reached
    	while(loop){
    		//Get the current node's previous node
    		DNVNode previousNode = currentNode.getPreviousNode();
    		DNVNode nodeVisited = currentNode.getSource();
    		
    		
    		DNVEdge edge = nodeVisited.getEdgeToNeighbor(previousNode.getId());
    		edge.setHighlighted(true);
    		
    		
    		
    		previousNode.setHighlighted(true);
    		previousNode.setLabelColor("#FFFFFF");
    		
    		
    		
    		
    		//Look for the destination node into the SP list
        	for (int i=0;i<SP.size();i++){
        		if(SP.get(i).getSource().toString().compareToIgnoreCase(previousNode.toString()) == 0){
        			currentNode = SP.get(i);
        		}
        	}        	
        	
    		if(source.toString().compareTo(previousNode.toString()) == 0){
    			loop = false;
    		}
    	}
    }
    
    
    
    
    
    
    
    
    
    /**
     * Return the distance value between the source node and the destination node
     * @param graph
     * @param source
     * @param destination
     * @return String distance
     */
    static String computeShortestPathDistance(ArrayList<ShortestPath> SP,DNVNode source, DNVNode destination){
    	
    	String res = "";
    	
    	for(int i=0;i<SP.size();i++){
    		if(SP.get(i).getSource().toString().compareToIgnoreCase(destination.toString()) == 0){
    			res = Integer.toString(SP.get(i).getDistance());
    		}
    	}

    
    	return res;
    }
    
    
    
    
    
    
    
    
    /**
     * Get the lowest distance of shortest path list object
     * 
     * @param SP
     * @return int index
     */
    private static ShortestPath getLowerDistanceValueObject(ArrayList<ShortestPath> SP, ArrayList<DNVNode> list){
    	int res = 999999999;
    	int index = 0;
    	
    	
    	for(int i=0;i<SP.size();i++){
    		for(int y=0;y<list.size();y++){
    			if(SP.get(i).getSource().toString().compareToIgnoreCase(list.get(y).toString()) == 0){
    				if(SP.get(i).getDistance() < res){
    	    			index = i;
    	    			res = SP.get(i).getDistance();
    	    		}
    			}
    		}

    	}
    	return SP.get(index);
    }
    
    
    
    
    
    
    
    /**
     * 
     * Method returning an ArrayList of all nodes from a cluster
     * 
     * @param graph, the whole graph
     * @param start, a node from the cluster thaht we want to get
     * @return result, ArrayList of DNVNodes of all the nodes from a cluster 
     */
    private static ArrayList<DNVNode> getCluster(DNVGraph graph, DNVNode start){
    	ArrayList<DNVNode> result = new ArrayList<DNVNode>();
    	ArrayList<DNVNode> nextNodes = new ArrayList<DNVNode>();
    	ArrayList<DNVNode> tmp = new ArrayList<DNVNode>();
    	DNVNode currentNode = new DNVNode();
    	boolean found = false;
    	
    	//set up the start node
    	nextNodes.add(start);
    	
    	//while all the nodes hasn't been explored yet
    	while(nextNodes.isEmpty() == false){
    		//get the current node
    		currentNode = nextNodes.get(0);
    		nextNodes.remove(0);
    		if(result.contains(currentNode) == false){
    			result.add(currentNode);
	    		tmp = (ArrayList<DNVNode>) currentNode.getNeighbors(true);
	    		//loop on all the visible neighbors
	    		for(int i=0;i<tmp.size();i++){
	    			if(result.contains(tmp.get(i))){
	    				found = true;
	    			}
	    			//if the neighbor hasn't been visited yet, add it up at the nextNodes list
	    			if(found == false){
	    				nextNodes.add(tmp.get(i));
	    			}
	    			found = false;
	    		}
    		}
    	}
    	return result;
    }
    
    
    
    
    /**
     * 
     * Tell whether the destination node is part of the cluster found
     * 
     * @param list
     * @param destination
     * @return
     */
    private static boolean isPartOfCluster(ArrayList<DNVNode> list, DNVNode destination){
    	boolean result = list.contains(destination);
    	return result;
    }
    
    
    
    
    
    static String computeShortestPath(DNVGraph graph, DNVNode source, DNVNode destination){
		
    	
    	
    	ArrayList<DNVNode> nextNodes = new ArrayList<DNVNode>();
    	nextNodes.add(source);
    	ArrayList<DNVNode> result = new ArrayList<DNVNode>();
    	
    	Timer timer = new Timer( Timer.MILLISECONDS );
    	timer.setStart();
    	ArrayList<DNVNode> clusterRecursive = new ArrayList<DNVNode>(getClusterRecursive(nextNodes, source, result));
    	timer.setEnd();
    	System.out.println( "Recursive clustering took " + timer.getLastSegment( Timer.SECONDS ) + " seconds." );
    	
    	
    	
    	
    	
    	
    	//Get the cluster nodes
    	timer.setStart();
    	ArrayList<DNVNode> cluster = new ArrayList<DNVNode>(getCluster(graph,source));
    	timer.setEnd();
    	System.out.println( "Clustering took " + timer.getLastSegment( Timer.SECONDS ) + " seconds." );
    	ArrayList<ShortestPath> SP = new ArrayList<ShortestPath>();
    	String distance = "";
    	
    	//check whether the source and destination nodes are in the same cluster
    	if(isPartOfCluster(clusterRecursive,destination)){
    		//call to the main shortest path method
    		SP = ShortestPath(clusterRecursive,source);
    		
    		//call to the shortest path distance
    		distance = computeShortestPathDistance(SP, source, destination);
    		
    		//call to the highlight method
    		highlightPath(SP, source, destination);
    	}
    	else{
    		distance = "Please select nodes from the same cluster";
    	}
    	return distance;
    	
    	
    }
    
    
    
    static ArrayList<DNVNode> getClusterRecursive(ArrayList<DNVNode> nextNodes, DNVNode currentNode, ArrayList<DNVNode> result){
    	
    	ArrayList<DNVNode> tmp = new ArrayList<DNVNode>();
    	tmp = (ArrayList<DNVNode>) currentNode.getNeighbors(true);
    	
    	int index = nextNodes.indexOf(currentNode);
    	nextNodes.remove(index);
    	
    	if(result.contains(currentNode) == false){
    		result.add(currentNode);
    		
    		for(int i=0;i<tmp.size();i++){
        		if(result.contains(tmp.get(i)) == false ){
        			currentNode = tmp.get(i);
        			nextNodes.add(currentNode);
        			getClusterRecursive(nextNodes, currentNode, result);
        		}
        	}
    		
    	}
    	
    	return result;
    	
    }
}
