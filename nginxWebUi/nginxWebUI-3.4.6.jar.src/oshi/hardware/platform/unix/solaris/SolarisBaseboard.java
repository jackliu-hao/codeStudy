/*    */ package oshi.hardware.platform.unix.solaris;
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
/*    */ 
/*    */ @Immutable
/*    */ final class SolarisBaseboard
/*    */   extends AbstractBaseboard
/*    */ {
/*    */   private final String manufacturer;
/*    */   private final String model;
/*    */   private final String serialNumber;
/*    */   private final String version;
/*    */   
/*    */   SolarisBaseboard(String manufacturer, String model, String serialNumber, String version) {
/* 41 */     this.manufacturer = manufacturer;
/* 42 */     this.model = model;
/* 43 */     this.serialNumber = serialNumber;
/* 44 */     this.version = version;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getManufacturer() {
/* 49 */     return this.manufacturer;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getModel() {
/* 54 */     return this.model;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getSerialNumber() {
/* 59 */     return this.serialNumber;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getVersion() {
/* 64 */     return this.version;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platfor\\unix\solaris\SolarisBaseboard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */