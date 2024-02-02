/*    */ package oshi.hardware.common;
/*    */ 
/*    */ import oshi.annotation.concurrent.Immutable;
/*    */ import oshi.hardware.Firmware;
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
/*    */ 
/*    */ 
/*    */ @Immutable
/*    */ public abstract class AbstractFirmware
/*    */   implements Firmware
/*    */ {
/*    */   public String getName() {
/* 42 */     return "unknown";
/*    */   }
/*    */ 
/*    */   
/*    */   public String getDescription() {
/* 47 */     return "unknown";
/*    */   }
/*    */ 
/*    */   
/*    */   public String getReleaseDate() {
/* 52 */     return "unknown";
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 57 */     StringBuilder sb = new StringBuilder();
/* 58 */     sb.append("manufacturer=").append(getManufacturer()).append(", ");
/* 59 */     sb.append("name=").append(getName()).append(", ");
/* 60 */     sb.append("description=").append(getDescription()).append(", ");
/* 61 */     sb.append("version=").append(getVersion()).append(", ");
/* 62 */     sb.append("release date=").append((getReleaseDate() == null) ? "unknown" : getReleaseDate());
/* 63 */     return sb.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\common\AbstractFirmware.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */