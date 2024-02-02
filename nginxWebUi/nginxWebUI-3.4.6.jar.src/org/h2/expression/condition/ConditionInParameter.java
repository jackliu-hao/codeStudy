/*     */ package org.h2.expression.condition;
/*     */ 
/*     */ import java.util.AbstractList;
/*     */ import org.h2.engine.CastDataProvider;
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
/*     */ import org.h2.value.ValueArray;
/*     */ import org.h2.value.ValueBoolean;
/*     */ import org.h2.value.ValueNull;
/*     */ 
/*     */ public final class ConditionInParameter
/*     */   extends Condition {
/*     */   private Expression left;
/*     */   private boolean not;
/*     */   private boolean whenOperand;
/*     */   private final Parameter parameter;
/*     */   
/*     */   private static final class ParameterList
/*     */     extends AbstractList<Expression> {
/*     */     private final Parameter parameter;
/*     */     
/*     */     ParameterList(Parameter param1Parameter) {
/*  33 */       this.parameter = param1Parameter;
/*     */     }
/*     */ 
/*     */     
/*     */     public Expression get(int param1Int) {
/*  38 */       Value value = this.parameter.getParamValue();
/*  39 */       if (value instanceof ValueArray) {
/*  40 */         return (Expression)ValueExpression.get(((ValueArray)value).getList()[param1Int]);
/*     */       }
/*  42 */       if (param1Int != 0) {
/*  43 */         throw new IndexOutOfBoundsException();
/*     */       }
/*  45 */       return (Expression)ValueExpression.get(value);
/*     */     }
/*     */ 
/*     */     
/*     */     public int size() {
/*  50 */       if (!this.parameter.isValueSet()) {
/*  51 */         return 0;
/*     */       }
/*  53 */       Value value = this.parameter.getParamValue();
/*  54 */       if (value instanceof ValueArray) {
/*  55 */         return (((ValueArray)value).getList()).length;
/*     */       }
/*  57 */       return 1;
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
/*     */   static Value getValue(SessionLocal paramSessionLocal, Value paramValue1, boolean paramBoolean, Value paramValue2) {
/*  79 */     boolean bool = false;
/*  80 */     if (paramValue2.containsNull()) {
/*  81 */       bool = true;
/*     */     } else {
/*  83 */       for (Value value1 : paramValue2.convertToAnyArray((CastDataProvider)paramSessionLocal).getList()) {
/*  84 */         Value value2 = Comparison.compare(paramSessionLocal, paramValue1, value1, 0);
/*  85 */         if (value2 == ValueNull.INSTANCE) {
/*  86 */           bool = true;
/*  87 */         } else if (value2 == ValueBoolean.TRUE) {
/*  88 */           return (Value)ValueBoolean.get(!paramBoolean);
/*     */         } 
/*     */       } 
/*     */     } 
/*  92 */     if (bool) {
/*  93 */       return (Value)ValueNull.INSTANCE;
/*     */     }
/*  95 */     return (Value)ValueBoolean.get(paramBoolean);
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
/*     */   public ConditionInParameter(Expression paramExpression, boolean paramBoolean1, boolean paramBoolean2, Parameter paramParameter) {
/* 109 */     this.left = paramExpression;
/* 110 */     this.not = paramBoolean1;
/* 111 */     this.whenOperand = paramBoolean2;
/* 112 */     this.parameter = paramParameter;
/*     */   }
/*     */ 
/*     */   
/*     */   public Value getValue(SessionLocal paramSessionLocal) {
/* 117 */     Value value = this.left.getValue(paramSessionLocal);
/* 118 */     if (value == ValueNull.INSTANCE) {
/* 119 */       return (Value)ValueNull.INSTANCE;
/*     */     }
/* 121 */     return getValue(paramSessionLocal, value, this.not, this.parameter.getValue(paramSessionLocal));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getWhenValue(SessionLocal paramSessionLocal, Value paramValue) {
/* 126 */     if (!this.whenOperand) {
/* 127 */       return super.getWhenValue(paramSessionLocal, paramValue);
/*     */     }
/* 129 */     if (paramValue == ValueNull.INSTANCE) {
/* 130 */       return false;
/*     */     }
/* 132 */     return getValue(paramSessionLocal, paramValue, this.not, this.parameter.getValue(paramSessionLocal)).isTrue();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isWhenConditionOperand() {
/* 137 */     return this.whenOperand;
/*     */   }
/*     */ 
/*     */   
/*     */   public void mapColumns(ColumnResolver paramColumnResolver, int paramInt1, int paramInt2) {
/* 142 */     this.left.mapColumns(paramColumnResolver, paramInt1, paramInt2);
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression optimize(SessionLocal paramSessionLocal) {
/* 147 */     this.left = this.left.optimize(paramSessionLocal);
/* 148 */     if (!this.whenOperand && this.left.isNullConstant()) {
/* 149 */       return (Expression)TypedValueExpression.UNKNOWN;
/*     */     }
/* 151 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression getNotIfPossible(SessionLocal paramSessionLocal) {
/* 156 */     if (this.whenOperand) {
/* 157 */       return null;
/*     */     }
/* 159 */     return new ConditionInParameter(this.left, !this.not, false, this.parameter);
/*     */   }
/*     */ 
/*     */   
/*     */   public void createIndexConditions(SessionLocal paramSessionLocal, TableFilter paramTableFilter) {
/* 164 */     if (this.not || this.whenOperand || !(this.left instanceof ExpressionColumn)) {
/*     */       return;
/*     */     }
/* 167 */     ExpressionColumn expressionColumn = (ExpressionColumn)this.left;
/* 168 */     if (paramTableFilter != expressionColumn.getTableFilter()) {
/*     */       return;
/*     */     }
/* 171 */     paramTableFilter.addIndexCondition(IndexCondition.getInList(expressionColumn, new ParameterList(this.parameter)));
/*     */   }
/*     */ 
/*     */   
/*     */   public void setEvaluatable(TableFilter paramTableFilter, boolean paramBoolean) {
/* 176 */     this.left.setEvaluatable(paramTableFilter, paramBoolean);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean needParentheses() {
/* 181 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 186 */     if (this.not) {
/* 187 */       paramStringBuilder.append("NOT (");
/*     */     }
/* 189 */     this.left.getSQL(paramStringBuilder, paramInt, 0);
/* 190 */     this.parameter.getSQL(paramStringBuilder.append(" = ANY("), paramInt, 0).append(')');
/* 191 */     if (this.not) {
/* 192 */       paramStringBuilder.append(')');
/*     */     }
/* 194 */     return paramStringBuilder;
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getWhenSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 199 */     if (this.not) {
/* 200 */       paramStringBuilder.append(" NOT IN(UNNEST(");
/* 201 */       this.parameter.getSQL(paramStringBuilder, paramInt, 0).append("))");
/*     */     } else {
/* 203 */       paramStringBuilder.append(" = ANY(");
/* 204 */       this.parameter.getSQL(paramStringBuilder, paramInt, 0).append(')');
/*     */     } 
/* 206 */     return paramStringBuilder;
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateAggregate(SessionLocal paramSessionLocal, int paramInt) {
/* 211 */     this.left.updateAggregate(paramSessionLocal, paramInt);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/* 216 */     return (this.left.isEverything(paramExpressionVisitor) && this.parameter.isEverything(paramExpressionVisitor));
/*     */   }
/*     */ 
/*     */   
/*     */   public int getCost() {
/* 221 */     return this.left.getCost();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\condition\ConditionInParameter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */