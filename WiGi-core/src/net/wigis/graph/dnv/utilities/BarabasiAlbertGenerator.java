package net.wigis.graph.dnv.utilities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import net.wigis.graph.dnv.DNVEdge;
import net.wigis.graph.dnv.DNVGraph;
import net.wigis.graph.dnv.DNVNode;

public class BarabasiAlbertGenerator {
	private DNVGraph mGraph = null;
    private int mNumEdgesToAttachPerStep;
    private int mElapsedTimeSteps;
    private Random mRandom;
    protected int init_vertices;
    protected List<DNVNode> vertex_index;
    
    /*protected Map<DNVNode, Integer> index_vertex;*/
    
    /**
     * Constructs a new instance of the generator.
     * @param init_vertices     number of unconnected 'seed' vertices that the graph should start with
     * @param numEdgesToAttach the number of edges that should be attached from the
     * new vertex to pre-existing vertices at each time step
     * @param directed  specifies whether the graph and edges to be created should be directed or not
     * @param parallel  specifies whether the algorithm permits parallel edges
     * @param seed  random number seed
     */
    public BarabasiAlbertGenerator(int init_vertices, int numEdgesToAttach, int seed) {
        assert init_vertices > 0 : "Number of initial unconnected 'seed' vertices "
                + "must be positive";
        assert numEdgesToAttach > 0 : "Number of edges to attach "
                + "at each time step must be positive";

        mNumEdgesToAttachPerStep = numEdgesToAttach;
        mRandom = new Random(seed);
        this .init_vertices = init_vertices;
        initialize();
    }

    /**
     * Constructs a new instance of the generator, whose output will be an undirected graph,
     * and which will use the current time as a seed for the random number generation.
     * @param init_vertices     number of vertices that the graph should start with
     * @param numEdgesToAttach the number of edges that should be attached from the
     * new vertex to pre-existing vertices at each time step
     */
    public BarabasiAlbertGenerator(int init_vertices, int numEdgesToAttach) {
        this (init_vertices, numEdgesToAttach, (int) System.currentTimeMillis());
    }
    private void initialize() {

        mGraph = new DNVGraph();
        

        vertex_index = new ArrayList<DNVNode>(2 * init_vertices);
        //index_vertex = new HashMap<DNVNode, Integer>(2 * init_vertices);
        for (int i = 0; i < init_vertices; i++) {
            DNVNode node = new DNVNode(mGraph);
            mGraph.addNode(0, node);
            vertex_index.add(node);
            //index_vertex.put(node, i);
        }

        mElapsedTimeSteps = 0;
    }
    private void createRandomEdge(List<DNVNode> preexistingNodes,
    	DNVNode newNode, HashSet<Pair<DNVNode, DNVNode>> added_pairs) {
    	DNVNode attach_point;
        boolean created_edge = false;
        Pair<DNVNode, DNVNode> endpoints;
        do {
            attach_point = vertex_index.get(mRandom
                    .nextInt(vertex_index.size()));

            endpoints = new Pair<DNVNode, DNVNode>(newNode, attach_point);

            // if parallel edges are not allowed, skip attach_point if <newNode, attach_point>
            // already exists; note that because of the way edges are added, we only need to check
            // the list of candidate edges for duplicates.
            if (added_pairs.contains(endpoints) || added_pairs.contains(new Pair<DNVNode, DNVNode>(attach_point, newNode)))
            	continue;

            double degree = attach_point.getDegree();

            // subtract 1 from numVertices because we don't want to count newNode
            // (which has already been added to the graph, but not to vertex_index)
            double attach_prob = (degree + 1)
                    / (mGraph.getEdges().size() + mGraph.getNodes(0).size() - 1);
            if (attach_prob >= mRandom.nextDouble())
                created_edge = true;
        } while (!created_edge);

        added_pairs.add(endpoints);

        added_pairs.add(new Pair<DNVNode, DNVNode>(attach_point, newNode));
    }

    public void evolveGraph(int numTimeSteps) {

        for (int i = 0; i < numTimeSteps; i++) {
            evolveGraph();
            mElapsedTimeSteps++;
            System.out.println("finish evolve step " + i);
        }
    }

    private void evolveGraph() {
        List<DNVNode> preexistingNodes = mGraph.getNodes(0);
        DNVNode newNode = new DNVNode(mGraph);

        mGraph.addNode(0, newNode);

        // generate and store the new edges; don't add them to the graph
        // yet because we don't want to bias the degree calculations
        // (all new edges in a timestep should be added in parallel)
        HashSet<Pair<DNVNode, DNVNode>> added_pairs = new HashSet<Pair<DNVNode, DNVNode>>(
                mNumEdgesToAttachPerStep * 3);

        for (int i = 0; i < mNumEdgesToAttachPerStep; i++)
            createRandomEdge(preexistingNodes, newNode, added_pairs);

        for (Pair<DNVNode, DNVNode> pair : added_pairs) {
        	DNVNode v1 = pair.getFirst();
        	DNVNode v2 = pair.getSecond();
            if (!v1.getNeighbors().contains(v2)){
            	DNVEdge edge = new DNVEdge(mGraph);
            	edge.setFrom(v1);
            	edge.setTo(v2);
                mGraph.addEdge(0, edge);
            }
        }
        // now that we're done attaching edges to this new vertex, 
        // add it to the index
        vertex_index.add(newNode);
        //index_vertex.put(newNode,
        //        new Integer(vertex_index.size() - 1));
    }

    public int numIterations() {
        return mElapsedTimeSteps;
    }

    public DNVGraph create() {
        return mGraph;
    }
    
    public static void main(String[] args){
    	BarabasiAlbertGenerator bsg = new BarabasiAlbertGenerator(500,10);
    	bsg.evolveGraph(500);
    	DNVGraph graph = bsg.create();
    	graph.writeGraph("/Users/scarlettteng/dev/bs1000_5000graph.dnv");
    }
}
