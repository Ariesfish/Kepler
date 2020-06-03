package xyz.ariesfish.libusb;

import com.sun.jna.Structure;
import com.sun.jna.ptr.ByteByReference;

public class UsbInterfaceDescriptor extends Structure {
    public static class ByReference extends UsbInterfaceDescriptor implements Structure.ByReference {

    }

    public byte length;
    public byte descriptorType;
    public byte interfaceNumber;
    public byte alternateSetting;
    public byte numEndpoints;
    public byte interfaceClass;
    public byte interfaceSubClass;
    public byte interfaceProtocol;
    public byte usbInterface;
    public UsbEndpointDescriptor.ByReference endpoint;
    public ByteByReference extra;
    public int extraLength;
}
