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
  
  List<NetworkIF> getNetworkIFs(boolean paramBoolean);
  
  List<Display> getDisplays();
  
  Sensors getSensors();
  
  List<UsbDevice> getUsbDevices(boolean paramBoolean);
  
  List<SoundCard> getSoundCards();
  
  List<GraphicsCard> getGraphicsCards();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\HardwareAbstractionLayer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */