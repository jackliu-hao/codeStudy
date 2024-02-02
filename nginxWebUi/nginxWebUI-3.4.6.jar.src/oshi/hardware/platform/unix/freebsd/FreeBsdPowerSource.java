/*     */ package oshi.hardware.platform.unix.freebsd;
/*     */ 
/*     */ import java.time.LocalDate;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.hardware.PowerSource;
/*     */ import oshi.hardware.common.AbstractPowerSource;
/*     */ import oshi.util.ExecutingCommand;
/*     */ import oshi.util.ParseUtil;
/*     */ import oshi.util.platform.unix.freebsd.BsdSysctlUtil;
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
/*     */ public final class FreeBsdPowerSource
/*     */   extends AbstractPowerSource
/*     */ {
/*     */   public FreeBsdPowerSource(String psName, String psDeviceName, double psRemainingCapacityPercent, double psTimeRemainingEstimated, double psTimeRemainingInstant, double psPowerUsageRate, double psVoltage, double psAmperage, boolean psPowerOnLine, boolean psCharging, boolean psDischarging, PowerSource.CapacityUnits psCapacityUnits, int psCurrentCapacity, int psMaxCapacity, int psDesignCapacity, int psCycleCount, String psChemistry, LocalDate psManufactureDate, String psManufacturer, String psSerialNumber, double psTemperature) {
/*  53 */     super(psName, psDeviceName, psRemainingCapacityPercent, psTimeRemainingEstimated, psTimeRemainingInstant, psPowerUsageRate, psVoltage, psAmperage, psPowerOnLine, psCharging, psDischarging, psCapacityUnits, psCurrentCapacity, psMaxCapacity, psDesignCapacity, psCycleCount, psChemistry, psManufactureDate, psManufacturer, psSerialNumber, psTemperature);
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
/*     */   public static List<PowerSource> getPowerSources() {
/*  65 */     return Collections.unmodifiableList((List)Arrays.asList((Object[])new FreeBsdPowerSource[] { getPowerSource("BAT0") }));
/*     */   }
/*     */   
/*     */   private static FreeBsdPowerSource getPowerSource(String name) {
/*  69 */     String psName = name;
/*  70 */     double psRemainingCapacityPercent = 1.0D;
/*  71 */     double psTimeRemainingEstimated = -1.0D;
/*  72 */     double psPowerUsageRate = 0.0D;
/*  73 */     int psVoltage = -1;
/*  74 */     double psAmperage = 0.0D;
/*  75 */     boolean psPowerOnLine = false;
/*  76 */     boolean psCharging = false;
/*  77 */     boolean psDischarging = false;
/*  78 */     PowerSource.CapacityUnits psCapacityUnits = PowerSource.CapacityUnits.RELATIVE;
/*  79 */     int psCurrentCapacity = 0;
/*  80 */     int psMaxCapacity = 1;
/*  81 */     int psDesignCapacity = 1;
/*  82 */     int psCycleCount = -1;
/*  83 */     LocalDate psManufactureDate = null;
/*     */     
/*  85 */     double psTemperature = 0.0D;
/*     */ 
/*     */     
/*  88 */     int state = BsdSysctlUtil.sysctl("hw.acpi.battery.state", 0);
/*  89 */     if (state == 2) {
/*  90 */       psCharging = true;
/*     */     } else {
/*  92 */       int i = BsdSysctlUtil.sysctl("hw.acpi.battery.time", -1);
/*     */       
/*  94 */       psTimeRemainingEstimated = (i < 0) ? -1.0D : (60.0D * i);
/*  95 */       if (state == 1) {
/*  96 */         psDischarging = true;
/*     */       }
/*     */     } 
/*     */     
/* 100 */     int life = BsdSysctlUtil.sysctl("hw.acpi.battery.life", -1);
/* 101 */     if (life > 0) {
/* 102 */       psRemainingCapacityPercent = life / 100.0D;
/*     */     }
/* 104 */     List<String> acpiconf = ExecutingCommand.runNative("acpiconf -i 0");
/* 105 */     Map<String, String> psMap = new HashMap<>();
/* 106 */     for (String line : acpiconf) {
/* 107 */       String[] split = line.split(":", 2);
/* 108 */       if (split.length > 1) {
/* 109 */         String value = split[1].trim();
/* 110 */         if (!value.isEmpty()) {
/* 111 */           psMap.put(split[0], value);
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 116 */     String psDeviceName = psMap.getOrDefault("Model number", "unknown");
/* 117 */     String psSerialNumber = psMap.getOrDefault("Serial number", "unknown");
/* 118 */     String psChemistry = psMap.getOrDefault("Type", "unknown");
/* 119 */     String psManufacturer = psMap.getOrDefault("OEM info", "unknown");
/* 120 */     String cap = psMap.get("Design capacity");
/* 121 */     if (cap != null) {
/* 122 */       psDesignCapacity = ParseUtil.getFirstIntValue(cap);
/* 123 */       if (cap.toLowerCase().contains("mah")) {
/* 124 */         psCapacityUnits = PowerSource.CapacityUnits.MAH;
/* 125 */       } else if (cap.toLowerCase().contains("mwh")) {
/* 126 */         psCapacityUnits = PowerSource.CapacityUnits.MWH;
/*     */       } 
/*     */     } 
/* 129 */     cap = psMap.get("Last full capacity");
/* 130 */     if (cap != null) {
/* 131 */       psMaxCapacity = ParseUtil.getFirstIntValue(cap);
/*     */     } else {
/* 133 */       psMaxCapacity = psDesignCapacity;
/*     */     } 
/* 135 */     double psTimeRemainingInstant = psTimeRemainingEstimated;
/* 136 */     String time = psMap.get("Remaining time");
/* 137 */     if (time != null) {
/* 138 */       String[] hhmm = time.split(":");
/* 139 */       if (hhmm.length == 2)
/*     */       {
/* 141 */         psTimeRemainingInstant = 3600.0D * ParseUtil.parseIntOrDefault(hhmm[0], 0) + 60.0D * ParseUtil.parseIntOrDefault(hhmm[1], 0);
/*     */       }
/*     */     } 
/* 144 */     String rate = psMap.get("Present rate");
/* 145 */     if (rate != null) {
/* 146 */       psPowerUsageRate = ParseUtil.getFirstIntValue(rate);
/*     */     }
/* 148 */     String volts = psMap.get("Present voltage");
/* 149 */     if (volts != null) {
/* 150 */       psVoltage = ParseUtil.getFirstIntValue(volts);
/* 151 */       if (psVoltage != 0) {
/* 152 */         psAmperage = psPowerUsageRate / psVoltage;
/*     */       }
/*     */     } 
/*     */     
/* 156 */     return new FreeBsdPowerSource(psName, psDeviceName, psRemainingCapacityPercent, psTimeRemainingEstimated, psTimeRemainingInstant, psPowerUsageRate, psVoltage, psAmperage, psPowerOnLine, psCharging, psDischarging, psCapacityUnits, psCurrentCapacity, psMaxCapacity, psDesignCapacity, psCycleCount, psChemistry, psManufactureDate, psManufacturer, psSerialNumber, psTemperature);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platfor\\unix\freebsd\FreeBsdPowerSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */