/*     */ package oshi.hardware.platform.unix.solaris;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import oshi.annotation.concurrent.Immutable;
/*     */ import oshi.hardware.UsbDevice;
/*     */ import oshi.hardware.common.AbstractUsbDevice;
/*     */ import oshi.util.ExecutingCommand;
/*     */ import oshi.util.ParseUtil;
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
/*     */ public class SolarisUsbDevice
/*     */   extends AbstractUsbDevice
/*     */ {
/*     */   private static final String PCI_TYPE_USB = "000c";
/*     */   
/*     */   public SolarisUsbDevice(String name, String vendor, String vendorId, String productId, String serialNumber, String uniqueDeviceId, List<UsbDevice> connectedDevices) {
/*  48 */     super(name, vendor, vendorId, productId, serialNumber, uniqueDeviceId, connectedDevices);
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
/*  59 */     List<UsbDevice> devices = getUsbDevices();
/*  60 */     if (tree) {
/*  61 */       return Collections.unmodifiableList(devices);
/*     */     }
/*  63 */     List<UsbDevice> deviceList = new ArrayList<>();
/*     */ 
/*     */     
/*  66 */     for (UsbDevice device : devices) {
/*  67 */       deviceList.add(new SolarisUsbDevice(device.getName(), device.getVendor(), device.getVendorId(), device
/*  68 */             .getProductId(), device.getSerialNumber(), device.getUniqueDeviceId(), 
/*  69 */             Collections.emptyList()));
/*  70 */       addDevicesToList(deviceList, device.getConnectedDevices());
/*     */     } 
/*  72 */     return Collections.unmodifiableList(deviceList);
/*     */   }
/*     */   
/*     */   private static List<UsbDevice> getUsbDevices() {
/*  76 */     Map<String, String> nameMap = new HashMap<>();
/*  77 */     Map<String, String> vendorIdMap = new HashMap<>();
/*  78 */     Map<String, String> productIdMap = new HashMap<>();
/*  79 */     Map<String, List<String>> hubMap = new HashMap<>();
/*  80 */     Map<String, String> deviceTypeMap = new HashMap<>();
/*     */ 
/*     */     
/*  83 */     List<String> devices = ExecutingCommand.runNative("prtconf -pv");
/*  84 */     if (devices.isEmpty()) {
/*  85 */       return Collections.emptyList();
/*     */     }
/*     */     
/*  88 */     Map<Integer, String> lastParent = new HashMap<>();
/*  89 */     String key = "";
/*  90 */     int indent = 0;
/*  91 */     List<String> usbControllers = new ArrayList<>();
/*  92 */     for (String line : devices) {
/*     */       
/*  94 */       if (line.contains("Node 0x")) {
/*     */         
/*  96 */         key = line.replaceFirst("^\\s*", "");
/*     */         
/*  98 */         int depth = line.length() - key.length();
/*     */         
/* 100 */         if (indent == 0) {
/* 101 */           indent = depth;
/*     */         }
/*     */         
/* 104 */         lastParent.put(Integer.valueOf(depth), key);
/*     */         
/* 106 */         if (depth > indent) {
/*     */           
/* 108 */           ((List<String>)hubMap.computeIfAbsent(lastParent.get(Integer.valueOf(depth - indent)), x -> new ArrayList())).add(key);
/*     */           continue;
/*     */         } 
/* 111 */         usbControllers.add(key); continue;
/*     */       } 
/* 113 */       if (!key.isEmpty()) {
/*     */ 
/*     */         
/* 116 */         line = line.trim();
/* 117 */         if (line.startsWith("model:")) {
/* 118 */           nameMap.put(key, ParseUtil.getSingleQuoteStringValue(line)); continue;
/* 119 */         }  if (line.startsWith("name:")) {
/*     */ 
/*     */           
/* 122 */           nameMap.putIfAbsent(key, ParseUtil.getSingleQuoteStringValue(line)); continue;
/* 123 */         }  if (line.startsWith("vendor-id:")) {
/*     */           
/* 125 */           vendorIdMap.put(key, line.substring(line.length() - 4)); continue;
/* 126 */         }  if (line.startsWith("device-id:")) {
/*     */           
/* 128 */           productIdMap.put(key, line.substring(line.length() - 4)); continue;
/* 129 */         }  if (line.startsWith("class-code:")) {
/*     */           
/* 131 */           deviceTypeMap.putIfAbsent(key, line.substring(line.length() - 8, line.length() - 4)); continue;
/* 132 */         }  if (line.startsWith("device_type:"))
/*     */         {
/* 134 */           deviceTypeMap.putIfAbsent(key, ParseUtil.getSingleQuoteStringValue(line));
/*     */         }
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 140 */     List<UsbDevice> controllerDevices = new ArrayList<>();
/* 141 */     for (String controller : usbControllers) {
/*     */       
/* 143 */       if ("000c".equals(deviceTypeMap.getOrDefault(controller, "")) || "usb"
/* 144 */         .equals(deviceTypeMap.getOrDefault(controller, ""))) {
/* 145 */         controllerDevices.add(
/* 146 */             getDeviceAndChildren(controller, "0000", "0000", nameMap, vendorIdMap, productIdMap, hubMap));
/*     */       }
/*     */     } 
/* 149 */     return controllerDevices;
/*     */   }
/*     */   
/*     */   private static void addDevicesToList(List<UsbDevice> deviceList, List<UsbDevice> list) {
/* 153 */     for (UsbDevice device : list) {
/* 154 */       deviceList.add(device);
/* 155 */       addDevicesToList(deviceList, device.getConnectedDevices());
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
/*     */   private static SolarisUsbDevice getDeviceAndChildren(String devPath, String vid, String pid, Map<String, String> nameMap, Map<String, String> vendorIdMap, Map<String, String> productIdMap, Map<String, List<String>> hubMap) {
/* 182 */     String vendorId = vendorIdMap.getOrDefault(devPath, vid);
/* 183 */     String productId = productIdMap.getOrDefault(devPath, pid);
/* 184 */     List<String> childPaths = hubMap.getOrDefault(devPath, new ArrayList<>());
/* 185 */     List<UsbDevice> usbDevices = new ArrayList<>();
/* 186 */     for (String path : childPaths) {
/* 187 */       usbDevices.add(getDeviceAndChildren(path, vendorId, productId, nameMap, vendorIdMap, productIdMap, hubMap));
/*     */     }
/* 189 */     Collections.sort(usbDevices);
/* 190 */     return new SolarisUsbDevice(nameMap.getOrDefault(devPath, vendorId + ":" + productId), "", vendorId, productId, "", devPath, usbDevices);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platfor\\unix\solaris\SolarisUsbDevice.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */