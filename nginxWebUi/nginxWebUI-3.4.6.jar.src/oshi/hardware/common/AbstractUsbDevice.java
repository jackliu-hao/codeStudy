/*     */ package oshi.hardware.common;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import oshi.annotation.concurrent.Immutable;
/*     */ import oshi.hardware.UsbDevice;
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
/*     */ public abstract class AbstractUsbDevice
/*     */   implements UsbDevice
/*     */ {
/*     */   private final String name;
/*     */   private final String vendor;
/*     */   private final String vendorId;
/*     */   private final String productId;
/*     */   private final String serialNumber;
/*     */   private final String uniqueDeviceId;
/*     */   private final List<UsbDevice> connectedDevices;
/*     */   
/*     */   protected AbstractUsbDevice(String name, String vendor, String vendorId, String productId, String serialNumber, String uniqueDeviceId, List<UsbDevice> connectedDevices) {
/*  48 */     this.name = name;
/*  49 */     this.vendor = vendor;
/*  50 */     this.vendorId = vendorId;
/*  51 */     this.productId = productId;
/*  52 */     this.serialNumber = serialNumber;
/*  53 */     this.uniqueDeviceId = uniqueDeviceId;
/*  54 */     this.connectedDevices = Collections.unmodifiableList(connectedDevices);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/*  59 */     return this.name;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getVendor() {
/*  64 */     return this.vendor;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getVendorId() {
/*  69 */     return this.vendorId;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getProductId() {
/*  74 */     return this.productId;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getSerialNumber() {
/*  79 */     return this.serialNumber;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getUniqueDeviceId() {
/*  84 */     return this.uniqueDeviceId;
/*     */   }
/*     */ 
/*     */   
/*     */   public List<UsbDevice> getConnectedDevices() {
/*  89 */     return this.connectedDevices;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int compareTo(UsbDevice usb) {
/*  95 */     return getName().compareTo(usb.getName());
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 100 */     return indentUsb(this, 1);
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
/*     */   private static String indentUsb(UsbDevice usbDevice, int indent) {
/* 113 */     String indentFmt = (indent > 2) ? String.format("%%%ds|-- ", new Object[] { Integer.valueOf(indent - 4) }) : String.format("%%%ds", new Object[] { Integer.valueOf(indent) });
/* 114 */     StringBuilder sb = new StringBuilder(String.format(indentFmt, new Object[] { "" }));
/* 115 */     sb.append(usbDevice.getName());
/* 116 */     if (usbDevice.getVendor().length() > 0) {
/* 117 */       sb.append(" (").append(usbDevice.getVendor()).append(')');
/*     */     }
/* 119 */     if (usbDevice.getSerialNumber().length() > 0) {
/* 120 */       sb.append(" [s/n: ").append(usbDevice.getSerialNumber()).append(']');
/*     */     }
/* 122 */     for (UsbDevice connected : usbDevice.getConnectedDevices()) {
/* 123 */       sb.append('\n').append(indentUsb(connected, indent + 4));
/*     */     }
/* 125 */     return sb.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\common\AbstractUsbDevice.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */