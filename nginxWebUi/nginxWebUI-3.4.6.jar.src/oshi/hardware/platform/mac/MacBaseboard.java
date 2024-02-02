/*    */ package oshi.hardware.platform.mac;
/*    */ 
/*    */ import com.sun.jna.platform.mac.IOKit;
/*    */ import com.sun.jna.platform.mac.IOKitUtil;
/*    */ import java.nio.charset.StandardCharsets;
/*    */ import java.util.function.Supplier;
/*    */ import oshi.annotation.concurrent.Immutable;
/*    */ import oshi.hardware.common.AbstractBaseboard;
/*    */ import oshi.util.Memoizer;
/*    */ import oshi.util.Util;
/*    */ import oshi.util.tuples.Quartet;
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
/*    */ final class MacBaseboard
/*    */   extends AbstractBaseboard
/*    */ {
/* 46 */   private final Supplier<Quartet<String, String, String, String>> manufModelVersSerial = Memoizer.memoize(MacBaseboard::queryPlatform);
/*    */ 
/*    */ 
/*    */   
/*    */   public String getManufacturer() {
/* 51 */     return (String)((Quartet)this.manufModelVersSerial.get()).getA();
/*    */   }
/*    */ 
/*    */   
/*    */   public String getModel() {
/* 56 */     return (String)((Quartet)this.manufModelVersSerial.get()).getB();
/*    */   }
/*    */ 
/*    */   
/*    */   public String getVersion() {
/* 61 */     return (String)((Quartet)this.manufModelVersSerial.get()).getC();
/*    */   }
/*    */ 
/*    */   
/*    */   public String getSerialNumber() {
/* 66 */     return (String)((Quartet)this.manufModelVersSerial.get()).getD();
/*    */   }
/*    */   
/*    */   private static Quartet<String, String, String, String> queryPlatform() {
/* 70 */     String manufacturer = null;
/* 71 */     String model = null;
/* 72 */     String version = null;
/* 73 */     String serialNumber = null;
/*    */     
/* 75 */     IOKit.IOService iOService = IOKitUtil.getMatchingService("IOPlatformExpertDevice");
/* 76 */     if (iOService != null) {
/* 77 */       byte[] data = iOService.getByteArrayProperty("manufacturer");
/* 78 */       if (data != null) {
/* 79 */         manufacturer = (new String(data, StandardCharsets.UTF_8)).trim();
/*    */       }
/* 81 */       data = iOService.getByteArrayProperty("board-id");
/* 82 */       if (data != null) {
/* 83 */         model = (new String(data, StandardCharsets.UTF_8)).trim();
/*    */       }
/* 85 */       data = iOService.getByteArrayProperty("version");
/* 86 */       if (data != null) {
/* 87 */         version = (new String(data, StandardCharsets.UTF_8)).trim();
/*    */       }
/* 89 */       serialNumber = iOService.getStringProperty("IOPlatformSerialNumber");
/* 90 */       iOService.release();
/*    */     } 
/* 92 */     return new Quartet(Util.isBlank(manufacturer) ? "Apple Inc." : manufacturer, 
/* 93 */         Util.isBlank(model) ? "unknown" : model, Util.isBlank(version) ? "unknown" : version, 
/* 94 */         Util.isBlank(serialNumber) ? "unknown" : serialNumber);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platform\mac\MacBaseboard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */