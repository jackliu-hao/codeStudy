package oshi.hardware.platform.unix.solaris;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.common.AbstractSensors;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@ThreadSafe
final class SolarisSensors extends AbstractSensors {
   public double queryCpuTemperature() {
      double maxTemp = 0.0;
      Iterator var3 = ExecutingCommand.runNative("/usr/sbin/prtpicl -v -c temperature-sensor").iterator();

      while(var3.hasNext()) {
         String line = (String)var3.next();
         if (line.trim().startsWith("Temperature:")) {
            int temp = ParseUtil.parseLastInt(line, 0);
            if ((double)temp > maxTemp) {
               maxTemp = (double)temp;
            }
         }
      }

      if (maxTemp > 1000.0) {
         maxTemp /= 1000.0;
      }

      return maxTemp;
   }

   public int[] queryFanSpeeds() {
      List<Integer> speedList = new ArrayList();
      Iterator var2 = ExecutingCommand.runNative("/usr/sbin/prtpicl -v -c fan").iterator();

      while(var2.hasNext()) {
         String line = (String)var2.next();
         if (line.trim().startsWith("Speed:")) {
            speedList.add(ParseUtil.parseLastInt(line, 0));
         }
      }

      int[] fans = new int[speedList.size()];

      for(int i = 0; i < speedList.size(); ++i) {
         fans[i] = (Integer)speedList.get(i);
      }

      return fans;
   }

   public double queryCpuVoltage() {
      double voltage = 0.0;
      Iterator var3 = ExecutingCommand.runNative("/usr/sbin/prtpicl -v -c voltage-sensor").iterator();

      while(var3.hasNext()) {
         String line = (String)var3.next();
         if (line.trim().startsWith("Voltage:")) {
            voltage = ParseUtil.parseDoubleOrDefault(line.replace("Voltage:", "").trim(), 0.0);
            break;
         }
      }

      return voltage;
   }
}
