package com.ksn.patterns.wal;

import com.ksn.patterns.common.Config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class WriteAheadLog {
    RandomAccessFile file;
    FileChannel fileChannel;
    private final Config config;

    public WriteAheadLog(String walDirectory) throws FileNotFoundException {
        config = new Config(walDirectory);
        file = new RandomAccessFile(new File(walDirectory),"rw");
        fileChannel = file.getChannel();
    }

    public void writeEntry(WALEntry wal) {
        writeToChannel(wal);
        flush();
    }

    private void flush() {
        try {
            fileChannel.force(true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeToChannel(WALEntry wal) {
        try {
            ByteBuffer byteBuffer = wal.serialize();
            byteBuffer.flip();
            while(byteBuffer.hasRemaining()) {
                fileChannel.write(byteBuffer);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
