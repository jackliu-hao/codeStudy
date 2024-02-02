/*    */ package oshi.hardware.platform.unix.aix;
/*    */ 
/*    */ import oshi.annotation.concurrent.Immutable;
/*    */ import oshi.hardware.common.AbstractFirmware;
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
/*    */ final class AixFirmware
/*    */   extends AbstractFirmware
/*    */ {
/*    */   private final String manufacturer;
/*    */   private final String name;
/*    */   private final String version;
/*    */   
/*    */   AixFirmware(String manufacturer, String name, String version) {
/* 40 */     this.manufacturer = manufacturer;
/* 41 */     this.name = name;
/* 42 */     this.version = version;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getManufacturer() {
/* 47 */     return this.manufacturer;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getName() {
/* 52 */     return this.name;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getVersion() {
/* 57 */     return this.version;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platfor\\unix\aix\AixFirmware.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */