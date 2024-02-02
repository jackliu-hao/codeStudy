/*     */ package oshi.hardware.platform.unix.solaris;
/*     */ 
/*     */ import com.sun.jna.platform.unix.solaris.LibKstat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.hardware.CentralProcessor;
/*     */ import oshi.hardware.common.AbstractCentralProcessor;
/*     */ import oshi.jna.platform.unix.solaris.SolarisLibc;
/*     */ import oshi.util.ExecutingCommand;
/*     */ import oshi.util.ParseUtil;
/*     */ import oshi.util.platform.unix.solaris.KstatUtil;
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
/*     */ final class SolarisCentralProcessor
/*     */   extends AbstractCentralProcessor
/*     */ {
/*     */   private static final String CPU_INFO = "cpu_info";
/*     */   
/*     */   protected CentralProcessor.ProcessorIdentifier queryProcessorId() {
/*  52 */     String cpuVendor = "";
/*  53 */     String cpuName = "";
/*  54 */     String cpuFamily = "";
/*  55 */     String cpuModel = "";
/*  56 */     String cpuStepping = "";
/*     */ 
/*     */     
/*  59 */     KstatUtil.KstatChain kc = KstatUtil.openChain(); 
/*  60 */     try { LibKstat.Kstat ksp = kc.lookup("cpu_info", -1, null);
/*     */       
/*  62 */       if (ksp != null && kc.read(ksp)) {
/*  63 */         cpuVendor = KstatUtil.dataLookupString(ksp, "vendor_id");
/*  64 */         cpuName = KstatUtil.dataLookupString(ksp, "brand");
/*  65 */         cpuFamily = KstatUtil.dataLookupString(ksp, "family");
/*  66 */         cpuModel = KstatUtil.dataLookupString(ksp, "model");
/*  67 */         cpuStepping = KstatUtil.dataLookupString(ksp, "stepping");
/*     */       } 
/*  69 */       if (kc != null) kc.close();  } catch (Throwable throwable) { if (kc != null)
/*  70 */         try { kc.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; }  boolean cpu64bit = "64".equals(ExecutingCommand.getFirstAnswer("isainfo -b").trim());
/*  71 */     String processorID = getProcessorID(cpuStepping, cpuModel, cpuFamily);
/*     */     
/*  73 */     return new CentralProcessor.ProcessorIdentifier(cpuVendor, cpuName, cpuFamily, cpuModel, cpuStepping, processorID, cpu64bit);
/*     */   }
/*     */ 
/*     */   
/*     */   protected List<CentralProcessor.LogicalProcessor> initProcessorCounts() {
/*  78 */     Map<Integer, Integer> numaNodeMap = mapNumaNodes();
/*  79 */     List<CentralProcessor.LogicalProcessor> logProcs = new ArrayList<>();
/*  80 */     KstatUtil.KstatChain kc = KstatUtil.openChain(); 
/*  81 */     try { List<LibKstat.Kstat> kstats = kc.lookupAll("cpu_info", -1, null);
/*     */       
/*  83 */       for (LibKstat.Kstat ksp : kstats) {
/*  84 */         if (ksp != null && kc.read(ksp)) {
/*  85 */           int procId = logProcs.size();
/*  86 */           String chipId = KstatUtil.dataLookupString(ksp, "chip_id");
/*  87 */           String coreId = KstatUtil.dataLookupString(ksp, "core_id");
/*     */           
/*  89 */           CentralProcessor.LogicalProcessor logProc = new CentralProcessor.LogicalProcessor(procId, ParseUtil.parseIntOrDefault(coreId, 0), ParseUtil.parseIntOrDefault(chipId, 0), ((Integer)numaNodeMap.getOrDefault(Integer.valueOf(procId), Integer.valueOf(0))).intValue());
/*  90 */           logProcs.add(logProc);
/*     */         } 
/*     */       } 
/*  93 */       if (kc != null) kc.close();  } catch (Throwable throwable) { if (kc != null)
/*  94 */         try { kc.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; }  if (logProcs.isEmpty()) {
/*  95 */       logProcs.add(new CentralProcessor.LogicalProcessor(0, 0, 0));
/*     */     }
/*  97 */     return logProcs;
/*     */   }
/*     */ 
/*     */   
/*     */   private static Map<Integer, Integer> mapNumaNodes() {
/* 102 */     Map<Integer, Integer> numaNodeMap = new HashMap<>();
/* 103 */     int lgroup = 0;
/* 104 */     for (String line : ExecutingCommand.runNative("lgrpinfo -c leaves")) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 112 */       if (line.startsWith("lgroup")) {
/* 113 */         lgroup = ParseUtil.getFirstIntValue(line); continue;
/* 114 */       }  if (line.contains("CPUs:") || line.contains("CPU:")) {
/* 115 */         for (Integer cpu : ParseUtil.parseHyphenatedIntList(line.split(":")[1])) {
/* 116 */           numaNodeMap.put(cpu, Integer.valueOf(lgroup));
/*     */         }
/*     */       }
/*     */     } 
/* 120 */     return numaNodeMap;
/*     */   }
/*     */ 
/*     */   
/*     */   public long[] querySystemCpuLoadTicks() {
/* 125 */     long[] ticks = new long[(CentralProcessor.TickType.values()).length];
/*     */     
/* 127 */     long[][] procTicks = getProcessorCpuLoadTicks();
/* 128 */     for (int i = 0; i < ticks.length; i++) {
/* 129 */       for (long[] procTick : procTicks) {
/* 130 */         ticks[i] = ticks[i] + procTick[i];
/*     */       }
/* 132 */       ticks[i] = ticks[i] / procTicks.length;
/*     */     } 
/* 134 */     return ticks;
/*     */   }
/*     */ 
/*     */   
/*     */   public long[] queryCurrentFreq() {
/* 139 */     long[] freqs = new long[getLogicalProcessorCount()];
/* 140 */     Arrays.fill(freqs, -1L);
/* 141 */     KstatUtil.KstatChain kc = KstatUtil.openChain(); 
/* 142 */     try { for (int i = 0; i < freqs.length; i++) {
/* 143 */         for (LibKstat.Kstat ksp : kc.lookupAll("cpu_info", i, null)) {
/* 144 */           if (kc.read(ksp)) {
/* 145 */             freqs[i] = KstatUtil.dataLookupLong(ksp, "current_clock_Hz");
/*     */           }
/*     */         } 
/*     */       } 
/* 149 */       if (kc != null) kc.close();  } catch (Throwable throwable) { if (kc != null)
/* 150 */         try { kc.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; }  return freqs;
/*     */   }
/*     */ 
/*     */   
/*     */   public long queryMaxFreq() {
/* 155 */     long max = -1L;
/* 156 */     KstatUtil.KstatChain kc = KstatUtil.openChain(); 
/* 157 */     try { for (LibKstat.Kstat ksp : kc.lookupAll("cpu_info", 0, null)) {
/* 158 */         if (kc.read(ksp)) {
/* 159 */           String suppFreq = KstatUtil.dataLookupString(ksp, "supported_frequencies_Hz");
/* 160 */           if (!suppFreq.isEmpty()) {
/* 161 */             for (String s : suppFreq.split(":")) {
/* 162 */               long freq = ParseUtil.parseLongOrDefault(s, -1L);
/* 163 */               if (max < freq) {
/* 164 */                 max = freq;
/*     */               }
/*     */             } 
/*     */           }
/*     */         } 
/*     */       } 
/* 170 */       if (kc != null) kc.close();  } catch (Throwable throwable) { if (kc != null)
/* 171 */         try { kc.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; }  return max;
/*     */   }
/*     */ 
/*     */   
/*     */   public double[] getSystemLoadAverage(int nelem) {
/* 176 */     if (nelem < 1 || nelem > 3) {
/* 177 */       throw new IllegalArgumentException("Must include from one to three elements.");
/*     */     }
/* 179 */     double[] average = new double[nelem];
/* 180 */     int retval = SolarisLibc.INSTANCE.getloadavg(average, nelem);
/* 181 */     if (retval < nelem) {
/* 182 */       for (int i = Math.max(retval, 0); i < average.length; i++) {
/* 183 */         average[i] = -1.0D;
/*     */       }
/*     */     }
/* 186 */     return average;
/*     */   }
/*     */ 
/*     */   
/*     */   public long[][] queryProcessorCpuLoadTicks() {
/* 191 */     long[][] ticks = new long[getLogicalProcessorCount()][(CentralProcessor.TickType.values()).length];
/* 192 */     int cpu = -1;
/* 193 */     KstatUtil.KstatChain kc = KstatUtil.openChain(); 
/* 194 */     try { for (LibKstat.Kstat ksp : kc.lookupAll("cpu", -1, "sys")) {
/*     */         
/* 196 */         if (++cpu >= ticks.length) {
/*     */           break;
/*     */         }
/*     */         
/* 200 */         if (kc.read(ksp)) {
/* 201 */           ticks[cpu][CentralProcessor.TickType.IDLE.getIndex()] = KstatUtil.dataLookupLong(ksp, "cpu_ticks_idle");
/* 202 */           ticks[cpu][CentralProcessor.TickType.SYSTEM.getIndex()] = KstatUtil.dataLookupLong(ksp, "cpu_ticks_kernel");
/* 203 */           ticks[cpu][CentralProcessor.TickType.USER.getIndex()] = KstatUtil.dataLookupLong(ksp, "cpu_ticks_user");
/*     */         } 
/*     */       } 
/* 206 */       if (kc != null) kc.close();  } catch (Throwable throwable) { if (kc != null)
/* 207 */         try { kc.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; }  return ticks;
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
/*     */   private static String getProcessorID(String stepping, String model, String family) {
/* 223 */     List<String> isainfo = ExecutingCommand.runNative("isainfo -v");
/* 224 */     StringBuilder flags = new StringBuilder();
/* 225 */     for (String line : isainfo) {
/* 226 */       if (line.startsWith("32-bit"))
/*     */         break; 
/* 228 */       if (!line.startsWith("64-bit")) {
/* 229 */         flags.append(' ').append(line.trim());
/*     */       }
/*     */     } 
/* 232 */     return createProcessorID(stepping, model, family, ParseUtil.whitespaces.split(flags.toString().toLowerCase()));
/*     */   }
/*     */ 
/*     */   
/*     */   public long queryContextSwitches() {
/* 237 */     long swtch = 0L;
/* 238 */     List<String> kstat = ExecutingCommand.runNative("kstat -p cpu_stat:::/pswitch\\\\|inv_swtch/");
/* 239 */     for (String s : kstat) {
/* 240 */       swtch += ParseUtil.parseLastLong(s, 0L);
/*     */     }
/* 242 */     return (swtch > 0L) ? swtch : -1L;
/*     */   }
/*     */ 
/*     */   
/*     */   public long queryInterrupts() {
/* 247 */     long intr = 0L;
/* 248 */     List<String> kstat = ExecutingCommand.runNative("kstat -p cpu_stat:::/intr/");
/* 249 */     for (String s : kstat) {
/* 250 */       intr += ParseUtil.parseLastLong(s, 0L);
/*     */     }
/* 252 */     return (intr > 0L) ? intr : -1L;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platfor\\unix\solaris\SolarisCentralProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */