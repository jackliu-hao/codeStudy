package oshi.hardware.platform.linux;

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
public final class LinuxHardwareAbstractionLayer extends AbstractHardwareAbstractionLayer {
   public ComputerSystem createComputerSystem() {
      return new LinuxComputerSystem();
   }

   public GlobalMemory createMemory() {
      return new LinuxGlobalMemory();
   }

   public CentralProcessor createProcessor() {
      return new LinuxCentralProcessor();
   }

   public Sensors createSensors() {
      return new LinuxSensors();
   }

   public List<PowerSource> getPowerSources() {
      return LinuxPowerSource.getPowerSources();
   }

   public List<HWDiskStore> getDiskStores() {
      return LinuxHWDiskStore.getDisks();
   }

   public List<Display> getDisplays() {
      return LinuxDisplay.getDisplays();
   }

   public List<NetworkIF> getNetworkIFs(boolean includeLocalInterfaces) {
      return LinuxNetworkIF.getNetworks(includeLocalInterfaces);
   }

   public List<UsbDevice> getUsbDevices(boolean tree) {
      return LinuxUsbDevice.getUsbDevices(tree);
   }

   public List<SoundCard> getSoundCards() {
      return LinuxSoundCard.getSoundCards();
   }

   public List<GraphicsCard> getGraphicsCards() {
      return LinuxGraphicsCard.getGraphicsCards();
   }
}
