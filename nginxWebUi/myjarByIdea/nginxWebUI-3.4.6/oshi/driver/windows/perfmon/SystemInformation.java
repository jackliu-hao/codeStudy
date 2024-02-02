package oshi.driver.windows.perfmon;

import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.PerfCounterQuery;

@ThreadSafe
public final class SystemInformation {
   private static final String SYSTEM = "System";
   private static final String WIN32_PERF_RAW_DATA_PERF_OS_SYSTEM = "Win32_PerfRawData_PerfOS_System";

   private SystemInformation() {
   }

   public static Map<ContextSwitchProperty, Long> queryContextSwitchCounters() {
      return PerfCounterQuery.queryValues(ContextSwitchProperty.class, "System", "Win32_PerfRawData_PerfOS_System");
   }

   public static enum ContextSwitchProperty implements PerfCounterQuery.PdhCounterProperty {
      CONTEXTSWITCHESPERSEC((String)null, "Context Switches/sec");

      private final String instance;
      private final String counter;

      private ContextSwitchProperty(String instance, String counter) {
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
