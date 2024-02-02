/*    */ package oshi.hardware.common;
/*    */ 
/*    */ import java.util.function.Supplier;
/*    */ import oshi.annotation.concurrent.Immutable;
/*    */ import oshi.hardware.Baseboard;
/*    */ import oshi.hardware.ComputerSystem;
/*    */ import oshi.hardware.Firmware;
/*    */ import oshi.util.Memoizer;
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
/*    */ public abstract class AbstractComputerSystem
/*    */   implements ComputerSystem
/*    */ {
/* 41 */   private final Supplier<Firmware> firmware = Memoizer.memoize(this::createFirmware);
/*    */   
/* 43 */   private final Supplier<Baseboard> baseboard = Memoizer.memoize(this::createBaseboard);
/*    */ 
/*    */   
/*    */   public Firmware getFirmware() {
/* 47 */     return this.firmware.get();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected abstract Firmware createFirmware();
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Baseboard getBaseboard() {
/* 59 */     return this.baseboard.get();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected abstract Baseboard createBaseboard();
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String toString() {
/* 71 */     StringBuilder sb = new StringBuilder();
/* 72 */     sb.append("manufacturer=").append(getManufacturer()).append(", ");
/* 73 */     sb.append("model=").append(getModel()).append(", ");
/* 74 */     sb.append("serial number=").append(getSerialNumber());
/* 75 */     return sb.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\common\AbstractComputerSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */