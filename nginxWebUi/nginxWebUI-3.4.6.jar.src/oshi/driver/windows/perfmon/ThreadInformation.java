/*    */ package oshi.driver.windows.perfmon;
/*    */ 
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import oshi.annotation.concurrent.ThreadSafe;
/*    */ import oshi.util.platform.windows.PerfCounterWildcardQuery;
/*    */ import oshi.util.tuples.Pair;
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
/*    */ 
/*    */ 
/*    */ @ThreadSafe
/*    */ public final class ThreadInformation
/*    */ {
/*    */   private static final String THREAD = "Thread";
/*    */   private static final String WIN32_PERF_RAW_DATA_PERF_PROC_THREAD = "Win32_PerfRawData_PerfProc_Thread WHERE NOT Name LIKE \"%_Total\"";
/*    */   
/*    */   public enum ThreadPerformanceProperty
/*    */     implements PerfCounterWildcardQuery.PdhCounterWildcardProperty
/*    */   {
/* 49 */     NAME("^*_Total"),
/*    */     
/* 51 */     PERCENTUSERTIME("% User Time"),
/* 52 */     PERCENTPRIVILEGEDTIME("% Privileged Time"),
/* 53 */     ELAPSEDTIME("Elapsed Time"),
/* 54 */     PRIORITYCURRENT("Priority Current"),
/* 55 */     STARTADDRESS("Start Address"),
/* 56 */     THREADSTATE("Thread State"),
/* 57 */     IDPROCESS("ID Process"),
/* 58 */     IDTHREAD("ID Thread"),
/* 59 */     CONTEXTSWITCHESPERSEC("Context Switches/sec");
/*    */     
/*    */     private final String counter;
/*    */     
/*    */     ThreadPerformanceProperty(String counter) {
/* 64 */       this.counter = counter;
/*    */     }
/*    */ 
/*    */     
/*    */     public String getCounter() {
/* 69 */       return this.counter;
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
/*    */   public static Pair<List<String>, Map<ThreadPerformanceProperty, List<Long>>> queryThreadCounters() {
/* 82 */     return PerfCounterWildcardQuery.queryInstancesAndValues(ThreadPerformanceProperty.class, "Thread", "Win32_PerfRawData_PerfProc_Thread WHERE NOT Name LIKE \"%_Total\"");
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\driver\windows\perfmon\ThreadInformation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */