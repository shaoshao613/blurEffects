#include <jni.h>

#ifndef _Included_blur

#define _Included_blur

#ifdef __cplusplus

extern "C" {
#endif
JNIEXPORT void JNICALL Java_com_enrique_stackblur_NativeBlurProcess_functionToBlur(JNIEnv* env, jclass clzz, jobject bitmapOut, jint radius, jint threadCount, jint threadIndex, jint round);

#ifdef __cplusplus

}

#endif

#endif