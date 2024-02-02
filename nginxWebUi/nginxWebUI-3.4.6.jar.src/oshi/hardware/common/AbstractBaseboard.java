/*    */ package oshi.hardware.common;
/*    */ 
/*    */ import oshi.annotation.concurrent.Immutable;
/*    */ import oshi.hardware.Baseboard;
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
/*    */ public abstract class AbstractBaseboard
/*    */   implements Baseboard
/*    */ {
/*    */   public String toString() {
/* 37 */     StringBuilder sb = new StringBuilder();
/* 38 */     sb.append("manufacturer=").append(getManufacturer()).append(", ");
/* 39 */     sb.append("model=").append(getModel()).append(", ");
/* 40 */     sb.append("version=").append(getVersion()).append(", ");
/* 41 */     sb.append("serial number=").append(getSerialNumber());
/* 42 */     return sb.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\common\AbstractBaseboard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */