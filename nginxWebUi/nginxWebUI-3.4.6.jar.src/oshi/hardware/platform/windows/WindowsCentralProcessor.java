/*     */ package oshi.hardware.platform.windows;
/*     */ 
/*     */ import com.sun.jna.Memory;
/*     */ import com.sun.jna.Native;
/*     */ import com.sun.jna.Pointer;
/*     */ import com.sun.jna.platform.win32.Advapi32Util;
/*     */ import com.sun.jna.platform.win32.COM.WbemcliUtil;
/*     */ import com.sun.jna.platform.win32.Kernel32;
/*     */ import com.sun.jna.platform.win32.VersionHelpers;
/*     */ import com.sun.jna.platform.win32.WinBase;
/*     */ import com.sun.jna.platform.win32.WinReg;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.driver.windows.LogicalProcessorInformation;
/*     */ import oshi.driver.windows.perfmon.ProcessorInformation;
/*     */ import oshi.driver.windows.perfmon.SystemInformation;
/*     */ import oshi.driver.windows.wmi.Win32Processor;
/*     */ import oshi.hardware.CentralProcessor;
/*     */ import oshi.hardware.common.AbstractCentralProcessor;
/*     */ import oshi.jna.platform.windows.PowrProf;
/*     */ import oshi.util.ParseUtil;
/*     */ import oshi.util.platform.windows.WmiUtil;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @ThreadSafe
/*     */ final class WindowsCentralProcessor
/*     */   extends AbstractCentralProcessor
/*     */ {
/*  70 */   private static final Logger LOG = LoggerFactory.getLogger(WindowsCentralProcessor.class);
/*     */ 
/*     */ 
/*     */   
/*     */   private Map<String, Integer> numaNodeProcToLogicalProcMap;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected CentralProcessor.ProcessorIdentifier queryProcessorId() {
/*  80 */     String processorID, cpuVendor = "";
/*  81 */     String cpuName = "";
/*  82 */     String cpuIdentifier = "";
/*  83 */     String cpuFamily = "";
/*  84 */     String cpuModel = "";
/*  85 */     String cpuStepping = "";
/*     */     
/*  87 */     boolean cpu64bit = false;
/*     */     
/*  89 */     String cpuRegistryRoot = "HARDWARE\\DESCRIPTION\\System\\CentralProcessor\\";
/*  90 */     String[] processorIds = Advapi32Util.registryGetKeys(WinReg.HKEY_LOCAL_MACHINE, "HARDWARE\\DESCRIPTION\\System\\CentralProcessor\\");
/*  91 */     if (processorIds.length > 0) {
/*  92 */       String cpuRegistryPath = "HARDWARE\\DESCRIPTION\\System\\CentralProcessor\\" + processorIds[0];
/*  93 */       cpuVendor = Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, cpuRegistryPath, "VendorIdentifier");
/*     */       
/*  95 */       cpuName = Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, cpuRegistryPath, "ProcessorNameString");
/*     */       
/*  97 */       cpuIdentifier = Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, cpuRegistryPath, "Identifier");
/*     */     } 
/*     */     
/* 100 */     if (!cpuIdentifier.isEmpty()) {
/* 101 */       cpuFamily = parseIdentifier(cpuIdentifier, "Family");
/* 102 */       cpuModel = parseIdentifier(cpuIdentifier, "Model");
/* 103 */       cpuStepping = parseIdentifier(cpuIdentifier, "Stepping");
/*     */     } 
/* 105 */     WinBase.SYSTEM_INFO sysinfo = new WinBase.SYSTEM_INFO();
/* 106 */     Kernel32.INSTANCE.GetNativeSystemInfo(sysinfo);
/* 107 */     int processorArchitecture = sysinfo.processorArchitecture.pi.wProcessorArchitecture.intValue();
/* 108 */     if (processorArchitecture == 9 || processorArchitecture == 12 || processorArchitecture == 6)
/*     */     {
/*     */       
/* 111 */       cpu64bit = true;
/*     */     }
/* 113 */     WbemcliUtil.WmiResult<Win32Processor.ProcessorIdProperty> processorId = Win32Processor.queryProcessorId();
/* 114 */     if (processorId.getResultCount() > 0) {
/* 115 */       processorID = WmiUtil.getString(processorId, (Enum)Win32Processor.ProcessorIdProperty.PROCESSORID, 0);
/*     */     } else {
/*     */       
/* 118 */       (new String[1])[0] = "ia64"; processorID = createProcessorID(cpuStepping, cpuModel, cpuFamily, cpu64bit ? new String[1] : new String[0]);
/*     */     } 
/* 120 */     return new CentralProcessor.ProcessorIdentifier(cpuVendor, cpuName, cpuFamily, cpuModel, cpuStepping, processorID, cpu64bit);
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
/*     */   private static String parseIdentifier(String identifier, String key) {
/* 133 */     String[] idSplit = ParseUtil.whitespaces.split(identifier);
/* 134 */     boolean found = false;
/* 135 */     for (String s : idSplit) {
/*     */       
/* 137 */       if (found) {
/* 138 */         return s;
/*     */       }
/* 140 */       found = s.equals(key);
/*     */     } 
/*     */     
/* 143 */     return "";
/*     */   }
/*     */ 
/*     */   
/*     */   protected List<CentralProcessor.LogicalProcessor> initProcessorCounts() {
/* 148 */     if (VersionHelpers.IsWindows7OrGreater()) {
/* 149 */       List<CentralProcessor.LogicalProcessor> logProcs = LogicalProcessorInformation.getLogicalProcessorInformationEx();
/*     */       
/* 151 */       this.numaNodeProcToLogicalProcMap = new HashMap<>();
/* 152 */       int lp = 0;
/* 153 */       for (CentralProcessor.LogicalProcessor logProc : logProcs) {
/* 154 */         this.numaNodeProcToLogicalProcMap
/* 155 */           .put(String.format("%d,%d", new Object[] { Integer.valueOf(logProc.getNumaNode()), Integer.valueOf(logProc.getProcessorNumber()) }), Integer.valueOf(lp++));
/*     */       } 
/* 157 */       return logProcs;
/*     */     } 
/* 159 */     return LogicalProcessorInformation.getLogicalProcessorInformation();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public long[] querySystemCpuLoadTicks() {
/* 165 */     long[] ticks = new long[(CentralProcessor.TickType.values()).length];
/* 166 */     WinBase.FILETIME lpIdleTime = new WinBase.FILETIME();
/* 167 */     WinBase.FILETIME lpKernelTime = new WinBase.FILETIME();
/* 168 */     WinBase.FILETIME lpUserTime = new WinBase.FILETIME();
/* 169 */     if (!Kernel32.INSTANCE.GetSystemTimes(lpIdleTime, lpKernelTime, lpUserTime)) {
/* 170 */       LOG.error("Failed to update system idle/kernel/user times. Error code: {}", Integer.valueOf(Native.getLastError()));
/* 171 */       return ticks;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 180 */     Map<ProcessorInformation.SystemTickCountProperty, Long> valueMap = ProcessorInformation.querySystemCounters();
/* 181 */     ticks[CentralProcessor.TickType.IRQ.getIndex()] = ((Long)valueMap.getOrDefault(ProcessorInformation.SystemTickCountProperty.PERCENTINTERRUPTTIME, (V)Long.valueOf(0L))).longValue() / 10000L;
/*     */     
/* 183 */     ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()] = ((Long)valueMap.getOrDefault(ProcessorInformation.SystemTickCountProperty.PERCENTDPCTIME, (V)Long.valueOf(0L))).longValue() / 10000L;
/*     */ 
/*     */     
/* 186 */     ticks[CentralProcessor.TickType.IDLE.getIndex()] = lpIdleTime.toDWordLong().longValue() / 10000L;
/* 187 */     ticks[CentralProcessor.TickType.SYSTEM.getIndex()] = lpKernelTime.toDWordLong().longValue() / 10000L - ticks[CentralProcessor.TickType.IDLE
/* 188 */         .getIndex()];
/* 189 */     ticks[CentralProcessor.TickType.USER.getIndex()] = lpUserTime.toDWordLong().longValue() / 10000L;
/*     */     
/* 191 */     ticks[CentralProcessor.TickType.SYSTEM.getIndex()] = ticks[CentralProcessor.TickType.SYSTEM.getIndex()] - ticks[CentralProcessor.TickType.IRQ.getIndex()] + ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
/* 192 */     return ticks;
/*     */   }
/*     */ 
/*     */   
/*     */   public long[] queryCurrentFreq() {
/* 197 */     if (VersionHelpers.IsWindows7OrGreater()) {
/*     */       
/* 199 */       Pair<List<String>, Map<ProcessorInformation.ProcessorFrequencyProperty, List<Long>>> instanceValuePair = ProcessorInformation.queryFrequencyCounters();
/* 200 */       List<String> instances = (List<String>)instanceValuePair.getA();
/* 201 */       Map<ProcessorInformation.ProcessorFrequencyProperty, List<Long>> valueMap = (Map<ProcessorInformation.ProcessorFrequencyProperty, List<Long>>)instanceValuePair.getB();
/* 202 */       List<Long> percentMaxList = valueMap.get(ProcessorInformation.ProcessorFrequencyProperty.PERCENTOFMAXIMUMFREQUENCY);
/* 203 */       if (!instances.isEmpty()) {
/* 204 */         long maxFreq = getMaxFreq();
/* 205 */         long[] freqs = new long[getLogicalProcessorCount()];
/* 206 */         for (int p = 0; p < instances.size(); p++) {
/*     */ 
/*     */           
/* 209 */           int cpu = ((String)instances.get(p)).contains(",") ? ((Integer)this.numaNodeProcToLogicalProcMap.getOrDefault(instances.get(p), Integer.valueOf(0))).intValue() : ParseUtil.parseIntOrDefault(instances.get(p), 0);
/* 210 */           if (cpu < getLogicalProcessorCount())
/*     */           {
/*     */             
/* 213 */             freqs[cpu] = ((Long)percentMaxList.get(cpu)).longValue() * maxFreq / 100L; } 
/*     */         } 
/* 215 */         return freqs;
/*     */       } 
/*     */     } 
/*     */     
/* 219 */     return queryNTPower(2);
/*     */   }
/*     */ 
/*     */   
/*     */   public long queryMaxFreq() {
/* 224 */     long[] freqs = queryNTPower(1);
/* 225 */     return Arrays.stream(freqs).max().orElse(-1L);
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
/*     */   private long[] queryNTPower(int fieldIndex) {
/* 238 */     PowrProf.ProcessorPowerInformation ppi = new PowrProf.ProcessorPowerInformation();
/* 239 */     long[] freqs = new long[getLogicalProcessorCount()];
/* 240 */     int bufferSize = ppi.size() * freqs.length;
/* 241 */     Memory mem = new Memory(bufferSize);
/* 242 */     if (0 != PowrProf.INSTANCE.CallNtPowerInformation(11, null, 0, (Pointer)mem, bufferSize)) {
/*     */       
/* 244 */       LOG.error("Unable to get Processor Information");
/* 245 */       Arrays.fill(freqs, -1L);
/* 246 */       return freqs;
/*     */     } 
/* 248 */     for (int i = 0; i < freqs.length; i++) {
/* 249 */       ppi = new PowrProf.ProcessorPowerInformation(mem.share(i * ppi.size()));
/* 250 */       if (fieldIndex == 1) {
/* 251 */         freqs[i] = ppi.maxMhz * 1000000L;
/* 252 */       } else if (fieldIndex == 2) {
/* 253 */         freqs[i] = ppi.currentMhz * 1000000L;
/*     */       } else {
/* 255 */         freqs[i] = -1L;
/*     */       } 
/*     */     } 
/* 258 */     return freqs;
/*     */   }
/*     */ 
/*     */   
/*     */   public double[] getSystemLoadAverage(int nelem) {
/* 263 */     if (nelem < 1 || nelem > 3) {
/* 264 */       throw new IllegalArgumentException("Must include from one to three elements.");
/*     */     }
/* 266 */     double[] average = new double[nelem];
/*     */     
/* 268 */     for (int i = 0; i < average.length; i++) {
/* 269 */       average[i] = -1.0D;
/*     */     }
/* 271 */     return average;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public long[][] queryProcessorCpuLoadTicks() {
/* 277 */     Pair<List<String>, Map<ProcessorInformation.ProcessorTickCountProperty, List<Long>>> instanceValuePair = ProcessorInformation.queryProcessorCounters();
/* 278 */     List<String> instances = (List<String>)instanceValuePair.getA();
/* 279 */     Map<ProcessorInformation.ProcessorTickCountProperty, List<Long>> valueMap = (Map<ProcessorInformation.ProcessorTickCountProperty, List<Long>>)instanceValuePair.getB();
/* 280 */     List<Long> systemList = valueMap.get(ProcessorInformation.ProcessorTickCountProperty.PERCENTPRIVILEGEDTIME);
/* 281 */     List<Long> userList = valueMap.get(ProcessorInformation.ProcessorTickCountProperty.PERCENTUSERTIME);
/* 282 */     List<Long> irqList = valueMap.get(ProcessorInformation.ProcessorTickCountProperty.PERCENTINTERRUPTTIME);
/* 283 */     List<Long> softIrqList = valueMap.get(ProcessorInformation.ProcessorTickCountProperty.PERCENTDPCTIME);
/*     */     
/* 285 */     List<Long> idleList = valueMap.get(ProcessorInformation.ProcessorTickCountProperty.PERCENTPROCESSORTIME);
/*     */     
/* 287 */     long[][] ticks = new long[getLogicalProcessorCount()][(CentralProcessor.TickType.values()).length];
/* 288 */     if (instances.isEmpty() || systemList == null || userList == null || irqList == null || softIrqList == null || idleList == null)
/*     */     {
/* 290 */       return ticks;
/*     */     }
/* 292 */     for (int p = 0; p < instances.size(); p++) {
/*     */       
/* 294 */       int cpu = ((String)instances.get(p)).contains(",") ? ((Integer)this.numaNodeProcToLogicalProcMap.getOrDefault(instances.get(p), Integer.valueOf(0))).intValue() : ParseUtil.parseIntOrDefault(instances.get(p), 0);
/* 295 */       if (cpu < getLogicalProcessorCount()) {
/*     */ 
/*     */         
/* 298 */         ticks[cpu][CentralProcessor.TickType.SYSTEM.getIndex()] = ((Long)systemList.get(cpu)).longValue();
/* 299 */         ticks[cpu][CentralProcessor.TickType.USER.getIndex()] = ((Long)userList.get(cpu)).longValue();
/* 300 */         ticks[cpu][CentralProcessor.TickType.IRQ.getIndex()] = ((Long)irqList.get(cpu)).longValue();
/* 301 */         ticks[cpu][CentralProcessor.TickType.SOFTIRQ.getIndex()] = ((Long)softIrqList.get(cpu)).longValue();
/* 302 */         ticks[cpu][CentralProcessor.TickType.IDLE.getIndex()] = ((Long)idleList.get(cpu)).longValue();
/*     */ 
/*     */ 
/*     */         
/* 306 */         ticks[cpu][CentralProcessor.TickType.SYSTEM.getIndex()] = ticks[cpu][CentralProcessor.TickType.SYSTEM.getIndex()] - ticks[cpu][CentralProcessor.TickType.IRQ.getIndex()] + ticks[cpu][CentralProcessor.TickType.SOFTIRQ
/* 307 */             .getIndex()];
/*     */ 
/*     */ 
/*     */         
/* 311 */         ticks[cpu][CentralProcessor.TickType.SYSTEM.getIndex()] = ticks[cpu][CentralProcessor.TickType.SYSTEM.getIndex()] / 10000L;
/* 312 */         ticks[cpu][CentralProcessor.TickType.USER.getIndex()] = ticks[cpu][CentralProcessor.TickType.USER.getIndex()] / 10000L;
/* 313 */         ticks[cpu][CentralProcessor.TickType.IRQ.getIndex()] = ticks[cpu][CentralProcessor.TickType.IRQ.getIndex()] / 10000L;
/* 314 */         ticks[cpu][CentralProcessor.TickType.SOFTIRQ.getIndex()] = ticks[cpu][CentralProcessor.TickType.SOFTIRQ.getIndex()] / 10000L;
/* 315 */         ticks[cpu][CentralProcessor.TickType.IDLE.getIndex()] = ticks[cpu][CentralProcessor.TickType.IDLE.getIndex()] / 10000L;
/*     */       } 
/*     */     } 
/* 318 */     return ticks;
/*     */   }
/*     */ 
/*     */   
/*     */   public long queryContextSwitches() {
/* 323 */     return ((Long)SystemInformation.queryContextSwitchCounters().getOrDefault(SystemInformation.ContextSwitchProperty.CONTEXTSWITCHESPERSEC, 
/* 324 */         Long.valueOf(0L))).longValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public long queryInterrupts() {
/* 329 */     return ((Long)ProcessorInformation.queryInterruptCounters().getOrDefault(ProcessorInformation.InterruptsProperty.INTERRUPTSPERSEC, Long.valueOf(0L))).longValue();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platform\windows\WindowsCentralProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */