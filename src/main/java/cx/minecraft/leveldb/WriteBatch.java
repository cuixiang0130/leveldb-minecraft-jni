package cx.minecraft.leveldb;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class WriteBatch extends NativeObject {

    final long pointer;

    public WriteBatch() {
        this.pointer = nativeNew();
    }

    public void put(byte @NotNull [] key, byte @NotNull [] value) {
        checkState();
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        nativePut(pointer, key, value);
    }

    public void delete(byte @NotNull [] key) {
        checkState();
        Objects.requireNonNull(key);
        nativeDelete(pointer, key);
    }

    public void set(byte @NotNull [] key, byte @Nullable [] value) {
        if (value == null)
            delete(key);
        else
            put(key, value);
    }


    public void clear() {
        checkState();
        nativeClear(pointer);
    }

    public void append(@NotNull WriteBatch source) {
        checkState();
        Objects.requireNonNull(source);
        nativeAppend(pointer, source.pointer);
    }

    @Override
    public void close() {
        if (!isClosed)
            return;
        nativeRelease(pointer);
        isClosed = true;
    }

    private static native long nativeNew();

    private static native void nativeRelease(long pointer);

    private static native void nativePut(long pointer, byte[] key, byte[] value);

    private static native void nativeDelete(long pointer, byte[] key);

    private static native void nativeClear(long pointer);

    private static native void nativeAppend(long pointer, long sourceBatchPointer);

}
