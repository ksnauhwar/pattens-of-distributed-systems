package com.ksn.patterns.wal;

import java.io.IOException;

public abstract class Command {
    public abstract byte[] serialize() throws IOException;
}
