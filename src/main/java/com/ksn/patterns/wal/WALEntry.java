package com.ksn.patterns.wal;


import java.nio.ByteBuffer;

public class WALEntry {
    static int sizeOfInt; //TODO: why is this required?
    private final byte[] data;

    public WALEntry(byte[] data){
        this.data = data;
    }

    public ByteBuffer serialize(){
      ByteBuffer buffer =  ByteBuffer.allocate(bufferSize());
      buffer.clear();//TODO: why do we clear first?
      buffer.put(data);
      return buffer;
    }

    private int bufferSize() {
        return sizeOfData() + sizeOfInt;
    }

    private int sizeOfData() {
        return data.length;
    }

}
