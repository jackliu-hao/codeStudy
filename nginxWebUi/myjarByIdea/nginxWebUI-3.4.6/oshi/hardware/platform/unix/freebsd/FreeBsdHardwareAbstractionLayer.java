package oshi.hardware.platform.unix.freebsd;

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
public final class FreeBsdHardwareAbstractionLayer extends AbstractHardwareAbstractionLayer {
   public ComputerSystem createComputerSystem() {
      return new FreeBsdComputerSystem();
   }

   public GlobalMemory createMemory() {
      return new FreeBsdGlobalMemory();
   }

   public CentralProcessor createProcessor() {
      return new FreeBsdCentralProcessor();
   }

   public Sensors createSensors() {
      return new FreeBsdSensors();
   }

   public List<PowerSource> getPowerSources() {
      return FreeBsdPowerSource.getPowerSources();
   }

   public List<HWDiskStore> getDiskStores() {
      return FreeBsdHWDiskStore.getDisks();
   }

   public List<Display> getDisplays() {
      return FreeBsdDisplay.getDisplays();
   }

   public List<NetworkIF> getNetworkIFs(boolean includeLocalInterfaces) {
      return FreeBsdNetworkIF.getNetworks(includeLocalInterfaces);
   }

   public List<UsbDevice> getUsbDevices(boolean tree) {
      return FreeBsdUsbDevice.getUsbDevices(tree);
   }

   public List<SoundCard> getSoundCards() {
      return FreeBsdSoundCard.getSoundCards();
   }

   public List<GraphicsCard> getGraphicsCards() {
      return FreeBsdGraphicsCard.getGraphicsCards();
   }
}
