/*     */ package oshi.hardware.platform.unix.aix;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.function.Supplier;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.driver.unix.aix.perfstat.PerfstatMemory;
/*     */ import oshi.hardware.PhysicalMemory;
/*     */ import oshi.hardware.VirtualMemory;
/*     */ import oshi.hardware.common.AbstractGlobalMemory;
/*     */ import oshi.jna.platform.unix.aix.Perfstat;
/*     */ import oshi.util.Memoizer;
/*     */ import oshi.util.ParseUtil;
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
/*     */ final class AixGlobalMemory
/*     */   extends AbstractGlobalMemory
/*     */ {
/*  48 */   private final Supplier<Perfstat.perfstat_memory_total_t> perfstatMem = Memoizer.memoize(AixGlobalMemory::queryPerfstat, 
/*  49 */       Memoizer.defaultExpiration());
/*     */ 
/*     */   
/*     */   private final Supplier<List<String>> lscfg;
/*     */   
/*     */   private static final long PAGESIZE = 4096L;
/*     */   
/*  56 */   private final Supplier<VirtualMemory> vm = Memoizer.memoize(this::createVirtualMemory);
/*     */   
/*     */   AixGlobalMemory(Supplier<List<String>> lscfg) {
/*  59 */     this.lscfg = lscfg;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getAvailable() {
/*  64 */     return ((Perfstat.perfstat_memory_total_t)this.perfstatMem.get()).real_avail * 4096L;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getTotal() {
/*  69 */     return ((Perfstat.perfstat_memory_total_t)this.perfstatMem.get()).real_total * 4096L;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getPageSize() {
/*  74 */     return 4096L;
/*     */   }
/*     */ 
/*     */   
/*     */   public VirtualMemory getVirtualMemory() {
/*  79 */     return this.vm.get();
/*     */   }
/*     */ 
/*     */   
/*     */   public List<PhysicalMemory> getPhysicalMemory() {
/*  84 */     List<PhysicalMemory> pmList = new ArrayList<>();
/*  85 */     boolean isMemModule = false;
/*  86 */     String bankLabel = "unknown";
/*  87 */     String locator = "";
/*  88 */     long capacity = 0L;
/*  89 */     for (String line : this.lscfg.get()) {
/*  90 */       String s = line.trim();
/*  91 */       if (s.endsWith("memory-module")) {
/*  92 */         isMemModule = true; continue;
/*  93 */       }  if (isMemModule) {
/*  94 */         if (s.startsWith("Node:")) {
/*  95 */           bankLabel = s.substring(5).trim();
/*  96 */           if (bankLabel.startsWith("IBM,"))
/*  97 */             bankLabel = bankLabel.substring(4);  continue;
/*     */         } 
/*  99 */         if (s.startsWith("Physical Location:")) {
/* 100 */           locator = "/" + s.substring(18).trim(); continue;
/* 101 */         }  if (s.startsWith("Size")) {
/* 102 */           capacity = ParseUtil.parseLongOrDefault(ParseUtil.removeLeadingDots(s.substring(4).trim()), 0L) << 20L; continue;
/*     */         } 
/* 104 */         if (s.startsWith("Hardware Location Code")) {
/*     */           
/* 106 */           if (capacity > 0L) {
/* 107 */             pmList.add(new PhysicalMemory(bankLabel + locator, capacity, 0L, "IBM", "unknown"));
/*     */           }
/* 109 */           bankLabel = "unknown";
/* 110 */           locator = "";
/* 111 */           capacity = 0L;
/* 112 */           isMemModule = false;
/*     */         } 
/*     */       } 
/*     */     } 
/* 116 */     return pmList;
/*     */   }
/*     */   
/*     */   private static Perfstat.perfstat_memory_total_t queryPerfstat() {
/* 120 */     return PerfstatMemory.queryMemoryTotal();
/*     */   }
/*     */   
/*     */   private VirtualMemory createVirtualMemory() {
/* 124 */     return (VirtualMemory)new AixVirtualMemory(this.perfstatMem);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platfor\\unix\aix\AixGlobalMemory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */