/*     */ package org.h2.expression.condition;
/*     */ 
/*     */ import org.h2.command.query.Query;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.ExpressionColumn;
/*     */ import org.h2.expression.ExpressionVisitor;
/*     */ import org.h2.index.IndexCondition;
/*     */ import org.h2.result.LocalResult;
/*     */ import org.h2.result.ResultInterface;
/*     */ import org.h2.table.ColumnResolver;
/*     */ import org.h2.table.TableFilter;
/*     */ import org.h2.value.DataType;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueBoolean;
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
/*     */ public final class ConditionInQuery
/*     */   extends PredicateWithSubquery
/*     */ {
/*     */   private Expression left;
/*     */   private final boolean not;
/*     */   private final boolean whenOperand;
/*     */   private final boolean all;
/*     */   private final int compareType;
/*     */   
/*     */   public ConditionInQuery(Expression paramExpression, boolean paramBoolean1, boolean paramBoolean2, Query paramQuery, boolean paramBoolean3, int paramInt) {
/*  38 */     super(paramQuery);
/*  39 */     this.left = paramExpression;
/*  40 */     this.not = paramBoolean1;
/*  41 */     this.whenOperand = paramBoolean2;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  46 */     paramQuery.setRandomAccessResult(true);
/*  47 */     paramQuery.setNeverLazy(true);
/*  48 */     paramQuery.setDistinctIfPossible();
/*  49 */     this.all = paramBoolean3;
/*  50 */     this.compareType = paramInt;
/*     */   }
/*     */ 
/*     */   
/*     */   public Value getValue(SessionLocal paramSessionLocal) {
/*  55 */     return getValue(paramSessionLocal, this.left.getValue(paramSessionLocal));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getWhenValue(SessionLocal paramSessionLocal, Value paramValue) {
/*  60 */     if (!this.whenOperand) {
/*  61 */       return super.getWhenValue(paramSessionLocal, paramValue);
/*     */     }
/*  63 */     return getValue(paramSessionLocal, paramValue).isTrue();
/*     */   }
/*     */   
/*     */   private Value getValue(SessionLocal paramSessionLocal, Value paramValue) {
/*  67 */     this.query.setSession(paramSessionLocal);
/*  68 */     LocalResult localResult = (LocalResult)this.query.query(0L);
/*  69 */     if (!localResult.hasNext()) {
/*  70 */       return (Value)ValueBoolean.get(this.not ^ this.all);
/*     */     }
/*  72 */     if ((this.compareType & 0xFFFFFFFE) == 6) {
/*  73 */       return getNullSafeValueSlow(paramSessionLocal, (ResultInterface)localResult, paramValue);
/*     */     }
/*  75 */     if (paramValue.containsNull()) {
/*  76 */       return (Value)ValueNull.INSTANCE;
/*     */     }
/*  78 */     if (this.all || this.compareType != 0 || !(paramSessionLocal.getDatabase().getSettings()).optimizeInSelect) {
/*  79 */       return getValueSlow(paramSessionLocal, (ResultInterface)localResult, paramValue);
/*     */     }
/*  81 */     int i = this.query.getColumnCount();
/*  82 */     if (i != 1) {
/*  83 */       Value[] arrayOfValue = paramValue.convertToAnyRow().getList();
/*  84 */       if (i == arrayOfValue.length && localResult.containsDistinct(arrayOfValue)) {
/*  85 */         return (Value)ValueBoolean.get(!this.not);
/*     */       }
/*     */     } else {
/*  88 */       TypeInfo typeInfo = localResult.getColumnType(0);
/*  89 */       if (typeInfo.getValueType() == 0) {
/*  90 */         return (Value)ValueNull.INSTANCE;
/*     */       }
/*  92 */       if (paramValue.getValueType() == 41) {
/*  93 */         paramValue = ((ValueRow)paramValue).getList()[0];
/*     */       }
/*  95 */       if (localResult.containsDistinct(new Value[] { paramValue })) {
/*  96 */         return (Value)ValueBoolean.get(!this.not);
/*     */       }
/*     */     } 
/*  99 */     if (localResult.containsNull()) {
/* 100 */       return (Value)ValueNull.INSTANCE;
/*     */     }
/* 102 */     return (Value)ValueBoolean.get(this.not);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private Value getValueSlow(SessionLocal paramSessionLocal, ResultInterface paramResultInterface, Value paramValue) {
/* 108 */     boolean bool1 = (paramValue.getValueType() != 41 && this.query.getColumnCount() == 1) ? true : false;
/* 109 */     boolean bool2 = false;
/* 110 */     ValueBoolean valueBoolean = ValueBoolean.get(!this.all);
/* 111 */     while (paramResultInterface.next()) {
/* 112 */       Value[] arrayOfValue = paramResultInterface.currentRow();
/* 113 */       Value value = Comparison.compare(paramSessionLocal, paramValue, bool1 ? arrayOfValue[0] : (Value)ValueRow.get(arrayOfValue), this.compareType);
/*     */       
/* 115 */       if (value == ValueNull.INSTANCE) {
/* 116 */         bool2 = true; continue;
/* 117 */       }  if (value == valueBoolean) {
/* 118 */         return (Value)ValueBoolean.get((this.not == this.all));
/*     */       }
/*     */     } 
/* 121 */     if (bool2) {
/* 122 */       return (Value)ValueNull.INSTANCE;
/*     */     }
/* 124 */     return (Value)ValueBoolean.get(this.not ^ this.all);
/*     */   }
/*     */   
/*     */   private Value getNullSafeValueSlow(SessionLocal paramSessionLocal, ResultInterface paramResultInterface, Value paramValue) {
/* 128 */     boolean bool = (paramValue.getValueType() != 41 && this.query.getColumnCount() == 1) ? true : false;
/* 129 */     boolean bool1 = (this.all == ((this.compareType == 7)));
/* 130 */     while (paramResultInterface.next()) {
/* 131 */       Value[] arrayOfValue = paramResultInterface.currentRow();
/* 132 */       if (paramSessionLocal.areEqual(paramValue, bool ? arrayOfValue[0] : (Value)ValueRow.get(arrayOfValue)) == bool1) {
/* 133 */         return (Value)ValueBoolean.get((this.not == this.all));
/*     */       }
/*     */     } 
/* 136 */     return (Value)ValueBoolean.get(this.not ^ this.all);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isWhenConditionOperand() {
/* 141 */     return this.whenOperand;
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression getNotIfPossible(SessionLocal paramSessionLocal) {
/* 146 */     if (this.whenOperand) {
/* 147 */       return null;
/*     */     }
/* 149 */     return new ConditionInQuery(this.left, !this.not, false, this.query, this.all, this.compareType);
/*     */   }
/*     */ 
/*     */   
/*     */   public void mapColumns(ColumnResolver paramColumnResolver, int paramInt1, int paramInt2) {
/* 154 */     this.left.mapColumns(paramColumnResolver, paramInt1, paramInt2);
/* 155 */     super.mapColumns(paramColumnResolver, paramInt1, paramInt2);
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression optimize(SessionLocal paramSessionLocal) {
/* 160 */     super.optimize(paramSessionLocal);
/* 161 */     this.left = this.left.optimize(paramSessionLocal);
/* 162 */     TypeInfo.checkComparable(this.left.getType(), this.query.getRowDataType());
/* 163 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setEvaluatable(TableFilter paramTableFilter, boolean paramBoolean) {
/* 168 */     this.left.setEvaluatable(paramTableFilter, paramBoolean);
/* 169 */     super.setEvaluatable(paramTableFilter, paramBoolean);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean needParentheses() {
/* 174 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 179 */     boolean bool = (this.not && (this.all || this.compareType != 0)) ? true : false;
/* 180 */     if (bool) {
/* 181 */       paramStringBuilder.append("NOT (");
/*     */     }
/* 183 */     this.left.getSQL(paramStringBuilder, paramInt, 0);
/* 184 */     getWhenSQL(paramStringBuilder, paramInt);
/* 185 */     if (bool) {
/* 186 */       paramStringBuilder.append(')');
/*     */     }
/* 188 */     return paramStringBuilder;
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getWhenSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 193 */     if (this.all) {
/* 194 */       paramStringBuilder.append(Comparison.COMPARE_TYPES[this.compareType]).append(" ALL");
/* 195 */     } else if (this.compareType == 0) {
/* 196 */       if (this.not) {
/* 197 */         paramStringBuilder.append(" NOT");
/*     */       }
/* 199 */       paramStringBuilder.append(" IN");
/*     */     } else {
/* 201 */       paramStringBuilder.append(' ').append(Comparison.COMPARE_TYPES[this.compareType]).append(" ANY");
/*     */     } 
/* 203 */     return super.getUnenclosedSQL(paramStringBuilder, paramInt);
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateAggregate(SessionLocal paramSessionLocal, int paramInt) {
/* 208 */     this.left.updateAggregate(paramSessionLocal, paramInt);
/* 209 */     super.updateAggregate(paramSessionLocal, paramInt);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/* 214 */     return (this.left.isEverything(paramExpressionVisitor) && super.isEverything(paramExpressionVisitor));
/*     */   }
/*     */ 
/*     */   
/*     */   public int getCost() {
/* 219 */     return this.left.getCost() + super.getCost();
/*     */   }
/*     */ 
/*     */   
/*     */   public void createIndexConditions(SessionLocal paramSessionLocal, TableFilter paramTableFilter) {
/* 224 */     if (!(paramSessionLocal.getDatabase().getSettings()).optimizeInList) {
/*     */       return;
/*     */     }
/* 227 */     if (this.not || this.compareType != 0) {
/*     */       return;
/*     */     }
/* 230 */     if (this.query.getColumnCount() != 1) {
/*     */       return;
/*     */     }
/* 233 */     if (!(this.left instanceof ExpressionColumn)) {
/*     */       return;
/*     */     }
/* 236 */     TypeInfo typeInfo1 = this.left.getType();
/* 237 */     TypeInfo typeInfo2 = ((Expression)this.query.getExpressions().get(0)).getType();
/* 238 */     if (!TypeInfo.haveSameOrdering(typeInfo1, TypeInfo.getHigherType(typeInfo1, typeInfo2))) {
/*     */       return;
/*     */     }
/* 241 */     int i = typeInfo1.getValueType();
/* 242 */     if (!DataType.hasTotalOrdering(i) && i != typeInfo2.getValueType()) {
/*     */       return;
/*     */     }
/* 245 */     ExpressionColumn expressionColumn = (ExpressionColumn)this.left;
/* 246 */     if (paramTableFilter != expressionColumn.getTableFilter()) {
/*     */       return;
/*     */     }
/* 249 */     ExpressionVisitor expressionVisitor = ExpressionVisitor.getNotFromResolverVisitor((ColumnResolver)paramTableFilter);
/* 250 */     if (!this.query.isEverything(expressionVisitor)) {
/*     */       return;
/*     */     }
/* 253 */     paramTableFilter.addIndexCondition(IndexCondition.getInQuery(expressionColumn, this.query));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\condition\ConditionInQuery.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */