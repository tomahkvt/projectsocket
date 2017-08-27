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

/** NOTES:
 *  # Function reference:
 *  https://docs.oracle.com/javase/8/docs/technotes/guides/jni/spec/functions.html#GetStringUTFCharsGetStringUTFChars
 *
 *  # Method signature:
 *  Java_CLASSNAME_NativeMethodName
 *  ex "helloworld()" in java translates to "Java_HelloJNI_helloworld()"
 */


/*
 * Example of a native call. No paramaters or return value.
 * env - is the environment that contains useful functions.
 * jobject is a pointer to the class from which we call this method.
 */
JNIEXPORT void JNICALL Java_HelloJNI_sayHello(JNIEnv *env, jobject thisObj) {
   printf("c: Hello World!\n");
   fflush(stdout);
   return;
}

/**
 * Example of passing String to C and returning one to Java.
 * from: chap2 -> Prompt. Book: 3.2
 */
JNIEXPORT jstring JNICALL Java_HelloJNI_printMyStringReturnString
  (JNIEnv *env, jobject thisObj, jstring javaString){

	// Do not use jstring directly in C code.
	const char *c_utfstr;
	c_utfstr = (*env)->GetStringUTFChars(env, javaString, NULL);
	if (c_utfstr == NULL) {
		return NULL; // out of memory handled by JVM, will already be thrown.
		// Exceptions are not thrown until method is returned.
	}

	printf("c: Received java string %s\n", c_utfstr);
	fflush(stdout);

	// Return a new JAVA string back to javaland.
	const char *return_string = "String created in C with fancy german letters: ö ä ü ß \n";
	return (*env)->NewStringUTF(env, return_string); // If we weren't returning, we should be checking if (...== null).
}

JNIEXPORT void JNICALL Java_HelloJNI_nativePrintBool (JNIEnv *env, jobject thisObj, jboolean myBool) {
	if (myBool) {
		printf("True %d\n", myBool);
	} else {
		printf("False %d\n", myBool);
	}
	fflush(stdout);
}


/**
 * Example of int Array.
 * Here we use "Region", i.e no need for allocating memory or releasing it.
 * For dynamic lenghts, see next example.
 */
JNIEXPORT jint JNICALL Java_HelloJNI_nativeProduct
  (JNIEnv *env, jobject thisObj, jintArray javaIntArray) {

	// We are not allowed to use jintArray directly, must convert first.
	jint c_ints[4];
	jint product = 1;
	(*env)->GetIntArrayRegion(env, javaIntArray, 0, 4, c_ints);
	for (int i = 0; i < 4; i++) {
		product = product * c_ints[i];
	}
	return product;
}

/**
 * Example of int Array.
 * Get it dynamically rather than using a static buffer.
 */
JNIEXPORT jint JNICALL Java_HelloJNI_nativeProductDynamic
  (JNIEnv *env, jobject thisObj, jintArray javaIntArray, jint arrayLength) {

	jint * c_intArray  = (*env)->GetIntArrayElements(env, javaIntArray, NULL);
	jint product = 1;
	if (c_intArray == NULL) {
		return 0; // OutOfMemory
	}

	for (int i = 0; i < arrayLength; i++) {
		product = product * c_intArray[i];
	}

	(*env)->ReleaseIntArrayElements(env, javaIntArray, c_intArray, 0);

	return product;
}

/**
 * Return a 2d array.
 */
JNIEXPORT jobjectArray JNICALL Java_HelloJNI_produce2dArray
  (JNIEnv *env, jobject thisObj, jint length) {

	jobjectArray firstDimension;

	jclass javaIntClass = (*env)->FindClass(env, "[I");
	if (javaIntClass == NULL) {
		return NULL;
	}

	firstDimension = (*env)->NewObjectArray(env, length, javaIntClass, NULL);
	if (firstDimension == NULL) {
		return NULL;
	}

	for (int i = 0; i < length; i++) {
		jint tmp[256]; // Ensure this is big enough.

		jintArray secondDimension = (*env)->NewIntArray(env, length);

		for (int j = 0; j < length; j++) {
			tmp[j] = i + j;
		}

		(*env)->SetIntArrayRegion(env, secondDimension, 0, length, tmp);
		(*env)->SetObjectArrayElement(env, firstDimension, i, secondDimension);
		(*env)->DeleteLocalRef(env, secondDimension);

	}
	return firstDimension;
}


