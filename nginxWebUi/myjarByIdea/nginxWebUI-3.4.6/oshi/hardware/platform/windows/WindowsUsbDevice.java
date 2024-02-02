package oshi.hardware.platform.windows;

import com.sun.jna.platform.win32.Cfgmgr32;
import com.sun.jna.platform.win32.Cfgmgr32Util;
import com.sun.jna.platform.win32.COM.WbemcliUtil;
import com.sun.jna.ptr.IntByReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.Immutable;
import oshi.driver.windows.wmi.Win32DiskDrive;
import oshi.driver.windows.wmi.Win32PnPEntity;
import oshi.driver.windows.wmi.Win32USBController;
import oshi.hardware.UsbDevice;
import oshi.hardware.common.AbstractUsbDevice;
import oshi.util.ParseUtil;
import oshi.util.platform.windows.WmiUtil;
import oshi.util.tuples.Pair;
import oshi.util.tuples.Triplet;

@Immutable
public class WindowsUsbDevice extends AbstractUsbDevice {
   private static final Logger LOG = LoggerFactory.getLogger(WindowsUsbDevice.class);

   public WindowsUsbDevice(String name, String vendor, String vendorId, String productId, String serialNumber, String uniqueDeviceId, List<UsbDevice> connectedDevices) {
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
      Map<String, List<String>> deviceTreeMap = new HashMap();
      Set<String> devicesSeen = new HashSet();
      List<UsbDevice> controllerDevices = new ArrayList();
      List<String> controllerDeviceIdList = getControllerDeviceIdList();
      Iterator var4 = controllerDeviceIdList.iterator();

      while(var4.hasNext()) {
         String controllerDeviceId = (String)var4.next();
         putChildrenInDeviceTree(controllerDeviceId, 0, deviceTreeMap, devicesSeen);
      }

      Map<String, Triplet<String, String, String>> deviceStringMap = queryDeviceStringsMap(devicesSeen);
      Iterator var9 = controllerDeviceIdList.iterator();

      while(var9.hasNext()) {
         String controllerDeviceId = (String)var9.next();
         WindowsUsbDevice deviceAndChildren = getDeviceAndChildren(controllerDeviceId, "0000", "0000", deviceTreeMap, deviceStringMap);
         if (deviceAndChildren != null) {
            controllerDevices.add(deviceAndChildren);
         }
      }

      return controllerDevices;
   }

   private static void addDevicesToList(List<UsbDevice> deviceList, List<UsbDevice> list) {
      Iterator var2 = list.iterator();

      while(var2.hasNext()) {
         UsbDevice device = (UsbDevice)var2.next();
         deviceList.add(new WindowsUsbDevice(device.getName(), device.getVendor(), device.getVendorId(), device.getProductId(), device.getSerialNumber(), device.getUniqueDeviceId(), Collections.emptyList()));
         addDevicesToList(deviceList, device.getConnectedDevices());
      }

   }

   private static Map<String, Triplet<String, String, String>> queryDeviceStringsMap(Set<String> devicesToAdd) {
      Map<String, Triplet<String, String, String>> deviceStringCache = new HashMap();
      if (!devicesToAdd.isEmpty()) {
         StringBuilder sb = new StringBuilder();
         boolean first = true;

         String deviceID;
         for(Iterator var4 = devicesToAdd.iterator(); var4.hasNext(); sb.append(deviceID).append("\")")) {
            deviceID = (String)var4.next();
            if (first) {
               sb.append(" WHERE (PnPDeviceID=\"");
               first = false;
            } else {
               sb.append(" OR (PnPDeviceID=\"");
            }
         }

         String whereClause = sb.toString();
         Map<String, String> pnpToSerialMap = new HashMap();
         WbemcliUtil.WmiResult<Win32DiskDrive.DeviceIdProperty> serialNumbers = Win32DiskDrive.queryDiskDriveId(whereClause);

         for(int i = 0; i < serialNumbers.getResultCount(); ++i) {
            String pnpDeviceID = WmiUtil.getString(serialNumbers, Win32DiskDrive.DeviceIdProperty.PNPDEVICEID, i);
            if (deviceStringCache.containsKey(pnpDeviceID)) {
               pnpToSerialMap.put(pnpDeviceID, ParseUtil.hexStringToString(WmiUtil.getString(serialNumbers, Win32DiskDrive.DeviceIdProperty.SERIALNUMBER, i)));
            }
         }

         WbemcliUtil.WmiResult<Win32PnPEntity.PnPEntityProperty> pnpEntity = Win32PnPEntity.queryDeviceId(whereClause);

         for(int i = 0; i < pnpEntity.getResultCount(); ++i) {
            String pnpDeviceID = WmiUtil.getString(pnpEntity, Win32PnPEntity.PnPEntityProperty.PNPDEVICEID, i);
            String name = WmiUtil.getString(pnpEntity, Win32PnPEntity.PnPEntityProperty.NAME, i);
            String vendor = WmiUtil.getString(pnpEntity, Win32PnPEntity.PnPEntityProperty.MANUFACTURER, i);
            deviceStringCache.put(pnpDeviceID, new Triplet(name, vendor, (String)pnpToSerialMap.getOrDefault(pnpDeviceID, "")));
            LOG.debug((String)"Adding {} to USB device cache.", (Object)pnpDeviceID);
         }
      }

      return deviceStringCache;
   }

