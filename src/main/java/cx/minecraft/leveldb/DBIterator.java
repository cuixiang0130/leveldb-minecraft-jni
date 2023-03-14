package cx.minecraft.leveldb;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

public final class DBIterator extends NativeObject {

    private final long pointer;

    DBIterator(long pointer) {
        this.pointer = pointer;
    }

    public boolean valid() {
        checkState();
        return nativeValid(pointer);
    }

    public void seekToFirst() {
        checkState();
        nativeSeekToFirst(pointer);
    }

    public void seekToLast() {
        checkState();
        nativeSeekToLast(pointer);
    }

    public void seek(byte[] key) {
        checkState();
        Objects.requireNonNull(key);
        nativeSeek(pointer, key);
    }

    public void next() {
        checkState();
        nativeNext(pointer);
    }

    public void prev() {
        checkState();
        nativePrev(pointer);
    }

    public byte @NotNull [] key() {
        if (!valid())
            throw new NoSuchElementException();
        return nativeKey(pointer);
    }

    public byte @NotNull [] value() {
        if (!valid())
            throw new NoSuchElementException();
        return nativeValue(pointer);
    }

    @Override
    public void close() {
        if (!isClosed)
            return;

        nativeRelease(pointer);
        isClosed = true;
    }

    private static native boolean nativeValid(long iteratorPtr);

    private static native void nativeSeekToFirst(long iteratorPtr);

    private static native void nativeSeekToLast(long iteratorPtr);

    private static native void nativeSeek(long iteratorPtr, byte[] key);

    private static native void nativeNext(long iteratorPtr);

    private static native void nativePrev(long iteratorPtr);

    private static native byte[] nativeKey(long iteratorPtr);

    private static native byte[] nativeValue(long iteratorPtr);

    private static native void nativeRelease(long iteratorPtr);

}