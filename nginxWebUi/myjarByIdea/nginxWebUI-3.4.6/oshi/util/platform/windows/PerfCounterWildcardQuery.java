package oshi.util.platform.windows;

import com.sun.jna.platform.win32.PdhUtil;
import com.sun.jna.platform.win32.COM.WbemcliUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.GuardedBy;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.Util;
import oshi.util.tuples.Pair;

@ThreadSafe
public final class PerfCounterWildcardQuery {
   private static final Logger LOG = LoggerFactory.getLogger(PerfCounterWildcardQuery.class);
   @GuardedBy("failedQueryCacheLock")
   private static final Set<String> failedQueryCache = new HashSet();
   private static final ReentrantLock failedQueryCacheLock = new ReentrantLock();

   private PerfCounterWildcardQuery() {
   }

   public static <T extends Enum<T>> Pair<List<String>, Map<T, List<Long>>> queryInstancesAndValues(Class<T> propertyEnum, String perfObject, String perfWmiClass) {
      if (!failedQueryCache.contains(perfObject)) {
         failedQueryCacheLock.lock();

         Pair var4;
         try {
            if (failedQueryCache.contains(perfObject)) {
               return queryInstancesAndValuesFromWMI(propertyEnum, perfWmiClass);
            }

            Pair<List<String>, Map<T, List<Long>>> instancesAndValuesMap = queryInstancesAndValuesFromPDH(propertyEnum, perfObject);
            if (((List)instancesAndValuesMap.getA()).isEmpty()) {
               LOG.warn((String)"Disabling further attempts to query {}.", (Object)perfObject);
               failedQueryCache.add(perfObject);
               return queryInstancesAndValuesFromWMI(propertyEnum, perfWmiClass);
            }

            var4 = instancesAndValuesMap;
         } finally {
            failedQueryCacheLock.unlock();
         }

         return var4;
      } else {
         return queryInstancesAndValuesFromWMI(propertyEnum, perfWmiClass);
      }
   }

   public static <T extends Enum<T>> Pair<List<String>, Map<T, List<Long>>> queryInstancesAndValuesFromPDH(Class<T> propertyEnum, String perfObject) {
      T[] props = (Enum[])propertyEnum.getEnumConstants();
      if (props.length < 2) {
         throw new IllegalArgumentException("Enum " + propertyEnum.getName() + " must have at least two elements, an instance filter and a counter.");
      } else {
         String instanceFilter = ((PdhCounterWildcardProperty)((Enum[])propertyEnum.getEnumConstants())[0]).getCounter().toLowerCase();
         String perfObjectLocalized = PerfCounterQuery.localize(perfObject);

         PdhUtil.PdhEnumObjectItems objectItems;
         try {
            objectItems = PdhUtil.PdhEnumObjectItems((String)null, (String)null, perfObjectLocalized, 100);
         } catch (PdhUtil.PdhException var18) {
            return new Pair(Collections.emptyList(), Collections.emptyMap());
         }

         List<String> instances = objectItems.getInstances();
         instances.removeIf((ix) -> {
            return !Util.wildcardMatch(ix.toLowerCase(), instanceFilter);
         });
         EnumMap<T, List<Long>> valuesMap = new EnumMap(propertyEnum);
         PerfCounterQueryHandler pdhQueryHandler = new PerfCounterQueryHandler();

         label77: {
            Pair var16;
            try {
               EnumMap<T, List<PerfDataUtil.PerfCounter>> counterListMap = new EnumMap(propertyEnum);
               int i = 1;

               label73:
               while(true) {
                  Enum prop;
                  ArrayList values;
                  Iterator var13;
                  if (i >= props.length) {
                     if (0L < pdhQueryHandler.updateQuery()) {
                        for(i = 1; i < props.length; ++i) {
                           prop = props[i];
                           values = new ArrayList();
                           var13 = ((List)counterListMap.get(prop)).iterator();

                           while(var13.hasNext()) {
                              PerfDataUtil.PerfCounter counter = (PerfDataUtil.PerfCounter)var13.next();
                              values.add(pdhQueryHandler.queryCounter(counter));
                           }

                           valuesMap.put(prop, values);
                        }
                     }
                     break label77;
                  }

                  prop = props[i];
                  values = new ArrayList(instances.size());
                  var13 = instances.iterator();

                  while(var13.hasNext()) {
                     String instance = (String)var13.next();
                     PerfDataUtil.PerfCounter counter = PerfDataUtil.createCounter(perfObject, instance, ((PdhCounterWildcardProperty)prop).getCounter());
                     if (!pdhQueryHandler.addCounterToQuery(counter)) {
                        var16 = new Pair(Collections.emptyList(), Collections.emptyMap());
                        break label73;
                     }

                     values.add(counter);
                  }

                  counterListMap.put(prop, values);
                  ++i;
               }
            } catch (Throwable var19) {
               try {
                  pdhQueryHandler.close();
               } catch (Throwable var17) {
                  var19.addSuppressed(var17);
               }

               throw var19;
            }

            pdhQueryHandler.close();
            return var16;
         }

         pdhQueryHandler.close();
         return new Pair(instances, valuesMap);
      }
   }

   public static <T extends Enum<T>> Pair<List<String>, Map<T, List<Long>>> queryInstancesAndValuesFromWMI(Class<T> propertyEnum, String wmiClass) {
      List<String> instances = new ArrayList();
      EnumMap<T, List<Long>> valuesMap = new EnumMap(propertyEnum);
      WbemcliUtil.WmiQuery<T> query = new WbemcliUtil.WmiQuery(wmiClass, propertyEnum);
      WbemcliUtil.WmiResult<T> result = WmiQueryHandler.createInstance().queryWMI(query);
      if (result.getResultCount() > 0) {
         Enum[] var6 = (Enum[])propertyEnum.getEnumConstants();
         int var7 = var6.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            T prop = var6[var8];
            if (prop.ordinal() == 0) {
               for(int i = 0; i < result.getResultCount(); ++i) {
                  instances.add(WmiUtil.getString(result, prop, i));
               }
            } else {
               List<Long> values = new ArrayList();

               for(int i = 0; i < result.getResultCount(); ++i) {
                  switch (result.getCIMType(prop)) {
                     case 18:
                        values.add((long)WmiUtil.getUint16(result, prop, i));
                        break;
                     case 19:
                        values.add(WmiUtil.getUint32asLong(result, prop, i));
                        break;
                     case 21:
                        values.add(WmiUtil.getUint64(result, prop, i));
                        break;
                     case 101:
                        values.add(WmiUtil.getDateTime(result, prop, i).toInstant().toEpochMilli());
                        break;
                     default:
                        throw new ClassCastException("Unimplemented CIM Type Mapping.");
                  }
               }

               valuesMap.put(prop, values);
            }
         }
      }

      return new Pair(instances, valuesMap);
   }

   public interface PdhCounterWildcardProperty {
      String getCounter();
   }
}
