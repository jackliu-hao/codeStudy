package oshi.hardware.platform.mac;

import com.sun.jna.platform.mac.IOKit;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.common.AbstractSensors;
import oshi.util.platform.mac.SmcUtil;

@ThreadSafe
final class MacSensors extends AbstractSensors {
   private int numFans = 0;

   public double queryCpuTemperature() {
      IOKit.IOConnect conn = SmcUtil.smcOpen();
      double temp = SmcUtil.smcGetFloat(conn, "TC0P");
      SmcUtil.smcClose(conn);
      return temp > 0.0 ? temp : 0.0;
   }

   public int[] queryFanSpeeds() {
      IOKit.IOConnect conn = SmcUtil.smcOpen();
      if (this.numFans == 0) {
         this.numFans = (int)SmcUtil.smcGetLong(conn, "FNum");
      }

      int[] fanSpeeds = new int[this.numFans];

      for(int i = 0; i < this.numFans; ++i) {
         fanSpeeds[i] = (int)SmcUtil.smcGetFloat(conn, String.format("F%dAc", i));
      }

      SmcUtil.smcClose(conn);
      return fanSpeeds;
   }

   public double queryCpuVoltage() {
      IOKit.IOConnect conn = SmcUtil.smcOpen();
      double volts = SmcUtil.smcGetFloat(conn, "VC0C") / 1000.0;
      SmcUtil.smcClose(conn);
      return volts;
   }
}