/**
 * Access class instance fields from Java. (See 4.1 of book).
 *
 * Instance vs Static fields:
 * - For instance fields, you use GetFieldID, you pass object around into Get<TYPE>Field(..)
 * - For static fields, you use GetStaticFieldId, you pass CLASS into GetStatic<TYPE>field(...)
 *
 */
JNIEXPORT void JNICALL Java_HelloJNI_accessFields
  (JNIEnv *env, jobject thisObj) {

	// Get ref to class
	jclass cls = (*env)->GetObjectClass(env, thisObj);

	// Find the instance field in class.
	// Notice ';' at end. See 12.3.3 for descriptors.
	// See java side docu, (use javac & javap -s -p to get field signature.
	// Copy descriptor EXACTLY as printed.
	jfieldID fieldID = (*env)->GetFieldID(env, cls, "str", "Ljava/lang/String;");
	if (fieldID == NULL)
		return; // exception thrown.

	// Can be other fields, ex: GetIntField, GetStaticObjectField  etc...
	jstring javaString = (*env)->GetObjectField(env, thisObj, fieldID);
	const char *cString = (*env)->GetStringUTFChars(env, javaString, 0);
	if (cString == NULL) return; // out of memory.

	printf("C: Received string: %s\n", cString);
	fflush(stdout);

	(*env)->ReleaseStringUTFChars(env, javaString, cString);

	// Create new strinnd modify class instance variables.
	jstring newJavaString = (*env)->NewStringUTF(env, "c has modified this string\n");
	(*env)->SetObjectField(env, thisObj, fieldID, newJavaString);  //can be SetBooleanField(..), setStatic<TYPE>Field. etc...
}

JNIEXPORT void JNICALL Java_HelloJNI_accessStaticBoolean
  (JNIEnv *env, jclass thisClass) {


	jfieldID fieldId = (*env)->GetStaticFieldID(env, thisClass, "javaBool", "Z"); // boolean = Z. See function above.
	jboolean javaBool = (*env)->GetStaticBooleanField(env, thisClass, fieldId);
	printf("C: javaBool: %d\n", javaBool);
	fflush(stdout);
	(*env)->SetStaticBooleanField(env, thisClass, fieldId, JNI_TRUE);


}


JNIEXPORT void JNICALL Java_HelloJNI_cCallingJava
  (JNIEnv *env, jobject thisObj) {
	jclass javaClass = (*env)->GetObjectClass(env, thisObj);
	/// signature acquired via: 	javac HelloJNI.java; javap -s -p HelloJNI.class
	jmethodID javaMethod = (*env)->GetMethodID(env, javaClass, "callbackFromC","(I)V");

	if (javaMethod == NULL) {
		return; /* Method not found */
	}
	printf("C: About to call java\n");
	fflush(stdout);

	/** Call back java method. For non-void, use other calls like
	 * CallIntMethod(..),  Call<type>Method(..).. etc..
	 */
	(*env)->CallVoidMethod(env,thisObj, javaMethod, 3);

	// Comment this in combination with "-Xcheck:jni" to get a sample error check warning in console.
	jboolean exceptionCheck = (*env)->ExceptionCheck(env);
	if (exceptionCheck == JNI_TRUE) {
		printf("C: Exception occured when trying to call void Method :-(\n");
		fflush(stdout);
		return;
	}

	/** FOR STATIC FUNCTIONS:
	 * GetStaticMethodID(..)
	 * CallStatic<type>Method
	 * bk: 4.2.3. See example in "chap 4 StaticMethodCall"
	 */
}






/* ----------------------------------------------------------------
 * ----------------------------------------------------------------
 * EXAMPLE - java Strings to platform local strings and back.
 * This doesn't mean proper umlaut support, but means convert
 * java unicode strings into strings supported on current platform,
 * ex file names.
 */
#include <string.h>
#include <stdlib.h>
jclass Class_java_lang_String;
jmethodID MID_String_init;
jmethodID MID_String_getBytes;
void initIDs(JNIEnv *env)
{
  Class_java_lang_String = (*env)->FindClass(env, "java/lang/String");
  MID_String_init = (*env)->GetMethodID(env, Class_java_lang_String,
				"<init>", "([B)V");
  MID_String_getBytes = (*env)->GetMethodID(env, Class_java_lang_String,
				"getBytes", "()[B");
}

/** Useful throw by name Utility method */
void
JNU_ThrowByName(JNIEnv *env, const char *name, const char *msg)
{
    jclass cls = (*env)->FindClass(env, name);
    /* If cls is NULL, an exception has already been thrown */
    if (cls != NULL) {
        (*env)->ThrowNew(env, cls, msg);
    }
    /* free the local ref */
    (*env)->DeleteLocalRef(env, cls);
}

/**
 * Given a locale specific C string, create a java String.
 * Note, there may be issues with umlauts.
 */
jstring JNU_NewStringNative(JNIEnv *env, const char *str)
{
    jstring result;
    jbyteArray bytes = 0;
    int len;
    if ((*env)->EnsureLocalCapacity(env, 2) < 0) {
        return NULL; /* out of memory error */
    }
    len = strlen(str);
    bytes = (*env)->NewByteArray(env, len);
    if (bytes != NULL) {
        (*env)->SetByteArrayRegion(env, bytes, 0, len,
                                   (jbyte *)str);
        result = (*env)->NewObject(env, Class_java_lang_String,
                                   MID_String_init, bytes);
        (*env)->DeleteLocalRef(env, bytes);
        return result;
    } /* else fall through */
    return NULL;
}

/**
 * Given a Java String, convert to a native C String.
 */
char *JNU_GetStringNativeChars(JNIEnv *env, jstring jstr)
{
    jbyteArray bytes = 0;
    jthrowable exc;
    char *result = 0;
    if ((*env)->EnsureLocalCapacity(env, 2) < 0) {
        return 0; /* out of memory error */
    }
    bytes = (*env)->CallObjectMethod(env, jstr,
                                     MID_String_getBytes);
    exc = (*env)->ExceptionOccurred(env);
    if (!exc) {
        jint len = (*env)->GetArrayLength(env, bytes);
        result = (char *)malloc(len + 1);
        if (result == 0) {
            JNU_ThrowByName(env, "java/lang/OutOfMemoryError",
                            0);
            (*env)->DeleteLocalRef(env, bytes);
            return 0;
        }
        (*env)->GetByteArrayRegion(env, bytes, 0, len,
                                   (jbyte *)result);
        result[len] = 0; /* NULL-terminate */
    } else {
        (*env)->DeleteLocalRef(env, exc);
    }
    (*env)->DeleteLocalRef(env, bytes);
    return result;
}

JNIEXPORT jstring JNICALL Java_HelloJNI_internationalStrings
  (JNIEnv *env, jobject thisObj, jstring javaString) {
	  initIDs(env);
	  {
		// Convert Java to native string.
		char *str = JNU_GetStringNativeChars(env, javaString);
	    printf("c: received: %s\n" , str);
	    fflush(stdout);

	    // Mess around with native string.
	    str[0] = 'd';
	    str[1] = 'e';
	    str[2] = 'f';

	    // Convert native back to Java
	    return JNU_NewStringNative(env, str);
	  }

}



// ---------------------- EXAMPLE 9 - C to instantiate a Java class and return it.
JNIEXPORT void JNICALL Java_HelloJNI_returnMultipleTypes
  (JNIEnv *env, jclass helloJNIClass, jobject multiValuesObj) {

	jclass cls = (*env)->GetObjectClass(env, multiValuesObj);

	// Modify int field.
	{
		jfieldID fieldID = (*env)->GetFieldID(env, cls, "num", "I");
		if (fieldID == NULL)
			return; // exception thrown.
		jint num = (*env)->GetIntField(env, multiValuesObj, fieldID);
		printf("C: Num is: %d\n", num);
		fflush(stdout);
		num++;
		(*env)->SetIntField(env, multiValuesObj, fieldID, num);
	}

	// Modify bool field
	{
		jfieldID fieldID = (*env)-> GetFieldID(env, cls, "bool", "Z");
		if (fieldID == NULL)
			return; // Exception thrown.
		jboolean bool = (*env)->GetBooleanField(env, multiValuesObj, fieldID);
		printf("C: Bool is: %d\n", bool);
		fflush(stdout);
		bool = JNI_FALSE;
		(*env)->SetBooleanField(env, multiValuesObj, fieldID, bool);
	}

	// Modify String field
	{
		jfieldID fieldID = (*env)->GetFieldID(env, cls, "str", "Ljava/lang/String;");
		if (fieldID == NULL)
			return; // exception occured
		jstring javaString = (*env)->GetObjectField(env, multiValuesObj, fieldID);
		const char *cString = (*env)->GetStringUTFChars(env, javaString, 0);
		if (cString == NULL)
			return; // out of memory
		printf("C: String received: %s\n", cString);
		fflush(stdout);
		(*env)->ReleaseStringUTFChars(env, javaString, cString);

		jstring newJavaString = (*env)->NewStringUTF(env, "Cool C World");
		(*env)->SetObjectField(env, multiValuesObj, fieldID, newJavaString);
	}




	return;


}
