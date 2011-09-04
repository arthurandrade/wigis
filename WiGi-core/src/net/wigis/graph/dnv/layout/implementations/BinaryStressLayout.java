package net.wigis.graph.dnv.layout.implementations;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.JNILib;
import net.wigis.graph.GraphsPathFilter;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.layout.interfaces.LayoutInterface;
import net.wigis.graph.dnv.layout.interfaces.SimpleLayoutInterface;
import net.wigis.graph.dnv.utilities.Vector2D;
import net.wigis.settings.Settings;



public class BinaryStressLayout implements SimpleLayoutInterface {

	public static final String LABEL = "Binary Stress Layout";
	private static double BSthreshold = 0.001;
	private static double CGthreshold = 1e-10;
	private static int maxIteration = 20;
	private double[] x_pos;
	private double[] y_pos;
	private double[] treex_pos;
	private double[] treey_pos;
	private double[] cosB;
	private double[] sinB;
	private double[] treecosB;
	private double[] treesinB;
	private Map<Integer,ArrayList<Integer>> indexToNeighbors;
	
	
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
	 * generate the random init layout positions
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
	/* 
	 * map the unique nodes id to index range 0 ~ nodes.size() - 1
	 * use the index value of the node as hash key, hash value is the neighbors' indexes of the node
	 */
	private void printArray(double[] arr,String name){
		System.out.println("printing " + name);
		for(int i = 0;i < Math.min(10,arr.length); i++){
			System.out.println(arr[i]);
		}
		System.out.println();
		
	}
	private void mapNodeIndex(List<DNVNode> nodes){
		Map<Integer,Integer> idToIndex = new HashMap<Integer,Integer>();
		indexToNeighbors = new HashMap<Integer,ArrayList<Integer>>();
		int index = 0;
		//sort the nodes according to their degree in descending order
		Collections.sort(nodes,new Comparator<DNVNode>(){

			@Override
			public int compare(DNVNode arg0, DNVNode arg1) {
				// TODO Auto-generated method stub
				return (arg0.getDegree() > arg1.getDegree()? -1 : (arg0.getDegree() == arg1.getDegree() ? 0 : 1));
			}
			
		});
		double[] gap = new double[nodes.size() - 1];
		for(index = 0; index < nodes.size() - 1; index++){
			idToIndex.put(nodes.get(index).getId(), index);
			gap[index] = nodes.get(index + 1).getDegreeCentrality() - nodes.get(index).getDegreeCentrality(); 
		}
		idToIndex.put(nodes.get(index).getId(), index);
		
		index = 0;
		for(DNVNode node : nodes){
			List<DNVNode> neighbors = node.getNeighbors();
			indexToNeighbors.put(index, new ArrayList<Integer>());
			for(DNVNode neighbor : neighbors){
				if(!neighbor.equals(node)){
					indexToNeighbors.get(index).add(idToIndex.get(neighbor.getId()));
				}
			}
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
		/*treecosB = new double[n];
		treesinB = new double[n];
		Bound range = getRange(treex_pos, treey_pos);
		range.print();*/
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
			//cosB[i] = B[0];
			//sinB[i] = B[1];
			//System.out.println(cosB[i] + " " + sinB[i] );
		}
		//getRange(x_pos,y_pos);
		
		
		/*for(int i = 0; i < n; i++){
			for(int j = i + 1; j < n; j++){
				double dist = (double) Math.sqrt((x_pos[i] - x_pos[j]) * (x_pos[i] - x_pos[j]) + (y_pos[i] - y_pos[j]) * (y_pos[i] - y_pos[j]));
				cosB[i] += (x_pos[i] - x_pos[j]) / dist;
				sinB[i] += (y_pos[i] - y_pos[j]) / dist;
				cosB[j] += (x_pos[j] - x_pos[i]) / dist;
				sinB[j] += (y_pos[j] - y_pos[i]) / dist;
			}
			//System.out.println("x_pos " + x_pos[i] + " y_pos " + y_pos[i]);
		}
		printArray(treecosB, "treecosB");
		printArray(cosB, "cosB");
		printArray(treesinB, "treesinB");		
		printArray(sinB, "sinB");*/
		//System.out.println("norm2 of diff cosB " + vecNorm(addvec(treecosB,cosB,false)));
		//System.out.println("norm2 of diff sinB " + vecNorm(addvec(treesinB,sinB,false)) + "\n\n\n");
	}
	
	private void passMatrix(int n, double alpha){
		long startTime = System.currentTimeMillis();
		JNILib.initMatrix(n, alpha);
		for(int i = 0; i < n; i++){
			ArrayList<Integer> neighbors = indexToNeighbors.get(i);
			int[] neighborArr = new int[neighbors.size()];
			for(int j = 0; j < neighbors.size(); j++){
				neighborArr[j] = neighbors.get(j);
			}
			JNILib.passValue(i, neighbors.size(), neighborArr);
		}
		long endTime = System.currentTimeMillis();
		System.out.println("construct mat took " + (endTime - startTime) + " miliseconds");
		startTime = System.currentTimeMillis();
		JNILib.decomMatrix();
		endTime = System.currentTimeMillis();
		System.out.println("LU decoposition took " + (endTime - startTime) + " miliseconds");
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
			//System.out.println("r norm: " + rsnew);
			//double relNorm = vecNorm(addvec(b, mat_mul_vec(alpha, res),false));
			if(Math.sqrt(rsnew) < CGthreshold){
				break;
			}
			p = addvec(r, p, rsnew / rsold, true);
			rsold = rsnew;
		}
		//double relNorm = vecNorm(addvec(b, mat_mul_vec(alpha, res),false));
		//System.out.println("after cg " + relNorm);
		return res;
	}
	private void runBSMLayout(DNVGraph graph, int level){	
		long startTime = System.currentTimeMillis();
		List<DNVNode> nodes = graph.getNodes(level);
		int n = nodes.size();
		mapNodeIndex(nodes);
		//get the default layout of the graph, store positions in x_pos and y_pos
		int index = 0;
		generateInitRandomCoord(n);
		
		/*x_pos = new double[n];
		y_pos = new double[n];
		for(DNVNode node : nodes){
			Vector2D pos = node.getPosition();
			//treex_pos[index] = 
			x_pos[index] = pos.getX();
			//treey_pos[index] = 
			y_pos[index] = pos.getY();
			index++;
		}*/
		Bound range = getRange(x_pos, y_pos);	
		for(int i = 0; i < n; i++){
			double[] constrainedPos = range.constrain(x_pos[i], y_pos[i]);
			x_pos[i] = constrainedPos[0];
			y_pos[i] = constrainedPos[1];
		}
		calcCosSin();
		//getRange(cosB,sinB);
		//calcCosSin_quadtree();
		//getRange(cosB,sinB);
		
		int iter = 0;
		double alpha = n;
		//passMatrix(n, alpha);		
		for(; iter < maxIteration; iter++){
			System.out.println("running iteration " + iter);
			//printArray(x_pos, "x_pos");
			double[] newx_pos = ConjugateGradient(alpha, x_pos, cosB);//JNILib.LUSolver(cosB);
			//printArray(y_pos, "y_pos");
			double[] newy_pos = ConjugateGradient(alpha, y_pos, sinB);//JNILib.LUSolver(sinB);
			//System.out.println("\nxrange");
			range = getRange(newx_pos, newy_pos);
			//System.out.println("\n");
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
			
			calcCosSin();
			//getRange(cosB,sinB);
			//calcCosSin_quadtree();
			//getRange(cosB,sinB);
			
			
			
		}
		/*for(; iter < maxIteration; iter++){
			System.out.println("running iteration " + iter);
			double[] newx_pos = JNILib.LUSolver(cosB);//ConjugateGradient(alpha, x_pos, cosB);
			double relNorm = vecNorm(addvec(cosB, mat_mul_vec(alpha, newx_pos),false));
			System.out.println("norm of Ax-cosB " + relNorm);
			double[] newy_pos = JNILib.LUSolver(sinB);//ConjugateGradient(alpha, y_pos, sinB); 
			relNorm = vecNorm(addvec(sinB, mat_mul_vec(alpha, newy_pos),false));
			System.out.println("norm of Ay-sinB " + relNorm);
			//x_pos = ConjugateGradient(alpha, x_pos, cosB);
			//y_pos = ConjugateGradient(alpha, y_pos, sinB); 
			double[] diffx = addvec(newx_pos, x_pos, false);
			double[] diffy = addvec(newy_pos, y_pos, false);

			if(vecNorm(diffx) / normx < BSthreshold && vecNorm(diffy) / normy < BSthreshold){
				x_pos = newx_pos;
				y_pos = newy_pos;
				break;
			}
			x_pos = newx_pos;
			y_pos = newy_pos;
			//normx = (double) Math.sqrt(vecNorm(x_pos));
			//normy = (double) Math.sqrt(vecNorm(y_pos));
			normx = vecNorm(x_pos);
			normy = vecNorm(y_pos);
			try{
				calcCosSin();
			}catch(StackOverflowError e){
				System.out.println("stack overflow at iter " + iter);
				break;
			}
			//alpha = 1f;
		}*/
		
		long endTime = System.currentTimeMillis();
		long consumeTime = endTime - startTime;
		System.out.println(LABEL + " finished in " + consumeTime + " milliseconds. " + iter + " iterations " +nodes.size() + " nodes");
		System.out.println(consumeTime / (double)iter + " milliseconds per iteration\n");
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
		//long startTime = System.currentTimeMillis();
		runBSMLayout(graph, level);		
		//long endTime = System.currentTimeMillis();
		//System.out.println("finish running test layout");
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
