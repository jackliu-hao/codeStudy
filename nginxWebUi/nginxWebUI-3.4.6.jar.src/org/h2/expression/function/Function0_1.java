/*    */ package org.h2.expression.function;
/*    */ 
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.expression.Expression;
/*    */ import org.h2.expression.ExpressionVisitor;
/*    */ import org.h2.table.ColumnResolver;
/*    */ import org.h2.table.TableFilter;
/*    */ import org.h2.value.TypeInfo;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class Function0_1
/*    */   extends Expression
/*    */   implements NamedExpression
/*    */ {
/*    */   protected Expression arg;
/*    */   protected TypeInfo type;
/*    */   
/*    */   protected Function0_1(Expression paramExpression) {
/* 31 */     this.arg = paramExpression;
/*    */   }
/*    */ 
/*    */   
/*    */   public TypeInfo getType() {
/* 36 */     return this.type;
/*    */   }
/*    */ 
/*    */   
/*    */   public void mapColumns(ColumnResolver paramColumnResolver, int paramInt1, int paramInt2) {
/* 41 */     if (this.arg != null) {
/* 42 */       this.arg.mapColumns(paramColumnResolver, paramInt1, paramInt2);
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public void setEvaluatable(TableFilter paramTableFilter, boolean paramBoolean) {
/* 48 */     if (this.arg != null) {
/* 49 */       this.arg.setEvaluatable(paramTableFilter, paramBoolean);
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public void updateAggregate(SessionLocal paramSessionLocal, int paramInt) {
/* 55 */     if (this.arg != null) {
/* 56 */       this.arg.updateAggregate(paramSessionLocal, paramInt);
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/* 62 */     return (this.arg == null || this.arg.isEverything(paramExpressionVisitor));
/*    */   }
/*    */ 
/*    */   
/*    */   public int getCost() {
/* 67 */     int i = 1;
/* 68 */     if (this.arg != null) {
/* 69 */       i += this.arg.getCost();
/*    */     }
/* 71 */     return i;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getSubexpressionCount() {
/* 76 */     return (this.arg != null) ? 1 : 0;
/*    */   }
/*    */ 
/*    */   
/*    */   public Expression getSubexpression(int paramInt) {
/* 81 */     if (paramInt == 0 && this.arg != null) {
/* 82 */       return this.arg;
/*    */     }
/* 84 */     throw new IndexOutOfBoundsException();
/*    */   }
/*    */ 
/*    */   
/*    */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 89 */     paramStringBuilder.append(getName()).append('(');
/* 90 */     if (this.arg != null) {
/* 91 */       this.arg.getUnenclosedSQL(paramStringBuilder, paramInt);
/*    */     }
/* 93 */     return paramStringBuilder.append(')');
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\function\Function0_1.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */