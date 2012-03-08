package net.wigis.graph.dnv.layout.implementations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;
import net.wigis.graph.dnv.utilities.Timer;

public class BinaryStressCalc {
	private HashSet<DNVNode> nodes;
	private int nodeListSize;
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
	
	
	public BinaryStressCalc(HashSet<DNVNode> nodes){
		this.nodes = nodes;
		nodeListSize = nodes.size();
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
		
		for(int i = 0; i < x.length; i++){
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
	private void generateInitRandomCoord(){
		Random generator = new Random();
		x_pos = new double[nodeListSize];
		y_pos = new double[nodeListSize];
		treex_pos = new double[nodeListSize];
		treey_pos = new double[nodeListSize];
		for(int i = 0; i < nodeListSize; i++){
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
	private void mapNodeIndex(HashSet<DNVNode> nodes){
		Map<Integer,Integer> idToIndex = new HashMap<Integer,Integer>();
		indexToNeighbors = new HashMap<Integer,ArrayList<Integer>>();
		int index = 0;
		for(DNVNode node : nodes){
			idToIndex.put(node.getId(), index);
			index++;
		}
		
		index = 0;
		for(DNVNode node : nodes){
			HashSet<DNVNode> neighbors = new HashSet<DNVNode>();
			for(DNVEdge edge : node.getFromEdges()){
				if(nodes.contains(edge.getTo())){
					neighbors.add(edge.getTo());
				}
			}
			for(DNVEdge edge : node.getToEdges()){
				if(nodes.contains(edge.getFrom())){
					neighbors.add(edge.getFrom());
				}
			}
			
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
		//int n = x_pos.length;
		cosB = new double[nodeListSize];
		sinB = new double[nodeListSize];
		for(int i = 0; i < nodeListSize; i++){
			for(int j = i + 1; j < nodeListSize; j++){
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
		Bound range = new Bound(0, 2, 0, 2);
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
		
		//System.out.println("	int length " + init.length);
		
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
		//double relNorm = vecNorm(addvec(b, mat_mul_vec(alpha, res),false));
		//System.out.println("after cg " + relNorm);
		return res;
	}
	public void runBSMLayout(){	
		Timer timer = new Timer();
		timer.setStart();
		
		mapNodeIndex(nodes);

		//get the default layout of the graph, store positions in x_pos and y_pos
		int index = 0;
		generateInitRandomCoord();
		
		Bound range = getRange(x_pos, y_pos);	
		for(int i = 0; i < nodeListSize; i++){
			double[] constrainedPos = range.constrain(x_pos[i], y_pos[i]);
			x_pos[i] = constrainedPos[0];
			y_pos[i] = constrainedPos[1];
		}
		calcCosSin();

		//System.out.println("x_pos length " + x_pos.length);
		//System.out.println("cosB length " + cosB.length);
		
		int iter = 0;
		double alpha = nodeListSize;
	
		for(; iter < maxIteration; iter++){
			System.out.println("running iteration " + iter);
			double[] newx_pos = ConjugateGradient(alpha, x_pos, cosB);//JNILib.LUSolver(cosB);
			double[] newy_pos = ConjugateGradient(alpha, y_pos, sinB);//JNILib.LUSolver(sinB);
			range = getRange(newx_pos, newy_pos);
			for(int i = 0; i < nodeListSize; i++){
				double[] constrainedPos = range.constrain(newx_pos[i], newy_pos[i]);
				newx_pos[i] = constrainedPos[0];
				newy_pos[i] = constrainedPos[1];
			}
			double diff = vecNorm(addvec(x_pos, newx_pos,false)) + vecNorm(addvec(y_pos, newy_pos,false));		
			x_pos = newx_pos;
			y_pos = newy_pos;
			//System.out.println("x_pos " + x_pos[0] + " y_pos " + y_pos[0]);
			if(diff <= BSthreshold){
				break;
			}
			
			calcCosSin();			
		}
		

		timer.setEnd();

		System.out.println("Binary Stress Calculation finished in " + timer.getLastSegment(Timer.SECONDS) + " seconds. " + iter + " iterations " +nodes.size() + " nodes");
		System.out.println(timer.getLastSegment(Timer.SECONDS) / (double)iter + " seconds per iteration\n");
		
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
}
