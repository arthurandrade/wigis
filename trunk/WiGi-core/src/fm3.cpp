#include "net_FM3JNILib.h"
#include <ogdf/energybased/FMMMLayout.h>
using namespace ogdf;

JNIEXPORT jfloatArray JNICALL Java_net_FM3JNILib_runFM3
  (JNIEnv * env, jclass jcl,  jint dim, jintArray edgeIndex, jint edgeNum){
  	/*Graph *G = new Graph();
	GraphAttributes *GA = new GraphAttributes(*G);
	node* nodes = new node[dim];
	FMMMLayout *fmmm = new FMMMLayout();	

	for(int i  = 0; i < dim; i++){
		nodes[i] = G->newNode(i);
	}
	
	jint* edges = env->GetIntArrayElements(edgeIndex, 0);
	for(int i = 0; i < edgeNum;i++){
		G->newEdge(nodes[edges[i*2]], nodes[edges[i*2+1]]);
	}
	
	fmmm->useHighLevelOptions(true);
	fmmm->unitEdgeLength(15.0); 
	fmmm->newInitialPlacement(true);
	fmmm->qualityVersusSpeed(FMMMLayout::qvsGorgeousAndEfficient);	
	fmmm->call(*GA);
	
	jfloatArray result = env->NewFloatArray(dim * 2);
	if(result == NULL){
		return NULL;
	}
	jfloat * fill = new float[dim * 2];

	int i = 0;
	node v;
	forall_nodes(v,*G){
		fill[i * 2] = GA->x(v);
		fill[i * 2 + 1] = GA->y(v);
		i++;
	}
	
	G->clear();
	if(G!=NULL){
		delete G;
	}
	if(GA!=NULL){
		delete GA;
	}
	if(nodes != NULL){
		delete[] nodes;
	}
	if(fmmm!=NULL){
		delete fmmm;
	}
	
	env->SetFloatArrayRegion(result, 0, dim * 2, fill);
	env->ReleaseIntArrayElements(edgeIndex, edges, 0);	
	return result;*/
	Graph G;
	GraphAttributes GA(G);
	node* nodes = new node[dim];
	FMMMLayout fmmm;

	for(int i  = 0; i < dim; i++){
		nodes[i] = G.newNode(i);
	}
	
	jint* edges = env->GetIntArrayElements(edgeIndex, 0);
	for(int i = 0; i < edgeNum;i++){
		G.newEdge(nodes[edges[i*2]], nodes[edges[i*2+1]]);
	}
	
	fmmm.useHighLevelOptions(true);
	fmmm.unitEdgeLength(15.0); 
	fmmm.newInitialPlacement(true);
	fmmm.qualityVersusSpeed(FMMMLayout::qvsGorgeousAndEfficient);	
	fmmm.call(GA);
	
	jfloatArray result = env->NewFloatArray(dim * 2);
	if(result == NULL){
		return NULL;
	}
	jfloat * fill = new float[dim * 2];

	int i = 0;
	node v;
	forall_nodes(v,G){
		fill[i * 2] = GA.x(v);
		fill[i * 2 + 1] = GA.y(v);
		i++;
	}
	
	G.clear();
	if(nodes != NULL){
		delete[] nodes;
	}
	
	env->SetFloatArrayRegion(result, 0, dim * 2, fill);
	env->ReleaseIntArrayElements(edgeIndex, edges, 0);	
	return result;
}
 