   private static void putChildrenInDeviceTree(String deviceId, int deviceInstance, Map<String, List<String>> deviceTreeMap, Set<String> devicesSeen) {
      devicesSeen.add(deviceId);
      int devInst = deviceInstance;
      IntByReference child;
      if (deviceInstance == 0) {
         child = new IntByReference();
         Cfgmgr32.INSTANCE.CM_Locate_DevNode(child, deviceId, 0);
         devInst = child.getValue();
      }

      child = new IntByReference();
      if (0 == Cfgmgr32.INSTANCE.CM_Get_Child(child, devInst, 0)) {
         List<String> childList = new ArrayList();
         String childId = Cfgmgr32Util.CM_Get_Device_ID(child.getValue());
         childList.add(childId);
         deviceTreeMap.put(deviceId, childList);
         putChildrenInDeviceTree(childId, child.getValue(), deviceTreeMap, devicesSeen);

         for(IntByReference sibling = new IntByReference(); 0 == Cfgmgr32.INSTANCE.CM_Get_Sibling(sibling, child.getValue(), 0); child = sibling) {
            String siblingId = Cfgmgr32Util.CM_Get_Device_ID(sibling.getValue());
            ((List)deviceTreeMap.get(deviceId)).add(siblingId);
            putChildrenInDeviceTree(siblingId, sibling.getValue(), deviceTreeMap, devicesSeen);
         }
      }

   }

   private static WindowsUsbDevice getDeviceAndChildren(String hubDeviceId, String vid, String pid, Map<String, List<String>> deviceTreeMap, Map<String, Triplet<String, String, String>> deviceStringMap) {
      String vendorId = vid;
      String productId = pid;
      Pair<String, String> idPair = ParseUtil.parsePnPDeviceIdToVendorProductId(hubDeviceId);
      if (idPair != null) {
         vendorId = (String)idPair.getA();
         productId = (String)idPair.getB();
      }

      List<String> pnpDeviceIds = (List)deviceTreeMap.getOrDefault(hubDeviceId, new ArrayList());
      List<UsbDevice> usbDevices = new ArrayList();
      Iterator var10 = pnpDeviceIds.iterator();

      String name;
      while(var10.hasNext()) {
         name = (String)var10.next();
         WindowsUsbDevice deviceAndChildren = getDeviceAndChildren(name, vendorId, productId, deviceTreeMap, deviceStringMap);
         if (deviceAndChildren != null) {
            usbDevices.add(deviceAndChildren);
         }
      }

      Collections.sort(usbDevices);
      if (deviceStringMap.containsKey(hubDeviceId)) {
         Triplet<String, String, String> device = (Triplet)deviceStringMap.get(hubDeviceId);
         name = (String)device.getA();
         if (name.isEmpty()) {
            name = vendorId + ":" + productId;
         }

         return new WindowsUsbDevice(name, (String)device.getB(), vendorId, productId, (String)device.getC(), hubDeviceId, usbDevices);
      } else {
         return null;
      }
   }

   private static List<String> getControllerDeviceIdList() {
      List<String> controllerDeviceIdsList = new ArrayList();
      WbemcliUtil.WmiResult<Win32USBController.USBControllerProperty> usbController = Win32USBController.queryUSBControllers();

      for(int i = 0; i < usbController.getResultCount(); ++i) {
         controllerDeviceIdsList.add(WmiUtil.getString(usbController, Win32USBController.USBControllerProperty.PNPDEVICEID, i));
      }

      return controllerDeviceIdsList;
   }
}
