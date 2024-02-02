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
