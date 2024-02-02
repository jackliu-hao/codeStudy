/*     */ package oshi.hardware.platform.linux;
/*     */ 
/*     */ import com.sun.jna.platform.linux.Udev;
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
/*     */ public class LinuxUsbDevice
/*     */   extends AbstractUsbDevice
/*     */ {
/*     */   public LinuxUsbDevice(String name, String vendor, String vendorId, String productId, String serialNumber, String uniqueDeviceId, List<UsbDevice> connectedDevices) {
/*  49 */     super(name, vendor, vendorId, productId, serialNumber, uniqueDeviceId, connectedDevices);
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
/*  60 */     List<UsbDevice> devices = getUsbDevices();
/*  61 */     if (tree) {
/*  62 */       return devices;
/*     */     }
/*  64 */     List<UsbDevice> deviceList = new ArrayList<>();
/*     */ 
/*     */     
/*  67 */     for (UsbDevice device : devices) {
/*  68 */       deviceList.add(new LinuxUsbDevice(device.getName(), device.getVendor(), device.getVendorId(), device
/*  69 */             .getProductId(), device.getSerialNumber(), device.getUniqueDeviceId(), 
/*  70 */             Collections.emptyList()));
/*  71 */       addDevicesToList(deviceList, device.getConnectedDevices());
/*     */     } 
/*  73 */     return deviceList;
/*     */   }
/*     */ 
/*     */   
/*     */   private static List<UsbDevice> getUsbDevices() {
/*  78 */     Udev.UdevContext udev = Udev.INSTANCE.udev_new();
/*     */     
/*  80 */     Udev.UdevEnumerate enumerate = Udev.INSTANCE.udev_enumerate_new(udev);
/*  81 */     Udev.INSTANCE.udev_enumerate_add_match_subsystem(enumerate, "usb");
/*  82 */     Udev.INSTANCE.udev_enumerate_scan_devices(enumerate);
/*  83 */     Udev.UdevListEntry devices = Udev.INSTANCE.udev_enumerate_get_list_entry(enumerate);
/*     */ 
/*     */     
/*  86 */     List<String> usbControllers = new ArrayList<>();
/*     */ 
/*     */     
/*  89 */     Map<String, String> nameMap = new HashMap<>();
/*  90 */     Map<String, String> vendorMap = new HashMap<>();
/*  91 */     Map<String, String> vendorIdMap = new HashMap<>();
/*  92 */     Map<String, String> productIdMap = new HashMap<>();
/*  93 */     Map<String, String> serialMap = new HashMap<>();
/*  94 */     Map<String, List<String>> hubMap = new HashMap<>();
/*     */ 
/*     */     
/*  97 */     for (Udev.UdevListEntry dev_list_entry = devices; dev_list_entry != null; 
/*  98 */       dev_list_entry = Udev.INSTANCE.udev_list_entry_get_next(dev_list_entry)) {
/*     */ 
/*     */ 
/*     */       
/* 102 */       String path = Udev.INSTANCE.udev_list_entry_get_name(dev_list_entry);
/* 103 */       Udev.UdevDevice dev = Udev.INSTANCE.udev_device_new_from_syspath(udev, path);
/*     */       
/* 105 */       if ("usb_device".equals(Udev.INSTANCE.udev_device_get_devtype(dev))) {
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 110 */         String value = Udev.INSTANCE.udev_device_get_sysattr_value(dev, "product");
/* 111 */         if (value != null) {
/* 112 */           nameMap.put(path, value);
/*     */         }
/* 114 */         value = Udev.INSTANCE.udev_device_get_sysattr_value(dev, "manufacturer");
/* 115 */         if (value != null) {
/* 116 */           vendorMap.put(path, value);
/*     */         }
/* 118 */         value = Udev.INSTANCE.udev_device_get_sysattr_value(dev, "idVendor");
/* 119 */         if (value != null) {
/* 120 */           vendorIdMap.put(path, value);
/*     */         }
/* 122 */         value = Udev.INSTANCE.udev_device_get_sysattr_value(dev, "idProduct");
/* 123 */         if (value != null) {
/* 124 */           productIdMap.put(path, value);
/*     */         }
/* 126 */         value = Udev.INSTANCE.udev_device_get_sysattr_value(dev, "serial");
/* 127 */         if (value != null) {
/* 128 */           serialMap.put(path, value);
/*     */         }
/* 130 */         Udev.UdevDevice parent = Udev.INSTANCE.udev_device_get_parent_with_subsystem_devtype(dev, "usb", "usb_device");
/* 131 */         if (parent == null) {
/*     */           
/* 133 */           usbControllers.add(path);
/*     */         } else {
/*     */           
/* 136 */           String parentPath = Udev.INSTANCE.udev_device_get_syspath(parent);
/* 137 */           ((List<String>)hubMap.computeIfAbsent(parentPath, x -> new ArrayList())).add(path);
/*     */         } 
/* 139 */         Udev.INSTANCE.udev_device_unref(dev);
/*     */       } 
/*     */     } 
/* 142 */     Udev.INSTANCE.udev_enumerate_unref(enumerate);
/* 143 */     Udev.INSTANCE.udev_unref(udev);
/*     */ 
/*     */     
/* 146 */     List<UsbDevice> controllerDevices = new ArrayList<>();
/* 147 */     for (String controller : usbControllers) {
/* 148 */       controllerDevices.add(getDeviceAndChildren(controller, "0000", "0000", nameMap, vendorMap, vendorIdMap, productIdMap, serialMap, hubMap));
/*     */     }
/*     */     
/* 151 */     return controllerDevices;
/*     */   }
/*     */   
/*     */   private static void addDevicesToList(List<UsbDevice> deviceList, List<UsbDevice> list) {
/* 155 */     for (UsbDevice device : list) {
/* 156 */       deviceList.add(device);
/* 157 */       addDevicesToList(deviceList, device.getConnectedDevices());
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
/*     */   private static LinuxUsbDevice getDeviceAndChildren(String devPath, String vid, String pid, Map<String, String> nameMap, Map<String, String> vendorMap, Map<String, String> vendorIdMap, Map<String, String> productIdMap, Map<String, String> serialMap, Map<String, List<String>> hubMap) {
/* 188 */     String vendorId = vendorIdMap.getOrDefault(devPath, vid);
/* 189 */     String productId = productIdMap.getOrDefault(devPath, pid);
/* 190 */     List<String> childPaths = hubMap.getOrDefault(devPath, new ArrayList<>());
/* 191 */     List<UsbDevice> usbDevices = new ArrayList<>();
/* 192 */     for (String path : childPaths) {
/* 193 */       usbDevices.add(getDeviceAndChildren(path, vendorId, productId, nameMap, vendorMap, vendorIdMap, productIdMap, serialMap, hubMap));
/*     */     }
/*     */     
/* 196 */     Collections.sort(usbDevices);
/* 197 */     return new LinuxUsbDevice(nameMap.getOrDefault(devPath, vendorId + ":" + productId), vendorMap
/* 198 */         .getOrDefault(devPath, ""), vendorId, productId, serialMap.getOrDefault(devPath, ""), devPath, usbDevices);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platform\linux\LinuxUsbDevice.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */