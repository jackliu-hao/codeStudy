package oshi.hardware.common;

import java.util.Arrays;
import java.util.function.Supplier;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.Sensors;
import oshi.util.Memoizer;

@ThreadSafe
public abstract class AbstractSensors implements Sensors {
   private final Supplier<Double> cpuTemperature = Memoizer.memoize(this::queryCpuTemperature, Memoizer.defaultExpiration());
   private final Supplier<int[]> fanSpeeds = Memoizer.memoize(this::queryFanSpeeds, Memoizer.defaultExpiration());
   private final Supplier<Double> cpuVoltage = Memoizer.memoize(this::queryCpuVoltage, Memoizer.defaultExpiration());

   public double getCpuTemperature() {
      return (Double)this.cpuTemperature.get();
   }

   protected abstract double queryCpuTemperature();

   public int[] getFanSpeeds() {
      return (int[])this.fanSpeeds.get();
   }

   protected abstract int[] queryFanSpeeds();

   public double getCpuVoltage() {
      return (Double)this.cpuVoltage.get();
   }

   protected abstract double queryCpuVoltage();

   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("CPU Temperature=").append(this.getCpuTemperature()).append("°C, ");
      sb.append("Fan Speeds=").append(Arrays.toString(this.getFanSpeeds())).append(", ");
      sb.append("CPU Voltage=").append(this.getCpuVoltage());
      return sb.toString();
   }
}
