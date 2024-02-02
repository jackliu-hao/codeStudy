package oshi.driver.windows.perfmon;

import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.PerfCounterQuery;

@ThreadSafe
public final class PagingFile {
   private static final String PAGING_FILE = "Paging File";
   private static final String WIN32_PERF_RAW_DATA_PERF_OS_PAGING_FILE = "Win32_PerfRawData_PerfOS_PagingFile";

   private PagingFile() {
   }

   public static Map<PagingPercentProperty, Long> querySwapUsed() {
      return PerfCounterQuery.queryValues(PagingPercentProperty.class, "Paging File", "Win32_PerfRawData_PerfOS_PagingFile");
   }

   public static enum PagingPercentProperty implements PerfCounterQuery.PdhCounterProperty {
      PERCENTUSAGE("_Total", "% Usage");

      private final String instance;
      private final String counter;

      private PagingPercentProperty(String instance, String counter) {
         this.instance = instance;
         this.counter = counter;
      }

      public String getInstance() {
         return this.instance;
      }

      public String getCounter() {
         return this.counter;
      }
   }
}
