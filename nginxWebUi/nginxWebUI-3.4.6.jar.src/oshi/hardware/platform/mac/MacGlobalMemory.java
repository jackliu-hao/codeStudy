/*     */ package oshi.hardware.platform.mac;
/*     */ 
/*     */ import com.sun.jna.Native;
/*     */ import com.sun.jna.Structure;
/*     */ import com.sun.jna.platform.mac.SystemB;
/*     */ import com.sun.jna.ptr.IntByReference;
/*     */ import com.sun.jna.ptr.LongByReference;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.function.Supplier;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.hardware.PhysicalMemory;
/*     */ import oshi.hardware.VirtualMemory;
/*     */ import oshi.hardware.common.AbstractGlobalMemory;
/*     */ import oshi.util.ExecutingCommand;
/*     */ import oshi.util.Memoizer;
/*     */ import oshi.util.ParseUtil;
/*     */ import oshi.util.platform.mac.SysctlUtil;
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
/*     */ final class MacGlobalMemory
/*     */   extends AbstractGlobalMemory
/*     */ {
/*  57 */   private static final Logger LOG = LoggerFactory.getLogger(MacGlobalMemory.class);
/*     */   
/*  59 */   private final Supplier<Long> available = Memoizer.memoize(this::queryVmStats, Memoizer.defaultExpiration());
/*     */   
/*  61 */   private final Supplier<Long> total = Memoizer.memoize(MacGlobalMemory::queryPhysMem);
/*     */   
/*  63 */   private final Supplier<Long> pageSize = Memoizer.memoize(MacGlobalMemory::queryPageSize);
/*     */   
/*  65 */   private final Supplier<VirtualMemory> vm = Memoizer.memoize(this::createVirtualMemory);
/*     */ 
/*     */   
/*     */   public long getAvailable() {
/*  69 */     return ((Long)this.available.get()).longValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public long getTotal() {
/*  74 */     return ((Long)this.total.get()).longValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public long getPageSize() {
/*  79 */     return ((Long)this.pageSize.get()).longValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public VirtualMemory getVirtualMemory() {
/*  84 */     return this.vm.get();
/*     */   }
/*     */ 
/*     */   
/*     */   public List<PhysicalMemory> getPhysicalMemory() {
/*  89 */     List<PhysicalMemory> pmList = new ArrayList<>();
/*  90 */     List<String> sp = ExecutingCommand.runNative("system_profiler SPMemoryDataType");
/*  91 */     int bank = 0;
/*  92 */     String bankLabel = "unknown";
/*  93 */     long capacity = 0L;
/*  94 */     long speed = 0L;
/*  95 */     String manufacturer = "unknown";
/*  96 */     String memoryType = "unknown";
/*  97 */     for (String line : sp) {
/*  98 */       if (line.trim().startsWith("BANK")) {
/*     */         
/* 100 */         if (bank++ > 0) {
/* 101 */           pmList.add(new PhysicalMemory(bankLabel, capacity, speed, manufacturer, memoryType));
/*     */         }
/* 103 */         bankLabel = line.trim();
/* 104 */         int colon = bankLabel.lastIndexOf(':');
/* 105 */         if (colon > 0)
/* 106 */           bankLabel = bankLabel.substring(0, colon - 1);  continue;
/*     */       } 
/* 108 */       if (bank > 0) {
/* 109 */         String[] split = line.trim().split(":");
/* 110 */         if (split.length == 2) {
/* 111 */           switch (split[0]) {
/*     */             case "Size":
/* 113 */               capacity = ParseUtil.parseDecimalMemorySizeToBinary(split[1].trim());
/*     */             
/*     */             case "Type":
/* 116 */               memoryType = split[1].trim();
/*     */             
/*     */             case "Speed":
/* 119 */               speed = ParseUtil.parseHertz(split[1]);
/*     */             
/*     */             case "Manufacturer":
/* 122 */               manufacturer = split[1].trim();
/*     */           } 
/*     */ 
/*     */ 
/*     */         
/*     */         }
/*     */       } 
/*     */     } 
/* 130 */     pmList.add(new PhysicalMemory(bankLabel, capacity, speed, manufacturer, memoryType));
/*     */     
/* 132 */     return pmList;
/*     */   }
/*     */   
/*     */   private long queryVmStats() {
/* 136 */     SystemB.VMStatistics vmStats = new SystemB.VMStatistics();
/* 137 */     if (0 != SystemB.INSTANCE.host_statistics(SystemB.INSTANCE.mach_host_self(), 2, (Structure)vmStats, new IntByReference(vmStats
/* 138 */           .size() / SystemB.INT_SIZE))) {
/* 139 */       LOG.error("Failed to get host VM info. Error code: {}", Integer.valueOf(Native.getLastError()));
/* 140 */       return 0L;
/*     */     } 
/* 142 */     return (vmStats.free_count + vmStats.inactive_count) * getPageSize();
/*     */   }
/*     */   
/*     */   private static long queryPhysMem() {
/* 146 */     return SysctlUtil.sysctl("hw.memsize", 0L);
/*     */   }
/*     */   
/*     */   private static long queryPageSize() {
/* 150 */     LongByReference pPageSize = new LongByReference();
/* 151 */     if (0 == SystemB.INSTANCE.host_page_size(SystemB.INSTANCE.mach_host_self(), pPageSize)) {
/* 152 */       return pPageSize.getValue();
/*     */     }
/* 154 */     LOG.error("Failed to get host page size. Error code: {}", Integer.valueOf(Native.getLastError()));
/* 155 */     return 4098L;
/*     */   }
/*     */   
/*     */   private VirtualMemory createVirtualMemory() {
/* 159 */     return (VirtualMemory)new MacVirtualMemory(this);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platform\mac\MacGlobalMemory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */