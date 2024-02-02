package oshi.hardware.platform.linux;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.common.AbstractSensors;
import oshi.util.ExecutingCommand;
import oshi.util.FileUtil;
import oshi.util.ParseUtil;

@ThreadSafe
final class LinuxSensors extends AbstractSensors {
   private static final String TEMP = "temp";
   private static final String FAN = "fan";
   private static final String VOLTAGE = "in";
   private static final String[] SENSORS = new String[]{"temp", "fan", "in"};
   private static final String HWMON = "hwmon";
   private static final String HWMON_PATH = "/sys/class/hwmon/hwmon";
   private static final String THERMAL_ZONE = "thermal_zone";
   private static final String THERMAL_ZONE_PATH = "/sys/class/thermal/thermal_zone";
   private static final boolean IS_PI = queryCpuTemperatureFromVcGenCmd() > 0.0;
   private final Map<String, String> sensorsMap = new HashMap();

   LinuxSensors() {
      if (!IS_PI) {
         this.populateSensorsMapFromHwmon();
         if (!this.sensorsMap.containsKey("temp")) {
            this.populateSensorsMapFromThermalZone();
         }
      }

   }

   private void populateSensorsMapFromHwmon() {
      String[] var1 = SENSORS;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         String sensor = var1[var3];
         this.getSensorFilesFromPath("/sys/class/hwmon/hwmon", sensor, (f) -> {
            try {
               return f.getName().startsWith(sensor) && f.getName().endsWith("_input") && FileUtil.getIntFromFile(f.getCanonicalPath()) > 0;
            } catch (IOException var3) {
               return false;
            }
         });
      }

   }

   private void populateSensorsMapFromThermalZone() {
      this.getSensorFilesFromPath("/sys/class/thermal/thermal_zone", "temp", (f) -> {
         return f.getName().equals("temp");
      });
   }

   private void getSensorFilesFromPath(String sensorPath, String sensor, FileFilter sensorFileFilter) {
      for(int i = 0; Paths.get(sensorPath + i).toFile().isDirectory(); ++i) {
         String path = sensorPath + i;
         File dir = new File(path);
         File[] matchingFiles = dir.listFiles(sensorFileFilter);
         if (matchingFiles != null && matchingFiles.length > 0) {
            this.sensorsMap.put(sensor, String.format("%s/%s", path, sensor));
         }
      }

   }

   public double queryCpuTemperature() {
      if (IS_PI) {
         return queryCpuTemperatureFromVcGenCmd();
      } else {
         String tempStr = (String)this.sensorsMap.get("temp");
         if (tempStr != null) {
            long millidegrees = 0L;
            if (tempStr.contains("hwmon")) {
               millidegrees = FileUtil.getLongFromFile(String.format("%s1_input", tempStr));
               if (millidegrees > 0L) {
                  return (double)millidegrees / 1000.0;
               }

               long sum = 0L;
               int count = 0;

               for(int i = 2; i <= 6; ++i) {
                  millidegrees = FileUtil.getLongFromFile(String.format("%s%d_input", tempStr, i));
                  if (millidegrees > 0L) {
                     sum += millidegrees;
                     ++count;
                  }
               }

               if (count > 0) {
                  return (double)sum / ((double)count * 1000.0);
               }
            } else if (tempStr.contains("thermal_zone")) {
               millidegrees = FileUtil.getLongFromFile(tempStr);
               if (millidegrees > 0L) {
                  return (double)millidegrees / 1000.0;
               }
            }
         }

         return 0.0;
      }
   }

   private static double queryCpuTemperatureFromVcGenCmd() {
      String tempStr = ExecutingCommand.getFirstAnswer("vcgencmd measure_temp");
      return tempStr.startsWith("temp=") ? ParseUtil.parseDoubleOrDefault(tempStr.replaceAll("[^\\d|\\.]+", ""), 0.0) : 0.0;
   }

   public int[] queryFanSpeeds() {
      if (!IS_PI) {
         String fanStr = (String)this.sensorsMap.get("fan");
         if (fanStr != null) {
            List<Integer> speeds = new ArrayList();
            int fan = 1;

            while(true) {
               String fanPath = String.format("%s%d_input", fanStr, fan);
               if (!(new File(fanPath)).exists()) {
                  int[] fanSpeeds = new int[speeds.size()];

                  for(int i = 0; i < speeds.size(); ++i) {
                     fanSpeeds[i] = (Integer)speeds.get(i);
                  }

                  return fanSpeeds;
               }

               speeds.add(FileUtil.getIntFromFile(fanPath));
               ++fan;
            }
         }
      }

      return new int[0];
   }

   public double queryCpuVoltage() {
      if (IS_PI) {
         return queryCpuVoltageFromVcGenCmd();
      } else {
         String voltageStr = (String)this.sensorsMap.get("in");
         return voltageStr != null ? (double)FileUtil.getIntFromFile(String.format("%s1_input", voltageStr)) / 1000.0 : 0.0;
      }
   }

   private static double queryCpuVoltageFromVcGenCmd() {
      String voltageStr = ExecutingCommand.getFirstAnswer("vcgencmd measure_volts core");
      return voltageStr.startsWith("volt=") ? ParseUtil.parseDoubleOrDefault(voltageStr.replaceAll("[^\\d|\\.]+", ""), 0.0) : 0.0;
   }
}
