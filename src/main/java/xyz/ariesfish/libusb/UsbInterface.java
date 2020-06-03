package xyz.ariesfish.libusb;

import com.sun.jna.Structure;

public class UsbInterface extends Structure {
    public static class ByReference extends UsbInterface implements Structure.ByReference {

    }

    public UsbInterfaceDescriptor.ByReference altSetting;
    public int numAltSetting;
}
