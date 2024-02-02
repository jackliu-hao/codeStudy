package oshi.hardware.common;

import java.util.List;
import java.util.function.Supplier;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.CentralProcessor;
import oshi.hardware.ComputerSystem;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import oshi.hardware.Sensors;
import oshi.util.Memoizer;

@ThreadSafe
public abstract class AbstractHardwareAbstractionLayer implements HardwareAbstractionLayer {
   private final Supplier<ComputerSystem> computerSystem = Memoizer.memoize(this::createComputerSystem);
   private final Supplier<CentralProcessor> processor = Memoizer.memoize(this::createProcessor);
   private final Supplier<GlobalMemory> memory = Memoizer.memoize(this::createMemory);
   private final Supplier<Sensors> sensors = Memoizer.memoize(this::createSensors);

   public ComputerSystem getComputerSystem() {
      return (ComputerSystem)this.computerSystem.get();
   }

   protected abstract ComputerSystem createComputerSystem();

   public CentralProcessor getProcessor() {
      return (CentralProcessor)this.processor.get();
   }

   protected abstract CentralProcessor createProcessor();

   public GlobalMemory getMemory() {
      return (GlobalMemory)this.memory.get();
   }

   protected abstract GlobalMemory createMemory();

   public Sensors getSensors() {
      return (Sensors)this.sensors.get();
   }

   protected abstract Sensors createSensors();

   public List<NetworkIF> getNetworkIFs() {
      return this.getNetworkIFs(false);
   }
}
