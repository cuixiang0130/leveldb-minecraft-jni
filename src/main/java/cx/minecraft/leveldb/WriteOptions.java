package cx.minecraft.leveldb;

public class WriteOptions {

    private boolean sync = false;

    public boolean getSync() {
        return sync;
    }

    public void setSync(boolean sync) {
        this.sync = sync;
    }

}
