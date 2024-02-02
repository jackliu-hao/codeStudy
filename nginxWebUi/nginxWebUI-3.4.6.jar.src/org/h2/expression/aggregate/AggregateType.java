/*     */ package org.h2.expression.aggregate;
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
/*     */ public enum AggregateType
/*     */ {
/*  16 */   COUNT_ALL,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  21 */   COUNT,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  26 */   SUM,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  31 */   MIN,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  36 */   MAX,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  41 */   AVG,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  46 */   STDDEV_POP,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  51 */   STDDEV_SAMP,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  56 */   VAR_POP,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  61 */   VAR_SAMP,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  66 */   ANY,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  71 */   EVERY,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  76 */   BIT_AND_AGG,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  81 */   BIT_OR_AGG,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  86 */   BIT_XOR_AGG,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  91 */   BIT_NAND_AGG,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  96 */   BIT_NOR_AGG,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 101 */   BIT_XNOR_AGG,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 106 */   HISTOGRAM,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 111 */   COVAR_POP,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 116 */   COVAR_SAMP,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 121 */   CORR,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 126 */   REGR_SLOPE,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 131 */   REGR_INTERCEPT,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 136 */   REGR_COUNT,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 141 */   REGR_R2,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 146 */   REGR_AVGX,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 151 */   REGR_AVGY,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 156 */   REGR_SXX,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 161 */   REGR_SYY,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 166 */   REGR_SXY,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 171 */   RANK,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 176 */   DENSE_RANK,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 181 */   PERCENT_RANK,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 186 */   CUME_DIST,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 191 */   PERCENTILE_CONT,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 196 */   PERCENTILE_DISC,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 201 */   MEDIAN,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 206 */   LISTAGG,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 211 */   ARRAY_AGG,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 216 */   MODE,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 221 */   ENVELOPE,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 226 */   JSON_OBJECTAGG,
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 231 */   JSON_ARRAYAGG;
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\aggregate\AggregateType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */