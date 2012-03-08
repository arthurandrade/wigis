package net.wigis.graph.data.utilities;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;

public class ConvertDNVTOGML {
	public static void convert(DNVGraph graph, String filename){
		Map<Integer,Integer> idToIndex = new HashMap<Integer,Integer>();
		List<DNVNode> nodes = graph.getNodes(0);
		for(int index = 0; index < nodes.size(); index++){
			idToIndex.put(nodes.get(index).getId(), index);
		}
		
		FileWriter outputFileWriter;
		try
		{
			outputFileWriter = new FileWriter( filename );
			BufferedWriter outputWriter = new BufferedWriter( outputFileWriter );
			outputWriter.write("graph [\n");
			
			for(int i = 0; i < nodes.size(); i++){				
				outputWriter.write(" node [ id " + i +" ]\n");
			}
			for(DNVEdge edge : graph.getEdges(0)){	
				//edge [ source 1 target 13 ]
				outputWriter.write(" edge [ source " + idToIndex.get(edge.getFrom().getId()) + " target " + idToIndex.get(edge.getTo().getId()) + " ]\n");
			}
			outputWriter.write("]\n");
			outputWriter.close();
			outputFileWriter.close();
		}
		catch( IOException e )
		{
			e.printStackTrace();
			//return false;
		}
	}

}
