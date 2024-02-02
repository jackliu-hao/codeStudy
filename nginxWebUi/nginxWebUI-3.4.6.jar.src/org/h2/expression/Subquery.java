/*     */ package org.h2.expression;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashSet;
/*     */ import org.h2.command.query.Query;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.result.ResultInterface;
/*     */ import org.h2.table.ColumnResolver;
/*     */ import org.h2.table.TableFilter;
/*     */ import org.h2.value.ExtTypeInfoRow;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Typed;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueNull;
/*     */ import org.h2.value.ValueRow;
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
/*     */ public final class Subquery
/*     */   extends Expression
/*     */ {
/*     */   private final Query query;
/*     */   private Expression expression;
/*     */   private Value nullValue;
/*  37 */   private HashSet<ColumnResolver> outerResolvers = new HashSet<>();
/*     */   
/*     */   public Subquery(Query paramQuery) {
/*  40 */     this.query = paramQuery;
/*     */   }
/*     */ 
/*     */   
/*     */   public Value getValue(SessionLocal paramSessionLocal) {
/*  45 */     this.query.setSession(paramSessionLocal);
/*  46 */     try (ResultInterface null = this.query.query(2L)) {
/*     */       
/*  48 */       if (!resultInterface.next()) {
/*  49 */         return this.nullValue;
/*     */       }
/*  51 */       Value value = readRow(resultInterface);
/*  52 */       if (resultInterface.hasNext()) {
/*  53 */         throw DbException.get(90053);
/*     */       }
/*     */       
/*  56 */       return value;
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
/*     */   public ArrayList<Value> getAllRows(SessionLocal paramSessionLocal) {
/*  68 */     ArrayList<Value> arrayList = new ArrayList();
/*  69 */     this.query.setSession(paramSessionLocal);
/*  70 */     try (ResultInterface null = this.query.query(2147483647L)) {
/*  71 */       while (resultInterface.next()) {
/*  72 */         arrayList.add(readRow(resultInterface));
/*     */       }
/*     */     } 
/*  75 */     return arrayList;
/*     */   }
/*     */   
/*     */   private Value readRow(ResultInterface paramResultInterface) {
/*  79 */     Value[] arrayOfValue = paramResultInterface.currentRow();
/*  80 */     int i = paramResultInterface.getVisibleColumnCount();
/*  81 */     return (i == 1) ? arrayOfValue[0] : 
/*  82 */       (Value)ValueRow.get(getType(), (i == arrayOfValue.length) ? arrayOfValue : Arrays.<Value>copyOf(arrayOfValue, i));
/*     */   }
/*     */ 
/*     */   
/*     */   public TypeInfo getType() {
/*  87 */     return this.expression.getType();
/*     */   }
/*     */ 
/*     */   
/*     */   public void mapColumns(ColumnResolver paramColumnResolver, int paramInt1, int paramInt2) {
/*  92 */     this.outerResolvers.add(paramColumnResolver);
/*  93 */     this.query.mapColumns(paramColumnResolver, paramInt1 + 1);
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression optimize(SessionLocal paramSessionLocal) {
/*  98 */     this.query.prepare();
/*  99 */     if (this.query.isConstantQuery()) {
/* 100 */       setType();
/* 101 */       return ValueExpression.get(getValue(paramSessionLocal));
/*     */     } 
/* 103 */     if (this.outerResolvers != null && (paramSessionLocal.getDatabase().getSettings()).optimizeSimpleSingleRowSubqueries) {
/* 104 */       Expression expression = this.query.getIfSingleRow();
/* 105 */       if (expression != null && expression.isEverything(ExpressionVisitor.getDecrementQueryLevelVisitor(this.outerResolvers, 0))) {
/* 106 */         expression.isEverything(ExpressionVisitor.getDecrementQueryLevelVisitor(this.outerResolvers, 1));
/* 107 */         return expression.optimize(paramSessionLocal);
/*     */       } 
/*     */     } 
/* 110 */     this.outerResolvers = null;
/* 111 */     setType();
/* 112 */     return this;
/*     */   }
/*     */   
/*     */   private void setType() {
/* 116 */     ArrayList<Expression> arrayList = this.query.getExpressions();
/* 117 */     int i = this.query.getColumnCount();
/* 118 */     if (i == 1) {
/* 119 */       this.expression = arrayList.get(0);
/* 120 */       this.nullValue = (Value)ValueNull.INSTANCE;
/*     */     } else {
/* 122 */       Expression[] arrayOfExpression = new Expression[i];
/* 123 */       Value[] arrayOfValue = new Value[i];
/* 124 */       for (byte b = 0; b < i; b++) {
/* 125 */         arrayOfExpression[b] = arrayList.get(b);
/* 126 */         arrayOfValue[b] = (Value)ValueNull.INSTANCE;
/*     */       } 
/* 128 */       ExpressionList expressionList = new ExpressionList(arrayOfExpression, false);
/* 129 */       expressionList.initializeType();
/* 130 */       this.expression = expressionList;
/* 131 */       this.nullValue = (Value)ValueRow.get(new ExtTypeInfoRow((Typed[])arrayOfExpression), arrayOfValue);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setEvaluatable(TableFilter paramTableFilter, boolean paramBoolean) {
/* 137 */     this.query.setEvaluatable(paramTableFilter, paramBoolean);
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 142 */     return paramStringBuilder.append('(').append(this.query.getPlanSQL(paramInt)).append(')');
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateAggregate(SessionLocal paramSessionLocal, int paramInt) {
/* 147 */     this.query.updateAggregate(paramSessionLocal, paramInt);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/* 152 */     return this.query.isEverything(paramExpressionVisitor);
/*     */   }
/*     */   
/*     */   public Query getQuery() {
/* 156 */     return this.query;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getCost() {
/* 161 */     return this.query.getCostAsExpression();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isConstant() {
/* 166 */     return this.query.isConstantQuery();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\Subquery.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */