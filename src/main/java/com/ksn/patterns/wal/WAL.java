package com.ksn.patterns.wal;

import com.ksn.patterns.common.Config;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class WAL implements Closeable {
    private final Config config;
    private final FileChannel wal;
    public WAL(Config config) throws IOException {
        this.config = config;
        wal = FileChannel.open(Paths.get(config.walDir(),"commit.log"),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND,
                StandardOpenOption.READ);
    }

    public synchronized void append(WALEntry walEntry){
        ByteBuffer walEntryBuffer = ByteBuffer.wrap(walEntry.data());

        try {
            while(walEntryBuffer.hasRemaining()) {
                wal.write(walEntryBuffer);
            }
            wal.force(true);
        } catch (IOException e) {
            throw new RuntimeException("Failed to append to wal ", e);
        }

    }

    @Override
    public synchronized void close(){
        if(wal!=null && wal.isOpen()){
            try {
                wal.force(true);
                wal.close();
            } catch (IOException e) {
                throw new RuntimeException("failed to close the wal ",e);
            }
        }
    }
}
