package cx.minecraft.leveldb;

public enum CompressionType {
    NONE(0x00),
    SNAPPY(0x01),
    ZLIB(0x02),
    ZLIB_RAW(0x04);
    CompressionType(int id){}

}
