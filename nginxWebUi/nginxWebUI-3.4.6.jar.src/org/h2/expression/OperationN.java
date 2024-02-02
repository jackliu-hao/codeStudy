/*     */ package org.h2.expression;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.table.ColumnResolver;
/*     */ import org.h2.table.TableFilter;
/*     */ import org.h2.value.TypeInfo;
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
/*     */ public abstract class OperationN
/*     */   extends Expression
/*     */   implements ExpressionWithVariableParameters
/*     */ {
/*     */   protected Expression[] args;
/*     */   protected int argsCount;
/*     */   protected TypeInfo type;
/*     */   
/*     */   protected OperationN(Expression[] paramArrayOfExpression) {
/*  37 */     this.args = paramArrayOfExpression;
/*     */   }
/*     */ 
/*     */   
/*     */   public void addParameter(Expression paramExpression) {
/*  42 */     int i = this.args.length;
/*  43 */     if (this.argsCount >= i) {
/*  44 */       this.args = Arrays.<Expression>copyOf(this.args, i * 2);
/*     */     }
/*  46 */     this.args[this.argsCount++] = paramExpression;
/*     */   }
/*     */ 
/*     */   
/*     */   public void doneWithParameters() throws DbException {
/*  51 */     if (this.args.length != this.argsCount) {
/*  52 */       this.args = Arrays.<Expression>copyOf(this.args, this.argsCount);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public TypeInfo getType() {
/*  58 */     return this.type;
/*     */   }
/*     */ 
/*     */   
/*     */   public void mapColumns(ColumnResolver paramColumnResolver, int paramInt1, int paramInt2) {
/*  63 */     for (Expression expression : this.args) {
/*  64 */       expression.mapColumns(paramColumnResolver, paramInt1, paramInt2);
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
/*     */   protected boolean optimizeArguments(SessionLocal paramSessionLocal, boolean paramBoolean) {
/*     */     byte b;
/*     */     int i;
/*  79 */     for (b = 0, i = this.args.length; b < i; b++) {
/*  80 */       Expression expression = this.args[b].optimize(paramSessionLocal);
/*  81 */       this.args[b] = expression;
/*  82 */       if (paramBoolean && !expression.isConstant()) {
/*  83 */         paramBoolean = false;
/*     */       }
/*     */     } 
/*  86 */     return paramBoolean;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setEvaluatable(TableFilter paramTableFilter, boolean paramBoolean) {
/*  91 */     for (Expression expression : this.args) {
/*  92 */       expression.setEvaluatable(paramTableFilter, paramBoolean);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateAggregate(SessionLocal paramSessionLocal, int paramInt) {
/*  98 */     for (Expression expression : this.args) {
/*  99 */       expression.updateAggregate(paramSessionLocal, paramInt);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/* 105 */     for (Expression expression : this.args) {
/* 106 */       if (!expression.isEverything(paramExpressionVisitor)) {
/* 107 */         return false;
/*     */       }
/*     */     } 
/* 110 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getCost() {
/* 115 */     int i = this.args.length + 1;
/* 116 */     for (Expression expression : this.args) {
/* 117 */       i += expression.getCost();
/*     */     }
/* 119 */     return i;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSubexpressionCount() {
/* 124 */     return this.args.length;
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression getSubexpression(int paramInt) {
/* 129 */     return this.args[paramInt];
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\OperationN.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */