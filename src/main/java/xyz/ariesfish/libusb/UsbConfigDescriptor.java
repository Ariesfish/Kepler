package xyz.ariesfish.libusb;

import com.sun.jna.Structure;
import com.sun.jna.ptr.ByteByReference;

public class UsbConfigDescriptor extends Structure {
    public static class ByReference extends UsbConfigDescriptor implements Structure.ByReference {

    }

    public byte length;
    public byte descriptorType;
    public short totalLength;
    public byte numInterfaces;
    public byte configValue;
    public byte configuration;
    public byte mAttributes;
    public byte maxPower;
    public UsbInterface.ByReference usbInterface;
    public ByteByReference extra;
    public int extraLength;
}
