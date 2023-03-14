package cx.minecraft.leveldb;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class DBTest {

    private static final String TEST_DB_NAME = "test";
    private static final Options options = new Options();

    static {
        options.setCompressionType(CompressionType.ZLIB);
        options.setCreateIfMissing(true);
    }

    @Test
    public void testDB() {
        byte[] testKey = "aaa".getBytes();
        byte[] testValue1 = "bbb".getBytes();
        byte[] testValue2 = "ccc".getBytes();
        try (DB db = new DB(TEST_DB_NAME, options)) {
            db.put(testKey, testValue1);
            assert Arrays.equals(db.get(testKey), testValue1);
            db.put(testKey, testValue2);
            assert Arrays.equals(db.get(testKey), testValue2);
            db.delete(testKey);
            assert db.get(testKey) == null;
            db.compactRange(null, null);
            db.set(testKey, testValue1);
            assert Arrays.equals(db.get(testKey), testValue1);
            db.set(testKey, null);
            assert db.get(testKey) == null;
            System.out.println(db.getProperty("leveldb.stats"));
        }

    }

    @Test
    public void testWriteBatch() {
        byte[] testKey = "aaa".getBytes();
        byte[] testValue1 = "bbb".getBytes();
        byte[] testValue2 = "ccc".getBytes();
        try (DB db = new DB(TEST_DB_NAME, options)) {
            WriteBatch batch = new WriteBatch();
            batch.put(testKey, testValue1);
            batch.put(testKey, testValue2);
            batch.delete(testKey);
            batch.set(testKey, testValue2);
            db.write(batch);
            batch.close();
            assert Arrays.equals(db.get(testKey), testValue2);
        }

    }

    @Test
    public void testIterator() {
        String prefix = "test_iteration:";
        try (DB db = new DB(TEST_DB_NAME, options)) {
            int num = 10;
            for (int i = 0; i < num; i++) {
                db.put((prefix + i).getBytes(), new byte[]{(byte) i});
            }
            try (DBIterator iterator = db.iterator()) {
                int forward = 0;
                for (iterator.seekToFirst(); iterator.valid(); iterator.next()) {
                    forward++;
                }
                int reverse = 0;
                for (iterator.seekToLast(); iterator.valid(); iterator.prev()) {
                    reverse++;
                }
                assert forward == reverse;
                int cnt = 0;
                for (iterator.seek(prefix.getBytes()); iterator.valid() && new String(iterator.key()).startsWith(prefix); iterator.next()) {
                    cnt++;
                }
                assert cnt == num;
            }
        }
    }
}
