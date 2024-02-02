/*     */ package org.h2.expression;
/*     */ 
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.table.ColumnResolver;
/*     */ import org.h2.table.TableFilter;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueNull;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class SimpleCase
/*     */   extends Expression
/*     */ {
/*     */   private Expression operand;
/*     */   private SimpleWhen when;
/*     */   private Expression elseResult;
/*     */   private TypeInfo type;
/*     */   
/*     */   public static final class SimpleWhen
/*     */   {
/*     */     Expression[] operands;
/*     */     Expression result;
/*     */     SimpleWhen next;
/*     */     
/*     */     public SimpleWhen(Expression param1Expression1, Expression param1Expression2) {
/*  29 */       this(new Expression[] { param1Expression1 }, param1Expression2);
/*     */     }
/*     */     
/*     */     public SimpleWhen(Expression[] param1ArrayOfExpression, Expression param1Expression) {
/*  33 */       this.operands = param1ArrayOfExpression;
/*  34 */       this.result = param1Expression;
/*     */     }
/*     */     
/*     */     public void setWhen(SimpleWhen param1SimpleWhen) {
/*  38 */       this.next = param1SimpleWhen;
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
/*     */   public SimpleCase(Expression paramExpression1, SimpleWhen paramSimpleWhen, Expression paramExpression2) {
/*  52 */     this.operand = paramExpression1;
/*  53 */     this.when = paramSimpleWhen;
/*  54 */     this.elseResult = paramExpression2;
/*     */   }
/*     */ 
/*     */   
/*     */   public Value getValue(SessionLocal paramSessionLocal) {
/*  59 */     Value value = this.operand.getValue(paramSessionLocal);
/*  60 */     for (SimpleWhen simpleWhen = this.when; simpleWhen != null; simpleWhen = simpleWhen.next) {
/*  61 */       for (Expression expression : simpleWhen.operands) {
/*  62 */         if (expression.getWhenValue(paramSessionLocal, value)) {
/*  63 */           return simpleWhen.result.getValue(paramSessionLocal).convertTo(this.type, (CastDataProvider)paramSessionLocal);
/*     */         }
/*     */       } 
/*     */     } 
/*  67 */     if (this.elseResult != null) {
/*  68 */       return this.elseResult.getValue(paramSessionLocal).convertTo(this.type, (CastDataProvider)paramSessionLocal);
/*     */     }
/*  70 */     return (Value)ValueNull.INSTANCE;
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression optimize(SessionLocal paramSessionLocal) {
/*  75 */     TypeInfo typeInfo1 = TypeInfo.TYPE_UNKNOWN;
/*  76 */     this.operand = this.operand.optimize(paramSessionLocal);
/*  77 */     boolean bool = this.operand.isConstant();
/*  78 */     Value value = null;
/*  79 */     if (bool) {
/*  80 */       value = this.operand.getValue(paramSessionLocal);
/*     */     }
/*  82 */     TypeInfo typeInfo2 = this.operand.getType();
/*  83 */     for (SimpleWhen simpleWhen = this.when; simpleWhen != null; simpleWhen = simpleWhen.next) {
/*  84 */       Expression[] arrayOfExpression = simpleWhen.operands;
/*  85 */       for (byte b = 0; b < arrayOfExpression.length; b++) {
/*  86 */         Expression expression = arrayOfExpression[b].optimize(paramSessionLocal);
/*  87 */         if (!expression.isWhenConditionOperand()) {
/*  88 */           TypeInfo.checkComparable(typeInfo2, expression.getType());
/*     */         }
/*  90 */         if (bool) {
/*  91 */           if (expression.isConstant()) {
/*  92 */             if (expression.getWhenValue(paramSessionLocal, value)) {
/*  93 */               return simpleWhen.result.optimize(paramSessionLocal);
/*     */             }
/*     */           } else {
/*  96 */             bool = false;
/*     */           } 
/*     */         }
/*  99 */         arrayOfExpression[b] = expression;
/*     */       } 
/* 101 */       simpleWhen.result = simpleWhen.result.optimize(paramSessionLocal);
/* 102 */       typeInfo1 = combineTypes(typeInfo1, simpleWhen.result);
/*     */     } 
/* 104 */     if (this.elseResult != null) {
/* 105 */       this.elseResult = this.elseResult.optimize(paramSessionLocal);
/* 106 */       if (bool) {
/* 107 */         return this.elseResult;
/*     */       }
/* 109 */       typeInfo1 = combineTypes(typeInfo1, this.elseResult);
/* 110 */     } else if (bool) {
/* 111 */       return ValueExpression.NULL;
/*     */     } 
/* 113 */     if (typeInfo1.getValueType() == -1) {
/* 114 */       typeInfo1 = TypeInfo.TYPE_VARCHAR;
/*     */     }
/* 116 */     this.type = typeInfo1;
/* 117 */     return this;
/*     */   }
/*     */   
/*     */   static TypeInfo combineTypes(TypeInfo paramTypeInfo, Expression paramExpression) {
/* 121 */     if (!paramExpression.isNullConstant()) {
/* 122 */       TypeInfo typeInfo = paramExpression.getType();
/* 123 */       int i = typeInfo.getValueType();
/* 124 */       if (i != -1 && i != 0) {
/* 125 */         paramTypeInfo = TypeInfo.getHigherType(paramTypeInfo, typeInfo);
/*     */       }
/*     */     } 
/* 128 */     return paramTypeInfo;
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 133 */     this.operand.getUnenclosedSQL(paramStringBuilder.append("CASE "), paramInt);
/* 134 */     for (SimpleWhen simpleWhen = this.when; simpleWhen != null; simpleWhen = simpleWhen.next) {
/* 135 */       paramStringBuilder.append(" WHEN");
/* 136 */       Expression[] arrayOfExpression = simpleWhen.operands; byte b; int i;
/* 137 */       for (b = 0, i = arrayOfExpression.length; b < i; b++) {
/* 138 */         if (b > 0) {
/* 139 */           paramStringBuilder.append(',');
/*     */         }
/* 141 */         arrayOfExpression[b].getWhenSQL(paramStringBuilder, paramInt);
/*     */       } 
/* 143 */       simpleWhen.result.getUnenclosedSQL(paramStringBuilder.append(" THEN "), paramInt);
/*     */     } 
/* 145 */     if (this.elseResult != null) {
/* 146 */       this.elseResult.getUnenclosedSQL(paramStringBuilder.append(" ELSE "), paramInt);
/*     */     }
/* 148 */     return paramStringBuilder.append(" END");
/*     */   }
/*     */ 
/*     */   
/*     */   public TypeInfo getType() {
/* 153 */     return this.type;
/*     */   }
/*     */ 
/*     */   
/*     */   public void mapColumns(ColumnResolver paramColumnResolver, int paramInt1, int paramInt2) {
/* 158 */     this.operand.mapColumns(paramColumnResolver, paramInt1, paramInt2);
/* 159 */     for (SimpleWhen simpleWhen = this.when; simpleWhen != null; simpleWhen = simpleWhen.next) {
/* 160 */       for (Expression expression : simpleWhen.operands) {
/* 161 */         expression.mapColumns(paramColumnResolver, paramInt1, paramInt2);
/*     */       }
/* 163 */       simpleWhen.result.mapColumns(paramColumnResolver, paramInt1, paramInt2);
/*     */     } 
/* 165 */     if (this.elseResult != null) {
/* 166 */       this.elseResult.mapColumns(paramColumnResolver, paramInt1, paramInt2);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void setEvaluatable(TableFilter paramTableFilter, boolean paramBoolean) {
/* 172 */     this.operand.setEvaluatable(paramTableFilter, paramBoolean);
/* 173 */     for (SimpleWhen simpleWhen = this.when; simpleWhen != null; simpleWhen = simpleWhen.next) {
/* 174 */       for (Expression expression : simpleWhen.operands) {
/* 175 */         expression.setEvaluatable(paramTableFilter, paramBoolean);
/*     */       }
/* 177 */       simpleWhen.result.setEvaluatable(paramTableFilter, paramBoolean);
/*     */     } 
/* 179 */     if (this.elseResult != null) {
/* 180 */       this.elseResult.setEvaluatable(paramTableFilter, paramBoolean);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateAggregate(SessionLocal paramSessionLocal, int paramInt) {
/* 186 */     this.operand.updateAggregate(paramSessionLocal, paramInt);
/* 187 */     for (SimpleWhen simpleWhen = this.when; simpleWhen != null; simpleWhen = simpleWhen.next) {
/* 188 */       for (Expression expression : simpleWhen.operands) {
/* 189 */         expression.updateAggregate(paramSessionLocal, paramInt);
/*     */       }
/* 191 */       simpleWhen.result.updateAggregate(paramSessionLocal, paramInt);
/*     */     } 
/* 193 */     if (this.elseResult != null) {
/* 194 */       this.elseResult.updateAggregate(paramSessionLocal, paramInt);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/* 200 */     if (!this.operand.isEverything(paramExpressionVisitor)) {
/* 201 */       return false;
/*     */     }
/* 203 */     for (SimpleWhen simpleWhen = this.when; simpleWhen != null; simpleWhen = simpleWhen.next) {
/* 204 */       for (Expression expression : simpleWhen.operands) {
/* 205 */         if (!expression.isEverything(paramExpressionVisitor)) {
/* 206 */           return false;
/*     */         }
/*     */       } 
/* 209 */       if (!simpleWhen.result.isEverything(paramExpressionVisitor)) {
/* 210 */         return false;
/*     */       }
/*     */     } 
/* 213 */     if (this.elseResult != null && !this.elseResult.isEverything(paramExpressionVisitor)) {
/* 214 */       return false;
/*     */     }
/* 216 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getCost() {
/* 221 */     int i = 1, j = 0;
/* 222 */     i += this.operand.getCost();
/* 223 */     for (SimpleWhen simpleWhen = this.when; simpleWhen != null; simpleWhen = simpleWhen.next) {
/* 224 */       for (Expression expression : simpleWhen.operands) {
/* 225 */         i += expression.getCost();
/*     */       }
/* 227 */       j = Math.max(j, simpleWhen.result.getCost());
/*     */     } 
/* 229 */     if (this.elseResult != null) {
/* 230 */       j = Math.max(j, this.elseResult.getCost());
/*     */     }
/* 232 */     return i + j;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSubexpressionCount() {
/* 237 */     int i = 1;
/* 238 */     for (SimpleWhen simpleWhen = this.when; simpleWhen != null; simpleWhen = simpleWhen.next) {
/* 239 */       i += simpleWhen.operands.length + 1;
/*     */     }
/* 241 */     if (this.elseResult != null) {
/* 242 */       i++;
/*     */     }
/* 244 */     return i;
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression getSubexpression(int paramInt) {
/* 249 */     if (paramInt >= 0) {
/* 250 */       if (paramInt == 0) {
/* 251 */         return this.operand;
/*     */       }
/* 253 */       int i = 1;
/* 254 */       for (SimpleWhen simpleWhen = this.when; simpleWhen != null; simpleWhen = simpleWhen.next) {
/* 255 */         Expression[] arrayOfExpression = simpleWhen.operands;
/* 256 */         int j = arrayOfExpression.length;
/* 257 */         int k = paramInt - i;
/* 258 */         if (k < j) {
/* 259 */           return arrayOfExpression[k];
/*     */         }
/* 261 */         i += j;
/* 262 */         if (paramInt == i++) {
/* 263 */           return simpleWhen.result;
/*     */         }
/*     */       } 
/* 266 */       if (this.elseResult != null && paramInt == i) {
/* 267 */         return this.elseResult;
/*     */       }
/*     */     } 
/* 270 */     throw new IndexOutOfBoundsException();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\SimpleCase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */