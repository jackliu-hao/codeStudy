/*     */ package org.h2.expression.function;
/*     */ 
/*     */ import org.h2.engine.DbObject;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.ExpressionVisitor;
/*     */ import org.h2.expression.ValueExpression;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.schema.FunctionAlias;
/*     */ import org.h2.table.ColumnResolver;
/*     */ import org.h2.table.TableFilter;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class JavaFunction
/*     */   extends Expression
/*     */   implements NamedExpression
/*     */ {
/*     */   private final FunctionAlias functionAlias;
/*     */   private final FunctionAlias.JavaMethod javaMethod;
/*     */   private final Expression[] args;
/*     */   
/*     */   public JavaFunction(FunctionAlias paramFunctionAlias, Expression[] paramArrayOfExpression) {
/*  30 */     this.functionAlias = paramFunctionAlias;
/*  31 */     this.javaMethod = paramFunctionAlias.findJavaMethod(paramArrayOfExpression);
/*  32 */     if (this.javaMethod.getDataType() == null) {
/*  33 */       throw DbException.get(90022, getName());
/*     */     }
/*  35 */     this.args = paramArrayOfExpression;
/*     */   }
/*     */ 
/*     */   
/*     */   public Value getValue(SessionLocal paramSessionLocal) {
/*  40 */     return this.javaMethod.getValue(paramSessionLocal, this.args, false);
/*     */   }
/*     */ 
/*     */   
/*     */   public TypeInfo getType() {
/*  45 */     return this.javaMethod.getDataType();
/*     */   }
/*     */ 
/*     */   
/*     */   public void mapColumns(ColumnResolver paramColumnResolver, int paramInt1, int paramInt2) {
/*  50 */     for (Expression expression : this.args) {
/*  51 */       expression.mapColumns(paramColumnResolver, paramInt1, paramInt2);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression optimize(SessionLocal paramSessionLocal) {
/*  57 */     boolean bool = this.functionAlias.isDeterministic(); byte b; int i;
/*  58 */     for (b = 0, i = this.args.length; b < i; b++) {
/*  59 */       Expression expression = this.args[b].optimize(paramSessionLocal);
/*  60 */       this.args[b] = expression;
/*  61 */       bool &= expression.isConstant();
/*     */     } 
/*  63 */     if (bool) {
/*  64 */       return (Expression)ValueExpression.get(getValue(paramSessionLocal));
/*     */     }
/*  66 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setEvaluatable(TableFilter paramTableFilter, boolean paramBoolean) {
/*  71 */     for (Expression expression : this.args) {
/*  72 */       if (expression != null) {
/*  73 */         expression.setEvaluatable(paramTableFilter, paramBoolean);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/*  80 */     return writeExpressions(this.functionAlias.getSQL(paramStringBuilder, paramInt).append('('), this.args, paramInt).append(')');
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateAggregate(SessionLocal paramSessionLocal, int paramInt) {
/*  85 */     for (Expression expression : this.args) {
/*  86 */       if (expression != null) {
/*  87 */         expression.updateAggregate(paramSessionLocal, paramInt);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/*  94 */     return this.functionAlias.getName();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/*  99 */     switch (paramExpressionVisitor.getType()) {
/*     */       case 2:
/*     */       case 5:
/*     */       case 8:
/* 103 */         if (!this.functionAlias.isDeterministic()) {
/* 104 */           return false;
/*     */         }
/*     */         break;
/*     */       
/*     */       case 7:
/* 109 */         paramExpressionVisitor.addDependency((DbObject)this.functionAlias);
/*     */         break;
/*     */     } 
/*     */     
/* 113 */     for (Expression expression : this.args) {
/* 114 */       if (expression != null && !expression.isEverything(paramExpressionVisitor)) {
/* 115 */         return false;
/*     */       }
/*     */     } 
/* 118 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getCost() {
/* 123 */     int i = this.javaMethod.hasConnectionParam() ? 25 : 5;
/* 124 */     for (Expression expression : this.args) {
/* 125 */       i += expression.getCost();
/*     */     }
/* 127 */     return i;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSubexpressionCount() {
/* 132 */     return this.args.length;
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression getSubexpression(int paramInt) {
/* 137 */     return this.args[paramInt];
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\function\JavaFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */