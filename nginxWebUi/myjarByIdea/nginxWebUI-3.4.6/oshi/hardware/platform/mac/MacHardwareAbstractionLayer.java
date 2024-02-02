package oshi.hardware.platform.mac;

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
public final class MacHardwareAbstractionLayer extends AbstractHardwareAbstractionLayer {
   public ComputerSystem createComputerSystem() {
      return new MacComputerSystem();
   }

   public GlobalMemory createMemory() {
      return new MacGlobalMemory();
   }

   public CentralProcessor createProcessor() {
      return new MacCentralProcessor();
   }

   public Sensors createSensors() {
      return new MacSensors();
   }

   public List<PowerSource> getPowerSources() {
      return MacPowerSource.getPowerSources();
   }

   public List<HWDiskStore> getDiskStores() {
      return MacHWDiskStore.getDisks();
   }

   public List<Display> getDisplays() {
      return MacDisplay.getDisplays();
   }

   public List<NetworkIF> getNetworkIFs(boolean includeLocalInterfaces) {
      return MacNetworkIF.getNetworks(includeLocalInterfaces);
   }

   public List<UsbDevice> getUsbDevices(boolean tree) {
      return MacUsbDevice.getUsbDevices(tree);
   }

   public List<SoundCard> getSoundCards() {
      return MacSoundCard.getSoundCards();
   }

   public List<GraphicsCard> getGraphicsCards() {
      return MacGraphicsCard.getGraphicsCards();
   }
}
