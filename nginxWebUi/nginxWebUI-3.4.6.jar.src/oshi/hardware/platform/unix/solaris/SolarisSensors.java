/*    */ package oshi.hardware.platform.unix.solaris;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import oshi.annotation.concurrent.ThreadSafe;
/*    */ import oshi.hardware.common.AbstractSensors;
/*    */ import oshi.util.ExecutingCommand;
/*    */ import oshi.util.ParseUtil;
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
/*    */ final class SolarisSensors
/*    */   extends AbstractSensors
/*    */ {
/*    */   public double queryCpuTemperature() {
/* 42 */     double maxTemp = 0.0D;
/*    */     
/* 44 */     for (String line : ExecutingCommand.runNative("/usr/sbin/prtpicl -v -c temperature-sensor")) {
/* 45 */       if (line.trim().startsWith("Temperature:")) {
/* 46 */         int temp = ParseUtil.parseLastInt(line, 0);
/* 47 */         if (temp > maxTemp) {
/* 48 */           maxTemp = temp;
/*    */         }
/*    */       } 
/*    */     } 
/*    */     
/* 53 */     if (maxTemp > 1000.0D) {
/* 54 */       maxTemp /= 1000.0D;
/*    */     }
/* 56 */     return maxTemp;
/*    */   }
/*    */ 
/*    */   
/*    */   public int[] queryFanSpeeds() {
/* 61 */     List<Integer> speedList = new ArrayList<>();
/*    */     
/* 63 */     for (String line : ExecutingCommand.runNative("/usr/sbin/prtpicl -v -c fan")) {
/* 64 */       if (line.trim().startsWith("Speed:")) {
/* 65 */         speedList.add(Integer.valueOf(ParseUtil.parseLastInt(line, 0)));
/*    */       }
/*    */     } 
/* 68 */     int[] fans = new int[speedList.size()];
/* 69 */     for (int i = 0; i < speedList.size(); i++) {
/* 70 */       fans[i] = ((Integer)speedList.get(i)).intValue();
/*    */     }
/* 72 */     return fans;
/*    */   }
/*    */ 
/*    */   
/*    */   public double queryCpuVoltage() {
/* 77 */     double voltage = 0.0D;
/* 78 */     for (String line : ExecutingCommand.runNative("/usr/sbin/prtpicl -v -c voltage-sensor")) {
/* 79 */       if (line.trim().startsWith("Voltage:")) {
/* 80 */         voltage = ParseUtil.parseDoubleOrDefault(line.replace("Voltage:", "").trim(), 0.0D);
/*    */         break;
/*    */       } 
/*    */     } 
/* 84 */     return voltage;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platfor\\unix\solaris\SolarisSensors.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */