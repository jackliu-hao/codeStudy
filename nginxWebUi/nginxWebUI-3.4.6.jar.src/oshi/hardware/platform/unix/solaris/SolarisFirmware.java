/*    */ package oshi.hardware.platform.unix.solaris;
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
/*    */ final class SolarisFirmware
/*    */   extends AbstractFirmware
/*    */ {
/*    */   private final String manufacturer;
/*    */   private final String version;
/*    */   private final String releaseDate;
/*    */   
/*    */   SolarisFirmware(String manufacturer, String version, String releaseDate) {
/* 40 */     this.manufacturer = manufacturer;
/* 41 */     this.version = version;
/* 42 */     this.releaseDate = releaseDate;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getManufacturer() {
/* 47 */     return this.manufacturer;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getVersion() {
/* 52 */     return this.version;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getReleaseDate() {
/* 57 */     return this.releaseDate;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platfor\\unix\solaris\SolarisFirmware.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */