package oshi.driver.windows.registry;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinPerf;
import com.sun.jna.platform.win32.WinReg;
import com.sun.jna.ptr.IntByReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.PerfCounterWildcardQuery;
import oshi.util.tuples.Pair;
import oshi.util.tuples.Triplet;

@ThreadSafe
public final class HkeyPerformanceDataUtil {
   private static final Logger LOG = LoggerFactory.getLogger(HkeyPerformanceDataUtil.class);
   private static final String HKEY_PERFORMANCE_TEXT = "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Perflib\\009";
   private static final String COUNTER = "Counter";
   private static final Map<String, Integer> COUNTER_INDEX_MAP = mapCounterIndicesFromRegistry();

   private HkeyPerformanceDataUtil() {
   }

   public static <T extends Enum<T> & PerfCounterWildcardQuery.PdhCounterWildcardProperty> Triplet<List<Map<T, Object>>, Long, Long> readPerfDataFromRegistry(String objectName, Class<T> counterEnum) {
      Pair<Integer, EnumMap<T, Integer>> indices = getCounterIndices(objectName, counterEnum);
      if (indices == null) {
         return null;
      } else {
         Memory pPerfData = readPerfDataBuffer(objectName);
         if (pPerfData == null) {
            return null;
         } else {
            WinPerf.PERF_DATA_BLOCK perfData = new WinPerf.PERF_DATA_BLOCK(pPerfData.share(0L));
            long perfTime100nSec = perfData.PerfTime100nSec.getValue();
            long now = WinBase.FILETIME.filetimeToDate((int)(perfTime100nSec >> 32), (int)(perfTime100nSec & 4294967295L)).getTime();
            long perfObjectOffset = (long)perfData.HeaderLength;

            for(int obj = 0; obj < perfData.NumObjectTypes; ++obj) {
               WinPerf.PERF_OBJECT_TYPE perfObject = new WinPerf.PERF_OBJECT_TYPE(pPerfData.share(perfObjectOffset));
               if (perfObject.ObjectNameTitleIndex == (Integer)COUNTER_INDEX_MAP.get(objectName)) {
                  long perfCounterOffset = perfObjectOffset + (long)perfObject.HeaderLength;
                  Map<Integer, Integer> counterOffsetMap = new HashMap();
                  Map<Integer, Integer> counterSizeMap = new HashMap();

                  for(int counter = 0; counter < perfObject.NumCounters; ++counter) {
                     WinPerf.PERF_COUNTER_DEFINITION perfCounter = new WinPerf.PERF_COUNTER_DEFINITION(pPerfData.share(perfCounterOffset));
                     counterOffsetMap.put(perfCounter.CounterNameTitleIndex, perfCounter.CounterOffset);
                     counterSizeMap.put(perfCounter.CounterNameTitleIndex, perfCounter.CounterSize);
                     perfCounterOffset += (long)perfCounter.ByteLength;
                  }

                  long perfInstanceOffset = perfObjectOffset + (long)perfObject.DefinitionLength;
                  List<Map<T, Object>> counterMaps = new ArrayList(perfObject.NumInstances);

                  for(int inst = 0; inst < perfObject.NumInstances; ++inst) {
                     WinPerf.PERF_INSTANCE_DEFINITION perfInstance = new WinPerf.PERF_INSTANCE_DEFINITION(pPerfData.share(perfInstanceOffset));
                     long perfCounterBlockOffset = perfInstanceOffset + (long)perfInstance.ByteLength;
                     Map<T, Object> counterMap = new EnumMap(counterEnum);
                     T[] counterKeys = (Enum[])counterEnum.getEnumConstants();
                     counterMap.put(counterKeys[0], pPerfData.getWideString(perfInstanceOffset + (long)perfInstance.NameOffset));

                     for(int i = 1; i < counterKeys.length; ++i) {
                        T key = counterKeys[i];
                        int keyIndex = (Integer)COUNTER_INDEX_MAP.get(((PerfCounterWildcardQuery.PdhCounterWildcardProperty)key).getCounter());
                        int size = (Integer)counterSizeMap.getOrDefault(keyIndex, 0);
                        if (size == 4) {
                           counterMap.put(key, pPerfData.getInt(perfCounterBlockOffset + (long)(Integer)counterOffsetMap.get(keyIndex)));
                        } else {
                           if (size != 8) {
                              return null;
                           }

                           counterMap.put(key, pPerfData.getLong(perfCounterBlockOffset + (long)(Integer)counterOffsetMap.get(keyIndex)));
                        }
                     }

                     counterMaps.add(counterMap);
                     perfInstanceOffset = perfCounterBlockOffset + (long)(new WinPerf.PERF_COUNTER_BLOCK(pPerfData.share(perfCounterBlockOffset))).ByteLength;
                  }

                  return new Triplet(counterMaps, perfTime100nSec, now);
               }

               perfObjectOffset += (long)perfObject.TotalByteLength;
            }

            return null;
         }
      }
   }

   private static <T extends Enum<T> & PerfCounterWildcardQuery.PdhCounterWildcardProperty> Pair<Integer, EnumMap<T, Integer>> getCounterIndices(String objectName, Class<T> counterEnum) {
      if (!COUNTER_INDEX_MAP.containsKey(objectName)) {
         LOG.debug((String)"Couldn't find counter index of {}.", (Object)objectName);
         return null;
      } else {
         int counterIndex = (Integer)COUNTER_INDEX_MAP.get(objectName);
         T[] enumConstants = (Enum[])counterEnum.getEnumConstants();
         EnumMap<T, Integer> indexMap = new EnumMap(counterEnum);

         for(int i = 1; i < enumConstants.length; ++i) {
            T key = enumConstants[i];
            String counterName = ((PerfCounterWildcardQuery.PdhCounterWildcardProperty)key).getCounter();
            if (!COUNTER_INDEX_MAP.containsKey(counterName)) {
               LOG.debug((String)"Couldn't find counter index of {}.", (Object)counterName);
               return null;
            }

            indexMap.put(key, (Integer)COUNTER_INDEX_MAP.get(counterName));
         }

         return new Pair(counterIndex, indexMap);
      }
   }

   private static Memory readPerfDataBuffer(String objectName) {
      String objectIndexStr = Integer.toString((Integer)COUNTER_INDEX_MAP.get(objectName));
      int bufferSize = 4096;
      IntByReference lpcbData = new IntByReference(bufferSize);
      Memory pPerfData = new Memory((long)bufferSize);
      int ret = Advapi32.INSTANCE.RegQueryValueEx(WinReg.HKEY_PERFORMANCE_DATA, objectIndexStr, 0, (IntByReference)null, (Pointer)pPerfData, lpcbData);
      if (ret != 0 && ret != 234) {
         LOG.error((String)"Error reading performance data from registry for {}.", (Object)objectName);
         return null;
      } else {
         while(ret == 234) {
            bufferSize += 4096;
            lpcbData.setValue(bufferSize);
            pPerfData = new Memory((long)bufferSize);
            ret = Advapi32.INSTANCE.RegQueryValueEx(WinReg.HKEY_PERFORMANCE_DATA, objectIndexStr, 0, (IntByReference)null, (Pointer)pPerfData, lpcbData);
         }

         return pPerfData;
      }
   }

   private static Map<String, Integer> mapCounterIndicesFromRegistry() {
      HashMap<String, Integer> indexMap = new HashMap();

      try {
         String[] counterText = Advapi32Util.registryGetStringArray(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Perflib\\009", "Counter");

         for(int i = 1; i < counterText.length; i += 2) {
            indexMap.putIfAbsent(counterText[i], Integer.parseInt(counterText[i - 1]));
         }
      } catch (Win32Exception var3) {
         LOG.error((String)"Unable to locate English counter names in registry Perflib 009. Counters may need to be rebuilt: ", (Throwable)var3);
      } catch (NumberFormatException var4) {
         LOG.error("Unable to parse English counter names in registry Perflib 009.");
      }

      return Collections.unmodifiableMap(indexMap);
   }
}
