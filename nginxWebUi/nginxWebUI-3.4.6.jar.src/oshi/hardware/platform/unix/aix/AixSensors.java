/*    */ package oshi.hardware.platform.unix.aix;
/*    */ 
/*    */ import java.util.List;
/*    */ import java.util.function.Supplier;
/*    */ import oshi.annotation.concurrent.ThreadSafe;
/*    */ import oshi.hardware.common.AbstractSensors;
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
/*    */ final class AixSensors
/*    */   extends AbstractSensors
/*    */ {
/*    */   private final Supplier<List<String>> lscfg;
/*    */   
/*    */   AixSensors(Supplier<List<String>> lscfg) {
/* 41 */     this.lscfg = lscfg;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public double queryCpuTemperature() {
/* 47 */     return 0.0D;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int[] queryFanSpeeds() {
/* 54 */     int fans = 0;
/* 55 */     for (String s : this.lscfg.get()) {
/* 56 */       if (s.contains("Air Mover")) {
/* 57 */         fans++;
/*    */       }
/*    */     } 
/* 60 */     return new int[fans];
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public double queryCpuVoltage() {
/* 66 */     return 0.0D;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platfor\\unix\aix\AixSensors.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */