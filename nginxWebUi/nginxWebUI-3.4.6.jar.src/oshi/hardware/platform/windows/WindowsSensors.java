/*     */ package oshi.hardware.platform.windows;
/*     */ 
/*     */ import com.sun.jna.platform.win32.COM.WbemcliUtil;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.driver.windows.wmi.MSAcpiThermalZoneTemperature;
/*     */ import oshi.driver.windows.wmi.OhmHardware;
/*     */ import oshi.driver.windows.wmi.OhmSensor;
/*     */ import oshi.driver.windows.wmi.Win32Fan;
/*     */ import oshi.driver.windows.wmi.Win32Processor;
/*     */ import oshi.hardware.common.AbstractSensors;
/*     */ import oshi.util.platform.windows.WmiUtil;
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
/*     */ final class WindowsSensors
/*     */   extends AbstractSensors
/*     */ {
/*  51 */   private static final Logger LOG = LoggerFactory.getLogger(WindowsSensors.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double queryCpuTemperature() {
/*  58 */     double tempC = getTempFromOHM();
/*  59 */     if (tempC > 0.0D) {
/*  60 */       return tempC;
/*     */     }
/*     */ 
/*     */     
/*  64 */     tempC = getTempFromWMI();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  69 */     return tempC;
/*     */   }
/*     */   
/*     */   private static double getTempFromOHM() {
/*  73 */     WbemcliUtil.WmiResult<OhmHardware.IdentifierProperty> ohmHardware = OhmHardware.queryHwIdentifier("Hardware", "CPU");
/*  74 */     if (ohmHardware.getResultCount() > 0) {
/*  75 */       LOG.debug("Found Temperature data in Open Hardware Monitor");
/*  76 */       String cpuIdentifier = WmiUtil.getString(ohmHardware, (Enum)OhmHardware.IdentifierProperty.IDENTIFIER, 0);
/*  77 */       if (cpuIdentifier.length() > 0) {
/*  78 */         WbemcliUtil.WmiResult<OhmSensor.ValueProperty> ohmSensors = OhmSensor.querySensorValue(cpuIdentifier, "Temperature");
/*  79 */         if (ohmSensors.getResultCount() > 0) {
/*  80 */           double sum = 0.0D;
/*  81 */           for (int i = 0; i < ohmSensors.getResultCount(); i++) {
/*  82 */             sum += WmiUtil.getFloat(ohmSensors, (Enum)OhmSensor.ValueProperty.VALUE, i);
/*     */           }
/*  84 */           return sum / ohmSensors.getResultCount();
/*     */         } 
/*     */       } 
/*     */     } 
/*  88 */     return 0.0D;
/*     */   }
/*     */   
/*     */   private static double getTempFromWMI() {
/*  92 */     double tempC = 0.0D;
/*  93 */     long tempK = 0L;
/*  94 */     WbemcliUtil.WmiResult<MSAcpiThermalZoneTemperature.TemperatureProperty> result = MSAcpiThermalZoneTemperature.queryCurrentTemperature();
/*  95 */     if (result.getResultCount() > 0) {
/*  96 */       LOG.debug("Found Temperature data in WMI");
/*  97 */       tempK = WmiUtil.getUint32asLong(result, (Enum)MSAcpiThermalZoneTemperature.TemperatureProperty.CURRENTTEMPERATURE, 0);
/*     */     } 
/*  99 */     if (tempK > 2732L) {
/* 100 */       tempC = tempK / 10.0D - 273.15D;
/* 101 */     } else if (tempK > 274L) {
/* 102 */       tempC = tempK - 273.0D;
/*     */     } 
/* 104 */     return (tempC < 0.0D) ? 0.0D : tempC;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int[] queryFanSpeeds() {
/* 110 */     int[] fanSpeeds = getFansFromOHM();
/* 111 */     if (fanSpeeds.length > 0) {
/* 112 */       return fanSpeeds;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 117 */     fanSpeeds = getFansFromWMI();
/* 118 */     if (fanSpeeds.length > 0) {
/* 119 */       return fanSpeeds;
/*     */     }
/*     */ 
/*     */     
/* 123 */     return new int[0];
/*     */   }
/*     */   
/*     */   private static int[] getFansFromOHM() {
/* 127 */     WbemcliUtil.WmiResult<OhmHardware.IdentifierProperty> ohmHardware = OhmHardware.queryHwIdentifier("Hardware", "CPU");
/* 128 */     if (ohmHardware.getResultCount() > 0) {
/* 129 */       LOG.debug("Found Fan data in Open Hardware Monitor");
/* 130 */       String cpuIdentifier = WmiUtil.getString(ohmHardware, (Enum)OhmHardware.IdentifierProperty.IDENTIFIER, 0);
/* 131 */       if (cpuIdentifier.length() > 0) {
/* 132 */         WbemcliUtil.WmiResult<OhmSensor.ValueProperty> ohmSensors = OhmSensor.querySensorValue(cpuIdentifier, "Fan");
/* 133 */         if (ohmSensors.getResultCount() > 0) {
/* 134 */           int[] fanSpeeds = new int[ohmSensors.getResultCount()];
/* 135 */           for (int i = 0; i < ohmSensors.getResultCount(); i++) {
/* 136 */             fanSpeeds[i] = (int)WmiUtil.getFloat(ohmSensors, (Enum)OhmSensor.ValueProperty.VALUE, i);
/*     */           }
/* 138 */           return fanSpeeds;
/*     */         } 
/*     */       } 
/*     */     } 
/* 142 */     return new int[0];
/*     */   }
/*     */   
/*     */   private static int[] getFansFromWMI() {
/* 146 */     WbemcliUtil.WmiResult<Win32Fan.SpeedProperty> fan = Win32Fan.querySpeed();
/* 147 */     if (fan.getResultCount() > 1) {
/* 148 */       LOG.debug("Found Fan data in WMI");
/* 149 */       int[] fanSpeeds = new int[fan.getResultCount()];
/* 150 */       for (int i = 0; i < fan.getResultCount(); i++) {
/* 151 */         fanSpeeds[i] = (int)WmiUtil.getUint64(fan, (Enum)Win32Fan.SpeedProperty.DESIREDSPEED, i);
/*     */       }
/* 153 */       return fanSpeeds;
/*     */     } 
/* 155 */     return new int[0];
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public double queryCpuVoltage() {
/* 161 */     double volts = getVoltsFromOHM();
/* 162 */     if (volts > 0.0D) {
/* 163 */       return volts;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 168 */     volts = getVoltsFromWMI();
/*     */     
/* 170 */     return volts;
/*     */   }
/*     */   
/*     */   private static double getVoltsFromOHM() {
/* 174 */     WbemcliUtil.WmiResult<OhmHardware.IdentifierProperty> ohmHardware = OhmHardware.queryHwIdentifier("Sensor", "Voltage");
/* 175 */     if (ohmHardware.getResultCount() > 0) {
/* 176 */       LOG.debug("Found Voltage data in Open Hardware Monitor");
/*     */       
/* 178 */       String cpuIdentifier = null;
/* 179 */       for (int i = 0; i < ohmHardware.getResultCount(); i++) {
/* 180 */         String id = WmiUtil.getString(ohmHardware, (Enum)OhmHardware.IdentifierProperty.IDENTIFIER, i);
/* 181 */         if (id.toLowerCase().contains("cpu")) {
/* 182 */           cpuIdentifier = id;
/*     */           
/*     */           break;
/*     */         } 
/*     */       } 
/* 187 */       if (cpuIdentifier == null) {
/* 188 */         cpuIdentifier = WmiUtil.getString(ohmHardware, (Enum)OhmHardware.IdentifierProperty.IDENTIFIER, 0);
/*     */       }
/*     */       
/* 191 */       WbemcliUtil.WmiResult<OhmSensor.ValueProperty> ohmSensors = OhmSensor.querySensorValue(cpuIdentifier, "Voltage");
/* 192 */       if (ohmSensors.getResultCount() > 0) {
/* 193 */         return WmiUtil.getFloat(ohmSensors, (Enum)OhmSensor.ValueProperty.VALUE, 0);
/*     */       }
/*     */     } 
/* 196 */     return 0.0D;
/*     */   }
/*     */   
/*     */   private static double getVoltsFromWMI() {
/* 200 */     WbemcliUtil.WmiResult<Win32Processor.VoltProperty> voltage = Win32Processor.queryVoltage();
/* 201 */     if (voltage.getResultCount() > 1) {
/* 202 */       LOG.debug("Found Voltage data in WMI");
/* 203 */       int decivolts = WmiUtil.getUint16(voltage, (Enum)Win32Processor.VoltProperty.CURRENTVOLTAGE, 0);
/*     */ 
/*     */ 
/*     */       
/* 207 */       if (decivolts > 0) {
/* 208 */         if ((decivolts & 0x80) == 0) {
/* 209 */           decivolts = WmiUtil.getUint32(voltage, (Enum)Win32Processor.VoltProperty.VOLTAGECAPS, 0);
/*     */           
/* 211 */           if ((decivolts & 0x1) > 0)
/* 212 */             return 5.0D; 
/* 213 */           if ((decivolts & 0x2) > 0)
/* 214 */             return 3.3D; 
/* 215 */           if ((decivolts & 0x4) > 0) {
/* 216 */             return 2.9D;
/*     */           }
/*     */         } else {
/*     */           
/* 220 */           return (decivolts & 0x7F) / 10.0D;
/*     */         } 
/*     */       }
/*     */     } 
/* 224 */     return 0.0D;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platform\windows\WindowsSensors.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */