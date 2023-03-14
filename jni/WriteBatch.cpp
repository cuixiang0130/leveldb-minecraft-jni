#include <jni.h>
#include "leveldb/write_batch.h"
#include "utils.hpp"

using std::string;
using leveldb::Slice;
using leveldb::WriteBatch;

extern "C" JNIEXPORT jlong JNICALL Java_cx_minecraft_leveldb_WriteBatch_nativeNew
(JNIEnv * env, jclass cls) {
	WriteBatch* batch = new WriteBatch();
	return reinterpret_cast<jlong>(batch);
}

extern "C" JNIEXPORT void JNICALL Java_cx_minecraft_leveldb_WriteBatch_nativeRelease
(JNIEnv * env, jclass cls,
	jlong pointer) {
	WriteBatch* batch = reinterpret_cast<WriteBatch*>(pointer);
	delete batch;
}

extern "C" JNIEXPORT void JNICALL Java_cx_minecraft_leveldb_WriteBatch_nativePut
(JNIEnv * env, jclass cls, jlong pointer, jbyteArray key, jbyteArray value) {
	WriteBatch* batch = reinterpret_cast<WriteBatch*>(pointer);
	string key_bytes = to_string(env, key);
	string value_bytes = to_string(env, value);
	batch->Put(key_bytes, value_bytes);
}

extern "C" JNIEXPORT void JNICALL Java_cx_minecraft_leveldb_WriteBatch_nativeDelete
(JNIEnv * env, jclass cls, jlong pointer, jbyteArray key) {
	WriteBatch* batch = reinterpret_cast<WriteBatch*>(pointer);
	string key_bytes = to_string(env, key);
	batch->Delete(key_bytes);
}

extern "C" JNIEXPORT void JNICALL Java_cx_minecraft_leveldb_WriteBatch_nativeClear
(JNIEnv * env, jclass cls, jlong pointer) {
	WriteBatch* batch = reinterpret_cast<WriteBatch*>(pointer);
	batch->Clear();
}

extern "C" JNIEXPORT void JNICALL Java_cx_minecraft_leveldb_WriteBatch_nativeAppend
(JNIEnv * env, jclass cls, jlong pointer, jlong source_pointer) {
	WriteBatch* batch = reinterpret_cast<WriteBatch*>(pointer);
	WriteBatch* source_batch = reinterpret_cast<WriteBatch*>(source_pointer);
	batch->Append(*source_batch);
}

