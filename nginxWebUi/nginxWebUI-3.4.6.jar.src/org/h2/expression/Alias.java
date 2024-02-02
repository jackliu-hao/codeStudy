/*     */ package org.h2.expression;
/*     */ 
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.table.ColumnResolver;
/*     */ import org.h2.table.TableFilter;
/*     */ import org.h2.util.ParserUtil;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class Alias
/*     */   extends Expression
/*     */ {
/*     */   private final String alias;
/*     */   private Expression expr;
/*     */   private final boolean aliasColumnName;
/*     */   
/*     */   public Alias(Expression paramExpression, String paramString, boolean paramBoolean) {
/*  25 */     this.expr = paramExpression;
/*  26 */     this.alias = paramString;
/*  27 */     this.aliasColumnName = paramBoolean;
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression getNonAliasExpression() {
/*  32 */     return this.expr;
/*     */   }
/*     */ 
/*     */   
/*     */   public Value getValue(SessionLocal paramSessionLocal) {
/*  37 */     return this.expr.getValue(paramSessionLocal);
/*     */   }
/*     */ 
/*     */   
/*     */   public TypeInfo getType() {
/*  42 */     return this.expr.getType();
/*     */   }
/*     */ 
/*     */   
/*     */   public void mapColumns(ColumnResolver paramColumnResolver, int paramInt1, int paramInt2) {
/*  47 */     this.expr.mapColumns(paramColumnResolver, paramInt1, paramInt2);
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression optimize(SessionLocal paramSessionLocal) {
/*  52 */     this.expr = this.expr.optimize(paramSessionLocal);
/*  53 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setEvaluatable(TableFilter paramTableFilter, boolean paramBoolean) {
/*  58 */     this.expr.setEvaluatable(paramTableFilter, paramBoolean);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isIdentity() {
/*  63 */     return this.expr.isIdentity();
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/*  68 */     this.expr.getUnenclosedSQL(paramStringBuilder, paramInt).append(" AS ");
/*  69 */     return ParserUtil.quoteIdentifier(paramStringBuilder, this.alias, paramInt);
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateAggregate(SessionLocal paramSessionLocal, int paramInt) {
/*  74 */     this.expr.updateAggregate(paramSessionLocal, paramInt);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getAlias(SessionLocal paramSessionLocal, int paramInt) {
/*  79 */     return this.alias;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getColumnNameForView(SessionLocal paramSessionLocal, int paramInt) {
/*  84 */     return this.alias;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getNullable() {
/*  89 */     return this.expr.getNullable();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/*  94 */     return this.expr.isEverything(paramExpressionVisitor);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getCost() {
/*  99 */     return this.expr.getCost();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getSchemaName() {
/* 104 */     if (this.aliasColumnName) {
/* 105 */       return null;
/*     */     }
/* 107 */     return this.expr.getSchemaName();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getTableName() {
/* 112 */     if (this.aliasColumnName) {
/* 113 */       return null;
/*     */     }
/* 115 */     return this.expr.getTableName();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getColumnName(SessionLocal paramSessionLocal, int paramInt) {
/* 120 */     if (!(this.expr instanceof ExpressionColumn) || this.aliasColumnName) {
/* 121 */       return this.alias;
/*     */     }
/* 123 */     return this.expr.getColumnName(paramSessionLocal, paramInt);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\Alias.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */