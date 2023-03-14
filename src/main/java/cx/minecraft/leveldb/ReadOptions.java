package cx.minecraft.leveldb;

import org.jetbrains.annotations.Nullable;

public class ReadOptions {

    private boolean verifyChecksums = false;
    private boolean fillCache = true;
    @Nullable
    private Snapshot snapshot = null;

    public boolean getVerifyChecksums() {
        return verifyChecksums;
    }

    public void setVerifyChecksums(boolean verifyChecksums) {
        this.verifyChecksums = verifyChecksums;
    }

    public boolean getFillCache() {
        return fillCache;
    }

    public void setFillCache(boolean fillCache) {
        this.fillCache = fillCache;
    }

    @Nullable
    public Snapshot getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(@Nullable Snapshot snapshot) {
        this.snapshot = snapshot;
    }


}
