/*     */ package cn.hutool.json;
/*     */ 
/*     */ import cn.hutool.core.comparator.CompareUtil;
/*     */ import java.io.Serializable;
/*     */ import java.util.Comparator;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JSONConfig
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 119730355204738278L;
/*     */   private Comparator<String> keyComparator;
/*     */   private boolean ignoreError;
/*     */   private boolean ignoreCase;
/*     */   private String dateFormat;
/*     */   private boolean ignoreNullValue = true;
/*     */   private boolean transientSupport = true;
/*     */   private boolean stripTrailingZeros = true;
/*     */   
/*     */   public static JSONConfig create() {
/*  53 */     return new JSONConfig();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public boolean isOrder() {
/*  64 */     return true;
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
/*     */   @Deprecated
/*     */   public JSONConfig setOrder(boolean order) {
/*  77 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Comparator<String> getKeyComparator() {
/*  88 */     return this.keyComparator;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JSONConfig setNatureKeyComparator() {
/*  98 */     return setKeyComparator(CompareUtil.naturalComparator());
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
/*     */   public JSONConfig setKeyComparator(Comparator<String> keyComparator) {
/* 110 */     this.keyComparator = keyComparator;
/* 111 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isIgnoreError() {
/* 120 */     return this.ignoreError;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JSONConfig setIgnoreError(boolean ignoreError) {
/* 130 */     this.ignoreError = ignoreError;
/* 131 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isIgnoreCase() {
/* 140 */     return this.ignoreCase;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JSONConfig setIgnoreCase(boolean ignoreCase) {
/* 150 */     this.ignoreCase = ignoreCase;
/* 151 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getDateFormat() {
/* 160 */     return this.dateFormat;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JSONConfig setDateFormat(String dateFormat) {
/* 171 */     this.dateFormat = dateFormat;
/* 172 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isIgnoreNullValue() {
/* 181 */     return this.ignoreNullValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JSONConfig setIgnoreNullValue(boolean ignoreNullValue) {
/* 191 */     this.ignoreNullValue = ignoreNullValue;
/* 192 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isTransientSupport() {
/* 202 */     return this.transientSupport;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JSONConfig setTransientSupport(boolean transientSupport) {
/* 213 */     this.transientSupport = transientSupport;
/* 214 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isStripTrailingZeros() {
/* 224 */     return this.stripTrailingZeros;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JSONConfig setStripTrailingZeros(boolean stripTrailingZeros) {
/* 235 */     this.stripTrailingZeros = stripTrailingZeros;
/* 236 */     return this;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\json\JSONConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */