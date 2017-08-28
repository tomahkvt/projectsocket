/*
 * HelloJNI.c
 *
 *  Created on: Oct 20, 2016
 *      Author: lufimtse
 *
 *      MANUALLY WRITTEN! See: http://www3.ntu.edu.sg/home/ehchua/programming/java/JavaNativeInterface.html
 */


#include "../jni/ArServerDetectorJni.h"

#include <jni.h>
#include <stdio.h>

#include <stdlib.h>


JNIEXPORT jint JNICALL Java_ArServerDetectorJni_initSession
  (JNIEnv *env, jclass thisClass, jstring pyramidPath,jstring dbowPath,jintArray ids,jobjectArray markers){
/*
  	BundleData bd;
    bd.dbowFile = dbowPathStr;
    bd.pyramidFile = piramidPathStr;
    const jsize markersPathCount = env->GetArrayLength(markers);
    const jsize markersIdCount = env->GetArrayLength(ids);
    double *markersIds = env->GetIntArrayElements(ids, NULL);
*/

  	const char *piramidPathStr = (*env)->GetStringUTFChars(env, pyramidPath, NULL);

  	const char *dbowPathStr = (*env)->GetStringUTFChars(env, dbowPath, NULL);

  	printf("pyramidPath %s\n", piramidPathStr);
  	printf("dbowPath %s\n", dbowPathStr);

  	jsize IdNumber = (*env)->GetArrayLength(env,ids);

  	printf("Number in ids array %d\n", IdNumber);
  	jint *array_ids = (*env)->GetIntArrayElements(env, ids, 0);
  	    for (int i=0; i<IdNumber; i++) {
  	      printf("element ids array %d\n", array_ids[i]);
  	    }

  	jsize markersPathCount = (*env)->GetArrayLength(env,markers);
  	printf("Number in markers array %d\n", markersPathCount);
	for (int i = 0; i < markersPathCount; ++i)
	{
		int markerId = array_ids[i];
		jstring markerPath = (*env)->GetObjectArrayElement(env,markers, i);
		const char *markerPathStr = (*env) ->GetStringUTFChars(env,markerPath, NULL);
		printf("element ids array %s\n", markerPathStr);
		//bd.markerFiles[markerId] = markerPathStr;
		(*env)->ReleaseStringUTFChars(env, markerPath, markerPathStr);
	}
		//int sessionId = createSession(bd);

		(*env)->ReleaseStringUTFChars(env, pyramidPath, piramidPathStr);
	    (*env)->ReleaseStringUTFChars(env, dbowPath, dbowPathStr);
  	return 34;
  	//return sessionId;
}

JNIEXPORT jint JNICALL Java_ArServerDetectorJni_detect
  (JNIEnv * env, jclass thisClass, jint sessionId,jbyteArray image){

	jsize imagesize = (*env)->GetArrayLength(env,image);

	jbyte* imageData = (*env)->GetByteArrayElements(env, image, NULL);

	for (int i = 0; i < imagesize; i++) {
		printf("imageelement %d\n", imageData[i]);
			}
  			return 14;

  	//   cv::Mat frame = cv::imdecode(imageData);
  	//	return detectSession( sessionId, frame);
  }

    JNIEXPORT void JNICALL Java_ArServerDetectorJni_closeSession
  (JNIEnv * env, jclass thisClass, jint sessionId){
    	printf("closeSession id %d\n", sessionId);
//  	deleteSession(sessionId);
  }

