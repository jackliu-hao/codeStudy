/*    */ package oshi.hardware.platform.unix.aix;
/*    */ 
/*    */ import java.time.LocalDate;
/*    */ import java.util.Collections;
/*    */ import java.util.List;
/*    */ import oshi.annotation.concurrent.ThreadSafe;
/*    */ import oshi.hardware.PowerSource;
/*    */ import oshi.hardware.common.AbstractPowerSource;
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
/*    */ @ThreadSafe
/*    */ public final class AixPowerSource
/*    */   extends AbstractPowerSource
/*    */ {
/*    */   public AixPowerSource(String name, String deviceName, double remainingCapacityPercent, double timeRemainingEstimated, double timeRemainingInstant, double powerUsageRate, double voltage, double amperage, boolean powerOnLine, boolean charging, boolean discharging, PowerSource.CapacityUnits capacityUnits, int currentCapacity, int maxCapacity, int designCapacity, int cycleCount, String chemistry, LocalDate manufactureDate, String manufacturer, String serialNumber, double temperature) {
/* 45 */     super(name, deviceName, remainingCapacityPercent, timeRemainingEstimated, timeRemainingInstant, powerUsageRate, voltage, amperage, powerOnLine, charging, discharging, capacityUnits, currentCapacity, maxCapacity, designCapacity, cycleCount, chemistry, manufactureDate, manufacturer, serialNumber, temperature);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static List<PowerSource> getPowerSources() {
/* 57 */     return Collections.emptyList();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platfor\\unix\aix\AixPowerSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */