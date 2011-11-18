package net.wigis.graph.data.utilities;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;

import net.wigis.graph.GraphsPathFilter;
import net.wigis.graph.dnv.*;
import net.wigis.settings.Settings;
import net.wigis.yun.Pair;
public class ConvertMtxToDNV {
	private static String path;
	public static void convert(String filename){
		System.out.println("converting " + filename);
		File file = new File(path + filename);
		if(file.exists()){
			String line;
			FileReader fr;
			try{
				fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);
				line = br.readLine();
				DNVGraph graph = new DNVGraph();
				while(line != null && line.startsWith("%")){
					line = br.readLine();
				}
				if(line != null){
					String[] tmp = line.split(" ");
					if(tmp.length < 3){
						System.out.println("dimmension info error!");
					}
					int numberOfNodes = Integer.parseInt(tmp[0]);
					int numberOfNodes2 = Integer.parseInt(tmp[1]);
					int numberOfEdges = Integer.parseInt(tmp[2]);
					if(numberOfNodes != numberOfNodes2){
						System.out.println("dimmension doesn't match!");
					}else{
						for(int i = 0; i < numberOfNodes; i++){
							DNVNode node = new DNVNode(graph);
							graph.addNode(0, node);
						}
						line = br.readLine();
						HashSet<Pair<Integer, Integer>> edgeMap = new HashSet<Pair<Integer, Integer>>();
						while(line != null){
							String[] indexes = line.split(" ");
							if(indexes.length != 2){
								System.out.println("edge index error!");
								break;
							}
							int ind1 = Integer.parseInt(indexes[0]) - 1;
							int ind2 = Integer.parseInt(indexes[1]) - 1;
							
							if(!edgeMap.contains(new Pair<Integer, Integer>(ind2, ind1))){
								edgeMap.add(new Pair<Integer, Integer>(ind1, ind2));								
								DNVEdge edge = new DNVEdge(graph);
								edge.setFrom(graph.getNode(0, ind1));
								edge.setTo(graph.getNode(0, ind2));
								graph.addEdge(0, edge);
							}
							line = br.readLine();
						}
						graph.writeGraph(path + "florida_" + filename.substring(0, filename.length() - 3) + "dnv");
					}
					
				}
				br.close();
				fr.close();
				System.out.println("finish converting " + filename);
			}catch(FileNotFoundException e){
				e.printStackTrace();
			}catch(IOException e){
				e.printStackTrace();
			}
			
		}
		
		/*try {
			InputStream inp = new FileInputStream(filename);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("FileNotFoundException " + filename);
			e.printStackTrace();
		}*/
		
	}
	public static void main(String[] args){
		GraphsPathFilter.init();
		path = Settings.GRAPHS_PATH + "florida/mtx/";
		File directory = new File(path);
		String[] files = directory.list( new FilenameFilter()
		{

			@Override
			public boolean accept(File arg0, String arg1) {
				if( arg1.endsWith( ".mtx" ) )
				{
					return true;
				}
				
				return false;
			}
			
		});
		for( String file : files )
		{
			convert(file);
		}
	}
}
