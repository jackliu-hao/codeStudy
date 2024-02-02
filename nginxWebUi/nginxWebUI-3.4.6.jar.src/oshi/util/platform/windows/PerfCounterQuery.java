/*     */ package oshi.util.platform.windows;
/*     */ 
/*     */ import com.sun.jna.platform.win32.COM.WbemcliUtil;
/*     */ import com.sun.jna.platform.win32.PdhUtil;
/*     */ import com.sun.jna.platform.win32.Win32Exception;
/*     */ import java.util.EnumMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.locks.ReentrantLock;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ import oshi.annotation.concurrent.GuardedBy;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
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
/*     */ public final class PerfCounterQuery
/*     */ {
/*  52 */   private static final Logger LOG = LoggerFactory.getLogger(PerfCounterQuery.class);
/*     */ 
/*     */   
/*     */   @GuardedBy("failedQueryCacheLock")
/*  56 */   private static final Set<String> failedQueryCache = new HashSet<>();
/*  57 */   private static final ReentrantLock failedQueryCacheLock = new ReentrantLock();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String TOTAL_INSTANCE = "_Total";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String TOTAL_INSTANCES = "*_Total";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String NOT_TOTAL_INSTANCE = "^_Total";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String NOT_TOTAL_INSTANCES = "^*_Total";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T extends Enum<T>> Map<T, Long> queryValues(Class<T> propertyEnum, String perfObject, String perfWmiClass) {
/*  92 */     if (!failedQueryCache.contains(perfObject)) {
/*  93 */       failedQueryCacheLock.lock();
/*     */       
/*     */       try {
/*  96 */         if (!failedQueryCache.contains(perfObject)) {
/*  97 */           Map<T, Long> valueMap = queryValuesFromPDH(propertyEnum, perfObject);
/*  98 */           if (!valueMap.isEmpty()) {
/*  99 */             return valueMap;
/*     */           }
/*     */           
/* 102 */           LOG.warn("Disabling further attempts to query {}.", perfObject);
/* 103 */           failedQueryCache.add(perfObject);
/*     */         } 
/*     */       } finally {
/* 106 */         failedQueryCacheLock.unlock();
/*     */       } 
/*     */     } 
/* 109 */     return queryValuesFromWMI(propertyEnum, perfWmiClass);
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
/*     */   public static <T extends Enum<T>> Map<T, Long> queryValuesFromPDH(Class<T> propertyEnum, String perfObject) {
/* 130 */     Enum[] arrayOfEnum = (Enum[])propertyEnum.getEnumConstants();
/* 131 */     String perfObjectLocalized = localize(perfObject);
/* 132 */     EnumMap<T, PerfDataUtil.PerfCounter> counterMap = new EnumMap<>(propertyEnum);
/* 133 */     EnumMap<T, Long> valueMap = new EnumMap<>(propertyEnum);
/* 134 */     PerfCounterQueryHandler pdhQueryHandler = new PerfCounterQueryHandler();
/*     */     
/* 136 */     try { for (Enum enum_ : arrayOfEnum)
/* 137 */       { PerfDataUtil.PerfCounter counter = PerfDataUtil.createCounter(perfObjectLocalized, ((PdhCounterProperty)enum_)
/* 138 */             .getInstance(), ((PdhCounterProperty)enum_).getCounter());
/* 139 */         counterMap.put((T)enum_, counter);
/* 140 */         if (!pdhQueryHandler.addCounterToQuery(counter))
/* 141 */         { EnumMap<T, Long> enumMap = valueMap;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 150 */           pdhQueryHandler.close(); return enumMap; }  }  if (0L < pdhQueryHandler.updateQuery()) for (Enum enum_ : arrayOfEnum) valueMap.put((T)enum_, Long.valueOf(pdhQueryHandler.queryCounter(counterMap.get(enum_))));   pdhQueryHandler.close(); } catch (Throwable throwable) { try { pdhQueryHandler.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }  throw throwable; }
/* 151 */      return valueMap;
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
/*     */   public static <T extends Enum<T>> Map<T, Long> queryValuesFromWMI(Class<T> propertyEnum, String wmiClass) {
/* 171 */     WbemcliUtil.WmiQuery<T> query = new WbemcliUtil.WmiQuery(wmiClass, propertyEnum);
/* 172 */     WbemcliUtil.WmiResult<T> result = WmiQueryHandler.createInstance().queryWMI(query);
/* 173 */     EnumMap<T, Long> valueMap = new EnumMap<>(propertyEnum);
/* 174 */     if (result.getResultCount() > 0) {
/* 175 */       for (Enum enum_ : (Enum[])propertyEnum.getEnumConstants()) {
/* 176 */         switch (result.getCIMType(enum_)) {
/*     */           case 18:
/* 178 */             valueMap.put((T)enum_, Long.valueOf(WmiUtil.<T>getUint16(result, (T)enum_, 0)));
/*     */             break;
/*     */           case 19:
/* 181 */             valueMap.put((T)enum_, Long.valueOf(WmiUtil.getUint32asLong(result, (T)enum_, 0)));
/*     */             break;
/*     */           case 21:
/* 184 */             valueMap.put((T)enum_, Long.valueOf(WmiUtil.getUint64(result, (T)enum_, 0)));
/*     */             break;
/*     */           case 101:
/* 187 */             valueMap.put((T)enum_, Long.valueOf(WmiUtil.<T>getDateTime(result, (T)enum_, 0).toInstant().toEpochMilli()));
/*     */             break;
/*     */           default:
/* 190 */             throw new ClassCastException("Unimplemented CIM Type Mapping.");
/*     */         } 
/*     */       } 
/*     */     }
/* 194 */     return valueMap;
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
/*     */   public static String localize(String perfObject) {
/* 211 */     String localized = perfObject;
/*     */     try {
/* 213 */       localized = PdhUtil.PdhLookupPerfNameByIndex(null, PdhUtil.PdhLookupPerfIndexByEnglishName(perfObject));
/* 214 */     } catch (Win32Exception e) {
/* 215 */       LOG.warn("Unable to locate English counter names in registry Perflib 009. Assuming English counters. Error {}. {}", 
/*     */           
/* 217 */           String.format("0x%x", new Object[] { Integer.valueOf(e.getHR().intValue()) }), "See https://support.microsoft.com/en-us/help/300956/how-to-manually-rebuild-performance-counter-library-values");
/*     */     }
/* 219 */     catch (com.sun.jna.platform.win32.PdhUtil.PdhException e) {
/* 220 */       LOG.warn("Unable to localize {} performance counter.  Error {}.", perfObject, 
/* 221 */           String.format("0x%x", new Object[] { Integer.valueOf(e.getErrorCode()) }));
/*     */     } 
/* 223 */     if (localized.isEmpty()) {
/* 224 */       return perfObject;
/*     */     }
/* 226 */     LOG.debug("Localized {} to {}", perfObject, localized);
/* 227 */     return localized;
/*     */   }
/*     */   
/*     */   public static interface PdhCounterProperty {
/*     */     String getInstance();
/*     */     
/*     */     String getCounter();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\osh\\util\platform\windows\PerfCounterQuery.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */