/*     */ package org.h2.expression.condition;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Comparator;
/*     */ import java.util.TreeSet;
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.ExpressionColumn;
/*     */ import org.h2.expression.ExpressionVisitor;
/*     */ import org.h2.index.IndexCondition;
/*     */ import org.h2.table.ColumnResolver;
/*     */ import org.h2.table.TableFilter;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueBoolean;
/*     */ import org.h2.value.ValueNull;
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
/*     */ public final class ConditionInConstantSet
/*     */   extends Condition
/*     */ {
/*     */   private Expression left;
/*     */   private final boolean not;
/*     */   private final boolean whenOperand;
/*     */   private final ArrayList<Expression> valueList;
/*     */   private final TreeSet<Value> valueSet;
/*     */   private boolean hasNull;
/*     */   private final TypeInfo type;
/*     */   
/*     */   ConditionInConstantSet(SessionLocal paramSessionLocal, Expression paramExpression, boolean paramBoolean1, boolean paramBoolean2, ArrayList<Expression> paramArrayList) {
/*  55 */     this.left = paramExpression;
/*  56 */     this.not = paramBoolean1;
/*  57 */     this.whenOperand = paramBoolean2;
/*  58 */     this.valueList = paramArrayList;
/*  59 */     this.valueSet = new TreeSet<>((Comparator<? super Value>)paramSessionLocal.getDatabase().getCompareMode());
/*  60 */     TypeInfo typeInfo = paramExpression.getType();
/*  61 */     for (Expression expression : paramArrayList) {
/*  62 */       typeInfo = TypeInfo.getHigherType(typeInfo, expression.getType());
/*     */     }
/*  64 */     this.type = typeInfo;
/*  65 */     for (Expression expression : paramArrayList) {
/*  66 */       add(expression.getValue(paramSessionLocal), paramSessionLocal);
/*     */     }
/*     */   }
/*     */   
/*     */   private void add(Value paramValue, SessionLocal paramSessionLocal) {
/*  71 */     if ((paramValue = paramValue.convertTo(this.type, (CastDataProvider)paramSessionLocal)).containsNull()) {
/*  72 */       this.hasNull = true;
/*     */     } else {
/*  74 */       this.valueSet.add(paramValue);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Value getValue(SessionLocal paramSessionLocal) {
/*  80 */     return getValue(this.left.getValue(paramSessionLocal), paramSessionLocal);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getWhenValue(SessionLocal paramSessionLocal, Value paramValue) {
/*  85 */     if (!this.whenOperand) {
/*  86 */       return super.getWhenValue(paramSessionLocal, paramValue);
/*     */     }
/*  88 */     return getValue(paramValue, paramSessionLocal).isTrue();
/*     */   }
/*     */   
/*     */   private Value getValue(Value paramValue, SessionLocal paramSessionLocal) {
/*  92 */     if ((paramValue = paramValue.convertTo(this.type, (CastDataProvider)paramSessionLocal)).containsNull()) {
/*  93 */       return (Value)ValueNull.INSTANCE;
/*     */     }
/*  95 */     boolean bool = this.valueSet.contains(paramValue);
/*  96 */     if (!bool && this.hasNull) {
/*  97 */       return (Value)ValueNull.INSTANCE;
/*     */     }
/*  99 */     return (Value)ValueBoolean.get(this.not ^ bool);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isWhenConditionOperand() {
/* 104 */     return this.whenOperand;
/*     */   }
/*     */ 
/*     */   
/*     */   public void mapColumns(ColumnResolver paramColumnResolver, int paramInt1, int paramInt2) {
/* 109 */     this.left.mapColumns(paramColumnResolver, paramInt1, paramInt2);
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression optimize(SessionLocal paramSessionLocal) {
/* 114 */     this.left = this.left.optimize(paramSessionLocal);
/* 115 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression getNotIfPossible(SessionLocal paramSessionLocal) {
/* 120 */     if (this.whenOperand) {
/* 121 */       return null;
/*     */     }
/* 123 */     return new ConditionInConstantSet(paramSessionLocal, this.left, !this.not, false, this.valueList);
/*     */   }
/*     */ 
/*     */   
/*     */   public void createIndexConditions(SessionLocal paramSessionLocal, TableFilter paramTableFilter) {
/* 128 */     if (this.not || this.whenOperand || !(this.left instanceof ExpressionColumn)) {
/*     */       return;
/*     */     }
/* 131 */     ExpressionColumn expressionColumn = (ExpressionColumn)this.left;
/* 132 */     if (paramTableFilter != expressionColumn.getTableFilter()) {
/*     */       return;
/*     */     }
/* 135 */     if ((paramSessionLocal.getDatabase().getSettings()).optimizeInList) {
/* 136 */       TypeInfo typeInfo = expressionColumn.getType();
/* 137 */       if (TypeInfo.haveSameOrdering(typeInfo, TypeInfo.getHigherType(typeInfo, this.type))) {
/* 138 */         paramTableFilter.addIndexCondition(IndexCondition.getInList(expressionColumn, this.valueList));
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setEvaluatable(TableFilter paramTableFilter, boolean paramBoolean) {
/* 145 */     this.left.setEvaluatable(paramTableFilter, paramBoolean);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean needParentheses() {
/* 150 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 155 */     return getWhenSQL(this.left.getSQL(paramStringBuilder, paramInt, 0), paramInt);
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getWhenSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 160 */     if (this.not) {
/* 161 */       paramStringBuilder.append(" NOT");
/*     */     }
/* 163 */     return writeExpressions(paramStringBuilder.append(" IN("), this.valueList, paramInt).append(')');
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateAggregate(SessionLocal paramSessionLocal, int paramInt) {
/* 168 */     this.left.updateAggregate(paramSessionLocal, paramInt);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/* 173 */     return this.left.isEverything(paramExpressionVisitor);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getCost() {
/* 178 */     return this.left.getCost();
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
/*     */   Expression getAdditional(SessionLocal paramSessionLocal, Comparison paramComparison) {
/* 190 */     if (!this.not && !this.whenOperand && this.left.isEverything(ExpressionVisitor.DETERMINISTIC_VISITOR)) {
/* 191 */       Expression expression = paramComparison.getIfEquals(this.left);
/* 192 */       if (expression != null && 
/* 193 */         expression.isConstant()) {
/* 194 */         ArrayList<Expression> arrayList = new ArrayList(this.valueList.size() + 1);
/* 195 */         arrayList.addAll(this.valueList);
/* 196 */         arrayList.add(expression);
/* 197 */         return new ConditionInConstantSet(paramSessionLocal, this.left, false, false, arrayList);
/*     */       } 
/*     */     } 
/*     */     
/* 201 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSubexpressionCount() {
/* 206 */     return 1 + this.valueList.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression getSubexpression(int paramInt) {
/* 211 */     if (paramInt == 0)
/* 212 */       return this.left; 
/* 213 */     if (paramInt > 0 && paramInt <= this.valueList.size()) {
/* 214 */       return this.valueList.get(paramInt - 1);
/*     */     }
/* 216 */     throw new IndexOutOfBoundsException();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\condition\ConditionInConstantSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */