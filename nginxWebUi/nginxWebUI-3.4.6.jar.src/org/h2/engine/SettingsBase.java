/*     */ package org.h2.engine;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.util.Utils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SettingsBase
/*     */ {
/*     */   private final HashMap<String, String> settings;
/*     */   
/*     */   protected SettingsBase(HashMap<String, String> paramHashMap) {
/*  26 */     this.settings = paramHashMap;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean get(String paramString, boolean paramBoolean) {
/*  37 */     String str = get(paramString, Boolean.toString(paramBoolean));
/*     */     try {
/*  39 */       return Utils.parseBoolean(str, paramBoolean, true);
/*  40 */     } catch (IllegalArgumentException illegalArgumentException) {
/*  41 */       throw DbException.get(22018, illegalArgumentException, new String[] { "key:" + paramString + " value:" + str });
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
/*     */   void set(String paramString, boolean paramBoolean) {
/*  53 */     this.settings.put(paramString, Boolean.toString(paramBoolean));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int get(String paramString, int paramInt) {
/*  64 */     String str = get(paramString, Integer.toString(paramInt));
/*     */     try {
/*  66 */       return Integer.decode(str).intValue();
/*  67 */     } catch (NumberFormatException numberFormatException) {
/*  68 */       throw DbException.get(22018, numberFormatException, new String[] { "key:" + paramString + " value:" + str });
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
/*     */   protected String get(String paramString1, String paramString2) {
/*  81 */     String str1 = this.settings.get(paramString1);
/*  82 */     if (str1 != null) {
/*  83 */       return str1;
/*     */     }
/*  85 */     StringBuilder stringBuilder = new StringBuilder("h2.");
/*  86 */     boolean bool = false;
/*  87 */     for (char c : paramString1.toCharArray()) {
/*  88 */       if (c == '_') {
/*  89 */         bool = true;
/*     */       } else {
/*     */         
/*  92 */         stringBuilder.append(bool ? Character.toUpperCase(c) : Character.toLowerCase(c));
/*  93 */         bool = false;
/*     */       } 
/*     */     } 
/*  96 */     String str2 = stringBuilder.toString();
/*  97 */     str1 = Utils.getProperty(str2, paramString2);
/*  98 */     this.settings.put(paramString1, str1);
/*  99 */     return str1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean containsKey(String paramString) {
/* 109 */     return this.settings.containsKey(paramString);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HashMap<String, String> getSettings() {
/* 118 */     return this.settings;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map.Entry<String, String>[] getSortedSettings() {
/* 128 */     Map.Entry[] arrayOfEntry = (Map.Entry[])this.settings.entrySet().toArray((Object[])new Map.Entry[0]);
/* 129 */     Arrays.sort(arrayOfEntry, Comparator.comparing(Map.Entry::getKey));
/* 130 */     return (Map.Entry<String, String>[])arrayOfEntry;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\engine\SettingsBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */