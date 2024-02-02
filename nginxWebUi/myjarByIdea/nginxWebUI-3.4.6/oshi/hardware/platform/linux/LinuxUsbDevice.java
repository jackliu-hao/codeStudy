package oshi.hardware.platform.linux;

import com.sun.jna.platform.linux.Udev;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.UsbDevice;
import oshi.hardware.common.AbstractUsbDevice;

@Immutable
public class LinuxUsbDevice extends AbstractUsbDevice {
   public LinuxUsbDevice(String name, String vendor, String vendorId, String productId, String serialNumber, String uniqueDeviceId, List<UsbDevice> connectedDevices) {
      super(name, vendor, vendorId, productId, serialNumber, uniqueDeviceId, connectedDevices);
   }

   public static List<UsbDevice> getUsbDevices(boolean tree) {
      List<UsbDevice> devices = getUsbDevices();
      if (tree) {
         return devices;
      } else {
         List<UsbDevice> deviceList = new ArrayList();
         Iterator var3 = devices.iterator();

         while(var3.hasNext()) {
            UsbDevice device = (UsbDevice)var3.next();
            deviceList.add(new LinuxUsbDevice(device.getName(), device.getVendor(), device.getVendorId(), device.getProductId(), device.getSerialNumber(), device.getUniqueDeviceId(), Collections.emptyList()));
            addDevicesToList(deviceList, device.getConnectedDevices());
         }

         return deviceList;
      }
   }

   private static List<UsbDevice> getUsbDevices() {
      Udev.UdevContext udev = Udev.INSTANCE.udev_new();
      Udev.UdevEnumerate enumerate = Udev.INSTANCE.udev_enumerate_new(udev);
      Udev.INSTANCE.udev_enumerate_add_match_subsystem(enumerate, "usb");
      Udev.INSTANCE.udev_enumerate_scan_devices(enumerate);
      Udev.UdevListEntry devices = Udev.INSTANCE.udev_enumerate_get_list_entry(enumerate);
      List<String> usbControllers = new ArrayList();
      Map<String, String> nameMap = new HashMap();
      Map<String, String> vendorMap = new HashMap();
      Map<String, String> vendorIdMap = new HashMap();
      Map<String, String> productIdMap = new HashMap();
      Map<String, String> serialMap = new HashMap();
      Map<String, List<String>> hubMap = new HashMap();

      for(Udev.UdevListEntry dev_list_entry = devices; dev_list_entry != null; dev_list_entry = Udev.INSTANCE.udev_list_entry_get_next(dev_list_entry)) {
         String path = Udev.INSTANCE.udev_list_entry_get_name(dev_list_entry);
         Udev.UdevDevice dev = Udev.INSTANCE.udev_device_new_from_syspath(udev, path);
         if ("usb_device".equals(Udev.INSTANCE.udev_device_get_devtype(dev))) {
            String value = Udev.INSTANCE.udev_device_get_sysattr_value(dev, "product");
            if (value != null) {
               nameMap.put(path, value);
            }

            value = Udev.INSTANCE.udev_device_get_sysattr_value(dev, "manufacturer");
            if (value != null) {
               vendorMap.put(path, value);
            }

            value = Udev.INSTANCE.udev_device_get_sysattr_value(dev, "idVendor");
            if (value != null) {
               vendorIdMap.put(path, value);
            }

            value = Udev.INSTANCE.udev_device_get_sysattr_value(dev, "idProduct");
            if (value != null) {
               productIdMap.put(path, value);
            }

            value = Udev.INSTANCE.udev_device_get_sysattr_value(dev, "serial");
            if (value != null) {
               serialMap.put(path, value);
            }

            Udev.UdevDevice parent = Udev.INSTANCE.udev_device_get_parent_with_subsystem_devtype(dev, "usb", "usb_device");
            if (parent == null) {
               usbControllers.add(path);
            } else {
               String parentPath = Udev.INSTANCE.udev_device_get_syspath(parent);
               ((List)hubMap.computeIfAbsent(parentPath, (x) -> {
                  return new ArrayList();
               })).add(path);
            }

            Udev.INSTANCE.udev_device_unref(dev);
         }
      }

      Udev.INSTANCE.udev_enumerate_unref(enumerate);
      Udev.INSTANCE.udev_unref(udev);
      List<UsbDevice> controllerDevices = new ArrayList();
      Iterator var17 = usbControllers.iterator();

      while(var17.hasNext()) {
         String controller = (String)var17.next();
         controllerDevices.add(getDeviceAndChildren(controller, "0000", "0000", nameMap, vendorMap, vendorIdMap, productIdMap, serialMap, hubMap));
      }

      return controllerDevices;
   }

   private static void addDevicesToList(List<UsbDevice> deviceList, List<UsbDevice> list) {
      Iterator var2 = list.iterator();

      while(var2.hasNext()) {
         UsbDevice device = (UsbDevice)var2.next();
         deviceList.add(device);
         addDevicesToList(deviceList, device.getConnectedDevices());
      }

   }

   private static LinuxUsbDevice getDeviceAndChildren(String devPath, String vid, String pid, Map<String, String> nameMap, Map<String, String> vendorMap, Map<String, String> vendorIdMap, Map<String, String> productIdMap, Map<String, String> serialMap, Map<String, List<String>> hubMap) {
      String vendorId = (String)vendorIdMap.getOrDefault(devPath, vid);
      String productId = (String)productIdMap.getOrDefault(devPath, pid);
      List<String> childPaths = (List)hubMap.getOrDefault(devPath, new ArrayList());
      List<UsbDevice> usbDevices = new ArrayList();
      Iterator var13 = childPaths.iterator();

      while(var13.hasNext()) {
         String path = (String)var13.next();
         usbDevices.add(getDeviceAndChildren(path, vendorId, productId, nameMap, vendorMap, vendorIdMap, productIdMap, serialMap, hubMap));
      }

      Collections.sort(usbDevices);
      return new LinuxUsbDevice((String)nameMap.getOrDefault(devPath, vendorId + ":" + productId), (String)vendorMap.getOrDefault(devPath, ""), vendorId, productId, (String)serialMap.getOrDefault(devPath, ""), devPath, usbDevices);
   }
}
