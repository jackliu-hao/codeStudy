package oshi.hardware.platform.linux;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.PowerSource;
import oshi.hardware.common.AbstractPowerSource;
import oshi.util.FileUtil;
import oshi.util.ParseUtil;

@ThreadSafe
public final class LinuxPowerSource extends AbstractPowerSource {
   private static final String PS_PATH = "/sys/class/power_supply/";

   public LinuxPowerSource(String psName, String psDeviceName, double psRemainingCapacityPercent, double psTimeRemainingEstimated, double psTimeRemainingInstant, double psPowerUsageRate, double psVoltage, double psAmperage, boolean psPowerOnLine, boolean psCharging, boolean psDischarging, PowerSource.CapacityUnits psCapacityUnits, int psCurrentCapacity, int psMaxCapacity, int psDesignCapacity, int psCycleCount, String psChemistry, LocalDate psManufactureDate, String psManufacturer, String psSerialNumber, double psTemperature) {
      super(psName, psDeviceName, psRemainingCapacityPercent, psTimeRemainingEstimated, psTimeRemainingInstant, psPowerUsageRate, psVoltage, psAmperage, psPowerOnLine, psCharging, psDischarging, psCapacityUnits, psCurrentCapacity, psMaxCapacity, psDesignCapacity, psCycleCount, psChemistry, psManufactureDate, psManufacturer, psSerialNumber, psTemperature);
   }

   public static List<PowerSource> getPowerSources() {
      double psRemainingCapacityPercent = -1.0;
      double psTimeRemainingEstimated = -1.0;
      double psTimeRemainingInstant = -1.0;
      double psPowerUsageRate = 0.0;
      double psVoltage = -1.0;
      double psAmperage = 0.0;
      boolean psPowerOnLine = false;
      boolean psCharging = false;
      boolean psDischarging = false;
      PowerSource.CapacityUnits psCapacityUnits = PowerSource.CapacityUnits.RELATIVE;
      int psCurrentCapacity = -1;
      int psMaxCapacity = -1;
      int psDesignCapacity = -1;
      int psCycleCount = -1;
      LocalDate psManufactureDate = null;
      double psTemperature = 0.0;
      File f = new File("/sys/class/power_supply/");
      String[] psNames = f.list();
      List<LinuxPowerSource> psList = new ArrayList();
      if (psNames != null) {
         String[] var31 = psNames;
         int var32 = psNames.length;

         for(int var33 = 0; var33 < var32; ++var33) {
            String name = var31[var33];
            if (!name.startsWith("ADP") && !name.startsWith("AC")) {
               List<String> psInfo = FileUtil.readFile("/sys/class/power_supply/" + name + "/uevent", false);
               if (!psInfo.isEmpty()) {
                  Map<String, String> psMap = new HashMap();
                  Iterator var37 = psInfo.iterator();

                  while(var37.hasNext()) {
                     String line = (String)var37.next();
                     String[] split = line.split("=");
                     if (split.length > 1 && !split[1].isEmpty()) {
                        psMap.put(split[0], split[1]);
                     }
                  }

                  String psName = (String)psMap.getOrDefault("POWER_SUPPLY_NAME", name);
                  String status = (String)psMap.get("POWER_SUPPLY_STATUS");
                  psCharging = "Charging".equals(status);
                  psDischarging = "Discharging".equals(status);
                  if (psMap.containsKey("POWER_SUPPLY_CAPACITY")) {
                     psRemainingCapacityPercent = (double)ParseUtil.parseIntOrDefault((String)psMap.get("POWER_SUPPLY_CAPACITY"), -100) / 100.0;
                  }

                  if (psMap.containsKey("POWER_SUPPLY_ENERGY_NOW")) {
                     psCurrentCapacity = ParseUtil.parseIntOrDefault((String)psMap.get("POWER_SUPPLY_ENERGY_NOW"), -1);
                  } else if (psMap.containsKey("POWER_SUPPLY_CHARGE_NOW")) {
                     psCurrentCapacity = ParseUtil.parseIntOrDefault((String)psMap.get("POWER_SUPPLY_CHARGE_NOW"), -1);
                  }

                  if (psMap.containsKey("POWER_SUPPLY_ENERGY_FULL")) {
                     psCurrentCapacity = ParseUtil.parseIntOrDefault((String)psMap.get("POWER_SUPPLY_ENERGY_FULL"), 1);
                  } else if (psMap.containsKey("POWER_SUPPLY_CHARGE_FULL")) {
                     psCurrentCapacity = ParseUtil.parseIntOrDefault((String)psMap.get("POWER_SUPPLY_CHARGE_FULL"), 1);
                  }

                  if (psMap.containsKey("POWER_SUPPLY_ENERGY_FULL_DESIGN")) {
                     psMaxCapacity = ParseUtil.parseIntOrDefault((String)psMap.get("POWER_SUPPLY_ENERGY_FULL_DESIGN"), 1);
                  } else if (psMap.containsKey("POWER_SUPPLY_CHARGE_FULL_DESIGN")) {
                     psMaxCapacity = ParseUtil.parseIntOrDefault((String)psMap.get("POWER_SUPPLY_CHARGE_FULL_DESIGN"), 1);
                  }

                  if (psMap.containsKey("POWER_SUPPLY_VOLTAGE_NOW")) {
                     psVoltage = (double)ParseUtil.parseIntOrDefault((String)psMap.get("POWER_SUPPLY_VOLTAGE_NOW"), -1);
                  }

                  if (psMap.containsKey("POWER_SUPPLY_POWER_NOW")) {
                     psPowerUsageRate = (double)ParseUtil.parseIntOrDefault((String)psMap.get("POWER_SUPPLY_POWER_NOW"), -1);
                  }

                  if (psVoltage > 0.0) {
                     psAmperage = psPowerUsageRate / psVoltage;
                  }

                  if (psMap.containsKey("POWER_SUPPLY_CYCLE_COUNT")) {
                     psCycleCount = ParseUtil.parseIntOrDefault((String)psMap.get("POWER_SUPPLY_CYCLE_COUNT"), -1);
                  }

                  String psChemistry = (String)psMap.getOrDefault("POWER_SUPPLY_TECHNOLOGY", "unknown");
                  String psDeviceName = (String)psMap.getOrDefault("POWER_SUPPLY_MODEL_NAME", "unknown");
                  String psManufacturer = (String)psMap.getOrDefault("POWER_SUPPLY_MANUFACTURER", "unknown");
                  String psSerialNumber = (String)psMap.getOrDefault("POWER_SUPPLY_SERIAL_NUMBER", "unknown");
                  if (ParseUtil.parseIntOrDefault((String)psMap.get("POWER_SUPPLY_PRESENT"), 1) > 0) {
                     psList.add(new LinuxPowerSource(psName, psDeviceName, psRemainingCapacityPercent, psTimeRemainingEstimated, psTimeRemainingInstant, psPowerUsageRate, psVoltage, psAmperage, psPowerOnLine, psCharging, psDischarging, psCapacityUnits, psCurrentCapacity, psMaxCapacity, psDesignCapacity, psCycleCount, psChemistry, (LocalDate)psManufactureDate, psManufacturer, psSerialNumber, psTemperature));
                  }
               }
            }
         }
      }

      return Collections.unmodifiableList(psList);
   }
}
