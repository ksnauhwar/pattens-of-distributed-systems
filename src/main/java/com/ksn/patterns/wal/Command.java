package com.ksn.patterns.wal;

public abstract class Command {
    public abstract byte[] serialize();
}
