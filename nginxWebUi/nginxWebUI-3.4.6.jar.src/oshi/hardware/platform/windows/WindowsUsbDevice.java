/*     */ package oshi.hardware.platform.windows;
/*     */ 
/*     */ import com.sun.jna.platform.win32.COM.WbemcliUtil;
/*     */ import com.sun.jna.platform.win32.Cfgmgr32;
/*     */ import com.sun.jna.platform.win32.Cfgmgr32Util;
/*     */ import com.sun.jna.ptr.IntByReference;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ import oshi.annotation.concurrent.Immutable;
/*     */ import oshi.driver.windows.wmi.Win32DiskDrive;
/*     */ import oshi.driver.windows.wmi.Win32PnPEntity;
/*     */ import oshi.driver.windows.wmi.Win32USBController;
/*     */ import oshi.hardware.UsbDevice;
/*     */ import oshi.hardware.common.AbstractUsbDevice;
/*     */ import oshi.util.ParseUtil;
/*     */ import oshi.util.platform.windows.WmiUtil;
/*     */ import oshi.util.tuples.Pair;
/*     */ import oshi.util.tuples.Triplet;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Immutable
/*     */ public class WindowsUsbDevice
/*     */   extends AbstractUsbDevice
/*     */ {
/*  62 */   private static final Logger LOG = LoggerFactory.getLogger(WindowsUsbDevice.class);
/*     */ 
/*     */   
/*     */   public WindowsUsbDevice(String name, String vendor, String vendorId, String productId, String serialNumber, String uniqueDeviceId, List<UsbDevice> connectedDevices) {
/*  66 */     super(name, vendor, vendorId, productId, serialNumber, uniqueDeviceId, connectedDevices);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List<UsbDevice> getUsbDevices(boolean tree) {
/*  77 */     List<UsbDevice> devices = getUsbDevices();
/*  78 */     if (tree) {
/*  79 */       return Collections.unmodifiableList(devices);
/*     */     }
/*  81 */     List<UsbDevice> deviceList = new ArrayList<>();
/*     */ 
/*     */     
/*  84 */     for (UsbDevice device : devices) {
/*  85 */       addDevicesToList(deviceList, device.getConnectedDevices());
/*     */     }
/*  87 */     return Collections.unmodifiableList(deviceList);
/*     */   }
/*     */ 
/*     */   
/*     */   private static List<UsbDevice> getUsbDevices() {
/*  92 */     Map<String, List<String>> deviceTreeMap = new HashMap<>();
/*     */     
/*  94 */     Set<String> devicesSeen = new HashSet<>();
/*     */ 
/*     */     
/*  97 */     List<UsbDevice> controllerDevices = new ArrayList<>();
/*  98 */     List<String> controllerDeviceIdList = getControllerDeviceIdList();
/*  99 */     for (String controllerDeviceId : controllerDeviceIdList) {
/* 100 */       putChildrenInDeviceTree(controllerDeviceId, 0, deviceTreeMap, devicesSeen);
/*     */     }
/*     */     
/* 103 */     Map<String, Triplet<String, String, String>> deviceStringMap = queryDeviceStringsMap(devicesSeen);
/*     */     
/* 105 */     for (String controllerDeviceId : controllerDeviceIdList) {
/* 106 */       WindowsUsbDevice deviceAndChildren = getDeviceAndChildren(controllerDeviceId, "0000", "0000", deviceTreeMap, deviceStringMap);
/*     */       
/* 108 */       if (deviceAndChildren != null) {
/* 109 */         controllerDevices.add(deviceAndChildren);
/*     */       }
/*     */     } 
/* 112 */     return controllerDevices;
/*     */   }
/*     */   
/*     */   private static void addDevicesToList(List<UsbDevice> deviceList, List<UsbDevice> list) {
/* 116 */     for (UsbDevice device : list) {
/* 117 */       deviceList.add(new WindowsUsbDevice(device.getName(), device.getVendor(), device.getVendorId(), device
/* 118 */             .getProductId(), device.getSerialNumber(), device.getUniqueDeviceId(), 
/* 119 */             Collections.emptyList()));
/* 120 */       addDevicesToList(deviceList, device.getConnectedDevices());
/*     */     } 
/*     */   }
/*     */   
/*     */   private static Map<String, Triplet<String, String, String>> queryDeviceStringsMap(Set<String> devicesToAdd) {
/* 125 */     Map<String, Triplet<String, String, String>> deviceStringCache = new HashMap<>();
/*     */     
/* 127 */     if (!devicesToAdd.isEmpty()) {
/* 128 */       StringBuilder sb = new StringBuilder();
/* 129 */       boolean first = true;
/* 130 */       for (String deviceID : devicesToAdd) {
/* 131 */         if (first) {
/* 132 */           sb.append(" WHERE (PnPDeviceID=\"");
/* 133 */           first = false;
/*     */         } else {
/* 135 */           sb.append(" OR (PnPDeviceID=\"");
/*     */         } 
/* 137 */         sb.append(deviceID).append("\")");
/*     */       } 
/* 139 */       String whereClause = sb.toString();
/*     */       
/* 141 */       Map<String, String> pnpToSerialMap = new HashMap<>();
/* 142 */       WbemcliUtil.WmiResult<Win32DiskDrive.DeviceIdProperty> serialNumbers = Win32DiskDrive.queryDiskDriveId(whereClause);
/* 143 */       for (int i = 0; i < serialNumbers.getResultCount(); i++) {
/* 144 */         String pnpDeviceID = WmiUtil.getString(serialNumbers, (Enum)Win32DiskDrive.DeviceIdProperty.PNPDEVICEID, i);
/* 145 */         if (deviceStringCache.containsKey(pnpDeviceID)) {
/* 146 */           pnpToSerialMap.put(pnpDeviceID, 
/* 147 */               ParseUtil.hexStringToString(WmiUtil.getString(serialNumbers, (Enum)Win32DiskDrive.DeviceIdProperty.SERIALNUMBER, i)));
/*     */         }
/*     */       } 
/*     */       
/* 151 */       WbemcliUtil.WmiResult<Win32PnPEntity.PnPEntityProperty> pnpEntity = Win32PnPEntity.queryDeviceId(whereClause);
/* 152 */       for (int j = 0; j < pnpEntity.getResultCount(); j++) {
/* 153 */         String pnpDeviceID = WmiUtil.getString(pnpEntity, (Enum)Win32PnPEntity.PnPEntityProperty.PNPDEVICEID, j);
/* 154 */         String name = WmiUtil.getString(pnpEntity, (Enum)Win32PnPEntity.PnPEntityProperty.NAME, j);
/* 155 */         String vendor = WmiUtil.getString(pnpEntity, (Enum)Win32PnPEntity.PnPEntityProperty.MANUFACTURER, j);
/* 156 */         deviceStringCache.put(pnpDeviceID, new Triplet(name, vendor, pnpToSerialMap
/* 157 */               .getOrDefault(pnpDeviceID, "")));
/* 158 */         LOG.debug("Adding {} to USB device cache.", pnpDeviceID);
/*     */       } 
/*     */     } 
/* 161 */     return deviceStringCache;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void putChildrenInDeviceTree(String deviceId, int deviceInstance, Map<String, List<String>> deviceTreeMap, Set<String> devicesSeen) {
/* 180 */     devicesSeen.add(deviceId);
/*     */     
/* 182 */     int devInst = deviceInstance;
/* 183 */     if (devInst == 0) {
/* 184 */       IntByReference pdnDevInst = new IntByReference();
/* 185 */       Cfgmgr32.INSTANCE.CM_Locate_DevNode(pdnDevInst, deviceId, 0);
/* 186 */       devInst = pdnDevInst.getValue();
/*     */     } 
/*     */     
/* 189 */     IntByReference child = new IntByReference();
/* 190 */     if (0 == Cfgmgr32.INSTANCE.CM_Get_Child(child, devInst, 0)) {
/*     */       
/* 192 */       List<String> childList = new ArrayList<>();
/* 193 */       String childId = Cfgmgr32Util.CM_Get_Device_ID(child.getValue());
/* 194 */       childList.add(childId);
/* 195 */       deviceTreeMap.put(deviceId, childList);
/* 196 */       putChildrenInDeviceTree(childId, child.getValue(), deviceTreeMap, devicesSeen);
/*     */       
/* 198 */       IntByReference sibling = new IntByReference();
/* 199 */       while (0 == Cfgmgr32.INSTANCE.CM_Get_Sibling(sibling, child.getValue(), 0)) {
/*     */         
/* 201 */         String siblingId = Cfgmgr32Util.CM_Get_Device_ID(sibling.getValue());
/* 202 */         ((List<String>)deviceTreeMap.get(deviceId)).add(siblingId);
/* 203 */         putChildrenInDeviceTree(siblingId, sibling.getValue(), deviceTreeMap, devicesSeen);
/*     */         
/* 205 */         child = sibling;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static WindowsUsbDevice getDeviceAndChildren(String hubDeviceId, String vid, String pid, Map<String, List<String>> deviceTreeMap, Map<String, Triplet<String, String, String>> deviceStringMap) {
/* 212 */     String vendorId = vid;
/* 213 */     String productId = pid;
/* 214 */     Pair<String, String> idPair = ParseUtil.parsePnPDeviceIdToVendorProductId(hubDeviceId);
/* 215 */     if (idPair != null) {
/* 216 */       vendorId = (String)idPair.getA();
/* 217 */       productId = (String)idPair.getB();
/*     */     } 
/* 219 */     List<String> pnpDeviceIds = deviceTreeMap.getOrDefault(hubDeviceId, new ArrayList<>());
/* 220 */     List<UsbDevice> usbDevices = new ArrayList<>();
/* 221 */     for (String pnpDeviceId : pnpDeviceIds) {
/* 222 */       WindowsUsbDevice deviceAndChildren = getDeviceAndChildren(pnpDeviceId, vendorId, productId, deviceTreeMap, deviceStringMap);
/*     */       
/* 224 */       if (deviceAndChildren != null) {
/* 225 */         usbDevices.add(deviceAndChildren);
/*     */       }
/*     */     } 
/* 228 */     Collections.sort(usbDevices);
/* 229 */     if (deviceStringMap.containsKey(hubDeviceId)) {
/*     */       
/* 231 */       Triplet<String, String, String> device = deviceStringMap.get(hubDeviceId);
/* 232 */       String name = (String)device.getA();
/* 233 */       if (name.isEmpty()) {
/* 234 */         name = vendorId + ":" + productId;
/*     */       }
/* 236 */       return new WindowsUsbDevice(name, (String)device.getB(), vendorId, productId, (String)device.getC(), hubDeviceId, usbDevices);
/*     */     } 
/*     */     
/* 239 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static List<String> getControllerDeviceIdList() {
/* 248 */     List<String> controllerDeviceIdsList = new ArrayList<>();
/*     */ 
/*     */     
/* 251 */     WbemcliUtil.WmiResult<Win32USBController.USBControllerProperty> usbController = Win32USBController.queryUSBControllers();
/* 252 */     for (int i = 0; i < usbController.getResultCount(); i++) {
/* 253 */       controllerDeviceIdsList.add(WmiUtil.getString(usbController, (Enum)Win32USBController.USBControllerProperty.PNPDEVICEID, i));
/*     */     }
/* 255 */     return controllerDeviceIdsList;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platform\windows\WindowsUsbDevice.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */