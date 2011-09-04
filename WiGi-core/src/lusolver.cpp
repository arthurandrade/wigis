#include "net_JNILib.h"
//#include "/Users/yunteng/Documents/work/Tobias/code/tnt_126/tnt.h"
#include <tnt.h>
//#include "/Users/yunteng/Documents/work/Tobias/code/jama125/jama_lu.h"
#include <jama_cholesky.h>
#include <jama_lu.h>
using namespace TNT;
using namespace JAMA;
Array2D<double> A;
LU<double> chol(A);
int dim;
double alpha;
JNIEXPORT void JNICALL Java_net_JNILib_initMatrix
(JNIEnv * env, jclass jcl, jint dimension, jdouble jalpha) {
	dim = dimension;
	alpha = jalpha;
	A = Array2D<double>(dim, dim, -1.0);
	for (int i = 0; i < dim; i++) {
		A[i][i] = dim - 1;
	}
}
JNIEXPORT void JNICALL Java_net_JNILib_passValue
  (JNIEnv *env, jclass jcl, jint jrow, jint jneighborCnt, jintArray jneighbors){
  	jint* neighbors = env->GetIntArrayElements(jneighbors, 0);
  	A[jrow][jrow] += jneighborCnt * alpha;
  	for(int i = 0; i < jneighborCnt; i++){
  		A[jrow][neighbors[i]] -= alpha;
  	}
  	env->ReleaseIntArrayElements(jneighbors, neighbors,0);
 }
 
 JNIEXPORT void JNICALL Java_net_JNILib_decomMatrix
  (JNIEnv * env, jclass jcl){
  	chol = LU<double>(A);
 }
  
JNIEXPORT jdoubleArray JNICALL Java_net_JNILib_LUSolver
(JNIEnv * env, jclass jobj, jdoubleArray jVec){
	jdoubleArray result = env->NewDoubleArray(dim);
	jdouble* res = new jdouble[dim];
	jdouble* jvec = env->GetDoubleArrayElements(jVec, 0);
	Array1D<double> b(dim, 0.f);
	for (int i = 0; i < dim; i++) {
		b[i] = jvec[i];
	}
	//JAMA::Cholesky<double> chol(A);
	//JAMA::LU<double> chol(A);
	Array1D<double> cres = chol.solve(b);
	for (int i = 0; i < dim; i++) {
		res[i] = cres[i];
	}
	env->SetDoubleArrayRegion(result, 0, dim, res);
	env->ReleaseDoubleArrayElements(jVec, jvec,0);
	return result;
}

