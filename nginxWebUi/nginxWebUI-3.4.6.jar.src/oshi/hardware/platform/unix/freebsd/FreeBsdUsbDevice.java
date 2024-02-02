/*     */ package oshi.hardware.platform.unix.freebsd;
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
/*     */ public class FreeBsdUsbDevice
/*     */   extends AbstractUsbDevice
/*     */ {
/*     */   public FreeBsdUsbDevice(String name, String vendor, String vendorId, String productId, String serialNumber, String uniqueDeviceId, List<UsbDevice> connectedDevices) {
/*  46 */     super(name, vendor, vendorId, productId, serialNumber, uniqueDeviceId, connectedDevices);
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
/*  57 */     List<UsbDevice> devices = getUsbDevices();
/*  58 */     if (tree) {
/*  59 */       return Collections.unmodifiableList(devices);
/*     */     }
/*  61 */     List<UsbDevice> deviceList = new ArrayList<>();
/*     */ 
/*     */     
/*  64 */     for (UsbDevice device : devices) {
/*  65 */       deviceList.add(new FreeBsdUsbDevice(device.getName(), device.getVendor(), device.getVendorId(), device
/*  66 */             .getProductId(), device.getSerialNumber(), device.getUniqueDeviceId(), 
/*  67 */             Collections.emptyList()));
/*  68 */       addDevicesToList(deviceList, device.getConnectedDevices());
/*     */     } 
/*  70 */     return Collections.unmodifiableList(deviceList);
/*     */   }
/*     */ 
/*     */   
/*     */   private static List<UsbDevice> getUsbDevices() {
/*  75 */     Map<String, String> nameMap = new HashMap<>();
/*  76 */     Map<String, String> vendorMap = new HashMap<>();
/*  77 */     Map<String, String> vendorIdMap = new HashMap<>();
/*  78 */     Map<String, String> productIdMap = new HashMap<>();
/*  79 */     Map<String, String> serialMap = new HashMap<>();
/*  80 */     Map<String, String> parentMap = new HashMap<>();
/*  81 */     Map<String, List<String>> hubMap = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  87 */     List<String> devices = ExecutingCommand.runNative("lshal");
/*  88 */     if (devices.isEmpty()) {
/*  89 */       return Collections.emptyList();
/*     */     }
/*     */     
/*  92 */     String key = "";
/*  93 */     List<String> usBuses = new ArrayList<>();
/*  94 */     for (String line : devices) {
/*     */       
/*  96 */       if (line.startsWith("udi =")) {
/*     */         
/*  98 */         key = ParseUtil.getSingleQuoteStringValue(line); continue;
/*  99 */       }  if (!key.isEmpty()) {
/*     */ 
/*     */         
/* 102 */         line = line.trim();
/* 103 */         if (!line.isEmpty()) {
/* 104 */           if (line.startsWith("freebsd.driver =") && "usbus"
/* 105 */             .equals(ParseUtil.getSingleQuoteStringValue(line))) {
/* 106 */             usBuses.add(key); continue;
/* 107 */           }  if (line.contains(".parent =")) {
/* 108 */             String parent = ParseUtil.getSingleQuoteStringValue(line);
/*     */             
/* 110 */             if (key.replace(parent, "").startsWith("_if")) {
/*     */               continue;
/*     */             }
/*     */             
/* 114 */             parentMap.put(key, parent);
/*     */             
/* 116 */             ((List<String>)hubMap.computeIfAbsent(parent, x -> new ArrayList())).add(key); continue;
/* 117 */           }  if (line.contains(".product =")) {
/* 118 */             nameMap.put(key, ParseUtil.getSingleQuoteStringValue(line)); continue;
/* 119 */           }  if (line.contains(".vendor =")) {
/* 120 */             vendorMap.put(key, ParseUtil.getSingleQuoteStringValue(line)); continue;
/* 121 */           }  if (line.contains(".serial =")) {
/* 122 */             String serial = ParseUtil.getSingleQuoteStringValue(line);
/* 123 */             serialMap.put(key, 
/* 124 */                 serial.startsWith("0x") ? ParseUtil.hexStringToString(serial.replace("0x", "")) : 
/* 125 */                 serial); continue;
/* 126 */           }  if (line.contains(".vendor_id =")) {
/* 127 */             vendorIdMap.put(key, String.format("%04x", new Object[] { Integer.valueOf(ParseUtil.getFirstIntValue(line)) })); continue;
/* 128 */           }  if (line.contains(".product_id =")) {
/* 129 */             productIdMap.put(key, String.format("%04x", new Object[] { Integer.valueOf(ParseUtil.getFirstIntValue(line)) }));
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 136 */     List<UsbDevice> controllerDevices = new ArrayList<>();
/* 137 */     for (String usbus : usBuses) {
/*     */ 
/*     */       
/* 140 */       String parent = parentMap.get(usbus);
/* 141 */       hubMap.put(parent, hubMap.get(usbus));
/* 142 */       controllerDevices.add(getDeviceAndChildren(parent, "0000", "0000", nameMap, vendorMap, vendorIdMap, productIdMap, serialMap, hubMap));
/*     */     } 
/*     */     
/* 145 */     return controllerDevices;
/*     */   }
/*     */   
/*     */   private static void addDevicesToList(List<UsbDevice> deviceList, List<UsbDevice> list) {
/* 149 */     for (UsbDevice device : list) {
/* 150 */       deviceList.add(device);
/* 151 */       addDevicesToList(deviceList, device.getConnectedDevices());
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
/*     */   private static FreeBsdUsbDevice getDeviceAndChildren(String devPath, String vid, String pid, Map<String, String> nameMap, Map<String, String> vendorMap, Map<String, String> vendorIdMap, Map<String, String> productIdMap, Map<String, String> serialMap, Map<String, List<String>> hubMap) {
/* 182 */     String vendorId = vendorIdMap.getOrDefault(devPath, vid);
/* 183 */     String productId = productIdMap.getOrDefault(devPath, pid);
/* 184 */     List<String> childPaths = hubMap.getOrDefault(devPath, new ArrayList<>());
/* 185 */     List<UsbDevice> usbDevices = new ArrayList<>();
/* 186 */     for (String path : childPaths) {
/* 187 */       usbDevices.add(getDeviceAndChildren(path, vendorId, productId, nameMap, vendorMap, vendorIdMap, productIdMap, serialMap, hubMap));
/*     */     }
/*     */     
/* 190 */     Collections.sort(usbDevices);
/* 191 */     return new FreeBsdUsbDevice(nameMap.getOrDefault(devPath, vendorId + ":" + productId), vendorMap
/* 192 */         .getOrDefault(devPath, ""), vendorId, productId, serialMap.getOrDefault(devPath, ""), devPath, usbDevices);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platfor\\unix\freebsd\FreeBsdUsbDevice.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */