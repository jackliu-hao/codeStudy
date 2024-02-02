/*    */ package oshi.driver.windows.perfmon;
/*    */ 
/*    */ import java.util.Map;
/*    */ import oshi.annotation.concurrent.ThreadSafe;
/*    */ import oshi.util.platform.windows.PerfCounterQuery;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @ThreadSafe
/*    */ public final class MemoryInformation
/*    */ {
/*    */   private static final String MEMORY = "Memory";
/*    */   private static final String WIN32_PERF_RAW_DATA_PERF_OS_MEMORY = "Win32_PerfRawData_PerfOS_Memory";
/*    */   
/*    */   public enum PageSwapProperty
/*    */     implements PerfCounterQuery.PdhCounterProperty
/*    */   {
/* 45 */     PAGESINPUTPERSEC(null, "Pages Input/sec"),
/* 46 */     PAGESOUTPUTPERSEC(null, "Pages Output/sec");
/*    */     
/*    */     private final String instance;
/*    */     private final String counter;
/*    */     
/*    */     PageSwapProperty(String instance, String counter) {
/* 52 */       this.instance = instance;
/* 53 */       this.counter = counter;
/*    */     }
/*    */ 
/*    */     
/*    */     public String getInstance() {
/* 58 */       return this.instance;
/*    */     }
/*    */ 
/*    */     
/*    */     public String getCounter() {
/* 63 */       return this.counter;
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static Map<PageSwapProperty, Long> queryPageSwaps() {
/* 76 */     return PerfCounterQuery.queryValues(PageSwapProperty.class, "Memory", "Win32_PerfRawData_PerfOS_Memory");
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\driver\windows\perfmon\MemoryInformation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */