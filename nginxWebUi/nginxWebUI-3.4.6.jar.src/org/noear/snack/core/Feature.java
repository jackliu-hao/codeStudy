/*     */ package org.noear.snack.core;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public enum Feature
/*     */ {
/*   8 */   QuoteFieldNames,
/*     */ 
/*     */   
/*  11 */   OrderedField,
/*     */ 
/*     */   
/*  14 */   WriteClassName,
/*     */ 
/*     */   
/*  17 */   WriteArrayClassName,
/*     */ 
/*     */   
/*  20 */   WriteDateUseTicks,
/*     */ 
/*     */   
/*  23 */   WriteDateUseFormat,
/*     */ 
/*     */   
/*  26 */   WriteBoolUse01,
/*     */ 
/*     */   
/*  29 */   WriteSlashAsSpecial,
/*     */ 
/*     */   
/*  32 */   WriteNumberUseString,
/*     */ 
/*     */   
/*  35 */   BrowserSecure,
/*     */ 
/*     */   
/*  38 */   BrowserCompatible,
/*     */ 
/*     */ 
/*     */   
/*  42 */   TransferCompatible,
/*     */ 
/*     */   
/*  45 */   EnumUsingName,
/*     */ 
/*     */   
/*  48 */   StringNullAsEmpty,
/*     */ 
/*     */   
/*  51 */   StringFieldInitEmpty,
/*     */ 
/*     */   
/*  54 */   SerializeNulls,
/*     */ 
/*     */   
/*  57 */   UseSingleQuotes,
/*     */ 
/*     */ 
/*     */   
/*  61 */   UseSetter,
/*     */ 
/*     */   
/*  64 */   DisThreadLocal,
/*     */ 
/*     */   
/*  67 */   StringJsonToNode;
/*     */   public final int code;
/*     */   
/*     */   Feature() {
/*  71 */     this.code = 1 << ordinal();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isEnabled(int features, Feature feature) {
/*  79 */     return ((features & feature.code) != 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public static int config(int features, Feature feature, boolean enable) {
/*  84 */     if (enable) {
/*  85 */       features |= feature.code;
/*     */     } else {
/*  87 */       features &= feature.code ^ 0xFFFFFFFF;
/*     */     } 
/*     */     
/*  90 */     return features;
/*     */   }
/*     */ 
/*     */   
/*     */   public static int of(Feature... features) {
/*  95 */     if (features == null) {
/*  96 */       return 0;
/*     */     }
/*     */     
/*  99 */     int value = 0;
/*     */     
/* 101 */     for (Feature feature : features) {
/* 102 */       value |= feature.code;
/*     */     }
/*     */     
/* 105 */     return value;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\snack\core\Feature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */