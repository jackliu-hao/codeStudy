/*     */ package com.mysql.cj.xdevapi;
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
/*     */ public abstract class FilterableStatement<STMT_T, RES_T>
/*     */   extends PreparableStatement<RES_T>
/*     */   implements Statement<STMT_T, RES_T>
/*     */ {
/*     */   protected FilterParams filterParams;
/*     */   
/*     */   public FilterableStatement(FilterParams filterParams) {
/*  50 */     this.filterParams = filterParams;
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
/*     */   public STMT_T where(String searchCondition) {
/*  66 */     resetPrepareState();
/*  67 */     this.filterParams.setCriteria(searchCondition);
/*  68 */     return (STMT_T)this;
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
/*     */   public STMT_T sort(String... sortFields) {
/*  84 */     return orderBy(sortFields);
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
/*     */   public STMT_T orderBy(String... sortFields) {
/* 101 */     resetPrepareState();
/* 102 */     this.filterParams.setOrder(sortFields);
/* 103 */     return (STMT_T)this;
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
/*     */   public STMT_T limit(long numberOfRows) {
/* 123 */     if (this.filterParams.getLimit() == null) {
/* 124 */       setReprepareState();
/*     */     }
/* 126 */     this.filterParams.setLimit(Long.valueOf(numberOfRows));
/* 127 */     return (STMT_T)this;
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
/*     */   public STMT_T offset(long limitOffset) {
/* 148 */     this.filterParams.setOffset(Long.valueOf(limitOffset));
/* 149 */     return (STMT_T)this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isRelational() {
/* 158 */     return this.filterParams.isRelational();
/*     */   }
/*     */ 
/*     */   
/*     */   public STMT_T clearBindings() {
/* 163 */     this.filterParams.clearArgs();
/* 164 */     return (STMT_T)this;
/*     */   }
/*     */ 
/*     */   
/*     */   public STMT_T bind(String argName, Object value) {
/* 169 */     this.filterParams.addArg(argName, value);
/* 170 */     return (STMT_T)this;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\FilterableStatement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */