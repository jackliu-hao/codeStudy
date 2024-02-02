/*     */ package oshi.hardware.platform.linux;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileFilter;
/*     */ import java.io.IOException;
/*     */ import java.nio.file.Paths;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.hardware.common.AbstractSensors;
/*     */ import oshi.util.ExecutingCommand;
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
/*     */ @ThreadSafe
/*     */ final class LinuxSensors
/*     */   extends AbstractSensors
/*     */ {
/*     */   private static final String TEMP = "temp";
/*     */   private static final String FAN = "fan";
/*     */   private static final String VOLTAGE = "in";
/*  51 */   private static final String[] SENSORS = new String[] { "temp", "fan", "in" };
/*     */   
/*     */   private static final String HWMON = "hwmon";
/*     */   
/*     */   private static final String HWMON_PATH = "/sys/class/hwmon/hwmon";
/*     */   
/*     */   private static final String THERMAL_ZONE = "thermal_zone";
/*     */   
/*     */   private static final String THERMAL_ZONE_PATH = "/sys/class/thermal/thermal_zone";
/*     */   
/*  61 */   private static final boolean IS_PI = (queryCpuTemperatureFromVcGenCmd() > 0.0D);
/*     */ 
/*     */   
/*  64 */   private final Map<String, String> sensorsMap = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   LinuxSensors() {
/*  72 */     if (!IS_PI) {
/*  73 */       populateSensorsMapFromHwmon();
/*     */       
/*  75 */       if (!this.sensorsMap.containsKey("temp")) {
/*  76 */         populateSensorsMapFromThermalZone();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void populateSensorsMapFromHwmon() {
/*  86 */     for (String sensor : SENSORS) {
/*     */       
/*  88 */       String sensorPrefix = sensor;
/*     */       
/*  90 */       getSensorFilesFromPath("/sys/class/hwmon/hwmon", sensor, f -> {
/*     */             try {
/*  92 */               return (f.getName().startsWith(sensorPrefix) && f.getName().endsWith("_input") && FileUtil.getIntFromFile(f.getCanonicalPath()) > 0);
/*     */             }
/*  94 */             catch (IOException e) {
/*     */               return false;
/*     */             } 
/*     */           });
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void populateSensorsMapFromThermalZone() {
/* 106 */     getSensorFilesFromPath("/sys/class/thermal/thermal_zone", "temp", f -> f.getName().equals("temp"));
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
/*     */   private void getSensorFilesFromPath(String sensorPath, String sensor, FileFilter sensorFileFilter) {
/* 120 */     int i = 0;
/* 121 */     while (Paths.get(sensorPath + i, new String[0]).toFile().isDirectory()) {
/* 122 */       String path = sensorPath + i;
/* 123 */       File dir = new File(path);
/* 124 */       File[] matchingFiles = dir.listFiles(sensorFileFilter);
/* 125 */       if (matchingFiles != null && matchingFiles.length > 0) {
/* 126 */         this.sensorsMap.put(sensor, String.format("%s/%s", new Object[] { path, sensor }));
/*     */       }
/* 128 */       i++;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public double queryCpuTemperature() {
/* 134 */     if (IS_PI) {
/* 135 */       return queryCpuTemperatureFromVcGenCmd();
/*     */     }
/* 137 */     String tempStr = this.sensorsMap.get("temp");
/* 138 */     if (tempStr != null) {
/* 139 */       long millidegrees = 0L;
/* 140 */       if (tempStr.contains("hwmon")) {
/*     */         
/* 142 */         millidegrees = FileUtil.getLongFromFile(String.format("%s1_input", new Object[] { tempStr }));
/*     */         
/* 144 */         if (millidegrees > 0L) {
/* 145 */           return millidegrees / 1000.0D;
/*     */         }
/*     */ 
/*     */         
/* 149 */         long sum = 0L;
/* 150 */         int count = 0;
/* 151 */         for (int i = 2; i <= 6; i++) {
/* 152 */           millidegrees = FileUtil.getLongFromFile(String.format("%s%d_input", new Object[] { tempStr, Integer.valueOf(i) }));
/* 153 */           if (millidegrees > 0L) {
/* 154 */             sum += millidegrees;
/* 155 */             count++;
/*     */           } 
/*     */         } 
/* 158 */         if (count > 0) {
/* 159 */           return sum / count * 1000.0D;
/*     */         }
/* 161 */       } else if (tempStr.contains("thermal_zone")) {
/*     */         
/* 163 */         millidegrees = FileUtil.getLongFromFile(tempStr);
/*     */         
/* 165 */         if (millidegrees > 0L) {
/* 166 */           return millidegrees / 1000.0D;
/*     */         }
/*     */       } 
/*     */     } 
/* 170 */     return 0.0D;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static double queryCpuTemperatureFromVcGenCmd() {
/* 179 */     String tempStr = ExecutingCommand.getFirstAnswer("vcgencmd measure_temp");
/*     */     
/* 181 */     if (tempStr.startsWith("temp=")) {
/* 182 */       return ParseUtil.parseDoubleOrDefault(tempStr.replaceAll("[^\\d|\\.]+", ""), 0.0D);
/*     */     }
/* 184 */     return 0.0D;
/*     */   }
/*     */ 
/*     */   
/*     */   public int[] queryFanSpeeds() {
/* 189 */     if (!IS_PI) {
/* 190 */       String fanStr = this.sensorsMap.get("fan");
/* 191 */       if (fanStr != null) {
/* 192 */         List<Integer> speeds = new ArrayList<>();
/* 193 */         int fan = 1;
/*     */         
/* 195 */         String fanPath = String.format("%s%d_input", new Object[] { fanStr, Integer.valueOf(fan) });
/* 196 */         while ((new File(fanPath)).exists()) {
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 201 */           speeds.add(Integer.valueOf(FileUtil.getIntFromFile(fanPath)));
/*     */           
/* 203 */           fan++;
/*     */         } 
/* 205 */         int[] fanSpeeds = new int[speeds.size()];
/* 206 */         for (int i = 0; i < speeds.size(); i++) {
/* 207 */           fanSpeeds[i] = ((Integer)speeds.get(i)).intValue();
/*     */         }
/* 209 */         return fanSpeeds;
/*     */       } 
/*     */     } 
/* 212 */     return new int[0];
/*     */   }
/*     */ 
/*     */   
/*     */   public double queryCpuVoltage() {
/* 217 */     if (IS_PI) {
/* 218 */       return queryCpuVoltageFromVcGenCmd();
/*     */     }
/* 220 */     String voltageStr = this.sensorsMap.get("in");
/* 221 */     if (voltageStr != null)
/*     */     {
/* 223 */       return FileUtil.getIntFromFile(String.format("%s1_input", new Object[] { voltageStr })) / 1000.0D;
/*     */     }
/* 225 */     return 0.0D;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static double queryCpuVoltageFromVcGenCmd() {
/* 235 */     String voltageStr = ExecutingCommand.getFirstAnswer("vcgencmd measure_volts core");
/*     */     
/* 237 */     if (voltageStr.startsWith("volt=")) {
/* 238 */       return ParseUtil.parseDoubleOrDefault(voltageStr.replaceAll("[^\\d|\\.]+", ""), 0.0D);
/*     */     }
/* 240 */     return 0.0D;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platform\linux\LinuxSensors.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */