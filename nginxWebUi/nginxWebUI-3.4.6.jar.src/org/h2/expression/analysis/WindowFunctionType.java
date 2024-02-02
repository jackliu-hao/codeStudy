/*     */ package org.h2.expression.analysis;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public enum WindowFunctionType
/*     */ {
/*  16 */   ROW_NUMBER,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  21 */   RANK,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  26 */   DENSE_RANK,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  31 */   PERCENT_RANK,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  36 */   CUME_DIST,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  41 */   NTILE,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  46 */   LEAD,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  51 */   LAG,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  56 */   FIRST_VALUE,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  61 */   LAST_VALUE,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  66 */   NTH_VALUE,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  71 */   RATIO_TO_REPORT;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static WindowFunctionType get(String paramString) {
/*  83 */     switch (paramString) {
/*     */       case "ROW_NUMBER":
/*  85 */         return ROW_NUMBER;
/*     */       case "RANK":
/*  87 */         return RANK;
/*     */       case "DENSE_RANK":
/*  89 */         return DENSE_RANK;
/*     */       case "PERCENT_RANK":
/*  91 */         return PERCENT_RANK;
/*     */       case "CUME_DIST":
/*  93 */         return CUME_DIST;
/*     */       case "NTILE":
/*  95 */         return NTILE;
/*     */       case "LEAD":
/*  97 */         return LEAD;
/*     */       case "LAG":
/*  99 */         return LAG;
/*     */       case "FIRST_VALUE":
/* 101 */         return FIRST_VALUE;
/*     */       case "LAST_VALUE":
/* 103 */         return LAST_VALUE;
/*     */       case "NTH_VALUE":
/* 105 */         return NTH_VALUE;
/*     */       case "RATIO_TO_REPORT":
/* 107 */         return RATIO_TO_REPORT;
/*     */     } 
/* 109 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getSQL() {
/* 120 */     return name();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean requiresWindowOrdering() {
/* 130 */     switch (this) {
/*     */       case RANK:
/*     */       case DENSE_RANK:
/*     */       case NTILE:
/*     */       case LEAD:
/*     */       case LAG:
/* 136 */         return true;
/*     */     } 
/* 138 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\analysis\WindowFunctionType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */