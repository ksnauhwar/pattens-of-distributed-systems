package com.ksn.patterns.wal;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.CRC32;

public class SaveCommand extends Command {
    final private String key;
    final private String value;

    public SaveCommand(String key,String value){
        this.key = key;
        this.value = value;
    }

    @Override
    public byte[] serialize() throws IOException {
        ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
        DataOutputStream command = new DataOutputStream(commandBytes);
        command.writeUTF(CommandType.Save.toString());
        command.writeUTF(key);
        command.writeUTF(value);
        command.writeLong(keyValueCheckSum());
        return commandBytes.toByteArray();
    }

    private long keyValueCheckSum() {
        CRC32 crc = new CRC32();
        String keyValue = key + value;
        crc.update(keyValue.getBytes());
        return crc.getValue();
    }

}

