/*     */ package oshi.hardware;
/*     */ 
/*     */ import java.util.List;
/*     */ import java.util.Properties;
/*     */ import java.util.function.Supplier;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import oshi.annotation.concurrent.Immutable;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.util.FileUtil;
/*     */ import oshi.util.Memoizer;
/*     */ import oshi.util.ParseUtil;
/*     */ import oshi.util.Util;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/*     */ public interface CentralProcessor
/*     */ {
/*     */   ProcessorIdentifier getProcessorIdentifier();
/*     */   
/*     */   long getMaxFreq();
/*     */   
/*     */   long[] getCurrentFreq();
/*     */   
/*     */   List<LogicalProcessor> getLogicalProcessors();
/*     */   
/*     */   double getSystemCpuLoadBetweenTicks(long[] paramArrayOflong);
/*     */   
/*     */   long[] getSystemCpuLoadTicks();
/*     */   
/*     */   double[] getSystemLoadAverage(int paramInt);
/*     */   
/*     */   double[] getProcessorCpuLoadBetweenTicks(long[][] paramArrayOflong);
/*     */   
/*     */   long[][] getProcessorCpuLoadTicks();
/*     */   
/*     */   int getLogicalProcessorCount();
/*     */   
/*     */   int getPhysicalProcessorCount();
/*     */   
/*     */   int getPhysicalPackageCount();
/*     */   
/*     */   long getContextSwitches();
/*     */   
/*     */   long getInterrupts();
/*     */   
/*     */   public enum TickType
/*     */   {
/* 244 */     USER(0),
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 249 */     NICE(1),
/*     */ 
/*     */ 
/*     */     
/* 253 */     SYSTEM(2),
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 258 */     IDLE(3),
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 263 */     IOWAIT(4),
/*     */ 
/*     */ 
/*     */     
/* 267 */     IRQ(5),
/*     */ 
/*     */ 
/*     */     
/* 271 */     SOFTIRQ(6),
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 276 */     STEAL(7);
/*     */     
/*     */     private int index;
/*     */     
/*     */     TickType(int value) {
/* 281 */       this.index = value;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int getIndex() {
/* 289 */       return this.index;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Immutable
/*     */   public static class LogicalProcessor
/*     */   {
/*     */     private final int processorNumber;
/*     */ 
/*     */     
/*     */     private final int physicalProcessorNumber;
/*     */ 
/*     */     
/*     */     private final int physicalPackageNumber;
/*     */ 
/*     */     
/*     */     private final int numaNode;
/*     */ 
/*     */     
/*     */     private final int processorGroup;
/*     */ 
/*     */ 
/*     */     
/*     */     public LogicalProcessor(int processorNumber, int physicalProcessorNumber, int physicalPackageNumber) {
/* 315 */       this(processorNumber, physicalProcessorNumber, physicalPackageNumber, 0, 0);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public LogicalProcessor(int processorNumber, int physicalProcessorNumber, int physicalPackageNumber, int numaNode) {
/* 330 */       this(processorNumber, physicalProcessorNumber, physicalPackageNumber, numaNode, 0);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public LogicalProcessor(int processorNumber, int physicalProcessorNumber, int physicalPackageNumber, int numaNode, int processorGroup) {
/* 347 */       this.processorNumber = processorNumber;
/* 348 */       this.physicalProcessorNumber = physicalProcessorNumber;
/* 349 */       this.physicalPackageNumber = physicalPackageNumber;
/* 350 */       this.numaNode = numaNode;
/* 351 */       this.processorGroup = processorGroup;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int getProcessorNumber() {
/* 361 */       return this.processorNumber;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int getPhysicalProcessorNumber() {
/* 372 */       return this.physicalProcessorNumber;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int getPhysicalPackageNumber() {
/* 383 */       return this.physicalPackageNumber;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int getNumaNode() {
/* 394 */       return this.numaNode;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int getProcessorGroup() {
/* 405 */       return this.processorGroup;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   @Immutable
/*     */   public static final class ProcessorIdentifier
/*     */   {
/*     */     private static final String OSHI_ARCHITECTURE_PROPERTIES = "oshi.architecture.properties";
/*     */     
/*     */     private final String cpuVendor;
/*     */     
/*     */     private final String cpuName;
/*     */     
/*     */     private final String cpuFamily;
/*     */     
/*     */     private final String cpuModel;
/*     */     
/*     */     private final String cpuStepping;
/*     */     private final String processorID;
/*     */     private final String cpuIdentifier;
/*     */     private final boolean cpu64bit;
/*     */     private final long cpuVendorFreq;
/* 428 */     private final Supplier<String> microArchictecture = Memoizer.memoize(this::queryMicroarchitecture);
/*     */ 
/*     */     
/*     */     public ProcessorIdentifier(String cpuVendor, String cpuName, String cpuFamily, String cpuModel, String cpuStepping, String processorID, boolean cpu64bit) {
/* 432 */       this(cpuVendor, cpuName, cpuFamily, cpuModel, cpuStepping, processorID, cpu64bit, -1L);
/*     */     }
/*     */ 
/*     */     
/*     */     public ProcessorIdentifier(String cpuVendor, String cpuName, String cpuFamily, String cpuModel, String cpuStepping, String processorID, boolean cpu64bit, long vendorFreq) {
/* 437 */       this.cpuVendor = cpuVendor;
/* 438 */       this.cpuName = cpuName;
/* 439 */       this.cpuFamily = cpuFamily;
/* 440 */       this.cpuModel = cpuModel;
/* 441 */       this.cpuStepping = cpuStepping;
/* 442 */       this.processorID = processorID;
/* 443 */       this.cpu64bit = cpu64bit;
/*     */ 
/*     */       
/* 446 */       StringBuilder sb = new StringBuilder();
/* 447 */       if (cpuVendor.contentEquals("GenuineIntel")) {
/* 448 */         sb.append(cpu64bit ? "Intel64" : "x86");
/*     */       } else {
/* 450 */         sb.append(cpuVendor);
/*     */       } 
/* 452 */       sb.append(" Family ").append(cpuFamily);
/* 453 */       sb.append(" Model ").append(cpuModel);
/* 454 */       sb.append(" Stepping ").append(cpuStepping);
/* 455 */       this.cpuIdentifier = sb.toString();
/*     */       
/* 457 */       if (vendorFreq >= 0L) {
/* 458 */         this.cpuVendorFreq = vendorFreq;
/*     */       } else {
/*     */         
/* 461 */         Pattern pattern = Pattern.compile("@ (.*)$");
/* 462 */         Matcher matcher = pattern.matcher(cpuName);
/* 463 */         if (matcher.find()) {
/* 464 */           String unit = matcher.group(1);
/* 465 */           this.cpuVendorFreq = ParseUtil.parseHertz(unit);
/*     */         } else {
/* 467 */           this.cpuVendorFreq = -1L;
/*     */         } 
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String getVendor() {
/* 478 */       return this.cpuVendor;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String getName() {
/* 487 */       return this.cpuName;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String getFamily() {
/* 497 */       return this.cpuFamily;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String getModel() {
/* 507 */       return this.cpuModel;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String getStepping() {
/* 517 */       return this.cpuStepping;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String getProcessorID() {
/* 542 */       return this.processorID;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String getIdentifier() {
/* 552 */       return this.cpuIdentifier;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean isCpu64bit() {
/* 561 */       return this.cpu64bit;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public long getVendorFreq() {
/* 571 */       return this.cpuVendorFreq;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String getMicroarchitecture() {
/* 581 */       return this.microArchictecture.get();
/*     */     }
/*     */     
/*     */     private String queryMicroarchitecture() {
/* 585 */       String arch = null;
/* 586 */       Properties archProps = FileUtil.readPropertiesFromFilename("oshi.architecture.properties");
/*     */       
/* 588 */       StringBuilder sb = new StringBuilder();
/*     */       
/* 590 */       if (this.cpuVendor.toUpperCase().contains("AMD")) {
/* 591 */         sb.append("amd.");
/* 592 */       } else if (getVendor().toUpperCase().contains("ARM")) {
/* 593 */         sb.append("arm.");
/* 594 */       } else if (getVendor().toUpperCase().contains("IBM")) {
/*     */         
/* 596 */         int powerIdx = this.cpuName.indexOf("_POWER");
/* 597 */         if (powerIdx > 0) {
/* 598 */           arch = this.cpuName.substring(powerIdx + 1);
/*     */         }
/*     */       } 
/* 601 */       if (Util.isBlank(arch) && !sb.toString().equals("arm.")) {
/*     */         
/* 603 */         sb.append(this.cpuFamily);
/* 604 */         arch = archProps.getProperty(sb.toString());
/*     */       } 
/*     */       
/* 607 */       if (Util.isBlank(arch)) {
/*     */         
/* 609 */         sb.append('.').append(this.cpuModel);
/* 610 */         arch = archProps.getProperty(sb.toString());
/*     */       } 
/*     */       
/* 613 */       if (Util.isBlank(arch)) {
/*     */         
/* 615 */         sb.append('.').append(this.cpuStepping);
/* 616 */         arch = archProps.getProperty(sb.toString());
/*     */       } 
/*     */       
/* 619 */       return Util.isBlank(arch) ? "unknown" : arch;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 624 */       return getIdentifier();
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\CentralProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */