package xyz.ariesfish.mdnssd.dns;

import java.nio.ByteBuffer;

public class UnknownRecord extends Record {
    public UnknownRecord(ByteBuffer buffer, String name, Record.Class recordClass, long ttl, int length) {
        super(name, recordClass, ttl);
        byte[] toSkip = new byte[length];
        buffer.get(toSkip);
    }
}
