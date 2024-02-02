/*     */ package oshi.hardware.platform.mac;
/*     */ 
/*     */ import com.sun.jna.PointerType;
/*     */ import com.sun.jna.platform.mac.CoreFoundation;
/*     */ import com.sun.jna.platform.mac.IOKit;
/*     */ import com.sun.jna.platform.mac.IOKitUtil;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import oshi.annotation.concurrent.Immutable;
/*     */ import oshi.hardware.UsbDevice;
/*     */ import oshi.hardware.common.AbstractUsbDevice;
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
/*     */ public class MacUsbDevice
/*     */   extends AbstractUsbDevice
/*     */ {
/*  51 */   private static final CoreFoundation CF = CoreFoundation.INSTANCE;
/*     */ 
/*     */   
/*     */   public MacUsbDevice(String name, String vendor, String vendorId, String productId, String serialNumber, String uniqueDeviceId, List<UsbDevice> connectedDevices) {
/*  55 */     super(name, vendor, vendorId, productId, serialNumber, uniqueDeviceId, connectedDevices);
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
/*  66 */     List<UsbDevice> devices = getUsbDevices();
/*  67 */     if (tree) {
/*  68 */       return Collections.unmodifiableList(devices);
/*     */     }
/*  70 */     List<UsbDevice> deviceList = new ArrayList<>();
/*     */ 
/*     */     
/*  73 */     for (UsbDevice device : devices) {
/*  74 */       addDevicesToList(deviceList, device.getConnectedDevices());
/*     */     }
/*  76 */     return Collections.unmodifiableList(deviceList);
/*     */   }
/*     */ 
/*     */   
/*     */   private static List<UsbDevice> getUsbDevices() {
/*  81 */     Map<Long, String> nameMap = new HashMap<>();
/*  82 */     Map<Long, String> vendorMap = new HashMap<>();
/*  83 */     Map<Long, String> vendorIdMap = new HashMap<>();
/*  84 */     Map<Long, String> productIdMap = new HashMap<>();
/*  85 */     Map<Long, String> serialMap = new HashMap<>();
/*  86 */     Map<Long, List<Long>> hubMap = new HashMap<>();
/*     */ 
/*     */ 
/*     */     
/*  90 */     List<Long> usbControllers = new ArrayList<>();
/*  91 */     IOKit.IOIterator iter = IOKitUtil.getMatchingServices("IOUSBController");
/*  92 */     if (iter != null) {
/*     */       
/*  94 */       CoreFoundation.CFStringRef locationIDKey = CoreFoundation.CFStringRef.createCFString("locationID");
/*  95 */       CoreFoundation.CFStringRef ioPropertyMatchKey = CoreFoundation.CFStringRef.createCFString("IOPropertyMatch");
/*     */       
/*  97 */       IOKit.IORegistryEntry device = iter.next();
/*  98 */       while (device != null) {
/*     */         
/* 100 */         long id = device.getRegistryEntryID();
/* 101 */         usbControllers.add(Long.valueOf(id));
/*     */ 
/*     */         
/* 104 */         nameMap.put(Long.valueOf(id), device.getName());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 110 */         CoreFoundation.CFTypeRef ref = device.createCFProperty(locationIDKey);
/* 111 */         if (ref != null) {
/* 112 */           getControllerIdByLocation(id, ref, locationIDKey, ioPropertyMatchKey, vendorIdMap, productIdMap);
/* 113 */           ref.release();
/*     */         } 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 119 */         IOKit.IOIterator childIter = device.getChildIterator("IOService");
/* 120 */         IOKit.IORegistryEntry childDevice = childIter.next();
/* 121 */         while (childDevice != null) {
/*     */           
/* 123 */           long parentId, childId = childDevice.getRegistryEntryID();
/*     */ 
/*     */           
/* 126 */           IOKit.IORegistryEntry parent = childDevice.getParentEntry("IOUSB");
/*     */ 
/*     */ 
/*     */           
/* 130 */           if (parent == null || !parent.conformsTo("IOUSBDevice")) {
/* 131 */             parentId = id;
/*     */           } else {
/*     */             
/* 134 */             parentId = parent.getRegistryEntryID();
/*     */           } 
/* 136 */           if (parent != null) {
/* 137 */             parent.release();
/*     */           }
/*     */           
/* 140 */           ((List<Long>)hubMap.computeIfAbsent(Long.valueOf(parentId), x -> new ArrayList())).add(Long.valueOf(childId));
/*     */           
/* 142 */           nameMap.put(Long.valueOf(childId), childDevice.getName().trim());
/*     */           
/* 144 */           String vendor = childDevice.getStringProperty("USB Vendor Name");
/* 145 */           if (vendor != null) {
/* 146 */             vendorMap.put(Long.valueOf(childId), vendor.trim());
/*     */           }
/*     */           
/* 149 */           Long vendorId = childDevice.getLongProperty("idVendor");
/* 150 */           if (vendorId != null) {
/* 151 */             vendorIdMap.put(Long.valueOf(childId), String.format("%04x", new Object[] { Long.valueOf(0xFFFFL & vendorId.longValue()) }));
/*     */           }
/*     */           
/* 154 */           Long productId = childDevice.getLongProperty("idProduct");
/* 155 */           if (productId != null) {
/* 156 */             productIdMap.put(Long.valueOf(childId), String.format("%04x", new Object[] { Long.valueOf(0xFFFFL & productId.longValue()) }));
/*     */           }
/*     */           
/* 159 */           String serial = childDevice.getStringProperty("USB Serial Number");
/* 160 */           if (serial != null) {
/* 161 */             serialMap.put(Long.valueOf(childId), serial.trim());
/*     */           }
/* 163 */           childDevice.release();
/* 164 */           childDevice = childIter.next();
/*     */         } 
/* 166 */         childIter.release();
/* 167 */         device.release();
/* 168 */         device = iter.next();
/*     */       } 
/* 170 */       iter.release();
/* 171 */       locationIDKey.release();
/* 172 */       ioPropertyMatchKey.release();
/*     */     } 
/*     */ 
/*     */     
/* 176 */     List<UsbDevice> controllerDevices = new ArrayList<>();
/* 177 */     for (Long controller : usbControllers) {
/* 178 */       controllerDevices.add(getDeviceAndChildren(controller, "0000", "0000", nameMap, vendorMap, vendorIdMap, productIdMap, serialMap, hubMap));
/*     */     }
/*     */     
/* 181 */     return controllerDevices;
/*     */   }
/*     */   
/*     */   private static void addDevicesToList(List<UsbDevice> deviceList, List<UsbDevice> list) {
/* 185 */     for (UsbDevice device : list) {
/* 186 */       deviceList.add(new MacUsbDevice(device
/* 187 */             .getName(), device.getVendor(), device.getVendorId(), device.getProductId(), device
/* 188 */             .getSerialNumber(), device.getUniqueDeviceId(), Collections.emptyList()));
/* 189 */       addDevicesToList(deviceList, device.getConnectedDevices());
/*     */     } 
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void getControllerIdByLocation(long id, CoreFoundation.CFTypeRef locationId, CoreFoundation.CFStringRef locationIDKey, CoreFoundation.CFStringRef ioPropertyMatchKey, Map<Long, String> vendorIdMap, Map<Long, String> productIdMap) {
/* 214 */     CoreFoundation.CFMutableDictionaryRef propertyDict = CF.CFDictionaryCreateMutable(CF.CFAllocatorGetDefault(), new CoreFoundation.CFIndex(0L), null, null);
/*     */     
/* 216 */     propertyDict.setValue((PointerType)locationIDKey, (PointerType)locationId);
/* 217 */     CoreFoundation.CFMutableDictionaryRef matchingDict = CF.CFDictionaryCreateMutable(CF.CFAllocatorGetDefault(), new CoreFoundation.CFIndex(0L), null, null);
/*     */     
/* 219 */     matchingDict.setValue((PointerType)ioPropertyMatchKey, (PointerType)propertyDict);
/*     */ 
/*     */     
/* 222 */     IOKit.IOIterator serviceIterator = IOKitUtil.getMatchingServices((CoreFoundation.CFDictionaryRef)matchingDict);
/*     */     
/* 224 */     propertyDict.release();
/*     */ 
/*     */ 
/*     */     
/* 228 */     boolean found = false;
/* 229 */     if (serviceIterator != null) {
/* 230 */       IOKit.IORegistryEntry matchingService = serviceIterator.next();
/* 231 */       while (matchingService != null && !found) {
/*     */         
/* 233 */         IOKit.IORegistryEntry parent = matchingService.getParentEntry("IOService");
/*     */ 
/*     */         
/* 236 */         if (parent != null) {
/* 237 */           byte[] vid = parent.getByteArrayProperty("vendor-id");
/* 238 */           if (vid != null && vid.length >= 2) {
/* 239 */             vendorIdMap.put(Long.valueOf(id), String.format("%02x%02x", new Object[] { Byte.valueOf(vid[1]), Byte.valueOf(vid[0]) }));
/* 240 */             found = true;
/*     */           } 
/*     */ 
/*     */           
/* 244 */           byte[] pid = parent.getByteArrayProperty("device-id");
/* 245 */           if (pid != null && pid.length >= 2) {
/* 246 */             productIdMap.put(Long.valueOf(id), String.format("%02x%02x", new Object[] { Byte.valueOf(pid[1]), Byte.valueOf(pid[0]) }));
/* 247 */             found = true;
/*     */           } 
/* 249 */           parent.release();
/*     */         } 
/*     */         
/* 252 */         matchingService.release();
/* 253 */         matchingService = serviceIterator.next();
/*     */       } 
/* 255 */       serviceIterator.release();
/*     */     } 
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
/*     */   private static MacUsbDevice getDeviceAndChildren(Long registryEntryId, String vid, String pid, Map<Long, String> nameMap, Map<Long, String> vendorMap, Map<Long, String> vendorIdMap, Map<Long, String> productIdMap, Map<Long, String> serialMap, Map<Long, List<Long>> hubMap) {
/* 286 */     String vendorId = vendorIdMap.getOrDefault(registryEntryId, vid);
/* 287 */     String productId = productIdMap.getOrDefault(registryEntryId, pid);
/* 288 */     List<Long> childIds = hubMap.getOrDefault(registryEntryId, new ArrayList<>());
/* 289 */     List<UsbDevice> usbDevices = new ArrayList<>();
/* 290 */     for (Long id : childIds) {
/* 291 */       usbDevices.add(getDeviceAndChildren(id, vendorId, productId, nameMap, vendorMap, vendorIdMap, productIdMap, serialMap, hubMap));
/*     */     }
/*     */     
/* 294 */     Collections.sort(usbDevices);
/* 295 */     return new MacUsbDevice(nameMap.getOrDefault(registryEntryId, vendorId + ":" + productId), vendorMap
/* 296 */         .getOrDefault(registryEntryId, ""), vendorId, productId, serialMap
/* 297 */         .getOrDefault(registryEntryId, ""), "0x" + Long.toHexString(registryEntryId.longValue()), usbDevices);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platform\mac\MacUsbDevice.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */