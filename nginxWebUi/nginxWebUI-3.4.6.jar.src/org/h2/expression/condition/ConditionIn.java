/*     */ package org.h2.expression.condition;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.ExpressionColumn;
/*     */ import org.h2.expression.ExpressionVisitor;
/*     */ import org.h2.expression.Parameter;
/*     */ import org.h2.expression.TypedValueExpression;
/*     */ import org.h2.expression.ValueExpression;
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
/*     */ public final class ConditionIn
/*     */   extends Condition
/*     */ {
/*     */   private Expression left;
/*     */   private final boolean not;
/*     */   private final boolean whenOperand;
/*     */   private final ArrayList<Expression> valueList;
/*     */   
/*     */   public ConditionIn(Expression paramExpression, boolean paramBoolean1, boolean paramBoolean2, ArrayList<Expression> paramArrayList) {
/*  43 */     this.left = paramExpression;
/*  44 */     this.not = paramBoolean1;
/*  45 */     this.whenOperand = paramBoolean2;
/*  46 */     this.valueList = paramArrayList;
/*     */   }
/*     */ 
/*     */   
/*     */   public Value getValue(SessionLocal paramSessionLocal) {
/*  51 */     return getValue(paramSessionLocal, this.left.getValue(paramSessionLocal));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getWhenValue(SessionLocal paramSessionLocal, Value paramValue) {
/*  56 */     if (!this.whenOperand) {
/*  57 */       return super.getWhenValue(paramSessionLocal, paramValue);
/*     */     }
/*  59 */     return getValue(paramSessionLocal, paramValue).isTrue();
/*     */   }
/*     */   
/*     */   private Value getValue(SessionLocal paramSessionLocal, Value paramValue) {
/*  63 */     if (paramValue.containsNull()) {
/*  64 */       return (Value)ValueNull.INSTANCE;
/*     */     }
/*  66 */     boolean bool = false;
/*  67 */     for (Expression expression : this.valueList) {
/*  68 */       Value value1 = expression.getValue(paramSessionLocal);
/*  69 */       Value value2 = Comparison.compare(paramSessionLocal, paramValue, value1, 0);
/*  70 */       if (value2 == ValueNull.INSTANCE) {
/*  71 */         bool = true; continue;
/*  72 */       }  if (value2 == ValueBoolean.TRUE) {
/*  73 */         return (Value)ValueBoolean.get(!this.not);
/*     */       }
/*     */     } 
/*  76 */     if (bool) {
/*  77 */       return (Value)ValueNull.INSTANCE;
/*     */     }
/*  79 */     return (Value)ValueBoolean.get(this.not);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isWhenConditionOperand() {
/*  84 */     return this.whenOperand;
/*     */   }
/*     */ 
/*     */   
/*     */   public void mapColumns(ColumnResolver paramColumnResolver, int paramInt1, int paramInt2) {
/*  89 */     this.left.mapColumns(paramColumnResolver, paramInt1, paramInt2);
/*  90 */     for (Expression expression : this.valueList) {
/*  91 */       expression.mapColumns(paramColumnResolver, paramInt1, paramInt2);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression optimize(SessionLocal paramSessionLocal) {
/*  97 */     this.left = this.left.optimize(paramSessionLocal);
/*  98 */     boolean bool1 = (!this.whenOperand && this.left.isConstant()) ? true : false;
/*  99 */     if (bool1 && this.left.isNullConstant()) {
/* 100 */       return (Expression)TypedValueExpression.UNKNOWN;
/*     */     }
/* 102 */     boolean bool2 = true;
/* 103 */     boolean bool3 = true;
/* 104 */     TypeInfo typeInfo = this.left.getType(); byte b; int i;
/* 105 */     for (b = 0, i = this.valueList.size(); b < i; b++) {
/* 106 */       Expression expression = this.valueList.get(b);
/* 107 */       expression = expression.optimize(paramSessionLocal);
/* 108 */       TypeInfo.checkComparable(typeInfo, expression.getType());
/* 109 */       if (expression.isConstant() && !expression.getValue(paramSessionLocal).containsNull()) {
/* 110 */         bool3 = false;
/*     */       }
/* 112 */       if (bool2 && !expression.isConstant()) {
/* 113 */         bool2 = false;
/*     */       }
/* 115 */       if (this.left instanceof ExpressionColumn && expression instanceof Parameter) {
/* 116 */         ((Parameter)expression).setColumn(((ExpressionColumn)this.left).getColumn());
/*     */       }
/* 118 */       this.valueList.set(b, expression);
/*     */     } 
/* 120 */     return optimize2(paramSessionLocal, bool1, bool2, bool3, this.valueList);
/*     */   }
/*     */ 
/*     */   
/*     */   private Expression optimize2(SessionLocal paramSessionLocal, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, ArrayList<Expression> paramArrayList) {
/* 125 */     if (paramBoolean1 && paramBoolean2) {
/* 126 */       return (Expression)ValueExpression.getBoolean(getValue(paramSessionLocal));
/*     */     }
/* 128 */     if (paramArrayList.size() == 1) {
/* 129 */       return (new Comparison(this.not ? 1 : 0, this.left, paramArrayList.get(0), this.whenOperand))
/* 130 */         .optimize(paramSessionLocal);
/*     */     }
/* 132 */     if (paramBoolean2 && !paramBoolean3) {
/* 133 */       int i = this.left.getType().getValueType();
/* 134 */       if (i == -1) {
/* 135 */         return this;
/*     */       }
/* 137 */       if (i == 36 && !(this.left instanceof ExpressionColumn)) {
/* 138 */         return this;
/*     */       }
/* 140 */       return (new ConditionInConstantSet(paramSessionLocal, this.left, this.not, this.whenOperand, paramArrayList)).optimize(paramSessionLocal);
/*     */     } 
/* 142 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression getNotIfPossible(SessionLocal paramSessionLocal) {
/* 147 */     if (this.whenOperand) {
/* 148 */       return null;
/*     */     }
/* 150 */     return new ConditionIn(this.left, !this.not, false, this.valueList);
/*     */   }
/*     */ 
/*     */   
/*     */   public void createIndexConditions(SessionLocal paramSessionLocal, TableFilter paramTableFilter) {
/* 155 */     if (this.not || this.whenOperand || !(this.left instanceof ExpressionColumn)) {
/*     */       return;
/*     */     }
/* 158 */     ExpressionColumn expressionColumn = (ExpressionColumn)this.left;
/* 159 */     if (paramTableFilter != expressionColumn.getTableFilter()) {
/*     */       return;
/*     */     }
/* 162 */     if ((paramSessionLocal.getDatabase().getSettings()).optimizeInList) {
/* 163 */       ExpressionVisitor expressionVisitor = ExpressionVisitor.getNotFromResolverVisitor((ColumnResolver)paramTableFilter);
/* 164 */       TypeInfo typeInfo = expressionColumn.getType();
/* 165 */       for (Expression expression : this.valueList) {
/* 166 */         if (!expression.isEverything(expressionVisitor) || 
/* 167 */           !TypeInfo.haveSameOrdering(typeInfo, TypeInfo.getHigherType(typeInfo, expression.getType()))) {
/*     */           return;
/*     */         }
/*     */       } 
/* 171 */       paramTableFilter.addIndexCondition(IndexCondition.getInList(expressionColumn, this.valueList));
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setEvaluatable(TableFilter paramTableFilter, boolean paramBoolean) {
/* 177 */     this.left.setEvaluatable(paramTableFilter, paramBoolean);
/* 178 */     for (Expression expression : this.valueList) {
/* 179 */       expression.setEvaluatable(paramTableFilter, paramBoolean);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean needParentheses() {
/* 185 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 190 */     return getWhenSQL(this.left.getSQL(paramStringBuilder, paramInt, 0), paramInt);
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getWhenSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 195 */     if (this.not) {
/* 196 */       paramStringBuilder.append(" NOT");
/*     */     }
/* 198 */     return writeExpressions(paramStringBuilder.append(" IN("), this.valueList, paramInt).append(')');
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateAggregate(SessionLocal paramSessionLocal, int paramInt) {
/* 203 */     this.left.updateAggregate(paramSessionLocal, paramInt);
/* 204 */     for (Expression expression : this.valueList) {
/* 205 */       expression.updateAggregate(paramSessionLocal, paramInt);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/* 211 */     if (!this.left.isEverything(paramExpressionVisitor)) {
/* 212 */       return false;
/*     */     }
/* 214 */     return areAllValues(paramExpressionVisitor);
/*     */   }
/*     */   
/*     */   private boolean areAllValues(ExpressionVisitor paramExpressionVisitor) {
/* 218 */     for (Expression expression : this.valueList) {
/* 219 */       if (!expression.isEverything(paramExpressionVisitor)) {
/* 220 */         return false;
/*     */       }
/*     */     } 
/* 223 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getCost() {
/* 228 */     int i = this.left.getCost();
/* 229 */     for (Expression expression : this.valueList) {
/* 230 */       i += expression.getCost();
/*     */     }
/* 232 */     return i;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Expression getAdditional(Comparison paramComparison) {
/* 243 */     if (!this.not && !this.whenOperand && this.left.isEverything(ExpressionVisitor.DETERMINISTIC_VISITOR)) {
/* 244 */       Expression expression = paramComparison.getIfEquals(this.left);
/* 245 */       if (expression != null) {
/* 246 */         ArrayList<Expression> arrayList = new ArrayList(this.valueList.size() + 1);
/* 247 */         arrayList.addAll(this.valueList);
/* 248 */         arrayList.add(expression);
/* 249 */         return new ConditionIn(this.left, false, false, arrayList);
/*     */       } 
/*     */     } 
/* 252 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSubexpressionCount() {
/* 257 */     return 1 + this.valueList.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression getSubexpression(int paramInt) {
/* 262 */     if (paramInt == 0)
/* 263 */       return this.left; 
/* 264 */     if (paramInt > 0 && paramInt <= this.valueList.size()) {
/* 265 */       return this.valueList.get(paramInt - 1);
/*     */     }
/* 267 */     throw new IndexOutOfBoundsException();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\condition\ConditionIn.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */