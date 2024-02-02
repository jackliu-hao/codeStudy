package oshi.hardware.platform.unix.solaris;

import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.CentralProcessor;
import oshi.hardware.ComputerSystem;
import oshi.hardware.Display;
import oshi.hardware.GlobalMemory;
import oshi.hardware.GraphicsCard;
import oshi.hardware.HWDiskStore;
import oshi.hardware.NetworkIF;
import oshi.hardware.PowerSource;
import oshi.hardware.Sensors;
import oshi.hardware.SoundCard;
import oshi.hardware.UsbDevice;
import oshi.hardware.common.AbstractHardwareAbstractionLayer;

@ThreadSafe
public final class SolarisHardwareAbstractionLayer extends AbstractHardwareAbstractionLayer {
   public ComputerSystem createComputerSystem() {
      return new SolarisComputerSystem();
   }

   public GlobalMemory createMemory() {
      return new SolarisGlobalMemory();
   }

   public CentralProcessor createProcessor() {
      return new SolarisCentralProcessor();
   }

   public Sensors createSensors() {
      return new SolarisSensors();
   }

   public List<PowerSource> getPowerSources() {
      return SolarisPowerSource.getPowerSources();
   }

   public List<HWDiskStore> getDiskStores() {
      return SolarisHWDiskStore.getDisks();
   }

   public List<Display> getDisplays() {
      return SolarisDisplay.getDisplays();
   }

   public List<NetworkIF> getNetworkIFs(boolean includeLocalInterfaces) {
      return SolarisNetworkIF.getNetworks(includeLocalInterfaces);
   }

   public List<UsbDevice> getUsbDevices(boolean tree) {
      return SolarisUsbDevice.getUsbDevices(tree);
   }

   public List<SoundCard> getSoundCards() {
      return SolarisSoundCard.getSoundCards();
   }

   public List<GraphicsCard> getGraphicsCards() {
      return SolarisGraphicsCard.getGraphicsCards();
   }
}
