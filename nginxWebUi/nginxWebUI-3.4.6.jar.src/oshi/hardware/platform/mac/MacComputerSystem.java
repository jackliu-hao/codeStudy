/*    */ package oshi.hardware.platform.mac;
/*    */ 
/*    */ import com.sun.jna.platform.mac.IOKit;
/*    */ import com.sun.jna.platform.mac.IOKitUtil;
/*    */ import java.nio.charset.StandardCharsets;
/*    */ import java.util.function.Supplier;
/*    */ import oshi.annotation.concurrent.Immutable;
/*    */ import oshi.hardware.Baseboard;
/*    */ import oshi.hardware.Firmware;
/*    */ import oshi.hardware.common.AbstractComputerSystem;
/*    */ import oshi.util.Memoizer;
/*    */ import oshi.util.Util;
/*    */ import oshi.util.tuples.Triplet;
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
/*    */ 
/*    */ @Immutable
/*    */ final class MacComputerSystem
/*    */   extends AbstractComputerSystem
/*    */ {
/* 48 */   private final Supplier<Triplet<String, String, String>> manufacturerModelSerial = Memoizer.memoize(MacComputerSystem::platformExpert);
/*    */ 
/*    */ 
/*    */   
/*    */   public String getManufacturer() {
/* 53 */     return (String)((Triplet)this.manufacturerModelSerial.get()).getA();
/*    */   }
/*    */ 
/*    */   
/*    */   public String getModel() {
/* 58 */     return (String)((Triplet)this.manufacturerModelSerial.get()).getB();
/*    */   }
/*    */ 
/*    */   
/*    */   public String getSerialNumber() {
/* 63 */     return (String)((Triplet)this.manufacturerModelSerial.get()).getC();
/*    */   }
/*    */ 
/*    */   
/*    */   public Firmware createFirmware() {
/* 68 */     return (Firmware)new MacFirmware();
/*    */   }
/*    */ 
/*    */   
/*    */   public Baseboard createBaseboard() {
/* 73 */     return (Baseboard)new MacBaseboard();
/*    */   }
/*    */   
/*    */   private static Triplet<String, String, String> platformExpert() {
/* 77 */     String manufacturer = null;
/* 78 */     String model = null;
/* 79 */     String serialNumber = null;
/* 80 */     IOKit.IOService iOService = IOKitUtil.getMatchingService("IOPlatformExpertDevice");
/* 81 */     if (iOService != null) {
/* 82 */       byte[] data = iOService.getByteArrayProperty("manufacturer");
/* 83 */       if (data != null) {
/* 84 */         manufacturer = (new String(data, StandardCharsets.UTF_8)).trim();
/*    */       }
/* 86 */       data = iOService.getByteArrayProperty("model");
/* 87 */       if (data != null) {
/* 88 */         model = (new String(data, StandardCharsets.UTF_8)).trim();
/*    */       }
/* 90 */       serialNumber = iOService.getStringProperty("IOPlatformSerialNumber");
/* 91 */       iOService.release();
/*    */     } 
/* 93 */     return new Triplet(Util.isBlank(manufacturer) ? "Apple Inc." : manufacturer, 
/* 94 */         Util.isBlank(model) ? "unknown" : model, 
/* 95 */         Util.isBlank(serialNumber) ? "unknown" : serialNumber);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platform\mac\MacComputerSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */