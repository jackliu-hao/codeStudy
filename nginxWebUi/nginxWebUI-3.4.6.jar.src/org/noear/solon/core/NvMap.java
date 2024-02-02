/*     */ package org.noear.solon.core;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.noear.solon.Utils;
/*     */ import org.noear.solon.ext.LinkedCaseInsensitiveMap;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class NvMap
/*     */   extends LinkedCaseInsensitiveMap<String>
/*     */ {
/*     */   public NvMap() {}
/*     */   
/*     */   public NvMap(Map map) {
/*  29 */     if (map != null) {
/*  30 */       map.forEach((k, v) -> {
/*     */             if (k != null && v != null) {
/*     */               put(k.toString(), v.toString());
/*     */             }
/*     */           });
/*     */     }
/*     */   }
/*     */   
/*     */   public NvMap set(String key, String val) {
/*  39 */     put(key, val);
/*  40 */     return this;
/*     */   }
/*     */   
/*     */   public static NvMap from(String[] args) {
/*  44 */     return from(Arrays.asList(args));
/*     */   }
/*     */   
/*     */   public static NvMap from(List<String> args) {
/*  48 */     NvMap d = new NvMap();
/*     */     
/*  50 */     if (args != null) {
/*  51 */       for (String arg : args) {
/*  52 */         int index = arg.indexOf('=');
/*  53 */         if (index > 0) {
/*  54 */           String name = arg.substring(0, index);
/*  55 */           String value = arg.substring(index + 1);
/*  56 */           d.put(name.replaceAll("^-*", ""), value); continue;
/*     */         } 
/*  58 */         d.put(arg.replaceAll("^-*", ""), "");
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*  63 */     return d;
/*     */   }
/*     */   
/*     */   public int getInt(String key) {
/*  67 */     return getInt(key, 0);
/*     */   }
/*     */   
/*     */   public int getInt(String key, int def) {
/*  71 */     String temp = (String)get(key);
/*  72 */     if (Utils.isEmpty(temp)) {
/*  73 */       return def;
/*     */     }
/*  75 */     return Integer.parseInt(temp);
/*     */   }
/*     */ 
/*     */   
/*     */   public long getLong(String key) {
/*  80 */     return getLong(key, 0L);
/*     */   }
/*     */   
/*     */   public long getLong(String key, long def) {
/*  84 */     String temp = (String)get(key);
/*  85 */     if (Utils.isEmpty(temp)) {
/*  86 */       return def;
/*     */     }
/*  88 */     return Long.parseLong(temp);
/*     */   }
/*     */ 
/*     */   
/*     */   public double getDouble(String key) {
/*  93 */     return getDouble(key, 0.0D);
/*     */   }
/*     */   
/*     */   public double getDouble(String key, double def) {
/*  97 */     String temp = (String)get(key);
/*  98 */     if (Utils.isEmpty(temp)) {
/*  99 */       return def;
/*     */     }
/* 101 */     return Double.parseDouble(temp);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getBool(String key, boolean def) {
/* 106 */     if (containsKey(key)) {
/* 107 */       return Boolean.parseBoolean((String)get(key));
/*     */     }
/* 109 */     return def;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\NvMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */