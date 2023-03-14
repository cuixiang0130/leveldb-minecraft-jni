package cx.minecraft.leveldb;

import java.io.Closeable;

public abstract class NativeObject implements Closeable {
    protected boolean isClosed = false;

    protected final void checkState(){
        if(isClosed)
            throw new IllegalStateException("NativeObject closed!");
    }

}
