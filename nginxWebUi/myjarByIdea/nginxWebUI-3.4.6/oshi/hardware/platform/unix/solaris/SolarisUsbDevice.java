package oshi.hardware.platform.unix.solaris;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.UsbDevice;
import oshi.hardware.common.AbstractUsbDevice;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@Immutable
public class SolarisUsbDevice extends AbstractUsbDevice {
   private static final String PCI_TYPE_USB = "000c";

   public SolarisUsbDevice(String name, String vendor, String vendorId, String productId, String serialNumber, String uniqueDeviceId, List<UsbDevice> connectedDevices) {
      super(name, vendor, vendorId, productId, serialNumber, uniqueDeviceId, connectedDevices);
   }

   public static List<UsbDevice> getUsbDevices(boolean tree) {
      List<UsbDevice> devices = getUsbDevices();
      if (tree) {
         return Collections.unmodifiableList(devices);
      } else {
         List<UsbDevice> deviceList = new ArrayList();
         Iterator var3 = devices.iterator();

         while(var3.hasNext()) {
            UsbDevice device = (UsbDevice)var3.next();
            deviceList.add(new SolarisUsbDevice(device.getName(), device.getVendor(), device.getVendorId(), device.getProductId(), device.getSerialNumber(), device.getUniqueDeviceId(), Collections.emptyList()));
            addDevicesToList(deviceList, device.getConnectedDevices());
         }

         return Collections.unmodifiableList(deviceList);
      }
   }

   private static List<UsbDevice> getUsbDevices() {
      Map<String, String> nameMap = new HashMap();
      Map<String, String> vendorIdMap = new HashMap();
      Map<String, String> productIdMap = new HashMap();
      Map<String, List<String>> hubMap = new HashMap();
      Map<String, String> deviceTypeMap = new HashMap();
      List<String> devices = ExecutingCommand.runNative("prtconf -pv");
      if (devices.isEmpty()) {
         return Collections.emptyList();
      } else {
         Map<Integer, String> lastParent = new HashMap();
         String key = "";
         int indent = 0;
         List<String> usbControllers = new ArrayList();
         Iterator var10 = devices.iterator();

         while(var10.hasNext()) {
            String line = (String)var10.next();
            if (line.contains("Node 0x")) {
               key = line.replaceFirst("^\\s*", "");
               int depth = line.length() - key.length();
               if (indent == 0) {
                  indent = depth;
               }

               lastParent.put(depth, key);
               if (depth > indent) {
                  ((List)hubMap.computeIfAbsent((String)lastParent.get(depth - indent), (x) -> {
                     return new ArrayList();
                  })).add(key);
               } else {
                  usbControllers.add(key);
               }
            } else if (!key.isEmpty()) {
               line = line.trim();
               if (line.startsWith("model:")) {
                  nameMap.put(key, ParseUtil.getSingleQuoteStringValue(line));
               } else if (line.startsWith("name:")) {
                  nameMap.putIfAbsent(key, ParseUtil.getSingleQuoteStringValue(line));
               } else if (line.startsWith("vendor-id:")) {
                  vendorIdMap.put(key, line.substring(line.length() - 4));
               } else if (line.startsWith("device-id:")) {
                  productIdMap.put(key, line.substring(line.length() - 4));
               } else if (line.startsWith("class-code:")) {
                  deviceTypeMap.putIfAbsent(key, line.substring(line.length() - 8, line.length() - 4));
               } else if (line.startsWith("device_type:")) {
                  deviceTypeMap.putIfAbsent(key, ParseUtil.getSingleQuoteStringValue(line));
               }
            }
         }

         List<UsbDevice> controllerDevices = new ArrayList();
         Iterator var14 = usbControllers.iterator();

         while(true) {
            String controller;
            do {
               if (!var14.hasNext()) {
                  return controllerDevices;
               }

               controller = (String)var14.next();
            } while(!"000c".equals(deviceTypeMap.getOrDefault(controller, "")) && !"usb".equals(deviceTypeMap.getOrDefault(controller, "")));

            controllerDevices.add(getDeviceAndChildren(controller, "0000", "0000", nameMap, vendorIdMap, productIdMap, hubMap));
         }
      }
   }

   private static void addDevicesToList(List<UsbDevice> deviceList, List<UsbDevice> list) {
      Iterator var2 = list.iterator();

      while(var2.hasNext()) {
         UsbDevice device = (UsbDevice)var2.next();
         deviceList.add(device);
         addDevicesToList(deviceList, device.getConnectedDevices());
      }

   }

   private static SolarisUsbDevice getDeviceAndChildren(String devPath, String vid, String pid, Map<String, String> nameMap, Map<String, String> vendorIdMap, Map<String, String> productIdMap, Map<String, List<String>> hubMap) {
      String vendorId = (String)vendorIdMap.getOrDefault(devPath, vid);
      String productId = (String)productIdMap.getOrDefault(devPath, pid);
      List<String> childPaths = (List)hubMap.getOrDefault(devPath, new ArrayList());
      List<UsbDevice> usbDevices = new ArrayList();
      Iterator var11 = childPaths.iterator();

      while(var11.hasNext()) {
         String path = (String)var11.next();
         usbDevices.add(getDeviceAndChildren(path, vendorId, productId, nameMap, vendorIdMap, productIdMap, hubMap));
      }

      Collections.sort(usbDevices);
      return new SolarisUsbDevice((String)nameMap.getOrDefault(devPath, vendorId + ":" + productId), "", vendorId, productId, "", devPath, usbDevices);
   }
}
