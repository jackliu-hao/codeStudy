package oshi.hardware;

import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public interface GlobalMemory {
  long getTotal();
  
  long getAvailable();
  
  long getPageSize();
  
  VirtualMemory getVirtualMemory();
  
  List<PhysicalMemory> getPhysicalMemory();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\GlobalMemory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */