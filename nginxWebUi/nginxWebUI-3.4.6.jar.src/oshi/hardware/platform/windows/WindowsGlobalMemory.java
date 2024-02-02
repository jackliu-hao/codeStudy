/*     */ package oshi.hardware.platform.windows;
/*     */ 
/*     */ import com.sun.jna.platform.win32.COM.WbemcliUtil;
/*     */ import com.sun.jna.platform.win32.Kernel32;
/*     */ import com.sun.jna.platform.win32.Psapi;
/*     */ import com.sun.jna.platform.win32.VersionHelpers;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.function.Supplier;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.driver.windows.wmi.Win32PhysicalMemory;
/*     */ import oshi.hardware.PhysicalMemory;
/*     */ import oshi.hardware.VirtualMemory;
/*     */ import oshi.hardware.common.AbstractGlobalMemory;
/*     */ import oshi.util.Memoizer;
/*     */ import oshi.util.platform.windows.WmiUtil;
/*     */ import oshi.util.tuples.Triplet;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @ThreadSafe
/*     */ final class WindowsGlobalMemory
/*     */   extends AbstractGlobalMemory
/*     */ {
/*  58 */   private static final Logger LOG = LoggerFactory.getLogger(WindowsGlobalMemory.class);
/*     */   
/*  60 */   private static final boolean IS_WINDOWS10_OR_GREATER = VersionHelpers.IsWindows10OrGreater();
/*     */   
/*  62 */   private final Supplier<Triplet<Long, Long, Long>> availTotalSize = Memoizer.memoize(WindowsGlobalMemory::readPerfInfo, 
/*  63 */       Memoizer.defaultExpiration());
/*     */   
/*  65 */   private final Supplier<VirtualMemory> vm = Memoizer.memoize(this::createVirtualMemory);
/*     */ 
/*     */   
/*     */   public long getAvailable() {
/*  69 */     return ((Long)((Triplet)this.availTotalSize.get()).getA()).longValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public long getTotal() {
/*  74 */     return ((Long)((Triplet)this.availTotalSize.get()).getB()).longValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public long getPageSize() {
/*  79 */     return ((Long)((Triplet)this.availTotalSize.get()).getC()).longValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public VirtualMemory getVirtualMemory() {
/*  84 */     return this.vm.get();
/*     */   }
/*     */   
/*     */   private VirtualMemory createVirtualMemory() {
/*  88 */     return (VirtualMemory)new WindowsVirtualMemory(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public List<PhysicalMemory> getPhysicalMemory() {
/*  93 */     List<PhysicalMemory> physicalMemoryList = new ArrayList<>();
/*  94 */     if (IS_WINDOWS10_OR_GREATER) {
/*  95 */       WbemcliUtil.WmiResult<Win32PhysicalMemory.PhysicalMemoryProperty> bankMap = Win32PhysicalMemory.queryphysicalMemory();
/*  96 */       for (int index = 0; index < bankMap.getResultCount(); index++) {
/*  97 */         String bankLabel = WmiUtil.getString(bankMap, (Enum)Win32PhysicalMemory.PhysicalMemoryProperty.BANKLABEL, index);
/*  98 */         long capacity = WmiUtil.getUint64(bankMap, (Enum)Win32PhysicalMemory.PhysicalMemoryProperty.CAPACITY, index);
/*  99 */         long speed = WmiUtil.getUint32(bankMap, (Enum)Win32PhysicalMemory.PhysicalMemoryProperty.SPEED, index) * 1000000L;
/* 100 */         String manufacturer = WmiUtil.getString(bankMap, (Enum)Win32PhysicalMemory.PhysicalMemoryProperty.MANUFACTURER, index);
/* 101 */         String memoryType = smBiosMemoryType(
/* 102 */             WmiUtil.getUint32(bankMap, (Enum)Win32PhysicalMemory.PhysicalMemoryProperty.SMBIOSMEMORYTYPE, index));
/* 103 */         physicalMemoryList.add(new PhysicalMemory(bankLabel, capacity, speed, manufacturer, memoryType));
/*     */       } 
/*     */     } else {
/* 106 */       WbemcliUtil.WmiResult<Win32PhysicalMemory.PhysicalMemoryPropertyWin8> bankMap = Win32PhysicalMemory.queryphysicalMemoryWin8();
/* 107 */       for (int index = 0; index < bankMap.getResultCount(); index++) {
/* 108 */         String bankLabel = WmiUtil.getString(bankMap, (Enum)Win32PhysicalMemory.PhysicalMemoryPropertyWin8.BANKLABEL, index);
/* 109 */         long capacity = WmiUtil.getUint64(bankMap, (Enum)Win32PhysicalMemory.PhysicalMemoryPropertyWin8.CAPACITY, index);
/* 110 */         long speed = WmiUtil.getUint32(bankMap, (Enum)Win32PhysicalMemory.PhysicalMemoryPropertyWin8.SPEED, index) * 1000000L;
/* 111 */         String manufacturer = WmiUtil.getString(bankMap, (Enum)Win32PhysicalMemory.PhysicalMemoryPropertyWin8.MANUFACTURER, index);
/* 112 */         String memoryType = memoryType(
/* 113 */             WmiUtil.getUint16(bankMap, (Enum)Win32PhysicalMemory.PhysicalMemoryPropertyWin8.MEMORYTYPE, index));
/* 114 */         physicalMemoryList.add(new PhysicalMemory(bankLabel, capacity, speed, manufacturer, memoryType));
/*     */       } 
/*     */     } 
/* 117 */     return physicalMemoryList;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String memoryType(int type) {
/* 128 */     switch (type) {
/*     */       case 1:
/* 130 */         return "Other";
/*     */       case 2:
/* 132 */         return "DRAM";
/*     */       case 3:
/* 134 */         return "Synchronous DRAM";
/*     */       case 4:
/* 136 */         return "Cache DRAM";
/*     */       case 5:
/* 138 */         return "EDO";
/*     */       case 6:
/* 140 */         return "EDRAM";
/*     */       case 7:
/* 142 */         return "VRAM";
/*     */       case 8:
/* 144 */         return "SRAM";
/*     */       case 9:
/* 146 */         return "RAM";
/*     */       case 10:
/* 148 */         return "ROM";
/*     */       case 11:
/* 150 */         return "Flash";
/*     */       case 12:
/* 152 */         return "EEPROM";
/*     */       case 13:
/* 154 */         return "FEPROM";
/*     */       case 14:
/* 156 */         return "EPROM";
/*     */       case 15:
/* 158 */         return "CDRAM";
/*     */       case 16:
/* 160 */         return "3DRAM";
/*     */       case 17:
/* 162 */         return "SDRAM";
/*     */       case 18:
/* 164 */         return "SGRAM";
/*     */       case 19:
/* 166 */         return "RDRAM";
/*     */       case 20:
/* 168 */         return "DDR";
/*     */       case 21:
/* 170 */         return "DDR2";
/*     */       case 22:
/* 172 */         return "DDR2-FB-DIMM";
/*     */       case 24:
/* 174 */         return "DDR3";
/*     */       case 25:
/* 176 */         return "FBD2";
/*     */     } 
/* 178 */     return "Unknown";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String smBiosMemoryType(int type) {
/* 192 */     switch (type) {
/*     */       case 1:
/* 194 */         return "Other";
/*     */       case 3:
/* 196 */         return "DRAM";
/*     */       case 4:
/* 198 */         return "EDRAM";
/*     */       case 5:
/* 200 */         return "VRAM";
/*     */       case 6:
/* 202 */         return "SRAM";
/*     */       case 7:
/* 204 */         return "RAM";
/*     */       case 8:
/* 206 */         return "ROM";
/*     */       case 9:
/* 208 */         return "FLASH";
/*     */       case 10:
/* 210 */         return "EEPROM";
/*     */       case 11:
/* 212 */         return "FEPROM";
/*     */       case 12:
/* 214 */         return "EPROM";
/*     */       case 13:
/* 216 */         return "CDRAM";
/*     */       case 14:
/* 218 */         return "3DRAM";
/*     */       case 15:
/* 220 */         return "SDRAM";
/*     */       case 16:
/* 222 */         return "SGRAM";
/*     */       case 17:
/* 224 */         return "RDRAM";
/*     */       case 18:
/* 226 */         return "DDR";
/*     */       case 19:
/* 228 */         return "DDR2";
/*     */       case 20:
/* 230 */         return "DDR2 FB-DIMM";
/*     */       case 24:
/* 232 */         return "DDR3";
/*     */       case 25:
/* 234 */         return "FBD2";
/*     */       case 26:
/* 236 */         return "DDR4";
/*     */       case 27:
/* 238 */         return "LPDDR";
/*     */       case 28:
/* 240 */         return "LPDDR2";
/*     */       case 29:
/* 242 */         return "LPDDR3";
/*     */       case 30:
/* 244 */         return "LPDDR4";
/*     */       case 31:
/* 246 */         return "Logical non-volatile device";
/*     */     } 
/*     */     
/* 249 */     return "Unknown";
/*     */   }
/*     */ 
/*     */   
/*     */   private static Triplet<Long, Long, Long> readPerfInfo() {
/* 254 */     Psapi.PERFORMANCE_INFORMATION performanceInfo = new Psapi.PERFORMANCE_INFORMATION();
/* 255 */     if (!Psapi.INSTANCE.GetPerformanceInfo(performanceInfo, performanceInfo.size())) {
/* 256 */       LOG.error("Failed to get Performance Info. Error code: {}", Integer.valueOf(Kernel32.INSTANCE.GetLastError()));
/* 257 */       return new Triplet(Long.valueOf(0L), Long.valueOf(0L), Long.valueOf(4098L));
/*     */     } 
/* 259 */     long pageSize = performanceInfo.PageSize.longValue();
/* 260 */     long memAvailable = pageSize * performanceInfo.PhysicalAvailable.longValue();
/* 261 */     long memTotal = pageSize * performanceInfo.PhysicalTotal.longValue();
/* 262 */     return new Triplet(Long.valueOf(memAvailable), Long.valueOf(memTotal), Long.valueOf(pageSize));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platform\windows\WindowsGlobalMemory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */