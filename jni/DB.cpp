#include <jni.h>
#include <string>

#include "leveldb/db.h"
#include "leveldb/cache.h"
#include "leveldb/env.h"
#include "leveldb/filter_policy.h"
#include "leveldb/write_batch.h"

#include "utils.hpp"

using std::string;
using leveldb::Slice;
using leveldb::DB;
using leveldb::Status;
using leveldb::Options;
using leveldb::ReadOptions;
using leveldb::WriteOptions;
using leveldb::WriteBatch;
using leveldb::Snapshot;

class NullLogger : public leveldb::Logger {
public:
	void Logv(const char*, va_list) override {}
};

extern "C" JNIEXPORT jlong JNICALL Java_cx_minecraft_leveldb_DB_nativeNewOptions
(JNIEnv * env, jclass cls, jboolean create_if_missing, jboolean paranoid_checks, jlong write_buffer_size, jint max_open_files, jlong cache_size, jlong block_size, jint block_restart_interval, jlong max_file_size, jint compression) {
	Options* options = new Options();

	options->create_if_missing = create_if_missing;

	options->paranoid_checks = paranoid_checks;

	options->info_log = new NullLogger();

	options->write_buffer_size = write_buffer_size;

	options->max_open_files = max_open_files;

	options->block_cache = leveldb::NewLRUCache(cache_size);

	options->block_size = block_size;

	options->block_restart_interval = block_restart_interval;

	options->max_file_size = max_file_size;

	options->compression = static_cast<leveldb::CompressionType>(compression);

	options->filter_policy = leveldb::NewBloomFilterPolicy(10);

	return reinterpret_cast<jlong>(options);
}

extern "C" JNIEXPORT void JNICALL Java_cx_minecraft_leveldb_DB_nativeReleaseOptions
(JNIEnv * env, jclass cls, jlong options_pointer) {
	Options* options = reinterpret_cast<Options*>(options_pointer);

	delete options->filter_policy;

	delete options->block_cache;

	delete options->info_log;

	delete options;
}

extern "C" JNIEXPORT jlong JNICALL Java_cx_minecraft_leveldb_DB_nativeOpen
(JNIEnv * env, jclass cls, jstring path, jlong options_pointer) {
	Options* options = reinterpret_cast<Options*>(options_pointer);
	DB* db = nullptr;
	string path_str = to_string(env, path);
	Status status = DB::Open(*options, path_str, &db);
	checkStatus(env, status);
	return reinterpret_cast<jlong>(db);
}

extern "C" JNIEXPORT void JNICALL Java_cx_minecraft_leveldb_DB_nativeRelease
(JNIEnv * env, jclass cls, jlong db_pointer) {
	DB* db = reinterpret_cast<DB*>(db_pointer);
	delete db;
}

extern "C" JNIEXPORT void JNICALL Java_cx_minecraft_leveldb_DB_nativePut
(JNIEnv * env, jclass cls, jlong db_pointer, jbyteArray key, jbyteArray value, jboolean sync) {
	DB* db = reinterpret_cast<DB*>(db_pointer);
	string key_bytes = to_string(env, key);
	string value_bytes = to_string(env, value);
	WriteOptions options;
	options.sync = sync;
	Status status = db->Put(options, key_bytes, value_bytes);
	checkStatus(env, status);
}

extern "C" JNIEXPORT void JNICALL Java_cx_minecraft_leveldb_DB_nativeDelete
(JNIEnv * env, jclass cls, jlong db_pointer, jbyteArray key, jboolean sync) {
	DB* db = reinterpret_cast<DB*>(db_pointer);
	string key_bytes = to_string(env, key);
	WriteOptions options;
	options.sync = sync;
	Status status = db->Delete(options, key_bytes);
	checkStatus(env, status);
}

extern "C" JNIEXPORT void JNICALL Java_cx_minecraft_leveldb_DB_nativeWrite
(JNIEnv * env, jclass cls, jlong db_pointer, jlong batch_pointer, jboolean sync) {
	DB* db = reinterpret_cast<DB*>(db_pointer);
	WriteBatch* batch = reinterpret_cast<WriteBatch*>(batch_pointer);
	WriteOptions options;
	options.sync = sync;
	Status status = db->Write(options, batch);
	checkStatus(env, status);
}

extern "C" JNIEXPORT jbyteArray JNICALL Java_cx_minecraft_leveldb_DB_nativeGet
(JNIEnv * env, jclass cls, jlong db_pointer, jbyteArray key, jboolean verify_checksums, jboolean fill_cache, jlong snapshot_pointer) {
	DB* db = reinterpret_cast<DB*>(db_pointer);
	string key_bytes = to_string(env, key);
	std::string value;
	ReadOptions options;
	options.verify_checksums = verify_checksums;
	options.fill_cache = fill_cache;
	const Snapshot* snapshot = reinterpret_cast<const Snapshot*>(snapshot_pointer);
	options.snapshot = snapshot;
	Status status = db->Get(options, key_bytes, &value);
	if (status.IsNotFound()) return nullptr;
	checkStatus(env, status);
	return to_jbyteArray(env, value);
}

extern "C" JNIEXPORT jlong JNICALL Java_cx_minecraft_leveldb_DB_nativeNewIterator
(JNIEnv * env, jclass cls, jlong db_pointer, jboolean verify_checksums, jboolean fill_cache, jlong snapshot_pointer) {
	DB* db = reinterpret_cast<DB*>(db_pointer);
	ReadOptions options;
	options.verify_checksums = verify_checksums;
	options.fill_cache = fill_cache;
	const Snapshot* snapshot = reinterpret_cast<const Snapshot*>(snapshot_pointer);
	options.snapshot = snapshot;
	leveldb::Iterator* itertor = db->NewIterator(options);
	return reinterpret_cast<jlong>(itertor);
}


extern "C" JNIEXPORT jlong JNICALL Java_cx_minecraft_leveldb_DB_nativeGetSnapshot
(JNIEnv * env, jclass cls, jlong db_pointer) {
	DB* db = reinterpret_cast<DB*>(db_pointer);
	const Snapshot* snapshot = db->GetSnapshot();
	return reinterpret_cast<jlong>(snapshot);
}

extern "C" JNIEXPORT void JNICALL Java_cx_minecraft_leveldb_DB_nativeReleaseSnapshot
(JNIEnv * env, jclass cls, jlong db_pointer, jlong snapshot_pointer) {
	DB* db = reinterpret_cast<DB*>(db_pointer);
	const Snapshot* snapshot = reinterpret_cast<const Snapshot*>(snapshot_pointer);
	db->ReleaseSnapshot(snapshot);
}

extern "C" JNIEXPORT jstring JNICALL Java_cx_minecraft_leveldb_DB_nativeGetProperty
(JNIEnv * env, jclass cls, jlong db_pointer, jstring property) {
	DB* db = reinterpret_cast<DB*>(db_pointer);
	string property_str = to_string(env, property);
	string property_value;
	db->GetProperty(property_str, &property_value);
	return env->NewStringUTF(property_value.c_str());
}

extern "C" JNIEXPORT void JNICALL Java_cx_minecraft_leveldb_DB_nativeCompactRange
(JNIEnv * env, jclass cls, jlong db_pointer, jbyteArray begin_key, jbyteArray end_key) {
	DB* db = reinterpret_cast<DB*>(db_pointer);
	Slice* begin = nullptr;
	Slice* end = nullptr;
	string begin_str, end_str;
	if (begin_key) {
		begin_str = to_string(env, begin_key);
		begin = new Slice(begin_str);
	}
	if (end_key) {
		end_str = to_string(env, begin_key);
		end = new Slice(end_str);
	}
	db->CompactRange(begin, end);
	if (begin)
		delete begin;
	if (end)
		delete end;
}


extern "C" JNIEXPORT void JNICALL Java_cx_minecraft_leveldb_DB_nativeRepair
(JNIEnv * env, jclass cls, jstring path, jlong options_pointer) {
	Options* options = reinterpret_cast<Options*>(options_pointer);
	DB* db = nullptr;
	string path_str = to_string(env, path);
	Status status = leveldb::RepairDB(path_str, *options);
	checkStatus(env, status);
}
