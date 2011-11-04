package net.wigis.graph.data.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;


import net.wigis.graph.GraphsPathFilter;
import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.utilities.Pair;
import net.wigis.settings.Settings;

public class ConvertPlainPairsToDNV {
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
				int maxIndex = Integer.MIN_VALUE;
				int minIndex = Integer.MAX_VALUE;
				ArrayList<Pair<Integer, Integer>> tupos = new ArrayList<Pair<Integer, Integer>>();
				while(line != null){
					String[] tmp = line.split(" ");
					if(tmp.length != 2){
						System.out.println("pasing error");
						break;
					}
					int ind1 = Integer.parseInt(tmp[0]);
					int ind2 = Integer.parseInt(tmp[1]);
					minIndex = Math.min(minIndex, Math.min(ind1, ind2));
					maxIndex = Math.max(maxIndex, Math.max(ind1, ind2));
					tupos.add(new Pair<Integer, Integer>(ind1, ind2));
					line = br.readLine();
				}
				for(int i = minIndex; i <= maxIndex; i++){
					DNVNode node = new DNVNode(graph);
					graph.addNode(0, node);
				}
				for(Pair<Integer, Integer> eIndex : tupos){
					DNVEdge edge = new DNVEdge(graph);
					edge.setFrom(graph.getNode(0, eIndex.getFirst() - minIndex));
					edge.setTo(graph.getNode(0, eIndex.getSecond() - minIndex));
					graph.addEdge(0, edge);
				}
				graph.writeGraph(path + "tupos_" + filename + ".dnv");
					
				
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
		path = Settings.GRAPHS_PATH + "topos/";
		File directory = new File(path);
		String[] files = directory.list();
		for( String file : files )
		{
			convert(file);
		}
	}
}
