#include <jni.h>
#include "leveldb/iterator.h"
#include "utils.hpp"

using std::string;
using leveldb::Status;
using leveldb::Slice;
using leveldb::Iterator;

extern "C" JNIEXPORT void JNICALL Java_cx_minecraft_leveldb_DBIterator_nativeRelease
(JNIEnv * env, jclass cls, jlong pointer) {
	Iterator* iterator = reinterpret_cast<Iterator*>(pointer);
	delete iterator;
}

extern "C" JNIEXPORT jboolean JNICALL Java_cx_minecraft_leveldb_DBIterator_nativeValid
(JNIEnv * env, jclass cls, jlong pointer) {
	Iterator* iterator = reinterpret_cast<Iterator*>(pointer);
	return iterator->Valid() ? JNI_TRUE : JNI_FALSE;
}

extern "C" JNIEXPORT void JNICALL Java_cx_minecraft_leveldb_DBIterator_nativeSeekToFirst
(JNIEnv * env, jclass cls, jlong pointer) {
	Iterator* iterator = reinterpret_cast<Iterator*>(pointer);
	iterator->SeekToFirst();
	Status status = iterator->status();
	checkStatus(env, status);
}

extern "C" JNIEXPORT void JNICALL Java_cx_minecraft_leveldb_DBIterator_nativeSeekToLast
(JNIEnv * env, jclass cls, jlong pointer) {
	Iterator* iterator = reinterpret_cast<Iterator*>(pointer);
	iterator->SeekToLast();
	Status status = iterator->status();
	checkStatus(env, status);
}

extern "C" JNIEXPORT void JNICALL Java_cx_minecraft_leveldb_DBIterator_nativeSeek
(JNIEnv * env, jclass cls, jlong pointer, jbyteArray key) {
	Iterator* iterator = reinterpret_cast<Iterator*>(pointer);
	string key_bytes = to_string(env, key);
	iterator->Seek(key_bytes);
	Status status = iterator->status();
	checkStatus(env, status);
}

extern "C" JNIEXPORT void JNICALL Java_cx_minecraft_leveldb_DBIterator_nativeNext
(JNIEnv * env, jclass cls, jlong pointer) {
	Iterator* iterator = reinterpret_cast<Iterator*>(pointer);
	iterator->Next();
	Status status = iterator->status();
	checkStatus(env, status);
}

extern "C" JNIEXPORT void JNICALL Java_cx_minecraft_leveldb_DBIterator_nativePrev
(JNIEnv * env, jclass cls, jlong pointer) {
	Iterator* iterator = reinterpret_cast<Iterator*>(pointer);
	iterator->Prev();
	Status status = iterator->status();
	checkStatus(env, status);
}

extern "C" JNIEXPORT jbyteArray JNICALL Java_cx_minecraft_leveldb_DBIterator_nativeKey
(JNIEnv * env, jclass cls, jlong pointer) {
	Iterator* iterator = reinterpret_cast<Iterator*>(pointer);
	Slice key = iterator->key();
	return to_jbyteArray(env, key);
}

extern "C" JNIEXPORT jbyteArray JNICALL Java_cx_minecraft_leveldb_DBIterator_nativeValue
(JNIEnv * env, jclass cls, jlong pointer) {
	Iterator* iterator = reinterpret_cast<Iterator*>(pointer);
	Slice value = iterator->value();
	return to_jbyteArray(env, value);
}
