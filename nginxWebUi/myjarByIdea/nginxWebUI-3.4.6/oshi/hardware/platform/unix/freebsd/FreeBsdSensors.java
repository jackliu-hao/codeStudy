package oshi.hardware.platform.unix.freebsd;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.common.AbstractSensors;
import oshi.jna.platform.unix.freebsd.FreeBsdLibc;

@ThreadSafe
final class FreeBsdSensors extends AbstractSensors {
   public double queryCpuTemperature() {
      return queryKldloadCoretemp();
   }

   private static double queryKldloadCoretemp() {
      String name = "dev.cpu.%d.temperature";
      IntByReference size = new IntByReference(FreeBsdLibc.INT_SIZE);
      Pointer p = new Memory((long)size.getValue());
      int cpu = 0;

      double sumTemp;
      for(sumTemp = 0.0; 0 == FreeBsdLibc.INSTANCE.sysctlbyname(String.format(name, cpu), p, size, (Pointer)null, 0); ++cpu) {
         sumTemp += (double)p.getInt(0L) / 10.0 - 273.15;
      }

      return cpu > 0 ? sumTemp / (double)cpu : Double.NaN;
   }

   public int[] queryFanSpeeds() {
      return new int[0];
   }

   public double queryCpuVoltage() {
      return 0.0;
   }
}
