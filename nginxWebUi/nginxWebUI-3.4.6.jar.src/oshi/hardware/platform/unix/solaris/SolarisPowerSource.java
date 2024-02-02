/*     */ package oshi.hardware.platform.unix.solaris;
/*     */ 
/*     */ import com.sun.jna.platform.unix.solaris.LibKstat;
/*     */ import java.time.LocalDate;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.hardware.PowerSource;
/*     */ import oshi.hardware.common.AbstractPowerSource;
/*     */ import oshi.util.platform.unix.solaris.KstatUtil;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @ThreadSafe
/*     */ public final class SolarisPowerSource
/*     */   extends AbstractPowerSource
/*     */ {
/*  47 */   private static final String[] KSTAT_BATT_MOD = new String[] { null, "battery", "acpi_drv" };
/*     */   
/*     */   private static final int KSTAT_BATT_IDX;
/*     */   
/*     */   static {
/*  52 */     KstatUtil.KstatChain kc = KstatUtil.openChain(); try {
/*  53 */       if (kc.lookup(KSTAT_BATT_MOD[1], 0, null) != null) {
/*  54 */         KSTAT_BATT_IDX = 1;
/*  55 */       } else if (kc.lookup(KSTAT_BATT_MOD[2], 0, null) != null) {
/*  56 */         KSTAT_BATT_IDX = 2;
/*     */       } else {
/*  58 */         KSTAT_BATT_IDX = 0;
/*     */       } 
/*  60 */       if (kc != null) kc.close(); 
/*     */     } catch (Throwable throwable) {
/*     */       if (kc != null)
/*     */         try {
/*     */           kc.close();
/*     */         } catch (Throwable throwable1) {
/*     */           throwable.addSuppressed(throwable1);
/*     */         }  
/*     */       throw throwable;
/*  69 */     }  } public SolarisPowerSource(String psName, String psDeviceName, double psRemainingCapacityPercent, double psTimeRemainingEstimated, double psTimeRemainingInstant, double psPowerUsageRate, double psVoltage, double psAmperage, boolean psPowerOnLine, boolean psCharging, boolean psDischarging, PowerSource.CapacityUnits psCapacityUnits, int psCurrentCapacity, int psMaxCapacity, int psDesignCapacity, int psCycleCount, String psChemistry, LocalDate psManufactureDate, String psManufacturer, String psSerialNumber, double psTemperature) { super(psName, psDeviceName, psRemainingCapacityPercent, psTimeRemainingEstimated, psTimeRemainingInstant, psPowerUsageRate, psVoltage, psAmperage, psPowerOnLine, psCharging, psDischarging, psCapacityUnits, psCurrentCapacity, psMaxCapacity, psDesignCapacity, psCycleCount, psChemistry, psManufactureDate, psManufacturer, psSerialNumber, psTemperature); }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List<PowerSource> getPowerSources() {
/*  81 */     return Collections.unmodifiableList((List)Arrays.asList((Object[])new SolarisPowerSource[] { getPowerSource("BAT0") }));
/*     */   }
/*     */   
/*     */   private static SolarisPowerSource getPowerSource(String name) {
/*  85 */     String psName = name;
/*  86 */     String psDeviceName = "unknown";
/*  87 */     double psRemainingCapacityPercent = 1.0D;
/*  88 */     double psTimeRemainingEstimated = -1.0D;
/*  89 */     double psTimeRemainingInstant = 0.0D;
/*  90 */     double psPowerUsageRate = 0.0D;
/*  91 */     double psVoltage = -1.0D;
/*  92 */     double psAmperage = 0.0D;
/*  93 */     boolean psPowerOnLine = false;
/*  94 */     boolean psCharging = false;
/*  95 */     boolean psDischarging = false;
/*  96 */     PowerSource.CapacityUnits psCapacityUnits = PowerSource.CapacityUnits.RELATIVE;
/*  97 */     int psCurrentCapacity = 0;
/*  98 */     int psMaxCapacity = 1;
/*  99 */     int psDesignCapacity = 1;
/* 100 */     int psCycleCount = -1;
/* 101 */     String psChemistry = "unknown";
/* 102 */     LocalDate psManufactureDate = null;
/* 103 */     String psManufacturer = "unknown";
/* 104 */     String psSerialNumber = "unknown";
/* 105 */     double psTemperature = 0.0D;
/*     */ 
/*     */     
/* 108 */     if (KSTAT_BATT_IDX > 0) {
/*     */       
/* 110 */       KstatUtil.KstatChain kc = KstatUtil.openChain(); 
/* 111 */       try { LibKstat.Kstat ksp = kc.lookup(KSTAT_BATT_MOD[KSTAT_BATT_IDX], 0, "battery BIF0");
/* 112 */         if (ksp != null) {
/*     */           
/* 114 */           long energyFull = KstatUtil.dataLookupLong(ksp, "bif_last_cap");
/* 115 */           if (energyFull == -1L || energyFull <= 0L) {
/* 116 */             energyFull = KstatUtil.dataLookupLong(ksp, "bif_design_cap");
/*     */           }
/* 118 */           if (energyFull != -1L && energyFull > 0L) {
/* 119 */             psMaxCapacity = (int)energyFull;
/*     */           }
/* 121 */           long unit = KstatUtil.dataLookupLong(ksp, "bif_unit");
/* 122 */           if (unit == 0L) {
/* 123 */             psCapacityUnits = PowerSource.CapacityUnits.MWH;
/* 124 */           } else if (unit == 1L) {
/* 125 */             psCapacityUnits = PowerSource.CapacityUnits.MAH;
/*     */           } 
/* 127 */           psDeviceName = KstatUtil.dataLookupString(ksp, "bif_model");
/* 128 */           psSerialNumber = KstatUtil.dataLookupString(ksp, "bif_serial");
/* 129 */           psChemistry = KstatUtil.dataLookupString(ksp, "bif_type");
/* 130 */           psManufacturer = KstatUtil.dataLookupString(ksp, "bif_oem_info");
/*     */         } 
/*     */ 
/*     */         
/* 134 */         ksp = kc.lookup(KSTAT_BATT_MOD[KSTAT_BATT_IDX], 0, "battery BST0");
/* 135 */         if (ksp != null) {
/*     */           
/* 137 */           long energyNow = KstatUtil.dataLookupLong(ksp, "bst_rem_cap");
/* 138 */           if (energyNow >= 0L) {
/* 139 */             psCurrentCapacity = (int)energyNow;
/*     */           }
/*     */           
/* 142 */           long powerNow = KstatUtil.dataLookupLong(ksp, "bst_rate");
/* 143 */           if (powerNow == -1L) {
/* 144 */             powerNow = 0L;
/*     */           }
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 150 */           boolean isCharging = ((KstatUtil.dataLookupLong(ksp, "bst_state") & 0x10L) > 0L);
/*     */           
/* 152 */           if (!isCharging) {
/* 153 */             psTimeRemainingEstimated = (powerNow > 0L) ? (3600.0D * energyNow / powerNow) : -1.0D;
/*     */           }
/*     */           
/* 156 */           long voltageNow = KstatUtil.dataLookupLong(ksp, "bst_voltage");
/* 157 */           if (voltageNow > 0L) {
/* 158 */             psVoltage = voltageNow / 1000.0D;
/* 159 */             psAmperage = psPowerUsageRate * 1000.0D / voltageNow;
/*     */           } 
/*     */         } 
/* 162 */         if (kc != null) kc.close();  } catch (Throwable throwable) { if (kc != null)
/*     */           try { kc.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; }
/*     */     
/* 165 */     }  return new SolarisPowerSource(psName, psDeviceName, psRemainingCapacityPercent, psTimeRemainingEstimated, psTimeRemainingInstant, psPowerUsageRate, psVoltage, psAmperage, psPowerOnLine, psCharging, psDischarging, psCapacityUnits, psCurrentCapacity, psMaxCapacity, psDesignCapacity, psCycleCount, psChemistry, psManufactureDate, psManufacturer, psSerialNumber, psTemperature);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platfor\\unix\solaris\SolarisPowerSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */