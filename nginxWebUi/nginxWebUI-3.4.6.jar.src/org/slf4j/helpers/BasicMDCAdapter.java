/*     */ package org.slf4j.helpers;
/*     */ 
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
/*     */ public class BasicMDCAdapter
/*     */   implements MDCAdapter
/*     */ {
/*  47 */   private InheritableThreadLocal<Map<String, String>> inheritableThreadLocal = new InheritableThreadLocal<Map<String, String>>()
/*     */     {
/*     */       protected Map<String, String> childValue(Map<String, String> parentValue) {
/*  50 */         if (parentValue == null) {
/*  51 */           return null;
/*     */         }
/*  53 */         return new HashMap<String, String>(parentValue);
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void put(String key, String val) {
/*  70 */     if (key == null) {
/*  71 */       throw new IllegalArgumentException("key cannot be null");
/*     */     }
/*  73 */     Map<String, String> map = this.inheritableThreadLocal.get();
/*  74 */     if (map == null) {
/*  75 */       map = new HashMap<String, String>();
/*  76 */       this.inheritableThreadLocal.set(map);
/*     */     } 
/*  78 */     map.put(key, val);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String get(String key) {
/*  85 */     Map<String, String> map = this.inheritableThreadLocal.get();
/*  86 */     if (map != null && key != null) {
/*  87 */       return map.get(key);
/*     */     }
/*  89 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void remove(String key) {
/*  97 */     Map<String, String> map = this.inheritableThreadLocal.get();
/*  98 */     if (map != null) {
/*  99 */       map.remove(key);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clear() {
/* 107 */     Map<String, String> map = this.inheritableThreadLocal.get();
/* 108 */     if (map != null) {
/* 109 */       map.clear();
/* 110 */       this.inheritableThreadLocal.remove();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Set<String> getKeys() {
/* 121 */     Map<String, String> map = this.inheritableThreadLocal.get();
/* 122 */     if (map != null) {
/* 123 */       return map.keySet();
/*     */     }
/* 125 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<String, String> getCopyOfContextMap() {
/* 135 */     Map<String, String> oldMap = this.inheritableThreadLocal.get();
/* 136 */     if (oldMap != null) {
/* 137 */       return new HashMap<String, String>(oldMap);
/*     */     }
/* 139 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setContextMap(Map<String, String> contextMap) {
/* 144 */     this.inheritableThreadLocal.set(new HashMap<String, String>(contextMap));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\slf4j\helpers\BasicMDCAdapter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */