/*    */ package oshi.hardware.platform.unix.freebsd;
/*    */ 
/*    */ import oshi.annotation.concurrent.Immutable;
/*    */ import oshi.hardware.common.AbstractBaseboard;
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
/*    */ final class FreeBsdBaseboard
/*    */   extends AbstractBaseboard
/*    */ {
/*    */   private final String manufacturer;
/*    */   private final String model;
/*    */   private final String serialNumber;
/*    */   private final String version;
/*    */   
/*    */   FreeBsdBaseboard(String manufacturer, String model, String serialNumber, String version) {
/* 40 */     this.manufacturer = manufacturer;
/* 41 */     this.model = model;
/* 42 */     this.serialNumber = serialNumber;
/* 43 */     this.version = version;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getManufacturer() {
/* 48 */     return this.manufacturer;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getModel() {
/* 53 */     return this.model;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getSerialNumber() {
/* 58 */     return this.serialNumber;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getVersion() {
/* 63 */     return this.version;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platfor\\unix\freebsd\FreeBsdBaseboard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */