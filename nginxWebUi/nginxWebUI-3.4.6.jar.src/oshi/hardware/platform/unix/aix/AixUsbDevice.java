/*    */ package oshi.hardware.platform.unix.aix;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collections;
/*    */ import java.util.List;
/*    */ import java.util.function.Supplier;
/*    */ import oshi.annotation.concurrent.Immutable;
/*    */ import oshi.hardware.UsbDevice;
/*    */ import oshi.hardware.common.AbstractUsbDevice;
/*    */ import oshi.util.ParseUtil;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Immutable
/*    */ public class AixUsbDevice
/*    */   extends AbstractUsbDevice
/*    */ {
/*    */   public AixUsbDevice(String name, String vendor, String vendorId, String productId, String serialNumber, String uniqueDeviceId, List<UsbDevice> connectedDevices) {
/* 45 */     super(name, vendor, vendorId, productId, serialNumber, uniqueDeviceId, connectedDevices);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static List<UsbDevice> getUsbDevices(boolean tree, Supplier<List<String>> lscfg) {
/* 58 */     return Collections.unmodifiableList(getUsbDevices(lscfg.get()));
/*    */   }
/*    */   
/*    */   private static List<UsbDevice> getUsbDevices(List<String> lsusb) {
/* 62 */     List<UsbDevice> deviceList = new ArrayList<>();
/* 63 */     for (String line : lsusb) {
/* 64 */       String s = line.trim();
/* 65 */       if (s.startsWith("usb")) {
/* 66 */         String[] split = ParseUtil.whitespaces.split(s, 3);
/* 67 */         if (split.length == 3) {
/* 68 */           deviceList.add(new AixUsbDevice(split[2], "unknown", "unknown", "unknown", "unknown", split[0], 
/* 69 */                 Collections.emptyList()));
/*    */         }
/*    */       } 
/*    */     } 
/* 73 */     return deviceList;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platfor\\unix\aix\AixUsbDevice.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */