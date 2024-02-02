/*     */ package oshi.hardware.platform.linux;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.driver.linux.Lshw;
/*     */ import oshi.driver.linux.proc.CpuStat;
/*     */ import oshi.hardware.CentralProcessor;
/*     */ import oshi.hardware.common.AbstractCentralProcessor;
/*     */ import oshi.jna.platform.linux.LinuxLibc;
/*     */ import oshi.software.os.linux.LinuxOperatingSystem;
/*     */ import oshi.util.ExecutingCommand;
/*     */ import oshi.util.FileUtil;
/*     */ import oshi.util.GlobalConfig;
/*     */ import oshi.util.ParseUtil;
/*     */ import oshi.util.platform.linux.ProcPath;
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
/*     */ final class LinuxCentralProcessor
/*     */   extends AbstractCentralProcessor
/*     */ {
/*     */   private static final String CPUFREQ_PATH = "oshi.cpu.freq.path";
/*     */   
/*     */   protected CentralProcessor.ProcessorIdentifier queryProcessorId() {
/*  57 */     String cpuVendor = "";
/*  58 */     String cpuName = "";
/*  59 */     String cpuFamily = "";
/*  60 */     String cpuModel = "";
/*  61 */     String cpuStepping = "";
/*     */     
/*  63 */     boolean cpu64bit = false;
/*     */     
/*  65 */     StringBuilder armStepping = new StringBuilder();
/*  66 */     String[] flags = new String[0];
/*  67 */     List<String> cpuInfo = FileUtil.readFile(ProcPath.CPUINFO);
/*  68 */     for (String line : cpuInfo) {
/*  69 */       String[] splitLine = ParseUtil.whitespacesColonWhitespace.split(line);
/*  70 */       if (splitLine.length < 2) {
/*     */         
/*  72 */         if (line.startsWith("CPU architecture: ")) {
/*  73 */           cpuFamily = line.replace("CPU architecture: ", "").trim();
/*     */         }
/*     */         continue;
/*     */       } 
/*  77 */       switch (splitLine[0]) {
/*     */         case "vendor_id":
/*     */         case "CPU implementer":
/*  80 */           cpuVendor = splitLine[1];
/*     */         
/*     */         case "model name":
/*  83 */           cpuName = splitLine[1];
/*     */         
/*     */         case "flags":
/*  86 */           flags = splitLine[1].toLowerCase().split(" ");
/*  87 */           for (String flag : flags) {
/*  88 */             if ("lm".equals(flag)) {
/*  89 */               cpu64bit = true;
/*     */               break;
/*     */             } 
/*     */           } 
/*     */         
/*     */         case "stepping":
/*  95 */           cpuStepping = splitLine[1];
/*     */         
/*     */         case "CPU variant":
/*  98 */           if (!armStepping.toString().startsWith("r")) {
/*  99 */             armStepping.insert(0, "r" + splitLine[1]);
/*     */           }
/*     */         
/*     */         case "CPU revision":
/* 103 */           if (!armStepping.toString().contains("p")) {
/* 104 */             armStepping.append('p').append(splitLine[1]);
/*     */           }
/*     */         
/*     */         case "model":
/*     */         case "CPU part":
/* 109 */           cpuModel = splitLine[1];
/*     */         
/*     */         case "cpu family":
/* 112 */           cpuFamily = splitLine[1];
/*     */       } 
/*     */ 
/*     */ 
/*     */     
/*     */     } 
/* 118 */     if (cpuStepping.isEmpty()) {
/* 119 */       cpuStepping = armStepping.toString();
/*     */     }
/* 121 */     String processorID = getProcessorID(cpuVendor, cpuStepping, cpuModel, cpuFamily, flags);
/* 122 */     if (cpuVendor.startsWith("0x")) {
/* 123 */       List<String> lscpu = ExecutingCommand.runNative("lscpu");
/* 124 */       for (String line : lscpu) {
/* 125 */         if (line.startsWith("Architecture:")) {
/* 126 */           cpuVendor = line.replace("Architecture:", "").trim();
/*     */         }
/*     */       } 
/*     */     } 
/* 130 */     return new CentralProcessor.ProcessorIdentifier(cpuVendor, cpuName, cpuFamily, cpuModel, cpuStepping, processorID, cpu64bit);
/*     */   }
/*     */ 
/*     */   
/*     */   protected List<CentralProcessor.LogicalProcessor> initProcessorCounts() {
/* 135 */     Map<Integer, Integer> numaNodeMap = mapNumaNodes();
/* 136 */     List<String> procCpu = FileUtil.readFile(ProcPath.CPUINFO);
/* 137 */     List<CentralProcessor.LogicalProcessor> logProcs = new ArrayList<>();
/* 138 */     int currentProcessor = 0;
/* 139 */     int currentCore = 0;
/* 140 */     int currentPackage = 0;
/* 141 */     boolean first = true;
/* 142 */     for (String cpu : procCpu) {
/*     */       
/* 144 */       if (cpu.startsWith("processor")) {
/* 145 */         if (!first) {
/* 146 */           logProcs.add(new CentralProcessor.LogicalProcessor(currentProcessor, currentCore, currentPackage, ((Integer)numaNodeMap
/* 147 */                 .getOrDefault(Integer.valueOf(currentProcessor), Integer.valueOf(0))).intValue()));
/*     */         } else {
/* 149 */           first = false;
/*     */         } 
/* 151 */         currentProcessor = ParseUtil.parseLastInt(cpu, 0); continue;
/* 152 */       }  if (cpu.startsWith("core id") || cpu.startsWith("cpu number")) {
/*     */         
/* 154 */         currentCore = ParseUtil.parseLastInt(cpu, 0); continue;
/* 155 */       }  if (cpu.startsWith("physical id")) {
/* 156 */         currentPackage = ParseUtil.parseLastInt(cpu, 0);
/*     */       }
/*     */     } 
/* 159 */     logProcs.add(new CentralProcessor.LogicalProcessor(currentProcessor, currentCore, currentPackage, ((Integer)numaNodeMap
/* 160 */           .getOrDefault(Integer.valueOf(currentProcessor), Integer.valueOf(0))).intValue()));
/*     */     
/* 162 */     return logProcs;
/*     */   }
/*     */   
/*     */   private static Map<Integer, Integer> mapNumaNodes() {
/* 166 */     Map<Integer, Integer> numaNodeMap = new HashMap<>();
/*     */     
/* 168 */     List<String> lscpu = ExecutingCommand.runNative("lscpu -p=cpu,node");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 174 */     for (String line : lscpu) {
/* 175 */       if (line.startsWith("#")) {
/*     */         continue;
/*     */       }
/* 178 */       String[] split = line.split(",");
/* 179 */       if (split.length == 2) {
/* 180 */         numaNodeMap.put(Integer.valueOf(ParseUtil.parseIntOrDefault(split[0], 0)), Integer.valueOf(ParseUtil.parseIntOrDefault(split[1], 0)));
/*     */       }
/*     */     } 
/* 183 */     return numaNodeMap;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public long[] querySystemCpuLoadTicks() {
/* 189 */     long[] ticks = CpuStat.getSystemCpuLoadTicks();
/* 190 */     long hz = LinuxOperatingSystem.getHz();
/* 191 */     for (int i = 0; i < ticks.length; i++) {
/* 192 */       ticks[i] = ticks[i] * 1000L / hz;
/*     */     }
/* 194 */     return ticks;
/*     */   }
/*     */ 
/*     */   
/*     */   public long[] queryCurrentFreq() {
/* 199 */     String cpuFreqPath = GlobalConfig.get("oshi.cpu.freq.path", "");
/* 200 */     long[] freqs = new long[getLogicalProcessorCount()];
/*     */     
/* 202 */     long max = 0L; int i;
/* 203 */     for (i = 0; i < freqs.length; i++) {
/* 204 */       freqs[i] = FileUtil.getLongFromFile(cpuFreqPath + "/cpu" + i + "/cpufreq/scaling_cur_freq");
/* 205 */       if (freqs[i] == 0L) {
/* 206 */         freqs[i] = FileUtil.getLongFromFile(cpuFreqPath + "/cpu" + i + "/cpufreq/cpuinfo_cur_freq");
/*     */       }
/* 208 */       if (max < freqs[i]) {
/* 209 */         max = freqs[i];
/*     */       }
/*     */     } 
/* 212 */     if (max > 0L) {
/*     */       
/* 214 */       for (i = 0; i < freqs.length; i++) {
/* 215 */         freqs[i] = freqs[i] * 1000L;
/*     */       }
/* 217 */       return freqs;
/*     */     } 
/*     */     
/* 220 */     Arrays.fill(freqs, -1L);
/* 221 */     List<String> cpuInfo = FileUtil.readFile(ProcPath.CPUINFO);
/* 222 */     int proc = 0;
/* 223 */     for (String s : cpuInfo) {
/* 224 */       if (s.toLowerCase().contains("cpu mhz")) {
/* 225 */         freqs[proc] = (long)(ParseUtil.parseLastDouble(s, 0.0D) * 1000000.0D);
/* 226 */         if (++proc >= freqs.length) {
/*     */           break;
/*     */         }
/*     */       } 
/*     */     } 
/* 231 */     return freqs;
/*     */   }
/*     */ 
/*     */   
/*     */   public long queryMaxFreq() {
/* 236 */     String cpuFreqPath = GlobalConfig.get("oshi.cpu.freq.path", "");
/* 237 */     long max = Arrays.stream(getCurrentFreq()).max().orElse(-1L);
/*     */ 
/*     */     
/* 240 */     File cpufreqdir = new File(cpuFreqPath + "/cpufreq");
/* 241 */     File[] policies = cpufreqdir.listFiles();
/* 242 */     if (policies != null) {
/* 243 */       for (int i = 0; i < policies.length; i++) {
/* 244 */         File f = policies[i];
/* 245 */         if (f.getName().startsWith("policy")) {
/* 246 */           long freq = FileUtil.getLongFromFile(cpuFreqPath + "/cpufreq/" + f.getName() + "/scaling_max_freq");
/* 247 */           if (freq == 0L) {
/* 248 */             freq = FileUtil.getLongFromFile(cpuFreqPath + "/cpufreq/" + f.getName() + "/cpuinfo_max_freq");
/*     */           }
/* 250 */           if (max < freq) {
/* 251 */             max = freq;
/*     */           }
/*     */         } 
/*     */       } 
/*     */     }
/* 256 */     if (max > 0L) {
/*     */       
/* 258 */       max *= 1000L;
/*     */ 
/*     */       
/* 261 */       long lshwMax = Lshw.queryCpuCapacity();
/* 262 */       return (lshwMax > max) ? lshwMax : max;
/*     */     } 
/* 264 */     return -1L;
/*     */   }
/*     */ 
/*     */   
/*     */   public double[] getSystemLoadAverage(int nelem) {
/* 269 */     if (nelem < 1 || nelem > 3) {
/* 270 */       throw new IllegalArgumentException("Must include from one to three elements.");
/*     */     }
/* 272 */     double[] average = new double[nelem];
/* 273 */     int retval = LinuxLibc.INSTANCE.getloadavg(average, nelem);
/* 274 */     if (retval < nelem) {
/* 275 */       for (int i = Math.max(retval, 0); i < average.length; i++) {
/* 276 */         average[i] = -1.0D;
/*     */       }
/*     */     }
/* 279 */     return average;
/*     */   }
/*     */ 
/*     */   
/*     */   public long[][] queryProcessorCpuLoadTicks() {
/* 284 */     long[][] ticks = CpuStat.getProcessorCpuLoadTicks(getLogicalProcessorCount());
/*     */     
/* 286 */     long hz = LinuxOperatingSystem.getHz();
/* 287 */     for (int i = 0; i < ticks.length; i++) {
/* 288 */       for (int j = 0; j < (ticks[i]).length; j++) {
/* 289 */         ticks[i][j] = ticks[i][j] * 1000L / hz;
/*     */       }
/*     */     } 
/* 292 */     return ticks;
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
/*     */ 
/*     */ 
/*     */   
/*     */   private static String getProcessorID(String vendor, String stepping, String model, String family, String[] flags) {
/* 313 */     boolean procInfo = false;
/* 314 */     String marker = "Processor Information";
/* 315 */     for (String checkLine : ExecutingCommand.runNative("dmidecode -t 4")) {
/* 316 */       if (!procInfo && checkLine.contains(marker)) {
/* 317 */         marker = "ID:";
/* 318 */         procInfo = true; continue;
/* 319 */       }  if (procInfo && checkLine.contains(marker)) {
/* 320 */         return checkLine.split(marker)[1].trim();
/*     */       }
/*     */     } 
/*     */     
/* 324 */     marker = "eax=";
/* 325 */     for (String checkLine : ExecutingCommand.runNative("cpuid -1r")) {
/* 326 */       if (checkLine.contains(marker) && checkLine.trim().startsWith("0x00000001")) {
/* 327 */         String eax = "";
/* 328 */         String edx = "";
/* 329 */         for (String register : ParseUtil.whitespaces.split(checkLine)) {
/* 330 */           if (register.startsWith("eax=")) {
/* 331 */             eax = ParseUtil.removeMatchingString(register, "eax=0x");
/* 332 */           } else if (register.startsWith("edx=")) {
/* 333 */             edx = ParseUtil.removeMatchingString(register, "edx=0x");
/*     */           } 
/*     */         } 
/* 336 */         return edx + eax;
/*     */       } 
/*     */     } 
/*     */     
/* 340 */     if (vendor.startsWith("0x")) {
/* 341 */       return createMIDR(vendor, stepping, model, family) + "00000000";
/*     */     }
/* 343 */     return createProcessorID(stepping, model, family, flags);
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
/*     */   private static String createMIDR(String vendor, String stepping, String model, String family) {
/* 360 */     int midrBytes = 0;
/*     */     
/* 362 */     if (stepping.startsWith("r") && stepping.contains("p")) {
/* 363 */       String[] rev = stepping.substring(1).split("p");
/*     */       
/* 365 */       midrBytes |= ParseUtil.parseLastInt(rev[1], 0);
/*     */       
/* 367 */       midrBytes |= ParseUtil.parseLastInt(rev[0], 0) << 20;
/*     */     } 
/*     */     
/* 370 */     midrBytes |= ParseUtil.parseLastInt(model, 0) << 4;
/*     */     
/* 372 */     midrBytes |= ParseUtil.parseLastInt(family, 0) << 16;
/*     */     
/* 374 */     midrBytes |= ParseUtil.parseLastInt(vendor, 0) << 24;
/*     */     
/* 376 */     return String.format("%08X", new Object[] { Integer.valueOf(midrBytes) });
/*     */   }
/*     */ 
/*     */   
/*     */   public long queryContextSwitches() {
/* 381 */     return CpuStat.getContextSwitches();
/*     */   }
/*     */ 
/*     */   
/*     */   public long queryInterrupts() {
/* 386 */     return CpuStat.getInterrupts();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platform\linux\LinuxCentralProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */