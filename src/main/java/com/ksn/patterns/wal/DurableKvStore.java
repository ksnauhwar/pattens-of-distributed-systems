package com.ksn.patterns.wal;

import java.util.Map;

public class DurableKvStore {
    private final Map<String,String> store;
    private final WAL wal;

    public DurableKvStore(WAL wal){
        this.wal = wal;
        store = wal.restore();
    }

    public void add(String key, String value){
        appendLog(key,value);
        store.put(key,value);
    }

    public String get(String key){
        return store.get(key);
    }

    private void appendLog(String key,String value){
        wal.append(new WALEntry(new SaveCommand(key,value)));
    }

    //simulates crash.
    public void close() {
        wal.close();
        store.clear();
    }
}
