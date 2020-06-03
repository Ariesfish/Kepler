package xyz.ariesfish.libusb;

import com.sun.jna.Structure;
import com.sun.jna.ptr.ByteByReference;

public class UsbEndpointDescriptor extends Structure {

    public static class ByReference extends UsbEndpointDescriptor implements Structure.ByReference {

    }

    public byte length;
    public byte descriptorType;
    public byte endpointAddress;
    public byte mAttributes;
    public short maxPacketSize;
    public byte interval;
    public byte refresh;
    public byte syncAddress;
    public ByteByReference extra;
    public int extraLength;
}
