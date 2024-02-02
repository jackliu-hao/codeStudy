package oshi.util.platform.windows;

import com.sun.jna.platform.win32.BaseTSD;
import com.sun.jna.platform.win32.Pdh;
import com.sun.jna.platform.win32.VersionHelpers;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.FormatUtil;
import oshi.util.ParseUtil;
import oshi.util.Util;

@ThreadSafe
public final class PerfDataUtil {
   private static final Logger LOG = LoggerFactory.getLogger(PerfDataUtil.class);
   private static final BaseTSD.DWORD_PTR PZERO = new BaseTSD.DWORD_PTR(0L);
   private static final WinDef.DWORDByReference PDH_FMT_RAW = new WinDef.DWORDByReference(new WinDef.DWORD(16L));
   private static final Pdh PDH;
   private static final boolean IS_VISTA_OR_GREATER;

   private PerfDataUtil() {
   }

   public static PerfCounter createCounter(String object, String instance, String counter) {
      return new PerfCounter(object, instance, counter);
   }

   public static long updateQueryTimestamp(WinNT.HANDLEByReference query) {
      WinDef.LONGLONGByReference pllTimeStamp = new WinDef.LONGLONGByReference();
      int ret = IS_VISTA_OR_GREATER ? PDH.PdhCollectQueryDataWithTime(query.getValue(), pllTimeStamp) : PDH.PdhCollectQueryData(query.getValue());

      for(int retries = 0; ret == -2147481643 && retries++ < 3; ret = IS_VISTA_OR_GREATER ? PDH.PdhCollectQueryDataWithTime(query.getValue(), pllTimeStamp) : PDH.PdhCollectQueryData(query.getValue())) {
         Util.sleep((long)(1 << retries));
      }

      if (ret != 0) {
         if (LOG.isWarnEnabled()) {
            LOG.warn((String)"Failed to update counter. Error code: {}", (Object)String.format(FormatUtil.formatError(ret)));
         }

         return 0L;
      } else {
         return IS_VISTA_OR_GREATER ? ParseUtil.filetimeToUtcMs(pllTimeStamp.getValue().longValue(), true) : System.currentTimeMillis();
      }
   }

   public static boolean openQuery(WinNT.HANDLEByReference q) {
      int ret = PDH.PdhOpenQuery((String)null, PZERO, q);
      if (ret != 0) {
         if (LOG.isErrorEnabled()) {
            LOG.error((String)"Failed to open PDH Query. Error code: {}", (Object)String.format(FormatUtil.formatError(ret)));
         }

         return false;
      } else {
         return true;
      }
   }

   public static boolean closeQuery(WinNT.HANDLEByReference q) {
      return 0 == PDH.PdhCloseQuery(q.getValue());
   }

   public static long queryCounter(WinNT.HANDLEByReference counter) {
      Pdh.PDH_RAW_COUNTER counterValue = new Pdh.PDH_RAW_COUNTER();
      int ret = PDH.PdhGetRawCounterValue(counter.getValue(), PDH_FMT_RAW, counterValue);
      if (ret != 0) {
         if (LOG.isWarnEnabled()) {
            LOG.warn((String)"Failed to get counter. Error code: {}", (Object)String.format(FormatUtil.formatError(ret)));
         }

         return (long)ret;
      } else {
         return counterValue.FirstValue;
      }
   }

   public static boolean addCounter(WinNT.HANDLEByReference query, String path, WinNT.HANDLEByReference p) {
      int ret = IS_VISTA_OR_GREATER ? PDH.PdhAddEnglishCounter(query.getValue(), path, PZERO, p) : PDH.PdhAddCounter(query.getValue(), path, PZERO, p);
      if (ret != 0) {
         if (LOG.isWarnEnabled()) {
            LOG.warn((String)"Failed to add PDH Counter: {}, Error code: {}", (Object)path, (Object)String.format(FormatUtil.formatError(ret)));
         }

         return false;
      } else {
         return true;
      }
   }

   public static boolean removeCounter(WinNT.HANDLEByReference p) {
      return 0 == PDH.PdhRemoveCounter(p.getValue());
   }

   static {
      PDH = Pdh.INSTANCE;
      IS_VISTA_OR_GREATER = VersionHelpers.IsWindowsVistaOrGreater();
   }

   public static class PerfCounter {
      private String object;
      private String instance;
      private String counter;

      public PerfCounter(String objectName, String instanceName, String counterName) {
         this.object = objectName;
         this.instance = instanceName;
         this.counter = counterName;
      }

      public String getObject() {
         return this.object;
      }

      public String getInstance() {
         return this.instance;
      }

      public String getCounter() {
         return this.counter;
      }

      public String getCounterPath() {
         StringBuilder sb = new StringBuilder();
         sb.append('\\').append(this.object);
         if (this.instance != null) {
            sb.append('(').append(this.instance).append(')');
         }

         sb.append('\\').append(this.counter);
         return sb.toString();
      }
   }
}
