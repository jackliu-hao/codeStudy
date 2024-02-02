/*    */ package oshi.hardware.platform.mac;
/*    */ 
/*    */ import com.sun.jna.platform.mac.IOKit;
/*    */ import oshi.annotation.concurrent.ThreadSafe;
/*    */ import oshi.hardware.common.AbstractSensors;
/*    */ import oshi.util.platform.mac.SmcUtil;
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
/*    */ @ThreadSafe
/*    */ final class MacSensors
/*    */   extends AbstractSensors
/*    */ {
/* 39 */   private int numFans = 0;
/*    */ 
/*    */   
/*    */   public double queryCpuTemperature() {
/* 43 */     IOKit.IOConnect conn = SmcUtil.smcOpen();
/* 44 */     double temp = SmcUtil.smcGetFloat(conn, "TC0P");
/* 45 */     SmcUtil.smcClose(conn);
/* 46 */     if (temp > 0.0D) {
/* 47 */       return temp;
/*    */     }
/* 49 */     return 0.0D;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public int[] queryFanSpeeds() {
/* 55 */     IOKit.IOConnect conn = SmcUtil.smcOpen();
/* 56 */     if (this.numFans == 0) {
/* 57 */       this.numFans = (int)SmcUtil.smcGetLong(conn, "FNum");
/*    */     }
/* 59 */     int[] fanSpeeds = new int[this.numFans];
/* 60 */     for (int i = 0; i < this.numFans; i++) {
/* 61 */       fanSpeeds[i] = (int)SmcUtil.smcGetFloat(conn, String.format("F%dAc", new Object[] { Integer.valueOf(i) }));
/*    */     } 
/* 63 */     SmcUtil.smcClose(conn);
/* 64 */     return fanSpeeds;
/*    */   }
/*    */ 
/*    */   
/*    */   public double queryCpuVoltage() {
/* 69 */     IOKit.IOConnect conn = SmcUtil.smcOpen();
/* 70 */     double volts = SmcUtil.smcGetFloat(conn, "VC0C") / 1000.0D;
/* 71 */     SmcUtil.smcClose(conn);
/* 72 */     return volts;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platform\mac\MacSensors.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */