/*     */ package oshi.hardware.platform.linux;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.time.LocalDate;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.hardware.PowerSource;
/*     */ import oshi.hardware.common.AbstractPowerSource;
/*     */ import oshi.util.FileUtil;
/*     */ import oshi.util.ParseUtil;
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
/*     */ 
/*     */ 
/*     */ @ThreadSafe
/*     */ public final class LinuxPowerSource
/*     */   extends AbstractPowerSource
/*     */ {
/*     */   private static final String PS_PATH = "/sys/class/power_supply/";
/*     */   
/*     */   public LinuxPowerSource(String psName, String psDeviceName, double psRemainingCapacityPercent, double psTimeRemainingEstimated, double psTimeRemainingInstant, double psPowerUsageRate, double psVoltage, double psAmperage, boolean psPowerOnLine, boolean psCharging, boolean psDischarging, PowerSource.CapacityUnits psCapacityUnits, int psCurrentCapacity, int psMaxCapacity, int psDesignCapacity, int psCycleCount, String psChemistry, LocalDate psManufactureDate, String psManufacturer, String psSerialNumber, double psTemperature) {
/*  55 */     super(psName, psDeviceName, psRemainingCapacityPercent, psTimeRemainingEstimated, psTimeRemainingInstant, psPowerUsageRate, psVoltage, psAmperage, psPowerOnLine, psCharging, psDischarging, psCapacityUnits, psCurrentCapacity, psMaxCapacity, psDesignCapacity, psCycleCount, psChemistry, psManufactureDate, psManufacturer, psSerialNumber, psTemperature);
/*     */   }
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
/*     */   public static List<PowerSource> getPowerSources() {
/*  69 */     double psRemainingCapacityPercent = -1.0D;
/*  70 */     double psTimeRemainingEstimated = -1.0D;
/*  71 */     double psTimeRemainingInstant = -1.0D;
/*  72 */     double psPowerUsageRate = 0.0D;
/*  73 */     double psVoltage = -1.0D;
/*  74 */     double psAmperage = 0.0D;
/*  75 */     boolean psPowerOnLine = false;
/*  76 */     boolean psCharging = false;
/*  77 */     boolean psDischarging = false;
/*  78 */     PowerSource.CapacityUnits psCapacityUnits = PowerSource.CapacityUnits.RELATIVE;
/*  79 */     int psCurrentCapacity = -1;
/*  80 */     int psMaxCapacity = -1;
/*  81 */     int psDesignCapacity = -1;
/*  82 */     int psCycleCount = -1;
/*     */     
/*  84 */     LocalDate psManufactureDate = null;
/*     */ 
/*     */     
/*  87 */     double psTemperature = 0.0D;
/*     */ 
/*     */     
/*  90 */     File f = new File("/sys/class/power_supply/");
/*  91 */     String[] psNames = f.list();
/*  92 */     List<LinuxPowerSource> psList = new ArrayList<>();
/*     */     
/*  94 */     if (psNames != null)
/*     */     {
/*  96 */       for (String name : psNames) {
/*     */         
/*  98 */         if (!name.startsWith("ADP") && !name.startsWith("AC")) {
/*     */ 
/*     */           
/* 101 */           List<String> psInfo = FileUtil.readFile("/sys/class/power_supply/" + name + "/uevent", false);
/* 102 */           if (!psInfo.isEmpty()) {
/*     */ 
/*     */             
/* 105 */             Map<String, String> psMap = new HashMap<>();
/* 106 */             for (String line : psInfo) {
/* 107 */               String[] split = line.split("=");
/* 108 */               if (split.length > 1 && !split[1].isEmpty()) {
/* 109 */                 psMap.put(split[0], split[1]);
/*     */               }
/*     */             } 
/* 112 */             String psName = psMap.getOrDefault("POWER_SUPPLY_NAME", name);
/* 113 */             String status = psMap.get("POWER_SUPPLY_STATUS");
/* 114 */             psCharging = "Charging".equals(status);
/* 115 */             psDischarging = "Discharging".equals(status);
/* 116 */             if (psMap.containsKey("POWER_SUPPLY_CAPACITY")) {
/* 117 */               psRemainingCapacityPercent = ParseUtil.parseIntOrDefault(psMap.get("POWER_SUPPLY_CAPACITY"), -100) / 100.0D;
/*     */             }
/*     */             
/* 120 */             if (psMap.containsKey("POWER_SUPPLY_ENERGY_NOW")) {
/* 121 */               psCurrentCapacity = ParseUtil.parseIntOrDefault(psMap.get("POWER_SUPPLY_ENERGY_NOW"), -1);
/* 122 */             } else if (psMap.containsKey("POWER_SUPPLY_CHARGE_NOW")) {
/* 123 */               psCurrentCapacity = ParseUtil.parseIntOrDefault(psMap.get("POWER_SUPPLY_CHARGE_NOW"), -1);
/*     */             } 
/* 125 */             if (psMap.containsKey("POWER_SUPPLY_ENERGY_FULL")) {
/* 126 */               psCurrentCapacity = ParseUtil.parseIntOrDefault(psMap.get("POWER_SUPPLY_ENERGY_FULL"), 1);
/* 127 */             } else if (psMap.containsKey("POWER_SUPPLY_CHARGE_FULL")) {
/* 128 */               psCurrentCapacity = ParseUtil.parseIntOrDefault(psMap.get("POWER_SUPPLY_CHARGE_FULL"), 1);
/*     */             } 
/* 130 */             if (psMap.containsKey("POWER_SUPPLY_ENERGY_FULL_DESIGN")) {
/* 131 */               psMaxCapacity = ParseUtil.parseIntOrDefault(psMap.get("POWER_SUPPLY_ENERGY_FULL_DESIGN"), 1);
/* 132 */             } else if (psMap.containsKey("POWER_SUPPLY_CHARGE_FULL_DESIGN")) {
/* 133 */               psMaxCapacity = ParseUtil.parseIntOrDefault(psMap.get("POWER_SUPPLY_CHARGE_FULL_DESIGN"), 1);
/*     */             } 
/* 135 */             if (psMap.containsKey("POWER_SUPPLY_VOLTAGE_NOW")) {
/* 136 */               psVoltage = ParseUtil.parseIntOrDefault(psMap.get("POWER_SUPPLY_VOLTAGE_NOW"), -1);
/*     */             }
/* 138 */             if (psMap.containsKey("POWER_SUPPLY_POWER_NOW")) {
/* 139 */               psPowerUsageRate = ParseUtil.parseIntOrDefault(psMap.get("POWER_SUPPLY_POWER_NOW"), -1);
/*     */             }
/* 141 */             if (psVoltage > 0.0D) {
/* 142 */               psAmperage = psPowerUsageRate / psVoltage;
/*     */             }
/* 144 */             if (psMap.containsKey("POWER_SUPPLY_CYCLE_COUNT")) {
/* 145 */               psCycleCount = ParseUtil.parseIntOrDefault(psMap.get("POWER_SUPPLY_CYCLE_COUNT"), -1);
/*     */             }
/* 147 */             String psChemistry = psMap.getOrDefault("POWER_SUPPLY_TECHNOLOGY", "unknown");
/* 148 */             String psDeviceName = psMap.getOrDefault("POWER_SUPPLY_MODEL_NAME", "unknown");
/* 149 */             String psManufacturer = psMap.getOrDefault("POWER_SUPPLY_MANUFACTURER", "unknown");
/* 150 */             String psSerialNumber = psMap.getOrDefault("POWER_SUPPLY_SERIAL_NUMBER", "unknown");
/* 151 */             if (ParseUtil.parseIntOrDefault(psMap.get("POWER_SUPPLY_PRESENT"), 1) > 0) {
/* 152 */               psList.add(new LinuxPowerSource(psName, psDeviceName, psRemainingCapacityPercent, psTimeRemainingEstimated, psTimeRemainingInstant, psPowerUsageRate, psVoltage, psAmperage, psPowerOnLine, psCharging, psDischarging, psCapacityUnits, psCurrentCapacity, psMaxCapacity, psDesignCapacity, psCycleCount, psChemistry, psManufactureDate, psManufacturer, psSerialNumber, psTemperature));
/*     */             }
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 161 */     return (List)Collections.unmodifiableList(psList);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platform\linux\LinuxPowerSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */