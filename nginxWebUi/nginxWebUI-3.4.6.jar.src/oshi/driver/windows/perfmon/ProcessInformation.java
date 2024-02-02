/*     */ package oshi.driver.windows.perfmon;
/*     */ 
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.util.platform.windows.PerfCounterWildcardQuery;
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
/*     */ public final class ProcessInformation
/*     */ {
/*     */   private static final String WIN32_PROCESS = "Win32_Process";
/*     */   private static final String PROCESS = "Process";
/*     */   private static final String WIN32_PROCESS_WHERE_NOT_NAME_LIKE_TOTAL = "Win32_Process WHERE NOT Name LIKE\"%_Total\"";
/*     */   
/*     */   public enum ProcessPerformanceProperty
/*     */     implements PerfCounterWildcardQuery.PdhCounterWildcardProperty
/*     */   {
/*  50 */     NAME("^*_Total"),
/*     */     
/*  52 */     PRIORITY("Priority Base"),
/*  53 */     CREATIONDATE("Elapsed Time"),
/*  54 */     PROCESSID("ID Process"),
/*  55 */     PARENTPROCESSID("Creating Process ID"),
/*  56 */     READTRANSFERCOUNT("IO Read Bytes/sec"),
/*  57 */     WRITETRANSFERCOUNT("IO Write Bytes/sec"),
/*  58 */     PRIVATEPAGECOUNT("Working Set - Private"),
/*  59 */     PAGEFAULTSPERSEC("Page Faults/sec");
/*     */     
/*     */     private final String counter;
/*     */     
/*     */     ProcessPerformanceProperty(String counter) {
/*  64 */       this.counter = counter;
/*     */     }
/*     */ 
/*     */     
/*     */     public String getCounter() {
/*  69 */       return this.counter;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public enum HandleCountProperty
/*     */     implements PerfCounterWildcardQuery.PdhCounterWildcardProperty
/*     */   {
/*  78 */     NAME("_Total"),
/*     */     
/*  80 */     HANDLECOUNT("Handle Count");
/*     */     
/*     */     private final String counter;
/*     */     
/*     */     HandleCountProperty(String counter) {
/*  85 */       this.counter = counter;
/*     */     }
/*     */ 
/*     */     
/*     */     public String getCounter() {
/*  90 */       return this.counter;
/*     */     }
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
/*     */   public static Pair<List<String>, Map<ProcessPerformanceProperty, List<Long>>> queryProcessCounters() {
/* 103 */     return PerfCounterWildcardQuery.queryInstancesAndValues(ProcessPerformanceProperty.class, "Process", "Win32_Process WHERE NOT Name LIKE\"%_Total\"");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Pair<List<String>, Map<HandleCountProperty, List<Long>>> queryHandles() {
/* 113 */     return PerfCounterWildcardQuery.queryInstancesAndValues(HandleCountProperty.class, "Process", "Win32_Process");
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\driver\windows\perfmon\ProcessInformation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */