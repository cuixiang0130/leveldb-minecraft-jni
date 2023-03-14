package cx.minecraft.leveldb;

public final class CorruptException extends RuntimeException {
    public CorruptException() {
        super();
    }

    public CorruptException(String message) {
        super(message);
    }
}
