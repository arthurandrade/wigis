#include <jni.h>
#include "/Users/yunteng/Documents/work/Tobias/code/tnt_126/tnt.h"
#include "/Users/yunteng/Documents/work/Tobias/code/jama125/jama_lu.h"
using namespace TNT;
Array2D<float> A;
int dim;
JNIEXPORT void JNICALL Java_net_wigis_graph_dnv_layout_implementations_JNILib_initMatrix
(JNIEnv * env, jobject jobj, jfloatArray jMatrix, jint dimension) {
	jfloat *jmat = env->GetFloatArrayElements(jMatrix, 0);
	dim = dimension;
	A = Array2D<float>(dim, dim, 0.f);
	for (int i = 0; i < dim; i++) {
		for (int j = 0; j < dim; j++) {
			A[i][j] = jmat[i * dim + j];
		}
	}
	env->ReleaseFloatArrayElements(jMatrix, jmat, 0);
}
JNIEXPORT jfloatArray JNICALL Java_net_wigis_graph_dnv_layout_implementations_JNILib_LUSolver
(JNIEnv * env, jobject jobj, jfloatArray jVec){
	jfloatArray result = env->NewFloatArray(dim);
	jfloat* jvec = env->GetFloatArrayElements(jVec, 0);
	Array1D<float> b(dim, 0.f);
	for (int i = 0; i < dim; i++) {
		b[i] = jvec[i];
	}
	JAMA::LU<float> lusolver(A);
	Array1D<float> cres = lusolver(b);
	for (int i = 0; i < dim; i++) {
		result[i] = cres[i];
	}
	env->ReleaseFloatArrayElements(jVec, jvec,0);
	return result;
}

