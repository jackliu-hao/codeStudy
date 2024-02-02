/*    */ package com.mysql.cj.conf;
/*    */ 
/*    */ import com.mysql.cj.exceptions.ExceptionInterceptor;
/*    */ import com.mysql.cj.util.StringUtils;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MemorySizePropertyDefinition
/*    */   extends IntegerPropertyDefinition
/*    */ {
/*    */   private static final long serialVersionUID = -6878680905514177949L;
/*    */   
/*    */   public MemorySizePropertyDefinition(PropertyKey key, int defaultValue, boolean isRuntimeModifiable, String description, String sinceVersion, String category, int orderInCategory) {
/* 41 */     super(key, defaultValue, isRuntimeModifiable, description, sinceVersion, category, orderInCategory);
/*    */   }
/*    */ 
/*    */   
/*    */   public MemorySizePropertyDefinition(PropertyKey key, int defaultValue, boolean isRuntimeModifiable, String description, String sinceVersion, String category, int orderInCategory, int lowerBound, int upperBound) {
/* 46 */     super(key, defaultValue, isRuntimeModifiable, description, sinceVersion, category, orderInCategory, lowerBound, upperBound);
/*    */   }
/*    */ 
/*    */   
/*    */   public Integer parseObject(String value, ExceptionInterceptor exceptionInterceptor) {
/* 51 */     this.multiplier = 1;
/*    */     
/* 53 */     if (value.endsWith("k") || value.endsWith("K") || value.endsWith("kb") || value.endsWith("Kb") || value.endsWith("kB") || value.endsWith("KB")) {
/* 54 */       this.multiplier = 1024;
/* 55 */       int indexOfK = StringUtils.indexOfIgnoreCase(value, "k");
/* 56 */       value = value.substring(0, indexOfK);
/* 57 */     } else if (value.endsWith("m") || value.endsWith("M") || value.endsWith("mb") || value.endsWith("Mb") || value.endsWith("mB") || value.endsWith("MB")) {
/* 58 */       this.multiplier = 1048576;
/* 59 */       int indexOfM = StringUtils.indexOfIgnoreCase(value, "m");
/* 60 */       value = value.substring(0, indexOfM);
/* 61 */     } else if (value.endsWith("g") || value.endsWith("G") || value.endsWith("gb") || value.endsWith("Gb") || value.endsWith("gB") || value.endsWith("GB")) {
/* 62 */       this.multiplier = 1073741824;
/* 63 */       int indexOfG = StringUtils.indexOfIgnoreCase(value, "g");
/* 64 */       value = value.substring(0, indexOfG);
/*    */     } 
/*    */     
/* 67 */     return super.parseObject(value, exceptionInterceptor);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public RuntimeProperty<Integer> createRuntimeProperty() {
/* 77 */     return new MemorySizeProperty(this);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\conf\MemorySizePropertyDefinition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */