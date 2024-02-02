/*    */ package oshi.hardware.common;
/*    */ 
/*    */ import java.util.Arrays;
/*    */ import oshi.annotation.concurrent.Immutable;
/*    */ import oshi.hardware.Display;
/*    */ import oshi.util.EdidUtil;
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
/*    */ 
/*    */ @Immutable
/*    */ public abstract class AbstractDisplay
/*    */   implements Display
/*    */ {
/*    */   private final byte[] edid;
/*    */   
/*    */   protected AbstractDisplay(byte[] edid) {
/* 47 */     this.edid = Arrays.copyOf(edid, edid.length);
/*    */   }
/*    */ 
/*    */   
/*    */   public byte[] getEdid() {
/* 52 */     return Arrays.copyOf(this.edid, this.edid.length);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 57 */     return EdidUtil.toString(this.edid);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\common\AbstractDisplay.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */