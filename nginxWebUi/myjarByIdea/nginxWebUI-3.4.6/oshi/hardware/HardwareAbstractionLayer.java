package oshi.hardware;

import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public interface HardwareAbstractionLayer {
   ComputerSystem getComputerSystem();

   CentralProcessor getProcessor();

   GlobalMemory getMemory();

   List<PowerSource> getPowerSources();

   List<HWDiskStore> getDiskStores();

   List<NetworkIF> getNetworkIFs();

   List<NetworkIF> getNetworkIFs(boolean var1);

   List<Display> getDisplays();

   Sensors getSensors();

   List<UsbDevice> getUsbDevices(boolean var1);

   List<SoundCard> getSoundCards();

   List<GraphicsCard> getGraphicsCards();
}
