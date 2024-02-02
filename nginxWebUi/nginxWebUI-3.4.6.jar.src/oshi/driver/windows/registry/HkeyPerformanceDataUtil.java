/*     */ package oshi.driver.windows.registry;
/*     */ 
/*     */ import com.sun.jna.Memory;
/*     */ import com.sun.jna.Pointer;
/*     */ import com.sun.jna.platform.win32.Advapi32;
/*     */ import com.sun.jna.platform.win32.Advapi32Util;
/*     */ import com.sun.jna.platform.win32.Win32Exception;
/*     */ import com.sun.jna.platform.win32.WinBase;
/*     */ import com.sun.jna.platform.win32.WinPerf;
/*     */ import com.sun.jna.platform.win32.WinReg;
/*     */ import com.sun.jna.ptr.IntByReference;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.EnumMap;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.util.platform.windows.PerfCounterWildcardQuery;
/*     */ import oshi.util.tuples.Pair;
/*     */ import oshi.util.tuples.Triplet;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @ThreadSafe
/*     */ public final class HkeyPerformanceDataUtil
/*     */ {
/*  61 */   private static final Logger LOG = LoggerFactory.getLogger(HkeyPerformanceDataUtil.class);
/*     */ 
/*     */   
/*     */   private static final String HKEY_PERFORMANCE_TEXT = "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Perflib\\009";
/*     */ 
/*     */   
/*     */   private static final String COUNTER = "Counter";
/*     */   
/*  69 */   private static final Map<String, Integer> COUNTER_INDEX_MAP = mapCounterIndicesFromRegistry();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T extends Enum<T> & PerfCounterWildcardQuery.PdhCounterWildcardProperty> Triplet<List<Map<T, Object>>, Long, Long> readPerfDataFromRegistry(String objectName, Class<T> counterEnum) {
/*  98 */     Pair<Integer, EnumMap<T, Integer>> indices = getCounterIndices(objectName, counterEnum);
/*  99 */     if (indices == null) {
/* 100 */       return null;
/*     */     }
/*     */ 
/*     */     
/* 104 */     Memory pPerfData = readPerfDataBuffer(objectName);
/* 105 */     if (pPerfData == null) {
/* 106 */       return null;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 124 */     WinPerf.PERF_DATA_BLOCK perfData = new WinPerf.PERF_DATA_BLOCK(pPerfData.share(0L));
/* 125 */     long perfTime100nSec = perfData.PerfTime100nSec.getValue();
/*     */     
/* 127 */     long now = WinBase.FILETIME.filetimeToDate((int)(perfTime100nSec >> 32L), (int)(perfTime100nSec & 0xFFFFFFFFL)).getTime();
/*     */ 
/*     */     
/* 130 */     long perfObjectOffset = perfData.HeaderLength;
/* 131 */     for (int obj = 0; obj < perfData.NumObjectTypes; obj++) {
/* 132 */       WinPerf.PERF_OBJECT_TYPE perfObject = new WinPerf.PERF_OBJECT_TYPE(pPerfData.share(perfObjectOffset));
/*     */ 
/*     */ 
/*     */       
/* 136 */       if (perfObject.ObjectNameTitleIndex == ((Integer)COUNTER_INDEX_MAP.get(objectName)).intValue()) {
/*     */ 
/*     */ 
/*     */         
/* 140 */         long perfCounterOffset = perfObjectOffset + perfObject.HeaderLength;
/*     */         
/* 142 */         Map<Integer, Integer> counterOffsetMap = new HashMap<>();
/* 143 */         Map<Integer, Integer> counterSizeMap = new HashMap<>();
/* 144 */         for (int counter = 0; counter < perfObject.NumCounters; counter++) {
/*     */           
/* 146 */           WinPerf.PERF_COUNTER_DEFINITION perfCounter = new WinPerf.PERF_COUNTER_DEFINITION(pPerfData.share(perfCounterOffset));
/* 147 */           counterOffsetMap.put(Integer.valueOf(perfCounter.CounterNameTitleIndex), Integer.valueOf(perfCounter.CounterOffset));
/* 148 */           counterSizeMap.put(Integer.valueOf(perfCounter.CounterNameTitleIndex), Integer.valueOf(perfCounter.CounterSize));
/*     */           
/* 150 */           perfCounterOffset += perfCounter.ByteLength;
/*     */         } 
/*     */ 
/*     */ 
/*     */         
/* 155 */         long perfInstanceOffset = perfObjectOffset + perfObject.DefinitionLength;
/*     */ 
/*     */         
/* 158 */         List<Map<T, Object>> counterMaps = new ArrayList<>(perfObject.NumInstances);
/* 159 */         for (int inst = 0; inst < perfObject.NumInstances; inst++) {
/*     */           
/* 161 */           WinPerf.PERF_INSTANCE_DEFINITION perfInstance = new WinPerf.PERF_INSTANCE_DEFINITION(pPerfData.share(perfInstanceOffset));
/* 162 */           long perfCounterBlockOffset = perfInstanceOffset + perfInstance.ByteLength;
/*     */           
/* 164 */           Map<T, Object> counterMap = new EnumMap<>(counterEnum);
/* 165 */           Enum[] arrayOfEnum = (Enum[])counterEnum.getEnumConstants();
/*     */ 
/*     */           
/* 168 */           counterMap.put((T)arrayOfEnum[0], pPerfData
/* 169 */               .getWideString(perfInstanceOffset + perfInstance.NameOffset));
/* 170 */           for (int i = 1; i < arrayOfEnum.length; i++) {
/* 171 */             Enum enum_ = arrayOfEnum[i];
/* 172 */             int keyIndex = ((Integer)COUNTER_INDEX_MAP.get(((PerfCounterWildcardQuery.PdhCounterWildcardProperty)enum_).getCounter())).intValue();
/*     */             
/* 174 */             int size = ((Integer)counterSizeMap.getOrDefault(Integer.valueOf(keyIndex), Integer.valueOf(0))).intValue();
/*     */ 
/*     */             
/* 177 */             if (size == 4) {
/* 178 */               counterMap.put((T)enum_, 
/* 179 */                   Integer.valueOf(pPerfData.getInt(perfCounterBlockOffset + ((Integer)counterOffsetMap.get(Integer.valueOf(keyIndex))).intValue())));
/* 180 */             } else if (size == 8) {
/* 181 */               counterMap.put((T)enum_, 
/* 182 */                   Long.valueOf(pPerfData.getLong(perfCounterBlockOffset + ((Integer)counterOffsetMap.get(Integer.valueOf(keyIndex))).intValue())));
/*     */             } else {
/*     */               
/* 185 */               return null;
/*     */             } 
/*     */           } 
/* 188 */           counterMaps.add(counterMap);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 197 */           perfInstanceOffset = perfCounterBlockOffset + (new WinPerf.PERF_COUNTER_BLOCK(pPerfData.share(perfCounterBlockOffset))).ByteLength;
/*     */         } 
/*     */ 
/*     */         
/* 201 */         return new Triplet(counterMaps, Long.valueOf(perfTime100nSec), Long.valueOf(now));
/*     */       } 
/*     */       
/* 204 */       perfObjectOffset += perfObject.TotalByteLength;
/*     */     } 
/*     */     
/* 207 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static <T extends Enum<T> & PerfCounterWildcardQuery.PdhCounterWildcardProperty> Pair<Integer, EnumMap<T, Integer>> getCounterIndices(String objectName, Class<T> counterEnum) {
/* 229 */     if (!COUNTER_INDEX_MAP.containsKey(objectName)) {
/* 230 */       LOG.debug("Couldn't find counter index of {}.", objectName);
/* 231 */       return null;
/*     */     } 
/* 233 */     int counterIndex = ((Integer)COUNTER_INDEX_MAP.get(objectName)).intValue();
/* 234 */     Enum[] arrayOfEnum = (Enum[])counterEnum.getEnumConstants();
/* 235 */     EnumMap<T, Integer> indexMap = new EnumMap<>(counterEnum);
/*     */ 
/*     */     
/* 238 */     for (int i = 1; i < arrayOfEnum.length; i++) {
/* 239 */       Enum enum_ = arrayOfEnum[i];
/* 240 */       String counterName = ((PerfCounterWildcardQuery.PdhCounterWildcardProperty)enum_).getCounter();
/* 241 */       if (!COUNTER_INDEX_MAP.containsKey(counterName)) {
/* 242 */         LOG.debug("Couldn't find counter index of {}.", counterName);
/* 243 */         return null;
/*     */       } 
/* 245 */       indexMap.put((T)enum_, COUNTER_INDEX_MAP.get(counterName));
/*     */     } 
/*     */     
/* 248 */     return new Pair(Integer.valueOf(counterIndex), indexMap);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Memory readPerfDataBuffer(String objectName) {
/* 262 */     String objectIndexStr = Integer.toString(((Integer)COUNTER_INDEX_MAP.get(objectName)).intValue());
/*     */ 
/*     */     
/* 265 */     int bufferSize = 4096;
/* 266 */     IntByReference lpcbData = new IntByReference(bufferSize);
/* 267 */     Memory pPerfData = new Memory(bufferSize);
/* 268 */     int ret = Advapi32.INSTANCE.RegQueryValueEx(WinReg.HKEY_PERFORMANCE_DATA, objectIndexStr, 0, null, (Pointer)pPerfData, lpcbData);
/*     */     
/* 270 */     if (ret != 0 && ret != 234) {
/* 271 */       LOG.error("Error reading performance data from registry for {}.", objectName);
/* 272 */       return null;
/*     */     } 
/*     */     
/* 275 */     while (ret == 234) {
/* 276 */       bufferSize += 4096;
/* 277 */       lpcbData.setValue(bufferSize);
/* 278 */       pPerfData = new Memory(bufferSize);
/* 279 */       ret = Advapi32.INSTANCE.RegQueryValueEx(WinReg.HKEY_PERFORMANCE_DATA, objectIndexStr, 0, null, (Pointer)pPerfData, lpcbData);
/*     */     } 
/*     */     
/* 282 */     return pPerfData;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Map<String, Integer> mapCounterIndicesFromRegistry() {
/* 299 */     HashMap<String, Integer> indexMap = new HashMap<>();
/*     */     try {
/* 301 */       String[] counterText = Advapi32Util.registryGetStringArray(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Perflib\\009", "Counter");
/*     */       
/* 303 */       for (int i = 1; i < counterText.length; i += 2) {
/* 304 */         indexMap.putIfAbsent(counterText[i], Integer.valueOf(Integer.parseInt(counterText[i - 1])));
/*     */       }
/* 306 */     } catch (Win32Exception we) {
/* 307 */       LOG.error("Unable to locate English counter names in registry Perflib 009. Counters may need to be rebuilt: ", (Throwable)we);
/*     */     
/*     */     }
/* 310 */     catch (NumberFormatException nfe) {
/*     */       
/* 312 */       LOG.error("Unable to parse English counter names in registry Perflib 009.");
/*     */     } 
/* 314 */     return Collections.unmodifiableMap(indexMap);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\driver\windows\registry\HkeyPerformanceDataUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */