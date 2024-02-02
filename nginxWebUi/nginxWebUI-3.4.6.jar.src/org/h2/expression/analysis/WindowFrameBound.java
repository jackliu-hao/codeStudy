/*     */ package org.h2.expression.analysis;
/*     */ 
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.table.ColumnResolver;
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
/*     */ public class WindowFrameBound
/*     */ {
/*     */   private final WindowFrameBoundType type;
/*     */   private Expression value;
/*     */   private boolean isVariable;
/*  23 */   private int expressionIndex = -1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WindowFrameBound(WindowFrameBoundType paramWindowFrameBoundType, Expression paramExpression) {
/*  34 */     this.type = paramWindowFrameBoundType;
/*  35 */     if (paramWindowFrameBoundType == WindowFrameBoundType.PRECEDING || paramWindowFrameBoundType == WindowFrameBoundType.FOLLOWING) {
/*  36 */       this.value = paramExpression;
/*     */     } else {
/*  38 */       this.value = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WindowFrameBoundType getType() {
/*  48 */     return this.type;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Expression getValue() {
/*  57 */     return this.value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isParameterized() {
/*  66 */     return (this.type == WindowFrameBoundType.PRECEDING || this.type == WindowFrameBoundType.FOLLOWING);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isVariable() {
/*  76 */     return this.isVariable;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getExpressionIndex() {
/*  85 */     return this.expressionIndex;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void setExpressionIndex(int paramInt) {
/*  95 */     this.expressionIndex = paramInt;
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
/*     */   void mapColumns(ColumnResolver paramColumnResolver, int paramInt1, int paramInt2) {
/* 109 */     if (this.value != null) {
/* 110 */       this.value.mapColumns(paramColumnResolver, paramInt1, paramInt2);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void optimize(SessionLocal paramSessionLocal) {
/* 121 */     if (this.value != null) {
/* 122 */       this.value = this.value.optimize(paramSessionLocal);
/* 123 */       if (!this.value.isConstant()) {
/* 124 */         this.isVariable = true;
/*     */       }
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
/*     */   void updateAggregate(SessionLocal paramSessionLocal, int paramInt) {
/* 139 */     if (this.value != null) {
/* 140 */       this.value.updateAggregate(paramSessionLocal, paramInt);
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
/*     */   public StringBuilder getSQL(StringBuilder paramStringBuilder, boolean paramBoolean, int paramInt) {
/* 158 */     if (this.type == WindowFrameBoundType.PRECEDING || this.type == WindowFrameBoundType.FOLLOWING) {
/* 159 */       this.value.getUnenclosedSQL(paramStringBuilder, paramInt).append(' ');
/*     */     }
/* 161 */     return paramStringBuilder.append(this.type.getSQL());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\analysis\WindowFrameBound.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */