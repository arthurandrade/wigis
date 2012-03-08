package net.wigis.graph.dnv.layout.implementations;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.wigis.graph.GraphsPathFilter;
import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.layout.interfaces.LayoutInterface;
import net.wigis.graph.dnv.layout.interfaces.SimpleLayoutInterface;
import net.wigis.graph.dnv.utilities.Timer;
import net.wigis.graph.dnv.utilities.Vector2D;
import net.wigis.settings.Settings;



public class BinaryStressLayout implements SimpleLayoutInterface {

	public static final String LABEL = "Binary Stress Layout";
	private static double BSthreshold = 0.001;
	private static double CGthreshold = 1e-5;
	private static int maxIteration = 20;
	private double[] x_pos;
	private double[] y_pos;
	private double[] treex_pos;
	private double[] treey_pos;
	private double[] cosB;
	private double[] sinB;
	private Map<Integer,ArrayList<Integer>> indexToNeighbors;
	
	private BufferedWriter writer;
	@Override
	public void setOutputWriter(BufferedWriter writer) {
		// TODO Auto-generated method stub
		this.writer = writer;
	}
	
	@Override
	public String getLabel() {
		return LABEL;
	}
	private double vecInnerProduct(double[] vec1, double[] vec2){
		double res = 0.f;
		for(int i = 0; i < vec1.length; i++){
			res += vec1[i] * vec2[i];
		}
		return res;
	}
	/*
	 * norm2 of a vector
	 */
	private double vecNorm(double[] vec){
		double res = 0.f;
		for(int i = 0; i < vec.length; i++){
			res += vec[i] * vec[i];
		}
		return res;
	}
	/*
	 * if add = true, calculate vec1 + factor * vec2
	 * otherwise calculate vec1 - factor * vec2
	 */
	private double[] addvec(double[] vec1, double[] vec2, double factor, boolean add){
		double[] res = new double[vec1.length];
		if(add){
			for(int i = 0; i < vec1.length; i++){
				res[i] = vec1[i] + factor * vec2[i];
			}
		}else{
			for(int i = 0; i < vec1.length; i++){
				res[i] = vec1[i] - factor * vec2[i];
			}		
		}
		return res;
	}
	/*
	 * get the boundary of the layout at the current iteration
	 */
	private Bound getRange(double[] x, double[] y){
		double xleft, xright, ytop, ybottom;
		ytop = xleft =  Double.MAX_VALUE;
		
		xright = ybottom = -Double.MAX_VALUE;
		//System.out.println("xleft " + xleft + " xright " + xright + " ytop  " + ytop + " ybottom" + ybottom);
		for(int i = 0; i < x_pos.length; i++){
			if(xleft > x[i]){
				xleft = x[i];
			}
			if(xright < x[i]){
				xright = x[i];
			}
			if(ytop > y[i]){
				ytop = y[i];
			}
			if(ybottom < y[i]){
				ybottom = y[i];
			}
		}
		//System.out.println("range " + xleft + " " + xright + " " + ytop + " " + ybottom);
		return new Bound(xleft, xright, ytop, ybottom);
	}
	/*
	 * if add = true, calculate vec1 + vec2
	 */
	private double[] addvec(double[] vec1, double[] vec2, boolean add){
		double[] res = new double[vec1.length];
		if(add){
			for(int i = 0; i < vec1.length; i++){
				res[i] = vec1[i] + vec2[i];
			}
		}else{
			for(int i = 0; i < vec1.length; i++){
				res[i] = vec1[i] - vec2[i];
				//System.out.println(vec1[i] + " " + vec2[i]);
			}			
		}
		return res;
	}
	/*
	 * generate the random init layout positions, map everything between -1 to 1
	 */
	private void generateInitRandomCoord(int n){
		Random generator = new Random();
		x_pos = new double[n];
		y_pos = new double[n];
		treex_pos = new double[n];
		treey_pos = new double[n];
		for(int i = 0; i < n; i++){
			treex_pos[i] = x_pos[i] = generator.nextFloat() * 2 - 1;
			treey_pos[i] = y_pos[i] = generator.nextFloat() * 2 - 1;
		}
	}
	
	private void printArray(double[] arr,String name){
		System.out.println("printing " + name);
		for(int i = 0;i < Math.min(10,arr.length); i++){
			System.out.println(arr[i]);
		}
		System.out.println();
		
	}
	
	/* 
	 * map the unique nodes id to index range 0 ~ nodes.size() - 1
	 * use the index value of the node as hash key, hash value is the neighbors' indexes of the node
	 */
	private void mapNodeIndex(List<DNVNode> nodes){
		Map<Integer,Integer> idToIndex = new HashMap<Integer,Integer>();
		indexToNeighbors = new HashMap<Integer,ArrayList<Integer>>();
		int index = 0;

		for(index = 0; index < nodes.size(); index++){
			idToIndex.put(nodes.get(index).getId(), index);
		}
		
		index = 0;
		for(DNVNode node : nodes){
			indexToNeighbors.put(index, new ArrayList<Integer>());
			for(DNVEdge edge : node.getToEdges()){
				indexToNeighbors.get(index).add(idToIndex.get(edge.getFromId()));
			}
			for(DNVEdge edge : node.getFromEdges()){
				indexToNeighbors.get(index).add(idToIndex.get(edge.getToId()));
			}
			/*List<DNVNode> neighbors = node.getNeighbors();
			indexToNeighbors.put(index, new ArrayList<Integer>());
			for(DNVNode neighbor : neighbors){
				if(!neighbor.equals(node)){
					indexToNeighbors.get(index).add(idToIndex.get(neighbor.getId()));
				}
			}*/
			index++;
		}
		
	}
	private void calcCosSin(){
		int n = x_pos.length;
		cosB = new double[n];
		sinB = new double[n];
		for(int i = 0; i < n; i++){
			for(int j = i + 1; j < n; j++){
				double dist = (double) Math.sqrt((x_pos[i] - x_pos[j]) * (x_pos[i] - x_pos[j]) + (y_pos[i] - y_pos[j]) * (y_pos[i] - y_pos[j]));
				cosB[i] += (x_pos[i] - x_pos[j]) / dist;
				sinB[i] += (y_pos[i] - y_pos[j]) / dist;
				cosB[j] += (x_pos[j] - x_pos[i]) / dist;
				sinB[j] += (y_pos[j] - y_pos[i]) / dist;
			}
		}
	}
	private void calcCosSin_quadtree() throws StackOverflowError{
		int n = x_pos.length;
		cosB = new double[n];
		sinB = new double[n];
		Bound range = new Bound(-1, 1, -1, 1);
		QuadTree tree = new QuadTree(range);
		for(int i = 0; i < n; i++){
			tree.addNode(x_pos[i], y_pos[i]);
		}
		tree.finishAdd();		
		for(int i = 0; i < n; i++){
			double[] B = tree.getCosSin(x_pos[i], y_pos[i]);
			cosB[i] = B[0];
			sinB[i] = B[1];
		}
	}

	private double[] ConjugateGradient(double alpha, double[] init, double[] b){
		double[] res = init;
		double[] r = addvec(b, mat_mul_vec(alpha, init), false);
		double[] p = r.clone();
		double rsold = vecNorm(r);
		
		for(int i = 0; i < b.length; i++){
			double[] Ap = mat_mul_vec(alpha, p);
			double alpha_cg = rsold / vecInnerProduct(p, Ap);
			res = addvec(res, p, alpha_cg, true);
			r = addvec(r, Ap, alpha_cg, false);
			double rsnew = vecNorm(r);
			if(Math.sqrt(rsnew) < CGthreshold){
				break;
			}
			p = addvec(r, p, rsnew / rsold, true);
			rsold = rsnew;
		}
		/*double relNorm = vecNorm(addvec(b, mat_mul_vec(alpha, res),false));
		if(relNorm > 10){
			System.out.println("		relNorm for CG is too big!");
		}*/
		//System.out.println("after cg " + relNorm);
		return res;
	}
	public void runBSMLayout(DNVGraph graph, int level){	
		Timer timer = new Timer();
		timer.setStart();
		//float timeForSinCos = 0;
		//float timeForCG = 0;
			
		List<DNVNode> nodes = graph.getNodes(level);
		int n = nodes.size();
		mapNodeIndex(nodes);
		//get the default layout of the graph, store positions in x_pos and y_pos
		int index = 0;
		x_pos = new double[n];
		y_pos = new double[n];
		for(int i = 0; i < nodes.size(); i++){
			x_pos[i] = nodes.get(i).getPosition().getX();
			y_pos[i] = nodes.get(i).getPosition().getY();
		}
		//generateInitRandomCoord(n);
		
		
		Bound range = getRange(x_pos, y_pos);	
		for(int i = 0; i < n; i++){
			double[] constrainedPos = range.constrain(x_pos[i], y_pos[i]);
			x_pos[i] = constrainedPos[0];
			y_pos[i] = constrainedPos[1];
		}
		//timer.setEnd();
		//calcCosSin_quadtree();
		calcCosSin();
		//timer.setEnd();
		//timeForSinCos += timer.getLastSegment(Timer.SECONDS);

		
		int iter = 0;
		double alpha = n;	
		for(; iter < maxIteration; iter++){
			//System.out.println("running iteration " + iter);

			double[] newx_pos = ConjugateGradient(alpha, x_pos, cosB);
			double[] newy_pos = ConjugateGradient(alpha, y_pos, sinB);
			//timer.setEnd();
			//timeForCG += timer.getLastSegment(Timer.SECONDS);
			
			range = getRange(newx_pos, newy_pos);
			for(int i = 0; i < n; i++){
				double[] constrainedPos = range.constrain(newx_pos[i], newy_pos[i]);
				newx_pos[i] = constrainedPos[0];
				newy_pos[i] = constrainedPos[1];
			}
			double diff = vecNorm(addvec(x_pos, newx_pos,false)) + vecNorm(addvec(y_pos, newy_pos,false));		
			/*if(diff < BSthreshold){
				x_pos = newx_pos;
				y_pos = newy_pos;
				System.out.println("diff " + diff);
				break;
			}*/
			x_pos = newx_pos;
			y_pos = newy_pos;
			
			//timer.setEnd();
			//calcCosSin_quadtree();
			calcCosSin();
			//timer.setEnd();
			//timeForSinCos += timer.getLastSegment(Timer.SECONDS);
			
		}
		
		float consumeTime = timer.getTimeSinceStart(Timer.SECONDS);
		System.out.println(LABEL + " finished in " + consumeTime + " seconds. " + iter + " iterations " +nodes.size() + " nodes");
		System.out.println(consumeTime / (double)iter + " seconds per iteration\n");
		
		if(writer != null){
			try {
				//writer.write(LABEL + " finished in " + consumeTime + " seconds. " + iter + " iterations " +nodes.size() + " nodes\n");
				//writer.write(consumeTime / (double)iter + " seconds per iteration\n\n");
				int e = graph.getEdges().size();
				double time = consumeTime;
				writer.write(time + "\t" + time/n + "\t" + time/e + "\t" + time/(n+e) + "\t" + time/(e/n) + "\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//System.out.println("time for sin cos " + timeForSinCos + " seconds");
		//System.out.println("time for CG " + timeForCG + "seconds");
		
		//set the layout positions
		index = 0;
		for(DNVNode node : nodes){
			node.setPosition((float)x_pos[index], (float)y_pos[index]);
			index++;
		}
		
		
		
		
	}
	private double[] mat_mul_vec(double alpha, double[] V){
		double[] res = new double[V.length];
		int nverts = V.length;
		double sumV = 0.f;
		for(int i = 0; i < nverts; i++){
			sumV += V[i];
		}
		
		for(int i = 0; i < nverts; i++)
		{	
			ArrayList<Integer> neighbors = indexToNeighbors.get(i);
			double temp = (nverts + alpha * neighbors.size())* V[i] - sumV ;			
			for(int neighbor: neighbors){
				temp -= alpha * V[neighbor];
			}
			res[i] = temp;
		}		
		return res;
	}
	@Override
	public void runLayout(DNVGraph graph, int level) {
		System.out.println( "Running Binary Stress layout" );
		
		runBSMLayout(graph, level);
	}
	
	/*public static void main( String args[] )
	{
		GraphsPathFilter.init();
		File directory = new File( Settings.GRAPHS_PATH );
		String[] files = directory.list( new FilenameFilter()
		{

			@Override
			public boolean accept(File arg0, String arg1) {
				if( arg1.endsWith( ".dnv" ) )
				{
					return true;
				}
				
				return false;
			}
			
		});
		for( String file : files )
		{
			//public static LayoutInterface[] LAYOUT_ALGORITHMS = { new BinaryStressLayout(), new FruchtermanReingold(), new CircularLayout(), new DisjointGraphLayout(), new Springs() };

			
			DNVGraph graph = new DNVGraph( Settings.GRAPHS_PATH + file );
			BinaryStressLayout bsl = new BinaryStressLayout();
			bsl.runLayout( graph, 0 );
			//FruchtermanReingold fr = new FruchtermanReingold();
			//fr.runLayout(graph, 0);
		}
	}*/
	

}
