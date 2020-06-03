package xyz.ariesfish.libusb;

import com.sun.jna.Library;
import com.sun.jna.Native;

import java.nio.Buffer;

public interface Usb extends Library {

    Usb libUsb = (Usb) Native.load("usb", Usb.class);

    void init();
    int findBuses();
    int findDevices();
    UsbBus getBuses();

    UsbDeviceHandle open(UsbDevice device);
    int close(UsbDeviceHandle deviceHandle);
    int setConfiguration(UsbDeviceHandle deviceHandle, int configuration);
    int setAltInterface(UsbDeviceHandle deviceHandle, int alternate);
    int resetEp(UsbDeviceHandle deviceHandle, int ep);
    int clearHalt(UsbDeviceHandle deviceHandle, int ep);
    int reset(UsbDeviceHandle deviceHandle);

    int claimInterface(UsbDeviceHandle deviceHandle, int usbInterface);
    int releaseInterface(UsbDeviceHandle deviceHandle, int usbInterface);

    int controlMessage(UsbDeviceHandle deviceHandle, int requestType, int request, int value,
                       int index, byte[] bytes, int size, int timeout);
    int controlMessage(UsbDeviceHandle deviceHandle, int requestType, int request, int value,
                       int index, Buffer bytes, int size, int timeout);
    int getString(UsbDeviceHandle deviceHandle, int index, int langId, byte[] buffer, int bufferLength);
    int getString(UsbDeviceHandle deviceHandle, int index, int langId, Buffer buffer, int bufferLength);
    int getStringSimple(UsbDeviceHandle deviceHandle, int index, byte[] buffer, int bufferLength);
    int getStringSimple(UsbDeviceHandle deviceHandle, int index, Buffer buffer, int bufferLength);
    int getDescriptor(UsbDeviceHandle deviceHandle, byte type, byte index, byte[] buffer, int size);
    int getDescriptor(UsbDeviceHandle deviceHandle, byte type, byte index, Buffer buffer, int size);
    int getDescriptorByEndpoint(UsbDeviceHandle deviceHandle, int ep, byte type, byte index, byte[] buffer, int size);
    int getDescriptorByEndpoint(UsbDeviceHandle deviceHandle, int ep, byte type, byte index, Buffer buffer, int size);

    int bulkWrite(UsbDeviceHandle deviceHandle, int ep, byte[] bytes, int size, int timeout);
    int bulkWrite(UsbDeviceHandle deviceHandle, int ep, Buffer bytes, int size, int timeout);
    int bulkRead(UsbDeviceHandle deviceHandle, int ep, byte[] bytes, int size, int timeout);
    int bulkRead(UsbDeviceHandle deviceHandle, int ep, Buffer bytes, int size, int timeout);

    int interruptWrite(UsbDeviceHandle deviceHandle, int ep, byte[] bytes, int size, int timeout);
    int interruptWrite(UsbDeviceHandle deviceHandle, int ep, Buffer bytes, int size, int timeout);
    int interruptRead(UsbDeviceHandle deviceHandle, int ep, byte[] bytes, int size, int timeout);
    int interruptRead(UsbDeviceHandle deviceHandle, int ep, Buffer bytes, int size, int timeout);

    int getDriverNp(UsbDeviceHandle deviceHandle, int usbInterface, byte[] name, int nameLength);
    int getDriverNp(UsbDeviceHandle deviceHandle, int usbInterface, Buffer name, int nameLength);

    int detachKernelDriver(UsbDeviceHandle deviceHandle, int usbInterface);
    void setDebug(int level);

    String getErrorString();
    UsbDevice createUsbDevice(UsbDeviceHandle deviceHandle);
}
