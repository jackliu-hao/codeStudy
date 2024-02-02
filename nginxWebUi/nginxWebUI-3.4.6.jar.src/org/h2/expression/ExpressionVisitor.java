/*     */ package org.h2.expression;
/*     */ 
/*     */ import java.util.HashSet;
/*     */ import org.h2.command.query.AllColumnsForPlan;
/*     */ import org.h2.engine.DbObject;
/*     */ import org.h2.table.Column;
/*     */ import org.h2.table.ColumnResolver;
/*     */ import org.h2.table.Table;
/*     */ import org.h2.table.TableFilter;
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
/*     */ public final class ExpressionVisitor
/*     */ {
/*     */   public static final int INDEPENDENT = 0;
/*  32 */   public static final ExpressionVisitor INDEPENDENT_VISITOR = new ExpressionVisitor(0);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int OPTIMIZABLE_AGGREGATE = 1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int DETERMINISTIC = 2;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  49 */   public static final ExpressionVisitor DETERMINISTIC_VISITOR = new ExpressionVisitor(2);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int EVALUATABLE = 3;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  61 */   public static final ExpressionVisitor EVALUATABLE_VISITOR = new ExpressionVisitor(3);
/*     */ 
/*     */   
/*     */   private static final int CACHED = 8;
/*     */ 
/*     */   
/*     */   private static final ExpressionVisitor[] INDEPENDENT_VISITORS;
/*     */ 
/*     */   
/*     */   private static final ExpressionVisitor[] EVALUATABLE_VISITORS;
/*     */ 
/*     */   
/*     */   public static final int SET_MAX_DATA_MODIFICATION_ID = 4;
/*     */ 
/*     */   
/*     */   public static final int READONLY = 5;
/*     */ 
/*     */ 
/*     */   
/*     */   static {
/*  81 */     ExpressionVisitor[] arrayOfExpressionVisitor = new ExpressionVisitor[8];
/*  82 */     arrayOfExpressionVisitor[0] = INDEPENDENT_VISITOR; byte b;
/*  83 */     for (b = 1; b < 8; b++) {
/*  84 */       arrayOfExpressionVisitor[b] = new ExpressionVisitor(0, b);
/*     */     }
/*  86 */     INDEPENDENT_VISITORS = arrayOfExpressionVisitor;
/*  87 */     arrayOfExpressionVisitor = new ExpressionVisitor[8];
/*  88 */     arrayOfExpressionVisitor[0] = EVALUATABLE_VISITOR;
/*  89 */     for (b = 1; b < 8; b++) {
/*  90 */       arrayOfExpressionVisitor[b] = new ExpressionVisitor(3, b);
/*     */     }
/*  92 */     EVALUATABLE_VISITORS = arrayOfExpressionVisitor;
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
/* 108 */   public static final ExpressionVisitor READONLY_VISITOR = new ExpressionVisitor(5);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int NOT_FROM_RESOLVER = 6;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int GET_DEPENDENCIES = 7;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int QUERY_COMPARABLE = 8;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int GET_COLUMNS1 = 9;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int GET_COLUMNS2 = 10;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int DECREMENT_QUERY_LEVEL = 11;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 148 */   public static final ExpressionVisitor QUERY_COMPARABLE_VISITOR = new ExpressionVisitor(8);
/*     */   
/*     */   private final int type;
/*     */   
/*     */   private final int queryLevel;
/*     */   
/*     */   private final HashSet<?> set;
/*     */   
/*     */   private final AllColumnsForPlan columns1;
/*     */   
/*     */   private final Table table;
/*     */   
/*     */   private final long[] maxDataModificationId;
/*     */   private final ColumnResolver resolver;
/*     */   
/*     */   private ExpressionVisitor(int paramInt1, int paramInt2, HashSet<?> paramHashSet, AllColumnsForPlan paramAllColumnsForPlan, Table paramTable, ColumnResolver paramColumnResolver, long[] paramArrayOflong) {
/* 164 */     this.type = paramInt1;
/* 165 */     this.queryLevel = paramInt2;
/* 166 */     this.set = paramHashSet;
/* 167 */     this.columns1 = paramAllColumnsForPlan;
/* 168 */     this.table = paramTable;
/* 169 */     this.resolver = paramColumnResolver;
/* 170 */     this.maxDataModificationId = paramArrayOflong;
/*     */   }
/*     */   
/*     */   private ExpressionVisitor(int paramInt) {
/* 174 */     this.type = paramInt;
/* 175 */     this.queryLevel = 0;
/* 176 */     this.set = null;
/* 177 */     this.columns1 = null;
/* 178 */     this.table = null;
/* 179 */     this.resolver = null;
/* 180 */     this.maxDataModificationId = null;
/*     */   }
/*     */   
/*     */   private ExpressionVisitor(int paramInt1, int paramInt2) {
/* 184 */     this.type = paramInt1;
/* 185 */     this.queryLevel = paramInt2;
/* 186 */     this.set = null;
/* 187 */     this.columns1 = null;
/* 188 */     this.table = null;
/* 189 */     this.resolver = null;
/* 190 */     this.maxDataModificationId = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ExpressionVisitor getDependenciesVisitor(HashSet<DbObject> paramHashSet) {
/* 201 */     return new ExpressionVisitor(7, 0, paramHashSet, null, null, null, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ExpressionVisitor getOptimizableVisitor(Table paramTable) {
/* 212 */     return new ExpressionVisitor(1, 0, null, null, paramTable, null, null);
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
/*     */   public static ExpressionVisitor getNotFromResolverVisitor(ColumnResolver paramColumnResolver) {
/* 224 */     return new ExpressionVisitor(6, 0, null, null, null, paramColumnResolver, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ExpressionVisitor getColumnsVisitor(AllColumnsForPlan paramAllColumnsForPlan) {
/* 235 */     return new ExpressionVisitor(9, 0, null, paramAllColumnsForPlan, null, null, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ExpressionVisitor getColumnsVisitor(HashSet<Column> paramHashSet, Table paramTable) {
/* 246 */     return new ExpressionVisitor(10, 0, paramHashSet, null, paramTable, null, null);
/*     */   }
/*     */   
/*     */   public static ExpressionVisitor getMaxModificationIdVisitor() {
/* 250 */     return new ExpressionVisitor(4, 0, null, null, null, null, new long[1]);
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
/*     */   public static ExpressionVisitor getDecrementQueryLevelVisitor(HashSet<ColumnResolver> paramHashSet, int paramInt) {
/* 267 */     return new ExpressionVisitor(11, paramInt, paramHashSet, null, null, null, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addDependency(DbObject paramDbObject) {
/* 278 */     this.set.add(paramDbObject);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void addColumn1(Column paramColumn) {
/* 288 */     this.columns1.add(paramColumn);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void addColumn2(Column paramColumn) {
/* 299 */     if (this.table == null || this.table == paramColumn.getTable()) {
/* 300 */       this.set.add(paramColumn);
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
/*     */   public HashSet<DbObject> getDependencies() {
/* 312 */     return (HashSet)this.set;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ExpressionVisitor incrementQueryLevel(int paramInt) {
/* 322 */     if (this.type == 0) {
/* 323 */       paramInt += this.queryLevel;
/* 324 */       return (paramInt < 8) ? INDEPENDENT_VISITORS[paramInt] : new ExpressionVisitor(0, paramInt);
/* 325 */     }  if (this.type == 3) {
/* 326 */       paramInt += this.queryLevel;
/* 327 */       return (paramInt < 8) ? EVALUATABLE_VISITORS[paramInt] : new ExpressionVisitor(3, paramInt);
/*     */     } 
/* 329 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ColumnResolver getResolver() {
/* 340 */     return this.resolver;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HashSet<ColumnResolver> getColumnResolvers() {
/* 351 */     return (HashSet)this.set;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addDataModificationId(long paramLong) {
/* 362 */     long l = this.maxDataModificationId[0];
/* 363 */     if (paramLong > l) {
/* 364 */       this.maxDataModificationId[0] = paramLong;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getMaxDataModificationId() {
/* 375 */     return this.maxDataModificationId[0];
/*     */   }
/*     */   
/*     */   int getQueryLevel() {
/* 379 */     assert this.type == 0 || this.type == 3 || this.type == 11;
/* 380 */     return this.queryLevel;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Table getTable() {
/* 390 */     return this.table;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getType() {
/* 399 */     return this.type;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void allColumnsForTableFilters(TableFilter[] paramArrayOfTableFilter, AllColumnsForPlan paramAllColumnsForPlan) {
/* 409 */     for (TableFilter tableFilter : paramArrayOfTableFilter) {
/* 410 */       if (tableFilter.getSelect() != null)
/* 411 */         tableFilter.getSelect().isEverything(getColumnsVisitor(paramAllColumnsForPlan)); 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\ExpressionVisitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */