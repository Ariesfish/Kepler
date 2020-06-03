package xyz.ariesfish.libusb;

import com.sun.jna.Structure;

public class UsbBus extends Structure {

    public static class ByReference extends UsbBus implements Structure.ByReference {

    }

    public UsbBus.ByReference next;
    public UsbBus.ByReference prev;
    public byte[] dirname = new byte[LibUsbConstants.PATH_MAX+1];
    public UsbDevice.ByReference devices;
    public int location;
    public UsbDevice.ByReference rootDevice;
}
