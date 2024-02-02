package oshi.util.platform.windows;

import com.sun.jna.platform.win32.WinNT;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.NotThreadSafe;
import oshi.util.FormatUtil;

@NotThreadSafe
public final class PerfCounterQueryHandler implements AutoCloseable {
   private static final Logger LOG = LoggerFactory.getLogger(PerfCounterQueryHandler.class);
   private Map<PerfDataUtil.PerfCounter, WinNT.HANDLEByReference> counterHandleMap = new HashMap();
   private WinNT.HANDLEByReference queryHandle = null;

   public boolean addCounterToQuery(PerfDataUtil.PerfCounter counter) {
      if (this.queryHandle == null) {
         this.queryHandle = new WinNT.HANDLEByReference();
         if (!PerfDataUtil.openQuery(this.queryHandle)) {
            LOG.warn((String)"Failed to open a query for PDH object: {}", (Object)counter.getObject());
            this.queryHandle = null;
            return false;
         }
      }

      WinNT.HANDLEByReference p = new WinNT.HANDLEByReference();
      if (!PerfDataUtil.addCounter(this.queryHandle, counter.getCounterPath(), p)) {
         LOG.warn((String)"Failed to add counter for PDH object: {}", (Object)counter.getObject());
         return false;
      } else {
         this.counterHandleMap.put(counter, p);
         return true;
      }
   }

   public boolean removeCounterFromQuery(PerfDataUtil.PerfCounter counter) {
      boolean success = false;
      WinNT.HANDLEByReference href = (WinNT.HANDLEByReference)this.counterHandleMap.remove(counter);
      if (href != null) {
         success = PerfDataUtil.removeCounter(href);
      }

      if (this.counterHandleMap.isEmpty()) {
         PerfDataUtil.closeQuery(this.queryHandle);
         this.queryHandle = null;
      }

      return success;
   }

   public void removeAllCounters() {
      Iterator var1 = this.counterHandleMap.values().iterator();

      while(var1.hasNext()) {
         WinNT.HANDLEByReference href = (WinNT.HANDLEByReference)var1.next();
         PerfDataUtil.removeCounter(href);
      }

      this.counterHandleMap.clear();
      if (this.queryHandle != null) {
         PerfDataUtil.closeQuery(this.queryHandle);
      }

      this.queryHandle = null;
   }

   public long updateQuery() {
      if (this.queryHandle == null) {
         LOG.warn("Query does not exist to update.");
         return 0L;
      } else {
         return PerfDataUtil.updateQueryTimestamp(this.queryHandle);
      }
   }

   public long queryCounter(PerfDataUtil.PerfCounter counter) {
      if (!this.counterHandleMap.containsKey(counter)) {
         if (LOG.isWarnEnabled()) {
            LOG.warn((String)"Counter {} does not exist to query.", (Object)counter.getCounterPath());
         }

         return 0L;
      } else {
         long value = PerfDataUtil.queryCounter((WinNT.HANDLEByReference)this.counterHandleMap.get(counter));
         if (value < 0L) {
            if (LOG.isWarnEnabled()) {
               LOG.warn((String)"Error querying counter {}: {}", (Object)counter.getCounterPath(), (Object)String.format(FormatUtil.formatError((int)value)));
            }

            return 0L;
         } else {
            return value;
         }
      }
   }

   public void close() {
      this.removeAllCounters();
   }
}
