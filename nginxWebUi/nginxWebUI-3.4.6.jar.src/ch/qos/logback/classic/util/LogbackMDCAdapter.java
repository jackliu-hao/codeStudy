/*     */ package ch.qos.logback.classic.util;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
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
/*     */ public class LogbackMDCAdapter
/*     */   implements MDCAdapter
/*     */ {
/*  55 */   final ThreadLocal<Map<String, String>> copyOnThreadLocal = new ThreadLocal<Map<String, String>>();
/*     */   
/*     */   private static final int WRITE_OPERATION = 1;
/*     */   
/*     */   private static final int MAP_COPY_OPERATION = 2;
/*     */   
/*  61 */   final ThreadLocal<Integer> lastOperation = new ThreadLocal<Integer>();
/*     */   
/*     */   private Integer getAndSetLastOperation(int op) {
/*  64 */     Integer lastOp = this.lastOperation.get();
/*  65 */     this.lastOperation.set(Integer.valueOf(op));
/*  66 */     return lastOp;
/*     */   }
/*     */   
/*     */   private boolean wasLastOpReadOrNull(Integer lastOp) {
/*  70 */     return (lastOp == null || lastOp.intValue() == 2);
/*     */   }
/*     */   
/*     */   private Map<String, String> duplicateAndInsertNewMap(Map<String, String> oldMap) {
/*  74 */     Map<String, String> newMap = Collections.synchronizedMap(new HashMap<String, String>());
/*  75 */     if (oldMap != null)
/*     */     {
/*     */       
/*  78 */       synchronized (oldMap) {
/*  79 */         newMap.putAll(oldMap);
/*     */       } 
/*     */     }
/*     */     
/*  83 */     this.copyOnThreadLocal.set(newMap);
/*  84 */     return newMap;
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
/*     */   public void put(String key, String val) throws IllegalArgumentException {
/*  99 */     if (key == null) {
/* 100 */       throw new IllegalArgumentException("key cannot be null");
/*     */     }
/*     */     
/* 103 */     Map<String, String> oldMap = this.copyOnThreadLocal.get();
/* 104 */     Integer lastOp = getAndSetLastOperation(1);
/*     */     
/* 106 */     if (wasLastOpReadOrNull(lastOp) || oldMap == null) {
/* 107 */       Map<String, String> newMap = duplicateAndInsertNewMap(oldMap);
/* 108 */       newMap.put(key, val);
/*     */     } else {
/* 110 */       oldMap.put(key, val);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void remove(String key) {
/* 119 */     if (key == null) {
/*     */       return;
/*     */     }
/* 122 */     Map<String, String> oldMap = this.copyOnThreadLocal.get();
/* 123 */     if (oldMap == null) {
/*     */       return;
/*     */     }
/* 126 */     Integer lastOp = getAndSetLastOperation(1);
/*     */     
/* 128 */     if (wasLastOpReadOrNull(lastOp)) {
/* 129 */       Map<String, String> newMap = duplicateAndInsertNewMap(oldMap);
/* 130 */       newMap.remove(key);
/*     */     } else {
/* 132 */       oldMap.remove(key);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clear() {
/* 140 */     this.lastOperation.set(Integer.valueOf(1));
/* 141 */     this.copyOnThreadLocal.remove();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String get(String key) {
/* 149 */     Map<String, String> map = this.copyOnThreadLocal.get();
/* 150 */     if (map != null && key != null) {
/* 151 */       return map.get(key);
/*     */     }
/* 153 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<String, String> getPropertyMap() {
/* 162 */     this.lastOperation.set(Integer.valueOf(2));
/* 163 */     return this.copyOnThreadLocal.get();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Set<String> getKeys() {
/* 171 */     Map<String, String> map = getPropertyMap();
/*     */     
/* 173 */     if (map != null) {
/* 174 */       return map.keySet();
/*     */     }
/* 176 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<String, String> getCopyOfContextMap() {
/* 185 */     Map<String, String> hashMap = this.copyOnThreadLocal.get();
/* 186 */     if (hashMap == null) {
/* 187 */       return null;
/*     */     }
/* 189 */     return new HashMap<String, String>(hashMap);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setContextMap(Map<String, String> contextMap) {
/* 194 */     this.lastOperation.set(Integer.valueOf(1));
/*     */     
/* 196 */     Map<String, String> newMap = Collections.synchronizedMap(new HashMap<String, String>());
/* 197 */     newMap.putAll(contextMap);
/*     */ 
/*     */     
/* 200 */     this.copyOnThreadLocal.set(newMap);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classi\\util\LogbackMDCAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */