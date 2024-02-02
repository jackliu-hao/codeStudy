package oshi.hardware.platform.windows;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Psapi;
import com.sun.jna.platform.win32.VersionHelpers;
import com.sun.jna.platform.win32.COM.WbemcliUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.windows.wmi.Win32PhysicalMemory;
import oshi.hardware.PhysicalMemory;
import oshi.hardware.VirtualMemory;
import oshi.hardware.common.AbstractGlobalMemory;
import oshi.util.Memoizer;
import oshi.util.platform.windows.WmiUtil;
import oshi.util.tuples.Triplet;

@ThreadSafe
final class WindowsGlobalMemory extends AbstractGlobalMemory {
   private static final Logger LOG = LoggerFactory.getLogger(WindowsGlobalMemory.class);
   private static final boolean IS_WINDOWS10_OR_GREATER = VersionHelpers.IsWindows10OrGreater();
   private final Supplier<Triplet<Long, Long, Long>> availTotalSize = Memoizer.memoize(WindowsGlobalMemory::readPerfInfo, Memoizer.defaultExpiration());
   private final Supplier<VirtualMemory> vm = Memoizer.memoize(this::createVirtualMemory);

   public long getAvailable() {
      return (Long)((Triplet)this.availTotalSize.get()).getA();
   }

   public long getTotal() {
      return (Long)((Triplet)this.availTotalSize.get()).getB();
   }

   public long getPageSize() {
      return (Long)((Triplet)this.availTotalSize.get()).getC();
   }

   public VirtualMemory getVirtualMemory() {
      return (VirtualMemory)this.vm.get();
   }

   private VirtualMemory createVirtualMemory() {
      return new WindowsVirtualMemory(this);
   }

   public List<PhysicalMemory> getPhysicalMemory() {
      List<PhysicalMemory> physicalMemoryList = new ArrayList();
      WbemcliUtil.WmiResult bankMap;
      int index;
      String bankLabel;
      long capacity;
      long speed;
      String manufacturer;
      String memoryType;
      if (IS_WINDOWS10_OR_GREATER) {
         bankMap = Win32PhysicalMemory.queryphysicalMemory();

         for(index = 0; index < bankMap.getResultCount(); ++index) {
            bankLabel = WmiUtil.getString(bankMap, Win32PhysicalMemory.PhysicalMemoryProperty.BANKLABEL, index);
            capacity = WmiUtil.getUint64(bankMap, Win32PhysicalMemory.PhysicalMemoryProperty.CAPACITY, index);
            speed = (long)WmiUtil.getUint32(bankMap, Win32PhysicalMemory.PhysicalMemoryProperty.SPEED, index) * 1000000L;
            manufacturer = WmiUtil.getString(bankMap, Win32PhysicalMemory.PhysicalMemoryProperty.MANUFACTURER, index);
            memoryType = smBiosMemoryType(WmiUtil.getUint32(bankMap, Win32PhysicalMemory.PhysicalMemoryProperty.SMBIOSMEMORYTYPE, index));
            physicalMemoryList.add(new PhysicalMemory(bankLabel, capacity, speed, manufacturer, memoryType));
         }
      } else {
         bankMap = Win32PhysicalMemory.queryphysicalMemoryWin8();

         for(index = 0; index < bankMap.getResultCount(); ++index) {
            bankLabel = WmiUtil.getString(bankMap, Win32PhysicalMemory.PhysicalMemoryPropertyWin8.BANKLABEL, index);
            capacity = WmiUtil.getUint64(bankMap, Win32PhysicalMemory.PhysicalMemoryPropertyWin8.CAPACITY, index);
            speed = (long)WmiUtil.getUint32(bankMap, Win32PhysicalMemory.PhysicalMemoryPropertyWin8.SPEED, index) * 1000000L;
            manufacturer = WmiUtil.getString(bankMap, Win32PhysicalMemory.PhysicalMemoryPropertyWin8.MANUFACTURER, index);
            memoryType = memoryType(WmiUtil.getUint16(bankMap, Win32PhysicalMemory.PhysicalMemoryPropertyWin8.MEMORYTYPE, index));
            physicalMemoryList.add(new PhysicalMemory(bankLabel, capacity, speed, manufacturer, memoryType));
         }
      }

      return physicalMemoryList;
   }

   private static String memoryType(int type) {
      switch (type) {
         case 1:
            return "Other";
         case 2:
            return "DRAM";
         case 3:
            return "Synchronous DRAM";
         case 4:
            return "Cache DRAM";
         case 5:
            return "EDO";
         case 6:
            return "EDRAM";
         case 7:
            return "VRAM";
         case 8:
            return "SRAM";
         case 9:
            return "RAM";
         case 10:
            return "ROM";
         case 11:
            return "Flash";
         case 12:
            return "EEPROM";
         case 13:
            return "FEPROM";
         case 14:
            return "EPROM";
         case 15:
            return "CDRAM";
         case 16:
            return "3DRAM";
         case 17:
            return "SDRAM";
         case 18:
            return "SGRAM";
         case 19:
            return "RDRAM";
         case 20:
            return "DDR";
         case 21:
            return "DDR2";
         case 22:
            return "DDR2-FB-DIMM";
         case 23:
         default:
            return "Unknown";
         case 24:
            return "DDR3";
         case 25:
            return "FBD2";
      }
   }

   private static String smBiosMemoryType(int type) {
      switch (type) {
         case 1:
            return "Other";
         case 2:
         case 21:
         case 22:
         case 23:
         default:
            return "Unknown";
         case 3:
            return "DRAM";
         case 4:
            return "EDRAM";
         case 5:
            return "VRAM";
         case 6:
            return "SRAM";
         case 7:
            return "RAM";
         case 8:
            return "ROM";
         case 9:
            return "FLASH";
         case 10:
            return "EEPROM";
         case 11:
            return "FEPROM";
         case 12:
            return "EPROM";
         case 13:
            return "CDRAM";
         case 14:
            return "3DRAM";
         case 15:
            return "SDRAM";
         case 16:
            return "SGRAM";
         case 17:
            return "RDRAM";
         case 18:
            return "DDR";
         case 19:
            return "DDR2";
         case 20:
            return "DDR2 FB-DIMM";
         case 24:
            return "DDR3";
         case 25:
            return "FBD2";
         case 26:
            return "DDR4";
         case 27:
            return "LPDDR";
         case 28:
            return "LPDDR2";
         case 29:
            return "LPDDR3";
         case 30:
            return "LPDDR4";
         case 31:
            return "Logical non-volatile device";
      }
   }

   private static Triplet<Long, Long, Long> readPerfInfo() {
      Psapi.PERFORMANCE_INFORMATION performanceInfo = new Psapi.PERFORMANCE_INFORMATION();
      if (!Psapi.INSTANCE.GetPerformanceInfo(performanceInfo, performanceInfo.size())) {
         LOG.error((String)"Failed to get Performance Info. Error code: {}", (Object)Kernel32.INSTANCE.GetLastError());
         return new Triplet(0L, 0L, 4098L);
      } else {
         long pageSize = performanceInfo.PageSize.longValue();
         long memAvailable = pageSize * performanceInfo.PhysicalAvailable.longValue();
         long memTotal = pageSize * performanceInfo.PhysicalTotal.longValue();
         return new Triplet(memAvailable, memTotal, pageSize);
      }
   }
}
