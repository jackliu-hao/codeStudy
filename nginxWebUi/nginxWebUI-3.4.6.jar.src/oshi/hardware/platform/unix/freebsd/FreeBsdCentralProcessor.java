/*     */ package oshi.hardware.platform.unix.freebsd;
/*     */ 
/*     */ import com.sun.jna.Memory;
/*     */ import com.sun.jna.Native;
/*     */ import com.sun.jna.Pointer;
/*     */ import com.sun.jna.Structure;
/*     */ import com.sun.jna.ptr.IntByReference;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.hardware.CentralProcessor;
/*     */ import oshi.hardware.common.AbstractCentralProcessor;
/*     */ import oshi.jna.platform.unix.freebsd.FreeBsdLibc;
/*     */ import oshi.util.ExecutingCommand;
/*     */ import oshi.util.FileUtil;
/*     */ import oshi.util.ParseUtil;
/*     */ import oshi.util.platform.unix.freebsd.BsdSysctlUtil;
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
/*     */ final class FreeBsdCentralProcessor
/*     */   extends AbstractCentralProcessor
/*     */ {
/*  55 */   private static final Logger LOG = LoggerFactory.getLogger(FreeBsdCentralProcessor.class);
/*     */   
/*  57 */   private static final Pattern CPUMASK = Pattern.compile(".*<cpu\\s.*mask=\"(?:0x)?(\\p{XDigit}+)\".*>.*</cpu>.*");
/*     */ 
/*     */ 
/*     */   
/*     */   protected CentralProcessor.ProcessorIdentifier queryProcessorId() {
/*  62 */     Pattern identifierPattern = Pattern.compile("Origin=\"([^\"]*)\".*Id=(\\S+).*Family=(\\S+).*Model=(\\S+).*Stepping=(\\S+).*");
/*  63 */     Pattern featuresPattern = Pattern.compile("Features=(\\S+)<.*");
/*     */     
/*  65 */     String cpuVendor = "";
/*  66 */     String cpuName = BsdSysctlUtil.sysctl("hw.model", "");
/*  67 */     String cpuFamily = "";
/*  68 */     String cpuModel = "";
/*  69 */     String cpuStepping = "";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  75 */     long processorIdBits = 0L;
/*  76 */     List<String> cpuInfo = FileUtil.readFile("/var/run/dmesg.boot");
/*  77 */     for (String line : cpuInfo) {
/*  78 */       line = line.trim();
/*     */       
/*  80 */       if (line.startsWith("CPU:") && cpuName.isEmpty()) {
/*  81 */         cpuName = line.replace("CPU:", "").trim(); continue;
/*  82 */       }  if (line.startsWith("Origin=")) {
/*  83 */         Matcher m = identifierPattern.matcher(line);
/*  84 */         if (m.matches()) {
/*  85 */           cpuVendor = m.group(1);
/*  86 */           processorIdBits |= Long.decode(m.group(2)).longValue();
/*  87 */           cpuFamily = Integer.decode(m.group(3)).toString();
/*  88 */           cpuModel = Integer.decode(m.group(4)).toString();
/*  89 */           cpuStepping = Integer.decode(m.group(5)).toString();
/*     */         }  continue;
/*  91 */       }  if (line.startsWith("Features=")) {
/*  92 */         Matcher m = featuresPattern.matcher(line);
/*  93 */         if (m.matches()) {
/*  94 */           processorIdBits |= Long.decode(m.group(1)).longValue() << 32L;
/*     */         }
/*     */         
/*     */         break;
/*     */       } 
/*     */     } 
/* 100 */     boolean cpu64bit = ExecutingCommand.getFirstAnswer("uname -m").trim().contains("64");
/* 101 */     String processorID = getProcessorIDfromDmiDecode(processorIdBits);
/*     */     
/* 103 */     return new CentralProcessor.ProcessorIdentifier(cpuVendor, cpuName, cpuFamily, cpuModel, cpuStepping, processorID, cpu64bit);
/*     */   }
/*     */ 
/*     */   
/*     */   protected List<CentralProcessor.LogicalProcessor> initProcessorCounts() {
/* 108 */     List<CentralProcessor.LogicalProcessor> logProcs = parseTopology();
/*     */     
/* 110 */     if (logProcs.isEmpty()) {
/* 111 */       logProcs.add(new CentralProcessor.LogicalProcessor(0, 0, 0));
/*     */     }
/* 113 */     return logProcs;
/*     */   }
/*     */   
/*     */   private static List<CentralProcessor.LogicalProcessor> parseTopology() {
/* 117 */     String[] topology = BsdSysctlUtil.sysctl("kern.sched.topology_spec", "").split("\\n|\\r");
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
/* 140 */     long group1 = 1L;
/* 141 */     List<Long> group2 = new ArrayList<>();
/* 142 */     List<Long> group3 = new ArrayList<>();
/* 143 */     int groupLevel = 0;
/* 144 */     for (String topo : topology) {
/* 145 */       if (topo.contains("<group level=")) {
/* 146 */         groupLevel++;
/* 147 */       } else if (topo.contains("</group>")) {
/* 148 */         groupLevel--;
/* 149 */       } else if (topo.contains("<cpu")) {
/*     */         
/* 151 */         Matcher m = CPUMASK.matcher(topo);
/* 152 */         if (m.matches())
/*     */         {
/*     */           
/* 155 */           switch (groupLevel) {
/*     */             case 1:
/* 157 */               group1 = Long.parseLong(m.group(1), 16);
/*     */               break;
/*     */             case 2:
/* 160 */               group2.add(Long.valueOf(Long.parseLong(m.group(1), 16)));
/*     */               break;
/*     */             case 3:
/* 163 */               group3.add(Long.valueOf(Long.parseLong(m.group(1), 16)));
/*     */               break;
/*     */           } 
/*     */ 
/*     */         
/*     */         }
/*     */       } 
/*     */     } 
/* 171 */     return matchBitmasks(group1, group2, group3);
/*     */   }
/*     */   
/*     */   private static List<CentralProcessor.LogicalProcessor> matchBitmasks(long group1, List<Long> group2, List<Long> group3) {
/* 175 */     List<CentralProcessor.LogicalProcessor> logProcs = new ArrayList<>();
/*     */     
/* 177 */     int lowBit = Long.numberOfTrailingZeros(group1);
/* 178 */     int hiBit = 63 - Long.numberOfLeadingZeros(group1);
/*     */     
/* 180 */     for (int i = lowBit; i <= hiBit; i++) {
/* 181 */       if ((group1 & 1L << i) > 0L) {
/* 182 */         int numaNode = 0;
/*     */         
/* 184 */         CentralProcessor.LogicalProcessor logProc = new CentralProcessor.LogicalProcessor(i, getMatchingBitmask(group3, i), getMatchingBitmask(group2, i), numaNode);
/* 185 */         logProcs.add(logProc);
/*     */       } 
/*     */     } 
/* 188 */     return logProcs;
/*     */   }
/*     */   
/*     */   private static int getMatchingBitmask(List<Long> bitmasks, int lp) {
/* 192 */     for (int j = 0; j < bitmasks.size(); j++) {
/* 193 */       if ((((Long)bitmasks.get(j)).longValue() & 1L << lp) > 0L) {
/* 194 */         return j;
/*     */       }
/*     */     } 
/* 197 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public long[] querySystemCpuLoadTicks() {
/* 202 */     long[] ticks = new long[(CentralProcessor.TickType.values()).length];
/* 203 */     FreeBsdLibc.CpTime cpTime = new FreeBsdLibc.CpTime();
/* 204 */     BsdSysctlUtil.sysctl("kern.cp_time", (Structure)cpTime);
/* 205 */     ticks[CentralProcessor.TickType.USER.getIndex()] = cpTime.cpu_ticks[0];
/* 206 */     ticks[CentralProcessor.TickType.NICE.getIndex()] = cpTime.cpu_ticks[1];
/* 207 */     ticks[CentralProcessor.TickType.SYSTEM.getIndex()] = cpTime.cpu_ticks[2];
/* 208 */     ticks[CentralProcessor.TickType.IRQ.getIndex()] = cpTime.cpu_ticks[3];
/* 209 */     ticks[CentralProcessor.TickType.IDLE.getIndex()] = cpTime.cpu_ticks[4];
/* 210 */     return ticks;
/*     */   }
/*     */ 
/*     */   
/*     */   public long[] queryCurrentFreq() {
/* 215 */     long freq = BsdSysctlUtil.sysctl("dev.cpu.0.freq", -1L);
/* 216 */     if (freq > 0L) {
/*     */       
/* 218 */       freq *= 1000000L;
/*     */     } else {
/* 220 */       freq = BsdSysctlUtil.sysctl("machdep.tsc_freq", -1L);
/*     */     } 
/* 222 */     long[] freqs = new long[getLogicalProcessorCount()];
/* 223 */     Arrays.fill(freqs, freq);
/* 224 */     return freqs;
/*     */   }
/*     */ 
/*     */   
/*     */   public long queryMaxFreq() {
/* 229 */     long max = -1L;
/* 230 */     String freqLevels = BsdSysctlUtil.sysctl("dev.cpu.0.freq_levels", "");
/*     */     
/* 232 */     for (String s : ParseUtil.whitespaces.split(freqLevels)) {
/* 233 */       long freq = ParseUtil.parseLongOrDefault(s.split("/")[0], -1L);
/* 234 */       if (max < freq) {
/* 235 */         max = freq;
/*     */       }
/*     */     } 
/* 238 */     if (max > 0L) {
/*     */       
/* 240 */       max *= 1000000L;
/*     */     } else {
/* 242 */       max = BsdSysctlUtil.sysctl("machdep.tsc_freq", -1L);
/*     */     } 
/* 244 */     return max;
/*     */   }
/*     */ 
/*     */   
/*     */   public double[] getSystemLoadAverage(int nelem) {
/* 249 */     if (nelem < 1 || nelem > 3) {
/* 250 */       throw new IllegalArgumentException("Must include from one to three elements.");
/*     */     }
/* 252 */     double[] average = new double[nelem];
/* 253 */     int retval = FreeBsdLibc.INSTANCE.getloadavg(average, nelem);
/* 254 */     if (retval < nelem) {
/* 255 */       for (int i = Math.max(retval, 0); i < average.length; i++) {
/* 256 */         average[i] = -1.0D;
/*     */       }
/*     */     }
/* 259 */     return average;
/*     */   }
/*     */ 
/*     */   
/*     */   public long[][] queryProcessorCpuLoadTicks() {
/* 264 */     long[][] ticks = new long[getLogicalProcessorCount()][(CentralProcessor.TickType.values()).length];
/*     */ 
/*     */     
/* 267 */     long size = (new FreeBsdLibc.CpTime()).size();
/* 268 */     long arraySize = size * getLogicalProcessorCount();
/* 269 */     Memory memory = new Memory(arraySize);
/* 270 */     String name = "kern.cp_times";
/*     */     
/* 272 */     if (0 != FreeBsdLibc.INSTANCE.sysctlbyname(name, (Pointer)memory, new IntByReference((int)arraySize), null, 0)) {
/* 273 */       LOG.error("Failed syctl call: {}, Error code: {}", name, Integer.valueOf(Native.getLastError()));
/* 274 */       return ticks;
/*     */     } 
/*     */     
/* 277 */     for (int cpu = 0; cpu < getLogicalProcessorCount(); cpu++) {
/* 278 */       ticks[cpu][CentralProcessor.TickType.USER.getIndex()] = memory
/* 279 */         .getLong(size * cpu + (0 * FreeBsdLibc.UINT64_SIZE));
/* 280 */       ticks[cpu][CentralProcessor.TickType.NICE.getIndex()] = memory
/* 281 */         .getLong(size * cpu + (1 * FreeBsdLibc.UINT64_SIZE));
/* 282 */       ticks[cpu][CentralProcessor.TickType.SYSTEM.getIndex()] = memory
/* 283 */         .getLong(size * cpu + (2 * FreeBsdLibc.UINT64_SIZE));
/* 284 */       ticks[cpu][CentralProcessor.TickType.IRQ.getIndex()] = memory.getLong(size * cpu + (3 * FreeBsdLibc.UINT64_SIZE));
/* 285 */       ticks[cpu][CentralProcessor.TickType.IDLE.getIndex()] = memory
/* 286 */         .getLong(size * cpu + (4 * FreeBsdLibc.UINT64_SIZE));
/*     */     } 
/* 288 */     return ticks;
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
/*     */   private static String getProcessorIDfromDmiDecode(long processorID) {
/* 300 */     boolean procInfo = false;
/* 301 */     String marker = "Processor Information";
/* 302 */     for (String checkLine : ExecutingCommand.runNative("dmidecode -t system")) {
/* 303 */       if (!procInfo && checkLine.contains(marker)) {
/* 304 */         marker = "ID:";
/* 305 */         procInfo = true; continue;
/* 306 */       }  if (procInfo && checkLine.contains(marker)) {
/* 307 */         return checkLine.split(marker)[1].trim();
/*     */       }
/*     */     } 
/*     */     
/* 311 */     return String.format("%016X", new Object[] { Long.valueOf(processorID) });
/*     */   }
/*     */ 
/*     */   
/*     */   public long queryContextSwitches() {
/* 316 */     String name = "vm.stats.sys.v_swtch";
/* 317 */     IntByReference size = new IntByReference(FreeBsdLibc.INT_SIZE);
/* 318 */     Memory memory = new Memory(size.getValue());
/* 319 */     if (0 != FreeBsdLibc.INSTANCE.sysctlbyname(name, (Pointer)memory, size, null, 0)) {
/* 320 */       return -1L;
/*     */     }
/* 322 */     return ParseUtil.unsignedIntToLong(memory.getInt(0L));
/*     */   }
/*     */ 
/*     */   
/*     */   public long queryInterrupts() {
/* 327 */     String name = "vm.stats.sys.v_intr";
/* 328 */     IntByReference size = new IntByReference(FreeBsdLibc.INT_SIZE);
/* 329 */     Memory memory = new Memory(size.getValue());
/* 330 */     if (0 != FreeBsdLibc.INSTANCE.sysctlbyname(name, (Pointer)memory, size, null, 0)) {
/* 331 */       return -1L;
/*     */     }
/* 333 */     return ParseUtil.unsignedIntToLong(memory.getInt(0L));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platfor\\unix\freebsd\FreeBsdCentralProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */