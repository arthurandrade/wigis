package net.wigis.graph.dnv.layout.implementations;

import java.util.Arrays;

public class QuadTree {
	Node root;
	public QuadTree(Bound Boundary){
		root = new Node(Boundary, 0);
	}
	public void addNode(double x, double y) throws StackOverflowError{
		if(!root.getBoundary().contains(x, y)){
			System.out.println("\nout of boundary\n");
		}
		root.insert(x, y);
	}
	public void finishAdd(){
		root.trimAndIntegrate();
	}
	public double[] getCosSin(double x, double y){
		return root.distFrom(x, y);
	}
	public void print(){
		root.printNode();
	}
}
class Node{
	private Node[] subNodes;
	private double geox, geoy;
	private int subCnt;
	private Bound boundary;
	private boolean valid;
	private static double threshold = 0.1;
	private int level;
	private int weight;
	public Node(Bound boundary, int level){
		valid = false;
		subCnt = 0;
		this.level = level;
		this.boundary = boundary;
		weight = 1;
	}
	public int getWeight(){
		return weight;
	}
	public int getSubCnt(){
		return subCnt;
	}
	public void setBoundary(Bound boundary){
		this.boundary = boundary;		
	}
	public boolean isValid(){
		return valid;
	}
	public Bound getBoundary(){
		return boundary;
	}
	public void setBoundary(double xleft, double xright, double ytop, double ybottom){
		this.boundary = new Bound(xleft, xright, ytop, ybottom);
	}
	public double[] distFrom(double x2, double y2){
		//double x2 = node.getGeoX();
		//double y2 = node.getGeoY();
		if(!valid){
			System.out.println("error!!!!");
		}
		double[] res = new double[2];
		res[0] = 0.f;
		res[1] = 0.f;
		/*if(subCnt == 0){
			if(Math.abs(x2 - geox) + Math.abs(y2 - geoy) <= 1e-10){
				//System.out.println(x2 + " " + y2 + " near myself");
				return res;
			}
		}*/
		double directDist = (double) Math.sqrt((geox - x2)* (geox - x2) + (geoy - y2) * (geoy - y2));
		if(directDist <= 1e-10){
			return res;
		}
		if(subCnt == 0 || boundary.size() / directDist < threshold ){
			res[0] = weight * (x2 - geox) / directDist;
			res[1] = weight * (y2 - geoy) / directDist;
		}
		else{
			for(int i = 0; i < subCnt; i++){
				double[] subRes = subNodes[i].distFrom(x2, y2);
				res[0] += subRes[0];
				res[1] += subRes[1];
			}
		}
		//System.out.println("node geo " + geox + " " + geoy + " pos " + " " + x2 + " " + y2 + " res " + res[0] + " " + res[1]);
		return res;
	}
	public double getGeoX(){
		return geox;
	}
	public double getGeoY(){
		return geoy;
	}
	public void insert(double x, double y) throws StackOverflowError{

		if(subCnt > 0){
			for(int i = 0; i < subCnt; i++){
				if(subNodes[i].getBoundary().contains(x,y)){
					subNodes[i].insert(x, y);
					return;
				}
			}
		}else if(valid){
			subCnt = 4; //initially split the node to 4 sub nodes
			subNodes = new Node[subCnt];
			Bound[] subBounds = boundary.splitBound();
			for(int i = 0; i < subCnt; i++){
				subNodes[i] = new Node(subBounds[i],level + 1);
			}
			this.insert(geox, geoy);
			this.insert(x, y);			
		}else{
			geox = x;
			geoy = y;
			valid = true;
		}
		
	}
	public Node getSubNode(int i){
		return subNodes[i];
	}
	public void trimAndIntegrate(){
		if(subCnt > 0){
			Node[] newSubNodes = new Node[subCnt];
			int index = 0;
			double x = 0.f;
			double y = 0.f;
			weight = 0;
			for(int i = 0; i < subCnt; i++){
				if(subNodes[i].isValid()){			
					subNodes[i].trimAndIntegrate();
					if(subNodes[i].getSubCnt() == 1){
						subNodes[i] = subNodes[i].getSubNode(0);
					}
					newSubNodes[index] = subNodes[i];
					
					int subWeight = subNodes[i].getWeight();					
					weight += subWeight;
					x += subWeight * subNodes[i].getGeoX();
					y += subWeight * subNodes[i].getGeoY();
					index++;
				}
			}
			if(weight == 0){
				System.out.println("error in trim + subCnt " + subCnt + " index" + index);
				for(int i = 0; i < subCnt; i++){
					subNodes[i].printNode();
				}
			}
			subCnt = index;
			subNodes = Arrays.copyOfRange(newSubNodes, 0, subCnt);;
			geox = x / weight;
			geoy = y / weight;
		}
	}
	public void printNode(){
		if(subCnt == 0){
			System.out.println("valide " + valid + " leaf " + geox + " " + geoy);
		}else{
			System.out.println("inner node subCnt " + subCnt + " " + geox + " " + geoy);
			for(int i = 0; i < subCnt; i++){
				System.out.print("sub " + i + " ");
				subNodes[i].printNode();
			}
		}
	}
}
class Bound{
	private double xleft, xright, ytop, ybottom, size, xsize, ysize;
	public Bound(double xleft, double xright, double ytop, double ybottom){
		this.xleft = xleft;
		this.xright = xright;
		this.ytop = ytop;
		this.ybottom = ybottom;
		this.xsize = this.size = xright - xleft;
		this.ysize = this.ybottom - this.ytop;
	}
	public Bound[] splitBound(){
		Bound[] subBounds = new Bound[4];
		double xmiddle = (xleft + xright) / 2;
		double ymiddle = (ytop + ybottom) / 2;
		subBounds[0] = new Bound(xleft, xmiddle, ytop, ymiddle);//upper left bound
		subBounds[1] = new Bound(xmiddle, xright, ytop, ymiddle); //upper right bound
		subBounds[2] = new Bound(xleft, xmiddle, ymiddle, ybottom);//lower left bound
		subBounds[3] = new Bound(xmiddle, xright,ymiddle, ybottom);//lower right bound
		return subBounds;
	}
	public double size(){
		return size;
	}
	public boolean contains(double x, double y){
		return (x >= xleft && x <= xright && y >= ytop && y<= ybottom);
	}
	public double[] constrain(double x, double y){
		double[] res = new double[2];
		res[0] = (x - xleft) / this.xsize * 2 - 1;
		res[1] = (y - ytop) / this.ysize * 2 - 1;
		return res;
	}
	public void print(){
		System.out.println("range is upperleft ( " + xleft + " " + ytop + " ) bottomright (" + xright + " " + ybottom + " )");
	}
}