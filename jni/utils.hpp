#pragma once

#include <string>
#include <jni.h>
#include "leveldb/slice.h"

inline jbyteArray to_jbyteArray(JNIEnv* env, std::string& str) {
	size_t len = str.size();
	jbyteArray jbytes = env->NewByteArray(len);
	env->SetByteArrayRegion(jbytes, 0, len, (jbyte*)str.data());
	return jbytes;
}

inline jbyteArray to_jbyteArray(JNIEnv* env, leveldb::Slice& slice) {
	size_t len = slice.size();
	jbyteArray jbytes = env->NewByteArray(len);
	env->SetByteArrayRegion(jbytes, 0, len, (jbyte*)slice.data());
	return jbytes;
}

inline std::string to_string(JNIEnv* env, jstring jstr) {
	const char* chars = env->GetStringUTFChars(jstr, nullptr);
	std::string str(chars);
	env->ReleaseStringUTFChars(jstr, chars);
	return str;
}

inline std::string to_string(JNIEnv* env, jbyteArray jbytes) {
	size_t len = env->GetArrayLength(jbytes);
	jbyte* bytes = env->GetByteArrayElements(jbytes, nullptr);
	std::string str(reinterpret_cast<char*>(bytes), len);
	env->ReleaseByteArrayElements(jbytes, bytes, JNI_ABORT);
	return str;
}

inline void checkStatus(JNIEnv* env, leveldb::Status& status) {
	if (status.ok())
		return;
	const char* exceptionClassName;
	if (status.IsNotFound())
		exceptionClassName = "java/lang/NullPointerException";
	else if (status.IsCorruption())
		exceptionClassName = "cx/minecraft/leveldb/CorruptException";
	else if (status.IsNotSupportedError())
		exceptionClassName = "java/lang/UnsupportedOperationException";
	else if (status.IsInvalidArgument())
		exceptionClassName = "java/lang/IllegalArgumentException";
	else if (status.IsIOError())
		exceptionClassName = "java/io/IOException";
	else
		return;
	jclass exceptionClass = env->FindClass(exceptionClassName);
	std::string errorMessage = status.ToString();
	env->ThrowNew(exceptionClass, errorMessage.c_str());
}