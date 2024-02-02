/*    */ package org.h2.expression.condition;
/*    */ 
/*    */ import org.h2.command.query.Query;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.expression.Expression;
/*    */ import org.h2.expression.ExpressionVisitor;
/*    */ import org.h2.table.ColumnResolver;
/*    */ import org.h2.table.TableFilter;
/*    */ import org.h2.util.StringUtils;
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
/*    */ abstract class PredicateWithSubquery
/*    */   extends Condition
/*    */ {
/*    */   final Query query;
/*    */   
/*    */   PredicateWithSubquery(Query paramQuery) {
/* 27 */     this.query = paramQuery;
/*    */   }
/*    */ 
/*    */   
/*    */   public void mapColumns(ColumnResolver paramColumnResolver, int paramInt1, int paramInt2) {
/* 32 */     this.query.mapColumns(paramColumnResolver, paramInt1 + 1);
/*    */   }
/*    */ 
/*    */   
/*    */   public Expression optimize(SessionLocal paramSessionLocal) {
/* 37 */     this.query.prepare();
/* 38 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setEvaluatable(TableFilter paramTableFilter, boolean paramBoolean) {
/* 43 */     this.query.setEvaluatable(paramTableFilter, paramBoolean);
/*    */   }
/*    */ 
/*    */   
/*    */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 48 */     return StringUtils.indent(paramStringBuilder.append('('), this.query.getPlanSQL(paramInt), 4, false).append(')');
/*    */   }
/*    */ 
/*    */   
/*    */   public void updateAggregate(SessionLocal paramSessionLocal, int paramInt) {
/* 53 */     this.query.updateAggregate(paramSessionLocal, paramInt);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/* 58 */     return this.query.isEverything(paramExpressionVisitor);
/*    */   }
/*    */ 
/*    */   
/*    */   public int getCost() {
/* 63 */     return this.query.getCostAsExpression();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\condition\PredicateWithSubquery.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */