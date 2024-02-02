/*     */ package oshi.hardware.platform.unix.aix;
/*     */ 
/*     */ import com.sun.jna.Native;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.function.Supplier;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.driver.unix.aix.Lssrad;
/*     */ import oshi.driver.unix.aix.perfstat.PerfstatConfig;
/*     */ import oshi.driver.unix.aix.perfstat.PerfstatCpu;
/*     */ import oshi.hardware.CentralProcessor;
/*     */ import oshi.hardware.common.AbstractCentralProcessor;
/*     */ import oshi.jna.platform.unix.aix.Perfstat;
/*     */ import oshi.util.ExecutingCommand;
/*     */ import oshi.util.FileUtil;
/*     */ import oshi.util.Memoizer;
/*     */ import oshi.util.ParseUtil;
/*     */ import oshi.util.tuples.Pair;
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
/*     */ final class AixCentralProcessor
/*     */   extends AbstractCentralProcessor
/*     */ {
/*  57 */   private final Supplier<Perfstat.perfstat_cpu_total_t> cpuTotal = Memoizer.memoize(PerfstatCpu::queryCpuTotal, Memoizer.defaultExpiration());
/*  58 */   private final Supplier<Perfstat.perfstat_cpu_t[]> cpuProc = Memoizer.memoize(PerfstatCpu::queryCpu, Memoizer.defaultExpiration());
/*  59 */   private static final int SBITS = querySbits();
/*     */ 
/*     */ 
/*     */   
/*     */   private Perfstat.perfstat_partition_config_t config;
/*     */ 
/*     */   
/*  66 */   private static final long USER_HZ = ParseUtil.parseLongOrDefault(ExecutingCommand.getFirstAnswer("getconf CLK_TCK"), 100L);
/*     */ 
/*     */ 
/*     */   
/*     */   protected CentralProcessor.ProcessorIdentifier queryProcessorId() {
/*  71 */     String cpuVendor = "unknown";
/*  72 */     String cpuName = "";
/*  73 */     String cpuFamily = "";
/*  74 */     boolean cpu64bit = false;
/*     */     
/*  76 */     String nameMarker = "Processor Type:";
/*  77 */     String familyMarker = "Processor Version:";
/*  78 */     String bitnessMarker = "CPU Type:";
/*  79 */     for (String checkLine : ExecutingCommand.runNative("prtconf")) {
/*  80 */       if (checkLine.startsWith("Processor Type:")) {
/*  81 */         cpuName = checkLine.split("Processor Type:")[1].trim();
/*  82 */         if (cpuName.startsWith("P")) {
/*  83 */           cpuVendor = "IBM"; continue;
/*  84 */         }  if (cpuName.startsWith("I"))
/*  85 */           cpuVendor = "Intel";  continue;
/*     */       } 
/*  87 */       if (checkLine.startsWith("Processor Version:")) {
/*  88 */         cpuFamily = checkLine.split("Processor Version:")[1].trim(); continue;
/*  89 */       }  if (checkLine.startsWith("CPU Type:")) {
/*  90 */         cpu64bit = checkLine.split("CPU Type:")[1].contains("64");
/*     */       }
/*     */     } 
/*     */     
/*  94 */     String cpuModel = "";
/*  95 */     String cpuStepping = "";
/*  96 */     String machineId = Native.toString(this.config.machineID);
/*  97 */     if (machineId.isEmpty()) {
/*  98 */       machineId = ExecutingCommand.getFirstAnswer("uname -m");
/*     */     }
/*     */     
/* 101 */     if (machineId.length() > 10) {
/* 102 */       int m = machineId.length() - 4;
/* 103 */       int s = machineId.length() - 2;
/* 104 */       cpuModel = machineId.substring(m, s);
/* 105 */       cpuStepping = machineId.substring(s);
/*     */     } 
/*     */     
/* 108 */     return new CentralProcessor.ProcessorIdentifier(cpuVendor, cpuName, cpuFamily, cpuModel, cpuStepping, machineId, cpu64bit, (long)(this.config.processorMHz * 1000000.0D));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected List<CentralProcessor.LogicalProcessor> initProcessorCounts() {
/* 114 */     this.config = PerfstatConfig.queryConfig();
/*     */     
/* 116 */     int physProcs = (int)this.config.numProcessors.max;
/* 117 */     if (physProcs < 1) {
/* 118 */       physProcs = 1;
/*     */     }
/* 120 */     int lcpus = this.config.lcpus;
/* 121 */     if (lcpus < 1) {
/* 122 */       lcpus = 1;
/*     */     }
/*     */     
/* 125 */     Map<Integer, Pair<Integer, Integer>> nodePkgMap = Lssrad.queryNodesPackages();
/* 126 */     List<CentralProcessor.LogicalProcessor> logProcs = new ArrayList<>();
/* 127 */     for (int proc = 0; proc < lcpus; proc++) {
/* 128 */       Pair<Integer, Integer> nodePkg = nodePkgMap.get(Integer.valueOf(proc));
/* 129 */       logProcs.add(new CentralProcessor.LogicalProcessor(proc, proc / physProcs, (nodePkg == null) ? 0 : ((Integer)nodePkg.getB()).intValue(), 
/* 130 */             (nodePkg == null) ? 0 : ((Integer)nodePkg.getA()).intValue()));
/*     */     } 
/* 132 */     return logProcs;
/*     */   }
/*     */ 
/*     */   
/*     */   public long[] querySystemCpuLoadTicks() {
/* 137 */     Perfstat.perfstat_cpu_total_t perfstat = this.cpuTotal.get();
/* 138 */     long[] ticks = new long[(CentralProcessor.TickType.values()).length];
/* 139 */     ticks[CentralProcessor.TickType.USER.ordinal()] = perfstat.user * 1000L / USER_HZ;
/*     */     
/* 141 */     ticks[CentralProcessor.TickType.SYSTEM.ordinal()] = perfstat.sys * 1000L / USER_HZ;
/* 142 */     ticks[CentralProcessor.TickType.IDLE.ordinal()] = perfstat.idle * 1000L / USER_HZ;
/* 143 */     ticks[CentralProcessor.TickType.IOWAIT.ordinal()] = perfstat.wait * 1000L / USER_HZ;
/* 144 */     ticks[CentralProcessor.TickType.IRQ.ordinal()] = perfstat.devintrs * 1000L / USER_HZ;
/* 145 */     ticks[CentralProcessor.TickType.SOFTIRQ.ordinal()] = perfstat.softintrs * 1000L / USER_HZ;
/* 146 */     ticks[CentralProcessor.TickType.STEAL.ordinal()] = (perfstat.idle_stolen_purr + perfstat.busy_stolen_purr) * 1000L / USER_HZ;
/* 147 */     return ticks;
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
/*     */   public long[] queryCurrentFreq() {
/* 159 */     long[] freqs = new long[getLogicalProcessorCount()];
/* 160 */     Arrays.fill(freqs, -1L);
/* 161 */     String freqMarker = "runs at";
/* 162 */     int idx = 0;
/* 163 */     for (String checkLine : ExecutingCommand.runNative("pmcycles -m")) {
/* 164 */       if (checkLine.contains(freqMarker)) {
/* 165 */         freqs[idx++] = ParseUtil.parseHertz(checkLine.split(freqMarker)[1].trim());
/* 166 */         if (idx >= freqs.length) {
/*     */           break;
/*     */         }
/*     */       } 
/*     */     } 
/* 171 */     return freqs;
/*     */   }
/*     */ 
/*     */   
/*     */   protected long queryMaxFreq() {
/* 176 */     Perfstat.perfstat_cpu_total_t perfstat = this.cpuTotal.get();
/* 177 */     return perfstat.processorHZ;
/*     */   }
/*     */ 
/*     */   
/*     */   public double[] getSystemLoadAverage(int nelem) {
/* 182 */     if (nelem < 1 || nelem > 3) {
/* 183 */       throw new IllegalArgumentException("Must include from one to three elements.");
/*     */     }
/* 185 */     double[] average = new double[nelem];
/* 186 */     long[] loadavg = ((Perfstat.perfstat_cpu_total_t)this.cpuTotal.get()).loadavg;
/* 187 */     for (int i = 0; i < nelem; i++) {
/* 188 */       average[i] = loadavg[i] / (1L << SBITS);
/*     */     }
/* 190 */     return average;
/*     */   }
/*     */ 
/*     */   
/*     */   public long[][] queryProcessorCpuLoadTicks() {
/* 195 */     Perfstat.perfstat_cpu_t[] cpu = this.cpuProc.get();
/* 196 */     long[][] ticks = new long[cpu.length][(CentralProcessor.TickType.values()).length];
/* 197 */     for (int i = 0; i < cpu.length; i++) {
/* 198 */       ticks[i] = new long[(CentralProcessor.TickType.values()).length];
/* 199 */       ticks[i][CentralProcessor.TickType.USER.ordinal()] = (cpu[i]).user * 1000L / USER_HZ;
/*     */       
/* 201 */       ticks[i][CentralProcessor.TickType.SYSTEM.ordinal()] = (cpu[i]).sys * 1000L / USER_HZ;
/* 202 */       ticks[i][CentralProcessor.TickType.IDLE.ordinal()] = (cpu[i]).idle * 1000L / USER_HZ;
/* 203 */       ticks[i][CentralProcessor.TickType.IOWAIT.ordinal()] = (cpu[i]).wait * 1000L / USER_HZ;
/* 204 */       ticks[i][CentralProcessor.TickType.IRQ.ordinal()] = (cpu[i]).devintrs * 1000L / USER_HZ;
/* 205 */       ticks[i][CentralProcessor.TickType.SOFTIRQ.ordinal()] = (cpu[i]).softintrs * 1000L / USER_HZ;
/* 206 */       ticks[i][CentralProcessor.TickType.STEAL.ordinal()] = ((cpu[i]).idle_stolen_purr + (cpu[i]).busy_stolen_purr) * 1000L / USER_HZ;
/*     */     } 
/* 208 */     return ticks;
/*     */   }
/*     */ 
/*     */   
/*     */   public long queryContextSwitches() {
/* 213 */     return ((Perfstat.perfstat_cpu_total_t)this.cpuTotal.get()).pswitch;
/*     */   }
/*     */ 
/*     */   
/*     */   public long queryInterrupts() {
/* 218 */     Perfstat.perfstat_cpu_total_t cpu = this.cpuTotal.get();
/* 219 */     return cpu.devintrs + cpu.softintrs;
/*     */   }
/*     */ 
/*     */   
/*     */   private static int querySbits() {
/* 224 */     for (String s : FileUtil.readFile("/usr/include/sys/proc.h")) {
/* 225 */       if (s.contains("SBITS") && s.contains("#define")) {
/* 226 */         return ParseUtil.parseLastInt(s, 16);
/*     */       }
/*     */     } 
/* 229 */     return 16;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platfor\\unix\aix\AixCentralProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */