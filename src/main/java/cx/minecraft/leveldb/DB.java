package cx.minecraft.leveldb;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class DB extends NativeObject {

    static {
        System.loadLibrary("leveldb-jni");
    }

    private final long dbPointer;

    private final long optionsPointer;

    public DB(@NotNull String path) {
        this(path, null);
    }

    public DB(@NotNull String path, @Nullable Options options) {
        Objects.requireNonNull(path);
        if (options == null)
            options = new Options();
        optionsPointer = nativeNewOptions(
                options.getCreateIfMissing(),
                options.getParanoidChecks(),
                options.getWriteBufferSize(),
                options.getMaxOpenFiles(),
                options.getCacheSize(),
                options.getBlockSize(),
                options.getBlockRestartInterval(),
                options.getMaxFileSize(),
                options.getCompressionType().ordinal()
        );
        dbPointer = nativeOpen(path, optionsPointer);
    }

    public void put(byte @NotNull [] key, byte @NotNull [] value) {
        put(key, value, null);
    }

    public void put(byte @NotNull [] key, byte @NotNull [] value, @Nullable WriteOptions options) {
        checkState();
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        if (options == null)
            options = new WriteOptions();
        nativePut(dbPointer, key, value, options.getSync());
    }

    public void delete(byte @NotNull [] key) {
        delete(key, null);
    }

    public void delete(byte @NotNull [] key, @Nullable WriteOptions options) {
        checkState();
        Objects.requireNonNull(key);
        if (options == null)
            options = new WriteOptions();
        nativeDelete(dbPointer, key, options.getSync());
    }

    public void set(byte @NotNull [] key, byte @Nullable [] value) {
        set(key, value, null);
    }

    public void set(byte @NotNull [] key, byte @Nullable [] value, @Nullable WriteOptions options) {
        if (value == null)
            delete(key, options);
        else
            put(key, value, options);
    }

    public void write(@NotNull WriteBatch writeBatch) {
        write(writeBatch, null);
    }

    public void write(@NotNull WriteBatch writeBatch, @Nullable WriteOptions options) {
        checkState();
        Objects.requireNonNull(writeBatch);
        writeBatch.checkState();
        if (options == null)
            options = new WriteOptions();
        nativeWrite(dbPointer, writeBatch.pointer, options.getSync());
    }

    public byte @Nullable [] get(byte @NotNull [] key) {
        return get(key, null);
    }

    public byte @Nullable [] get(byte @NotNull [] key, @Nullable ReadOptions options) {
        checkState();
        Objects.requireNonNull(key);
        if (options == null)
            options = new ReadOptions();
        Snapshot snapshot = options.getSnapshot();
        long snapshotPointer = 0;
        if (snapshot != null)
            snapshotPointer = snapshot.pointer;
        return nativeGet(dbPointer, key, options.getVerifyChecksums(), options.getFillCache(), snapshotPointer);
    }

    @NotNull
    public DBIterator iterator() {
        return iterator(null);
    }

    @NotNull
    public DBIterator iterator(@Nullable ReadOptions options) {
        checkState();
        if (options == null) {
            options = new ReadOptions();
            options.setFillCache(false);
        }
        Snapshot snapshot = options.getSnapshot();
        long snapshotPointer = 0;
        if (snapshot != null)
            snapshotPointer = snapshot.pointer;
        DBIterator iterator = new DBIterator(nativeNewIterator(dbPointer, options.getVerifyChecksums(), options.getFillCache(), snapshotPointer));
        return iterator;
    }

    @NotNull
    public Snapshot getSnapshot() {
        checkState();
        return new Snapshot(nativeGetSnapshot(dbPointer));
    }

    public void releaseSnapshot(@NotNull Snapshot snapshot) {
        checkState();
        nativeReleaseSnapshot(dbPointer, snapshot.pointer);
    }

    @NotNull
    public String getProperty(@NotNull String property) {
        checkState();
        Objects.requireNonNull(property);
        return nativeGetProperty(dbPointer, property);
    }

    public void compactRange(byte @Nullable [] begin, byte @Nullable [] end) {
        checkState();
        nativeCompactRange(dbPointer, begin, end);
    }

    @Override
    public void close() {
        if (isClosed)
            return;

        nativeRelease(dbPointer);
        nativeReleaseOptions(optionsPointer);
        isClosed = true;
    }


    public static void repair(@NotNull String path) {
        repair(path, null);
    }

    public static void repair(@NotNull String path, @Nullable Options options) {
        Objects.requireNonNull(path);
        if (options == null)
            options = new Options();
        long optionsPointer = nativeNewOptions(
                options.getCreateIfMissing(),
                options.getParanoidChecks(),
                options.getWriteBufferSize(),
                options.getMaxOpenFiles(),
                options.getCacheSize(),
                options.getBlockSize(),
                options.getBlockRestartInterval(),
                options.getMaxFileSize(),
                options.getCompressionType().ordinal()
        );
        try {
            nativeRepair(path, optionsPointer);
        } finally {
            nativeReleaseOptions(optionsPointer);
        }

    }

    private static native long nativeNewOptions(boolean createIfMissing, boolean paranoidChecks, long writeBufferSize, int maxOpenFiles, long cacheSize, long blockSize, int blockRestartInterval, long maxFileSize, int compressionType);

    private static native void nativeReleaseOptions(long optionsPointer);

    private static native long nativeOpen(String path, long optionsPointer);

    private static native void nativeRelease(long pointer);

    private static native void nativePut(long pointer, byte[] key, byte[] value, boolean sync);

    private static native void nativeDelete(long pointer, byte[] key, boolean sync);

    private static native void nativeWrite(long pointer, long batchPtr, boolean sync);

    private static native byte[] nativeGet(long pointer, byte[] key, boolean verifyChecksums, boolean fillCache, long snapshotPointer);

    private static native long nativeNewIterator(long pointer, boolean verifyChecksums, boolean fillCache, long snapshotPointer);

    private static native long nativeGetSnapshot(long pointer);

    private static native void nativeReleaseSnapshot(long pointer, long snapshotPointer);

    private static native String nativeGetProperty(long pointer, String property);

    private static native void nativeCompactRange(long pointer, byte[] begin, byte[] end);

    private static native void nativeRepair(String path, long optionsPointer);


}