package com.ksn.patterns.wal;

import com.ksn.patterns.common.Config;
import com.ksn.patterns.common.TestUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DurableKvStoreTests {

    @Test
    public void durableKvStoreIsDurable() throws IOException {
        File walDir = TestUtils.tempDir("distrib/patterns/wal");
        DurableKvStore kv = new DurableKvStore(new WAL(new Config(walDir.getAbsolutePath())));
        kv.add("title", "Microservices");
        kv.add("author", "Unmesh");
        kv.close();


        kv = new DurableKvStore(new WAL(new Config(walDir.getAbsolutePath())));

        assertEquals("Microservices", kv.get("title"));
        assertEquals("Unmesh",kv.get("author"));
    }
}
