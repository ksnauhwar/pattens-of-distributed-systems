package com.ksn.patterns.wal;

public class WALEntry {
    private final Command command;

    public WALEntry(Command command){
        this.command = command;
    }

    public byte[] data(){
        return command.serialize();
    }
}
