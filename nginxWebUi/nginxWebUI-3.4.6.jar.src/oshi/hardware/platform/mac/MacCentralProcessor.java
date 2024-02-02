/*     */ package oshi.hardware.platform.mac;
/*     */ 
/*     */ import com.sun.jna.Native;
/*     */ import com.sun.jna.Structure;
/*     */ import com.sun.jna.platform.mac.SystemB;
/*     */ import com.sun.jna.ptr.IntByReference;
/*     */ import com.sun.jna.ptr.PointerByReference;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.hardware.CentralProcessor;
/*     */ import oshi.hardware.common.AbstractCentralProcessor;
/*     */ import oshi.util.FormatUtil;
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
/*     */ @ThreadSafe
/*     */ final class MacCentralProcessor
/*     */   extends AbstractCentralProcessor
/*     */ {
/*  52 */   private static final Logger LOG = LoggerFactory.getLogger(MacCentralProcessor.class);
/*     */ 
/*     */   
/*     */   protected CentralProcessor.ProcessorIdentifier queryProcessorId() {
/*  56 */     String cpuVendor = SysctlUtil.sysctl("machdep.cpu.vendor", "");
/*  57 */     String cpuName = SysctlUtil.sysctl("machdep.cpu.brand_string", "");
/*  58 */     int i = SysctlUtil.sysctl("machdep.cpu.stepping", -1);
/*  59 */     String cpuStepping = (i < 0) ? "" : Integer.toString(i);
/*  60 */     i = SysctlUtil.sysctl("machdep.cpu.model", -1);
/*  61 */     String cpuModel = (i < 0) ? "" : Integer.toString(i);
/*  62 */     i = SysctlUtil.sysctl("machdep.cpu.family", -1);
/*  63 */     String cpuFamily = (i < 0) ? "" : Integer.toString(i);
/*  64 */     long processorIdBits = 0L;
/*  65 */     processorIdBits |= SysctlUtil.sysctl("machdep.cpu.signature", 0);
/*  66 */     processorIdBits |= (SysctlUtil.sysctl("machdep.cpu.feature_bits", 0L) & 0xFFFFFFFFFFFFFFFFL) << 32L;
/*  67 */     String processorID = String.format("%016X", new Object[] { Long.valueOf(processorIdBits) });
/*  68 */     boolean cpu64bit = (SysctlUtil.sysctl("hw.cpu64bit_capable", 0) != 0);
/*     */     
/*  70 */     return new CentralProcessor.ProcessorIdentifier(cpuVendor, cpuName, cpuFamily, cpuModel, cpuStepping, processorID, cpu64bit);
/*     */   }
/*     */ 
/*     */   
/*     */   protected List<CentralProcessor.LogicalProcessor> initProcessorCounts() {
/*  75 */     int logicalProcessorCount = SysctlUtil.sysctl("hw.logicalcpu", 1);
/*  76 */     int physicalProcessorCount = SysctlUtil.sysctl("hw.physicalcpu", 1);
/*  77 */     int physicalPackageCount = SysctlUtil.sysctl("hw.packages", 1);
/*  78 */     List<CentralProcessor.LogicalProcessor> logProcs = new ArrayList<>(logicalProcessorCount);
/*  79 */     for (int i = 0; i < logicalProcessorCount; i++) {
/*  80 */       logProcs.add(new CentralProcessor.LogicalProcessor(i, i * physicalProcessorCount / logicalProcessorCount, i * physicalPackageCount / logicalProcessorCount));
/*     */     }
/*     */     
/*  83 */     return logProcs;
/*     */   }
/*     */ 
/*     */   
/*     */   public long[] querySystemCpuLoadTicks() {
/*  88 */     long[] ticks = new long[(CentralProcessor.TickType.values()).length];
/*  89 */     int machPort = SystemB.INSTANCE.mach_host_self();
/*  90 */     SystemB.HostCpuLoadInfo cpuLoadInfo = new SystemB.HostCpuLoadInfo();
/*  91 */     if (0 != SystemB.INSTANCE.host_statistics(machPort, 3, (Structure)cpuLoadInfo, new IntByReference(cpuLoadInfo
/*  92 */           .size()))) {
/*  93 */       LOG.error("Failed to get System CPU ticks. Error code: {} ", Integer.valueOf(Native.getLastError()));
/*  94 */       return ticks;
/*     */     } 
/*     */     
/*  97 */     ticks[CentralProcessor.TickType.USER.getIndex()] = cpuLoadInfo.cpu_ticks[0];
/*  98 */     ticks[CentralProcessor.TickType.NICE.getIndex()] = cpuLoadInfo.cpu_ticks[3];
/*  99 */     ticks[CentralProcessor.TickType.SYSTEM.getIndex()] = cpuLoadInfo.cpu_ticks[1];
/* 100 */     ticks[CentralProcessor.TickType.IDLE.getIndex()] = cpuLoadInfo.cpu_ticks[2];
/*     */     
/* 102 */     return ticks;
/*     */   }
/*     */ 
/*     */   
/*     */   public long[] queryCurrentFreq() {
/* 107 */     long[] freqs = new long[getLogicalProcessorCount()];
/* 108 */     Arrays.fill(freqs, SysctlUtil.sysctl("hw.cpufrequency", -1L));
/* 109 */     return freqs;
/*     */   }
/*     */ 
/*     */   
/*     */   public long queryMaxFreq() {
/* 114 */     return SysctlUtil.sysctl("hw.cpufrequency_max", -1L);
/*     */   }
/*     */ 
/*     */   
/*     */   public double[] getSystemLoadAverage(int nelem) {
/* 119 */     if (nelem < 1 || nelem > 3) {
/* 120 */       throw new IllegalArgumentException("Must include from one to three elements.");
/*     */     }
/* 122 */     double[] average = new double[nelem];
/* 123 */     int retval = SystemB.INSTANCE.getloadavg(average, nelem);
/* 124 */     if (retval < nelem) {
/* 125 */       Arrays.fill(average, -1.0D);
/*     */     }
/* 127 */     return average;
/*     */   }
/*     */ 
/*     */   
/*     */   public long[][] queryProcessorCpuLoadTicks() {
/* 132 */     long[][] ticks = new long[getLogicalProcessorCount()][(CentralProcessor.TickType.values()).length];
/*     */     
/* 134 */     int machPort = SystemB.INSTANCE.mach_host_self();
/*     */     
/* 136 */     IntByReference procCount = new IntByReference();
/* 137 */     PointerByReference procCpuLoadInfo = new PointerByReference();
/* 138 */     IntByReference procInfoCount = new IntByReference();
/* 139 */     if (0 != SystemB.INSTANCE.host_processor_info(machPort, 2, procCount, procCpuLoadInfo, procInfoCount)) {
/*     */       
/* 141 */       LOG.error("Failed to update CPU Load. Error code: {}", Integer.valueOf(Native.getLastError()));
/* 142 */       return ticks;
/*     */     } 
/*     */     
/* 145 */     int[] cpuTicks = procCpuLoadInfo.getValue().getIntArray(0L, procInfoCount.getValue());
/* 146 */     for (int cpu = 0; cpu < procCount.getValue(); cpu++) {
/* 147 */       int offset = cpu * 4;
/* 148 */       ticks[cpu][CentralProcessor.TickType.USER.getIndex()] = FormatUtil.getUnsignedInt(cpuTicks[offset + 0]);
/* 149 */       ticks[cpu][CentralProcessor.TickType.NICE.getIndex()] = FormatUtil.getUnsignedInt(cpuTicks[offset + 3]);
/* 150 */       ticks[cpu][CentralProcessor.TickType.SYSTEM.getIndex()] = 
/* 151 */         FormatUtil.getUnsignedInt(cpuTicks[offset + 1]);
/* 152 */       ticks[cpu][CentralProcessor.TickType.IDLE.getIndex()] = FormatUtil.getUnsignedInt(cpuTicks[offset + 2]);
/*     */     } 
/* 154 */     return ticks;
/*     */   }
/*     */ 
/*     */   
/*     */   public long queryContextSwitches() {
/* 159 */     int machPort = SystemB.INSTANCE.mach_host_self();
/* 160 */     SystemB.VMMeter vmstats = new SystemB.VMMeter();
/* 161 */     if (0 != SystemB.INSTANCE.host_statistics(machPort, 2, (Structure)vmstats, new IntByReference(vmstats
/* 162 */           .size()))) {
/* 163 */       LOG.error("Failed to update vmstats. Error code: {}", Integer.valueOf(Native.getLastError()));
/* 164 */       return -1L;
/*     */     } 
/* 166 */     return ParseUtil.unsignedIntToLong(vmstats.v_swtch);
/*     */   }
/*     */ 
/*     */   
/*     */   public long queryInterrupts() {
/* 171 */     int machPort = SystemB.INSTANCE.mach_host_self();
/* 172 */     SystemB.VMMeter vmstats = new SystemB.VMMeter();
/* 173 */     if (0 != SystemB.INSTANCE.host_statistics(machPort, 2, (Structure)vmstats, new IntByReference(vmstats
/* 174 */           .size()))) {
/* 175 */       LOG.error("Failed to update vmstats. Error code: {}", Integer.valueOf(Native.getLastError()));
/* 176 */       return -1L;
/*     */     } 
/* 178 */     return ParseUtil.unsignedIntToLong(vmstats.v_intr);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platform\mac\MacCentralProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */