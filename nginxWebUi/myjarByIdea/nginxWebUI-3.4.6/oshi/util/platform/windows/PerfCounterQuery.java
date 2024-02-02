package oshi.util.platform.windows;

import com.sun.jna.platform.win32.PdhUtil;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.COM.WbemcliUtil;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.GuardedBy;
import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class PerfCounterQuery {
   private static final Logger LOG = LoggerFactory.getLogger(PerfCounterQuery.class);
   @GuardedBy("failedQueryCacheLock")
   private static final Set<String> failedQueryCache = new HashSet();
   private static final ReentrantLock failedQueryCacheLock = new ReentrantLock();
   public static final String TOTAL_INSTANCE = "_Total";
   public static final String TOTAL_INSTANCES = "*_Total";
   public static final String NOT_TOTAL_INSTANCE = "^_Total";
   public static final String NOT_TOTAL_INSTANCES = "^*_Total";

   private PerfCounterQuery() {
   }

   public static <T extends Enum<T>> Map<T, Long> queryValues(Class<T> propertyEnum, String perfObject, String perfWmiClass) {
      if (!failedQueryCache.contains(perfObject)) {
         failedQueryCacheLock.lock();

         Map var4;
         try {
            if (failedQueryCache.contains(perfObject)) {
               return queryValuesFromWMI(propertyEnum, perfWmiClass);
            }

            Map<T, Long> valueMap = queryValuesFromPDH(propertyEnum, perfObject);
            if (valueMap.isEmpty()) {
               LOG.warn((String)"Disabling further attempts to query {}.", (Object)perfObject);
               failedQueryCache.add(perfObject);
               return queryValuesFromWMI(propertyEnum, perfWmiClass);
            }

            var4 = valueMap;
         } finally {
            failedQueryCacheLock.unlock();
         }

         return var4;
      } else {
         return queryValuesFromWMI(propertyEnum, perfWmiClass);
      }
   }

   public static <T extends Enum<T>> Map<T, Long> queryValuesFromPDH(Class<T> propertyEnum, String perfObject) {
      T[] props = (Enum[])propertyEnum.getEnumConstants();
      String perfObjectLocalized = localize(perfObject);
      EnumMap<T, PerfDataUtil.PerfCounter> counterMap = new EnumMap(propertyEnum);
      EnumMap<T, Long> valueMap = new EnumMap(propertyEnum);
      PerfCounterQueryHandler pdhQueryHandler = new PerfCounterQueryHandler();

      EnumMap var12;
      label48: {
         try {
            Enum[] var7 = props;
            int var8 = props.length;

            int var9;
            Enum prop;
            for(var9 = 0; var9 < var8; ++var9) {
               prop = var7[var9];
               PerfDataUtil.PerfCounter counter = PerfDataUtil.createCounter(perfObjectLocalized, ((PdhCounterProperty)prop).getInstance(), ((PdhCounterProperty)prop).getCounter());
               counterMap.put(prop, counter);
               if (!pdhQueryHandler.addCounterToQuery(counter)) {
                  var12 = valueMap;
                  break label48;
               }
            }

            if (0L < pdhQueryHandler.updateQuery()) {
               var7 = props;
               var8 = props.length;

               for(var9 = 0; var9 < var8; ++var9) {
                  prop = var7[var9];
                  valueMap.put(prop, pdhQueryHandler.queryCounter((PerfDataUtil.PerfCounter)counterMap.get(prop)));
               }
            }
         } catch (Throwable var14) {
            try {
               pdhQueryHandler.close();
            } catch (Throwable var13) {
               var14.addSuppressed(var13);
            }

            throw var14;
         }

         pdhQueryHandler.close();
         return valueMap;
      }

      pdhQueryHandler.close();
      return var12;
   }

   public static <T extends Enum<T>> Map<T, Long> queryValuesFromWMI(Class<T> propertyEnum, String wmiClass) {
      WbemcliUtil.WmiQuery<T> query = new WbemcliUtil.WmiQuery(wmiClass, propertyEnum);
      WbemcliUtil.WmiResult<T> result = WmiQueryHandler.createInstance().queryWMI(query);
      EnumMap<T, Long> valueMap = new EnumMap(propertyEnum);
      if (result.getResultCount() > 0) {
         Enum[] var5 = (Enum[])propertyEnum.getEnumConstants();
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            T prop = var5[var7];
            switch (result.getCIMType(prop)) {
               case 18:
                  valueMap.put(prop, (long)WmiUtil.getUint16(result, prop, 0));
                  break;
               case 19:
                  valueMap.put(prop, WmiUtil.getUint32asLong(result, prop, 0));
                  break;
               case 21:
                  valueMap.put(prop, WmiUtil.getUint64(result, prop, 0));
                  break;
               case 101:
                  valueMap.put(prop, WmiUtil.getDateTime(result, prop, 0).toInstant().toEpochMilli());
                  break;
               default:
                  throw new ClassCastException("Unimplemented CIM Type Mapping.");
            }
         }
      }

      return valueMap;
   }

   public static String localize(String perfObject) {
      String localized = perfObject;

      try {
         localized = PdhUtil.PdhLookupPerfNameByIndex((String)null, PdhUtil.PdhLookupPerfIndexByEnglishName(perfObject));
      } catch (Win32Exception var3) {
         LOG.warn((String)"Unable to locate English counter names in registry Perflib 009. Assuming English counters. Error {}. {}", (Object)String.format("0x%x", var3.getHR().intValue()), (Object)"See https://support.microsoft.com/en-us/help/300956/how-to-manually-rebuild-performance-counter-library-values");
      } catch (PdhUtil.PdhException var4) {
         LOG.warn((String)"Unable to localize {} performance counter.  Error {}.", (Object)perfObject, (Object)String.format("0x%x", var4.getErrorCode()));
      }

      if (localized.isEmpty()) {
         return perfObject;
      } else {
         LOG.debug((String)"Localized {} to {}", (Object)perfObject, (Object)localized);
         return localized;
      }
   }

   public interface PdhCounterProperty {
      String getInstance();

      String getCounter();
   }
}
