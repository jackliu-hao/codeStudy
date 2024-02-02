/*     */ package org.slf4j;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.util.Map;
/*     */ import org.slf4j.helpers.NOPMDCAdapter;
/*     */ import org.slf4j.helpers.Util;
/*     */ import org.slf4j.impl.StaticMDCBinder;
/*     */ import org.slf4j.spi.MDCAdapter;
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
/*     */ public class MDC
/*     */ {
/*     */   static final String NULL_MDCA_URL = "http://www.slf4j.org/codes.html#null_MDCA";
/*     */   static final String NO_STATIC_MDC_BINDER_URL = "http://www.slf4j.org/codes.html#no_static_mdc_binder";
/*     */   static MDCAdapter mdcAdapter;
/*     */   
/*     */   public static class MDCCloseable
/*     */     implements Closeable
/*     */   {
/*     */     private final String key;
/*     */     
/*     */     private MDCCloseable(String key) {
/*  77 */       this.key = key;
/*     */     }
/*     */     
/*     */     public void close() {
/*  81 */       MDC.remove(this.key);
/*     */     }
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
/*     */   private static MDCAdapter bwCompatibleGetMDCAdapterFromBinder() throws NoClassDefFoundError {
/*     */     try {
/*  99 */       return StaticMDCBinder.getSingleton().getMDCA();
/* 100 */     } catch (NoSuchMethodError nsme) {
/*     */       
/* 102 */       return StaticMDCBinder.SINGLETON.getMDCA();
/*     */     } 
/*     */   }
/*     */   
/*     */   static {
/*     */     try {
/* 108 */       mdcAdapter = bwCompatibleGetMDCAdapterFromBinder();
/* 109 */     } catch (NoClassDefFoundError ncde) {
/* 110 */       mdcAdapter = (MDCAdapter)new NOPMDCAdapter();
/* 111 */       String msg = ncde.getMessage();
/* 112 */       if (msg != null && msg.contains("StaticMDCBinder")) {
/* 113 */         Util.report("Failed to load class \"org.slf4j.impl.StaticMDCBinder\".");
/* 114 */         Util.report("Defaulting to no-operation MDCAdapter implementation.");
/* 115 */         Util.report("See http://www.slf4j.org/codes.html#no_static_mdc_binder for further details.");
/*     */       } else {
/* 117 */         throw ncde;
/*     */       } 
/* 119 */     } catch (Exception e) {
/*     */       
/* 121 */       Util.report("MDC binding unsuccessful.", e);
/*     */     } 
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
/*     */   public static void put(String key, String val) throws IllegalArgumentException {
/* 141 */     if (key == null) {
/* 142 */       throw new IllegalArgumentException("key parameter cannot be null");
/*     */     }
/* 144 */     if (mdcAdapter == null) {
/* 145 */       throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA");
/*     */     }
/* 147 */     mdcAdapter.put(key, val);
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
/*     */   public static MDCCloseable putCloseable(String key, String val) throws IllegalArgumentException {
/* 179 */     put(key, val);
/* 180 */     return new MDCCloseable(key);
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
/*     */   public static String get(String key) throws IllegalArgumentException {
/* 196 */     if (key == null) {
/* 197 */       throw new IllegalArgumentException("key parameter cannot be null");
/*     */     }
/*     */     
/* 200 */     if (mdcAdapter == null) {
/* 201 */       throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA");
/*     */     }
/* 203 */     return mdcAdapter.get(key);
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
/*     */   public static void remove(String key) throws IllegalArgumentException {
/* 217 */     if (key == null) {
/* 218 */       throw new IllegalArgumentException("key parameter cannot be null");
/*     */     }
/*     */     
/* 221 */     if (mdcAdapter == null) {
/* 222 */       throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA");
/*     */     }
/* 224 */     mdcAdapter.remove(key);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void clear() {
/* 231 */     if (mdcAdapter == null) {
/* 232 */       throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA");
/*     */     }
/* 234 */     mdcAdapter.clear();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Map<String, String> getCopyOfContextMap() {
/* 245 */     if (mdcAdapter == null) {
/* 246 */       throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA");
/*     */     }
/* 248 */     return mdcAdapter.getCopyOfContextMap();
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
/*     */   public static void setContextMap(Map<String, String> contextMap) {
/* 261 */     if (mdcAdapter == null) {
/* 262 */       throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA");
/*     */     }
/* 264 */     mdcAdapter.setContextMap(contextMap);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static MDCAdapter getMDCAdapter() {
/* 274 */     return mdcAdapter;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\slf4j\MDC.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */