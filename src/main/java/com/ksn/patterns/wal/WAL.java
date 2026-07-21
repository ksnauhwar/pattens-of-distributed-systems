package com.ksn.patterns.wal;

import com.ksn.patterns.common.Config;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.CRC32;

public class WAL implements Closeable {

    private final FileChannel wal;
    public WAL(Config config) throws IOException {

        wal = FileChannel.open(Paths.get(config.walDir(),"commit.log"),
                StandardOpenOption.CREATE,
                StandardOpenOption.READ,
                StandardOpenOption.WRITE);
        wal.position(wal.size());
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

    public Map<String, String> restore() {
        Map<String,String> state = new HashMap<>();
        try {
            wal.position(0);
            ByteBuffer payloadLengthBuffer = ByteBuffer.allocate(Integer.BYTES);

            while(true){
                payloadLengthBuffer.clear();
                while(payloadLengthBuffer.hasRemaining()){
                    if(wal.read(payloadLengthBuffer) == -1){
                        return state;
                    }
                }
                payloadLengthBuffer.flip();

                int payloadLength = payloadLengthBuffer.getInt();
                ByteBuffer payloadBuffer = ByteBuffer.allocate(payloadLength);
                while(payloadBuffer.hasRemaining()){
                    wal.read(payloadBuffer);
                }
                payloadBuffer.flip();

                try(DataInputStream payloadInputStream = new DataInputStream(new ByteArrayInputStream(payloadBuffer.array()))){
                    int commandTypeLength = payloadInputStream.readInt();
                    byte[] commandTypeBytes = new byte[commandTypeLength];
                    payloadInputStream.readFully(commandTypeBytes);
                    switch(CommandType.valueOf(new String(commandTypeBytes))){
                        case Save:{
                            int keyLength = payloadInputStream.readInt();
                            byte[] keyBytes = new byte[keyLength];
                            payloadInputStream.readFully(keyBytes);
                            String key = new String(keyBytes);

                            int valueLength = payloadInputStream.readInt();
                            byte[] valueBytes = new byte[valueLength];
                            payloadInputStream.readFully(valueBytes);
                            String value = new String(valueBytes);

                            long checksum = payloadInputStream.readLong();

                            CRC32 crc = new CRC32();
                            crc.update(String.format("%s%s",key,value).getBytes(StandardCharsets.UTF_8));
                            if(checksum != crc.getValue()){
                                throw new Exception("checksums do not match");
                            }
                            state.put(key,value);
                            break;
                        }
                        case Delete:{
                            break;
                        }
                    }
                }catch(Exception e){
                    throw new RuntimeException("Unable to validate the checksums for the entry in wal",e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error while changing position to 0 for reading wal", e);
        }
    }

}
