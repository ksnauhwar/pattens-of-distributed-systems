package com.ksn.patterns.wal;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;

public class SaveCommand extends Command {
    final private String key;
    final private String value;

    public SaveCommand(String key,String value){
        this.key = key;
        this.value = value;
    }

    @Override
    public byte[] serialize() {
        byte[] typeBytes = CommandType.Save.toString().getBytes(StandardCharsets.UTF_8);
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] valueBytes = value.getBytes(StandardCharsets.UTF_8);

        try(ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
            DataOutputStream command = new DataOutputStream(commandBytes)) {
            command.writeInt(typeBytes.length);
            command.write(typeBytes);
            command.writeInt(keyBytes.length);
            command.write(keyBytes);
            command.writeInt(valueBytes.length);
            command.write(valueBytes);
            command.writeLong(keyValueCheckSum());
            return commandBytes.toByteArray();
        }catch(IOException exception){
            return new byte[0];
        }
    }

    private long keyValueCheckSum() {
        CRC32 crc = new CRC32();
        String keyValue = key + value;
        crc.update(keyValue.getBytes());
        return crc.getValue();
    }

}

