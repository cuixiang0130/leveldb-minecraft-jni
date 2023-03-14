package cx.minecraft.leveldb;

public class Options {
    private boolean createIfMissing = true;
    private boolean errorIfExists = false;
    private boolean paranoidChecks = false;
    private long writeBufferSize = 4 * 1024 * 1024;
    private int maxOpenFiles = 1000;

    private long cacheSize = 8 * 1024 * 1024;

    private long blockSize = 4 * 1024;

    private int blockRestartInterval = 16;

    private long maxFileSize = 2 * 1024 * 1024;

    private CompressionType compressionType = CompressionType.NONE;

    public boolean getCreateIfMissing() {
        return createIfMissing;
    }

    public void setCreateIfMissing(boolean createIfMissing) {
        this.createIfMissing = createIfMissing;
    }

    public boolean getErrorIfExists() {
        return errorIfExists;
    }

    public void setErrorIfExists(boolean errorIfExists) {
        this.errorIfExists = errorIfExists;
    }

    public boolean getParanoidChecks() {
        return paranoidChecks;
    }

    public void setParanoidChecks(boolean paranoidChecks) {
        this.paranoidChecks = paranoidChecks;
    }

    public long getWriteBufferSize() {
        return writeBufferSize;
    }

    public void setWriteBufferSize(long writeBufferSize) {
        this.writeBufferSize = writeBufferSize;
    }

    public int getMaxOpenFiles() {
        return maxOpenFiles;
    }

    public void setMaxOpenFiles(int maxOpenFiles) {
        this.maxOpenFiles = maxOpenFiles;
    }

    public long getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(long cacheSize) {
        this.cacheSize = cacheSize;
    }

    public long getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(long blockSize) {
        this.blockSize = blockSize;
    }

    public int getBlockRestartInterval() {
        return blockRestartInterval;
    }

    public void setBlockRestartInterval(int blockRestartInterval) {
        this.blockRestartInterval = blockRestartInterval;
    }

    public long getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public CompressionType getCompressionType() {
        return compressionType;
    }

    public void setCompressionType(CompressionType compressionType) {
        this.compressionType = compressionType;
    }
}
