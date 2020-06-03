package xyz.ariesfish.libusb;

import com.sun.jna.Structure;

public class UsbDeviceDescriptor extends Structure {

    public byte length;
    public byte descriptorType;
    public short bcdUsb;
    public byte deviceClass;
    public byte deviceSubClass;
    public byte deviceProtocol;
    public byte maxPacketSize;
    public short idVendor;
    public short idProduct;
    public short bcdDevice;
    public byte manufacturer;
    public byte product;
    public byte serialNumber;
    public byte numConfigurations;
}
