package oshi.driver.windows.perfmon;

import java.util.List;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.PerfCounterWildcardQuery;
import oshi.util.tuples.Pair;

@ThreadSafe
public final class ProcessInformation {
   private static final String WIN32_PROCESS = "Win32_Process";
   private static final String PROCESS = "Process";
   private static final String WIN32_PROCESS_WHERE_NOT_NAME_LIKE_TOTAL = "Win32_Process WHERE NOT Name LIKE\"%_Total\"";

   private ProcessInformation() {
   }

   public static Pair<List<String>, Map<ProcessPerformanceProperty, List<Long>>> queryProcessCounters() {
      return PerfCounterWildcardQuery.queryInstancesAndValues(ProcessPerformanceProperty.class, "Process", "Win32_Process WHERE NOT Name LIKE\"%_Total\"");
   }

   public static Pair<List<String>, Map<HandleCountProperty, List<Long>>> queryHandles() {
      return PerfCounterWildcardQuery.queryInstancesAndValues(HandleCountProperty.class, "Process", "Win32_Process");
   }

   public static enum ProcessPerformanceProperty implements PerfCounterWildcardQuery.PdhCounterWildcardProperty {
      NAME("^*_Total"),
      PRIORITY("Priority Base"),
      CREATIONDATE("Elapsed Time"),
      PROCESSID("ID Process"),
      PARENTPROCESSID("Creating Process ID"),
      READTRANSFERCOUNT("IO Read Bytes/sec"),
      WRITETRANSFERCOUNT("IO Write Bytes/sec"),
      PRIVATEPAGECOUNT("Working Set - Private"),
      PAGEFAULTSPERSEC("Page Faults/sec");

      private final String counter;

      private ProcessPerformanceProperty(String counter) {
         this.counter = counter;
      }

      public String getCounter() {
         return this.counter;
      }
   }

   public static enum HandleCountProperty implements PerfCounterWildcardQuery.PdhCounterWildcardProperty {
      NAME("_Total"),
      HANDLECOUNT("Handle Count");

      private final String counter;

      private HandleCountProperty(String counter) {
         this.counter = counter;
      }

      public String getCounter() {
         return this.counter;
      }
   }
}
