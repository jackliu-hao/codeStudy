/*     */ package oshi.hardware.platform.mac;
/*     */ 
/*     */ import com.sun.jna.Pointer;
/*     */ import com.sun.jna.PointerType;
/*     */ import com.sun.jna.platform.mac.CoreFoundation;
/*     */ import com.sun.jna.platform.mac.IOKit;
/*     */ import com.sun.jna.platform.mac.IOKitUtil;
/*     */ import java.time.LocalDate;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.hardware.PowerSource;
/*     */ import oshi.hardware.common.AbstractPowerSource;
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
/*     */ 
/*     */ 
/*     */ @ThreadSafe
/*     */ public final class MacPowerSource
/*     */   extends AbstractPowerSource
/*     */ {
/*  54 */   private static final CoreFoundation CF = CoreFoundation.INSTANCE;
/*  55 */   private static final IOKit IO = IOKit.INSTANCE;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MacPowerSource(String psName, String psDeviceName, double psRemainingCapacityPercent, double psTimeRemainingEstimated, double psTimeRemainingInstant, double psPowerUsageRate, double psVoltage, double psAmperage, boolean psPowerOnLine, boolean psCharging, boolean psDischarging, PowerSource.CapacityUnits psCapacityUnits, int psCurrentCapacity, int psMaxCapacity, int psDesignCapacity, int psCycleCount, String psChemistry, LocalDate psManufactureDate, String psManufacturer, String psSerialNumber, double psTemperature) {
/*  63 */     super(psName, psDeviceName, psRemainingCapacityPercent, psTimeRemainingEstimated, psTimeRemainingInstant, psPowerUsageRate, psVoltage, psAmperage, psPowerOnLine, psCharging, psDischarging, psCapacityUnits, psCurrentCapacity, psMaxCapacity, psDesignCapacity, psCycleCount, psChemistry, psManufactureDate, psManufacturer, psSerialNumber, psTemperature);
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
/*  75 */     String psDeviceName = "unknown";
/*  76 */     double psTimeRemainingInstant = 0.0D;
/*  77 */     double psPowerUsageRate = 0.0D;
/*  78 */     double psVoltage = -1.0D;
/*  79 */     double psAmperage = 0.0D;
/*  80 */     boolean psPowerOnLine = false;
/*  81 */     boolean psCharging = false;
/*  82 */     boolean psDischarging = false;
/*  83 */     PowerSource.CapacityUnits psCapacityUnits = PowerSource.CapacityUnits.RELATIVE;
/*  84 */     int psCurrentCapacity = 0;
/*  85 */     int psMaxCapacity = 1;
/*  86 */     int psDesignCapacity = 1;
/*  87 */     int psCycleCount = -1;
/*  88 */     String psChemistry = "unknown";
/*  89 */     LocalDate psManufactureDate = null;
/*  90 */     String psManufacturer = "unknown";
/*  91 */     String psSerialNumber = "unknown";
/*  92 */     double psTemperature = 0.0D;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 102 */     IOKit.IOService iOService = IOKitUtil.getMatchingService("AppleSmartBattery");
/* 103 */     if (iOService != null) {
/* 104 */       String s = iOService.getStringProperty("DeviceName");
/* 105 */       if (s != null) {
/* 106 */         psDeviceName = s;
/*     */       }
/* 108 */       s = iOService.getStringProperty("Manufacturer");
/* 109 */       if (s != null) {
/* 110 */         psManufacturer = s;
/*     */       }
/* 112 */       s = iOService.getStringProperty("BatterySerialNumber");
/* 113 */       if (s != null) {
/* 114 */         psSerialNumber = s;
/*     */       }
/*     */       
/* 117 */       Integer temp = iOService.getIntegerProperty("ManufactureDate");
/* 118 */       if (temp != null) {
/*     */ 
/*     */ 
/*     */         
/* 122 */         int day = temp.intValue() & 0x1F;
/* 123 */         int month = temp.intValue() >> 5 & 0xF;
/* 124 */         int year80 = temp.intValue() >> 9 & 0x7F;
/* 125 */         psManufactureDate = LocalDate.of(1980 + year80, month, day);
/*     */       } 
/*     */       
/* 128 */       temp = iOService.getIntegerProperty("DesignCapacity");
/* 129 */       if (temp != null) {
/* 130 */         psDesignCapacity = temp.intValue();
/*     */       }
/* 132 */       temp = iOService.getIntegerProperty("MaxCapacity");
/* 133 */       if (temp != null) {
/* 134 */         psMaxCapacity = temp.intValue();
/*     */       }
/* 136 */       temp = iOService.getIntegerProperty("CurrentCapacity");
/* 137 */       if (temp != null) {
/* 138 */         psCurrentCapacity = temp.intValue();
/*     */       }
/* 140 */       psCapacityUnits = PowerSource.CapacityUnits.MAH;
/*     */       
/* 142 */       temp = iOService.getIntegerProperty("TimeRemaining");
/* 143 */       if (temp != null) {
/* 144 */         psTimeRemainingInstant = temp.intValue() * 60.0D;
/*     */       }
/* 146 */       temp = iOService.getIntegerProperty("CycleCount");
/* 147 */       if (temp != null) {
/* 148 */         psCycleCount = temp.intValue();
/*     */       }
/* 150 */       temp = iOService.getIntegerProperty("Temperature");
/* 151 */       if (temp != null) {
/* 152 */         psTemperature = temp.intValue() / 100.0D;
/*     */       }
/* 154 */       temp = iOService.getIntegerProperty("Voltage");
/* 155 */       if (temp != null) {
/* 156 */         psVoltage = temp.intValue() / 1000.0D;
/*     */       }
/* 158 */       temp = iOService.getIntegerProperty("Amperage");
/* 159 */       if (temp != null) {
/* 160 */         psAmperage = temp.intValue();
/*     */       }
/* 162 */       psPowerUsageRate = psVoltage * psAmperage;
/*     */       
/* 164 */       Boolean bool = iOService.getBooleanProperty("ExternalConnected");
/* 165 */       if (bool != null) {
/* 166 */         psPowerOnLine = bool.booleanValue();
/*     */       }
/* 168 */       bool = iOService.getBooleanProperty("IsCharging");
/* 169 */       if (bool != null) {
/* 170 */         psCharging = bool.booleanValue();
/*     */       }
/* 172 */       psDischarging = !psCharging;
/*     */       
/* 174 */       iOService.release();
/*     */     } 
/*     */ 
/*     */     
/* 178 */     CoreFoundation.CFTypeRef powerSourcesInfo = IO.IOPSCopyPowerSourcesInfo();
/* 179 */     CoreFoundation.CFArrayRef powerSourcesList = IO.IOPSCopyPowerSourcesList(powerSourcesInfo);
/* 180 */     int powerSourcesCount = powerSourcesList.getCount();
/*     */ 
/*     */ 
/*     */     
/* 184 */     double psTimeRemainingEstimated = IO.IOPSGetTimeRemainingEstimate();
/*     */     
/* 186 */     CoreFoundation.CFStringRef nameKey = CoreFoundation.CFStringRef.createCFString("Name");
/* 187 */     CoreFoundation.CFStringRef isPresentKey = CoreFoundation.CFStringRef.createCFString("Is Present");
/* 188 */     CoreFoundation.CFStringRef currentCapacityKey = CoreFoundation.CFStringRef.createCFString("Current Capacity");
/* 189 */     CoreFoundation.CFStringRef maxCapacityKey = CoreFoundation.CFStringRef.createCFString("Max Capacity");
/*     */     
/* 191 */     List<MacPowerSource> psList = new ArrayList<>(powerSourcesCount);
/* 192 */     for (int ps = 0; ps < powerSourcesCount; ps++) {
/*     */       
/* 194 */       Pointer pwrSrcPtr = powerSourcesList.getValueAtIndex(ps);
/* 195 */       CoreFoundation.CFTypeRef powerSource = new CoreFoundation.CFTypeRef();
/* 196 */       powerSource.setPointer(pwrSrcPtr);
/* 197 */       CoreFoundation.CFDictionaryRef dictionary = IO.IOPSGetPowerSourceDescription(powerSourcesInfo, powerSource);
/*     */ 
/*     */ 
/*     */       
/* 201 */       Pointer result = dictionary.getValue((PointerType)isPresentKey);
/* 202 */       if (result != null) {
/* 203 */         CoreFoundation.CFBooleanRef isPresentRef = new CoreFoundation.CFBooleanRef(result);
/* 204 */         if (0 != CF.CFBooleanGetValue(isPresentRef)) {
/*     */           
/* 206 */           result = dictionary.getValue((PointerType)nameKey);
/* 207 */           CoreFoundation.CFStringRef cfName = new CoreFoundation.CFStringRef(result);
/* 208 */           String psName = cfName.stringValue();
/* 209 */           if (psName == null) {
/* 210 */             psName = "unknown";
/*     */           }
/*     */           
/* 213 */           double currentCapacity = 0.0D;
/* 214 */           if (dictionary.getValueIfPresent((PointerType)currentCapacityKey, null)) {
/* 215 */             result = dictionary.getValue((PointerType)currentCapacityKey);
/* 216 */             CoreFoundation.CFNumberRef cap = new CoreFoundation.CFNumberRef(result);
/* 217 */             currentCapacity = cap.intValue();
/*     */           } 
/* 219 */           double maxCapacity = 1.0D;
/* 220 */           if (dictionary.getValueIfPresent((PointerType)maxCapacityKey, null)) {
/* 221 */             result = dictionary.getValue((PointerType)maxCapacityKey);
/* 222 */             CoreFoundation.CFNumberRef cap = new CoreFoundation.CFNumberRef(result);
/* 223 */             maxCapacity = cap.intValue();
/*     */           } 
/* 225 */           double psRemainingCapacityPercent = Math.min(1.0D, currentCapacity / maxCapacity);
/*     */           
/* 227 */           psList.add(new MacPowerSource(psName, psDeviceName, psRemainingCapacityPercent, psTimeRemainingEstimated, psTimeRemainingInstant, psPowerUsageRate, psVoltage, psAmperage, psPowerOnLine, psCharging, psDischarging, psCapacityUnits, psCurrentCapacity, psMaxCapacity, psDesignCapacity, psCycleCount, psChemistry, psManufactureDate, psManufacturer, psSerialNumber, psTemperature));
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 235 */     isPresentKey.release();
/* 236 */     nameKey.release();
/* 237 */     currentCapacityKey.release();
/* 238 */     maxCapacityKey.release();
/*     */     
/* 240 */     powerSourcesList.release();
/* 241 */     powerSourcesInfo.release();
/*     */     
/* 243 */     return (List)Collections.unmodifiableList(psList);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platform\mac\MacPowerSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */