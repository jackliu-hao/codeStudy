/*      */ package org.h2.command.query;
/*      */ 
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import org.h2.command.Prepared;
/*      */ import org.h2.engine.Database;
/*      */ import org.h2.engine.DbObject;
/*      */ import org.h2.engine.Session;
/*      */ import org.h2.engine.SessionLocal;
/*      */ import org.h2.expression.Expression;
/*      */ import org.h2.expression.ExpressionColumn;
/*      */ import org.h2.expression.ExpressionVisitor;
/*      */ import org.h2.expression.Parameter;
/*      */ import org.h2.expression.ValueExpression;
/*      */ import org.h2.message.DbException;
/*      */ import org.h2.result.LocalResult;
/*      */ import org.h2.result.ResultInterface;
/*      */ import org.h2.result.ResultTarget;
/*      */ import org.h2.result.SortOrder;
/*      */ import org.h2.table.Column;
/*      */ import org.h2.table.ColumnResolver;
/*      */ import org.h2.table.Table;
/*      */ import org.h2.table.TableFilter;
/*      */ import org.h2.table.TableView;
/*      */ import org.h2.util.Utils;
/*      */ import org.h2.value.ExtTypeInfo;
/*      */ import org.h2.value.ExtTypeInfoRow;
/*      */ import org.h2.value.TypeInfo;
/*      */ import org.h2.value.Typed;
/*      */ import org.h2.value.Value;
/*      */ import org.h2.value.ValueInteger;
/*      */ import org.h2.value.ValueNull;
/*      */ 
/*      */ 
/*      */ 
/*      */ public abstract class Query
/*      */   extends Prepared
/*      */ {
/*      */   ArrayList<Expression> expressions;
/*      */   Expression[] expressionArray;
/*      */   ArrayList<QueryOrderBy> orderList;
/*      */   SortOrder sort;
/*      */   Expression fetchExpr;
/*      */   boolean fetchPercent;
/*      */   boolean withTies;
/*      */   Expression offsetExpr;
/*      */   boolean distinct;
/*      */   boolean randomAccessResult;
/*      */   int visibleColumnCount;
/*      */   int resultColumnCount;
/*      */   private boolean noCache;
/*      */   private long lastLimit;
/*      */   private long lastEvaluated;
/*      */   private ResultInterface lastResult;
/*      */   private Boolean lastExists;
/*      */   private Value[] lastParameters;
/*      */   private boolean cacheableChecked;
/*      */   private boolean neverLazy;
/*      */   boolean checkInit;
/*      */   boolean isPrepared;
/*      */   
/*      */   static final class OffsetFetch
/*      */   {
/*      */     final long offset;
/*      */     final long fetch;
/*      */     final boolean fetchPercent;
/*      */     
/*      */     OffsetFetch(long param1Long1, long param1Long2, boolean param1Boolean) {
/*   70 */       this.offset = param1Long1;
/*   71 */       this.fetch = param1Long2;
/*   72 */       this.fetchPercent = param1Boolean;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   Query(SessionLocal paramSessionLocal) {
/*  155 */     super(paramSessionLocal);
/*      */   }
/*      */   
/*      */   public void setNeverLazy(boolean paramBoolean) {
/*  159 */     this.neverLazy = paramBoolean;
/*      */   }
/*      */   
/*      */   public boolean isNeverLazy() {
/*  163 */     return this.neverLazy;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract boolean isUnion();
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultInterface queryMeta() {
/*  175 */     LocalResult localResult = new LocalResult(this.session, this.expressionArray, this.visibleColumnCount, this.resultColumnCount);
/*  176 */     localResult.done();
/*  177 */     return (ResultInterface)localResult;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected abstract ResultInterface queryWithoutCache(long paramLong, ResultTarget paramResultTarget);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private ResultInterface queryWithoutCacheLazyCheck(long paramLong, ResultTarget paramResultTarget) {
/*  192 */     boolean bool = (this.neverLazy && this.session.isLazyQueryExecution()) ? true : false;
/*  193 */     if (bool) {
/*  194 */       this.session.setLazyQueryExecution(false);
/*      */     }
/*      */     try {
/*  197 */       return queryWithoutCache(paramLong, paramResultTarget);
/*      */     } finally {
/*  199 */       if (bool) {
/*  200 */         this.session.setLazyQueryExecution(true);
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract void init();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ArrayList<Expression> getExpressions() {
/*  217 */     return this.expressions;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract double getCost();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getCostAsExpression() {
/*  237 */     return (int)Math.min(1000000.0D, 10.0D + 10.0D * getCost());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract HashSet<Table> getTables();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setOrder(ArrayList<QueryOrderBy> paramArrayList) {
/*  253 */     this.orderList = paramArrayList;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean hasOrder() {
/*  262 */     return (this.orderList != null || this.sort != null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract void setForUpdate(boolean paramBoolean);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getColumnCount() {
/*  278 */     return this.visibleColumnCount;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TypeInfo getRowDataType() {
/*  287 */     if (this.visibleColumnCount == 1) {
/*  288 */       return this.expressionArray[0].getType();
/*      */     }
/*  290 */     return TypeInfo.getTypeInfo(41, -1L, -1, (ExtTypeInfo)new ExtTypeInfoRow((Typed[])this.expressionArray, this.visibleColumnCount));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract void mapColumns(ColumnResolver paramColumnResolver, int paramInt);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract void setEvaluatable(TableFilter paramTableFilter, boolean paramBoolean);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract void addGlobalCondition(Parameter paramParameter, int paramInt1, int paramInt2);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract boolean allowGlobalConditions();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract boolean isEverything(ExpressionVisitor paramExpressionVisitor);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isReadOnly() {
/*  343 */     return isEverything(ExpressionVisitor.READONLY_VISITOR);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract void updateAggregate(SessionLocal paramSessionLocal, int paramInt);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract void fireBeforeSelectTriggers();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setDistinctIfPossible() {
/*  364 */     if (!isAnyDistinct() && this.offsetExpr == null && this.fetchExpr == null) {
/*  365 */       this.distinct = true;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isStandardDistinct() {
/*  373 */     return this.distinct;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isAnyDistinct() {
/*  381 */     return this.distinct;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isRandomAccessResult() {
/*  390 */     return this.randomAccessResult;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setRandomAccessResult(boolean paramBoolean) {
/*  399 */     this.randomAccessResult = paramBoolean;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isQuery() {
/*  404 */     return true;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isTransactional() {
/*  409 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void disableCache() {
/*  416 */     this.noCache = true;
/*      */   }
/*      */   
/*      */   private boolean sameResultAsLast(Value[] paramArrayOfValue1, Value[] paramArrayOfValue2, long paramLong) {
/*  420 */     if (!this.cacheableChecked) {
/*  421 */       long l = getMaxDataModificationId();
/*  422 */       this.noCache = (l == Long.MAX_VALUE);
/*  423 */       if (!isEverything(ExpressionVisitor.DETERMINISTIC_VISITOR) || 
/*  424 */         !isEverything(ExpressionVisitor.INDEPENDENT_VISITOR)) {
/*  425 */         this.noCache = true;
/*      */       }
/*  427 */       this.cacheableChecked = true;
/*      */     } 
/*  429 */     if (this.noCache) {
/*  430 */       return false;
/*      */     }
/*  432 */     for (byte b = 0; b < paramArrayOfValue1.length; b++) {
/*  433 */       Value value1 = paramArrayOfValue2[b], value2 = paramArrayOfValue1[b];
/*  434 */       if (value1.getValueType() != value2.getValueType() || !this.session.areEqual(value1, value2)) {
/*  435 */         return false;
/*      */       }
/*      */     } 
/*  438 */     return (getMaxDataModificationId() <= paramLong);
/*      */   }
/*      */   
/*      */   private Value[] getParameterValues() {
/*  442 */     ArrayList<Parameter> arrayList = getParameters();
/*  443 */     if (arrayList == null) {
/*  444 */       return Value.EMPTY_VALUES;
/*      */     }
/*  446 */     int i = arrayList.size();
/*  447 */     Value[] arrayOfValue = new Value[i];
/*  448 */     for (byte b = 0; b < i; b++) {
/*  449 */       Value value = ((Parameter)arrayList.get(b)).getParamValue();
/*  450 */       arrayOfValue[b] = value;
/*      */     } 
/*  452 */     return arrayOfValue;
/*      */   }
/*      */ 
/*      */   
/*      */   public final ResultInterface query(long paramLong) {
/*  457 */     return query(paramLong, (ResultTarget)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final ResultInterface query(long paramLong, ResultTarget paramResultTarget) {
/*  468 */     if (isUnion())
/*      */     {
/*      */       
/*  471 */       return queryWithoutCacheLazyCheck(paramLong, paramResultTarget);
/*      */     }
/*  473 */     fireBeforeSelectTriggers();
/*  474 */     if (this.noCache || !this.session.getDatabase().getOptimizeReuseResults() || (this.session
/*  475 */       .isLazyQueryExecution() && !this.neverLazy)) {
/*  476 */       return queryWithoutCacheLazyCheck(paramLong, paramResultTarget);
/*      */     }
/*  478 */     Value[] arrayOfValue = getParameterValues();
/*  479 */     long l = this.session.getDatabase().getModificationDataId();
/*  480 */     if (isEverything(ExpressionVisitor.DETERMINISTIC_VISITOR) && 
/*  481 */       this.lastResult != null && !this.lastResult.isClosed() && paramLong == this.lastLimit)
/*      */     {
/*  483 */       if (sameResultAsLast(arrayOfValue, this.lastParameters, this.lastEvaluated)) {
/*  484 */         this.lastResult = this.lastResult.createShallowCopy((Session)this.session);
/*  485 */         if (this.lastResult != null) {
/*  486 */           this.lastResult.reset();
/*  487 */           return this.lastResult;
/*      */         } 
/*      */       } 
/*      */     }
/*      */     
/*  492 */     this.lastParameters = arrayOfValue;
/*  493 */     closeLastResult();
/*  494 */     ResultInterface resultInterface = queryWithoutCacheLazyCheck(paramLong, paramResultTarget);
/*  495 */     this.lastResult = resultInterface;
/*  496 */     this.lastExists = null;
/*  497 */     this.lastEvaluated = l;
/*  498 */     this.lastLimit = paramLong;
/*  499 */     return resultInterface;
/*      */   }
/*      */   
/*      */   private void closeLastResult() {
/*  503 */     if (this.lastResult != null) {
/*  504 */       this.lastResult.close();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final boolean exists() {
/*  514 */     if (isUnion())
/*      */     {
/*      */       
/*  517 */       return executeExists();
/*      */     }
/*  519 */     fireBeforeSelectTriggers();
/*  520 */     if (this.noCache || !this.session.getDatabase().getOptimizeReuseResults()) {
/*  521 */       return executeExists();
/*      */     }
/*  523 */     Value[] arrayOfValue = getParameterValues();
/*  524 */     long l = this.session.getDatabase().getModificationDataId();
/*  525 */     if (isEverything(ExpressionVisitor.DETERMINISTIC_VISITOR) && 
/*  526 */       this.lastExists != null && 
/*  527 */       sameResultAsLast(arrayOfValue, this.lastParameters, this.lastEvaluated)) {
/*  528 */       return this.lastExists.booleanValue();
/*      */     }
/*      */ 
/*      */     
/*  532 */     this.lastParameters = arrayOfValue;
/*  533 */     boolean bool = executeExists();
/*  534 */     this.lastExists = Boolean.valueOf(bool);
/*  535 */     this.lastResult = null;
/*  536 */     this.lastEvaluated = l;
/*  537 */     return bool;
/*      */   }
/*      */   
/*      */   private boolean executeExists() {
/*  541 */     ResultInterface resultInterface = queryWithoutCacheLazyCheck(1L, (ResultTarget)null);
/*  542 */     boolean bool = resultInterface.hasNext();
/*  543 */     resultInterface.close();
/*  544 */     return bool;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   boolean initOrder(ArrayList<String> paramArrayList, boolean paramBoolean, ArrayList<TableFilter> paramArrayList1) {
/*  557 */     for (Iterator<QueryOrderBy> iterator = this.orderList.iterator(); iterator.hasNext(); ) {
/*  558 */       QueryOrderBy queryOrderBy = iterator.next();
/*  559 */       Expression expression = queryOrderBy.expression;
/*  560 */       if (expression == null) {
/*      */         continue;
/*      */       }
/*  563 */       if (expression.isConstant()) {
/*  564 */         iterator.remove();
/*      */         continue;
/*      */       } 
/*  567 */       int i = initExpression(paramArrayList, expression, paramBoolean, paramArrayList1);
/*  568 */       queryOrderBy.columnIndexExpr = (Expression)ValueExpression.get((Value)ValueInteger.get(i + 1));
/*  569 */       queryOrderBy.expression = ((Expression)this.expressions.get(i)).getNonAliasExpression();
/*      */     } 
/*  571 */     if (this.orderList.isEmpty()) {
/*  572 */       this.orderList = null;
/*  573 */       return false;
/*      */     } 
/*  575 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   int initExpression(ArrayList<String> paramArrayList, Expression paramExpression, boolean paramBoolean, ArrayList<TableFilter> paramArrayList1) {
/*  589 */     Database database = this.session.getDatabase();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  594 */     if (paramExpression instanceof ExpressionColumn) {
/*      */       
/*  596 */       ExpressionColumn expressionColumn = (ExpressionColumn)paramExpression;
/*  597 */       String str1 = expressionColumn.getOriginalTableAliasName();
/*  598 */       String str2 = expressionColumn.getOriginalColumnName(); byte b; int j;
/*  599 */       for (b = 0, j = getColumnCount(); b < j; b++) {
/*  600 */         Expression expression = this.expressions.get(b);
/*  601 */         if (expression instanceof ExpressionColumn) {
/*      */           
/*  603 */           ExpressionColumn expressionColumn1 = (ExpressionColumn)expression;
/*  604 */           if (database.equalsIdentifiers(str2, expressionColumn1.getColumnName(this.session, b))) {
/*      */ 
/*      */             
/*  607 */             if (str1 == null) {
/*  608 */               return b;
/*      */             }
/*  610 */             String str = expressionColumn1.getOriginalTableAliasName();
/*  611 */             if (str != null) {
/*  612 */               if (database.equalsIdentifiers(str, str1)) {
/*  613 */                 return b;
/*      */               }
/*  615 */             } else if (paramArrayList1 != null) {
/*      */               
/*  617 */               for (TableFilter tableFilter : paramArrayList1) {
/*  618 */                 if (database.equalsIdentifiers(tableFilter.getTableAlias(), str1))
/*  619 */                   return b; 
/*      */               } 
/*      */             } 
/*      */           } 
/*  623 */         } else if (expression instanceof org.h2.expression.Alias) {
/*  624 */           if (str1 == null && database.equalsIdentifiers(str2, expression.getAlias(this.session, b))) {
/*  625 */             return b;
/*      */           }
/*  627 */           Expression expression1 = expression.getNonAliasExpression();
/*  628 */           if (expression1 instanceof ExpressionColumn) {
/*  629 */             ExpressionColumn expressionColumn1 = (ExpressionColumn)expression1;
/*  630 */             String str3 = expressionColumn.getSQL(0, 2);
/*  631 */             String str4 = expressionColumn1.getSQL(0, 2);
/*  632 */             String str5 = expressionColumn1.getColumnName(this.session, b);
/*  633 */             if (database.equalsIdentifiers(str2, str5) && database.equalsIdentifiers(str3, str4)) {
/*  634 */               return b;
/*      */             }
/*      */           } 
/*      */         } 
/*      */       } 
/*  639 */     } else if (paramArrayList != null) {
/*  640 */       String str = paramExpression.getSQL(0, 2); byte b; int j;
/*  641 */       for (b = 0, j = paramArrayList.size(); b < j; b++) {
/*  642 */         if (database.equalsIdentifiers(paramArrayList.get(b), str)) {
/*  643 */           return b;
/*      */         }
/*      */       } 
/*      */     } 
/*  647 */     if (paramArrayList == null || (paramBoolean && 
/*  648 */       !(database.getMode()).allowUnrelatedOrderByExpressionsInDistinctQueries && 
/*  649 */       !checkOrderOther(this.session, paramExpression, paramArrayList))) {
/*  650 */       throw DbException.get(90068, paramExpression.getTraceSQL());
/*      */     }
/*  652 */     int i = this.expressions.size();
/*  653 */     this.expressions.add(paramExpression);
/*  654 */     paramArrayList.add(paramExpression.getSQL(0, 2));
/*  655 */     return i;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static boolean checkOrderOther(SessionLocal paramSessionLocal, Expression paramExpression, ArrayList<String> paramArrayList) {
/*  671 */     if (paramExpression == null || paramExpression.isConstant())
/*      */     {
/*  673 */       return true;
/*      */     }
/*  675 */     String str = paramExpression.getSQL(0, 2);
/*  676 */     for (String str1 : paramArrayList) {
/*  677 */       if (paramSessionLocal.getDatabase().equalsIdentifiers(str, str1)) {
/*  678 */         return true;
/*      */       }
/*      */     } 
/*  681 */     int i = paramExpression.getSubexpressionCount();
/*  682 */     if (!paramExpression.isEverything(ExpressionVisitor.DETERMINISTIC_VISITOR))
/*  683 */       return false; 
/*  684 */     if (i <= 0)
/*      */     {
/*      */       
/*  687 */       return false;
/*      */     }
/*  689 */     for (byte b = 0; b < i; b++) {
/*  690 */       if (!checkOrderOther(paramSessionLocal, paramExpression.getSubexpression(b), paramArrayList)) {
/*  691 */         return false;
/*      */       }
/*      */     } 
/*  694 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void prepareOrder(ArrayList<QueryOrderBy> paramArrayList, int paramInt) {
/*  705 */     int i = paramArrayList.size();
/*  706 */     int[] arrayOfInt1 = new int[i];
/*  707 */     int[] arrayOfInt2 = new int[i];
/*  708 */     for (byte b = 0; b < i; b++) {
/*  709 */       int j; QueryOrderBy queryOrderBy = paramArrayList.get(b);
/*      */       
/*  711 */       boolean bool = false;
/*  712 */       Value value = queryOrderBy.columnIndexExpr.getValue(null);
/*  713 */       if (value == ValueNull.INSTANCE) {
/*      */         
/*  715 */         j = 0;
/*      */       } else {
/*  717 */         j = value.getInt();
/*  718 */         if (j < 0) {
/*  719 */           bool = true;
/*  720 */           j = -j;
/*      */         } 
/*  722 */         j--;
/*  723 */         if (j < 0 || j >= paramInt) {
/*  724 */           throw DbException.get(90068, Integer.toString(j + 1));
/*      */         }
/*      */       } 
/*  727 */       arrayOfInt1[b] = j;
/*  728 */       int k = queryOrderBy.sortType;
/*  729 */       if (bool)
/*      */       {
/*  731 */         k ^= 0x1;
/*      */       }
/*  733 */       arrayOfInt2[b] = k;
/*      */     } 
/*  735 */     this.sort = new SortOrder(this.session, arrayOfInt1, arrayOfInt2, paramArrayList);
/*  736 */     this.orderList = null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void cleanupOrder() {
/*  747 */     int[] arrayOfInt1 = this.sort.getQueryColumnIndexes();
/*  748 */     int i = arrayOfInt1.length;
/*  749 */     byte b1 = 0; int j;
/*  750 */     for (j = 0; j < i; j++) {
/*  751 */       if (((Expression)this.expressions.get(arrayOfInt1[j])).isConstant()) {
/*  752 */         b1++;
/*      */       }
/*      */     } 
/*  755 */     if (b1 == 0) {
/*      */       return;
/*      */     }
/*  758 */     if (b1 == i) {
/*  759 */       this.sort = null;
/*      */       return;
/*      */     } 
/*  762 */     j = i - b1;
/*  763 */     int[] arrayOfInt2 = new int[j];
/*  764 */     int[] arrayOfInt3 = new int[j];
/*  765 */     int[] arrayOfInt4 = this.sort.getSortTypes();
/*  766 */     ArrayList arrayList = this.sort.getOrderList();
/*  767 */     for (byte b2 = 0, b3 = 0; b3 < j; b2++) {
/*  768 */       if (!((Expression)this.expressions.get(arrayOfInt1[b2])).isConstant()) {
/*  769 */         arrayOfInt2[b3] = arrayOfInt1[b2];
/*  770 */         arrayOfInt3[b3] = arrayOfInt4[b2];
/*  771 */         b3++;
/*      */       } else {
/*  773 */         arrayList.remove(b3);
/*      */       } 
/*      */     } 
/*  776 */     this.sort = new SortOrder(this.session, arrayOfInt2, arrayOfInt3, arrayList);
/*      */   }
/*      */ 
/*      */   
/*      */   public int getType() {
/*  781 */     return 66;
/*      */   }
/*      */   
/*      */   public void setOffset(Expression paramExpression) {
/*  785 */     this.offsetExpr = paramExpression;
/*      */   }
/*      */   
/*      */   public Expression getOffset() {
/*  789 */     return this.offsetExpr;
/*      */   }
/*      */   
/*      */   public void setFetch(Expression paramExpression) {
/*  793 */     this.fetchExpr = paramExpression;
/*      */   }
/*      */   
/*      */   public Expression getFetch() {
/*  797 */     return this.fetchExpr;
/*      */   }
/*      */   
/*      */   public void setFetchPercent(boolean paramBoolean) {
/*  801 */     this.fetchPercent = paramBoolean;
/*      */   }
/*      */   
/*      */   public boolean isFetchPercent() {
/*  805 */     return this.fetchPercent;
/*      */   }
/*      */   
/*      */   public void setWithTies(boolean paramBoolean) {
/*  809 */     this.withTies = paramBoolean;
/*      */   }
/*      */   
/*      */   public boolean isWithTies() {
/*  813 */     return this.withTies;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void addParameter(Parameter paramParameter) {
/*  822 */     if (this.parameters == null) {
/*  823 */       this.parameters = Utils.newSmallArrayList();
/*      */     }
/*  825 */     this.parameters.add(paramParameter);
/*      */   }
/*      */   
/*      */   public final long getMaxDataModificationId() {
/*  829 */     ExpressionVisitor expressionVisitor = ExpressionVisitor.getMaxModificationIdVisitor();
/*  830 */     isEverything(expressionVisitor);
/*  831 */     return Math.max(expressionVisitor.getMaxDataModificationId(), this.session.getSnapshotDataModificationId());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void appendEndOfQueryToSQL(StringBuilder paramStringBuilder, int paramInt, Expression[] paramArrayOfExpression) {
/*  842 */     if (this.sort != null) {
/*  843 */       this.sort.getSQL(paramStringBuilder.append("\nORDER BY "), paramArrayOfExpression, this.visibleColumnCount, paramInt);
/*  844 */     } else if (this.orderList != null) {
/*  845 */       paramStringBuilder.append("\nORDER BY "); byte b; int i;
/*  846 */       for (b = 0, i = this.orderList.size(); b < i; b++) {
/*  847 */         if (b > 0) {
/*  848 */           paramStringBuilder.append(", ");
/*      */         }
/*  850 */         ((QueryOrderBy)this.orderList.get(b)).getSQL(paramStringBuilder, paramInt);
/*      */       } 
/*      */     } 
/*  853 */     if (this.offsetExpr != null) {
/*  854 */       String str = this.offsetExpr.getSQL(paramInt, 2);
/*  855 */       paramStringBuilder.append("\nOFFSET ").append(str).append("1".equals(str) ? " ROW" : " ROWS");
/*      */     } 
/*  857 */     if (this.fetchExpr != null) {
/*  858 */       paramStringBuilder.append("\nFETCH ").append((this.offsetExpr != null) ? "NEXT" : "FIRST");
/*  859 */       String str = this.fetchExpr.getSQL(paramInt, 2);
/*  860 */       boolean bool = (this.fetchPercent || !"1".equals(str)) ? true : false;
/*  861 */       if (bool) {
/*  862 */         paramStringBuilder.append(' ').append(str);
/*  863 */         if (this.fetchPercent) {
/*  864 */           paramStringBuilder.append(" PERCENT");
/*      */         }
/*      */       } 
/*  867 */       paramStringBuilder.append(!bool ? " ROW" : " ROWS")
/*  868 */         .append(this.withTies ? " WITH TIES" : " ONLY");
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   OffsetFetch getOffsetFetch(long paramLong) {
/*      */     long l1;
/*  881 */     if (this.offsetExpr != null) {
/*  882 */       Value value = this.offsetExpr.getValue(this.session);
/*  883 */       if (value == ValueNull.INSTANCE || (l1 = value.getLong()) < 0L) {
/*  884 */         throw DbException.getInvalidValueException("result OFFSET", value);
/*      */       }
/*      */     } else {
/*  887 */       l1 = 0L;
/*      */     } 
/*  889 */     long l2 = (paramLong == 0L) ? -1L : paramLong;
/*  890 */     if (this.fetchExpr != null) {
/*  891 */       Value value = this.fetchExpr.getValue(this.session);
/*      */       long l;
/*  893 */       if (value == ValueNull.INSTANCE || (l = value.getLong()) < 0L) {
/*  894 */         throw DbException.getInvalidValueException("result FETCH", value);
/*      */       }
/*  896 */       l2 = (l2 < 0L) ? l : Math.min(l, l2);
/*      */     } 
/*  898 */     boolean bool = this.fetchPercent;
/*  899 */     if (bool) {
/*  900 */       if (l2 > 100L) {
/*  901 */         throw DbException.getInvalidValueException("result FETCH PERCENT", Long.valueOf(l2));
/*      */       }
/*      */       
/*  904 */       if (l2 == 0L) {
/*  905 */         bool = false;
/*      */       }
/*      */     } 
/*  908 */     return new OffsetFetch(l1, l2, bool);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   LocalResult finishResult(LocalResult paramLocalResult, long paramLong1, long paramLong2, boolean paramBoolean, ResultTarget paramResultTarget) {
/*  928 */     if (paramLong1 != 0L) {
/*  929 */       paramLocalResult.setOffset(paramLong1);
/*      */     }
/*  931 */     if (paramLong2 >= 0L) {
/*  932 */       paramLocalResult.setLimit(paramLong2);
/*  933 */       paramLocalResult.setFetchPercent(paramBoolean);
/*  934 */       if (this.withTies) {
/*  935 */         paramLocalResult.setWithTies(this.sort);
/*      */       }
/*      */     } 
/*  938 */     paramLocalResult.done();
/*  939 */     if (this.randomAccessResult && !this.distinct) {
/*  940 */       paramLocalResult = convertToDistinct((ResultInterface)paramLocalResult);
/*      */     }
/*  942 */     if (paramResultTarget != null) {
/*  943 */       while (paramLocalResult.next()) {
/*  944 */         paramResultTarget.addRow(paramLocalResult.currentRow());
/*      */       }
/*  946 */       paramLocalResult.close();
/*  947 */       return null;
/*      */     } 
/*  949 */     return paramLocalResult;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   LocalResult convertToDistinct(ResultInterface paramResultInterface) {
/*  959 */     LocalResult localResult = new LocalResult(this.session, this.expressionArray, this.visibleColumnCount, this.resultColumnCount);
/*  960 */     localResult.setDistinct();
/*  961 */     paramResultInterface.reset();
/*  962 */     while (paramResultInterface.next()) {
/*  963 */       localResult.addRow(paramResultInterface.currentRow());
/*      */     }
/*  965 */     paramResultInterface.close();
/*  966 */     localResult.done();
/*  967 */     return localResult;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Table toTable(String paramString, Column[] paramArrayOfColumn, ArrayList<Parameter> paramArrayList, boolean paramBoolean, Query paramQuery) {
/*  982 */     setParameterList(new ArrayList<>(paramArrayList));
/*  983 */     if (!this.checkInit) {
/*  984 */       init();
/*      */     }
/*  986 */     return (Table)TableView.createTempView(paramBoolean ? this.session.getDatabase().getSystemSession() : this.session, this.session
/*  987 */         .getUser(), paramString, paramArrayOfColumn, this, paramQuery);
/*      */   }
/*      */ 
/*      */   
/*      */   public void collectDependencies(HashSet<DbObject> paramHashSet) {
/*  992 */     ExpressionVisitor expressionVisitor = ExpressionVisitor.getDependenciesVisitor(paramHashSet);
/*  993 */     isEverything(expressionVisitor);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isConstantQuery() {
/* 1004 */     return (!hasOrder() && (this.offsetExpr == null || this.offsetExpr.isConstant()) && (this.fetchExpr == null || this.fetchExpr
/* 1005 */       .isConstant()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Expression getIfSingleRow() {
/* 1015 */     return null;
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\query\Query.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */