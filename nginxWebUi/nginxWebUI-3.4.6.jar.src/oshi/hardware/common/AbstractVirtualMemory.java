/*    */ package oshi.hardware.common;
/*    */ 
/*    */ import oshi.annotation.concurrent.ThreadSafe;
/*    */ import oshi.hardware.VirtualMemory;
/*    */ import oshi.util.FormatUtil;
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
/*    */ @ThreadSafe
/*    */ public abstract class AbstractVirtualMemory
/*    */   implements VirtualMemory
/*    */ {
/*    */   public String toString() {
/* 38 */     StringBuilder sb = new StringBuilder();
/* 39 */     sb.append("Swap Used/Avail: ");
/* 40 */     sb.append(FormatUtil.formatBytes(getSwapUsed()));
/* 41 */     sb.append("/");
/* 42 */     sb.append(FormatUtil.formatBytes(getSwapTotal()));
/* 43 */     sb.append(", Virtual Memory In Use/Max=");
/* 44 */     sb.append(FormatUtil.formatBytes(getVirtualInUse()));
/* 45 */     sb.append("/");
/* 46 */     sb.append(FormatUtil.formatBytes(getVirtualMax()));
/* 47 */     return sb.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\common\AbstractVirtualMemory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */