package oshi.hardware.common;

import com.sun.jna.Platform;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import oshi.SystemInfo;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.PowerSource;
import oshi.hardware.platform.linux.LinuxPowerSource;
import oshi.hardware.platform.mac.MacPowerSource;
import oshi.hardware.platform.unix.aix.AixPowerSource;
import oshi.hardware.platform.unix.freebsd.FreeBsdPowerSource;
import oshi.hardware.platform.unix.solaris.SolarisPowerSource;
import oshi.hardware.platform.windows.WindowsPowerSource;

@ThreadSafe
public abstract class AbstractPowerSource implements PowerSource {
   private String name;
   private String deviceName;
   private double remainingCapacityPercent;
   private double timeRemainingEstimated;
   private double timeRemainingInstant;
   private double powerUsageRate;
   private double voltage;
   private double amperage;
   private boolean powerOnLine;
   private boolean charging;
   private boolean discharging;
   private PowerSource.CapacityUnits capacityUnits;
   private int currentCapacity;
   private int maxCapacity;
   private int designCapacity;
   private int cycleCount;
   private String chemistry;
   private LocalDate manufactureDate;
   private String manufacturer;
   private String serialNumber;
   private double temperature;

   protected AbstractPowerSource(String name, String deviceName, double remainingCapacityPercent, double timeRemainingEstimated, double timeRemainingInstant, double powerUsageRate, double voltage, double amperage, boolean powerOnLine, boolean charging, boolean discharging, PowerSource.CapacityUnits capacityUnits, int currentCapacity, int maxCapacity, int designCapacity, int cycleCount, String chemistry, LocalDate manufactureDate, String manufacturer, String serialNumber, double temperature) {
      this.name = name;
      this.deviceName = deviceName;
      this.remainingCapacityPercent = remainingCapacityPercent;
      this.timeRemainingEstimated = timeRemainingEstimated;
      this.timeRemainingInstant = timeRemainingInstant;
      this.powerUsageRate = powerUsageRate;
      this.voltage = voltage;
      this.amperage = amperage;
      this.powerOnLine = powerOnLine;
      this.charging = charging;
      this.discharging = discharging;
      this.capacityUnits = capacityUnits;
      this.currentCapacity = currentCapacity;
      this.maxCapacity = maxCapacity;
      this.designCapacity = designCapacity;
      this.cycleCount = cycleCount;
      this.chemistry = chemistry;
      this.manufactureDate = manufactureDate;
      this.manufacturer = manufacturer;
      this.serialNumber = serialNumber;
      this.temperature = temperature;
   }

   public String getName() {
      return this.name;
   }

   public String getDeviceName() {
      return this.deviceName;
   }

   public double getRemainingCapacityPercent() {
      return this.remainingCapacityPercent;
   }

   public double getTimeRemainingEstimated() {
      return this.timeRemainingEstimated;
   }

   public double getTimeRemainingInstant() {
      return this.timeRemainingInstant;
   }

   public double getPowerUsageRate() {
      return this.powerUsageRate;
   }

   public double getVoltage() {
      return this.voltage;
   }

   public double getAmperage() {
      return this.amperage;
   }

   public boolean isPowerOnLine() {
      return this.powerOnLine;
   }

   public boolean isCharging() {
      return this.charging;
   }

   public boolean isDischarging() {
      return this.discharging;
   }

   public PowerSource.CapacityUnits getCapacityUnits() {
      return this.capacityUnits;
   }

   public int getCurrentCapacity() {
      return this.currentCapacity;
   }

   public int getMaxCapacity() {
      return this.maxCapacity;
   }

   public int getDesignCapacity() {
      return this.designCapacity;
   }

   public int getCycleCount() {
      return this.cycleCount;
   }

   public String getChemistry() {
      return this.chemistry;
   }

   public LocalDate getManufactureDate() {
      return this.manufactureDate;
   }

   public String getManufacturer() {
      return this.manufacturer;
   }

   public String getSerialNumber() {
      return this.serialNumber;
   }

   public double getTemperature() {
      return this.temperature;
   }

   public boolean updateAttributes() {
      List<PowerSource> psArr = getPowerSources();
      Iterator var2 = psArr.iterator();

      PowerSource ps;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         ps = (PowerSource)var2.next();
      } while(!ps.getName().equals(this.name));

      this.name = ps.getName();
      this.deviceName = ps.getDeviceName();
      this.remainingCapacityPercent = ps.getRemainingCapacityPercent();
      this.timeRemainingEstimated = ps.getTimeRemainingEstimated();
      this.timeRemainingInstant = ps.getTimeRemainingInstant();
      this.powerUsageRate = ps.getPowerUsageRate();
      this.voltage = ps.getVoltage();
      this.amperage = ps.getAmperage();
      this.powerOnLine = ps.isPowerOnLine();
      this.charging = ps.isCharging();
      this.discharging = ps.isDischarging();
      this.capacityUnits = ps.getCapacityUnits();
      this.currentCapacity = ps.getCurrentCapacity();
      this.maxCapacity = ps.getMaxCapacity();
      this.designCapacity = ps.getDesignCapacity();
      this.cycleCount = ps.getCycleCount();
      this.chemistry = ps.getChemistry();
      this.manufactureDate = ps.getManufactureDate();
      this.manufacturer = ps.getManufacturer();
      this.serialNumber = ps.getSerialNumber();
      this.temperature = ps.getTemperature();
      return true;
   }

   private static List<PowerSource> getPowerSources() {
      switch (SystemInfo.getCurrentPlatformEnum()) {
         case WINDOWS:
            return WindowsPowerSource.getPowerSources();
         case MACOSX:
            return MacPowerSource.getPowerSources();
         case LINUX:
            return LinuxPowerSource.getPowerSources();
         case SOLARIS:
            return SolarisPowerSource.getPowerSources();
         case FREEBSD:
            return FreeBsdPowerSource.getPowerSources();
         case AIX:
            return AixPowerSource.getPowerSources();
         default:
            throw new UnsupportedOperationException("Operating system not supported: " + Platform.getOSType());
      }
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("Name: ").append(this.getName()).append(", ");
      sb.append("Device Name: ").append(this.getDeviceName()).append(",\n ");
      sb.append("RemainingCapacityPercent: ").append(this.getRemainingCapacityPercent() * 100.0).append("%, ");
      sb.append("Time Remaining: ").append(formatTimeRemaining(this.getTimeRemainingEstimated())).append(", ");
      sb.append("Time Remaining Instant: ").append(formatTimeRemaining(this.getTimeRemainingInstant())).append(",\n ");
      sb.append("Power Usage Rate: ").append(this.getPowerUsageRate()).append("mW, ");
      sb.append("Voltage: ").append(this.getVoltage()).append("V, ");
      sb.append("Amperage: ").append(this.getAmperage()).append("mA,\n ");
      sb.append("Power OnLine: ").append(this.isPowerOnLine()).append(", ");
      sb.append("Charging: ").append(this.isCharging()).append(", ");
      sb.append("Discharging: ").append(this.isDischarging()).append(",\n ");
      sb.append("Capacity Units: ").append(this.getCapacityUnits()).append(", ");
      sb.append("Current Capacity: ").append(this.getCurrentCapacity()).append(", ");
      sb.append("Max Capacity: ").append(this.getMaxCapacity()).append(", ");
      sb.append("Design Capacity: ").append(this.getDesignCapacity()).append(",\n ");
      sb.append("Cycle Count: ").append(this.getCycleCount()).append(", ");
      sb.append("Chemistry: ").append(this.getChemistry()).append(", ");
      sb.append("Manufacture Date: ").append(this.getManufactureDate() != null ? this.getManufactureDate() : "unknown").append(", ");
      sb.append("Manufacturer: ").append(this.getManufacturer()).append(",\n ");
      sb.append("SerialNumber: ").append(this.getSerialNumber()).append(", ");
      sb.append("Temperature: ");
      if (this.getTemperature() > 0.0) {
         sb.append(this.getTemperature()).append("°C");
      } else {
         sb.append("unknown");
      }

      return sb.toString();
   }

   private static String formatTimeRemaining(double timeInSeconds) {
      String formattedTimeRemaining;
      if (timeInSeconds < -1.5) {
         formattedTimeRemaining = "Charging";
      } else if (timeInSeconds < 0.0) {
         formattedTimeRemaining = "Unknown";
      } else {
         int hours = (int)(timeInSeconds / 3600.0);
         int minutes = (int)(timeInSeconds % 3600.0 / 60.0);
         formattedTimeRemaining = String.format("%d:%02d", hours, minutes);
      }

      return formattedTimeRemaining;
   }
}
