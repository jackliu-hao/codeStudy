/*     */ package oshi.hardware.common;
/*     */ 
/*     */ import com.sun.jna.Platform;
/*     */ import java.time.LocalDate;
/*     */ import java.util.List;
/*     */ import oshi.PlatformEnum;
/*     */ import oshi.SystemInfo;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.hardware.PowerSource;
/*     */ import oshi.hardware.platform.linux.LinuxPowerSource;
/*     */ import oshi.hardware.platform.mac.MacPowerSource;
/*     */ import oshi.hardware.platform.unix.aix.AixPowerSource;
/*     */ import oshi.hardware.platform.unix.freebsd.FreeBsdPowerSource;
/*     */ import oshi.hardware.platform.unix.solaris.SolarisPowerSource;
/*     */ import oshi.hardware.platform.windows.WindowsPowerSource;
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
/*     */ public abstract class AbstractPowerSource
/*     */   implements PowerSource
/*     */ {
/*     */   private String name;
/*     */   private String deviceName;
/*     */   private double remainingCapacityPercent;
/*     */   private double timeRemainingEstimated;
/*     */   private double timeRemainingInstant;
/*     */   private double powerUsageRate;
/*     */   private double voltage;
/*     */   private double amperage;
/*     */   private boolean powerOnLine;
/*     */   private boolean charging;
/*     */   private boolean discharging;
/*     */   private PowerSource.CapacityUnits capacityUnits;
/*     */   private int currentCapacity;
/*     */   private int maxCapacity;
/*     */   private int designCapacity;
/*     */   private int cycleCount;
/*     */   private String chemistry;
/*     */   private LocalDate manufactureDate;
/*     */   private String manufacturer;
/*     */   private String serialNumber;
/*     */   private double temperature;
/*     */   
/*     */   protected AbstractPowerSource(String name, String deviceName, double remainingCapacityPercent, double timeRemainingEstimated, double timeRemainingInstant, double powerUsageRate, double voltage, double amperage, boolean powerOnLine, boolean charging, boolean discharging, PowerSource.CapacityUnits capacityUnits, int currentCapacity, int maxCapacity, int designCapacity, int cycleCount, String chemistry, LocalDate manufactureDate, String manufacturer, String serialNumber, double temperature) {
/*  76 */     this.name = name;
/*  77 */     this.deviceName = deviceName;
/*  78 */     this.remainingCapacityPercent = remainingCapacityPercent;
/*  79 */     this.timeRemainingEstimated = timeRemainingEstimated;
/*  80 */     this.timeRemainingInstant = timeRemainingInstant;
/*  81 */     this.powerUsageRate = powerUsageRate;
/*  82 */     this.voltage = voltage;
/*  83 */     this.amperage = amperage;
/*  84 */     this.powerOnLine = powerOnLine;
/*  85 */     this.charging = charging;
/*  86 */     this.discharging = discharging;
/*  87 */     this.capacityUnits = capacityUnits;
/*  88 */     this.currentCapacity = currentCapacity;
/*  89 */     this.maxCapacity = maxCapacity;
/*  90 */     this.designCapacity = designCapacity;
/*  91 */     this.cycleCount = cycleCount;
/*  92 */     this.chemistry = chemistry;
/*  93 */     this.manufactureDate = manufactureDate;
/*  94 */     this.manufacturer = manufacturer;
/*  95 */     this.serialNumber = serialNumber;
/*  96 */     this.temperature = temperature;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 101 */     return this.name;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getDeviceName() {
/* 106 */     return this.deviceName;
/*     */   }
/*     */ 
/*     */   
/*     */   public double getRemainingCapacityPercent() {
/* 111 */     return this.remainingCapacityPercent;
/*     */   }
/*     */ 
/*     */   
/*     */   public double getTimeRemainingEstimated() {
/* 116 */     return this.timeRemainingEstimated;
/*     */   }
/*     */ 
/*     */   
/*     */   public double getTimeRemainingInstant() {
/* 121 */     return this.timeRemainingInstant;
/*     */   }
/*     */ 
/*     */   
/*     */   public double getPowerUsageRate() {
/* 126 */     return this.powerUsageRate;
/*     */   }
/*     */ 
/*     */   
/*     */   public double getVoltage() {
/* 131 */     return this.voltage;
/*     */   }
/*     */ 
/*     */   
/*     */   public double getAmperage() {
/* 136 */     return this.amperage;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isPowerOnLine() {
/* 141 */     return this.powerOnLine;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isCharging() {
/* 146 */     return this.charging;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isDischarging() {
/* 151 */     return this.discharging;
/*     */   }
/*     */ 
/*     */   
/*     */   public PowerSource.CapacityUnits getCapacityUnits() {
/* 156 */     return this.capacityUnits;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getCurrentCapacity() {
/* 161 */     return this.currentCapacity;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getMaxCapacity() {
/* 166 */     return this.maxCapacity;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getDesignCapacity() {
/* 171 */     return this.designCapacity;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getCycleCount() {
/* 176 */     return this.cycleCount;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getChemistry() {
/* 181 */     return this.chemistry;
/*     */   }
/*     */ 
/*     */   
/*     */   public LocalDate getManufactureDate() {
/* 186 */     return this.manufactureDate;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getManufacturer() {
/* 191 */     return this.manufacturer;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getSerialNumber() {
/* 196 */     return this.serialNumber;
/*     */   }
/*     */ 
/*     */   
/*     */   public double getTemperature() {
/* 201 */     return this.temperature;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean updateAttributes() {
/* 206 */     List<PowerSource> psArr = getPowerSources();
/* 207 */     for (PowerSource ps : psArr) {
/* 208 */       if (ps.getName().equals(this.name)) {
/* 209 */         this.name = ps.getName();
/* 210 */         this.deviceName = ps.getDeviceName();
/* 211 */         this.remainingCapacityPercent = ps.getRemainingCapacityPercent();
/* 212 */         this.timeRemainingEstimated = ps.getTimeRemainingEstimated();
/* 213 */         this.timeRemainingInstant = ps.getTimeRemainingInstant();
/* 214 */         this.powerUsageRate = ps.getPowerUsageRate();
/* 215 */         this.voltage = ps.getVoltage();
/* 216 */         this.amperage = ps.getAmperage();
/* 217 */         this.powerOnLine = ps.isPowerOnLine();
/* 218 */         this.charging = ps.isCharging();
/* 219 */         this.discharging = ps.isDischarging();
/* 220 */         this.capacityUnits = ps.getCapacityUnits();
/* 221 */         this.currentCapacity = ps.getCurrentCapacity();
/* 222 */         this.maxCapacity = ps.getMaxCapacity();
/* 223 */         this.designCapacity = ps.getDesignCapacity();
/* 224 */         this.cycleCount = ps.getCycleCount();
/* 225 */         this.chemistry = ps.getChemistry();
/* 226 */         this.manufactureDate = ps.getManufactureDate();
/* 227 */         this.manufacturer = ps.getManufacturer();
/* 228 */         this.serialNumber = ps.getSerialNumber();
/* 229 */         this.temperature = ps.getTemperature();
/* 230 */         return true;
/*     */       } 
/*     */     } 
/*     */     
/* 234 */     return false;
/*     */   }
/*     */   
/*     */   private static List<PowerSource> getPowerSources() {
/* 238 */     switch (SystemInfo.getCurrentPlatformEnum()) {
/*     */       case WINDOWS:
/* 240 */         return WindowsPowerSource.getPowerSources();
/*     */       case MACOSX:
/* 242 */         return MacPowerSource.getPowerSources();
/*     */       case LINUX:
/* 244 */         return LinuxPowerSource.getPowerSources();
/*     */       case SOLARIS:
/* 246 */         return SolarisPowerSource.getPowerSources();
/*     */       case FREEBSD:
/* 248 */         return FreeBsdPowerSource.getPowerSources();
/*     */       case AIX:
/* 250 */         return AixPowerSource.getPowerSources();
/*     */     } 
/* 252 */     throw new UnsupportedOperationException("Operating system not supported: " + Platform.getOSType());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 258 */     StringBuilder sb = new StringBuilder();
/* 259 */     sb.append("Name: ").append(getName()).append(", ");
/* 260 */     sb.append("Device Name: ").append(getDeviceName()).append(",\n ");
/* 261 */     sb.append("RemainingCapacityPercent: ").append(getRemainingCapacityPercent() * 100.0D).append("%, ");
/* 262 */     sb.append("Time Remaining: ").append(formatTimeRemaining(getTimeRemainingEstimated())).append(", ");
/* 263 */     sb.append("Time Remaining Instant: ").append(formatTimeRemaining(getTimeRemainingInstant())).append(",\n ");
/* 264 */     sb.append("Power Usage Rate: ").append(getPowerUsageRate()).append("mW, ");
/* 265 */     sb.append("Voltage: ").append(getVoltage()).append("V, ");
/* 266 */     sb.append("Amperage: ").append(getAmperage()).append("mA,\n ");
/* 267 */     sb.append("Power OnLine: ").append(isPowerOnLine()).append(", ");
/* 268 */     sb.append("Charging: ").append(isCharging()).append(", ");
/* 269 */     sb.append("Discharging: ").append(isDischarging()).append(",\n ");
/* 270 */     sb.append("Capacity Units: ").append(getCapacityUnits()).append(", ");
/* 271 */     sb.append("Current Capacity: ").append(getCurrentCapacity()).append(", ");
/* 272 */     sb.append("Max Capacity: ").append(getMaxCapacity()).append(", ");
/* 273 */     sb.append("Design Capacity: ").append(getDesignCapacity()).append(",\n ");
/* 274 */     sb.append("Cycle Count: ").append(getCycleCount()).append(", ");
/* 275 */     sb.append("Chemistry: ").append(getChemistry()).append(", ");
/* 276 */     sb.append("Manufacture Date: ").append((getManufactureDate() != null) ? getManufactureDate() : "unknown")
/* 277 */       .append(", ");
/* 278 */     sb.append("Manufacturer: ").append(getManufacturer()).append(",\n ");
/* 279 */     sb.append("SerialNumber: ").append(getSerialNumber()).append(", ");
/* 280 */     sb.append("Temperature: ");
/* 281 */     if (getTemperature() > 0.0D) {
/* 282 */       sb.append(getTemperature()).append("°C");
/*     */     } else {
/* 284 */       sb.append("unknown");
/*     */     } 
/* 286 */     return sb.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String formatTimeRemaining(double timeInSeconds) {
/*     */     String formattedTimeRemaining;
/* 298 */     if (timeInSeconds < -1.5D) {
/* 299 */       formattedTimeRemaining = "Charging";
/* 300 */     } else if (timeInSeconds < 0.0D) {
/* 301 */       formattedTimeRemaining = "Unknown";
/*     */     } else {
/* 303 */       int hours = (int)(timeInSeconds / 3600.0D);
/* 304 */       int minutes = (int)(timeInSeconds % 3600.0D / 60.0D);
/* 305 */       formattedTimeRemaining = String.format("%d:%02d", new Object[] { Integer.valueOf(hours), Integer.valueOf(minutes) });
/*     */     } 
/* 307 */     return formattedTimeRemaining;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\common\AbstractPowerSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */