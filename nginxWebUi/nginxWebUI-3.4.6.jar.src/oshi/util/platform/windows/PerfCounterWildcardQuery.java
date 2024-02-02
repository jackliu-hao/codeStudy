/*     */ package oshi.util.platform.windows;
/*     */ 
/*     */ import com.sun.jna.platform.win32.COM.WbemcliUtil;
/*     */ import com.sun.jna.platform.win32.PdhUtil;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.EnumMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.locks.ReentrantLock;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ import oshi.annotation.concurrent.GuardedBy;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.util.Util;
/*     */ import oshi.util.tuples.Pair;
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
/*     */ 
/*     */ @ThreadSafe
/*     */ public final class PerfCounterWildcardQuery
/*     */ {
/*  57 */   private static final Logger LOG = LoggerFactory.getLogger(PerfCounterWildcardQuery.class);
/*     */ 
/*     */   
/*     */   @GuardedBy("failedQueryCacheLock")
/*  61 */   private static final Set<String> failedQueryCache = new HashSet<>();
/*  62 */   private static final ReentrantLock failedQueryCacheLock = new ReentrantLock();
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
/*     */   public static <T extends Enum<T>> Pair<List<String>, Map<T, List<Long>>> queryInstancesAndValues(Class<T> propertyEnum, String perfObject, String perfWmiClass) {
/*  90 */     if (!failedQueryCache.contains(perfObject)) {
/*  91 */       failedQueryCacheLock.lock();
/*     */       
/*     */       try {
/*  94 */         if (!failedQueryCache.contains(perfObject)) {
/*  95 */           Pair<List<String>, Map<T, List<Long>>> instancesAndValuesMap = queryInstancesAndValuesFromPDH(propertyEnum, perfObject);
/*     */           
/*  97 */           if (!((List)instancesAndValuesMap.getA()).isEmpty()) {
/*  98 */             return instancesAndValuesMap;
/*     */           }
/*     */           
/* 101 */           LOG.warn("Disabling further attempts to query {}.", perfObject);
/* 102 */           failedQueryCache.add(perfObject);
/*     */         } 
/*     */       } finally {
/* 105 */         failedQueryCacheLock.unlock();
/*     */       } 
/*     */     } 
/* 108 */     return queryInstancesAndValuesFromWMI(propertyEnum, perfWmiClass);
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
/*     */   public static <T extends Enum<T>> Pair<List<String>, Map<T, List<Long>>> queryInstancesAndValuesFromPDH(Class<T> propertyEnum, String perfObject) {
/*     */     PdhUtil.PdhEnumObjectItems objectItems;
/* 131 */     Enum[] arrayOfEnum = (Enum[])propertyEnum.getEnumConstants();
/* 132 */     if (arrayOfEnum.length < 2) {
/* 133 */       throw new IllegalArgumentException("Enum " + propertyEnum.getName() + " must have at least two elements, an instance filter and a counter.");
/*     */     }
/*     */ 
/*     */     
/* 137 */     String instanceFilter = ((PdhCounterWildcardProperty)((Enum[])propertyEnum.getEnumConstants())[0]).getCounter().toLowerCase();
/* 138 */     String perfObjectLocalized = PerfCounterQuery.localize(perfObject);
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 143 */       objectItems = PdhUtil.PdhEnumObjectItems(null, null, perfObjectLocalized, 100);
/* 144 */     } catch (com.sun.jna.platform.win32.PdhUtil.PdhException e) {
/* 145 */       return new Pair(Collections.emptyList(), Collections.emptyMap());
/*     */     } 
/* 147 */     List<String> instances = objectItems.getInstances();
/*     */     
/* 149 */     instances.removeIf(i -> !Util.wildcardMatch(i.toLowerCase(), instanceFilter));
/* 150 */     EnumMap<T, List<Long>> valuesMap = new EnumMap<>(propertyEnum);
/* 151 */     PerfCounterQueryHandler pdhQueryHandler = new PerfCounterQueryHandler();
/*     */     
/* 153 */     try { EnumMap<T, List<PerfDataUtil.PerfCounter>> counterListMap = new EnumMap<>(propertyEnum);
/*     */       int i;
/* 155 */       for (i = 1; i < arrayOfEnum.length; i++)
/* 156 */       { Enum enum_ = arrayOfEnum[i];
/* 157 */         List<PerfDataUtil.PerfCounter> counterList = new ArrayList<>(instances.size());
/* 158 */         for (String instance : instances)
/* 159 */         { PerfDataUtil.PerfCounter counter = PerfDataUtil.createCounter(perfObject, instance, ((PdhCounterWildcardProperty)enum_)
/* 160 */               .getCounter());
/* 161 */           if (!pdhQueryHandler.addCounterToQuery(counter))
/* 162 */           { Pair<List<String>, Map<T, List<Long>>> pair = new Pair(Collections.emptyList(), Collections.emptyMap());
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
/* 180 */             pdhQueryHandler.close(); return pair; }  counterList.add(counter); }  counterListMap.put((T)enum_, counterList); }  if (0L < pdhQueryHandler.updateQuery()) for (i = 1; i < arrayOfEnum.length; i++) { Enum enum_ = arrayOfEnum[i]; List<Long> values = new ArrayList<>(); for (PerfDataUtil.PerfCounter counter : counterListMap.get(enum_)) values.add(Long.valueOf(pdhQueryHandler.queryCounter(counter)));  valuesMap.put((T)enum_, values); }   pdhQueryHandler.close(); } catch (Throwable throwable) { try { pdhQueryHandler.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }  throw throwable; }
/* 181 */      return new Pair(instances, valuesMap);
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
/*     */   public static <T extends Enum<T>> Pair<List<String>, Map<T, List<Long>>> queryInstancesAndValuesFromWMI(Class<T> propertyEnum, String wmiClass) {
/* 203 */     List<String> instances = new ArrayList<>();
/* 204 */     EnumMap<T, List<Long>> valuesMap = new EnumMap<>(propertyEnum);
/* 205 */     WbemcliUtil.WmiQuery<T> query = new WbemcliUtil.WmiQuery(wmiClass, propertyEnum);
/* 206 */     WbemcliUtil.WmiResult<T> result = WmiQueryHandler.createInstance().queryWMI(query);
/* 207 */     if (result.getResultCount() > 0) {
/* 208 */       for (Enum enum_ : (Enum[])propertyEnum.getEnumConstants()) {
/*     */         
/* 210 */         if (enum_.ordinal() == 0) {
/* 211 */           for (int i = 0; i < result.getResultCount(); i++) {
/* 212 */             instances.add(WmiUtil.getString(result, (T)enum_, i));
/*     */           }
/*     */         } else {
/* 215 */           List<Long> values = new ArrayList<>();
/* 216 */           for (int i = 0; i < result.getResultCount(); i++) {
/* 217 */             switch (result.getCIMType(enum_)) {
/*     */               case 18:
/* 219 */                 values.add(Long.valueOf(WmiUtil.<T>getUint16(result, (T)enum_, i)));
/*     */                 break;
/*     */               case 19:
/* 222 */                 values.add(Long.valueOf(WmiUtil.getUint32asLong(result, (T)enum_, i)));
/*     */                 break;
/*     */               case 21:
/* 225 */                 values.add(Long.valueOf(WmiUtil.getUint64(result, (T)enum_, i)));
/*     */                 break;
/*     */               case 101:
/* 228 */                 values.add(Long.valueOf(WmiUtil.<T>getDateTime(result, (T)enum_, i).toInstant().toEpochMilli()));
/*     */                 break;
/*     */               default:
/* 231 */                 throw new ClassCastException("Unimplemented CIM Type Mapping.");
/*     */             } 
/*     */           } 
/* 234 */           valuesMap.put((T)enum_, values);
/*     */         } 
/*     */       } 
/*     */     }
/* 238 */     return new Pair(instances, valuesMap);
/*     */   }
/*     */   
/*     */   public static interface PdhCounterWildcardProperty {
/*     */     String getCounter();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\osh\\util\platform\windows\PerfCounterWildcardQuery.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */