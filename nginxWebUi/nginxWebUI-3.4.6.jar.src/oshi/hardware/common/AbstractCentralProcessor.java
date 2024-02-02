/*     */ package oshi.hardware.common;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.function.Supplier;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.hardware.CentralProcessor;
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
/*     */ public abstract class AbstractCentralProcessor
/*     */   implements CentralProcessor
/*     */ {
/*  48 */   private static final Logger LOG = LoggerFactory.getLogger(AbstractCentralProcessor.class);
/*     */   
/*  50 */   private final Supplier<CentralProcessor.ProcessorIdentifier> cpuid = Memoizer.memoize(this::queryProcessorId);
/*  51 */   private final Supplier<Long> maxFreq = Memoizer.memoize(this::queryMaxFreq, Memoizer.defaultExpiration());
/*  52 */   private final Supplier<long[]> currentFreq = Memoizer.memoize(this::queryCurrentFreq, Memoizer.defaultExpiration());
/*  53 */   private final Supplier<Long> contextSwitches = Memoizer.memoize(this::queryContextSwitches, Memoizer.defaultExpiration());
/*  54 */   private final Supplier<Long> interrupts = Memoizer.memoize(this::queryInterrupts, Memoizer.defaultExpiration());
/*     */   
/*  56 */   private final Supplier<long[]> systemCpuLoadTicks = Memoizer.memoize(this::querySystemCpuLoadTicks, Memoizer.defaultExpiration());
/*  57 */   private final Supplier<long[][]> processorCpuLoadTicks = Memoizer.memoize(this::queryProcessorCpuLoadTicks, 
/*  58 */       Memoizer.defaultExpiration());
/*     */ 
/*     */   
/*     */   private final int physicalPackageCount;
/*     */ 
/*     */   
/*     */   private final int physicalProcessorCount;
/*     */ 
/*     */   
/*     */   private final int logicalProcessorCount;
/*     */   
/*     */   private final List<CentralProcessor.LogicalProcessor> logicalProcessors;
/*     */ 
/*     */   
/*     */   protected AbstractCentralProcessor() {
/*  73 */     this.logicalProcessors = Collections.unmodifiableList(initProcessorCounts());
/*     */     
/*  75 */     Set<String> physProcPkgs = new HashSet<>();
/*  76 */     Set<Integer> physPkgs = new HashSet<>();
/*  77 */     for (CentralProcessor.LogicalProcessor logProc : this.logicalProcessors) {
/*  78 */       int pkg = logProc.getPhysicalPackageNumber();
/*  79 */       physProcPkgs.add(logProc.getPhysicalProcessorNumber() + ":" + pkg);
/*  80 */       physPkgs.add(Integer.valueOf(pkg));
/*     */     } 
/*  82 */     this.logicalProcessorCount = this.logicalProcessors.size();
/*  83 */     this.physicalProcessorCount = physProcPkgs.size();
/*  84 */     this.physicalPackageCount = physPkgs.size();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract List<CentralProcessor.LogicalProcessor> initProcessorCounts();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract CentralProcessor.ProcessorIdentifier queryProcessorId();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CentralProcessor.ProcessorIdentifier getProcessorIdentifier() {
/* 103 */     return this.cpuid.get();
/*     */   }
/*     */ 
/*     */   
/*     */   public long getMaxFreq() {
/* 108 */     return ((Long)this.maxFreq.get()).longValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract long queryMaxFreq();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long[] getCurrentFreq() {
/* 120 */     return this.currentFreq.get();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract long[] queryCurrentFreq();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getContextSwitches() {
/* 132 */     return ((Long)this.contextSwitches.get()).longValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract long queryContextSwitches();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getInterrupts() {
/* 144 */     return ((Long)this.interrupts.get()).longValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract long queryInterrupts();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<CentralProcessor.LogicalProcessor> getLogicalProcessors() {
/* 156 */     return this.logicalProcessors;
/*     */   }
/*     */ 
/*     */   
/*     */   public long[] getSystemCpuLoadTicks() {
/* 161 */     return this.systemCpuLoadTicks.get();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract long[] querySystemCpuLoadTicks();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long[][] getProcessorCpuLoadTicks() {
/* 173 */     return this.processorCpuLoadTicks.get();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract long[][] queryProcessorCpuLoadTicks();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getSystemCpuLoadBetweenTicks(long[] oldTicks) {
/* 185 */     if (oldTicks.length != (CentralProcessor.TickType.values()).length) {
/* 186 */       throw new IllegalArgumentException("Tick array " + oldTicks.length + " should have " + (
/* 187 */           CentralProcessor.TickType.values()).length + " elements");
/*     */     }
/* 189 */     long[] ticks = getSystemCpuLoadTicks();
/*     */     
/* 191 */     long total = 0L;
/* 192 */     for (int i = 0; i < ticks.length; i++) {
/* 193 */       total += ticks[i] - oldTicks[i];
/*     */     }
/*     */ 
/*     */     
/* 197 */     long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()] + ticks[CentralProcessor.TickType.IOWAIT.getIndex()] - oldTicks[CentralProcessor.TickType.IDLE.getIndex()] - oldTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
/* 198 */     LOG.trace("Total ticks: {}  Idle ticks: {}", Long.valueOf(total), Long.valueOf(idle));
/*     */     
/* 200 */     return (total > 0L && idle >= 0L) ? ((total - idle) / total) : 0.0D;
/*     */   }
/*     */ 
/*     */   
/*     */   public double[] getProcessorCpuLoadBetweenTicks(long[][] oldTicks) {
/* 205 */     if (oldTicks.length != this.logicalProcessorCount || (oldTicks[0]).length != (CentralProcessor.TickType.values()).length) {
/* 206 */       throw new IllegalArgumentException("Tick array " + oldTicks.length + " should have " + this.logicalProcessorCount + " arrays, each of which has " + (
/*     */           
/* 208 */           CentralProcessor.TickType.values()).length + " elements");
/*     */     }
/* 210 */     long[][] ticks = getProcessorCpuLoadTicks();
/* 211 */     double[] load = new double[this.logicalProcessorCount];
/* 212 */     for (int cpu = 0; cpu < this.logicalProcessorCount; cpu++) {
/* 213 */       long total = 0L;
/* 214 */       for (int i = 0; i < (ticks[cpu]).length; i++) {
/* 215 */         total += ticks[cpu][i] - oldTicks[cpu][i];
/*     */       }
/*     */ 
/*     */       
/* 219 */       long idle = ticks[cpu][CentralProcessor.TickType.IDLE.getIndex()] + ticks[cpu][CentralProcessor.TickType.IOWAIT.getIndex()] - oldTicks[cpu][CentralProcessor.TickType.IDLE.getIndex()] - oldTicks[cpu][CentralProcessor.TickType.IOWAIT.getIndex()];
/* 220 */       LOG.trace("CPU: {}  Total ticks: {}  Idle ticks: {}", new Object[] { Integer.valueOf(cpu), Long.valueOf(total), Long.valueOf(idle) });
/*     */       
/* 222 */       load[cpu] = (total > 0L && idle >= 0L) ? ((total - idle) / total) : 0.0D;
/*     */     } 
/* 224 */     return load;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getLogicalProcessorCount() {
/* 229 */     return this.logicalProcessorCount;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getPhysicalProcessorCount() {
/* 234 */     return this.physicalProcessorCount;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getPhysicalPackageCount() {
/* 239 */     return this.physicalPackageCount;
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static String createProcessorID(String stepping, String model, String family, String[] flags) {
/* 257 */     long processorIdBytes = 0L;
/* 258 */     long steppingL = ParseUtil.parseLongOrDefault(stepping, 0L);
/* 259 */     long modelL = ParseUtil.parseLongOrDefault(model, 0L);
/* 260 */     long familyL = ParseUtil.parseLongOrDefault(family, 0L);
/*     */     
/* 262 */     processorIdBytes |= steppingL & 0xFL;
/*     */     
/* 264 */     processorIdBytes |= (modelL & 0xFL) << 4L;
/* 265 */     processorIdBytes |= (modelL & 0xF0L) << 16L;
/*     */     
/* 267 */     processorIdBytes |= (familyL & 0xFL) << 8L;
/* 268 */     processorIdBytes |= (familyL & 0xF0L) << 20L;
/*     */     
/* 270 */     for (String flag : flags) {
/* 271 */       switch (flag) {
/*     */         case "fpu":
/* 273 */           processorIdBytes |= 0x100000000L;
/*     */           break;
/*     */         case "vme":
/* 276 */           processorIdBytes |= 0x200000000L;
/*     */           break;
/*     */         case "de":
/* 279 */           processorIdBytes |= 0x400000000L;
/*     */           break;
/*     */         case "pse":
/* 282 */           processorIdBytes |= 0x800000000L;
/*     */           break;
/*     */         case "tsc":
/* 285 */           processorIdBytes |= 0x1000000000L;
/*     */           break;
/*     */         case "msr":
/* 288 */           processorIdBytes |= 0x2000000000L;
/*     */           break;
/*     */         case "pae":
/* 291 */           processorIdBytes |= 0x4000000000L;
/*     */           break;
/*     */         case "mce":
/* 294 */           processorIdBytes |= 0x8000000000L;
/*     */           break;
/*     */         case "cx8":
/* 297 */           processorIdBytes |= 0x10000000000L;
/*     */           break;
/*     */         case "apic":
/* 300 */           processorIdBytes |= 0x20000000000L;
/*     */           break;
/*     */         case "sep":
/* 303 */           processorIdBytes |= 0x80000000000L;
/*     */           break;
/*     */         case "mtrr":
/* 306 */           processorIdBytes |= 0x100000000000L;
/*     */           break;
/*     */         case "pge":
/* 309 */           processorIdBytes |= 0x200000000000L;
/*     */           break;
/*     */         case "mca":
/* 312 */           processorIdBytes |= 0x400000000000L;
/*     */           break;
/*     */         case "cmov":
/* 315 */           processorIdBytes |= 0x800000000000L;
/*     */           break;
/*     */         case "pat":
/* 318 */           processorIdBytes |= 0x1000000000000L;
/*     */           break;
/*     */         case "pse-36":
/* 321 */           processorIdBytes |= 0x2000000000000L;
/*     */           break;
/*     */         case "psn":
/* 324 */           processorIdBytes |= 0x4000000000000L;
/*     */           break;
/*     */         case "clfsh":
/* 327 */           processorIdBytes |= 0x8000000000000L;
/*     */           break;
/*     */         case "ds":
/* 330 */           processorIdBytes |= 0x20000000000000L;
/*     */           break;
/*     */         case "acpi":
/* 333 */           processorIdBytes |= 0x40000000000000L;
/*     */           break;
/*     */         case "mmx":
/* 336 */           processorIdBytes |= 0x80000000000000L;
/*     */           break;
/*     */         case "fxsr":
/* 339 */           processorIdBytes |= 0x100000000000000L;
/*     */           break;
/*     */         case "sse":
/* 342 */           processorIdBytes |= 0x200000000000000L;
/*     */           break;
/*     */         case "sse2":
/* 345 */           processorIdBytes |= 0x400000000000000L;
/*     */           break;
/*     */         case "ss":
/* 348 */           processorIdBytes |= 0x800000000000000L;
/*     */           break;
/*     */         case "htt":
/* 351 */           processorIdBytes |= 0x1000000000000000L;
/*     */           break;
/*     */         case "tm":
/* 354 */           processorIdBytes |= 0x2000000000000000L;
/*     */           break;
/*     */         case "ia64":
/* 357 */           processorIdBytes |= 0x4000000000000000L;
/*     */           break;
/*     */         case "pbe":
/* 360 */           processorIdBytes |= Long.MIN_VALUE;
/*     */           break;
/*     */       } 
/*     */ 
/*     */     
/*     */     } 
/* 366 */     return String.format("%016X", new Object[] { Long.valueOf(processorIdBytes) });
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 371 */     StringBuilder sb = new StringBuilder(getProcessorIdentifier().getName());
/* 372 */     sb.append("\n ").append(getPhysicalPackageCount()).append(" physical CPU package(s)");
/* 373 */     sb.append("\n ").append(getPhysicalProcessorCount()).append(" physical CPU core(s)");
/* 374 */     sb.append("\n ").append(getLogicalProcessorCount()).append(" logical CPU(s)");
/* 375 */     sb.append('\n').append("Identifier: ").append(getProcessorIdentifier().getIdentifier());
/* 376 */     sb.append('\n').append("ProcessorID: ").append(getProcessorIdentifier().getProcessorID());
/* 377 */     sb.append('\n').append("Microarchitecture: ").append(getProcessorIdentifier().getMicroarchitecture());
/* 378 */     return sb.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\common\AbstractCentralProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */