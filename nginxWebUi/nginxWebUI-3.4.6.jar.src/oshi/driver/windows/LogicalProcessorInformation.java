/*     */ package oshi.driver.windows;
/*     */ 
/*     */ import com.sun.jna.platform.win32.Kernel32Util;
/*     */ import com.sun.jna.platform.win32.WinNT;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.hardware.CentralProcessor;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/*     */ public final class LogicalProcessorInformation
/*     */ {
/*     */   public static List<CentralProcessor.LogicalProcessor> getLogicalProcessorInformationEx() {
/*  61 */     WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX[] procInfo = Kernel32Util.getLogicalProcessorInformationEx(65535);
/*  62 */     List<WinNT.GROUP_AFFINITY[]> packages = (List)new ArrayList<>();
/*  63 */     List<WinNT.NUMA_NODE_RELATIONSHIP> numaNodes = new ArrayList<>();
/*  64 */     List<WinNT.GROUP_AFFINITY> cores = new ArrayList<>();
/*     */     
/*  66 */     for (int i = 0; i < procInfo.length; i++) {
/*  67 */       switch ((procInfo[i]).relationship) {
/*     */         case 3:
/*  69 */           packages.add(((WinNT.PROCESSOR_RELATIONSHIP)procInfo[i]).groupMask);
/*     */           break;
/*     */         case 1:
/*  72 */           numaNodes.add((WinNT.NUMA_NODE_RELATIONSHIP)procInfo[i]);
/*     */           break;
/*     */         case 0:
/*  75 */           cores.add(((WinNT.PROCESSOR_RELATIONSHIP)procInfo[i]).groupMask[0]);
/*     */           break;
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     } 
/*  85 */     cores.sort(Comparator.comparing(c -> Long.valueOf(c.group * 64L + c.mask.longValue())));
/*  86 */     packages.sort(Comparator.comparing(p -> Long.valueOf((p[0]).group * 64L + (p[0]).mask.longValue())));
/*     */ 
/*     */ 
/*     */     
/*  90 */     List<CentralProcessor.LogicalProcessor> logProcs = new ArrayList<>();
/*  91 */     for (WinNT.GROUP_AFFINITY coreMask : cores) {
/*  92 */       int group = coreMask.group;
/*  93 */       long mask = coreMask.mask.longValue();
/*     */       
/*  95 */       int lowBit = Long.numberOfTrailingZeros(mask);
/*  96 */       int hiBit = 63 - Long.numberOfLeadingZeros(mask);
/*  97 */       for (int lp = lowBit; lp <= hiBit; lp++) {
/*  98 */         if ((mask & 1L << lp) > 0L) {
/*     */           
/* 100 */           CentralProcessor.LogicalProcessor logProc = new CentralProcessor.LogicalProcessor(lp, getMatchingCore(cores, group, lp), getMatchingPackage(packages, group, lp), getMatchingNumaNode(numaNodes, group, lp), group);
/* 101 */           logProcs.add(logProc);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 107 */     logProcs.sort(Comparator.comparing(CentralProcessor.LogicalProcessor::getNumaNode)
/* 108 */         .thenComparing(CentralProcessor.LogicalProcessor::getProcessorNumber));
/* 109 */     return logProcs;
/*     */   }
/*     */   
/*     */   private static int getMatchingPackage(List<WinNT.GROUP_AFFINITY[]> packages, int g, int lp) {
/* 113 */     for (int i = 0; i < packages.size(); i++) {
/* 114 */       for (int j = 0; j < ((WinNT.GROUP_AFFINITY[])packages.get(i)).length; j++) {
/* 115 */         if (((((WinNT.GROUP_AFFINITY[])packages.get(i))[j]).mask.longValue() & 1L << lp) > 0L && (((WinNT.GROUP_AFFINITY[])packages.get(i))[j]).group == g) {
/* 116 */           return i;
/*     */         }
/*     */       } 
/*     */     } 
/* 120 */     return 0;
/*     */   }
/*     */   
/*     */   private static int getMatchingNumaNode(List<WinNT.NUMA_NODE_RELATIONSHIP> numaNodes, int g, int lp) {
/* 124 */     for (int j = 0; j < numaNodes.size(); j++) {
/* 125 */       if ((((WinNT.NUMA_NODE_RELATIONSHIP)numaNodes.get(j)).groupMask.mask.longValue() & 1L << lp) > 0L && 
/* 126 */         ((WinNT.NUMA_NODE_RELATIONSHIP)numaNodes.get(j)).groupMask.group == g) {
/* 127 */         return ((WinNT.NUMA_NODE_RELATIONSHIP)numaNodes.get(j)).nodeNumber;
/*     */       }
/*     */     } 
/* 130 */     return 0;
/*     */   }
/*     */   
/*     */   private static int getMatchingCore(List<WinNT.GROUP_AFFINITY> cores, int g, int lp) {
/* 134 */     for (int j = 0; j < cores.size(); j++) {
/* 135 */       if ((((WinNT.GROUP_AFFINITY)cores.get(j)).mask.longValue() & 1L << lp) > 0L && ((WinNT.GROUP_AFFINITY)cores.get(j)).group == g) {
/* 136 */         return j;
/*     */       }
/*     */     } 
/* 139 */     return 0;
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
/*     */   public static List<CentralProcessor.LogicalProcessor> getLogicalProcessorInformation() {
/* 154 */     List<Long> packageMaskList = new ArrayList<>();
/* 155 */     List<Long> coreMaskList = new ArrayList<>();
/* 156 */     WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION[] processors = Kernel32Util.getLogicalProcessorInformation();
/* 157 */     for (WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION proc : processors) {
/* 158 */       if (proc.relationship == 3) {
/* 159 */         packageMaskList.add(Long.valueOf(proc.processorMask.longValue()));
/* 160 */       } else if (proc.relationship == 0) {
/* 161 */         coreMaskList.add(Long.valueOf(proc.processorMask.longValue()));
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 166 */     coreMaskList.sort(null);
/* 167 */     packageMaskList.sort(null);
/*     */ 
/*     */     
/* 170 */     List<CentralProcessor.LogicalProcessor> logProcs = new ArrayList<>();
/* 171 */     for (int core = 0; core < coreMaskList.size(); core++) {
/* 172 */       long coreMask = ((Long)coreMaskList.get(core)).longValue();
/*     */       
/* 174 */       int lowBit = Long.numberOfTrailingZeros(coreMask);
/* 175 */       int hiBit = 63 - Long.numberOfLeadingZeros(coreMask);
/*     */       
/* 177 */       for (int i = lowBit; i <= hiBit; i++) {
/* 178 */         if ((coreMask & 1L << i) > 0L) {
/*     */           
/* 180 */           CentralProcessor.LogicalProcessor logProc = new CentralProcessor.LogicalProcessor(i, core, getBitMatchingPackageNumber(packageMaskList, i));
/* 181 */           logProcs.add(logProc);
/*     */         } 
/*     */       } 
/*     */     } 
/* 185 */     return logProcs;
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
/*     */   private static int getBitMatchingPackageNumber(List<Long> packageMaskList, int logProc) {
/* 198 */     for (int i = 0; i < packageMaskList.size(); i++) {
/* 199 */       if ((((Long)packageMaskList.get(i)).longValue() & 1L << logProc) > 0L) {
/* 200 */         return i;
/*     */       }
/*     */     } 
/* 203 */     return 0;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\driver\windows\LogicalProcessorInformation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */