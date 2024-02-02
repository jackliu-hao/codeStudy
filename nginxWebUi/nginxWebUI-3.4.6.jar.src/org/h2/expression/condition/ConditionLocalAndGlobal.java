/*     */ package org.h2.expression.condition;
/*     */ 
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.ExpressionVisitor;
/*     */ import org.h2.message.DbException;
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
/*     */ public class ConditionLocalAndGlobal
/*     */   extends Condition
/*     */ {
/*     */   private Expression local;
/*     */   private Expression global;
/*     */   
/*     */   public ConditionLocalAndGlobal(Expression paramExpression1, Expression paramExpression2) {
/*  28 */     if (paramExpression2 == null) {
/*  29 */       throw DbException.getInternalError();
/*     */     }
/*  31 */     this.local = paramExpression1;
/*  32 */     this.global = paramExpression2;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean needParentheses() {
/*  37 */     return (this.local != null || this.global.needParentheses());
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/*  42 */     if (this.local == null) {
/*  43 */       return this.global.getUnenclosedSQL(paramStringBuilder, paramInt);
/*     */     }
/*  45 */     this.local.getSQL(paramStringBuilder, paramInt, 0);
/*  46 */     paramStringBuilder.append("\n    _LOCAL_AND_GLOBAL_ ");
/*  47 */     return this.global.getSQL(paramStringBuilder, paramInt, 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public void createIndexConditions(SessionLocal paramSessionLocal, TableFilter paramTableFilter) {
/*  52 */     if (this.local != null) {
/*  53 */       this.local.createIndexConditions(paramSessionLocal, paramTableFilter);
/*     */     }
/*  55 */     this.global.createIndexConditions(paramSessionLocal, paramTableFilter);
/*     */   }
/*     */ 
/*     */   
/*     */   public Value getValue(SessionLocal paramSessionLocal) {
/*  60 */     if (this.local == null) {
/*  61 */       return this.global.getValue(paramSessionLocal);
/*     */     }
/*  63 */     Value value1 = this.local.getValue(paramSessionLocal); Value value2;
/*  64 */     if (value1.isFalse() || (value2 = this.global.getValue(paramSessionLocal)).isFalse()) {
/*  65 */       return (Value)ValueBoolean.FALSE;
/*     */     }
/*  67 */     if (value1 == ValueNull.INSTANCE || value2 == ValueNull.INSTANCE) {
/*  68 */       return (Value)ValueNull.INSTANCE;
/*     */     }
/*  70 */     return (Value)ValueBoolean.TRUE;
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression optimize(SessionLocal paramSessionLocal) {
/*  75 */     this.global = this.global.optimize(paramSessionLocal);
/*  76 */     if (this.local != null) {
/*  77 */       this.local = this.local.optimize(paramSessionLocal);
/*  78 */       Expression expression = ConditionAndOr.optimizeIfConstant(paramSessionLocal, 0, this.local, this.global);
/*  79 */       if (expression != null) {
/*  80 */         return expression;
/*     */       }
/*     */     } 
/*  83 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void addFilterConditions(TableFilter paramTableFilter) {
/*  88 */     if (this.local != null) {
/*  89 */       this.local.addFilterConditions(paramTableFilter);
/*     */     }
/*  91 */     this.global.addFilterConditions(paramTableFilter);
/*     */   }
/*     */ 
/*     */   
/*     */   public void mapColumns(ColumnResolver paramColumnResolver, int paramInt1, int paramInt2) {
/*  96 */     if (this.local != null) {
/*  97 */       this.local.mapColumns(paramColumnResolver, paramInt1, paramInt2);
/*     */     }
/*  99 */     this.global.mapColumns(paramColumnResolver, paramInt1, paramInt2);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setEvaluatable(TableFilter paramTableFilter, boolean paramBoolean) {
/* 104 */     if (this.local != null) {
/* 105 */       this.local.setEvaluatable(paramTableFilter, paramBoolean);
/*     */     }
/* 107 */     this.global.setEvaluatable(paramTableFilter, paramBoolean);
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateAggregate(SessionLocal paramSessionLocal, int paramInt) {
/* 112 */     if (this.local != null) {
/* 113 */       this.local.updateAggregate(paramSessionLocal, paramInt);
/*     */     }
/* 115 */     this.global.updateAggregate(paramSessionLocal, paramInt);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/* 120 */     return ((this.local == null || this.local.isEverything(paramExpressionVisitor)) && this.global.isEverything(paramExpressionVisitor));
/*     */   }
/*     */ 
/*     */   
/*     */   public int getCost() {
/* 125 */     int i = this.global.getCost();
/* 126 */     if (this.local != null) {
/* 127 */       i += this.local.getCost();
/*     */     }
/* 129 */     return i;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSubexpressionCount() {
/* 134 */     return (this.local == null) ? 1 : 2;
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression getSubexpression(int paramInt) {
/* 139 */     switch (paramInt) {
/*     */       case 0:
/* 141 */         return (this.local != null) ? this.local : this.global;
/*     */       case 1:
/* 143 */         if (this.local != null) {
/* 144 */           return this.global;
/*     */         }
/*     */         break;
/*     */     } 
/* 148 */     throw new IndexOutOfBoundsException();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\condition\ConditionLocalAndGlobal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */