package com.ksn.patterns.wal;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class WALEntry {
    private final Command command;

    public WALEntry(Command command){
        this.command = command;
    }

    public byte[] data(){
        byte[] commandBytes = command.serialize();
        try(ByteArrayOutputStream walEntryBytes = new ByteArrayOutputStream();
            DataOutputStream walEntry = new DataOutputStream(walEntryBytes)){
            walEntry.writeInt(commandBytes.length);
            walEntry.write(commandBytes);
            return walEntryBytes.toByteArray();
        }catch(Exception e){
            throw new RuntimeException("Error while getting wal entry",e);
        }
    }
}
