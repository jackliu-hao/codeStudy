/*    */ package oshi.hardware.platform.unix.freebsd;
/*    */ 
/*    */ import com.sun.jna.Memory;
/*    */ import com.sun.jna.Pointer;
/*    */ import com.sun.jna.ptr.IntByReference;
/*    */ import oshi.annotation.concurrent.ThreadSafe;
/*    */ import oshi.hardware.common.AbstractSensors;
/*    */ import oshi.jna.platform.unix.freebsd.FreeBsdLibc;
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
/*    */ final class FreeBsdSensors
/*    */   extends AbstractSensors
/*    */ {
/*    */   public double queryCpuTemperature() {
/* 42 */     return queryKldloadCoretemp();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private static double queryKldloadCoretemp() {
/* 52 */     String name = "dev.cpu.%d.temperature";
/* 53 */     IntByReference size = new IntByReference(FreeBsdLibc.INT_SIZE);
/* 54 */     Memory memory = new Memory(size.getValue());
/* 55 */     int cpu = 0;
/* 56 */     double sumTemp = 0.0D;
/* 57 */     while (0 == FreeBsdLibc.INSTANCE.sysctlbyname(String.format(name, new Object[] { Integer.valueOf(cpu) }), (Pointer)memory, size, null, 0)) {
/* 58 */       sumTemp += memory.getInt(0L) / 10.0D - 273.15D;
/* 59 */       cpu++;
/*    */     } 
/* 61 */     return (cpu > 0) ? (sumTemp / cpu) : Double.NaN;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public int[] queryFanSpeeds() {
/* 67 */     return new int[0];
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public double queryCpuVoltage() {
/* 73 */     return 0.0D;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platfor\\unix\freebsd\FreeBsdSensors.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */