package oshi.hardware.platform.mac;

import com.sun.jna.Pointer;
import com.sun.jna.platform.mac.CoreFoundation;
import com.sun.jna.platform.mac.IOKit;
import com.sun.jna.platform.mac.IOKitUtil;
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
public class MacUsbDevice extends AbstractUsbDevice {
   private static final CoreFoundation CF;

   public MacUsbDevice(String name, String vendor, String vendorId, String productId, String serialNumber, String uniqueDeviceId, List<UsbDevice> connectedDevices) {
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
            addDevicesToList(deviceList, device.getConnectedDevices());
         }

         return Collections.unmodifiableList(deviceList);
      }
   }

   private static List<UsbDevice> getUsbDevices() {
      Map<Long, String> nameMap = new HashMap();
      Map<Long, String> vendorMap = new HashMap();
      Map<Long, String> vendorIdMap = new HashMap();
      Map<Long, String> productIdMap = new HashMap();
      Map<Long, String> serialMap = new HashMap();
      Map<Long, List<Long>> hubMap = new HashMap();
      List<Long> usbControllers = new ArrayList();
      IOKit.IOIterator iter = IOKitUtil.getMatchingServices("IOUSBController");
      if (iter != null) {
         CoreFoundation.CFStringRef locationIDKey = CoreFoundation.CFStringRef.createCFString("locationID");
         CoreFoundation.CFStringRef ioPropertyMatchKey = CoreFoundation.CFStringRef.createCFString("IOPropertyMatch");

         for(IOKit.IORegistryEntry device = iter.next(); device != null; device = iter.next()) {
            long id = device.getRegistryEntryID();
            usbControllers.add(id);
            nameMap.put(id, device.getName());
            CoreFoundation.CFTypeRef ref = device.createCFProperty(locationIDKey);
            if (ref != null) {
               getControllerIdByLocation(id, ref, locationIDKey, ioPropertyMatchKey, vendorIdMap, productIdMap);
               ref.release();
            }

            IOKit.IOIterator childIter = device.getChildIterator("IOService");

            for(IOKit.IORegistryEntry childDevice = childIter.next(); childDevice != null; childDevice = childIter.next()) {
               long childId = childDevice.getRegistryEntryID();
               IOKit.IORegistryEntry parent = childDevice.getParentEntry("IOUSB");
               long parentId;
               if (parent != null && parent.conformsTo("IOUSBDevice")) {
                  parentId = parent.getRegistryEntryID();
               } else {
                  parentId = id;
               }

               if (parent != null) {
                  parent.release();
               }

               ((List)hubMap.computeIfAbsent(parentId, (x) -> {
                  return new ArrayList();
               })).add(childId);
               nameMap.put(childId, childDevice.getName().trim());
               String vendor = childDevice.getStringProperty("USB Vendor Name");
               if (vendor != null) {
                  vendorMap.put(childId, vendor.trim());
               }

               Long vendorId = childDevice.getLongProperty("idVendor");
               if (vendorId != null) {
                  vendorIdMap.put(childId, String.format("%04x", 65535L & vendorId));
               }

               Long productId = childDevice.getLongProperty("idProduct");
               if (productId != null) {
                  productIdMap.put(childId, String.format("%04x", 65535L & productId));
               }

               String serial = childDevice.getStringProperty("USB Serial Number");
               if (serial != null) {
                  serialMap.put(childId, serial.trim());
               }

               childDevice.release();
            }

            childIter.release();
            device.release();
         }

         iter.release();
         locationIDKey.release();
         ioPropertyMatchKey.release();
      }

      List<UsbDevice> controllerDevices = new ArrayList();
      Iterator var26 = usbControllers.iterator();

      while(var26.hasNext()) {
         Long controller = (Long)var26.next();
         controllerDevices.add(getDeviceAndChildren(controller, "0000", "0000", nameMap, vendorMap, vendorIdMap, productIdMap, serialMap, hubMap));
      }

      return controllerDevices;
   }

   private static void addDevicesToList(List<UsbDevice> deviceList, List<UsbDevice> list) {
      Iterator var2 = list.iterator();

      while(var2.hasNext()) {
         UsbDevice device = (UsbDevice)var2.next();
         deviceList.add(new MacUsbDevice(device.getName(), device.getVendor(), device.getVendorId(), device.getProductId(), device.getSerialNumber(), device.getUniqueDeviceId(), Collections.emptyList()));
         addDevicesToList(deviceList, device.getConnectedDevices());
      }

   }

   private static void getControllerIdByLocation(long id, CoreFoundation.CFTypeRef locationId, CoreFoundation.CFStringRef locationIDKey, CoreFoundation.CFStringRef ioPropertyMatchKey, Map<Long, String> vendorIdMap, Map<Long, String> productIdMap) {
      CoreFoundation.CFMutableDictionaryRef propertyDict = CF.CFDictionaryCreateMutable(CF.CFAllocatorGetDefault(), new CoreFoundation.CFIndex(0L), (Pointer)null, (Pointer)null);
      propertyDict.setValue(locationIDKey, locationId);
      CoreFoundation.CFMutableDictionaryRef matchingDict = CF.CFDictionaryCreateMutable(CF.CFAllocatorGetDefault(), new CoreFoundation.CFIndex(0L), (Pointer)null, (Pointer)null);
      matchingDict.setValue(ioPropertyMatchKey, propertyDict);
      IOKit.IOIterator serviceIterator = IOKitUtil.getMatchingServices((CoreFoundation.CFDictionaryRef)matchingDict);
      propertyDict.release();
      boolean found = false;
      if (serviceIterator != null) {
         for(IOKit.IORegistryEntry matchingService = serviceIterator.next(); matchingService != null && !found; matchingService = serviceIterator.next()) {
            IOKit.IORegistryEntry parent = matchingService.getParentEntry("IOService");
            if (parent != null) {
               byte[] vid = parent.getByteArrayProperty("vendor-id");
               if (vid != null && vid.length >= 2) {
                  vendorIdMap.put(id, String.format("%02x%02x", vid[1], vid[0]));
                  found = true;
               }

               byte[] pid = parent.getByteArrayProperty("device-id");
               if (pid != null && pid.length >= 2) {
                  productIdMap.put(id, String.format("%02x%02x", pid[1], pid[0]));
                  found = true;
               }

               parent.release();
            }

            matchingService.release();
         }

         serviceIterator.release();
      }

   }

   private static MacUsbDevice getDeviceAndChildren(Long registryEntryId, String vid, String pid, Map<Long, String> nameMap, Map<Long, String> vendorMap, Map<Long, String> vendorIdMap, Map<Long, String> productIdMap, Map<Long, String> serialMap, Map<Long, List<Long>> hubMap) {
      String vendorId = (String)vendorIdMap.getOrDefault(registryEntryId, vid);
      String productId = (String)productIdMap.getOrDefault(registryEntryId, pid);
      List<Long> childIds = (List)hubMap.getOrDefault(registryEntryId, new ArrayList());
      List<UsbDevice> usbDevices = new ArrayList();
      Iterator var13 = childIds.iterator();

      while(var13.hasNext()) {
         Long id = (Long)var13.next();
         usbDevices.add(getDeviceAndChildren(id, vendorId, productId, nameMap, vendorMap, vendorIdMap, productIdMap, serialMap, hubMap));
      }

      Collections.sort(usbDevices);
      return new MacUsbDevice((String)nameMap.getOrDefault(registryEntryId, vendorId + ":" + productId), (String)vendorMap.getOrDefault(registryEntryId, ""), vendorId, productId, (String)serialMap.getOrDefault(registryEntryId, ""), "0x" + Long.toHexString(registryEntryId), usbDevices);
   }

   static {
      CF = CoreFoundation.INSTANCE;
   }
}
