package oshi.hardware.common;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.UsbDevice;

@Immutable
public abstract class AbstractUsbDevice implements UsbDevice {
   private final String name;
   private final String vendor;
   private final String vendorId;
   private final String productId;
   private final String serialNumber;
   private final String uniqueDeviceId;
   private final List<UsbDevice> connectedDevices;

   protected AbstractUsbDevice(String name, String vendor, String vendorId, String productId, String serialNumber, String uniqueDeviceId, List<UsbDevice> connectedDevices) {
      this.name = name;
      this.vendor = vendor;
      this.vendorId = vendorId;
      this.productId = productId;
      this.serialNumber = serialNumber;
      this.uniqueDeviceId = uniqueDeviceId;
      this.connectedDevices = Collections.unmodifiableList(connectedDevices);
   }

   public String getName() {
      return this.name;
   }

   public String getVendor() {
      return this.vendor;
   }

   public String getVendorId() {
      return this.vendorId;
   }

   public String getProductId() {
      return this.productId;
   }

   public String getSerialNumber() {
      return this.serialNumber;
   }

   public String getUniqueDeviceId() {
      return this.uniqueDeviceId;
   }

   public List<UsbDevice> getConnectedDevices() {
      return this.connectedDevices;
   }

   public int compareTo(UsbDevice usb) {
      return this.getName().compareTo(usb.getName());
   }

   public String toString() {
      return indentUsb(this, 1);
   }

   private static String indentUsb(UsbDevice usbDevice, int indent) {
      String indentFmt = indent > 2 ? String.format("%%%ds|-- ", indent - 4) : String.format("%%%ds", indent);
      StringBuilder sb = new StringBuilder(String.format(indentFmt, ""));
      sb.append(usbDevice.getName());
      if (usbDevice.getVendor().length() > 0) {
         sb.append(" (").append(usbDevice.getVendor()).append(')');
      }

      if (usbDevice.getSerialNumber().length() > 0) {
         sb.append(" [s/n: ").append(usbDevice.getSerialNumber()).append(']');
      }

      Iterator var4 = usbDevice.getConnectedDevices().iterator();

      while(var4.hasNext()) {
         UsbDevice connected = (UsbDevice)var4.next();
         sb.append('\n').append(indentUsb(connected, indent + 4));
      }

      return sb.toString();
   }
}
